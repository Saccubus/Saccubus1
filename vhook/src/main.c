
#include <SDL/SDL.h>
#include <SDL/SDL_ttf.h>
#include <stdio.h>
#include "main.h"
#include "mydef.h"
#include "nicodef.h"
#include "process.h"
int initCommentData(DATA* data, CDATA* cdata, FILE* log, const char* path, int max_slot, const char* com_type);

/**
 * ���C�u����������
 */
int init(FILE* log){
	fputs("[main/init]initializing libs...\n",log);
	//SDL
	if(SDL_Init(SDL_INIT_VIDEO)>=0){
		fputs("[main/init]initialized SDL.\n",log);
	}else{
		fputs("[main/init]failed to initialize SDL.\n",log);
			return FALSE;
	}
	//SDL_ttf
	if(TTF_Init() >= 0){
		fputs("[main/init]initialized SDL_ttf.\n",log);
	}else{
		fputs("[main/init]failed to initialize SDL_ttf.\n",log);
			return FALSE;
	}
	fputs("[main/init]initialized libs.\n",log);
	return TRUE;
}
/*
 * �f�[�^�̏�����
 * ContextInfo ci->DATA data �� SETTING setting
 */
int initData(DATA* data,FILE* log,const SETTING* setting){
	int i;
	data->user.enable_comment = setting->enable_user_comment;
	data->owner.enable_comment = setting->enable_owner_comment;
	data->optional.enable_comment = setting->enable_optional_comment;
	data->log = log;
	data->fontsize_fix = setting->fontsize_fix;
	data->show_video = setting->show_video;
	data->opaque_comment = setting->opaque_comment;
	data->optional_trunslucent = setting->optional_trunslucent;
	data->shadow_kind = setting->shadow_kind;
	data->process_first_called=FALSE;
	data->video_length = setting->video_length;
	if (data->video_length <= 0){
		data->video_length = INTEGER_MAX;
	}
	data->nico_width_now = setting->nico_width_now;
	data->font_h_fix_r = setting->font_h_fix_r;
	data->original_resize = setting->original_resize;
	data->comment_speed = setting->comment_speed;
	data->debug = setting->debug;
//	data->limit_height = NICO_HEIGHT;
/*
	// experimental
	data->limitwidth_resize = setting->limitwidth_resize;
	data->linefeed_resize = setting->linefeed_resize;
	data->double_resize = setting->double_resize;
	//data->font_double_scale = setting->font_double_scale;
	int is_wide;
	if (data->nico_width_now == NICO_WIDTH){
		is_wide = 0;
	} else {
		is_wide = 1;
	}
	data->font_height_rate = setting->font_height_rate[is_wide];
	data->limit_height = setting->nico_limit_height[is_wide];
	data->next_h_rate = setting->next_h_rate[is_wide];
	data->next_h_pixel = setting->next_h_pixel[is_wide];
	data->limit_width[0] = setting->nico_limit_width[0];
	data->limit_width[1] = setting->nico_limit_width[1];
	data->double_resize_width[0] = setting->double_resize_width[0];
	data->double_resize_width[1] = setting->double_resize_width[1];
	data->double_limit_width[0] = setting->double_limit_width[0];
	data->double_limit_width[1] = setting->double_limit_width[1];
	data->target_width = setting->target_width;
	int scale100 = 100;
	if(data->target_width == 0){
		data->target_width = data->nico_width_now;
	} else {
		 scale100 = (data->target_width * 100) / data->nico_width_now;
	}
	fprintf(log,"[main/debug]video scaling:%d%%.\n",scale100);
	data->font_scaling = 1;
	// �t�H���g��2�{������̂̓��T�C�Y�v�Z�����킹��̂Œ��~
	//  �^�[�Q�b�g���g�債�����Ƀt�H���g�����炩�ɂ���͍̂���ʎ�i�ōs��

	fprintf(log,"[main/debug]font pre-scaling:%d%%.\n",data->font_scaling*100);
*/
	fputs("[main/init]initializing context...\n",log);
	//�t�H���g
	TTF_Font** font = data->font;
	const char* font_path = setting->font_path;
	const int font_index = setting->font_index;
	for(i=0;i<CMD_FONT_MAX;i++){
		/*
		 * �t�H���g��2�{������̂̓��T�C�Y�v�Z�����킹��̂Œ��~
		 *  �^�[�Q�b�g���g�債�����Ƀt�H���g�����炩�ɂ���͍̂���ʎ�i�ōs��
		 */
	//	int fontsize = setting->fixed_font_size[i];
		int fontsize = COMMENT_FONT_SIZE[i];
		/*
		if(data->font_scaling == 2){
			fontsize <<= 1;
		}
	 	 */
		font[i] = TTF_OpenFontIndex(font_path,fontsize,font_index);
		if(font[i] == NULL){
			fprintf(log,"[main/init]failed to load font:%s index:[%d].\n",font_path,font_index);
			//0�ł������Ă݂�B
			fputs("[main/init]retrying to open font at index:0...",log);
			font[i] = TTF_OpenFontIndex(font_path,fontsize,0);
			if(font[i] == NULL){
				fputs("failed.\n",log);
				return FALSE;
			}else{
				fputs("success.\n",log);
			}
		}
		TTF_SetFontStyle(font[i],TTF_STYLE_BOLD);
		data->fixed_font_size[i] = TTF_FontHeight(font[i]);
		// TTF_FontHeight()�����������ǂ����͋^��H
		// �����ł͐ݒ�l�ƈႤpt���ɂȂ���
		data->font_pixel_size[i] = TTF_FontLineSkip(font[i]);
		//�����pt���������B
		// �Q�l 1 pt = 1/72 inch, 1 px = 1 dot
		data->font_pixel_size[i] = FONT_PIXEL_SIZE[i];
		// SDL_Surface �ɕ`���̍����͕���(��)���ɈقȂ�̂ł��̒l�ŏC������B
	}
	fputs("[main/init]initialized font, ",log);
	fprintf(log,"height is DEF=%dpt(%dpx), BIG=%dpt(%dpx), SMALL=%dpt(%dpx)\n",
		data->fixed_font_size[CMD_FONT_DEF],data->font_pixel_size[CMD_FONT_DEF],
		data->fixed_font_size[CMD_FONT_BIG],data->font_pixel_size[CMD_FONT_BIG],
		data->fixed_font_size[CMD_FONT_SMALL],data->font_pixel_size[CMD_FONT_SMALL]
	);

	fprintf(log, "[main/init]font height fix ratio:%d%% (experimental)\n",(int)(data->font_h_fix_r * 100));
/*
	fprintf(log, "[main/DEBUG]limit width:%d %d, double_resize:%d %d (experimental)\n",
		data->limit_width[0], data->limit_width[1],
		data->double_resize_width[0],data->double_resize_width[1]);
*/
//	fprintf(log, "[main/init]limit height:%d (experimental)\n",data->limit_height);
//	fprintf(log, "[main/init]target width: %d (experimaental)\n",data->target_width);
	fflush(log);
	/*
	 * ���[�U�R�����g
	 */
	if (!initCommentData(data, &data->user, log,
			setting->data_user_path, setting->user_slot_max, "user")){
		return FALSE;
	}
	/*
	 * �I�[�i�R�����g
	 */
	if (!initCommentData(data, &data->owner, log,
			setting->data_owner_path, setting->owner_slot_max, "owner")){
		return FALSE;
	}
	/*
	 * �I�v�V���i���R�����g
	 */
	if (!initCommentData(data, &data->optional, log,
			setting->data_optional_path, setting->optional_slot_max, "optional")){
		return FALSE;
	}

	//�I���B
	fputs("[main/init]initialized context.\n",log);
	return TRUE;
}
/*
 * �R�����g�f�[�^�̏�����
 * DATA data->user owner optional
 */
int initCommentData(DATA* data, CDATA* cdata,FILE* log,const char* path, int max_slot, const char* com_type){
	if (cdata->enable_comment){
		fprintf(log,"[main/init]%s comment is enabled.\n",com_type);
		//�R�����g�f�[�^
		if (initChat(log, &cdata->chat, path, &cdata->slot, data->video_length)){
			fprintf(log,"[main/init]initialized %s comment.\n",com_type);
		}else{
			fprintf(log,"[main/init]failed to initialize %s comment.",com_type);
			// closeChat(&cdata->chat);	// ���������[�N�h�~
			return FALSE;
		}
		if (cdata->chat.max_item > 0){
			//�R�����g�X���b�g
			if(max_slot > cdata->chat.max_item){
				max_slot = cdata->chat.max_item;
				fprintf(log,"[main/init]%s comment max_slot changed to %d.\n",com_type, max_slot);
			}
			if(initChatSlot(log, &cdata->slot, max_slot, &cdata->chat)){
				fprintf(log,"[main/init]initialized %s comment slot.\n",com_type);
			}else{
				fprintf(log,"[main/init]failed to initialize %s comment slot.",com_type);
				// closeChatSlot(&cdata->slot);	// ���������[�N�h�~
				return FALSE;
			}
		} else {
			cdata->enable_comment = FALSE;
			fprintf(log,"[main/init]%s comment has changed to disable.\n",com_type);
		}
	}
	return TRUE;
}

/*
 * �f���̕ϊ�
 */
int main_process(DATA* data,SDL_Surface* surf,const int now_vpos){
	FILE* log = data->log;
	if(!data->process_first_called){
		int aspect100 = surf->w * 100 /surf->h;
		fprintf(log,"[main/process]screen size is %dx%d, aspect is %d/100.\n",surf->w,surf->h, aspect100);
		fflush(log);
	/*
		double scale = (double)(surf->w) / (double)(data->nico_width_now);
		double aspect = (double)surf->w  /(double)surf->h;
		fprintf(log,"[main/process]screen size:%dx%d, aspect:%5.3f, scale:%5.3f.\n",surf->w,surf->h, aspect,scale);
		if(surf->w != (int)data->target_width){
			fprintf(log,"[main/process]screen size differs from target_width:%d.\n",data->target_width);
			fflush(log);
			return FALSE;
		}
	*/
	}
	/*�t�B���^��������*/
	if(process(data,surf,now_vpos)){
	}
	fflush(log);
	/*�ϊ������摜��������B*/
	if(data->show_video){
		if(!data->process_first_called){
			data->screen = SDL_SetVideoMode(surf->w, surf->h, 24, SDL_HWSURFACE | SDL_DOUBLEBUF);
			if(data->screen == NULL){
				fputs("[main/process]failed to initialize screen.\n",log);
				fflush(log);
				return FALSE;
			}
		}
		SDL_BlitSurface(surf,NULL,data->screen,NULL);
		SDL_Flip(data->screen);
		SDL_Event event;
		while(SDL_PollEvent(&event)){}
	}
	//���ڈȍ~��TRUE�ɂȂ�B
	data->process_first_called=TRUE;
	fflush(log);
	return TRUE;
}
/*
 * �f�[�^�̃N���[�Y
 */
int closeData(DATA* data){
	int i;
	//���[�U�R�����g���L���Ȃ�J��
	if(data->user.enable_comment){
		closeChat(&data->user.chat);
		closeChatSlot(&data->user.slot);
	}
	//�I�[�i�R�����g���L���Ȃ�J��
	if(data->owner.enable_comment){
		closeChat(&data->owner.chat);
		closeChatSlot(&data->owner.slot);
	}
	//�I�v�V���i���R�����g���L���Ȃ�J��
	if(data->optional.enable_comment){
		closeChat(&data->optional.chat);
		closeChatSlot(&data->optional.slot);
	}
	//�t�H���g�J��
	for(i=0;i<CMD_FONT_MAX;i++){
		TTF_CloseFont(data->font[i]);
	}
	return TRUE;
}

/*
 * ���C�u�����V���b�g�_�E��
 */
int close(){
		//SDL���V���b�g�_�E��
		SDL_Quit();
	//������TTF���V���b�g�_�E��
		TTF_Quit();
	return TRUE;
}

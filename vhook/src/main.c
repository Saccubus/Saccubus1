
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
	data->original_resize = setting->original_resize;
	data->limitwidth_resize = setting->limitwidth_resize;
	data->linefeed_resize = setting->linefeed_resize;
	data->double_resize = setting->double_resize;
	data->font_double_scale = setting->font_double_scale;
	data->nico_width_now = setting->nico_width_now;
	if (data->nico_width_now == NICO_WIDTH){
		data->font_h_fix_r = setting->font_h_fix_r;
		data->nico_limit_height = setting->nico_limit_height;
	} else {
		data->font_h_fix_r = setting->font_h_fix_r_wide;
		data->nico_limit_height = setting->nico_limit_height_wide;
	}
	data->nico_limit_width = setting->nico_limit_width;
	data->nico_limit_width_full = setting->nico_limit_width_full;
	data->next_y_ratio = setting->next_y_ratio;
	data->target_width = setting->target_width;
	double autoscale = 1.0;
	if(data->target_width == 0){
		data->target_width = data->nico_width_now;
	} else {
		 autoscale = (double)(data->target_width) / (double)(data->nico_width_now);
	}
	//�����X�P�[�����Q�{���傫�����Q�C�P�C�P�^�Q�A�P�^�S�ȊO�ꍇ�̓t�H���g���Q�{�Ɋg��
	int font_scaling = 1;
	if(autoscale > 2.0 ||
		(autoscale != 2.0 && autoscale != 1.0 && autoscale != 0.5 && autoscale != 0.25)){
		font_scaling = 2;
	}
	if(setting->fontsize_fix){
		data->font_scaling = font_scaling;
	} else {
		data->font_scaling = 1;
	}

	fputs("[main/init]initializing context...\n",log);
	//�t�H���g
	TTF_Font** font = data->font;
	const char* font_path = setting->font_path;
	const int font_index = setting->font_index;
	for(i=0;i<CMD_FONT_MAX;i++){
		int fontsize = setting->fixed_font_size[i];
		if(data->font_scaling == 2){
			fontsize <<= 1;
		}
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
		// �Q�l 1 pt = 1/72 inch, 1 px = 1 dot
		// PC�ł� 96 dot per inch ������ 4/3�{
		data->font_pixel_size[i] = data->fixed_font_size[i] * 4 / 3;
	}
	fprintf(log,"[main/init]initialized font, height is DEF=%dpt(%dpx), BIG=%dpt(%dpx), SMALL=%dpt(%dpx)\n",
		data->fixed_font_size[CMD_FONT_DEF],data->font_pixel_size[CMD_FONT_DEF],
		data->fixed_font_size[CMD_FONT_BIG],data->font_pixel_size[CMD_FONT_BIG],
		data->fixed_font_size[CMD_FONT_SMALL],data->font_pixel_size[CMD_FONT_SMALL]
		);
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
	fflush(log);
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
		if (initChat(log, &cdata->chat, path, &cdata->slot, data->video_length,data)){
			fprintf(log,"[main/init]initialized %s comment.\n",com_type);
		}else{
			fprintf(log,"[main/init]failed to initialize %s comment.",com_type);
			closeChat(&cdata->chat);	// ���������[�N�h�~
			return FALSE;
		}
		if (cdata->chat.max_item > 0){
			//�R�����g�X���b�g
			if(initChatSlot(log, &cdata->slot, max_slot, &cdata->chat)){
				fprintf(log,"[main/init]initialized %s comment slot.\n",com_type);
			}else{
				fprintf(log,"[main/init]failed to initialize %s comment slot.",com_type);
				closeChatSlot(&cdata->slot);	// ���������[�N�h�~
				return FALSE;
			}
		} else {
			fprintf(log,"[main/init]%s comment has changed to disable.\n",com_type);
			//closeChat(&cdata->chat);
			cdata->enable_comment = FALSE;
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
		double scale = (double)(surf->w) / (double)(data->nico_width_now);
		double aspect = (double)surf->w  /(double)surf->h;
		fprintf(log,"[main/process]screen size is %dx%d, aspect is %5.3f, scale is %5.3f.\n",surf->w,surf->h, aspect,scale);
		fflush(log);
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
		if(data->original_resize){
			SDL_Event event;
			while(SDL_PollEvent(&event)){}
		}
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
	// enable_comment�͓r���Ŗ���������ꍇ�����邪���Ȃ�
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

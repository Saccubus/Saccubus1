#include <SDL/SDL.h>
#include <SDL/SDL_ttf.h>
#include <SDL/SDL_rotozoom.h>
#include "com_surface.h"
#include "surf_util.h"
#include "../chat/chat.h"
#include "../chat/chat_slot.h"
#include "../nicodef.h"
#include "../mydef.h"
#include "../main.h"
#include "shadow.h"


SDL_Surface* drawText(DATA* data,int size,int color,Uint16* str);

SDL_Surface* makeCommentSurface(DATA* data,const CHAT_ITEM* item,int video_width,int video_height,int next_y_diff){
	Uint16* index = item->str;
	Uint16* last = item->str;
	SDL_Surface* ret = NULL;
	int color = item->color;
	int size = item->size;
	int nb_line = 1;

	/*
	 * �e�͒u���Ă����āA�Ƃ肠���������̕`��
	 */
	while(*index != '\0'){
		if(*index == '\n'){
			*index = '\0';//�����ň�U�؂�
			if(ret == null){//���ǉ��s�͖���
				ret = drawText(data,size,color,last);
			}else{/*���s����*/
				ret = connectSurface(ret,drawText(data,size,color,last),next_y_diff);
				nb_line++;
			}
			*index = '\n';//�����ň�U�؂�
			last = index+1;
		}
		index++;
	}
	if(ret == null){//���ǉ��s�͖���
		ret = drawText(data,size,color,item->str);
	}else{/*���s����*/
		ret = connectSurface(ret,drawText(data,size,color,last),next_y_diff);
		nb_line++;
	}

	if(ret->w == 0 || ret->h == 0){
		fprintf(data->log,"[comsurface/make]comment %4d has no char.\n",item->no);
		fflush(data->log);
		return ret;
	}

	/*
	 * �e����
	 */
	int shadow = data->shadow_kind;
	if(shadow >= SHADOW_MAX){
		shadow = SHADOW_DEFAULT;
	}
	int is_fix_size = data->font_scaling != 1;
	ret = (*ShadowFunc[shadow])(ret,item->color == CMD_COLOR_BLACK,is_fix_size);

	/*
	 * �A���t�@�l�̐ݒ�
	 */
	float alpha_t = 1.0;
	if(!data->opaque_comment){
		alpha_t = (((float)(item->no)/(item->chat->max_no)) * 0.4) + 0.6;
	}
	if(&item->chat->max_no == &data->optional.chat.max_no && data->optional_trunslucent){
		if(alpha_t>0.3) alpha_t = 0.3;			// ����ł����̂��ȁH�K���Ȃ񂾂��B
	}
	if(alpha_t<1.0){
		fprintf(data->log,"[comsurface/make]comment %4d set alpha:%5.2f%%.\n",item->no,alpha_t*100.0f);
		setAlpha(ret,alpha_t);
	}
	fprintf(data->log,"[comsurface/make]comment %4d builded (%d, %d), %d line.\n",item->no,ret->w,ret->h,nb_line);

 if (data->original_resize){
	// ������΂��]��
	/*
	 * �X�P�[���ݒ�
	 * ���� zoomx
	 * ���� zoomy	�����I��ratio(%)���w�肷��
	 */

	double zoomx = 1.0;
	double zoomy = (double)(data->font_h_fix_r)/100.0;
	//�k��

	int auto_scaled = FALSE;
	if(data->fontsize_fix){
		zoomx = (double) video_width / (double)(data->nico_width_now * data->font_scaling);
		//zoomx = (0.5f * (double)video_width) / (double)NICO_WIDTH;
		//zoomy = (0.5f * (double)video_height) / (double)NICO_HEIGHT;
		if(zoomx != 1.0){
			auto_scaled = TRUE;
		}
	}

	int saccubus_resized = FALSE;
	/*�X�P�[���̒���*/
	//if(((double)ret->h * zoomy) > ((double)video_height/3.0f)){
	if(((double)ret->h * zoomx) > ((double)video_height/3.0f)){
		zoomx *= 0.5f;
		//zoomy *= 0.5f;
		// 	�R�����g�̉摜�̍���������̍����̂P�^�R���傫���Ɣ{�����P�^�Q�ɂ���
		//  ������΂��Ǝ����T�C�Y�H�@���̍����́@�H
		//  ���������ā@���s���T�C�Y�H
		saccubus_resized =TRUE;
	}
	int videowidth_resized = FALSE;
	if(item->location != CMD_LOC_DEF && (ret->w * zoomx) > (double)video_width){
		double scale = ((double)video_width) / (ret->w * zoomx);
		zoomx *= scale;
		//zoomy *= scale;
		//  �R�����g�̕�������̕��Ɏ��܂�悤�ɔ{���𒲐��@���@�ՊE�����T�C�Y
		videowidth_resized = TRUE;
	}
	//  ���s���T�C�Y�@������
	//  �_�u�����T�C�Y�@������

	zoomy *= zoomx;

	//��ʃT�C�Y�ɍ��킹�ĕύX
	if(zoomx != 1.0f || zoomy != 1.0f){
	//if(zoomx != 1.0f){
		fprintf(data->log,"[comsurface/make]comment %4d resized.(%5.2f%%,%5.2f%%)\n",item->no,zoomx*100,zoomy*100);
		//fprintf(data->log,"[comsurface/make]comment %04d resized.(%5.2f%%)\n",item->no,zoomx*100);
		fflush(data->log);
		SDL_Surface* tmp = ret;
		ret = zoomSurface(tmp,zoomx,zoomy,SMOOTHING_ON);
		SDL_FreeSurface(tmp);
	}

	FILE* log = data->log;
	fprintf(log,"[comsurface/make]comment %4d w:%d, h:%d, cmd:%d, full:%1d, resize_limit_w: %d, view_limit_w:%dpx\n",item->no,ret->w,ret->h,item->location,(item->full != 0),video_width,video_width);
	fprintf(log,"                 comment %4d resize is ",item->no);
	if(saccubus_resized){
		fputs("linefeed, ",log);
	}
	if(videowidth_resized){
		fputs("limit_width, ",log);
	}
	if(FALSE){
		fputs("double, ",log);
	}
	if(auto_scaled){
		fputs("auto ",log);
	}
	fputs("\n",log);
	fflush(log);

	return ret;

 }
	// ����
	// �Q�l 1 pt = 1/72 inch, 1 px = 1 dot
	/*
	 * �X�P�[���ݒ�
	 */
	double zoomx = 1.0f;

	// �t�H���g�T�C�Y���Q�{�ɂ������H
	if(data->font_scaling == 2){
		zoomx = 0.5f;
	} else if(data->font_scaling != 1){
		zoomx /= (double)data->font_scaling;
	}

	int limit_width_resized = FALSE;
	int linefeed_resized = FALSE;
	int double_resized = FALSE;
	int auto_scaled = FALSE;

	// ���s���T�C�Y
	if(data->linefeed_resize){
		if (item->location != CMD_LOC_DEF && (nb_line >= LINEFEED_RESIZE_LIMIT[item->size])){
			linefeed_resized = TRUE;
			zoomx *= 0.5f;
		}
	}

	// �ՊE�����T�C�Y
	// �ՊE���͓��{���̓����544(512�`600)px  ���悪4:3��16:9�ɖ��֌W
	// �@�@�@�@full�R�}���h��672(640�`?)
	// test1: ���ʂɌ��؂�Ȃ��悤�ɓ��敝�ɂ���
	// �����̑傫���ŗՊE���͕ϓ�����
	double nicolimit_width;
	if(item->full){
	//	nicolimit_width = (double)data->nico_limit_width_full;
		nicolimit_width = (double)NICO_WIDTH_WIDE;
	} else {
	//	nicolimit_width = (double)data->nico_limit_width;
		nicolimit_width = (double)NICO_WIDTH;
	}
	if(data->limitwidth_resize){
		// �R�����g�̕�������̕�(�܂��͎w��̗ՊE��)�Ɏ��܂�悤�ɔ{���𒲐�
		if(item->location != CMD_LOC_DEF && (ret->w * zoomx) > nicolimit_width){
			zoomx *= nicolimit_width / (ret->w * zoomx);;
			limit_width_resized = TRUE;
		}
	}

	//  �_�u�����T�C�Y
	if (data->double_resize){
		if(linefeed_resized && limit_width_resized){
			// ���s���T�C�Y���ՊE�����T�C�Y���{ �� ���s���T�C�Y�L�����Z��
			zoomx *= 2.0f;
			// ���̌��ʁA�������̗ՊE���͓��扡�����傫���Ȃ�
			//           ���؂�̉����͕ω����Ȃ��B
			double_resized = TRUE;
		}
	}

	// �t�H���g�T�C�Y��������
	// ���敝�ƃj�R�j�R����̕��̃X�P�[��
	double autoscale = (double)video_width / (double)data->nico_width_now;
	if(data->fontsize_fix && autoscale != 1.0f){
		zoomx *= autoscale;
		auto_scaled = TRUE;
	}

	// �����̒���
	double zoomy = zoomx * (double)(data->font_h_fix_r) / 100.0;

	FILE* log = data->log;
	//��ʃT�C�Y�ɍ��킹�ĕύX
	if(zoomx != 1.0f || zoomy != 1.0f){
	//if(zoomx != 1.0f){
		fprintf(log,"[comsurface/make]comment %4d resized.(%5.2f%%,%5.2f%%)\n",item->no,zoomx*100,zoomy*100);
		//fprintf(data->log,"[comsurface/make]comment %04d resized.(%5.2f%%)\n",item->no,zoomx*100);
		//fflush(log);
		SDL_Surface* zoomed = zoomSurface(ret,zoomx,zoomy,SMOOTHING_ON);
		SDL_FreeSurface(ret);
		ret = zoomed;
	}

	//����̉��������؂� 16:9����̂݁H
	// full�R�}���h�͍L�� 544��672px(���̏ꍇ�͌��؂�Ȃ�)
	int view_trimed = FALSE;
	// ���؂艡��
	int view_limit_width =  data->nico_limit_width * autoscale;
	if(item->full){
		view_limit_width = data->nico_limit_width_full * autoscale;
	}
	// naka�R�}���h�͌��؂�Ȃ�
	if(item->location != CMD_LOC_DEF && item->full == 0 &&
		ret->w > view_limit_width){
		surftrimWidth(ret, view_limit_width);
		view_trimed = TRUE;
	}
	fprintf(log,"[comsurface/make]comment %4d w:%d, h:%d, cmd:%d, full:%1d, resize_limit_w: %d, view_limit_w:%dpx  ",item->no,ret->w,ret->h,item->location,(item->full != 0),data->nico_limit_width,view_limit_width);
	fprintf(log,"                 comment %4d resize is ",item->no);
	if(linefeed_resized){
		fputs("linefeed, ",log);
	}
	if(limit_width_resized){
		fputs("limit_width, ",log);
	}
	if(double_resized){
		fputs("double, ",log);
	}
	if(auto_scaled){
		fputs("auto ",log);
	}
	if(view_trimed){
		fputs("view_trimed  ",log);
	}
	fputs("\n",log);
	fflush(log);

	return ret;
}

/**
 * ������`��
 */

SDL_Surface* drawText(DATA* data,int size,int color,Uint16* str){
	if(str[0] == '\0'){
		return SDL_CreateRGBSurface(	SDL_SRCALPHA | SDL_HWSURFACE | SDL_HWACCEL,
										0,data->fixed_font_size[size],32,
											#if SDL_BYTEORDER == SDL_BIG_ENDIAN
													0xff000000,
													0x00ff0000,
													0x0000ff00,
													0x000000ff
											#else
													0x000000ff,
													0x0000ff00,
													0x00ff0000,
													0xff000000
											#endif
									);
	}
	/*
	SDL_Surface* fmt = SDL_CreateRGBSurface(	SDL_SRCALPHA | SDL_HWSURFACE | SDL_HWACCEL,
												0,
												0,
												32,
												#if SDL_BYTEORDER == SDL_BIG_ENDIAN
														0xff000000,
														0x00ff0000,
														0x0000ff00,
														0x000000ff
												#else
														0x000000ff,
														0x0000ff00,
														0x00ff0000,
														0xff000000
												#endif
											);

	SDL_Surface* tmp = TTF_RenderUNICODE_Blended(data->font[size],str,COMMENT_COLOR[color]);
	SDL_SetAlpha(tmp,SDL_SRCALPHA | SDL_RLEACCEL,0xff);
	SDL_Surface* surf = SDL_ConvertSurface(tmp,fmt->format,SDL_SRCALPHA | SDL_HWSURFACE);
	SDL_FreeSurface(tmp);
	SDL_FreeSurface(fmt);
	*/
	SDL_Surface* surf = TTF_RenderUNICODE_Blended(data->font[size],str,COMMENT_COLOR[color]);
	return surf;
}

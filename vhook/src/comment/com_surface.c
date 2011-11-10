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


//SDL_Surface* drawText(DATA* data,int size,int color,Uint16* str);
SDL_Surface* drawText2(DATA* data,int size,SDL_Color color,Uint16* str);

SDL_Surface* makeCommentSurface(DATA* data,const CHAT_ITEM* item,int video_width,int video_height){
	Uint16* index = item->str;
	Uint16* last = item->str;
	SDL_Surface* ret = NULL;
	//int color = item->color;
	SDL_Color rgba = item->color24;
	int size = item->size;
	int nb_line = 1;
	FILE* log = data->log;
	double font_height_rate = data->font_h_fix_r;
	int nico_width = data->nico_width_now;

	/*
	 * �e�͒u���Ă����āA�Ƃ肠���������̕`��
	 */
	while(*index != '\0'){
		if(*index == '\n'){
			*index = '\0';//�����ň�U�؂�
			if(ret == null){//���ǉ��s�͖���
				ret = drawText2(data,size,rgba,last);
			}else{/*���s����*/
				ret = connectSurface(ret,drawText2(data,size,rgba,last));
				nb_line++;
			}
			*index = '\n';//�����ň�U�؂�
			last = index+1;
		}
		index++;
	}
	if(ret == null){//���ǉ��s�͖���
		ret = drawText2(data,size,rgba,item->str);
	}else{/*���s����*/
		ret = connectSurface(ret,drawText2(data,size,rgba,last));
		nb_line++;
	}

	if(ret->w == 0 || ret->h == 0){
		fprintf(log,"[comsurface/makeE]comment %d has no char.\n",item->no);
		fflush(log);
		return ret;
	}

	/*
	 * �e����
	 */
	int shadow = data->shadow_kind;
	if(shadow >= SHADOW_MAX){
		shadow = SHADOW_DEFAULT;
	}
	int is_black = item->color == CMD_COLOR_BLACK;
	ret = (*ShadowFunc[shadow])(ret,is_black,FALSE);

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
		fprintf(log,"[comsurface/make1]comment %d set alpha:%5.2f%%.\n",item->no,alpha_t*100.0f);
		setAlpha(ret,alpha_t);
	}
	fprintf(log,"[comsurface/make2]comment %d builded (%d, %d) %d line  ",item->no,ret->w,ret->h,nb_line);
	fprintf(log,"video (%d, %d) original_resize %1d\n",video_width,video_height,data->original_resize);

	// ���T�C�Y���ɖ��֌W�ȃX�P�[���v�Z
	double autoscale = (double)video_width / (double)nico_width;
	int auto_scaled = FALSE;
	int linefeed_resized = FALSE;
	int limit_width_resized = FALSE;
	int double_resized = FALSE;
	/*
	 * �ՊE���͓��{���̓����544(512�`600)px  ���悪4:3��16:9�ɖ��֌W
	 * �@�@�@�@full�R�}���h��672(640�`?)
	 */
	int nicolimit_width = NICO_WIDTH;
	if(item->full){
		nicolimit_width = NICO_WIDTH_WIDE;
	}
	if (data->original_resize){
		/*
		 * ������΂��]��
		 *
		 * �X�P�[���ݒ�
		 * ���� zoomx
		 * ���� zoomy	�����I��ratio(%)���w�肷��
		 */

		double zoomx = 1.0f;
		double zoomy = (double)font_height_rate;
		//�k��

		if(data->fontsize_fix){
			zoomx = autoscale;
			//zoomx = (0.5 * (double)video_width) / (double)data->nico_width_now;
			//zoomx = (0.5f * (double)video_width) / (double)NICO_WIDTH;
			//zoomy = (0.5f * (double)video_height) / (double)NICO_HEIGHT;
			if(zoomx < 0.9f || zoomx > 1.1f){
				auto_scaled = TRUE;
			}
		}

		/*�X�P�[���̒���*/
		nicolimit_width *= autoscale;
		// ���s���T�C�Y
		// �R�����g�̉摜�̍������j�R�j�R�����̍����̂P�^�R���傫���Ɣ{�����P�^�Q�ɂ���
		int nico_limit_height = (NICO_HEIGHT/3) * autoscale + 1;
		if((int)(ret->h * zoomx) > nico_limit_height){
			// �_�u�����T�C�Y����
			// ���s���T�C�Y�����s��̔{���ŗՊE���𒴂����ꍇ �� ���s���T�C�Y�L�����Z��
			if(item->location != CMD_LOC_DEF && (int)(LINEFEED_RESIZE_SCALE[size] * zoomx * ret->w) > nicolimit_width){
				//  �_�u�����T�C�Y���� �� ���s���T�C�Y�L�����Z��
				nicolimit_width <<= 1;
				double_resized = TRUE;
			} else{
				// �_�u�����T�C�Y�Ȃ�
				zoomx *= LINEFEED_RESIZE_SCALE[size];
				linefeed_resized =TRUE;
			}
		}
		if(item->location != CMD_LOC_DEF){
			/* ue shita�R�}���h�̂݃��T�C�Y���� */
			/*
			 * �ՊE�����T�C�Y
			 * �ՊE���͓��{���̓����544(512�`600)px  ���悪4:3��16:9�ɖ��֌W
			 * �@�@�@�@full�R�}���h��672(640�`?)
			 * �����̑傫���ŗՊE���͕ϓ����適���m�ɍ��킹��̂͌���ł͖����H
			 * �R�����g�̕�������̕��Ɏ��܂�悤�ɔ{���𒲐�
			 * �_�u�����T�C�Y�@���@�������Ƀ��T�C�Y�i����ς݁j
			 * ���s���T�C�Y�@���@�������ɂȂ��i����ς݁j
			 * �����Ȃ��@���@���񔻒�
			 */
			double scale = (double)nicolimit_width / (ret->w * zoomx);
			if(double_resized){
				//�_�u�����T�C�Y���ɂ͗ՊE���͂Q�{��
				zoomx *= scale;
			} else if(!linefeed_resized && scale < 1.0f){
				// �k��
				zoomx *= scale;
				limit_width_resized = TRUE;
			}
	   }
		// ue shita�R�}���h�̂݃��T�C�Y�I���
		zoomy *= zoomx;

		// ��ʃT�C�Y�ɍ��킹�ĕύX
		if(zoomx != 1.0f || zoomy != 1.0f){
			fprintf(log,"[comsurface/make3]comment %d resized.(%5.2f%%,%5.2f%%)\n",item->no,zoomx*100,zoomy*100);
			fflush(log);
			SDL_Surface* tmp = ret;
			ret = zoomSurface(tmp,zoomx,zoomy,SMOOTHING_ON);
			SDL_FreeSurface(tmp);
		}

		fprintf(log,"[comsurface/make4]comment %d (%d, %d) loc=%d size=%d full=%d line=%d limit=%d ",
			item->no,ret->w,ret->h,item->location,item->size,item->full,nb_line,nicolimit_width);
		if(double_resized){
			fputs(" DoubleResize",log);
		} else if(linefeed_resized){
			fputs(" LinefeedResize",log);
		} else if(limit_width_resized){
			fputs(" LimitWidthResize",log);
		}
		if(auto_scaled){
			fputs(" AutoScale",log);
		}
		fputs("\n",log);
		fprintf(log,"[comsurface/make5]comment %d font_height:%d%%, nico_width:%d\n",
			item->no,(int)(font_height_rate * 100),nico_width);
		fflush(log);

		return ret;

	 }

	/*�����A�X�P�[���ݒ�̓��T�C�Y��̒l���g��*/
	double zoomx = 1.0f;
	double zoomy = 1.0f;
	int zoom_w = ret->w;
	int zoom_h = ret->h;

	/*
	 * �ՊE���͓��{���̓����544(512�`600)px  ���悪4:3��16:9�ɖ��֌W
	 * �@�@�@�@full�R�}���h��672(640�`?)
	 * �����̑傫���ŗՊE���͕ϓ����適�j�R���ɍ��킹��̂͌���ł͖����H
	 * �����I�Ɏw�肵�Ă݂�
	 */
	nicolimit_width = NICO_WIDTH;
	if(item->full){
		nicolimit_width = NICO_WIDTH_WIDE;
	}
	//nicolimit_width += 32;	// 512->544, 640->672

	if (nb_line >= LINEFEED_RESIZE_LIMIT[size]){
		/*
		 * ���s���T�C�Y���� �_�u�����T�C�Y����
		 * ���s���T�C�Y�����s��̔{���ŉ��s�ՊE��(�����I�ݒ�2)�𒴂����ꍇ �� ���s���T�C�Y�L�����Z��
		 */
		if(item->location != CMD_LOC_DEF && (zoom_w * LINEFEED_RESIZE_SCALE[size]) > nicolimit_width){
			// �_�u�����T�C�Y����
			double_resized = TRUE;
			//�_�u�����T�C�Y���ɂ͓��敝�̂Q�{(�����I�ݒ�R)�Ƀ��T�C�Y����锤
			nicolimit_width <<= 1;
		}
		if(!double_resized){
			// �_�u�����T�C�Y�Ȃ�
			linefeed_resized = TRUE;
			zoom_w *= LINEFEED_RESIZE_SCALE[size];
			//zoom_h *= LINEFEED_RESIZE_SCALE[size];
		}
	}

	if(item->location != CMD_LOC_DEF){
		// ue shita�R�}���h�̂݃��T�C�Y����

		/*
		 * �ՊE�����T�C�Y
		 * �����̑傫���ŗՊE���͕ϓ�����
		 * �R�����g�̕����ՊE��(�܂���2�{)�Ɏ��܂�悤�ɔ{���𒲐�
		 * ���s���T�C�Y�@���@�������ɂȂ��i����ς݁j
		 * �_�u�����T�C�Y�@���@nicolimit_width��2�{�� �Ŕ���
		 * �����Ȃ��@���@���񔻒�
		 */
		if(!linefeed_resized && zoom_w > nicolimit_width){
			zoom_w = nicolimit_width;
			//zoom_h = (ret->h * nicolimit_width) / ret->w;
			if((ret->h * nicolimit_width) / ret->w < 8){
				//zoom_h = 8;
				// �u���E�U��ʂŃt�H���g����8px(6pt)���Ⴍ�͂Ȃ�Ȃ���
				zoom_w = (ret->w * 8) / ret->h;
			}
			limit_width_resized = TRUE;
		}
	}
	// ue shita�R�}���h�̂݃��T�C�Y�I���

	/*
	 * �t�H���g�T�C�Y��������
	 * ���敝�ƃj�R�j�R����̕��̃X�P�[��
	 */
	if(data->fontsize_fix && video_width != nico_width){
		zoom_w = (zoom_w * video_width) / nico_width;
		auto_scaled = TRUE;
	}

	// �����F�t�H���g���̒���
	zoom_h = (ret->h * zoom_w * font_height_rate) / ret->w;

	//�j�R���v���C���[��������傫���̂ŉ����̂ݕ␳
/*
	if(item->location!=CMD_LOC_DEF){
		zoom_w = (zoom_w * nico_width)/(nico_width+32);
	}
*/
	//��ʃT�C�Y�ɍ��킹�ĕύX
	if(zoom_w != ret->w || zoom_h != ret->h){
		zoomx = (double)zoom_w/(double)ret->w;
		zoomy = (double)zoom_h/(double)ret->h;
		fprintf(log,"[comsurface/make3]comment %d resized.(%5.2f%%,%5.2f%%)\n",item->no,zoomx*100,zoomy*100);
		SDL_Surface* tmp = ret;
		ret = zoomSurface(tmp,zoomx,zoomy,SMOOTHING_ON);
		SDL_FreeSurface(tmp);
	}

	fprintf(log,"[comsurface/make4]comment %d (%d, %d) loc=%d size=%d full=%d line=%d limit=%d ",
		item->no,ret->w,ret->h,item->location,item->size,item->full,nb_line,nicolimit_width);
	if(double_resized){
		fputs(" DoubleResize",log);
	} else if(linefeed_resized){
		fputs(" LinefeedResize",log);
	} else if(limit_width_resized){
		fputs(" LimitWidthResize",log);
	}
	if(auto_scaled){
		fputs(" AutoScale",log);
	}
	fputs("\n",log);
	fprintf(log,"[comsurface/make5]comment %d font_height:%d%%, nico_width:%d\n",
		item->no,(int)(font_height_rate * 100),nico_width);
	fflush(log);

	return ret;
}

/**
 * ������`��
 */
/*
SDL_Surface* drawText(DATA* data,int size,int color,Uint16* str){
	if(str[0] == '\0'){
		return SDL_CreateRGBSurface(	SDL_SRCALPHA | SDL_HWSURFACE | SDL_HWACCEL,
										0,data->font_pixel_size[size],32,
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
	/ *
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
	*//*
	SDL_Surface* surf = TTF_RenderUNICODE_Blended(data->font[size],str,COMMENT_COLOR[color]);
	return surf;
}
*/
SDL_Surface* drawNullSurface(int w,int h);

SDL_Surface* drawText2(DATA* data,int size,SDL_Color rgba,Uint16* str){
	if(str == NULL || str[0] == '\0'){
		return drawNullSurface(0,data->font_pixel_size[size]);
	} else {
		SDL_Surface* surf = TTF_RenderUNICODE_Blended(data->font[size],str,rgba);
		SDL_SetAlpha(surf,SDL_SRCALPHA | SDL_RLEACCEL,0xff);
		SDL_Surface* ret = drawNullSurface(surf->w,data->font_pixel_size[size]);
		SDL_BlitSurface(surf,NULL,ret,NULL);
		// �e�L�X�g������font_pixel_size[size]�ɂȂ锤
		SDL_FreeSurface(surf);
		return ret;
	}
}

SDL_Surface* drawNullSurface(int w,int h){
	return SDL_CreateRGBSurface( SDL_SRCALPHA | SDL_HWSURFACE | SDL_HWACCEL,
	                             w,h,32,
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

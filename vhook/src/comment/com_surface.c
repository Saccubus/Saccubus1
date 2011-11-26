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

SDL_Surface* drawText(DATA* data,int size,SDL_Color color,Uint16* str);

SDL_Surface* drawText2(DATA* data,int size,SDL_Color color,Uint16* str);

SDL_Surface* makeCommentSurface(DATA* data,const CHAT_ITEM* item,int video_width,int video_height){
	Uint16* index = item->str;
	Uint16* last = item->str;
	SDL_Surface* ret = NULL;
	int color = item->color;
	SDL_Color sdl_color;
	int is_black;
	if(item->color24 == 0){
		sdl_color = COMMENT_COLOR[color];
		is_black = (color == CMD_COLOR_BLACK);
	} else {
		// color should be 24bit RGB
		sdl_color.r = (color >> 16) & 0xff;
		sdl_color.g = (color >> 8) & 0xff;
		sdl_color.b = (color) & 0xff;
		sdl_color.unused = 0;
		is_black = (color == 0x00000000);
	}
	// now, sdl_color is 32bit RGBA
	int size = item->size;
	int nb_line = 1;

	/*
	 * �e�͒u���Ă����āA�Ƃ肠���������̕`��
	 */
	while(*index != '\0'){
		if(*index == '\n'){
			*index = '\0';//�����ň�U�؂�
			if(ret == null){//���ǉ��s�͖���
				ret = drawText2(data,size,sdl_color,last);
			}else{/*���s����*/
				ret = connectSurface(ret,drawText2(data,size,sdl_color,last));
				nb_line++;
			}
			*index = '\n';//�����ň�U�؂�
			last = index+1;
		}
		index++;
	}
	if(ret == null){//���ǉ��s�͖���
		ret = drawText2(data,size,sdl_color,item->str);
	}else{/*���s����*/
		ret = connectSurface(ret,drawText2(data,size,sdl_color,last));
		nb_line++;
	}

	if(ret->w == 0 || ret->h == 0){
		fprintf(data->log,"[comsurface/make]comment %-4d has no char.\n",item->no);
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
		fprintf(data->log,"[comsurface/make]comment %-4d set alpha:%5.2f%%.\n",item->no,alpha_t*100.0f);
		setAlpha(ret,alpha_t);
	}
	fprintf(data->log,"[comsurface/make]comment %-4d builded (%d, %d), %d line\n",item->no,ret->w,ret->h,nb_line);
#if DEBUG == 1
	fprintf(data->log,"[comsurface/DEBUG]comment %-4d video (%d, %d), original?%1d\n",item->no,video_width,video_height,data->original_resize);
#endif

	// ���T�C�Y���ɖ��֌W�ȃX�P�[���v�Z
	double autoscale = (double)video_width / (double)data->nico_width_now;
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
		 */

		double zoomx = 1.0f;

		if(data->fontsize_fix){
			zoomx = autoscale;
			if(zoomx != 1.0){
				auto_scaled = TRUE;
			}
		}

		/* ue shita�R�}���h�̂݃��T�C�Y���� */
		if(item->location != CMD_LOC_DEF){
			/*
			 * �X�P�[���̒���
			 */
			nicolimit_width *= autoscale;
			/*
			 *  ���s���T�C�Y
			 * 	�R�����g�̉摜�̍������j�R�j�R�����̍����̂P�^�R���傫���Ɣ{�����P�^�Q�ɂ���
			 */
			int nico_limit_height = (NICO_HEIGHT/3) * autoscale + 1;
			if((int)(ret->h * zoomx) > nico_limit_height){
				/*
				 * �_�u�����T�C�Y����
				 * ���s���T�C�Y�����s��̔{���ŗՊE���𒴂����ꍇ �� ���s���T�C�Y�L�����Z��
				 */
				if((int)(LINEFEED_RESIZE_SCALE * zoomx * ret->w) > nicolimit_width){
					//  �_�u�����T�C�Y���� �� ���s���T�C�Y�L�����Z��
					nicolimit_width /= LINEFEED_RESIZE_SCALE;
					double_resized = TRUE;
				} else{
					// �_�u�����T�C�Y�Ȃ�
					zoomx *= LINEFEED_RESIZE_SCALE;
					linefeed_resized =TRUE;
				}
			}

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

		/*
		 * ��ʃT�C�Y�ɍ��킹�ĕύX
		 */
		if(zoomx != 1.0f){
			fprintf(data->log,"[comsurface/make]comment %-4d resized.(%5.2f%%)\n",item->no,zoomx*100);
			fflush(data->log);
			SDL_Surface* tmp = ret;
			ret = zoomSurface(tmp,zoomx,zoomx,SMOOTHING_ON);
			SDL_FreeSurface(tmp);
		}

		FILE* log = data->log;
		fprintf(log,"[comsurface/make]comment %-4d (%d, %d) loc=%d size=%d full=%d line=%d limit=%d ",
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
	#if DEBUG == 1
		fprintf(log,"[comsurface/DEBUG]original %-4d  font_h_fix_r:%d, nico_width_now:%d, location:%d\n",
			item->no,data->font_height_rate,data->nico_width_now,item->location);
	#endif
		fflush(log);

		return ret;

	 }
	/*
	 * ����
	 * �X�P�[���ݒ�̓��T�C�Y��̒l���g��
	 */
	double zoomx = 1.0f;
	double zoomy = 1.0f;
	int zoom_w = ret->w;
	int zoom_h = ret->h;

/*
	// �t�H���g�T�C�Y���Q�{�ɂ������H
	if(data->font_scaling == 2){
		zoom_w >>= 1;
		zoom_h >>= 1;
	}
*/

	/*
	 * �ՊE���͓��{���̓����544(512�`600)px  ���悪4:3��16:9�ɖ��֌W
	 * �@�@�@�@full�R�}���h��672(640�`?)
	 * �����̑傫���ŗՊE���͕ϓ����適�j�R���ɍ��킹��̂͌���ł͖����H
	 * �����I�Ɏw�肵�Ă݂�
	 */
	nicolimit_width = data->limit_width[item->full];

	if(item->location != CMD_LOC_DEF){
		// ue shita�R�}���h�̂݃��T�C�Y����

		if (nb_line >= LINEFEED_RESIZE_LIMIT[item->size]){
			/*
			 * �_�u�����T�C�Y����
			 * ���s���T�C�Y�����s��̔{���ŉ��s�ՊE��(�����I�ݒ�2)�𒴂����ꍇ �� ���s���T�C�Y�L�����Z��
			 */
			if(data->double_resize && (zoom_w * LINEFEED_RESIZE_SCALE) > data->double_resize_width[item->full]){
				// �_�u�����T�C�Y����
				double_resized = TRUE;
				//�_�u�����T�C�Y���ɂ͓��敝�̂Q�{(�����I�ݒ�R)�Ƀ��T�C�Y����锤
				nicolimit_width = data->double_limit_width[item->full];	// double_limit_width >= 2*doouble_resize_width
			}
			if(!double_resized){
				// �_�u�����T�C�Y�Ȃ�
				linefeed_resized = TRUE;
				if(data->linefeed_resize){
					zoom_w *= LINEFEED_RESIZE_SCALE;
					zoom_h *= LINEFEED_RESIZE_SCALE;
				}
			}
		}

		/*
		 * �ՊE�����T�C�Y
		 * �����̑傫���ŗՊE���͕ϓ�����
		 */
		if(data->limitwidth_resize){
			/*
			 * �R�����g�̕����ՊE��(�܂���2�{)�Ɏ��܂�悤�ɔ{���𒲐�
			 * ���s���T�C�Y�@���@�������ɂȂ��i����ς݁j
			 * �_�u�����T�C�Y�@���@nicolimit_width��2�{�� �Ŕ���
			 * �����Ȃ��@���@���񔻒�
			 */
			if(!linefeed_resized && zoom_w > nicolimit_width){
				zoom_h = (zoom_h * nicolimit_width) / zoom_w;
				if(zoom_h < 8){
					zoom_h = 8;
					// �u���E�U��ʂŃt�H���g����8px(6pt)���Ⴍ�͂Ȃ�Ȃ���
				}
				zoom_w = (zoom_w * zoom_h) / ret->h;	// �Čv�Z
				limit_width_resized = TRUE;
			}
		}
	}
	// ue shita�R�}���h�̂݃��T�C�Y�I���

	/*
	 * �t�H���g�T�C�Y��������
	 * ���敝�ƃj�R�j�R����̕��̃X�P�[��
	 */
	if(data->fontsize_fix && video_width != data->nico_width_now){
		zoom_w *= autoscale;
		zoom_h *= autoscale;
		auto_scaled = TRUE;
	}

	// �����F�t�H���g���̒���
	if(data->font_height_rate != 100){
		zoom_h = (zoom_h * data->font_height_rate) / 100;
	}

	FILE* log = data->log;
	//��ʃT�C�Y�ɍ��킹�ĕύX
	if(zoom_w != ret->w || zoom_h != ret->h){
		zoomx = (double)zoom_w/(double)ret->w;
		zoomy = (double)zoom_h/(double)ret->h;
		fprintf(log,"[comsurface/make]comment %-4d resized.(%5.2f%%,%5.2f%%)\n",item->no,zoomx*100,zoomy*100);
		SDL_Surface* tmp = ret;
		ret = zoomSurface(tmp,zoomx,zoomy,SMOOTHING_ON);
		SDL_FreeSurface(tmp);
	}

	fprintf(log,"[comsurface/make]comment %-4d (%d, %d) loc=%d size=%d full=%d line=%d limit=%d ",
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
#if 1
	fprintf(log,"[comsurface/DEBUG]new %-4d  font_h_fix_r:%d, nico_width_now:%d, location:%d, nicolimit_width:%d, (%d,%d)\n",
		item->no,data->font_height_rate,data->nico_width_now,item->location,nicolimit_width,zoom_w,zoom_h);
#endif
	fflush(log);

	return ret;
}

/**
 * ������`��
 */

SDL_Surface* drawText(DATA* data,int size,SDL_Color color,Uint16* str){
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
	SDL_Surface* surf = TTF_RenderUNICODE_Blended(data->font[size],str,color);
	return surf;
}

SDL_Surface* drawNullSurface(int w,int h);

SDL_Surface* drawText2(DATA* data,int size,SDL_Color color,Uint16* str){
	if(str == NULL || str[0] == '\0'){
		return drawNullSurface(0,data->font_pixel_size[size]);
	} else {
		SDL_Surface* surf = TTF_RenderUNICODE_Blended(data->font[size],str,color);
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

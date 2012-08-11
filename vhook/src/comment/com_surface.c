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
#include "../unicode/uniutil.h"
#include "adjustComment.h"
#include "render_unicode.h"

SDL_Surface* arrangeSurface(SDL_Surface* left,SDL_Surface* right);
//SDL_Surface* drawText(DATA* data,int size,int color,Uint16* str);
SDL_Surface* drawText2(DATA* data,int size,SDL_Color color,Uint16* str);
SDL_Surface* drawText3(DATA* data,int size,SDL_Color color,int fontsel,Uint16* from,Uint16* to);
SDL_Surface* drawText4(DATA* data,int size,SDL_Color color,TTF_Font* font,Uint16* str,int fontsel);
int cmpSDLColor(SDL_Color col1, SDL_Color col2);
int isDoubleResize(double width, double limit_width, int size, int line, FILE* log);

SDL_Surface* makeCommentSurface(DATA* data,const CHAT_ITEM* item,int video_width,int video_height){
	Uint16* index = item->str;
	Uint16* last = item->str;
	SDL_Surface* ret = NULL;
	SDL_Color SdlColor = item->color24;
	int size = item->size;
	int location = item->location;
	int nb_line = 1;
	FILE* log = data->log;
	int debug = data->debug;
	double font_width_rate = data->font_w_fix_r;
	double font_height_rate = data->font_h_fix_r;
	int nico_width = data->nico_width_now;
	int color = item->color;

	if(item->script){
		int cmd = item->script & 0xffff0000;
		if(cmd == SCRIPT_DEFAULT){		//���f�t�H���g
			if(color != CMD_COLOR_DEF)
				data->defcolor = color;
			if(location != CMD_LOC_DEF)
				data->deflocation = location;
			if(size != CMD_FONT_DEF)
				data->defsize = size;
			return drawNullSurface(0,0);
		}
		if(cmd == SCRIPT_GYAKU){	//���t
			int bits = item->script & 3;
			if(bits & SCRIPT_OWNER){
				data->owner.chat.to_left = -1;
			}
			if(bits & SCRIPT_USER){
				data->user.chat.to_left = -1;
				data->optional.chat.to_left = -1;
			}
			return drawNullSurface(0,0);
		}
	}
	/*
	 * default color�ύX
	 */
	if(data->defcolor>401){	//401 means April 01, i.e. force april fool
		color = data->defcolor - 401;
		SdlColor = getSDL_color(color);
	}else if(color==CMD_COLOR_DEF){	//this may be @default
		color = data->defcolor;
		SdlColor = getSDL_color(color);
	}
	/*
	 * default size �ύX
	 */
	if(size == CMD_FONT_DEF){
		size = data->defsize;
	}
	/*
	 * default lcation �ύX
	 */
	if(location == CMD_LOC_DEF){
		location = data->deflocation;
	}
	/*
	 * �e�͒u���Ă����āA�Ƃ肠���������̕`��
	 */
	while(*index != '\0'){
		if(*index == '\n'){
			*index = '\0';//�����ň�U�؂�
			if(ret == null){//�ŏ��̉��s
				ret = drawText2(data,size,SdlColor,last);
				if(ret!=NULL && debug)
					fprintf(log,"[comsurface/make.0]drawText2 surf(%d, %d) %s\n",ret->w,ret->h,COM_FONTSIZE_NAME[size]);
			}else{/*���s����*/
				ret = connectSurface(ret,drawText2(data,size,SdlColor,last));
				nb_line++;
				if(ret!=NULL && debug)
					fprintf(log,"[comsurface/make.1]connectSurface surf(%d, %d) %s line %d\n",ret->w,ret->h,COM_FONTSIZE_NAME[size],nb_line);
			}
			*index = '\n';//�����ň�U�؂�
			last = index+1;
		}
		index++;
	}
	if(ret == null){//���ǉ��s�͖���
		ret = drawText2(data,size,SdlColor,item->str);
		if(ret==NULL)
			return NULL;
		if(debug)
			fprintf(log,"[comsurface/make.2]drawText2 surf(%d, %d) %s\n",ret->w,ret->h,COM_FONTSIZE_NAME[size]);
	}else{/*���s����*/
		ret = connectSurface(ret,drawText2(data,size,SdlColor,last));
		if(ret==NULL)
			return NULL;
		nb_line++;
		if(debug)
			fprintf(log,"[comsurface/make.3]connectSurface surf(%d, %d) %s line %d\n",ret->w,ret->h,COM_FONTSIZE_NAME[size],nb_line);
	}

	if(ret==NULL || ret->h == 0){
		fprintf(log,"***ERROR*** [comsurface/makeE]comment %d has no char.\n",item->no);
		fflush(log);
		return ret;
	}
	fprintf(log,"[comsurface/make0]comment %d build(%d, %d) %s %d line%s%s%s.\n",
		item->no,ret->w,ret->h,COM_FONTSIZE_NAME[size],nb_line,
		(data->original_resize ? "": " dev"),(data->enableCA?" CA":""),(data->fontsize_fix?" fix":""));

	/*
	 * �e����
	 */
	int shadow = data->shadow_kind;
	if(shadow >= SHADOW_MAX){
		shadow = SHADOW_DEFAULT;
	}
	int is_black = cmpSDLColor(SdlColor, COMMENT_COLOR[CMD_COLOR_BLACK]);
	if(strstr(data->extra_mode,"font")!=NULL && strstr(data->extra_mode,"fg")!=NULL){
		is_black = 2;	//SHADOW COLOR is FONT
	}
	ret = (*ShadowFunc[shadow])(ret,is_black,data->fontsize_fix,SdlColor);
	fprintf(log,"[comsurface/make1]ShadowFunc:%d (%d, %d) %s %d line\n",shadow,ret->w,ret->h,COM_FONTSIZE_NAME[size],nb_line);

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
		fprintf(log,"[comsurface/makeA]comment %d set alpha:%5.2f%%.\n",item->no,alpha_t*100.0f);
		setAlpha(ret,alpha_t);
	}

	// ���T�C�Y���ɖ��֌W�ȃX�P�[���v�Z
	double autoscale = data->width_scale;
	int auto_scaled = FALSE;
	int linefeed_resized = FALSE;
	int limit_width_resized = FALSE;
	int double_resized = FALSE;
	/*
	 * �ՊE���͓��{���̓����544(512�`600)px  ���悪4:3��16:9�ɖ��֌W
	 *  full�R�}���h��672(640�`?)
	 */
	double nicolimit_width = (double)NICO_WIDTH;
	if(item->full){
		nicolimit_width = (double)NICO_WIDTH_WIDE;
	}
	fprintf(log,"[comsurface/make3]comment %d (%d, %d) font_rate(%.0f%%,%.0f%%) nico_width:%d x%.3f\n",
		item->no,ret->w,ret->h,font_width_rate*100.0,font_height_rate*100.0,nico_width,autoscale);

	if (data->original_resize){
		/*
		 * ������΂��]��
		 *
		 * �X�P�[���ݒ�
		 * ���� zoomx
		 * ���� zoomy	�����I��ratio(%)���w�肷��
		 */

		double zoomx = font_width_rate;
		double zoomy;
		//�k��

		if(data->fontsize_fix || data->enableCA){
			zoomx *= autoscale;
			if(data->fontsize_fix){
				zoomx *= 0.5;
			}
			//zoomx = (0.5 * (double)video_width) / (double)data->nico_width_now;
			//zoomx = (0.5f * (double)video_width) / (double)NICO_WIDTH;
			//zoomy = (0.5f * (double)video_height) / (double)NICO_HEIGHT;
			if(autoscale != 1.0f){
				auto_scaled = TRUE;
			}
		}

		/*�X�P�[���̒���*/
		nicolimit_width *= autoscale;
		//	�R�����g�����␳
		int h = adjustHeight(nb_line,size,FALSE,data->fontsize_fix);
		if(h!=ret->h){
			ret = adjustComment(ret,data,h);
			fprintf(log,"[comsurface/adjust]comment %d adjust(%d, %d) %s\n",
				item->no,ret->w,ret->h,(data->fontsize_fix?" fix":""));
		}
		// ���s���T�C�Y
		// �R�����g�̉摜�̍������j�R�j�R�����̍����̂P�^�R���傫���Ɣ{�����P�^�Q�ɂ���
		if((int)(ret->h * zoomx) > (NICO_HEIGHT/3) * autoscale + 1){
			// �_�u�����T�C�Y����
			// ���s���T�C�Y�����s��̔{���ŗՊE���𒴂����ꍇ �� ���s���T�C�Y�L�����Z��
			double linefeed_zoom = linefeedResizeScale(size,nb_line,data->fontsize_fix);
			double resized_w = linefeed_zoom * zoomx * ret->w;
			if((location == CMD_LOC_TOP||location == CMD_LOC_BOTTOM)
				&& isDoubleResize(resized_w, nicolimit_width, size, nb_line, log)){
				//  �_�u�����T�C�Y���� �� ���s���T�C�Y�L�����Z��
				nicolimit_width /= linefeed_zoom;	//*= 2.0;
				double_resized = TRUE;
			} else{
				// �_�u�����T�C�Y�Ȃ�
				zoomx *= linefeed_zoom;	// *= 0.5
				linefeed_resized =TRUE;
			}
		}
/*
		//	�R�����g�����␳
		if(!linefeed_resized){
			int h = adjustHeight(nb_line,size,FALSE,data->fontsize_fix);
			if(h!=ret->h){
				ret = adjustComment(ret,data,h);
				fprintf(log,"[comsurface/adjust]comment %d adjust(%d, %d) %s\n",
					item->no,ret->w,ret->h,(data->fontsize_fix?" fix":""));
			}
		}
*/
		if(location == CMD_LOC_TOP||location == CMD_LOC_BOTTOM){
			/* ue shita�R�}���h�̂݃��T�C�Y���� */
			/*
			 * �ՊE�����T�C�Y
			 * �ՊE���͓��{���̓����544(512�`600)px  ���悪4:3��16:9�ɖ��֌W
			 *  full�R�}���h��672(640�`?)
			 * �����̑傫���ŗՊE���͕ϓ����適���m�ɍ��킹��̂͌���ł͖����H
			 *  dFS=(15,24,39),LW=(512,640)
			 *  rFS=round(LW/width*dFS) �ɂ���ĐV�����t�H���g�T�C�Y�����܂�B
			 *  �A��Windows�ł�wFS=rFS+1�i�����̏ꍇ�j�ł���B
			 * �R�����g�̕�������̕��Ɏ��܂�悤�ɔ{���𒲐�
			 * �_�u�����T�C�Y�@���@�������Ƀ��T�C�Y�i����ς݁j
			 * ���s���T�C�Y�@���@�������ɂȂ��i�Ĕ��聨�t�H���g�����k���j
			 * �����Ȃ��@���@���񔻒�
			 */
			double rate = nicolimit_width / (double)ret->w;
			if(linefeed_resized && zoomx > rate){
				fprintf(log,"[comsurface/LF]comment %d previous width %.0f rate %.2f%%%s\n",
					item->no,(double)ret->w * zoomx,rate * 100.0,(data->fontsize_fix?" fix":""));
				font_width_rate *= rate;
				zoomx = rate;
			}else
			if(!linefeed_resized && (double)ret->w * zoomx > nicolimit_width){
				//�_�u�����T�C�Y���ɂ͗ՊE���͂Q�{��
				// �k��
				zoomx = nicolimit_width / (double)ret->w;
				limit_width_resized = TRUE;
			}
		}
		// ue shita�R�}���h�̂݃��T�C�Y�I���
		zoomy = (zoomx / font_width_rate) * font_height_rate;

		// ��ʃT�C�Y�ɍ��킹�ĕύX
		if(zoomx != 1.0f || zoomy != 1.0f){
			fprintf(log,"[comsurface/make4]comment %d resized.(%5.2f%%,%5.2f%%)\n",item->no,zoomx*100,zoomy*100);
			fflush(log);
			SDL_Surface* tmp = ret;
			ret = zoomSurface(tmp,zoomx,zoomy,SMOOTHING_ON);
			SDL_FreeSurface(tmp);
			if(!ret){
				fprintf(log,"***ERROR*** [comsurface/makeZ]zoomSurface : %s\n",SDL_GetError());
				fflush(log);
				return NULL;
			}
		}

		fprintf(log,"[comsurface/make5]comment %d (%d, %d) %s %s %s %d lines %.0f nicolimit ",
			item->no,ret->w,ret->h,COM_LOC_NAME[location],COM_FONTSIZE_NAME[item->size],
			item->full?"full":"",nb_line,nicolimit_width);
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
		if(data->fontsize_fix){
			fputs(" FontFix",log);
		}
		fputs("\n",log);
		fflush(log);

		/*
		 * �g������H
		 */
		if(strstr(data->extra_mode,"-frame")!=NULL){
			SDL_Surface* tmp = ret;
			ret = drawFrame(data,item,tmp,RENDER_COLOR_BG,1);
			SDL_FreeSurface(tmp);
		}

		return ret;

	 }

	/*�����A�X�P�[���ݒ�̓��T�C�Y��̒l���g��*/
	double zoomx = 1.0f;
	double zoomy = 1.0f;
	double zoom_w = (double)ret->w;
	double zoom_h = (double)ret->h;
	zoom_w *= font_width_rate;
	zoom_h *= font_height_rate;
	/*
	 * �ՊE���͓��{���̓����544(512�`600)px  ���悪4:3��16:9�ɖ��֌W
	 * �@�@�@�@full�R�}���h��672(640�`?)
	 * �����̑傫���ŗՊE���͕ϓ����適�j�R���ɍ��킹��̂͌���ł͖����H
	 * �����I�Ɏw�肵�Ă݂�
	 */
	if(data->fontsize_fix){
		// nicolimit_width *= 2.0;
		zoom_w *= 0.5;
		zoom_h *= 0.5;
		//auto_scaled = TRUE;
	}
	//nico_width += 32;	// 512->544, 640->672

	//	�R�����g�����␳
	int h = adjustHeight(nb_line,size,FALSE,data->fontsize_fix);
	if(h!=ret->h){
		ret = adjustComment(ret,data,h);
		fprintf(log,"[comsurface/adjust]comment %d adjust(%d, %d) %s\n",
			item->no,ret->w,ret->h,(data->fontsize_fix?" fix":""));
	}
	double resized_w;
	if (nb_line >= LINEFEED_RESIZE_LIMIT[size]){
		/*
		 * ���s���T�C�Y���� �_�u�����T�C�Y����
		 * ���s���T�C�Y�����s��̔{���ŉ��s�ՊE��(nicolimit_width)�𒴂����ꍇ �� ���s���T�C�Y�L�����Z��
		 */
		double linefeed_zoom = linefeedResizeScale(size,nb_line,data->fontsize_fix);
		int dfs = COMMENT_FONT_SIZE[size];
		int rfs = (int)round(0.5*(double)dfs);
		double rsRate = (double)(rfs+1)/(double)(dfs+1);
		resized_w = zoom_w * linefeed_zoom;
		double resize = resized_w / zoom_w;
		fprintf(log,"[comsurface/LFresize]comment %d LFzoom %.0f%% LFrateFS %.0f%% %s rFS %d\n",
			item->no,linefeed_zoom*100.0,rsRate*100.0,COM_FONTSIZE_NAME[size],rfs);
		if((location == CMD_LOC_TOP||location == CMD_LOC_BOTTOM)
			&& isDoubleResize(resized_w, nicolimit_width, size, nb_line, log)){
			// �_�u�����T�C�Y����
			double_resized = TRUE;
			//�_�u�����T�C�Y���ɂ͓��敝�̂Q�{�Ƀ��T�C�Y����锤
			double double_limit_width = nicolimit_width / resize;	//*= 2.0;

			/*
			 * �_�u�����T�C�Y�̗ՊE�����T�C�Y
			 * �����̑傫���ŗՊE���͕ϓ�����
			 * �R�����g�̕����ՊE����2�{�Ɏ��܂�悤�ɔ{���𒲐�
			 */
			if(resized_w > nicolimit_width){
				/*
				 *  dFS=(15,24,39),LW=(512,640)
				 *  rFS=round(LW/width*dFS) �ɂ���ĐV�����t�H���g�T�C�Y�����܂�B
				 *  �A��Windows�ł�wFS=rFS+1�i�����̏ꍇ�j�ł���B
				 */
				rfs = (int)round(nicolimit_width/resized_w*(double)dfs);
				rsRate = (double)(rfs+1)/(double)(dfs+1);
				resized_w = zoom_w * rsRate;
				fprintf(log,"[comsurface/DR limit1]comment %d default width %.0f dFS %d resized %.0f limit %.0f\n",
					item->no,zoom_w,dfs,resized_w,nicolimit_width);
				zoom_w = resized_w;
				fprintf(log,"[comsurface/DR limit2]comment %d %s width %.0f rFS %d wrate %.0f%%\n",
					item->no,COM_FONTSIZE_NAME[size],zoom_w,rfs,rsRate*100.0);
				zoom_h = (zoom_w/(double)ret->w / font_width_rate) * font_height_rate * (double)ret->h;
				//zoomy = zoom_h/(double)ret->h;
			}

			//�Ӑ}�����_�u�����T�C�Y�Ȃ�΍�����Ń��T�C�Y���������ǂ��H
			//���ۂɂ͍�����1�s�������Č����Ȃ��s����邱�Ƃ�����
			double wrate = double_limit_width / zoom_w;
			double hrate = (double)NICO_HEIGHT / zoom_h;
			fprintf(log,"[comsurface/DR detail]comment %d w %.1f%% h %.1f%%%s\n",
				item->no,wrate*100.0,hrate*100.0,(data->fontsize_fix?" fix":""));
			if(size == CMD_FONT_BIG && 8 < nb_line && nb_line < 16){
				//�R�����g�s���ɂ�苸��
				double resized_h = COMMENT_BIG_DR_HEIGHT[nb_line];
				hrate = (double)resized_h / zoom_h;
				resized_w = zoom_w * hrate;
				fprintf(log,"[comsurface/DR AdjByWiki]comment %d maybe(%.0f,%.0f) w %.2f%% h %.2f%% font_width %.2f%%\n",
					item->no,resized_w,resized_h,wrate*100.0,hrate*100.0,font_width_rate*100.0);
				//�_�u�����T�C�Y���ĉ�ʓ��ɕ\�����o��Ƃ͎v���Ȃ��Ƃ���
				if(resized_w > nicolimit_width ){
					zoom_w = resized_w;
				}
			}
			else {
				double h2 = wrate / hrate;
				if(385 < zoom_h && zoom_h < 768){
					//�R�����g��������ȏ�ł���_�u�����T�C�Y�ɂ�蓮�捂�ɍ��킹���ƌ���B?
					resized_w = zoom_w * hrate;
					if(resized_w > nicolimit_width && resized_w > zoom_w){
						//�������傫���Ȃ�␳
						fprintf(log,"[comsurface/DR hrate1]comment %d resized_width %.0f %.2f%% font_width %.2f%%\n",
							item->no,resized_w,hrate*100.0,font_width_rate*100.0);
						zoom_w = resized_w;
					}else{
						//���̃}�}
						fprintf(log,"[comsurface/DR hrate0]comment %d resized_width %.0f %.2f%% font_width %.2f%%\n",
								item->no,resized_w,hrate*100.0,font_width_rate*100.0);
						//zoom_w = resized_w;
					}
				}else
				if(zoom_h <= 385){
					//�R�����g��������ȉ��ł��艡���Ō��߂邵���肪�Ȃ�������͊��Ɍv�Z�����͂��B
					fprintf(log,"[comsurface/DR wrate]comment %d  width %.0f %.2f%% font_width %.2f%%\n",
						item->no,zoom_w,wrate*100.0,font_width_rate*100.0);
				}
				//�ȉ��͓�����R�����g������������
				else
				if(0.75 <= h2 && h2 <= 1.5){
					//������ō���������������ɂȂ�Ȃ瓮�捂�ɍ��킹�� ���͂��Ȃ�
					//zoom_w *= hrate;
					fprintf(log,"[comsurface/DR hrate2]comment %d  width %.0f %.2f%% font_width %.2f%%\n",
						item->no,zoom_w,hrate*100.0,font_width_rate*100.0);
				}
				else
				{
					//�����ƃA�X�䂪����ƑS�R�Ⴄ�̂ō��킹���Ȃ�
					//zoom_w *= hrate;
					fprintf(log,"[comsurface/DR wrate2]comment %d  width %.0f %.2f%% font_width %.2f%%\n",
						item->no,zoom_w,wrate*100.0,font_width_rate*100.0);
				}
			}

		}else{
			// �_�u�����T�C�Y�Ȃ�
			linefeed_resized = TRUE;
			zoom_w = resized_w;	// *= 0.5
			//zoom_h *= linefeed_zoom;
			/* zoomx *= linefeedResizeScale(size,nb_line,data->fontsize_fix); */
		}
	}

	if(location == CMD_LOC_TOP||location == CMD_LOC_BOTTOM){
		// ue shita�R�}���h�̂݃��T�C�Y����

		/*
		 * �ՊE�����T�C�Y
		 * �����̑傫���ŗՊE���͕ϓ�����
		 * �R�����g�̕����ՊE��(�܂���2�{)�Ɏ��܂�悤�ɔ{���𒲐�
		 * ���s���T�C�Y�@���@�Ȃ��i����ς݁j�����A�����I�ɂ�����x�k��
		 * �_�u�����T�C�Y�@���@nicolimit_width��2�{�� �Ŕ���
		 * �����Ȃ��@���@���񔻒�
		 */
		if(linefeed_resized && zoom_w > nicolimit_width){
			fprintf(log,"[comsurface/AfterLF]comment %d previous width%.0f > limit%.0f, font_width %.2f%%%s\n",
				item->no,zoom_w,nicolimit_width,font_width_rate * 100.0,(data->fontsize_fix?" fix":""));
			//zoom_w = nicolimit_width;
		}
		if(!linefeed_resized && !double_resized && zoom_w > nicolimit_width){
			/*
			 *  dFS=(15,24,39),LW=(512,640)
			 *  rFS=round(LW/width*dFS) �ɂ���ĐV�����t�H���g�T�C�Y�����܂�B
			 *  �A��Windows�ł�wFS=rFS+1�i�����̏ꍇ�j�ł���B
			 *
			 */
			int dfs = data->font_pixel_size[size]>>data->fontsize_fix;
			double rsRate = (round(nicolimit_width/zoom_w*(double)dfs)+1.0)
					/ (double)(dfs+1);
			resized_w = zoom_w * rsRate;
			fprintf(log,"[comsurface/LWresize]comment %d previous width %.0f dFS %d resize %.1f%% resized %.0f limit %.0f\n",
				item->no,zoom_w,dfs,rsRate*100.0,resized_w,nicolimit_width);
			limit_width_resized = TRUE;
			zoom_w = resized_w;
			fprintf(log,"[comsurface/LWresize]comment %d width %.0f dFS %d wrate %.1f%%\n",
				item->no,zoom_w,dfs,rsRate*100.0);
		}
	}
	// ue shita�R�}���h�̂݃��T�C�Y�I���

	/*
	 * �t�H���g�T�C�Y��������
	 * ���敝�ƃj�R�j�R����̕��̃X�P�[��
	 */
	if(data->fontsize_fix || data->enableCA){
		if(video_width != nico_width){
			zoom_w *= autoscale;
			//zoom_h *= autoscale;
			// zoomx *= autoscale
			auto_scaled = TRUE;
		}
	}

	// �����F�t�H���g���E�����̒���
	zoomx = zoom_w/(double)ret->w;
	//zoomy = zoom_h/(double)ret->h;
	zoomy = (zoomx / font_width_rate) * font_height_rate;

	//�ݒ胊�T�C�Y�ɍ��킹�ĕύX
	if(zoomx!=1.0 || zoomy!=1.0){
		fprintf(log,"[comsurface/make4]comment %d resized.(%5.2f%%,%5.2f%%)\n",item->no,zoomx*100,zoomy*100);
		SDL_Surface* tmp = ret;
		ret = zoomSurface(tmp,zoomx,zoomy,SMOOTHING_ON);
		SDL_FreeSurface(tmp);
		if(ret==NULL){
			fprintf(log,"***ERROR*** [comsurface/makeZ]zoomSurface : %s\n",SDL_GetError());
			fflush(log);
			return NULL;
		}
	}

	fprintf(log,"[comsurface/make5]comment %d (%d, %d) %s %s %s %d lines %.0f nicolimit ",
		item->no,ret->w,ret->h,COM_LOC_NAME[location],COM_FONTSIZE_NAME[item->size],
		item->full?"full":"",nb_line,nicolimit_width);
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
	if(data->fontsize_fix){
		fputs(" FontFix",log);
	}
	fputs("\n",log);
	fflush(log);

	/*
	 * �g������H
	 */
	if(strstr(data->extra_mode,"-frame")!=NULL){
		SDL_Surface* tmp = ret;
		ret = drawFrame(data,item,tmp,RENDER_COLOR_BG,1);
		SDL_FreeSurface(tmp);
	}

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

// this function should not return NULL, except fatal error.
SDL_Surface* drawText2(DATA* data,int size,SDL_Color SdlColor,Uint16* str){
	if(str == NULL || str[0] == '\0'){
		return drawNullSurface(0,data->font_pixel_size[size]);
	}
	FILE* log = data->log;
	int debug = data->debug;
	if(!data->enableCA){
		return drawText4(data,size,SdlColor,data->font[size],str,-1);
	}
	SDL_Surface* ret = NULL;
	Uint16* index = str;
	Uint16* last = index;
	int basefont = getFirstFont(last,UNDEFINED_FONT);	//����t�H���g
	int secondBase = UNDEFINED_FONT;
	if(debug){
		fprintf(log,"[comsurface/drawText2]first base font %s\n",CA_FONT_NAME[basefont & 15]);
	}
	int fonttype = basefont;
	int newfont = basefont;
	int nextfont = basefont;
	int saved;
	int foundAscii = FALSE;
	int wasAscii = FALSE;
	while(*index != '\0'){
		if(nextfont==UNDEFINED_FONT)
			nextfont = GOTHIC_FONT;
		if(debug)
			fprintf(log,"[comsurface/drawText2]str[%d] U+%04hX try %s (base %s)",
				index-str,*index,CA_FONT_NAME[nextfont],CA_FONT_NAME[basefont & 15]);
		newfont = getFontType(index,nextfont,data);
		wasAscii = foundAscii;
		foundAscii = isAscii(index);
		if(newfont==UNDEFINED_FONT||newfont==NULL_FONT)
			newfont = nextfont;
		if(debug)
			fprintf(log," -->%s%s%s\n",CA_FONT_NAME[newfont & 15],
				foundAscii?" foundAscii":"",wasAscii?" wasAscii":"");
		if(newfont != fonttype){	//�ʂ̃t�H���g�o��
			if(index!=last){
				ret = arrangeSurface(ret,drawText3(data,size,SdlColor,fonttype,last,index));
				if(debug && ret!=NULL){
					fprintf(log,"[comsurface/drawText2]arrangeSurface surf(%d, %d) %s %d chars.\n",
						ret->w,ret->h,COM_FONTSIZE_NAME[size],index-str);
				}
			}
			fonttype = newfont;	//GOTHIC, SMSUN. GULIM, ARIAL, GEORGIA,�c
			last = index;
		}
		newfont &= 15;
		//��Q��t�H���g�̌���
		if(secondBase==UNDEFINED_FONT){
			if((foundAscii && !wasAscii && basefont<=GOTHIC_FONT)||
				(basefont==GOTHIC_FONT &&(newfont==SIMSUN_FONT || newfont==GULIM_FONT))){
				secondBase = getFirstFont(index,basefont);
				if(secondBase==basefont || secondBase==GOTHIC_FONT){
					secondBase = UNDEFINED_FONT;
				}
				if(secondBase!=UNDEFINED_FONT && debug)
					fprintf(log,"[somsurface/drawText2]second base font %s\n",
						CA_FONT_NAME[secondBase & 15]);
			}
		}
		//�אڃt�H���g�̌���
		saved = nextfont;
		if(foundAscii && !wasAscii){	//when HANKAKU showed first
			int tryfont = basefont;
			tryfont = getFirstFont(last,tryfont);
			if(tryfont!=UNDEFINED_FONT){
				//Case Win7,Vista; secondBase is stronger than check
				if(secondBase!=UNDEFINED_FONT && tryfont>GOTHIC_FONT){
					tryfont = secondBase;
				}
				nextfont = tryfont;
			}else{
				nextfont = GOTHIC_FONT;
			}
		}else if(newfont!=nextfont){
			int typechar = getDetailType(*index);
			switch (newfont) {
			case SIMSUN_FONT:
				if(typechar==STRONG_SIMSUN_CHAR || typechar==WEAK_SIMSUN_CHAR){
					nextfont = SIMSUN_FONT;
				}
				break;
			case GULIM_FONT:
				if(typechar==GULIM_CHAR){
					nextfont = GULIM_FONT;
				}
				break;
			case GOTHIC_FONT:
				//Case XP, nextfont must be GOTHIC if char is ZENKAKU
				//TO BE DEFINED
				//Win7,Vista,XP common
				if(typechar==GOTHIC_CHAR){
					nextfont = GOTHIC_FONT;
				}
				break;
			default:
				break;
			}
		}
		if(nextfont!=saved && debug){
			fprintf(log,"[somsurface/drawText2]nextfont %s-> %s\n",
				CA_FONT_NAME[saved & 15],CA_FONT_NAME[nextfont & 15]);
		}
		index++;
	}
	ret = arrangeSurface(ret,drawText3(data,size,SdlColor,fonttype,last,index));
	if(ret==NULL){
		fprintf(log,"[comsurface/drawText2]drawtext3 NULL last. make NullSurface.\n");
		fflush(log);
		return drawNullSurface(0,data->font_pixel_size[size]);
	}
	if(debug){
		fprintf(log,"[comsurface/drawText2]arrangeSurface surf(%d, %d) %s %d chars\n",
			ret->w,ret->h,COM_FONTSIZE_NAME[size],index-str);
		fflush(log);
	}
	return ret;
}

SDL_Surface* drawNullSurface(int w,int h){
	//not make nor use alpha
	return SDL_CreateRGBSurface(SDL_HWSURFACE | SDL_HWACCEL,
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

SDL_Surface* arrangeSurface(SDL_Surface* left,SDL_Surface* right){
	if(left==NULL){
		return right;	// this may be NULL
	}
	if(right==NULL){
		return left;
	}
	//not make nor use alpha
	SDL_Surface* ret = drawNullSurface(left->w+right->w, MAX(left->h,right->h));
	SDL_SetAlpha(left,SDL_RLEACCEL,0xff);	//not use alpha
	SDL_SetAlpha(right,SDL_RLEACCEL,0xff);	//not use alpha
	SDL_Rect rect = {left->w,0,0,0};		//use only x y
	SDL_BlitSurface(left,NULL,ret,NULL);
	SDL_BlitSurface(right,NULL,ret,&rect);
	SDL_FreeSurface(left);
	SDL_FreeSurface(right);
	return ret;
}

SDL_Surface* drawText3(DATA* data,int size,SDL_Color SdlColor,int fontsel,Uint16* from,Uint16* to){
	int len = to-from;
	FILE* log = data->log;
	int debug = data->debug;
	int h = data->font_pixel_size[size];

	if(fontsel>=CA_FONT_MAX){	//CA_FONT_MAX must be less than or equals 16
		int code = fontsel & 0xfff0;
		int w = data->fontsize_fix;
		if(code==0x0020 || code==0x00a0){
			w = (CA_FONT_SPACE_WIDTH[size] * len)<<w;
		}else if(code==0x3000){
			w = (CA_FONT_3000_WIDTH[fontsel&3][size] * len)<<w;
		}else if((code & 0xff00)==0x2000){	//fontsel should belog to GOTHIC or fontsel is SIMSUN or GULIM
			w = (CA_FONT_2000_WIDTH[(code & 0x00f0)>>4][size] * len)<<w;
		}else{
			fprintf(log,"[comsurface/drawText3]fontsel error %d\n",fontsel);
			fflush(log);
			return NULL;
		}
		SDL_Surface* ret = drawNullSurface(w,h);
		if(debug){
			int codeno;
			switch (code & 0xfff0) {
				case 0x0020:	codeno = 0; break;
				case 0x00a0:	codeno = 1; break;
				case 0x3000:	codeno = 3; break;
				default:		codeno = 2; break;
			}
			fprintf(log,"[comsurface/drawText3]return %s font %04X %s %d chars.(%d,%d)\n"
				,CA_SPACE_NAME[codeno],code,COM_FONTSIZE_NAME[size],len,ret->w,ret->h);
			fflush(log);
		}
		return ret;
	}
	if(*from=='\0' || len==0){
		if(debug)
			fprintf(log,"[comsurface/drawText3]return font %s NULL\n",CA_FONT_NAME[fontsel]);
		return drawNullSurface(0,h);
	}
	Uint16* text = (Uint16*)malloc(sizeof(Uint16)*(len+1));
	if(text==NULL){
		fprintf(log,"[comsurface/drawText3]can't alloc memory font %s.\n",CA_FONT_NAME[fontsel]);
		fflush(log);
		return NULL;
	}
	int l2 = 0;
	for(;from<to;from++){
		if(!isZeroWidth(from)){
			text[l2++] = *from;
		}
	}
	text[l2]='\0';
	if(debug)
		fprintf(log,"[comsurface/drawText3]building U+%04hX %d chars. in %s %s\n",
			text[0],l2,CA_FONT_NAME[fontsel],COM_FONTSIZE_NAME[size]);
	if(l2==0){
		free(text);
		return drawNullSurface(0,h);
	}
	SDL_Surface* ret = drawText4(data,size,SdlColor,data->CAfont[fontsel][size],text,fontsel);
	free(text);
	return ret;
}

SDL_Surface* drawText4(DATA* data,int size,SDL_Color SdlColor,TTF_Font* font,Uint16* str,int fontsel){
	FILE* log = data->log;
	int debug = data->debug;
	//SDL_Surface* surf = TTF_RenderUNICODE_Blended(font,str,SdlColor);
	//SDL_Color bgc = COMMENT_COLOR[CMD_COLOR_YELLOW];
	SDL_Surface* surf = render_unicode(data,font,str,SdlColor,size,fontsel);

	if(surf==NULL){
		fprintf(log,"***ERROR*** [comsurface/drawText4]TTF_RenderUNICODE : %s\n",TTF_GetError());
		fflush(log);
		return NULL;
	}
	if(debug)
		fprintf(log,"[comsurface/drawText4]TTF_RenderUNICODE surf(%d, %d) %s %d chars\n",
			surf->w,surf->h,COM_FONTSIZE_NAME[size],uint16len(str));
	SDL_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
	SDL_Surface* ret = drawNullSurface(surf->w,data->font_pixel_size[size]);
	if(ret==NULL){
		fprintf(log,"***ERROR*** [comsurface/drawText4]drawNullSurface : %s\n",SDL_GetError());
		fflush(log);
		return NULL;
	}
	SDL_Rect srcrect = {0,0,ret->w,ret->h};
	//rect.y = 0;	// = (ret->h - surf->h)>>1
	SDL_BlitSurface(surf,&srcrect,ret,NULL);
	SDL_FreeSurface(surf);
	return ret;
}

int isDoubleResize(double width, double limit_width, int size, int line, FILE* log){
	if(size==CMD_FONT_BIG && line>=16){
		//�����Œ�,big16�̉\��
		if(width * 0.9 < limit_width){
			return FALSE;
		}
		fprintf(log,"[isDoubleResize]found big16 but too wide.\n");
	}
	if((size==CMD_FONT_DEF || size==CMD_FONT_MEDIUM) && line>=25){
		//�����Œ�̉\��
		if(width * 0.9 < limit_width){
			return FALSE;
		}
		fprintf(log,"[isDoubleResize]found medium25 but too wide.\n");
	}
	if(size==CMD_FONT_SMALL && line>=38){
		//�����Œ�̉\��
		if(width * 0.9 < limit_width){
			return FALSE;
		}
		fprintf(log,"[isDoubleResize]found small38 but too wide.\n");
	}
	if(width > limit_width){
		return TRUE;
	}
	return FALSE;
}

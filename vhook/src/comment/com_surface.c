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


SDL_Surface* drawNullSurface(int w,int h);
SDL_Surface* arrangeSurface(SDL_Surface* left,SDL_Surface* right);
//SDL_Surface* drawText(DATA* data,int size,int color,Uint16* str);
SDL_Surface* drawText2(DATA* data,int size,SDL_Color color,Uint16* str);
SDL_Surface* drawText3(DATA* data,int size,SDL_Color color,int fontsel,Uint16* from,Uint16* to);
SDL_Surface* drawText4(DATA* data,int size,SDL_Color color,TTF_Font* font,Uint16* str);
int cmpSDLColor(SDL_Color col1, SDL_Color col2);
int isDoubleResize(double width, double limit_width, int size, int line, FILE* log);

SDL_Surface* makeCommentSurface(DATA* data,const CHAT_ITEM* item,int video_width,int video_height){
	Uint16* index = item->str;
	Uint16* last = item->str;
	SDL_Surface* ret = NULL;
	//int color = item->color;
	SDL_Color SdlColor = item->color24;
	int size = item->size;
	int nb_line = 1;
	FILE* log = data->log;
	int debug = data->debug;
	double font_width_rate = data->font_w_fix_r;
	double font_height_rate = data->font_h_fix_r;
	int nico_width = data->nico_width_now;

	/*
	 * 影は置いておいて、とりあえず文字の描画
	 */
	while(*index != '\0'){
		if(*index == '\n'){
			*index = '\0';//ここで一旦切る
			if(ret == null){//最初の改行
				ret = drawText2(data,size,SdlColor,last);
				if(ret!=NULL && debug)
					fprintf(log,"[comsurface/make.0]drawText2 surf(%d, %d) size %d\n",ret->w,ret->h,size);
			}else{/*改行あり*/
				ret = connectSurface(ret,drawText2(data,size,SdlColor,last));
				nb_line++;
				if(ret!=NULL && debug)
					fprintf(log,"[comsurface/make.1]connectSurface surf(%d, %d) size %d line %d\n",ret->w,ret->h,size,nb_line);
			}
			*index = '\n';//ここで一旦切る
			last = index+1;
		}
		index++;
	}
	if(ret == null){//結局改行は無い
		ret = drawText2(data,size,SdlColor,item->str);
		if(!ret)
			return NULL;
		if(debug)
			fprintf(log,"[comsurface/make.2]drawText2 surf(%d, %d) size %d\n",ret->w,ret->h,size);
	}else{/*改行あり*/
		ret = connectSurface(ret,drawText2(data,size,SdlColor,last));
		if(!ret)
			return NULL;
		nb_line++;
		if(debug)
			fprintf(log,"[comsurface/make.3]connectSurface surf(%d, %d) size %d line %d\n",ret->w,ret->h,size,nb_line);
	}

	if(ret==NULL || ret->h == 0){
		fprintf(log,"***ERROR*** [comsurface/makeE]comment %d has no char.\n",item->no);
		fflush(log);
		return ret;
	}

	/*
	 * 影処理
	 */
	int shadow = data->shadow_kind;
	if(shadow >= SHADOW_MAX){
		shadow = SHADOW_DEFAULT;
	}
	int is_black = item->color == CMD_COLOR_BLACK
		|| cmpSDLColor(item->color24, COMMENT_COLOR[CMD_COLOR_BLACK]);
	ret = (*ShadowFunc[shadow])(ret,is_black,data->fontsize_fix);
	fprintf(log,"[comsurface/make1](*ShadowFunc[%d]) surf(%d, %d) size %d line %d\n",shadow,ret->w,ret->h,size,nb_line);

	/*
	 * アルファ値の設定
	 */
	float alpha_t = 1.0;
	if(!data->opaque_comment){
		alpha_t = (((float)(item->no)/(item->chat->max_no)) * 0.4) + 0.6;
	}
	if(&item->chat->max_no == &data->optional.chat.max_no && data->optional_trunslucent){
		if(alpha_t>0.3) alpha_t = 0.3;			// これでいいのかな？適当なんだが。
	}
	if(alpha_t<1.0){
		fprintf(log,"[comsurface/makeA]comment %d set alpha:%5.2f%%.\n",item->no,alpha_t*100.0f);
		setAlpha(ret,alpha_t);
	}
	fprintf(log,"[comsurface/make2]comment %d build (%d, %d) %d line%s%s%s.\n",
		item->no,ret->w,ret->h,nb_line,(data->original_resize ? "": " dev"),(data->enableCA?" CA":""),(data->fontsize_fix?" 2x":""));

	// リサイズ率に無関係なスケール計算
	double autoscale = data->width_scale;
	int auto_scaled = FALSE;
	int linefeed_resized = FALSE;
	int limit_width_resized = FALSE;
	int double_resized = FALSE;
	/*
	 * 臨界幅は同倍率の動画で544(512～600)px  動画が4:3か16:9に無関係
	 * 　　　　fullコマンドで672(640～?)
	 */
	double nicolimit_width = (double)NICO_WIDTH;
	if(item->full){
		nicolimit_width = (double)NICO_WIDTH_WIDE;
	}
	fprintf(log,"[comsurface/make3]comment %d (%d, %d) font_rate (%.0f%%,%.0f%%) nico_width:%d x%.3f\n",
		item->no,video_width,video_height,font_width_rate*100.0,font_height_rate*100.0,nico_width,autoscale);

	if (data->original_resize){
		/*
		 * さきゅばす従来
		 *
		 * スケール設定
		 * 横幅 zoomx
		 * 高さ zoomy	実験的にratio(%)を指定する
		 */

		double zoomx = font_width_rate;
		double zoomy;
		//縮小

		if(data->fontsize_fix || data->enableCA){
			zoomx *= autoscale;
			if(data->fontsize_fix)
				zoomx *= 0.5;
			//zoomx = (0.5 * (double)video_width) / (double)data->nico_width_now;
			//zoomx = (0.5f * (double)video_width) / (double)NICO_WIDTH;
			//zoomy = (0.5f * (double)video_height) / (double)NICO_HEIGHT;
			if(zoomx != 1.0f){
				auto_scaled = TRUE;
			}
		}

		/*スケールの調整*/
		nicolimit_width *= autoscale;
		// 改行リサイズ
		// コメントの画像の高さがニコニコ動画基準の高さの１／３より大きいと倍率を１／２にする
		if((int)(ret->h * zoomx) > (NICO_HEIGHT/3) * autoscale + 1){
			// ダブルリサイズ検査
			// 改行リサイズ＆改行後の倍率で臨界幅を超えた場合 → 改行リサイズキャンセル
			if(item->location != CMD_LOC_DEF
				&& isDoubleResize(0.5 * zoomx * ret->w, nicolimit_width, size, nb_line, log)){
				//  ダブルリサイズあり → 改行リサイズキャンセル
				nicolimit_width *= 2.0;
				double_resized = TRUE;
			} else{
				// ダブルリサイズなし
				zoomx *= LINEFEED_RESIZE_SCALE[size];	// *= 0.5
				linefeed_resized =TRUE;
			}
		}
		if(item->location != CMD_LOC_DEF){
			/* ue shitaコマンドのみリサイズあり */
			/*
			 * 臨界幅リサイズ
			 * 臨界幅は同倍率の動画で544(512～600)px  動画が4:3か16:9に無関係
			 * 　　　　fullコマンドで672(640～?)
			 * 文字の大きさで臨界幅は変動する←正確に合わせるのは現状では無理？
			 * コメントの幅が動画の幅に収まるように倍率を調整
			 * ダブルリサイズ　→　無条件にリサイズ（判定済み）
			 * 改行リサイズ　→　無条件になし（判定済み）
			 * 両方なし　→　今回判定
			 */
			if(!linefeed_resized && (double)ret->w * zoomx > nicolimit_width){
				//ダブルリサイズ時には臨界幅は２倍済
				// 縮小
				zoomx = nicolimit_width / (double)ret->w;
				limit_width_resized = TRUE;
			}
		}
		// ue shitaコマンドのみリサイズ終わり
		zoomy = (zoomx / font_width_rate) * font_height_rate;

		// 画面サイズに合わせて変更
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

		fprintf(log,"[comsurface/make5]comment %d (%d, %d) loc=%d size=%d full=%d line=%d limit=%.0f ",
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
		fflush(log);

		return ret;

	 }

	/*実験、スケール設定はリサイズ後の値を使う*/
	double zoomx = 1.0f;
	double zoomy = 1.0f;
	double zoom_w = (double)ret->w;
	zoom_w *= font_width_rate;
	// zoomx *= font_width_rate;
	/*
	 * 臨界幅は同倍率の動画で544(512～600)px  動画が4:3か16:9に無関係
	 * 　　　　fullコマンドで672(640～?)
	 * 文字の大きさで臨界幅は変動する←ニコ動に合わせるのは現状では無理？
	 * 実験的に指定してみる
	 */
	if(data->fontsize_fix){
		// nicolimit_width *= 2.0;
		zoom_w *= 0.5;
		/* zoom *= 0.5 */
	}
	//nico_width += 32;	// 512->544, 640->672

	if (nb_line >= LINEFEED_RESIZE_LIMIT[size]){
		/*
		 * 改行リサイズあり ダブルリサイズ検査
		 * 改行リサイズかつ改行後の倍率で改行臨界幅(nicolimit_width)を超えた場合 → 改行リサイズキャンセル
		 */
		if(item->location != CMD_LOC_DEF && isDoubleResize(0.5 * zoom_w /* zoomx * ret->w */, nicolimit_width, size, nb_line, log)){
			// ダブルリサイズあり
			double_resized = TRUE;
			//ダブルリサイズ時には動画幅の２倍にリサイズされる筈
			nicolimit_width *= 2.0;
		}else{
			// ダブルリサイズなし
			linefeed_resized = TRUE;
			zoom_w *= LINEFEED_RESIZE_SCALE[size];
			/* zoomx *= LINEFEED_RESIZE_SCALE[size]; */
		}
	}

	if(item->location != CMD_LOC_DEF){
		// ue shitaコマンドのみリサイズあり

		/*
		 * 臨界幅リサイズ
		 * 文字の大きさで臨界幅は変動する
		 * コメントの幅が臨界幅(または2倍)に収まるように倍率を調整
		 * 改行リサイズ　→　なし（判定済み）だが、実験的にもう一度縮小
		 * ダブルリサイズ　→　nicolimit_widthは2倍済 で判定
		 * 両方なし　→　今回判定
		 */
		if(zoom_w /* ret->w * zoomx */ > nicolimit_width){
		// if(!linefeed_resized && zoom_w > nicolimit_width){
			limit_width_resized = TRUE;
			zoom_w = nicolimit_width;
			// zoomx = nicolimit_width/(double)ret-w;
		}
	}
	// ue shitaコマンドのみリサイズ終わり

	/*
	 * フォントサイズ自動調整
	 * 動画幅とニコニコ動画の幅のスケール
	 */
	if(data->fontsize_fix || data->enableCA){
		if(video_width != nico_width){
			zoom_w = ceil(zoom_w * autoscale);
			// zoomx *= autoscale
			auto_scaled = TRUE;
		}
	}

	// 実験：フォント幅・高さの調整
	zoomx = zoom_w/(double)ret->w;
	zoomy = (zoomx / font_width_rate) * font_height_rate;

	//設定リサイズに合わせて変更
	if(zoomx!=1.0 || zoomy!=1.0){
		fprintf(log,"[comsurface/make4]comment %d resized.(%5.2f%%,%5.2f%%)\n",item->no,zoomx*100,zoomy*100);
		SDL_Surface* tmp = ret;
		ret = zoomSurface(tmp,zoomx,zoomy,SMOOTHING_ON);
		SDL_FreeSurface(tmp);
		if(!ret){
			fprintf(log,"***ERROR*** [comsurface/makeZ]zoomSurface : %s\n",SDL_GetError());
			fflush(log);
			return NULL;
		}
	}

	fprintf(log,"[comsurface/make5]comment %d (%d, %d) loc=%d size=%d full=%d line=%d limit=%.0f ",
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
	fflush(log);

	return ret;
}

/**
 * 文字を描画
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
		return drawText4(data,size,SdlColor,data->font[size],str);
	}
	SDL_Surface* ret = NULL;
	Uint16* index = str;
	Uint16* last = index;
	int basefont = getFirstFont(last,GOTHIC_FONT,data);	//第一基準フォント
	if(debug)
		fprintf(log,"[somsurface/drawText2]first base font %s\n",CA_FONT_NAME[basefont]);
	int newfont;
	int fonttype = basefont;
	while(*index != '\0'){
		if(debug)
			fprintf(log,"[comsurface/drawText2]str[%d] U%04hX %s (base %s)\n",index-str,*index,CA_FONT_NAME[fonttype],CA_FONT_NAME[basefont]);
		newfont = getFontType(index,basefont,data);
		if(newfont != fonttype){	//別のフォント出現
			if(index!=last){
				ret = arrangeSurface(ret,drawText3(data,size,SdlColor,fonttype,last,index));
				if(debug && ret!=NULL){
					fprintf(log,"[comsurface/drawText2]arrangeSurface surf(%d, %d) %s %d chars.\n",
						ret->w,ret->h,CA_FONT_NAME[fonttype],index-last);
				}
			}
			if(newfont==UNDEFINED_FONT||newfont==NULL_FONT){
				fonttype = basefont;
			}else{
				fonttype = newfont;	//GOTHIC, SMSUN. GULIM, ARIAL, GEORGIA
			}
			last = index;
			if(isAscii(last)){
				basefont = getFirstFont(last,basefont,data);	//第二基準フォント
				if(debug)
					fprintf(log,"[somsurface/drawText2]second base font %s\n",CA_FONT_NAME[basefont]);
			}
		}
		index++;
	}
	ret = arrangeSurface(ret,drawText3(data,size,SdlColor,fonttype,last,index));
	if(!ret){
		fprintf(log,"[comsurface/drawText2]drawtext3 NULL last. make NullSurface.\n");
		return drawNullSurface(0,data->font_pixel_size[size]);
	}
	if(debug){
		fprintf(log,"[comsurface/drawText2]arrangeSurface surf(%d, %d)\n",ret->w,ret->h);
	}
	return ret;
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

SDL_Surface* arrangeSurface(SDL_Surface* left,SDL_Surface* right){
	if(left==NULL){
		return right;	// this may be NULL
	}
	if(right==NULL){
		return left;
	}
	SDL_Surface* ret = SDL_CreateRGBSurface(SDL_SRCALPHA,
                                            left->w+right->w,
                                            MAX(left->h,right->h),
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
	SDL_SetAlpha(left,SDL_SRCALPHA | SDL_RLEACCEL,0xff);
	SDL_SetAlpha(right,SDL_SRCALPHA | SDL_RLEACCEL,0xff);
	SDL_Rect rect = {left->w,0,ret->w,ret->h};
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
	if(*from=='\0' || len==0){
		if(debug)
			fprintf(log,"[comsurface/drawText3]return font %s NULL\n",CA_FONT_NAME[fontsel]);
		return drawNullSurface(0,data->font_pixel_size[size]);
	}
	Uint16* text = (Uint16*)malloc(sizeof(Uint16)*(len+1));
	if(text==NULL){
		if(debug)
			fprintf(log,"[comsurface/drawText3]can't alloc memory font %s.\n",CA_FONT_NAME[fontsel]);
		return NULL;
	}
	int l2 = 0;
	for(;from<to;from++){
		if(!isZeroWidth(from)){
			text[l2++] = replaceSpace(*from);
		}
	}
	text[l2]='\0';
	if(debug)
		fprintf(log,"[comsurface/drawText3]building U%04hX %d chars. in %s\n",text[0],l2,CA_FONT_NAME[fontsel]);
	if(l2==0){
		free(text);
		return drawNullSurface(0,data->font_pixel_size[size]);
	}
	SDL_Surface* ret = drawText4(data,size,SdlColor,data->CAfont[fontsel][size],text);
	free(text);
	return ret;
}

SDL_Surface* drawText4(DATA* data,int size,SDL_Color SdlColor,TTF_Font* font,Uint16* str){
	FILE* log = data->log;
	int debug = data->debug;
	SDL_Surface* surf = TTF_RenderUNICODE_Blended(font,str,SdlColor);
	if(!surf){
		fprintf(log,"***ERROR*** [comsurface/drawText4]TTF_RenderUNICODE : %s\n",TTF_GetError());
		fflush(log);
		return NULL;
	}
	if(debug)
		fprintf(log,"[comsurface/drawText4]TTF_RenderUNICODE surf(%d, %d) size:%d\n",surf->w,surf->h,size);
	SDL_SetAlpha(surf,SDL_SRCALPHA | SDL_RLEACCEL,0xff);
	SDL_Surface* ret = drawNullSurface(surf->w,data->font_pixel_size[size]);
	if(!ret){
		fprintf(log,"***ERROR*** [comsurface/drawText4]drawNullSurface : %s\n",SDL_GetError());
		fflush(log);
		return NULL;
	}
	SDL_Rect rect = {0,0,ret->w,ret->h};
	//rect.y = 0;	// = (ret->h - surf->h)>>1
	SDL_BlitSurface(surf,NULL,ret,&rect);
	SDL_FreeSurface(surf);
	return ret;
}

int cmpSDLColor(SDL_Color col1, SDL_Color col2){
	return (col1.r == col2.r && col1.g == col2.g && col1.b == col2.b);
}

int isDoubleResize(double width, double limit_width, int size, int line, FILE* log){
	if(width > limit_width){
		return TRUE;
	}
	return FALSE;
}

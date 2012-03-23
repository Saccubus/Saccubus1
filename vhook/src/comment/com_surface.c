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
	//int color = item->color;
	SDL_Color SdlColor = item->color24;
	int size = item->size;
	int location = item->location;
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
					fprintf(log,"[comsurface/make.0]drawText2 surf(%d, %d) %s\n",ret->w,ret->h,COM_FONTSIZE_NAME[size]);
			}else{/*改行あり*/
				ret = connectSurface(ret,drawText2(data,size,SdlColor,last));
				nb_line++;
				if(ret!=NULL && debug)
					fprintf(log,"[comsurface/make.1]connectSurface surf(%d, %d) %s line %d\n",ret->w,ret->h,COM_FONTSIZE_NAME[size],nb_line);
			}
			*index = '\n';//ここで一旦切る
			last = index+1;
		}
		index++;
	}
	if(ret == null){//結局改行は無い
		ret = drawText2(data,size,SdlColor,item->str);
		if(ret==NULL)
			return NULL;
		if(debug)
			fprintf(log,"[comsurface/make.2]drawText2 surf(%d, %d) %s\n",ret->w,ret->h,COM_FONTSIZE_NAME[size]);
	}else{/*改行あり*/
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
	 * 影処理
	 */
	int shadow = data->shadow_kind;
	if(shadow >= SHADOW_MAX){
		shadow = SHADOW_DEFAULT;
	}
	int is_black = item->color == CMD_COLOR_BLACK
		|| cmpSDLColor(item->color24, COMMENT_COLOR[CMD_COLOR_BLACK]);
	if(debug && strstr(data->extra_mode,"fg")!=NULL){
		is_black = 2;
	}
	ret = (*ShadowFunc[shadow])(ret,is_black,data->fontsize_fix,SdlColor);
	fprintf(log,"[comsurface/make1]ShadowFunc:%d (%d, %d) %s %d line\n",shadow,ret->w,ret->h,COM_FONTSIZE_NAME[size],nb_line);

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
	fprintf(log,"[comsurface/make3]comment %d (%d, %d) font_rate(%.0f%%,%.0f%%) nico_width:%d x%.3f\n",
		item->no,ret->w,ret->h,font_width_rate*100.0,font_height_rate*100.0,nico_width,autoscale);

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

		/*スケールの調整*/
		nicolimit_width *= autoscale;
		// 改行リサイズ
		// コメントの画像の高さがニコニコ動画基準の高さの１／３より大きいと倍率を１／２にする
		if((int)(ret->h * zoomx) > (NICO_HEIGHT/3) * autoscale + 1){
			// ダブルリサイズ検査
			// 改行リサイズ＆改行後の倍率で臨界幅を超えた場合 → 改行リサイズキャンセル
			if(location != CMD_LOC_DEF
				&& isDoubleResize(0.5 * zoomx * ret->w, nicolimit_width, size, nb_line, log)){
				//  ダブルリサイズあり → 改行リサイズキャンセル
				nicolimit_width *= 2.0;
				double_resized = TRUE;
			} else{
				// ダブルリサイズなし
				zoomx *= linefeedResizeScale(size,nb_line,data->fontsize_fix);	// *= 0.5
				linefeed_resized =TRUE;
			}
		}
		//	コメント高さ補正
		if(!linefeed_resized){
			int h = adjustHeight(nb_line,size,FALSE,data->fontsize_fix);
			if(h!=ret->h){
				ret = adjustComment(ret,data,h);
				fprintf(log,"[comsurface/adjust]comment %d adjust(%d, %d) %s\n",
					item->no,ret->w,ret->h,(data->fontsize_fix?" fix":""));
			}
		}
		if(location != CMD_LOC_DEF){
			/* ue shitaコマンドのみリサイズあり */
			/*
			 * 臨界幅リサイズ
			 * 臨界幅は同倍率の動画で544(512～600)px  動画が4:3か16:9に無関係
			 * 　　　　fullコマンドで672(640～?)
			 * 文字の大きさで臨界幅は変動する←正確に合わせるのは現状では無理？
			 * コメントの幅が動画の幅に収まるように倍率を調整
			 * ダブルリサイズ　→　無条件にリサイズ（判定済み）
			 * 改行リサイズ　→　無条件になし（再判定→フォント幅を縮小）
			 * 両方なし　→　今回判定
			 */
			double rate = nicolimit_width / (double)ret->w;
			if(linefeed_resized && zoomx > rate){
				fprintf(log,"[comsurface/LF]comment %d previous width %.0f rate %.2f%%%s\n",
					item->no,(double)ret->w * zoomx,rate * 100.0,(data->fontsize_fix?" fix":""));
				font_width_rate *= rate;
				zoomx = rate;
			}else
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
		 * 枠をつける？
		 */
		if(strstr(data->extra_mode,"-frame")!=NULL){
			SDL_Surface* tmp = ret;
			ret = drawFrame(data,item,tmp,RENDER_COLOR_BG,1);
			SDL_FreeSurface(tmp);
		}

		return ret;

	 }

	/*実験、スケール設定はリサイズ後の値を使う*/
	double zoomx = 1.0f;
	double zoomy = 1.0f;
	double zoom_w = (double)ret->w;
	double zoom_h = (double)ret->h;
	zoom_w *= font_width_rate;
	zoom_h *= font_height_rate;
	/*
	 * 臨界幅は同倍率の動画で544(512～600)px  動画が4:3か16:9に無関係
	 * 　　　　fullコマンドで672(640～?)
	 * 文字の大きさで臨界幅は変動する←ニコ動に合わせるのは現状では無理？
	 * 実験的に指定してみる
	 */
	if(data->fontsize_fix){
		// nicolimit_width *= 2.0;
		zoom_w *= 0.5;
		zoom_h *= 0.5;
		//auto_scaled = TRUE;
	}
	//nico_width += 32;	// 512->544, 640->672

	double resized_w;
	if (nb_line >= LINEFEED_RESIZE_LIMIT[size]){
		/*
		 * 改行リサイズあり ダブルリサイズ検査
		 * 改行リサイズかつ改行後の倍率で改行臨界幅(nicolimit_width)を超えた場合 → 改行リサイズキャンセル
		 */
		double linefeed_zoom = linefeedResizeScale(size,nb_line,data->fontsize_fix);
		resized_w = zoom_w * linefeed_zoom;
		if(location != CMD_LOC_DEF && isDoubleResize(0.5 * zoom_w, nicolimit_width, size, nb_line, log)){
			// ダブルリサイズあり
			double_resized = TRUE;
			//ダブルリサイズ時には動画幅の２倍にリサイズされる筈
			nicolimit_width *= 2.0;
			//意図したダブルリサイズならば高さ基準でリサイズした方が良い？
			int h = adjustHeight(nb_line,size,FALSE,data->fontsize_fix);
			if(h!=ret->h){
				ret = adjustComment(ret,data,h);
				fprintf(log,"[comsurface/DRadjust]comment %d adjust(%d, %d) %s\n",
					item->no,ret->w,ret->h,(data->fontsize_fix?" fix":""));
			}
			/*
			 * ダブルリサイズの臨界幅リサイズ
			 * 文字の大きさで臨界幅は変動する
			 * コメントの幅が臨界幅の2倍に収まるように倍率を調整
			 */
			double resize;
			if(zoom_w > nicolimit_width){
				/*
				 * h<=[(Limit/ref->w)*PIX_HEIGHT]切り上げ
				 * resized_widrh = N * resize
				 * M * reszie < limit  M=1..N
				 * M < limit / resize
				 */
				resize = zoom_w / COMMENT_FONT_SIZE[size];
				resized_w = floor(nicolimit_width / resize) * resize;
				fprintf(log,"[comsurface/DR limit1]comment %d previous width %.0f chars %.0f resized %.0f limit %.0f\n",
					item->no,zoom_w,resize,resized_w,nicolimit_width);
				if(resized_w+resize < nicolimit_width+32){
					resized_w += resize;
				}
				zoom_w = resized_w;
				fprintf(log,"[comsurface/DR limit2]comment %d width %.0f resize %.0f limit+ %.0f\n",
					item->no,zoom_w,resize,nicolimit_width+32);
			}
/*
			double wrate = nicolimit_width / zoom_w;
			double hrate = (double)NICO_HEIGHT / zoom_h;
			fprintf(log,"[comsurface/DR detail]comment %d  w %.2f%% h %.2f%%%s\n",
				item->no,wrate*100.0,hrate*100.0,(data->fontsize_fix?" fix":""));
			double h2 = wrate / hrate;
			if(0.6 < hrate && hrate <= 1.0){
				//コメント高が動画以上でありダブルリサイズにより動画高に合わせたと見る。
				zoom_w *= hrate;
				if(zoom_w > nicolimit_width){
					//横幅が大きすぎ
					font_width_rate *= nicolimit_width / zoom_w;
					zoom_w = nicolimit_width;
				}
				fprintf(log,"[comsurface/DR hrate]comment %d  width %.0f %.2f%% font_width %.2f%%\n",
					item->no,zoom_w,hrate*100.0,font_width_rate*100.0);
			}else
			if(hrate > 1.0){
				//コメント高が動画以下であり横幅で決めるしか手がない
				zoom_w *= wrate;
				fprintf(log,"[comsurface/DR wrate]comment %d  width %.0f %.2f%% font_width %.2f%%\n",
					item->no,zoom_w,wrate*100.0,font_width_rate*100.0);
			}
			//以下は動画よりコメント高が凄く高い
			else
			if(0.75 <= h2 && h2 <= 1.5){
				//横幅基準で高さが動画より微妙になるなら動画高に合わせる
				zoom_w *= hrate;
				if(zoom_w > nicolimit_width){
					//横幅が大きすぎ
					font_width_rate *= nicolimit_width / zoom_w;
					zoom_w = nicolimit_width;
				}
				fprintf(log,"[comsurface/DR hrate2]comment %d  width %.0f %.2f%% font_width %.2f%%\n",
					item->no,zoom_w,hrate*100.0,font_width_rate*100.0);
			}
			else
			{
				//高さが動画と全然違うので合わせられない
				zoom_w *= wrate;
				fprintf(log,"[comsurface/DR wrate2]comment %d  width %.0f %.2f%% font_width %.2f%%\n",
					item->no,zoom_w,wrate*100.0,font_width_rate*100.0);
			}
*/

		}else{
			// ダブルリサイズなし
			linefeed_resized = TRUE;
			zoom_w = resized_w;	// *= 0.5
			//zoom_h *= linefeed_zoom;
			/* zoomx *= linefeedResizeScale(size,nb_line,data->fontsize_fix); */
		}
	}
	//	コメント高さ補正
	if(!linefeed_resized && !double_resized){
		int h = adjustHeight(nb_line,size,FALSE,data->fontsize_fix);
		if(h!=ret->h){
			ret = adjustComment(ret,data,h);
			fprintf(log,"[comsurface/adjust]comment %d adjust(%d, %d) %s\n",
				item->no,ret->w,ret->h,(data->fontsize_fix?" fix":""));
		}
	}
	if(location != CMD_LOC_DEF){
		// ue shitaコマンドのみリサイズあり

		/*
		 * 臨界幅リサイズ
		 * 文字の大きさで臨界幅は変動する
		 * コメントの幅が臨界幅(または2倍)に収まるように倍率を調整
		 * 改行リサイズ　→　なし（判定済み）だが、実験的にもう一度縮小
		 * ダブルリサイズ　→　nicolimit_widthは2倍済 で判定
		 * 両方なし　→　今回判定
		 */
		double resize;
		if(linefeed_resized && zoom_w > nicolimit_width){
			fprintf(log,"[comsurface/AfterLF]comment %d previous width%.0f > limit%.0f, font_width %.2f%%%s\n",
				item->no,zoom_w,nicolimit_width,font_width_rate * 100.0,(data->fontsize_fix?" fix":""));
			//zoom_w = nicolimit_width;
		}
		if(!linefeed_resized && !double_resized && zoom_w > nicolimit_width){
			/*
			 * h<=[(Limit/ref->w)*PIX_HEIGHT]切り上げ
			 * resized_widrh = N * resize
			 * M * reszie < limit  M=1..N
			 * M < limit / resize
			 */
			resize = zoom_w / COMMENT_FONT_SIZE[size];
			resized_w = floor(nicolimit_width / resize) * resize;
			fprintf(log,"[comsurface/LWresize]comment %d previous width %.0f chars %.0f resized %.0f limit %.0f\n",
				item->no,zoom_w,resize,resized_w,nicolimit_width);
			if(resized_w+resize < nicolimit_width+32){
				resized_w += resize;
			}
			limit_width_resized = TRUE;
			zoom_w = resized_w;
			fprintf(log,"[comsurface/LWresize]comment %d width %.0f resize %.0f limit+ %.0f\n",
				item->no,zoom_w,resize,nicolimit_width+32);
		}
	}
	// ue shitaコマンドのみリサイズ終わり

	/*
	 * フォントサイズ自動調整
	 * 動画幅とニコニコ動画の幅のスケール
	 */
	if(data->fontsize_fix || data->enableCA){
		if(video_width != nico_width){
			zoom_w *= autoscale;
			//zoom_h *= autoscale;
			// zoomx *= autoscale
			auto_scaled = TRUE;
		}
	}

	// 実験：フォント幅・高さの調整
	zoomx = zoom_w/(double)ret->w;
	//zoomy = zoom_h/(double)ret->h;
	zoomy = (zoomx / font_width_rate) * font_height_rate;

	//設定リサイズに合わせて変更
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
	 * 枠をつける？
	 */
	if(strstr(data->extra_mode,"-frame")!=NULL){
		SDL_Surface* tmp = ret;
		ret = drawFrame(data,item,tmp,RENDER_COLOR_BG,1);
		SDL_FreeSurface(tmp);
	}

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
		return drawText4(data,size,SdlColor,data->font[size],str,-1);
	}
	SDL_Surface* ret = NULL;
	Uint16* index = str;
	Uint16* last = index;
	int basefont = getFirstFont(last,GOTHIC_FONT);	//第一基準フォント[0..2]
	int secondBase;
	if(debug)
		fprintf(log,"[comsurface/drawText2]first base font %s\n",CA_FONT_NAME[basefont]);
	int newfont;
	int fonttype = basefont;
	while(*index != '\0'){
		if(debug)
			fprintf(log,"[comsurface/drawText2]str[%d] U+%04hX try %s (base %s)",
				index-str,*index,CA_FONT_NAME[fonttype&63],CA_FONT_NAME[basefont]);
		newfont = getFontType(index,basefont,data);
		if(newfont==UNDEFINED_FONT||newfont==NULL_FONT)
			newfont = basefont;
		if(debug)
			fprintf(log," -->%s\n",CA_FONT_NAME[newfont&63]);
		if(newfont != fonttype){	//別のフォント出現
			if(index!=last){
				ret = arrangeSurface(ret,drawText3(data,size,SdlColor,fonttype,last,index));
				if(debug && ret!=NULL){
					fprintf(log,"[comsurface/drawText2]arrangeSurface surf(%d, %d) %s %d chars.\n",
						ret->w,ret->h,COM_FONTSIZE_NAME[size],index-str);
				}
			}
			fonttype = newfont;	//GOTHIC, SMSUN. GULIM, ARIAL, GEORGIA,…
			last = index;
			if(isAscii(last)){
				secondBase = getFirstFont(last,basefont);	//第二基準フォント
				if(basefont!=secondBase){
					basefont = secondBase;
					if(debug)
						fprintf(log,"[somsurface/drawText2]second base font %s\n",CA_FONT_NAME[basefont]);
				}
			}
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
//	fprintf(log,"**DEBUG** size%d fontsel%d from%08x to%08x\n",size,fontsel,from,to);
	if(fontsel>0x100){
		int code = fontsel>>8;
		int w = data->fontsize_fix;
		if(code==0x0020 || code==0x00a0){
			w = (CA_FONT_SPACE_WIDTH[size] * len)<<w;
		}else if(code==0x30){
			code = 0x3000;
			w = (CA_FONT_3000_WIDTH[fontsel&3][size] * len)<<w;
		}else{
			fprintf(log,"[comsurface/drawText3]fontsel error %d\n",fontsel);
			fflush(log);
			return NULL;
		}
		if(debug){
			int codeno = (code==0x0020)? 0:((code==0x00a0)? 1: 2);
			fprintf(log,"[comsurface/drawText3]return SPACE font %s U+%04X %s %d chars.\n"
				,CA_FONT_NAME[CA_FONT_MAX+codeno],code,COM_FONTSIZE_NAME[size],len);
			fflush(log);
		}
		return drawNullSurface(w,h);
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
		//高さ固定,big16の可能性
		if(width * 0.9 < limit_width){
			return FALSE;
		}
		fprintf(log,"[isDoubleResize]found big16 but too wide.\n");
	}
	if(size==CMD_FONT_DEF && line>=25){
		//高さ固定の可能性
		if(width * 0.9 < limit_width){
			return FALSE;
		}
		fprintf(log,"[isDoubleResize]found medium25 but too wide.\n");
	}
	if(size==CMD_FONT_SMALL && line>=38){
		//高さ固定の可能性
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

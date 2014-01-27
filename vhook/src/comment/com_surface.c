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
SDL_Surface* drawText2(DATA* data,int size,SDL_Color color,Uint16* str,int fill_bg);
SDL_Surface* drawText3(DATA* data,int size,SDL_Color color,FontType fonttype,Uint16* from,Uint16* to,int fill_bg);
SDL_Surface* drawText4(DATA* data,int size,SDL_Color SdlColor,TTF_Font* font,Uint16* str,int fontsel,int fill_bg);
//int cmpSDLColor(SDL_Color col1, SDL_Color col2);
int isDoubleResize(double width, double limit_width, int size, int line, FILE* log, int is_full);
int deleteLastLF(Uint16* index);

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
	int is_button = 0;
	int is_owner = item->chat->cid == CID_OWNER;

	//Script処理
	if(item->script){
		int cmd = item->script & 0xffff0000;
		fprintf(log,"[comsurface/make script]%04x vpos:%d vstart:%d vend%d\n",
			cmd>>16, item->vpos, item->vstart, item->vend);
		if(cmd == SCRIPT_DEFAULT){		//＠デフォルト
			if(color != CMD_COLOR_DEF)
				data->defcolor = color;
			if(location != CMD_LOC_DEF)
				data->deflocation = location;
			if(size != CMD_FONT_DEF)
				data->defsize = size;
			fprintf(log,"[comsurface/make script]@DEFAULT(color:%d location:%d size:%d) done\n",
				color,location,size);
			//nullコメントを表示
			return drawNullSurface(0,0);
		}
		if(cmd == SCRIPT_GYAKU){	//＠逆
			int bits = item->script & 3;
			int vpos = item->vpos;
			int duration = item->duration;
			if(bits & SCRIPT_OWNER){
				data->owner.chat.to_left = -1;
				data->owner.chat.reverse_vpos = vpos;
				data->owner.chat.reverse_duration = duration;
			}
			if(bits & SCRIPT_USER){
				data->user.chat.to_left = -1;
				data->user.chat.reverse_vpos = vpos;
				data->user.chat.reverse_duration = duration;
				data->optional.chat.to_left = -1;
				data->optional.chat.reverse_vpos = vpos;
				data->optional.chat.reverse_duration = duration;
			}
			fprintf(log,"[comsurface/make script]@GYAKU done vpos:%d duration:%d start:%d end:%d\n",
				vpos,duration,item->vstart,item->vend);
			return drawNullSurface(0,0);
		}
		if(cmd == SCRIPT_REPLACE){
			//process comment
			fprintf(log,"[comsurface/make script]@REPLACE done\n");
			return drawNullSurface(0,0);
		}
		if(cmd == SCRIPT_BUTTON){
			//@ボタン
			is_button = 1;
			fprintf(log,"[comsurface/make script]@BUTTON rendering...\n");
		}
	}
	/*
	 * default color変更
	 */
	if(data->defcolor>401){	//401 means April 01, i.e. force april fool
		color = data->defcolor - 401;
		SdlColor = getSDL_color(color);
	}else if(color==CMD_COLOR_DEF){	//this may be @default
		color = data->defcolor;
		SdlColor = getSDL_color(color);
	}
	/*
	 * default size 変更
	 */
	if(size == CMD_FONT_DEF){
		size = data->defsize;
	}
	/*
	 * default lcation 変更
	 */
	if(location == CMD_LOC_DEF){
		location = data->deflocation;
	}
	/*
	 * 影は置いておいて、とりあえず文字の描画
	 */
	SDL_Surface* surf = NULL;
	SDL_Surface* before_button = NULL;
	// last == index == item->str;
	if(deleteLastLF(index)<=0)
		return NULL;
	while(*index != '\0'){
		if(*index=='[' && is_button==1){
			*index = '\0';//ここで一旦切る
			surf = drawText2(data,size,SdlColor,last,FALSE);
			if(surf!=NULL && debug)
				fprintf(log,"[comsurface/make.0]drawText2 surf(%d, %d) %s\n",surf->w,surf->h,COM_FONTSIZE_NAME[size]);
			if(ret != null){
				//改行後のボタン開始
				surf = connectSurface(ret,surf);
				nb_line++;
				if(surf!=NULL && debug)
					fprintf(log,"[comsurface/make.1]connectSurface surf(%d, %d) %s line %d\n",surf->w,surf->h,COM_FONTSIZE_NAME[size],nb_line);
			}
			*index = '[';//ここで一旦切る
			last = index+1;
			before_button = surf;
			ret = NULL;
			is_button = 2;
		}
		else if(*index==']' && is_button==2){
			*index = '\0';//ここで一旦切る
			surf = drawText2(data,size,SdlColor,last,is_owner);
			if(surf!=NULL && debug)
				fprintf(log,"[comsurface/make.0]drawText2 surf(%d, %d)\n",surf->w,surf->h);
			if(ret != NULL){
				//複数行のボタン終了
				surf = connectSurface(ret,surf);
				nb_line++;
				if(surf!=NULL && debug)
					fprintf(log,"[comsurface/make.1]connectSurface surf(%d, %d) line %d\n",surf->w,surf->h,nb_line);
			}
			if(is_owner)
				//投稿者ボタンを塗る
				ret = drawOwnerButton(data,surf,SdlColor);
			else
				//視聴者ボタン
				ret = drawButton(data,surf);

			SDL_FreeSurface(surf);
			if(ret!=NULL && debug)
				fprintf(log,"[comsurface/make.1]drawButton surf(%d, %d)\n",ret->w,ret->h);
			*index = ']';//ここで一旦切る
			last = index+1;
			// ボタン描画終了 ボタン前とボタン後をつなげる
			if(before_button!=NULL){
				// 左右にくっつける
				ret = arrangeSurface(before_button,ret);
				if(ret!=NULL && debug)
					fprintf(log,"[comsurface/make.1]arrange surf(%d, %d)\n",ret->w,ret->h);
			}
			before_button = ret;
			ret = NULL;
			is_button = 3;
		}
		else if(*index == '\n'){
			*index = '\0';//ここで一旦切る
			int fill_bg = is_owner && is_button==2;
			if(ret == null){//最初の改行
				ret = drawText2(data,size,SdlColor,last,fill_bg);
				if(ret!=NULL && debug)
					fprintf(log,"[comsurface/make.0]drawText2 surf(%d, %d) %s\n",ret->w,ret->h,COM_FONTSIZE_NAME[size]);
			}else{/*改行あり*/
				ret = connectSurface(ret,drawText2(data,size,SdlColor,last,fill_bg));
				nb_line++;
				if(ret!=NULL && debug)
					fprintf(log,"[comsurface/make.1]connectSurface surf(%d, %d) %s line %d\n",ret->w,ret->h,COM_FONTSIZE_NAME[size],nb_line);
			}
			*index = '\n';//ここで一旦切る
			last = index+1;
		}
		index++;
	}
	int fill_bg = is_owner && is_button!=0;
	if(ret == null){//結局改行は無い
		ret = drawText2(data,size,SdlColor,last,fill_bg);
		if(debug && ret!=NULL)
			fprintf(log,"[comsurface/make.2]drawText2 surf(%d, %d) %s\n",ret->w,ret->h,COM_FONTSIZE_NAME[size]);
	}else{/*改行あり*/
		ret = connectSurface(ret,drawText2(data,size,SdlColor,last,fill_bg));
		nb_line++;
		if(debug && ret!=NULL)
			fprintf(log,"[comsurface/make.3]connectSurface surf(%d, %d) %s line %d\n",ret->w,ret->h,COM_FONTSIZE_NAME[size],nb_line);

	}
	//ret = surf;
	if(is_button){
		if(is_button==1){
			// [は来なかった
			// ret全体がボタン
			surf = ret;
			if(is_owner)
				ret = drawOwnerButton(data,surf,SdlColor);
			else
				ret = drawButton(data,surf);

			SDL_FreeSurface(surf);
			if(ret!=NULL && debug)
				fprintf(log,"[comsurface/make.3]drawButton surf(%d, %d)\n",ret->w,ret->h);
		}
		else if(is_button==2){
			// [来た後で]の前に終了
			surf = ret;
			if(is_owner)
				ret = drawOwnerButton(data,surf,SdlColor);
			else
				ret = drawButton(data,surf);
			SDL_FreeSurface(surf);
			if(ret!=NULL && debug)
				fprintf(log,"[comsurface/make.3]drawButton surf(%d, %d)\n",ret->w,ret->h);
		}
		if(before_button!=NULL){
			//1つ前が残ってる
			ret = arrangeSurface(before_button,ret);
			if(ret!=NULL && debug)
				fprintf(log,"[comsurface/make.3]arranged surf(%d, %d)\n",ret->w,ret->h);
		}
		is_button = 0;
	}

	if(ret==NULL || ret->h == 0){
		fprintf(log,"***ERROR*** [comsurface/makeE]comment %d has no char.\n",item->no);
		fflush(log);
		return ret;
	}
	if(debug)
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
	int is_black = cmpSDLColor(SdlColor, COMMENT_COLOR[CMD_COLOR_BLACK]);
	if(strstr(data->extra_mode,"font")!=NULL && strstr(data->extra_mode,"fg")!=NULL){
		is_black = 2;	//SHADOW COLOR is FONT
	}
	ret = (*ShadowFunc[shadow])(ret,is_black,data->fontsize_fix,SdlColor);
	if(debug)
	fprintf(log,"[comsurface/make1]ShadowFunc:%d (%d, %d) %s %d line\n",shadow,ret->w,ret->h,COM_FONTSIZE_NAME[size],nb_line);

	/*
	 * アルファ値の設定
	 */
	float alpha_t = 1.0;
	if(data->opaque_rate > 0.0){
		alpha_t = data->opaque_rate;
	}else{
		alpha_t = (((float)(item->no)/(item->chat->max_no)) * 0.4) + 0.6;
		if(item->chat->cid == CID_OPTIONAL && data->optional_trunslucent){
			if(alpha_t>0.3) alpha_t = 0.3;			// これでいいのかな？適当なんだが。
		}
	}
	if(alpha_t<1.0){
		if(debug)
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
	 *  fullコマンドで672(640～?)
	 */
	double nicolimit_width = (double)NICO_WIDTH;
	if(item->full){
		nicolimit_width = (double)NICO_WIDTH_WIDE;
	}
	if(debug)
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

		//if(data->fontsize_fix || data->enableCA){
		if(data->fontsize_fix){
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
		//	コメント高さ補正
		int h = adjustHeight(nb_line,size,FALSE,data->fontsize_fix);
		if(h!=ret->h){
			ret = adjustComment(ret,data,h);
			if(debug)
			fprintf(log,"[comsurface/adjust]comment %d adjust(%d, %d) %s\n",
				item->no,ret->w,ret->h,(data->fontsize_fix?" fix":""));
		}
		// 改行リサイズ
		// コメントの画像の高さがニコニコ動画基準の高さの１／３より大きいと倍率を１／２にする
		// コマンドenderでは改行リサイズなし
		if(zoomx * 3 * ret->h > autoscale * NICO_HEIGHT && !item->ender){
			// ダブルリサイズ検査
			// 改行リサイズ＆改行後の倍率で臨界幅を超えた場合 → 改行リサイズキャンセル
			double linefeed_zoom = linefeedResizeScale(size,nb_line,data->fontsize_fix);
			double resized_w = linefeed_zoom * zoomx * ret->w;
			if((location == CMD_LOC_TOP||location == CMD_LOC_BOTTOM)
				&& isDoubleResize(resized_w, nicolimit_width, size, nb_line, log, item->full)){
				//  ダブルリサイズあり → 改行リサイズキャンセル
				nicolimit_width /= linefeed_zoom;	//*= 2.0;
				double_resized = TRUE;
			} else{
				// ダブルリサイズなし
				zoomx *= linefeed_zoom;	// *= 0.5
				linefeed_resized =TRUE;
			}
		}
/*
		//	コメント高さ補正
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
			/* ue shitaコマンドのみリサイズあり */
			/*
			 * 臨界幅リサイズ
			 * 臨界幅は同倍率の動画で544(512～600)px  動画が4:3か16:9に無関係
			 *  fullコマンドで672(640～?)
			 * 文字の大きさで臨界幅は変動する←正確に合わせるのは現状では無理？
			 *  dFS=(15,24,39),LW=(512,640)
			 *  rFS=round(LW/width*dFS) によって新しいフォントサイズが決まる。
			 *  但しWindowsではwFS=rFS+1（漢字の場合）である。
			 * コメントの幅が動画の幅に収まるように倍率を調整
			 * ダブルリサイズ　→　無条件にリサイズ（判定済み）
			 * 改行リサイズ　→　無条件になし（再判定→フォント幅を縮小）
			 * 両方なし　→　今回判定
			 */
			double rate = nicolimit_width / (double)ret->w;
			if(linefeed_resized && zoomx > rate){
				if(debug)
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
			if(debug)
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
		if(strstr(data->extra_mode,"-frame")!=NULL||data->wakuiro_dat!=NULL||item->waku){
			SDL_Surface* tmp = ret;
			ret = drawFrame(data,item,location,tmp,RENDER_COLOR_BG,1);
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

	//	コメント高さ補正
	int h = adjustHeight(nb_line,size,FALSE,data->fontsize_fix);
	if(h!=ret->h){
		ret = adjustComment(ret,data,h);
		if(debug)
		fprintf(log,"[comsurface/adjust]comment %d adjust(%d, %d) %s\n",
			item->no,ret->w,ret->h,(data->fontsize_fix?" fix":""));
	}
	// コマンドenderでは改行リサイズなし
	double resized_w;
	if (nb_line >= LINEFEED_RESIZE_LIMIT[size] && !item->ender){
		/*
		 * 改行リサイズあり ダブルリサイズ検査
		 * 改行リサイズかつ改行後の倍率で改行臨界幅(nicolimit_width)を超えた場合 → 改行リサイズキャンセル
		 */
		double linefeed_zoom = linefeedResizeScale(size,nb_line,data->fontsize_fix);
		int dfs = COMMENT_FONT_SIZE[size];
		int rfs = (int)round(0.5*(double)dfs);
		double rsRate = (double)(rfs+1)/(double)(dfs+1);
		double resize = linefeed_zoom;
		//double resize = rsRate;
		resized_w = zoom_w * resize;
		if(debug)
		fprintf(log,"[comsurface/LFresize]comment %d LFzoom %.2f%% RSrate %.2f%% %s resized %.0f\n",
			item->no,linefeed_zoom*100.0,rsRate*100.0,COM_FONTSIZE_NAME[size],resized_w);
		if((location == CMD_LOC_TOP||location == CMD_LOC_BOTTOM)
			&& isDoubleResize(resized_w, nicolimit_width, size, nb_line, log, item->full)){
			// ダブルリサイズあり
			double_resized = TRUE;
			//ダブルリサイズ時には動画幅の２倍にリサイズされる筈
			double double_limit_width = nicolimit_width / resize;	//*= 2.0;

			/*
			 * ダブルリサイズの臨界幅リサイズ
			 * 文字の大きさで臨界幅は変動する
			 * コメントの幅が臨界幅の2倍に収まるように倍率を調整
			 */
			if(resized_w > nicolimit_width){
				/*
				 *  dFS=(15,24,39),LW=(512,640)
				 *  rFS=round(LW/width*dFS) によって新しいフォントサイズが決まる。
				 *  但しWindowsではwFS=rFS+1（漢字の場合）である。
				 */
				rfs = (int)round(nicolimit_width/resized_w*(double)dfs);
				rsRate = (double)(rfs+1)/(double)(dfs+1);
				resized_w = zoom_w * rsRate;
				if(debug)
				fprintf(log,"[comsurface/DR limit1]comment %d default width %.0f dFS %d resized %.0f limit %.0f\n",
					item->no,zoom_w,dfs,resized_w,double_limit_width);
				zoom_w = resized_w;
				if(debug)
				fprintf(log,"[comsurface/DR limit2]comment %d %s width %.0f rFS %d wRate %.1f%%\n",
					item->no,COM_FONTSIZE_NAME[size],zoom_w,rfs,rsRate*100.0);
				zoom_h = (zoom_w/(double)ret->w / font_width_rate) * font_height_rate * (double)ret->h;
				//zoomy = zoom_h/(double)ret->h;
			}

			//意図したダブルリサイズならば高さ基準でリサイズした方が良い？
			//実際には高さを1行高くして見えない行を作ることもある
			double wrate = double_limit_width / zoom_w;
			double hrate = (double)NICO_HEIGHT / zoom_h;
			if(debug)
			fprintf(log,"[comsurface/DR detail]comment %d w %.1f%% h %.1f%%%s\n",
				item->no,wrate*100.0,hrate*100.0,(data->fontsize_fix?" fix":""));
			if(strstr(data->extra_mode,"-old")!=NULL){
				//意図したダブルリサイズならば高さ基準でリサイズ -oldモード
				if(size == CMD_FONT_BIG && 8 < nb_line && nb_line < 16){
					//コメント行数により矯正
					double resized_h = COMMENT_BIG_DR_HEIGHT[nb_line];
					hrate = (double)resized_h / zoom_h;
					resized_w = zoom_w * hrate;
					if(debug)
					fprintf(log,"[comsurface/DR AdjByWiki]comment %d maybe(%.0f,%.0f) w %.2f%% h %.2f%% font_width %.2f%%\n",
						item->no,resized_w,resized_h,wrate*100.0,hrate*100.0,font_width_rate*100.0);
					//ダブルリサイズして画面内に表示が出るとは思えないとする
					if(resized_w > nicolimit_width ){
						zoom_w = resized_w;
					}
				}
				else {
					double h2 = wrate / hrate;
					if(385 < zoom_h && zoom_h < 768){
						//コメント高が動画以上でありダブルリサイズにより動画高に合わせたと見る。?
						resized_w = zoom_w * hrate;
						if(resized_w > nicolimit_width && resized_w > zoom_w){
							//横幅が大きいなら補正
							if(debug)
							fprintf(log,"[comsurface/DR hrate1]comment %d resized_width %.0f %.2f%% font_width %.2f%%\n",
								item->no,resized_w,hrate*100.0,font_width_rate*100.0);
							zoom_w = resized_w;
						}else{
							//元のママ
							if(debug)
							fprintf(log,"[comsurface/DR hrate0]comment %d resized_width %.0f %.2f%% font_width %.2f%%\n",
									item->no,resized_w,hrate*100.0,font_width_rate*100.0);
							//zoom_w = resized_w;
						}
					}else
					if(zoom_h <= 385){
						//コメント高が動画以下であり横幅で決めるしか手がないがこれは既に計算したはず。
						if(debug)
						fprintf(log,"[comsurface/DR wrate]comment %d  width %.0f %.2f%% font_width %.2f%%\n",
							item->no,zoom_w,wrate*100.0,font_width_rate*100.0);
					}
					//以下は動画よりコメント高が凄く高い
					else
					if(0.9 <= h2 && h2 <= 1.1){
						//横幅基準で高さが動画より微妙になるなら動画高に合わせる 今はやらない
						//zoom_w *= hrate;
						if(debug)
						fprintf(log,"[comsurface/DR hrate2]comment %d  width %.0f %.2f%% font_width %.2f%%\n",
							item->no,zoom_w,hrate*100.0,font_width_rate*100.0);
					}
					else
					{
						//高さとアス比が動画と全然違うので合わせられない
						//zoom_w *= hrate;
						if(debug)
						fprintf(log,"[comsurface/DR wrate2]comment %d  width %.0f %.2f%% font_width %.2f%%\n",
							item->no,zoom_w,wrate*100.0,font_width_rate*100.0);
					}
				}
			}

		}else{
			// ダブルリサイズなし
			linefeed_resized = TRUE;
			zoom_w = resized_w;	// *= 0.5
			//zoom_h *= linefeed_zoom;
			/* zoomx *= linefeedResizeScale(size,nb_line,data->fontsize_fix); */
		}
	}

	if(location == CMD_LOC_TOP||location == CMD_LOC_BOTTOM){
		// ue shitaコマンドのみリサイズあり

		/*
		 * 臨界幅リサイズ
		 * 文字の大きさで臨界幅は変動する
		 * コメントの幅が臨界幅(または2倍)に収まるように倍率を調整
		 * 改行リサイズ　→　なし（判定済み）だが、実験的にもう一度縮小
		 * ダブルリサイズ　→　nicolimit_widthは2倍済 で判定
		 * 両方なし　→　今回判定
		 */
		if(linefeed_resized && zoom_w > nicolimit_width){
			if(debug)
			fprintf(log,"[comsurface/AfterLF]comment %d previous width%.0f > limit%.0f, font_width %.2f%%%s\n",
				item->no,zoom_w,nicolimit_width,font_width_rate * 100.0,(data->fontsize_fix?" fix":""));
			//zoom_w = nicolimit_width;
		}
		if(!linefeed_resized && !double_resized && zoom_w > nicolimit_width){
			/*
			 *  dFS=(15,24,39),LW=(512,640)
			 *  rFS=round(LW/width*dFS) によって新しいフォントサイズが決まる。
			 *  但しWindowsではwFS=rFS+1（漢字の場合）である。
			 *
			 */
			int dfs = COMMENT_FONT_SIZE[size];
			double rsRate = (round(nicolimit_width/zoom_w*(double)dfs)+1.0)
					/ (double)(dfs+1);
			resized_w = zoom_w * rsRate;
			if(debug)
			fprintf(log,"[comsurface/LWresize]comment %d previous width %.0f dFS %d resize %.1f%% resized %.0f limit %.0f\n",
				item->no,zoom_w,dfs,rsRate*100.0,resized_w,nicolimit_width);
			limit_width_resized = TRUE;
			zoom_w = resized_w;
			if(debug)
			fprintf(log,"[comsurface/LWresize]comment %d width %.0f dFS %d wrate %.1f%%\n",
				item->no,zoom_w,dfs,rsRate*100.0);
		}
	}
	// ue shitaコマンドのみリサイズ終わり

	/*
	 * フォントサイズ自動調整
	 * 動画幅とニコニコ動画の幅のスケール
	 */
	//if(data->fontsize_fix || data->enableCA){
	if(data->fontsize_fix){
		if(autoscale != 1.0f){
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
		if(debug)
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
	 * 枠をつける
	 */
	if(strstr(data->extra_mode,"-frame")!=NULL||data->wakuiro_dat!=NULL||item->waku){
		SDL_Surface* tmp = ret;
		ret = drawFrame(data,item,location,tmp,RENDER_COLOR_BG,1);
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
SDL_Surface* drawText2(DATA* data,int size,SDL_Color SdlColor,Uint16* str,int fill_bg){
	if(str == NULL || str[0] == '\0'){
		return drawNullSurface(0,data->font_pixel_size[size]);
	}
	FILE* log = data->log;
	int debug = data->debug;
	if(!data->enableCA){
		return drawText4(data,size,SdlColor,data->font[size],str,UNDEFINED_FONT,fill_bg);
	}
	SDL_Surface* ret = NULL;
	Uint16* index = str;
	Uint16* last = index;
	int basefont = getFirstFont(last,UNDEFINED_FONT);	//第一基準フォント
	// FontType is font_index(bit 4..0) + space-char-unicode(bit 31..16)
	int secondBase = UNDEFINED_FONT;
	if(debug){
		fprintf(log,"[comsurface/drawText2]first base font %s\n",getfontname(basefont));
	}
	FontType fonttype = basefont;
	FontType newfont = basefont;
	int nextfont = basefont;
	int saved;
	int foundAscii = FALSE;
	int wasAscii = FALSE;
	int isKanji = FALSE;
	int wasKanji = FALSE;
	while(*index != '\0'){
		if(nextfont==UNDEFINED_FONT)
			nextfont = GOTHIC_FONT;
		if(debug)
			fprintf(log,"[comsurface/drawText2]str[%d] U+%04hX try %s (base %s)",
				index-str,*index,getfontname(nextfont),getfontname(basefont));
		//get FontType and spaced code
		newfont = getFontType(index,nextfont,data);
		wasAscii = foundAscii;
		foundAscii = isAscii(index);
		wasKanji = isKanji;
		isKanji = isKanjiWidth(index);
		if(newfont==UNDEFINED_FONT||newfont==NULL_FONT)
			newfont = nextfont;
		if(debug)
			fprintf(log," -->%s%s%s%s%s\n",getfontname(newfont),
				foundAscii?" found_Ascii":"",wasAscii?" was_Ascii":"",
				isKanji?" Kanji":"",isKanji!=wasKanji?" change_Kanji_width":"");
		if(newfont != fonttype || (fonttype!=SIMSUN_FONT && isKanji != wasKanji)){	//別のフォント出現、又は漢字幅チェック変化
			if(index!=last){
				ret = arrangeSurface(ret,drawText3(data,size,SdlColor,fonttype,last,index,fill_bg));
				if(debug && ret!=NULL){
					fprintf(log,"[comsurface/drawText2]arrangeSurface surf(%d, %d) %s %d chars.\n",
						ret->w,ret->h,COM_FONTSIZE_NAME[size],index-str);
				}
			}
			fonttype = newfont;	//Spaced-char or GOTHIC, SMSUN. GULIM, ARIAL, GEORGIA,…
			last = index;
		}
		newfont &= CA_TYPE_MASK;	//here drop spaced attribute
		//第２基準フォントの検査
		if(secondBase==UNDEFINED_FONT){
			if((foundAscii && !wasAscii && basefont<=GOTHIC_FONT)||
				(basefont==GOTHIC_FONT &&(newfont==SIMSUN_FONT || newfont==GULIM_FONT))){
				secondBase = getFirstFont(index,basefont);
				if(secondBase==basefont || secondBase==GOTHIC_FONT){
					secondBase = UNDEFINED_FONT;
				}
				if(secondBase!=UNDEFINED_FONT && debug)
					fprintf(log,"[somsurface/drawText2]second base font %s\n",
							getfontname(secondBase));
			}
		}
		//隣接フォントの検査
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
				getfontname(saved),getfontname(nextfont));
		}
		index++;
	}
	ret = arrangeSurface(ret,drawText3(data,size,SdlColor,fonttype,last,index,fill_bg));
	if(ret==NULL){
		//fprintf(log,"[comsurface/drawText2]drawtext3 NULL last. make NullSurface.\n");
		fprintf(log,"[comsurface/drawText2]***ERR*** drawtext3 NULL last. return Null.\n");
		fflush(log);
		//return drawNullSurface(0,data->font_pixel_size[size]);	//~1.37r
		return NULL;
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
	if(ret == NULL)
		return NULL;	//for Error
	SDL_SetAlpha(left,SDL_RLEACCEL,0xff);	//not use alpha
	SDL_SetAlpha(right,SDL_RLEACCEL,0xff);	//not use alpha
	SDL_Rect rect = {left->w,0,0,0};		//use only x y
	SDL_BlitSurface(left,NULL,ret,NULL);
	SDL_BlitSurface(right,NULL,ret,&rect);
	SDL_FreeSurface(left);
	SDL_FreeSurface(right);
	return ret;
}

SDL_Surface* drawText3(DATA* data,int size,SDL_Color SdlColor,FontType fonttype,Uint16* from,Uint16* to,int fill_bg){
	int len = to-from;
	FILE* log = data->log;
	int debug = data->debug;
	int h = data->font_pixel_size[size];
	int fontsel = GET_TYPE(fonttype);	//get fonttype

	if(isSpaceFont(fonttype)){	//fonttype is one of space-char's
		Uint16 code = GET_CODE(fonttype);	//get unicode0
		int w = data->fontsize_fix;
		if(code==0x0020 || code==0x00A0){
			// half space
			w = (CA_FONT_SPACE_WIDTH[size] * len)<<w;
		}else if(code==0x3000){
			// full space
			if(fontsel > GULIM_FONT){	//fonttype should be 0..2 (gothic,simsun,gulim)
				fprintf(log,"[comsurface/drawText3]fontsel error %d\n",fonttype);
				fflush(log);
				return NULL;
			}
			w = (CA_FONT_3000_WIDTH[fontsel][size] * len)<<w;
		}else if(isZeroWidth(code)){
			// zero width
			w = 0;
			fprintf(log,"[comsurface/drawText3]found ZERO width char 0x%04x\n",code);
		}else if((code & 0xfff0)==0x2000){
			//code should be 2000..200a 200c
			//Here, it assumed fonttype should belog to GOTHIC
			//but width of 2000 series DIFFERS when SIMSUN (or GULIM?) in Windows7
			//futhermore it FAULTS (TOUFU) when ARIAL in XP
			w = (CA_FONT_2000_WIDTH[code & 0x000f][size] * len)<<w;
		}else if(code==0x0009){
			// code 0009 TAB
			w = (CA_FONT_TAB_WIDTH[size] * len)<<w;
		}else {
			fprintf(log,"[comsurface/drawText3]fontsel error %d\n",fonttype);
			fflush(log);
			return NULL;
		}
		SDL_Surface* ret = drawNullSurface(w,h);
		if(debug){
			int codeno;
			switch (code) {
				case 0x0020:	codeno = 0; break;
				case 0x00a0:	codeno = 1; break;
				case 0x3000:	codeno = 3; break;
				case 0x0009:	codeno = 4; break;
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
			fprintf(log,"[comsurface/drawText3]return font %s NULL\n",getfontname(fontsel));
		return drawNullSurface(0,h);
	}
	Uint16* text = (Uint16*)malloc(sizeof(Uint16)*(len+1));
	if(text==NULL){
		fprintf(log,"[comsurface/drawText3]can't alloc memory font %s.\n",getfontname(fontsel));
		fflush(log);
		return NULL;
	}
	Uint16* text2 = text;
	while(from < to){
		//if(!isZeroWidth())
		*text2++ = *from++;
	}
	*text2 ='\0';
	if(debug)
		fprintf(log,"[comsurface/drawText3]building U+%04hX %d chars. in %s %s\n",
			text[0],len,getfontname(fontsel),COM_FONTSIZE_NAME[size]);
	SDL_Surface* ret = drawText4(data,size,SdlColor,data->CAfont[fontsel][size],text,fontsel,fill_bg);
	free(text);
	return ret;
}

SDL_Surface* drawText4(DATA* data,int size,SDL_Color SdlColor,TTF_Font* font,Uint16* str,int fontsel,int fill_bg){
	FILE* log = data->log;
	int debug = data->debug;
	//SDL_Surface* surf = TTF_RenderUNICODE_Blended(font,str,SdlColor);
	//SDL_Color bgc = COMMENT_COLOR[CMD_COLOR_YELLOW];
	SDL_Surface* surf = render_unicode(data,font,str,SdlColor,size,fontsel,fill_bg);

	if(surf==NULL){
		fprintf(log,"***ERROR*** [comsurface/drawText4]TTF_RenderUNICODE : %s\n",TTF_GetError());
		fflush(log);
		return NULL;
	}
	if(debug)
		fprintf(log,"[comsurface/drawText4]TTF_RenderUNICODE surf(%d, %d) %s %d chars\n",
			surf->w,surf->h,COM_FONTSIZE_NAME[size],uint16len(str));
	//高さ補正
	SDL_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
	int difh = data->font_pixel_size[size] - surf->h;
	if(difh==0){
		return surf;
	}
	SDL_Surface* ret = drawNullSurface(surf->w,data->font_pixel_size[size]);
	if(ret==NULL){
		fprintf(log,"***ERROR*** [comsurface/drawText4]drawNullSurface : %s\n",SDL_GetError());
		fflush(log);
		return NULL;
	}
	if(difh > 0){
		difh = (difh+1)>>1;
	}else{
		difh = 0;
		if(debug)
			fprintf(log,"[comsurface/drawText4]hight %d > font_pixel_size %d\n",
				surf->h,data->font_pixel_size[size]);
	}
	SDL_Rect srcrect = {0,0,ret->w,ret->h};
	SDL_Rect destrect = {0,difh,ret->w,ret->h};
	//rect.y = 0;	// = (ret->h - surf->h)>>1
	SDL_BlitSurface(surf,&srcrect,ret,&destrect);
	SDL_FreeSurface(surf);
	return ret;
}

int isDoubleResize(double width, double limit_width, int size, int line, FILE* log, int is_full){
	if(width < limit_width  * 0.9 || width > limit_width * 1.1)
		return width > limit_width;	//10% is abviously ok
	if(size==CMD_FONT_BIG){
		if(8<=line && line<=14){
			//ダブルリサイズの可能性
			if(!is_full && width > limit_width * 0.99){
				if(width < limit_width)
					fprintf(log,"[isDoubleResize]found NotFull and shorter then DR but ok. line:%d width:%.1f\n",line,width);
				return TRUE;
			}
		}
		if(width <= limit_width)
			return FALSE;
		if(line>=16){
			//高さ固定,big16の可能性
			if(width * 0.95 < limit_width){
				if(limit_width<width)
					fprintf(log,"[isDoubleResize]found a little wider then big16 but ok. line:%d width:%.1f %s\n",
						line,width,is_full? "Full":"NotFull");
				return FALSE;
			}
		//	fprintf(log,"[isDoubleResize]found big16 but too wide.\n");
		}
		if(is_full){
			//full
			if(width * 0.95 < limit_width){
				if(limit_width<width)
					fprintf(log,"[isDoubleResize]found big is wider, but linefeed resize. line:%d width:%.1f Full\n",line,width);
				return FALSE;
			}
		}
		else {
			fprintf(log,"[isDoubleResize]found bigDR not linefeed resize. line:%d width:%.1f NotFull\n",
				line,width);
			return TRUE;
		}
		fprintf(log,"[isDoubleResize]found big is wider for linefeed resize. line:%d width:%.1f \n",
			line, width);
		return width > limit_width;
	}
	if(width <= limit_width)
		return FALSE;
	if((size==CMD_FONT_DEF || size==CMD_FONT_MEDIUM) && line>=25){
		//高さ固定の可能性
		if(width * 0.95 < limit_width){
			fprintf(log,"[isDoubleResize]found wider then medium25 but ok.\n");
			return FALSE;
		}
	}
	if(size==CMD_FONT_SMALL && line>=38){
		//高さ固定の可能性
		if(width * 0.95 < limit_width){
			fprintf(log,"[isDoubleResize]found wider then small38 but ok.\n");
			return FALSE;
		}
	}
	if(width * 0.95 < limit_width){
		fprintf(log,"[isDoubleResize]found a little wide for linefeed resize. line:%d width:%.1f \n",
			line, width);
	}
	return width > limit_width;
}

int deleteLastLF(Uint16* index){
	Uint16* p = NULL;
	int l = 1028;
	while(*index != '\0' && l-->0){
		p = index++;
	}
	if(p!=NULL && *p=='\n'){
		*p = '\0';
	}
	return l;
}

SDL_Surface* getErrFont(DATA* data){
	Uint16 errMark[2] = {0x2620, '\0'};
	if(data->ErrFont == NULL){
		TTF_Font* font =(data->enableCA)?
			data->CAfont[GOTHIC_FONT][CMD_FONT_SMALL]
			: data->font[CMD_FONT_SMALL];
		data->ErrFont = drawText4(data,CMD_FONT_SMALL,COMMENT_COLOR[CMD_COLOR_PASSIONORANGE],font,errMark,GOTHIC_FONT,FALSE);
	}
	SDL_Surface* ret = NULL;
	if(data->ErrFont!=NULL){
		ret = drawNullSurface(data->ErrFont->w,data->ErrFont->h);
		SDL_SetAlpha(ret,SDL_RLEACCEL,0xff);	//not use alpha
		SDL_BlitSurface(data->ErrFont,NULL,ret,NULL);
	}
	return ret;	//copied ErrFont
}

void closeErrFont(DATA* data){
	if(data->ErrFont != NULL){
		SDL_FreeSurface(data->ErrFont);
	}
}

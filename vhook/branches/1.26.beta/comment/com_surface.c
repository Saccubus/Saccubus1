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
	 * 影は置いておいて、とりあえず文字の描画
	 */
	while(*index != '\0'){
		if(*index == '\n'){
			*index = '\0';//ここで一旦切る
			if(ret == null){//結局改行は無い
				ret = drawText(data,size,color,last);
			}else{/*改行あり*/
				ret = connectSurface(ret,drawText(data,size,color,last),next_y_diff);
				nb_line++;
			}
			*index = '\n';//ここで一旦切る
			last = index+1;
		}
		index++;
	}
	if(ret == null){//結局改行は無い
		ret = drawText(data,size,color,item->str);
	}else{/*改行あり*/
		ret = connectSurface(ret,drawText(data,size,color,last),next_y_diff);
		nb_line++;
	}

	if(ret->w == 0 || ret->h == 0){
		fprintf(data->log,"[comsurface/make]comment %4d has no char.\n",item->no);
		fflush(data->log);
		return ret;
	}

	/*
	 * 影処理
	 */
	int shadow = data->shadow_kind;
	if(shadow >= SHADOW_MAX){
		shadow = SHADOW_DEFAULT;
	}
	int is_fix_size = data->font_scaling != 1;
	ret = (*ShadowFunc[shadow])(ret,item->color == CMD_COLOR_BLACK,is_fix_size);

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
		fprintf(data->log,"[comsurface/make]comment %4d set alpha:%5.2f%%.\n",item->no,alpha_t*100.0f);
		setAlpha(ret,alpha_t);
	}
	fprintf(data->log,"[comsurface/make]comment %4d builded (%d, %d), %d line.\n",item->no,ret->w,ret->h,nb_line);

 if (data->original_resize){
	// さきゅばす従来
	/*
	 * スケール設定
	 * 横幅 zoomx
	 * 高さ zoomy	実験的にratio(%)を指定する
	 */

	double zoomx = 1.0;
	double zoomy = (double)(data->font_h_fix_r)/100.0;
	//縮小

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
	/*スケールの調整*/
	//if(((double)ret->h * zoomy) > ((double)video_height/3.0f)){
	if(((double)ret->h * zoomx) > ((double)video_height/3.0f)){
		zoomx *= 0.5f;
		//zoomy *= 0.5f;
		// 	コメントの画像の高さが動画の高さの１／３より大きいと倍率を１／２にする
		//  さきゅばす独自リサイズ？　↑の根拠は　？
		//  もしかして　改行リサイズ？
		saccubus_resized =TRUE;
	}
	int videowidth_resized = FALSE;
	if(item->location != CMD_LOC_DEF && (ret->w * zoomx) > (double)video_width){
		double scale = ((double)video_width) / (ret->w * zoomx);
		zoomx *= scale;
		//zoomy *= scale;
		//  コメントの幅が動画の幅に収まるように倍率を調整　＝　臨界幅リサイズ
		videowidth_resized = TRUE;
	}
	//  改行リサイズ　未実装
	//  ダブルリサイズ　未実装

	zoomy *= zoomx;

	//画面サイズに合わせて変更
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
	// 実験
	// 参考 1 pt = 1/72 inch, 1 px = 1 dot
	/*
	 * スケール設定
	 */
	double zoomx = 1.0f;

	// フォントサイズを２倍にしたか？
	if(data->font_scaling == 2){
		zoomx = 0.5f;
	} else if(data->font_scaling != 1){
		zoomx /= (double)data->font_scaling;
	}

	int limit_width_resized = FALSE;
	int linefeed_resized = FALSE;
	int double_resized = FALSE;
	int auto_scaled = FALSE;

	// 改行リサイズ
	if(data->linefeed_resize){
		if (item->location != CMD_LOC_DEF && (nb_line >= LINEFEED_RESIZE_LIMIT[item->size])){
			linefeed_resized = TRUE;
			zoomx *= 0.5f;
		}
	}

	// 臨界幅リサイズ
	// 臨界幅は同倍率の動画で544(512～600)px  動画が4:3か16:9に無関係
	// 　　　　fullコマンドで672(640～?)
	// test1: 普通に見切れないように動画幅にする
	// 文字の大きさで臨界幅は変動する
	double nicolimit_width;
	if(item->full){
	//	nicolimit_width = (double)data->nico_limit_width_full;
		nicolimit_width = (double)NICO_WIDTH_WIDE;
	} else {
	//	nicolimit_width = (double)data->nico_limit_width;
		nicolimit_width = (double)NICO_WIDTH;
	}
	if(data->limitwidth_resize){
		// コメントの幅が動画の幅(または指定の臨界幅)に収まるように倍率を調整
		if(item->location != CMD_LOC_DEF && (ret->w * zoomx) > nicolimit_width){
			zoomx *= nicolimit_width / (ret->w * zoomx);;
			limit_width_resized = TRUE;
		}
	}

	//  ダブルリサイズ
	if (data->double_resize){
		if(linefeed_resized && limit_width_resized){
			// 改行リサイズかつ臨界幅リサイズ実施 → 改行リサイズキャンセル
			zoomx *= 2.0f;
			// この結果、見かけの臨界幅は動画横幅より大きくなる
			//           見切りの横幅は変化しない。
			double_resized = TRUE;
		}
	}

	// フォントサイズ自動調整
	// 動画幅とニコニコ動画の幅のスケール
	double autoscale = (double)video_width / (double)data->nico_width_now;
	if(data->fontsize_fix && autoscale != 1.0f){
		zoomx *= autoscale;
		auto_scaled = TRUE;
	}

	// 高さの調整
	double zoomy = zoomx * (double)(data->font_h_fix_r) / 100.0;

	FILE* log = data->log;
	//画面サイズに合わせて変更
	if(zoomx != 1.0f || zoomy != 1.0f){
	//if(zoomx != 1.0f){
		fprintf(log,"[comsurface/make]comment %4d resized.(%5.2f%%,%5.2f%%)\n",item->no,zoomx*100,zoomy*100);
		//fprintf(data->log,"[comsurface/make]comment %04d resized.(%5.2f%%)\n",item->no,zoomx*100);
		//fflush(log);
		SDL_Surface* zoomed = zoomSurface(ret,zoomx,zoomy,SMOOTHING_ON);
		SDL_FreeSurface(ret);
		ret = zoomed;
	}

	//動画の横幅を見切る 16:9動画のみ？
	// fullコマンドは広い 544→672px(この場合は見切れない)
	int view_trimed = FALSE;
	// 見切り横幅
	int view_limit_width =  data->nico_limit_width * autoscale;
	if(item->full){
		view_limit_width = data->nico_limit_width_full * autoscale;
	}
	// nakaコマンドは見切れない
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
 * 文字を描画
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

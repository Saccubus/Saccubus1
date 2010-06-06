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

SDL_Surface* makeCommentSurface(DATA* data,const CHAT_ITEM* item,int video_width,int video_height){
	Uint16* index = item->str;
	Uint16* last = item->str;
	SDL_Surface* ret = NULL;
	int color = item->color;
	int size = item->size;

	/*
	 * 影は置いておいて、とりあえず文字の描画
	 */
	while(*index != '\0'){
		if(*index == '\n'){
			*index = '\0';//ここで一旦切る
			if(ret == null){//結局改行は無い
				ret = drawText(data,size,color,last);
			}else{/*改行あり*/
				ret = connectSurface(ret,drawText(data,size,color,last));
			}
			*index = '\n';//ここで一旦切る
			last = index+1;
		}
		index++;
	}
	if(ret == null){//結局改行は無い
		ret = drawText(data,size,color,item->str);
	}else{/*改行あり*/
		ret = connectSurface(ret,drawText(data,size,color,last));
	}
	
	if(ret->w == 0 || ret->h == 0){
		fprintf(data->log,"[comsurface/make]comment %04d has no char.\n",item->no);
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
	  ret = (*ShadowFunc[shadow])(ret,item->color == CMD_COLOR_BLACK,data->fontsize_fix);

	/*
	 * アルファ値の設定
	 */
	 if(!data->opaque_comment){
		float alpha_t = (((float)(item->no)/(item->chat->max_no)) * 0.4) + 0.6;
		fprintf(data->log,"[comsurface/make]comment %04d set alpha:%5.2f%%.\n",item->no,alpha_t*100);
		setAlpha(ret,alpha_t);
	 }

	/*
	 * スケール設定
	 */

	double zoomx = 1.0f;
	//double zoomy = 1.0f;
	//縮小
	
	if(data->fontsize_fix){
		zoomx = (0.5f * (double)video_width) / (double)NICO_WIDTH;
		//zoomy = (0.5f * (double)video_height) / (double)NICO_HEIGHT;
	}

	/*スケールの調整*/
	//if(((double)ret->h * zoomy) > ((double)video_height/3.0f)){
	if(((double)ret->h * zoomx) > ((double)video_height/3.0f)){
		zoomx *= 0.5f;
		//zoomy *= 0.5f;
	}
	if(item->location != CMD_LOC_DEF && (ret->w * zoomx) > (double)video_width){
		double scale = ((double)video_width) / (ret->w * zoomx);
		zoomx *= scale;
		//zoomy *= scale;
	}
	//画面サイズに合わせて変更
	//if(zoomx != 1.0f || zoomy != 1.0f){
	if(zoomx != 1.0f){
		//fprintf(data->log,"[comsurface/make]comment %04d resized.(%5.2f%%,%5.2f%%)\n",item->no,zoomx*100,zoomy*100);
		fprintf(data->log,"[comsurface/make]comment %04d resized.(%5.2f%%)\n",item->no,zoomx*100);
		fflush(data->log);
		SDL_Surface* tmp = ret;
		ret = zoomSurface(tmp,zoomx,zoomx,SMOOTHING_ON);
		SDL_FreeSurface(tmp);
	}

	return ret;
}
/**
 * 文字を描画
 */

SDL_Surface* drawText(DATA* data,int size,int color,Uint16* str){
	if(str[0] == '\0'){
		return SDL_CreateRGBSurface(	SDL_SRCALPHA | SDL_HWSURFACE | SDL_HWACCEL,
										0,COMMENT_FONT_SIZE[size],32,
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

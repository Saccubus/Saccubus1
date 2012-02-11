/*
 * adjustComment.c
 *
 *  Created on: 2011/12/26
 *      Author: orz
 */
#include "adjustComment.h"
#include <SDL/SDL.h>
#include "../nicodef.h"
#include "../main.h"
#include "../mydef.h"
#include "com_surface.h"
#include <stdio.h>
/*
THANKS for Comment Artisan! A.K.A. SHOKUNIN!
 http://www37.atwiki.jp/commentart/pages/26.html
 http://www37.atwiki.jp/commentart?cmd=upload&act=open&pageid=21&file=%E3%82%B3%E3%83%A1%E3%83%B3%E3%83%88%E9%AB%98%E3%81%95%E4%B8%80%E8%A6%A7.jpg

  size        n    Windows Mac   Linux?
  DEF(medium) 1-4  29n+5   27n+5
  (resized)   > 5  15n+3   14n+3
  BIG         1-2  45n+5   43n+5
  (resized)   > 3  24n+3   14n+3
  SMALL       1-6  18n+5   17n+5
  (resized?)  7    10n+3   17n+5
  (resized)   > 8  10n+3    9n+3
 */

int adjustHeight(int nb_line,int size,int linefeedResize,int fontFixed){
	int h;
	if(linefeedResize){
		h = LINEFEED_RESIZED_PIXEL_SIZE[size] * nb_line + 3;
	}else{
		h = FONT_PIXEL_SIZE[size] * nb_line + 5;
	}
	h <<= fontFixed;
	return h;
}

double linefeedResizeScale(int size,int nb_line,int fontFixed){
	return (double)adjustHeight(nb_line,size,TRUE,fontFixed) / (double)adjustHeight(nb_line,size,FALSE,fontFixed);
}

SDL_Surface* adjustComment(SDL_Surface* surf,DATA* data,int height){
	//not make nor use alpha
	int width = surf->w;
	SDL_Surface* ret = drawNullSurface(width, height);
	if(ret==NULL){
		FILE* log = data->log;
		fprintf(log,"***ERROR*** [comsurface/adjust]drawNullSurface : %s\n",SDL_GetError());
		fflush(log);
		return surf;
	}
	SDL_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
	SDL_Rect rect = {0,0,width,height};
	SDL_BlitSurface(surf,&rect,ret,NULL);
	SDL_FreeSurface(surf);
	return ret;
}

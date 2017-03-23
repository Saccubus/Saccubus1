/*
 * adjustComment.c
 *
 *  Created on: 2011/12/26
 *      Author: orz
 */
#include <SDL/SDL.h>
#include "adjustComment.h"
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
 *
 * html5コメントの複数行高さ　 ヒロスさんのブロマガより
 * http://ch.nicovideo.jp/883797/blomaga/ar1149544
 *
 * 最小二乗法　 not resized       Line resized
 * 定数+補正　big   medium small  big    medium  small
 *  a       42.452 27.143 16.95  22.356 14.094  9.4058
 *  b        4.05   5.076  4.9    2.6     2.748 3.138
 */

int adjustHeight(int nb_line,int size,int linefeedResize,int fontFixed, int html5){
	int h;
	if(html5){
		h = (int)floor(HTML5_PIXEL_SIZE[linefeedResize][size][0] * nb_line
					+ HTML5_PIXEL_SIZE[linefeedResize][size][1]);
	}else{
		if(linefeedResize)
			h = LINEFEED_RESIZED_PIXEL_SIZE[size] * nb_line + 3;
		else
			h = FONT_PIXEL_SIZE[size] * nb_line + 5;
	}
	h <<= fontFixed;
	return h;
}

double linefeedResizeScale(int size,int nb_line,int fontFixed,int html5){
	return (double)adjustHeight(nb_line,size,TRUE,fontFixed,html5)
		/ (double)adjustHeight(nb_line,size,FALSE,fontFixed,html5);
}

h_Surface* adjustComment(h_Surface* surf,DATA* data,int height){
	h_Surface* ret = adjustComment2(surf,height);
	if(ret==NULL){
		fprintf(data->log,"***ERROR*** [comsurface/adjust]adjustComment2 : %s\n",SDL_GetError());
		fflush(data->log);
		return NULL;
	}
	return ret;
}
h_Surface* adjustComment2(h_Surface* surf,int height){
	if(surf==NULL)
		return NULL;
	int y = (height - surf->h)>>1;
	if(y == 0)
		return surf;
	int width = surf->w;
	h_Surface* ret = drawNullSurface(width, height);
	if(ret==NULL)
		return surf;
	int srcy = 0;
	int dsty = 0;
	if(y > 0){
		srcy = 0;
		dsty = y;
	}else{
		srcy = -y;
		dsty = 0;
		height -= srcy;
	}
	SDL_Rect srcrect = {0, srcy, width, height};
	SDL_Rect dstrect = {0, dsty, width, height};
	//not make nor use alpha
	h_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
	h_BlitSurface(surf,&srcrect,ret,&dstrect);
	h_FreeSurface(surf);
	return ret;
}

h_Surface* adjustCommentSize(h_Surface* surf,int width,int height){
	if(surf==NULL)
		return NULL;
	int y = (height - surf->h)>>1;
	int x = (width - surf->w)>>1;
	if(x==0 && y == 0)
		return surf;
	h_Surface* ret = drawNullSurface(width, height);
	if(ret==NULL)
		return surf;
	int srcx = 0;
	int srcy = 0;
	int dstx = 0;
	int dsty = 0;
	if(x > 0){
		srcx = 0;
		dstx = x;
	}else{
		srcx = -x;
		dstx = 0;
		width -= srcx<<1;
	}
	if(y > 0){
		srcy = 0;
		dsty = y;
	}else{
		srcy = -y;
		dsty = 0;
		height -= srcy;
	}
	SDL_Rect srcrect = {srcx, srcy, width, height};
	SDL_Rect dstrect = {dstx, dsty, width, height};
	//not make nor use alpha
	h_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
	h_BlitSurface(surf,&srcrect,ret,&dstrect);
	h_FreeSurface(surf);
	return ret;
}

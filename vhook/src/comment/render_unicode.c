/*
 * ttf_unicode.c
 *
 *  Created on: 2012/02/12
 *      Author: orz
 */
#include <SDL/SDL_rotozoom.h>
#include "render_unicode.h"
#include "surf_util.h"
#include "com_surface.h"
#include "../unicode/uniutil.h"

SDL_Surface* pointsConv(DATA* data,SDL_Surface* surf,Uint16* str,int size,int fontsel);
SDL_Surface* render_unicode(DATA* data,TTF_Font* font,Uint16* str,SDL_Color fg,int size,int fontsel){
	//SDL_Surface* surf = TTF_RenderUNICODE_Blended(font,str,SdlColor);
	SDL_Surface* ret;
	const char* mode=data->extra_mode;
	if(strstr(mode,"-render")==NULL){
		ret = TTF_RenderUNICODE_Blended(font,str,fg);	//default original mode
	}else{
		SDL_Color bg = {0,0,0,0};
		int fontfg = FALSE;
		if(strstr(mode,"-font")!=NULL){
			switch(fontsel){
			case GOTHIC_FONT:	bg.r = 0xff; break;
			case SIMSUN_FONT:	bg.g = 0xff; break;
			case GULIM_FONT:	bg.b = 0xff; break;
			case -1:
			case ARIAL_FONT:	bg.r = bg.g = bg.b = 0xff;	break;
			default:			break;
			}
		}
		if(strstr(mode,"-fg")!=NULL){
			fg = bg;
			bg = COMMENT_COLOR[CMD_COLOR_BLACK];
			fontfg = TRUE;
		}
		SDL_Surface* surf;
		Uint32 colkey;
		if(strstr(mode,"-solid")!=NULL){
			surf = TTF_RenderUNICODE_Solid(font,str,fg);
			colkey = 0;
		}else if(strstr(mode,"-shaded")!=NULL){
			SDL_Color black = {0x00,0x00,0x00,0x00};
			surf = TTF_RenderUNICODE_Shaded(font,str,fg,black);
			colkey = 0;
		}else{
			surf = TTF_RenderUNICODE_Blended(font,str,fg);
			//colkey = SDL_MapRGBA(surf->format,0,0,0,0);
			colkey = 0;
		}
		SDL_Surface* tmp = drawNullSurface(surf->w,surf->h);	//surface for background
		if (!fontfg){
			if(cmpSDLColor(fg,bg)){
				bg.r = fg.r ^ 0x01;
			}
			Uint32 bgc = SDL_MapRGBA(tmp->format,bg.r,bg.g,bg.b,255);	//bg color in pixformat
			SDL_Rect rect = {1,1,tmp->w-1,tmp->h-1};	//rectangle for fill with bgc
			SDL_FillRect(tmp,&rect,bgc);
		}
		SDL_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use surface alpha in RGBA(with pixel alpha)
		SDL_SetColorKey(surf,SDL_SRCCOLORKEY|SDL_RLEACCEL,colkey);
		SDL_BlitSurface(surf,NULL,tmp,NULL);
		SDL_FreeSurface(surf);
		SDL_SetColorKey(tmp,SDL_RLEACCEL,0xff);	//reset color key
		ret = tmp;
	}
	if(strstr(mode,"-point")!=NULL || strstr(mode,"-tune")!=NULL){
		ret = pointsConv(data,ret,str,size,fontsel);
	}
	return ret;
}

SDL_Surface* pointsConv(DATA *data,SDL_Surface* surf,Uint16 *str,int size,int fontsel){
	SDL_Surface* ret;
	FILE *log = data->log;
	int sizeFix = data->fontsize_fix;

	int ow = surf->w;
	int oh = surf->h;
	// point(72dpi)->pixel(96dpi) 1.33333333”{
	//int len = uint16len(str);
	double doh;
	double dh;
	double rate;
	if(fontsel<GOTHIC_FONT || fontsel>ARIAL_FONT){
		fontsel=GOTHIC_FONT;
	}
	doh = (double)oh;
	dh = (double)(CA_FONT_HIGHT_TUNED[fontsel][0][size] << sizeFix);
	if(dh==doh){
		return surf;
	}
	rate = dh/doh;
	ret = zoomSurface(surf,rate,rate,SMOOTHING_ON);
	SDL_FreeSurface(surf);
	if(ret==NULL){
		fprintf(log,"***ERROR*** [comsurface/point]zoomSurface : %s\n",SDL_GetError());
		fflush(log);
		return NULL;
	}
	fprintf(log,"[point] build(%d, %d)->(%d, %d)%s%s%s.\n",
		ow,oh,ret->w,ret->h,(data->original_resize ? "": " dev"),(data->enableCA?" CA":""),(data->fontsize_fix?" fix":""));
	return ret;
}

SDL_Surface* drawFrame(DATA* data,const CHAT_ITEM* item,SDL_Surface* surf,SDL_Color col,int s){

	if(strstr(data->extra_mode,"-loc")!=NULL){
		col.r = col.g = col.b = 0x00;
		switch (item->location) {
			case CMD_LOC_TOP:
				col.g = 0xff;
				break;
			case CMD_LOC_BOTTOM:
				col.b = 0xff;
				break;
			case CMD_LOC_DEF:
				col.r = col.g = 0xff;
				break;
		}
	}
	if(strstr(data->extra_mode,"-double")!=NULL){
		s <<= 1;
	}
	SDL_Surface* tmp = drawNullSurface(surf->w,surf->h);
	SDL_Rect rect = {0,0,tmp->w,tmp->h};
	SDL_Rect rect2 = {s,0,tmp->w-(s<<1),tmp->h-(s<<1)};
	SDL_Rect rect3 = {s,s,tmp->w-(s<<1),tmp->h-(s<<1)};
	Uint32 col32 = SDL_MapRGB(tmp->format,col.r,col.g,col.b);
	SDL_FillRect(tmp,&rect,col32);
	SDL_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
	SDL_BlitSurface(surf,&rect2,tmp,&rect3);
	SDL_SetClipRect(tmp,&rect);
	return tmp;
}

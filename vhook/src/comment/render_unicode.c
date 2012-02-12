/*
 * ttf_unicode.c
 *
 *  Created on: 2012/02/12
 *      Author: orz
 */
#include "render_unicode.h"
#include "surf_util.h"
#include "com_surface.h"

SDL_Surface* render_unicode(DATA* data,TTF_Font* font,Uint16* str,SDL_Color fg,int fontsel){
	//SDL_Surface* surf = TTF_RenderUNICODE_Blended(font,str,SdlColor);
	if(!data->debug){
		return TTF_RenderUNICODE_Blended(font,str,fg);	//default original mode
	}
	SDL_Color bg = {0,0,0,0};
	int fontfg = FALSE;
	if(strstr(data->debug_mode,"-font")!=NULL){
		switch(fontsel){
		case GOTHIC_FONT:	bg.r = 0xff; break;
		case SIMSUN_FONT:	bg.g = 0xff; break;
		case GULIM_FONT:	bg.b = 0xff; break;
		case ARIAL_FONT:	bg.r = bg.g = bg.b = 0xff;	break;
		default:			break;
		}
	} else {
		bg = RENDER_COLOR_BG;
	}
	if(strstr(data->debug_mode,"-fontfg")!=NULL){
		fg = bg;
		bg = COMMENT_COLOR[CMD_COLOR_BLACK];
		fontfg = TRUE;
	}
	SDL_Surface* surf;
	Uint32 colkey;
	if(strstr(data->debug_mode,"-solid")!=NULL){
		surf = TTF_RenderUNICODE_Solid(font,str,fg);
		colkey = 0;
	}else if(strstr(data->debug_mode,"-shaded")!=NULL){
		SDL_Color black = {0x00,0x00,0x00,0x00};
		surf = TTF_RenderUNICODE_Shaded(font,str,fg,black);
		colkey = 0;
	}else{
		surf = TTF_RenderUNICODE_Blended(font,str,fg);
		colkey = SDL_MapRGBA(surf->format,0,0,0,0);
	}
	SDL_Surface* tmp = drawNullSurface(surf->w,surf->h);	//surface for background
	if (!fontfg){
		if(cmpSDLColor(fg,bg)){
			bg.r = fg.r ^ 0x01;
		}
		Uint32 bgc = SDL_MapRGBA(tmp->format,bg.r,bg.g,bg.b,128);	//bg color in pixformat
		SDL_Rect rect = {1,1,tmp->w-1,tmp->h-1};	//rectangle for fill with bgc
		SDL_FillRect(tmp,&rect,bgc);
	}
	SDL_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use surface alpha in RGBA(with pixel alpha)
	SDL_SetColorKey(surf,SDL_SRCCOLORKEY|SDL_RLEACCEL,colkey);
	SDL_BlitSurface(surf,NULL,tmp,NULL);
	SDL_FreeSurface(surf);
	//SDL_SetColorKey(tmp,SDL_RLEACCEL,0);	//reset color key
	return tmp;
}

SDL_Surface* drawFrame(DATA* data,const CHAT_ITEM* item,SDL_Surface* surf,SDL_Color col,int s){

	if(strstr(data->debug_mode,"-loc")!=NULL){
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
	if(strstr(data->debug_mode,"-double")!=NULL){
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

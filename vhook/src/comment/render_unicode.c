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
#include "../wakuiro.h"

SDL_Surface* pointsConv(DATA* data,SDL_Surface* surf,Uint16* str,int size,int fontsel);
SDL_Surface* widthFixConv(DATA *data,SDL_Surface* surf,Uint16 *str,int size,int fontsel);
SDL_Surface* render_unicode(DATA* data,TTF_Font* font,Uint16* str,SDL_Color fg,int size,int fontsel){
	//SDL_Surface* surf = TTF_RenderUNICODE_Blended(font,str,SdlColor);
	SDL_Surface* ret;
	const char* mode=data->extra_mode;
	if(strstr(mode,"-font")==NULL){
		ret = TTF_RenderUNICODE_Blended(font,str,fg);	//default original mode
	}else{
		SDL_Color bg = {0,0,0,0};
		int fontfg = FALSE;
		switch(fontsel){
		case GOTHIC_FONT:	bg.r = 0xff; break;	//red
		case SIMSUN_FONT:	bg.g = 0xff; break;	//green
		case GULIM_FONT:	bg.b = 0xff; break;	//blue
		case UNDEFINED_FONT:
		case ARIAL_FONT:	bg.r = bg.g = 0xff;	break;	//yellow
		default:			bg.r = bg.g = bg.b = 0x80; break;	//gray
		}
		if(strstr(mode,"-fg")!=NULL){	//use whith -font, -font-fg
			fg = bg;
			bg = COMMENT_COLOR[CMD_COLOR_BLACK];
			fontfg = TRUE;
		}
		SDL_Surface* surf;
		Uint32 colkey;
		SDL_Color black = COMMENT_COLOR[CMD_COLOR_BLACK];
		surf = TTF_RenderUNICODE_Shaded(font,str,fg,black);
		colkey = 0;
		SDL_Surface* tmp = drawNullSurface(surf->w,surf->h);	//surface for background
		if (!fontfg){
			if(cmpSDLColor(fg,bg)){
				bg.r = fg.r ^ 0xff;
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
//	if(data->original_resize)
//		return ret;
	if(strstr(mode,"-point")!=NULL || strstr(mode,"-tune")!=NULL){
		ret = pointsConv(data,ret,str,size,fontsel);
	}else if(strstr(mode,"-old")==NULL){
		ret = widthFixConv(data,ret,str,size,fontsel);
	}
	return ret;
}

SDL_Surface* pointsConv(DATA *data,SDL_Surface* surf,Uint16 *str,int size,int fontsel){
	SDL_Surface* ret;
	FILE *log = data->log;
	int sizeFix = data->fontsize_fix;

	int ow = surf->w;
	int oh = surf->h;
	// point(72dpi)->pixel(96dpi) 1.33333333倍
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

SDL_Surface* widthFixConv(DATA *data,SDL_Surface* surf,Uint16 *str,int size,int fontsel){
	FILE* log = data->log;
	if(fontsel < GOTHIC_FONT || fontsel > ARIAL_FONT){
		if(data->debug)
			fprintf(log,"[render_unicode/widthFix]not change Font %s.\n",getfontname(fontsel));
		return surf;
	}
	switch (fontsel) {
		// now defined only when　fontsel==SIMSUN_FONT
		case SIMSUN_FONT:
			break;
		// asume MOMOSPACE when fontsel==GULIM and KanjiWidth
		case GULIM_FONT:
			if(isKanjiWidth(str)){
				break;
			}
			if(data->debug)
				fprintf(log,"[render_unicode/widthFix]not change Font %s.\n",getfontname(fontsel));
			return surf;
		// asume MOMOSPACE when fontsel==GOTHIC and KanjiWidth
		case GOTHIC_FONT:
			if(isKanjiWidth(str)){
				break;
			}
			if(data->debug)
				fprintf(log,"[render_unicode/widthFix]not change Font %s.\n",getfontname(fontsel));
			return surf;
		case ARIAL_FONT:
		default:
			if(data->debug)
				fprintf(log,"[render_unicode/widthFix]not change Font %s(defalut).\n",getfontname(fontsel));
			return surf;
	}
	SDL_Surface* ret = NULL;
	int w = surf->w;
	int h = surf->h;
	int l = uint16len(str);
	int dfs = CA_FONT_NICO_WIDTH[fontsel][size];
	int dfw = dfs * l;	// dFW=dFS*len + BorderWidth - shodow_width
	dfw <<= data->fontsize_fix;
	if(w == dfw){
		if(data->debug)
			fprintf(log,"[render_unicode/widthFix]same width %d.\n",w);
		return surf;
	}
	ret = drawNullSurface(dfw,h);
	if(w > dfw){
		if(data->debug)
			fprintf(log,"[render_unicode/widthFix]width shrinking %d to %d.\n",w,dfw);
		int x = MIN(2,(w-dfw)>>1);
		SDL_Rect src = {x,0,dfw,h};
		SDL_Rect dest = {0,0,dfw,h};
		SDL_Rect rect = {0,0,dfw,h};
		SDL_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
		SDL_BlitSurface(surf,&src,ret,&dest);
		SDL_SetClipRect(ret,&rect);
		SDL_FreeSurface(surf);
	}else{
		if(data->debug)
			fprintf(data->log,"[render_unicode/widthFix]width expanding %d to %d.\n",w,dfw);
		int x = MIN(2,(dfw-w)>>1);
		SDL_Rect src = {0,0,w,h};
		SDL_Rect dest = {x,0,w,h};
		SDL_Rect rect = {0,0,dfw,h};
		SDL_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
		SDL_BlitSurface(surf,&src,ret,&dest);
		SDL_SetClipRect(ret,&rect);
		SDL_FreeSurface(surf);
	}
	if(data->debug)
		fprintf(data->log,"[render_unicode/widthFix]width fixed done.\n");
	return ret;
}

SDL_Surface* drawFrame(DATA* data,const CHAT_ITEM* item,int location,SDL_Surface* surf,SDL_Color col,int s){
	int is_color_set = FALSE;
	char buf[16];
	if(data->debug)
		fprintf(data->log,"[render_unicode/drawFrame]comment %d waku.\n",item->no);
	if(data->wakuiro_dat!=NULL){
		//枠色変更
		int no = item->no;
		int cid = item->chat->cid;
		no = SET_WAKUIRO(cid,no);
		int color = item->color;
		int colkey = GET_WAKUIRO_KEY(item->color);
		if(colkey==WAKUIRO_COLORCODE)
			color = SET_WAKUIRO(WAKUIRO_COLORCODE,color);
		else
			color = SET_WAKUIRO(WAKUIRO_COLORNAME,color);
		unsigned int* waku = data->wakuiro_dat;
		int n = waku[0];
		if(n == 1){
			int wcolor = waku[1];
			if(GET_WAKUIRO_KEY(wcolor)==WAKUIRO_COLORNAME)
				wcolor = GET_WAKUIRO_VAL(wcolor);
			col = getSDL_color(wcolor);
			is_color_set = TRUE;
			if(data->debug)
				fprintf(data->log,"[render_unicode/drawFrame]comment %d %s all wakuiro %s #%06x\n",
					item->no,item->chat->com_type,getColorName(buf,wcolor),wcolor & 0xffffff);
		}else{
			n <<= 1;
			int i;
			for(i = 2; i < n; i+=2){
				int key = waku[i];
				if(key == no || key == color){
					int wcolor = waku[i+1];
					if(GET_WAKUIRO_KEY(wcolor)==WAKUIRO_COLORNAME)
						wcolor = GET_WAKUIRO_VAL(wcolor);
					col = getSDL_color(wcolor);
					is_color_set = TRUE;
					if(data->debug)
						fprintf(data->log,"[render_unicode/drawFrame]comment %d %s wakuiro %s #%06x\n",
							item->no,item->chat->com_type,getColorName(buf,wcolor),wcolor & 0xffffff);
					break;
				}
			}
		}
	}
	if(!is_color_set && strstr(data->extra_mode,"-frame")==NULL && item->waku==0){
		//wakuiro is set, but waku is not set at this comment nor color
		//no frame is drawn, just copy surf
		SDL_Surface* tmp = drawNullSurface(surf->w,surf->h);
		SDL_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
		SDL_BlitSurface(surf,NULL,tmp,NULL);
		return tmp;
	}
	if(!is_color_set && strstr(data->extra_mode,"-loc")!=NULL){
		col.r = col.g = col.b = 0x00;
		switch (location) {
			case CMD_LOC_TOP:
			//	col.g = 0xff;
				col.r = 0xff;	//red
				break;
			case CMD_LOC_BOTTOM:
				col.b = 0xff;	//blue
				break;
			case CMD_LOC_DEF:
			case CMD_LOC_NAKA:
				col.r = col.g = 0xff;	//yellow
				break;
		}
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

SDL_Surface* drawButton(DATA* data,SDL_Surface* surf){
	if(data->debug)
		fprintf(data->log,"[render_unicode/drawButton]waku(%d,%d)\n",surf->w,surf->h);
	//@ボタン（視聴者）
	//frame is not drawn yet,draw of width 3*lines line as frame.
	// s should be set to frame width
	int s = MAX(surf->h / 20,1);
	SDL_Color col = COMMENT_COLOR[CMD_COLOR_WHITE];
	SDL_Surface* tmp = drawNullSurface(surf->w,surf->h);
	SDL_Rect rect = {0,0,tmp->w,tmp->h};
//	SDL_Rect rect2 = {2,0,tmp->w-4,tmp->h-(s<<1)};
	SDL_Rect rect3 = {2,s,tmp->w-4,tmp->h-(s<<1)};
	Uint32 col32 = SDL_MapRGB(tmp->format,col.r,col.g,col.b);
	if(data->debug)
		fprintf(data->log,"[render_unicode/drawButton]waku(%d,%d) color#%06x w%d\n",tmp->w,tmp->h,col32,s);
	SDL_FillRect(tmp,&rect,col32);
	SDL_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
	SDL_BlitSurface(surf,&rect3,tmp,&rect3);
	SDL_SetClipRect(tmp,&rect);
	return tmp;
}

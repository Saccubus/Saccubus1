/*
 * ttf_unicode.c
 *
 *  Created on: 2012/02/12
 *      Author: orz
 */
#include <SDL/SDL_rotozoom.h>
#include "surf_util.h"
#include "render_unicode.h"
#include "com_surface.h"
#include "../unicode/uniutil.h"
#include "../wakuiro.h"

h_Surface* pointsConv(DATA* data,h_Surface* surf,Uint16* str,int size,int fontsel);
h_Surface* widthFixConv(DATA *data,h_Surface* surf,Uint16 *str,int size,int fontsel);
h_Surface* render_unicode(DATA* data,TTF_Font* font,Uint16* str1,SDL_Color fg,int size,int fontsel,int fill_bg){
	//SDL_Surface* surf = TTF_RenderUNICODE_Blended(font,str,SdlColor);
	//html5 font check
	Uint16* str = str1;
	if(data->html5comment){
		int limit = 128;
		while(*str!='\0' && --limit>0){
			if(!isGlyphExist(data,fontsel,*str)){
				*str = (Uint16)0x3000;
			}
			str++;
		}
		str = str1;
	}
	h_Surface* ret;
	const char* mode=data->extra_mode;
	if(strstr(mode,"-font")==NULL && !fill_bg){
		ret = newSurface(TTF_RenderUNICODE_Blended(font,str,fg));	//default original mode
		if(ret==NULL){
			fprintf(data->log,"***ERROR*** [ttf_unicode/render_unicode]TTF_RenderUNICODE_Blended : %s\n",TTF_GetError());
			fflush(data->log);
			return NULL;
		}
	}else{
		SDL_Color bg = {0,0,0,0};
		int fontfg = FALSE;
		if(fill_bg){
			bg = fg;
			fg = COMMENT_COLOR[CMD_COLOR_WHITE];
			fontfg = FALSE;
		}else{
			switch(fontsel){
			case GOTHIC_FONT:	bg.r = 0xff; break;	//red
			case SIMSUN_FONT:	bg.g = 0xff; break;	//green
			case GULIM_FONT:	bg.b = 0xff; break;	//blue
			case MINGLIU_FONT:	bg.g = bg.b = 0xff;	break;	//cyan
			case UNDEFINED_FONT:
			case ARIAL_FONT:	bg.r = bg.g = 0xff;	break;	//yellow
			default:			bg.r = bg.g = bg.b = 0x80; break;	//gray
			}
			if(strstr(mode,"-fg")!=NULL){	//use whith -font, -font-fg
				fg = bg;
				bg = COMMENT_COLOR[CMD_COLOR_BLACK];
				fontfg = TRUE;
			}
		}
		Uint32 colkey;
		SDL_Color black = COMMENT_COLOR[CMD_COLOR_BLACK];
		h_Surface* surf = newSurface(TTF_RenderUNICODE_Shaded(font,str,fg,black));
		if(surf==NULL){
			fprintf(data->log,"***ERROR*** [ttf_unicode/render_unicode]TTF_RenderUNICODE_Shaded : %s\n",TTF_GetError());
			fflush(data->log);
			return NULL;
		}
		colkey = 0;		//it must be black
		h_Surface* tmp = drawNullSurface(surf->w,surf->h);	//surface for background
		if(tmp==NULL){
			fprintf(data->log,"***ERROR*** [ttf_unicode/render_unicode]drawNullSurface/SDL_CreateRGBSurface: %s\n",SDL_GetError());
			fflush(data->log);
			return NULL;
		}
		if (!fontfg){
			if(cmpSDLColor(fg,bg)){
				bg.r = fg.r ^ 0xff;
			}
			Uint32 bgc = SDL_MapRGBA(tmp->s->format,bg.r,bg.g,bg.b,255);	//bg color in pixformat
			SDL_Rect rect = {0,0,tmp->w,tmp->h};	//rectangle for fill with bgc
			h_FillRect(tmp,&rect,bgc);
		}
		h_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use surface alpha in RGBA(with pixel alpha)
		h_SetColorKey(surf,SDL_SRCCOLORKEY|SDL_RLEACCEL,colkey);
		h_BlitSurface(surf,NULL,tmp,NULL);
		h_FreeSurface(surf);
		h_SetColorKey(tmp,SDL_RLEACCEL,0xff);	//reset color key
		ret = tmp;
	}
	if(data->original_resize||data->html5comment)
		return ret;
	if(strstr(mode,"-point")!=NULL || strstr(mode,"-tune")!=NULL){
		ret = pointsConv(data,ret,str,size,fontsel);
	}else if(strstr(mode,"-old")==NULL){
		ret = widthFixConv(data,ret,str,size,fontsel);
	}
	return ret;
}

h_Surface* pointsConv(DATA *data,h_Surface* surf,Uint16 *str,int size,int fontsel){
	h_Surface* ret;
	FILE *log = data->log;
	int sizeFix = data->fontsize_fix;

	if(surf==NULL) return NULL;
	int ow = surf->w;
	int oh = surf->h;
	// point(72dpi)->pixel(96dpi) 1.33333333倍
	//int len = uint16len(str);
	double doh;
	double dh;
	double rate;
	if(fontsel!=MINGLIU_FONT && (fontsel<GOTHIC_FONT || fontsel>ARIAL_FONT)){
		fontsel=GOTHIC_FONT;
	}
	doh = (double)oh;
	dh = (double)(CA_FONT_HIGHT_TUNED[fontsel][0][size] << sizeFix);
	if(dh==doh){
		return surf;
	}
	rate = dh/doh;
	ret = newSurface(zoomSurface(surf->s,rate,rate,SMOOTHING_ON));
	h_FreeSurface(surf);
	if(ret==NULL){
		fprintf(log,"***ERROR*** [comsurface/point]zoomSurface : %s\n",SDL_GetError());
		fflush(log);
		return NULL;
	}
	fprintf(log,"[point] build(%d, %d)->(%d, %d)%s%s%s.\n",
		ow,oh,ret->w,ret->h,(data->original_resize ? "": " dev"),(data->enableCA?" CA":""),(data->fontsize_fix?" fix":""));
	return ret;
}

h_Surface* widthFixConv(DATA *data,h_Surface* surf,Uint16 *str,int size,int fontsel){
	FILE* log = data->log;
	if(fontsel < GOTHIC_FONT || (fontsel > ARIAL_FONT && fontsel!=MINGLIU_FONT)){
		if(data->debug)
			fprintf(log,"[render_unicode/widthFix]not change Font %s.\n",getfontname(fontsel));
		return surf;
	}
	switch (fontsel) {
		// now defined only when　fontsel==SIMSUN_FONT
		case SIMSUN_FONT:
			break;
		// alse fontsel==MINGLIU
		case MINGLIU_FONT:
			fontsel = SIMSUN_FONT;
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
	h_Surface* ret = NULL;
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
		h_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
		h_BlitSurface(surf,&src,ret,&dest);
		h_SetClipRect(ret,&rect);
		h_FreeSurface(surf);
	}else{
		if(data->debug)
			fprintf(data->log,"[render_unicode/widthFix]width expanding %d to %d.\n",w,dfw);
		int x = MIN(2,(dfw-w)>>1);
		SDL_Rect src = {0,0,w,h};
		SDL_Rect dest = {x,0,w,h};
		SDL_Rect rect = {0,0,dfw,h};
		h_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
		h_BlitSurface(surf,&src,ret,&dest);
		h_SetClipRect(ret,&rect);
		h_FreeSurface(surf);
	}
	if(data->debug)
		fprintf(data->log,"[render_unicode/widthFix]width fixed done.\n");
	return ret;
}

h_Surface* drawFrame(DATA* data,const CHAT_ITEM* item,int location,h_Surface* surf,SDL_Color col,int s){
	int is_color_set = FALSE;
	int is_frame_set = strstr(data->extra_mode,"-frame")!=NULL;
	int pixel_down = 0;
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
	if(is_frame_set && item->waku && !is_color_set){
		//waku command and -frame option and no -wakuiro -> command typing red
		col = COMMENT_COLOR[CMD_COLOR_RED];
		if(item->location == CMD_LOC_BOTTOM){
			pixel_down = s;
		}
		s <<= 1;	// frame size doublize
		is_color_set = TRUE;
	}
	if(!is_color_set && !is_frame_set && item->waku==0){
		//wakuiro is set, but waku is not set at this comment nor color
		//no frame is drawn, just copy surf
		h_Surface* tmp = drawNullSurface(surf->w,surf->h);
		h_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
		h_BlitSurface(surf,NULL,tmp,NULL);
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
	h_Surface* tmp = drawNullSurface(surf->w,surf->h);
	SDL_Rect rect = {0,pixel_down,tmp->w,tmp->h-pixel_down};
	SDL_Rect rect2 = {s,0,tmp->w-(s<<1),tmp->h-(s<<1)};
	SDL_Rect rect3 = {s,s+pixel_down,tmp->w-(s<<1),tmp->h-(s<<1)};
	Uint32 col32 = SDL_MapRGB(tmp->s->format,col.r,col.g,col.b);
	h_FillRect(tmp,&rect,col32);
	h_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
	h_BlitSurface(surf,&rect2,tmp,&rect3);
	h_SetClipRect(tmp,&rect);
	return tmp;
}

h_Surface* drawUserButton(DATA* data,h_Surface* surf){
	if(data->debug)
		fprintf(data->log,"[render_unicode/drawUserButton]waku(%d,%d)\n",surf->w,surf->h);
	//@ボタン（視聴者）
	//frame is not drawn yet,draw of width height/20 px line as frame.
	// s should be set to frame width
	int s = MAX(surf->h / 20,1);
	SDL_Color col = COMMENT_COLOR[CMD_COLOR_WHITE];
	h_Surface* tmp = drawNullSurface(surf->w,surf->h);
	SDL_Rect rect = {0,0,tmp->w,tmp->h};
//	SDL_Rect rect2 = {2,0,tmp->w-4,tmp->h-(s<<1)};
	SDL_Rect rect3 = {2,s,tmp->w-4,tmp->h-(s<<1)};
	Uint32 col32 = SDL_MapRGB(tmp->s->format,col.r,col.g,col.b);
	if(data->debug)
		fprintf(data->log,"[render_unicode/drawUserButton]waku(%d,%d) color#%06x w%d\n",tmp->w,tmp->h,col32,s);
	h_FillRect(tmp,&rect,col32);
	h_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
	h_BlitSurface(surf,&rect3,tmp,&rect3);
	h_SetClipRect(tmp,&rect);
	return tmp;
}

h_Surface* drawOwnerButton(DATA* data,h_Surface* surf,SDL_Color col){
	if(data->debug)
		fprintf(data->log,"[render_unicode/drawOwnerButton]waku(%d,%d)\n",surf->w,surf->h);
	//@ボタン（投稿者）
	//surface nor frame is not drawn yet,
	//paint surface with color and draw string with WHITE
	int s = 3;
	h_Surface* tmp = drawNullSurface(surf->w,surf->h);
	SDL_Rect rect = {0,0,tmp->w,tmp->h};
	SDL_Rect rect2 = {s,0,tmp->w-(s<<1),tmp->h-(s<<1)};
	SDL_Rect rect3 = {s,s,tmp->w-(s<<1),tmp->h-(s<<1)};
	Uint32 col32 = SDL_MapRGB(tmp->s->format,col.r,col.g,col.b);
	if(data->debug)
		fprintf(data->log,"[render_unicode/drawOwnerButton]waku(%d,%d) color#%06x w%d\n",tmp->w,tmp->h,col32,s);
	h_FillRect(tmp,&rect,col32);
	h_SetAlpha(surf,SDL_RLEACCEL,0xff);	//not use alpha
	h_BlitSurface(surf,&rect2,tmp,&rect3);
	h_SetClipRect(tmp,&rect);
	return tmp;
}
h_Surface* drawButton(DATA* data,h_Surface* surf,SDL_Color col,int is_owner)
{
	if(is_owner!=0)
		return drawOwnerButton(data,surf,col);
	else
		return drawUserButton(data,surf);
}

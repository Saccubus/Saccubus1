/*
 * ttf_unicode.h
 *
 *  Created on: 2012/02/12
 *      Author: orz
 */

#ifndef TTF_UNICODE_H_
#define TTF_UNICODE_H_
#include <SDL/SDL.h>
#include <SDL/SDL_ttf.h>
#include "../main.h"
#include "../mydef.h"
#define RENDER_COLOR_BG COMMENT_COLOR[CMD_COLOR_YELLOW]

SDL_Surface* render_unicode(DATA* data,TTF_Font* font,Uint16* str,SDL_Color SdlColor,int size,int fontsel,int fill_bg);
SDL_Surface* drawFrame(DATA* data,const CHAT_ITEM* item,int location,SDL_Surface* surf,SDL_Color col,int s);
SDL_Surface* drawButton(DATA* data,SDL_Surface* surf,SDL_Color col,int is_owner);
SDL_Surface* drawOwnerButton(DATA* data,SDL_Surface* surf,SDL_Color col);

#endif /* TTF_UNICODE_H_ */

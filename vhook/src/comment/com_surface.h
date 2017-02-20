#ifndef COM_SURFACE_H_
#define COM_SURFACE_H_
#include <SDL/SDL.h>
#include "surf_util.h"
#include "../chat/chat.h"
#include "../chat/chat_slot.h"
#include "../main.h"

SDL_Surface* makeCommentSurface(DATA* data,CHAT_ITEM* item,int video_width,int video_height);
SDL_Surface* getErrFont(DATA* data);
void closeErrFont(DATA* data);

#endif /*COM_SURFACE_H_*/

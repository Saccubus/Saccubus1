#ifndef COM_SURFACE_H_
#define COM_SURFACE_H_
#include <SDL/SDL.h>
#include "../chat/chat.h"
#include "../chat/chat_slot.h"
#include "../main.h"

SDL_Surface* makeCommentSurface(DATA* data,const CHAT_ITEM* item,int video_width,int video_height,int next_y_diff);


#endif /*COM_SURFACE_H_*/

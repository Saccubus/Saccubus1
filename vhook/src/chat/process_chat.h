#ifndef PROCESS_CHAT_H_
#define PROCESS_CHAT_H_
#include <SDL/SDL.h>
#include "chat.h"
#include "chat_slot.h"
#include "../main.h"
int process_chat(DATA* data,CDATA* cdata, const char* com_type,SDL_Surface* surf,const int now_vpos);
int getX(int now_vpos,const CHAT_SLOT_ITEM* item,int video_width);
void setspeed(int comment_speed,CHAT_SLOT_ITEM* item,int video_width,int nico_width);

#endif /*PROCESS_CHAT_H_*/

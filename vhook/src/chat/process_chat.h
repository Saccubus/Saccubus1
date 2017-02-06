#ifndef PROCESS_CHAT_H_
#define PROCESS_CHAT_H_
#include <SDL/SDL.h>
#include "chat.h"
#include "chat_slot.h"
#include "../main.h"
int process_chat(DATA* data,CDATA* cdata,SDL_Surface* surf,const int now_vpos);
double getX(int now_vpos,CHAT_SLOT_ITEM* item,int video_width,double scale,int aspect_mode);
double getX1(FILE* log,int now_vpos,CHAT_SLOT_ITEM* item,int video_width,double scale,int aspect_mode);
void setspeed(DATA* data,CHAT_SLOT_ITEM* item,int video_width,int nico_width,double scale);
int getVposItem(DATA* data,CHAT_SLOT_ITEM* item,int n_xpos,double s_tpos);
int convSDLcolor(SDL_Color sc);
#endif /*PROCESS_CHAT_H_*/

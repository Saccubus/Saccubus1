#include <SDL/SDL.h>
#include "chat.h"
#include "chat_slot.h"
#include "process_chat.h"
#include "../main.h"
#include "../mydef.h"

//このソース内でしか使わないメソッド
void drawComment(SDL_Surface* surf,CHAT_SLOT* slot,int now_vpos);
int convSDLcolor(SDL_Color sc);

/**
 * コメントを描画する。
 */
int process_chat(DATA* data,CDATA* cdata,const char* com_type,SDL_Surface* surf,const int now_vpos){
	CHAT* chat;
	CHAT_SLOT* slot;
	CHAT_ITEM* chat_item;
	CHAT_SLOT_ITEM* slot_item;
	FILE* log = data->log;
	if (cdata->enable_comment){
		/*
		if(data->debug){
			fprintf(log,"[process-chat/DEBUG]<vpos:%d>%s w:%d, h:%d\n",now_vpos,com_type,surf->w,surf->h);
		}
		*/
		/*見せないものを削除 */
		slot = &cdata->slot;
		resetChatSlotIterator(slot);
		while((slot_item = getChatSlotErased(slot,now_vpos)) != NULL){
			chat_item = slot_item->chat_item;
			fprintf(log,"[process-chat/process]comment %d<vpos:%d>%s<color:%d:#%06x loc:%d size:%d  %d - %d(vpos:%d)> erased. \n",
				chat_item->no,now_vpos,com_type,chat_item->color,convSDLcolor(chat_item->color24),chat_item->location,chat_item->size,chat_item->vstart,chat_item->vend,chat_item->vpos);
			fflush(log);
			deleteChatSlot(slot,slot_item);
		}
		/*見せるものをセット*/
		chat = &cdata->chat;
		resetChatIterator(chat);
		while((chat_item = getChatShowed(chat,now_vpos)) != NULL){
			addChatSlot(data,slot,chat_item,surf->w,surf->h);
			fprintf(log,"[process-chat/process]comment %d<vpos:%d>%s<color:%d:#%06x loc:%d size:%d  %d - %d(vpos:%d)> added. \n",
				chat_item->no,now_vpos,com_type,chat_item->color,convSDLcolor(chat_item->color24),chat_item->location,chat_item->size,chat_item->vstart,chat_item->vend,chat_item->vpos);
			fflush(log);
		}
		drawComment(surf,slot,now_vpos);
	}
	return TRUE;
}
int convSDLcolor(SDL_Color sc){
	return ((sc.r)<<16)+((sc.g)<<8)+(sc.b);
}

/*
 * レイヤ順にそって描画する
 */

void drawComment(SDL_Surface* surf,CHAT_SLOT* slot,int now_vpos){
	int i;
	SDL_Rect rect;
	int max_item = slot->max_item;
	CHAT_SLOT_ITEM* item;
	for(i=0;i<max_item;i++){
		item = &slot->item[i];
		if(item->used){
			rect.x = getX(now_vpos,item,surf->w);
			rect.y = item->y;
			SDL_BlitSurface(item->surf,NULL,surf,&rect);
		}
	}
}

/*
 * 位置を求める
 */
int getX(int now_vpos,const CHAT_SLOT_ITEM* item,int video_width){
	int text_width = item->surf->w;
	int width = video_width;
	if(item->chat_item->location != CMD_LOC_DEF){
		return (width - text_width) >>1;
	}else{
		int tmp = now_vpos - item->chat_item->vpos + TEXT_AHEAD_SEC;
		if(item->speed < 0.0f){
			return -text_width -(tmp * item->speed);
		}
		return width - tmp * item->speed;
	}
	return -1;
}

/**
 *
 */
void setspeed(int comment_speed,CHAT_SLOT_ITEM* item,int video_width){
	int text_width = item->surf->w;
	CHAT_ITEM* chat_item = item->chat_item;
	if(chat_item->location!=CMD_LOC_DEF){
		item->speed = 0.0f;
	}else if(comment_speed!=0){
		item->speed = (float)comment_speed/(float)VPOS_FACTOR;
		chat_item->vend = chat_item->vstart
			+ (video_width + text_width) * VPOS_FACTOR / abs(comment_speed);
	}else{
		item->speed = (float)(video_width + text_width)
			/(float)(chat_item->vend - chat_item->vstart);
	}
}

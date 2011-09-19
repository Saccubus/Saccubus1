#include <SDL/SDL.h>
#include "chat.h"
#include "chat_slot.h"
#include "process_chat.h"
#include "../main.h"
#include "../mydef.h"

//このソース内でしか使わないメソッド
void drawComment(SDL_Surface* surf,CHAT_SLOT* slot,int now_vpos);

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
		/*見せないものを削除 */
		slot = &cdata->slot;
		resetChatSlotIterator(slot);
		while((slot_item = getChatSlotErased(slot,now_vpos)) != NULL){
			chat_item = slot_item->chat_item;
			fprintf(log,"[process-chat/process]%s<vpos:%6d>com %4d<color:%2d loc:%2d size:%d %d..%d(vpos:%d)> erased. \n",
				com_type,now_vpos,chat_item->no,chat_item->color,chat_item->location,chat_item->size,chat_item->vstart,chat_item->vend,chat_item->vpos);
			fflush(log);
			deleteChatSlot(slot,slot_item);
		}
		/*見せるものをセット*/
		chat = &cdata->chat;
		resetChatIterator(chat);
		while((chat_item = getChatShowed(chat,now_vpos)) != NULL){
			fprintf(log,"[process-chat/process]%s<vpos:%6d>com %4d<color:%2d loc:%2d size:%d %d..%d(vpos:%d)> added. \n",
				com_type,now_vpos,chat_item->no,chat_item->color,chat_item->location,chat_item->size,chat_item->vstart,chat_item->vend,chat_item->vpos);
			fflush(log);
			addChatSlot(data,slot,chat_item,surf->w,surf->h);
		}
		drawComment(surf,slot,now_vpos);
	}
	return TRUE;
}

/*
 * レイヤ順にそって描画する
 */
// slot->max_item回のSDL書き込みが行われる
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
	CHAT_ITEM* chat_item = item->chat_item;
	if(chat_item->location != CMD_LOC_DEF){
		return (width - text_width) >>1;
	}else{
		int tmp = now_vpos - chat_item->vstart;
		return width - ((tmp * (width + text_width)) / chat_item->duration);
	}
	return -1;
}

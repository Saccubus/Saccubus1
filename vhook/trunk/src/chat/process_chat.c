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
int chat_process(DATA* data,SDL_Surface* surf,const int now_vpos){
	CHAT* chat = &data->chat;
	CHAT_SLOT* slot = &data->slot;
	CHAT* opt_chat = &data->optionalchat;
	CHAT_SLOT* opt_slot = &data->optionalslot;
	CHAT* ochat = &data->ownerchat;
	CHAT_SLOT* oslot = &data->ownerslot;
	FILE* log = data->log;
	CHAT_SLOT_ITEM* slot_item;
	CHAT_ITEM* chat_item;
	/*見せないものを削除 */
	if (data->enable_user_comment){
		resetChatSlotIterator(slot);
		while((slot_item = getChatSlotErased(slot,now_vpos)) != NULL){
			chat_item = slot_item->chat_item;
			fprintf(log,"[process-chat/process]<vpos:%6d>com%4d<color:%2d loc:%2d size:%2d %6d-%6d(%6d)> erased. \n",now_vpos,chat_item->no,chat_item->color,chat_item->location,chat_item->size,chat_item->vstart,chat_item->vend,chat_item->vpos);
			fflush(log);
			deleteChatSlot(slot,slot_item);
		}
	}
	/*見せないものを削除 owner*/
	if (data->enable_owner_comment && ochat && oslot){
		resetChatSlotIterator(oslot);
		while((slot_item = getChatSlotErased(oslot,now_vpos)) != NULL){
			chat_item = slot_item->chat_item;
			fprintf(log,"[process-chat/process]<vpos:%6d:owner>com%4d<color:%2d loc:%2d size:%2d %6d-%6d(%6d)> erased. \n",now_vpos,chat_item->no,chat_item->color,chat_item->location,chat_item->size,chat_item->vstart,chat_item->vend,chat_item->vpos);
			fflush(log);
			deleteChatSlot(oslot,slot_item);
		}
	}
	/*見せないものを削除 optional*/
	if (data->enable_optional_comment && opt_chat && opt_slot){
		resetChatSlotIterator(opt_slot);
		while((slot_item = getChatSlotErased(opt_slot,now_vpos)) != NULL){
			chat_item = slot_item->chat_item;
			fprintf(log,"[process-chat/process]<vpos:%6d:optional>com%4d<color:%2d loc:%2d size:%2d %6d-%6d(%6d)> erased. \n",now_vpos,chat_item->no,chat_item->color,chat_item->location,chat_item->size,chat_item->vstart,chat_item->vend,chat_item->vpos);
			fflush(log);
			deleteChatSlot(opt_slot,slot_item);
		}
	}
	/*見せるものをセット*/
	if (data->enable_user_comment){
		resetChatIterator(chat);
		while((chat_item = getChatShowed(chat,now_vpos)) != NULL){
			fprintf(log,"[process-chat/process]<vpos:%6d>com%4d<color:%2d loc:%2d size:%2d %6d-%6d(%6d)> added. \n",now_vpos,chat_item->no,chat_item->color,chat_item->location,chat_item->size,chat_item->vstart,chat_item->vend,chat_item->vpos);
			fflush(log);
			addChatSlot(data,slot,chat_item,surf->w,surf->h);
		}
		drawComment(surf,slot,now_vpos);
	}
	/*見せるものをセット owner*/
	if (data->enable_owner_comment && ochat && oslot){
		resetChatIterator(ochat);
		while((chat_item = getChatShowed(ochat,now_vpos)) != NULL){
			fprintf(log,"[process-chat/process]<vpos:%6d:owner>com%4d<color:%2d loc:%2d size:%2d %6d-%6d(%6d)> added. \n",now_vpos,chat_item->no,chat_item->color,chat_item->location,chat_item->size,chat_item->vstart,chat_item->vend,chat_item->vpos);
			fflush(log);
			addChatSlot(data,oslot,chat_item,surf->w,surf->h);
		}
		drawComment(surf,oslot,now_vpos);
	}
	/*見せるものをセット optional*/
	if (data->enable_optional_comment && opt_chat && opt_slot){
		resetChatIterator(opt_chat);
		while((chat_item = getChatShowed(opt_chat,now_vpos)) != NULL){
			fprintf(log,"[process-chat/process]<vpos:%6d:optional>com%4d<color:%2d loc:%2d size:%2d %6d-%6d(%6d)> added. \n",now_vpos,chat_item->no,chat_item->color,chat_item->location,chat_item->size,chat_item->vstart,chat_item->vend,chat_item->vpos);
			fflush(log);
			addChatSlot(data,opt_slot,chat_item,surf->w,surf->h);
		}
		drawComment(surf,opt_slot,now_vpos);
	}
	return TRUE;
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
		return width - ((tmp * (width + text_width)) / TEXT_SHOW_SEC);
	}
	return -1;
}

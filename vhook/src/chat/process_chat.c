#include <SDL/SDL.h>
#include <math.h>
#include "chat.h"
#include "chat_slot.h"
#include "process_chat.h"
#include "../main.h"
#include "../mydef.h"

//このソース内でしか使わないメソッド
void drawComment(DATA* data,SDL_Surface* surf,CHAT_SLOT* slot,int now_vpos,int x,int y);
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
			fprintf(log,"[process-chat/process]comment %d vpos:%d %s color:%d:#%06x %5s %6s  %d - %d(vpos:%d) erased.\n",
				chat_item->no,now_vpos,com_type,chat_item->color,convSDLcolor(chat_item->color24),
				COM_LOC_NAME[chat_item->location],COM_FONTSIZE_NAME[chat_item->size],chat_item->vstart,chat_item->vend,chat_item->vpos);
			fflush(log);
			deleteChatSlot(slot,slot_item);
		}
		/*見せるものをセット*/
		chat = &cdata->chat;
		resetChatIterator(chat);
		while((chat_item = getChatShowed(chat,now_vpos)) != NULL){
			addChatSlot(data,slot,chat_item,data->vout_width,data->vout_height);
		//	addChatSlot(data,slot,chat_item,surf->w,surf->h);
			fprintf(log,"[process-chat/process]comment %d vpos:%d %s color:%d:#%06x %5s %6s  %d - %d(vpos:%d) added.\n",
				chat_item->no,now_vpos,com_type,chat_item->color,convSDLcolor(chat_item->color24),
				COM_LOC_NAME[chat_item->location],COM_FONTSIZE_NAME[chat_item->size],chat_item->vstart,chat_item->vend,chat_item->vpos);
			fflush(log);
		}
		drawComment(data,surf,slot,now_vpos,data->vout_x,data->vout_y);
	}
	return TRUE;
}
/*
 * cnvert SDL_Color to RGB 24bit
 */
int convSDLcolor(SDL_Color sc){
	return ((sc.r)<<16)+((sc.g)<<8)+(sc.b);
}

/*
 * レイヤ順にそって描画する
 */

void drawComment(DATA* data,SDL_Surface* surf,CHAT_SLOT* slot,int now_vpos, int x, int y){
	int i;
	SDL_Rect rect;
	int max_item = slot->max_item;
	CHAT_SLOT_ITEM* item;
	for(i=0;i<max_item;i++){
		item = &slot->item[i];
		if(item->used){
			rect.x = getX(now_vpos,item,data->vout_width) + x;
			rect.y = item->y + y;
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
		int tmp = now_vpos - item->chat_item->vstart;
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
void setspeed(int comment_speed,CHAT_SLOT_ITEM* item,int video_width,int nico_width){
	int text_width = item->surf->w;
	CHAT_ITEM* chat_item = item->chat_item;
	if(chat_item->location!=CMD_LOC_DEF){
		item->speed = 0.0f;
	}else{
		//int vpos = chat_item->vpos;
		int vstart = chat_item->vstart;
		float width = video_width + text_width;
		if(nico_width == NICO_WIDTH_WIDE){
			//ワイドの場合は表示期間が５秒
			//ただし衝突判定はvposを基準に４秒である。
			item->speed = width * 0.8f / (float)TEXT_SHOW_SEC;
			//chat_item->vstart = (int)(vpos - (TEXT_AHEAD_SEC * 1.25));
			chat_item->vpos = (int)(vstart + (TEXT_AHEAD_SEC * 1.25));
			chat_item->vend = (int)(vstart + (TEXT_SHOW_SEC * 1.25));
		}else{
			item->speed = width / (float)TEXT_SHOW_SEC;
		}
		if(comment_speed==0){
			return;
		}
		if(comment_speed==-20080401){	//reverse
			item->speed = -item->speed;
			return;
		}
		if(comment_speed==20090401){	//3 times speed
			item->speed *= 3;
//			chat_item->vstart = vpos -
//				(int)(width_4sec * 0.25f / fabsf(item->speed));
			chat_item->vpos = vstart +
				(int)(width * 0.25f / fabsf(item->speed));
			chat_item->vend = vstart +
				(int)(width / fabsf(item->speed));
			return;
		}
		item->speed = (float)comment_speed/(float)VPOS_FACTOR;
		chat_item->vpos = vstart +
			(int)(width * 0.25f / fabsf(item->speed));
		chat_item->vend = vstart +
			(int)(width / fabsf(item->speed));
	}
}

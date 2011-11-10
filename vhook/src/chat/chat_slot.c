#include "chat.h"
#include "chat_slot.h"
#include "process_chat.h"
#include "../mydef.h"
#include "../comment/com_surface.h"
#include "../nicodef.h"
#include "../util.h"
#include <SDL/SDL.h>
#include <stdio.h>
#include <string.h>

/*
 * 出力 CHAT_SLOT slot 項目設定
 * 出力 CHAT_ITEM slot->item 領域確保、項目設定
 */
int initChatSlot(FILE* log,CHAT_SLOT* slot,int max_slot,CHAT* chat){
	slot->max_item=max_slot;
	slot->chat = chat;
	slot->item = malloc(sizeof(CHAT_SLOT_ITEM) * max_slot);
	if(slot->item == NULL){
		fputs("failed to malloc for comment slot.\n",log);
		return FALSE;
	}
	int i;
	CHAT_SLOT_ITEM* item;
	for(i=0;i<max_slot;i++){
		item = &slot->item[i];
		item->used = FALSE;
		item->slot = slot;
		item->surf=NULL;
	}
	return TRUE;
}
void closeChatSlot(CHAT_SLOT* slot){
	int i;
	CHAT_SLOT_ITEM* item;
	for(i=0;i<slot->max_item;i++){
		item = &slot->item[i];
		SDL_FreeSurface(item->surf);
	}
	//アイテムを消去。
	free(slot->item);
}

void deleteChatSlot(CHAT_SLOT* slot,CHAT_SLOT_ITEM* item){
	item->chat_item=NULL;
	SDL_FreeSurface(item->surf);
	item->surf = NULL;
	item->used = FALSE;
}

void deleteChatSlotFromIndex(CHAT_SLOT* slot,int index){
	CHAT_SLOT_ITEM* item = &slot->item[index];
	deleteChatSlot(slot,item);
}

/*
 * スロットに追加する。
 */
int addChatSlot(DATA* data,CHAT_SLOT* slot,CHAT_ITEM* item,int video_width,int video_height){
	//もう見せられた。
	item->showed = TRUE;
	if(slot->max_item <= 0){
		return 0;
	}
	SDL_Surface* surf = makeCommentSurface(data,item,video_width,video_height);
	/*開きスロットル検索*/
	int i;
	int cnt = -1;
	int slot_max = slot->max_item;
	for(i=0;i<slot_max;i++){
		if(!slot->item[i].used){
			cnt = i;
			break;
		}
		if(cnt < 0 || slot->item[cnt].chat_item->vend > slot->item[i].chat_item->vend){
			cnt = i;
		}
	}
	CHAT_SLOT_ITEM* slot_item = &slot->item[cnt];
	/*空きが無ければ強制的に作る。*/
	if(slot_item->used){
		deleteChatSlotFromIndex(slot,cnt);
	}
	//この時点で追加
	slot_item->chat_item = item;
	slot_item->surf = surf;
	// 弾幕モードの高さの設定　16:9でue shitaコマンドの場合は上下にはみ出す
	int limit_height = video_height;
	if(!data->original_resize && item->location != CMD_LOC_DEF){
		limit_height = (NICO_HEIGHT * video_width) / data->nico_width_now;
	}
	int y_min = (video_height - limit_height) >> 1;
	int y_max = y_min + limit_height;
	/*ロケーションで分岐*/
	int y;
	if(item->location == CMD_LOC_BOTTOM){
		y = y_max - surf->h;
	}else{
		y = y_min;
	}
	setspeed(data->comment_speed,slot_item,video_width);
	int running;
	do{
		running = FALSE;
		for(i=0;i<slot_max;i++){
			CHAT_SLOT_ITEM* other_slot = &slot->item[i];
			if(!other_slot->used){
				continue;
			}
			const CHAT_ITEM* other_item = other_slot->chat_item;
			int other_y = other_slot->y;
			/*無視する条件*/
			if(other_y + other_slot->surf->h <= y){
				continue;
			}
			if(y + surf->h <= other_y){
				continue;
			}
			if(other_item->location != item->location){
				continue;
			}
			int start = MAX(other_item->vstart,item->vstart);
			int end = MIN(other_item->vend,item->vend);
			int obj_x_t1 = getX(start,slot_item,video_width);
			int obj_x_t2 = getX(end,slot_item,video_width);
			int o_x_t1 = getX(start,other_slot,video_width);
			int o_x_t2 = getX(end,other_slot,video_width);
			//当たり判定
			if ((obj_x_t1 <= o_x_t1 + other_slot->surf->w && o_x_t1 <= obj_x_t1 + surf->w)
					|| (obj_x_t2 <= o_x_t2 + other_slot->surf->w && o_x_t2 <= obj_x_t2 + surf->w)){
				if(item->location == CMD_LOC_BOTTOM){
					y = other_y - surf->h - 1;
				}else{
					y = other_y + other_slot->surf->h + 1;
				}
				running = TRUE;
				break;
			}
		}
	}while(running);
	/*そもそも画面内に無ければ無意味。*/
	if(y < y_min || y+surf->h > y_max){//範囲を超えてるので、ランダムに配置。
		y = y_min + ((rnd() & 0xffff) * (limit_height - surf->h)) / 0xffff;
	}
	//追加
	slot_item->used = TRUE;
	slot_item->y = y;
	return y;
}
/*
 * イテレータをリセットする。
 */
void resetChatSlotIterator(CHAT_SLOT* slot){
	slot->iterator_index = 0;
}
/*
 * イテレータを得る
 */
CHAT_SLOT_ITEM* getChatSlotErased(CHAT_SLOT* slot,int now_vpos){
	int *i = &slot->iterator_index;
	int max_item = slot->max_item;
	CHAT_ITEM* item;
	CHAT_SLOT_ITEM* slot_item;
	for(;*i<max_item;(*i)++){
		slot_item = &slot->item[*i];
		if(!slot_item->used){
			continue;
		}
		item = slot_item->chat_item;
		if(item==NULL)continue;
		if(now_vpos < item->vstart || now_vpos > item->vend){
			return slot_item;
		}
	}
	return NULL;
}

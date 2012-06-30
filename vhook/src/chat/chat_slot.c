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
	if(surf == NULL){
		return 0;
	}
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
	// 弾幕モードの高さの設定　16:9でオリジナルリサイズでない場合は上下にはみ出す
	int limit_height = video_height;
	if(!data->original_resize){
		//comment area height is independent from video height
		limit_height = NICO_HEIGHT * data->width_scale;
	}
	int y_min = (video_height>>1) - (limit_height>>1);
	int y_max = y_min + limit_height +
		limit_height/NICO_HEIGHT;	//コメントの高さは385=384+1 下にはみ出す
	/*ロケーションで分岐*/
	int y;
	if(item->location == CMD_LOC_BOTTOM){
		y = y_max - surf->h;
	}else{
		y = y_min;
	}
	if(data->debug)
	fprintf(data->log,"[chat_slot/add]width %d, height %d, widthscale %.3f, limitheight %d, y_min %d, y_max %d y %d\n",
		video_width, video_height, data->width_scale, limit_height, y_min, y_max, y);
	setspeed(data->comment_speed,slot_item,video_width,data->nico_width_now,data->width_scale);
	if(data->debug){
		fprintf(data->log,"[chat_slot/add speed]comment speed %.2fpix/sec.\n",slot_item->speed*100.0);
		fprintf(data->log,"[chat_slot/add speed2]vpos %d..%d(%d) duration(%d)\n",
			(int)item->vstart,(int)item->vend,(int)item->vpos,(int)(item->vend-item->vstart));
	}
	int running;
	int first_comment=TRUE;
	CHAT_SLOT_ITEM* bang_slot = NULL;
	VPOS_T start = 0;
	VPOS_T end = 0;
	VPOS_T bang_vpos = 0;
	VPOS_T bang_end = 0;
	double bang_xpos[2];
	do{
		running = FALSE;
		first_comment=TRUE;
		for(i=0;i<slot_max;i++){
			CHAT_SLOT_ITEM* other_slot = &slot->item[i];
			if(!other_slot->used){
				continue;
			}
			const CHAT_ITEM* other_item = other_slot->chat_item;
			int other_y = other_slot->y;
			int other_y_next1 = other_y + other_slot->surf->h;
			/*無視する条件*/
			if(other_item->location != item->location){	//別ロケーション
				continue;
			}
			//同一ロケーション発見→第1コメントではない
			first_comment=FALSE;
			//第2コメント以後で画面以上の高さは調べるまでもなく弾幕化
			if(surf->h >= limit_height){
				break;
			}
			//高さの判定
			if(other_y_next1 <= y){
				continue;
			}
			if(y + surf->h <= other_y){
				continue;
			}
			//チェックの開始と終了
			start = MAX(other_item->vstart,item->vstart);
			end = MIN(other_item->vend,item->vend);
			if(start > end){
				continue;
			}
			if(item->location != CMD_LOC_DEF){
				//ue shita は X を調べる必要なく重なる
				if(item->location == CMD_LOC_BOTTOM){
					y = other_y - surf->h;
				}else{
					y = other_y_next1;
				}
				running = TRUE;
				break;
			}

			//vendは最後の数vposは揺らぐので仮に17vposとして計算
			end -= (VPOS_T)17;
			int x_t1 = getX(start,slot_item,video_width,data->width_scale);
			int x_t2 = getX(end,slot_item,video_width,data->width_scale);
			int o_x_t1 = getX(start,other_slot,video_width,data->width_scale);
			int o_x_t2 = getX(end,other_slot,video_width,data->width_scale);
			double dxstart[2] = {(double)x_t1,(double)x_t1+(double)surf->w-1.0};
			double dxend[2] = {(double)x_t2,(double)x_t2+(double)surf->w-1.0};
			double o_dxstart[2] = {(double)o_x_t1,(double)o_x_t1+(double)other_slot->surf->w-1.0};
			double o_dxend[2] = {(double)o_x_t2,(double)o_x_t2+(double)other_slot->surf->w-1.0};
			double dtmp[2];
			int w_off = data->nico_width_now==NICO_WIDTH_WIDE? 64 : 0;
			w_off *= data->width_scale;
			double range[2] = {(double)(w_off + 1),(double)(video_width - 1 - w_off)};
			//当たり判定　追い越し無し前提
			if(data->debug)
				fprintf(data->log,"at %d check (%.0f,%.0f) (%.0f,%.0f)\n",(int)end,dxend[0],dxend[1],o_dxend[0],o_dxend[1]);
			if(set_crossed(dtmp,dxend,o_dxend) && set_crossed(bang_xpos,range,dtmp)){
				y = other_y_next1;
				running = TRUE;
				if(data->debug){
					fprintf(data->log,"--> (%.0f,%.0f)\n",dtmp[0],dtmp[1]);
					bang_vpos = getVposItem(data,slot_item,0,dtmp[slot_item->speed>=0]);
					bang_end = end;
					bang_slot = other_slot;
				}
				break;
			}
			if(data->debug)
				fprintf(data->log,"at %d check (%.0f,%.0f) (%.0f,%.0f)\n",(int)start,dxstart[0],dxstart[1],o_dxstart[0],o_dxstart[1]);
			if(set_crossed(dtmp,dxstart,o_dxstart) && set_crossed(bang_xpos,range,dtmp)){
				y = other_y_next1;
				running = TRUE;
				if(data->debug){
					fprintf(data->log,"--> (%.0f,%.0f)\n",dtmp[0],dtmp[1]);
					bang_vpos = start;
					bang_end = end;
					bang_slot = other_slot;
				}
				break;
			}
		}
	}while(running);
	/*そもそも画面内に無ければ無意味。*/
	if(first_comment){
		//第1コメントは画面外でも弾幕化しない
		fprintf(data->log,"[chat_slot/add first]comment %d %s %s y=%d\n",
			item->no,COM_LOC_NAME[item->location],COM_FONTSIZE_NAME[item->size],y);
	}else
	if(y < y_min || y+surf->h > y_max){	// 範囲を超えてるので、ランダムに配置。
		fprintf(data->log,"[chat_slot/add random]comment %d %s %s y=%d -> random\n",
			item->no,COM_LOC_NAME[item->location],COM_FONTSIZE_NAME[item->size],y);
		y = y_min + ((rnd() & 0xffff) * (limit_height - surf->h)) / 0xffff;
	}
	//追加
	slot_item->used = TRUE;
	slot_item->y = y;
	fprintf(data->log,"[chat_slot/add]comment %d %s %s y=%d\n",
		item->no,COM_LOC_NAME[item->location],COM_FONTSIZE_NAME[item->size],y);
	if(data->debug && bang_slot!=NULL){
		int bang_x1 = bang_xpos[0];
		int bang_x2 = bang_xpos[1];
		CHAT_ITEM* item = bang_slot->chat_item;
		fprintf(data->log,"[chat_slot/add bang]bang_vpos:%d..%d(%d) with %d:%d..%d(vpos:%d %d) x:%d..%d y:%d\n",
			(int)bang_vpos,(int)bang_end,(int)(bang_end-bang_vpos),
			item->no,(int)item->vstart,(int)item->vend,(int)item->vpos,(int)(item->vend - item->vstart),
			bang_x1,bang_x2,bang_slot->y);
	}
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
CHAT_SLOT_ITEM* getChatSlotErased(CHAT_SLOT* slot,VPOS_T now_vpos){
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
		if(now_vpos > item->vend &&
			(item->location!=CMD_LOC_DEF||now_vpos > item->vend+TEXT_AHEAD_SEC)){
		//if(now_vpos < item->vstart || now_vpos > item->vend){
			return slot_item;
		}
	}
	return NULL;
}

//min<=maxならばTRUE
int is_valid_pair(double pair[2]){
	return pair[0]<=pair[1];
}

//後ろ２つのpairの重なりを最初のpairに設定する。
int set_crossed(double ret[2],double pair1[2],double pair2[2]){
	ret[0] = MAX(pair1[0],pair2[0]);
	ret[1] = MIN(pair1[1],pair2[1]);
	return is_valid_pair(ret);
}

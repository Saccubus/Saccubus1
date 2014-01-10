#include "chat.h"
#include "chat_slot.h"
#include "process_chat.h"
#include "../mydef.h"
#include "../comment/com_surface.h"
#include "../nicodef.h"
#include "../util.h"
#include "../comment/surf_util.h"
#include <SDL/SDL.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

/*
 * 出力 CHAT_SLOT slot 項目設定
 * 出力 CHAT_ITEM slot->item 領域確保、項目設定
 */
int initChatSlot(FILE* log,CHAT_SLOT* slot,int max_slot,CHAT* chat){
	slot->max_item=max_slot;
	slot->chat = chat;
	//slot->com_type = chat->com_type;
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
		if(item->used && item->surf!=NULL)
			SDL_FreeSurface(item->surf);
	}
	//アイテムを消去。
	free(slot->item);
}

void deleteChatSlot(CHAT_SLOT_ITEM* slot_item,DATA* data){
	CHAT_ITEM* item = slot_item->chat_item;
	char buf[16];
	if(data->log){
		fprintf(data->log,"[chat_slot/delete]comment %d %s color:%s %s %s  %d - %d(vpos:%d) erased.\n",
			item->no,item->chat->com_type,getColorName(buf,item->color),
			COM_LOC_NAME[item->location],COM_FONTSIZE_NAME[item->size],
			item->vstart,item->vend,item->vpos);
		fflush(data->log);
	}
	if(item->script){
		int bits = item->script & 3;
		if(bits & SCRIPT_OWNER){
			if(item->vpos==data->owner.chat.reverse_vpos){
				data->owner.chat.to_left = 1;
				data->owner.chat.reverse_vpos = 0;
				data->owner.chat.reverse_duration = -1;
			}
		}
		if(bits & SCRIPT_USER){
			if(item->vpos==data->user.chat.reverse_vpos){
				data->user.chat.to_left = 1;
				data->user.chat.reverse_vpos = 0;
				data->user.chat.reverse_duration = -1;
				data->optional.chat.to_left = 1;
				data->optional.chat.reverse_vpos = 0;
				data->optional.chat.reverse_duration = -1;
			}
		}
	}
	slot_item->chat_item=NULL;
	SDL_FreeSurface(slot_item->surf);
	slot_item->surf = NULL;
	slot_item->used = FALSE;
}
/*
void deleteChatSlotFromIndex(CHAT_SLOT* slot,int index){
	CHAT_SLOT_ITEM* item = &slot->item[index];
	deleteChatSlot(slot,item);
}
*/
/*
 * スロットに追加する。
 */
int addChatSlot(DATA* data,CHAT_SLOT* slot,CHAT_ITEM* item,int video_width,int video_height){
	//もう見せられた。
	//item->showed = TRUE;
	if(slot->max_item <= 0){
		return 0;
	}
	//patissierコマンド
	if(item->patissier && item->no <= item->chat->patissier_ignore){
		fprintf(data->log,"[chat_slot/add]comment %d %s patissier vanish.\n",
			item->no,item->chat->com_type);
		return 0;
	}
	//invisibleコマンド
	if(item->invisible){
		fprintf(data->log,"[chat_slot/add]comment %d %s invisible.\n",
			item->no,item->chat->com_type);
		return 0;
	}
	//コメント描画 size color 再設定
	SDL_Surface* surf = makeCommentSurface(data,item,video_width,video_height);
	if((surf == NULL) && ((surf = getErrFont(data)) == NULL)){
			return 0;
	}
	int size = (item->size == CMD_FONT_DEF)? data->defsize : item->size;
	/*開きスロットル検索*/
	int i;
	int cnt = 0;
	int slot_max = slot->max_item;
	int min_vstart = item->vstart;
	for(i=0;i<slot_max;i++){
		if(!slot->item[i].used){
			cnt = i;
			break;
		}
		if(slot->item[i].chat_item->vend < item->vstart){	//連結 (used=falseの筈だが)
			cnt = i;
			break;
		}
		if(slot->item[i].chat_item->vstart < min_vstart) {
			cnt = i;
			min_vstart = slot->item[i].chat_item->vstart;
		}
	}
	CHAT_SLOT_ITEM* slot_item = &slot->item[cnt];	//このスロットに追加
	/*空きが無ければ強制的に作る。*/
	if(slot_item->used){
		deleteChatSlot(slot_item,data);
	}
	//この時点で追加
	slot_item->chat_item = item;
	slot_item->surf = surf;
	//speed vstart vend location 再設定
	double scale = data->width_scale;
	setspeed(data,slot_item,video_width,data->nico_width_now,scale);
	if(data->debug){
		fprintf(data->log,"[chat_slot/add speed]vpos %d..%d(%d) duration(%d)\n",
			item->vstart,item->vend,item->vpos,(item->vend+1-item->vstart));
	}
	//location 取得
	int location = slot_item->slot_location;
	if(location==CMD_LOC_DEF){
		fprintf(data->log,"[chat_slot/add]***BUG*** comment %d vpos %d location %d def %d\n",
			item->no,item->vpos,item->location,data->deflocation);
	}
	/*ロケーションで分岐*/
	int y;
	int y_min = data->y_min;
	int y_max = data->y_max;
	int limit_height = data->limit_height;
	int surf_h = surf->h;
	if(location == CMD_LOC_BOTTOM){
		y = y_max - surf_h;
	}else{
		y = y_min;
	}
	if(data->debug)
	fprintf(data->log,"[chat_slot/add limit]w %d, h %d, scale %.3f, y_min %d, y_max %d y %d\n",
		video_width, video_height, scale, y_min, y_max, y);
	int running;
	int first_comment=TRUE;
	CHAT_SLOT_ITEM* bang_slot = NULL;
	int start = 0;
	int end = 0;
	int bang_vpos = 0;
	int bang_end = 0;
	int y_bottom = y + surf_h;
	double bang_xpos[2] = {0.0, 0.0};
	do{
		y_bottom = y + surf_h;
		running = FALSE;
		first_comment=TRUE;
		CHAT_SLOT_ITEM* other_slot = &slot->item[0];
		CHAT_SLOT_ITEM* slot_max_slot = &slot->item[slot_max];
		for(other_slot = &slot->item[0]; other_slot < slot_max_slot; other_slot++){
			if(!other_slot->used){
				continue;
			}
			const CHAT_ITEM* other_item = other_slot->chat_item;
			int other_y = other_slot->y;
			int other_y_bottom = other_y + other_slot->surf->h;
			/*無視する条件*/
			if(other_slot->slot_location != location){	//別ロケーション
				continue;
			}
			//チェックの開始と終了
			start = MAX(other_item->vstart,item->vstart);
			end = MIN(other_item->vend,item->vend);
			if(location != CMD_LOC_NAKA){
				//vendは最後の数vposは揺らぐ
				end -= 12;	// 12vpos は重なってもいい?
			}else{
				//nakaコメントの場合は5sec→4secに直す
				end -= TEXT_AHEAD_SEC;
				//end -= 3;
			}
			if(start > end){
				continue;
			}
			//同一ロケーション発見→第1コメントではない
			first_comment=FALSE;
			//第2コメント以後で画面以上の高さは調べるまでもなく弾幕化
			if(surf_h >= limit_height){
				y = other_y_bottom;
				break;
			}
			//高さの判定
			if(other_y_bottom <= y){
				continue;
			}
			if(y_bottom <= other_y){
				continue;
			}
			if(location != CMD_LOC_NAKA){
				//ue shita は X を調べる必要なく重なる
				if(location == CMD_LOC_BOTTOM){
					y = other_y - surf_h;
				}else{
					y = other_y_bottom;
				}
				running = TRUE;
				break;
			}

			double x_t1 = getX(start,slot_item,video_width,scale,0);
			double x_t2 = getX(end,slot_item,video_width,scale,0);
			double o_x_t1 = getX(start,other_slot,video_width,scale,0);
			double o_x_t2 = getX(end,other_slot,video_width,scale,0);
			double dxstart[2] = {x_t1, x_t1 + surf->w};
			double dxend[2] = {x_t2, x_t2 + surf->w};
			double o_dxstart[2] = {o_x_t1, o_x_t1 + other_slot->surf->w};
			double o_dxend[2] = {o_x_t2, o_x_t2 + other_slot->surf->w};
			double dtmp[2];
			double range[2] = {-16*scale, (NICO_WIDTH+16)*scale};
			if(data->debug)
				fprintf(data->log,"range (%.0f,%.0f)\n",range[0],range[1]);
			//当たり判定　追い越し無し前提
			if(data->debug)
				fprintf(data->log,"at %d(end) check [%.1f,%.1f] [%.1f,%.1f]y[%d] item %d\n",
					end,dxend[0],dxend[1],o_dxend[0],o_dxend[1],other_y,other_item->no);
			if(set_crossed(dtmp,dxend,o_dxend) && d_width(dtmp)>=scale){
				if(set_crossed(bang_xpos,range,dtmp)){
					y = other_y_bottom;
					running = TRUE;
					if(data->debug){
						fprintf(data->log,"--> x[%.1f,%.1f]y[%d]\n",dtmp[0],dtmp[1],other_y);
						bang_vpos = getVposItem(data,slot_item,0,dtmp[1]);
						bang_end = end;
						bang_slot = other_slot;
					}
					break;
				}
				if(data->debug){
					fprintf(data->log,"out of range [%.1f,%.1f]&[%.1f,%.1f]=[%.1f,%.1f]\n",
						dtmp[0],dtmp[1],range[0],range[1],bang_xpos[0],bang_xpos[1]);
				}
			}
			if(data->debug)
				fprintf(data->log,"at %d(start) check [%.1f,%.1f] [%.1f,%.1f]y[%d] item %d\n",
					start,dxstart[0],dxstart[1],o_dxstart[0],o_dxstart[1],other_y,other_item->no);
			if(set_crossed(dtmp,dxstart,o_dxstart) && d_width(dtmp)>=scale){
				if(set_crossed(bang_xpos,range,dtmp)){
					y = other_y_bottom;
					running = TRUE;
					if(data->debug){
						fprintf(data->log,"--> [%.1f,%.1f]y[%d]\n",dtmp[0],dtmp[1],other_y);
						bang_vpos = start;
						bang_end = end;
						bang_slot = other_slot;
					}
					break;
				}
				if(data->debug){
					fprintf(data->log,"out of range [%.1f,%.1f]&[%.1f,%.1f]=[%.1f,%.1f]\n",
						dtmp[0],dtmp[1],range[0],range[1],bang_xpos[0],bang_xpos[1]);
				}
			}
		}
	}while(running);
	/*そもそも画面内に無ければ無意味。*/
	if(first_comment){
		//第1コメントは画面外でも弾幕化しない
		fprintf(data->log,"[chat_slot/add first]comment %d %s %s y=%d\n",
			item->no,COM_LOC_NAME[location],COM_FONTSIZE_NAME[size],y);
	}else
	if(y < y_min || y + surf_h > y_max){	// 範囲を超えてるので、ランダムに配置。
		fprintf(data->log,"[chat_slot/add random]comment %d %s %s y=%d -> random\n",
			item->no,COM_LOC_NAME[location],COM_FONTSIZE_NAME[size],y);
		y = y_min + ((rnd() & 0xffff) * (limit_height - surf_h)) / 0xffff;
	}
	//追加
	slot_item->used = TRUE;
	slot_item->y = y;
	fprintf(data->log,"[chat_slot/add]comment %d %s %s y=%d\n",
		item->no,COM_LOC_NAME[location],COM_FONTSIZE_NAME[size],y);
	if(data->debug && bang_slot!=NULL){
		int bang_x1 = bang_xpos[0];
		int bang_x2 = bang_xpos[1];
		CHAT_ITEM* item = bang_slot->chat_item;
		fprintf(data->log,"[chat_slot/add bang]bang_vpos:%d..%d(%d) item %d x[%d,%d] y[%d] %d..%d(vpos:%d)\n",
			bang_vpos,bang_end,(bang_end+1-bang_vpos),
			item->no,bang_x1,bang_x2,bang_slot->y,
			item->vstart,item->vend,item->vpos);
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
		if(now_vpos > item->vend){
			return slot_item;
		}
	}
	return NULL;
}
//後ろ２つのpairの重なりを最初のpairに設定する。
int set_crossed(double ret[2],double pair1[2],double pair2[2]){
	ret[0] = MAX(pair1[0],pair2[0]);
	ret[1] = MIN(pair1[1],pair2[1]);
	return (int)(ret[1] - ret[0]) >= 0;
}
//幅
double d_width(double pair[2]){
	return pair[1]-pair[0];
}

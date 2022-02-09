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
	//min vpos
	if(item->vpos < data->min_vpos){
		fprintf(data->log,"[chat_slot/add]comment %d %s earlier than min_vpos %d.\n",
			item->no,item->chat->com_type, data->min_vpos);
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
//		if(slot->item[i].chat_item->vend < item->vstart){	//連結 (used=falseの筈だが)
//			cnt = i;
//			break;
//		}
		if(slot->item[i].chat_item->vstart < min_vstart) {
			cnt = i;
			min_vstart = slot->item[i].chat_item->vstart;
		}
	}
	CHAT_SLOT_ITEM* slot_item = &slot->item[cnt];	//このスロットに追加
	/*空きが無ければ強制的に作る。*/
	if(slot_item->used){
		// 空きが無い場合
		if(data->comment_erase_type==0){	//従来通り先のコメントchatを消す
			deleteChatSlot(slot_item,data);
			//slot_item->used == FALSE;
		}else if(data->comment_erase_type==1){	//新しいコメントchatを無視する
			//コメントsurfを解放
			if(surf!=null) SDL_FreeSurface(surf);
			if(data->debug){
				fprintf(data->log,"[chat_slot/add]comment %d %s ignored. no free slot.\n",
					item->no,item->chat->com_type);
			}
			return 0;
		}
	}
	//この時点で追加
	slot_item->chat_item = item;
	slot_item->surf = surf;
	//speed vstart vend location 再設定
	double scale = data->width_scale;
	setspeed(data,slot_item,video_width,data->nico_width_now,scale);
	if(data->debug){
		fprintf(data->log,"[chat_slot/add speed]comment=%d vpos %d..%d(%d) duration(%d)\n",
			item->no,item->vstart,item->vend,item->vpos,(item->vend+1-item->vstart));
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
		if(data->html5comment)
			y--;
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
	double range[2] = {-16*scale, (NICO_WIDTH+16)*scale};
	int fixmode = data->fixmode;
	if(fixmode){
		double range_w = range[1] - range[0];
		if(range_w < video_width){
			double dif_w = video_width - range_w / 2.0;
			range[0] -= dif_w;
			range[1] += dif_w;
		}
	}
	if(data->debug)
		fprintf(data->log,"range (%.0f,%.0f)\n",range[0],range[1]);
	// コメントオフ
	int off_h = 0;		//オフの高さ
	int off_min = y_min;
	int off_max = y_max;
	int off_kind = 0;
	int off_naka = FALSE;
	int off_sign = 0;
	int comment_off = FALSE;
	if(data->comment_off && item->chat->cid!=CID_OWNER && !item->itemfork){
		off_naka = data->comment_off_naka;
		if(!off_naka || (off_naka && location==CMD_LOC_NAKA)){
			comment_off = TRUE;
			if(data->debug)
				fprintf(data->log,"[chat_slot/add]comment_off enable\n");
			off_kind = data->comment_off_kind;
			off_sign = data->comment_off_sign;
			off_h = data->comment_off_y;
			// pixel値に変換
			if(off_kind==4){
				// %指定
				off_h = (int)(0.01 * off_h * video_height);
			} else if(off_kind != 0){
				// 文字サイズ指定
				off_h = (int)(scale * off_h * FONT_PIXEL_SIZE[off_kind]);
			}
			// 方向
			if(off_sign == 0){
				// 中央からは未実装
				comment_off = FALSE;
			}else if(off_sign > 0){
				//上からマスク
				off_min += off_h;
				if(location==CMD_LOC_NAKA)
					y = y_min = off_min;
				// !off_nakaの時はueコメントは弾幕化→消去になる
			}else{
				//下からマスク
				off_max -= off_h;
				if(location==CMD_LOC_NAKA)
					y_max = off_max;
				// !off_nakaの時はshitaコメントは弾幕化→消去になる
			}
			if(data->debug)
				fprintf(data->log,"[chat_slot/add]comment_off %s, min=%d, max=%d\n"
					,(comment_off?"enable":"disable"),off_min,off_max);
		}
	}
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
			if(!fixmode){
				start = MAX(other_item->vstart,item->vstart);
			}else{
				start = MAX(other_item->vappear,item->vappear);
			}
			end = MIN(other_item->vend,item->vend);
			if(location != CMD_LOC_NAKA){
				//vendは最後の数vposは揺らぐ
				end -= 12;	// 12vpos は重なってもいい?
			}else{
				//nakaコメントの場合は5sec→4secに直す
				end -= data->ahead_vpos;
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
			int surf_w = surf->w;
			double dxstart[2] = {x_t1, x_t1 + surf_w};
			double dxend[2] = {x_t2, x_t2 + surf_w};
			int other_w = other_slot->surf->w;
			double o_dxstart[2] = {o_x_t1, o_x_t1 + other_w};
			double o_dxend[2] = {o_x_t2, o_x_t2 + other_w};
			double dtmp[2];
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
			if(fixmode){
				//当たり判定　追い越しチェック有り
				double range_xpos;
				int check_vpos;
				int other_xpos;
				if(surf_w >= other_w){
					// itemが後でスピードが速い場合
					// 動画の左端に到達する時刻
					range_xpos = range[0];
				}else{
					// itemが後でスピードが遅い場合
					// 動画の右端に表示される時刻
					range_xpos = range[1];
				}
				check_vpos = getVposItem(data,slot_item,range_xpos,0);
				other_xpos = (int)lround(getX(check_vpos,other_slot,video_width,scale,0));
				if(data->debug){
					fprintf(data->log,"--> going to check passing vpos=%d, other_x=[%d,%d]\n",
						check_vpos, other_xpos, other_xpos + other_w);
				}
				if(other_xpos + other_w > range_xpos){
					y = other_y_bottom;
					running = TRUE;
					if(data->debug){
						fprintf(data->log,"--> passing, vpos=%d, y[%d]\n",check_vpos,other_y);
						bang_vpos = check_vpos;
						bang_end = check_vpos;
						bang_slot = other_slot;
					}
					break;
				}
			}
		}
	}while(running);
	y_bottom = y+surf_h;
	if(data->comment_lf_control > 1 && data->html5comment==0){
		// lf行間補正 ver2
		// lf_control = 0:なし 　1: rev1.67.1.2 の時はしない
		// lf制御 コメント高さの補正はrender時に済
		int h = (int)((data->comment_linefeed_ratio - 1.0) * surf_h);
		int gap[4] = {3, 5, 2, 3};
		int gaph = (int)(scale * gap[size] + 0.5);
		if(data->debug)
			fprintf(data->log,"[chat_slot/add]lf_ratio=%5.2f, h=%d, gap=%d\n"
				,data->comment_linefeed_ratio, h, gaph);
		if(h < 0 && data->shadow_kind!=0
			&& y > y_min && y > off_min && y_bottom < y_max && y_bottom < off_max){
			// 行間を小さくする時は既定の行間を補正する
			if(location==CMD_LOC_BOTTOM){
				y_bottom += gaph;
				if(y_bottom > y_max) y_bottom = y_max;
				if(comment_off && y_bottom > off_max) y_bottom = off_max;
				y = y_bottom - surf_h;
			}else{
				y -= gaph;
				if(y < y_min) y = y_min;
				if(comment_off && y < off_min) y = off_min;
				y_bottom = y + surf_h;
			}
			fprintf(data->log,"[chat_slot/add]lf-control-fixed y=%d, line_feed %d\n", y, -gaph);
		}
	}
	/*そもそも画面内に無ければ無意味。*/
	if(comment_off){
		if(y < off_min || y_bottom > off_max){
			// コメントオフ
			//コメントsurfを解放
			if(surf!=null) SDL_FreeSurface(surf);
			// 既に追加しているので消去
			slot_item->chat_item = NULL;
			slot_item->surf = NULL;
			slot_item->used = FALSE;
			fprintf(data->log,"[chat_slot/erase]comment %d %s comment_off.\n",
					item->no,item->chat->com_type);
			return 0;
		}
	}
	if(first_comment){
		//第1コメントは画面外でも弾幕化しない
		fprintf(data->log,"[chat_slot/add first]comment %d %s %s y=%d\n",
			item->no,COM_LOC_NAME[location],COM_FONTSIZE_NAME[size],y);
	}else
	if(y < y_min || y_bottom > y_max){	// 範囲を超えてるので、ランダムに配置。
		fprintf(data->log,"[chat_slot/add random]comment %d %s %s y=%d -> random\n",
			item->no,COM_LOC_NAME[location],COM_FONTSIZE_NAME[size],y);
		//big16は固定
		if(item->nb_line==16 && size==CMD_FONT_BIG){
			y = y_min;
		}
		else
		//naka弾幕は固定
		if(surf_h>limit_height && location==CMD_LOC_NAKA){
			if(data->html5comment){
				//html5 naka弾幕は上下中央配置
				y = (y_min + y_max - surf_h)>>1;
			}
			else{
				//flash naka弾幕は上0固定
				y = y_min;
			}
		}
		else
		//DR弾幕も固定
		if(item->double_resized){
			y = location==CMD_LOC_BOTTOM? (y_max - surf_h) : y_min;
		}
		else
		//big16でもDRでもnakaでもない
		//HTML5の時は高さが画面より大きいコメントは固定
		if(data->html5comment && surf_h>=limit_height){
			y = location==CMD_LOC_BOTTOM? (y_max - surf_h) : y_min;
		}
		else{
			y = y_min + ((rnd() & 0xffff) * (limit_height - surf_h)) / 0xffff;
		}
	}
	//追加
	slot_item->used = TRUE;
	if(data->html5comment){
		// html5の黄枠は重なることが多い
		if(location==CMD_LOC_BOTTOM){
			if(y < y_max) y++;
		}else{
			if(y > y_min) y--;
		}

	}
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
	return 1;
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
CHAT_SLOT_ITEM* getChatSlotErased(CHAT_SLOT* slot,int now_vpos,int min_vpos){
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
		if(min_vpos > item->vpos)
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

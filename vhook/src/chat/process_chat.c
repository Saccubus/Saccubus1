#include <SDL/SDL.h>
#include <math.h>
#include "chat.h"
#include "chat_slot.h"
#include "process_chat.h"
#include "chat_pool.h"
#include "../main.h"
#include "../mydef.h"
#include "../comment/surf_util.h"

//このソース内でしか使わないメソッド
void drawComment(DATA* data,SDL_Surface* surf,CHAT_SLOT* slot,int now_vpos,int x,int y);

/**
 * コメントを描画する。
 */
int process_chat(DATA* data,CDATA* cdata,SDL_Surface* surf,const int now_vpos){
	CHAT* chat;
	CHAT_SLOT* slot;
	CHAT_ITEM* chat_item;
	CHAT_SLOT_ITEM* slot_item;
	int ahead_vpos = data->ahead_vpos;
	FILE* log = data->log;
	char buf[16];
	if (cdata->enable_comment){
		/*見せないものを削除 */
		slot = &cdata->slot;
		resetChatSlotIterator(slot);
		while((slot_item = getChatSlotErased(slot,now_vpos,data->min_vpos)) != NULL){
			deleteChatSlot(slot_item,data);
		}
		/*見せるものをセット*/
		chat = &cdata->chat;
		resetChatIterator(chat);
		//プールは以前からのが残っているのでリセットしてはいけない
		//resetPoolIterator(chat->pool);
		// now_vposを1秒先読みしてvstartからvendまでのコメントをプール
		while((chat_item = getChatShowed(chat,now_vpos,ahead_vpos)) != NULL){
			// debug
			fprintf(log,"[process-chat/process]getChatShowed(chat,vpos=%d) comment %d.\n",now_vpos,chat_item->no);
			addChatPool(data,chat->pool,chat_item);
		}
		// プールをvstart,noでソートし取り出す
		while((chat_item = getChatPooled(data,chat->pool,now_vpos)) != NULL){
			if (addChatSlot(data,slot,chat_item,data->vout_width,data->vout_height) == 0)
			{//表示しなかった
				char* item_kind = "IGNORE";
				if(chat_item->patissier)
					item_kind = "patissier";
				else if(chat_item->invisible)
					item_kind = "invisible";
				fprintf(log,"[process-chat/process]comment %d %s vpos:%d chat(%s):%d - %d(vpos:%d) ignored.\n",
					chat_item->no,chat->com_type,now_vpos,item_kind,
					chat_item->vstart,chat_item->vend,chat_item->vpos);
			}else{
				fprintf(log,"[process-chat/process]comment %d %s vpos:%d color:%s %s %s  %d - %d(vpos:%d) added.\n",
					chat_item->no,chat->com_type,now_vpos,getColorName(buf,chat_item->color),
					COM_LOC_NAME[chat_item->location],COM_FONTSIZE_NAME[chat_item->size],
					chat_item->vstart,chat_item->vend,chat_item->vpos);
			}
		}
/* debug
		fprintf(log,"[process-chat/process]drawComment(data,surf(%d,%d),slot,vpos%d,x%d,y%d) aspect%d scale%.1f w%d h%d\n",
			surf->w,surf->h,now_vpos,data->vout_x,data->vout_y,
			data->aspect_mode,data->width_scale,data->vout_width,data->vout_height);
*/
		if(now_vpos >= data->min_vpos)
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

void drawCommentA(DATA* data,SDL_Surface* surf,CHAT_SLOT* slot,int now_vpos, int x, int y, int loc){
	int i;
	SDL_Rect rect;
	int max_item = slot->max_item;
	CHAT_SLOT_ITEM* item;
#ifdef VHOOKDEBUG
		fprintf(data->log,"[drawcomment/debug1]args:surf %p (x y):(%d %d) vpos:%d slot:%p->%p->%8p\n",
				surf, x, y,now_vpos,slot,slot->chat, slot->chat->item);
#endif
	for(i=0;i<max_item;i++){
		item = &slot->item[i];
		if(item->used && (loc==CMD_LOC_ALL || loc==item->slot_location)){
#ifdef VHOOKDEBUG
			CHAT_ITEM* chatitem = item->chat_item;
			int no = chatitem->no;
			fprintf(data->log,"[drawcomment/debug2]item(%d):no=%d, y=%d\n",
					i, no, item->y);
#endif
/*
			if(now_vpos > item->chat_item->vend){
				deleteChatSlot(item,data);
				continue;
			}
*/
			int normal_x = lround(getX1(data->log,now_vpos,item,data->vout_width,data->width_scale,data->aspect_mode));
			if(slot->chat->to_left < 0){
#ifdef VHOOKDEBUG
//				CHAT_ITEM* citem = item->chat_item;
//				fprintf(data->log,"[drawcomment/script GYAKU]now:%d appear:%d vanish:%d vpos:%d start:%d end:%d duration:%d\n",
//					now_vpos,item->vappear, item->vvanish,citem->vpos,citem->vstart,citem->vend,citem->duration);
//				fprintf(data->log,"[drawcomment/script GYAKU]now:%d reverse_vpos:%d reverse_duration:%d \n",
//					now_vpos,slot->chat->reverse_vpos, slot->chat->reverse_duration);
#endif
				if(slot->chat->reverse_vpos <= now_vpos && slot->chat->reverse_vpos + slot->chat->reverse_duration > now_vpos){
					normal_x = data->vout_width - (normal_x + item->surf->w);
				}
			}
			rect.x = normal_x;
			rect.y = item->y;
#ifdef VHOOKDEBUG
			fprintf(data->log,"[drawcomment/debug3]comment=%d, SDL_BlitSurface(item->surf:%p,NULL:%p,surf:%p,rect:(x=%d,y=%d))\n",
					no,item->surf,NULL,surf,rect.x,rect.y);
#endif
			SDL_BlitSurface(item->surf,NULL,surf,&rect);
		}
	}
}

void drawComment(DATA* data,SDL_Surface* surf,CHAT_SLOT* slot,int now_vpos, int x, int y){
	if(data->layerctrl==1){
		// ue shita を後の優先描画
		// naka -> shita -> ueの順番に描画する
		// この時点ではloc_def は無い
		drawCommentA(data, surf, slot, now_vpos, x, y, CMD_LOC_NAKA);
		drawCommentA(data, surf, slot, now_vpos, x, y, CMD_LOC_BOTTOM);
		drawCommentA(data, surf, slot, now_vpos, x, y, CMD_LOC_TOP);
	}else{
		// slot順 従来通り
		drawCommentA(data, surf, slot, now_vpos, x, y, CMD_LOC_ALL);
	}
}

/*
 * 位置を求める
 */
double getX1(FILE* log,int vpos,CHAT_SLOT_ITEM* item,int video_width,double scale,int aspect_mode){
	if(item->slot_location != CMD_LOC_NAKA){
		return (double)((video_width >> 1) - (item->surf->w >> 1));
	}
	//CMD_LOC_NAKA
/*
 * naka 位置を求める
 */
	// 現時刻now_vposのコメントvstartからの相対値
	// vpos - item->chat_item->vstart;
	double progress = item->speed * (vpos-item->chat_item->vstart);
	double xstart;
	double xpos;
		//-16-text    if 512 -> 48-text if 640
		//528      if 512 -> 592 if 640
	if(aspect_mode){
		xstart = scale * (NICO_WIDTH + 16 + 64);
	}else{
		xstart = scale * (NICO_WIDTH + 16);
	}
	xpos = -progress + xstart;
	return xpos;
}
double getX(int vpos,CHAT_SLOT_ITEM* item,int video_width,double scale,int aspect_mode){
	return getX1(NULL,vpos,item,video_width,scale,aspect_mode);
}

int getVposItem(DATA* data,CHAT_SLOT_ITEM* item,int n_xpos,double s_tpos){
	// xpos = n_xpos * data->scale + s_textpos
	// getX(vpos) = (NICO_WIDTH + 16) * scale - (vpos - vstart) * speed;
	// if getX(vpos)==xpos -> vpos == ((NICO_WIDTH + 16) * scale - xpos)/speed + vstart
	//  == ((NICO_WIDTH + 16 - ns_xpos) * scale - s_textpos) / speed + vstart;
	double xstart = NICO_WIDTH + 16;
	if (data->aspect_mode){
		xstart += 64.0;
	}
	return  item->chat_item->vstart +
			lround((xstart - n_xpos) * data->width_scale - s_tpos) / ABS(item->speed);
}

/**
 *
 */
void setspeed(DATA* data,CHAT_SLOT_ITEM* slot_item,int video_width,int nico_width,double scale){
	int comment_speed = data->comment_speed;
	int comment_duration = (int)(data->comment_duration * VPOS_FACTOR);
	CHAT_ITEM* item = slot_item->chat_item;
	int vpos = item->vpos;
	int location = item->location;
	int ahead_vpos = data->ahead_vpos;
	/*
	 * default lcation 変更
	 */
	if(location == CMD_LOC_DEF){
		location = data->deflocation;
	}
	slot_item->slot_location = location;
	int duration = item->duration;
	//slot_item->slot_duration = duration
	if(location == CMD_LOC_TOP||location==CMD_LOC_BOTTOM){
		item->vstart = vpos;
		item->vend = vpos + duration - 1;
		slot_item->speed = 0.0f;
		item->vappear = vpos;
	}else{
		if(comment_duration > 0)
			duration = comment_duration;
		item->vstart = vpos - ahead_vpos;
		//if(item->script==SCRIPT_GYAKU||item->script==SCRIPT_DEFAULT){
		//	item->vstart = vpos;
		//}
		item->vend = vpos + duration - 1 + ahead_vpos;
		item->vappear = item->vstart - ahead_vpos;
		int text_width = slot_item->surf->w;
		double width = scale * (NICO_WIDTH + 32) + text_width;
		double speed = width / (double)(duration + ahead_vpos);
		//speed *= 1.006;	//特別補正→ edge of video will be reached at vend-3
		if(comment_speed==0){
			//speed = speed * 1.02 - 0.04;	//特別補正
			slot_item->speed = (float)speed;
		}
		else if(comment_speed==-20080401){	//reverse
			slot_item->speed = (float)speed;
		}
		else if(comment_speed==20090401){	//3 times speed
			slot_item->speed = (float)(speed * 3.0);
			item->vend = item->vstart + lround((duration + ahead_vpos) / 3.0);
		}
		else {
			speed  = (double)comment_speed/(double)VPOS_FACTOR;
			slot_item->speed = (float)speed;
			if(speed < 0.0){
				speed = -speed;
			}
			item->vend = item->vstart + lround(width / speed);
			item->vappear = item->vstart - ahead_vpos;
		}
		if(data->debug){
			fprintf(data->log,"[process_chat/set_speed]comment %d, vappear %d, speed %.2fpix/sec.\n",
				item->no,item->vappear,slot_item->speed*100.0);
		}
	}
}

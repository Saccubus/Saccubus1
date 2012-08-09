#include <SDL/SDL.h>
#include <math.h>
#include "chat.h"
#include "chat_slot.h"
#include "process_chat.h"
#include "chat_pool.h"
#include "../main.h"
#include "../mydef.h"

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
	FILE* log = data->log;
	if (cdata->enable_comment){
		/*見せないものを削除 */
		slot = &cdata->slot;
		resetChatSlotIterator(slot);
		while((slot_item = getChatSlotErased(slot,now_vpos)) != NULL){
			deleteChatSlot(slot_item,log);
		}
		/*見せるものをセット*/
		chat = &cdata->chat;
		resetChatIterator(chat);
		resetPoolIterator(chat->pool);
			// vposを超えたものをプール
		while((chat_item = getChatShowed(chat,now_vpos)) != NULL){
			addChatPool(data,chat->pool,chat_item);
		}
			// プールをvposでソートし取り出す
		while((chat_item = getChatPooled(data,chat->pool)) != NULL){
			addChatSlot(data,slot,chat_item,data->vout_width,data->vout_height);
			fprintf(log,"[process-chat/process]comment %d vpos:%d %s color:%d:#%06x %5s %6s  %d - %d(vpos:%d) added.\n",
				chat_item->no,now_vpos,chat->com_type,chat_item->color,convSDLcolor(chat_item->color24),
				COM_LOC_NAME[chat_item->location],COM_FONTSIZE_NAME[chat_item->size],
				chat_item->vstart,chat_item->vend,chat_item->vpos);
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
			if(now_vpos < item->chat_item->vappear){
				continue;
			}
			if(now_vpos > item->chat_item->vvanish){
				deleteChatSlot(item,data->log);
				continue;
			}
			rect.x = lround(getX(now_vpos,item,data->vout_width,data->width_scale,data->aspect_mode) + x);
			rect.y = item->y + y;
			SDL_BlitSurface(item->surf,NULL,surf,&rect);
		}
	}
}

/*
 * 位置を求める
 */
/*
int getX_org(int now_vpos,const CHAT_SLOT_ITEM* item,int video_width,int nico_width,double scale){
	int text_width = item->surf->w;
	int width = video_width;
	if(item->chat_item->location != CMD_LOC_DEF){
		return (width - text_width) >>1;
	}else{
		int vstart = item->chat_item->vpos - TEXT_AHEAD_SEC;
		//if 4:3, vstart is chat_item->vstart;
		//but 16:9, vstart is later than chat_item->vstart.
		double xstart = (NICO_WIDTH + 44/2) * scale;
		if(nico_width==NICO_WIDTH_WIDE){
			xstart += 64.0 * scale;
		}
		double tmp = now_vpos - vstart;
		if(item->speed < 0.0f){
			return (xstart - tmp * item->speed
				- (video_width + text_width));
		}
		return (xstart - tmp * item->speed);
	}
	return -1;
}
*/

/*
 * naka 位置を求める
 */
double getXnaka(int vpos,CHAT_SLOT_ITEM* item,int aspect_mode,double scale){
	//int text_width = item->surf->w;
	//int vstart = item->chat_item->vstart;
	double progress = (vpos - item->chat_item->vstart) * item->speed;
	double xpos;
	if(item->speed < 0.0f){
		xpos = -progress - 16 * scale - item->surf->w;	//-16-text_width at vstart if 512
	}else {
		xpos = (NICO_WIDTH + 15) * scale - progress;	//527 at vstart if 512
	}
	if(aspect_mode){
		xpos += 64 * scale;
		//-16 -> 48 if 640
		//527 -> 591 if 640
	}
	return xpos;
}

/*
 * 位置を求める
 */
double getX(int vpos,CHAT_SLOT_ITEM* item,int video_width,double scale,int aspect_mode){
	double text_width = item->surf->w;
	if(item->chat_item->location != CMD_LOC_DEF){
		return ((double)video_width - text_width) / 2.0;
	}
	//CMD_LOC_DEF (naka)
	return getXnaka(vpos,item,aspect_mode,scale);
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
void setspeed(int comment_speed,CHAT_SLOT_ITEM* item,int video_width,int nico_width,double scale){
	CHAT_ITEM* chat_item = item->chat_item;
	int duration = chat_item->duration;
	if(chat_item->location!=CMD_LOC_DEF){
		item->speed = 0.0f;
		if(duration!=0){
			chat_item->vend = chat_item->vstart + duration - 1;
		}
	}else{
		int vpos = chat_item->vpos;
		int text_width = item->surf->w;
		double width = scale * (NICO_WIDTH + 32) + text_width;
		//					//video_width + scale * 36 + text_width;
		if(duration==0){
			duration = TEXT_SHOW_SEC_S;
		}
		// set vstart, since vstart was set to vpos - 1.5 sec
		chat_item->vstart = vpos - TEXT_AHEAD_SEC;
		// set vend, since vend was set to vpos + 3.5 sec
		chat_item->vend = vpos + (duration - 1);
		double speed = width / (double)(duration + TEXT_AHEAD_SEC);
		speed *= 1.006;	//特別補正
		double time_add = scale * 64 / speed;
		// comment appaers at x < 672 when wide mode(640x360)
		chat_item->vappear = chat_item->vstart - lround(time_add);
		// comment vanishes at x < 0 when wide mode(640x360)
		chat_item->vvanish = chat_item->vend + lround(time_add);
		if(comment_speed==0){
			item->speed = (float)speed;
			return;
		}
		if(comment_speed==-20080401){	//reverse
			item->speed = (float)-speed;
			return;
		}
		if(comment_speed==20090401){	//3 times speed
			item->speed = (float)(speed * 3.0);
			chat_item->vstart = vpos - lround((double)TEXT_AHEAD_SEC / 3.0);
			chat_item->vend = chat_item->vstart + lround((duration + TEXT_AHEAD_SEC) / 3.0) - 1;
			chat_item->vappear = chat_item->vstart - lround(time_add / 3.0);
			chat_item->vvanish = chat_item->vend + lround(time_add / 3.0);
			return;
		}
		speed  = (double)comment_speed/(double)VPOS_FACTOR;
		item->speed = (float)speed;
		if(speed < 0.0)
			speed = -speed;
		//chat_item->vstart = (vpos - TEXT_AHEAD_SEC);
		chat_item->vend = chat_item->vstart + lround(width / speed) - 1;
		time_add = lround(scale * 64 / speed);
		chat_item->vappear = chat_item->vstart - (int)time_add;
		chat_item->vvanish = chat_item->vend + (int)time_add;
		return;
	}
}

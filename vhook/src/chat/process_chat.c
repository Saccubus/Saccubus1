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
		/*見せないものを削除 */
		slot = &cdata->slot;
		resetChatSlotIterator(slot);
		while((slot_item = getChatSlotErased(slot,now_vpos)) != NULL){
			chat_item = slot_item->chat_item;
			fprintf(log,"[process-chat/process]comment %d vpos:%d %s color:%d:#%06x %5s %6s  %d - %d(vpos:%d) erased.\n",
				chat_item->no,now_vpos,com_type,chat_item->color,convSDLcolor(chat_item->color24),
				COM_LOC_NAME[chat_item->location],COM_FONTSIZE_NAME[chat_item->size],
				(int)chat_item->vstart,(int)chat_item->vend,(int)(chat_item->vpos));
			fflush(log);
			deleteChatSlot(slot,slot_item);
		}
		/*見せるものをセット*/
		chat = &cdata->chat;
		resetChatIterator(chat);
		while((chat_item = getChatShowed(chat,now_vpos)) != NULL){
			addChatSlot(data,slot,chat_item,data->vout_width,data->vout_height);
			fprintf(log,"[process-chat/process]comment %d vpos:%d %s color:%d:#%06x %5s %6s  %d - %d(vpos:%d) added.\n",
				chat_item->no,now_vpos,com_type,chat_item->color,convSDLcolor(chat_item->color24),
				COM_LOC_NAME[chat_item->location],COM_FONTSIZE_NAME[chat_item->size],
				(int)chat_item->vstart,(int)chat_item->vend,(int)chat_item->vpos);
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
			rect.x = getX(now_vpos,item,data->vout_width,data->width_scale) + x;
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
			return (int)(xstart - tmp * item->speed
				- (video_width + text_width));
		}
		return (int)(xstart - tmp * item->speed);
	}
	return -1;
}
*/

/*
 * naka 位置を求める
 */
int getXnaka(VPOS_T vpos,CHAT_SLOT_ITEM* item,int video_width,double scale){
	//int text_width = item->surf->w;
	VPOS_T vstart = item->chat_item->vpos - TEXT_AHEAD_SEC;
	int progress = (vpos - vstart) * item->speed;
	int xpos;
	if(item->speed < 0.0f){
		xpos = (int)(-progress - 16 * scale);	//-16 if 512
	}else {
		xpos = (int)((NICO_WIDTH + 16) * scale - progress);	//528 if 512
	}
	if(video_width > NICO_WIDTH * scale){
		xpos += 64 * scale;
		//-16 -> 48 if 640
		//528 -> 692 if 640
	}
	return xpos;
}

/*
 * 位置を求める
 */
int getX(VPOS_T vpos,CHAT_SLOT_ITEM* item,int video_width,double scale){
	int text_width = item->surf->w;
	if(item->chat_item->location != CMD_LOC_DEF){
		return (video_width >> 1) - (text_width >> 1);
	}
	//CMD_LOC_DEF (naka)
	return getXnaka(vpos,item,video_width,scale);
}

VPOS_T getVposItem(DATA* data,CHAT_SLOT_ITEM* item,int n_xpos,int s_tpos){
	// xpos = n_xpos * data->scale + s_textpos
	// getX(vpos) = (NICO_WIDTH + 16) * scale - (vpos - vstart) * speed;
	// if getX(vpos)==xpos -> vpos == ((NICO_WIDTH + 16) * scale - xpos)/speed + vstart
	//  == ((NICO_WIDTH + 16 - ns_xpos) * scale - s_textpos) / speed + vstart;
	int xstart = NICO_WIDTH + 16;
	if (data->nico_width_now==NICO_WIDTH_WIDE){
		xstart += 64;
	}
	return ((xstart - n_xpos) * data->width_scale - s_tpos) / fabs((double)item->speed) +
			(item->chat_item->vpos - TEXT_AHEAD_SEC);
}

/**
 *
 */
void setspeed(int comment_speed,CHAT_SLOT_ITEM* item,int video_width,int nico_width,double scale){
	CHAT_ITEM* chat_item = item->chat_item;
	if(chat_item->location!=CMD_LOC_DEF){
		item->speed = 0.0f;
	}else{
		VPOS_T vpos = chat_item->vpos;
		int text_width = item->surf->w;
		double width = scale * (NICO_WIDTH + 38) + text_width;
		//					//video_width + scale * 44 + text_width;
		double speed = width / TEXT_SHOW_SEC;
		item->speed = (float)speed;
		chat_item->vstart = (VPOS_T)(vpos - (width / speed) * 0.25);
		chat_item->vend = chat_item->vstart + (VPOS_T)(TEXT_SHOW_SEC - 1);
		if(nico_width==NICO_WIDTH_WIDE){
			width += (NICO_WIDTH_WIDE - NICO_WIDTH) * scale;
		}
		if(comment_speed==0){
			return;
		}
		if(comment_speed==-20080401){	//reverse
			item->speed = -speed;
			return;
		}
		if(comment_speed==20090401){	//3 times speed
			speed *= 3;
		} else {
			speed = (double)comment_speed/(double)VPOS_FACTOR;
		}
		item->speed = (float)speed;
		chat_item->vstart = (VPOS_T)(vpos - (width / speed) * 0.25);
		chat_item->vend = (VPOS_T)(vpos + (width / speed) * 0.75);
	}
}

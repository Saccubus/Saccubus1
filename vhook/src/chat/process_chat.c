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
			deleteChatSlot(slot_item,data);
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
			if(now_vpos < item->vappear){
				continue;
			}
			if(now_vpos > item->vvanish){
				deleteChatSlot(item,data);
				continue;
			}
			int normal_x = lround(getX(now_vpos,item,data->vout_width,data->width_scale,data->aspect_mode));
			if(slot->chat->to_left < 0)
				normal_x = data->vout_width - (normal_x + item->surf->w);
			rect.x = normal_x + x;
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
	// this meens getX=-width/(vend-vstart)*(vpos-vstart)+xstart(=NICO_WIDTH+16)+64_wide
	//   vpos=vstart -> getX=width-16-text_width+64_wide=NICO_WIDTH+16+64_wide
	//   vpos=vend   -> getX=-16-text_width+64_wide
	double progress = item->speed * (vpos-item->chat_item->vstart);
	double xstart = scale * (NICO_WIDTH+16);	//=512*(33/32)=640*(41/40)
	double xpos = -progress + xstart + (aspect_mode? scale*64 : 0);	//528 when vpos=vstart if 512
		//64=512*(1/8)=640*(1/10)
		//-16-text if 512 -> 48-text if 640		-(1/32)-text : (3/40)-text
		//528      if 512 -> 592 if 640			(33/32)      : (37/40)
	return xpos;
}

/*
 * 位置を求める
 */
double getX(int vpos,CHAT_SLOT_ITEM* item,int video_width,double scale,int aspect_mode){
	//double text_width = item->surf->w;
	if(item->slot_location == CMD_LOC_TOP||item->slot_location==CMD_LOC_BOTTOM){
		return (double)((video_width >> 1) - (item->surf->w >> 1));
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
void setspeed(DATA* data,CHAT_SLOT_ITEM* slot_item,int video_width,int nico_width,double scale){
	int comment_speed = data->comment_speed;
	CHAT_ITEM* item = slot_item->chat_item;
	int vpos = item->vpos;
	int location = item->location;
	int itime_add = 0;
	/*
	 * default lcation 変更
	 */
	if(location == CMD_LOC_DEF){
		location = data->deflocation;
	}
	slot_item->slot_location = location;
	int duration = item->duration;
	if(duration==0){
		duration = TEXT_SHOW_SEC_S;
	}
	if(location == CMD_LOC_TOP||location==CMD_LOC_BOTTOM){
		item->vstart = vpos;
		item->vend = vpos + duration - 1;
		slot_item->speed = 0.0f;
		slot_item->vappear = item->vstart - itime_add;
		slot_item->vvanish = item->vend + itime_add;
	}else{
		item->vstart = vpos - TEXT_AHEAD_SEC;
		if(item->script!=0){
			item->vstart = vpos;
		}
		item->vend = vpos + duration - 1;
		int text_width = slot_item->surf->w;
		double width = scale * (NICO_WIDTH + 32) + text_width;
		//					//video_width + scale * 36 + text_width;
		double speed = width / (double)(item->vend - item->vstart);
		// this meens getX=-width/(vend-vstart)*(vpos-vstart)+xstart(=width-16-text_width)+64_wide
		//   vpos=vstart -> getX=width-16-text_width+64_wide=NICO_WIDTH+16+64_wide
		//   vpos=vend   -> getX=-16-text_width+64_wide
		//speed *= 1.006;	//特別補正→ edge of video will be reached at vend-3
		itime_add = MAX(lround(scale * 64 / speed),TEXT_AHEAD_SEC);
		if(comment_speed==0){
			slot_item->speed = (float)speed;
		}
		else if(comment_speed==-20080401){	//reverse
			slot_item->speed = (float)speed;
			//slot_item->slot->chat->to_left = -1;	//this should be in initChat
		}
		else if(comment_speed==20090401){	//3 times speed
			slot_item->speed = (float)(speed * 3.0);
			//item->vstart = vpos - lround((double)TEXT_AHEAD_SEC / 3.0);
			item->vend = item->vstart + lround((duration + TEXT_AHEAD_SEC) / 3.0);
			itime_add = TEXT_AHEAD_SEC;
		}
		else {
			speed  = (double)comment_speed/(double)VPOS_FACTOR;
			slot_item->speed = (float)speed;
			if(speed < 0.0){
				speed = -speed;
				//slot_item->slot->chat->to_left = -1;	//this should be in initChat
			}
			//item->vstart = (vpos - TEXT_AHEAD_SEC);
			item->vend = item->vstart + lround(width / speed);
			itime_add = MAX(lround(scale * 64 / speed),(TEXT_AHEAD_SEC>>1));
		}
		if(item->script!=0){
			itime_add = 0;
		}
		slot_item->vappear = item->vstart - itime_add;
		slot_item->vvanish = item->vend + itime_add;
		if(data->debug){
			fprintf(data->log,"[process_chat/set_speed]comment speed %.2fpix/sec.\n",slot_item->speed*100.0);
		}
		return;
	}
}

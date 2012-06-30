#include <SDL/SDL_endian.h>
#include <stdio.h>

#include <stdlib.h>
#include "chat.h"
#include "../mydef.h"
#include "../nicodef.h"
#include "../unicode/uniutil.h"

SDL_Color convColor24(int color);
/*
 * 出力 CHAT chat 領域確保、項目設定
 * 出力 CHAT_SLOT chat->slot ← slot ポインタ設定のみ
 */
int initChat(FILE* log,CHAT* chat,const char* file_path,CHAT_SLOT* slot,int video_length,int nico_width){
	int i;
	int max_no = INTEGER_MIN;
	int min_no = INTEGER_MAX;
	int max_item;
	chat->slot = slot;
	FILE* com_f = fopen(file_path,"rb");
	if(com_f == NULL){
		fputs("[chat/init]failed to open comment file.\n",log);
		return FALSE;
	}
	/*要素数の取得*/
	if(fread(&max_item,sizeof(max_item),1,com_f) <= 0){
		fputs("[chat/init]failed to read the number of comments.\n",log);
		return FALSE;
	}
	max_item = SDL_SwapLE32(max_item);
	fprintf(log,"[chat/init]%d comments.\n",max_item);
	chat->max_item = max_item;
	//
	if (max_item == 0){
		return TRUE;
	}
	//アイテム配列の確保
	chat->item = malloc(sizeof(CHAT_ITEM) * max_item);
	if(chat->item == NULL){
		fputs("[chat/init]failed to malloc for comment.\n",log);
		return FALSE;
	}
	if (video_length == 0){
		fprintf(log,"[chat/fix]cannot adjust end time since video_length UNKOWN\n");
	}
	/*個別要素の初期化*/
	CHAT_ITEM* item;
	int no;
	int vpos;
	int location;
	int size;
	int color;
	int str_length;
	// int duration;
	int full;
	SDL_Color color24;
	Uint16* str;
	for(i=0;i<max_item;i++){
		item = &chat->item[i];
		item->chat = chat;
		item->showed = FALSE;
		//コメント番号
		if(fread(&no,sizeof(no),1,com_f) <= 0){
			fputs("[chat/init]failed to read comment number.\n",log);
			return FALSE;
		}
		no = SDL_SwapLE32(no);
		max_no = MAX(max_no,no);
		min_no = MIN(min_no,no);
		//vpos
		if(fread(&vpos,sizeof(vpos),1,com_f) <= 0){
			fputs("[chat/init]failed to read comment vpos.\n",log);
			return FALSE;
		}
		vpos = SDL_SwapLE32(vpos);
		//文字の場所
		if(fread(&location,sizeof(location),1,com_f) <= 0){
			fputs("[chat/init]failed to read comment location.\n",log);
			return FALSE;
		}
		location = SDL_SwapLE32(location);
		//サイズ
		if(fread(&size,sizeof(size),1,com_f) <= 0){
			fputs("[chat/init]failed to read comment size.\n",log);
			return FALSE;
		}
		size = SDL_SwapLE32(size);
		//色
		if(fread(&color,sizeof(color),1,com_f) <= 0){
			fputs("[chat/init]failed to read comment color.\n",log);
			return FALSE;
		}
		color = SDL_SwapLE32(color);
		//文字数
		if(fread(&str_length,sizeof(str_length),1,com_f) <= 0){
			fputs("[chat/init]failed to read comment length.\n",log);
			return FALSE;
		}
		str_length = SDL_SwapLE32(str_length);
		//文字列
		str = malloc(str_length);
		if(str == NULL){
			fputs("[chat/init]failed to malloc for comment text.\n",log);
			return FALSE;
		}
		if(fread(str,str_length,1,com_f) <= 0){
			fputs("[chat/init]failed to read comment text.\n",log);
			return FALSE;
		}
		// full コマンド？
		if(GET_CMD_FULL(location)!= 0){
			full = 1;
		} else {
			full = 0;
		}
		// color24bit ?
		if(color < 0){
		//	color = color & 0x00ffffff;
			color24 = convColor24(color & 0x00ffffff);
			color = -24;
		}else if(color < CMD_COLOR_MAX){
			color24 = COMMENT_COLOR[color];
		}else{
			color24 = COMMENT_COLOR[CMD_COLOR_DEF];
		}
		// bit 15-8 を＠秒数とみなす　saccubus1.26α1以降
		//duration = GET_CMD_DURATION(location);
		location = GET_CMD_LOC(location);
	/*
		if (duration == 0){	// 通常コメント
			if (location != CMD_LOC_DEF){
				duration = TEXT_SHOW_SEC - TEXT_AHEAD_SEC;
			} else {
				duration = TEXT_SHOW_SEC;
			}
		} else {	// @秒数
			duration *= VPOS_FACTOR;
		}
	*/
		//変数セット
		item->no = no;
		item->vpos = (VPOS_T)vpos;
		item->location = location;
		item->size = size;
		item->color = color;
		removeZeroWidth(str,str_length/sizeof(Uint16));
		item->str = str;
		/*内部処理より*/
		if(location != CMD_LOC_DEF){
			item->vstart = (VPOS_T)vpos;
			item->vend = (VPOS_T)(vpos + TEXT_SHOW_SEC_S - 1);
			//vend is last tick of LIFE, so must be - 1 done.
			// item->vend = vpos + duration - 1;
		}else{
			item->vstart = (VPOS_T)(vpos - TEXT_AHEAD_SEC);
			item->vstart -= (VPOS_T)25;	//for debug
			item->vend = (VPOS_T)(vpos + TEXT_SHOW_SEC_S - 1);
			//vend is last tick of LIFE, so must be - 1 done.
			// item->vend = item->vstart + duration - 1;
			if(nico_width==NICO_WIDTH_WIDE){
				//TEXT_SHOW_SEC is 1.00sec (100vpos) longer. but reset after added to slot.
				item->vstart -= (VPOS_T)25;	//100 * 1/4
				item->vend += (VPOS_T)75;	//100 * 3/4
			}
		}
		item->full = full;
		// item->duration = duration;
		item->color24 = color24;
		if (video_length > 0){
			VPOS_T fix = item->vend - (VPOS_T)video_length;
			if(fix > 0){
				if(fix > (VPOS_T)4)
					fix = (VPOS_T)4;
				//item->verase -= fix;
				item->vend -= fix;
				item->vpos -= fix;
				item->vstart -= fix;
				fprintf(log,"[chat/fix]comment %d<index:%d> time adjusted : %5.2f Sec.\n",no,i,(double)fix/(double)VPOS_FACTOR);
			}
		}
		/*内部処理より　おわり*/
	}
	fclose(com_f);
	chat->max_no = max_no;
	chat->min_no = min_no;
	return TRUE;
}
//BE,LE共通
SDL_Color convColor24(int c){
	SDL_Color sc;
	sc.r = (c & 0x00ff0000) >> 16;
	sc.g = (c & 0x0000ff00) >> 8;
	sc.b = (c & 0x000000ff) >> 0;
	sc.unused = 0;
	return sc;
}
void closeChat(CHAT* chat){
	int i;
	int max_item = chat->max_item;
	for(i=0;i<max_item;i++){
		free((void*)chat->item[i].str);
	}
	free(chat->item);
}

/*
 * イテレータをリセットする。
 */
void resetChatIterator(CHAT* chat){
	chat->iterator_index = 0;
}
/*
 * イテレータを得る
 */
CHAT_ITEM* getChatShowed(CHAT* chat,VPOS_T now_vpos){
	int *i = &chat->iterator_index;
	int max_item = chat->max_item;
	CHAT_ITEM* item;
	for(;*i<max_item;(*i)++){
		item = &chat->item[*i];
		if(now_vpos >= item->vstart && now_vpos <= item->vend && !item->showed){
			return item;
		}
	}
	return NULL;
}

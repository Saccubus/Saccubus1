#ifndef CHAT_H_
#define CHAT_H_

#include <SDL/SDL.h>
#include "../struct_define.h"

struct CHAT_ITEM{
	//場所の特定
	int no;
	int vpos;
	int location;
	int full;	// whether full ommand?
	//文字の修飾
	int size;
	int color;
	SDL_Color color24;
	Uint16* str;
	//内部処理で使う
	int vstart;
	int vend;
	int showed;
	// int duration;	// vend - vstart
	  // ＠秒数の場合  指定値
	  // ue shitaの場合  300 vpos
	  // 4:3の場合  400 vpos
	  // 16:9の場合  400+α vpos
	//リファレンス
	CHAT* chat;
};

struct CHAT{
	int max_no;
	int min_no;
	//アイテム
	int max_item;
	int iterator_index;
	CHAT_ITEM* item;
	//リファレンス
	CHAT_SLOT* slot;
};

#include "chat_slot.h"
struct CHAT_SET{
	CHAT chat;
	CHAT_SLOT slot;
};

//初期化
int initChat(FILE* log,CHAT* chat,const char* file_path,CHAT_SLOT* slot,int video_length);
void closeChat();
//イテレータ
void resetChatIterator(CHAT* chat);
CHAT_ITEM* getChatShowed(CHAT* chat,int now_vpos);

#endif /*CHAT_H_*/

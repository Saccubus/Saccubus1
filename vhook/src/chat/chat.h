#ifndef CHAT_H_
#define CHAT_H_

#include <SDL/SDL.h>
#include "../struct_define.h"

//ニコスクリプトでのキー ワードUnicode定義
#define UNICODE_GYAKU	0x00009006	/*逆*/
#define UNICODE_TOU		0x00006295	/*投　投コメ*/
#define UNICODE_KO		0x000030b3	/*コ　コメ*/
#define UNICODE_DE		0x000030c7	/*デ　デフォルト*/
//ニコスクリプト　 ワード定義
#define SCRIPT_GYAKU	0x00010000
#define SCRIPT_OWNER	0x0001
#define SCRIPT_USER		0x0002
#define SCRIPT_DEFAULT	0x00020000

struct CHAT_ITEM{
	//場所の特定
	int no;
	int vpos;
	int location;
	int full;	// whether full ommand?
	int script;	// whether nico script?
	//文字の修飾
	int size;
	int color;
	SDL_Color color24;
	Uint16* str;
	//内部処理で使う
	int vstart;		//begin vpos of check y
	int vend;		//last vpos of check y
	int vappear;		//start display when wide(640x360)
	int vvanish;		//end display when wide
	int showed;			// whether checked y pos, 0: not showed, 1:showed and not finished, finished
	int duration;	// vend - vstart
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
	//プール
	CHAT_POOL* pool;
	//コメントタイプ
	const char* com_type;
	//＠逆フラグ
	int to_left;
};

#include "chat_slot.h"
struct CHAT_SET{
	CHAT chat;
	CHAT_SLOT slot;
};

//初期化
int initChat(FILE* log,CHAT* chat,const char* file_path,CHAT_SLOT* slot,int video_length,int nico_width,const char* com_type);
void closeChat();
//イテレータ
void resetChatIterator(CHAT* chat);
CHAT_ITEM* getChatShowed(CHAT* chat,int now_vpos);
SDL_Color convColor24(int c);
SDL_Color getSDL_color(int c);

#endif /*CHAT_H_*/

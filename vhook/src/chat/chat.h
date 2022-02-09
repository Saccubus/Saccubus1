#ifndef CHAT_H_
#define CHAT_H_

#include <SDL/SDL.h>
#include "../struct_define.h"

//ニコスクリプトでのキー ワードUnicode定義
#define UNICODE_GYAKU	0x00009006	/*逆*/
#define UNICODE_TOU		0x00006295	/*投　投コメ*/
#define UNICODE_KO		0x000030b3	/*コ　コメ*/
#define UNICODE_DE		0x000030c7	/*デ　デフォルト*/
#define UNICODE_BO		0x000030dc	/*ボ　ボタン */
#define UNICODE_KAKKO	0x0000300c	/*「　括弧 */
//ニコスクリプト　 ワード定義
#define SCRIPT_GYAKU	0x00010000
#define SCRIPT_OWNER	0x0001
#define SCRIPT_USER		0x0002
#define SCRIPT_DEFAULT	0x00020000
#define SCRIPT_REPLACE	0x00040000
#define SCRIPT_BUTTON	0x00080000
#define SCRIPT_VOTE		0x00100000

struct CHAT_ITEM{
	//場所の特定
	int no;
	int vpos;
	int location;
	int html5font;	//html5フォントコマンド
		// 0	//ゴシック標準
		// 1	// 4096明朝体
		// 2	//丸ゴシック体
		// 3	//リザーブ
	int full;	// whether full ommand?
	int waku;	// 黄枠付加
	int script;	// whether nico script?
	int patissier;	//patissier command
	int invisible;	//invisible command
	int replace_user;	// /replace target:user
	int replace_owner;	//	/replace target:owner
	int is_button;	// ボタン
	int ender;	//enderコマンド 改行リサイズキャンセル
	int itemfork;	//itemfork
	//文字の修飾
	int size;
	int color;
	SDL_Color color24;
	Uint16* str;
	//内部処理で使う
	int vstart;		//begin vpos of check y
	int vend;		//last vpos of check y
	int vappear;	//start display of comment
	//int vvanish;		//end display when wide
	//int showed;			// whether checked y pos, 0: not showed, 1:showed and not finished, finished
	int pooled;			// wheather item is pooled?
	int duration;	// vend - vstart
	  // ＠秒数の場合  指定値
	  // ue shitaの場合  300 vpos
	  // 4:3の場合  400 vpos
	  // 16:9の場合  400+α vpos
	int nb_line;	//コメント行数
	int double_resized;	//二重リサイズしたか？
	//リファレンス
	CHAT* chat;
};

struct CHAT{
	// コメントID	0:user, 1:owner, 2:oprional
	int cid;
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
	int reverse_vpos;	// VPOS
	int reverse_duration;	// VPOS
	//patissierコマンドによる無視コメント番号最大値
	int patissier_ignore;
	// /vote
	CHAT_ITEM *vote_chatitem;
};

#include "chat_slot.h"
struct CHAT_SET{
	CHAT chat;
	CHAT_SLOT slot;
};

//初期化
int initChat(FILE* log,CHAT* chat,const char* file_path,CHAT_SLOT* slot,int video_length,int nico_width,
	int cid,const char* com_type,int toLeft, int is_live,int vpos_shift,int ahead_vpos, int min_vpos);
void closeChat();
//イテレータ
void resetChatIterator(CHAT* chat);
CHAT_ITEM* getChatShowed(CHAT* chat,int now_vpos,int ahead_vpos);
SDL_Color convColor24(int c);
SDL_Color getSDL_color(int c);

#endif /*CHAT_H_*/

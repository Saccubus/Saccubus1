#ifndef MAIN_H_
#define MAIN_H_
#include <SDL/SDL.h>
#include <SDL/SDL_ttf.h>
#include "nicodef.h"
#include "struct_define.h"
#include "chat/chat.h"
#include "chat/chat_slot.h"

struct CDATA{
	int enable_comment;
	CHAT chat;
	CHAT_SLOT slot;
};

struct DATA{
	FILE* log;
	TTF_Font* font[CMD_FONT_MAX];
	SDL_Surface* screen;
	/*それぞれのコメントに応じたデータ*/
	//ユーザコメント
	CDATA user;
//	int enable_user_comment;
//	CHAT chat;
//	CHAT_SLOT slot;
	//投稿者コメント
	CDATA owner;
//	int enable_owner_comment;
//	CHAT ownerchat;
//	CHAT_SLOT ownerslot;
	//オプショナルコメント
	CDATA optional;
//	int enable_optional_comment;
//	CHAT optionalchat;
//	CHAT_SLOT optionalslot;
	//一般的なデータ
	int shadow_kind;
	int show_video;
	int fontsize_fix;
	int opaque_comment;
	int optional_trunslucent;
	int process_first_called;
	int video_length;
//	int aspect100;		// アスペクト比*100	Not used now
	int nico_width_now;	// 元動画の横幅
	float font_h_fix_r;	// フォントの高さをnicoplayer.swfに合わせる倍率(0< <2)（実験的）
	int original_resize;	// さきゅばす独自リサイズが有効（デフォルト有効）
};

typedef struct SETTING{
	const char* data_user_path;
	const char* data_owner_path;
	const char* data_optional_path;
	const char* font_path;
	int video_length;
	// 新しいffmpegからvideoの時間を貰うInterfaceが分かるまで代わりに
	// さきゅばすから渡す（但し不明の場合は　0　or　-1）
	// コメント表示の最後の調整だけなのでなくても我慢するように変更。
	int font_index;
	int user_slot_max;
	int owner_slot_max;
	int optional_slot_max;
	int shadow_kind;
	int nico_width_now;	// 元動画の横幅
	/*TRUE OR FALSE*/
	int enable_user_comment;
	int enable_owner_comment;
	int enable_optional_comment;
	int show_video;
	int fontsize_fix;
	int opaque_comment;
	int optional_trunslucent;
	// コミュニティ動画では通常コメントをオプショナルスレッドとして半透明にしているため、選択可能にする
	float font_h_fix_r;	// フォントの高さをnicoplayer.swfに合わせる倍率（実験的）
	int original_resize;	// さきゅばす独自リサイズが有効（デフォルト有効）
}SETTING;

int init(FILE* log);
int initData(DATA* data,FILE* log,const SETTING* setting);
int main_process(DATA* data,SDL_Surface* surf,const int now_vpos);
int closeData(DATA* data);
int close();

#endif /*MAIN_H_*/

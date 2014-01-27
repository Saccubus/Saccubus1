#ifndef MAIN_H_
#define MAIN_H_
#include <SDL/SDL.h>
#include <SDL/SDL_ttf.h>
#include <math.h>
#include "nicodef.h"
#include "struct_define.h"
#include "chat/chat.h"
#include "chat/chat_slot.h"
#include "unicode/unitable.h"
#define CA_FONT_PATH_MAX 70

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
	float opaque_rate;
	int optional_trunslucent;
	int process_first_called;
	int video_length;
//	int aspect100;		// アスペクト比*100	Not used now
	int nico_width_now;	// 元動画の横幅
	int aspect_mode;		// 0: 512, 1:640
	float aspect_rate;		// w/h
	int vout_width;
	int vout_height;
	int vout_x;
	int vout_y;
	int pad_w;
	int pad_h;
	int limit_height;
	int y_min;
	int y_max;
	float font_w_fix_r;	// フォントの幅をnicoplayer.swfに合わせる倍率(0< <2)（実験的）
	float font_h_fix_r;	// フォントの高さをnicoplayer.swfに合わせる倍率(0< <2)（実験的）
	int original_resize;	// さきゅばす独自リサイズが有効（デフォルト有効）
	int comment_speed;	// コメント速度を指定する場合≠0
	int enableCA;
	const char* fontdir;
	int use_lineskip_as_fontsize;	//フォントサイズを決めるのにLineSkipを合わせる（実験的）
	int debug;
	const char* extra_mode;
	double width_scale;	//書き込み可　videowidth/nicowidth_now
	int defcolor;	//デフォルトカラー24bit（エイプリルフール用、@デフォルト）
	int deflocation;
	int defsize;
	// CA用フォント
	TTF_Font* CAfont[CA_FONT_PATH_MAX][CMD_FONT_MAX];
	// CA切替用Unicode群
	//Uint16* font_change[CA_FONT_MAX];
	// 0:*protect_gothic
	// 1:*change_simsun
	// 2:*change_gulim
	// 3:*arial
	// 4:*gergia
	// 5:*msui_gothic
	// 6:*devanagari
	// 7:*extra
	Uint16* extra_change;
	//Uint16* zero_width;
	//Uint16* spaceable;
//	int limit_height;
	SDL_Surface* ErrFont;
	unsigned int * wakuiro_dat;
	int q_player;
	//char wstr[128];
#ifdef VHOOKDEBUG
//	float dts_rate;	// フレームレート
//	float dts;		// DTS
//	int last_vpos;
#endif
	// 実験的設定
	short font_pixel_size[CMD_FONT_MAX];
	short fixed_font_height[CMD_FONT_MAX];	// 修正フォント指定(ポイント指定)
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
	// ↑必要なくなった→復活
	int font_index;
	int user_slot_max;
	int owner_slot_max;
	int optional_slot_max;
	int shadow_kind;
	int nico_width_now;	// 元動画の横幅
	//int nico_height_now = 384
	//int aspect_mode;		// 0: 512, 1:640
	const char* input_size;
	const char* set_size;
	const char* pad_option;
	const char* out_size;
	/*TRUE OR FALSE*/
	int enable_user_comment;
	int enable_owner_comment;
	int enable_optional_comment;
	int show_video;
	int fontsize_fix;
	int opaque_comment;
	const char* opaque_rate;
	int optional_trunslucent;
	int q_player;		//コメントが動画の高さ以下になるか？
	// CA用フォント
	const char* CAfont_path[CA_FONT_PATH_MAX];
	int CAfont_index[CA_FONT_PATH_MAX];
	const char* fontdir;
	// CA切替用Unicode群
	//const char* CAfont_change_uc[CA_FONT_MAX];
	//const char* zero_width_uc;
	//const char* spaceable_uc;
	// 実験用追加フォント
	const char* extra_path;
	const char* extra_uc;
	//int extra_fontindex;
	// コミュニティ動画では通常コメントをオプショナルスレッドとして半透明にしているため、選択可能にする
	float font_w_fix_r;	// フォントの幅をnicoplayer.swfに合わせる倍率（実験的）
	float font_h_fix_r;	// フォントの高さをnicoplayer.swfに合わせる倍率（実験的）
	int original_resize;	// さきゅばす独自リサイズが有効（デフォルト有効）
	int comment_speed;	// コメント速度を指定する場合≠0
	int enableCA;
	int use_lineskip_as_fontsize;	//フォントサイズを決めるのにLineSkipを合わせる（実験的）
	int debug;
	const char* extra_mode;	// debugモード文字列
	const char* april_fool;	// エイプリルフール文字列
	const char* wakuiro;	// 黄枠色指定文字列
//	const char* framerate;	// フレームレート
	char* fontlist;	// フォントのリスト　999fontname ...（128個まで）
}SETTING;

#include "struct_define.h"
int init(FILE* log);
int initData(DATA* data,FILE* log,SETTING* setting);
int main_process(DATA* data,SDL_Surface* surf,const int now_vpos);
int closeData(DATA* data);
int close();

#endif /*MAIN_H_*/

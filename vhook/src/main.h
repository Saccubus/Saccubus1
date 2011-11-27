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
	int comment_speed;	// コメント速度を指定する場合≠0
	int enableCA;
	int debug;
	// CA用フォント
	TTF_Font* CAfont[CA_FONT_MAX][CMD_FONT_MAX];
	// CA切替用Unicode群
	Uint16* font_change[CA_FONT_MAX];
	// 0:*protect_gothic
	// 1:*change_simsun
	// 2:*change_gulim
	// 3:*arial
	// 4:*gergia
	Uint16* zero_width;
	Uint16* spaceable;
//	int limit_height;
	// 実験的設定↓
	short font_pixel_size[CMD_FONT_MAX];
	short fixed_font_height[CMD_FONT_MAX];	// 修正フォント指定(ポイント指定)
/*
	// short font_height_rate;	// フォントの高さをニコ動に合わせる倍率（元動画アスペクト比による）
	short next_h_rate;	// コメントの高さの次の高さとの差（%）（元動画アスペクト比による）
	short next_h_pixel;	// コメントの高さの次の高さとの差（px）(4:3,16:9)
	short limit_width[2];	// 臨界幅リサイズ幅（ノーマル、フル）
	short double_resize_width[2];	// ダブルリサイズ開始幅（ノーマル、フル）
	short limit_height;	// 弾幕モード開始の高さ（元動画アスペクト比による）
	short font_scaling;	//data->font[]内のフォントサイズのスケーリング倍率
	short double_limit_width[2];	// ダブルリサイズ臨界幅（ノーマル、フル）
	short debug_key;	// デバッグキー
	short target_width;	// 変換後の横幅、指定がなければ元動画の横幅
	//  ↑ニコ動に対し２倍や１／２倍のスケールの時にフォントをzoomさせないため
	//  TRUE OR FALSE
	short limitwidth_resize;	// 臨界幅リサイズが有効（デフォルト有効）
	short linefeed_resize;	// 改行リサイズが有効（デフォルト有効）
	short double_resize;		// ダブルリサイズが有効（デフォルト有効）
	short font_double_scale;		// フォントサイズ自動修正が有効（デフォルト2倍サイズが有効）
*/
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
	// ↑必要なくなった
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
	// CA用フォント
	const char* CAfont_path[CA_FONT_MAX];
	// CA切替用Unicode群
	const char* change_simsun_uc;
	const char* change_gulim_uc;
	const char* protect_gothic_uc;
	const char* zero_width_uc;
	const char* spaceable_uc;
	const char* georgia_uc;
	// コミュニティ動画では通常コメントをオプショナルスレッドとして半透明にしているため、選択可能にする
	float font_h_fix_r;	// フォントの高さをnicoplayer.swfに合わせる倍率（実験的）
	int original_resize;	// さきゅばす独自リサイズが有効（デフォルト有効）
	int comment_speed;	// コメント速度を指定する場合≠0
	int enableCA;
	int debug;
/*
	// 実験的設定↓
	short font_height_rate[2];	// フォントの高さをニコ動に合わせる倍率（4:3,16:9）
	short nico_limit_width[2];	// 臨界幅リサイズ幅（ノーマル、フル）
	short double_resize_width[2];	// ダブルリサイズ開始幅（ノーマル、フル）
	short nico_limit_height[2];	// 弾幕モード開始の高さ（4:3,16:9）
	short next_h_rate[2];	// コメントの高さの次の高さとの差（%）（4:3,16:9）
	short next_h_pixel[2];	// コメントの高さの次の高さとの差（px）(4:3,16:9)
	short double_limit_width[2];	// ダブルリサイズ臨界幅（ノーマル、フル）
	short fixed_font_size[CMD_FONT_MAX];	// 修正フォント指定(ポイント指定)
	short debug_key;	// デバッグキー
	short target_width;	// オプションから変換後の横幅をスクレイピング、指定がなければ元動画の横幅
	//  ニコ動に対し２倍や１／２倍のスケールの時にフォントをzoomさせないため
	//  TRUE OR FALSE
	short limitwidth_resize;	// 臨界幅リサイズが有効（デフォルト有効）
	short linefeed_resize;	// 改行リサイズが有効（デフォルト有効）
	short double_resize;		// ダブルリサイズが有効（デフォルト有効）
	// obsolate
	//short font_double_scale;		// フォントサイズ自動修正が有効（デフォルト2倍サイズが有効）
*/
}SETTING;

int init(FILE* log);
int initData(DATA* data,FILE* log,const SETTING* setting);
int main_process(DATA* data,SDL_Surface* surf,const int now_vpos);
int closeData(DATA* data);
int close();

#endif /*MAIN_H_*/

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
	// コミュニティ動画では通常コメントをオプショナルスレッドとして半透明にしているため、選択可能にする
	int process_first_called;
	int video_length;
//	int aspect100;		// アスペクト比*100	Not used now
	int nico_width_now;	// 元動画の横幅
	int font_h_fix_r;	// フォントの高さをnicoplayer.swfに合わせる倍率％(0< <200)（実験的）
	int original_resize;	// さきゅばす独自リサイズが有効（デフォルト有効）
	int limitwidth_resize;	// 臨界幅リサイズが有効（デフォルト有効）
	int linefeed_resize;	// 改行リサイズが有効（デフォルト有効）
	int double_resize;		// ダブルリサイズが有効（デフォルト有効）
	int font_double_scale;		// フォント字形の自動修正が有効（デフォルト2倍の字形が有効）
	int nico_limit_width;	// ノーマル臨界幅
	int nico_limit_width_full;	// フルコマンド臨界幅
	int nico_limit_height;	// 弾幕モード開始の高さ
	int fixed_font_size[CMD_FONT_MAX];	// 修正フォント指定(ポイント指定)
	int next_y_ratio;	// コメントの高さの次の高さとの差（%）
	int target_width;	// オプションから変換後の横幅をスクレイピング、指定がなければ元動画の横幅
	// ニコ動に対し２倍１倍１／２倍１／４倍のスケールの時にフォントをzoomさせないため
	int font_scaling;	//data->font[]内のフォントサイズのスケーリング倍率
	int font_pixel_size[CMD_FONT_DEF];
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
	int font_h_fix_r;	// フォントの高さをnicoplayer.swfに合わせる倍率 4:3（実験的）
	int font_h_fix_r_wide;	// フォントの高さをnicoplayer.swfに合わせる倍率 16:9（実験的）
	int original_resize;	// さきゅばす独自リサイズが有効（デフォルト有効）
	int limitwidth_resize;	// 臨界幅リサイズが有効（デフォルト有効）
	int linefeed_resize;	// 改行リサイズが有効（デフォルト有効）
	int double_resize;		// ダブルリサイズが有効（デフォルト有効）
	int font_double_scale;		// フォント字形の自動修正が有効（デフォルト2倍の字形が有効）
	int nico_limit_width;	// 4:3 臨界幅
	int nico_limit_width_full;	// 16:9 臨界幅
	int nico_limit_height;	// 4:3 弾幕モード開始の高さ
	int nico_limit_height_wide;	// 16:9 弾幕モード開始の高さ
	int fixed_font_size[CMD_FONT_MAX];	// 修正フォント指定(ポイント指定)
	int next_y_ratio;	// コメントの高さの次の高さとの差（%）
	int target_width;	// オプションから変換後の横幅をスクレイピング、指定がなければ元動画の横幅
	// ニコ動に対し２倍や１／２倍のスケールの時にフォントをzoomさせないため
}SETTING;

int init(FILE* log);
int initData(DATA* data,FILE* log,const SETTING* setting);
int main_process(DATA* data,SDL_Surface* surf,const int now_vpos);
int closeData(DATA* data);
int close();

#endif /*MAIN_H_*/

#ifndef MAIN_H_
#define MAIN_H_
#include <SDL/SDL.h>
#include <SDL/SDL_ttf.h>
#include "nicodef.h"
#include "struct_define.h"
#include "chat/chat.h"
#include "chat/chat_slot.h"

struct DATA{
	FILE* log;
	TTF_Font* font[CMD_FONT_MAX];
	SDL_Surface* screen;
	/*それぞれのコメントに応じたデータ*/
	//ユーザコメント
	int enable_user_comment;
	CHAT chat;
	CHAT_SLOT slot;
	//投稿者コメント
	int enable_owner_comment;
	
	//一般的なデータ
	int shadow_kind;
	int show_video;
	int fontsize_fix;
	int opaque_comment;
	int process_first_called;
	int video_length;
};

typedef struct SETTING{
	const char* data_user_path;
	const char* data_owner_path;
	const char* font_path;
	int video_length;
	int font_index;
	int user_slot_max;
	int owner_slot_max;
	int shadow_kind;
	/*TRUE OR FALSE*/
	int enable_user_comment;
	int enable_owner_comment;
	int show_video;
	int fontsize_fix;
	int opaque_comment;
}SETTING;

int init(FILE* log);
int initData(DATA* data,FILE* log,const SETTING* setting);
int main_process(DATA* data,SDL_Surface* surf,const int now_vpos);
int closeData(DATA* data);
int close();

#endif /*MAIN_H_*/

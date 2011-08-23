
#include <SDL/SDL.h>
#include <SDL/SDL_ttf.h>
#include <stdio.h>
#include "main.h"
#include "mydef.h"
#include "nicodef.h"
#include "process.h"
/**
 * ライブラリ初期化
 */
int init(FILE* log){
	fputs("[main/init]initializing libs...\n",log);
	//SDL
	if(SDL_Init(SDL_INIT_VIDEO)>=0){
		fputs("[main/init]initialized SDL.\n",log);
	}else{
		fputs("[main/init]failed to initialize SDL.\n",log);
			return FALSE;
	}
	//SDL_ttf
	if(TTF_Init() >= 0){
		fputs("[main/init]initialized SDL_ttf.\n",log);
	}else{
		fputs("[main/init]failed to initialize SDL_ttf.\n",log);
			return FALSE;
	}
	fputs("[main/init]initialized libs.\n",log);
	return TRUE;
}
/*
 * データの初期化
 * ContextInfo ci->DATA data ← SETTING setting
 */
int initData(DATA* data,FILE* log,const SETTING* setting){
	int i;
	data->enable_user_comment = setting->enable_user_comment;
	data->enable_owner_comment = setting->enable_owner_comment;
	data->log = log;
	data->fontsize_fix = setting->fontsize_fix;
	data->show_video = setting->show_video;
	data->opaque_comment = setting->opaque_comment;
	data->shadow_kind = setting->shadow_kind;
	data->process_first_called=FALSE;
	data->video_length = setting->video_length;
	data->nico_width_now = setting->nico_width_now;
	fputs("[main/init]initializing context...\n",log);
	//フォント
	TTF_Font** font = data->font;
	const char* font_path = setting->font_path;
	const int font_index = setting->font_index;
	for(i=0;i<CMD_FONT_MAX;i++){
		int fontsize = COMMENT_FONT_SIZE[i];
		if(setting->fontsize_fix){
			fontsize <<= 1;
		}
		font[i] = TTF_OpenFontIndex(font_path,fontsize,font_index);
		if(font[i] == NULL){
				fprintf(log,"[main/init]failed to load font:%s index:[%d].\n",font_path,font_index);
				//0でも試してみる。
				fputs("[main/init]retrying to open font at index:0...",log);
				font[i] = TTF_OpenFontIndex(font_path,fontsize,0);
				if(font[i] == NULL){
					fputs("failed.\n",log);
					return FALSE;
				}else{
					fputs("success.\n",log);
				}
		}
		TTF_SetFontStyle(font[i],TTF_STYLE_BOLD);
	}
	fputs("[main/init]initialized font.\n",log);
	/*
	 * ユーザコメント
	 */
	if(data->enable_user_comment){
		fputs("[main/init]User Comment is enabled.\n",log);
		//コメントデータ
		if(initChat(log,&data->chat,setting->data_user_path,&data->slot,data->video_length)){
			fputs("[main/init]initialized comment.\n",log);
		}else{
			fputs("[main/init]failed to initialize comment.",log);
			return FALSE;
		}
		//コメントスロット
		if(initChatSlot(log,&data->slot,setting->user_slot_max,&data->chat)){
			fputs("[main/init]initialized comment slot.\n",log);
		}else{
			fputs("[main/init]failed to initialize comment slot.",log);
			return FALSE;
		}
	}
	/*
	 * オーナコメント
	 */
	if(data->enable_owner_comment){
		fputs("[main/init]Owner Comment is enabled.\n",log);
		//コメントデータ
		if(initChat(log,&data->ownerchat,setting->data_owner_path,&data->ownerslot,data->video_length)){
			fputs("[main/init]initialized owner comment.\n",log);
		}else{
			fputs("[main/init]failed to initialize owner comment.",log);
			return FALSE;
		}
		//コメントスロット	owner_slot_max must be infinite
		if(initChatSlot(log,&data->ownerslot, setting->owner_slot_max,&data->ownerchat)){
			fputs("[main/init]initialized owner comment slot.\n",log);
		}else{
			fputs("[main/init]failed to initialize owner comment slot.",log);
			return FALSE;
		}
	}

	//終わり。
	fputs("[main/init]initialized context.\n",log);
	return TRUE;
}
/*
 * 映像の変換
 */
int main_process(DATA* data,SDL_Surface* surf,const int now_vpos){
	FILE* log = data->log;
	if(!data->process_first_called){
		data->aspect100 = surf->w * 100 /surf->h;
		fprintf(log,"[main/process]screen size is %dx%d, aspect is %d/100.\n",surf->w,surf->h, data->aspect100);
		fflush(log);
	}
	/*フィルタをかける*/
	if(process(data,surf,now_vpos)){
	}
	fflush(log);
	/*変換した画像を見せる。*/
	if(data->show_video){
		if(!data->process_first_called){
			data->screen = SDL_SetVideoMode(surf->w, surf->h, 24, SDL_HWSURFACE | SDL_DOUBLEBUF);
			if(data->screen == NULL){
				fputs("[main/process]failed to initialize screen.\n",log);
				fflush(log);
				return FALSE;
			}
		}
		SDL_BlitSurface(surf,NULL,data->screen,NULL);
		SDL_Flip(data->screen);
		SDL_Event event;
		while(SDL_PollEvent(&event)){}
	}
	//一回目以降はTRUEになる。
	data->process_first_called=TRUE;
	fflush(log);
	return TRUE;
}
/*
 * データのクローズ
 */
int closeData(DATA* data){
	int i;
	//ユーザコメントが有効なら開放
	if(data->enable_user_comment){
		closeChat(&data->chat);
		closeChatSlot(&data->slot);
	}
	//オーナコメントが有効なら開放
	if(data->enable_owner_comment){
		closeChat(&data->ownerchat);
		closeChatSlot(&data->ownerslot);
	}
		//フォント開放
	for(i=0;i<CMD_FONT_MAX;i++){
		TTF_CloseFont(data->font[i]);
	}
	return TRUE;
}

/*
 * ライブラリシャットダウン
 */
int close(){
		//SDLをシャットダウン
		SDL_Quit();
	//同じくTTFをシャットダウン
		TTF_Quit();
	return TRUE;
}

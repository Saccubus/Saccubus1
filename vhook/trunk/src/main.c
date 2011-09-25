
#include <SDL/SDL.h>
#include <SDL/SDL_ttf.h>
#include <stdio.h>
#include "main.h"
#include "mydef.h"
#include "nicodef.h"
#include "process.h"
int initCommentData(DATA* data, CDATA* cdata, FILE* log, const char* path, int max_slot, const char* com_type);

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
	data->user.enable_comment = setting->enable_user_comment;
	data->owner.enable_comment = setting->enable_owner_comment;
	data->optional.enable_comment = setting->enable_optional_comment;
	data->log = log;
	data->fontsize_fix = setting->fontsize_fix;
	data->show_video = setting->show_video;
	data->opaque_comment = setting->opaque_comment;
	data->optional_trunslucent = setting->optional_trunslucent;
	data->shadow_kind = setting->shadow_kind;
	data->process_first_called=FALSE;
	data->nico_width_now = setting->nico_width_now;
	// followings are obsolate.
	data->video_length = setting->video_length;
	if (data->video_length <= 0){
		data->video_length = INTEGER_MAX;
	}

	// experimental
	data->original_resize = setting->original_resize;
	data->limitwidth_resize = setting->limitwidth_resize;
	data->linefeed_resize = setting->linefeed_resize;
	data->double_resize = setting->double_resize;
	data->font_double_scale = setting->font_double_scale;
	if (data->nico_width_now == NICO_WIDTH){
		data->font_height_rate = setting->font_height_rate[0];
		data->limit_height = setting->nico_limit_height[0];
		data->next_h_rate = setting->next_h_rate[0];
	} else {
		data->font_height_rate = setting->font_height_rate[1];
		data->limit_height = setting->nico_limit_height[1];
		data->next_h_rate = setting->next_h_rate[1];
	}
	data->limit_width[0] = setting->nico_limit_width[0];
	data->limit_width[1] = setting->nico_limit_width[1];
	data->double_resize_width[0] = setting->double_resize_width[0];
	data->double_resize_width[1] = setting->double_resize_width[1];
	data->double_limit_width[0] = setting->double_limit_width[0];
	data->double_limit_width[1] = setting->double_limit_width[1];
	data->target_width = setting->target_width;
	int scale100 = 100;
	if(data->target_width == 0){
		data->target_width = data->nico_width_now;
	} else {
		 scale100 = (data->target_width * 100) / data->nico_width_now;
	}
	fprintf(log,"[main/debug]video scaling:%d%%.\n",scale100);
	data->font_scaling = 1;
	if(setting->fontsize_fix && setting->font_double_scale){
		if(setting->original_resize){
			data->font_scaling = 2;
		} else if(scale100 >= 200 || (scale100 != 100 && scale100 != 50)){
			//自動スケールが２倍以上か１，１／２以外場合はフォントを２倍に拡大
			data->font_scaling = 2;
		}
	}
	fprintf(log,"[main/debug]font pre-scaling:%d%%.\n",data->font_scaling*100);

	fputs("[main/init]initializing context...\n",log);
	//フォント
	TTF_Font** font = data->font;
	const char* font_path = setting->font_path;
	const int font_index = setting->font_index;
	for(i=0;i<CMD_FONT_MAX;i++){
		int fontsize = setting->fixed_font_size[i];
		if(data->font_scaling == 2){
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
		data->fixed_font_size[i] = TTF_FontHeight(font[i]);
		// TTF_FontHeight()が正しいかどうかは疑問？
		//  実験では設定値と違うpt数になった
		// 参考 1 pt = 1/72 inch, 1 px = 1 dot
		// Win-PCでは 96 dot per inch だから 4/3倍
		data->font_pixel_size[i] = data->fixed_font_size[i] * 4 / 3;
	}
	fprintf(log,"[main/init]initialized font, height is DEF=%dpt(%dpx), BIG=%dpt(%dpx), SMALL=%dpt(%dpx)\n",
		data->fixed_font_size[CMD_FONT_DEF],data->font_pixel_size[CMD_FONT_DEF],
		data->fixed_font_size[CMD_FONT_BIG],data->font_pixel_size[CMD_FONT_BIG],
		data->fixed_font_size[CMD_FONT_SMALL],data->font_pixel_size[CMD_FONT_SMALL]
		);

#if DEBUG == 1
	fprintf(log, "[main/DEBUG]font height fix ratio:%d%%, y_diff:%d%% (experimental)\n",
		data->font_height_rate,data->next_h_rate);
	fprintf(log, "[main/DEBUG]limit width:%d %d, double_resize:%d %d (experimental)\n",
		data->limit_width[0], data->limit_width[1],
		data->double_resize_width[0],data->double_resize_width[1]);
	fprintf(log, "[main/DEBUG]limit height:%d %d (experimental)\n",
		data->limit_height);
	fprintf(log, "[main/DEBUG]target width: %d (experimaental)\n",
		data->target_width);
#endif
	fflush(log);
	/*
	 * ユーザコメント
	 */
	if (!initCommentData(data, &data->user, log,
			setting->data_user_path, setting->user_slot_max, "user")){
		return FALSE;
	}
	/*
	 * オーナコメント
	 */
	if (!initCommentData(data, &data->owner, log,
			setting->data_owner_path, setting->owner_slot_max, "owner")){
		return FALSE;
	}
	/*
	 * オプショナルコメント
	 */
	if (!initCommentData(data, &data->optional, log,
			setting->data_optional_path, setting->optional_slot_max, "optional")){
		return FALSE;
	}

	//終わり。
	fputs("[main/init]initialized context.\n",log);
	fflush(log);
	return TRUE;
}
/*
 * コメントデータの初期化
 * DATA data->user owner optional
 */
int initCommentData(DATA* data, CDATA* cdata,FILE* log,const char* path, int max_slot, const char* com_type){
	if (cdata->enable_comment){
		fprintf(log,"[main/init]%s comment is enabled.\n",com_type);
		//コメントデータ
		if (initChat(log, &cdata->chat, path, &cdata->slot, data->video_length,data)){
			fprintf(log,"[main/init]initialized %s comment.\n",com_type);
		}else{
			fprintf(log,"[main/init]failed to initialize %s comment.",com_type);
			closeChat(&cdata->chat);	// メモリリーク防止
			return FALSE;
		}
		if (cdata->chat.max_item > 0){
			//コメントスロット
			if(max_slot > cdata->chat.max_item){
				max_slot = cdata->chat.max_item;
				fprintf(log,"[main/init]%s comment max_slot changed to %d.\n",com_type, max_slot);
			}
			if(initChatSlot(log, &cdata->slot, max_slot, &cdata->chat)){
				fprintf(log,"[main/init]initialized %s comment slot.\n",com_type);
			}else{
				fprintf(log,"[main/init]failed to initialize %s comment slot.",com_type);
				closeChatSlot(&cdata->slot);	// メモリリーク防止
				return FALSE;
			}
		} else {
			fprintf(log,"[main/init]%s comment has changed to disable.\n",com_type);
			//closeChat(&cdata->chat);
			cdata->enable_comment = FALSE;
		}
	}
	return TRUE;
}

/*
 * 映像の変換
 */
int main_process(DATA* data,SDL_Surface* surf,const int now_vpos){
	FILE* log = data->log;
	if(!data->process_first_called){
		double scale = (double)(surf->w) / (double)(data->nico_width_now);
		double aspect = (double)surf->w  /(double)surf->h;
		fprintf(log,"[main/process]screen size:%dx%d, aspect:%5.3f, scale:%5.3f.\n",surf->w,surf->h, aspect,scale);
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
	// enable_commentは途中で無効化する場合があるが問題ない
	//ユーザコメントが有効なら開放
	if(data->user.enable_comment){
		fputs("[main/closeData]user\n",data->log);
		closeChat(&data->user.chat);
		closeChatSlot(&data->user.slot);
	}
	//オーナコメントが有効なら開放
	if(data->owner.enable_comment){
		fputs("[main/closeData]owner\n",data->log);
		closeChat(&data->owner.chat);
		closeChatSlot(&data->owner.slot);
	}
	//オプショナルコメントが有効なら開放
	if(data->optional.enable_comment){
		fputs("[main/closeData]optional\n",data->log);
		closeChat(&data->optional.chat);
		closeChatSlot(&data->optional.slot);
	}
	//フォント開放
	for(i=0;i<CMD_FONT_MAX;i++){
		TTF_CloseFont(data->font[i]);
	}
	fputs("[main/closeData]finished.\n",data->log);
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


#include <SDL/SDL.h>
#include <SDL/SDL_ttf.h>
#include <stdio.h>
#include "main.h"
#include "mydef.h"
#include "nicodef.h"
#include "process.h"
#include "unicode/util.h"
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
	data->video_length = setting->video_length;
	if (data->video_length <= 0){
		data->video_length = INTEGER_MAX;
	}
	data->nico_width_now = setting->nico_width_now;
	data->font_h_fix_r = setting->font_h_fix_r;
	data->original_resize = setting->original_resize;
	data->comment_speed = setting->comment_speed;
	data->enableCA = setting->enableCA;
	data->debug = setting->debug;
//	data->limit_height = NICO_HEIGHT;
/*
	// experimental
	data->limitwidth_resize = setting->limitwidth_resize;
	data->linefeed_resize = setting->linefeed_resize;
	data->double_resize = setting->double_resize;
	//data->font_double_scale = setting->font_double_scale;
	int is_wide;
	if (data->nico_width_now == NICO_WIDTH){
		is_wide = 0;
	} else {
		is_wide = 1;
	}
	data->font_height_rate = setting->font_height_rate[is_wide];
	data->limit_height = setting->nico_limit_height[is_wide];
	data->next_h_rate = setting->next_h_rate[is_wide];
	data->next_h_pixel = setting->next_h_pixel[is_wide];
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
	// フォントを2倍化するのはリサイズ計算を狂わせるので中止
	//  ターゲットを拡大した時にフォントが滑らかにするのは今後別手段で行う

	fprintf(log,"[main/debug]font pre-scaling:%d%%.\n",data->font_scaling*100);
*/
	fputs("[main/init]initializing context...\n",log);
	//フォント
	TTF_Font** font = data->font;
	const char* font_path = setting->font_path;
	const int font_index = setting->font_index;
	int fixed_font_index = font_index;
	int fontsize;
	/* ターゲットを拡大した時にフォントが滑らかにするため２倍化する。 */
	int isfontdoubled = 0;
	if(data->fontsize_fix){
		isfontdoubled = 1;
	}
	int line_skip[CMD_FONT_MAX];
	for(i=0;i<CMD_FONT_MAX;i++){
		/* ターゲットを拡大した時にフォントが滑らかにするため２倍化する。 */
	//	int fontsize = setting->fixed_font_size[i];
		fontsize = COMMENT_FONT_SIZE[i]<<isfontdoubled;
		//実験からSDL指定値は-1するとニコニコ動画と文字幅が合う?
		//fontsize -= 1;

		font[i] = TTF_OpenFontIndex(font_path,fontsize,font_index);
		if(font[i] == NULL){
			fprintf(log,"[main/init]failed to load font:%s size:%d index:%d.\n",font_path,fontsize,font_index);
			//0でも試してみる。
			fputs("[main/init]retrying to open font at index:0...",log);
			font[i] = TTF_OpenFontIndex(font_path,fontsize,0);
			if(font[i] == NULL){
				fputs("failed.\n",log);
				return FALSE;
			}else{
				fputs("success.\n",log);
				fixed_font_index = 0;
			}
		}
		TTF_SetFontStyle(font[i],TTF_STYLE_BOLD);
		data->fixed_font_height[i] = TTF_FontHeight(font[i]);
		// TTF_FontHeight()が正しいかどうかは疑問？
		// 実験では設定値と違う値になった
		line_skip[i] = TTF_FontLineSkip(font[i]);
		//これも違う値だった。
		// 参考 1 pt = 1/72 inch, 1 px = 1 dot
		data->font_pixel_size[i] = FONT_PIXEL_SIZE[i]<<isfontdoubled;
		// SDL_Surface に描画後の高さは文字(列)毎に異なるのでこの値で修正する。drawText()
		fprintf(log,"[main/init]load font[%d]:%s size:%d index:%d.\n",i,font_path,fontsize,fixed_font_index);
	}
	fputs("[main/init]initialized font, ",log);
	if(isfontdoubled){
		fputs("X2 scaled ",log);
	}
	fprintf(log,"height is DEF=%d %dpx(%dpx), BIG=%d %dpx(%dpx), SMALL=%d %dpx(%dpx)\n",
		data->fixed_font_height[CMD_FONT_DEF],line_skip[CMD_FONT_DEF],data->font_pixel_size[CMD_FONT_DEF],
		data->fixed_font_height[CMD_FONT_BIG],line_skip[CMD_FONT_BIG],data->font_pixel_size[CMD_FONT_BIG],
		data->fixed_font_height[CMD_FONT_SMALL],line_skip[CMD_FONT_SMALL],data->font_pixel_size[CMD_FONT_SMALL]
	);

	if(data->enableCA){
		// CAフォント
		fputs("[main/init]initializing CA(Comment Art) Feature...\n",log);
		int font_height[CMD_FONT_MAX];
		int comment_fontsize;
		int f;
		for(f = 0;f<CA_FONT_MAX;f++){
			font = &data->CAfont[f][0];		//pointer2 set
			font_path = setting->CAfont_path[f];
			if(font_path==NULL){
				fprintf(log,"[main/init]error. CA font path[%d] is NULL\n",f);
				return FALSE;
			}
			for(i=0;i<CMD_FONT_MAX;i++){
				fontsize = COMMENT_FONT_SIZE[i];
				//実験からSDL指定値はマイナスするとニコニコ動画と文字幅が合う?
				fontsize -= CA_FONT_SIZE_DECREMENT[f];
				/* ターゲットを拡大した時にフォントが滑らかにするため２倍化する。 */
				fontsize <<= isfontdoubled;
				comment_fontsize = COMMENT_FONT_SIZE[i]<<isfontdoubled;
				fixed_font_index = 0;
				if(f == GOTHIC_FONT){
					fixed_font_index = 1;
				}
				fprintf(log,"[main/init]loading CAfont[%d][%d]:%s size:%d index:%d ",f,i,font_path,fontsize,fixed_font_index);
				int try=10;
				while(try>0){
					font[i] = TTF_OpenFontIndex(font_path,fontsize,fixed_font_index);
					fputs(".",log);
					if(font[i] == NULL){
						fprintf(log,"\n[main/init]failed to load CAfont[%d][%d]:%s size:%d index:%d.\n",f,i,font_path,fontsize,fixed_font_index);
						return FALSE;
					}
					TTF_SetFontStyle(font[i],TTF_STYLE_BOLD);
					font_height[i] = TTF_FontHeight(font[i]);
					line_skip[i] = TTF_FontLineSkip(font[i]);
					if(font_height[i]==comment_fontsize){
						break;
					}
					if(--try<=0){
						break;
					}
					TTF_CloseFont(font[i]);
					if(font_height[i]>comment_fontsize){
						fontsize--;
					}else{
						fontsize++;
					}
				}
				fprintf(log,"\n[main/init]load CAfont[%d][%d]:%s size:%d index:%d.\n",f,i,font_path,fontsize,fixed_font_index);
			}
			fprintf(log,"CAfont:%d>%s height is DEF=%dpt %dpx(%dpx), BIG=%dpt %dpx(%dpx), SMALL=%dpt %dpx(%dpx)\n",
				f,(data->fontsize_fix?"x2 scaled":""),
				font_height[CMD_FONT_DEF],line_skip[CMD_FONT_DEF],data->font_pixel_size[CMD_FONT_DEF],
				font_height[CMD_FONT_BIG],line_skip[CMD_FONT_BIG],data->font_pixel_size[CMD_FONT_BIG],
				font_height[CMD_FONT_SMALL],line_skip[CMD_FONT_SMALL],data->font_pixel_size[CMD_FONT_SMALL]
			);
		}
		fputs("[main/init]Initializing Font change Characters,\n",log);
		i = convUint16(setting->protect_gothic_uc,&data->font_change[GOTHIC_FONT]);
		fprintf(log, "[main/init]GOTHIC Font protect %d pairs.\n", i>>1);
		i = convUint16(setting->change_simsun_uc,&data->font_change[SIMSUN_FONT]);
		fprintf(log, "[main/init]SIMSUN Font change %d pairs.\n", i>>1);
		i = convUint16(setting->change_gulim_uc,&data->font_change[GULIM_FONT]);
		fprintf(log, "[main/init]GULIM Font change %d pairs.\n", i>>1);
		data->font_change[ARIAL_FONT] = NULL;
		i = convUint16(setting->georgia_uc,&data->font_change[GEORGIA_FONT]);
		fprintf(log,"[main/init]GEORGIA Font use %d pairs.\n", i>>1);
		i = convUint16(setting->zero_width_uc,&data->zero_width);
		fprintf(log,"[main/init]Zero width char use %d pairs.\n", i>>1);
		if(data->debug){
			for(f=0;f<CA_FONT_MAX;f++){
				Uint16* u = data->font_change[f];
				if(u==NULL){
					continue;
				}
				fprintf(log,"font change(%d)",f);
				while(*u){
					fprintf(log," %04x-%04x",*u,*(u+1));
					u += 2;
				}
				fputs("\n",log);
			}
			Uint16* u = data->zero_width;
			if(u!=NULL){
				fprintf(log,"zero width");
				while(*u){
					fprintf(log," %04x-%04x",*u,*(u+1));
					u += 2;
				}
				fputs("\n",log);
			}
		}
		fputs("[main/init]initialized CA(Comment Art) Feature.\n",log);
	}
	fprintf(log, "[main/init]font height fix ratio:%.0f%% (experimental)\n",(data->font_h_fix_r * 100));
/*
	fprintf(log, "[main/DEBUG]limit width:%d %d, double_resize:%d %d (experimental)\n",
		data->limit_width[0], data->limit_width[1],
		data->double_resize_width[0],data->double_resize_width[1]);
*/
//	fprintf(log, "[main/init]limit height:%d (experimental)\n",data->limit_height);
//	fprintf(log, "[main/init]target width: %d (experimaental)\n",data->target_width);
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
		if (initChat(log, &cdata->chat, path, &cdata->slot, data->video_length)){
			fprintf(log,"[main/init]initialized %s comment.\n",com_type);
		}else{
			fprintf(log,"[main/init]failed to initialize %s comment.",com_type);
			// closeChat(&cdata->chat);	// メモリリーク防止
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
				// closeChatSlot(&cdata->slot);	// メモリリーク防止
				return FALSE;
			}
		} else {
			cdata->enable_comment = FALSE;
			fprintf(log,"[main/init]%s comment has changed to disable.\n",com_type);
		}
	}
	fflush(log);
	return TRUE;
}

/*
 * 映像の変換
 */
int main_process(DATA* data,SDL_Surface* surf,const int now_vpos){
	FILE* log = data->log;
	if(!data->process_first_called){
		int aspect100 = surf->w * 100 /surf->h;
		fprintf(log,"[main/process]screen size is %dx%d, aspect is %d/100.\n",surf->w,surf->h, aspect100);
		fflush(log);
	/*
		double scale = (double)(surf->w) / (double)(data->nico_width_now);
		double aspect = (double)surf->w  /(double)surf->h;
		fprintf(log,"[main/process]screen size:%dx%d, aspect:%5.3f, scale:%5.3f.\n",surf->w,surf->h, aspect,scale);
		if(surf->w != (int)data->target_width){
			fprintf(log,"[main/process]screen size differs from target_width:%d.\n",data->target_width);
			fflush(log);
			return FALSE;
		}
	*/
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
	if(data->user.enable_comment){
		closeChat(&data->user.chat);
		closeChatSlot(&data->user.slot);
	}
	//オーナコメントが有効なら開放
	if(data->owner.enable_comment){
		closeChat(&data->owner.chat);
		closeChatSlot(&data->owner.slot);
	}
	//オプショナルコメントが有効なら開放
	if(data->optional.enable_comment){
		closeChat(&data->optional.chat);
		closeChatSlot(&data->optional.slot);
	}
	//フォント開放
	for(i=0;i<CMD_FONT_MAX;i++){
		TTF_CloseFont(data->font[i]);
	}
	//CAフォント開放
	if(data->enableCA){
		int f;
		for(f=0;f<CA_FONT_MAX;f++){
			for(i=0;i<CMD_FONT_MAX;i++){
				if(data->CAfont[f][i]!=data->font[i]){
					fprintf(data->log,"free CAfont[%d][%d]\n",f,i);
					TTF_CloseFont(data->CAfont[f][i]);
				}
			}
			fprintf(data->log,"free font_change[%d]\n",f);
			free(data->font_change[f]);
		}
		free(data->zero_width);
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

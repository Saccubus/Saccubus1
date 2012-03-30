
#include <SDL/SDL.h>
#include <SDL/SDL_ttf.h>
#include <stdio.h>
#include "main.h"
#include "mydef.h"
#include "nicodef.h"
#include "process.h"
#include "unicode/uniutil.h"
#include "april_fool.h"
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

int printFontInfo(FILE* log,TTF_Font** pfont,int size,const char* name);
int extra_font(SETTING* setting, FILE* log);
/*
 * データの初期化
 * ContextInfo ci->DATA data ← SETTING setting
 */
int initData(DATA* data,FILE* log,SETTING* setting){
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
	data->font_w_fix_r = setting->font_w_fix_r;
	data->font_h_fix_r = setting->font_h_fix_r;
	data->original_resize = setting->original_resize;
	data->comment_speed = setting->comment_speed;
	data->enableCA = setting->enableCA;
	data->use_lineskip_as_fontsize = setting->use_lineskip_as_fontsize;
	data->debug = setting->debug;
	data->defcolor = CMD_COLOR_DEF;
//	data->limit_height = NICO_HEIGHT;
	int outw = setting->nico_width_now;
	int outh = outw==NICO_WIDTH_WIDE ? NICO_HEIGHT_WIDE : NICO_HEIGHT;
	if(setting->out_size!=NULL){	//-vfilters outs=width:hieight
		sscanf(setting->out_size,"%d:%d",&outw,&outh);
	}else if(setting->set_size!=NULL){	//-s "width"x"height" in ffmpeg OUT OPTION
		sscanf(setting->set_size,"%d:%d",&outw,&outh);
	}else if(setting->input_size!=NULL){
		sscanf(setting->input_size,"%d:%d",&outw,&outh);
	}
	data->vout_width = outw;
	data->vout_height = outh;
	data->vout_x = 0;
	data->vout_y = 0;
	if(setting->pad_option!=NULL){
		sscanf(setting->pad_option,"%*d:%*d:%d:%d",&data->vout_x,&data->vout_y);
	}
	fprintf(log,"[main/init]output: %dx%d @(%d,%d)\n",
		data->vout_width,data->vout_height,data->vout_x,data->vout_y);
	data->extra_mode = setting->extra_mode;
	if(setting->april_fool != NULL){
		set_aprilfool(setting,data);
	}
	fputs("[main/init]initializing context...\n",log);
	//フォント
	TTF_Font** font = data->font;
	const char* font_path = setting->font_path;
	const int font_index = setting->font_index;
	const char* fontdir = setting->fontdir;
	int fixed_font_index = font_index;
	int fontsize;
	/* ターゲットを拡大した時にフォントが滑らかにするため２倍化する。 */
	int isfontdoubled = 0;
	if(data->fontsize_fix){
		isfontdoubled = 1;
	}
	int line_skip[CMD_FONT_MAX];
	int pointsizemode = FALSE;
	if(setting->debug && strstr(setting->extra_mode,"-point")!=NULL){
		pointsizemode = TRUE;
	}
	for (i=0;i<CMD_FONT_MAX;++i) {
		data->font_pixel_size[i] = FONT_PIXEL_SIZE[i]<<isfontdoubled;
	}
	fprintf(log,"[main/init]Font height is DEF=%dpx, BIG=%dpx, SMALL=%dpx\n",
		data->font_pixel_size[CMD_FONT_DEF],data->font_pixel_size[CMD_FONT_BIG],data->font_pixel_size[CMD_FONT_SMALL]
	);
	if(!setting->enableCA){
		fputs("[main/init]initializing default Font...\n",log);
		for(i=0;i<CMD_FONT_MAX;i++){
			/* ターゲットを拡大した時にフォントが滑らかにするため２倍化する。 */
			//int fontsize = setting->fixed_font_size[i];
			fontsize = COMMENT_FONT_SIZE[i]<<isfontdoubled;
			if(pointsizemode){
				fontsize = COMMENT_POINT_SIZE[i]<<isfontdoubled;
			}
			//実験からSDL指定値は-1するとニコニコ動画と文字幅が合う?
			fontsize -= 1;

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
			printFontInfo(log,font,i,"");
		}
		fputs("[main/init]initialized font, ",log);
		if(isfontdoubled){
			fputs("Double scaled ",log);
		}
		fprintf(log,"height is DEF=%d %dpx(%dpx), BIG=%d %dpx(%dpx), SMALL=%d %dpx(%dpx)\n",
			data->fixed_font_height[CMD_FONT_DEF],line_skip[CMD_FONT_DEF],data->font_pixel_size[CMD_FONT_DEF],
			data->fixed_font_height[CMD_FONT_BIG],line_skip[CMD_FONT_BIG],data->font_pixel_size[CMD_FONT_BIG],
			data->fixed_font_height[CMD_FONT_SMALL],line_skip[CMD_FONT_SMALL],data->font_pixel_size[CMD_FONT_SMALL]
		);
	}

	int f;
	char font_file_path[128];
	if(data->enableCA){
		// CAフォント
		fputs("[main/init]initializing CA(Comment Art) Font...\n",log);
		for(f=0;f<CA_FONT_MAX;f++){
			for(i=0;i<CMD_FONT_MAX;i++){
				data->CAfont[f][i] = NULL;
			}
		}
		if(!extra_font((SETTING*)setting,log)){
			return FALSE;
		}
		int font_height[CMD_FONT_MAX];
		int target_size;
		int current_size;
		int try = 1;
		for(f = 0;f<CA_FONT_MAX;f++){
			font = &data->CAfont[f][0];		//pointer2 set
			font_path = setting->CAfont_path[f];
			if(font_path==NULL){
				if(f==EXTRA_FONT || f==ARIALUNI_FONT){
					continue;
				}
				fprintf(log,"[main/init]error. CA font path[%d] is NULL\n",f);
				return FALSE;
			}
			strcpy(font_file_path,fontdir);
			strcat(font_file_path,font_path);
			if(f!=EXTRA_FONT){
				font_path = font_file_path;
			}
			fixed_font_index = setting->CAfont_index[f];
			for(i=0;i<CMD_FONT_MAX;i++){
				fontsize = COMMENT_FONT_SIZE[i];
				//実験からSDL指定値はマイナスするとニコニコ動画と文字幅が合う?
				fontsize -= 1;
				/* ターゲットを拡大した時にフォントが滑らかにするため２倍化する。 */
				fontsize <<= isfontdoubled;
				try = 1;
				target_size = fontsize;
				if(pointsizemode){
					fontsize = COMMENT_POINT_SIZE[i];
					fontsize <<= isfontdoubled;
					target_size = fontsize;
					if(!data->original_resize){
						try = 10;
					}
				}else
				if(!data->original_resize){
					try = 10;
					if(f <= ARIAL_FONT){	//gothic simsun gulim arial
						fontsize = CA_FONT_SIZE_TUNED[f][isfontdoubled][i];
						target_size = CA_FONT_HIGHT_TUNED[f][isfontdoubled][i];
					}else{	//文字間隔は合わないが文字サイズを合わせる
						fontsize += CA_FONT_SIZE_FIX[f][i]<<isfontdoubled;
						target_size = fontsize;
					}
				}
/*
				if(!data->original_resize){
					fontsize += CA_FONT_SIZE_FIX[f][i]<<isfontdoubled;	//文字間隔は合わないが文字サイズを合わせる
					try = 10;
				}
				target_size = fontsize;
*/
				if(data->debug)
					fprintf(log,"[main/init]loading CAfont[%s][%d]:%s size:%d index:%d target:%d\n",CA_FONT_NAME[f],i,font_path,fontsize,fixed_font_index,target_size);
				while(try>0){
					font[i] = TTF_OpenFontIndex(font_path,fontsize,fixed_font_index);
					if(font[i] == NULL){
						if(data->debug)
							fprintf(log,"[main/init]failed to load CAfont[%s][%d]:%s size:%d index:%d.\n",CA_FONT_NAME[f],i,font_path,fontsize,fixed_font_index);
						return FALSE;
					}
					TTF_SetFontStyle(font[i],TTF_STYLE_BOLD);
					font_height[i] = TTF_FontHeight(font[i]);
					line_skip[i] = TTF_FontLineSkip(font[i]);
					if(data->use_lineskip_as_fontsize){
						current_size = line_skip[i];
					}else{
						current_size = font_height[i];
					}
					if(current_size==target_size){
						break;
					}
					if(--try<=0){
						if(data->debug)
							fprintf(log,"[main/init]load CAfont try count end.\n");
						break;
					}
					TTF_CloseFont(font[i]);
					if(current_size>target_size){
						fontsize--;
					}else{
						fontsize++;
					}
				}
				if(data->debug){
					fprintf(log,"[main/init]loaded  CAfont[%s][%d]:%s size:%d index:%d.\n",CA_FONT_NAME[f],i,font_path,fontsize,fixed_font_index);
					printFontInfo(log,font,i,CA_FONT_NAME[f]);
				}
			}
			fprintf(log,"CAfont[%s]%s height is DEF=%dpt %dpx(%dpx), BIG=%dpt %dpx(%dpx), SMALL=%dpt %dpx(%dpx)\n",
				CA_FONT_NAME[f],(data->fontsize_fix?" Double scaled":""),
				font_height[CMD_FONT_DEF],line_skip[CMD_FONT_DEF],data->font_pixel_size[CMD_FONT_DEF],
				font_height[CMD_FONT_BIG],line_skip[CMD_FONT_BIG],data->font_pixel_size[CMD_FONT_BIG],
				font_height[CMD_FONT_SMALL],line_skip[CMD_FONT_SMALL],data->font_pixel_size[CMD_FONT_SMALL]
			);
		}
		fputs("[main/init]Initializing Font change Characters,\n",log);
		/*
		i = convUint16(setting->CAfont_change_uc[GOTHIC_FONT],&data->font_change[GOTHIC_FONT]);
		fprintf(log, "[main/init]GOTHIC Font protect %d pairs.\n", i>>1);
		i = convUint16(setting->CAfont_change_uc[SIMSUN_FONT],&data->font_change[SIMSUN_FONT]);
		fprintf(log, "[main/init]SIMSUN Font change %d pairs.\n", i>>1);
		i = convUint16(setting->CAfont_change_uc[GULIM_FONT],&data->font_change[GULIM_FONT]);
		fprintf(log, "[main/init]GULIM Font change %d pairs.\n", i>>1);
		data->font_change[ARIAL_FONT] = NULL;
		i = convUint16(setting->CAfont_change_uc[GEORGIA_FONT],&data->font_change[GEORGIA_FONT]);
		fprintf(log,"[main/init]GEORGIA Font use %d pairs.\n", i>>1);
		i = convUint16(setting->zero_width_uc,&data->zero_width);
		fprintf(log,"[main/init]Zero width char use %d pairs.\n", i>>1);
		data->font_change[UI_GOTHIC_FONT] = NULL;
		i = convUint16(setting->CAfont_change_uc[DEVANAGARI],&data->font_change[DEVANAGARI]);
		fprintf(log,"[main/init]DEVANAGARI Font use %d pairs.\n", i>>1);
		*/
		data->extra_change = NULL;
		if(setting->CAfont_path[EXTRA_FONT]){
			i = convUint16(setting->extra_uc,&data->extra_change);
			fprintf(log,"[main/init]EXTRA Font use %d pairs.\n", i>>1);
		}
		Uint16* u = data->extra_change;
		fprintf(log,"font change(%s)",CA_FONT_NAME[EXTRA_FONT]);
		if(u==NULL){
			fprintf(log," is NULL.\n");
		}else{
			while(*u){
				fprintf(log," %04x-%04x",*u,*(u+1));
				u += 2;
			}
			fputs("\n",log);
		}
		/*
		Uint16* u = data->zero_width;
		if(u!=NULL){
			fprintf(log,"zero width");
			while(*u){
				fprintf(log," %04x-%04x",*u,*(u+1));
				u += 2;
			}
			fputs("\n",log);
		}
		*/
		fputs("[main/init]initialized CA(Comment Art) Feature.\n",log);
	}
	fprintf(log, "[main/init]font width fix ratio:%.0f%% (experimental)\n",(data->font_w_fix_r * 100));
	fprintf(log, "[main/init]font height fix ratio:%.0f%% (experimental)\n",(data->font_h_fix_r * 100));
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

// check and print font info
int printFontInfo(FILE* log, TTF_Font** font,int size,const char* name){
	char *familyname=TTF_FontFaceFamilyName(font[size]);
	if(!familyname)
		familyname = "no family";
	char *stylename=TTF_FontFaceStyleName(font[size]);
	if(!stylename)
		stylename = "no style";
	int outline=TTF_GetFontOutline(font[size]);
	int kerning=TTF_GetFontKerning(font[size]);
	fprintf(log,"[main/init]INFO CAfont[%s][%d] %s %s outline %dpx kerning %s, style",name,size,familyname,stylename,outline,kerning==0?"Off":"On");
	int style;
	style=TTF_GetFontStyle(font[size]);
	if(style==TTF_STYLE_NORMAL)
		fprintf(log," normal");
	else {
		if(style&TTF_STYLE_BOLD)
			fprintf(log," bold");
		if(style&TTF_STYLE_ITALIC)
			fprintf(log," italic");
		if(style&TTF_STYLE_UNDERLINE)
			fprintf(log," underline");
		if(style&TTF_STYLE_STRIKETHROUGH)
			fprintf(log," strikethrough");
	}
	if(TTF_FontFaceIsFixedWidth(font[size]))
		fprintf(log," fixed_width");
	fprintf(log,"\n");
	return TRUE;
}

// extra font (experimental Windows Only)
// expra_path="path index unicodeLow-unicodeHigh"
int extra_font(SETTING* setting, FILE* log){
	if(!setting->extra_path){
		return TRUE;
	}
	const char* fontpath = setting->extra_path;
	fprintf(log,"[main/extra_font]extra path is %s\n",fontpath);
	char* next = strchr(fontpath,' ');
	if(next==NULL){
		fprintf(log,"[main/extra_font]error. separator' ' not found.\n");
		return FALSE;
	}
	char* path = (char*)malloc(next-fontpath+1);
	if(path==NULL){
		fprintf(log,"[main/extra_font]malloc failed.\n");
		return FALSE;
	}
	strncpy(path,fontpath,next-fontpath);
	path[next-fontpath] = '\0';
	int fontindex = MAX(0,atoi(next+1));
	next= strchr(next+1,' ');
	if(next==NULL){
		fprintf(log,"[main/extra_font]range unicode can not parsed:%s.\n",next);
		return FALSE;
	}
	setting->CAfont_path[EXTRA_FONT] = path;
	setting->CAfont_index[EXTRA_FONT] = fontindex;
	setting->extra_uc = next+1;
	return TRUE;
}

/*
 * 映像の変換
 */
int main_process(DATA* data,SDL_Surface* surf,const int now_vpos){
	FILE* log = data->log;
	if(!data->process_first_called){
		data->width_scale = (double)(data->vout_width) / (double)(data->nico_width_now);
		fprintf(log,"[main/process]screen size:%dx%d aspect:%.3f->%.3f scale:%.0f%%.\n",
			surf->w,surf->h,data->nico_width_now==NICO_WIDTH_WIDE?1.7777:1.3333,
			(double)data->vout_width/(double)data->vout_height,data->width_scale*100.0);
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
				if(data->CAfont[f][i]!=NULL){
					fprintf(data->log,"free CAfont[%d][%d]\n",f,i);
					TTF_CloseFont(data->CAfont[f][i]);
				}
			}
			//free(data->font_change[f]);
		}
		free(data->extra_change);
		//free(data->zero_width);
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


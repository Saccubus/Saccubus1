
#include <SDL/SDL.h>
#include <SDL/SDL_ttf.h>
#include <stdio.h>
#include "main.h"
#include "mydef.h"
#include "nicodef.h"
#include "process.h"
#include "unicode/uniutil.h"
#include "april_fool.h"
#include "wakuiro.h"
#include "comment/com_surface.h"
#include "comment/adjustComment.h"
#include "SDL/SDL_rotozoom.h"

int initCommentData(DATA* data, CDATA* cdata, FILE* log, const char* path, int max_slot, int cid, const char* com_type);
int isPathRelative(const char* path);
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

int checkMSPGOTHIC(TTF_Font* font);
int printFontInfo(FILE* log,TTF_Font** pfont,int size,const char* name);
//int extra_font(SETTING* setting, FILE* log);
/*
 * データの初期化
 * ContextInfo ci->DATA data ← SETTING setting
 */
int initData(DATA* data,FILE* log,SETTING* setting){
	int i;
	data->version = setting->version;
	data->data_title = setting->data_title;
	data->show_thumbnail_size = setting->show_thumbnail_size;
	data->typeNicovideoE = setting->typeNicovideoE;
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
	data->aspect_mode = (data->nico_width_now > NICO_WIDTH);
	data->font_w_fix_r = setting->font_w_fix_r;
	data->font_h_fix_r = setting->font_h_fix_r;
	data->original_resize = setting->original_resize;
	data->comment_speed = setting->comment_speed;
	data->comment_duration = setting->comment_duration;
	data->ahead_vpos = setting->comment_ahead_vpos;
	data->fixmode = setting->fixmode;
	data->enableCA = setting->enableCA;
	data->use_lineskip_as_fontsize = setting->use_lineskip_as_fontsize;
	data->debug = setting->debug;
	data->defcolor = CMD_COLOR_WHITE;
	data->deflocation = CMD_LOC_NAKA;
	data->defsize = CMD_FONT_MEDIUM;
	data->opaque_rate = -1.0;
	if(data->opaque_comment){
		data->opaque_rate = 1.0;
		if(setting->opaque_rate != NULL){
			float opaq = (float)strtod(setting->opaque_rate,NULL);
			if(opaq>=0.0f || opaq<=1.0f){
				data->opaque_rate = opaq;
			}
		}
	}
	//フレームレート
//	data->limit_height = NICO_HEIGHT;
	data->q_player = setting->q_player;
	data->is_live = setting->is_live;
	data->comment_vpos_shift = 0;
	if(setting->comment_shift !=NULL){
		double vpos_shift = (double)strtod(setting->comment_shift,NULL);
		data->comment_vpos_shift = (int)(vpos_shift * VPOS_FACTOR);
	}
	data->min_vpos = 0;
	if(setting->min_vpos_sec != 0.0){
		data->min_vpos = (int)(setting->min_vpos_sec * VPOS_FACTOR);
	}
	if(setting->comment_erase !=NULL){
		int erase_type = (int)strtol(setting->comment_erase,NULL,10);
		data->comment_erase_type = erase_type;
	}
	data->comment_off = FALSE;
	if(setting->comment_off !=NULL){
		// [方向][文字サイズ指定]数値[パーセント指定][nakaコメントフラグ]
		// 方向:上から+又はスペース,下から-
		// 文字サイズ指定:b=big m=medium s=small
		// パーセント指定:%動画高さに対する相対値(100分率)
		// nakaコメントフラグ:n
		data->comment_off = TRUE;
		const char* ptr = setting->comment_off;
		char* endptr = NULL;
		int val = 0;
		int sign = 1;
		int kind = 0; // 0:pixel指定, 1:big, 2:small, 3:medium, 4:パーセント指定
		int naka = FALSE;
		char c = *ptr++;
		if(c=='+'||c==' ') sign = 1;
		else if(c=='-') sign = -1;
		else ptr--;
		c = *ptr++;
		if(c=='b'||c=='B') kind = 1;
		else if(c=='s'||c=='S') kind = 2;
		else if(c=='m'||c=='M') kind = 3;
		else ptr--;
		val = (int)strtol(ptr, &endptr, 10);
		ptr = endptr;
		if(ptr==NULL){
			// val ok
		}else{
			c = *ptr++;
			if(c=='%') kind = 4;
			else ptr--;
			c = *ptr++;
			if(c=='n'||c=='N'){
				naka = TRUE;
			}
		}
		data->comment_off_y = val;
		data->comment_off_sign = sign;
		data->comment_off_kind = kind;
		data->comment_off_naka = naka;
	}
	data->layerctrl = setting->layerctrl;
	data->html5comment = setting->html5comment;
	data->comment_resize_adjust = setting->comment_resize_adjust;
	data->comment_linefeed_ratio = 0.0f;
	data->comment_lf_control = 0;
	if(setting->comment_linefeed !=NULL){
		char *endptr = NULL;
		float val = 0.0f;
		val  = (float)strtod(setting->comment_linefeed,&endptr);
		if(val!=0.0f || endptr==NULL || setting->comment_linefeed[0]=='0'){
			data->comment_linefeed_ratio  = (val / 100.0) + 1.0;
			int lfc = 2;	// new ver=2
			if(endptr!=NULL){
				if(*endptr=='%') endptr++;
				if(*endptr==',' || *endptr==';' || *endptr==' ') endptr++;
				lfc = (int)strtol(endptr,NULL,10);
				if(lfc <= 0) lfc = 2;
			}
			data->comment_lf_control = lfc;
			fprintf(log,"[main/init]linefeed control: mode=%d, ratio=%-6.3f\n"
				,data->comment_lf_control, data->comment_linefeed_ratio);
		}
	}
	data->vfspeedflag = 0;
	if(setting->vfspeedrate !=NULL){
		char *endptr = NULL;
		float val = 1.0;
		val = (float)strtod(setting->vfspeedrate,&endptr);
		if(val!=0.0f){
			data->vfspeedrate = val;
			data->vfspeedflag = 1;
			if(endptr!=NULL){
				if(*endptr=='v'||*endptr=='V'){
					// v付きは出力速度も倍率で変更
					data->vfspeedflag = 2;
				}
			}
			fprintf(log,"[main/init]vfspeedrate: mode=%d, rate=%-6.3f\n"
				,data->vfspeedflag, data->vfspeedrate);
		}
	}
	data->pad_w = 0;
	data->pad_h = 0;
	int outw = setting->nico_width_now;
	int outh = outw==NICO_WIDTH_WIDE ? NICO_HEIGHT_WIDE : NICO_HEIGHT;
	int w;
	int h;
	int outx = 0;
	int outy = 0;
	if(setting->input_size!=NULL){
		//input size
		sscanf(setting->input_size,"%d:%d",&outw,&outh);
	}
	if(setting->set_size!=NULL){
		//-s "width"x"height" in ffmpeg OUT OPTION
		sscanf(setting->set_size,"%d:%d",&outw,&outh);
	}
	//outw outh is video output
	if(setting->out_size!=NULL){
		//-vfilters outs=width:hieight
		//sscanf(setting->out_size,"%d:%d",&coutw,&couth);
	}else if(setting->pad_option!=NULL){
		//-vfilters pad=w:h:x:y
		sscanf(setting->pad_option,"%d:%d:%d:%d",&w,&h,&outx,&outy);
		data->pad_w = w>0 && w!=outw ? w : 0;
		data->pad_h = h>0 && h!=outh ? h : 0;
	}
	data->vout_width = outw;
	data->vout_height = outh;
	data->vout_x = outx;
	data->vout_y = outy;
	fprintf(log,"[main/init]output: %dx%d @(%d,%d)of %d:%d\n",
		data->vout_width,data->vout_height,data->vout_x,data->vout_y,data->pad_w,data->pad_h);
	data->extra_mode = setting->extra_mode;
	data->drawframe = data->extra_mode!=NULL && strstr(data->extra_mode,"-frame");
	//カスタム影設定
	setting_shadow(setting->extra_mode, data);
	if(setting->april_fool != NULL){
		set_aprilfool(setting,data);
	}
	//黄枠の色を設定
	data->wakuiro_dat = NULL;
	if(setting->wakuiro != NULL){
		set_wakuiro(setting->wakuiro,data);
		if(data->wakuiro_dat!=NULL)
			data->drawframe = TRUE;
	}
	// 弾幕モードの高さの設定　16:9でオリジナルリサイズでない場合は上下にはみ出す
	// Qwatch,html5commentのときは、はみ出さない
	//comment area height is independent from video height
	if(data->html5comment){
		if(data->nico_width_now==NICO_WIDTH_WIDE){
			// wide mode
			data->nico_height = NICO_HEIGHT_WIDE;
		}else{
			// 4:3 mode
			data->nico_height = NICO_HEIGHT_WIDE;
			data->nico_width_now = HTML5_WIDTH_NARROW;
		}
	}else
		data->nico_height = NICO_HEIGHT;
	data->width_scale = (double)data->vout_width / (double)data->nico_width_now;
	double height_scale = (double)data->vout_height / (double)data->nico_height;
	int comment_height = lround(data->width_scale * data->nico_height);
	int limit_height = data->vout_height;
	if(data->q_player){
		// Qwatch
		if(data->vout_height >= comment_height){
			//data->width_scale = width_scale;
			//limit_height = data->vout_height+1;
			// scale縮小 limitそのまま
		}else if(data->pad_h >= comment_height){
			//padがあればpad以下ならlimit_heightだけ変更
			// scale そのまま limit 変更
			limit_height = comment_height;
			fprintf(log,"[main/process]limit_height %d.\n",limit_height);
		}else{
			//pad以上かつ動画の高さ以上
			data->width_scale = MAX(data->pad_h,data->vout_height) / (double)data->nico_height;
			fprintf(log,"[main/process]width scale is set-again by height. %.3f%%\n", data->width_scale * 100.0);
			limit_height = lround(data->width_scale * data->nico_height);
			fprintf(log,"[main/process]limit_height %d.\n",limit_height);
		}
	} else if(data->original_resize){
		// 原宿 旧リサイズ
		// scale そのまま limit そのまま(起点ははみ出さない、終点ははみだすかも)
		//data->width_scale = width_scale;
		//data->limit_height = data->vout_height+1;
	} else if(data->pad_h==0){
		// 原宿 padなし
		if(data->vout_height < comment_height){
			// scale縮小 limitそのまま
			data->width_scale = height_scale;
			fprintf(log,"[main/process]width scale is set-again by height. %.3f%%\n", data->width_scale * 100.0);
		}else{
			// scale そのまま limit そのまま
			//data->width_scale = width_scale;
		}
		//limit_height = data->vout_height+1;
	} else {
		// 原宿 padあり
		// scale そのまま limit拡大
		//data->width_scale = width_scale;
		limit_height = comment_height;
		fprintf(log,"[main/process]limit_height %d.\n",limit_height);
	}
	if(!data->html5comment)
		limit_height += lround(((double)limit_height / (double)NICO_HEIGHT));
		//コメントの高さは385=384+1 下にはみ出す
	data->limit_height = limit_height;
	fprintf(data->log,"[chat_slot/add]video height %d  limit height %d\n",data->vout_height,data->limit_height);
	int y_min = (data->vout_height>>1) - (limit_height>>1);
	int y_max = y_min + limit_height;
	data->y_min = y_min;
	data->y_max = y_max;
	fprintf(data->log,"[chat_slot/add]height min %d  max %d\n",y_min,y_max);
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
	int html5 = data->html5comment;
	if(setting->debug && strstr(setting->extra_mode,"-point")!=NULL){
		pointsizemode = TRUE;
	}
	for (i=0;i<CMD_FONT_MAX;++i) {
		if(html5)
			data->font_pixel_size[i] = adjustHeight(1,i,FALSE,isfontdoubled,TRUE);
		else
			data->font_pixel_size[i] = FONT_PIXEL_SIZE[i]<<isfontdoubled;
	}
	fprintf(log,"[main/init]Font height is DEF=%dpx, MEDIUM=%dpx, BIG=%dpx, SMALL=%dpx\n",
		data->font_pixel_size[CMD_FONT_DEF],data->font_pixel_size[CMD_FONT_MEDIUM],
		data->font_pixel_size[CMD_FONT_BIG],data->font_pixel_size[CMD_FONT_SMALL]
	);
	int ttf_style = TTF_STYLE_BOLD;
	if(strstr(setting->extra_mode,"-normal")!=NULL
		||data->shadow_data.fontnormal)
		ttf_style = TTF_STYLE_NORMAL;
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
			if(html5){
				//調整
			}else{
				fontsize -= 1;
			}

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
			TTF_SetFontStyle(font[i],ttf_style);
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
		fprintf(log,"height is MEDIUM=%d %dpx(%dpx), BIG=%d %dpx(%dpx), SMALL=%d %dpx(%dpx)\n",
			data->fixed_font_height[CMD_FONT_MEDIUM],line_skip[CMD_FONT_MEDIUM],data->font_pixel_size[CMD_FONT_MEDIUM],
			data->fixed_font_height[CMD_FONT_BIG],line_skip[CMD_FONT_BIG],data->font_pixel_size[CMD_FONT_BIG],
			data->fixed_font_height[CMD_FONT_SMALL],line_skip[CMD_FONT_SMALL],data->font_pixel_size[CMD_FONT_SMALL]
		);
	}

	int f;
	char font_file_path[128];
	if(data->enableCA){
		fputs("[main/init]initializing CA(Comment Art) Font...\n",log);
		// CAフォント
		for(f=0;f<CA_FONT_MAX;f++){
			for(i=0;i<CMD_FONT_MAX;i++){
				data->CAfont[f][i] = NULL;
			}
		}
//		if(!extra_font((SETTING*)setting,log)){
//			return FALSE;
//		}
		int font_height[CMD_FONT_MAX];
		int target_size;
		int current_size;
		int try = 1;
		int direction = 0;
		for(f = 0;f<CA_FONT_PATH_MAX;f++){
			font = &data->CAfont[f][0];		//pointer2 set
			font_path = setting->CAfont_path[f];
			if(font_path==NULL){
				if(f==EXTRA_FONT || f==ARIALUNI_FONT){
					continue;
				}
				if(f <= ARIAL_FONT){
					fprintf(log,"[main/init]error. CA font path[%d:%s] is NULL\n",f,getfontname(f));
					return FALSE;
				}
				continue;
			}
			if(isPathRelative(font_path)){
				strcpy(font_file_path,fontdir);
				strcat(font_file_path,font_path);
				font_path = font_file_path;
			}
			fixed_font_index = setting->CAfont_index[f];
			for(i=0;i<CMD_FONT_MAX;i++){
				fontsize = COMMENT_FONT_SIZE[i];
				if(html5){
					//調整
				}else{
					//flash
					//実験からSDL指定値はマイナスするとニコニコ動画と文字幅が合う?
					fontsize -= 1;
				}
				/* ターゲットを拡大した時にフォントが滑らかにするため２倍化する。 */
				fontsize <<= isfontdoubled;
				try = data->original_resize ? 100 : 1;
				target_size = fontsize;
				if(html5 && !data->original_resize){
					// fontsize, target_sizeの調整
					try = 1;
					target_size = fontsize;
					if(f <= ARIAL_FONT){	//gothic simsun gulim arial
						fontsize = HTML5_FONT_WIDTH_TUNED[f][isfontdoubled][i];
						target_size = HTML5_FONT_HIGHT_TUNED[f][isfontdoubled][i];
					}else if(f <= GURMUKHI_FONT){	//文字間隔は合わないが文字サイズを合わせる
						fontsize += CA_FONT_SIZE_FIX[f][i]<<isfontdoubled;
						target_size = fontsize;
					}else{
						target_size = fontsize;
					}
				}else
				if(pointsizemode){
					fontsize = COMMENT_POINT_SIZE[i] << isfontdoubled;
					target_size = fontsize;
				}else
				if(!data->original_resize){	//not point and CA mode
					if(f <= ARIAL_FONT){	//gothic simsun gulim arial
						fontsize = CA_FONT_SIZE_TUNED[f][isfontdoubled][i];
						target_size = CA_FONT_HIGHT_TUNED[f][isfontdoubled][i];
					}else if(f <= GURMUKHI_FONT){	//文字間隔は合わないが文字サイズを合わせる
						fontsize += CA_FONT_SIZE_FIX[f][i]<<isfontdoubled;
						target_size = fontsize;
					}else{
						//fontsize += 2<<isfontdoubled;
						target_size = fontsize;
					}
				}
				if(data->debug)
					fprintf(log,"[main/init]loading CAfont[%s][%d]:%s size:%d index:%d target:%d\n",getfontname(f),i,font_path,fontsize,fixed_font_index,target_size);
				direction = 0;
				while(try>0){
					font[i] = TTF_OpenFontIndex(font_path,fontsize,fixed_font_index);
					if(f==0 && fixed_font_index!=0 && font[i]!=NULL && !checkMSPGOTHIC(font[i])){
						fputs("[main/init]Not MS PGothic, retry\n",log);
						fixed_font_index = fixed_font_index==1 ? 2:1;
						font[i] = TTF_OpenFontIndex(font_path,fontsize,fixed_font_index);
					}
					if(fixed_font_index!=0 && font[i] == NULL){
						//try index 0
						fixed_font_index = 0;
						font[i] = TTF_OpenFontIndex(font_path,fontsize,fixed_font_index);
					}
					if(font[i] == NULL){
						if(data->debug)
							fprintf(log,"[main/init]failed to load CAfont[%s][%d]:%s size:%d index:%d.\n",getfontname(f),i,font_path,fontsize,fixed_font_index);
						return FALSE;
					}
					int tstyle = ttf_style;
					if(html5){
						if(f==GOTHIC_FONT || f>=ARIAL_FONT)
							tstyle = ttf_style;
						else
							tstyle = TTF_STYLE_NORMAL;
					}
					TTF_SetFontStyle(font[i],tstyle);
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
						direction++;
						fontsize--;
					}else{
						direction--;
						fontsize++;
					}
					if(direction==0)
						break;
				}
				if(data->debug){
					fprintf(log,"[main/init]loaded  CAfont[%s][%d]:%s size:%d index:%d.\n",
						getfontname(f),i,font_path,current_size,fixed_font_index);
					printFontInfo(log,font,i,getfontname(f));
				}
			}
			fprintf(log,"CAfont[%s]%s height is MEDIUM=%dpx %dpx(%dpx), BIG=%dpx %dpx(%dpx), SMALL=%dpx %dpx(%dpx)\n",
				getfontname(f),(isfontdoubled?" Double scaled":""),
				font_height[CMD_FONT_MEDIUM],line_skip[CMD_FONT_MEDIUM],data->font_pixel_size[CMD_FONT_MEDIUM],
				font_height[CMD_FONT_BIG],line_skip[CMD_FONT_BIG],data->font_pixel_size[CMD_FONT_BIG],
				font_height[CMD_FONT_SMALL],line_skip[CMD_FONT_SMALL],data->font_pixel_size[CMD_FONT_SMALL]
			);
		}
		fputs("[main/init]Initializing Font change Characters.\n",log);
		data->extra_change = NULL;
		if(setting->CAfont_path[EXTRA_FONT]!=NULL){
			i = convUint16(setting->extra_uc,&data->extra_change);
			fprintf(log,"[main/init]EXTRA Font use %d pairs.\n", i>>1);
		}
		Uint16* u = data->extra_change;
		fprintf(log,"font change(%s)",getfontname(EXTRA_FONT));
		if(u==NULL){
			fprintf(log," is NULL.\n");
		}else{
			while(*u){
				fprintf(log," %04x-%04x",*u,*(u+1));
				u += 2;
			}
			fputs("\n",log);
		}
		if(setting->fontlist!=NULL)
			free(setting->fontlist);
		fputs("[main/init]initialized CA(Comment Art) Feature.\n",log);
	}
	//エラーフォント
	fprintf(log, "[main/init]initialize ErrFont.\n");
	(void)getErrFont(data);
	//
	fprintf(log, "[main/init]font width fix ratio:%.0f%% (experimental)\n",(data->font_w_fix_r * 100));
	fprintf(log, "[main/init]font height fix ratio:%.0f%% (experimental)\n",(data->font_h_fix_r * 100));
	fflush(log);
	/*
	 * ユーザコメント
	 */
	if (!initCommentData(data, &data->user, log,
			setting->data_user_path, setting->user_slot_max, CID_USER, COM_TYPE[CID_USER])){
		return FALSE;
	}
	/*
	 * オーナコメント
	 */
	if (!initCommentData(data, &data->owner, log,
			setting->data_owner_path, setting->owner_slot_max, CID_OWNER, COM_TYPE[CID_OWNER])){
		return FALSE;
	}
	/*
	 * オプショナルコメント
	 */
	if (!initCommentData(data, &data->optional, log,
			setting->data_optional_path, setting->optional_slot_max, CID_OPTIONAL, COM_TYPE[CID_OPTIONAL])){
		return FALSE;
	}

	//エラー用フォント
	data->ErrFont = NULL;
	//終わり。
	fputs("[main/init]initialized context.\n",log);
	return TRUE;
}
/*
 * コメントデータの初期化
 * DATA data->user owner optional
 */
int initCommentData(DATA* data, CDATA* cdata, FILE* log, const char* path, int max_slot, int cid, const char* dummy_com_type){
	const char* com_type = COM_TYPE[cid];
	int tl = data->comment_speed<0? -1:1;
	if (cdata->enable_comment){
		fprintf(log,"[main/init]%s comment is enabled.\n",com_type);
		//コメントデータ
		if (initChat(log, &cdata->chat, path, &cdata->slot, data->video_length, data->nico_width_now,
				cid, com_type, tl, data->is_live, data->comment_vpos_shift, data->ahead_vpos, data->min_vpos)){
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
// check MS P GOTHIC
int checkMSPGOTHIC(TTF_Font* font){
	char *familyname=TTF_FontFaceFamilyName(font);
	if(strstr(familyname,"MS PGothic")){
		return TRUE;
	}
	return FALSE;
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

/*
 *
 */
int isPathRelative(const char* path){
	char c0 = path[0];
	char c1 = path[1];
	if(c0 == '/'||c0=='\\')
		return FALSE;
	c0 = toupper(c0);
	if(c1 == ':' && ('A'<=c0 && c0<='Z') && path[2] == '\\')
		return FALSE;
	return TRUE;
}
/*
 * 映像の変換
 */
int main_process(DATA* data,SDL_Surface* surf,int now_vpos){
	FILE* log = data->log;
	double zoomy;
	int zoomed_width = 256;
	int zoomed_height = 144;
	zoomy = (double)zoomed_height / (double)data->vout_height;
	zoomed_width = (int)(data->vout_width * zoomy);

	if(!data->process_first_called){
		// 弾幕モードの高さの設定　16:9でオリジナルリサイズでない場合は上下にはみ出す
		// Qwatch, html5のときは、はみ出さない
		//comment area height is independent from video height
		if(surf->w!=data->vout_width||surf->h!=data->vout_height){
			fprintf(log,"[main/process]screen size != video size\n");
			fprintf(log,"[main/process]this may be warning Ver.%s\n",data->version);
		}
		fprintf(log,"[main/process]screen %dx%d, video %dx%d, comment %.0fx%.0f\n",
			surf->w,surf->h,data->vout_width,data->vout_height,
			data->width_scale*data->nico_width_now,data->width_scale*data->nico_height);
		data->aspect_rate = (float)data->vout_width/(float)data->vout_height;
		fprintf(log,"[main/process]screen aspect:%.3f->%.3f scale:%.2f%%.\n",
			(float)surf->w / (float)surf->h,
			data->aspect_rate,data->width_scale*100.0);
//		fprintf(log,"[main/process]framerate:%.2f\n",data->dts_rate);
		if(data->vfspeedflag!=0){
			fprintf(log,"[main/process]vfspeedrate:%.2f\n",data->vfspeedrate);
		}
		fflush(log);
	}
	/*フィルタをかける*/
	if(data->vfspeedflag > 0){
		now_vpos = (double)now_vpos * data->vfspeedrate;
	}
	if(process(data,surf,now_vpos)){
	}
	fflush(log);
	/*変換した画像を見せる。*/
	if(data->show_video){
		SDL_Surface* zoomed = null;
		SDL_Event event;
		if(!data->process_first_called){
			const char *title;
			SDL_Surface *icon;
			/* Set the icon -- this must be done before the first mode set */
			icon = SDL_LoadBMP("bin/icon32.bmp");
			if ( icon != NULL ) {
				SDL_WM_SetIcon(icon, NULL);
			}
			title = data->data_title;
			fprintf(log,"[main/process]Window Title: %s\n", title);
			SDL_WM_SetCaption(title, NULL);
			/* See if it's really set */
			title = NULL;
			SDL_WM_GetCaption((char **)&title, (char **)NULL);
			if ( title!=NULL )
				fprintf(log,"[main/process]Title was set to: %s\n", title);
			else
				fprintf(log,"[main/process]No window title was set!\n");

			if(data->show_thumbnail_size)
				data->screen = SDL_SetVideoMode(zoomed_width, zoomed_height, 24, SDL_HWSURFACE | SDL_DOUBLEBUF);
			else
				data->screen = SDL_SetVideoMode(surf->w, surf->h, 24, SDL_HWSURFACE | SDL_DOUBLEBUF);
			if(data->screen == NULL){
				fputs("[main/process]failed to initialize screen.\n",log);
				fflush(log);
				return FALSE;
			}
		}
		//映像表示
		if(data->show_thumbnail_size){
			zoomed = zoomSurface(surf,zoomy,zoomy,SMOOTHING_OFF);
			if(zoomed!=null){
				SDL_BlitSurface(zoomed,NULL,data->screen,NULL);
				SDL_Flip(data->screen);
				while(SDL_PollEvent(&event)){}
				SDL_FreeSurface(zoomed);
			}
		}else{
			SDL_BlitSurface(surf,NULL,data->screen,NULL);
			SDL_Flip(data->screen);
			while(SDL_PollEvent(&event)){}
		}
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
	fprintf(data->log,"All Chat closed.\n");
	//エラー用フォント開放
	closeErrFont(data);
	fprintf(data->log,"ErrFont closed.\n");
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


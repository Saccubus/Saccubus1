/*フレームフックの相手をするため専用*/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <SDL/SDL.h>
#include "common/framehook_ext_old.h"
#include "framehook.h"
#include "main.h"
#include "mydef.h"
#include "nicodef.h"
#include "util.h"
#include "unicode/uniutil.h"

typedef struct ContextInfo{
	FILE* log;
	DATA data;
} ContextInfo;

/*
 * 必要な関数ひとつめ。最初に呼ばれるよ！
 *
 */
int init_setting(FILE*log,SETTING* setting,int argc, char *argv[], char* version, int typeNicovideoE);
FILE* changelog(FILE* log,SETTING* setting);

__declspec(dllexport) int ExtConfigure(void **ctxp, void* dummy, int argc, char *argv[]){
	int typeNicovideoE = TRUE;
	if(0 < (unsigned)dummy && (unsigned)dummy < 0x0400){
		argv = (char **)argc;
		argc = (int)dummy;
		typeNicovideoE = FALSE;
	}
	int i;
	//ログ
	FILE* log = fopen("[log]vhext.txt", "w+");
	char linebuf[128];
	char *ver="1.67.7.01b";
	snprintf(linebuf,63,"%s\nBuild %s %s\n",ver,__DATE__,__TIME__);
	if(log == NULL){
		puts(linebuf);
		puts("[framehook/init]failed to open logfile.\n");
		fflush(log);
		return -1;
	}else{
		fputs(linebuf,log);
		fputs("[framehook/init]initializing..\n",log);
		fflush(log);
	}
	//必要な設定があるかの確認
	fprintf(log,"[framehook/init]called with argc = %d\n",argc);
	fflush(log);
	for(i=0;i<argc;i++){
		fprintf(log,"[framehook/init]arg[%2d] = %s\n",i,argv[i]);
		fflush(log);
	}
	//セッティング取得。
	SETTING setting;
	if(init_setting(log,&setting,argc,argv,ver,typeNicovideoE)){
		fputs("[framehook/init]initialized settings.\n",log);
		fflush(log);
	}else{
		fputs("[framehook/init]failed to initialize settings.\n",log);
		fflush(log);
		return -2;
	}
	log = changelog(log, &setting);
	//ライブラリなどの初期化
	if(init(log)){
		fputs("[framehook/init]initialized libs.\n",log);
		fflush(log);
	}else{
		fputs("[framehook/init]failed to initialize libs.\n",log);
		fflush(log);
		return -3;
	}
	/*コンテキストの設定*/
	*ctxp = malloc(sizeof(ContextInfo));
	if(*ctxp == NULL){
		fputs("[framehook/init]failed to malloc for context.\n",log);
		fflush(log);
		return -5;
	}
	ContextInfo* ci = (ContextInfo*)*ctxp;
	memset(ci, (int)NULL, sizeof(ContextInfo));
	ci->log = log;
	fflush(log);
	if(initData(&ci->data,log,&setting)){
		fputs("[framehook/init]initialized context.\n",log);
		fputs("[framehook/init]initialized.\n",log);
		fflush(log);
		return 0;
	}else{
		fputs("[framehook/init]failed to initialize context.\n",log);
		fflush(log);
		return -4;
	}
}
/*
 * 内部でのみ呼ばれる。
 */

/*
	FILE* log		:ログファイル opened as "w"
	toolbox* tbox	:ffmpeg interface?
	SETTING* setting:設定データ構造 出力
	int argc		: argv[] size
	argv[0]:プログラム　（残しておくけどargvの説明は古い）
	argv[1]:vhook
	argv[2]:フォント
	argv[3]:フォントインデックス
	argv[4]:一画面
	argv[5]:影の種類
	以降オプション
	--enable-show-video：描画中に動画を見せる。
	--enable-fontsize-fix：フォントサイズを自動で調整する。
	--nico-width-wide : ワイドプレーヤー16:9対応
	--font-height-fix-ratio:%d ： フォント高さ自動変更の倍率（％）+ int
	--disable-original-resize : さきゅばす独自リサイズを無効にする（実験的）
	--comment-speed: コメント速度を指定する場合≠0
	--debug-print : デバッグ出力を有効にする
*/
int extra_font(SETTING* setting,FILE* log);
int parseFontList(SETTING* setting,FILE* log);

int init_setting(FILE*log,SETTING* setting,int argc, char *argv[], char* version, int typeNicovideoE){
	/* TOOLBOXのバージョンチェック */
	fprintf(log,"[framehook/init]type NicovideoE? %s.\n", typeNicovideoE ? "yes":"no");
	setting->typeNicovideoE = typeNicovideoE;
	fprintf(log,"[framehook/init]TOOLBOX version is DELETED.\n");
	fflush(log);
	/*videoの長さ*/
	setting->video_length = 0; //(tbox->video_length * VPOS_FACTOR);
	setting->version = version;	// 1.60
	/*以降オプション*/

	//コメントを見せるか否か？
	setting->enable_user_comment = FALSE;
	setting->enable_owner_comment = FALSE;
	setting->enable_optional_comment = FALSE;
	setting->data_title = NULL;
	setting->show_thumbnail_size = FALSE;
	setting->data_user_path = NULL;
	setting->data_owner_path = NULL;
	setting->data_optional_path = NULL;
	//一般的な設定
	setting->font_path = NULL;
	setting->font_index = 0;
	setting->user_slot_max = 30;	// 40 ?
	setting->optional_slot_max = 30;
	setting->owner_slot_max = 30;	// infinite ?
	setting->shadow_kind = 1;//デフォルトはニコニコ動画風
	setting->show_video = FALSE;
	setting->fontsize_fix=FALSE;
	setting->opaque_comment=FALSE;
	setting->nico_width_now=NICO_WIDTH;	//デフォルトは旧プレイヤー幅
	setting->optional_trunslucent=FALSE;	//デフォルトは半透明にしない
	setting->font_w_fix_r = 1.0f;	//デフォルトは従来通り（最終調整で合わせること）
	setting->font_h_fix_r = 1.0f;	//デフォルトは従来通り（最終調整で合わせること）
	setting->original_resize = TRUE;	//デフォルトは有効（実験的に無効にする選択を行う）
	setting->comment_speed = 0;
	setting->comment_duration = 0.0f;
	setting->comment_ahead_vpos = TEXT_AHEAD_SEC;
	setting->fixmode = FALSE;
	setting->enableCA = FALSE;
	setting->debug = FALSE;
	setting->use_lineskip_as_fontsize = FALSE;	//デフォルトは無効 FonrsizeにLineskipを合わせる（実験的）
	setting->extra_mode = "";
	setting->input_size = NULL;
	setting->set_size = NULL;
	setting->pad_option = NULL;
	setting->out_size = NULL;
	setting->fontdir = "";
	setting->april_fool = NULL;
	setting->wakuiro = NULL;
	setting->opaque_rate = NULL;
	setting->is_live = FALSE;
	setting->comment_shift = NULL;
	setting->comment_off = NULL;
	setting->comment_linefeed = NULL;
	setting->vfspeedrate = NULL;
	setting->layerctrl = FALSE;
	setting->comment_resize_adjust = 1.0;
	setting->html5comment = FALSE;
	setting->min_vpos_sec = -4.0;
	setting->q_player = FALSE;
	// CA用フォント
	//  MS UI GOTHIC は msgothic.ttc の index=2
	int f;
	for(f=0;f<CA_FONT_PATH_MAX;f++){
		setting->CAfont_path[f] = NULL;
		setting->CAfont_index[f] = 0;
	}
	setting->fontdir = NULL;
	// 実験的追加フォント
	setting->extra_path = NULL;
	//setting->extra_uc = NULL;
	//setting->extra_fontindex = 0;
	//フォントリスト
	setting->fontlist = NULL;

	int i;
	char* arg;
	for(i=0;i<argc;i++){
		arg = argv[i];
		if(setting->data_title==NULL && strncmp(FRAMEHOOK_OPT_TITLE,arg,FRAMEHOOK_OPT_TITLE_LEN) == 0){
			setting->data_title = arg+FRAMEHOOK_OPT_TITLE_LEN;;
			fprintf(log,"[framehook/init]data title:%s\n",setting->data_title);
			fflush(log);
		}else if(!setting->data_user_path && strncmp(FRAMEHOOK_OPT_DATA_USER,arg,FRAMEHOOK_OPT_DATA_USER_LEN) == 0){
			char* data_user = arg+FRAMEHOOK_OPT_DATA_USER_LEN;
			setting->data_user_path = data_user;
			setting->enable_user_comment = TRUE;
			fprintf(log,"[framehook/init]User Comment data path:%s\n",setting->data_user_path);
			fflush(log);
		}else if(!setting->data_owner_path && strncmp(FRAMEHOOK_OPT_DATA_OWNER,arg,FRAMEHOOK_OPT_DATA_OWNER_LEN) == 0){
			char* data_owner = arg+FRAMEHOOK_OPT_DATA_OWNER_LEN;
			setting->data_owner_path = data_owner;
			setting->enable_owner_comment = TRUE;
			fprintf(log,"[framehook/init]Owner Comment data path:%s\n",setting->data_owner_path);
			fflush(log);
		} else if(!setting->data_optional_path && strncmp(FRAMEHOOK_OPT_DATA_OPTIONAL,arg,FRAMEHOOK_OPT_DATA_OPTIONAL_LEN) == 0){
			char* data_optional = arg+FRAMEHOOK_OPT_DATA_OPTIONAL_LEN;
			setting->data_optional_path = data_optional;
			setting->enable_optional_comment = TRUE;
			fprintf(log,"[framehook/init]Optional Comment data path:%s\n",setting->data_optional_path);
			fflush(log);
		}else if(!setting->font_path && strncmp(FRAMEHOOK_OPT_FONT,arg,FRAMEHOOK_OPT_FONT_LEN) == 0){
			char* font = arg+FRAMEHOOK_OPT_FONT_LEN;
			setting->font_path = font;
			fprintf(log,"[framehook/init]Font path:%s\n",setting->font_path);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_FONTINDEX,arg,FRAMEHOOK_OPT_FONTINDEX_LEN) == 0){
			setting->font_index = MAX(0,atoi(arg+FRAMEHOOK_OPT_FONTINDEX_LEN));
			fprintf(log,"[framehook/init]font index:%d\n",setting->font_index);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_SHADOW,arg,FRAMEHOOK_OPT_SHADOW_LEN) == 0){
			setting->shadow_kind = MAX(0,atoi(arg+FRAMEHOOK_OPT_SHADOW_LEN));
			fprintf(log,"[framehook/init]shadow kind:%d\n",setting->shadow_kind);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_SHOW_USER,arg,FRAMEHOOK_OPT_SHOW_USER_LEN) == 0){
			setting->user_slot_max = MAX(0,atoi(arg+FRAMEHOOK_OPT_SHOW_USER_LEN));
			fprintf(log,"[framehook/init]User Comments on screen:%d\n",setting->user_slot_max);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_SHOW_OWNER,arg,FRAMEHOOK_OPT_SHOW_OWNER_LEN) == 0){
			setting->owner_slot_max = MAX(0,atoi(arg+FRAMEHOOK_OPT_SHOW_OWNER_LEN));
			fprintf(log,"[framehook/init]Owner Comments on screen:%d\n",setting->owner_slot_max);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_SHOW_OPTIONAL,arg,FRAMEHOOK_OPT_SHOW_OPTIONAL_LEN) == 0) {
			setting->optional_slot_max = MAX(0,atoi(arg+FRAMEHOOK_OPT_SHOW_OPTIONAL_LEN));
			fprintf(log,"[framehook/init]Optional Comment on screen: %d\n", setting->optional_slot_max);
			fflush(log);
		} else if(!setting->show_video && strcmp(arg,"--enable-show-video") == 0){
			fputs("[framehook/init]show video while converting.\n",log);
			fflush(log);
			setting->show_video=TRUE;
		}else if(!setting->show_thumbnail_size && strcmp(arg,"--show-thumbnail-size") == 0){
			fputs("[framehook/init]Show thumbnail size.\n",log);
			fflush(log);
			setting->show_thumbnail_size = TRUE;
		}else if(!setting->fontsize_fix && strcmp(arg,"--enable-fix-font-size") == 0){
			fputs("[framehook/init]fix font size automatically.\n",log);
			fflush(log);
			setting->fontsize_fix=TRUE;
		}else if(!setting->opaque_comment && strcmp(arg,"--enable-opaque-comment") == 0){
			fputs("[framehook/init]enable opaque comment.\n",log);
			fflush(log);
			setting->opaque_comment=TRUE;
		}else if (!setting->optional_trunslucent && strcmp(arg,"--optional-translucent")==0) {
			fputs("[framehook/init]optonal comment translucent.\n", log);
			fflush(log);
			setting->optional_trunslucent=TRUE;
		}else if(strcmp(arg,"--nico-width-wide")==0){
			fputs("[framehook/init]use wide player.\n",log);
			fflush(log);
			setting->nico_width_now = NICO_WIDTH_WIDE;
		}else if(strcmp(arg,"--enable-Qwatch")==0){
			fputs("[framehook/init]use Qwatch player.\n",log);
			fflush(log);
			setting->q_player = TRUE;
		}else if(setting->video_length <= 0 && strncmp(FRAMEHOOK_OPT_VIDEO_LENGTH,arg,FRAMEHOOK_OPT_VIDEO_LENGTH_LEN) == 0){
			setting->video_length = MAX(0,atoi(arg+FRAMEHOOK_OPT_VIDEO_LENGTH_LEN)) * VPOS_FACTOR;
			fprintf(log,"[framehook/init]video length (to assist ffmpeg):%d\n",setting->video_length);
			fflush(log);
		} else if (strncmp(FRAMEHOOK_OPT_FONT_WIDTH_FIX,arg,FRAMEHOOK_OPT_FONT_WIDTH_FIX_LEN) == 0){
			int font_w_fix_ratio = MAX(0,atoi(arg+FRAMEHOOK_OPT_FONT_WIDTH_FIX_LEN));
			if (setting->font_w_fix_r==1.0f && font_w_fix_ratio > 0){
				setting->font_w_fix_r = (float)font_w_fix_ratio / 100.0f;
				fprintf(log,"[framehook/init]font width fix: %d%%\n",font_w_fix_ratio);
				fflush(log);
			}
		} else if (strncmp(FRAMEHOOK_OPT_FONT_HEIGHT_FIX,arg,FRAMEHOOK_OPT_FONT_HEIGHT_FIX_LEN) == 0){
			int font_h_fix_ratio = MAX(0,atoi(arg+FRAMEHOOK_OPT_FONT_HEIGHT_FIX_LEN));
			if (setting->font_h_fix_r==1.0f && font_h_fix_ratio > 0){
				setting->font_h_fix_r = (float)font_h_fix_ratio / 100.0f;
				fprintf(log,"[framehook/init]font height fix: %d%%\n",font_h_fix_ratio);
				fflush(log);
			}
		} else if (strncmp(FRAMEHOOK_OPT_ASPECT_MODE, arg, FRAMEHOOK_OPT_ASPECT_MODE_LEN) == 0) {
			int aspect_mode = MAX(0, atoi(arg + FRAMEHOOK_OPT_ASPECT_MODE_LEN));
			/**
			 * アスペクト比の指定. コメントのフォントサイズや速度に影響する.（いんきゅばす互換）
			 * 0 -  4:3  → 512
			 * 1 - 16:9  → 640
			 */
			fprintf(log, "[framehook/init]aspect mode:%d\n", aspect_mode);
			fflush(log);
			if (aspect_mode){
				fputs("[framehook/init]use wide player.\n",log);
				fflush(log);
				setting->nico_width_now = NICO_WIDTH_WIDE;
			} else {
				fputs("[framehook/init]use normal player.\n",log);
				fflush(log);
				setting->nico_width_now = NICO_WIDTH;
			}
		} else if (setting->original_resize && strcmp("--disable-original-resize",arg) == 0){
			setting->original_resize = FALSE;
			fprintf(log,"[framehook/init]disable original resize (experimental)\n");
			fflush(log);
		} else if (strncmp(FRAMEHOOK_OPT_COMMENT_SPEED,arg,FRAMEHOOK_OPT_COMMENT_SPEED_LEN) == 0){
			char* com_speed_str = arg+FRAMEHOOK_OPT_COMMENT_SPEED_LEN;
			setting->fixmode = TRUE;
			int ahead_vpos = setting->comment_ahead_vpos;
			if(com_speed_str[0]=='@'){
				//秒数指定
				float com_duration = (float)atof(com_speed_str+1);
				if (com_duration != 0.0){
					setting->comment_duration = com_duration;
					fprintf(log,"[framehook/init]comment duration fix: %.2f sec.\n",com_duration);
					if(com_duration != 4.0){
						ahead_vpos = (int)lround(1.0 * VPOS_FACTOR * (com_duration / 4.0));
					}
					fflush(log);
				}
			}else{
				int com_speed = atoi(com_speed_str);
				if (com_speed != 0){
					setting->comment_speed = com_speed;
					fprintf(log,"[framehook/init]comment speed fix: %d pixel/sec.\n",com_speed);
					if(com_speed < 130){
						ahead_vpos = (int)lround(1.0 * VPOS_FACTOR * (130.0 / (double)com_speed));
					}
					fflush(log);
				}
			}
			if(ahead_vpos != setting->comment_ahead_vpos){
				setting->comment_ahead_vpos = ahead_vpos;
				fprintf(log,"[framehook/init]ahead VPOS fix: %d VPOS.\n",setting->comment_ahead_vpos);
				fflush(log);
			}
		} else if(!setting->enableCA && strcmp("--enable-CA",arg) == 0){
			setting->enableCA = TRUE;
			fprintf(log,"[framehook/init]Comment Art mode enable.\n");
			fflush(log);
		} else if(!setting->debug && strcmp(arg,"--debug-print") == 0){
			setting->debug = TRUE;
			fprintf(log,"[framehook/init]print debug information\n");
			fflush(log);
		} else if(strncmp(FRAMEHOOK_OPT_DEBUG,arg,FRAMEHOOK_OPT_DEBUG_LEN) == 0){
			setting->extra_mode = arg+FRAMEHOOK_OPT_DEBUG_LEN;
			fprintf(log,"[framehook/init]extra mode:%s\n",setting->extra_mode);
			fflush(log);
		} else if(!setting->use_lineskip_as_fontsize && strcmp("--use-lineskip-as-fontsize",arg) == 0){
			setting->use_lineskip_as_fontsize = TRUE;
			fprintf(log,"[framehook/init]use Lineskip as Fontsize (experimental)\n");
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_INPUT_SIZE,arg,FRAMEHOOK_OPT_INPUT_SIZE_LEN) == 0){
			setting->input_size = arg+FRAMEHOOK_OPT_INPUT_SIZE_LEN;
			fprintf(log,"[framehook/init]input size: %s\n",setting->input_size);
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_SET_SIZE,arg,FRAMEHOOK_OPT_SET_SIZE_LEN) == 0){
			setting->set_size = arg+FRAMEHOOK_OPT_SET_SIZE_LEN;
			fprintf(log,"[framehook/init]set size: %s\n",setting->set_size);
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_PAD_OPTION,arg,FRAMEHOOK_OPT_PAD_OPTION_LEN) == 0){
			setting->pad_option = arg+FRAMEHOOK_OPT_PAD_OPTION_LEN;
			fprintf(log,"[framehook/init]pad option: %s\n",setting->pad_option);
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_OUT_SIZE,arg,FRAMEHOOK_OPT_OUT_SIZE_LEN) == 0){
			setting->out_size = arg+FRAMEHOOK_OPT_OUT_SIZE_LEN;
			fprintf(log,"[framehook/init]output size: %s\n",setting->out_size);
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_APRIL_FOOL,arg,FRAMEHOOK_OPT_APRIL_FOOL_LEN) == 0){
			setting->april_fool = arg+FRAMEHOOK_OPT_APRIL_FOOL_LEN;
			fprintf(log,"[framehook/init]april fool: %s\n",setting->april_fool);
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_WAKUIRO,arg,FRAMEHOOK_OPT_WAKUIRO_LEN) == 0){
			setting->wakuiro = arg+FRAMEHOOK_OPT_WAKUIRO_LEN;
			fprintf(log,"[framehook/init]wakuiro: %s\n",setting->wakuiro);
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_OPAQUE,arg,FRAMEHOOK_OPT_OPAQUE_LEN) == 0){
			setting->opaque_rate = arg+FRAMEHOOK_OPT_OPAQUE_LEN;
			fprintf(log,"[framehook/init]opaque_rate: %s\n",setting->opaque_rate);
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_LIVE,arg,FRAMEHOOK_OPT_LIVE_LEN) == 0){
			setting->is_live = true;
			fputs("[framehook/init]enable opaque comment.\n",log);
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_COMMENT_SHIFT,arg,FRAMEHOOK_OPT_COMMENT_SHIFT_LEN) == 0){
			setting->comment_shift = arg+FRAMEHOOK_OPT_COMMENT_SHIFT_LEN;
			fprintf(log,"[framehook/init]comment_shift: %s\n",setting->comment_shift);
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_COMMENT_ERASE,arg,FRAMEHOOK_OPT_COMMENT_ERASE_LEN) == 0){
			setting->comment_erase = arg+FRAMEHOOK_OPT_COMMENT_ERASE_LEN;
			fprintf(log,"[framehook/init]comment_erase: %s\n",setting->comment_erase);
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_COMMENT_OFF,arg,FRAMEHOOK_OPT_COMMENT_OFF_LEN) == 0){
			setting->comment_off = arg+FRAMEHOOK_OPT_COMMENT_OFF_LEN;
			fprintf(log,"[framehook/init]comment_off: %s\n",setting->comment_off);
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_COMMENT_LF,arg,FRAMEHOOK_OPT_COMMENT_LF_LEN) == 0){
			setting->comment_linefeed = arg+FRAMEHOOK_OPT_COMMENT_LF_LEN;
			fprintf(log,"[framehook/init]comment_linefeed: %s\n",setting->comment_linefeed);
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_VFSPEEDRATE,arg,FRAMEHOOK_OPT_VFSPEEDRATE_LEN) == 0){
			setting->vfspeedrate = arg+FRAMEHOOK_OPT_VFSPEEDRATE_LEN;
			fprintf(log,"[framehook/init]vfspeedrate: %s\n",setting->vfspeedrate);
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_LAYER_CTRL,arg,FRAMEHOOK_OPT_LAYER_CTRL_LEN) == 0){
			setting->layerctrl = TRUE;
			fprintf(log,"[framehook/init]layer control: %d\n",setting->layerctrl);
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_RESIZE_ADJUST,arg,FRAMEHOOK_OPT_RESIZE_ADJUST_LEN) == 0){
			double adjust = strtod(arg+FRAMEHOOK_OPT_RESIZE_ADJUST_LEN, NULL);
			if(adjust>=0.0){
				setting->comment_resize_adjust = (float)(adjust / 100.0);
				fprintf(log,"[framehook/init]resize adjust: %.2f\n",setting->comment_resize_adjust);
				fflush(log);
			}
		}
		else if (strncmp(FRAMEHOOK_OPT_HTML5_COMMENT,arg,FRAMEHOOK_OPT_HTML5_COMMENT_LEN) == 0){
			setting->html5comment = TRUE;
			fprintf(log,"[framehook/init]html5 comment: %d\n",setting->html5comment);
			fflush(log);
		}
		else if (strncmp(FRAMEHOOK_OPT_MIN_VPOS,arg,FRAMEHOOK_OPT_MIN_VPOS_LEN) == 0){
			double min_vpos = strtod(arg+FRAMEHOOK_OPT_MIN_VPOS_LEN, NULL);
			setting->min_vpos_sec = min_vpos;
			fprintf(log,"[framehook/init]minimum vpos: %.2f\n",setting->min_vpos_sec);
			fflush(log);
		}
		// CA用フォント
		else if(strncmp(FRAMEHOOK_OPT_FONT_DIR,arg,FRAMEHOOK_OPT_FONT_DIR_LEN)==0){
			setting->fontdir = arg+FRAMEHOOK_OPT_FONT_DIR_LEN;
			fprintf(log,"[framehook/init]font dir: %s\n",setting->fontdir);
			fflush(log);
		}
		else if(strncmp(FRAMEHOOK_OPT_SIMSUN_FONT,arg,FRAMEHOOK_OPT_SIMSUN_FONT_LEN) == 0
				&& setting->CAfont_path[SIMSUN_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_SIMSUN_FONT_LEN;
			setting->CAfont_path[SIMSUN_FONT] = font;
			fprintf(log,"[framehook/init]SIMSUN Font path:%s\n",setting->CAfont_path[SIMSUN_FONT]);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_GULIM_FONT,arg,FRAMEHOOK_OPT_GULIM_FONT_LEN) == 0
				&& setting->CAfont_path[GULIM_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_GULIM_FONT_LEN;
			setting->CAfont_path[GULIM_FONT] = font;
			fprintf(log,"[framehook/init]GULIM Font path:%s\n",setting->CAfont_path[GULIM_FONT]);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_ARIAL_FONT,arg,FRAMEHOOK_OPT_ARIAL_FONT_LEN) == 0
				&& setting->CAfont_path[ARIAL_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_ARIAL_FONT_LEN;
			setting->CAfont_path[ARIAL_FONT] = font;
			fprintf(log,"[framehook/init]ARIAL Font path:%s\n",setting->CAfont_path[ARIAL_FONT]);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_GOTHIC_FONT,arg,FRAMEHOOK_OPT_GOTHIC_FONT_LEN) == 0
				&& setting->CAfont_path[GOTHIC_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_GOTHIC_FONT_LEN;
			setting->CAfont_path[GOTHIC_FONT] = font;
			setting->CAfont_index[GOTHIC_FONT] = 1;
			fprintf(log,"[framehook/init]GOTHIC Font path:%s\n",setting->CAfont_path[GOTHIC_FONT]);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_GEORGIA_FONT,arg,FRAMEHOOK_OPT_GEORGIA_FONT_LEN) == 0
				&& setting->CAfont_path[GEORGIA_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_GEORGIA_FONT_LEN;
			setting->CAfont_path[GEORGIA_FONT] = font;
			fprintf(log,"[framehook/init]GEORGIA Font path:%s\n",setting->CAfont_path[GEORGIA_FONT]);
			fflush(log);
//		}else if(strncmp(FRAMEHOOK_OPT_MSUI_GOTHIC_FONT,arg,FRAMEHOOK_OPT_MSUI_GOTHIC_FONT_LEN) == 0
//				&& setting->CAfont_path[UI_GOTHIC_FONT]==NULL){
//			char* font = arg+FRAMEHOOK_OPT_MSUI_GOTHIC_FONT_LEN;
//			setting->CAfont_path[UI_GOTHIC_FONT] = font;
//			fprintf(log,"[framehook/init]UI GOTHIC Font path:%s\n",setting->CAfont_path[UI_GOTHIC_FONT]);
//			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_ARIALUNI_FONT,arg,FRAMEHOOK_OPT_ARIALUNI_FONT_LEN) == 0
				&& setting->CAfont_path[ARIALUNI_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_ARIALUNI_FONT_LEN;
			setting->CAfont_path[ARIALUNI_FONT] = font;
			fprintf(log,"[framehook/init]ARIAL UNICODE MS Font path:%s\n",setting->CAfont_path[ARIAL_FONT]);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_DEVANAGARI_FONT,arg,FRAMEHOOK_OPT_DEVANAGARI_FONT_LEN) == 0
				&& setting->CAfont_path[DEVANAGARI]==NULL){
			char* font = arg+FRAMEHOOK_OPT_DEVANAGARI_FONT_LEN;
			setting->CAfont_path[DEVANAGARI] = font;
			fprintf(log,"[framehook/init]DEVANAGARI Font path:%s\n",setting->CAfont_path[DEVANAGARI]);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_TAHOMA_FONT,arg,FRAMEHOOK_OPT_TAHOMA_FONT_LEN) == 0
				&& setting->CAfont_path[TAHOMA_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_TAHOMA_FONT_LEN;
			setting->CAfont_path[TAHOMA_FONT] = font;
			fprintf(log,"[framehook/init]TAHOMA Font path:%s\n",setting->CAfont_path[TAHOMA_FONT]);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_MINGLIU_FONT,arg,FRAMEHOOK_OPT_MINGLIU_FONT_LEN) == 0
				&& setting->CAfont_path[MINGLIU_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_MINGLIU_FONT_LEN;
			setting->CAfont_path[MINGLIU_FONT] = font;
			fprintf(log,"[framehook/init]MingLiU Font path:%s\n",setting->CAfont_path[MINGLIU_FONT]);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_NMINCHO_FONT,arg,FRAMEHOOK_OPT_NMINCHO_FONT_LEN) == 0
				&& setting->CAfont_path[N_MINCHO_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_NMINCHO_FONT_LEN;
			int index = atoi(font);
			if(index!=0){
				font = strchr(font,' ');
				if(font!=NULL){
					font++;
				}
			}
			setting->CAfont_path[N_MINCHO_FONT] = font;
			setting->CAfont_index[N_MINCHO_FONT] = index;
			fprintf(log,"[framehook/init]NMINCHO Font path:%s %d\n",setting->CAfont_path[N_MINCHO_FONT],index);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_ESTRANGELO_FONT,arg,FRAMEHOOK_OPT_ESTRANGELO_FONT_LEN) == 0
				&& setting->CAfont_path[ESTRANGELO_EDESSA_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_ESTRANGELO_FONT_LEN;
			setting->CAfont_path[ESTRANGELO_EDESSA_FONT] = font;
			fprintf(log,"[framehook/init]ESTRANGELO EDESSA Font path:%s\n",setting->CAfont_path[ESTRANGELO_EDESSA_FONT]);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_GUJARATI_FONT,arg,FRAMEHOOK_OPT_GUJARATI_FONT_LEN) == 0
				&& setting->CAfont_path[GUJARATI_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_GUJARATI_FONT_LEN;
			setting->CAfont_path[GUJARATI_FONT] = font;
			fprintf(log,"[framehook/init]GUJARATI Font path:%s\n",setting->CAfont_path[GUJARATI_FONT]);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_BENGAL_FONT,arg,FRAMEHOOK_OPT_BENGAL_FONT_LEN) == 0
				&& setting->CAfont_path[BENGAL_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_BENGAL_FONT_LEN;
			setting->CAfont_path[BENGAL_FONT] = font;
			fprintf(log,"[framehook/init]BENGAL Font path:%s\n",setting->CAfont_path[BENGAL_FONT]);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_TAMIL_FONT,arg,FRAMEHOOK_OPT_TAMIL_FONT_LEN) == 0
				&& setting->CAfont_path[TAMIL_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_TAMIL_FONT_LEN;
			setting->CAfont_path[TAMIL_FONT] = font;
			fprintf(log,"[framehook/init]TAMIL Font path:%s\n",setting->CAfont_path[TAMIL_FONT]);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_LAOO_FONT,arg,FRAMEHOOK_OPT_LAOO_FONT_LEN) == 0
				&& setting->CAfont_path[LAOO_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_LAOO_FONT_LEN;
			setting->CAfont_path[LAOO_FONT] = font;
			fprintf(log,"[framehook/init]LAOO Font path:%s\n",setting->CAfont_path[LAOO_FONT]);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_GURMUKHI_FONT,arg,FRAMEHOOK_OPT_GURMUKHI_FONT_LEN) == 0
				&& setting->CAfont_path[GURMUKHI_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_GURMUKHI_FONT_LEN;
			setting->CAfont_path[GURMUKHI_FONT] = font;
			fprintf(log,"[framehook/init]GURMUKHI Font path:%s\n",setting->CAfont_path[GURMUKHI_FONT]);
			fflush(log);

		}else if(strncmp(FRAMEHOOK_OPT_FONT_LIST,arg,FRAMEHOOK_OPT_FONT_LIST_LEN) == 0
				&& setting->fontlist==NULL){
			char* font = arg+FRAMEHOOK_OPT_FONT_LIST_LEN;
			setting->fontlist = font;
			fprintf(log,"[framehook/init]Font List:<%s>\n",setting->fontlist);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_EXTRA_FONT,arg,FRAMEHOOK_OPT_EXTRA_FONT_LEN) == 0
				&& setting->extra_path==NULL){
			char* font = arg+FRAMEHOOK_OPT_EXTRA_FONT_LEN;
			setting->extra_path = font;
			fprintf(log,"[framehook/init]Extra Font:%s\n",setting->extra_path);
			fflush(log);
		}
	}
	//引数を正しく入力したか否かのチェック
	//ここでチェックしているの以外は、デフォルト設定で逃げる。
	if(!setting->enableCA){
		if(!setting->font_path){
			fputs("[framehook/init]please set FONT PATH.\n",log);
			fflush(log);
			return FALSE;
		}
		fflush(log);
		return TRUE;
	}
	if(!parseFontList((SETTING*)setting,log)){
		return FALSE;
	}
	if(!extra_font((SETTING*)setting,log)){
		return FALSE;
	}
	if(!setting->CAfont_path[GOTHIC_FONT]){
		fprintf(log,"[framehook/init]no GOTHIC Font path.\n");
		return FALSE;
	}
	if(!setting->CAfont_path[SIMSUN_FONT]){
		setting->CAfont_path[SIMSUN_FONT] = setting->CAfont_path[GOTHIC_FONT];
		fprintf(log,"[framehook/init]no SIMSUN Font path. Use Font path<%s>.\n",setting->CAfont_path[GOTHIC_FONT]);
	}
	if(!setting->CAfont_path[GULIM_FONT]){
		setting->CAfont_path[GULIM_FONT] = setting->CAfont_path[SIMSUN_FONT];
		fprintf(log,"[framehook/init]no GULIM Font path. Use Font path<%s>.\n",setting->CAfont_path[SIMSUN_FONT]);
	}
	if(!setting->CAfont_path[ARIAL_FONT]){
		setting->CAfont_path[ARIAL_FONT] = setting->CAfont_path[GOTHIC_FONT];
		fprintf(log,"[framehook/init]no ARIAL Font path. Use Font path<%s>.\n",setting->CAfont_path[GOTHIC_FONT]);
	}

	if(!setting->CAfont_path[GEORGIA_FONT]){
		setting->CAfont_path[GEORGIA_FONT] = setting->CAfont_path[ARIAL_FONT];
		fprintf(log,"[framehook/init]no GEORGIA Font path. Use Font path<%s>.\n",setting->CAfont_path[ARIAL_FONT]);
	}
	if(!setting->CAfont_path[ARIALUNI_FONT]){
		fprintf(log,"[framehook/init]no ARIAL UNICODE Font path.\n");
	}
	if(!setting->CAfont_path[DEVANAGARI]){
		setting->CAfont_path[DEVANAGARI] = setting->CAfont_path[ARIAL_FONT];
		fprintf(log,"[framehook/init]no DEVANAGARI Font path. Use Font path<%s>.\n",setting->CAfont_path[ARIAL_FONT]);
	}
	if(!setting->CAfont_path[TAHOMA_FONT]){
		setting->CAfont_path[TAHOMA_FONT] = setting->CAfont_path[ARIAL_FONT];
		fprintf(log,"[framehook/init]no TAHOMA Font path. Use Font path<%s>.\n",setting->CAfont_path[ARIAL_FONT]);
	}
	if(!setting->CAfont_path[MINGLIU_FONT]){
		setting->CAfont_path[MINGLIU_FONT] = setting->CAfont_path[GOTHIC_FONT];
		fprintf(log,"[framehook/init]no MINGLIU Font path. Use Font path<%s>.\n",setting->CAfont_path[GOTHIC_FONT]);
	}
	if(!setting->CAfont_path[N_MINCHO_FONT]){
		setting->CAfont_path[N_MINCHO_FONT] = setting->CAfont_path[SIMSUN_FONT];
		fprintf(log,"[framehook/init]no N MINCHO Font path. Use Font path<%s>.\n",setting->CAfont_path[SIMSUN_FONT]);
	}
	if(!setting->CAfont_path[ESTRANGELO_EDESSA_FONT]){
		setting->CAfont_path[ESTRANGELO_EDESSA_FONT] = setting->CAfont_path[ARIAL_FONT];
		fprintf(log,"[framehook/init]no ESTRANGELO_EDESSA Font path. Use Font path<%s>.\n",setting->CAfont_path[ARIAL_FONT]);
	}
	if(!setting->CAfont_path[GUJARATI_FONT]){
		setting->CAfont_path[GUJARATI_FONT] = setting->CAfont_path[ARIAL_FONT];
		fprintf(log,"[framehook/init]no GUJARATI Font path. Use Font path<%s>.\n",setting->CAfont_path[ARIAL_FONT]);
	}
	if(!setting->CAfont_path[BENGAL_FONT]){
		setting->CAfont_path[BENGAL_FONT] = setting->CAfont_path[ARIAL_FONT];
		fprintf(log,"[framehook/init]no BENGAL Font path. Use Font path<%s>.\n",setting->CAfont_path[ARIAL_FONT]);
	}
	if(!setting->CAfont_path[TAMIL_FONT]){
		setting->CAfont_path[TAMIL_FONT] = setting->CAfont_path[ARIAL_FONT];
		fprintf(log,"[framehook/init]no TAMIL Font path. Use Font path<%s>.\n",setting->CAfont_path[ARIAL_FONT]);
	}
	if(!setting->CAfont_path[LAOO_FONT]){
		setting->CAfont_path[LAOO_FONT] = setting->CAfont_path[ARIAL_FONT];
		fprintf(log,"[framehook/init]no LAOO Font path. Use Font path<%s>.\n",setting->CAfont_path[ARIAL_FONT]);
	}
	if(!setting->CAfont_path[GURMUKHI_FONT]){
		setting->CAfont_path[GURMUKHI_FONT] = setting->CAfont_path[ARIAL_FONT];
		fprintf(log,"[framehook/init]no GURMUKHI Font path. Use Font path<%s>.\n",setting->CAfont_path[ARIAL_FONT]);
	}

	fflush(log);
	return TRUE;
}
void filecopy(FILE*dst,FILE*src);
/*
 *
 */
FILE* changelog(FILE* log,SETTING* setting){
	char label[] = "[log]vhext.txt";
	const char *path = setting->data_user_path;
	if(path == NULL ){
		path = setting->data_owner_path;
	}
	if(path == NULL ){
		path = setting->data_optional_path;
	}
	if(path == NULL){
		return log;
	}
	long l = strlen(path) + strlen(label) + 1;
	char *newname = (char *)malloc(l);
	if(newname==NULL){
		return log;
	}
	long pos = strcspn(path,"_");
	strncpy(newname,path,pos);
	newname[pos] = '\0';
	strcat(newname,label);
	FILE* newlog = fopen(newname,"w");
	free(newname);
	if(newlog==NULL){
		return log;
	}
	fflush(log);
	filecopy(newlog,log);
	fclose(log);
	fflush(newlog);
	return newlog;
}
/**
 *
 */
void filecopy(FILE*dst,FILE*src){
	int c = -1;
	fseek(src,0L,SEEK_SET);
	while((c = fgetc(src))!=EOF){
		fputc(c,dst);
	}
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

// fontlistを変換する
// fontlist="999:fontname ..."->fontnames[999]
// return succeeded?
int parseFontList(SETTING* setting,FILE* log){
	if(setting->fontlist==NULL)
		return TRUE;
	size_t s = strlen(setting->fontlist);
	char* fontname = (char*)malloc(s+1);
	if(fontname==NULL){
		fprintf(log,"[main/parseFontList]malloc failed.\n");
		return FALSE;
	}
	strcpy(fontname,setting->fontlist);
	setting->fontlist = fontname;	// prepare to free().
	fprintf(log,"[main/parseFontList]fontlist is %s.\n",fontname);
	int n;
	int i;
	char i999[4];
	for(n=0; n<CA_FONT_PATH_MAX; n++){
		for(i=0;i<3;i++){
			i999[i] = fontname[i];
			if(i999[i]==':')
				break;
		}
		i999[i] = '\0';
		fontname += i+1;
		int fontindex = 0;
		if(fontname[1]==' '){
			fontindex = fontname[0]-'0';
			fontname+=2;
		}
		i = (int)atoi(i999);
		if(i<CA_FONT_PATH_MAX && !setting->CAfont_path[i]){
			setting->CAfont_index[i] = fontindex;
			setting->CAfont_path[i] = fontname;
		}
//		fprintf(log,"[main/parseFontList]n:%d i:%d fontname:0x%08x\n",n,i,(Uint32)fontname);
		fontname = strchr(fontname,' ');
		if(!fontname)
			break;
		*fontname++ = '\0';
		fprintf(log,"[main/parseFontList]fontname[%d]:%s\n",i,setting->CAfont_path[i]);
	}
	fprintf(log,"[main/parseFontList]%d font names parsed.\n",n);
	return TRUE;
}

/*
 * 必要な関数二つめ。フレームごとに呼ばれるよ！
 *
 */
__declspec(dllexport) void ExtProcess(void *ctx,void* dummy, vhext_frame *pict){
		ContextInfo *ci = (ContextInfo *) ctx;
		FILE* log = ci->log;
		if(!ci->data.typeNicovideoE){
			pict = (vhext_frame*)dummy;
		}

	/* Note:
	 * Saccubus 1.22以降の拡張vhookフィルタでは、RGB24フォーマットでのみ
	 * 画像が提供されます。
	 */

		//SDLのサーフェイスに変換
		SDL_Surface* surf = SDL_CreateRGBSurfaceFrom(pict->data,
												pict->w,pict->h,24,pict->linesize,
											#if SDL_BYTEORDER == SDL_BIG_ENDIAN
												0xff000000,
												0x00ff0000,
												0x0000ff00,
											#else
												0x000000ff,
												0x0000ff00,
												0x00ff0000,
											#endif
												0x00000000
												);
	//フィルタ
	int now_vpos = (pict->pts * VPOS_FACTOR);
	if(!main_process(&ci->data,surf,now_vpos)){
		fputs("[framehook/process]failed to process.\n",log);
		fflush(log);
		exit(1);
	}
	//サーフェイス開放
	SDL_FreeSurface(surf);
	fflush(log);
}

/*
 * 必要な関数最後。終わったら呼ばれるよ！
 *
 */

__declspec(dllexport) void ExtRelease(void *ctx, void* dummy){
		ContextInfo *ci;
		ci = (ContextInfo *) ctx;
		FILE* log = ci->log;
		fputs("[framehook/close]closing...\n",log);
		if (ctx) {
			closeData(&ci->data);
			fputs("[framehook/close]closed.\n",log);
			fclose(log);
			//コンテキスト全体
			free(ctx);
		}
		//ライブラリの終了
		close();
}

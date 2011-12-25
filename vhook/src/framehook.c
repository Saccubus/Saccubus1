/*フレームフックの相手をするため専用*/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <SDL/SDL.h>
#include "common/framehook_ext.h"
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
int init_setting(FILE*log,const toolbox *tbox,SETTING* setting,int argc, char *argv[]);
FILE* changelog(FILE* log,SETTING* setting);

__declspec(dllexport) int ExtConfigure(void **ctxp,const toolbox *tbox, int argc, char *argv[]){
	int i;
	//ログ
	FILE* log = fopen("[log]vhext.txt", "w+");
	char linebuf[128];
	char *ver="1.29.16";
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
	if(init_setting(log,tbox,&setting,argc,argv)){
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
	argv[0]:プログラム
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
	--deb : デバッグ出力を有効にする
*/

int init_setting(FILE*log,const toolbox *tbox,SETTING* setting,int argc, char *argv[]){
	/* TOOLBOXのバージョンチェック */
	if (tbox->version != TOOLBOX_VERSION){
		fprintf(log,"[framehook/init]TOOLBOX version(%d) is not %d.\n", tbox->version, TOOLBOX_VERSION);
		fflush(log);
		return FALSE;
	}
	/*videoの長さ*/
	setting->video_length = (tbox->video_length * VPOS_FACTOR);
	if (setting->video_length<=0){
		fprintf(log,"[framehook/init]video_length is less or equals 0.\n");
		fflush(log);
//		return FALSE;
	}
	/*以降オプション*/

	//コメントを見せるか否か？
	setting->enable_user_comment = FALSE;
	setting->enable_owner_comment = FALSE;
	setting->enable_optional_comment = FALSE;
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
	setting->enableCA = FALSE;
	setting->debug = FALSE;
	setting->use_lineskip_as_fontsize = FALSE;	//デフォルトは無効 FonrsizeにLineskipを合わせる（実験的）
	// CA用フォント
	//  MS UI GOTHIC は msgothic.ttc の index=2
	int f;
	for(f=0;f<CA_FONT_MAX;f++){
		setting->CAfont_path[f] = NULL;
	}
	// CA切替用Unicode群
	//setting->CAfont_change_uc[SIMSUN_FONT] = "02cb 2196-2199 2470-249b 2504-250b 250d-250e 2550-2573 2581-258f 2593-2595 3021-3029 3105-3129 3220-3229 e758-e864 f929 f995";	//明朝化 SIMSUN
	//setting->CAfont_change_uc[GULIM_FONT] = "249c-24b5 24d0-24e9 2592 25a3-25a9 25b6-25b7 25c0-25c1 25c8 25d0-25d1 260e-260f 261c 261e 2660-2661 2663-2665 2667-2669 266c 3131-318e 3200-321c 3260-327b ac00-d7a3 f900-fa0b";	//丸ゴ GULIM
	//setting->CAfont_change_uc[GOTHIC_FONT] = "30fb ff61-ff9f";	//ゴシック：保護 ・ 中点 半角カナ
	//setting->zero_width_uc = "0x200b 0x2029-0x202f";	// ゼロ幅
	//setting->spaceable_uc = "0x02cb";	// griph が未対応のため空白に
	//setting->CAfont_change_uc[ARIAL_FONT] = NULL;
	//setting->CAfont_change_uc[GEORGIA_FONT] = "10d0-10fb";	//グルジア文字 winフォント
	//setting->CAfont_change_uc[UI_GOTHIC_FONT] = NULL;
	//setting->CAfont_change_uc[DEVANAGARI] = "0900-097f";	//デーヴァナーガリー文字 winフォント
	//setting->CAfont_change_uc[TAHOMA_FONT] = "0e00-0e7f";	//タホマ
	/* その他　参考
	 * 空白   0x00a0 0x2001 0x3000 など
	 */
	// 実験的追加フォント
	setting->extra_path = NULL;
	//setting->extra_uc = NULL;
	setting->extra_fontindex = 0;

	int i;
	char* arg;
	for(i=0;i<argc;i++){
		arg = argv[i];
		if(!setting->data_user_path && strncmp(FRAMEHOOK_OPT_DATA_USER,arg,FRAMEHOOK_OPT_DATA_USER_LEN) == 0){
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
			int com_speed = atoi(arg+FRAMEHOOK_OPT_COMMENT_SPEED_LEN);
			if (com_speed != 0){
				setting->comment_speed = com_speed;
				fprintf(log,"[framehook/init]font height fix: %d pixel/sec.\n",com_speed);
				fflush(log);
			}
		} else if(!setting->enableCA && strcmp("--enable-CA",arg) == 0){
			setting->enableCA = TRUE;
			fprintf(log,"[framehook/init]Comment Art mode enable.\n");
			fflush(log);
		} else if(!setting->debug && strcmp("--debug-print",arg) == 0){
			setting->debug = TRUE;
			fprintf(log,"[framehook/init]print debug information\n");
			fflush(log);
		} else if(!setting->use_lineskip_as_fontsize && strcmp("--use-lineskip-as-fontsize",arg) == 0){
			setting->use_lineskip_as_fontsize = TRUE;
			fprintf(log,"[framehook/init]use Lineskip as Fontsize (experimental)\n");
			fflush(log);
		}
		// CA用フォント
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
			setting->CAfont_path[N_MINCHO_FONT] = font;
			fprintf(log,"[framehook/init]NMINCHO Font path:%s\n",setting->CAfont_path[N_MINCHO_FONT]);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_ESTRANGELO_FONT,arg,FRAMEHOOK_OPT_ESTRANGELO_FONT_LEN) == 0
				&& setting->CAfont_path[N_MINCHO_FONT]==NULL){
			char* font = arg+FRAMEHOOK_OPT_ESTRANGELO_FONT_LEN;
			setting->CAfont_path[N_MINCHO_FONT] = font;
			fprintf(log,"[framehook/init]ESTRANGELO EDESSA Font path:%s\n",setting->CAfont_path[ESTRANGELO_EDESSA_FONT]);
			fflush(log);
		}else if(strncmp(FRAMEHOOK_OPT_EXTRA_FONT,arg,FRAMEHOOK_OPT_EXTRA_FONT_LEN) == 0
				&& setting->extra_path==NULL){
			char* font = arg+FRAMEHOOK_OPT_EXTRA_FONT_LEN;
			setting->extra_path = font;
			fprintf(log,"[framehook/init]Extra Font:%s\n",setting->extra_path);
			fflush(log);
		}
		// CA切替用Unicode群
		/*
		else if(strncmp(FRAMEHOOK_OPT_CHANGE_SIMSUN_UNICODE,arg,FRAMEHOOK_OPT_CHANGE_SIMSUN_UNICODE_LEN) == 0){
			char* unicode = arg+FRAMEHOOK_OPT_CHANGE_SIMSUN_UNICODE_LEN;
			setting->CAfont_change_uc[SIMSUN_FONT] = unicode;
			fprintf(log,"[framehook/init]Change to SIMSUN Font Unicode:%s\n",setting->CAfont_change_uc[SIMSUN_FONT]);
			fflush(log);
		}
		else if(strncmp(FRAMEHOOK_OPT_CHANGE_GULIM_UNICODE,arg,FRAMEHOOK_OPT_CHANGE_GULIM_UNICODE_LEN) == 0){
			char* unicode = arg+FRAMEHOOK_OPT_CHANGE_GULIM_UNICODE_LEN;
			setting->CAfont_change_uc[GULIM_FONT] = unicode;
			fprintf(log,"[framehook/init]Change to GULIM Font Unicode:%s\n",setting->CAfont_change_uc[GULIM_FONT]);
			fflush(log);
		}
		else if(strncmp(FRAMEHOOK_OPT_PROTECT_GOTHIC_UNICODE,arg,FRAMEHOOK_OPT_PROTECT_GOTHIC_UNICODE_LEN) == 0){
			char* unicode = arg+FRAMEHOOK_OPT_PROTECT_GOTHIC_UNICODE_LEN;
			setting->CAfont_change_uc[GOTHIC_FONT] = unicode;
			fprintf(log,"[framehook/init]Protect GOTHIC Font Unicode:%s\n",setting->CAfont_change_uc[GOTHIC_FONT]);
			fflush(log);
		}
		else if(strncmp(FRAMEHOOK_OPT_ZERO_WIDTH_UNICODE,arg,FRAMEHOOK_OPT_ZERO_WIDTH_UNICODE_LEN) == 0){
			char* unicode = arg+FRAMEHOOK_OPT_ZERO_WIDTH_UNICODE_LEN;
			setting->zero_width_uc = unicode;
			fprintf(log,"[framehook/init]Zero width Font Unicode:%s\n",setting->zero_width_uc);
			fflush(log);
		}
		*/
	}
	//引数を正しく入力したか否かのチェック
	//ここでチェックしているの以外は、デフォルト設定で逃げる。
	if(!setting->font_path){
		fputs("[framehook/init]please set FONT PATH.\n",log);
		fflush(log);
		return FALSE;
	}
	if(!setting->enableCA){
		fflush(log);
		return TRUE;
	}
	if(!setting->CAfont_path[GOTHIC_FONT]){
		setting->CAfont_path[GOTHIC_FONT] = setting->font_path;
		fprintf(log,"[framehook/init]no GOTHIC Font path. Use Font path<%s>.\n",setting->font_path);
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
		//setting->CAfont_path[ARIALUNI_FONT] = setting->CAfont_path[ARIAL_FONT];
		fprintf(log,"[framehook/init]no ARIAL UNICODE Font path.\n",setting->CAfont_path[ARIALUNI_FONT]);
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
	replaceSPACE = 0x3000;
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

/*
 * 必要な関数二つめ。フレームごとに呼ばれるよ！
 *
 */
__declspec(dllexport) void ExtProcess(void *ctx,const toolbox *tbox,vhext_frame *pict){
		ContextInfo *ci = (ContextInfo *) ctx;
		FILE* log = ci->log;

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

__declspec(dllexport) void ExtRelease(void *ctx,const toolbox *tbox){
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

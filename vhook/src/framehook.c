/*�t���[���t�b�N�̑�������邽�ߐ�p*/

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
 * �K�v�Ȋ֐��ЂƂ߁B�ŏ��ɌĂ΂���I
 *
 */
int init_setting(FILE*log,const toolbox *tbox,SETTING* setting,int argc, char *argv[]);
FILE* changelog(FILE* log,SETTING* setting);

__declspec(dllexport) int ExtConfigure(void **ctxp,const toolbox *tbox, int argc, char *argv[]){
	int i;
	//���O
	FILE* log = fopen("[log]vhext.txt", "w+");
	char linebuf[128];
	char *ver="1.31.13";
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
	//�K�v�Ȑݒ肪���邩�̊m�F
	fprintf(log,"[framehook/init]called with argc = %d\n",argc);
	fflush(log);
	for(i=0;i<argc;i++){
		fprintf(log,"[framehook/init]arg[%2d] = %s\n",i,argv[i]);
		fflush(log);
	}
	//�Z�b�e�B���O�擾�B
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
	//���C�u�����Ȃǂ̏�����
	if(init(log)){
		fputs("[framehook/init]initialized libs.\n",log);
		fflush(log);
	}else{
		fputs("[framehook/init]failed to initialize libs.\n",log);
		fflush(log);
		return -3;
	}
	/*�R���e�L�X�g�̐ݒ�*/
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
 * �����ł̂݌Ă΂��B
 */

/*
	FILE* log		:���O�t�@�C�� opened as "w"
	toolbox* tbox	:ffmpeg interface?
	SETTING* setting:�ݒ�f�[�^�\�� �o��
	int argc		: argv[] size
	argv[0]:�v���O����
	argv[1]:vhook
	argv[2]:�t�H���g
	argv[3]:�t�H���g�C���f�b�N�X
	argv[4]:����
	argv[5]:�e�̎��
	�ȍ~�I�v�V����
	--enable-show-video�F�`�撆�ɓ����������B
	--enable-fontsize-fix�F�t�H���g�T�C�Y�������Œ�������B
	--nico-width-wide : ���C�h�v���[���[16:9�Ή�
	--font-height-fix-ratio:%d �F �t�H���g���������ύX�̔{���i���j+ int
	--disable-original-resize : ������΂��Ǝ����T�C�Y�𖳌��ɂ���i�����I�j
	--comment-speed: �R�����g���x���w�肷��ꍇ��0
	--deb : �f�o�b�O�o�͂�L���ɂ���
*/

int init_setting(FILE*log,const toolbox *tbox,SETTING* setting,int argc, char *argv[]){
	/* TOOLBOX�̃o�[�W�����`�F�b�N */
	if (tbox->version != TOOLBOX_VERSION){
		fprintf(log,"[framehook/init]TOOLBOX version(%d) is not %d.\n", tbox->version, TOOLBOX_VERSION);
		fflush(log);
		return FALSE;
	}
	/*video�̒���*/
	setting->video_length = (tbox->video_length * VPOS_FACTOR);
	if (setting->video_length<=0){
		fprintf(log,"[framehook/init]video_length is less or equals 0.\n");
		fflush(log);
//		return FALSE;
	}
	/*�ȍ~�I�v�V����*/

	//�R�����g�������邩�ۂ��H
	setting->enable_user_comment = FALSE;
	setting->enable_owner_comment = FALSE;
	setting->enable_optional_comment = FALSE;
	setting->data_user_path = NULL;
	setting->data_owner_path = NULL;
	setting->data_optional_path = NULL;
	//��ʓI�Ȑݒ�
	setting->font_path = NULL;
	setting->font_index = 0;
	setting->user_slot_max = 30;	// 40 ?
	setting->optional_slot_max = 30;
	setting->owner_slot_max = 30;	// infinite ?
	setting->shadow_kind = 1;//�f�t�H���g�̓j�R�j�R���敗
	setting->show_video = FALSE;
	setting->fontsize_fix=FALSE;
	setting->opaque_comment=FALSE;
	setting->nico_width_now=NICO_WIDTH;	//�f�t�H���g�͋��v���C���[��
	setting->optional_trunslucent=FALSE;	//�f�t�H���g�͔������ɂ��Ȃ�
	setting->font_w_fix_r = 1.0f;	//�f�t�H���g�͏]���ʂ�i�ŏI�����ō��킹�邱�Ɓj
	setting->font_h_fix_r = 1.0f;	//�f�t�H���g�͏]���ʂ�i�ŏI�����ō��킹�邱�Ɓj
	setting->original_resize = TRUE;	//�f�t�H���g�͗L���i�����I�ɖ����ɂ���I�����s���j
	setting->comment_speed = 0;
	setting->enableCA = FALSE;
	setting->debug = FALSE;
	setting->use_lineskip_as_fontsize = FALSE;	//�f�t�H���g�͖��� Fonrsize��Lineskip�����킹��i�����I�j
	setting->extra_mode = "";
	setting->input_size = NULL;
	setting->set_size = NULL;
	setting->pad_option = NULL;
	// CA�p�t�H���g
	//  MS UI GOTHIC �� msgothic.ttc �� index=2
	int f;
	for(f=0;f<CA_FONT_MAX;f++){
		setting->CAfont_path[f] = NULL;
		setting->CAfont_index[f] = 0;
	}
	// CA�ؑ֗pUnicode�Q
	//setting->CAfont_change_uc[SIMSUN_FONT] = "02cb 2196-2199 2470-249b 2504-250b 250d-250e 2550-2573 2581-258f 2593-2595 3021-3029 3105-3129 3220-3229 e758-e864 f929 f995";	//������ SIMSUN
	//setting->CAfont_change_uc[GULIM_FONT] = "249c-24b5 24d0-24e9 2592 25a3-25a9 25b6-25b7 25c0-25c1 25c8 25d0-25d1 260e-260f 261c 261e 2660-2661 2663-2665 2667-2669 266c 3131-318e 3200-321c 3260-327b ac00-d7a3 f900-fa0b";	//�ۃS GULIM
	//setting->CAfont_change_uc[GOTHIC_FONT] = "30fb ff61-ff9f";	//�S�V�b�N�F�ی� �E ���_ ���p�J�i
	//setting->zero_width_uc = "0x200b 0x2029-0x202f";	// �[����
	//setting->spaceable_uc = "0x02cb";	// griph �����Ή��̂��ߋ󔒂�
	//setting->CAfont_change_uc[ARIAL_FONT] = NULL;
	//setting->CAfont_change_uc[GEORGIA_FONT] = "10d0-10fb";	//�O���W�A���� win�t�H���g
	//setting->CAfont_change_uc[UI_GOTHIC_FONT] = NULL;
	//setting->CAfont_change_uc[DEVANAGARI] = "0900-097f";	//�f�[���@�i�[�K���[���� win�t�H���g
	//setting->CAfont_change_uc[TAHOMA_FONT] = "0e00-0e7f";	//�^�z�}
	/* ���̑��@�Q�l
	 * ��   0x00a0 0x2001 0x3000 �Ȃ�
	 */
	// �����I�ǉ��t�H���g
	setting->extra_path = NULL;
	//setting->extra_uc = NULL;
	//setting->extra_fontindex = 0;

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
			 * �A�X�y�N�g��̎w��. �R�����g�̃t�H���g�T�C�Y�⑬�x�ɉe������.�i���񂫂�΂��݊��j
			 * 0 -  4:3  �� 512
			 * 1 - 16:9  �� 640
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
		// CA�p�t�H���g
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
		}else if(strncmp(FRAMEHOOK_OPT_EXTRA_FONT,arg,FRAMEHOOK_OPT_EXTRA_FONT_LEN) == 0
				&& setting->extra_path==NULL){
			char* font = arg+FRAMEHOOK_OPT_EXTRA_FONT_LEN;
			setting->extra_path = font;
			fprintf(log,"[framehook/init]Extra Font:%s\n",setting->extra_path);
			fflush(log);
		}
		// CA�ؑ֗pUnicode�Q
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
	//�����𐳂������͂������ۂ��̃`�F�b�N
	//�����Ń`�F�b�N���Ă���̈ȊO�́A�f�t�H���g�ݒ�œ�����B
	if(!setting->enableCA){
		if(!setting->font_path){
			fputs("[framehook/init]please set FONT PATH.\n",log);
			fflush(log);
			return FALSE;
		}
		fflush(log);
		return TRUE;
	}
	if(!setting->CAfont_path[GOTHIC_FONT]){
//		setting->CAfont_path[GOTHIC_FONT] = setting->font_path;
//		fprintf(log,"[framehook/init]no GOTHIC Font path. Use Font path<%s>.\n",setting->font_path);
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
 * �K�v�Ȋ֐���߁B�t���[�����ƂɌĂ΂���I
 *
 */
__declspec(dllexport) void ExtProcess(void *ctx,const toolbox *tbox,vhext_frame *pict){
		ContextInfo *ci = (ContextInfo *) ctx;
		FILE* log = ci->log;

	/* Note:
	 * Saccubus 1.22�ȍ~�̊g��vhook�t�B���^�ł́ARGB24�t�H�[�}�b�g�ł̂�
	 * �摜���񋟂���܂��B
	 */

		//SDL�̃T�[�t�F�C�X�ɕϊ�
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
	//�t�B���^
	int now_vpos = (pict->pts * VPOS_FACTOR);
	if(!main_process(&ci->data,surf,now_vpos)){
		fputs("[framehook/process]failed to process.\n",log);
		fflush(log);
		exit(1);
	}
	//�T�[�t�F�C�X�J��
	SDL_FreeSurface(surf);
	fflush(log);
}

/*
 * �K�v�Ȋ֐��Ō�B�I�������Ă΂���I
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
			//�R���e�L�X�g�S��
			free(ctx);
		}
		//���C�u�����̏I��
		close();
}

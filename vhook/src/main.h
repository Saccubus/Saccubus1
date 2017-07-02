#ifndef MAIN_H_
#define MAIN_H_
#include <SDL/SDL.h>
#include <SDL/SDL_ttf.h>
#include <math.h>
#include "nicodef.h"
#include "struct_define.h"
#include "chat/chat.h"
#include "chat/chat_slot.h"
#include "unicode/unitable.h"
#include "comment/shadow.h"
#define CA_FONT_PATH_MAX 70

struct CDATA{
	int enable_comment;
	CHAT chat;
	CHAT_SLOT slot;
};

struct DATA{
	FILE* log;
	TTF_Font* font[CMD_FONT_MAX];
	SDL_Surface* screen;
	/*���ꂼ��̃R�����g�ɉ������f�[�^*/
	//���[�U�R�����g
	CDATA user;
//	int enable_user_comment;
//	CHAT chat;
//	CHAT_SLOT slot;
	//���e�҃R�����g
	CDATA owner;
//	int enable_owner_comment;
//	CHAT ownerchat;
//	CHAT_SLOT ownerslot;
	//�I�v�V���i���R�����g
	CDATA optional;
//	int enable_optional_comment;
//	CHAT optionalchat;
//	CHAT_SLOT optionalslot;
	const char* version;
	int typeNicovideoE;	// this is SwitchFlag wheather nicovideoE(TRUE) or nicovideoH(FALSE)
	//��ʓI�ȃf�[�^
	const char* data_title;
	int show_thumbnail_size;
	int shadow_kind;
	int show_video;
	int fontsize_fix;
	int opaque_comment;
	float opaque_rate;
	int optional_trunslucent;
	int process_first_called;
	int video_length;
//	int aspect100;		// �A�X�y�N�g��*100	Not used now
	int nico_width_now;	// ������̉���
	int nico_height;
	int aspect_mode;		// 0: 512, 1:640
	float aspect_rate;		// w/h
	int vout_width;
	int vout_height;
	int vout_x;
	int vout_y;
	int pad_w;
	int pad_h;
	int limit_height;
	int y_min;
	int y_max;
	float font_w_fix_r;	// �t�H���g�̕���nicoplayer.swf�ɍ��킹��{��(0< <2)�i�����I�j
	float font_h_fix_r;	// �t�H���g�̍�����nicoplayer.swf�ɍ��킹��{��(0< <2)�i�����I�j
	int original_resize;	// ������΂��Ǝ����T�C�Y���L���i�f�t�H���g�L���j
	int comment_speed;	// �R�����g���x���w�肷��ꍇ��0
	float comment_duration;	// UI�ŃR�����g�b���w�肷��ꍇ��0
	int ahead_vpos;
	int fixmode;	//�R�����g�Փ˔����ύX����
	int enableCA;
	const char* fontdir;
	int use_lineskip_as_fontsize;	//�t�H���g�T�C�Y�����߂�̂�LineSkip�����킹��i�����I�j
	int debug;
	const char* extra_mode;
	int drawframe;
	struct_shadow_data shadow_data;
	double width_scale;	//�������݉@videowidth/nicowidth_now
	int defcolor;	//�f�t�H���g�J���[24bit�i�G�C�v�����t�[���p�A@�f�t�H���g�j
	int deflocation;
	int defsize;
	// CA�p�t�H���g
	TTF_Font* CAfont[CA_FONT_PATH_MAX][CMD_FONT_MAX];
	// CA�ؑ֗pUnicode�Q
	//Uint16* font_change[CA_FONT_MAX];
	// 0:*protect_gothic
	// 1:*change_simsun
	// 2:*change_gulim
	// 3:*arial
	// 4:*gergia
	// 5:*msui_gothic
	// 6:*devanagari
	// 7:*extra
	Uint16* extra_change;
	//Uint16* zero_width;
	//Uint16* spaceable;
//	int limit_height;
	SDL_Surface* ErrFont;
	unsigned int * wakuiro_dat;
	int q_player;
	int is_live;
	int comment_vpos_shift;
	int comment_erase_type;
	int comment_off;
	int comment_off_y;	//���l
	int comment_off_sign;	// 1:�ォ��, -1:������
	int comment_off_kind;	// 0:pixel, 1:big, 2:small, 3:medium, 4:�p�[�Z���g
	int comment_off_naka;	// TRUE�̏ꍇnaka�R�����g�����}�X�N
	int comment_lf_control;	//�s���萧�� 0:���� 1:rev.1.67.1.12 2:new(�t�H���g���ɂ���)
	float comment_linefeed_ratio;	// �R�����g�s���芄�� -1.0 �̏ꍇ�͖���(0.0�`1.0f)
	float vfspeedrate;		// �R�����g��PTS��{���{����B
	int vfspeedflag;		// 0:����, 1:�R�����g����, 2:video�o�͂��ύX
	int layerctrl;		//ue shita naka�̃��C���[������ 0:�Ȃ� 1:naka������
	float comment_resize_adjust;	// scaling��Ƀ��T�C�Y�␳�@���[�U�[�ŏI����
	int html5comment;
		//html5�R�����g���[�h�L��	defont mincho gothic�R�}���h�L���@�t�H���g�T�C�Y�ύX
	int min_vpos;	//vpos�ŏ��l
	//char wstr[128];
	// �����I�ݒ�
	short font_pixel_size[CMD_FONT_MAX];
	short fixed_font_height[CMD_FONT_MAX];	// �C���t�H���g�w��(�|�C���g�w��)
};

typedef struct SETTING{
	const char* data_title;
	int show_thumbnail_size;
	const char* data_user_path;
	const char* data_owner_path;
	const char* data_optional_path;
	const char* font_path;
	const char* version;	// Saccubus1 version (ver>=1.60)   (version of frontend FFmpeg nicovideo set)
	int typeNicovideoE;	// this is SwitchFlag wheather nicovideoE(TRUE) or nicovideoH(FALSE)
	int video_length;
	// �V����ffmpeg����video�̎��Ԃ�ႤInterface��������܂ő����
	// ������΂�����n���i�A���s���̏ꍇ�́@0�@or�@-1�j
	// �R�����g�\���̍Ō�̒��������Ȃ̂łȂ��Ă��䖝����悤�ɕύX�B
	// ���K�v�Ȃ��Ȃ���������
	int font_index;
	int user_slot_max;
	int owner_slot_max;
	int optional_slot_max;
	int shadow_kind;
	int nico_width_now;	// ������̉���
	//int nico_height_now = 384
	//int aspect_mode;		// 0: 512, 1:640
	const char* input_size;
	const char* set_size;
	const char* pad_option;
	const char* out_size;
	/*TRUE OR FALSE*/
	int enable_user_comment;
	int enable_owner_comment;
	int enable_optional_comment;
	int show_video;
	int fontsize_fix;
	int opaque_comment;
	const char* opaque_rate;
	int optional_trunslucent;
	int q_player;		//�R�����g������̍����ȉ��ɂȂ邩�H
	int is_live;	//��������?(��) �R�����g�R�}���h�̎d�l���ς��
	const char* comment_shift;
	const char* comment_erase;	//�R�����g�̓����\�������ߎ��̏����� 0: 1:
	const char* comment_off;	//�R�����g�I�t�w��
	const char* comment_linefeed;	//�R�����g�s����w��
	// [����][�����T�C�Y�w��]���l[�p�[�Z���g�w��][naka�R�����g�t���O]
	// ����:�ォ��+,������-
	// �����T�C�Y�w��:b=big m=medium s=small
	// �p�[�Z���g�w��:%���捂���ɑ΂��鑊�Βl(100����)
	// naka�R�����g�t���O:n
	const char* vfspeedrate;	//video filter speedrate �w��
	int layerctrl;		//ue shita naka�̃��C���[������ 0:�Ȃ� 1:naka������
	float comment_resize_adjust;	// scaling��Ƀ��T�C�Y�␳
	int html5comment;
	float min_vpos_sec;	//�\��Vpos�ŏ��l(�␳��)
		//html5�R�����g���[�h�L��	defont mincho gothic�R�}���h�L���@�t�H���g�T�C�Y�ύX
	// CA�p�t�H���g
	const char* CAfont_path[CA_FONT_PATH_MAX];
	int CAfont_index[CA_FONT_PATH_MAX];
	const char* fontdir;
	// CA�ؑ֗pUnicode�Q
	//const char* CAfont_change_uc[CA_FONT_MAX];
	//const char* zero_width_uc;
	//const char* spaceable_uc;
	// �����p�ǉ��t�H���g
	const char* extra_path;
	const char* extra_uc;
	//int extra_fontindex;
	// �R�~���j�e�B����ł͒ʏ�R�����g���I�v�V���i���X���b�h�Ƃ��Ĕ������ɂ��Ă��邽�߁A�I���\�ɂ���
	float font_w_fix_r;	// �t�H���g�̕���nicoplayer.swf�ɍ��킹��{���i�����I�j
	float font_h_fix_r;	// �t�H���g�̍�����nicoplayer.swf�ɍ��킹��{���i�����I�j
	int original_resize;	// ������΂��Ǝ����T�C�Y���L���i�f�t�H���g�L���j
	int comment_speed;	// �R�����g���x���w�肷��ꍇ��0
	float comment_duration;	// UI�ŃR�����g�b���w�肷��ꍇ��0
	int comment_ahead_vpos;
	int fixmode;	//�R�����g�Փ˔����ύX����
	int enableCA;
	int use_lineskip_as_fontsize;	//�t�H���g�T�C�Y�����߂�̂�LineSkip�����킹��i�����I�j
	int debug;
	const char* extra_mode;	// debug���[�h������
	const char* april_fool;	// �G�C�v�����t�[��������
	const char* wakuiro;	// ���g�F�w�蕶����
//	const char* framerate;	// �t���[�����[�g
	char* fontlist;	// �t�H���g�̃��X�g�@999fontname ...�i128�܂Łj
}SETTING;

#include "struct_define.h"
int init(FILE* log);
int initData(DATA* data,FILE* log,SETTING* setting);
int main_process(DATA* data,SDL_Surface* surf,int now_vpos);
int closeData(DATA* data);
int close();

#endif /*MAIN_H_*/

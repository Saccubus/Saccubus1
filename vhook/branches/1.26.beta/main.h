#ifndef MAIN_H_
#define MAIN_H_
#include <SDL/SDL.h>
#include <SDL/SDL_ttf.h>
#include "nicodef.h"
#include "struct_define.h"
#include "chat/chat.h"
#include "chat/chat_slot.h"

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
	//��ʓI�ȃf�[�^
	int shadow_kind;
	int show_video;
	int fontsize_fix;
	int opaque_comment;
	int optional_trunslucent;
	// �R�~���j�e�B����ł͒ʏ�R�����g���I�v�V���i���X���b�h�Ƃ��Ĕ������ɂ��Ă��邽�߁A�I���\�ɂ���
	int process_first_called;
	int video_length;
//	int aspect100;		// �A�X�y�N�g��*100	Not used now
	int nico_width_now;	// ������̉���
	int font_h_fix_r;	// �t�H���g�̍�����nicoplayer.swf�ɍ��킹��{����(0< <200)�i�����I�j
	int original_resize;	// ������΂��Ǝ����T�C�Y���L���i�f�t�H���g�L���j
	int limitwidth_resize;	// �ՊE�����T�C�Y���L���i�f�t�H���g�L���j
	int linefeed_resize;	// ���s���T�C�Y���L���i�f�t�H���g�L���j
	int double_resize;		// �_�u�����T�C�Y���L���i�f�t�H���g�L���j
	int font_double_scale;		// �t�H���g���`�̎����C�����L���i�f�t�H���g2�{�̎��`���L���j
	int nico_limit_width;	// �m�[�}���ՊE��
	int nico_limit_width_full;	// �t���R�}���h�ՊE��
	int nico_limit_height;	// �e�����[�h�J�n�̍���
	int fixed_font_size[CMD_FONT_MAX];	// �C���t�H���g�w��(�|�C���g�w��)
	int next_y_ratio;	// �R�����g�̍����̎��̍����Ƃ̍��i%�j
	int target_width;	// �I�v�V��������ϊ���̉������X�N���C�s���O�A�w�肪�Ȃ���Ό�����̉���
	// �j�R���ɑ΂��Q�{�P�{�P�^�Q�{�P�^�S�{�̃X�P�[���̎��Ƀt�H���g��zoom�����Ȃ�����
	int font_scaling;	//data->font[]���̃t�H���g�T�C�Y�̃X�P�[�����O�{��
	int font_pixel_size[CMD_FONT_DEF];
};

typedef struct SETTING{
	const char* data_user_path;
	const char* data_owner_path;
	const char* data_optional_path;
	const char* font_path;
	int video_length;
	// �V����ffmpeg����video�̎��Ԃ�ႤInterface��������܂ő����
	// ������΂�����n���i�A���s���̏ꍇ�́@0�@or�@-1�j
	// �R�����g�\���̍Ō�̒��������Ȃ̂łȂ��Ă��䖝����悤�ɕύX�B
	int font_index;
	int user_slot_max;
	int owner_slot_max;
	int optional_slot_max;
	int shadow_kind;
	int nico_width_now;	// ������̉���
	/*TRUE OR FALSE*/
	int enable_user_comment;
	int enable_owner_comment;
	int enable_optional_comment;
	int show_video;
	int fontsize_fix;
	int opaque_comment;
	int optional_trunslucent;
	// �R�~���j�e�B����ł͒ʏ�R�����g���I�v�V���i���X���b�h�Ƃ��Ĕ������ɂ��Ă��邽�߁A�I���\�ɂ���
	int font_h_fix_r;	// �t�H���g�̍�����nicoplayer.swf�ɍ��킹��{�� 4:3�i�����I�j
	int font_h_fix_r_wide;	// �t�H���g�̍�����nicoplayer.swf�ɍ��킹��{�� 16:9�i�����I�j
	int original_resize;	// ������΂��Ǝ����T�C�Y���L���i�f�t�H���g�L���j
	int limitwidth_resize;	// �ՊE�����T�C�Y���L���i�f�t�H���g�L���j
	int linefeed_resize;	// ���s���T�C�Y���L���i�f�t�H���g�L���j
	int double_resize;		// �_�u�����T�C�Y���L���i�f�t�H���g�L���j
	int font_double_scale;		// �t�H���g���`�̎����C�����L���i�f�t�H���g2�{�̎��`���L���j
	int nico_limit_width;	// 4:3 �ՊE��
	int nico_limit_width_full;	// 16:9 �ՊE��
	int nico_limit_height;	// 4:3 �e�����[�h�J�n�̍���
	int nico_limit_height_wide;	// 16:9 �e�����[�h�J�n�̍���
	int fixed_font_size[CMD_FONT_MAX];	// �C���t�H���g�w��(�|�C���g�w��)
	int next_y_ratio;	// �R�����g�̍����̎��̍����Ƃ̍��i%�j
	int target_width;	// �I�v�V��������ϊ���̉������X�N���C�s���O�A�w�肪�Ȃ���Ό�����̉���
	// �j�R���ɑ΂��Q�{��P�^�Q�{�̃X�P�[���̎��Ƀt�H���g��zoom�����Ȃ�����
}SETTING;

int init(FILE* log);
int initData(DATA* data,FILE* log,const SETTING* setting);
int main_process(DATA* data,SDL_Surface* surf,const int now_vpos);
int closeData(DATA* data);
int close();

#endif /*MAIN_H_*/

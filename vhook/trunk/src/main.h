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
	//���e�҃R�����g
	CDATA owner;
	//�I�v�V���i���R�����g
	CDATA optional;
//���[�U�R�����g
//	int enable_user_comment;
//	CHAT chat;
//	CHAT_SLOT slot;
//���e�҃R�����g
//	int enable_owner_comment;
//	CHAT ownerchat;
//	CHAT_SLOT ownerslot;
//�I�v�V���i���R�����g
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
	int nico_width_now;	// ������̉���
	int video_length;	// �K�v�Ȃ��Ȃ���
	// �����I�ݒ聫
	short font_height_rate;	// �t�H���g�̍������j�R���ɍ��킹��{���i������A�X�y�N�g��ɂ��j
	short next_h_rate;	// �R�����g�̍����̎��̍����Ƃ̍��i%�j�i������A�X�y�N�g��ɂ��j
	short limit_width[2];	// �ՊE�����T�C�Y���i�m�[�}���A�t���j
	short double_resize_width[2];	// �_�u�����T�C�Y�J�n���i�m�[�}���A�t���j
	short limit_height;	// �e�����[�h�J�n�̍����i������A�X�y�N�g��ɂ��j
	short font_scaling;	//data->font[]���̃t�H���g�T�C�Y�̃X�P�[�����O�{��
	short double_limit_width[2];	// �_�u�����T�C�Y�ՊE���i�m�[�}���A�t���j
	short font_pixel_size[CMD_FONT_MAX];
	short fixed_font_size[CMD_FONT_MAX];	// �C���t�H���g�w��(�|�C���g�w��)
	short target_width;	// �ϊ���̉����A�w�肪�Ȃ���Ό�����̉���
	//  ���j�R���ɑ΂��Q�{��P�^�Q�{�̃X�P�[���̎��Ƀt�H���g��zoom�����Ȃ�����
	//  TRUE OR FALSE
	short original_resize;	// ������΂��Ǝ����T�C�Y���L���i�f�t�H���g�L���j
	short limitwidth_resize;	// �ՊE�����T�C�Y���L���i�f�t�H���g�L���j
	short linefeed_resize;	// ���s���T�C�Y���L���i�f�t�H���g�L���j
	short double_resize;		// �_�u�����T�C�Y���L���i�f�t�H���g�L���j
	short font_double_scale;		// �t�H���g�T�C�Y�����C�����L���i�f�t�H���g2�{�T�C�Y���L���j
};

typedef struct SETTING{
	const char* data_user_path;
	const char* data_owner_path;
	const char* data_optional_path;
	const char* font_path;
	int font_index;
	int user_slot_max;
	int owner_slot_max;
	int optional_slot_max;
	int shadow_kind;
	int nico_width_now;	// ������̉���
	int video_length;
	// �V����ffmpeg����video�̎��Ԃ�ႤInterface��������܂ő����
	// ������΂�����n���i�A���s���̏ꍇ�́@0�@or�@-1�j
	// �R�����g�\���̍Ō�̒��������Ȃ̂łȂ��Ă��䖝����悤�ɕύX�B
	// ���K�v�Ȃ��Ȃ���
	/*TRUE OR FALSE*/
	int enable_user_comment;
	int enable_owner_comment;
	int enable_optional_comment;
	int show_video;
	int fontsize_fix;
	int opaque_comment;
	int optional_trunslucent;
	// �R�~���j�e�B����ł͒ʏ�R�����g���I�v�V���i���X���b�h�Ƃ��Ĕ������ɂ��Ă��邽�߁A�I���\�ɂ���
	// �����I�ݒ聫
	short font_height_rate[2];	// �t�H���g�̍������j�R���ɍ��킹��{���i4:3,16:9�j
	short nico_limit_width[2];	// �ՊE�����T�C�Y���i�m�[�}���A�t���j
	short double_resize_width[2];	// �_�u�����T�C�Y�J�n���i�m�[�}���A�t���j
	short nico_limit_height[2];	// �e�����[�h�J�n�̍����i4:3,16:9�j
	short next_h_rate[2];	// �R�����g�̍����̎��̍����Ƃ̍��i%�j�i4:3,16:9�j
	short double_limit_width[2];	// �_�u�����T�C�Y�ՊE���i�m�[�}���A�t���j
	short fixed_font_size[CMD_FONT_MAX];	// �C���t�H���g�w��(�|�C���g�w��)
	short target_width;	// �I�v�V��������ϊ���̉������X�N���C�s���O�A�w�肪�Ȃ���Ό�����̉���
	//  �j�R���ɑ΂��Q�{��P�^�Q�{�̃X�P�[���̎��Ƀt�H���g��zoom�����Ȃ�����
	//  TRUE OR FALSE
	short original_resize;	// ������΂��Ǝ����T�C�Y���L���i�f�t�H���g�L���j
	short limitwidth_resize;	// �ՊE�����T�C�Y���L���i�f�t�H���g�L���j
	short linefeed_resize;	// ���s���T�C�Y���L���i�f�t�H���g�L���j
	short double_resize;		// �_�u�����T�C�Y���L���i�f�t�H���g�L���j
	short font_double_scale;		// �t�H���g�T�C�Y�����C�����L���i�f�t�H���g2�{�T�C�Y���L���j
}SETTING;

int init(FILE* log);
int initData(DATA* data,FILE* log,const SETTING* setting);
int main_process(DATA* data,SDL_Surface* surf,const int now_vpos);
int closeData(DATA* data);
int close();

#endif /*MAIN_H_*/

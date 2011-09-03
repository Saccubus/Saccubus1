#ifndef MAIN_H_
#define MAIN_H_
#include <SDL/SDL.h>
#include <SDL/SDL_ttf.h>
#include "nicodef.h"
#include "struct_define.h"
#include "chat/chat.h"
#include "chat/chat_slot.h"

struct DATA{
	FILE* log;
	TTF_Font* font[CMD_FONT_MAX];
	SDL_Surface* screen;
	/*���ꂼ��̃R�����g�ɉ������f�[�^*/
	//���[�U�R�����g
	int enable_user_comment;
	CHAT chat;
	CHAT_SLOT slot;
	//���e�҃R�����g
	int enable_owner_comment;
	CHAT ownerchat;
	CHAT_SLOT ownerslot;
	//�I�v�V���i���R�����g
	int enable_optional_comment;
	CHAT optionalchat;
	CHAT_SLOT optionalslot;
	//��ʓI�ȃf�[�^
	int shadow_kind;
	int show_video;
	int fontsize_fix;
	int opaque_comment;
	int optional_trunslucent;
	int process_first_called;
	int video_length;
	int aspect100;		// �A�X�y�N�g��*100
	int nico_width_now;	// ������̉���
};

typedef struct SETTING{
	const char* data_user_path;
	const char* data_owner_path;
	const char* data_optional_path;
	const char* font_path;
	int video_length;
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
}SETTING;

int init(FILE* log);
int initData(DATA* data,FILE* log,const SETTING* setting);
int main_process(DATA* data,SDL_Surface* surf,const int now_vpos);
int closeData(DATA* data);
int close();

#endif /*MAIN_H_*/

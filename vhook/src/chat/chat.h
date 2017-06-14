#ifndef CHAT_H_
#define CHAT_H_

#include <SDL/SDL.h>
#include "../struct_define.h"

//�j�R�X�N���v�g�ł̃L�[ ���[�hUnicode��`
#define UNICODE_GYAKU	0x00009006	/*�t*/
#define UNICODE_TOU		0x00006295	/*���@���R��*/
#define UNICODE_KO		0x000030b3	/*�R�@�R��*/
#define UNICODE_DE		0x000030c7	/*�f�@�f�t�H���g*/
#define UNICODE_BO		0x000030dc	/*�{�@�{�^�� */
#define UNICODE_KAKKO	0x0000300c	/*�u�@���� */
//�j�R�X�N���v�g�@ ���[�h��`
#define SCRIPT_GYAKU	0x00010000
#define SCRIPT_OWNER	0x0001
#define SCRIPT_USER		0x0002
#define SCRIPT_DEFAULT	0x00020000
#define SCRIPT_REPLACE	0x00040000
#define SCRIPT_BUTTON	0x00080000
#define SCRIPT_VOTE		0x00100000

struct CHAT_ITEM{
	//�ꏊ�̓���
	int no;
	int vpos;
	int location;
	int html5font;	//html5�t�H���g�R�}���h
		// 0	//�S�V�b�N�W��
		// 1	// 4096������
		// 2	//�ۃS�V�b�N��
		// 3	//���U�[�u
	int full;	// whether full ommand?
	int waku;	// ���g�t��
	int script;	// whether nico script?
	int patissier;	//patissier command
	int invisible;	//invisible command
	int replace_user;	// /replace target:user
	int replace_owner;	//	/replace target:owner
	int is_button;	// �{�^��
	int ender;	//ender�R�}���h ���s���T�C�Y�L�����Z��
	int itemfork;	//itemfork
	//�����̏C��
	int size;
	int color;
	SDL_Color color24;
	Uint16* str;
	//���������Ŏg��
	int vstart;		//begin vpos of check y
	int vend;		//last vpos of check y
	int vappear;	//start display of comment
	//int vvanish;		//end display when wide
	//int showed;			// whether checked y pos, 0: not showed, 1:showed and not finished, finished
	int pooled;			// wheather item is pooled?
	int duration;	// vend - vstart
	  // ���b���̏ꍇ  �w��l
	  // ue shita�̏ꍇ  300 vpos
	  // 4:3�̏ꍇ  400 vpos
	  // 16:9�̏ꍇ  400+�� vpos
	int nb_line;	//�R�����g�s��
	int double_resized;	//��d���T�C�Y�������H
	//���t�@�����X
	CHAT* chat;
};

struct CHAT{
	// �R�����gID	0:user, 1:owner, 2:oprional
	int cid;
	int max_no;
	int min_no;
	//�A�C�e��
	int max_item;
	int iterator_index;
	CHAT_ITEM* item;
	//���t�@�����X
	CHAT_SLOT* slot;
	//�v�[��
	CHAT_POOL* pool;
	//�R�����g�^�C�v
	const char* com_type;
	//���t�t���O
	int to_left;
	int reverse_vpos;	// VPOS
	int reverse_duration;	// VPOS
	//patissier�R�}���h�ɂ�閳���R�����g�ԍ��ő�l
	int patissier_ignore;
	// /vote
	CHAT_ITEM *vote_chatitem;
};

#include "chat_slot.h"
struct CHAT_SET{
	CHAT chat;
	CHAT_SLOT slot;
};

//������
int initChat(FILE* log,CHAT* chat,const char* file_path,CHAT_SLOT* slot,int video_length,int nico_width,
	int cid,const char* com_type,int toLeft, int is_live,int vpos_shift,int ahead_vpos, int min_vpos);
void closeChat();
//�C�e���[�^
void resetChatIterator(CHAT* chat);
CHAT_ITEM* getChatShowed(CHAT* chat,int now_vpos,int ahead_vpos);
SDL_Color convColor24(int c);
SDL_Color getSDL_color(int c);

#endif /*CHAT_H_*/

#ifndef CHAT_H_
#define CHAT_H_

#include <SDL/SDL.h>
#include "../struct_define.h"

//typedef int VPOS_T;

struct CHAT_ITEM{
	//�ꏊ�̓���
	int no;
	int vpos;
	int location;
	int full;	// whether full ommand?
	//�����̏C��
	int size;
	int color;
	SDL_Color color24;
	Uint16* str;
	//���������Ŏg��
	int vstart;		//begin vpos of check y
	int vend;		//last vpos of check y
	int vappear;		//start display when wide(640x360)
	int vvanish;		//end display when wide
	int showed;			// whether checked y pos, 0: not showed, 1:showed and not finished, finished
	int duration;	// vend - vstart
	  // ���b���̏ꍇ  �w��l
	  // ue shita�̏ꍇ  300 vpos
	  // 4:3�̏ꍇ  400 vpos
	  // 16:9�̏ꍇ  400+�� vpos
	//���t�@�����X
	CHAT* chat;
};

struct CHAT{
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
};

#include "chat_slot.h"
struct CHAT_SET{
	CHAT chat;
	CHAT_SLOT slot;
};

//������
int initChat(FILE* log,CHAT* chat,const char* file_path,CHAT_SLOT* slot,int video_length,int nico_width,const char* com_type);
void closeChat();
//�C�e���[�^
void resetChatIterator(CHAT* chat);
CHAT_ITEM* getChatShowed(CHAT* chat,int now_vpos);
SDL_Color convColor24(int c);

#endif /*CHAT_H_*/

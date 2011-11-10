#ifndef CHAT_H_
#define CHAT_H_

#include <SDL/SDL.h>
#include "../struct_define.h"

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
	int vstart;
	int vend;
	int showed;
	// int duration;	// vend - vstart
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
};

#include "chat_slot.h"
struct CHAT_SET{
	CHAT chat;
	CHAT_SLOT slot;
};

//������
int initChat(FILE* log,CHAT* chat,const char* file_path,CHAT_SLOT* slot,int video_length);
void closeChat();
//�C�e���[�^
void resetChatIterator(CHAT* chat);
CHAT_ITEM* getChatShowed(CHAT* chat,int now_vpos);

#endif /*CHAT_H_*/

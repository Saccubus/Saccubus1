#ifndef CHAT_SLOT_H_
#define CHAT_SLOT_H_

#include "../struct_define.h"
#include "chat.h"
#include <SDL/SDL.h>

struct CHAT_SLOT_ITEM{
	int used;
	CHAT_ITEM* chat_item;
	SDL_Surface* surf;
	int y;
	float speed;
	//���t�@�����X
	CHAT_SLOT* slot;
};

struct CHAT_SLOT{
	int max_item;
	int iterator_index;
	CHAT_SLOT_ITEM* item;
	//���t�@�����X
	CHAT* chat;
};
#include "../main.h"
//������
int initChatSlot(FILE* log,CHAT_SLOT* slot,int max_slot,CHAT* chat);
void closeChatSlot(CHAT_SLOT* slot);
//�ǉ��A�폜
int addChatSlot(DATA* data,CHAT_SLOT* slot,CHAT_ITEM* item,int video_width,int video_height);
void deleteChatSlot(CHAT_SLOT* slot,CHAT_SLOT_ITEM* item);
void deleteChatSlotFromIndex(CHAT_SLOT* slot,int index);

//�C�e���[�^
void resetChatSlotIterator(CHAT_SLOT* slot);
CHAT_SLOT_ITEM* getChatSlotErased(CHAT_SLOT* slot,int now_vpos);
#endif /*CHAT_SLOT_H_*/

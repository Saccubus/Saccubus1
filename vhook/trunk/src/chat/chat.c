#include <SDL/SDL_endian.h>
#include <stdio.h>

#include <stdlib.h>
#include "chat.h"
#include "../mydef.h"
#include "../nicodef.h"
/*
 * �o�� CHAT chat �̈�m�ہA���ڐݒ�
 * �o�� CHAT_SLOT chat->slot �� slot �|�C���^�ݒ�̂�
 */
int initChat(FILE* log,CHAT* chat,const char* file_path,CHAT_SLOT* slot,int video_length){
	int i;
	int max_no = INTEGER_MIN;
	int min_no = INTEGER_MAX;
	int max_item;
	chat->slot = slot;
	FILE* com_f = fopen(file_path,"rb");
	if(com_f == NULL){
		fputs("[chat/init]failed to open comment file.\n",log);
		return FALSE;
	}
	/*�v�f���̎擾*/
	if(fread(&max_item,sizeof(max_item),1,com_f) <= 0){
		fputs("[chat/init]failed to read the number of comments.\n",log);
		return FALSE;
	}
	max_item = SDL_SwapLE32(max_item);
	fprintf(log,"[chat/init]%d comments.\n",max_item);
	chat->max_item = max_item;
	//�A�C�e���z��̊m��
	chat->item = malloc(sizeof(CHAT_ITEM) * max_item);
	if(chat->item == NULL){
		fputs("[chat/init]failed to malloc for comment.\n",log);
		return FALSE;
	}
	if (video_length == 0){
		fprintf(log,"[chat/fix]cannot adjust end time since video_length UNKOWN\n");
	}
	/*�ʗv�f�̏�����*/
	CHAT_ITEM* item;
	int no;
	int vpos;
	int location;
	int size;
	int color;
	int str_length;
	Uint16* str;
	for(i=0;i<max_item;i++){
		item = &chat->item[i];
		item->chat = chat;
		item->showed = FALSE;
		//�R�����g�ԍ�
		if(fread(&no,sizeof(no),1,com_f) <= 0){
			fputs("[chat/init]failed to read comment number.\n",log);
			return FALSE;
		}
		no = SDL_SwapLE32(no);
		max_no = MAX(max_no,no);
		min_no = MIN(min_no,no);
		//vpos
		if(fread(&vpos,sizeof(vpos),1,com_f) <= 0){
			fputs("[chat/init]failed to read comment vpos.\n",log);
			return FALSE;
		}
		vpos = SDL_SwapLE32(vpos);
		//�����̏ꏊ
		if(fread(&location,sizeof(location),1,com_f) <= 0){
			fputs("[chat/init]failed to read comment location.\n",log);
			return FALSE;
		}
		location = SDL_SwapLE32(location);
		//�T�C�Y
		if(fread(&size,sizeof(size),1,com_f) <= 0){
			fputs("[chat/init]failed to read comment size.\n",log);
			return FALSE;
		}
		size = SDL_SwapLE32(size);
		//�F
		if(fread(&color,sizeof(color),1,com_f) <= 0){
			fputs("[chat/init]failed to read comment color.\n",log);
			return FALSE;
		}
		color = SDL_SwapLE32(color);
		//������
		if(fread(&str_length,sizeof(str_length),1,com_f) <= 0){
			fputs("[chat/init]failed to read comment length.\n",log);
			return FALSE;
		}
		str_length = SDL_SwapLE32(str_length);
		//������
		str = malloc(str_length);
		if(str == NULL){
			fputs("[chat/init]failed to malloc for comment text.\n",log);
			return FALSE;
		}
		if(fread(str,str_length,1,com_f) <= 0){
			fputs("[chat/init]failed to read comment text.\n",log);
			return FALSE;
		}
		//�ϐ��Z�b�g
		item->no = no;
		item->vpos = vpos;
		item->location = location;
		item->size = size;
		item->color = color;
		item->str = str;
		/*�����������*/
		if(location != CMD_LOC_DEF){
			item->vstart = vpos;
			item->vend = vpos + TEXT_SHOW_SEC - TEXT_AHEAD_SEC;
		}else{
			item->vstart = vpos - TEXT_AHEAD_SEC;
			item->vend = item->vstart + TEXT_SHOW_SEC;
		}
		if (video_length != 0){
			int fix = item->vend - video_length;
			if(fix > 0){
				item->vend -= fix;
				item->vpos -= fix;
				item->vstart -= fix;
				fprintf(log,"[chat/fix]comment %d time adjusted : %d units.\n",i, fix);
			}
		}
		/*�����������@�����*/
	}
	fclose(com_f);
	chat->max_no = max_no;
	chat->min_no = min_no;
	return TRUE;
}

void closeChat(CHAT* chat){
	int i;
	int max_item = chat->max_item;
	for(i=0;i<max_item;i++){
		free((void*)chat->item[i].str);
	}
	free(chat->item);
}

/*
 * �C�e���[�^�����Z�b�g����B
 */
void resetChatIterator(CHAT* chat){
	chat->iterator_index = 0;
}
/*
 * �C�e���[�^�𓾂�
 */
CHAT_ITEM* getChatShowed(CHAT* chat,int now_vpos){
	int *i = &chat->iterator_index;
	int max_item = chat->max_item;
	CHAT_ITEM* item;
	for(;*i<max_item;(*i)++){
		item = &chat->item[*i];
		if(now_vpos >= item->vstart && now_vpos <= item->vend && !item->showed){
			return item;
		}
	}
	return NULL;
}

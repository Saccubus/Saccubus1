#include "chat.h"
#include "chat_slot.h"
#include "process_chat.h"
#include "../mydef.h"
#include "../comment/com_surface.h"
#include "../nicodef.h"
#include "../util.h"
#include <SDL/SDL.h>
#include <stdio.h>
#include <string.h>

/*
 * �o�� CHAT_SLOT slot ���ڐݒ�
 * �o�� CHAT_ITEM slot->item �̈�m�ہA���ڐݒ�
 */
int initChatSlot(FILE* log,CHAT_SLOT* slot,int max_slot,CHAT* chat){
	slot->max_item=max_slot;
	slot->chat = chat;
	slot->item = malloc(sizeof(CHAT_SLOT_ITEM) * max_slot);
	if(slot->item == NULL){
		fputs("failed to malloc for comment slot.\n",log);
		return FALSE;
	}
	int i;
	CHAT_SLOT_ITEM* item;
	for(i=0;i<max_slot;i++){
		item = &slot->item[i];
		item->used = FALSE;
		item->slot = slot;
		item->surf=NULL;
	}
	return TRUE;
}
void closeChatSlot(CHAT_SLOT* slot){
	int i;
	CHAT_SLOT_ITEM* item;
	if(slot->item!=NULL){
		for(i=0;i<slot->max_item;i++){
			item = &slot->item[i];
			if (item->surf != NULL){
				SDL_FreeSurface(item->surf);
			}
		}
		//�A�C�e���������B
		free(slot->item);
	}
}

void deleteChatSlot(CHAT_SLOT* slot,CHAT_SLOT_ITEM* item){
	item->chat_item=NULL;
	SDL_FreeSurface(item->surf);
	item->surf = NULL;
	item->used = FALSE;
}

void deleteChatSlotFromIndex(CHAT_SLOT* slot,int index){
	CHAT_SLOT_ITEM* item = &slot->item[index];
	deleteChatSlot(slot,item);
}

/*
 * �X���b�g�ɒǉ�����B
 */
CHAT_SLOT_ITEM* addChatSlot(DATA* data,CHAT_SLOT* slot,CHAT_ITEM* item,int video_width,int video_height){
	//����������ꂽ�B
	item->showed = TRUE;
	if(slot->max_item <= 0){
		return (CHAT_SLOT_ITEM*)NULL;
	}
	// �����s�̏ꍇ�ɂ�next_y_diff�͎g�p���Ȃ���font_h_ratio or fix_font_size �Œ���
	SDL_Surface* surf = makeCommentSurface(data,item,video_width,video_height);
	/*�J���X���b�g������*/
	int i;
	int cnt = -1;
	int slot_max = slot->max_item;
	for(i=0;i<slot_max;i++){
		if(!slot->item[i].used){
			cnt = i;
			break;
		}
		if(cnt < 0 || slot->item[cnt].chat_item->vend > slot->item[i].chat_item->vend){
			cnt = i;
		}
	}
	CHAT_SLOT_ITEM* slot_item = &slot->item[cnt];
	/*�󂫂�������΋����I�ɍ��B*/
	if(slot_item->used){
		deleteChatSlotFromIndex(slot,cnt);
	}
	//���̎��_�Œǉ�
	slot_item->chat_item = item;
	slot_item->surf = surf;
	/*
	 * ���敝�̃X�P�[�� �v�Z
	 */
	double width_scale = video_width / data->nico_width_now;
	/*
	 * �e�����[�h�̍����̐ݒ�
	 * 16:9��ue,shita�R�}���h�̏ꍇ�͏㉺�Ɍ��؂��ݒ���\
	 */
	int limit_height = video_height;
	if(item->location != CMD_LOC_DEF && !data->original_resize){
		limit_height = data->limit_height * width_scale;
		// data->limit_height = 384 or 385?
	}
	int y_min = (video_height - limit_height) >> 1;
	int y_max = y_min + limit_height;
	/*���P�[�V�����ŕ���*/
	int y;
	if(item->location == CMD_LOC_BOTTOM){
		y = y_max - surf->h;
	}else {
		y = y_min;
	}
	// ���̍����Ƃ̍�����ݒ�
	int next_y_diff = 1;
	if(!data->original_resize){
		// next_h_ratio��next_h_ptxcel�����Z����
		next_y_diff = data->next_h_pixel
					+ data->font_pixel_size[item->size] * width_scale * data->next_h_rate / 100;
		if(next_y_diff < 1){
			next_y_diff = 1;
		}
	}
	int running;
	do{
		running = FALSE;
		for(i=0;i<slot_max;i++){
			CHAT_SLOT_ITEM* other_slot = &slot->item[i];
			if(!other_slot->used){
				continue;
			}
			const CHAT_ITEM* other_item = other_slot->chat_item;
			int other_y = other_slot->y;
			/*�����������*/
			if(other_y + other_slot->surf->h <= y){
				continue;
			}
			if(y + surf->h <= other_y){
				continue;
			}
			if(other_item->location != item->location){
				continue;
			}
			int start = MAX(other_item->vstart,item->vstart);
			int end = MIN(other_item->vend,item->vend);
			int obj_x_t1 = getX(start,slot_item,video_width);
			int obj_x_t2 = getX(end,slot_item,video_width);
			int o_x_t1 = getX(start,other_slot,video_width);
			int o_x_t2 = getX(end,other_slot,video_width);
			//�����蔻��
			if ((obj_x_t1 <= o_x_t1 + other_slot->surf->w && o_x_t1 <= obj_x_t1 + surf->w)
					|| (obj_x_t2 <= o_x_t2 + other_slot->surf->w && o_x_t2 <= obj_x_t2 + surf->w)){
				if(item->location == CMD_LOC_BOTTOM){
					y = other_y - surf->h - next_y_diff;
				}else{
					y = other_y + other_slot->surf->h + next_y_diff;
				}
				running = TRUE;
				break;
			}
		}
	}while(running);
	/*
	 * ����������ʓ��ɖ�����Ζ��Ӗ��B
	 * 	�A�����؂ꂽ�ꍇ��OK
	 */
	if(y < y_min || y+surf->h > y_max){
		//�͈͂𒴂��Ă�̂ŁA�����_���ɔz�u�B(�e�����[�h)
		y = y_min + ((rnd() & 0xffff) * (limit_height - surf->h)) / 0xffff;
	}
	//�ǉ�
	slot_item->used = TRUE;
	slot_item->y = y;
	return slot_item;
}
/*
 * �C�e���[�^�����Z�b�g����B
 */
void resetChatSlotIterator(CHAT_SLOT* slot){
	slot->iterator_index = 0;
}
/*
 * �C�e���[�^�𓾂�
 */
CHAT_SLOT_ITEM* getChatSlotErased(CHAT_SLOT* slot,int now_vpos){
	int i = slot->iterator_index;
	int max_item = slot->max_item;
	CHAT_ITEM* item;
	CHAT_SLOT_ITEM* slot_item;
	for(; i<max_item; i++){
		slot_item = &slot->item[i];
		if(!slot_item->used){
			continue;
		}
		item = slot_item->chat_item;
		if(item==NULL)continue;
		if(now_vpos < item->vstart || now_vpos > item->vend){
			slot->iterator_index =i;
			return slot_item;
		}
	}
	slot->iterator_index =i;
	return NULL;
}

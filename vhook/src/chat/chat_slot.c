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
	slot->com_type = chat->com_type;
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
	for(i=0;i<slot->max_item;i++){
		item = &slot->item[i];
		SDL_FreeSurface(item->surf);
	}
	//�A�C�e���������B
	free(slot->item);
}

void deleteChatSlot(CHAT_SLOT_ITEM* item,FILE* log){
	CHAT_ITEM* citem = item->chat_item;
	if(log){
		fprintf(log,"[chat_slot/addChat]comment %d vpos:%d color:%d:#%06x %5s %6s  %d - %d(vpos:%d) erased.\n",
			citem->no,citem->vstart-TEXT_AHEAD_SEC,citem->color,convSDLcolor(citem->color24),
			COM_LOC_NAME[citem->location],COM_FONTSIZE_NAME[citem->size],
			citem->vstart,citem->vend,citem->vpos);
		fflush(log);
	}
	item->chat_item=NULL;
	SDL_FreeSurface(item->surf);
	item->surf = NULL;
	item->used = FALSE;
}
/*
void deleteChatSlotFromIndex(CHAT_SLOT* slot,int index){
	CHAT_SLOT_ITEM* item = &slot->item[index];
	deleteChatSlot(slot,item);
}
*/
/*
 * �X���b�g�ɒǉ�����B
 */
int addChatSlot(DATA* data,CHAT_SLOT* slot,CHAT_ITEM* item,int video_width,int video_height){
	//����������ꂽ�B
	item->showed = TRUE;
	if(slot->max_item <= 0){
		return 0;
	}
	SDL_Surface* surf = makeCommentSurface(data,item,video_width,video_height);
	if(surf == NULL){
		return 0;
	}
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
		deleteChatSlot(slot_item,data->log);
	}
	//���̎��_�Œǉ�
	slot_item->chat_item = item;
	slot_item->surf = surf;
	// �e�����[�h�̍����̐ݒ�@16:9�ŃI���W�i�����T�C�Y�łȂ��ꍇ�͏㉺�ɂ͂ݏo��
	int limit_height = video_height;
	double scale = data->width_scale;
	if(!data->original_resize){
		//comment area height is independent from video height
		limit_height = NICO_HEIGHT * scale;
	}
	int y_min = (video_height>>1) - (limit_height>>1);
	int y_max = y_min + limit_height +
		limit_height/NICO_HEIGHT;	//�R�����g�̍�����385=384+1 ���ɂ͂ݏo��
	/*���P�[�V�����ŕ���*/
	int y;
	if(item->location == CMD_LOC_BOTTOM){
		y = y_max - surf->h;
	}else{
		y = y_min;
	}
	if(data->debug)
	fprintf(data->log,"[chat_slot/add speed0]width %d, height %d, widthscale %.3f, limitheight %d, y_min %d, y_max %d y %d\n",
		video_width, video_height, scale, limit_height, y_min, y_max, y);
	setspeed(data->comment_speed,slot_item,video_width,data->nico_width_now,scale);
	if(data->debug){
		fprintf(data->log,"[chat_slot/add speed1]comment speed %.2fpix/sec.\n",slot_item->speed*100.0);
		fprintf(data->log,"[chat_slot/add speed2]vpos %d..%d(%d) duration(%d)\n",
			item->vstart,item->vend,item->vpos,(item->vend+1-item->vstart));
	}
	int running;
	int first_comment=TRUE;
	CHAT_SLOT_ITEM* bang_slot = NULL;
	int start = 0;
	int end = 0;
	int bang_vpos = 0;
	int bang_end = 0;
	double bang_xpos[2];
	do{
		running = FALSE;
		first_comment=TRUE;
		for(i=0;i<slot_max;i++){
			CHAT_SLOT_ITEM* other_slot = &slot->item[i];
			if(!other_slot->used){
				continue;
			}
			const CHAT_ITEM* other_item = other_slot->chat_item;
			int other_y = other_slot->y;
			int other_y_next1 = other_y + other_slot->surf->h;
			/*�����������*/
			if(other_item->location != item->location){	//�ʃ��P�[�V����
				continue;
			}
			//���ꃍ�P�[�V������������1�R�����g�ł͂Ȃ�
			first_comment=FALSE;
			//��2�R�����g�Ȍ�ŉ�ʈȏ�̍����͒��ׂ�܂ł��Ȃ��e����
			if(surf->h >= limit_height){
				break;
			}
			//�����̔���
			if(other_y_next1 <= y){
				continue;
			}
			if(y + surf->h <= other_y){
				continue;
			}
			//�`�F�b�N�̊J�n�ƏI��
			start = MAX(other_item->vstart,item->vstart);
			end = MIN(other_item->vend,item->vend);
			if(start > end){
				continue;
			}
			if(item->location != CMD_LOC_DEF){
				//ue shita �� X �𒲂ׂ�K�v�Ȃ��d�Ȃ�
				if(item->location == CMD_LOC_BOTTOM){
					y = other_y - surf->h;
				}else{
					y = other_y_next1;
				}
				running = TRUE;
				break;
			}

			//vend�͍Ō�̐�vpos�͗h�炮�̂ŉ���17vpos�Ƃ��Čv�Z
			end -= 3;
			int aspect_mode = data->aspect_mode;
			double x_t1 = getX(start,slot_item,video_width,scale,aspect_mode);
			double x_t2 = getX(end,slot_item,video_width,scale,aspect_mode);
			double o_x_t1 = getX(start,other_slot,video_width,scale,aspect_mode);
			double o_x_t2 = getX(end,other_slot,video_width,scale,aspect_mode);
			double dxstart[2] = {x_t1, x_t1 + surf->w};
			double dxend[2] = {x_t2, x_t2 + surf->w};
			double o_dxstart[2] = {o_x_t1, o_x_t1 + other_slot->surf->w};
			double o_dxend[2] = {o_x_t2, o_x_t2 + other_slot->surf->w};
			double dtmp[2];
			double range[2];
			if(aspect_mode==0){
				range[0] = (-16)*scale;
				range[1] = video_width+(16)*scale;
			}else{
				range[0] = (-16+64)*scale;
				range[1] = video_width+(16-64)*scale;
			}
			if(data->debug)
				fprintf(data->log,"range (%.0f,%.0f)\n",range[0],range[1]);
			//�����蔻��@�ǂ��z�������O��
			if(data->debug)
				fprintf(data->log,"at %d(end) check [%.1f,%.1f] [%.1f,%.1f]y[%d] item %d\n",
					end,dxend[0],dxend[1],o_dxend[0],o_dxend[1],other_y,other_item->no);
			if(set_crossed(dtmp,dxend,o_dxend) && d_width(dtmp)>=scale){
				if(set_crossed(bang_xpos,range,dtmp)){
					y = other_y_next1;
					running = TRUE;
					if(data->debug){
						fprintf(data->log,"--> x[%.1f,%.1f]y[%d]\n",dtmp[0],dtmp[1],other_y);
						bang_vpos = getVposItem(data,slot_item,0,dtmp[slot_item->speed>=0.0f]);
						bang_end = end;
						bang_slot = other_slot;
					}
					break;
				}
				if(data->debug){
					fprintf(data->log,"out of range [%.1f,%.1f]\n",dtmp[0],dtmp[1]);
				}
			}
			if(data->debug)
				fprintf(data->log,"at %d(start) check [%.1f,%.1f] [%.1f,%.1f]y[%d] item %d\n",
					start,dxstart[0],dxstart[1],o_dxstart[0],o_dxstart[1],other_y,other_item->no);
			if(set_crossed(dtmp,dxstart,o_dxstart) && d_width(dtmp)>=scale){
				if(set_crossed(bang_xpos,range,dtmp)){
					y = other_y_next1;
					running = TRUE;
					if(data->debug){
						fprintf(data->log,"--> [%.1f,%.1f]y[%d]\n",dtmp[0],dtmp[1],other_y);
						bang_vpos = start;
						bang_end = end;
						bang_slot = other_slot;
					}
					break;
				}
				if(data->debug){
					fprintf(data->log,"out of range [%.1f,%.1f]\n",dtmp[0],dtmp[1]);
				}
			}
		}
	}while(running);
	/*����������ʓ��ɖ�����Ζ��Ӗ��B*/
	if(first_comment){
		//��1�R�����g�͉�ʊO�ł��e�������Ȃ�
		fprintf(data->log,"[chat_slot/add first]comment %d %s %s y=%d\n",
			item->no,COM_LOC_NAME[item->location],COM_FONTSIZE_NAME[item->size],y);
	}else
	if(y < y_min || y+surf->h > y_max){	// �͈͂𒴂��Ă�̂ŁA�����_���ɔz�u�B
		fprintf(data->log,"[chat_slot/add random]comment %d %s %s y=%d -> random\n",
			item->no,COM_LOC_NAME[item->location],COM_FONTSIZE_NAME[item->size],y);
		y = y_min + ((rnd() & 0xffff) * (limit_height - surf->h)) / 0xffff;
	}
	//�ǉ�
	slot_item->used = TRUE;
	slot_item->y = y;
	fprintf(data->log,"[chat_slot/add]comment %d %s %s y=%d\n",
		item->no,COM_LOC_NAME[item->location],COM_FONTSIZE_NAME[item->size],y);
	if(data->debug && bang_slot!=NULL){
		int bang_x1 = bang_xpos[0];
		int bang_x2 = bang_xpos[1];
		CHAT_ITEM* item = bang_slot->chat_item;
		fprintf(data->log,"[chat_slot/add bang]bang_vpos:%d..%d(%d) item %d x[%d,%d] y[%d] %d..%d(vpos:%d)\n",
			bang_vpos,bang_end,(bang_end+1-bang_vpos),
			item->no,bang_x1,bang_x2,bang_slot->y,
			item->vstart,item->vend,item->vpos);
	}
	return y;
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
	int *i = &slot->iterator_index;
	int max_item = slot->max_item;
	CHAT_ITEM* item;
	CHAT_SLOT_ITEM* slot_item;
	for(;*i<max_item;(*i)++){
		slot_item = &slot->item[*i];
		if(!slot_item->used){
			continue;
		}
		item = slot_item->chat_item;
		if(item==NULL)continue;
		if(now_vpos < item->vstart || now_vpos > item->vend){
			// slot��������K�v�͂Ȃ��H
			//return slot_item;
		}
	}
	return NULL;
}
//���Q��pair�̏d�Ȃ���ŏ���pair�ɐݒ肷��B
int set_crossed(double ret[2],double pair1[2],double pair2[2]){
	ret[0] = MAX(pair1[0],pair2[0]);
	ret[1] = MIN(pair1[1],pair2[1]);
	return ret[0] < ret[1];
}
//��
double d_width(double pair[2]){
	return pair[1]-pair[0];
}

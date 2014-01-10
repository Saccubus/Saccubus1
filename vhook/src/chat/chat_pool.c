/*
 * chat_pool.c
 *
 *  Created on: 2012/08/08
 *      Author: orz
 */
#include "chat_pool.h"
#include "../mydef.h"

int initChatPool(FILE* log,CHAT* chat,int max_pool){
	//fprintf(log,"entered initChatPool %08x, %d\n",(unsigned int)chat,max_pool);
	//fprintf(log,"malloc %d+%d*%d \n",sizeof(CHAT_POOL),sizeof(CHAT_ITEM *),max_pool);
	CHAT_POOL* pool = malloc(sizeof(CHAT_POOL));
	CHAT_ITEM** item_pointer = malloc(sizeof(CHAT_ITEM *) * max_pool);
	if(pool == NULL || item_pointer == NULL){
		fputs("[chat_pool/init]failed to malloc for comment pool.\n",log);
		return FALSE;
	}
	pool->max_size = max_pool;
	pool->num_item = 0;
	pool->index = 0;
	pool->is_sorted = TRUE;
	pool->itemp = item_pointer;
	int i;
	for(i=0;i<max_pool;i++){
		item_pointer[i] = NULL;
	}
	chat->pool = pool;
	fprintf(log,"[chat_pool/init]initialized.\n");
	return TRUE;
}

void resetPoolIterator(CHAT_POOL* pool){
	pool->num_item = 0;
	pool->index = 0;
	pool->is_sorted = TRUE;
}

void addChatPool(DATA* data,CHAT_POOL* pool,CHAT_ITEM* chat_item){
	if(pool->num_item < pool->max_size){
		CHAT_ITEM** item = pool->itemp;
		item[pool->num_item++] = chat_item;
		chat_item->pooled = TRUE;
		if(pool->num_item>1)
			pool->is_sorted = FALSE;
		if(data->debug)
			fprintf(data->log,"[chat_pool/add]num %d item %d\n",pool->num_item,chat_item->no);
	}else{
		fprintf(data->log,"[chat_pool/add]failed num %d item %d\n",pool->num_item,chat_item->no);
	}
}

CHAT_ITEM* getChatPooled(DATA* data,CHAT_POOL* pool,int now_vpos){
	CHAT_ITEM** item = pool->itemp;
	CHAT_ITEM* ret_item = NULL;
	if(!pool) return NULL;
	if(pool->num_item <= 0) return NULL;
	if(pool->index >= pool->num_item) {
		// empty
		pool->index = pool->num_item = 0;
		pool->is_sorted = TRUE;
		return NULL;
	}
	if(pool->num_item==1)
		pool->is_sorted = TRUE;
	// not sorted?
	if(!pool->is_sorted){
		//debug
		fprintf(data->log,"[chat_pool/get]sort(index=%d,num=%d) vpos=%d \n",pool->index,pool->num_item,now_vpos);
		//start sorting
		//first: by vstart, second: by no.
		int min_vstart;
		int vstart;
		int index;
		int done = pool->index;
		int target;
		int new_num = 0;
		for(done=pool->index; done<pool->num_item-1; done++){
			target = done;
			min_vstart = item[done]->vstart;
			for(index=done+1;index<pool->num_item;index++){
				vstart = item[index]->vstart;
				if (vstart < min_vstart
						||(vstart==min_vstart && item[index]->no < item[target]->no)){
					target = index;
					min_vstart = vstart;
				}
			}
			ret_item = item[target];
			item[target] = item[done];
			item[new_num++] = ret_item;
		}
		item[new_num++] = item[done++];
		//sort done.
		pool->is_sorted = TRUE;
		pool->num_item = new_num;
		pool->index = 0;
		//debug
		//fprintf(data->log,"[chat_pool/get]sorted(index=%d,num=%d)\n",pool->index,pool->num_item);
	}
	// sorted
	if(item[pool->index]->vappear <= now_vpos){
		if(data->debug)
			fprintf(data->log,"[chat_pool/get]return(%d)\n",pool->index);
		ret_item = item[pool->index++];
		return ret_item;
	}
	//‚à‚µ vappear > now_vpos‚Ì‚à‚Ì‚ªæ“ª‚É‚ ‚ê‚Îæ“Ç‚Ý‚³‚ê‚½‚¾‚¯‚¾‚©‚ç
	//ŽŸ‚Ì“Ç‚Ýž‚ÝŽž‚Ü‚Åƒv[ƒ‹‚µ‚Ä‚¨‚­
	//debug
	//fprintf(data->log,"[chat_pool/get]last(index=%d,num=%d), return NULL\n",pool->index,pool->num_item);
	return NULL;
}

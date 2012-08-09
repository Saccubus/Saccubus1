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
	CHAT_POOL* pool = malloc(sizeof(CHAT_POOL) + sizeof(CHAT_ITEM *) * max_pool);
	if(pool == NULL){
		fputs("[chat_pool/init]failed to malloc for comment pool.\n",log);
		return FALSE;
	}
	pool->max_size = max_pool;
	pool->num_item = 0;
	pool->index = 0;
	int i;
	for(i=0;i<max_pool;i++){
		pool->item[i] = NULL;
	}
	chat->pool = pool;
	fprintf(log,"[chat_pool/init]pool %s\n",chat->com_type);
	return TRUE;
}

void resetPoolIterator(CHAT_POOL* pool){
	pool->num_item = 0;
	pool->index = 0;
}

void addChatPool(DATA* data,CHAT_POOL* pool,CHAT_ITEM* chat_item){
	if(pool->num_item < pool->max_size){
		pool->item[pool->num_item++] = chat_item;
		chat_item->showed = TRUE;
		pool->index = 0;
		if(data->debug)
			fprintf(data->log,"[chat_pool/add]num %d item %d\n",pool->num_item,chat_item->no);
	}else{
		fprintf(data->log,"[chat_pool/add]failed num %d item %d\n",pool->num_item,chat_item->no);
	}
}

CHAT_ITEM* getChatPooled(DATA* data,CHAT_POOL* pool){
	CHAT_ITEM** item = &pool->item[0];
	CHAT_ITEM* ret_item = NULL;
	if(!pool) return NULL;
	if(pool->num_item <= 0) return NULL;
	if(pool->index >= pool->num_item) return NULL;
	if(pool->index > 0 || pool->num_item==1){
		if(data->debug)
			fprintf(data->log,"[chat_pool/get]return(%d)\n",pool->index);
		ret_item = item[pool->index++];
		ret_item->showed = FALSE;
		return ret_item;
	}
	//this is first call, sort all item
	//first: by vpos, second: by no.
	int min_vpos;
	int vpos;
	int index;
	int done;
	int target;
	for(done=0;done<pool->num_item-1;done++){
		target = done;
		min_vpos = item[done]->vpos;
		for(index=done+1;index<pool->num_item;index++){
			vpos = item[index]->vpos;
			if (vpos < min_vpos
					||(vpos==min_vpos && item[index]->no < item[target]->no)){
				target = index;
				min_vpos = vpos;
			}
		}
		if(target==done) continue;
		CHAT_ITEM* tmp = item[done];
		item[done] = item[target];
		item[target] = tmp;
	}
	pool->index = 1;
	item[0]->showed = FALSE;
	if(data->debug)
		fprintf(data->log,"[chat_pool/get]return first\n");
	return item[0];
}

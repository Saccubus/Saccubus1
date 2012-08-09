/*
 * chat_pool.h
 *
 *  Created on: 2012/08/08
 *      Author: orz
 */

#ifndef CHAT_POOL_H_
#define CHAT_POOL_H_

#include "../main.h"
#include "../mydef.h"
#include "chat.h"

struct CHAT_POOL {
	int max_size;
	int num_item;
	int index;
	CHAT_ITEM* item[1];
};

int initChatPool(FILE* log,CHAT* chat,int max_pool);
void resetPoolIterator(CHAT_POOL* pool);
void addChatPool(DATA* data,CHAT_POOL* pool,CHAT_ITEM* chat_item);
CHAT_ITEM* getChatPooled(DATA* data,CHAT_POOL* pool);


#endif /* CHAT_POOL_H_ */

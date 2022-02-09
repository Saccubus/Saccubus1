#include "mydef.h"
#include "process.h"
#include "chat/process_chat.h"
#include "chat/chat.h"
#include "chat/chat_slot.h"

//プロセス
int process(DATA* data,SDL_Surface* surf,const int now_vpos){
	FILE* log = data->log;
	//ユーザコメント
	if(!process_chat(data,&data->user, surf,now_vpos)){
		fprintf(log,"[process/process]failed to process %s comment.\n", "User");
		return FALSE;
	}
	//オプショナルコメント
	if(!process_chat(data, &data->optional, surf, now_vpos)){
		fprintf(log,"[process/process]failed to process %s comment.\n", "Optional");
		return FALSE;
	}
	//オーナコメント
	if(!process_chat(data,&data->owner, surf,now_vpos)){
		fprintf(log,"[process/process]failed to process %s comment.\n", "Owner");
		return FALSE;
	}
	return TRUE;
}


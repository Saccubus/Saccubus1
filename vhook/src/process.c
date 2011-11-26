#include "mydef.h"
#include "process.h"
#include "chat/process_chat.h"
#include "chat/chat.h"
#include "chat/chat_slot.h"

//プロセス
int process(DATA* data,SDL_Surface* surf,const int now_vpos){
	FILE* log = data->log;
	//ユーザコメント
	if(!process_chat(data,&data->user, "usr", surf, now_vpos)){
		fprintf(log,"[process/process]failed to process User comment.\n");
		return FALSE;
	}
	//オーナコメント
	if(!process_chat(data,&data->owner, "own", surf, now_vpos)){
		fprintf(log,"[process/process]failed to process Owner comment.\n");
		return FALSE;
	}
	//オプショナルコメント
	if(!process_chat(data, &data->optional, "opt", surf, now_vpos)){
		fprintf(log,"[process/process]failed to process Optionals comment.\n");
		return FALSE;
	}
	return TRUE;
}


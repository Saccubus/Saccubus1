#include "mydef.h"
#include "process.h"
#include "chat/process_chat.h"
#include "chat/chat.h"
#include "chat/chat_slot.h"

//�v���Z�X
int process(DATA* data,SDL_Surface* surf,const int now_vpos){
	//���[�U�R�����g
	if(data->enable_user_comment || data->enable_owner_comment){
		if(!chat_process(data,surf,now_vpos)){
			fputs("[process/process]failed to process comment.\n",data->log);
			return FALSE;
		}
	}
	//�I�[�i�R�����g
//	if(data->enable_owner_comment){
//	}
	return TRUE;
}


/*
 * wakuiro.c
 *
 *  Created on: 2012/08/24
 *      Author: orz
 */
#include "wakuiro.h"

void set_wakuiro(const char* str,DATA* data){
	int n = 0;
	const char* tmp = str;
	while(tmp!=NULL){
		tmp = strstr(tmp+1,"/");
		n++;
	}
	//データ領域確保
	data->wakuiro_dat = (unsigned int*)malloc(n * sizeof(unsigned int) * 2);
	if(data->wakuiro_dat == NULL){
		fprintf(data->log,"[set_wakuiro]failed to alloc wakuiro_dat.\n");
		return;
	}
	unsigned int* waku = data->wakuiro_dat;
	unsigned int color;
	unsigned int com_no;
	if(n==1){
		waku[0] = 1;
		color = strtol(str,NULL,0);
		if(strcmp(str,"0x")==0){
			color = SET_WAKUIRO(WAKUIRO_COLORCODE,color);	//24ビットカラーコード
		}else{
			color = SET_WAKUIRO(WAKUIRO_COLORNAME,color);	//カラー名
		}
		waku[1] = color;
		if(data->debug)
			fprintf(data->log,"[set_wakuiro]All wakuiro %08x:%08x\n",waku[0],waku[1]);
		return;
	}
	waku[0] = n;
	waku[1] = 0;	//not used
	if(data->debug)
		fprintf(data->log,"[set_wakuiro]wakuiro_dat %d\n",waku[0]);
	int i;
	tmp = str;
	char* nptr;
	n <<= 1;
	for(i = 2; i < n; i+=2){
		//waku[i]　　：　コメント番号(0)または文字色指定(カラー名,カラーコード)
		//waku[i+1]：　枠色指定
		if(tmp[0]=='_'){
			// tmp[0] 文字色指定
			tmp++;
			color = strtol(tmp,&nptr,0);
			if(strncmp(tmp,"0x",2)==0){
				color = SET_WAKUIRO(WAKUIRO_COLORCODE,color);	//24ビットカラーコード
			}else{
				color = SET_WAKUIRO(WAKUIRO_COLORNAME,color);	//カラー名
			}
			waku[i] = color;
		}else{
			// tmp[0] コメント番号
			com_no = strtol(tmp,&nptr,10);
			int waku_type = WAKUIRO_OWNER;
			if(*nptr=='u'){
				waku_type = WAKUIRO_USER;
				nptr++;
			}else if(*nptr=='n'){
				waku_type = WAKUIRO_OPTIONAL;
				nptr++;
			}
			com_no = SET_WAKUIRO(waku_type,com_no);
			waku[i] = com_no;
		}
		tmp = nptr + 1;		//skip '_'
		// 枠色指定
		color = strtol(tmp,&nptr,0);
		if(strncmp(tmp,"0x",2)==0){
			color = SET_WAKUIRO(WAKUIRO_COLORCODE,color);	//24ビットカラーコード
		}else{
			color = SET_WAKUIRO(WAKUIRO_COLORNAME,color);	//カラー名
		}
		waku[i+1] = color;
		tmp = nptr + 1;		//skip '/'
		if(data->debug)
			fprintf(data->log,"[set_wakuiro]waku[%d] %08x:%08x\n",i,waku[i],waku[i+1]);
	}
}

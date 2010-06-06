/*
 * Windows�pCookie�擾�v���O����
 * copyright (c) 2008 �Ձi�v�T�C�j
 *
 * IE�̃N�b�L�[���擾���ĕ\������v���O�����ł��B
 *
 * ���̃t�@�C���́u������΂��v�̈ꕔ�ł���A
 * ���̃\�[�X�R�[�h��GPL���C�Z���X�Ŕz�z����܂��ł��B
 */
#include <windows.h>
#include <stdio.h>

//�j�R�j�R����URL
#define NICO_URL "http://www.nicovideo.jp/"
//�O�̂��߈��폜���邽�߂̎g���N�b�L�[�B
#define NICO_CLEAR_COOKIE "dummy; expires=Thu, 1-Jan-1970 00:00:00 GMT; domain=.nicovideo.jp; path=/;"
#define COOKIE_MAX 4096

int getCookie(void* dynamic){
	//�N�b�L�[�擾�֐����擾
	int (*get_cookie)(const char* url,const char* str,char* buff,int* size) = (void*)GetProcAddress((HMODULE)dynamic, "InternetGetCookieA");
	if(!get_cookie){
    	fputs("failed to load InternetGetCookieA",stderr);
        return -1;
	}
	//�o�b�t�@�p�������̊m�ہB
	char* str = malloc(COOKIE_MAX);
	if(!str){
    	fputs("failed to malloc memory",stderr);
        return -1;
	}
	int size = COOKIE_MAX;
	if(get_cookie(NICO_URL,NULL,str,&size)){
		puts(str);
	}else if(size > COOKIE_MAX){//�o�b�t�@������Ȃ��Ď��s�����Ǝv����B
		//�o�b�t�@�Ċm��
		str = realloc(str,size);
		if(!str){
	    	fputs("failed to realloc memory",stderr);
	        return -1;
		}
		if(get_cookie(NICO_URL,NULL,str,&size)){
			puts(str);
		}else{//�Ȃ񂩃\�[�X�������������Ǔ����΂�낵�B
	    	fputs("failed to get cookie",stderr);
	    	free(str);
	        return -1;
		}
	}else{
    	fputs("failed to get cookie",stderr);
	   	free(str);
		return -1;
	}
	free(str);
	return 0;
}

int setCookie(void* dynamic,const char* str){
	//�N�b�L�[�擾�֐����擾
	int (*set_cookie)(const char* url,const char* name,const char* data) = (void*)GetProcAddress((HMODULE)dynamic, "InternetSetCookieA");
	if(!set_cookie){
    	fputs("failed to load InternetGetCookieA",stderr);
        return -1;
	}
	//���O�ƃf�[�^�̕���
	int i=0;
	int length = strlen(str);
	char* tmp = malloc(length+1);
	memcpy(tmp,str,length);
	tmp[length] = '\0';
	char* name = tmp;
	char* data = NULL;
	for(i=0;i<length;i++){
		if(tmp[i] == '='){
			tmp[i] = '\0';
			data = &tmp[i+1];
			break;
		}
	}
	if(data == NULL){
    	fputs("Cookie str is invalid.",stderr);
    	free(tmp);
		return -1;
	}
	//�o�^����O�ɂƂ肠�����폜���Ă݂�
	if(set_cookie(NICO_URL,name,NICO_CLEAR_COOKIE) == FALSE){
    	fputs("failed to clear cookie.",stderr);
    	free(tmp);
		return -1;
	}
	//���ۂɓo�^���Ă݂�
	if(set_cookie(NICO_URL,name,data) == TRUE){
    	free(tmp);
		return 0;
	}else{
    	fputs("failed to set cookie.",stderr);
    	free(tmp);
		return -1;
	}
}

int main(int argc,char* argv[]){
	//wininet.dll�̃_�C�i�~�b�N�ǂݍ��݁B
    void* dynamic = (void*)LoadLibrary("wininet.dll");
    if (!dynamic) {
    	fputs("failed to load wininet.dll",stderr);
        return -1;
    }
	int ret = 0;
	if(argc > 1){/* set����N�b�L�[�����݂��� */
		ret = setCookie(dynamic,argv[1]);
	}else{/*���݂��Ȃ�*/
		ret = getCookie(dynamic);
	}
	//�J���A�I���B
	FreeLibrary((HMODULE)dynamic);
	return ret;
}

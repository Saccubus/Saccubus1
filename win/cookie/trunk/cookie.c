/*
 * Windows用Cookie取得プログラム
 * copyright (c) 2008 ψ（プサイ）
 *
 * IEのクッキーを取得して表示するプログラムです。
 *
 * このファイルは「さきゅばす」の一部であり、
 * このソースコードはGPLライセンスで配布されますです。
 */
#include <windows.h>
#include <stdio.h>

//ニコニコ動画URL
#define NICO_URL "http://www.nicovideo.jp/"
//念のため一回削除するための使うクッキー。
#define NICO_CLEAR_COOKIE "dummy; expires=Thu, 1-Jan-1970 00:00:00 GMT; domain=.nicovideo.jp; path=/;"
#define COOKIE_MAX 4096

int getCookie(void* dynamic){
	//クッキー取得関数を取得
	int (*get_cookie)(const char* url,const char* str,char* buff,int* size) = (void*)GetProcAddress((HMODULE)dynamic, "InternetGetCookieA");
	if(!get_cookie){
    	fputs("failed to load InternetGetCookieA",stderr);
        return -1;
	}
	//バッファ用メモリの確保。
	char* str = malloc(COOKIE_MAX);
	if(!str){
    	fputs("failed to malloc memory",stderr);
        return -1;
	}
	int size = COOKIE_MAX;
	if(get_cookie(NICO_URL,NULL,str,&size)){
		puts(str);
	}else if(size > COOKIE_MAX){//バッファが足りなくて失敗したと思われる。
		//バッファ再確保
		str = realloc(str,size);
		if(!str){
	    	fputs("failed to realloc memory",stderr);
	        return -1;
		}
		if(get_cookie(NICO_URL,NULL,str,&size)){
			puts(str);
		}else{//なんかソースが多少汚いけど動けばよろし。
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
	//クッキー取得関数を取得
	int (*set_cookie)(const char* url,const char* name,const char* data) = (void*)GetProcAddress((HMODULE)dynamic, "InternetSetCookieA");
	if(!set_cookie){
    	fputs("failed to load InternetGetCookieA",stderr);
        return -1;
	}
	//名前とデータの分離
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
	//登録する前にとりあえず削除してみる
	if(set_cookie(NICO_URL,name,NICO_CLEAR_COOKIE) == FALSE){
    	fputs("failed to clear cookie.",stderr);
    	free(tmp);
		return -1;
	}
	//実際に登録してみる
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
	//wininet.dllのダイナミック読み込み。
    void* dynamic = (void*)LoadLibrary("wininet.dll");
    if (!dynamic) {
    	fputs("failed to load wininet.dll",stderr);
        return -1;
    }
	int ret = 0;
	if(argc > 1){/* setするクッキーが存在する */
		ret = setCookie(dynamic,argv[1]);
	}else{/*存在しない*/
		ret = getCookie(dynamic);
	}
	//開放、終了。
	FreeLibrary((HMODULE)dynamic);
	return ret;
}

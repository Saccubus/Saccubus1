/*
 * util.c
 *
 *  Created on: 2011/11/14
 *      Author: orz
 */
#include "util.h"
#include "../mydef.h"
#include <stdio.h>

int isMatchKanji(Uint16* u,Uint16* kanji){
	while(*kanji!='\0'){
		if(*kanji<=*u && *u<=*(kanji+1)){
			return TRUE;
		}
		kanji += 2;
	}
	return FALSE;
}

int isMatchZeroWidth(Uint16* u,Uint16* zero){
	while(*zero!='\0'){
		if(*zero==*u){
			return TRUE;
		}
		zero++;
	}
	return FALSE;
}

int isAscii(Uint16* u){
	if(0x0000 < *u && *u < 0x0080){
		return TRUE;
	}else{
		return FALSE;
	}
}

int isMincho(Uint16* u){
	if(0x2581<=*u && *u<=0x258f){
		return TRUE;
	}
	return FALSE;
}

int isHANKAKU(Uint16* u){
	if(0xff61<=*u && *u<=0xff9f){
		return TRUE;
	}
	return FALSE;
}

int isZeroWidth(Uint16* u){
	if(0x2029<=*u && *u<=0x202f){
		return TRUE;
	}
	if(*u==0x200b){
		return TRUE;
	}
	return FALSE;
}

int getFontType(Uint16* u,Uint16** ca,int basefont){
	if(*u == '\0'){
		return NULL_FONT;
	}
	if(isMatchKanji(u,ca[SIMSUN_FONT])){
		return SIMSUN_FONT;
	}
	if(isMatchKanji(u,ca[GULIM_FONT])){
		return GULIM_FONT;
	}
	if(isMatchKanji(u,ca[GOTHIC_FONT])){
		return GOTHIC_FONT;
	}
	if(isMatchKanji(u,ca[GEORGIA_FONT])){	//use special
		return GEORGIA_FONT;
	}
	if(isMincho(u)){
		return SIMSUN_FONT;
	}
	if(isAscii(u)){
		return ARIAL_FONT;
	}
	if(isHANKAKU(u)){
		return GOTHIC_FONT;
	}
	if(*u==0x2001){		// one of SPACE char and GOTHIC only
		return GOTHIC_FONT;
	}
	return basefont;
}

int getFirstFont(Uint16* u,int fonttype,Uint16** ca){
	while(*u!=0){
		if(isMatchKanji(u,ca[SIMSUN_FONT])){	//change
			return SIMSUN_FONT;
		}
		if(isMatchKanji(u,ca[GULIM_FONT])){	//change
			return GULIM_FONT;
		}
		if(isMatchKanji(u,ca[GOTHIC_FONT])){	//protect
			return GOTHIC_FONT;
		}
		if(isMincho(u)){
			return SIMSUN_FONT;
		}
		if(isHANKAKU(u)){	//protect
			return GOTHIC_FONT;
		}
		u++;
	}
	return fonttype;
}

Uint16 replaceSpace(Uint16 u){
	if(u == 0x02cb){
		return 0x3000;
	}else{
		return u;
	}
}

void removeZeroWidth(Uint16* str,int len){
	int i;
	Uint16* dst = str;
	for(i=0;i<len;i++){
		if(!isZeroWidth(str)){
			*dst++ = *str++;
		}else{
			str++;
		}
	}
	return;
}

int convUint16Pair(const char** unicodep,Uint16* up);
int getUint16(const char** unicodep);

int convUint16(const char* unicode, Uint16** out){
	int len = 256;
	Uint16* p = malloc(sizeof(Uint16)*2*len);
	if(p==NULL){
		*out = NULL;
		return 0;
	}
	int i;
	int n = 0;
	int uc;
	const char* start = unicode;
	for(i=0;i<len;i+=2){
		if(convUint16Pair(&start,&p[i])){
			continue;
		}
		uc = getUint16(&start);
		if(uc != UNDEFINED_FONT){
			uc &= 0xffff;
			p[i] = uc;
			p[i+1] = uc;
			continue;
		}
		break;
	}
	n = i;
	*out = malloc(sizeof(Uint16)*(n+2));
	if(*out==NULL){
		free(p);
		return 0;
	}
	for(i=0;i<n;i++){
		(*out)[i] = p[i];
	}
	(*out)[n] = '\0';
	(*out)[n+1] = '\0';
	free(p);
	return n;
}

int convUint16Pair(const char** unicodep,Uint16* up){
	int u0, u1;
	const char* ip = *unicodep;
	u0 = getUint16(&ip);
	if(u0 == UNDEFINED_FONT){
		return FALSE;
	}
	if(*ip!='-'){
		return FALSE;
	}
	ip++;
	u1 = getUint16(&ip);
	if(u1 == UNDEFINED_FONT){
		return FALSE;
	}
	up[0] = (Uint16)u0;
	up[1] = (Uint16)u1;
	*unicodep = ip;
	return TRUE;
}

int getUint16(const char** unicodep){
	int uc;
	char* endp;
	uc = strtoul(*unicodep,&endp,16);
	if(uc <=0 || 0xffff< uc){
		return UNDEFINED_FONT;
	}
	*unicodep = endp;
	return uc;
}

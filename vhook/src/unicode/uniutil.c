/*
 * util.c
 *
 *  Created on: 2011/11/14
 *      Author: orz
 */
#include <stdio.h>
#include "uniutil.h"
#include "../mydef.h"
#include "unitable.h"
#include "../nicodef.h"

int isMatchKanji(Uint16* u,Uint16* kanji){
	if(kanji==NULL)
		return FALSE;
	while(*kanji!='\0'){
		if(*kanji<=*u && *u<=*(kanji+1)){
			return TRUE;
		}
		kanji += 2;
	}
	return FALSE;
}

int isAscii(Uint16* up){
	Uint16 u = *up;
	return ((0x0000 < u && u < 0x0080) || u==0x00a0 || u==0x200c);
}

int getDetailType(int u){
	return UNITABLE[u];
}

int isGlyphExist(DATA* data,int fonttype,Uint16 u){
	TTF_Font* font = data->CAfont[fonttype][CMD_FONT_DEF];
	return (font!=NULL && TTF_GlyphIsProvided(font,u));
}

int getGlyphExist(DATA* data,Uint16 u){
	int f;
	for(f=0;f<CA_FONT_MAX;f++){
		if(isGlyphExist(data,f,u))
			return f;
	}
	return UNDEFINED_FONT;
}

/*
int isZeroWidth(Uint16* u){
	if(0x2029<=*u && *u<=0x202f){
		return TRUE;
	}
	if(*u==0x200b){
		return TRUE;
	}
	return FALSE;
}
*/
int isZeroWidthP(Uint16* u){
	return isZeroWidth(*u);
}

int isZeroWidth(Uint16 u){
	return (getDetailType(u)==ZERO_WIDTH_CHAR);
}

int cjka(Uint16 u){
	if(u < 0x0080)
		return TRUE;
	if(u > 0x3000 && u < 0xa000)
		return TRUE;
	return FALSE;
}

FontType getFontType2(Uint16* up,int basefont,DATA* data,int stable){
	Uint16 u;
	if(up==NULL || (u = *up) == '\0'){
		return NULL_FONT;
	}
/*
	if(*u==0x0020){	//Ascii space -> fix fontsize w,h
		return ARIAL_FONT|CA_TYPE_SPACE_0020;	//0020-> 00200003
	}
	if(*u==0x00a0){	//Ascii space -> fix fontsize w,h
		return ARIAL_FONT|CA_TYPE_SPACE_00A0;	//00a0-> 00a00003
	}
*/
	FontType uft = u<<16;
	if(uft==CA_TYPE_SPACE_3000){	//‘SŠp‹ó”’->fix fontsize w,h
		if(basefont<=GOTHIC_FONT)
			return uft;				// GOTHIC 30000000 (UNDEFINED 30000000)
		if(basefont<=GULIM_FONT||basefont==MINGLIU_FONT)
			return basefont|uft;		//SIMSUN 30000001 GULIM 30000002 MINGLIU 30000008
		return uft;					//otherwise GOTHIC 30000000
	}
	if(uft==CA_TYPE_SPACE_0020||uft==CA_TYPE_SPACE_00A0
		||uft==CA_TYPE_SPACE_200C||uft==CA_TYPE_SPACE_0009){
		//Ascii space -> fix fontsize w,h
		return ARIAL_FONT|uft;		//uuuu -> uuuu0003
	}
	if(0x2000<=u && u<=0x200f){	//Various width Space -> fix fontsize w,h
		return basefont|uft;	//2001..200f<<16+0000..0003;
	}
	if(stable && cjka(u))
		return basefont;
	switch(getDetailType(u)){
		case STRONG_SIMSUN_CHAR:
			//case UNDEF_OR_SIMSUN:	//ÅI“I‚É‚ÍXP,Vista,Win7‹¤’ÊŽž‚ÉSIMSUN‚É
			if(0xE758<=u && u<=0xE864){	//Simsun
				if(!isGlyphExist(data,SIMSUN_FONT,u))
					//even if glyph does not exist, fonttype should be defined
					return SIMSUN_FONT|CA_TYPE_NOGLYPH_SIMSUN;
			}
			return SIMSUN_FONT;
		case WEAK_SIMSUN_CHAR:
		case SIMSUN_NOT_CHANGE_CHAR:
			return SIMSUN_FONT;
		case MINGLIU_CHAR:	//Žb’èC³F–¾’©•Ï‰»‚·‚é(XP‚æ‚èŒã‚Í)
			if(0xE865<=u && u<=0xF8FF){	//MingLiu
				if(!isGlyphExist(data,MINGLIU_FONT,u))
					//even if glyph does not exist, fonttype should be defined
					return MINGLIU_FONT|CA_TYPE_NOGLYPH_MINGLIU;
			}
			//return MINGLIU_FONT|CA_TYPE_NOGLYPH_MINGLIU;	//debug 2014.5.9
			return MINGLIU_FONT;	//debug 2014.5.9
		case N_MINCHO_CHAR:	//–¾’©•Ï‰»‚È‚µ simsun or ngulim
			return N_MINCHO_FONT;
		case GULIM_CHAR:
			return GULIM_FONT;
		case GOTHIC_CHAR:	//ƒSƒVƒbƒN•ÛŒì
		case GOTHIC_NOT_PROTECT:	//•ÛŒì‚È‚µƒSƒVƒbƒN
			return GOTHIC_FONT;
		case ZERO_WIDTH_CHAR:
			return basefont|uft;
		case ARIAL_CHAR:
			return ARIAL_FONT;
		//use special font
		case GEORGIA_CHAR:
			return GEORGIA_FONT;
		case DEVANAGARI_CHAR:
			return DEVANAGARI;
		case TAHOMA_CHAR:
			return TAHOMA_FONT;
		case ESTRANGELO_EDESSA:
			return ESTRANGELO_EDESSA_FONT;
		case GUJARATI_CHAR:
			return GUJARATI_FONT;
		case BENGAL_CHAR:
			return BENGAL_FONT;
		case TAMIL_CHAR:
			return TAMIL_FONT;
		case LAOO_CHAR:
			return LAOO_FONT;
		case GURMUKHI_CHAR:
			return GURMUKHI_FONT;
		default:
		//include UNDEFINED_CHAR
			if(isGlyphExist(data,basefont,u))
				return basefont;
			else
				return getGlyphExist(data,u);
	}
}

FontType getFontType(Uint16* up,int basefont,DATA* data,int stable){
	if(isMatchExtra(up,data->extra_change))
		return EXTRA_FONT;
	else
		return getFontType2(up,basefont,data,stable);
}

int getFirstFont(Uint16* up,int basefont){
	if(up==NULL || *up == '\0'){
		return basefont;
	}
	int foundBase = FALSE;
	while(*up!='\0'){
		if(!isAscii(up))
			foundBase = TRUE;
		else if(foundBase)
			return basefont;
		switch (getDetailType(*up)) {
			case MINGLIU_CHAR:
				return MINGLIU_FONT;
			case STRONG_SIMSUN_CHAR:
				return SIMSUN_FONT;
			case GULIM_CHAR:
				return GULIM_FONT;
			case GOTHIC_CHAR:
				return GOTHIC_FONT;
			case WEAK_SIMSUN_CHAR:
				basefont = SIMSUN_FONT;
				break;
			default:
				break;
		}
		up++;
	}
	return basefont;
}

/*
Uint16 replaceSpace(Uint16 u){
	if(u == 0x02cb){
		return 0x02cb;
	}else{
		return u;
	}
}
*/
/*
void removeZeroWidth(Uint16* str,int len){
	int i;
	Uint16* dst = str;
	for(i=0;i<len;i++){
		if(!isZeroWidthP(str)){
			*dst++ = *str++;
		}else{
			str++;
		}
	}
	return;
}
*/
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

int uint16len(Uint16* u){
	if(u==NULL)
		return 0;
	int l=0;
	while(*u++!='\0')
		l++;
	return l;
}

int isKanjiWidth(Uint16* u){
	return isMatchKanji(u,(Uint16*)&KANJI_WIDTH[0]);
}

const char *getfontname(FontType fonttype){
	int i = fonttype & CA_FONT_NAME_MASK;
	if(i<CA_FONT_NAME_SIZE)
		return CA_FONT_NAME[i];
	return "appended";
}

int indexOf(Uint16* src, Uint16 key){
	if(key=='\0')
		return 0;
	int index = 0;
	for(index=0;src[index]!='\0';index++){
		if(src[index]==key)
			return index;
	}
	return -1;
}

void moveUint16(Uint16* from, Uint16* to){
	if(from > to){
		do{
			*to++ = *from++;
		}while(*from!='\0');
	}else{
		//error->do nothing
		return;
	}
}

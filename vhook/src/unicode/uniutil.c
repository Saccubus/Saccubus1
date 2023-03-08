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

int getDetailType2(int u,DATA *data){
	int type = UNITABLE[u];
	if(type==SURROGATE_PAIR_CHAR) return type;
	if(isGlyphExist(data,SEGOEUI_EMJ_FONT,(Uint16)u)) {
		if(type!=ARIAL_CHAR)
			type = SEGOEUI_EMJ_CHAR;
	}else if(isGlyphExist(data,SEGOEUI_SYM_FONT,(Uint16)u)) {
		if(type!=ARIAL_CHAR)
			type = SEGOEUI_SYM_CHAR;
	}
	return type;
}
int getDetailType32(Uint32 u,DATA *data){
	int type = UNITABLE[u];
	if(isGlyphExist32(data,SEGOEUI_EMJ_FONT,u)){
		type = SEGOEUI_EMJ_CHAR;
	}else if(isGlyphExist32(data,SEGOEUI_SYM_FONT,u)){
		type = SEGOEUI_SYM_CHAR;
	}
	return type;
}

int isGlyphExist(DATA* data,int fonttype,Uint16 u){
	TTF_Font* font = data->CAfont[fonttype][CMD_FONT_DEF];
	return (font!=NULL && TTF_GlyphIsProvided(font,u));
}

int isGlyphExist32(DATA* data,int fonttype,Uint32 u){
	TTF_Font* font = data->CAfont[fonttype][CMD_FONT_DEF];
	return (font!=NULL && TTF_GlyphIsProvided32(font,u));
}

int getGlyphExist(DATA* data,Uint16 u){
	int f;
	for(f=0;f<CA_FONT_MAX;f++){
		if(isGlyphExist(data,f,u))
			return f;
	}
	return UNDEFINED_FONT;
}

int getGlyphExist32(DATA* data,Uint32 u){
	int f;
	for(f=0;f<CA_FONT_MAX;f++){
		if(isGlyphExist32(data,f,u))
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
	if(isLowSurrogate(u))
		return NULL_FONT;
/*
	if(*u==0x0020){	//Ascii space -> fix fontsize w,h
		return ARIAL_FONT|CA_TYPE_SPACE_0020;	//0020-> 00200003
	}
	if(*u==0x00a0){	//Ascii space -> fix fontsize w,h
		return ARIAL_FONT|CA_TYPE_SPACE_00A0;	//00a0-> 00a00003
	}
*/
	FontType uft = u<<16;
	if(uft==CA_TYPE_SPACE_3000){	//全角空白->fix fontsize w,h
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
	switch(getDetailType2(u, data)){
		case STRONG_SIMSUN_CHAR:
			//case UNDEF_OR_SIMSUN:	//最終的にはXP,Vista,Win7共通時にSIMSUNに
			if(0xE758<=u && u<=0xE864){	//Simsun
				if(!isGlyphExist(data,SIMSUN_FONT,u))
					//even if glyph does not exist, fonttype should be defined
					return SIMSUN_FONT|CA_TYPE_NOGLYPH_SIMSUN;
			}
			return SIMSUN_FONT;
		case WEAK_SIMSUN_CHAR:
		case SIMSUN_NOT_CHANGE_CHAR:
			return SIMSUN_FONT;
		case MINGLIU_CHAR:	//暫定修正：明朝変化する(XPより後は)
			if(0xE865<=u && u<=0xF8FF){	//MingLiu
				if(!isGlyphExist(data,MINGLIU_FONT,u))
					//even if glyph does not exist, fonttype should be defined
					return MINGLIU_FONT|CA_TYPE_NOGLYPH_MINGLIU;
			}
			//return MINGLIU_FONT|CA_TYPE_NOGLYPH_MINGLIU;	//debug 2014.5.9
			return MINGLIU_FONT;	//debug 2014.5.9
		case N_MINCHO_CHAR:	//明朝変化なし simsun or ngulim
			return N_MINCHO_FONT;
		case GULIM_CHAR:
			return GULIM_FONT;
		case GOTHIC_CHAR:	//ゴシック保護
		case GOTHIC_NOT_PROTECT:	//保護なしゴシック
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
		case SEGOEUI_EMJ_CHAR:
			return SEGOEUI_EMJ_FONT;
		case SEGOEUI_SYM_CHAR:
			return SEGOEUI_SYM_FONT;
		case SURROGATE_PAIR_CHAR:
			//サロゲートペア文字の処理
			Uint32 u32 = convUTF16toUNICODE2(up);
			switch (getDetailType32(u32,data)) {
				case SEGOEUI_EMJ_CHAR:
					return SEGOEUI_EMJ_FONT;
				case SEGOEUI_SYM_CHAR:
					return SEGOEUI_SYM_FONT;
				default:
					if (isGlyphExist32(data,basefont,u32))
						return basefont;
					else
						return getGlyphExist32(data,u32);
			}
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

int getFirstFont(Uint16* up,int basefont,DATA* data){
	if(up==NULL || *up == '\0'){
		return basefont;
	}
	if(isLowSurrogate(*up)){
		return basefont;
	}
	int foundBase = FALSE;
	while(*up!='\0'){
		if(!isAscii(up))
			foundBase = TRUE;
		else if(foundBase)
			return basefont;
		switch (getDetailType2(*up, data)) {
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
サロゲートペア(代用対)は16ビットUnicodeの領域1024文字分を2つ使い
（前半 U+D800 〜 U+DBFF、後半 U+DC00 〜 U+DFFF）、
各々1個ずつからなるペアで1024 × 1024 = 1,048,576文字を表す。
*/
int isHighSurrogate(Uint16 u){
	return (0xD800 <= u && u <= 0xDBFF);
}

int isLowSurrogate(Uint16 u){
	return (0xDC00 <= u && u <= 0xDFFF);
}

// UTF16のサロゲートペアをUNICODEに変換
Uint32 convUTF16toUNICODE(Uint16 hs, Uint16 ls){
	Uint32 unicode = 0x10000;
	unicode += ((hs - 0xD800) << 10) + (ls - 0xDC00);
	return unicode;
}

Uint32 convUTF16toUNICODE2(Uint16* up){
	Uint32 unicode = 0x10000;
	Uint16 hs = *up++;
	Uint16 ls = *up;
	unicode += ((hs - 0xD800) << 10) + (ls - 0xDC00);
	return unicode;
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

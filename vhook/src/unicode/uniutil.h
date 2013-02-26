/*
 * util.h
 *
 *  Created on: 2011/11/14
 *      Author: orz
 */

#ifndef UNIUTIL_H_
#define UNIUTIL_H_

#include "../main.h"
#include "unitable.h"

// FontType is font_index(bit 4..0) + font_attribute(bit 31..16)
typedef int FontType;
int isMatchKanji(Uint16* u,Uint16* kanji);
//int isMatchZeroWidth(Uint16* u,Uint16* zero);
int isAscii(Uint16* u);
int isMincho(Uint16* u);
int isHANKAKU(Uint16* u);
int isZeroWidth(Uint16* u);
#define isMatchExtra(u, extra) isMatchKanji(u, extra)
int getDetailType(int u);
FontType getFontType2(Uint16* u,int fonttype,DATA* data);
FontType getFontType(Uint16* u,int fonttype,DATA* data);
FontType getFirstFont(Uint16* u,int fonttype);
Uint16 replaceSpace(Uint16 u);
void removeZeroWidth(Uint16* str,int len);
int convUint16(const char* unicode, Uint16** out);
int convUint16Pair(const char** unicodep,Uint16* up);
int getUint16(const char** unicodep);
int uint16len(Uint16* u);
int isKanjiWidth(Uint16* u);
const char *getfontname(int fonttype);

#endif /* UTIL_H_ */

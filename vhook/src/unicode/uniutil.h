/*
 * util.h
 *
 *  Created on: 2011/11/14
 *      Author: orz
 */

#ifndef UNIUTIL_H_
#define UNIUTIL_H_

#include "../main.h"

int isMatchKanji(Uint16* u,Uint16* kanji);
//int isMatchZeroWidth(Uint16* u,Uint16* zero);
int isAscii(Uint16* u);
int isMincho(Uint16* u);
int isHANKAKU(Uint16* u);
int isZeroWidth(Uint16* u);
#define isMatchExtra(u, extra) isMatchKanji(u, extra)
int getDetailType(int u);
int getFontType2(Uint16* u,int fonttype,DATA* data);
int getFontType(Uint16* u,int fonttype,DATA* data);
int getFirstFont(Uint16* u,int fonttype);
Uint16 replaceSpace(Uint16 u);
void removeZeroWidth(Uint16* str,int len);
int convUint16(const char* unicode, Uint16** out);
int convUint16Pair(const char** unicodep,Uint16* up);
int getUint16(const char** unicodep);
int uint16len(Uint16* u);

#endif /* UTIL_H_ */

#ifndef SHADOW_H_
#define SHADOW_H_

#define SHADOW_MAX 8
#define SHADOW_DEFAULT 1
#include <SDL/SDL.h>

//汎用シャドウ
// データ5組 カンマ(,)で区切る 後ろは省略可能。
// 0:影のビット幅=slide(0~15)
// 1:自動調整時に倍率を掛け算しないかするか:0(しない)/1(する)(省略時0しない)
// 2:影の方向=左上,上,右上,右,右下,下,左下,左の8パターンを有り1,無し0の8文字組み合わせ
//    16進数2文字にエンコードしてもよい。全周囲=FF,上下左右=55(省略値55),右下のみ=08
// 3:影のグラデーションビット幅=数値またはパーセント値(0%~200%)(0はグラデーション無し)(省略値0)
// 4:影のグラデーションmax=(パーセント値)->0%~100% (グラデーション無しなら影の濃さ)(省略値100%)
// 5:影のグラデーションmin=(パーセント値)->0%~100% (0は刻み幅最小値にする＝省略値)
// 6:フォントの細字化しないかするか:0(しない)/1(する)(省略値0しない)
typedef struct shadow_data {
	unsigned slide: 4;
	unsigned autoresize: 1;
	unsigned fontnormal: 1;
	unsigned pattern: 8;
	unsigned char gradation;
	unsigned char grad_max;
	unsigned char grad_min;
} struct_shadow_data;
// チェックは影と反対方向を見る。
#define SHADOW_UPLEFT	0x08
#define SHADOW_UP		0x04
#define SHADOW_UPRIGHT	0x02
#define SHADOW_RIGHT	0x01
#define SHADOW_DOWNRIGHT	0x80
#define SHADOW_DOWN		0x40
#define SHADOW_DOWNLEFT	0x20
#define SHADOW_LEFT		0x10

#include "../main.h"
SDL_Surface* (*ShadowFunc[SHADOW_MAX+1])(SDL_Surface* surf,int is_black,SDL_Color c,DATA* data);
void setting_shadow(const char* datastr, DATA* data);
#include "../mydef.h"
#endif /*SHADOW_H_*/

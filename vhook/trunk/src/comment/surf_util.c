#include <SDL/SDL.h>
#include "surf_util.h"
#include "../mydef.h"

SDL_Surface* connectSurface(SDL_Surface* top,SDL_Surface* bottom,int next_y_diff){
	SDL_Surface* ret = SDL_CreateRGBSurface( SDL_SRCALPHA,
											MAX(top->w,bottom->w),
											(top->h)+(bottom->h)+(next_y_diff-1),
											32,
											#if SDL_BYTEORDER == SDL_BIG_ENDIAN
											    0xff000000,
											    0x00ff0000,
											    0x0000ff00,
											    0x000000ff
											#else
											    0x000000ff,
											    0x0000ff00,
											    0x00ff0000,
												0xff000000
											#endif
											);
	SDL_SetAlpha(top,SDL_SRCALPHA | SDL_RLEACCEL,0xff);
	SDL_SetAlpha(bottom,SDL_SRCALPHA | SDL_RLEACCEL,0xff);

	SDL_Rect rect;
	rect.x = 0;
	rect.y = 0;
	SDL_BlitSurface(top,NULL,ret,&rect);

//	SDL_Rect rect2 = {0,top->h};
	SDL_Rect rect2;
	rect2.x = 0;
	rect2.y = top->h + next_y_diff - 1;
	SDL_BlitSurface(bottom,NULL,ret,&rect2);
	SDL_FreeSurface(top);
	SDL_FreeSurface(bottom);
	return ret;
}

void setAlpha(SDL_Surface* surf,double alpha_t){
	int x,y;
	int h = surf->h;
	int w = surf->w;
	Uint32 mask,shift,bytesp,pitch,loss;
	Uint8 alpha;
	Uint8* pixels;
	Uint32* pix;
	SDL_PixelFormat* format = surf->format;
	/*変数の設定*/
	mask = format->Amask;
	shift = format->Ashift;
	loss = format->Aloss;
	bytesp = format->BytesPerPixel;
	pitch = surf->pitch;
	pixels = surf->pixels;
	SDL_LockSurface(surf);//サーフェイスをロック
	for(y=0;y<h;y++){
		for(x=0;x<w;x++){
			pix = (Uint32*)(&pixels[y*pitch + x*bytesp]);
			alpha = (Uint8)((((*pix) & mask) >> shift) << loss);
			alpha *= alpha_t;
			(*pix) &= ~((0xff >> loss) << shift);
			(*pix) |= (alpha >> loss) << shift;
		}
	}
	SDL_UnlockSurface(surf);//アンロック
}

/**
 * srcの不透明度を上書きしてしまう。
 * srcの方が不透明なら、それを上書き。
 */

void overrideAlpha(SDL_Surface *src, SDL_Rect *srcrect, SDL_Surface *dst, SDL_Rect *dstrect){
	SDL_LockSurface(src);//サーフェイスをロック
	SDL_LockSurface(dst);//サーフェイスをロック
	//範囲の確定
	int sw = src->w;
	int sh = src->h;
	int sx = 0;
	int sy = 0;
	if(srcrect != NULL){
		sx = srcrect->x;
		sy = srcrect->y;
		if(sx >= sw || sy > sh){
			return;
		}
		sw = MIN(sw-sx,srcrect->w);
		sh = MIN(sh-sy,srcrect->h);
	}
	int dw = dst->w;
	int dh = dst->h;
	int dx = 0;
	int dy = 0;
	if(dstrect != NULL){
		dx = dstrect->x;
		dy = dstrect->y;
		if(dx >= dw || dy > dh){
			return;
		}
		dw = MIN(dw-dx,dstrect->w);
		dh = MIN(dh-dy,dstrect->h);
	}
	//小さいほうにあわせる
	if(dw > sw){
		dw = sw;
	}else{
		sw = dw;
	}
	if(dh > sh){
		dh = sh;
	}else{
		sh = dh;
	}
	//やっとこさ描画。。
	int sbytesp = src->format->BytesPerPixel;
	int dbytesp = dst->format->BytesPerPixel;
	int spitch = src->pitch;
	int dpitch = dst->pitch;
	int sAmask = src->format->Amask;
	int dAmask = dst->format->Amask;
	int sAshift = src->format->Ashift;
	int dAshift = dst->format->Ashift;
	int sAloss = src->format->Aloss;
	int dAloss = dst->format->Aloss;
	Uint8* spix = (Uint8*)src->pixels;
	Uint8* dpix = (Uint8*)dst->pixels;
	Uint32* spt;
	Uint32* dpt;
	Uint32 salpha;
	Uint32 dalpha;
	int x,y;
	for(y=0;y<sh;y++){
		for(x=0;x<sw;x++){
			spt = (Uint32*)(&spix[(sy+y)*spitch+(sx+x)*sbytesp]);
			dpt = (Uint32*)(&dpix[(dy+y)*dpitch+(dx+x)*dbytesp]);
			salpha = ((*spt & sAmask)>>sAshift)<<sAloss;
			dalpha = ((*dpt & dAmask)>>dAshift)<<dAloss;
			if(salpha > dalpha){
				*dpt &= ~dAmask;
				*dpt |= (salpha>>dAloss)<<dAshift;
			}
		}
	}
	SDL_UnlockSurface(dst);//アンロック
	SDL_UnlockSurface(src);//アンロック
}

void inline shadowBlitSurface(SDL_Surface *src, SDL_Rect *srcrect, SDL_Surface *dst, SDL_Rect *dstrect){
	SDL_BlitSurface(src,srcrect,dst,dstrect);
	overrideAlpha(src,srcrect,dst,dstrect);
}

void setRGB(SDL_Surface* surf,Uint32 color){
	int x,y;
	int h = surf->h;
	int w = surf->w;
	int bytesp = surf->format->BytesPerPixel;
	int pitch = surf->pitch;
	Uint32 Amask = surf->format->Amask;
	color &= surf->format->Rmask | surf->format->Gmask | surf->format->Bmask;
	Uint8* pix = (Uint8*)surf->pixels;
	Uint32* pt;
	SDL_LockSurface(surf);//サーフェイスをロック
	for(y=0;y<h;y++){
		for(x=0;x<w;x++){
			pt = (Uint32*)(&pix[y*pitch + x*bytesp]);
			*pt &= Amask;
			*pt |= color;
		}
	}
	SDL_UnlockSurface(surf);//サーフェイスをアンロック
}

void getRGBA(SDL_Surface* surf,int x,int y,char* r,char* g,char* b,char* a){
	int pix_index = y * surf->pitch + x * surf->format->BytesPerPixel;
	char* pix = (char*)surf->pixels;
	SDL_GetRGBA(*(Uint32*)(&pix[pix_index]),surf->format,(Uint8*)r,(Uint8*)g,(Uint8*)b,(Uint8*)a);
}

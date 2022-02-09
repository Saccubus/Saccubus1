#include <SDL/SDL.h>
#include "com_surface.h"
#include "surf_util.h"
#include "../mydef.h"
#include "adjustComment.h"

int h_SetAlpha(h_Surface *surface, Uint32 flag, Uint8 alpha){
	return SDL_SetAlpha(surface->s,flag,alpha);
}
int h_BlitSurface(h_Surface *src, SDL_Rect *srcrect, h_Surface *dst, SDL_Rect *dstrect){
	return SDL_BlitSurface(src->s,srcrect,dst->s,dstrect);
}
void h_FreeSurface(h_Surface *surface){
	SDL_FreeSurface(surface->s);
	free(surface);
}
h_Surface* newSurface(SDL_Surface* surf){
	if(surf==NULL) return NULL;
	h_Surface* ret = (h_Surface*)malloc(sizeof(h_Surface));
	if(ret==NULL) {
		SDL_FreeSurface(surf);
		return NULL;
	}
	ret->s = surf;
	ret->w = surf->w;
	ret->h = surf->h;
	return ret;
}
SDL_Surface* h_SDLSurf(h_Surface* surf){
	SDL_Surface* sdlret = nullSurface(surf->w, surf->h);
	h_SetAlpha(surf,SDL_RLEACCEL,0xff);		//not use alpha
	SDL_BlitSurface(surf->s,NULL,sdlret,NULL);
	h_FreeSurface(surf);
	return sdlret;
}
int h_FillRect(h_Surface *dst, SDL_Rect *dstrect, Uint32 color){
	return SDL_FillRect(dst->s, dstrect, color);
}
int h_SetClipRect(h_Surface *surface, const SDL_Rect *rect){
	return SDL_SetClipRect(surface->s, rect)==SDL_TRUE;
}
int h_SetColorKey(h_Surface *surface, Uint32 flag, Uint32 key){
	return SDL_SetColorKey(surface->s, flag, key);
}
SDL_Surface* nullSurface(int w,int h){
	//not make nor use alpha
	return SDL_CreateRGBSurface(SDL_HWSURFACE | SDL_HWACCEL,
	                             w,h,32,
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
}
SDL_Surface* nullSurf(){
	return nullSurface(0, 0);
}
h_Surface* drawNullSurface(int w,int h){
	return newSurface(nullSurface(w,h));
}

h_Surface* connectSurface(h_Surface* top,h_Surface* bottom, int height){
	//not make nor use alpha channel
	//either top or bottom may be NULL
	h_Surface* ret = NULL;
	if(top==NULL){
		if(bottom==NULL)
			return NULL;
		ret = adjustComment2(bottom,height);
		h_FreeSurface(bottom);
		return ret;
	}
	if(bottom==NULL){
		ret = adjustComment2(top,height);
		h_FreeSurface(top);
		return ret;
	}
	int h = bottom->h;
	int y = top->h;
	if(height==0)
		height = y + h;
	ret = drawNullSurface(MAX(top->w,bottom->w), height);
	if(ret == NULL) return NULL;	//for Error
	h_SetAlpha(top,SDL_RLEACCEL,0xff);	//not use alpha
	h_SetAlpha(bottom,SDL_RLEACCEL,0xff);	//not use alpha
	h_BlitSurface(top,NULL,ret,NULL);
	int w = bottom->w;
	int dh = (height-(y+h))>>1 ;
	if(dh>=0){
		// 丸ごとBlitしてよい。
		SDL_Rect rect = {0,y,w,h};
		h_BlitSurface(bottom,NULL,ret,&rect);
	}else{
		// dh分だけ削る。(dh < 0)
		SDL_Rect srcrect = {0, -dh, w, h+dh};
		SDL_Rect dstrect = {0,y+dh,w,h+dh};
		h_BlitSurface(bottom,&srcrect,ret,&dstrect);
	}
	h_FreeSurface(top);
	h_FreeSurface(bottom);
	return ret;
}

h_Surface* arrangeSurface(h_Surface* left,h_Surface* right){
	//not make nor use alpha
	if(left==NULL)
		return right;	// this may be NULL
	if(right==NULL)
		return left;
	h_Surface* ret = drawNullSurface(left->w+right->w, MAX(left->h,right->h));
	if(ret == NULL) return NULL;	//for Error
	h_SetAlpha(left,SDL_RLEACCEL,0xff);	//not use alpha
	h_SetAlpha(right,SDL_RLEACCEL,0xff);	//not use alpha
	h_BlitSurface(left,NULL,ret,NULL);
	SDL_Rect rect = {left->w,0,ret->w,ret->h};		//use only x y
	h_BlitSurface(right,NULL,ret,&rect);
	h_FreeSurface(left);
	h_FreeSurface(right);
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

int cmpSDLColor(SDL_Color col1, SDL_Color col2){
	return (col1.r == col2.r && col1.g == col2.g && col1.b == col2.b);
}

char* getColorName(char* buf, int color){
	if(color >= 0){
		return CMD_COLOR_NAME[(color & 15)];
	}
	sprintf(buf,"#%06x",(color & 0x00ffffff));
	return buf;
}

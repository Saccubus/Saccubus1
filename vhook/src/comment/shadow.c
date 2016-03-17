#include <SDL/SDL.h>
#include "surf_util.h"
#include "shadow.h"

/*影なし*/
SDL_Surface* noShadow(SDL_Surface* surf,int is_black,int is_fix_size,SDL_Color c){
	return surf;
}
/*右下*/
#define SHADOW_SIZE 2
#define SHADOW2_SIZE 2

SDL_Surface* likeNicoNico(SDL_Surface* surf,int is_black,int is_fix_size,SDL_Color c){
	/*スライド幅の確定*/
	int slide = SHADOW_SIZE;
	int slide2 = SHADOW2_SIZE;
	if(is_fix_size){
		slide <<= 1;
		slide2 <<= 1;
	}
	int w = surf->w;
	int h = surf->h;
	SDL_Surface* shadow = SDL_CreateRGBSurface(		SDL_SRCALPHA | SDL_HWSURFACE | SDL_HWACCEL,
												w+(slide+slide2),
												h+(slide+slide2),
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
	SDL_Surface* shadow2 = SDL_CreateRGBSurface(	SDL_SRCALPHA | SDL_HWSURFACE | SDL_HWACCEL,
												w+(slide+slide2),
												h+(slide+slide2),
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
	SDL_Rect rect = {slide,slide};
	SDL_SetAlpha(surf,0,0xff);
	SDL_BlitSurface(surf,NULL,shadow,&rect);
	SDL_SetAlpha(surf,SDL_SRCALPHA,0xff);
	if(is_black==1){//黒であれば、周りをしろで囲む
		setRGB(shadow,0xffffffff);
	}else if(is_black==0){
		setRGB(shadow,0);
	}else{
		setRGB(shadow,SDL_MapRGB(surf->format,c.r,c.g,c.b));
	}
	SDL_SetAlpha(shadow,0,0xff);
	SDL_BlitSurface(shadow,NULL,shadow2,NULL);
	SDL_SetAlpha(shadow,SDL_SRCALPHA,0xff);
	int x,y,z;
	int nw = shadow->w;
	int nh = shadow->h;
	int *pix;
	int *pix2;
	int pitch = shadow->pitch;
	int bps = shadow->format->BytesPerPixel;
	Uint32 Amask = shadow->format->Amask;
	Uint32 Mask = (shadow->format->Rmask | shadow->format->Gmask | shadow->format->Bmask);
	Uint32 Ashift = shadow->format->Ashift;
	Uint32 Aloss = shadow->format->Aloss;
	SDL_LockSurface(shadow);
	SDL_LockSurface(shadow2);
	//ここは偶数にすること。
	int zmax = 10;
	if(is_fix_size){
		zmax = 16;
	}
	SDL_Surface* tmp;
	for(z=0;z<zmax;z++){
		char *pixels = (char*)shadow->pixels;
		char *pixels2 = (char*)shadow2->pixels;
		for(y=0;y<nh;y++){
			pix = (int*)(&pixels[pitch * y]);
			pix2 = (int*)(&pixels2[pitch * y]);
			for(x=0;x<nw;x++){
				int right = (x==nw-1) ? 0 : *(int*)((((char*)pix)+bps));
				int left = (x==0) ? 0 : *(int*)((((char*)pix)-bps));
				int up = (y==0) ? 0 : *(int*)((((char*)pix)-pitch));
				int down = (y==nh-1) ? 0 : *(int*)((((char*)pix)+pitch));
				int my = *pix2;
				int new_alpha = (((((my & Amask) >> Ashift) << Aloss) +(((right & Amask) >> Ashift) << Aloss)+(((left & Amask) >> Ashift) << Aloss)+(((up & Amask) >> Ashift) << Aloss)+(((down & Amask) >> Ashift) << Aloss)) / 5) & 0xff;
				new_alpha = (new_alpha * 18) >> 4;
				if(new_alpha > 0xff){
					new_alpha = 0xff;
				}
				*pix2 &= Mask;
				*pix2 |= ((new_alpha >> Aloss) << Ashift) & Amask;
				pix = (int*)(((char*)pix)+bps);
				pix2 = (int*)(((char*)pix2)+bps);
			}
		}
		tmp = shadow2;
		shadow2 = shadow;
		shadow = tmp;
	}
	SDL_UnlockSurface(shadow);
	SDL_UnlockSurface(shadow2);
	shadowBlitSurface(surf,NULL,shadow,&rect);
	SDL_FreeSurface(surf);
	SDL_FreeSurface(shadow2);
	return shadow;
}

/*右下*/
#define SHADOW_SLIDE 2
SDL_Surface* likeNovel(SDL_Surface* surf,int is_black,int is_fix_size,SDL_Color c){
	/*スライド幅の確定*/
	int slide = SHADOW_SLIDE;
	if(is_fix_size){
		slide <<= 1;
	}
	/*黒の用意*/
	SDL_Surface* black = SDL_CreateRGBSurface(	SDL_SRCALPHA | SDL_HWSURFACE | SDL_HWACCEL,
												surf->w+slide,
												surf->h+slide,
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
	SDL_Rect rect = {slide,slide};
	SDL_SetAlpha(surf,0,0xff);//一回alpha合成を切る
	SDL_BlitSurface(surf,NULL,black,&rect);
	SDL_SetAlpha(surf,SDL_SRCALPHA,0xff);
	if(is_black==1){//黒であれば、周りをしろで囲む
		setRGB(black,0xffffffff);
	}else if(is_black==0){
		setRGB(black,0);
	}else{
		setRGB(black,SDL_MapRGB(surf->format,c.r,c.g,c.b));
	}
	setAlpha(black,0.6f);
	shadowBlitSurface(surf,NULL,black,NULL);
	SDL_FreeSurface(surf);
	return black;
}

//散らすのではなく、囲ってしまう。
SDL_Surface* likeOld(SDL_Surface* surf,int is_black,int is_fix_size,SDL_Color c){
	/*スライド幅の確定*/
	int slide = SHADOW_SIZE;
	if(is_fix_size){
		slide <<= 1;
	}
	int w = surf->w;
	int h = surf->h;
	SDL_Surface* shadow = SDL_CreateRGBSurface(		SDL_SRCALPHA | SDL_HWSURFACE | SDL_HWACCEL,
												w+(slide<<1),
												h+(slide<<1),
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
	SDL_Surface* shadow2 = SDL_CreateRGBSurface(	SDL_SRCALPHA | SDL_HWSURFACE | SDL_HWACCEL,
												w+(slide<<1),
												h+(slide<<1),
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
	SDL_Rect rect = {slide,slide};
	SDL_SetAlpha(surf,0,0xff);
	SDL_BlitSurface(surf,NULL,shadow,&rect);
	SDL_SetAlpha(surf,SDL_SRCALPHA,0xff);
	if(is_black==1){//黒であれば、周りをしろで囲む
		setRGB(shadow,0xffffffff);
	}else if(is_black==0){
		setRGB(shadow,0);
	}else{
		setRGB(shadow,SDL_MapRGB(surf->format,c.r,c.g,c.b));
	}
	SDL_SetAlpha(shadow,0,0xff);
	SDL_BlitSurface(shadow,NULL,shadow2,NULL);
	SDL_SetAlpha(shadow,SDL_SRCALPHA,0xff);
	int x,y,z;
	int nw = shadow->w;
	int nh = shadow->h;
	int *pix;
	int *pix2;
	int pitch = shadow->pitch;
	int bps = shadow->format->BytesPerPixel;
	Uint32 Amask = shadow->format->Amask;
	Uint32 Mask = (shadow->format->Rmask | shadow->format->Gmask | shadow->format->Bmask);
	Uint32 Ashift = shadow->format->Ashift;
	Uint32 Aloss = shadow->format->Aloss;
	SDL_Surface* tmp;
	SDL_LockSurface(shadow);
	SDL_LockSurface(shadow2);
	int zmax = 1;
	if(is_fix_size){
		zmax = 2;
	}
	for(z=0;z<zmax;z++){
		char *pixels = (char*)shadow->pixels;
		char *pixels2 = (char*)shadow2->pixels;
		for(y=0;y<nh;y++){
			pix = (int*)(&pixels[pitch * y]);
			pix2 = (int*)(&pixels2[pitch * y]);
			for(x=0;x<nw;x++){
				int right = (x==nw-1) ? 0 : *(int*)((((char*)pix)+bps));
				int left = (x==0) ? 0 : *(int*)((((char*)pix)-bps));
				int up = (y==0) ? 0 : *(int*)((((char*)pix)-pitch));
				int down = (y==nh-1) ? 0 : *(int*)((((char*)pix)+pitch));
				int my = *pix2;
				//周りが空白でない
				if(((right | left | up | down | my) & Amask) != 0){
					*pix2 &= Mask;
					*pix2 |= (((0xff/(z+1)) >> Aloss) << Ashift) & Amask;
				}
				pix = (int*)(((char*)pix)+bps);
				pix2 = (int*)(((char*)pix2)+bps);
			}
		}
		tmp = shadow2;
		shadow2 = shadow;
		shadow = tmp;
	}
	SDL_UnlockSurface(shadow);
	SDL_UnlockSurface(shadow2);
	shadowBlitSurface(surf,NULL,shadow,&rect);
	SDL_FreeSurface(surf);
	SDL_FreeSurface(shadow2);
	return shadow;
}

#define SHADOW_SACCUBUS2 2
//Saccubus2同様?
SDL_Surface* likeSaccubus2(SDL_Surface* surf,int is_black,int is_fix_size,SDL_Color c){
	/*スライド幅の確定*/
	int slide = SHADOW_SACCUBUS2;
	if(is_fix_size){
		slide <<= 1;
	}
	int w = surf->w;
	int h = surf->h;
	SDL_Surface* shadow = SDL_CreateRGBSurface(		SDL_SRCALPHA | SDL_HWSURFACE | SDL_HWACCEL,
												w+(slide<<1),
												h+(slide<<1),
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
	SDL_Surface* shadow2 = SDL_CreateRGBSurface(	SDL_SRCALPHA | SDL_HWSURFACE | SDL_HWACCEL,
												w+(slide<<1),
												h+(slide<<1),
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
	SDL_Rect rect = {slide,slide};	//左上座標
	SDL_SetAlpha(surf,0,0xff);		//αを切る
	SDL_BlitSurface(surf,NULL,shadow,&rect);	//surf→shadowコピー
	SDL_SetAlpha(surf,SDL_SRCALPHA,0xff);		//αを入れる
	if(is_black==1){//黒であれば、周りをしろで囲む
		setRGB(shadow,0xffffffff);		//影の色で塗りつぶす
	}else if(is_black==0){
		setRGB(shadow,0);
	}else{
		setRGB(shadow,SDL_MapRGB(surf->format,c.r,c.g,c.b));
	}
	SDL_SetAlpha(shadow,0,0xff);		//αを切る
	SDL_BlitSurface(shadow,NULL,shadow2,NULL);	//shadow→shadow2コピー
	SDL_SetAlpha(shadow,SDL_SRCALPHA,0xff);		//αを入れる
	int x,y,z;
	int nw = shadow->w;
	int nh = shadow->h;
	int *pix;
	int *pix2;
	int pitch = shadow->pitch;
	int bps = shadow->format->BytesPerPixel;
	Uint32 Amask = shadow->format->Amask;
	Uint32 Mask = (shadow->format->Rmask | shadow->format->Gmask | shadow->format->Bmask);
	Uint32 Ashift = shadow->format->Ashift;
	Uint32 Aloss = shadow->format->Aloss;
	SDL_Surface* tmp;
	SDL_LockSurface(shadow);
	SDL_LockSurface(shadow2);
	int zmax = slide;
	if(is_fix_size){
		zmax <<= 1;
	}
	for(z=0;z<zmax;z++){
		char *pixels = (char*)shadow->pixels;
		char *pixels2 = (char*)shadow2->pixels;
		for(y=0;y<nh;y++){
			pix = (int*)(&pixels[pitch * y]);
			pix2 = (int*)(&pixels2[pitch * y]);
			for(x=0;x<nw;x++){
				int right = (x==nw-1) ? 0 : *(int*)((((char*)pix)+bps));
				int left = (x==0) ? 0 : *(int*)((((char*)pix)-bps));
				int up = (y==0) ? 0 : *(int*)((((char*)pix)-pitch));
				int down = (y==nh-1) ? 0 : *(int*)((((char*)pix)+pitch));
				int my = *pix2;
				//周りが空白でない
				if(((right | left | up | down | my) & Amask) != 0){
					*pix2 &= Mask;
					*pix2 |= ((0xff >> Aloss) << Ashift) & Amask;
				}
				pix = (int*)(((char*)pix)+bps);
				pix2 = (int*)(((char*)pix2)+bps);
			}
		}
		tmp = shadow2;
		shadow2 = shadow;
		shadow = tmp;
	}
	SDL_UnlockSurface(shadow);
	SDL_UnlockSurface(shadow2);
	shadowBlitSurface(surf,NULL,shadow,&rect);
	SDL_FreeSurface(surf);
	SDL_FreeSurface(shadow2);
	return shadow;
}


//定義
SDL_Surface* (*ShadowFunc[SHADOW_MAX])(SDL_Surface* surf,int is_black,int is_fix_size,SDL_Color c) = {
	noShadow,
	likeNicoNico,
	likeNovel,
	likeOld,
	likeSaccubus2
};

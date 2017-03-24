#include <SDL/SDL.h>
#include "surf_util.h"
#include "shadow.h"

/*影なし*/
SDL_Surface* noShadow(SDL_Surface* surf,int is_black,SDL_Color c,DATA* data){
	return surf;
}
/*右下*/
#define SHADOW_SIZE 2
#define SHADOW2_SIZE 2

SDL_Surface* likeNicoNico(SDL_Surface* surf,int is_black,SDL_Color c,DATA* data){
	int is_fix_size = data->fontsize_fix;
	/*スライド幅の確定*/
	int slide = SHADOW_SIZE;
	int slide2 = SHADOW2_SIZE;
	int gmax = 0xff;
	if(data->shadow_data.slide>0){
		slide = slide2 = data->shadow_data.slide;
		gmax *= data->shadow_data.grad_max / 100;
	}
	if(is_fix_size && data->shadow_data.autoresize){
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
				if(new_alpha > gmax){
					new_alpha = gmax;
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

SDL_Surface* likeNicoNico2(SDL_Surface* surf,int is_black,SDL_Color c,DATA* data){
	int is_fix_size = data->fontsize_fix;
	/*スライド幅の確定*/
	int slide = SHADOW_SIZE;
	int slide2 = SHADOW2_SIZE;
	if(data->shadow_data.slide>0){
		slide = slide2 = data->shadow_data.slide;
	}
	if(is_fix_size && data->shadow_data.autoresize){
		slide <<= 1;
		slide2 <<= 1;
	}
	int w = surf->w;
	int h = surf->h;
	surf = likeNicoNico(surf, is_black, c, data);
	SDL_Rect srcrect = {slide,slide,w,h};
	SDL_Surface *ret = nullSurface(w, h);
	if(ret==NULL) return NULL;
	SDL_SetAlpha(surf,SDL_RLEACCEL,0xff);		//not use alpha
	SDL_BlitSurface(surf,&srcrect,ret,NULL);
	SDL_FreeSurface(surf);
	return  ret;
}

/*右下*/
#define SHADOW_SLIDE 2
SDL_Surface* likeNovel(SDL_Surface* surf,int is_black,SDL_Color c,DATA* data){
	int is_fix_size = data->fontsize_fix;
	/*スライド幅の確定*/
	int slide = SHADOW_SLIDE;
	double gmax = 0.6f;
	if(data->shadow_data.slide>0){
		slide = data->shadow_data.slide;
		gmax = (double)data->shadow_data.grad_max / 100.0;
	}
	if(is_fix_size && data->shadow_data.autoresize){
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
	setAlpha(black,gmax);
	shadowBlitSurface(surf,NULL,black,NULL);
	SDL_FreeSurface(surf);
	return black;
}

//散らすのではなく、囲ってしまう。
SDL_Surface* likeOld(SDL_Surface* surf,int is_black,SDL_Color c,DATA* data){
	int is_fix_size = data->fontsize_fix;
	/*スライド幅の確定*/
	int slide = SHADOW_SIZE;
	int gmax = 0xff;
	if(data->shadow_data.slide>0){
		slide = data->shadow_data.slide;
		gmax *= data->shadow_data.grad_max / 100;
	}
	if(is_fix_size && data->shadow_data.autoresize){
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

#define SHADOW_S2NEW 1
#define SHADOW_SACCUBUS2 2
//Saccubus2同様?
SDL_Surface* likeSaccubus2a(SDL_Surface* surf,int is_black,SDL_Color c,DATA* data,int zmax_select){
	int is_fix_size = data->fontsize_fix;
	/*スライド幅の確定*/
	int slide = SHADOW_SACCUBUS2;
	int gmax = 0xff;
	if(data->shadow_data.slide>0){
		slide =  data->shadow_data.slide;
		gmax *= data->shadow_data.grad_max / 100;
	}
	if(is_fix_size && data->shadow_data.autoresize){
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
	if(is_fix_size && zmax_select==SHADOW_SACCUBUS2){
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
				int upr = (y==0||x==nw-1) ? 0 : *(int*)((((char*)pix)-pitch+bps));
				int upl = (y==0||x==0) ? 0 : *(int*)((((char*)pix)-pitch-bps));
				int down = (y==nh-1) ? 0 : *(int*)((((char*)pix)+pitch));
				int downr = (y==nh-1||x==nw-1) ? 0 : *(int*)((((char*)pix)+pitch+bps));
				int downl = (y==nh-1||x==0) ? 0 : *(int*)((((char*)pix)+pitch-bps));
				int my = *pix2;
				//周りが空白でない
				if(((right | left | up | down | my | upl | upr | downl | downr) & Amask) != 0){
					*pix2 &= Mask;
					*pix2 |= ((gmax >> Aloss) << Ashift) & Amask;
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

SDL_Surface* likeSaccubus2(SDL_Surface* surf,int is_black,SDL_Color c,DATA* data){
	return likeSaccubus2a(surf,is_black,c,data,SHADOW_SACCUBUS2);
}

SDL_Surface* likeSaccubus2new(SDL_Surface* surf,int is_black,SDL_Color c,DATA* data){
	return likeSaccubus2a(surf,is_black,c,data,SHADOW_S2NEW);
}

#define HTML5_SHADOW_SIZE 1
SDL_Surface* likeHtml5(SDL_Surface* surf,int is_black,SDL_Color c,DATA* data){
	/*スライド幅の確定*/
	int is_fix_size = data->fontsize_fix;
	int slide = HTML5_SHADOW_SIZE;
	int gmax = 0xc0;
	if(data->shadow_data.slide>0){
		slide = data->shadow_data.slide;
		gmax *= data->shadow_data.grad_max / 100;
	}
	if(is_fix_size && data->shadow_data.autoresize){
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
	int zmax = slide;
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
				int upr = (y==0||x==nw-1) ? 0 : *(int*)((((char*)pix)-pitch+bps));
				int upl = (y==0||x==0) ? 0 : *(int*)((((char*)pix)-pitch-bps));
				int down = (y==nh-1) ? 0 : *(int*)((((char*)pix)+pitch));
				int downr = (y==nh-1||x==nw-1) ? 0 : *(int*)((((char*)pix)+pitch+bps));
				int downl = (y==nh-1||x==0) ? 0 : *(int*)((((char*)pix)+pitch-bps));
				int my = *pix2;
				//周りが空白でない
				if(((right | left | up | down | my | upl | upr | downl | downr) & Amask) != 0){
					*pix2 &= Mask;
					*pix2 |= ((gmax >> Aloss) << Ashift) & Amask;
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

SDL_Surface* customShadow(SDL_Surface* surf,int is_black,SDL_Color c,DATA* data){
	int is_fix_size = data->fontsize_fix;
	/*スライド幅の確定*/
	struct_shadow_data shd = data->shadow_data;
	int slide = shd.slide;
	if(is_fix_size && shd.autoresize){
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
	int zmax = shd.gradation;
	if(zmax==0){
		shd.grad_min = shd.grad_max;
		zmax = slide;
	}
	int f_upl = shd.pattern & SHADOW_UPLEFT;
	int f_up = shd.pattern & SHADOW_UP;
	int f_upr = shd.pattern & SHADOW_UPRIGHT;
	int f_right = shd.pattern & SHADOW_RIGHT;
	int f_downr = shd.pattern & SHADOW_DOWNRIGHT;
	int f_down = shd.pattern & SHADOW_DOWN;
	int f_downl = shd.pattern & SHADOW_DOWNLEFT;
	int f_left = shd.pattern & SHADOW_LEFT;
	float grad = MIN((double)(shd.grad_max - shd.grad_min) / 100.0, 1.0) / zmax;
	int max_grad = MIN(255, (int)(255 * shd.grad_max / 100));
	for(z=0;z<zmax;z++){
		char *pixels = (char*)shadow->pixels;
		char *pixels2 = (char*)shadow2->pixels;
		for(y=0;y<nh;y++){
			pix = (int*)(&pixels[pitch * y]);
			pix2 = (int*)(&pixels2[pitch * y]);
			for(x=0;x<nw;x++){
				int right = (!f_right||x==nw-1) ? 0 : *(int*)((((char*)pix)+bps));
				int left = (!f_left||x==0) ? 0 : *(int*)((((char*)pix)-bps));
				int up = (!f_up||y==0) ? 0 : *(int*)((((char*)pix)-pitch));
				int upr = (!f_upr||y==0||x==nw-1) ? 0 : *(int*)((((char*)pix)-pitch+bps));
				int upl = (!f_upl||y==0||x==0) ? 0 : *(int*)((((char*)pix)-pitch-bps));
				int down = (!f_down||y==nh-1) ? 0 : *(int*)((((char*)pix)+pitch));
				int downr = (!f_downr||y==nh-1||x==nw-1) ? 0 : *(int*)((((char*)pix)+pitch+bps));
				int downl = (!f_downl||y==nh-1||x==0) ? 0 : *(int*)((((char*)pix)+pitch-bps));
				int my = *pix2;
				//周りが空白でない
				if(((right | left | up | down | my | upl | upr | downl | downr) & Amask) != 0){
					*pix2 &= Mask;
					*pix2 |= ((((int)(max_grad * (1.0 - grad * z))) >> Aloss) << Ashift) & Amask;
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
	SDL_Rect srcrect = {slide,slide,w,h};
	SDL_Surface *ret = nullSurface(w, h);
	if(ret==NULL) return shadow;
	SDL_SetAlpha(shadow,SDL_RLEACCEL,0xff);		//not use alpha
	SDL_BlitSurface(shadow,&srcrect,ret,NULL);
	SDL_FreeSurface(shadow);
	return  ret;
}

//定義

SDL_Surface* (*ShadowFunc[SHADOW_MAX+1])(SDL_Surface* surf,int is_black,SDL_Color c,DATA* data) = {
	noShadow,
	likeNicoNico,
	likeNovel,
	likeOld,
	likeSaccubus2,
	likeSaccubus2new,
	likeHtml5,
	likeNicoNico2,
	customShadow,
};

//カスタム影設定
void setting_shadow(const char* datastr, DATA* data){
	// データ5組 カンマ(,)で区切る 後ろは省略可能。
	// 0:影のビット幅=slide(0~15)
	// 1:自動調整時に倍率を掛け算するかしないか:1(する)/0(しない)(省略時1する)
	// 2:影の方向=左上,上,右上,右,右下,下,左下,左の8パターンを有り1,無し0の8文字組み合わせ
	//    16進数2文字にエンコードしてもよい。全周囲=FF,上下左右=55(省略値55),右下のみ=08
	// 3:影のグラデーションビット幅=数値またはパーセント値(0%~200%)(0はグラデーション無し)(省略値0)
	// 4:影のグラデーションmax=(パーセント値)->0%~100% (グラデーション無しなら影の濃さ)(省略値100%)
	// 5:影のグラデーションmin=(パーセント値)->0%~100% (0は刻み幅最小値にする＝省略値)
	// 6:フォントの細字化しないかするか:0(しない)/1(する)(省略値0しない)
	struct_shadow_data shd = {
		.slide = 0,
		.autoresize = 0,
		.fontnormal = 0,
		.pattern = 0x55,
		.gradation = 0,
		.grad_max = 100,
		.grad_min = 100,
	};
	data->shadow_data = shd;
	char* ptr = strstr(datastr,"-shadow=");
	if(ptr==NULL) return;
	ptr += strlen("-shadow=");
	FILE* log = data->log;
	unsigned slide = strtol(ptr,&ptr,10);
	if(slide>0){
		slide = MIN(slide,15);
		data->shadow_data.slide = slide;
		fprintf(log,"[shadow/setting_shadow]shadow slide=%d\n", slide);
	}
	if(ptr==NULL || *ptr++!=','){
		return;
	}
	int autoresize = 0;
	if(*ptr=='0'||*ptr=='1'){
		autoresize = (*ptr++ == '1');
		data->shadow_data.autoresize = autoresize;
		fprintf(log,"[shadow/setting_shadow]shadow autoresize=%d\n", autoresize);
	}
	else
		return;
	if(ptr==NULL || *ptr++!=',')
		return;
	unsigned pattern = 0x55;
	if(ptr[2]==','||ptr[2]=='\0')
		pattern = strtol(ptr,&ptr,16);	//16進数パターン
	else if(ptr[8]==','||ptr[8]=='\0')
		pattern = strtol(ptr,&ptr,2);	//2進数パターン
	else
		return;
	data->shadow_data.pattern = pattern;
	fprintf(log,"[shadow/setting_shadow]shadow pattern=%2x\n", pattern);
	if(ptr==NULL || *ptr++!=',')
		return;
	unsigned gradation = 0;
	gradation = strtol(ptr,&ptr,0);
	if(gradation!=0){
		if(ptr!=NULL && *ptr=='%'){
			gradation = slide * gradation / 100;
			ptr++;
		}
	}
	if(gradation!=0){
		gradation = MAX(0,MIN(slide*2,gradation));
		data->shadow_data.gradation = gradation;
		fprintf(log,"[shadow/setting_shadow]shadow gradation=%d pixel\n", gradation);
	}
	if(ptr==NULL || *ptr++!=',')
		return;
	unsigned grad_max = 0;
	grad_max = strtol(ptr,&ptr,0);
	if(ptr!=NULL && *ptr=='%')
		ptr++;
	if(grad_max!=0){
		grad_max = MAX(0,MIN(100,grad_max));
		data->shadow_data.grad_max = grad_max;
		fprintf(log,"[shadow/setting_shadow]shadow grad_max=%d%%\n", grad_max);
	}
	if(ptr==NULL || *ptr++!=',')
		return;
	unsigned grad_min = 0;
	grad_min = strtol(ptr,&ptr,0);
	if(ptr!=NULL && *ptr=='%')
		ptr++;
	if(grad_min!=0){
		grad_min = MAX(0,MIN(100,grad_min));
		data->shadow_data.grad_min = grad_min;
		fprintf(log,"[shadow/setting_shadow]shadow grad_min=%d%%\n", grad_min);
	}
	if(ptr==NULL || *ptr++!=',')
		return;
	unsigned fontnormal = 0;
	fontnormal = strtol(ptr,&ptr,0);
	if(fontnormal!=0){
		data->shadow_data.fontnormal = TRUE;
		fprintf(log,"[shadow/setting_shadow]shadow fontnormal=%d\n", fontnormal);
	}
}

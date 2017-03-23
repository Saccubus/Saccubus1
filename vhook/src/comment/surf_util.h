#ifndef SURF_UTIL_H_
#define SURF_UTIL_H_
#include <SDL/SDL.h>

typedef struct h_Surface {
	SDL_Surface *s;
	int w;
	int h;
} h_Surface;

int h_SetAlpha(h_Surface *surface, Uint32 flag, Uint8 alpha);
int h_BlitSurface(h_Surface *src, SDL_Rect *srcrect, h_Surface *dst, SDL_Rect *dstrect);
void h_FreeSurface(h_Surface *surface);
int h_FillRect(h_Surface *dst, SDL_Rect *dstrect, Uint32 color);
int h_SetClipRect(h_Surface *surface, const SDL_Rect *rect);
int h_SetColorKey(h_Surface *surface, Uint32 flag, Uint32 key);
h_Surface* drawNullSurface(int w,int h);
h_Surface* newSurface(SDL_Surface* surf);
SDL_Surface* nullSurface(int w,int h);
SDL_Surface* nullSurf();
SDL_Surface* h_SDLSurf(h_Surface* surf);
h_Surface* connectSurface(h_Surface* top,h_Surface* bottom,int fixh);
h_Surface* arrangeSurface(h_Surface* left,h_Surface* right);
void setAlpha(SDL_Surface* surf,double alpha_t);
void overrideAlpha(SDL_Surface *src, SDL_Rect *srcrect, SDL_Surface *dst, SDL_Rect *dstrect);
void shadowBlitSurface(SDL_Surface *src, SDL_Rect *srcrect, SDL_Surface *dst, SDL_Rect *dstrect);
void setRGB(SDL_Surface* surf,Uint32 color);
int cmpSDLColor(SDL_Color col1, SDL_Color col2);
char* getColorName(char* buf, int color);
#endif /*SURF_UTIL_H_*/

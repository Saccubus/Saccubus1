#ifndef SURF_UTIL_H_
#define SURF_UTIL_H_
#include <SDL/SDL.h>
SDL_Surface* connectSurface(SDL_Surface* top,SDL_Surface* bottom,int next_y_diff);
void setAlpha(SDL_Surface* surf,double alpha_t);
void overrideAlpha(SDL_Surface *src, SDL_Rect *srcrect, SDL_Surface *dst, SDL_Rect *dstrect);
void inline shadowBlitSurface(SDL_Surface *src, SDL_Rect *srcrect, SDL_Surface *dst, SDL_Rect *dstrect);
void setRGB(SDL_Surface* surf,Uint32 color);
void surftrimWidth(SDL_Surface* surf,int width);
#endif /*SURF_UTIL_H_*/

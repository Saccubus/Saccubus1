#ifndef SHADOW_H_
#define SHADOW_H_

#define SHADOW_MAX 5
#define SHADOW_DEFAULT 1
#include <SDL/SDL.h>
SDL_Surface* (*ShadowFunc[SHADOW_MAX])(SDL_Surface* surf,int is_black,int is_fix_size,SDL_Color c);

#endif /*SHADOW_H_*/

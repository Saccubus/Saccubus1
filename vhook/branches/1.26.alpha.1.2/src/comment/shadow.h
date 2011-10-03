#ifndef SHADOW_H_
#define SHADOW_H_

#define SHADOW_MAX 4
#define SHADOW_DEFAULT 1
SDL_Surface* (*ShadowFunc[SHADOW_MAX])(SDL_Surface* surf,int is_black,int is_fix_size);

#endif /*SHADOW_H_*/

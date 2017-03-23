/*
 * adjustComment.h
 *
 *  Created on: 2011/12/26
 *      Author: orz
 */

#ifndef ADJUSTCOMMENT_H_
#define ADJUSTCOMMENT_H_
/*
THANKS for Comment Artisan! A.K.A. SHOKUNIN!
 http://www37.atwiki.jp/commentart/pages/26.html
 http://www37.atwiki.jp/commentart?cmd=upload&act=open&pageid=21&file=%E3%82%B3%E3%83%A1%E3%83%B3%E3%83%88%E9%AB%98%E3%81%95%E4%B8%80%E8%A6%A7.jpg

  size        n    Windows Mac   Linux?
  DEF(medium) 1-4  29n+5   27n+5
  (resized)   > 5  15n+3   14n+3
  BIG         1-2  45n+5   43n+5
  (resized)   > 3  24n+3   14n+3
  SMALL       1-6  18n+5   17n+5
  (resized?)  7    10n+3   17n+5
  (resized)   > 8  10n+3    9n+3
 */
#include <SDL/SDL.h>
#include "surf_util.h"
#include "../main.h"

int adjustHeight(int nb_line,int size,int linefeedResize,int fontFixed,int html5);
double linefeedResizeScale(int size,int nb_line,int fontFixed,int html5);
h_Surface* adjustComment(h_Surface* surf,DATA* data,int height);
h_Surface* adjustComment2(h_Surface* surf,int height);
h_Surface* adjustCommentSize(h_Surface* surf,int width,int height);

#endif /* ADJUSTCOMMENT_H_ */

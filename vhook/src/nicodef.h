#ifndef NICODEF_H_
#define NICODEF_H_
#include <SDL/SDL_ttf.h>

//定義
#define NICO_WIDTH		512
#define NICO_HEIGHT	384
#define NICO_WIDTH_WIDE	640
#define NICO_LIMIT_WIDTH	544

#define VPOS_FACTOR		100	//多分係数の意味では・・・やはり1/100で記録してるっぽい
#define TEXT_AHEAD_SEC	(1 * VPOS_FACTOR)
#define TEXT_SHOW_SEC	(4 * VPOS_FACTOR)
#define TEXT_HEIGHT		22

#define CMD_LOC_DEF		(0)
#define CMD_LOC_TOP		(1)
#define CMD_LOC_BOTTOM	(2)

#define GET_CMD_LOC(x)	((x) & 0x03)
#define GET_CMD_DURATION(x)	((x) >> 8 & 0xff)
#define GET_CMD_FULL(x)	((x) & 0x0040)
#define GET_CMD_COLOR24(x)	((x) & 0x0080)

#define CMD_FONT_MAX	3
#define CMD_FONT_DEF	0
#define CMD_FONT_BIG	1
#define CMD_FONT_SMALL	2

static const int LINEFEED_RESIZE_LIMIT[CMD_FONT_MAX] = {
//  Lines of this limit should be resized.
	5,//DEF
	3,//BIG
	7,//SMALL
};
/*
!THIS LIMIT IS OF WINDOWS!!, MAC/LINUX DIFFERS!?
The Comments made to be viewed as ART only on Mac/Linux Browsers should differ.
  Lines	  Default  %384    %360
  5,DEF   160 px   42%    44%
  if 4    128 px   33.3%? 35,5%NG	
  3,BIG   156 px   41%    43%
  if 2    104 px   27%    28,9%
  7,SMALL 140 px   36.5%  39%
  if 6    120 px   31.3%  33.3%?
Rules of Height * 1/3 isn't very risky?
What if that's the same implementation as NICOPLAYER.SWF.
(And SDL height seems to be taller than default calculas.)
It should be 36% Height or 129px ?
or just use Number of Lines of each OS (Win or Mac/Linux?).

The REAL is as followwing. THANKS for Comment Artist!!
( http://www37.atwiki.jp/commentart/pages/26.html
  http://www37.atwiki.jp/commentart?cmd=upload&act=open&pageid=21&file=%E3%82%B3%E3%83%A1%E3%83%B3%E3%83%88%E9%AB%98%E3%81%95%E4%B8%80%E8%A6%A7.jpg
 )
  size        n    Windows Mac   Linux?
  DEF(medium) 1-4  29n+5   27n+5
  (resized)   > 5  15n+3   14n+3
  BIG         1-2  45n+5   43n+5
  (resized)   > 3  24n+3   14n+3
  SMALL       1-6  18n+5   17n+5
  (resized?)  7    10n+3   17n+5    -> threthold is 125-130px
  (resized)   > 8  10n+3    9n+3
BUT, this is too complicated, Futhermore SDL differs from Flashplayer,
So let me do something else.
*/

//Linefeed Resize scale 51.7%,53.3%,55.6%
#define LINEFEED_RESIZE_SCALE	(0.52f)

static const int COMMENT_FONT_SIZE[CMD_FONT_MAX] = {
	24,//DEF
	39,//BIG
	15,//SMALL
};
//Not resized in Windows (96dpi)
static const int FONT_PIXEL_SIZE[CMD_FONT_MAX] = {
	29,//DEF
	45,//BIG
	18,//SMALL
};

#define CMD_COLOR_MAX	17
#define CMD_COLOR_DEF	0
#define CMD_COLOR_RED	1
#define CMD_COLOR_ORANGE	2
#define CMD_COLOR_YELLOW	3
#define CMD_COLOR_PINK	4
#define CMD_COLOR_BLUE	5
#define CMD_COLOR_PURPLE	6
#define CMD_COLOR_CYAN	7
#define CMD_COLOR_GREEN	8
#define CMD_COLOR_NICOWHITE	9
#define CMD_COLOR_MARINEBLUE	10
#define CMD_COLOR_MADYELLOW	11
#define CMD_COLOR_PASSIONORANGE	12
#define CMD_COLOR_NOBLEVIOLET	13
#define CMD_COLOR_ELEMENTALGREEN	14
#define CMD_COLOR_TRUERED	15
#define CMD_COLOR_BLACK	16

static const SDL_Color COMMENT_COLOR[CMD_COLOR_MAX] = {
	{0xff,0xff,0xff,0x00},//DEF
	{0xff,0x00,0x00,0x00},//RED
	{0xff,0xC0,0x00,0x00},//ORANGE
	{0xff,0xff,0x00,0x00},//YELLOW
	{0xff,0x80,0x80,0x00},//PINK
	{0x00,0x00,0xff,0x00},//BLUE
	{0xc0,0x00,0xff,0x00},//PURPLE
	{0x00,0xff,0xff,0x00},//CYAN
	{0x00,0xff,0x00,0x00},//GREEN
	/*プレミア専用*/
	{0xCC,0xCC,0x99,0x00},//NICOWHITE
	{0x33,0xff,0xFC,0x00},//MARINEBLUE
	{0x99,0x99,0x00,0x00},//MADYELLOW
	{0xFF,0x66,0x00,0x00},//PASSIONORANGE
	{0x66,0x33,0xCC,0x00},//NOBLEVIOLET
	{0x00,0xCC,0x66,0x00},//ELEMENTALGREEN
	{0xCC,0x00,0x33,0x00},//TRUERED
	{0x00,0x00,0x00,0x00},//BLACK

};

#endif /*NICODEF_H_*/

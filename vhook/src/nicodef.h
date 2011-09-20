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

#define GET_CMD_LOC(x)	(x & 0xff)
#define GET_CMD_DURATION(x)	((x >> 8) & 0xff)
#define GET_CMD_LOCATION(x)	((x) & 0xff)

#define CMD_LOC_FULL	(0x10000)
#define GET_CMD_FULL(x)	((x) & CMD_LOC_FULL)

#define CMD_FONT_MAX	3
#define CMD_FONT_DEF	0
#define CMD_FONT_BIG	1
#define CMD_FONT_SMALL	2

static const int LINEFEED_RESIZE_LIMIT[CMD_FONT_MAX] = {
	// THIS LIMIT IS OF WINDOWS, MAC/LINUX DIFFERS!?
	5,//DEF
	3,//BIG
	7,//SMALL
};

static const int COMMENT_FONT_SIZE[CMD_FONT_MAX] = {
	24,//DEF
	39,//BIG
	15,//SMALL
};

#define CMD_COLOR_MAX				17
#define CMD_COLOR_DEF				0
#define CMD_COLOR_RED				1
#define CMD_COLOR_ORANGE			2
#define CMD_COLOR_YELLOW			3
#define CMD_COLOR_PINK				4
#define CMD_COLOR_BLUE				5
#define CMD_COLOR_PURPLE			6
#define CMD_COLOR_CYAN				7
#define CMD_COLOR_GREEN			8
#define CMD_COLOR_NICOWHITE		9
#define CMD_COLOR_MARINEBLUE		10
#define CMD_COLOR_MADYELLOW		11
#define CMD_COLOR_PASSIONORANGE	12
#define CMD_COLOR_NOBLEVIOLET		13
#define CMD_COLOR_ELEMENTALGREEN	14
#define CMD_COLOR_TRUERED			15
#define CMD_COLOR_BLACK			16

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

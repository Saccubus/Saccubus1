#ifndef NICODEF_H_
#define NICODEF_H_
#include <SDL/SDL_ttf.h>

//定義
#define NICO_WIDTH		512
#define NICO_HEIGHT	384
#define NICO_HEIGHT_WIDE 360
#define NICO_WIDTH_WIDE	640
#define HTML5_WIDTH_NARROW	480
#define NICO_COMMENT_HIGHT 385
#define NICO_LIMIT_WIDTH	544
#define NICO_LIMIT_WIDTH_WIDE	672

#define VPOS_FACTOR		100	//多分係数の意味では・・・やはり1/100で記録してるっぽい
#define TEXT_AHEAD_SEC	(1 * VPOS_FACTOR)
#define TEXT_SHOW_SEC	(4 * VPOS_FACTOR)
#define TEXT_SHOW_SEC_S	(3 * VPOS_FACTOR)
#define TEXT_HEIGHT		22

#define CMD_LOC_DEF		(0)
#define CMD_LOC_TOP		(1)
#define CMD_LOC_BOTTOM	(2)
#define CMD_LOC_NAKA	(3)
#define CMD_LOC_MAX		4
#define CMD_LOC_ALL		(-1)

static char* const COM_LOC_NAME[CMD_LOC_MAX] = {
	"def",	//CMD_LOC_DEF
	"ue",	//CMD_LOC_TOP
	"shita",	//CMD_LOC_BOTTOM
	"naka",	//CMD_LOC_NAKA
};

#define GET_CMD_LOC(x)	((x) & 3)
#define GET_CMD_DURATION(x)	(((x) >> 16) & 0xffff)
#define GET_CMD_FULL(x)	((x) & 4)
#define GET_CMD_WAKU(x)	((x) & 8)
#define GET_CMD_SCRIPT(x)	((x) & 16)
#define GET_CMD_PATISSIER(x)	((x) & 32)
#define GET_CMD_INVISIBLE(x)	((x) & 64)
#define GET_CMD_IS_BUTTON(x)	((x) & 128)
#define GET_CMD_REPLACE_USER(x)	((x) & 256)
#define GET_CMD_REPLACE_OWNER(x)	((x) & 512)
#define GET_CMD_ENDER(x)	((x) & 1024)
#define GET_CMD_ITEMFORK(x)	((x) & 2048)
#define GET_CMD_FONT(x)	(((x)>>12) & 0x3)

#define CMD_FONT_MAX	4
#define CMD_FONT_DEF	0
#define CMD_FONT_BIG	1
#define CMD_FONT_SMALL	2
#define CMD_FONT_MEDIUM	3

static char* const COM_FONTSIZE_NAME[CMD_FONT_MAX] = {
	"medium",	//CMD_FONT_DEF
	"big",
	"small",
	"medium",	//CMD_FONT_MEDIUM
};

static const int LINEFEED_RESIZE_LIMIT[CMD_FONT_MAX] = {
//  Lines of this limit should be resized.
	5,//DEF
	3,//BIG
	7,//SMALL
	5,//MEDIUM=DEF
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

The REAL is as followwing. THANKS for Comment Artisan! A.K.A. SHOKUNIN!
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
//Line Skip (96dpi), Not resized, without shadow, in Windows
static const int FONT_PIXEL_SIZE[CMD_FONT_MAX] = {
	29,//DEF
	45,//BIG
	18,//SMALL
	29,//MEDIUM
};
//Line Skip (96dpi), Resized, without shadow, in Windows
static const int LINEFEED_RESIZED_PIXEL_SIZE[CMD_FONT_MAX] = {
	15,//DEF
	24,//BIG
	10,//SMALL
	15,//MEDIUM
};
// Base is font size comparison by RAW EYES (using browser and notepad). [Gothic]
static const float LINEFEED_RESIZE_FONT_SCALE[CMD_FONT_MAX] = {
	0.500f,	// 12/24
	0.513f,	// 20/39
	0.533f,	//  8/15
	0.500f,	// 12/24
};
// Base is Surface height after SDL rendering [Gothic?]
static const float LINEFEED_RESIZE_SCALE[2][CMD_FONT_MAX] = {
	{ //flash
	0.519f,	// 0.517 15/29  0.518 378/730(25Lines) 0.519 108/208( 7Lines)
	0.535f,	// 0.533 24/45  0.534 387/725(16Lines) 0.535  99/185( 4Lines)
	0.557f,	// 0.556 10/18  0.556 383/689(38Lines) 0.557 103/185(10Lines)
	0.519f,	// 0.517 15/29  0.518 378/730(25Lines) 0.519 108/208( 7Lines)
	},
	{ //html5
	  // 最小二乗法　 not resized       Line resized
	  // 定数+補正　big   medium small  big    medium  small
	  //  a       42.452 27.143 16.95  22.356 14.094  9.4058
	  //  b        4.05   5.076  4.9    2.6     2.748 3.138
	0.519f,	// 0.519  14.094/27.143  0.520 355/683(25Lines) 0.518 101/195( 7Lines)
	0.527f,	// 0.527  22.356/42.452  0.527 360/683(16Lines) 0.532  92/173( 4Lines)
	0.555f,	// 0.555   9.4058/16.95  0.555 360/649(38Lines) 0.557  97/174(10Lines)
	0.519f,	// 0.519  14.094/27.143  0.520 355/683(25Lines) 0.518 101/195( 7Lines)
	},
};
/*
LineFeed Resize Of FontSize(font_height surface_height/96dpi) [gothic]
        DEF  BIG  SMALL  DEF  BIG  SMALL
orig    24   39   15     29   45   18
resized 12   20    8     15   24   10
scale%  50.0 51.3 53.3   51.7 53.3 55.6
*/

//pixel size in 96dpi Windows
static const int COMMENT_FONT_SIZE[CMD_FONT_MAX] = {
	24,//DEF
	39,//BIG
	15,//SMALL
	24,//MEDIUM
};
//points, i.e. pixel size in 72dpi
static const int COMMENT_POINT_SIZE[CMD_FONT_MAX] = {
	18,	//def DEF
	29,	//big BIG
	11,	//small SMALL
	18,	//medium MEDIUM
};
//Big double resize height
/*
big二重リサイズ9行(高さ392px・固定時横幅1041～1067px)： 漢字幅26文字程度
big二重リサイズ10行(高さ384px・固定時横幅1137～1167px)： 漢字幅×29文字程度
big二重リサイズ11行(高さ389px・固定時横幅1247～1283px)： 漢字幅×32文字程度
big二重リサイズ12行(高さ388px・固定時横幅1385～1433px)： 漢字幅×35文字程度
big二重リサイズ13行(高さ381px・固定時横幅1553～1565px)： 漢字幅×39文字程度
big二重リサイズ14行(高さ381px・固定時横幅1619～1687px)： 漢字幅×41文字程度
*/
static const int COMMENT_BIG_DR_HEIGHT[16] = {
	NICO_HEIGHT,NICO_HEIGHT,NICO_HEIGHT,NICO_HEIGHT,NICO_HEIGHT,	//0-4
	NICO_HEIGHT,NICO_HEIGHT,NICO_HEIGHT,NICO_HEIGHT,	//5-8
	392,		//9行
	384,		//10
	389,		//11
	388,		//12
	381,		//13
	381,		//14
	384,		//15
};

#define CMD_COLOR_MAX	22
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
#define CMD_COLOR_WHITE	17
#define CMD_COLOR_PINK2 18
#define CMD_COLOR_CYAN2 19
#define CMD_COLOR_BLACK2 20

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
	//white spcified
	{0xff,0xff,0xff,0x00},//DEF
	{0xff,0x33,0xcc,0x00},//PINK2
	{0x00,0xcc,0xcc,0x00},//CYAN2
	{0x66,0x66,0x66,0x00},//BLACK2
	{0x33,0x66,0xff,0x00},//BLUE2(NP3から動画のみ)
};

//
static char* const CMD_COLOR_NAME[CMD_COLOR_MAX] = {
	"def",
	"red",
	"orange",
	"yellow",
	"pink",
	"blue",
	"purple",
	"cyan",
	"green",
	"white2",	//nicowhite
	"blue2",	//marineblue
	"yellow2",	//madyellow
	"orange2",	//passionorange
	"violet2",	//nobleviolet
	"green2",	//elementalgreen
	"red2",		//truered
	"black",
	"white",
	"pink2",
	"cyan2",
	"black2",
	"blue2N",	//marineblueN
};

static const int CA_FONT_HIGHT_TUNED[4][2][CMD_FONT_MAX] = {
//	{{DEF,BIG,SMALL,MEDIUM,},{DEF,BIG,SMALL,MEDIUM,}for fontsize_fixed},
	{{24,38,16,24,},{47,74,31,47,}},	//gothic glyph-advance width is {25,40,16}{50,80,33(>32)}
										//setting SDL size is {23,37,15},{46,73,30}
	//specially 2000-200f,3000 is other definition
	{{24,38,16,24,},{47,74,31,47,}},	//simsun
	{{24,38,16,24,},{47,74,31,47,}},	//gulim
	{{24,41,15,24,},{46,78,30,47,}},	//arial glyph-advance width of'a' is {13,22,8},{26,44,16}
										//setting SDL size is {20,36,13},{40,69,26}
	//specially 00A0 & 0020 is other definition
};

static const int CA_FONT_WIDTH_TUNED[4][2][CMD_FONT_MAX] = {
//	{{DEF,BIG,SMALL,MEDIUM,},{DEF,BIG,SMALL,MEDIUM,}}
	{{25,40,16,25,},{50,80,32,50,}},	//gothic
	{{25,40,16,25,},{50,80,32,50,}},	//simsun
	{{25,40,16,25,},{50,80,32,50,}},	//gulim
	{{13,22, 8,13,},{26,44,16,26,}},	//arial 'a'
};

static const int CA_FONT_SIZE_TUNED[4][2][CMD_FONT_MAX] = {
//	{{DEF,BIG,SMALL,MEDIUM,},{DEF,BIG,SMALL,MEDIUM,}for fontsize_fixed},
	{{23,37,15,23,},{46,73,30,46,}},	//gothic
	{{23,37,15,23,},{46,73,30,46,}},	//simsun
	{{23,37,15,23,},{46,73,30,46,}},	//gulim
	{{20,36,13,20,},{40,69,26,40,}},	//arial
};

//									[font_sel][SIZE]
static const int CA_FONT_NICO_WIDTH[4][CMD_FONT_MAX] = {
	{25,40,16,25},		//gothic
	{25,40,16,25},		//simsun
	{25,40,16,25},		//gulim
	{25,40,16,25},		//arial
};
/*
参照teacup掲示板　CA研究用1　http://8713.teacup.com/cas/bbs/t2/17

文字の幅一覧（一部） 投稿者：チャリイ 投稿日：2009年 2月13日(金)04時05分32秒 返信
  ありがとうございます．お蔭様でタブの数字も集まりました．
以下がそれを含めた空白関係の幅の表です．ツンデレは20と同じ値になります．
勿論大部分は以前ぐだぐださんと一緒に作ったものです．

Win, Mac (b, m, s, br, mr, sr)
2000　21,20　13,12　09,13　11,10　07,06　05,04
2001　40,39　25,24　16,15　21,20　13,12　09,08
2002　21,20　13,12　09,08　11,10　07,06　05,04
2003　40,39　25,24　16,15　21,20　13,12　09,08
2004　14,13　09,08　06,05　08,07　05,04　04,03
2005　12,10　07,06　05,04　06,05　04,03　03,02
2006　07,07　05,04　03,03　04,03　03,02　02,01
2007　25,26　16,16　10,10　14,13　09,08　06,05
2008　13,10　09,06　06,04　07,05　05,03　04,02
2009　06,08　04,05　03,03　04,04　03,02　02,02
200a　03,05　03,03　02,02　02,03　02,02　02,01
3000　27,39　17,24　11,15　14,20　09,12　06,08
0020　11,11　07,07　04,04　06,06　03,03　02,02
0009　76,84　44,52　28,33　40,43　24,27　16,16
（全角スペース＝3000，半角スペース＝20，タブ＝9）
 */

static const int CA_FONT_2000_WIDTH[16][CMD_FONT_MAX] = {
//	DEF BIG SMALL MEDIUM msgothic.ttc#1
	{13,21, 9,13},		//2000
	{25,40,16,25},		//2001
	{13,21, 9,13},		//2002
	{25,40,16,25},		//2003
	{ 9,14, 6, 9},		//2004
	{ 7,12, 5, 7},		//2005
	{ 5, 7, 3, 5},		//2006
	{16,25,10,16},		//2007
	{ 9,13, 6, 9},		//2008
	{ 4, 6, 3, 4},		//2009
	{ 3, 3, 2, 3},		//200a
	{ 0, 0, 0, 0},		//200b
	{ 0, 0, 0, 0},		//200c
	{ 0, 0, 0, 0},		//200d
	{ 0, 0, 0, 0},		//200e
	{ 0, 0, 0, 0},		//200f
};
static const int CA_FONT_SPACE_WIDTH[CMD_FONT_MAX] = {
//ASCII SPACE 0020/00A0 arial.ttf
	7,11,4,7	//2012.3.24変更
};
#define CA_FONT_3000_WIDTH_SZ	4
static const int CA_FONT_3000_WIDTH[CA_FONT_3000_WIDTH_SZ][CMD_FONT_MAX] = {
//KANJI SPACE 3000 msgothic.ttc#1
	{17,27,11,17},		//gothic
	{25,40,16,25},		//simsun
	{25,40,16,25},		//gulim
	{25,40,16,25},		//MingLiu
};
static const int CA_FONT_TAB_WIDTH[CMD_FONT_MAX] = {
//TAB SPACE 0009
	44,76,28,44,
};
/*
 * CJKフォントの既定文字幅
 * U+3400-D7FF	全角、漢字幅
 * U+E750-F8FF	全角、漢字幅(私用領域、外字)
 * U+F900-FAFF	全角、漢字幅
 */
static const Uint16 KANJI_WIDTH[] =
	{0x3400,0xd7ff,0xe750,0xf8ff,0xf900,0xfaff,0,0};

/*
 * コメントタイプ CID
 */
#define CID_USER 0
#define CID_OWNER 1
#define CID_OPTIONAL 2
#define CID_MAX 3
static char* const COM_TYPE[CID_MAX] = {
	"user", "owner", "optional",
};

//HTML5 font types
#define HTML5_FONT_TYPES	3
#define HTML5_FONT_DEFONT	0
#define HTML5_FONT_MINCHO	1
#define HTML5_FONT_GOTHGIC	2
/**
 * ヒロスさんのブロマガより
 * http://ch.nicovideo.jp/883797/blomaga/ar1149544
 *
 * 最小二乗法　 not resized       Line resized
 * 定数+補正　big   medium small  big    medium  small
 *  a       42.452 27.143 16.95  22.356 14.094  9.4058
 *  b        4.05   5.076  4.9    2.6     2.748 3.138
 */

//Line Skip (96dpi), in HTML5
static const float HTML5_PIXEL_SIZE[2][CMD_FONT_MAX][2] = {
	{	//Not resized
		{27.143, 5.076},	//DEF
		{42.452, 4.050},	//BIG
		{16.950, 4.900},	//SMALL
		{27.143, 5.076},	//MEDIUM
	},
	{	//linefeed resized
		{14.094, 2.748},	//DEF
		{22.356, 2.600},	//BIG
		{9.4058, 3.138},	//SMALL
		{14.094, 2.748},	//MEDIUM
	},
};

#define HTML5_FONT_MAX 3
#define HTML5_FONT_DEFONT 0
#define HTML5_FONT_MINCHO 1
#define HTML5_FONT_GOTHIC 2

static const float HTML5_2000_WIDTH[16][CMD_FONT_MAX] = {
//	DEF  BIG   SMALL MEDIUM msgothic.ttc#1
	{13  ,19,   9,  13},		//2000
	{26  ,38,  19,  26},		//2001
	{13  ,19,   9,  13},		//2002
	{26  ,38,  19,  26},		//2003
	{ 9  ,13,   6,   9},		//2004
	{ 6.5,10,   5, 6.5},		//2005
	{ 4.3,6.3,3.2, 4.3},		//2006
	{13,  19,   9,  13},		//2007
	{ 5,   7,   3,   5},		//2008
	{ 5,   7,   3,   5},		//2009
	{ 3,   3,   2,   3},		//200a
	{ 0, 0, 0, 0},		//200b
	{ 0, 0, 0, 0},		//200c
	{ 0, 0, 0, 0},		//200d
	{ 0, 0, 0, 0},		//200e
	{ 0, 0, 0, 0},		//200f
};
static const float HTML5_SPACE_WIDTH[CMD_FONT_MAX] = {
//ASCII SPACE 0020/00A0 arial.ttf
	6.625,9.57,4.45,6.625	//2012.3.24変更
};
#define HTML5_3000_WIDTH_SZ	4
static const float HTML5_3000_WIDTH[CA_FONT_3000_WIDTH_SZ][CMD_FONT_MAX] = {
//KANJI SPACE 3000 msgothic.ttc#1
	{25.6,36.7,17.1,25.6},	//defont(gothic)
	{25.6,36.7,17.1,25.6},	//mincho(simsun)
	{25.6,36.7,17.1,25.6},	//gothic(gulim)
	{25.6,36.7,17.1,25.6},	//Arial
};
static const int HTML5_TAB_WIDTH[CMD_FONT_MAX] = {
//TAB SPACE 0009
	52,76,36,52,
};

static const int HTML5_FONT_WIDTH_TUNED[4][2][CMD_FONT_MAX] = {
//	{{DEF,BIG,SMALL,MEDIUM,},{DEF,BIG,SMALL,MEDIUM,}}
	{{24,35,17,24,},{48,69,33,48,}},	//gothic for DEFONT
	{{25,37,17,25,},{51,73,34,51,}},	//simsun for MINCHO
	{{25,37,17,25,},{51,73,34,51,}},	//gulim for GOTHIC
	{{24,35,16,24,},{46,70,32,46,}},	//arial & other
};
static const int HTML5_FONT_HIGHT_TUNED[4][2][CMD_FONT_MAX] = {
//	{{DEF,BIG,SMALL,MEDIUM,},{DEF,BIG,SMALL,MEDIUM,}for fontsize_fixed},
	{{25,36,18,25,},{49,70,34,49,}},	//gothic for DEFONT
	{{28,42,18,28,},{57,82,36,57,}},	//simsun for MINCHO
	{{28,42,18,28,},{57,82,36,57,}},	//gulim for GOTHIC
	{{28,40,19,28,},{52,79,36,52,}},	//arial & other
};


#endif /*NICODEF_H_*/

/*
 * unitable.h
 *
 *  Created on: 2011/12/13
 *      Author: orz
 */

#ifndef UNITABLE_H_
#define UNITABLE_H_

/*
 以下のテーブルの文字の意味は次の通り
 - USUAL_FONT 	usually gothic, may change simsun or gulim
 U MS UI GOTHIC
 g GOTHIC_FONT    	protect gothic	30fb ・中点 ff61-ff9f 半角カナ
 G GOTHIC_FONT    	gothic Not Change
 w SIMSUN_FONT    	weaker than GULIM, change simsun
 s SIMSUN_FONT    	change simsun
 S SIMSUN_FONT    	simsun Not Change
 m GULIM_FONT     	change Maru Gothic (malgun.ttf win10)
 M GULI_FONT		Maru Gothic Not Change (malgun.ttf win10)
 A ARIAL_FONT     	arial
 J GEORGIA_FONT   	(win=sylfaen.ttf)
 D DEVANAGARI		(win=mangal.ttf) India
 T TAHOMA_FONT		(win=Tahoma) Thai etc.
 L MINGLIU_FONT		(win=MingLiU) 繁字体、変化なし
 N N_MINCHO_FONT	(win=SIMSUN XP=NGULIM) 明朝、変化なし
 E ESTRANGELO_EDESSA	シリア
 B BENGAL_FONT   	(vrinda.ttf)Bangla lipi
 C TAMIL_FONT 	 	(latha.ttf)Tamil
 e LAOO_FONT		(laoui.ttf)laoo
 f GURMKHI_FONT		(raavi.ttf)Gurmkhi
 k KANNADA_FONT		(tunga.ttf)カナラ
 h THAANA_FONT		(mvboli.ttf)ターナ
 i MALAYALAM_FONT	(kartika.ttf)マラヤラム
 I TELUGU_CHAR		(gautami.ttf)テルグ
 Z ZERO_WIDTH     	(200b-200f)2000series 2028-202f (No Font Griph)

 参考資料（Windows Vistaに基づく）
 http://haraise.web.fc2.com/hyou.htm
 http://haraise.web.fc2.com/hyou_t.htm
 http://haraise.web.fc2.com/hyou_kanji1.htm
 http://haraise.web.fc2.com/hyou_kanji2.htm
 http://haraise.web.fc2.com/hyou_kanji3.htm
 http://haraise.web.fc2.com/hyou_kanji4.htm
 修正 (Win7に基づく) Using WpfFontViewer 1.0.0.0 by mikeo_410さん

*/

#define UNSUPPORTED_CHAR '.'
#define USUAL_CHAR '-'
#define UI_GOTHIC_CHAR 'U'
#define GOTHIC_CHAR 'g'
#define GOTHIC_NOT_PROTECT 'G'
#define WEAK_SIMSUN_CHAR 'w'
#define STRONG_SIMSUN_CHAR 's'
#define SIMSUN_NOT_CHANGE_CHAR 'S'
#define GULIM_CHAR 'm'
#define ARIAL_CHAR 'A'
#define GEORGIA_CHAR 'J'
#define DEVANAGARI_CHAR	'D'
#define TAHOMA_CHAR	'T'
#define MINGLIU_CHAR 'L'
#define N_MINCHO_CHAR 'N'
#define ESTRANGELO_EDESSA 'E'
#define GUJARATI_CHAR 'j'
#define BENGAL_CHAR 'B'
#define TAMIL_CHAR 'C'
#define LAOO_CHAR 'e'
#define GURMUKHI_CHAR 'f'
#define KANNADA_CHAR 'k'
#define THAANA_CHAR 'h'
#define MALAYALAM_CHAR 'i'
#define TELUGU_CHAR 'I'
#define ZERO_WIDTH_CHAR 'Z'

/* コメントアウトは参考資料による定義 */
static const Uint8 UNITABLE[] =
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*0000*/ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA--------------------------------G-----------------------------------------------------------------------------------------------"
/*0100*/ "--------------------------------------mm----------mm----m------mm--------mmm--------------------------mm------------------------AAAAAAAAAAAAAAAAAA--AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA-AAAAAAAAAA-s-s-s-s-s-s-s-sAAAAAAAAAAAAAAAAAAAAAAAA-AA--------"
/*0200*/ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA-s---------------s-----------------------------------------------------------------------AAAAAAAAAAAAAAAAAAAAAAAAAAAAA---wss-AAAm-AAAAAA--m----AAAAAA-----AAAAAAAAAAAAAAAAAAAAAA"
/*0300*/ "----ZA-A-AA--AA-AAAAAAAA---A----ZAAZ--AAA--A-AA--AAA-AAAA-----AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA-AAAAAAAAAAAAAA....--....-AAA-.....-------.-.--------------------.--------------------------------------------.AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
/*0400*/ "A------------A------------------------------------------------------------------A------------A-----------------------------------------AAAAAAAAA-----------------------------------------------------AA--AA--AAA----------------------------AA--------AA--AAAAAA"
/*0500*/ "AAAAAAAAAAAAAAAAAAAA......AAAA...................TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT..TTTTTTT.TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT.TT......AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA........AAAAAAAAAAAAAAAAAAAAAAAAAAA.....AAAAA..........."
/*0600*/ "AAAA.......AAAAAAAAAAA.....A..AA.AAAAAAAAAAAAAAAAAAAAAAAAAA.....AAAAAAAAAAAAAAAAAAAAAZAAAAAAAAA.AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
/*0700*/ "EEEEEEEEEEEEEE.EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE..EEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA..................hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh.............................................................................."
/*0800*/ "................................................................................................................................................................................................................................................................"
/*0900*/ ".DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD..DDDDDDDDDDDDDDDDDD..DDDDD...DDDDDDDDDDDDDDDDDDDDDDDDDDD........DDDDD.BBB.BBBBBBBB..BB..BBBBBBBBBBBBBBBBBBBBBB.BBBBBBB.B...BBBB..BBBBBBBBB..BB..BBBB........B....BB.BBBBB..BBBBBBBBBBBBBBBBBBBBB....."
/*0a00*/ ".fff.ffffff....ff..ffffffffffffffffffffff.fffffff.ff.ff.ff..f.fffff....ff..fff...f.......ffff.f.......ffffffffffffffff...........jjj.jjjjjjjjj.jjj.jjjjjjjjjjjjjjjjjjjjjj.jjjjjjj.jj.jjjjj..jjjjjjjjjj.jjj.jjj..j...............jjjj..jjjjjjjjjjjj.............."
/*0b00*/ "..................................................................................................................................CC.CCCCCC...CCC.CCCC...CC...CC...CC...CCC...CCCCCCCCCCCC....CCCCC...CCC.CCCC..C......C..............CCCCCCCCCCCCCCCCCCCCC....."
/*0c00*/ ".III.IIIIIIII.III.IIIIIIIIIIIIIIIIIIIIIII.IIIIIIIIII.IIIII...IIIIIIII.III.IIII.......II.II......IIII..IIIIIIIIII........IIIIIIII..kk.kkkkkkkk.kkk.kkkkkkkkkkkkkkkkkkkkkkk.kkkkkkkkkk.kkkkk..kkkkkkkkk.kkk.kkkk.......kk.......k.kkkk..kkkkkkkkkk.kk............."
/*0d00*/ "..ii.iiiiiiii.iii.iiiiiiiiiiiiiiiiiiiiiii.iiiiiiiiiiiiiiii...iiiiiiii.iii.iiii.........i........iiii..iiiiiiiiiiiiiiii...iiiiiii................................................................................................................................"
/*0e00*/ ".TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT....-TTTTTTTTTTTTTTTTTTTTTTTTTTTT.....................................ee.e..ee.e..e......eeee.eeeeeee.eee.e.e..ee.eeeeeeeeeeeee.eee..eeeee.e.eeeeee..eeeeeeeeee..eeee................................"
/*0f00*/ "................................................................................................................................................................................................................................................................"
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*1000*/ "................................................................................................................................................................................................................JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJ..."
/*1100*/ "................................................................................................................................................................................................................................................................"
/*1200*/ "................................................................................................................................................................................................................................................................"
/*1300*/ "................................................................................................................................................................................................................................................................"
/*1400*/ "................................................................................................................................................................................................................................................................"
/*1500*/ "................................................................................................................................................................................................................................................................"
/*1600*/ "................................................................................................................................................................................................................................................................"
/*1700*/ "................................................................................................................................................................................................................................................................"
/*1800*/ "................................................................................................................................................................................................................................................................"
/*1900*/ "................................................................................................................................................................................................................................................................"
/*1a00*/ "................................................................................................................................................................................................................................................................"
/*1b00*/ "................................................................................................................................................................................................................................................................"
/*1c00*/ "................................................................................................................................................................................................................................................................"
/*1d00*/ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA...................................................AA"
/*1e00*/ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA--AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA------AAAAAAAAAAAAAAAAAAAAAA..A.AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA--AAAAAA......"
/*1f00*/ "AAAAAAAAAAAAAAAAAAAAAA..AAAAAA..AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA..AAAAAA..AAAAAAAA.A.A.A.AAAAAAAAAAAAAAAAA----AAAAAAAAAA..AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA.AAAAAAAAAAAAAAA.AAAAAAAAAAAAAA..AAAAAA.AAAAAAAAAAAAAAAAAAA..AAAAAAAAAAAAA."
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*2000*/ "-----------ZAZZZ----S-s-----------------ZZZZZZZZ-----s--------------------.......-............A...........AAAAAA----m----------m-mmmm----------.AAAAA...........------------------AAAA..AA......................................................A..............."
/*2100*/ "-----s---s---------m-------------------------------------....................AA....mm-------mmm-----------ss-----------------------.A...........----mmwwww---------------------------------------------------------------------------------....................."
/*2200*/ "---------------w-----w-------------s------------------ss----m-----------w---s-----------------------ww--------ss-------------------------------------s---w----------------------------------------------------------------------------------------.............."
/*2300*/ "..-..--L........-.-.....-.......--............................................................................................................................................................---------------.-...........--...................................."
/*2400*/ "...................................-............................................................--------------------wwwwwwwwwwwwwwwsssssssssssssssssssssssssmmmmmmmmmmmmmmmmmmmmmmmmmm--------------------------mmmmmmmmmmmmmmmmmmmmmmmmmm---------------------."
/*2500*/ "----ssssssss-ww--ww--ww--ww---ww-ww---ww-ww--ww--ww--ww--ww--ww-ww-wwwwwwww-----wwwwwwwwwwwwwwwwwwwwwwwwwwwwwsssssss-------------ssswssswssswsss--mwss..........---mmmmmmm------------mm--------mm------m-------mm----------------ssss----------................"
/*2600*/ "---------s----mm----..--..--m-m-----------------------------------------------------------------mm-mmm-mmm--m---EE.............................................................................................................................................."
/*2700*/ ".----.----..----------------------------.-----------------------------------.-.----...-.-------..-------..............-------------------------------...------------------------.--------------................................................................."
/*2800*/ "................................................................................................................................................................................................................................................................"
/*2900*/ "....................................................--...............................................................................--........................................................-..........................................................--...."
/*2a00*/ "................................................................................................................................................................................................................................................................"
/*2b00*/ "................................................................................................................................................................................................................................................................"
/*2c00*/ "................................................................................................AAAAAAAAAAAAAA...AAAAAAA........................................................................................................................................"
/*2d00*/ "................................................................................................................................................................................................................................................................"
/*2e00*/ ".......................A.........................................................................S..S...S..SS..........................................S...............S..S...S....S..SS...S..............S....................................................."
/*2f00*/ "................................................................................................................................................................................................................................................SSSSSSSSSSSS...."
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*3000*/ "----------------------ss------s--sssssssss......--------...---S..--------------------------------------------------------------------------------------..-----g--------------------------------------------------------------------------------------------g----"
/*3100*/ ".....sssssssssssssssssssssssssssssssssssss.......mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm...LLLLLLLLLLLLLL................................................................................----------------"
/*3200*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmm...ssssssssss--------------------------.............---------------mmmmmmmmmmmmmmmmmmmmmmmmmmmm...M-----------------------------------s----------------------------------------....-----------------------------------------------."
/*3300*/ "-----------------------------------------------------------------------------------------------------------------------....-----mmmmm---mmmmmm--mmmmmmmmmmmm---mm-mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm---smmssm-sm-m--mmm..-------------------------------."
/*3400*/ "NN-NNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-N-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-N-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNN-NNNN-NNNNN-NNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"
/*3500*/ "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN--NNNN-NNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-N-NNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNN"
/*3600*/ "NNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNN-NN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"
/*3700*/ "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN--NNNNNNNN--NNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNN-NNNNNNNNNNN-NNNNNNNN-NN"
/*3800*/ "-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNN-NNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNN"
/*3900*/ "NNNNNNNNNNNNNNNNNNNNNNN-NN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"
/*3a00*/ "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN--NNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNN"
/*3b00*/ "NNNNNNNNNNNNNN-NNNNNNNNNNN-N-NNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNN-NNNNNNNNNNNNNNN--NNNN-NNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNN-NNNNNNNNNNNN-NNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNN"
/*3c00*/ "NNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"
/*3d00*/ "NNNNNNNNNNNNNNNNN-NNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"
/*3e00*/ "NNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNN-N-NNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"
/*3f00*/ "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNN-NN-N-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*4000*/ "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"
/*4100*/ "NNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNN-NNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNN-NNNN-NNNNNNNNNNNN"
/*4200*/ "NNNNNNN-NNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNN-NNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"
/*4300*/ "NN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-N-NNNNNNNNNNNNNNN"
/*4400*/ "NNNNNNNN-NNNNNNNNNNNNNN-NNNN-NNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNN-NNN-NNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"
/*4500*/ "NNNNNNNN-NNNN-NNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNN-NNNNNNNNNNNNNNNNNNNNN"
/*4600*/ "NNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"
/*4700*/ "NNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NN"
/*4800*/ "NNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"
/*4900*/ "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNN-NNNNN"
/*4a00*/ "NNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"
/*4b00*/ "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNN-NNNNNNN-NNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNN"
/*4c00*/ "NNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN"
/*4d00*/ "NNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN-NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN.........................................................................."
/*4e00*/ "--s-sss-----s--s--ss------ssss-ss-ssss-s-s-ss-sss--sss-s--s--ss-ss--s-sssss-s---sssss-----sss---ss-ssssssssssssss-s-ssssssssss-s-s-ss--s-----s-ss--s--ss--s--s-----s---s-ss----s-ss-ss-sss-sssss---s-s--ss--s---ssss------sss---s-s---sssssss--s-s-sss--sss--sss"
/*4f00*/ "--s-sssss--ss-----ssssssss-s--sssssssssssssssss--sss-s-s---s--sssss-ss--sssss-----s-s---s------ssssssssss-sssss--ss-s--ssss--ss-s--s-s-s-s--s-sss--s-s-s-s--s-ss--sssssssss-s---sssss--ssssssss-ss---ssss--ss--s--ss-ss--s--s-s-s-s---ssssssss--sss-s--s-s-sss--"
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*5000*/ "sssss--ss-s-s-s-s--s-s-ss--sss--s-------------ssssssss-ss-ssssss-s--ss----sssss--ssss--sss-s-ssssssss-ssssss-sss-s-s---s-ssss-ss-ssss-sssssss-sss-ss-sss---sssssssssssssssss--ssss----s-ssssss-sss-ss-sss--ss-s-s-sss--s-s-sss-ssss-s-s-sssss--sssss--sss-s-ssss"
/*5100*/ "---s-ssss-ssssssss-s---s-s-ssss-s-ssssssss-sssssss-ssss-ss---ss---s------------s-s-s-sssss-s-sssss-s--ss-------ss-sss----sss-sss-s-ss--ss--s--s-----s---s-sss-ss-s-s---s-----sss--------sssss--sssss---ss-s---ssssssss-ssss---ss--ssss--s--s--ss--sss--s---ss--s"
/*5200*/ "-ss--s---s--ss-ss-ss--s-sssss-ssssss--s-s--sss-s-ss-ss------sssssss--ss-ss----s-ssss-s-ssss-ss-ssss---sss--ssss-------sssssss-s-sss-sss---sss-sss--s-ssssss--ss--ss-ss-ss-----s-s-ss--sss-ss-s-s--s-s-s-s-sss-ssss-ss-s---s-s----s---s--ssssssssss--s-ss---sss--"
/*5300*/ "---ss----ssss-s--ssss---s--ss-ss--s--sssss-ssss-s-s-ssss----sss---s-s------ss-sss----ss--s-s-s-s-sssss-ss-ssss--------s--ss-sss-ss-s-ssssssssssssss-ss-s-s-ssss--ssss--s--sss--s-s--ss-ssss-ssssss--ssss-------sssss-s--s-s-s-s-s-----ss------------ss---s-sssss"
/*5400*/ "s-s--sss----------sssssssss-s-s--sssss-ss-s----sssssss-s--s----s-s-sss-s---sss-ss-sssssssssssss-ssssssss-s-sssss--s-s---sss---ss-sss-s-sss---s---s-sssssssss-sssss-s--ss--s--ss-ss--ssss-sss---s---s-ss---ssssssssssssss-ssssssss--ss--s--sss--sss-sssssss-ss-s-"
/*5500*/ "ssss-s--sssssss--sss-s-sssssssssssssssssssssss--s-s-ssss--ssss-s-sss---sssss-ss-sss-ss--ssss--sssss-sssssssssssssssssssssss--s-s-ss--s--s---ssssssssssss---s----sssssss------s-s-sssss-sssssssssssss--s-ssssssssssss-sssss-s-ss-sss--ssssssssssssssssss-s-sss--s"
/*5600*/ "ssssss-ss-ssssssssss-s---ss-sssssssssssss-sssss-s--s-s-s-sssssssss-sssssssss-s-s-ssssssssss-ssssssss-sss-s---sssssss-sss-s-sssss-sssss--ss-ssss-ssss-sssssssssss-s-ss-ssssssss-sssss-s-sssss-sss----ssss-sssss-ss-s-sss--s--ss-s-ss-ssssssssss-s-s--sssss--ss-s-"
/*5700*/ "-ss--sss--s-s-s-ss--ss-s-sss-ss-ssssss---ssss-ss-ssssss--ss-ssss-s-ssss-ss-sss----sssssss-sssssss-ss---ss--ssssssssssssssssssss-ss-sssss--s-sssssss-ssssssssssss-s---sssss-s-sss-ss-ssssssssssss-ss-ss---ss-ss-sss---s-sssss-ss--ss-ssssssssssssssss-ss-s--s-sss"
/*5800*/ "-s-ss--sss--sssssssss-sss-sss-sss-ss-sssss-ssss---ss--ssss-ss-ss--ssssssss--sssss--s-ss----sss-sss-ssssss-s-ssss-s-ss-sss-ssss-ssss-s-sssssssssssss-sss-ssss-s--ssssssss-ss-ss-sss--ssss----ss-ss-sss-s-ss-s-ssss-s-s-s---ss-s--ssss--sssss--s-----ssss-s-----ss"
/*5900*/ "ss-ssssss---sss--ssss--s-----sssss-ss-s-s------ss--ssss--sssss-sssss-ss---ssss----s---s--s--s-ss-s--s-s----s-s-ssss--sss-ssss-sss----sssss-ss-sssss-ss-ss-s-s-sssss---ss-sss-sssss-ssssss---ss-sssssss-ss-s-ssss--s--ssss--s-ssssssss--s-s--ssssssssss-ssss-sss-"
/*5a00*/ "s-s-sssss-sssssss-ssssss-s-s-ss--ssss-sss-sssss-sssss--sssss-sss--ssss-ss-ssssssssssssssss-sssssss-sss-sss-s-ssssssssssssssssss-ssssssssssssssssss-sssssss--ssssssssssssssssssssssssssssssss---ss--ssssss-s--sss-sssss--sssssssss-s-ss-ss-ssssssssssssssss--ssss"
/*5b00*/ "sssssssss-s--sssssssss-sssssssssss-sssssss-s-sss-s-sss-sssssss-s-ss-s-ssssssssss--ss-----s----s-sss----ss-s-ssss--s-s-ss-s-sssss-ss-s-s---s---s-sssss-s-------s-ss-----sssssss-s-ss----s--sssss--s------s-ss-sss-s---sss-ss-s---s--s------s--s-s-ss-s--s-s-sss--"
/*5c00*/ "s--s--------s---s-s-ss-sss-sss-s-s-s-sss-ssss-sss-ssssss----------sss--s-s--s-----s-s-ssssssss-s--ss--ssssss-s--s-ssss-ss-ssssssssssssssssss-sss--ss-ssssssssssss-ssss-s--s--ssss-s-ss---s---s-ssssss-s-sssssssssssssssss-ssssss--ssssss---ss-s--ssss--sss--s-ss"
/*5d00*/ "sssssss-sss-ss-ss-ss--------sss-ss-ssss-s-ssssssssssssssssssssssss-ssssssss--s-s-s--ssssssss-ssssssssssss-ss--s-sss-ss-sssssssssss-s-ss-sss--sss-ssssssssssss-ssss-sssssssss-s-ssssssss----s--sssssssssss-ss--ss-s--ss-ssss-s--ss-s-s----ss-ss-ss-----s-sss-s--s"
/*5e00*/ "ss--ss-ssss--ssss-ssss-ss---s-sssssss-sssss-s-s--ss-ss---ssss-ss-ss---s-ssss-s-sssss--s-sssssss-s----sssssssssssss-----s--------s-s--ss-ss-ssss-sssss---ss-s-sss-sssss--sss-s-sssssss----ssssssss---ssss---ssss--ss-ss-sss--s-s-----ssss--ss-sss--s--s---s---s--"
/*5f00*/ "s-s--ssss-----s---s------ss-sss-s-sss---s-sss-s-s-ss--s--sss-s-ss-sss-ss-s-s-s-ss-s-ss--s-ss--sss--sss--s-----ss--s-sss-s-ss-ss-------s--s---sss----sss---ssss-s--ssssss---ss--ssss--ss-s-ss--sssss-s-ssssss--ssssssss----ss---s-sss-ssssss-ssss--sss-ss-ss-s-s-"
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*6000*/ "ssssssssssssss---s-ss--ss-s---ss--sss-------sss-s-ssssssss-ssssss---ss-sss--s-ss-s-ss-sss--ss-s--s----ss------s--ssss-s-sssssssss-s---sss-----ssss-s-s--ss--sss--ss-ss--s--sssssss-----s-sss--sssssss---sssssssss-s-s-ss-s-s-s----s-sss--sssssss-----s--s---ssss"
/*6100*/ "--s-ss-s--sss---s-sss-ssss--sss---sssss--sss-sss-sss-ss-ssss----ss-s-ss--s-----ssss-s-ss---ss-s-ss--s-s--ss-ss----s-----ssssss-sss-ssss-ss-sss-s--ss-s-s---sssssssss-ss-s-s--s-sss-sss-sss-sss-ssss-ss--------ss-ssssssssssssssssss-ss-sssssssssss-s-s---s-s----"
/*6200*/ "-sssssss---s---s-----s-sss--s---s-ssss-sss-sss---s---sss-ss-sss---sssss---s-s--ssss-s-ss-ss-ss-s-ss-ssss-sssss-ss-ssss-ss-ss-s---s---ssss--ssssss--------ss--s-sssssss-ssss--ssss-sss-sss-s---ssss-ss------s--s------ss---s---ss--ssssssssss----s-s-s---ssssss--"
/*6300*/ "s--ssss---ss-ssss-sssssss-sssss-sssssss--ss-sss-ssssssssss-ss---sssssssss-ss--s--ssss-s-ssss-ssssssssss---s-ss-sss-sss--ss--ssss-ss-ssss--ss-s--ss-sss-s-ss-sss-----s-s------sssss-s--sssss-ss-s-ss--s-ss-sssss--s-sss-sss--sssss-s-sssss-ssss-sssss---sss-sssss"
/*6400*/ "ssssss-ssssss-s-sss-ss--ssss-sssssssss-s-sss--ssssss-s-sss-sss-sss-sssssssssss-sssssssss-sssssss-ssssss-s-sssss-ssssss-s-s-ssssssss-ssss-sssssssss--s-ssss-ss--sssss--sss-s-s--s-s-ssssss-s--ssss--ss-s-sssss--sss-s-sss-s-sssss----ss--ssss-ss-s--s-s-sss-ss--s"
/*6500*/ "-ssss-ssssssssssssssssss-sss--sssss--sssss---ss-ssss------s-ss--sssss-ss-ssss---s-sss-----sss--sss--ss-sssss-sss-s-s--s--sssssssss--sss---ss-s-s--sssss-s-s--ss-s-ss--s-sss---s--ssssss-s-ss--sss-s----ssss--ss-ss-ssss-s-s-ssss---ss-----ss--sss-ssssssss--ssss"
/*6600*/ "-s--ss--s--s-s--sss---ssssss-s---sss--s--ssss---s-ss---ssss--ss-s----ssss-s-sss-ss-ssss-s-sss---ss-s------ssss---ss--s-sss-ssssss-s--ss---ssss-ss-ssss----sss-ss-s-sss-ssss-ss-sss-s-sss--ss-s--s-ss-ss-s-ssssssssssss-ss--s--ss-sssss-ss-ssssss-s----s---------"
/*6700*/ "-ss-ssss--s-s---ssss----sss-s---ssssss---s-----ss-ss-s---s-ss-s-s-ssss-ss-ssss----s-ss-ss-ss-s---------sss-ss-s-----s-s-ssss-s--sssss-s-s-s--sss-ssss-s-ss-s--ss---sss-ss-sssss-sss--s----s-ssss--ss-s-sss-sss----s--sss-s-ss--sss-s-ss-s-ss-s--s-s---sssss-ss--"
/*6800*/ "s----ssssssssssssss-ss--ssssss-ss--ssssss---ssssss-s-sss--ss--ss-----s-s-ssss--s-----ssss-ss--s-sss-sss-ssssssssssss-s--ssssss--s-s-s-sssssss-s-sss--ss-sss-s-s--s-sss---ssss-s---s-s--ss--s-sssssss-s-s----s-s-ss-s--s--s-ssss---s-sss-ssssss--ss-ssssss--sssss"
/*6900*/ "--ss--ss-ss-----ss-ssssss----ssss---s--s-s-sssss-sss-s-ss-sss-s-ssssssssss-ssssssss---sss--s---s---sssss-s--s---sss---s---ss---ss--sssssss-sss-ss-ss--ss-ss--sss-ssssss-ssssss-ss--s-ssssss-ss--s-s-sss-ss-----s-ss-ssss--sss--sss-ssss--ss-s-ssss-ssssss-s-s-s-"
/*6a00*/ "ss-ss-ssss---sssss---ss-s-s-ss--s---sssss---ss-s-ssss--s---ss-ssssss-s---ss-ssssssssssss--sssss-s--sss-ssss-ssssss--ssss-sssss---sss-ssssssss--s-ssssss-ssss-sss-s--ssssss-s-s-ssss-ssss-ss-sssss---sssssssssssss-s-ssssss--ss--ss-s-sss-s-sssssssssssssss--ssss"
/*6b00*/ "ssss--ssss-sssssss-sss-ssssss-s---s-sss-ssssssssss-ssss----ss--ssss-sss-s-ss-s-s-ss--ssss-s-sss-s----s-ss--ssss-sss--sss--s-sss--ss--s-ss---s-sssssss--s-sssss-sssss-sssss--sss-s-----s-ss---ss--ssss--ssss-s--sss---s-s-ss-sss-sssssssssss--ss-sss-ssssssssssss"
/*6c00*/ "ssssssss-ssssss-s-s--ss-sss-sssssss--sssssssssssssss-ss--sssss-----sssssssssss-s-ssss-s-ss-s-----s-sssss-s-ssss--s--ssssss-ss--ss---ss-s-sss--ss-s--ss-ss---sssss--ssssssss-ss-ss-s-ssss--------s-ss--sss--s-ssssss-s-s-s---s-sss---s-ss-s-ssss---s-ssssssssssss"
/*6d00*/ "ssss-ssssss--sssss-ssss-s-s-ss--sssss-sss---ssssss--s--s-ss-s--ss-ss--sssssssssssssssssss--s-ssssss--s-ss--s-s--ssss-ss---sssssssssss-s--sss-s-ssss-s--ss-s--sssssssssssssss-ss-ss-ss-ss-sss-sss-ssss---sss--ss-s--ss-ss--ssss-ss-ss-s-s-s---s-ss---s-s------sss"
/*6e00*/ "sssss-s-----sssssss-s-sss---s-s---s-----s-s-----ssssssss---s-s-ssss-ssssss-ss--sssssss-s-ss--ss-sssssss-sss-ss--ss-sss-sssssss---s-sssssssss-ss--sssss-s-sss--s-ss-ss-ssss-ssss-ss-sss--ss-ss-s-ss-s--sss-s--ssss-s---sssssss--sssssssssssss-ss-ss-s-ss--sssss--"
/*6f00*/ "s--sss-ss-sssss-s-s---ssssssssss-s--sssssss--ssss--sssss-sssss--s-sss-ssssssssssssss-sss-ss--ss-ssss-s-ssssss----sss-sss-s-s-sss---s-s-s-sssss-ss-sssss-sssssssss-s--sssss-ssssss-s-s-sss-ssssss----ss-sssssssssssss--ss-ss-sss---ss-ssssss--s--s-s-s--sss-sss-s"
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*7000*/ "s-sss-s-s-s-sss-s-sss-ss-s--s---ssssss---sss-sss-s-sssssssssss-sssssssssssss-ssss-ssssss-ssssssssss-sssssss-sss--sssssss-sss--sssssss-sss--sss-sss-ssssss-sssssssssssssssss-----sss-ssss----ssssssssssss-ss-sss-sssssssss-sss-s-sssssssssssssssss-sssssss-sss-ss"
/*7100*/ "ssss-ssss-sssss-ssss-ssss--s-ssss-ssss-sssssssssssssss-sssss-sssssssss--s-ss-s-ssssss--ss-ss-sssss-s----s-ss-s-ssssssssssssss-ssssss-sss-s-ssss-ssss--sss-sssss-ssssssss-sss-ssss-sssssss-ssss-ss-s-ssss--ssss-s-s-s--s-sssssss--ssss---ssss---ssssss-sss-s--s--"
/*7200*/ "ssssss-ssssss-ss-ssssssssss-ssssssssssss-s-s--ss-s-ss--sss-------sssss---ss--sssss-sssss--s-s-s-s--ssss-s-ssssssss-s-ssss-sss--s---ssss-ssssssssss-sss-sssssssss-s-ssss-ssss-ss-s--sss-ss-ssss-sss---s-sssssss-s-s-ssss-s-s-ssss---ssssss-ss--sssssssss---ss--ss"
/*7300*/ "ssssssssss-sssssssssss--sss---s-ssss--sss---ss--ssss-s--ssssss--ssss--ssssssss--sssssss-sssssssssss-ssss-s-sssss-s-ss-s--s--ssssssss-ss-s-s-ssssssssss-ssssssssssssssssss-ssssssss--sssssss-s-ss-s-sssss---ss--sss-sss-sssssss-s-ss-s-ssss-ss--ss-sss-ss-sssss-s"
/*7400*/ "sss-s---s-ssssssssssssssssssssssss-ss--ss--sss-sss-----sss-ssss-s-sssssssssssssssssss-sss----s---s---ssss--ssss--ss-ss-sssssss-ssss-sssss-s-ssssssssssssssssss--ss-ssss-ssssssss-ssssssssssss-ssssssssssss-ssss-ssss-sssssss-sss-s--ss--s-ssss-s---sss---sssssss"
/*7500*/ "s-s---ssssss---ss-s-s-ss-s-s-s--sss-s--s-ss--ss-----sss--s---sssssss-s-ss-----s-s-ss-ssss-----ss-s-s----s---s-s--ss--s---ssssss-ss-sss--s---ss--s-ss-sssss-ss-sssss-s-sssss-sssss---s-ss--ss---sss--s-s-ss-ss-ssss-s--ss--s-ss-sss--sssss-ssssss-s---sssss-s-s--"
/*7600*/ "s-sssssss-s-s-sssssssssssssssss----s-ss-ssssssss-sss-ssssss-ssssss-sss---sss-sssss-sss-s-sss-ssss--ssss----s-sss-s-sss-s-s-----s-s---s---ss-ss-s-ss-ss-ss----s-sssssss-sssssss-s-sss-ss----ssss-ss--ss-s-s-ss-ssss-sss--sss--s--s-s---s-ss-sss-sss-s-sss-ss-ss-s"
/*7700*/ "s-ss-ss---s--ssssssssssssss-ss---sss---ss-sssssssssssss--s-s-sss-sssss--ssssssssssssssssss--sssss-s-s--s-ss-sssssssssssss-ssss--sssssssssss-ss-ss-ssssssssssss-s-ssss-ssssss--ss-ss-ss-ss-s---s-sssssss-sssss-sssssssss-ss---sssss--s-s-s-sss---sss-ssssssss-sss"
/*7800*/ "ss-sssssssss-sssss-s--ssssssssss--sss---ssssssssss-s-sssss-ssss-sssss-ssssssss-ssssssssssssss-ssssss-ssssss--ss-ss-s-sssss-s-ssss-ssss--ssss---ss-s-s-s-ss-ssssssss-sss-s--ssss-sssss-ssss-s-s-ss-sss--sss--ssss--ss-sssss-ssssssssssss--sss-ss-ssss-ssssssss-ss"
/*7900*/ "s-sssss-ssssss-ss--sssssss-sssssssssss-sss---sss-sssssssss-s-s-s--sssss---ssssss-ss-s---ss-ss----s-ss-ss-ssss-sssssssss-ss-ssss---ss--ssss-ss---ssss-ssssss-s-ssssssss--ss-sss-s-ss-sssss--ss-----sssssss-s-sssss--ss-ss-ssssss-s-s--s--s-ss-sss-ssssssssss-ssss"
/*7a00*/ "-sssssss-ss-s--sssss-ss----s-ss--sssssssssssss-ss--ssss-sss------s--ss-ss-sss----ssssss-sssssssss---sssss-s-ssss-sss-s-ss--ss-s-s-s--sss-sssssssss--s----ssssss-sssssssss--sss---sssss-sss-ssss-sss---s--s--s-s-s---s-sss--s--s-----s---ss--s-s--sssss-s---ssss-"
/*7b00*/ "ss-s-s-s-s--sss-s-ssssss--s-ss-s-ssss--s-sss-ssssss-s--ss-sssssssssss--s--s---s----s-s-ssssss-sssssss-s-ssss-s-s--ss--ssss-sssssssssss--sss-s-s-ss-s--s----s----s-ssssssss-ss-sss-ss-sss-sssssss--ss-s--s-s--ss-sssssssssssss-ss-sss---ss-sss-sssss-ss--ssssssss"
/*7c00*/ "-ssssss-sssss-sss----ss-sssssss-s-s-sss-ss--sssssssssss--ssss----ss-ssssssss--s--sss-s-s-ssssss--sss--ssssss-ssssss-s-ssssssss-ss---sssss-s-s-ss-s-ss-s--ss-sss-s--s--s--ss-s--ss---sssss-sss--s-s-ss-ssss-sss-sss-sss-s-sss-s---s-ssss-sssssss-ss-s-s-s-s--ss-s"
/*7d00*/ "-s-s---sss--s-ss-sss--s------sss---ssssssss--s---s--s-sss--ssss-ss-----s-ss--s---sssss-ssss--s-ss---ss-s-sssss-ss---s--ss-sss-sssssssssss-sssss-sss-sssss----ss--s--sssssss--------s--s--s--s---sssssss-ss--sss-s--ss--s-s-s---s--ss-sss--ss-ss-ss-s-ssssss-ssss"
/*7e00*/ "s-ss--sss---ssssss-ssssssss-ss--s---ss-ssss-ss-ss--ss-s-s---s--ss-s-ss-sss--s-ssss-s---ss--ss--sssssss--s--ss-ss-ssssssss-s---s-ss--ssss---s-s---s---s-ssss--sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"
/*7f00*/ "ssssssssssssssssssssssssssssssssssssssssssssssssssssss-s-s-ssssssssss-s-ssss---s--ss--ss-ssssss--ssssss-----ss-s-s-ss-s---ssssssss--s----s-s-s-sssss-sssss-ss--ss-s--sss--ssss--ss-sss-s--sss-sss-sss--sss-s-sssss-s--ssssssssss--ssss-ss-s-ssss-ss-sssss-s--sss"
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*8000*/ "--s----ssss--sss-s-ss-s---ss-ssss-ssssss-ssssssssss-ss-ssss-s-s-ssssss-sss-sssssss-sss-s-s-sss--s--sssss-ssssss--s---s--s-sss---ssss----s-s--ssssss-ss-s-s--s-sss--ss-sss--s--s-s--s-sssss-ssssssss--s-sssss-s-sssssss-ss---s--ss-ss--sssssssss-s-ss-sss-sss--ss"
/*8100*/ "ss-ss------sssssssssssssss--sssssss-sssss-sssss-s-s-sssss-ssss-sssssss-ssss-ss-s--s---sssssssss-sssss--ssss-ss-s--ss-sss---ssss--s--ssss-s-ssss-sss-s-ssss-s--ss-ss--sss--ssssss-ss-s-ss-s-ss----s-sss-s--sss-sss-s-ssss---ssss--ss-s-s--s-ss-sssss--sssss---s-s"
/*8200*/ "s--ss-s----s---s-s-sss---ss--s--sssssssss----s-ssss-s-----ssssss-ssssss-ssssssssssssssss---ss-s-ss-s-s-s-s--ss--s--sss---sssss-ssssssssssss-s-ssss-ssssss-sss-s-sssss--ssss---s-s-s-ssss--s-s-sssssss-sssssssssss----ss-s-s--s--s-s-s---sss-sssss-s--ssss---ssss"
/*8300*/ "s------ss-ssss-sssssss---sss-ssssss-ssss-ss-sss-s--s---s--ssssss-ssss-sss--ssss--s-sssss-sssssssss-ssssssssssssssss-s-s-sss--ss-sssss-s-s--sss-ssss-ss-sss-sss---s-sssss-s--sssss-sss-sssssss-sss-sss-s-ss-s-s-ssss-ss-s-sss-ss--ssssssss-s-sss----s-s--sss-s-ss"
/*8400*/ "sss--ss-sss----ssss-ssssssssssss-s-ssssss--s-ssss-sss-ss-sss--ssssssss-s--ssss-ssssssss-sss-sssss---ss-ss-s-----s-sss-s-s--sssssss-s-ssssss-ssss-sss-ssss-ss-ss-s-sssssssssss-ssss-s-sss--s--ss-s-ss-s-ss---s-ss--ssss-ss--s-sssssssssssssss-s-sssss-sssssss-ss-"
/*8500*/ "-sssss-ssssssssss-s---s--s-ssss-s-ssss-sssss--sssssss-sssssss-ss--s-ssss----ss-ssss-s-s----ssssssss-ssss----s-sssssssss-ssssss-s-sss-ss--s-sssss--ss-ss-s-s--sssssss-s-s-----s---ssssssss--ssssss-sssssss-sss-s--ssss-ssssss--ssssss--sss--ssssssssssss-s---ss-s"
/*8600*/ "ss-sss--ss--sssssss-ss--ss-sssssss-ssssssssss-s--ssssssssssssss-sssssssssssss--s-sss--ssss-s-s--sssssss-sss-sssss-sssssss-s-ssssssssssssss---ssssss-s-sssssssssssss--ssss---sss--sssss-sssssssssssss-s--s-s-s--sssss-ssss-s-ss--ssss-ssss-ss----ssssssss--s-ss-s"
/*8700*/ "-s--ss-s---ss-sss--sssss-s-s-ssssssss-sss-ssssssssss-ss-sss-sss-sssssssss-s--s-ssss-s-s-s-sssss--ss-ss-s-s-sss-sssss-s-s-ssssss-ss-ssssssssss-sssssssssssssssss-ss-ssssssss-sss-sss-ssssss--s-ss-sss-s--sss-ssss-s-sssssssssssss-ssssssssssssss-ss-sss--s-s-ss-s"
/*8800*/ "sssss-s-sssss---s-sss--ssssssssss---sss-sssssssss-ssss-ss-s-ssss-s-s-s-sssss--ssss--sss-s-s-s--ss---ssss-ss-ssss-s-ss-s-sssss---s--sssss-ss-s-ssss-sss--s-ssss-sss-s-ssssss-ss-s--ss--s-sssssss-s-----sssssssss-ssss--ss--ss--s-s-ssssss-sssssssss----ss--ss---s"
/*8900*/ "ss-s-ss-ss-s-sss-s--ssssssss---ssssss-ssss--ssssssssss-s-ss-sssss-s--sssssss--ssssssss-sssssss---sss-s-sss-ss-s-ss-s-ss-ssssss--s-s-ss---s--sss-sss-ss---s-ssssss-ssss--s--s-ss-ss--ssssss-ss-s--sssssssssssssssss-sssssss-s--sssss-ss--ssssssssssss-sss-sssssss"
/*8a00*/ "-s--ssss-s-s-s-s-s--ss---ss-s-s-sss-s-ssss-ss-sss-s--s--ss---ssss-ssss-s-sssssss---s--sssss-ss-s-s--ss-ss-s----s----sssss-ss-sssss-s--s-s-ss--sss-s-s-ss-s-sss-s--s------sss--ss-s-ssssss-ss-s--ss-s-ss-sss---s-ss-sss-sss---s-----s-s--sss-s--ss-s-ss---s-sss-s"
/*8b00*/ "---s-ss-ssss-s-s-sss-s--s---s-ss--ssss-s-ss--ssssss-sssss-ssss-ss-sssssss-ss-s--sss-ss-s-s---ss-ssssss-ssss--ss----s-ss-sssss-s--ss-ssssss-s-s-s-s--ss-ss--sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"
/*8c00*/ "sssssssssssssssssssssssssssssssssssssssssssssssssssssss-ss-ssss-s-ssss-s-s-s-s-s-ssss-ssss-ssssss--sssssss---sssssssssss---s-sssss-ss-sss--s---sssss-sss-ssss--s---ssss----------s---s---ss---s------ss--s-ss--ss-s-ssssss---s-s-s---s-sss-ss-ss-sss-sssss----ss"
/*8d00*/ "ssss--s--s--s-s--s---s-sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss-s--sss-s-ss--s--s--sssssssss-sss-ssss-ssssssssssssss-sssssssss-ssss-ssssssssss-ssssss-sss-sss-ssssssss--ss-ssssss-sss--s-s-s-s-ssss-s--sss-sss-s-ssssss-ss-"
/*8e00*/ "ssssssss---ssss--ssssssssssss---ssssssssss-sssss-sss--ssssssssssss-s-ss----s-sss-ssss-sss-sssss--ss--sssssssssssss-s-s-sssss-ssss-ss--s-ss--s-sss-s--ssss-sssssss-ssssssss---ss---ssssssssssss-ssssss--s-s----s-ss-ssssssss-sss-ss--sssssss-ssssssssssss-ss----s"
/*8f00*/ "sss-s-sss--s-sssss----sss-s---s-ssssss-ss--ssss-sss-ssss--s-ss--ss-s---ss-ss---ssssssss-ssss-ss-s----ssssssssssssssssssssssssssssssssssssssssssssssssssssss--s--sss-sss--ssss------ssss-ss---ss-ss-s--ssssssss-ss-ss-sssss-sssssss-ss--ss---s-s--sss-ss----ss-ss"
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*9000*/ "--s-s--ssss-s-----s-----s--ss-------sss-ssssss-ss--ss--s--ss-s-ss--ss-s-s---s----------s--ss-s-s--s-s-s---sss---ss-ss----s-s--s------ss-s--ssss-s-sssssssssssssssss-ss-s-s-ssss-s-sss-ss-ssssssss-ssssssss-sss-ssssssssssss-ss-ss--s-sss-ssss-sssssss-s-sssss-ss"
/*9100*/ "ss-sssssssssssssss-ss-sss-sssssssssssss-sssss-ss-s-ssssssssssssssssssssss------sss-s-s-s-sssssssss--s-sss--s-sssss--s-s--sssssssss-ssss-s-s-s-ss-s-ssss-ssss-sssss-s-sssss--sss-ssss--ss-s-sssss--ssss----s-------ssss---s------s-s-----sssss--ssssss--sssss-ss-"
/*9200*/ "ssssss-sss-ss--s--ss--ssssssss-ssssssssss-ss-sssssss-ss-s--s-ss--sss--ss--s-ss-s--sssss-s---ss-sss-s-s--sssssssss-sssss--sssss-s-ss-s-ss-ssssssss-s-s--s-s---ssssssssss-sssss-sssssssss-s-sssssssssssssssssssss--s--s-s-s-ssssss-sss-ss-s--ss-ssss--ssss-----ss-"
/*9300*/ "ss-sss-ssssssss--sssssss---ss--s----s--s-ss--s--ss-ss-ssss--ssssssss-sss-ss-s-ssssss-s--sss--sss-sssssssssss-s-s-ssss-ssssss-s-sssssssssssss-sssssss-s--ss-sssssssss-ss-ssss---s-ssssssss-sssssssss-ss-s-sssssss--ssss---ssss--ss-ss--ss-sssssssssssssss-sssssss"
/*9400*/ "sss-sss-ssssssss-ss--sss---ssssss-sssssssss-sssss-sss--ss-sssssss-ss--ss-ssssssss---ssssss--ss-s-s-sssssss-sssss-ssss-s-ssss----s-ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"
/*9500*/ "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss-ssssssss-s--sss-s---sss-s----s-s--ssssss-s----s--ssss-ssss-ssssss-s--s-ssss-sss-ss-s--ssssss---s-sss-ssss--ss-ssssssssssssssssssssssssss"
/*9600*/ "ssssssssssssssssssssssssssss-ssss-ssssss-s-sss--ss-ssssssss-sss--s-s-ssssss---s--ssssssssss-----ss-----sss-s-sss-s--s----s-ss-sssssss--s-s--s---ssss--s---s---ss-ss-sss--s-ssss----s-s----s--sss--ss----s-s----ss-sss--ss-s--sssss--ssss-s--ssss-s-sss--s-s-ssss"
/*9700*/ "-sss-s---s-ss---s-s-ss-ss-ss-s-sssss-ss-ss-sssss-s--ssss--s-s--sss---s-s--sss-s-s--ss--ss-ss-s-s---s-s-s--s-s-sss-ss-ssss--s-ssss-ss---ssss-s-s--sssssss-sss-sss-ss-ss-s-ss-s-sssss--ssssssssssssss-ss-s-ss-sssssss-ssssssss-ssssssssssssssss--sss--s--ssss-sss-"
/*9800*/ "s---s--s-sss-ss-----sss--s-ssssss-ss-sssssss--ssssss-ss--ss---ssssssss-ssss-----ssss--s--ss-ss-ssssss-s-sss-sss---s--sssssssssssssssssssssssssssssssssssssssssssssssssss-s-ssss-s-ssss-ssssssssssss--s-ssssssssssssssssssss--ss-ss-ssssss-s-s---ss-s-sssssss---s"
/*9900*/ "sss-s-sss--s-sss-s---sss-ssss--s--ss-ss--sss-s-ssssssssssssss--sss-ss-sss-s--sss---ss-s-ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss----ssss-ssssss-ss-sss---ssss--sssssss-ssss-ss---s-sssssss---ss-ss-ss-s-s-ss-ssssssssss--ss--sssss-ss-sss-"
/*9a00*/ "s-sss-ssssssss--ss--sssss-ssssssssssssss-ss-ssss-ssssss-ssssss-s-s--s-sssssss--ssssss-s-ss--sss-ss-s--sss---ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss-ssss-ss-sssssss-sss-sss-sss-ssssssssss-s-s--sss--ss-s--ss--ss-sss--s---s-ss-ss-sss-ssss"
/*9b00*/ "ssssss-sssssssssssssssss-s-ssss-ss--s-s----sss--s--ssssssss--ssss-----sssssss---s-ss-sss-s-ssssssssssssssssssss-ss-s--sssssssssssss-ssssssssss--s---ss--sssssss--sssssss-s--s--ss-ss-ssss-s-ssss-sssss-ss--ssss-s--s-s-ssss-sssss----sss-sssssss---ss-ssssssssss"
/*9c00*/ "-sss-s-s---s--ss-s----sssss-sssss-ss--sssssss----s-ssssss---ss-sssssss---sssssssss-ssss-ss-sssss-ssssss-ssssssssssssss-s-ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss-s-s-s--sss-ss--s-sssssssss"
/*9d00*/ "sss-ss----ssss-sss-ss-sssss-sss-sss-ss-s-s---ssssssssssssss-ss--s-ss-s-s-sssssss--sssssss-ss---s--ss-ssssss--ss--s-sssssss-ssssssssssss-s-sssss-ssssssssss-sssssssss-ssss-s-sss-ss-s-sss-s--sssss--s-s-ssssssss-sss-sssss-ssssssssssss-ssssss-s-ss-sssss---ss-ss"
/*9e00*/ "sssssssssssssssssssssssss---ss-ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss-ss--sss-s-s-ssssss-ss--ssss---s-s-sssss-s-sssss--ss--ss-ssssssssss-----s--ssss-sssssss-------s-sss--s----s-ssss-ss-ssssss-ssss-s--s-s---ss"
/*9f00*/ "sssssss--sssss-ssss-s-ssssssssss--ssssssssss-ssssssssssssss-ss-sssssssssss--ss--ss-s-ssssssssss-----ss--ss-s-sssss-sss--sssssssssssssssssssss-sssssss-ssssss--ss-sssssLLLLLLLLLLLLLLSSSSSSSS...................................................................."
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*a000*/ "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"
/*a100*/ "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"
/*a200*/ "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"
/*a300*/ "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"
/*a400*/ "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"
/*a500*/ "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"
/*a600*/ "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"
/*a700*/ "-----------------------AAAAAAAAAAA------------------------------------------------------------------------------------------------------AAAAA-------------------------------------------------------------------------------------------------------------------"
/*a800*/ "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"
/*a900*/ "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"
/*aa00*/ "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"
/*ab00*/ "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"
/*ac00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*ad00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*ae00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*af00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*b000*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*b100*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*b200*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*b300*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*b400*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*b500*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*b600*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*b700*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*b800*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*b900*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*ba00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*bb00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*bc00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*bd00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*be00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*bf00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*c000*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*c100*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*c200*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*c300*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*c400*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*c500*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*c600*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*c700*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*c800*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*c900*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*ca00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*cb00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*cc00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*cd00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*ce00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*cf00*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*d000*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*d100*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*d200*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*d300*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*d400*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*d500*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*d600*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*d700*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm............................................................................................"
/*d800*/ "................................................................................................................................................................................................................................................................"
/*d900*/ "................................................................................................................................................................................................................................................................"
/*da00*/ "................................................................................................................................................................................................................................................................"
/*db00*/ "................................................................................................................................................................................................................................................................"
/*dc00*/ "................................................................................................................................................................................................................................................................"
/*dd00*/ "................................................................................................................................................................................................................................................................"
/*de00*/ "................................................................................................................................................................................................................................................................"
/*df00*/ "................................................................................................................................................................................................................................................................"
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*e000*/ "................................................................................................................................................................................................................................................................"
/*e100*/ "................................................................................................................................................................................................................................................................"
/*e200*/ "................................................................................................................................................................................................................................................................"
/*e300*/ "................................................................................................................................................................................................................................................................"
/*e400*/ "................................................................................................................................................................................................................................................................"
/*e500*/ "................................................................................................................................................................................................................................................................"
/*e600*/ "................................................................................................................................................................................................................................................................"
/*e700*/ "........................................................................................ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"
/*e800*/ "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*e900*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*ea00*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*eb00*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*ec00*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*ed00*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*ee00*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*ef00*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
/*f000*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*f100*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*f200*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*f300*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*f400*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*f500*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*f600*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*f700*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*f800*/ "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL"
/*f900*/ "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm-mmsmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmsmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"
/*fa00*/ "mmmmmmmmmmmmss--------------------------------..-----------------------------------------------------------....................................................................................................................................................."
/*fb00*/ ".--................JJJJJ.....AAAAAAAAAAAAAAAAAAAAAAAAAA.AAAAA.A.AA.AA.AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA.................................AAAAAAAAAAAAAAAAAAAAAAA..................AAAA"
/*fc00*/ "..............................................................................................AAAAA............................................................................................................................................................."
/*fd00*/ "..............................................................AA..................................................................................................................................................................................A.........-..."
/*fe00*/ "................SSSSSSSSSS......AAAA............ss-ssssssssssssssssss--..ssssssssss.ssss.ssssssssssssss.ssss....................AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA..."
/*ff00*/ ".------------------------------------------------------------------------------------------------ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg................................................................------m.-------.............TT.."
/*       "0               1               2               3               4               5               6               7               8               9               a               b               c               d               e               f               "*/
/*       "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"*/
;
/*
0000-001f 制御
0020-007f 基本ラテン
0080-00ff ラテン１補助
0100-024f ラテン拡張A,B IPA拡張 スペース調整
0300-036f 結合分音記号		■check■ 0304,0320,0323はゼロ幅文字扱い（暫定）
0370-03ff ギリシャコプト	(arial)
0400-052f キリル        	■check■
0530-058f アルメニア   (tahoma.ttf)
0590-05ff ヘブライ    (arial.ttf)
0600-06ff アラビア     (arial.ttf) 0655はゼロ幅文字扱い
0700-077f シリア        estrangelo edessa estre.ttf または seguihis.ttf
0780-08ff ターナ    mvboli.ttf
0900-097f テバナガリ    mangal.ttf または nirmala.ttf
0980-09ff ベンガル      vrinda.ttf または　shonar.ttf または nirmala.ttf
0a00-0a7f グルムキー   raavi.ttf または nirmala.ttf
0a80-0aff クジャラート  shruti.ttf または nirmala.ttf
0b00-0b7f オリヤー      	■check■
0b80-0bff タミール     latha.ttf または nirmala.ttf
0c00-0c7f テルグ      gautami.ttf または nirmala.ttf
0c80-0cff カナラ      tunga.ttf または nirmala.ttf
0d00-0d7f マラヤラム    kartika.ttf または nirmala.ttf
0d80-0dff シンハラ      	■check■
0e00-0e7f タイ          tahoma.ttf
0e80-0eff ラオス       laoui.ttf または LeelawUI.ttf
0f00-0fff チベット      	■check■
1000-109f ミャンマーMSゴシック不可	■check■
10a0-10ff グルジア		sylfaen.ttf
1100-11ff ハングル字母  	もしかしてGulim丸文字？	■check■
1200-139f エチオピア    	■check■
13a0-13ff チョロキー    	■check■
1400-167f カナダ先住民音節	■check■
1680-169f オガム        	■check■
16a0-16ff ルーン        	■check■
1700-171f タガログ      	■check■
1720-173f ハヌノオ      	■check■
1740-175f ブヒッド      	■check■
1760-177f タグバヌア    	■check■
1780-17ff クメール      	■check■
1800-18ff モンゴル      	■check■
1900-194f リンブ        	■check■
1950-19ef タイレ        	■check■
19e0-1cff クメール記号  	■check■
1d00-1dff ふりがな拡張  	Arial
1e00-1eff ラテン拡張追加	Arial
1f00-1fff ギリシャ拡張  	Arial
2000-33ff 一般句読点
3400-4dbf CJK統合漢字拡～
4e00-629f	CJK統合漢字～	simsunかundef undefと誤魔化す
62a0-773f	CJK統合漢字～	simsunかundef undefと誤魔化す
7740-8b9f	CJK統合漢字～	simsunかundef undefと誤魔化す
8ba0-8bdf	CJK統合漢字～ 	's'
8be0-9fbf	CJK統合漢字～
9fc0-9fff ?
a000-a48f イ音節
a490-abff イ部首
ac00-d7af ハングル
d800-e74f 不使用？
e750-f8ff 私用領域
f900-faff CJK互換漢字
fb00-fb4f アフファベット表示形
fb50-fe1f アラビア表示形A
fe20-fe2f 結合半角記号
fe30-fe6f CJK互換形
fe70-feff アラビア表示形B
ff00-ff5f 半角形／全角形1
ff60-ff9f 半角カナ
ffa0-ffdf 半角形／全角形2
ffe0-ffff 特殊文字
*/
// CA Font Set Index
#define GOTHIC_FONT	0
#define	SIMSUN_FONT	1
#define GULIM_FONT	2
#define ARIAL_FONT	3
#define GEORGIA_FONT	4
//UI_GOTHIC is out of use
//#define UI_GOTHIC_FONT 5
#define ARIALUNI_FONT 5
#define DEVANAGARI	6
#define TAHOMA_FONT 7
#define MINGLIU_FONT 8
#define N_MINCHO_FONT 9
#define ESTRANGELO_EDESSA_FONT 10
#define GUJARATI_FONT 11
#define BENGAL_FONT 12
#define TAMIL_FONT 13
#define LAOO_FONT 14
#define GURMUKHI_FONT 15
#define KANNADA_FONT 16
#define THAANA_FONT	17
#define MALAYALAM_FONT 18
#define TELUGU_FONT 19
#define EXTRA_FONT 20
#define UNDEFINED_FONT	(EXTRA_FONT+1)
#define NULL_FONT	(EXTRA_FONT+2)
#define CA_FONT_MAX	(EXTRA_FONT+1)
#define CA_FONT_NAME_MASK 0xff
#define CA_TYPE_MASK 0xffff
#define CA_SPACE_ATTRIBUTE 0xffff0000
static char* const CA_FONT_NAME[] = {
	"GOTHIC",
	"SIMSUN",
	"GULIM",
	"ARIAL",
	"GEORGIA",
	//"UI GOTHIC",
	"ARIAL UNICODE",
	"DEVANAGARI",
	"TAHOMA",
	"MINGLIU",
	"NEW MINCHO",
	"ESTRANGELO EDESSA",
	"GUJARATI",
	"BENGAL",
	"TAMIL",
	"LAOO",
	"GURMUKHI",
	"KANNADA",
	"THAANA",
	"MALAYALAM",
	"TELUGU",
	"EXTRA",
	//--end of font type---//
	"UNDEFINED",	//EXTRA_FONT+1
	"NULL FONT",	//EXTRA_FONT+2
};
#define CA_FONT_NAME_SIZE (sizeof(CA_FONT_NAME)/sizeof(char* const))

#define HTML5_FONT_DEFONT	0
#define HTML5_FONT_MINCHO	1
#define HTML5_FONT_GOTHIC	2
static char* const HTML5_FONT_CMD[] = {
	"h5defont", "h5mincho", "h5gothic",
};
static int const HTML5_CA_FONT[] = {
	UNDEFINED_FONT,
	SIMSUN_FONT,
	GULIM_FONT,
};

static const int CA_FONT_SIZE_FIX[][CMD_FONT_MAX] = {
//	DEF,BIG,SMALL
	{0,-1,1,0},	//gothic
	{0,-1,1,0},	//simsun
	{0,-1,1,0},	//gulim
	{0,1,0,0},	//arial
	{-2,-2,-2,-2},	//georgia
	{0,0,0,0},	//arial unicode
	{0,0,0,0},	//devanagari
	{0,0,0,0},	//tahoma
	{0,0,0,0},	//MingLiU
	{0,0,0,0},	//new mincho, smsun or new_gulim
	{0,0,2,0},	//estrangelo edessa
	{0,0,2,0},	//gujarati
	{0,0,2,0},	//bengal
	{0,0,2,0},	//tamil
	{0,0,2,0},	//laoo
	{0,0,2,0},	//gurmukhi
	{0,0,0,0},	//extra
};

#define CA_TYPE_SPACE_00A0 0x00A00000
#define CA_CODE_SPACE_00A0 0x00A0
#define CA_TYPE_SPACE_0020 0x00200000
#define CA_CODE_SPACE_0020 0x0020
#define CA_TYPE_SPACE_2000 0x20000000
#define CA_CODE_SPACE_2000 0x2000
#define CA_TYPE_SPACE_200C 0x200C0000
#define CA_CODE_SPACE_200C 0x200C
#define CA_TYPE_SPACE_3000 0x30000000
#define CA_CODE_SPACE_3000 0x3000
#define CA_TYPE_SPACE_0009 0x00090000
#define CA_CODE_SPACE_0009 0x0009
#define CA_TYPE_NOGLYPH_SIMSUN	0xe8000000
#define CA_CODE_NOGLYPH_SIMSUN	0xe800
#define CA_TYPE_NOGLYPH_MINGLIU	0xef000000
#define CA_CODE_NOGLYPH_MINGLIU	0xef00
#define isSpaceFont(ft) ((ft)&CA_SPACE_ATTRIBUTE)
#define GET_CODE(ft)	(((ft)>>16)&0xffff)
#define GET_TYPE(ft)	((ft)&CA_TYPE_MASK)

#define SPACE_0020 0
#define SPACE_00A0 1
#define SPACE_2000 2
#define SPACE_3000 3
#define NO_GLYPH_SIMSUN 5
#define NO_GLYPH_MINGLIU 6
static char* const CA_SPACE_NAME[] = {
		"SPACE",
		"no-break-SPACE",
		"U+2000 series",
		"CJK SPACE",
		"TAB",
		"No Glyph Simsun",
		"No Glyph MingLiu",
};


#endif /* UNITABLE_H_ */

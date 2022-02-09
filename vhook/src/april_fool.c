/*
 * april_fool.c
 *
 *  Created on: 2012/03/26
 *      Author: orz
 */

#include "april_fool.h"

void set_aprilfool(SETTING* setting,DATA* data){
	const char* year = setting->april_fool;
	if(strncmp(year,"2008",(size_t)4)==0){
		//逆走
		data->comment_speed = -20080401;
	}else if(strncmp(year,"2009",(size_t)4)==0){
		//赤字、３倍速
		data->comment_speed = 20090401;
		data->defcolor = CMD_COLOR_RED + 401;
	}else if(strncmp(year,"2010",(size_t)4)==0){
		//黒字
		data->defcolor = CMD_COLOR_BLACK + 401;
	}
}

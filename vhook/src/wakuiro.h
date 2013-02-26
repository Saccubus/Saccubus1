/*
 * wakuiro.h
 *
 *  Created on: 2012/08/24
 *      Author: orz
 */

#ifndef WAKUIRO_H_
#define WAKUIRO_H_

#include "main.h"
#include "struct_define.h"

#define WAKUIRO_COLORCODE	8
#define WAKUIRO_COLORNAME	4
#define WAKUIRO_USER		0
#define WAKUIRO_OWNER		1
#define WAKUIRO_OPTIONAL	2
#define GET_WAKUIRO_KEY(x)	((x)>>28)
#define GET_WAKUIRO_VAL(x)	((x)&0x00ffffff)
#define SET_WAKUIRO(k,v)	(((k)<<28)|((v)&0x00ffffff))

void set_wakuiro(const char* str,DATA* data);

#endif /* WAKUIRO_H_ */

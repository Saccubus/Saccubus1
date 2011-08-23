#include "mydef.h"
#include "util.h"

#include <stdlib.h>
/*����*/
int rnd(){
	static int Seed = 23573;
	int result;
	Seed *= 214013;
	Seed += 2531011; // ->���ɌĂяo���ꂽ�Ƃ���seed�Ɏg��
	result = Seed;
	result = result >> 0x10;
	return result;
}

int myfflush(FILE *file){
#ifndef DEBUG
	return 0;	// not flush when RELEASE
#else
	retrun fflush(file);
#endif
}

#include "mydef.h"
#include "util.h"

#include <stdlib.h>
/*乱数*/
int rnd(){
	static int Seed = 23573;
	int result;
	Seed *= 214013;
	Seed += 2531011; // ->次に呼び出されたときのseedに使う
	result = Seed;
	result = result >> 0x10;
	return result;
}


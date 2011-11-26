#include "mydef.h"
#include "util.h"

#include <stdlib.h>
/*—”*/
int rnd(){
	static int Seed = 23573;
	int result;
	Seed *= 214013;
	Seed += 2531011; // ->ŽŸ‚ÉŒÄ‚Ño‚³‚ê‚½‚Æ‚«‚Ìseed‚ÉŽg‚¤
	result = Seed;
	result = result >> 0x10;
	return result;
}


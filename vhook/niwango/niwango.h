/* 
 * File:   niwango.h
 * Author: psi
 *
 * Created on 2010/06/06, 12:07
 */

#ifndef _NIWANGO_H
#define	_NIWANGO_H

#ifdef	__cplusplus
extern "C" {
#endif
#include <glib.h>
//ファイル内で使う構造体のタイプをここで宣言
    typedef struct Node Node;
    typedef struct Env Env;
    typedef struct Obj Obj;
//
	typedef struct NiwangoScript NiwangoScript;
	typedef struct Niwango Niwango;
//外側から見えるインターフェース
	Niwango* Niwango_new();
	void Niwango_free(Niwango* self);
	void Niwango_addScript(Niwango* self, glong timestamp, const gchar* script_txt);
	void Niwango_seek(Niwango* self, glong timestamp);
	void Niwango_play(Niwango* self, glong timestamp);

#ifdef	__cplusplus
}
#endif

#endif	/* _NIWANGO_H */


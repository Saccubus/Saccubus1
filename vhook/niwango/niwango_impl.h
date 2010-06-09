/* 
 * File:   niwango_impl.h
 * Author: psi
 *
 * Created on 2010/06/08, 17:47
 */

#ifndef _NIWANGO_IMPL_H
#define	_NIWANGO_IMPL_H

#ifdef	__cplusplus
extern "C" {
#endif
#include <glib.h>
#include "niwango.h"
	struct NiwangoScript {
		glong order;	//何番目のスクリプトか？
		glong timestamp; //スクリプトが実行されるべき時間
		Node* script_node; //その構文木
	};
	struct Niwango {
		glong now_timestamp; //現在のスクリプト内時間
		GTree* script_node_tree; // Tree of NiwangoScript
	};
	
#ifdef	__cplusplus
}
#endif

#endif	/* _NIWANGO_IMPL_H */


/* 
 * File:   top_lebel_node..h
 * Author: psi
 *
 * Created on 2010/06/06, 19:43
 */

#ifndef _TOP_LEBEL_NODE__H
#define	_TOP_LEBEL_NODE__H

#ifdef	__cplusplus
extern "C" {
#endif
    #include "node.h"
    #include <glib.h>
    Node* TopLevelNode_new();
    void TopLevelNode_add(Node* self,Node* stmt);


#ifdef	__cplusplus
}
#endif

#endif	/* _TOP_LEBEL_NODE__H */


/* 
 * File:   calling_node.h
 * Author: psi
 *
 * Created on 2010/06/06, 19:44
 */

#ifndef _CALLING_NODE_H
#define	_CALLING_NODE_H

#ifdef	__cplusplus
extern "C" {
#endif
    #include "node.h"
    #include <glib.h>
    Node* CallingNode_new(Node* obj,Node* args);


#ifdef	__cplusplus
}
#endif

#endif	/* _CALLING_NODE_H */


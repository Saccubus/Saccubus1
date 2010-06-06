/* 
 * File:   assign_node.h
 * Author: psi
 *
 * Created on 2010/06/06, 19:36
 */

#ifndef _ASSIGN_NODE_H
#define	_ASSIGN_NODE_H

#ifdef	__cplusplus
extern "C" {
#endif
    #include "node.h"
    #include <glib.h>
    Node* AssignNode_new(Node* term,Node* expr);


#ifdef	__cplusplus
}
#endif

#endif	/* _ASSIGN_NODE_H */


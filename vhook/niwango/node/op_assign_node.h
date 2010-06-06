/* 
 * File:   op_assing_node.h
 * Author: psi
 *
 * Created on 2010/06/06, 19:38
 */

#ifndef _OP_ASSING_NODE_H
#define	_OP_ASSING_NODE_H

#ifdef	__cplusplus
extern "C" {
#endif
    #include "node.h"

    Node* OpAssignNode_new(Node* term,Node* expr,const gchar* op);

#ifdef	__cplusplus
}
#endif

#endif	/* _OP_ASSING_NODE_H */


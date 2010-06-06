/* 
 * File:   binary_op_node.h
 * Author: psi
 *
 * Created on 2010/06/06, 18:38
 */

#ifndef _BINARY_OP_NODE_H
#define	_BINARY_OP_NODE_H

#ifdef	__cplusplus
extern "C" {
#endif
    #include <glib.h>
    #include "node.h"

    Node* BinaryOpNode_new(const gchar* op_str,Node* left,Node* right);


#ifdef	__cplusplus
}
#endif

#endif	/* _BINARY_OP_NODE_H */


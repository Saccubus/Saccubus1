/* 
 * File:   args_node.h
 * Author: psi
 *
 * Created on 2010/06/06, 19:39
 */

#ifndef _ARGS_NODE_H
#define	_ARGS_NODE_H

#ifdef	__cplusplus
extern "C" {
#endif
#include "node.h"
    Node* ArgsNode_new();
    void ArgsNode_add(Node* self,Node* element);
    Node* ArgNode_new(const gchar* label,Node* expr);


#ifdef	__cplusplus
}
#endif

#endif	/* _ARGS_NODE_H */


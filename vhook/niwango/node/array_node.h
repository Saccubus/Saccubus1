/* 
 * File:   array_node.h
 * Author: psi
 *
 * Created on 2010/06/06, 17:43
 */

#ifndef _ARRAY_NODE_H
#define	_ARRAY_NODE_H

#ifdef	__cplusplus
extern "C" {
#endif
#include "node.h"

    Node* ArrayAccessNode_new(Node* array,Node* expr);
    Node* ArrayNode_new();
    void ArrayNode_add(Node* self,Node* element);

#ifdef	__cplusplus
}
#endif

#endif	/* _ARRAY_NODE_H */


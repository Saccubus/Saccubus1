/* 
 * File:   literal_node.h
 * Author: psi
 *
 * Created on 2010/06/06, 11:51
 */

#ifndef _LITERAL_NODE_H
#define	_LITERAL_NODE_H

#ifdef	__cplusplus
extern "C" {
#endif

#include "node.h"
Node* StringNode_new_Single(const gchar* literal);
Node* StringNode_new_Double(const gchar* literal);
Node* IntegerNode_new(const gchar* literal);
Node* FloatNode_new(const gchar* literal);


#ifdef	__cplusplus
}
#endif

#endif	/* _LITERAL_NODE_H */


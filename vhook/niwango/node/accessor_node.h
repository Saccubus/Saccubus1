/* 
 * File:   accessor_node.h
 * Author: psi
 *
 * Created on 2010/06/06, 18:10
 */

#ifndef _ACCESSOR_NODE_H
#define	_ACCESSOR_NODE_H

#ifdef	__cplusplus
extern "C" {
#endif
    #include "node.h"

    Node* AccessorNode_new(Node* parent,const gchar* name);


#ifdef	__cplusplus
}
#endif

#endif	/* _ACCESSOR_NODE_H */


/* 
 * File:   prefix_op_node.h
 * Author: psi
 *
 * Created on 2010/06/06, 18:51
 */

#ifndef _PREFIX_OP_NODE_H
#define	_PREFIX_OP_NODE_H

#ifdef	__cplusplus
extern "C" {
#endif

    #include "node.h"
    #include <glib.h>
    Node* PrefixOpNode_new(const gchar* op_str,Node* node);


#ifdef	__cplusplus
}
#endif

#endif	/* _PREFIX_OP_NODE_H */


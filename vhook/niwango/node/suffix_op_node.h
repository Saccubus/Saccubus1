/* 
 * File:   suffix_op_node.h
 * Author: psi
 *
 * Created on 2010/06/06, 18:57
 */

#ifndef _SUFFIX_OP_NODE_H
#define	_SUFFIX_OP_NODE_H

#ifdef	__cplusplus
extern "C" {
#endif

    #include "node.h"
    #include <glib.h>
    Node* SuffixOpNode_new(const gchar* op_str,Node* node);


#ifdef	__cplusplus
}
#endif

#endif	/* _SUFFIX_OP_NODE_H */


/* 
 * File:   node.h
 * Author: psi
 *
 * Created on 2010/06/06, 12:06
 */

#ifndef _NODE_H
#define	_NODE_H

#ifdef	__cplusplus
extern "C" {
#endif
#include "../niwango.h"

    struct Node{
        union{
            struct{
                Node* left;
                Node* center;
                Node* right;
            } nodes;
            gpointer* other;
            gint int_value;
            gdouble float_value;
            gchar* str_value;
        } data;
        Obj* (*exec)(Node* self);
        void (*free)(Node* self);
    };
    Node* Node_alloc();
    void Node_free(Node* self);

    #include "accessor_node.h"
    #include "args_node.h"
    #include "array_node.h"
    #include "assign_node.h"
    #include "binary_op_node.h"
    #include "calling_node.h"
    #include "label_node.h"
    #include "literal_node.h"
    #include "op_assign_node.h"
    #include "prefix_op_node.h"
    #include "suffix_op_node.h"
    #include "top_level_node.h"
    
#ifdef	__cplusplus
}
#endif

#endif	/* _NODE_H */


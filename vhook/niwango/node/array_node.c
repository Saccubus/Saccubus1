#include "glib.h"
#include "node.h"

Node* ArrayAccessNode_new(Node* array,Node* expr){

}

///////////////////////////////////////////////////////////////////////////////

void _ArrayNode_free(Node* self){
    g_ptr_array_free((GPtrArray*)self->data.other,TRUE);
}

Obj* _ArrayNode_exec(Node* self){
}

Node* ArrayNode_new(){
    Node* node = Node_alloc();
    node->data.other = (gpointer)g_ptr_array_new_with_free_func((GDestroyNotify)Node_free);
    node->free = _ArrayNode_free;
    node->exec = _ArrayNode_exec;
}
void ArrayNode_add(Node* self,Node* element){
    g_ptr_array_add((GPtrArray*)self->data.other,element);
}

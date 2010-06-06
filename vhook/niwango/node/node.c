#include "node.h"
#include <glib.h>

Node* Node_alloc(){
    return g_new(Node,1);
}
void Node_free(Node* self){
    self->free(self);
    g_free(self);
}

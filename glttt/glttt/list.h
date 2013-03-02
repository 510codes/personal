#ifndef LIST_H_
#define LIST_H_

#include <stdlib.h>

typedef struct list_node
{
	struct list_node *prev;
	struct list_node *next;
	
	void *obj;
} list_node_t;

typedef struct
{
	list_node_t *first;
	list_node_t *last;
	int size;
} list_t;




list_t *list_new();
void list_delete( list_t *list );

void list_insert( list_t *list, list_node_t *node, void *obj );
void list_append( list_t *list, list_node_t *node, void *obj );
void list_remove( list_t *list, list_node_t *node );
void list_clear( list_t *list );

#endif

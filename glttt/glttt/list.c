#ifndef NULL
	#define NULL 0
#endif

#include "list.h"

list_t *list_new()
{
	list_t *list;

	list=(list_t *)malloc(sizeof(list_t));
	list->first=NULL;
	list->last=NULL;
	list->size=0;

	return list;
}

list_node_t *list_node_new()
{
	list_node_t *node;

	node=(list_node_t *)malloc(sizeof(list_node_t));

	return node;
}

void list_node_delete( list_node_t *node )
{
	free(node);
}

// Free the list structure, and all the list nodes.
// Objects stored in the nodes are NOT freed.
// 
void list_delete( list_t *list )
{
	list_clear(list);
	free(list);
}

// Append after 'node'.
// If 'node' is NULL, just append to tail.
// 
void list_append( list_t *list, list_node_t *node, void *obj )
{
	list_node_t *newnode;

	newnode=list_node_new();

	newnode->obj=obj;

	if (node==NULL)
	{
		newnode->prev=list->last;
		newnode->next=NULL;
		
		if (newnode->prev != NULL)
			newnode->prev->next=newnode;
		else
			list->first=newnode;
		
		list->last=newnode;
	}
	else
	{
		newnode->next=node->next;
		newnode->prev=node;
		
		if (node->next != NULL)
			node->next->prev=newnode;
		else
			list->last=newnode;
		
		node->next=newnode;	
	}

	list->size++;
}

// Insert before 'node'.
// If 'node' is NULL, insert at the list head.
// 
void list_insert( list_t *list, list_node_t *node, void *obj )
{
	list_node_t *newnode;

	newnode=list_node_new();

	newnode->obj=obj;

	if (node==NULL)
	{
		newnode->prev=NULL;
		newnode->next=list->first;
		
		if (list->first != NULL)
			list->first->prev=newnode;
		else
			list->last=newnode;
		
		list->first=newnode;
	}
	else
	{
		newnode->prev=node->prev;
		newnode->next=node;

		if (node->prev != NULL)
			node->prev->next=newnode;
		else
			list->first=newnode;

		node->prev=newnode;
	}

	list->size++;
}

// De-link the node, and free it's memory.
// 
void list_remove( list_t *list, list_node_t *node )
{
	if (node->prev==NULL)
		list->first=node->next;
	else
		node->prev->next=node->next;

	if (node->next==NULL)
		list->last=node->prev;
	else
		node->next->prev=node->prev;

	list_node_delete( node );
	list->size--;
}

void list_clear( list_t *list )
{
	list_node_t *i, *i2;

	i=list->first;

	while (i != NULL)
	{
		i2=i->next;
		list_node_delete(i);
		i=i2;
	}
}

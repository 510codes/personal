#ifndef GL_MSG_H_
#define GL_MSG_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <GL/gl.h>
#include <GL/glut.h>


typedef struct MSG_NODE
{
	struct MSG_NODE *next;
	char *msg;
	int time;
	GLdouble col[3];
} msg_node_t;


typedef struct
{
	msg_node_t *first;
	msg_node_t *last;
	GLdouble col[4];
	int size;
	int max_size;
	GLdouble width_pct;
	int timeout;
} glmsgbox_t;


glmsgbox_t *glmsgbox_new( GLdouble *col, int max_size, GLdouble width_pct, int timeout );

void glmsg_add( glmsgbox_t *msg_box, char *msg, GLdouble *col );

void glmsgbox_draw( glmsgbox_t *msg_box );

#endif

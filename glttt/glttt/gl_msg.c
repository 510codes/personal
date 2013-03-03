#include "gl_msg.h"
#include "platform/platform.h"

const int BASE_WIDTH=500;
const int BASE_HEIGHT=500;

glmsgbox_t *glmsgbox_new( GLdouble *col, int max_size, GLdouble width_pct, int timeout )
{
	glmsgbox_t *msg_box;

	if (max_size < 1)
		return NULL;
	
	msg_box=(glmsgbox_t *)malloc(sizeof(glmsgbox_t));
	msg_box->size=0;
	msg_box->first=NULL;
	msg_box->last=NULL;
	msg_box->max_size=max_size;
	msg_box->width_pct=width_pct;
	msg_box->timeout=timeout;
	memcpy( msg_box->col, col, sizeof(GLdouble) * 4 );

	return msg_box;
}

void msg_node_delete( msg_node_t *node )
{
	free(node->msg);
	free(node);
}

void glmsg_add( glmsgbox_t *msg_box, char *msg, GLdouble *col )
{
	msg_node_t *node;
	int t;

	t=glttt_platform_time_in_millis_since_init();
	
	node=(msg_node_t *)malloc(sizeof(msg_node_t));
	node->msg=(char *)malloc(strlen(msg) + 1);
	node->next=NULL;
	node->time=t;
	strcpy(node->msg,msg);
	memcpy(node->col, col, sizeof(GLdouble) * 3);

	//printf("\nglmsg_add(): first: %d, last: %d\n",msg_box->first,msg_box->last);
	
	if (msg_box->last==NULL)
	{
		msg_box->last=node;
		msg_box->first=node;
		msg_box->size++;
	}
	else
	{
		msg_box->last->next=node;
		msg_box->last=node;
		msg_box->size++;

		if (msg_box->size > msg_box->max_size)
		{
			node=msg_box->first->next;
			msg_node_delete(msg_box->first);
			msg_box->size--;
			msg_box->first=node;
			if (msg_box->first != NULL && msg_box->first->next == NULL)
				msg_box->last=node;
		}
	}
}

void print_stroke_msg( int xp, int yp, GLdouble scale, const char *msg )
{
	int xsize,ysize;
	int x;
	
	glMatrixMode(GL_MODELVIEW);
	glPushMatrix();
	glLoadIdentity();

	glMatrixMode(GL_PROJECTION);
	glPushMatrix();
	glLoadIdentity();

	xsize=glttt_platform_get_window_width();
	ysize=glttt_platform_get_window_height();

	glOrtho( 0.0, xsize, ysize, 0.0, -1.0, 1.0 );
	glTranslated( xp, yp, 1 );
	glScaled( scale, -scale, scale );

	for (x=0; x<strlen(msg); x++)
		glttt_platform_draw_char( msg[x] );
	
	glPopMatrix();

	glMatrixMode(GL_MODELVIEW);
	glPopMatrix();
}


/*void dump_list( msg_node_t *node )
{
	printf("=== dump list ===\n");
	while (node != NULL)
	{
		if (node != NULL && node->next != NULL && node->next->next == node)
		{
			printf("\n== STOP: node->next->next == node!\n\n");
			exit(1);
		}
		printf("node: %d, next: %d\n",node,node->next);
		node=node->next;
	}
	printf("==================\n");
}*/

void glmsgbox_draw( glmsgbox_t *msg_box )
{
	GLint xp1,xp2;
	GLint ypos;
	msg_node_t *node;
	GLdouble col[4];
	int t;
	GLdouble scale;
	int width;
	int xsize, ysize;

	t=glttt_platform_time_in_millis_since_init();
	
	glMatrixMode(GL_MODELVIEW);
	glPushMatrix();
	glLoadIdentity();
	
	glMatrixMode(GL_PROJECTION);
	glPushMatrix();
	glLoadIdentity();

	glDisable(GL_DEPTH_TEST);
	
	glEnable(GL_ALPHA_TEST);
	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);			

	xsize=glttt_platform_get_window_width();
	ysize=glttt_platform_get_window_height();
	glOrtho( 0.0, xsize, ysize, 0.0, -1.0, 1.0 );
	glTranslated( 0, 0, 0 );

	while (msg_box->first != NULL && t > msg_box->first->time + msg_box->timeout)
	{
		//printf("\n1. list size: %d, first: %d, last: %d\n",msg_box->size,msg_box->first,msg_box->last);
		//dump_list(msg_box->first);
		//printf("TIMEOUT: deleting node: %d\n",msg_box->first);
		node=msg_box->first;
		msg_box->first=msg_box->first->next;
		if (msg_box->first == NULL)
			msg_box->last=NULL;
		else if (msg_box->first->next == NULL)
			msg_box->last=msg_box->first;
	
		//printf("intermediate\n");
		//dump_list(msg_box->first);
		
		msg_node_delete(node);
		msg_box->size--;
		//printf("\n2. list size: %d, first: %d, last: %d\n",msg_box->size,msg_box->first,msg_box->last);
		//dump_list(msg_box->first);
		//printf("\n\n");
	}
	
	width=xsize * msg_box->width_pct;
	xp1=(xsize / 2) - (width / 2);
	xp2=xp1+width;

	scale=(GLdouble)xsize / (GLdouble)BASE_WIDTH;
	
	glColor4dv( msg_box->col );
	glBegin(GL_POLYGON);
		glVertex2i( xp1, 0 );
		glVertex2i( xp1, 10 + (msg_box->size * 21) );
		glVertex2i( xp2, 10 + (msg_box->size * 21) );
		glVertex2i( xp2, 0 );
	glEnd();

	ypos=0;
	node=msg_box->first;
	while (node != NULL)
	{
		memcpy( col, node->col, sizeof(GLdouble) * 3 );
		col[3]=0.4;
		glColor4dv( col );
		print_stroke_msg( xp1+20, 20+(ypos*20), 0.12 * scale, node->msg );
		node=node->next;
		ypos++;
	}

	glPopMatrix();

	glMatrixMode(GL_MODELVIEW);
	glPopMatrix();

	glDisable(GL_ALPHA_TEST);
	glDisable(GL_BLEND);	
	
	glEnable(GL_DEPTH_TEST);
}


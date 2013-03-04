#include <stdio.h>
#include <string.h>
#include <math.h>

#include "GL/gl.h"
#include "GL/glu.h"

#include "glttt.h"
#include "gl_msg.h"

#include "cpu.h"
#include "board.h"
#include "defs.h"
#include "globals.h"
#include "game_settings.h"
#include "platform/platform.h"



// random prototypes
//
void do_turn( peg_label_t peg );
void add_vert_rows_to_queue( int oldmask, int newmask, peg_colour_t col );
void add_horiz_rows_to_queue( int oldmask, int newmask, peg_colour_t col );

typedef struct
{
	peg_label_t p1;
	peg_label_t p2;
	peg_label_t p3;
	
	int h1;
	int h2;
	int h3;

	int time;
} row_t;



global_t globals;


void global_init()
{
	globals.cpu=NULL;
	globals.game_state=GAMESTATE_NEW_GAME;
	globals.colour=PC_NONE;
	globals.human_turn=FALSE;
	globals.msg_box=NULL;
	globals.cpu_old=0;
	globals.hum_old=0;
	globals.move_count=0;

	globals.mask_vert_cpu=0;
	globals.mask_horiz_cpu=0;	
	globals.mask_vert_hum=0;
	globals.mask_horiz_hum=0;
	
	globals.new_rot=0.0;
	globals.rot=0.0;
	globals.zoom=250;
	globals.new_zoom=0;
	globals.repos_active=FALSE;

	globals.mx_left_down=0;
	globals.my_left_down=0;
	globals.mx_hover=0;
	globals.my_hover=0;
	globals.peg_select_hover=PEG_NONE;
	globals.peg_select_down=PEG_NONE;

	globals.human_move_time=0;

	// make sure this variable is null before calling
	// this function...
	// 
	globals.row_queue=list_new();
}

void draw_char( GLTTT_FP_TYPE x, GLTTT_FP_TYPE z, char c )
{
	glPushMatrix();
	glTranslatef( x, 1, z );
	glScalef( 0.1, 0.1, 0.1 );
	glRotatef( -90.0, 1.0, 0.0, 0.0 );
	glttt_platform_draw_char( c );
	glPopMatrix();
}

void draw_peg( int x, int y, peg_colour_t col, int flashing )
{	
	if (col != PC_NONE)
	{
		if (col==PC_RED)
		{
			if (flashing)
			{
				glMaterialfv(GL_FRONT, GL_AMBIENT, redpeg_flash_ambient);
				glMaterialfv(GL_FRONT, GL_DIFFUSE, redpeg_flash_diffuse);
				glMaterialfv(GL_FRONT, GL_SPECULAR, redpeg_flash_specular);
				glMaterialfv(GL_FRONT, GL_SHININESS, redpeg_flash_shininess);
			}
			else
			{
				glMaterialfv(GL_FRONT, GL_AMBIENT, redpeg_ambient);
				glMaterialfv(GL_FRONT, GL_DIFFUSE, redpeg_diffuse);
				glMaterialfv(GL_FRONT, GL_SPECULAR, redpeg_specular);
				glMaterialfv(GL_FRONT, GL_SHININESS, redpeg_shininess);
			}
		}
		else
		{
			if (flashing)
			{
				glMaterialfv(GL_FRONT, GL_AMBIENT, whitepeg_flash_ambient);
				glMaterialfv(GL_FRONT, GL_DIFFUSE, whitepeg_flash_diffuse);
				glMaterialfv(GL_FRONT, GL_SPECULAR, whitepeg_flash_specular);
				glMaterialfv(GL_FRONT, GL_SHININESS, whitepeg_flash_shininess);
			}
			else
			{
				glMaterialfv(GL_FRONT, GL_AMBIENT, whitepeg_ambient);
				glMaterialfv(GL_FRONT, GL_DIFFUSE, whitepeg_diffuse);
				glMaterialfv(GL_FRONT, GL_SPECULAR, whitepeg_specular);
				glMaterialfv(GL_FRONT, GL_SHININESS, whitepeg_shininess);
			}
		}

		glPushMatrix();
		glTranslatef( PEG_POS[x][0], (y * 25) + 10, PEG_POS[x][1] );

		/*glBegin( GL_QUADS );
			for (x=1; x>=-1; x-=2 )
			{
				glVertex3f( -PEG_SIZE, PEG_SIZE, PEG_SIZE*x );
				glVertex3f( -PEG_SIZE, -PEG_SIZE, PEG_SIZE*x );
				glVertex3f( PEG_SIZE, -PEG_SIZE, PEG_SIZE*x );
				glVertex3f( PEG_SIZE, PEG_SIZE, PEG_SIZE*x );

				glVertex3f( -PEG_SIZE, PEG_SIZE*x, PEG_SIZE );
				glVertex3f( -PEG_SIZE, PEG_SIZE*x, -PEG_SIZE );
				glVertex3f( PEG_SIZE, PEG_SIZE*x, -PEG_SIZE );
				glVertex3f( PEG_SIZE, PEG_SIZE*x, PEG_SIZE );
				
				glVertex3f( PEG_SIZE*x, PEG_SIZE, -PEG_SIZE );
				glVertex3f( PEG_SIZE*x, -PEG_SIZE, -PEG_SIZE );
				glVertex3f( PEG_SIZE*x, -PEG_SIZE, PEG_SIZE );
				glVertex3f( PEG_SIZE*x, PEG_SIZE, PEG_SIZE );
			}
		glEnd();*/

		glttt_platform_draw_solid_sphere( 10 );
		
		glPopMatrix();
	}	
}

void draw_message( GLTTT_FP_TYPE sf, GLint xp, GLint yp, char *msg )
{
	int x;

	glMatrixMode(GL_MODELVIEW);
	glPushMatrix();
	glLoadIdentity();

	glMatrixMode(GL_PROJECTION);
	glPushMatrix();
	glLoadIdentity();

	glDisable(GL_DEPTH_TEST);

	glOrtho( 0.0, 1000.0, 0.0, 1000.0, -1.0, 1.0 );
	GLTTT_GLTRANSLATE(xp,1000-yp,0);
	glScalef(sf, sf, sf);

	for (x=0; x<strlen(msg); x++)
		glttt_platform_draw_char( msg[x] );

	glEnable(GL_DEPTH_TEST);

	glPopMatrix();

	glMatrixMode(GL_MODELVIEW);
	glPopMatrix();
}

void getWindowCoords( GLTTT_FP_TYPE worldcx, GLTTT_FP_TYPE worldcy, GLTTT_FP_TYPE worldcz, GLTTT_FP_TYPE *winx, GLTTT_FP_TYPE *winy, GLTTT_FP_TYPE *winz )
{
	GLTTT_FP_TYPE matmodel[16], matproj[16];
	GLint vp[4];

	glGetIntegerv( GL_VIEWPORT, vp );
	GLTTT_GLGETFPTYPEV( GL_MODELVIEW_MATRIX, matmodel );
	GLTTT_GLGETFPTYPEV( GL_PROJECTION_MATRIX, matproj );

	gluProject( worldcx, worldcy, worldcz, matmodel, matproj, vp, winx, winy, winz );
}

int peg_in_queue( list_t *queue, peg_label_t peg, int height )
{
	int ret;
	list_node_t *i;
	row_t *r;

	ret=FALSE;

	i=queue->first;

	while (i != NULL && !ret)
	{
		//printf("iteration\n");
		r=(row_t *)i->obj;
		if ( (r->p1 == peg && r->h1 ==height) || (r->p2 == peg && r->h2 ==height) || (r->p3 == peg && r->h3 ==height) )
		{
			//printf("found: peg: %d, height: %d\n",peg,height);
			//printf("r->p1: %d, r->p2: %d, r->p3: %d, r->h1: %d, r->h2: %d, r->h3: %d\n",r->p1,r->p2,r->p3,r->h1,r->h2,r->h3);
			ret=TRUE;
		}

		i=i->next;
	}
	//printf("iteration ends, returning: %d\n\n",ret);

	return ret;
}

void draw_game_screen()
{
	int x, y;
	GLTTT_FP_TYPE winx, winy, winz;
	int r,w;
	char buf[100];
	double dist, short_dist;
	int short_peg;
	int flashing;
	list_node_t *i, *i2;
	int t;
	int mx_shortdist, my_shortdist;

	mx_shortdist = globals.mx_hover;
	my_shortdist = globals.my_hover;
	short_peg=0;
	getWindowCoords( PEG_POS[0][0], 25, PEG_POS[0][1], &winx, &winy, &winz );
	short_dist=sqrt( ((mx_shortdist - (GLint)winx)*(mx_shortdist - (GLint)winx)) + ((my_shortdist - (GLint)winy)*(my_shortdist - (GLint)winy)) );

	for (x=1; x<8; x++)
	{
		getWindowCoords( PEG_POS[x][0], 25, PEG_POS[x][1], &winx, &winy, &winz );
		dist=sqrt( ((mx_shortdist - (GLint)winx)*(mx_shortdist - (GLint)winx)) + ((my_shortdist - (GLint)winy)*(my_shortdist - (GLint)winy)) );
		if (dist < short_dist)
		{
			short_dist=dist;
			short_peg=x;
		}
	}

	if (short_dist < PEG_SELECT_DIST)
		globals.peg_select_hover=short_peg;
	else
		globals.peg_select_hover=PEG_NONE;
	
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	
	gluPerspective( 35.0,1.0,10,1000 );
	gluLookAt( 100, globals.zoom + globals.new_zoom, 250, 0, 0, 0, 0.0, 1.0, 0.0 );

	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();

	glRotatef( globals.rot + globals.new_rot, 0.0, 1.0, 0.0 );
	
	glMaterialfv(GL_FRONT, GL_AMBIENT, board_ambient);
	glMaterialfv(GL_FRONT, GL_DIFFUSE, board_diffuse);
	glMaterialfv(GL_FRONT, GL_SPECULAR, board_specular);
	glMaterialfv(GL_FRONT, GL_SHININESS, board_shininess);

	glBegin(GL_POLYGON);
		glVertex3f( -100, 0, -100 );
		glVertex3f( -100, 0, 100 );
		glVertex3f( 100, 0, 100 );
		glVertex3f( 100, 0, -100 );
	glEnd();

	glMaterialfv(GL_FRONT, GL_AMBIENT, letter_ambient);
	glMaterialfv(GL_FRONT, GL_DIFFUSE, letter_diffuse);
	glMaterialfv(GL_FRONT, GL_SPECULAR, letter_specular);
	glMaterialfv(GL_FRONT, GL_SHININESS, letter_shininess);

	for (x=PEG_A; x<=PEG_H; x++)
		draw_char( PEG_LETTER_POS[x][0], PEG_LETTER_POS[x][1], 'A'+x );

	glPushMatrix();
	glTranslatef( 60, 1, 100 );
	glScalef( 0.03, 0.03, 0.03 );
	glRotatef( -90.0, 1.0, 0.0, 0.0 );

	for (x=0; x<strlen(SIG); x++)
		glttt_platform_draw_char( SIG[x] );
	
	glPopMatrix();					
	
	glMaterialfv(GL_FRONT, GL_AMBIENT, peg_normal_ambient);
	glMaterialfv(GL_FRONT, GL_DIFFUSE, peg_normal_diffuse);
	glMaterialfv(GL_FRONT, GL_SPECULAR, peg_normal_specular);
	glMaterialfv(GL_FRONT, GL_SHININESS, peg_normal_shininess);

	glBegin( GL_QUADS );
		for (x=PEG_A; x<=PEG_H; x++)
		{
			if (x==short_peg && short_dist < PEG_SELECT_DIST)
			{
				if (globals.peg_select_hover == globals.peg_select_down)
				{
					glMaterialfv(GL_FRONT, GL_AMBIENT, peg_hover_select_ambient);
					glMaterialfv(GL_FRONT, GL_DIFFUSE, peg_hover_select_diffuse);
					glMaterialfv(GL_FRONT, GL_SPECULAR, peg_hover_select_specular);
					glMaterialfv(GL_FRONT, GL_SHININESS, peg_hover_select_shininess);
				}
				else
				{
					glMaterialfv(GL_FRONT, GL_AMBIENT, peg_hover_ambient);
					glMaterialfv(GL_FRONT, GL_DIFFUSE, peg_hover_diffuse);
					glMaterialfv(GL_FRONT, GL_SPECULAR, peg_hover_specular);
					glMaterialfv(GL_FRONT, GL_SHININESS, peg_hover_shininess);
				}
			}
			else if (x == globals.peg_select_down)
			{
				glMaterialfv(GL_FRONT, GL_AMBIENT, peg_select_ambient);
				glMaterialfv(GL_FRONT, GL_DIFFUSE, peg_select_diffuse);
				glMaterialfv(GL_FRONT, GL_SPECULAR, peg_select_specular);
				glMaterialfv(GL_FRONT, GL_SHININESS, peg_select_shininess);
			}
			
			glVertex3f( PEG_POS[x][0]-PEG_THICK, 0, PEG_POS[x][1]-PEG_THICK );
			glVertex3f( PEG_POS[x][0]-PEG_THICK, 75, PEG_POS[x][1]-PEG_THICK );
			glVertex3f( PEG_POS[x][0]+PEG_THICK, 75, PEG_POS[x][1]-PEG_THICK );
			glVertex3f( PEG_POS[x][0]+PEG_THICK, 0, PEG_POS[x][1]-PEG_THICK );
			
			glVertex3f( PEG_POS[x][0]-PEG_THICK, 0, PEG_POS[x][1]+PEG_THICK );
			glVertex3f( PEG_POS[x][0]-PEG_THICK, 75, PEG_POS[x][1]+PEG_THICK );
			glVertex3f( PEG_POS[x][0]+PEG_THICK, 75, PEG_POS[x][1]+PEG_THICK );
			glVertex3f( PEG_POS[x][0]+PEG_THICK, 0, PEG_POS[x][1]+PEG_THICK );
			
			glVertex3f( PEG_POS[x][0]-PEG_THICK, 0, PEG_POS[x][1]+PEG_THICK );
			glVertex3f( PEG_POS[x][0]-PEG_THICK, 75, PEG_POS[x][1]+PEG_THICK );
			glVertex3f( PEG_POS[x][0]-PEG_THICK, 75, PEG_POS[x][1]-PEG_THICK );
			glVertex3f( PEG_POS[x][0]-PEG_THICK, 0, PEG_POS[x][1]-PEG_THICK );
			
			glVertex3f( PEG_POS[x][0]+PEG_THICK, 0, PEG_POS[x][1]+PEG_THICK );
			glVertex3f( PEG_POS[x][0]+PEG_THICK, 75, PEG_POS[x][1]+PEG_THICK );
			glVertex3f( PEG_POS[x][0]+PEG_THICK, 75, PEG_POS[x][1]-PEG_THICK );
			glVertex3f( PEG_POS[x][0]+PEG_THICK, 0, PEG_POS[x][1]-PEG_THICK );

			glMaterialfv(GL_FRONT, GL_AMBIENT, peg_normal_ambient);
			glMaterialfv(GL_FRONT, GL_DIFFUSE, peg_normal_diffuse);
			glMaterialfv(GL_FRONT, GL_SPECULAR, peg_normal_specular);
			glMaterialfv(GL_FRONT, GL_SHININESS, peg_normal_shininess);
		}
	glEnd();

	i=globals.row_queue->first;
	t=glttt_platform_time_in_millis_since_init();

	while (i != NULL)
	{
		i2=i->next;
		if (t > ((row_t *)i->obj)->time + PEG_FLASH_TIMEOUT)
		{
			free(i->obj);
			list_remove(globals.row_queue,i);
		}
		i=i2;
	}
	
	for (x=PEG_A; x<=PEG_H; x++)
		for (y=0; y<3; y++)
		{
			flashing=peg_in_queue( globals.row_queue, x, y );
			draw_peg( x, y, globals.cpu->root_node->board.peg[x][y],flashing );
		}

	glDisable(GL_LIGHTING);
	
	r=cpu_score( globals.cpu, PC_RED);
	w=cpu_score( globals.cpu, PC_WHITE);
	GLTTT_GLCOLOR3V( COLOUR_RED );
	if (globals.colour==PC_RED)
		snprintf(buf,100,"YOU: %d",r);
	else
		snprintf(buf,100,"CPU: %d",r);
	draw_message( 0.2, 20, 40, buf );

	GLTTT_GLCOLOR3V( COLOUR_WHITE );
	if (globals.colour==PC_RED)
		snprintf(buf,100,"CPU: %d",w);
	else
		snprintf(buf,100,"YOU: %d",w);
	draw_message( 0.2, 880, 40, buf );

	glmsgbox_draw( globals.msg_box );

	if (globals.game_state==GAMESTATE_GAME_OVER)
	{
		glLineWidth(3.0);
		glColor3f( 0.0, 0.0, 1.0 );
		if (globals.cpu_old > globals.hum_old)
			draw_message( 0.5, 300, 300, "CPU Wins!" );
		else if (globals.cpu_old == globals.hum_old)
			draw_message( 0.5, 300, 300, "Its a draw!" );
		else
			draw_message( 0.5, 300, 300, "You win!" );
		glLineWidth(1.0);
	}
		
	glEnable(GL_LIGHTING);
}

void draw_new_game()
{
	glColor3f( 1.0, 0.0, 0.0 );
	draw_message( 0.5, 200, 200, "Select your colour:" );

	glMatrixMode( GL_PROJECTION );
	glLoadIdentity();
	glOrtho( 0.0, 1000.0, 0.0, 1000.0, -1.0, 1.0 );

	glMatrixMode( GL_MODELVIEW );
	
	GLTTT_GLCOLOR3V( COLOUR_WHITE );
	glBegin(GL_QUADS);
		glVertex2i( 250,300 );
		glVertex2i( 250,500 );
		glVertex2i( 450,500 );
		glVertex2i( 450,300 );
	glEnd();

	GLTTT_GLCOLOR3V( COLOUR_RED );
	glBegin(GL_QUADS);
		glVertex2i( 500,300 );
		glVertex2i( 500,500 );
		glVertex2i( 700,500 );
		glVertex2i( 700,300 );
	glEnd();
}

void draw_move_first()
{
	glColor3f( 1.0, 0.0, 0.0 );
	draw_message( 0.5, 300, 200, "Move first?" );

	glMatrixMode( GL_PROJECTION );
	glLoadIdentity();
	glOrtho( 0.0, 1000.0, 0.0, 1000.0, -1.0, 1.0 );
	
	glMatrixMode( GL_MODELVIEW );

	GLTTT_GLCOLOR3V( COLOUR_WHITE );
	glBegin(GL_QUADS);
		glVertex2i( 250,300 );
		glVertex2i( 250,500 );
		glVertex2i( 450,500 );
		glVertex2i( 450,300 );
	glEnd();

	GLTTT_GLCOLOR3V( COLOUR_RED );
	draw_message( 0.4, 300, 585, "Yes" );
	
	GLTTT_GLCOLOR3V( COLOUR_WHITE );
	glBegin(GL_QUADS);
		glVertex2i( 500,300 );
		glVertex2i( 500,500 );
		glVertex2i( 700,500 );
		glVertex2i( 700,300 );
	glEnd();
	
	GLTTT_GLCOLOR3V( COLOUR_RED );
	draw_message( 0.4, 560, 585, "No" );	
}

void inc_move()
{
	globals.move_count++;
	if (globals.move_count==24)
		globals.game_state=GAMESTATE_GAME_OVER;
}

void mouse_choose( int x, int y )
{
	int xp;
	int yp;
	GLint vp[4];

	glGetIntegerv( GL_VIEWPORT, vp );

	xp=x;
	yp=vp[3]-y-1;

	if (yp >= 150 && yp <= 250)
	{
		if (xp >= 125 && xp <= 225)
		{
			if (globals.game_state==GAMESTATE_MOVE_FIRST)
			{
				globals.human_turn=TRUE;
				globals.game_state=GAMESTATE_IN_GAME;
				globals.cpu=cpu_new(globals.colour);
				glmsg_add( globals.msg_box, VERSION_STRING, COLOUR_VERSION_STRING );
				glmsg_add( globals.msg_box, "The game begins!", COLOUR_MSG_TEXT );
				glEnable(GL_LIGHTING);
			}
			else if (globals.game_state==GAMESTATE_NEW_GAME)
			{
				glDisable(GL_LIGHTING);
				globals.colour=PC_WHITE;
				globals.game_state=GAMESTATE_MOVE_FIRST;
			}
		}
		else if (xp >= 250 && xp <= 350)
		{
			if (globals.game_state==GAMESTATE_MOVE_FIRST)
			{
				globals.human_turn=FALSE;
				globals.game_state=GAMESTATE_IN_GAME;
				glEnable(GL_LIGHTING);
				glmsg_add( globals.msg_box, "The game begins!", COLOUR_MSG_TEXT );
				if (globals.colour==PC_RED)
					globals.cpu=cpu_new(PC_WHITE);
				else
					globals.cpu=cpu_new(PC_RED);
			}
			else if (globals.game_state==GAMESTATE_NEW_GAME)
			{
				globals.colour=PC_RED;
				glDisable(GL_LIGHTING);
				globals.game_state=GAMESTATE_MOVE_FIRST;
			}
		}
	}
}

void add_vert_rows_to_queue( int oldmask, int newmask, peg_colour_t col )
{
	int mask;
	int t,x;
	row_t *r;

	mask=newmask & ~oldmask;
	t=glttt_platform_time_in_millis_since_init();
	
	for (x=0; x<8; x++)
		if (mask & (1 << x))
		{
			//printf("add to queue: peg: %d, rows 1 2 3\n",x);
			r=(row_t *)malloc(sizeof(row_t));
			r->time=t;
			r->p1=x;
			r->p2=x;
			r->p3=x;
			r->h1=0;
			r->h2=1;
			r->h3=2;

			list_append( globals.row_queue, NULL, (void *)r );
			//printf("queue size: %d\n",globals.row_queue->size);
			//printf("first: %d\n",globals.row_queue->first);
			//printf("last: %d\n",globals.row_queue->last);
		}
}

void add_horiz_rows_to_queue( int oldmask, int newmask, peg_colour_t col )
{
	int mask;
	int t,x,y,n;
	row_t *r;

	t=glttt_platform_time_in_millis_since_init();
	mask=newmask & ~oldmask;

	for (x=0; x<6; x++)
	{
		n=1<<(x * 5);
		for (y=0; y<3; y++)
		{
			if (mask & (n << y))
			{
				//printf("horiz: add to queue: height: %d, pegs: %d, %d, %d\n",y,PEG_ROW[x][0],PEG_ROW[x][1],PEG_ROW[x][2]);
				r=(row_t *)malloc(sizeof(row_t));
				r->time=t;
				r->p1=PEG_ROW[x][0];
				r->p2=PEG_ROW[x][1];
				r->p3=PEG_ROW[x][2];
				r->h1=y;
				r->h2=y;
				r->h3=y;

				list_append( globals.row_queue, NULL, (void *)r );
			}
		}

		if ( (mask & (n << 3)) || (mask & (n << 4)) )
		{
			//printf("horiz: add to queue: pegs: %d, %d, %d, ",PEG_ROW[x][0],PEG_ROW[x][1],PEG_ROW[x][2]);
			r=(row_t *)malloc(sizeof(row_t));
			r->time=t;
			r->p1=PEG_ROW[x][0];
			r->p2=PEG_ROW[x][1];
			r->p3=PEG_ROW[x][2];
			r->h2=1;
			
			if (mask & (n << 3))
			{
				//printf("heights: 2, 1, 0\n");
				r->h1=2;
				r->h3=0;
			}
			else
			{
				//printf("heights: 0, 1, 2\n");
				r->h1=0;
				r->h3=2;
			}
			
			list_append( globals.row_queue, NULL, (void *)r );
		}
	}
}

void do_turn( peg_label_t peg )
{
	char buf[100];

	if (globals.game_state==GAMESTATE_GAME_OVER)
		return;
	
	if (cpu_sendmove( globals.cpu, peg ))
	{
		inc_move();
		globals.human_move_time = glttt_platform_time_in_millis_since_init();
		globals.human_turn=FALSE;
		if (globals.hum_old < cpu_score(globals.cpu,globals.colour))
		{
			snprintf(buf,100,"You scored %d rows!",cpu_score(globals.cpu,globals.colour)-globals.hum_old);
			glmsg_add( globals.msg_box, buf, COLOUR_MSG_TEXT );
			globals.hum_old=cpu_score(globals.cpu,globals.colour);

			//printf("old horiz mask: %x, old vert mask: %x\n",globals.mask_horiz_hum,globals.mask_vert_hum);

			add_vert_rows_to_queue( globals.mask_vert_hum, board_get_vert_mask( &globals.cpu->root_node->board, globals.colour ), globals.colour );
			add_horiz_rows_to_queue( globals.mask_horiz_hum, board_get_horiz_mask( &globals.cpu->root_node->board, globals.colour ), globals.colour );
			
			globals.mask_horiz_hum=board_get_horiz_mask( &globals.cpu->root_node->board, globals.colour );
			globals.mask_vert_hum=board_get_vert_mask( &globals.cpu->root_node->board, globals.colour );
			
			//printf("new horiz mask: %x, new vert mask: %x\n",globals.mask_horiz_hum,globals.mask_vert_hum);
		}
	}
	else
		glmsg_add( globals.msg_box, "Invalid move.", COLOUR_MSG_TEXT );
}

void glttt_start( int* argc, char **argv )
{
	glttt_platform_init( argc, argv, 560, 560, "GL TicTacToe" );
	
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();

	glLightfv( GL_LIGHT0, GL_AMBIENT, light_ambient );
	glLightfv( GL_LIGHT0, GL_DIFFUSE, light_diffuse );
	glLightfv( GL_LIGHT0, GL_SPECULAR, light_specular );

	/*glLightfv( GL_LIGHT1, GL_AMBIENT, light_ambient );
	glLightfv( GL_LIGHT1, GL_DIFFUSE, light_diffuse );
	glLightfv( GL_LIGHT1, GL_SPECULAR, light_specular );*/

	glLightfv( GL_LIGHT0, GL_POSITION, light_position0 );
	/*glLightfv( GL_LIGHT1, GL_POSITION, light_position1 );*/
	
	glEnable( GL_LIGHT0 );
	/*glEnable( GL_LIGHT1 );*/
	
	glEnable(GL_DEPTH_TEST);
	glEnable(GL_NORMALIZE);
	glShadeModel(GL_SMOOTH);
	
	global_init();
	globals.msg_box=glmsgbox_new( COLOUR_MSG_BOX, 4, 0.7, 8000 );
	
	glttt_platform_run();
}


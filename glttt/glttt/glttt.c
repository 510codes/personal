#include <stdio.h>
#include <string.h>
#include <math.h>

#include <GL/gl.h>
#include <GL/glut.h>

#include "gl_msg.h"

#include "cpu.h"
#include "board.h"
#include "defs.h"

const GLint MIN_ZOOM=50;
const GLint ZOOM_FACTOR=10;
const GLdouble ROTATE_FACTOR=2.0;
const GLdouble MOUSE_ROT_FACT=0.5;
const GLdouble MOUSE_ZOOM_FACT=1.5;

const GLint PEG_SIZE=11;
const GLint PEG_THICK=1;

const GLdouble PEG_SELECT_DIST=100.0;

const int PEG_FLASH_TIMEOUT=1250;

const int CPU_WAIT_TO_MOVE=2000;
//const int CPU_WAIT_TO_MOVE=0;

const char *PEGNAME="ABCDEFGH";
const char *SIG="Chris Riley - 2003";
const char *VERSION_STRING="GLTicTacToe (20030622): C. Riley '03";

const GLdouble PEG_LETTER_POS[][2]={	{-60,70},{-10,70},{40,70,},
					{-50,10},{40, 10},
					{-70,-70},{-10,-70},{50,-70} };
								

const GLdouble PEG_POS[][2]={	{-50,50},{0,50},{50,50},
				{-25,0},{25,0},
				{-50,-50},{0,-50},{50,-50} };


const peg_label_t PEG_ROW[][3]={	{PEG_F, PEG_G, PEG_H},
					{PEG_F, PEG_D, PEG_B},
					{PEG_G, PEG_E, PEG_C},
					{PEG_A, PEG_B, PEG_C},
					{PEG_A, PEG_D, PEG_G},
					{PEG_B, PEG_E, PEG_H} };

const GLdouble COLOUR_WHITE[]={ 0.9, 0.9, 0.9 };
const GLdouble COLOUR_RED[]={ 1.0, 0.0, 0.0 };

GLdouble COLOUR_MSG_BOX[]={ 0.3,0.1,0.1,0.5 };
GLdouble COLOUR_MSG_TEXT[]={ 0.9, 0.9, 0.9 };
GLdouble COLOUR_VERSION_STRING[]={ 0.8, 1.0, 0.8 };

GLdouble COLOUR_MSG_BACK[]={ 0.3,0.1,0.1,0.5 };

GLfloat light_ambient[] = {0.5, 0.5, 0.5, 1.0};
GLfloat light_diffuse[] = {0.5, 0.5, 0.5, 1.0};
GLfloat light_specular[] = {0.5, 0.5, 0.5, 1.0};

//GLfloat light_position0[] = {-100,100,-100, 0.0};
GLfloat light_position0[] = {10,5000,10, 0.0};
GLfloat light_position1[] = {100,100,100, 0.0};

GLfloat redpeg_ambient[] = {0.9, 0.2, 0.2, 1.0};
GLfloat redpeg_diffuse[] = {0.4, 0.2, 0.1, 1.0};
GLfloat redpeg_specular[] = {0.5, 0.5, 0.5, 1.0};
GLfloat redpeg_shininess[] = {0.30};

GLfloat redpeg_flash_ambient[] = {0.4, 0.1, 0.1, 1.0};
GLfloat redpeg_flash_diffuse[] = {0.4, 0.1, 0.1, 1.0};
GLfloat redpeg_flash_specular[] = {0.4, 0.1, 0.1, 1.0};
GLfloat redpeg_flash_shininess[] = {0.30};

GLfloat whitepeg_ambient[] = {0.7, 0.7, 0.7, 1.0};
GLfloat whitepeg_diffuse[] = {0.7, 0.7, 0.7, 1.0};
GLfloat whitepeg_specular[] = {0.2, 0.2, 0.2, 1.0};
GLfloat whitepeg_shininess[] = {0.30};

GLfloat whitepeg_flash_ambient[] = {0.5, 0.5, 0.7, 1.0};
GLfloat whitepeg_flash_diffuse[] = {0.5, 0.5, 0.7, 1.0};
GLfloat whitepeg_flash_specular[] = {0.2, 0.2, 0.3, 1.0};
GLfloat whitepeg_flash_shininess[] = {0.30};

//GLfloat board_ambient[] = {0.2, 0.9, 0.2, 0.7};
GLfloat board_ambient[] = {0.1, 0.2, 0.5, 1.0};
GLfloat board_diffuse[] = {0.1, 0.1, 0.2, 1.0};
GLfloat board_specular[] = {0.1, 0.1, 0.2, 1.0};
GLfloat board_shininess[] = {0.0};

//GLfloat peg_normal_ambient[] = {1.0, 1.0, 0.0, 0.3};
GLfloat peg_normal_ambient[] = {0.55, 0.55, 0.48, 0.3};
GLfloat peg_normal_diffuse[] = {0.7, 0.7, 0.7, 0.7};
GLfloat peg_normal_specular[] = {0.8, 0.8, 0.8, 0.7};
GLfloat peg_normal_shininess[] = {0.90};

GLfloat peg_select_ambient[] = {0.1, 0.2, 0.9, 0.3};
GLfloat peg_select_diffuse[] = {0.4, 0.2, 0.1, 0.7};
GLfloat peg_select_specular[] = {0.5, 0.5, 0.5, 0.7};
GLfloat peg_select_shininess[] = {0.70};

GLfloat letter_ambient[] = {0.4, 0.7, 0.4, 1.0};
GLfloat letter_diffuse[] = {0.4, 0.7, 0.4, 1.0};
GLfloat letter_specular[] = {0.4, 0.7, 0.4, 1.0};
GLfloat letter_shininess[] = {0.10};


// random prototypes
//
void do_turn( peg_label_t peg );
void add_vert_rows_to_queue( int oldmask, int newmask, peg_colour_t col );
void add_horiz_rows_to_queue( int oldmask, int newmask, peg_colour_t col );

#define GAMESTATE_NEW_GAME 1
#define GAMESTATE_IN_GAME 2
#define GAMESTATE_MOVE_FIRST 3
#define GAMESTATE_GAME_OVER 4


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


typedef struct
{
	cpu_t *cpu;
	int game_state;
	peg_colour_t colour;
	int human_turn;
	glmsgbox_t *msg_box;
	int cpu_old;
	int hum_old;
	int move_count;
	int human_move_time;

	int mask_vert_cpu;
	int mask_horiz_cpu;
	int mask_vert_hum;
	int mask_horiz_hum;
	
	GLdouble rot;
	GLdouble new_rot;
	GLint zoom;
	GLint new_zoom;
	int repos_active;
	int xp;
	int yp;
	GLint mx;
	GLint my;

	list_t *row_queue;
	
	peg_label_t peg_select;

} global_t;

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

	globals.mx=0;
	globals.my=0;
	globals.peg_select=PEG_NONE;

	globals.human_move_time=0;

	// make sure this variable is null before calling
	// this function...
	// 
	globals.row_queue=list_new();
}

void draw_char( GLdouble x, GLdouble z, char c )
{
	glPushMatrix();
	glTranslatef( x, 1, z );
	glScalef( 0.1, 0.1, 0.1 );
	glRotatef( -90.0, 1.0, 0.0, 0.0 );
	glutStrokeCharacter( GLUT_STROKE_ROMAN, c );
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

		glutSolidSphere( 10, 20, 20 );
		
		glPopMatrix();
	}	
}

void draw_message( GLdouble sf, GLint xp, GLint yp, char *msg )
{
	int x;

	glMatrixMode(GL_MODELVIEW);
	glPushMatrix();
	glLoadIdentity();

	glMatrixMode(GL_PROJECTION);
	glPushMatrix();
	glLoadIdentity();

	glDisable(GL_DEPTH_TEST);

	gluOrtho2D(0,1000,0,1000);
	glTranslated(xp,1000-yp,0);
	glScalef(sf, sf, sf);

	for (x=0; x<strlen(msg); x++)
		glutStrokeCharacter( GLUT_STROKE_ROMAN, msg[x] );

	glEnable(GL_DEPTH_TEST);
	
	glPopMatrix();

	glMatrixMode(GL_MODELVIEW);
	glPopMatrix();
}

void getWindowCoords( GLdouble worldcx, GLdouble worldcy, GLdouble worldcz, GLdouble *winx, GLdouble *winy, GLdouble *winz )
{
	GLdouble matmodel[16], matproj[16];
	GLint vp[4];

	glGetIntegerv( GL_VIEWPORT, vp );
	glGetDoublev( GL_MODELVIEW_MATRIX, matmodel );
	glGetDoublev( GL_PROJECTION_MATRIX, matproj );

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
	GLdouble winx, winy, winz;
	int r,w;
	char buf[100];
	double dist, short_dist;
	int short_peg;
	int flashing;
	list_node_t *i, *i2;
	int t;
	
	short_peg=0;
	getWindowCoords( PEG_POS[0][0], 25, PEG_POS[0][1], &winx, &winy, &winz );
	short_dist=sqrt( ((globals.mx - (GLint)winx)*(globals.mx - (GLint)winx)) + ((globals.my - (GLint)winy)*(globals.my - (GLint)winy)) );

	for (x=1; x<8; x++)
	{
		getWindowCoords( PEG_POS[x][0], 25, PEG_POS[x][1], &winx, &winy, &winz );
		dist=sqrt( ((globals.mx - (GLint)winx)*(globals.mx - (GLint)winx)) + ((globals.my - (GLint)winy)*(globals.my - (GLint)winy)) );
		if (dist < short_dist)
		{
			short_dist=dist;
			short_peg=x;
		}
	}

	if (short_dist < PEG_SELECT_DIST)
		globals.peg_select=short_peg;
	else
		globals.peg_select=PEG_NONE;
	
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
		glutStrokeCharacter( GLUT_STROKE_ROMAN, SIG[x] );
	
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

			if (x==short_peg && short_dist < PEG_SELECT_DIST)
			{
				glMaterialfv(GL_FRONT, GL_AMBIENT, peg_normal_ambient);
				glMaterialfv(GL_FRONT, GL_DIFFUSE, peg_normal_diffuse);
				glMaterialfv(GL_FRONT, GL_SPECULAR, peg_normal_specular);
				glMaterialfv(GL_FRONT, GL_SHININESS, peg_normal_shininess);
			}
		}
	glEnd();

	i=globals.row_queue->first;
	t=glutGet(GLUT_ELAPSED_TIME);

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
	glColor3dv( COLOUR_RED );
	if (globals.colour==PC_RED)
		snprintf(buf,100,"YOU: %d",r);
	else
		snprintf(buf,100,"CPU: %d",r);
	draw_message( 0.2, 20, 40, buf );

	glColor3dv( COLOUR_WHITE );
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
	gluOrtho2D( 0, 1000, 0, 1000 );

	glMatrixMode( GL_MODELVIEW );
	
	glColor3dv( COLOUR_WHITE );
	glBegin(GL_QUADS);
		glVertex2i( 250,300 );
		glVertex2i( 250,500 );
		glVertex2i( 450,500 );
		glVertex2i( 450,300 );
	glEnd();

	glColor3dv( COLOUR_RED );
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
	gluOrtho2D( 0, 1000, 0, 1000 );
	
	glMatrixMode( GL_MODELVIEW );

	glColor3dv( COLOUR_WHITE );
	glBegin(GL_QUADS);
		glVertex2i( 250,300 );
		glVertex2i( 250,500 );
		glVertex2i( 450,500 );
		glVertex2i( 450,300 );
	glEnd();

	glColor3dv( COLOUR_RED );
	draw_message( 0.4, 300, 585, "Yes" );
	
	glColor3dv( COLOUR_WHITE );
	glBegin(GL_QUADS);
		glVertex2i( 500,300 );
		glVertex2i( 500,500 );
		glVertex2i( 700,500 );
		glVertex2i( 700,300 );
	glEnd();
	
	glColor3dv( COLOUR_RED );
	draw_message( 0.4, 560, 585, "No" );	
}

void display()
{
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	switch(globals.game_state)
	{
		case GAMESTATE_NEW_GAME:
			draw_new_game();
			break;

		case GAMESTATE_MOVE_FIRST:
			draw_move_first();
			break;

		case GAMESTATE_IN_GAME:
		case GAMESTATE_GAME_OVER:
			draw_game_screen();
			break;
	}
	
	glutSwapBuffers();
}

void inc_move()
{
	globals.move_count++;
	if (globals.move_count==24)
		globals.game_state=GAMESTATE_GAME_OVER;
}

void idlefunc()
{
	peg_label_t move;
	char buf[100];
	peg_colour_t cpu_col;
	int sc;
	int t;

	t=glutGet(GLUT_ELAPSED_TIME);
	
	if (globals.game_state==GAMESTATE_IN_GAME)
	{
		if (!globals.human_turn && cpu_ready(globals.cpu) && t > (globals.human_move_time + CPU_WAIT_TO_MOVE))
		{
			if (globals.colour==PC_RED)
				cpu_col=PC_WHITE;
			else
				cpu_col=PC_RED;
		
			inc_move();
			move=cpu_getmove(globals.cpu);
			snprintf(buf,100,"CPU moves to peg %c.",PEGNAME[move]);
			glmsg_add( globals.msg_box, buf, COLOUR_MSG_TEXT );
			cpu_sendmove(globals.cpu,move);

			sc=cpu_score(globals.cpu,cpu_col);
			if (sc > globals.cpu_old)
			{
				snprintf(buf,100,"CPU scores %d rows!",sc-globals.cpu_old);
				glmsg_add( globals.msg_box, buf, COLOUR_MSG_TEXT );
				globals.cpu_old=sc;
				
				//printf("old horiz mask: %x, old vert mask: %x\n",globals.mask_horiz_cpu,globals.mask_vert_cpu);

				add_vert_rows_to_queue( globals.mask_vert_cpu, board_get_vert_mask( &globals.cpu->root_node->board, cpu_col ), cpu_col );
				add_horiz_rows_to_queue( globals.mask_horiz_cpu, board_get_horiz_mask( &globals.cpu->root_node->board, cpu_col ), cpu_col );
			
				globals.mask_horiz_cpu=board_get_horiz_mask( &globals.cpu->root_node->board, cpu_col );
				globals.mask_vert_cpu=board_get_vert_mask( &globals.cpu->root_node->board, cpu_col );
			
				//printf("new horiz mask: %x, new vert mask: %x\n",globals.mask_horiz_cpu,globals.mask_vert_cpu);
			}
			
			globals.human_turn=TRUE;
		}
	}
	
	glutPostRedisplay();
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

void motionfunc( int x, int y )
{
	if (globals.repos_active)
	{
		globals.new_rot = 0-((globals.xp-x) * MOUSE_ROT_FACT);
		while (globals.new_rot > 360)
			globals.new_rot -= 360;
		while (globals.new_rot < 0)
			globals.new_rot += 360;
		globals.new_zoom = ((globals.yp-y) * MOUSE_ZOOM_FACT);
	}
}

void passivemotionfunc( int x, int y )
{
	GLint vp[4];

	glGetIntegerv( GL_VIEWPORT,vp );
	globals.mx=x;
	globals.my=vp[3]-(GLint)y-1;
}


void mousefunc( int b, int s, int xp, int yp )
{
	if (b==GLUT_LEFT_BUTTON && s==GLUT_DOWN)
		switch (globals.game_state)
		{
			case GAMESTATE_NEW_GAME:
			case GAMESTATE_MOVE_FIRST:
				mouse_choose( xp, yp );
				break;

			case GAMESTATE_IN_GAME:
				if (globals.peg_select != PEG_NONE && globals.human_turn)
					do_turn(globals.peg_select);
				break;
		}
	
	else if (b==GLUT_RIGHT_BUTTON && s==GLUT_DOWN)
		switch (globals.game_state)
		{
			case GAMESTATE_IN_GAME: case GAMESTATE_GAME_OVER:
				globals.repos_active=TRUE;
				globals.xp=xp;
				globals.yp=yp;
				globals.new_rot=0;
				globals.new_zoom=0;
				break;
		}

	else if (b==GLUT_RIGHT_BUTTON && s==GLUT_UP)
		switch (globals.game_state)
		{
			case GAMESTATE_IN_GAME: case GAMESTATE_GAME_OVER:
				globals.repos_active=FALSE;
				globals.rot += globals.new_rot;
				globals.zoom += globals.new_zoom;
				globals.new_rot=0;
				globals.new_zoom=0;
				break;
		}
}

void add_vert_rows_to_queue( int oldmask, int newmask, peg_colour_t col )
{
	int mask;
	int t,x;
	row_t *r;

	mask=newmask & ~oldmask;
	t=glutGet(GLUT_ELAPSED_TIME);
	
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

	t=glutGet(GLUT_ELAPSED_TIME);
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
		globals.human_move_time=glutGet(GLUT_ELAPSED_TIME);
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

void kbfunc( unsigned char c, int x, int y )
{
	if (globals.game_state==GAMESTATE_IN_GAME || globals.game_state==GAMESTATE_GAME_OVER)
		switch (c)
		{
			case (char)27:
				exit(1);
			case '4':
				globals.rot -= ROTATE_FACTOR;
				break;

			case '6':
				globals.rot += ROTATE_FACTOR;
				break;

			case '8':
				globals.zoom += ZOOM_FACTOR;
				break;

			case '2':
				globals.zoom -= ZOOM_FACTOR;
				if (globals.zoom < MIN_ZOOM)
					globals.zoom=MIN_ZOOM;
				break;

			case 'a': case 'b': case 'c': case 'd':
			case 'e': case 'f': case 'g': case 'h':
				if (globals.human_turn)
					do_turn((int)c - 'a');
				break;
		}
}

int main( int argc, char **argv )
{
	glutInit( &argc, argv );
	glutInitWindowSize( 560, 560 );
	glutInitDisplayMode( GLUT_RGB | GLUT_DOUBLE | GLUT_DEPTH );
	glutCreateWindow( "GL TicTacToe" );

	glutDisplayFunc(display);
	glutKeyboardFunc(kbfunc);
	glutIdleFunc(idlefunc);
	glutMouseFunc(mousefunc);
	glutMotionFunc(motionfunc);
	glutPassiveMotionFunc(passivemotionfunc);
	
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
	
	glutMainLoop();
	
	return 0;
}

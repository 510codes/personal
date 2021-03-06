#include "callbacks.h"
#include "../../glttt_callbacks.h"

#include "GL/glut.h"


void kbfunc( unsigned char c, int x, int y )
{
	glttt_callbacks_kb( c );
}

void motionfunc( int x, int y )
{
	glttt_callback_motion( x, y );
}

void passivemotionfunc( int x, int y )
{
	glttt_callback_passivemotion( x, y );
}

void mousefunc( int b, int s, int xp, int yp )
{
	if (b==GLUT_LEFT_BUTTON && s==GLUT_DOWN)
	{
		glttt_callback_command_action_start( xp, yp );
	}
	else if (b==GLUT_LEFT_BUTTON && s==GLUT_UP)
	{
		glttt_callback_command_action_stop( xp, yp );
	}
	else if (b==GLUT_RIGHT_BUTTON && s==GLUT_DOWN)
	{
		glttt_callback_rotate_action_start( xp, yp );
	}
	else if (b==GLUT_RIGHT_BUTTON && s==GLUT_UP)
	{
		glttt_callback_rotate_action_stop( xp, yp );
	}
}

void displayfunc()
{
	glttt_callback_display();
}

void idlefunc()
{
	glttt_callback_idle();
}



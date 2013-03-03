#include "callbacks.h"
#include "../../glttt_callbacks.h"

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
	glttt_callback_mouse( b, s, xp, yp );
}

void displayfunc()
{
	glttt_callback_display();
}

void idlefunc()
{
	glttt_callback_idle();
}



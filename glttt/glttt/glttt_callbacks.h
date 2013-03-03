#ifndef __GLTTT_CALLBACKS_H__
#define __GLTTT_CALLBACKS_H__

#include "globals.h"

void glttt_callbacks_kb( unsigned char c );

void glttt_callback_motion( int x, int y );

void glttt_callback_passivemotion( int x, int y );

void glttt_callback_left_mouse_down( int xp, int yp );

void glttt_callback_left_mouse_up( int xp, int yp );

void glttt_callback_right_mouse_down( int xp, int yp );

void glttt_callback_right_mouse_up( int xp, int yp );

void glttt_callback_display();

void glttt_callback_idle();

#endif


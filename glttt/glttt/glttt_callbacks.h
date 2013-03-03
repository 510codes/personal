#ifndef __GLTTT_CALLBACKS_H__
#define __GLTTT_CALLBACKS_H__

#include "globals.h"

void glttt_callbacks_kb( unsigned char c );

void glttt_callback_motion( int x, int y );

void glttt_callback_passivemotion( int x, int y );

void glttt_callback_command_action_start( int xp, int yp );

void glttt_callback_command_action_stop( int xp, int yp );

void glttt_callback_rotate_action_start( int xp, int yp );

void glttt_callback_rotate_action_stop( int xp, int yp );

void glttt_callback_display();

void glttt_callback_idle();

#endif


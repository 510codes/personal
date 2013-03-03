#ifndef __GLTTT_H__
#define __GLTTT_H__

#include "defs.h"

void do_turn( peg_label_t peg );
void mouse_choose( int x, int y );
void draw_new_game();
void draw_move_first();
void draw_game_screen();
void inc_move();
void add_vert_rows_to_queue( int oldmask, int newmask, peg_colour_t col );
void add_horiz_rows_to_queue( int oldmask, int newmask, peg_colour_t col );

#endif


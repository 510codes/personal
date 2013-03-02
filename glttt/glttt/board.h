#ifndef BOARD_H_
#define BOARD_H_

#include <stdio.h>
#include <stdlib.h>

#include "defs.h"

void board_init( board_t *b );

void board_copy( board_t *dst, board_t *src );

int peg_full( board_t *board, peg_label_t peg );

void add_peg( board_t *board, peg_label_t peg, peg_colour_t col );

void board_print( board_t *board );

int board_get_vert_mask( board_t *board, peg_colour_t col );
int board_get_horiz_mask( board_t *board, peg_colour_t col );

int board_complete_rows( board_t *board, peg_colour_t col );

#endif

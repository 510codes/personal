#include "board.h"

void board_init( board_t *b )
{
	int x;
	int y;

	for (x=PEG_A; x<=PEG_H; x++)
		for (y=0; y<3; y++)
			b->peg[x][y]=PC_NONE;
}

void board_copy( board_t *dst, board_t *src )
{
	int x;
	int y;
	
	for (x=PEG_A; x<=PEG_H; x++)
		for (y=0; y<3; y++)
			dst->peg[x][y]=src->peg[x][y];
}

int peg_full( board_t *board, peg_label_t peg )
{
	if (board->peg[peg][2] == PC_NONE)
		return FALSE;
	else
		return TRUE;
}

void add_peg( board_t *board, peg_label_t peg, peg_colour_t col )
{
	if (board->peg[peg][0] == PC_NONE)
		board->peg[peg][0] = col;
	else if (board->peg[peg][1] == PC_NONE)
		board->peg[peg][1] = col;
	else if (board->peg[peg][2] == PC_NONE)
		board->peg[peg][2] = col;
	else
	{
		printf("\n** ERROR:  add_peg(): the peg is full (peg: %d)\n\n",peg);
		exit(1);
	}
}

void print_peg( peg_colour_t p )
{
	if (p == PC_RED)
		printf("R");
	else if (p == PC_WHITE)
		printf("W");
	else
		printf("-");
}

void board_print( board_t *board )
{
	int x;
	int y;

	printf("F     G     H\n\n");
	for (y=2; y>=0; y--)
	{
		for (x=PEG_F; x<=PEG_H; x++)
		{
			print_peg(board->peg[x][y]);
			printf("     ");
		}
		printf("\n");
	}

	printf("\n");
	for (y=2; y>=0; y--)
	{
		for (x=PEG_D; x<=PEG_E; x++)
		{
			if (y==1 && x==PEG_D)
				printf("D");
			else
				printf(" ");
			printf("  ");
			print_peg(board->peg[x][y]);

			if (x==PEG_D)
				printf("  ");

			if (y==1 && x==PEG_E)
				printf("  E");
		}
		printf("\n");
	}

	printf("\n");
	for (y=2; y>=0; y--)
	{
		for (x=PEG_A; x<=PEG_C; x++)
		{
			print_peg(board->peg[x][y]);
			printf("     ");
		}
		printf("\n");
	}
	
	printf("\nA     B     C\n");
}

int board_find_rows_vert( board_t *board, peg_label_t p, peg_colour_t col )
{
	if (board->peg[p][0]==col && board->peg[p][1]==col && board->peg[p][2]==col)
		return 1;
	else
		return 0;
}

int board_find_rows( board_t *board, peg_label_t p1, peg_label_t p2, peg_label_t p3, peg_colour_t col )
{
	int n;
	int x;

	n=0;

	if (col != PC_NONE)
	{
		for (x=0; x<3; x++)
			if ( board->peg[p1][x]==col && board->peg[p2][x]==col && board->peg[p3][x]==col )
				n++;

		if ( board->peg[p2][1]==col)
		{
			if (board->peg[p1][2]==col && board->peg[p3][0]==col)
				n++;
			if (board->peg[p3][2]==col && board->peg[p1][0]==col)
				n++;
		}
	}

	return n;
}

int board_complete_rows( board_t *board, peg_colour_t col )
{
	int n;
	int x;

	n = board_find_rows( board, PEG_F, PEG_G, PEG_H, col );
	n += board_find_rows( board, PEG_F, PEG_D, PEG_B, col );
	n += board_find_rows( board, PEG_G, PEG_E, PEG_C, col );
	n += board_find_rows( board, PEG_A, PEG_B, PEG_C, col );
	n += board_find_rows( board, PEG_A, PEG_D, PEG_G, col );
	n += board_find_rows( board, PEG_B, PEG_E, PEG_H, col );

	for (x=PEG_A; x<=PEG_H; x++)
		n+=board_find_rows_vert( board, x, col );

	return n;
}

int board_get_row_mask( board_t *board, peg_colour_t p1, peg_colour_t p2, peg_colour_t p3, peg_colour_t col )
{
	int mask;
	int x;

	mask=0;
	
	if (col != PC_NONE)
	{
		for (x=0; x<3; x++)
			if ( board->peg[p1][x]==col && board->peg[p2][x]==col && board->peg[p3][x]==col )
				mask |= (1 << x);

		if ( board->peg[p2][1]==col)
		{
			if (board->peg[p1][2]==col && board->peg[p3][0]==col)
				mask |= 8;
			if (board->peg[p3][2]==col && board->peg[p1][0]==col)
				mask |= 16;
		}
	}

	return mask;
}

int board_get_horiz_mask( board_t *board, peg_colour_t col )
{
	int mask;

	mask = board_get_row_mask( board, PEG_F, PEG_G, PEG_H, col );
	mask |= (board_get_row_mask( board, PEG_F, PEG_D, PEG_B, col ) << 5);
	mask |= (board_get_row_mask( board, PEG_G, PEG_E, PEG_C, col ) << 10);
	mask |= (board_get_row_mask( board, PEG_A, PEG_B, PEG_C, col ) << 15);
	mask |= (board_get_row_mask( board, PEG_A, PEG_D, PEG_G, col ) << 20);
	mask |= (board_get_row_mask( board, PEG_B, PEG_E, PEG_H, col ) << 25);

	return mask;
}

int board_get_vert_mask( board_t *board, peg_colour_t col )
{
	int mask;
	int x;

	mask=0;

	for (x=0; x<9; x++)
		if (board_find_rows_vert( board, x, col ) == 1)
			mask |= (1 << x);

	return mask;
}

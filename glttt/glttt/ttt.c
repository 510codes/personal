#include <stdio.h>
#include <unistd.h>

#include "cpu.h"
#include "board.h"

const char* pegname="ABCDEFGH";

const char *COLOUR[]={"NONE","RED","WHITE"};

void print_display( cpu_t *cpu, int *score_hum, int *score_cpu, peg_colour_t col_hum, peg_colour_t col_cpu )
{
	if (cpu_score(cpu, col_cpu) > *score_cpu)
	{
		printf("==> CPU scored %d rows!\n\n",cpu_score(cpu,col_cpu)-(*score_cpu));
		*score_cpu=cpu_score(cpu,col_cpu);
	}

	if (cpu_score(cpu,col_hum) > *score_hum)
	{
		printf("==> You scored %d rows!\n\n",cpu_score(cpu,col_hum)-(*score_hum));
		*score_hum=cpu_score(cpu,col_hum);
	}
	
	board_print( &cpu->root_node->board );
	printf("\nSCORE:   You (%s): %d    CPU (%s): %d\n\n",COLOUR[col_hum],*score_hum, COLOUR[col_cpu], *score_cpu);
}

int main()
{
	cpu_t *cpu;
	char  buf[100];
	peg_label_t move;
	int check;
	peg_colour_t col_human, col_cpu;
	int count;
	int score_cpu, score_human;

	col_cpu=PC_WHITE;
	col_human=PC_RED;

	printf("\n\nWelcome to Tic Tac Toe.\n");
	check=FALSE;

	while (!check)
	{
		printf("\nChoose your colour [R]ed or [W]hite: ");
		scanf("%s",buf);

		if (buf[0]=='r' || buf[0]=='R')
			check=TRUE;
		else if (buf[0]=='W' || buf[0]=='w')
		{
			check=TRUE;
			col_cpu=PC_RED;
			col_human=PC_WHITE;
		}
	}

	check=FALSE;
	cpu=NULL;
	count=0;
	score_cpu=0;
	score_human=0;

	while (!check)
	{
		printf("\nWould you like to move first [Y/N]? ");
		scanf("%s",buf);

		if (buf[0]=='Y' || buf[0]=='y')
		{
			cpu=cpu_new(col_human);
			check=TRUE;
		}
		else if (buf[0]=='n' || buf[0]=='N')
		{
			cpu=cpu_new(col_cpu);
			move=cpu_getmove(cpu);
			printf("\n==> CPU moves to peg %c.\n",pegname[move]);
	                count=1;
			cpu_sendmove(cpu, move);
			check=TRUE;
		}
	}					
	
	while (count < 24)
	{
		print_display( cpu, &score_human, &score_cpu, col_human, col_cpu );
		move=PEG_NONE;

		while (move==PEG_NONE)
		{
			printf("\n==> Move: ");
			scanf("%s",buf);
		
			switch(buf[0])
			{
				case 'A': case 'a':
					move=PEG_A;
					break;
				
				case 'B': case 'b':
					move=PEG_B;
					break;
				
				case 'C': case 'c':
					move=PEG_C;
					break;
				
				case 'D': case 'd':
					move=PEG_D;
					break;
				
				case 'E': case 'e':
					move=PEG_E;
					break;
				
				case 'F': case 'f':
					move=PEG_F;
					break;
				
				case 'G': case 'g':
					move=PEG_G;
					break;
				
				case 'H': case 'h':
					move=PEG_H;
					break;

				default:
					printf("\n-- Invalid move.\n\n");
					break;
			}
		}
		
		if (!cpu_sendmove( cpu, move ))
			printf("\n-- Invalid move.\n\n");
		else
		{
			count++;
			if (count < 24)
			{
				print_display( cpu, &score_human, &score_cpu, col_human, col_cpu );
				count++;
				move=cpu_getmove( cpu );
				printf("\n==> CPU moves to peg %c.\n",pegname[move]);
				cpu_sendmove(cpu, move);
			}
		}
	}

	print_display( cpu, &score_human, &score_cpu, col_human, col_cpu );

	if (cpu_score(cpu,col_human) > cpu_score(cpu,col_cpu))
		printf("\nYou win!\n\n");
	else if (cpu_score(cpu,col_cpu) > cpu_score(cpu,col_human))
		printf("\nCPU wins!\n\n");
	else
		printf("\nIts a draw!\n\n");

	return 0;
}

#ifndef CPU_H_
#define CPU_H_

#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

#ifdef HAVE_ERRNO_H
	#include <sys/errno.h>
#endif

#include "defs.h"
#include "board.h"

#include "list.h"



typedef struct TREENODE
{
	board_t board;
	struct TREENODE *parent;
	struct TREENODE *child[8];
	int num_children;
} treenode_t;


typedef struct
{
	pthread_t thread_id;
	peg_colour_t root_colour;
	treenode_t *root_node;
	int cur_tree_level;
	int move_received;

	pthread_mutex_t mutex_message;
	pthread_cond_t cv_message;

	pthread_mutex_t mutex_tree;
	
	pthread_mutex_t mutex_move;
	pthread_cond_t cv_move;
} cpu_t;






cpu_t *cpu_new( peg_colour_t first_move );

int cpu_ready( cpu_t *cpu );
peg_label_t cpu_getmove( cpu_t *cpu );
int cpu_sendmove( cpu_t *cpu, peg_label_t move );

int cpu_score( cpu_t *cpu, peg_colour_t col );

#endif

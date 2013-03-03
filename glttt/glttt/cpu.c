#include "cpu.h"

typedef void *(*threadfunc_t)(void *);



// random prototypes...
// 
int treenode_height( treenode_t *node );




int cpu_ready( cpu_t *cpu )
{
	if (pthread_mutex_trylock( &cpu->mutex_move )==0)
	{
		MUTEX_UNLOCK( &cpu->mutex_move )
		return TRUE;
	}
	else
		return FALSE;
}


void debug( char *text )
{
#ifdef DEBUG_
	printf("%s",text);
#endif
}



peg_colour_t whos_turn( peg_colour_t root_move_colour, int node_level )
{
	if (node_level % 2 == 1)
		return root_move_colour;
	else if (root_move_colour==PC_RED)
		return PC_WHITE;
	else
		return PC_RED;
}


treenode_t *treenode_new( treenode_t *parent, board_t *board )
{
	treenode_t *newnode;
	peg_label_t x;

	newnode=(treenode_t *)malloc( sizeof(treenode_t) );
	board_copy( &newnode->board, board );

	for (x=PEG_A; x<=PEG_H; x++)
		newnode->child[x]=NULL;
	
	newnode->parent=parent;

	return newnode;
}

void treenode_detatch( treenode_t *node )
{
	int x;

	if (node->parent != NULL)
	{
		for (x=PEG_A; x<=PEG_H; x++)
			if (node->parent->child[x] == node)
				node->parent->child[x]=NULL;
		node->parent=NULL;
	}
}

void treenode_prune( treenode_t *node )
{
	peg_label_t x;
	
	for (x=PEG_A; x<PEG_H; x++)
		if (node->child[x] != NULL)
			treenode_prune( node->child[x] );

	if (node->parent != NULL)
		for (x=PEG_A; x<=PEG_H; x++)
			if (node->parent->child[x]==node)
				node->parent->child[x]=NULL;
	
	free(node);
}

int treenode_height( treenode_t *node )
{
	int max_height;
	int height;
	int x;

	max_height = 0;
	
	for (x=PEG_A; x<=PEG_H; x++)
		if (node->child[x] != NULL)
		{
			height=treenode_height( node->child[x] );
			if (height > max_height)
				max_height=height;
		}

	return (max_height + 1);
}

int treenode_level( treenode_t *node )
{
	if (node->parent==NULL)
		return 0;
	else
		return 1 + treenode_level(node->parent);
}

int treenode_size( treenode_t *node )
{
	int count;
	int x;

	count=1;

	for (x=PEG_A; x<=PEG_H; x++)
		if (node->child[x] != NULL)
			count+=treenode_size(node->child[x]);

	return count;
}

peg_label_t treenode_missing_child( treenode_t *node )
{
	peg_label_t missing_child;

	missing_child=PEG_A;
	while (missing_child != PEG_NONE && (node->child[missing_child] != NULL || peg_full(&node->board,missing_child)))
		missing_child++;

	return missing_child;
}

treenode_t *treenode_find_incomplete( treenode_t *node, int desired_level, int cur_level, peg_colour_t root_move_colour )
{
	peg_label_t missing_child;
	treenode_t *ret_node;
	treenode_t *fn2;
	peg_label_t x;

	ret_node=NULL;

	//printf("\n-- treenode_find_incomplete(): desired_level: %d, cur_level: %d\n",desired_level, cur_level);
	
	if (desired_level == cur_level)
	{
		missing_child=treenode_missing_child( node );
		if (missing_child != PEG_NONE)
			ret_node=node;
	}
	else
	{
		x=PEG_A;
		fn2=NULL;
		while ( x != PEG_NONE && fn2 == NULL)
		{
			if (!peg_full( &node->board, x ))
			{
				if (node->child[x] == NULL)
				{
					node->child[x]=treenode_new( node, &node->board );
					add_peg( &node->child[x]->board, x, whos_turn( root_move_colour, treenode_level(node->child[x]) ) );
				}

				fn2=treenode_find_incomplete( node->child[x], desired_level, cur_level+1, root_move_colour );
				ret_node=fn2;
			}

			x++;
		}
	}

	return ret_node;
}

int add_treenode( cpu_t *cpu )
{
	treenode_t *node, *newnode;
	peg_colour_t mc;
	int retval;

	node=treenode_find_incomplete( cpu->root_node, cpu->cur_tree_level, 1, cpu->root_colour );

	if (node==NULL)
		retval=FALSE;
	else
	{
		mc=treenode_missing_child(node);
#ifdef _DEBUG
		if (mc < PEG_A || mc > PEG_H)
		{
			printf("\n** ERROR: add_treenode(): mc is out of range!  mc: %d\n\n",mc);
			exit(1);
		}
#endif
		newnode=treenode_new( node, &node->board );
		node->child[mc]=newnode;
		add_peg( &newnode->board, mc, whos_turn(cpu->root_colour, treenode_level(newnode)) );

		retval=TRUE;
	}

	return retval;
}

// If list is non-null, it will be filled with
// lines made from this move.
// 
int cpu_sendmove( cpu_t *cpu, peg_label_t move )
{
	char buf[100];
	treenode_t *newroot;

	if (cpu->root_node->child[move]==NULL)
		return FALSE;
	else
	{	
		MUTEX_LOCK( &cpu->mutex_message )
		cpu->move_received=TRUE;
		COND_SIGNAL( &cpu->cv_move )

		debug("-- CPU: cpu_sendmove(): mutex_message locked, waiting for signal...........\n");
		COND_WAIT( &cpu->cv_message, &cpu->mutex_message )
		MUTEX_UNLOCK( &cpu->mutex_message )
		debug("-- CPU: cpu_sendmove(): received message signal.\n");
	
		MUTEX_LOCK( &cpu->mutex_tree );
		debug("-- CPU: cpu_sendmove(): mutex_tree locked.\n");

		newroot=cpu->root_node->child[move];
		treenode_detatch( newroot );

		snprintf(buf,100,"-- CPU: cpu_sendmove(): new root detatched, deleting old root (%d nodes).\n",treenode_size( cpu->root_node ));
		debug(buf);

		treenode_prune( cpu->root_node );
		cpu->root_node=newroot;

		snprintf(buf,100,"-- CPU: cpu_sendmove(): new root created, %d nodes.\n",treenode_size( cpu->root_node ));

		if (cpu->root_colour==PC_RED)
			cpu->root_colour=PC_WHITE;
		else
			cpu->root_colour=PC_RED;

		MUTEX_UNLOCK( &cpu->mutex_tree )

		return TRUE;
	}
}

int get_board_rating( board_t *board, peg_colour_t col, peg_colour_t oppcol )
{
	return ( board_complete_rows(board, col) - board_complete_rows(board, oppcol) );
}

double node_rating( treenode_t *node, peg_colour_t col, peg_colour_t oppcol )
{
	double rating, dom_child_rating, r;
	int has_child;
	int x;

	dom_child_rating=0;

	rating=((double)get_board_rating(&node->board, col, oppcol) * SCORE_LINE) - ((double)get_board_rating(&node->board, oppcol, col) * SCORE_OPP_LINE);

	rating *= (1.0 / ((double)treenode_level(node)+1));
	has_child=FALSE;

	if (treenode_level(node) % 2 == 1)
	{
		for (x=PEG_A; x<=PEG_H; x++)
			if (node->child[x] != NULL)
			{
				r=node_rating(node->child[x],col,oppcol);
				if (!has_child)
				{
					has_child=TRUE;
					dom_child_rating=r;
				}
				else if (r < dom_child_rating)
					dom_child_rating=r;
			}
	}
	else
	{
		for (x=PEG_A; x<=PEG_H; x++)
			if (node->child[x] != NULL)
			{
				r=node_rating(node->child[x],col,oppcol);
				if (!has_child)
				{
					has_child=TRUE;
					dom_child_rating=r;
				}
				else if (r > dom_child_rating)
					dom_child_rating=r;
			}
	}

	if (has_child)
		rating += dom_child_rating;

	return rating;
}

treenode_t *best_move( treenode_t *node, peg_colour_t col, peg_colour_t oppcol )
{
	int first;
	int x;
	double r, best_rating;
	treenode_t *best_node;

	first=TRUE;
	best_rating=0;
	best_node=NULL;

	for (x=PEG_A; x<=PEG_H; x++)
		if (node->child[x] != NULL)
		{
			r=node_rating(node->child[x],col,oppcol);
			if (first)
			{
				best_node=node->child[x];
				best_rating=r;
				first=FALSE;
			}
			else if (node_rating(node->child[x],col,oppcol) > best_rating)
			{
				best_node=node->child[x];
				best_rating=r;
			}
		}

	return best_node;
}


int cpu_score( cpu_t *cpu, peg_colour_t col )
{
	return board_complete_rows( &cpu->root_node->board, col );
}


peg_label_t cpu_getmove( cpu_t *cpu )
{
	treenode_t *move_node;
	peg_colour_t col, oppcol;
	int x;
	peg_label_t move;

	move=PEG_NONE;

	col=cpu->root_colour;
	if (col==PC_RED)
		oppcol=PC_WHITE;
	else
		oppcol=PC_RED;
	
	move_node=best_move( cpu->root_node, col, oppcol );

	for (x=PEG_A; x<=PEG_H; x++)
		if (cpu->root_node->child[x]==move_node)
			move=x;

	return move;
}

void thread_cpu_buildtree( void *data )
{
	cpu_t *cpu;
	char buf[100];

	cpu=(cpu_t *)data;

	snprintf( buf,100,"-- CPU: buildtree thread started: thread_id: %d\n",(int)cpu->thread_id );
	debug(buf);
	
	MUTEX_LOCK( &cpu->mutex_move )

	while (TRUE)
	{
		if (cpu->move_received)
		{
			debug("-- CPU: buildthread: move recieved (top of while loop).\n");
			cpu->cur_tree_level--;
			cpu->move_received=FALSE;
		}
		
		if (pthread_mutex_trylock(&cpu->mutex_move)!=EBUSY)
		{
			printf("\n** ERROR: buildthread: mutex_move should be locked!\n\n");
			exit(1);
		}

		debug("-- CPU: buildthread: locking mutex_message\n");
		MUTEX_LOCK( &cpu->mutex_message )
		debug("-- CPU: buildthread: unlocking mutex_message\n");
		MUTEX_UNLOCK( &cpu->mutex_message )
		debug("-- CPU: buildthread: signalling cv_message\n");
		COND_SIGNAL( &cpu->cv_message )

		while (cpu->cur_tree_level <= CPU_TREE_MIN_MOVE)
		{
			MUTEX_LOCK( &cpu->mutex_tree )
			//debug("-- CPU: buildthread: mutex_tree locked.\n");
			
			if (add_treenode( cpu )==FALSE)
			{
				cpu->cur_tree_level++;
				snprintf(buf,100,"-- CPU: completed tree level %d, tree size is %d nodes.\n",cpu->cur_tree_level, treenode_size(cpu->root_node) );
				debug(buf);
			}

			MUTEX_UNLOCK( &cpu->mutex_tree )
			//debug("-- CPU: buildthread: mutex_tree unlocked.\n");
		}

		debug("\n-- CPU: Minimum build complete, unlocking move mutex.\n\n");
		MUTEX_UNLOCK( &cpu->mutex_move )

		while (cpu->cur_tree_level <= CPU_TREE_MAX_BUILD && !cpu->move_received)
		{
			MUTEX_LOCK( &cpu->mutex_tree )
			
			if (add_treenode( cpu )==FALSE)
			{
				cpu->cur_tree_level++;
				snprintf(buf,100,"-- CPU: completed tree level %d\n",cpu->cur_tree_level );
				debug(buf);
			}

			MUTEX_UNLOCK( &cpu->mutex_tree )
		}

		if (cpu->move_received)
		{
			debug("-- CPU: build thread: recieved a move.  Locking mutex_move and restarting build loop.\n");
			MUTEX_LOCK( &cpu->mutex_move );
		}
		else
		{

			debug("\n-- CPU: Max tree building complete. Going to sleep.\n");

			MUTEX_LOCK( &cpu->mutex_move )
			COND_WAIT( &cpu->cv_move, &cpu->mutex_move )
		
			debug("\n-- CPU: buildtree thread awoken, move mutex locked.\n");
		}
	}
}


cpu_t *cpu_new( peg_colour_t first_move )
{
	cpu_t *cpu;
	board_t board;

	board_init( &board );
	
	cpu=(cpu_t *)malloc( sizeof(cpu_t) );
	cpu->root_node=treenode_new( NULL, &board );
	cpu->cur_tree_level=1;

	cpu->root_colour=first_move;
	cpu->move_received=FALSE;

	pthread_mutex_init( &cpu->mutex_message, NULL );
	pthread_cond_init( &cpu->cv_message, NULL );
	
	pthread_mutex_init( &cpu->mutex_tree, NULL );
	
	pthread_mutex_init( &cpu->mutex_move, NULL );
	pthread_cond_init( &cpu->cv_move, NULL );

	MUTEX_LOCK( &cpu->mutex_message )
	pthread_create( &cpu->thread_id, NULL, (threadfunc_t)thread_cpu_buildtree, (void *)cpu );

	COND_WAIT( &cpu->cv_message, &cpu->mutex_message )
	MUTEX_UNLOCK( &cpu->mutex_message )

	
	return cpu;
}

#include "glttt_callbacks.h"
#include "globals.h"
#include "game_settings.h"
#include "glttt.h"
#include "platform/platform.h"

extern global_t globals;

void glttt_callbacks_kb( unsigned char c )
{
	if (globals.game_state==GAMESTATE_IN_GAME || globals.game_state==GAMESTATE_GAME_OVER)
		switch (c)
		{
			case (char)27:
				exit(1);
			case '4':
				globals.rot -= ROTATE_FACTOR;
				break;

			case '6':
				globals.rot += ROTATE_FACTOR;
				break;

			case '8':
				globals.zoom += ZOOM_FACTOR;
				break;

			case '2':
				globals.zoom -= ZOOM_FACTOR;
				if (globals.zoom < MIN_ZOOM)
					globals.zoom=MIN_ZOOM;
				break;

			case 'a': case 'b': case 'c': case 'd':
			case 'e': case 'f': case 'g': case 'h':
				if (globals.human_turn)
					do_turn((int)c - 'a');
				break;
		}
}

void reset_hover( int x, int y )
{
	GLint vp[4];

	glGetIntegerv( GL_VIEWPORT,vp );
	globals.mx_hover=x;
	globals.my_hover=vp[3]-(GLint)y-1;
}

void glttt_callback_motion( int x, int y )
{
	if (globals.repos_active)
	{
		globals.new_rot = 0-((globals.xp-x) * MOUSE_ROT_FACT);
		while (globals.new_rot > 360)
			globals.new_rot -= 360;
		while (globals.new_rot < 0)
			globals.new_rot += 360;
		globals.new_zoom = ((globals.yp-y) * MOUSE_ZOOM_FACT);
	}

	reset_hover(x, y);
}

void glttt_callback_passivemotion( int x, int y )
{
	reset_hover(x, y);
}

void glttt_callback_command_action_start( int xp, int yp )
{
	globals.mx_left_down = globals.mx_hover;
	globals.my_left_down = globals.my_hover;

	switch (globals.game_state)
	{
		case GAMESTATE_NEW_GAME:
		case GAMESTATE_MOVE_FIRST:
			mouse_choose( xp, yp );
			break;

		case GAMESTATE_IN_GAME:
			if (globals.peg_select_hover != PEG_NONE && globals.human_turn)
			{
				globals.peg_select_down = globals.peg_select_hover;
			}
			break;
	}
}

void glttt_callback_command_action_stop( int xp, int yp )
{
	switch (globals.game_state)
	{
		case GAMESTATE_IN_GAME:
			if (globals.peg_select_hover == globals.peg_select_down && globals.peg_select_down != PEG_NONE && globals.human_turn)
			{
				do_turn(globals.peg_select_hover);
			}
			break;
	}

	globals.peg_select_down = PEG_NONE;
}

void glttt_callback_rotate_action_start( int xp, int yp )
{
	switch (globals.game_state)
	{
		case GAMESTATE_IN_GAME: case GAMESTATE_GAME_OVER:
			globals.repos_active=TRUE;
			globals.xp=xp;
			globals.yp=yp;
			globals.new_rot=0;
			globals.new_zoom=0;
			break;
	}
}

void glttt_callback_rotate_action_stop( int xp, int yp )
{
	switch (globals.game_state)
	{
		case GAMESTATE_IN_GAME: case GAMESTATE_GAME_OVER:
			globals.repos_active=FALSE;
			globals.rot += globals.new_rot;
			globals.zoom += globals.new_zoom;
			globals.new_rot=0;
			globals.new_zoom=0;
			break;
	}
}

void glttt_callback_display()
{
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	switch(globals.game_state)
	{
		case GAMESTATE_NEW_GAME:
			draw_new_game();
			break;

		case GAMESTATE_MOVE_FIRST:
			draw_move_first();
			break;

		case GAMESTATE_IN_GAME:
		case GAMESTATE_GAME_OVER:
			draw_game_screen();
			break;
	}

	glttt_platform_display_callback_end();	
}

void glttt_callback_idle()
{
	peg_label_t move;
	char buf[100];
	peg_colour_t cpu_col;
	int sc;
	int t;

	t = glttt_platform_time_in_millis_since_init();
	
	if (globals.game_state==GAMESTATE_IN_GAME)
	{
		if (!globals.human_turn && cpu_ready(globals.cpu) && t > (globals.human_move_time + CPU_WAIT_TO_MOVE))
		{
			if (globals.colour==PC_RED)
				cpu_col=PC_WHITE;
			else
				cpu_col=PC_RED;
		
			inc_move();
			move=cpu_getmove(globals.cpu);
			snprintf(buf,100,"CPU moves to peg %c.",PEGNAME[move]);
			glmsg_add( globals.msg_box, buf, COLOUR_MSG_TEXT );
			cpu_sendmove(globals.cpu,move);

			sc=cpu_score(globals.cpu,cpu_col);
			if (sc > globals.cpu_old)
			{
				snprintf(buf,100,"CPU scores %d rows!",sc-globals.cpu_old);
				glmsg_add( globals.msg_box, buf, COLOUR_MSG_TEXT );
				globals.cpu_old=sc;
				
				//printf("old horiz mask: %x, old vert mask: %x\n",globals.mask_horiz_cpu,globals.mask_vert_cpu);

				add_vert_rows_to_queue( globals.mask_vert_cpu, board_get_vert_mask( &globals.cpu->root_node->board, cpu_col ), cpu_col );
				add_horiz_rows_to_queue( globals.mask_horiz_cpu, board_get_horiz_mask( &globals.cpu->root_node->board, cpu_col ), cpu_col );
			
				globals.mask_horiz_cpu=board_get_horiz_mask( &globals.cpu->root_node->board, cpu_col );
				globals.mask_vert_cpu=board_get_vert_mask( &globals.cpu->root_node->board, cpu_col );
			
				//printf("new horiz mask: %x, new vert mask: %x\n",globals.mask_horiz_cpu,globals.mask_vert_cpu);
			}
			
			globals.human_turn=TRUE;
		}
	}
	
	glttt_platform_request_redraw();
}



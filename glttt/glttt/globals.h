#ifndef __GLTTT_GLOBALS_H__
#define __GLTTT_GLOBALS_H__

#include "cpu.h"
#include "gl_msg.h"

typedef struct
{
	cpu_t *cpu;
	int game_state;
	peg_colour_t colour;
	int human_turn;
	glmsgbox_t *msg_box;
	int cpu_old;
	int hum_old;
	int move_count;
	int human_move_time;

	int mask_vert_cpu;
	int mask_horiz_cpu;
	int mask_vert_hum;
	int mask_horiz_hum;
	
	GLdouble rot;
	GLdouble new_rot;
	GLint zoom;
	GLint new_zoom;
	int repos_active;
	int xp;
	int yp;
	GLint mx;
	GLint my;

	list_t *row_queue;
	
	peg_label_t peg_select;

} global_t;

#endif



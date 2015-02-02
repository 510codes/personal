#ifndef DEFS_H_
#define DEFS_H_

// uncomment this to enable pthread debugging
// #define PTHREAD_DEBUG 1

#include "pthread_debug.h"

#define TRUE 1
#define FALSE 0

//#define _DEBUG 1


#define SCORE_LINE 1.0
#define SCORE_OPP_LINE 0.9

#ifdef ANDROID
#define GLTTT_USE_GLFLOAT
#endif

#ifdef GLTTT_USE_GLFLOAT
#define GLTTT_FP_TYPE GLfloat
#define GLTTT_GLCOLOR3V glColor3df
#define GLTTT_GLTRANSLATE glTranslatef
#define GLTTT_GLGETFPTYPEV glGetFloatv
#else
#define GLTTT_FP_TYPE GLdouble
#define GLTTT_GLCOLOR3V glColor3dv
#define GLTTT_GLTRANSLATE glTranslated
#define GLTTT_GLGETFPTYPEV glGetDoublev
#endif


typedef enum PEG_LABEL
{
	PEG_A=0, PEG_B=1, PEG_C=2, PEG_D=3, PEG_E=4, \
	PEG_F=5, PEG_G=6, PEG_H=7, PEG_NONE=8
} peg_label_t;

typedef enum PEG_COLOUR { PC_RED=1, PC_WHITE=2, PC_NONE=0 } peg_colour_t;

typedef struct
{
	peg_colour_t peg[8][3];
} board_t;


#define CPU_TREE_MAX_BUILD 5
#define CPU_TREE_MIN_MOVE 4



#define GAMESTATE_NEW_GAME 1
#define GAMESTATE_IN_GAME 2
#define GAMESTATE_MOVE_FIRST 3
#define GAMESTATE_GAME_OVER 4



#endif

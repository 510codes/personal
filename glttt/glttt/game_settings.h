#ifndef __GLTTT_GAME_SETTINGS_H__
#define __GLTTT_GAME_SETTINGS_H__

#include "GL/gl.h"

#include "defs.h"

const GLint MIN_ZOOM;
const GLint ZOOM_FACTOR;
const GLdouble ROTATE_FACTOR;
const GLdouble MOUSE_ROT_FACT;
const GLdouble MOUSE_ZOOM_FACT;


const GLint PEG_SIZE;
const GLint PEG_THICK;

const GLdouble PEG_SELECT_DIST;

const int PEG_FLASH_TIMEOUT;

const int CPU_WAIT_TO_MOVE;

const char *PEGNAME;
const char *SIG;
char *VERSION_STRING;

const GLdouble PEG_LETTER_POS[8][2];
								

const GLdouble PEG_POS[8][2];


const peg_label_t PEG_ROW[18][3];

const GLdouble COLOUR_WHITE[3];
const GLdouble COLOUR_RED[3];

GLdouble COLOUR_MSG_BOX[4];
GLdouble COLOUR_MSG_TEXT[3];
GLdouble COLOUR_VERSION_STRING[3];

GLdouble COLOUR_MSG_BACK[4];

GLfloat light_ambient[4];
GLfloat light_diffuse[4];
GLfloat light_specular[4];

GLfloat light_position0[4];
GLfloat light_position1[4];

GLfloat redpeg_ambient[4];
GLfloat redpeg_diffuse[4];
GLfloat redpeg_specular[4];
GLfloat redpeg_shininess[1];

GLfloat redpeg_flash_ambient[4];
GLfloat redpeg_flash_diffuse[4];
GLfloat redpeg_flash_specular[4];
GLfloat redpeg_flash_shininess[1];

GLfloat whitepeg_ambient[4];
GLfloat whitepeg_diffuse[4];
GLfloat whitepeg_specular[4];
GLfloat whitepeg_shininess[1];

GLfloat whitepeg_flash_ambient[4];
GLfloat whitepeg_flash_diffuse[4];
GLfloat whitepeg_flash_specular[4];
GLfloat whitepeg_flash_shininess[1];

GLfloat board_ambient[4];
GLfloat board_diffuse[4];
GLfloat board_specular[4];
GLfloat board_shininess[1];

GLfloat peg_normal_ambient[4];
GLfloat peg_normal_diffuse[4];
GLfloat peg_normal_specular[4];
GLfloat peg_normal_shininess[1];

GLfloat peg_select_ambient[4];
GLfloat peg_select_diffuse[4];
GLfloat peg_select_specular[4];
GLfloat peg_select_shininess[1];

GLfloat letter_ambient[4];
GLfloat letter_diffuse[4];
GLfloat letter_specular[4];
GLfloat letter_shininess[1];


#endif


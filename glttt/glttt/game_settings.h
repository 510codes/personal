#ifndef __GLTTT_GAME_SETTINGS_H__
#define __GLTTT_GAME_SETTINGS_H__

#include "GL/gl.h"

#include "defs.h"

extern const GLint MIN_ZOOM;
extern const GLint ZOOM_FACTOR;
extern const GLdouble ROTATE_FACTOR;
extern const GLdouble MOUSE_ROT_FACT;
extern const GLdouble MOUSE_ZOOM_FACT;


extern const GLint PEG_SIZE;
extern const GLint PEG_THICK;

extern const GLdouble PEG_SELECT_DIST;

extern const int PEG_FLASH_TIMEOUT;

extern const int CPU_WAIT_TO_MOVE;

extern const char *PEGNAME;
extern const char *SIG;
extern char *VERSION_STRING;

extern const GLdouble PEG_LETTER_POS[8][2];
								

extern const GLdouble PEG_POS[8][2];


extern const peg_label_t PEG_ROW[18][3];

extern const GLdouble COLOUR_WHITE[3];
extern const GLdouble COLOUR_RED[3];

extern GLdouble COLOUR_MSG_BOX[4];
extern GLdouble COLOUR_MSG_TEXT[3];
extern GLdouble COLOUR_VERSION_STRING[3];

extern GLdouble COLOUR_MSG_BACK[4];

extern GLfloat light_ambient[4];
extern GLfloat light_diffuse[4];
extern GLfloat light_specular[4];

extern GLfloat light_position0[4];
extern GLfloat light_position1[4];

extern GLfloat redpeg_ambient[4];
extern GLfloat redpeg_diffuse[4];
extern GLfloat redpeg_specular[4];
extern GLfloat redpeg_shininess[1];

extern GLfloat redpeg_flash_ambient[4];
extern GLfloat redpeg_flash_diffuse[4];
extern GLfloat redpeg_flash_specular[4];
extern GLfloat redpeg_flash_shininess[1];

extern GLfloat whitepeg_ambient[4];
extern GLfloat whitepeg_diffuse[4];
extern GLfloat whitepeg_specular[4];
extern GLfloat whitepeg_shininess[1];

extern GLfloat whitepeg_flash_ambient[4];
extern GLfloat whitepeg_flash_diffuse[4];
extern GLfloat whitepeg_flash_specular[4];
extern GLfloat whitepeg_flash_shininess[1];

extern GLfloat board_ambient[4];
extern GLfloat board_diffuse[4];
extern GLfloat board_specular[4];
extern GLfloat board_shininess[1];

extern GLfloat peg_normal_ambient[4];
extern GLfloat peg_normal_diffuse[4];
extern GLfloat peg_normal_specular[4];
extern GLfloat peg_normal_shininess[1];

extern GLfloat peg_select_ambient[4];
extern GLfloat peg_select_diffuse[4];
extern GLfloat peg_select_specular[4];
extern GLfloat peg_select_shininess[1];

extern GLfloat peg_hover_ambient[4];
extern GLfloat peg_hover_diffuse[4];
extern GLfloat peg_hover_specular[4];
extern GLfloat peg_hover_shininess[1];

extern GLfloat peg_hover_select_ambient[4];
extern GLfloat peg_hover_select_diffuse[4];
extern GLfloat peg_hover_select_specular[4];
extern GLfloat peg_hover_select_shininess[1];

extern GLfloat letter_ambient[4];
extern GLfloat letter_diffuse[4];
extern GLfloat letter_specular[4];
extern GLfloat letter_shininess[1];


#endif


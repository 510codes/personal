#include "game_settings.h"
#include "game_constants.h"
#include "defs.h"

const GLint MIN_ZOOM = GLTTT_MIN_ZOOM_DEFAULT;
const GLint ZOOM_FACTOR = GLTTT_ZOOM_FACTOR_DEFAULT;
const GLTTT_FP_TYPE ROTATE_FACTOR = GLTTT_ROTATE_FACTOR_DEFAULT;
const GLTTT_FP_TYPE MOUSE_ROT_FACT = GLTTT_MOUSE_ROT_FACT_DEFAULT;
const GLTTT_FP_TYPE MOUSE_ZOOM_FACT = GLTTT_MOUSE_ZOOM_FACT_DEFAULT;

const GLint PEG_SIZE = GLTTT_PEG_SIZE_DEFAULT;
const GLint PEG_THICK = GLTTT_PEG_THICK_DEFAULT;

const GLTTT_FP_TYPE PEG_SELECT_DIST = GLTTT_PEG_SELECT_DIST_DEFAULT;

const int PEG_FLASH_TIMEOUT = GLTTT_PEG_FLASH_TIMEOUT_DEFAULT;

const int CPU_WAIT_TO_MOVE = GLTTT_CPU_WAIT_TO_MOVE_DEFAULT;

const char *PEGNAME = GLTTT_PEGNAME_DEFAULT;
const char *SIG = GLTTT_SIG_DEFAULT;
char *VERSION_STRING = GLTTT_VERSION_STRING_DEFAULT;

const GLTTT_FP_TYPE PEG_LETTER_POS[][2] = GLTTT_PEG_LETTER_POS_DEFAULT;
								

const GLTTT_FP_TYPE PEG_POS[][2] = GLTTT_PEG_POS_DEFAULT;


const peg_label_t PEG_ROW[][3] = GLTTT_PEG_ROW_DEFAULT;

const GLTTT_FP_TYPE COLOUR_WHITE[] = GLTTT_COLOUR_WHITE_DEFAULT;
const GLTTT_FP_TYPE COLOUR_RED[] = GLTTT_COLOUR_RED_DEFAULT;

GLTTT_FP_TYPE COLOUR_MSG_BOX[] = GLTTT_COLOUR_MSG_BOX_DEFAULT;
GLTTT_FP_TYPE COLOUR_MSG_TEXT[] = GLTTT_COLOUR_MSG_TEXT_DEFAULT;
GLTTT_FP_TYPE COLOUR_VERSION_STRING[] = GLTTT_COLOUR_VERSION_STRING_DEFAULT;

GLTTT_FP_TYPE COLOUR_MSG_BACK[] = GLTTT_COLOUR_MSG_BACK_DEFAULT;

GLfloat light_ambient[] = GLTTT_light_ambient_DEFAULT;
GLfloat light_diffuse[] = GLTTT_light_diffuse_DEFAULT;
GLfloat light_specular[] = GLTTT_light_specular_DEFAULT;

GLfloat light_position0[] = GLTTT_light_position0_DEFAULT;
GLfloat light_position1[] = GLTTT_light_position1_DEFAULT;

GLfloat redpeg_ambient[] = GLTTT_redpeg_ambient_DEFAULT;
GLfloat redpeg_diffuse[] = GLTTT_redpeg_diffuse_DEFAULT;
GLfloat redpeg_specular[] = GLTTT_redpeg_specular_DEFAULT;
GLfloat redpeg_shininess[] = GLTTT_redpeg_shininess_DEFAULT;

GLfloat redpeg_flash_ambient[] = GLTTT_redpeg_flash_ambient_DEFAULT;
GLfloat redpeg_flash_diffuse[] = GLTTT_redpeg_flash_diffuse_DEFAULT;
GLfloat redpeg_flash_specular[] = GLTTT_redpeg_flash_specular_DEFAULT;
GLfloat redpeg_flash_shininess[] = GLTTT_redpeg_flash_shininess_DEFAULT;

GLfloat whitepeg_ambient[] = GLTTT_whitepeg_ambient_DEFAULT;
GLfloat whitepeg_diffuse[] = GLTTT_whitepeg_diffuse_DEFAULT;
GLfloat whitepeg_specular[] = GLTTT_whitepeg_specular_DEFAULT;
GLfloat whitepeg_shininess[] = GLTTT_whitepeg_shininess_DEFAULT;

GLfloat whitepeg_flash_ambient[] = GLTTT_whitepeg_flash_ambient_DEFAULT;
GLfloat whitepeg_flash_diffuse[] = GLTTT_whitepeg_flash_diffuse_DEFAULT;
GLfloat whitepeg_flash_specular[] = GLTTT_whitepeg_flash_specular_DEFAULT;
GLfloat whitepeg_flash_shininess[] = GLTTT_whitepeg_flash_shininess_DEFAULT;

GLfloat board_ambient[] = GLTTT_board_ambient_DEFAULT;
GLfloat board_diffuse[] = GLTTT_board_diffuse_DEFAULT;
GLfloat board_specular[] = GLTTT_board_specular_DEFAULT;
GLfloat board_shininess[] = GLTTT_board_shininess_DEFAULT;

GLfloat peg_normal_ambient[] = GLTTT_peg_normal_ambient_DEFAULT;
GLfloat peg_normal_diffuse[] = GLTTT_peg_normal_diffuse_DEFAULT;
GLfloat peg_normal_specular[] = GLTTT_peg_normal_specular_DEFAULT;
GLfloat peg_normal_shininess[] = GLTTT_peg_normal_shininess_DEFAULT;

GLfloat peg_select_ambient[] = GLTTT_peg_select_ambient_DEFAULT;
GLfloat peg_select_diffuse[] = GLTTT_peg_select_diffuse_DEFAULT;
GLfloat peg_select_specular[] = GLTTT_peg_select_specular_DEFAULT;
GLfloat peg_select_shininess[] = GLTTT_peg_select_shininess_DEFAULT;

GLfloat peg_hover_ambient[] = GLTTT_peg_hover_ambient_DEFAULT;
GLfloat peg_hover_diffuse[] = GLTTT_peg_hover_diffuse_DEFAULT;
GLfloat peg_hover_specular[] = GLTTT_peg_hover_specular_DEFAULT;
GLfloat peg_hover_shininess[] = GLTTT_peg_hover_shininess_DEFAULT;

GLfloat peg_hover_select_ambient[] = GLTTT_peg_hover_select_ambient_DEFAULT;
GLfloat peg_hover_select_diffuse[] = GLTTT_peg_hover_select_diffuse_DEFAULT;
GLfloat peg_hover_select_specular[] = GLTTT_peg_hover_select_specular_DEFAULT;
GLfloat peg_hover_select_shininess[] = GLTTT_peg_hover_select_shininess_DEFAULT;

GLfloat letter_ambient[] = GLTTT_letter_ambient_DEFAULT;
GLfloat letter_diffuse[] = GLTTT_letter_diffuse_DEFAULT;
GLfloat letter_specular[] = GLTTT_letter_specular_DEFAULT;
GLfloat letter_shininess[] = GLTTT_letter_shininess_DEFAULT;



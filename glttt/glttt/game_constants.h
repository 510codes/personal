#ifndef __GLTTT_GAME_CONSTANTS_H__
#define __GLTTT_GAME_CONSTANTS_H__

#define GLTTT_MIN_ZOOM_DEFAULT 50
#define GLTTT_ZOOM_FACTOR_DEFAULT 10
#define GLTTT_ROTATE_FACTOR_DEFAULT 2.0
#define GLTTT_MOUSE_ROT_FACT_DEFAULT 0.5
#define GLTTT_MOUSE_ZOOM_FACT_DEFAULT 1.5

#define GLTTT_PEG_SIZE_DEFAULT 11
#define GLTTT_PEG_THICK_DEFAULT 1

#define GLTTT_PEG_SELECT_DIST_DEFAULT 100.0

#define GLTTT_PEG_FLASH_TIMEOUT_DEFAULT 1250

#define GLTTT_CPU_WAIT_TO_MOVE_DEFAULT 2000
//#define GLTTT_CPU_WAIT_TO_MOVE_DEFAULT 0

#define GLTTT_PEGNAME_DEFAULT "ABCDEFGH"
#define GLTTT_SIG_DEFAULT "Chris Riley - 2003"
#define GLTTT_VERSION_STRING_DEFAULT "GLTicTacToe (20030622): C. Riley '03"

#define GLTTT_PEG_LETTER_POS_DEFAULT {	{-60,70},{-10,70},{40,70,}, \
					{-50,10},{40, 10}, \
					{-70,-70},{-10,-70},{50,-70} }
								

#define GLTTT_PEG_POS_DEFAULT {	{-50,50},{0,50},{50,50}, \
				{-25,0},{25,0}, \
				{-50,-50},{0,-50},{50,-50} }


#define GLTTT_PEG_ROW_DEFAULT {	{PEG_F, PEG_G, PEG_H}, \
					{PEG_F, PEG_D, PEG_B}, \
					{PEG_G, PEG_E, PEG_C}, \
					{PEG_A, PEG_B, PEG_C}, \
					{PEG_A, PEG_D, PEG_G}, \
					{PEG_B, PEG_E, PEG_H} }

#define GLTTT_COLOUR_WHITE_DEFAULT { 0.9, 0.9, 0.9 }
#define GLTTT_COLOUR_RED_DEFAULT { 1.0, 0.0, 0.0 }

#define GLTTT_COLOUR_MSG_BOX_DEFAULT { 0.3,0.1,0.1,0.5 }
#define GLTTT_COLOUR_MSG_TEXT_DEFAULT { 0.9, 0.9, 0.9 }
#define GLTTT_COLOUR_VERSION_STRING_DEFAULT { 0.8, 1.0, 0.8 }

#define GLTTT_COLOUR_MSG_BACK_DEFAULT { 0.3,0.1,0.1,0.5 }

#define GLTTT_light_ambient_DEFAULT {0.5, 0.5, 0.5, 1.0}
#define GLTTT_light_diffuse_DEFAULT {0.5, 0.5, 0.5, 1.0}
#define GLTTT_light_specular_DEFAULT {0.5, 0.5, 0.5, 1.0}

//#define GLTTT_light_position0_DEFAULT {-100,100,-100, 0.0}
#define GLTTT_light_position0_DEFAULT {10,5000,10, 0.0}
#define GLTTT_light_position1_DEFAULT {100,100,100, 0.0}

#define GLTTT_redpeg_ambient_DEFAULT {0.9, 0.2, 0.2, 1.0}
#define GLTTT_redpeg_diffuse_DEFAULT {0.4, 0.2, 0.1, 1.0}
#define GLTTT_redpeg_specular_DEFAULT {0.5, 0.5, 0.5, 1.0}
#define GLTTT_redpeg_shininess_DEFAULT {0.30}

#define GLTTT_redpeg_flash_ambient_DEFAULT {0.4, 0.1, 0.1, 1.0}
#define GLTTT_redpeg_flash_diffuse_DEFAULT {0.4, 0.1, 0.1, 1.0}
#define GLTTT_redpeg_flash_specular_DEFAULT {0.4, 0.1, 0.1, 1.0}
#define GLTTT_redpeg_flash_shininess_DEFAULT {0.30}

#define GLTTT_whitepeg_ambient_DEFAULT {0.7, 0.7, 0.7, 1.0}
#define GLTTT_whitepeg_diffuse_DEFAULT {0.7, 0.7, 0.7, 1.0}
#define GLTTT_whitepeg_specular_DEFAULT {0.2, 0.2, 0.2, 1.0}
#define GLTTT_whitepeg_shininess_DEFAULT {0.30}

#define GLTTT_whitepeg_flash_ambient_DEFAULT {0.5, 0.5, 0.7, 1.0}
#define GLTTT_whitepeg_flash_diffuse_DEFAULT {0.5, 0.5, 0.7, 1.0}
#define GLTTT_whitepeg_flash_specular_DEFAULT {0.2, 0.2, 0.3, 1.0}
#define GLTTT_whitepeg_flash_shininess_DEFAULT {0.30}

//#define GLTTT_board_ambient_DEFAULT {0.2, 0.9, 0.2, 0.7}
#define GLTTT_board_ambient_DEFAULT {0.1, 0.2, 0.5, 1.0}
#define GLTTT_board_diffuse_DEFAULT {0.1, 0.1, 0.2, 1.0}
#define GLTTT_board_specular_DEFAULT {0.1, 0.1, 0.2, 1.0}
#define GLTTT_board_shininess_DEFAULT {0.0}

//#define GLTTT_peg_normal_ambient_DEFAULT {1.0, 1.0, 0.0, 0.3}
#define GLTTT_peg_normal_ambient_DEFAULT {0.55, 0.55, 0.48, 0.3}
#define GLTTT_peg_normal_diffuse_DEFAULT {0.7, 0.7, 0.7, 0.7}
#define GLTTT_peg_normal_specular_DEFAULT {0.8, 0.8, 0.8, 0.7}
#define GLTTT_peg_normal_shininess_DEFAULT {0.90}

#define GLTTT_peg_hover_ambient_DEFAULT {0.1, 0.2, 0.9, 0.3}
#define GLTTT_peg_hover_diffuse_DEFAULT {0.4, 0.2, 0.1, 0.7}
#define GLTTT_peg_hover_specular_DEFAULT {0.5, 0.5, 0.5, 0.7}
#define GLTTT_peg_hover_shininess_DEFAULT {0.70}

#define GLTTT_peg_select_ambient_DEFAULT {0.6, 0.6, 0.3, 0.3}
#define GLTTT_peg_select_diffuse_DEFAULT {0.7, 0.6, 0.05, 0.7}
#define GLTTT_peg_select_specular_DEFAULT {0.625, 0.7, 0.1, 0.7}
#define GLTTT_peg_select_shininess_DEFAULT {0.70}

#define GLTTT_peg_hover_select_ambient_DEFAULT {0.7, 0.7, 0.4, 0.3}
#define GLTTT_peg_hover_select_diffuse_DEFAULT {0.8, 0.7, 0.1, 0.7}
#define GLTTT_peg_hover_select_specular_DEFAULT {0.85, 0.9, 0.2, 0.7}
#define GLTTT_peg_hover_select_shininess_DEFAULT {0.70}

#define GLTTT_letter_ambient_DEFAULT {0.4, 0.7, 0.4, 1.0}
#define GLTTT_letter_diffuse_DEFAULT {0.4, 0.7, 0.4, 1.0}
#define GLTTT_letter_specular_DEFAULT {0.4, 0.7, 0.4, 1.0}
#define GLTTT_letter_shininess_DEFAULT {0.10}



#endif


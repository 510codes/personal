#include "..\platform.h"
#include "callbacks.h"

#include <GL/gl.h>
#include <GL/glut.h>

void glttt_platform_init( int *argc, char** argv,
	int windowW, int windowH, const char* windowTitle )
{
	glutInit( argc, argv );
	glutInitWindowSize( windowW, windowH );
	glutInitDisplayMode( GLUT_RGB | GLUT_DOUBLE | GLUT_DEPTH );
	glutCreateWindow( windowTitle );

	glutDisplayFunc(displayfunc);
	glutKeyboardFunc(kbfunc);
	glutIdleFunc(idlefunc);
	glutMouseFunc(mousefunc);
	glutMotionFunc(motionfunc);
	glutPassiveMotionFunc(passivemotionfunc);
}

void glttt_platform_run()
{
	glutMainLoop();
}

void glttt_platform_draw_char( char c )
{
	glutStrokeCharacter( GLUT_STROKE_ROMAN, c );
}

void glttt_platform_draw_solid_sphere( int r )
{
	glutSolidSphere( r, r * 2, r * 2 );
}

void glttt_platform_request_redraw()
{
	glutPostRedisplay();
}

int glttt_platform_time_in_millis_since_init()
{
	return glutGet(GLUT_ELAPSED_TIME);
}

int glttt_platform_get_window_width()
{
	return glutGet(GLUT_WINDOW_WIDTH);
}

int glttt_platform_get_window_height()
{
	return glutGet(GLUT_WINDOW_HEIGHT);
}

void glttt_platform_display_callback_end()
{
	glutSwapBuffers();
}


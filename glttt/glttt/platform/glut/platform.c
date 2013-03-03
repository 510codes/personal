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


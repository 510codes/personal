#ifndef __PLATFORM_H__
#define __PLATFORM_H__


/*
 *
 * each platform must define these functions
 *
 */


void glttt_platform_init( int *argc, char** argv,
	int windowW, int windowH, const char* windowTitle );

void glttt_platform_run();

void glttt_platform_draw_char( char c );

void glttt_platform_draw_solid_sphere( int r );

void glttt_platform_request_redraw();

int glttt_platform_time_in_millis_since_init();

int glttt_platform_get_window_width();

int glttt_platform_get_window_height();

void glttt_platform_display_callback_end();


#endif


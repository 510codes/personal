# Makefile for Tic-Tac-Toe
#
# Platform independent targets
# Platform-specific Makefiles should include this file.
#
# Chris Riley, April 2003
#

COMMON_HEADERS= defs.h pthread_debug.h

# all: ttt glttt
all: glttt

list.o: list.c list.h

board.o: board.h board.c $(COMMON_HEADERS)

cpu.o: cpu.h list.h cpu.c list.o board.o $(COMMON_HEADERS)

ttt.o: ttt.c cpu.o board.o $(COMMON_HEADERS)

gl_msg.o: gl_msg.c gl_msg.h globals.h $(COMMON_HEADERS)

glttt_callbacks.o: glttt_callbacks.c glttt_callbacks.h glttt.h gl_msg.h globals.h $(COMMON_HEADERS)

platform/glut/platform.o: platform/glut/platform.c platform/platform.h $(COMMON_HEADERS)

platform/glut/callbacks.o: platform/glut/callbacks.c platform/glut/callbacks.h $(COMMON_HEADERS)

game_settings.o: game_settings.c game_settings.h game_constants.h $(COMMON_HEADERS)

glttt.o: glttt.c glttt.h globals.h platform/glut/platform.o glttt_callbacks.o game_settings.o cpu.o list.o board.o gl_msg.o $(COMMON_HEADERS)

$(GLTTT_LIB): glttt.o glttt_callbacks.o game_settings.o platform/glut/platform.o platform/glut/callbacks.o board.o cpu.o gl_msg.o list.o
	$(LD_LIB) $(LD_LIB_FLAGS) $@ $?

ttt: ttt.o board.o cpu.o
	$(CC) $(CFLAGS) -o $@ $? $(LDFLAGS)


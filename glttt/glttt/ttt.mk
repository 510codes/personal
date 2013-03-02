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

gl_msg.o: gl_msg.c gl_msg.h $(COMMON_HEADERS)

glttt.o: glttt.c cpu.o list.o board.o gl_msg.o $(COMMON_HEADERS)

glttt: glttt.o board.o cpu.o gl_msg.o list.o
	$(CC) $(CFLAGS) -o $@ $? $(LDFLAGS)

ttt: ttt.o board.o cpu.o
	$(CC) $(CFLAGS) -o $@ $? $(LDFLAGS)


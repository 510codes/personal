GL Tic-Tac-Toe :: 20030622
--------------------------

(C) 2003 by Chris Riley




Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.



Basically, do whatever you want with it, except claim you wrote it.  If it makes you rich, you have to buy me a beer ;)


This is a very early release, there are likely problems.

If you find bugs, problems, stuff missing, or if you just find it amusing, send comments!


Chris Riley
cr01ab@cosc.brocku.ca




Windows Instructions
--------------------

Please make sure that pthreadGC.dll and glut32.dll are in the same directory as glttt.exe (this should not be a problem, as these files are all in the zip file together).  Run the file "glttt.exe" to play the game.



SGI Instructions
----------------

Nothing special is required.  The SGI release was compiled on SGI IRIX64 6.5 (sandcastle.cosc.brocku.ca).  You must have OpenGL and GLUT installed to run it.  The binary is "glttt".



FreeBSD-5 Instructions
----------------------

Nothing special is required.  The FreeBSD release was compiled on FreeBSD 5.0-RELEASE i386.  You must have OpenGL and GLUT installed to run it.  The binary is "glttt".







COMPILING
---------


Windows
-------

To build the program on Windows, you must have MinGW (2.0.0 or higher) installed, as well as the OpenGL/GLUT headers and libraries.

GLUT setup instructions:

- download and extract glut-3.7.6-bin.zip, from http://user.xmission.com/~nate/glut.html, for example
- in the directory where the extracted files are, create a subdirectory called GL
- move the file 'glut.h' into this subdirectory
- from the MinGW windows command prompt, type the following (or else do the equivalents)
-- set C_INCLUDE_PATH=<basedir>\glut-3.7.6-bin
-- set LIBRARY_PATH=<basedir>\glut-3.7.6-bin
-- set PATH=%PATH%;<basedir>\glut-3.7.6-bin

note: replace <basedir> with wherever you downloaded/extratced glut, eg c:\downloads


Also, this program uses the Pthreads-Win32 package (http://sources.redhat.com/pthreads-win32/) for pthreads on Windows.  The applicable headers and libraries are distributed with the source.

MinGW is not necessarily _required_ to build the program, it's just how I do it.  You can probably use the Microsoft or the Intel compiler tools, for example.  I have not done this yet, perhaps in the future.


FreeBSD, IRIX
-------------

Straightforward, both use whatever default compiler the system uses.  This can be changed by editing the appropriate Makefile.


All
---

Make targets:  all, glttt, ttt, clean

all: build glttt and ttt

ttt: build command line game

glttt: build opengl game

clean: do a clean

Each platform-specific Makefile imports platform-independent targets from "ttt.mk".  This file should not be modified.  See a platform specific Makefile (eg. Makefile-BSD or Makefile-SGI) for hints on how to create a Makefile for some unsupported (as of yet) system, like Linux for example.



--- END ---

#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=gfortran
AS=as

# Macros
CND_PLATFORM=GNU-Linux
CND_DLIB_EXT=so
CND_CONF=Debug
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/sources/bidi/brrule.o \
	${OBJECTDIR}/sources/bidi/brtable.o \
	${OBJECTDIR}/sources/bidi/brtest.o \
	${OBJECTDIR}/sources/bidi/brutils.o \
	${OBJECTDIR}/sources/clientthread.o \
	${OBJECTDIR}/sources/config.o \
	${OBJECTDIR}/sources/controls.o \
	${OBJECTDIR}/sources/device.o \
	${OBJECTDIR}/sources/devicethread.o \
	${OBJECTDIR}/sources/font_drawer.o \
	${OBJECTDIR}/sources/font_util.o \
	${OBJECTDIR}/sources/fonts/DengXian16.o \
	${OBJECTDIR}/sources/fonts/DengXian24.o \
	${OBJECTDIR}/sources/fonts/F08_1.o \
	${OBJECTDIR}/sources/fonts/F08_ASCII.o \
	${OBJECTDIR}/sources/fonts/F10S_1.o \
	${OBJECTDIR}/sources/fonts/F10S_ASCII.o \
	${OBJECTDIR}/sources/fonts/F10_1.o \
	${OBJECTDIR}/sources/fonts/F10_ASCII.o \
	${OBJECTDIR}/sources/fonts/F13B_1.o \
	${OBJECTDIR}/sources/fonts/F13B_ASCII.o \
	${OBJECTDIR}/sources/fonts/F13HB_1.o \
	${OBJECTDIR}/sources/fonts/F13HB_ASCII.o \
	${OBJECTDIR}/sources/fonts/F13H_1.o \
	${OBJECTDIR}/sources/fonts/F13H_ASCII.o \
	${OBJECTDIR}/sources/fonts/F13_1.o \
	${OBJECTDIR}/sources/fonts/F13_ASCII.o \
	${OBJECTDIR}/sources/fonts/F16B_1.o \
	${OBJECTDIR}/sources/fonts/F16B_ASCII.o \
	${OBJECTDIR}/sources/fonts/F16_1.o \
	${OBJECTDIR}/sources/fonts/F16_1HK.o \
	${OBJECTDIR}/sources/fonts/F16_ASCII.o \
	${OBJECTDIR}/sources/fonts/F16_HK.o \
	${OBJECTDIR}/sources/fonts/F24B_1.o \
	${OBJECTDIR}/sources/fonts/F24B_ASCII.o \
	${OBJECTDIR}/sources/fonts/F24_1.o \
	${OBJECTDIR}/sources/fonts/F24_ASCII.o \
	${OBJECTDIR}/sources/fonts/F32B_1.o \
	${OBJECTDIR}/sources/fonts/F32B_ASCII.o \
	${OBJECTDIR}/sources/fonts/F32_1.o \
	${OBJECTDIR}/sources/fonts/F32_ASCII.o \
	${OBJECTDIR}/sources/fonts/F4x6.o \
	${OBJECTDIR}/sources/fonts/F6x8.o \
	${OBJECTDIR}/sources/fonts/F8x10_ASCII.o \
	${OBJECTDIR}/sources/fonts/F8x12_ASCII.o \
	${OBJECTDIR}/sources/fonts/F8x13_1.o \
	${OBJECTDIR}/sources/fonts/F8x13_ASCII.o \
	${OBJECTDIR}/sources/fonts/F8x15B_1.o \
	${OBJECTDIR}/sources/fonts/F8x15B_ASCII.o \
	${OBJECTDIR}/sources/fonts/F8x16.o \
	${OBJECTDIR}/sources/fonts/F8x8.o \
	${OBJECTDIR}/sources/fonts/FComic18B_1.o \
	${OBJECTDIR}/sources/fonts/FComic18B_ASCII.o \
	${OBJECTDIR}/sources/fonts/FComic24B_1.o \
	${OBJECTDIR}/sources/fonts/FComic24B_ASCII.o \
	${OBJECTDIR}/sources/fonts/FD24x32.o \
	${OBJECTDIR}/sources/fonts/FD32.o \
	${OBJECTDIR}/sources/fonts/FD36x48.o \
	${OBJECTDIR}/sources/fonts/FD48.o \
	${OBJECTDIR}/sources/fonts/FD48x64.o \
	${OBJECTDIR}/sources/fonts/FD60x80.o \
	${OBJECTDIR}/sources/fonts/FD64.o \
	${OBJECTDIR}/sources/fonts/FD80.o \
	${OBJECTDIR}/sources/fonts/FONT_Arabic.o \
	${OBJECTDIR}/sources/fonts/FontDigitsArial72.o \
	${OBJECTDIR}/sources/fonts/FontSymbol.o \
	${OBJECTDIR}/sources/gklservice.o \
	${OBJECTDIR}/sources/mutex.o \
	${OBJECTDIR}/sources/packet.o \
	${OBJECTDIR}/sources/render.o \
	${OBJECTDIR}/sources/thread.o


# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/gklservice

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/gklservice: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.cc} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/gklservice ${OBJECTFILES} ${LDLIBSOPTIONS} `pkg-config --cflags gtk+-3.0` `pkg-config --libs gtk+-3.0`

${OBJECTDIR}/sources/bidi/brrule.o: sources/bidi/brrule.c
	${MKDIR} -p ${OBJECTDIR}/sources/bidi
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/bidi/brrule.o sources/bidi/brrule.c

${OBJECTDIR}/sources/bidi/brtable.o: sources/bidi/brtable.c
	${MKDIR} -p ${OBJECTDIR}/sources/bidi
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/bidi/brtable.o sources/bidi/brtable.c

${OBJECTDIR}/sources/bidi/brtest.o: sources/bidi/brtest.c
	${MKDIR} -p ${OBJECTDIR}/sources/bidi
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/bidi/brtest.o sources/bidi/brtest.c

${OBJECTDIR}/sources/bidi/brutils.o: sources/bidi/brutils.c
	${MKDIR} -p ${OBJECTDIR}/sources/bidi
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/bidi/brutils.o sources/bidi/brutils.c

${OBJECTDIR}/sources/clientthread.o: sources/clientthread.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/clientthread.o sources/clientthread.cpp

${OBJECTDIR}/sources/config.o: sources/config.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/config.o sources/config.cpp

${OBJECTDIR}/sources/controls.o: sources/controls.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/controls.o sources/controls.cpp

${OBJECTDIR}/sources/device.o: sources/device.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/device.o sources/device.cpp

${OBJECTDIR}/sources/devicethread.o: sources/devicethread.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/devicethread.o sources/devicethread.cpp

${OBJECTDIR}/sources/font_drawer.o: sources/font_drawer.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/font_drawer.o sources/font_drawer.cpp

${OBJECTDIR}/sources/font_util.o: sources/font_util.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/font_util.o sources/font_util.cpp

${OBJECTDIR}/sources/fonts/DengXian16.o: sources/fonts/DengXian16.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/DengXian16.o sources/fonts/DengXian16.c

${OBJECTDIR}/sources/fonts/DengXian24.o: sources/fonts/DengXian24.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/DengXian24.o sources/fonts/DengXian24.c

${OBJECTDIR}/sources/fonts/F08_1.o: sources/fonts/F08_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F08_1.o sources/fonts/F08_1.c

${OBJECTDIR}/sources/fonts/F08_ASCII.o: sources/fonts/F08_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F08_ASCII.o sources/fonts/F08_ASCII.c

${OBJECTDIR}/sources/fonts/F10S_1.o: sources/fonts/F10S_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F10S_1.o sources/fonts/F10S_1.c

${OBJECTDIR}/sources/fonts/F10S_ASCII.o: sources/fonts/F10S_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F10S_ASCII.o sources/fonts/F10S_ASCII.c

${OBJECTDIR}/sources/fonts/F10_1.o: sources/fonts/F10_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F10_1.o sources/fonts/F10_1.c

${OBJECTDIR}/sources/fonts/F10_ASCII.o: sources/fonts/F10_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F10_ASCII.o sources/fonts/F10_ASCII.c

${OBJECTDIR}/sources/fonts/F13B_1.o: sources/fonts/F13B_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13B_1.o sources/fonts/F13B_1.c

${OBJECTDIR}/sources/fonts/F13B_ASCII.o: sources/fonts/F13B_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13B_ASCII.o sources/fonts/F13B_ASCII.c

${OBJECTDIR}/sources/fonts/F13HB_1.o: sources/fonts/F13HB_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13HB_1.o sources/fonts/F13HB_1.c

${OBJECTDIR}/sources/fonts/F13HB_ASCII.o: sources/fonts/F13HB_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13HB_ASCII.o sources/fonts/F13HB_ASCII.c

${OBJECTDIR}/sources/fonts/F13H_1.o: sources/fonts/F13H_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13H_1.o sources/fonts/F13H_1.c

${OBJECTDIR}/sources/fonts/F13H_ASCII.o: sources/fonts/F13H_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13H_ASCII.o sources/fonts/F13H_ASCII.c

${OBJECTDIR}/sources/fonts/F13_1.o: sources/fonts/F13_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13_1.o sources/fonts/F13_1.c

${OBJECTDIR}/sources/fonts/F13_ASCII.o: sources/fonts/F13_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13_ASCII.o sources/fonts/F13_ASCII.c

${OBJECTDIR}/sources/fonts/F16B_1.o: sources/fonts/F16B_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F16B_1.o sources/fonts/F16B_1.c

${OBJECTDIR}/sources/fonts/F16B_ASCII.o: sources/fonts/F16B_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F16B_ASCII.o sources/fonts/F16B_ASCII.c

${OBJECTDIR}/sources/fonts/F16_1.o: sources/fonts/F16_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F16_1.o sources/fonts/F16_1.c

${OBJECTDIR}/sources/fonts/F16_1HK.o: sources/fonts/F16_1HK.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F16_1HK.o sources/fonts/F16_1HK.c

${OBJECTDIR}/sources/fonts/F16_ASCII.o: sources/fonts/F16_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F16_ASCII.o sources/fonts/F16_ASCII.c

${OBJECTDIR}/sources/fonts/F16_HK.o: sources/fonts/F16_HK.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F16_HK.o sources/fonts/F16_HK.c

${OBJECTDIR}/sources/fonts/F24B_1.o: sources/fonts/F24B_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F24B_1.o sources/fonts/F24B_1.c

${OBJECTDIR}/sources/fonts/F24B_ASCII.o: sources/fonts/F24B_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F24B_ASCII.o sources/fonts/F24B_ASCII.c

${OBJECTDIR}/sources/fonts/F24_1.o: sources/fonts/F24_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F24_1.o sources/fonts/F24_1.c

${OBJECTDIR}/sources/fonts/F24_ASCII.o: sources/fonts/F24_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F24_ASCII.o sources/fonts/F24_ASCII.c

${OBJECTDIR}/sources/fonts/F32B_1.o: sources/fonts/F32B_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F32B_1.o sources/fonts/F32B_1.c

${OBJECTDIR}/sources/fonts/F32B_ASCII.o: sources/fonts/F32B_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F32B_ASCII.o sources/fonts/F32B_ASCII.c

${OBJECTDIR}/sources/fonts/F32_1.o: sources/fonts/F32_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F32_1.o sources/fonts/F32_1.c

${OBJECTDIR}/sources/fonts/F32_ASCII.o: sources/fonts/F32_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F32_ASCII.o sources/fonts/F32_ASCII.c

${OBJECTDIR}/sources/fonts/F4x6.o: sources/fonts/F4x6.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F4x6.o sources/fonts/F4x6.c

${OBJECTDIR}/sources/fonts/F6x8.o: sources/fonts/F6x8.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F6x8.o sources/fonts/F6x8.c

${OBJECTDIR}/sources/fonts/F8x10_ASCII.o: sources/fonts/F8x10_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x10_ASCII.o sources/fonts/F8x10_ASCII.c

${OBJECTDIR}/sources/fonts/F8x12_ASCII.o: sources/fonts/F8x12_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x12_ASCII.o sources/fonts/F8x12_ASCII.c

${OBJECTDIR}/sources/fonts/F8x13_1.o: sources/fonts/F8x13_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x13_1.o sources/fonts/F8x13_1.c

${OBJECTDIR}/sources/fonts/F8x13_ASCII.o: sources/fonts/F8x13_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x13_ASCII.o sources/fonts/F8x13_ASCII.c

${OBJECTDIR}/sources/fonts/F8x15B_1.o: sources/fonts/F8x15B_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x15B_1.o sources/fonts/F8x15B_1.c

${OBJECTDIR}/sources/fonts/F8x15B_ASCII.o: sources/fonts/F8x15B_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x15B_ASCII.o sources/fonts/F8x15B_ASCII.c

${OBJECTDIR}/sources/fonts/F8x16.o: sources/fonts/F8x16.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x16.o sources/fonts/F8x16.c

${OBJECTDIR}/sources/fonts/F8x8.o: sources/fonts/F8x8.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x8.o sources/fonts/F8x8.c

${OBJECTDIR}/sources/fonts/FComic18B_1.o: sources/fonts/FComic18B_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FComic18B_1.o sources/fonts/FComic18B_1.c

${OBJECTDIR}/sources/fonts/FComic18B_ASCII.o: sources/fonts/FComic18B_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FComic18B_ASCII.o sources/fonts/FComic18B_ASCII.c

${OBJECTDIR}/sources/fonts/FComic24B_1.o: sources/fonts/FComic24B_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FComic24B_1.o sources/fonts/FComic24B_1.c

${OBJECTDIR}/sources/fonts/FComic24B_ASCII.o: sources/fonts/FComic24B_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FComic24B_ASCII.o sources/fonts/FComic24B_ASCII.c

${OBJECTDIR}/sources/fonts/FD24x32.o: sources/fonts/FD24x32.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD24x32.o sources/fonts/FD24x32.c

${OBJECTDIR}/sources/fonts/FD32.o: sources/fonts/FD32.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD32.o sources/fonts/FD32.c

${OBJECTDIR}/sources/fonts/FD36x48.o: sources/fonts/FD36x48.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD36x48.o sources/fonts/FD36x48.c

${OBJECTDIR}/sources/fonts/FD48.o: sources/fonts/FD48.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD48.o sources/fonts/FD48.c

${OBJECTDIR}/sources/fonts/FD48x64.o: sources/fonts/FD48x64.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD48x64.o sources/fonts/FD48x64.c

${OBJECTDIR}/sources/fonts/FD60x80.o: sources/fonts/FD60x80.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD60x80.o sources/fonts/FD60x80.c

${OBJECTDIR}/sources/fonts/FD64.o: sources/fonts/FD64.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD64.o sources/fonts/FD64.c

${OBJECTDIR}/sources/fonts/FD80.o: sources/fonts/FD80.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD80.o sources/fonts/FD80.c

${OBJECTDIR}/sources/fonts/FONT_Arabic.o: sources/fonts/FONT_Arabic.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FONT_Arabic.o sources/fonts/FONT_Arabic.c

${OBJECTDIR}/sources/fonts/FontDigitsArial72.o: sources/fonts/FontDigitsArial72.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FontDigitsArial72.o sources/fonts/FontDigitsArial72.c

${OBJECTDIR}/sources/fonts/FontSymbol.o: sources/fonts/FontSymbol.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FontSymbol.o sources/fonts/FontSymbol.c

${OBJECTDIR}/sources/gklservice.o: sources/gklservice.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/gklservice.o sources/gklservice.cpp

${OBJECTDIR}/sources/mutex.o: sources/mutex.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/mutex.o sources/mutex.cpp

${OBJECTDIR}/sources/packet.o: sources/packet.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/packet.o sources/packet.cpp

${OBJECTDIR}/sources/render.o: sources/render.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/render.o sources/render.cpp

${OBJECTDIR}/sources/thread.o: sources/thread.cpp
	${MKDIR} -p ${OBJECTDIR}/sources
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/usr/include/at-spi-2.0 -I/usr/include/at-spi2-atk/2.0 -I/usr/include/atk-1.0 -I/usr/include/cairo -I/usr/include/dbus-1.0 -I/usr/include/freetype2 -I/usr/include/gdk-pixbuf-2.0 -I/usr/include/gio-unix-2.0/ -I/usr/include/glib-2.0 -I/usr/include/gtk-3.0 -I/usr/include/harfbuzz -I/usr/include/libpng12 -I/usr/include/pango-1.0 -I/usr/include/pixman-1 -I/usr/lib/arm-linux-gnueabihf/dbus-1.0/include -I/usr/lib/arm-linux-gnueabihf/glib-2.0/include -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/thread.o sources/thread.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc

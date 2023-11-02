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
CND_CONF=Release
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
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
	${OBJECTDIR}/sources/fonts/FontSymbol.o


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
	${LINK.c} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/gklservice ${OBJECTFILES} ${LDLIBSOPTIONS}

${OBJECTDIR}/sources/fonts/DengXian16.o: sources/fonts/DengXian16.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/DengXian16.o sources/fonts/DengXian16.c

${OBJECTDIR}/sources/fonts/DengXian24.o: sources/fonts/DengXian24.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/DengXian24.o sources/fonts/DengXian24.c

${OBJECTDIR}/sources/fonts/F08_1.o: sources/fonts/F08_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F08_1.o sources/fonts/F08_1.c

${OBJECTDIR}/sources/fonts/F08_ASCII.o: sources/fonts/F08_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F08_ASCII.o sources/fonts/F08_ASCII.c

${OBJECTDIR}/sources/fonts/F10S_1.o: sources/fonts/F10S_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F10S_1.o sources/fonts/F10S_1.c

${OBJECTDIR}/sources/fonts/F10S_ASCII.o: sources/fonts/F10S_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F10S_ASCII.o sources/fonts/F10S_ASCII.c

${OBJECTDIR}/sources/fonts/F10_1.o: sources/fonts/F10_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F10_1.o sources/fonts/F10_1.c

${OBJECTDIR}/sources/fonts/F10_ASCII.o: sources/fonts/F10_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F10_ASCII.o sources/fonts/F10_ASCII.c

${OBJECTDIR}/sources/fonts/F13B_1.o: sources/fonts/F13B_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13B_1.o sources/fonts/F13B_1.c

${OBJECTDIR}/sources/fonts/F13B_ASCII.o: sources/fonts/F13B_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13B_ASCII.o sources/fonts/F13B_ASCII.c

${OBJECTDIR}/sources/fonts/F13HB_1.o: sources/fonts/F13HB_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13HB_1.o sources/fonts/F13HB_1.c

${OBJECTDIR}/sources/fonts/F13HB_ASCII.o: sources/fonts/F13HB_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13HB_ASCII.o sources/fonts/F13HB_ASCII.c

${OBJECTDIR}/sources/fonts/F13H_1.o: sources/fonts/F13H_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13H_1.o sources/fonts/F13H_1.c

${OBJECTDIR}/sources/fonts/F13H_ASCII.o: sources/fonts/F13H_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13H_ASCII.o sources/fonts/F13H_ASCII.c

${OBJECTDIR}/sources/fonts/F13_1.o: sources/fonts/F13_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13_1.o sources/fonts/F13_1.c

${OBJECTDIR}/sources/fonts/F13_ASCII.o: sources/fonts/F13_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F13_ASCII.o sources/fonts/F13_ASCII.c

${OBJECTDIR}/sources/fonts/F16B_1.o: sources/fonts/F16B_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F16B_1.o sources/fonts/F16B_1.c

${OBJECTDIR}/sources/fonts/F16B_ASCII.o: sources/fonts/F16B_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F16B_ASCII.o sources/fonts/F16B_ASCII.c

${OBJECTDIR}/sources/fonts/F16_1.o: sources/fonts/F16_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F16_1.o sources/fonts/F16_1.c

${OBJECTDIR}/sources/fonts/F16_1HK.o: sources/fonts/F16_1HK.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F16_1HK.o sources/fonts/F16_1HK.c

${OBJECTDIR}/sources/fonts/F16_ASCII.o: sources/fonts/F16_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F16_ASCII.o sources/fonts/F16_ASCII.c

${OBJECTDIR}/sources/fonts/F16_HK.o: sources/fonts/F16_HK.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F16_HK.o sources/fonts/F16_HK.c

${OBJECTDIR}/sources/fonts/F24B_1.o: sources/fonts/F24B_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F24B_1.o sources/fonts/F24B_1.c

${OBJECTDIR}/sources/fonts/F24B_ASCII.o: sources/fonts/F24B_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F24B_ASCII.o sources/fonts/F24B_ASCII.c

${OBJECTDIR}/sources/fonts/F24_1.o: sources/fonts/F24_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F24_1.o sources/fonts/F24_1.c

${OBJECTDIR}/sources/fonts/F24_ASCII.o: sources/fonts/F24_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F24_ASCII.o sources/fonts/F24_ASCII.c

${OBJECTDIR}/sources/fonts/F32B_1.o: sources/fonts/F32B_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F32B_1.o sources/fonts/F32B_1.c

${OBJECTDIR}/sources/fonts/F32B_ASCII.o: sources/fonts/F32B_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F32B_ASCII.o sources/fonts/F32B_ASCII.c

${OBJECTDIR}/sources/fonts/F32_1.o: sources/fonts/F32_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F32_1.o sources/fonts/F32_1.c

${OBJECTDIR}/sources/fonts/F32_ASCII.o: sources/fonts/F32_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F32_ASCII.o sources/fonts/F32_ASCII.c

${OBJECTDIR}/sources/fonts/F4x6.o: sources/fonts/F4x6.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F4x6.o sources/fonts/F4x6.c

${OBJECTDIR}/sources/fonts/F6x8.o: sources/fonts/F6x8.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F6x8.o sources/fonts/F6x8.c

${OBJECTDIR}/sources/fonts/F8x10_ASCII.o: sources/fonts/F8x10_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x10_ASCII.o sources/fonts/F8x10_ASCII.c

${OBJECTDIR}/sources/fonts/F8x12_ASCII.o: sources/fonts/F8x12_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x12_ASCII.o sources/fonts/F8x12_ASCII.c

${OBJECTDIR}/sources/fonts/F8x13_1.o: sources/fonts/F8x13_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x13_1.o sources/fonts/F8x13_1.c

${OBJECTDIR}/sources/fonts/F8x13_ASCII.o: sources/fonts/F8x13_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x13_ASCII.o sources/fonts/F8x13_ASCII.c

${OBJECTDIR}/sources/fonts/F8x15B_1.o: sources/fonts/F8x15B_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x15B_1.o sources/fonts/F8x15B_1.c

${OBJECTDIR}/sources/fonts/F8x15B_ASCII.o: sources/fonts/F8x15B_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x15B_ASCII.o sources/fonts/F8x15B_ASCII.c

${OBJECTDIR}/sources/fonts/F8x16.o: sources/fonts/F8x16.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x16.o sources/fonts/F8x16.c

${OBJECTDIR}/sources/fonts/F8x8.o: sources/fonts/F8x8.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/F8x8.o sources/fonts/F8x8.c

${OBJECTDIR}/sources/fonts/FComic18B_1.o: sources/fonts/FComic18B_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FComic18B_1.o sources/fonts/FComic18B_1.c

${OBJECTDIR}/sources/fonts/FComic18B_ASCII.o: sources/fonts/FComic18B_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FComic18B_ASCII.o sources/fonts/FComic18B_ASCII.c

${OBJECTDIR}/sources/fonts/FComic24B_1.o: sources/fonts/FComic24B_1.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FComic24B_1.o sources/fonts/FComic24B_1.c

${OBJECTDIR}/sources/fonts/FComic24B_ASCII.o: sources/fonts/FComic24B_ASCII.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FComic24B_ASCII.o sources/fonts/FComic24B_ASCII.c

${OBJECTDIR}/sources/fonts/FD24x32.o: sources/fonts/FD24x32.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD24x32.o sources/fonts/FD24x32.c

${OBJECTDIR}/sources/fonts/FD32.o: sources/fonts/FD32.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD32.o sources/fonts/FD32.c

${OBJECTDIR}/sources/fonts/FD36x48.o: sources/fonts/FD36x48.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD36x48.o sources/fonts/FD36x48.c

${OBJECTDIR}/sources/fonts/FD48.o: sources/fonts/FD48.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD48.o sources/fonts/FD48.c

${OBJECTDIR}/sources/fonts/FD48x64.o: sources/fonts/FD48x64.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD48x64.o sources/fonts/FD48x64.c

${OBJECTDIR}/sources/fonts/FD60x80.o: sources/fonts/FD60x80.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD60x80.o sources/fonts/FD60x80.c

${OBJECTDIR}/sources/fonts/FD64.o: sources/fonts/FD64.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD64.o sources/fonts/FD64.c

${OBJECTDIR}/sources/fonts/FD80.o: sources/fonts/FD80.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FD80.o sources/fonts/FD80.c

${OBJECTDIR}/sources/fonts/FONT_Arabic.o: sources/fonts/FONT_Arabic.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FONT_Arabic.o sources/fonts/FONT_Arabic.c

${OBJECTDIR}/sources/fonts/FontDigitsArial72.o: sources/fonts/FontDigitsArial72.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FontDigitsArial72.o sources/fonts/FontDigitsArial72.c

${OBJECTDIR}/sources/fonts/FontSymbol.o: sources/fonts/FontSymbol.c
	${MKDIR} -p ${OBJECTDIR}/sources/fonts
	${RM} "$@.d"
	$(COMPILE.c) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/sources/fonts/FontSymbol.o sources/fonts/FontSymbol.c

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

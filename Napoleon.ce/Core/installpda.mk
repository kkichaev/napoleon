include ../make.vars
include make.vars

PROGRAM=NplInstall

SOURCE=install

RESOURCE=install

MANIFEST=NplInstall/Install.manifest

LIBS=
ADD_INCLUDE=C:\Program Files (x86)\Windows Mobile 6 SDK\Activesync\inc
ADD_LIB=C:\Program Files (x86)\Windows Mobile 6 SDK\Activesync\Lib
MS_LIBS=rapi.lib

VPATH:=NplInstall

all: $(OUT_DIR)/$(PROGRAM).exe
pch: $(INT_DIR)/stdafx.obj

include ../make.pc

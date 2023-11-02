include ../make.vars
include make.vars

CONSOLE=1

PROGRAM=MkUpdate

SOURCE=AES C32 Key MkUpdate

RESOURCE=MkUpdate

ifdef DYNAMIC_RT
LIBS=Lib.pc.rt
else
LIBS=Lib.pc
endif

ADD_INCLUDE=Lib.pc/ZLib

VPATH:=MkUpdate/Core Include MkUpdate/Resource

all: $(OUT_DIR)/$(PROGRAM).exe
pch: $(INT_DIR)/stdafx.obj

include ../make.pc

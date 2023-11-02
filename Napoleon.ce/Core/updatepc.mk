include ../make.vars
include make.vars

PROGRAM=GRUpdate

SOURCE=AES C32 Key update updpacket

RESOURCE=update

ifdef DYNAMIC_RT
LIBS=Lib.pc.rt
else
LIBS=Lib.pc
endif

ADD_INCLUDE=GRServer/Include;Lib.pc/ZLib;Lib.pc/Include

VPATH:=GRServer/Server/Core Include GRServer/GRUpdate

all: $(OUT_DIR)/$(PROGRAM).exe
pch: $(INT_DIR)/stdafx.obj

include ../make.pc

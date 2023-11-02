include ../make.vars
include make.vars

PROGRAM=1CPlugin

SOURCE=1CPlugin dllmain
RESOURCE=1CPlugin
DEF=1CPlugin.def

LIBS=Lib.pc

ADD_INCLUDE=GRServer/Plugins/1c;GRServer/Include;Lib.pc/Include;Lib.pc/ZLib;Napoleon.sql/Include;
#c:/Program Files (x86)/Visual Leak Detector/Include;
#ADD_LIB:=c:\Program Files (x86)\Visual Leak Detector\lib;$(ADD_LIB)

VPATH:=GRServer/Plugins/1c/1CPlugin Include

all: $(OUT_DIR)/$(DEF) $(OUT_DIR)/$(PROGRAM).dll
pch: $(INT_DIR)/stdafx.obj

$(OUT_DIR)/$(DEF) : $(DEF)
	@cp "$+" "$@"
	@rm -f $(OUT_DIR)/$(PROGRAM).dll

$(OUT_DIR)/$(PROGRAM).dll: MS_LIBS+=Ws2_32.lib

include ../make.pc

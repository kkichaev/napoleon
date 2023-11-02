include ../make.vars
include make.vars

PROGRAM=OleDB

SOURCE=Config DataSource FieldBinder FolderReader OleDB OleReader OleSelector OleWriter QuerySource dllmain
RESOURCE=OleDB
DEF=OleDB.def

LIBS=Lib.pc

ADD_INCLUDE=Include;Lib.pc/Include;GRServer/Include;

VPATH:=GRServer/Plugins/OleDB/Core

all: $(OUT_DIR)/$(DEF) $(OUT_DIR)/$(PROGRAM).dll
pch: $(INT_DIR)/stdafx.obj

#$(OUT_DIR)/$(PROGRAM).dll: $(OUT_DIR)/$(DEF)
$(OUT_DIR)/$(DEF) : $(DEF)
	@cp "$+" "$@"
	@rm -f $(OUT_DIR)/$(PROGRAM).dll

$(OUT_DIR)/$(PROGRAM).dll: MS_LIBS+=Ws2_32.lib

include ../make.pc

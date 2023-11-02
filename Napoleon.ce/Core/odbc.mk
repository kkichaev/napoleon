include ../make.vars
include make.vars

PROGRAM=ODBC$(X64_ADD)

SOURCE=Config CostReader DataSource FieldBinder FolderReader ObjSource ODBC Reader Selector Writer QuerySource dllmain
RESOURCE=ODBC

ifdef X64
else
DEF=ODBC.def
endif

ifdef DYNAMIC_RT
LIBS=Lib.pc.rt$(X64_ADD)
else
LIBS=Lib.pc$(X64_ADD)
endif

ADD_INCLUDE=Include;Lib.pc/Include;GRServer/Include;

VPATH:=GRServer/Plugins/ODBC/Core

all: $(OUT_DIR)/$(DEF) $(OUT_DIR)/$(PROGRAM).dll
pch: $(INT_DIR)/stdafx.obj

#$(OUT_DIR)/$(PROGRAM).dll: $(OUT_DIR)/$(DEF)
$(OUT_DIR)/$(DEF) : $(DEF)
	@cp "$+" "$@"
	@rm -f $(OUT_DIR)/$(PROGRAM).dll

$(OUT_DIR)/$(PROGRAM).dll: MS_LIBS+=Ws2_32.lib

include ../make.pc

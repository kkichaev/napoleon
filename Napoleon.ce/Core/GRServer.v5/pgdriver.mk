include ../../make.vars
include ../make.vars

PROGRAM=PGDriver$(X64_ADD)

SOURCE=binders config dllmain postgre query sqtable table
RESOURCE=

ifdef X64
DEF=postgre.def
endif

ifdef DYNAMIC_RT
LIBS=Lib.pc.rt$(X64_ADD)
else
LIBS=Lib.pc$(X64_ADD)
endif

ADD_INCLUDE=../Include;Lib.pc/Include;Include;C:/Program Files/PostgreSQL/15/include

VPATH:=Plugins/PostgreSQL/Core

all: $(OUT_DIR)/$(DEF) $(OUT_DIR)/$(PROGRAM).dll
pch: $(INT_DIR)/stdafx.obj

#$(OUT_DIR)/$(PROGRAM).dll: $(OUT_DIR)/$(DEF)
$(OUT_DIR)/$(DEF) : $(DEF)
	@cp "$+" "$@"
	@rm -f $(OUT_DIR)/$(PROGRAM).dll

 
$(OUT_DIR)/$(PROGRAM).dll: MS_LIBS+=Ws2_32.lib C:/Works/Napoleon/Core/GRServer.v5/Plugins/PostgreSQL/References/libpq.lib

include ../../make.pc

include ../make.vars
include make.vars

PROGRAM=Napoleon1c

SOURCE=Napoleon1c SODispatch dllmain $(PROGRAM)_i
RESOURCE=Napoleon1c
DEF=Napoleon1c.def

LIBS=Lib.pc

ADD_INCLUDE=GRServer/Plugins/1c;GRServer/Include;Lib.pc/Include;Lib.pc/ZLib;Napoleon.sql/Include;c:/Program Files (x86)/Visual Leak Detector/Include;
ADD_LIB:=c:\Program Files (x86)\Visual Leak Detector\lib;$(ADD_LIB)

VPATH:=GRServer/Plugins/1c/Napoleon1c Include

all: $(OUT_DIR)/$(DEF) $(OUT_DIR)/$(PROGRAM).dll
pch: $(INT_DIR)/stdafx.obj
midl: $(PROGRAM)_i.c $(INT_DIR)/$(PROGRAM).tlb

#$(OUT_DIR)/$(PROGRAM).dll: $(OUT_DIR)/$(DEF)
$(OUT_DIR)/$(DEF) : $(DEF)
	@cp "$+" "$@"
	@rm -f $(OUT_DIR)/$(PROGRAM).dll

$(PROGRAM)_i.c $(INT_DIR)/$(PROGRAM).tlb:$(PROGRAM).idl
	@midl /dlldata "$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(dir $+))dlldata.c" /proxy "$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(dir $+))$(PROGRAM)_p.c" /h "$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(dir $+))$(PROGRAM)_i.h" /iid "$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(dir $+))$(PROGRAM)_i.c" /tlb "$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(INT_DIR))/$(PROGRAM).tlb" $(subst $(CYG_DRIVE),$(WIN_DRIVE),$+)

$(OUT_DIR)/$(PROGRAM).dll: MS_LIBS+=Ws2_32.lib

include ../make.pc

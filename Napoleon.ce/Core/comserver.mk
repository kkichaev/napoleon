include ../make.vars
include make.vars

PROGRAM=ComGRServer$(X64_ADD)

SOURCE=BinaryField Collection ComGRServer Field Key ObjCol Object Server dllmain $(PROGRAM)_i
RESOURCE=ComGRServer
DEF=ComGRServer.def

LIBS=Lib.pc$(X64_ADD)

ADD_INCLUDE=Include;Lib.pc/Include;GRServer/Include;

VPATH:=GRServer/ComServer/Source Include

all: $(OUT_DIR)/$(DEF) $(OUT_DIR)/$(PROGRAM).dll
pch: $(INT_DIR)/stdafx.obj
midl: $(PROGRAM)_i.c $(INT_DIR)/ComGRServer.tlb

#$(OUT_DIR)/$(PROGRAM).dll: $(OUT_DIR)/$(DEF)
$(OUT_DIR)/$(DEF) : $(DEF)
	@cp "$+" "$@"
	@rm -f $(OUT_DIR)/$(PROGRAM).dll

$(PROGRAM)_i.c $(INT_DIR)/ComGRServer.tlb:ComGRServer.idl
	@midl /dlldata "$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(dir $+))dlldata.c" /proxy "$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(dir $+))$(PROGRAM)_p.c" /h "$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(dir $+))$(PROGRAM)_i.h" /iid "$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(dir $+))$(PROGRAM)_i.c" /tlb "$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(INT_DIR))/ComGRServer.tlb" $(subst $(CYG_DRIVE),$(WIN_DRIVE),$+)

#ComGRServer$(X64_ADD).idl: ComGRServer.idl
#	@cp "$+" "$@"

$(OUT_DIR)/$(PROGRAM).dll: MS_LIBS+=Ws2_32.lib

include ../make.pc

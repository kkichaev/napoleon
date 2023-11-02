include ../../make.vars
include ../make.vars
include make.vars

PROGRAM=NplDrv
NO_PCH=1

SOURCE=Drv
RESOURCE=
DEF=Drv/Drv.def


VPATH:=Drv $(VPATH)

all: $(OUT_DIR)/$(PROGRAM).dll
pch: $(INT_DIR)/stdafx.obj

include ../../make.ce

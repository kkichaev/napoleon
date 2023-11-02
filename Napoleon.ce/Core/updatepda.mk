include ../make.vars
include make.vars

PROGRAM=NplUpdate

SOURCE=App C32 State States UpdateConfig Util AES NplUpdate Alert Key Install

RESOURCE=NplUpdate

LIBS=Lib
MS_LIBS=toolhelp.lib

ADD_INCLUDE=Lib/Core;Include;Lib/Zlib

VPATH:=NplUpdate/Core NplUpdate/Resource Include $(VPATH)

all: $(OUT_DIR)/$(PROGRAM).exe
pch: $(INT_DIR)/stdafx.obj

include ../make.ce


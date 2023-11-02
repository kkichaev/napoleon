.PHONY: clean

include ../make.vars
include make.vars

PROGRAM=BTWC

SOURCE=BTWC

RESOURCE=BTWC

LIBS=

MS_LIBS=BtSdkCE50.lib

DEF=NPrinter/BTWC.def

ADD_INCLUDE=../Include;c:\Program Files (x86)\WIDCOMM\WIDCOMM BTW-CE SDK\Sdk\Inc
ADD_LIB=c:\Program Files (x86)\WIDCOMM\WIDCOMM BTW-CE SDK\Sdk\Lib\ArmRel;

VPATH:=NPrinter Include

all: $(OUT_DIR)/$(PROGRAM).dll
pch: $(INT_DIR)/stdafx.obj

include ../make.ce


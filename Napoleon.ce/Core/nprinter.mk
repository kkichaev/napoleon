.PHONY: clean

include ../make.vars
include make.vars
include apdk.vars

PROGRAM=NPrinter

SOURCE=BTConn NPrinter IStreamable NForm NLoader SaxXML $(SOURCE_APDK)

RESOURCE=NPrinter

#DEBUG_RELEASE=1

MS_LIBS=bthutil.lib ole32.lib

DEF=NPrinter/NPrinter.def

#ADD_INCLUDE=../Include

VPATH:=NPrinter Include NPrinter/NForm NPrinter/apdk

all: $(OUT_DIR)/$(PROGRAM).dll
pch: $(INT_DIR)/stdafx.obj

include ../make.ce


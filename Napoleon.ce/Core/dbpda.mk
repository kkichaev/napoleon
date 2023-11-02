include ../make.vars
include make.vars

PROGRAM=NplDB

SOURCE=sqlitedll sqlite3

RESOURCE=NplDB

LIBS=

DEF=Lib/NplDB/NplDB.def

VPATH:=Lib/NplDB

all: $(OUT_DIR)/$(PROGRAM).dll
pch: $(INT_DIR)/stdafx.obj

include ../make.ce


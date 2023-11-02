include ../../make.vars
include ../make.vars
include make.vars

PROGRAM=NapoleonApps

ifdef APPS_OLD
SOURCE=Apps FindPort Location Ril Time DeepIATHook
CAB_ADD_STR="DestDir=NplDrv.dll:CE2"
else
SOURCE=Apps GPS GSM Location Ril Timer DeepIATHook
endif

RESOURCE=
DEF=

LIBS=Lib

ADD_INCLUDE=../Lib/Core

VPATH:=Apps $(VPATH)

all: $(OUT_DIR)/$(PROGRAM).dll
pch: $(INT_DIR)/stdafx.obj

include ../../make.ce

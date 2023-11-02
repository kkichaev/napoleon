include ../../make.vars
include ../make.vars
include make.vars

PROGRAM=NapoleonSetup

SOURCE=ini setup PrefCtr Preference


ifdef NPL_PDA_SETUP_SOURCE
SOURCE += $(NPL_PDA_SETUP_SOURCE)
endif   


RESOURCE=Setup

LIBS=Lib

DEF=Setup/setup.def

ADD_INCLUDE=../Include;../Lib/ZLib

VPATH:=Setup Include Pda/Core

all: $(OUT_DIR)/$(PROGRAM).dll
pch: $(INT_DIR)/stdafx.obj

include ../../make.ce


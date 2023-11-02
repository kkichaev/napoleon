.PHONY: clean

include ../make.vars
include make.vars
include apdk.vars

VPATH:=NPrinter/apdk

PROGRAM=NPrinter

NO_PCH=1
#DEBUG_RELEASE=1

all: $(foreach fileName,$(SOURCE_APDK),$(INT_DIR)/$(fileName).obj)

include ../make.ce


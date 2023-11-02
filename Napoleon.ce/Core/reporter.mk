include ../make.vars
include make.vars

PROGRAM=Reporter$(X64_ADD)

SOURCE=Config PyObjList PyServer PythonObject PyCom Reporter dllmain
RESOURCE=Reporter

ifdef VS14
	ifdef PY_39
		PYTHON_PATH=C:/Works/Python3.9
	else
		PYTHON_PATH=C:/Works/Python3.8
	endif
ifdef X64
	ADD_LIB=$(PYTHON_PATH)/PCBuild/amd64
else
	DEF=Reporter.def
	ADD_LIB=$(PYTHON_PATH)/PCBuild/win32
endif
else
ifdef VS12
	PYTHON_PATH=D:/Works/Python-2.7.14
else
	PYTHON_PATH=D:/Works/Python-2.7.3
endif
ifdef X64
	ADD_LIB=$(PYTHON_PATH)/PCBuild/amd64
else
	DEF=Reporter.def
	ADD_LIB=$(PYTHON_PATH)/PCBuild
endif
endif

ifdef USE_CURL
	SOURCE+=PyCurl
endif


DYNAMIC_RT=1

LIBS=Lib.pc.rt$(X64_ADD)

ADD_INCLUDE=Include;Lib.pc/Include;GRServer/Include;$(PYTHON_PATH)/include;$(PYTHON_PATH)/PC

ifdef VS14
	ifdef PY_39
		VPATH:=GRServer/Plugins/Reporter3.9/Core
	else
		VPATH:=GRServer/Plugins/Reporter3.8/Core
	endif
else
VPATH:=GRServer/Plugins/Reporter/Core
endif

all: $(OUT_DIR)/$(DEF) $(OUT_DIR)/$(PROGRAM).dll
pch: $(INT_DIR)/stdafx.obj

#$(OUT_DIR)/$(PROGRAM).dll: $(OUT_DIR)/$(DEF)
$(OUT_DIR)/$(DEF) : $(DEF)
	@cp "$+" "$@"
	@rm -f $(OUT_DIR)/$(PROGRAM).dll

$(OUT_DIR)/$(PROGRAM).dll: MS_LIBS+=Ws2_32.lib Psapi.lib

include ../make.pc

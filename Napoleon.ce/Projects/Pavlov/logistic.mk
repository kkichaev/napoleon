include ../../make.vars

ifeq ($(findstring SQL_TABLES, $(FEATURES)),)
FEATURES += SQL_TABLES
endif

SOURCE_LIB=BaseForm BaseDialog ListForm NumInput Password PicWindow Progress PropDialog SQLFolderForm SQLSearch SumLabel TopApp
SOURCE=$(SOURCE_LIB) About App Cost DocList Main MainFrame MainForm Net Partners PrefData PrfDlg Scan WhDoc
RESOURCE=About Main Version

PROGRAM=NapoleonLogistic
SETUP_PROGRAM=NapoleonLogistic
CAB_BIN:=NplDB.dll $(CAB_BIN)
CAB_SOURCE:=$(OUT_DIR)/$(PROGRAM).exe $(CAB_SOURCE)

LIBS=Lib NplDB

ADD_INCLUDE=../../$(PCORE)/Include;../../$(PCORE)/Lib/Core
VPATH:=Logistic/Core Logistic/Resource ../../$(PCORE)/Lib/Modules


all: $(OUT_DIR)/$(PROGRAM).exe
pch: $(INT_DIR)/stdafx.obj

cab: $(OUT_DIR)/$(SETUP_PROGRAM).cab

include ../../make.ce

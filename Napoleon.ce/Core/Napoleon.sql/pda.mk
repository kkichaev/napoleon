include ../../make.vars
include ../make.vars
include make.vars

ifndef PROGRAM
PROGRAM=Napoleon
endif
SETUP_PROGRAM=NapoleonSetup
SETUP_DLL=NapoleonSetup.dll

CAB_PROG_NAME=$(PROGRAM)

CAB_BIN:=NplDB.dll NplUpdate.exe $(CAB_BIN)
CAB_SOURCE:=$(OUT_DIR)/$(PROGRAM).exe $(OUT_DIR)/NapoleonSetup.dll $(CAB_SOURCE)

SOURCE=About AddDoc BalanceRcv Contacts DeliveryImpl DocList DocList_ci DocListRmv DocType FileType FldOrgs InitDoc InitDocC Invoice LoadSales \
  Main MainFrame Module ModuleNet \
  NetworkPrf NplConfig NumInput OpenDelivery OpenInvoice OpenOrgDocs OpenOrgList OpenPrice OrdPcd OrderCreate OrderEvents OrderHandler OrderImpl OrderImplCtr \
  OrderSend OrgDocs OrgFuncs OrgList Password PhoneDlg PhotoFolder PictButton PrfDlg PrfPrice PriceBase PriceForm PropDialog Qty QtyData QtySales RegHash \
  MRcvPrice SendHooks SAnchor SetQty Sync TopApp UpdateConfig UpdPref WriteLog \
  BaseDialog BaseForm ListForm PrefCtr Preference Progress RADrawer SearchCtrl SQLFolderForm SumLabel  SQLSearch PicWindow 


RESOURCE=About MainMenu NapoleonRes Price Qty Version

ifneq ($(findstring SHOW_OFF_TAKE, $(FEATURES)),)
SOURCE+=OffTakeCoef
endif

ifneq ($(findstring VISIT_DOC, $(FEATURES)),)
SOURCE+=Visit OpenVisit
endif

ifneq ($(findstring GPS_POS, $(FEATURES)),)
SOURCE+=GPSPrf GPSArchive
endif

ifneq ($(findstring RCV_MESSAGE, $(FEATURES)),)
SOURCE+=MsgList
endif

ifneq ($(findstring VAN_SELLING, $(FEATURES)),)
RESOURCE+=Van
SOURCE+=VanDetail
else ifeq ($(findstring PAY_DELAY, $(FEATURES)),)
RESOURCE+=Detail
SOURCE+=OrderDetail
else
RESOURCE+=PDetail
SOURCE+=OrdPDetail
endif

ifneq ($(findstring PROXY_DOC, $(FEATURES)),)
SOURCE+=Proxy
endif

ifneq ($(findstring ORG_TASK, $(FEATURES)),)
SOURCE+=Task
endif

ifneq ($(findstring ORG_STOCK, $(FEATURES)),)
SOURCE+=Stock
endif

ifneq ($(findstring PRICE_MATRIX, $(FEATURES)),)
SOURCE+=PriceMatrix
endif

ifneq ($(findstring ORG_NOTE, $(FEATURES)),)
SOURCE+=Notes
endif

ifneq ($(findstring ORG_REMNANTS, $(FEATURES)),)
SOURCE+=OrgRmnts
endif

ifneq ($(findstring COST_MANAGER, $(FEATURES)),)
SOURCE+=Costs
endif

ifneq ($(findstring ORDER_ONLINE, $(FEATURES)),)
SOURCE+=ObjExchange
endif

ifdef NPL_PDA_SOURCE
SOURCE += $(NPL_PDA_SOURCE)
endif   

ifdef NPL_PDA_RESOURCE
RESOURCE += $(NPL_PDA_RESOURCE)
endif   

ifdef NATIVE_CE
SOURCE+=ce
endif

ifdef MARK_SYNCED
SOURCE+=DoSync
endif

ifneq ($(findstring VAN_SELLING, $(FEATURES)),)
SOURCE+=DoPrint PrintPref DataSource Dig2Str DeliveryPrint PayImpl
CAB_SOURCE+=SCHF.xml Torg12.xml PKO.xml
CAB_BIN+=BTWC.dll NPrinter.dll
endif

ifneq ($(findstring NAPOLEON_APPS, $(FEATURES)),)
#ifdef APPS_NEW
CAB_BIN+=NapoleonApps.dll
#else
#CAB_BIN+=NapoleonApps.dll NplDrv.dll
#CAB_ADD_STR=DestDir=NplDrv.dll:CE2
#endif
endif

LIBS=Lib NplDB

ADD_INCLUDE=../Lib/ZLib;../Lib/Core;Apps

VPATH:=Pda/Core Pda/Resource ../Lib/Modules ../Lib/SQLite ../NplUpdate/Core Forms Include $(VPATH)

all: $(OUT_DIR)/$(PROGRAM).exe
pch: $(INT_DIR)/stdafx.obj

cab: $(OUT_DIR)/$(SETUP_PROGRAM).cab

include ../../make.ce


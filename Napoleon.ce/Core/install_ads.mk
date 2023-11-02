# INST_FILES файлы которые копировать 
# SOURCES wxs файлы 

.PHONY:

include ../make.vars
include make.vars

SOURCES=Folders Common ServerAds AdsManager Admin

INST_FILES:=GRPacket.dll GRServer.ftr GRServer.exe GRServer.ini GRServer.ads.sdb GRServer.pdf serverDefs.xml addDefs.xml Site_Napoleon.URL KB.URL \
 Ads2017.exe NapoleonAdmin.exe Admin.pdf Dispatcher.pdf GRDBRestore.exe $(INST_FILES)

VPATH += GRServer/GRDBRestore/Release

ifndef NO_ANDROID
SOURCES += Android 
INST_FILES += Android.pdf 
endif

ifneq ($(findstring COM_SERVER, $(FEATURES)),)
SOURCES += ComSrv
INST_FILES += ComGRServer.dll ComGRServer.pdf ComGRServer_x64.dll 
VPATH += GRServer/ComServer/Docs
endif

ifneq ($(findstring ODBC, $(FEATURES)),)
SOURCES += ODBC
INST_FILES += ODBC.dll ODBC.pdf
#VPATH += GRServer/ComServer/Docs
endif

VPATH += References/Dosc.ads

ifneq ($(findstring PYTHON_REPORTER, $(FEATURES)),)
ifneq ($(findstring USE_CURL, $(FEATURES)),)
SOURCES += PythonReporter_2_7_14
VPATH += References/Python.2.7.14/Release
INST_FILES += python27.dll Reporter.dll msvcp120.dll msvcr120.dll
else
SOURCES += PythonReporter
VPATH += References/Python
INST_FILES += python27.dll Reporter.dll Microsoft.VC90.CRT.manifest msvcm90.dll msvcp90.dll msvcr90.dll
endif
endif

ifdef SERVER_DEMO_BASE
SOURCES += DemoBase ManagerPDA
INST_FILES += ManagerPDA.pdf
endif

MSI_FILE=NapoleonSetup

all: $(OUT_DIR)/$(MSI_FILE).msi

features: $(OUT_DIR)/GRServer.ftr

$(OUT_DIR)/GRServer.ftr: FORCE_MAKE
	@bash -c "echo $(FEATURES) | sed 's/ \+/\n/g' > $@"

FORCE_MAKE:

include ../make.wix

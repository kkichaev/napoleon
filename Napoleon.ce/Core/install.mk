# INST_FILES файлы которые копировать 
# SOURCES wxs файлы 

.PHONY:

include ../make.vars
include make.vars

SOURCES=Common Admin Add

INST_FILES:=Formats.pdf GRPacket.dll GRServer.ftr GRServer.ini GRServer.sdb GRServer.pdf serverDefs.xml Site_Napoleon.URL KB.URL \
 Admin.pdf NapoleonAdmin.exe GRDBRestore.exe $(INST_FILES)
#GRUpdate.exe 

ifdef SERVER_X64
  INST_FILES += GRServer_x64.exe
  SOURCES += Server_x64 Folders_x64
else
 INST_FILES += GRServer.exe
  SOURCES += Server Folders
endif

VPATH += GRServer/GRDBRestore/Release

ifndef NO_ANDROID
SOURCES += Android 
INST_FILES += Android.pdf 
endif

ifndef NO_WIN_MOBILE
SOURCES += Pda
INST_FILES+= NapoleonSetup.cab Pda.pdf NplInstall.exe NapoleonSetup.ini 
endif

ifdef Docs_1c
SOURCES += ONE_C_Docs
INST_FILES+= NapoleonExchange.pdf 
endif

ifneq ($(findstring COM_SERVER, $(FEATURES)),)
SOURCES += ComSrv
INST_FILES += ComGRServer.dll ComGRServer.pdf ComGRServer_x64.dll 
VPATH += GRServer/ComServer/Docs
endif

ifneq ($(findstring ODBC, $(FEATURES)),)
  INST_FILES += ODBC.pdf

 ifdef SERVER_X64
   INST_FILES += ODBC_x64.dll
   SOURCES += ODBC_x64
 else
   INST_FILES += ODBC.dll
   SOURCES += ODBC
 endif
endif

ifneq ($(findstring JOIN_SERVER, $(FEATURES)),)
INST_FILES += GRJS.pdf
SOURCES += GRJS
endif

ifneq ($(findstring VERSION_4, $(FEATURES)),)
	INST_FILES += ManagerPDA.pdf vcruntime140.dll msvcp140.dll
	VPATH += References/Docs.4.0 GRServer/curl/builds/libcurl-vc14-x86-release-dll-ipv6-sspi-winssl/bin

	INST_FILES += python39.dll
	ifdef SERVER_X64
		INST_FILES += Reporter_x64.dll
		VPATH += References/Python.3.9/Release.x64
		SOURCES += PythonReporter_3_9_x64
	else 
		INST_FILES += Reporter.dll
		VPATH += References/Python.3.9/Release
		SOURCES += PythonReporter_3_9
	endif
else
	ifneq ($(findstring VERSION_3_5, $(FEATURES)),)
		INST_FILES += ManagerPDA.pdf
		VPATH += References/Docs.3.5
	else
		VPATH += References/Docs
	endif
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
endif


ifdef NO_MANAGER
ifneq ($(findstring ADS, $(FEATURES)),)
SOURCES += AdsManager ServerAds
INST_FILES += Ads.exe GRServer.sdb
#System.Data.SQLite.dll SQLite.Interop_32.dll SQLite.Interop_64.dll
else
endif
else
  INST_FILES += Manager.pdf NapoleonManager.exe Microsoft.Web.WebView2.Core.dll Microsoft.Web.WebView2.WinForms.dll
  SOURCES += Manager webview
  ifdef EXCEL_LIBRARY
    SOURCES += ManagerXls
	INST_FILES += ExcelLibrary.dll
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

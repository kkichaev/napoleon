include ../../make.vars
include ../make.vars

PROGRAM=GRServer$(X64_ADD)

LIB_SOURCE = sqlite3 xmlparse xmlrole xmltok xmltok_impl xmltok_ns 

SOURCE=$(LIB_SOURCE) action actloader addon auth backup base64 chgdef commands config constloader csvsource \
 datactrl datasource dbfcost dbfsource \
 dbftblset dbfwriter wdisp xmlsource\
 dispatcher dofunc event evtloader features filter folderset grserver Key kvtable license loaders log msgtable objdef objects OutStream parse \
 plugcfg pricetable reqhandler runaction reporter \
 sequence service sessobj session serversrc sqsource srvdata srvupdate srvutility tholder tray updpacket xml 
 
 #intel_resize

RESOURCE=grserver

#ADD_LINK_FLAGS= /STACK:10240000

#PROG_MANIFEST=GRServer.manifest

ifdef DYNAMIC_RT
LIBS=Lib.pc.rt$(X64_ADD)
else
LIBS=Lib.pc$(X64_ADD)
endif

ADD_INCLUDE=Include;Lib.pc/Include;Lib.pc/ZLib
#;Lib.pc/LibJPEG;Lib.pc/LibPNG
#;C:/Program Files (x86)/Intel/oneAPI/ipp/latest/include/
#ifdef X64
#ADD_LIB:=$(ADD_LIB);C:/Program Files (x86)/Intel/oneAPI/ipp/latest/lib/intel64;
#else
#ADD_LIB:=$(ADD_LIB);C:/Program Files (x86)/Intel/oneAPI/ipp/latest/lib/ia32;
#endif

#ippimt.lib ippcoremt.lib ippsmt.lib 
MS_LIBS:=Dbghelp.lib Psapi.lib

SOURCE+=http_server httpreq json

ifneq ($(findstring JOIN_SERVER, $(FEATURES)),)
SOURCE+=joinsrv 
endif

# CURL
ADD_INCLUDE+=;curl/include
MS_LIBS+=wldap32.lib Crypt32.lib 
SOURCE+=curl_service

CURL_OPT = x86
ifdef X64
   CURL_OPT=x64
endif

ifdef DEBUG
	ADD_LIB+=;curl/builds/libcurl-vc14-$(CURL_OPT)-debug-static-ipv6-sspi-winssl/lib;
	MS_LIBS+=msvcrtd.lib oldnames.lib msvcprtd.lib libcurl_a_debug.lib
else
	ADD_LIB+=;curl/builds/libcurl-vc14-$(CURL_OPT)-release-static-ipv6-sspi-winssl/lib;
	MS_LIBS+=msvcrt.lib oldnames.lib msvcprt.lib libcurl_a.lib 
endif

ifdef X64
	ADD_LIB+=;Lib.pc/bin/Dist.v14.x64;
else
	ADD_LIB+=;Lib.pc/bin/Dist.v14;
endif

# ifdef DEBUG
	# MS_LIBS+= LibJPEGd.lib LibPNGd.lib 
# else
	# MS_LIBS+= LibJPEG.lib LibPNG.lib
# endif

MS_LIBS += Rpcrt4.lib # Rpcrt4 need form guid gen

VPATH:=Include Server/Core ../Include Server/Core/expat Server/Core/sqlite Server/Resource

all: $(OUT_DIR)/$(PROGRAM).exe
pch: $(INT_DIR)/stdafx.obj

$(INT_DIR)/grserver.res: Server/Resource/stdObjects.bin
Server/Resource/stdObjects.bin: Server/Core/objects/stdObjects.xml
	@compress $+ $@

$(OUT_DIR)/$(PROGRAM).exe: MS_LIBS+=Ws2_32.lib

include ../../make.pc

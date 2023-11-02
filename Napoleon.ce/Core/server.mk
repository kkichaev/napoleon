include ../make.vars
include make.vars

PROGRAM=GRServer$(X64_ADD)

LIB_SOURCE = sqlite3 xmlparse xmlrole xmltok xmltok_impl xmltok_ns

SOURCE=$(LIB_SOURCE) action actloader addon auth backup chgdef commands compat config constloader csvsource \
 datactrl datasource dbfcost dbfsource \
 dbftblset dbfwriter wdisp xmlsource\
 dispatcher dofunc event evtloader features filter folderset grserver Key kvtable license loaders log msgtable objdef objects OutStream parse \
 plugcfg pricetable reqhandler runaction reporter scheduler \
 sequence service sessobj session serversrc sqsource srvdata srvupdate srvutility tholder tray updpacket xml \
 Compressor Reflection Streamer  \
 exif gpsinfo iptc jhead jpgfile makernote myglob paths intel_resize

RESOURCE=grserver

#ADD_LINK_FLAGS= /STACK:10240000

#PROG_MANIFEST=GRServer.manifest

ifdef DYNAMIC_RT
LIBS=Lib.pc.rt$(X64_ADD)
else
LIBS=Lib.pc$(X64_ADD)
endif

ADD_INCLUDE=GRServer/Include;Lib.pc/Include;Lib.pc/ZLib;Lib.pc/LibJPEG;Lib.pc/LibPNG;Napoleon.sql/Include;C:/Program Files (x86)/Intel/oneAPI/ipp/latest/include/
ifdef X64
ADD_LIB:=$(ADD_LIB);C:/Program Files (x86)/Intel/oneAPI/ipp/latest/lib/intel64;
else
ADD_LIB:=$(ADD_LIB);C:/Program Files (x86)/Intel/oneAPI/ipp/latest/lib/ia32;
endif

MS_LIBS:=ippimt.lib ippcoremt.lib ippsmt.lib Dbghelp.lib Psapi.lib 

ifneq ($(findstring HTTP_SERVER, $(FEATURES)),)
SOURCE+=http_server
endif

ifneq ($(findstring VERSION_5, $(FEATURES)),)
SOURCE+=httpreq
endif

ifneq ($(findstring JOIN_SERVER, $(FEATURES)),)
SOURCE+=joinsrv json
endif

ifdef USE_CURL
ADD_INCLUDE+=;GRServer/curl/include
MS_LIBS+=wldap32.lib Crypt32.lib 
SOURCE+=curl_service

ifdef VS14
ifdef X64
	ifdef DEBUG
		ADD_LIB+=;GRServer/curl/builds/libcurl-vc14-x64-debug-static-ipv6-sspi-winssl/lib;
		MS_LIBS+=msvcrtd.lib oldnames.lib msvcprtd.lib libcurl_a_debug.lib
	else
		ADD_LIB+=;GRServer/curl/builds/libcurl-vc14-x64-release-static-ipv6-sspi-winssl/lib;
		MS_LIBS+=msvcrt.lib oldnames.lib msvcprt.lib libcurl_a.lib 
	endif
else
	ifdef DEBUG
		ADD_LIB+=;GRServer/curl/builds/libcurl-vc14-x86-debug-static-ipv6-sspi-winssl/lib;
		MS_LIBS+=msvcrtd.lib oldnames.lib msvcprtd.lib libcurl_a_debug.lib
	else
		ADD_LIB+=;GRServer/curl/builds/libcurl-vc14-x86-release-static-ipv6-sspi-winssl/lib;
		MS_LIBS+=msvcrt.lib oldnames.lib msvcprt.lib libcurl_a.lib 
	endif
endif
else
ifdef DEBUG
 ADD_LIB+=;GRServer/curl/build/Win32/VC12/LIB Debug - DLL Windows SSPI;
 MS_LIBS+=/NODEFAULTLIB msvcrtd.lib oldnames.lib msvcprtd.lib libcurld.lib 
else
 ADD_LIB+=;GRServer/curl/build/Win32/VC12/LIB Release - DLL Windows SSPI;
 MS_LIBS+=/NODEFAULTLIB msvcrt.lib oldnames.lib msvcprt.lib libcurl.lib 
endif
endif
endif # ifdef USE_CURL

ifdef VS14
ifdef X64
	ADD_LIB+=;Lib.pc/bin/Dist.v14.x64;
else
	ADD_LIB+=;Lib.pc/bin/Dist.v14;
endif
else
	ifdef VS12
		ADD_LIB+=;Lib.pc/bin/Dist.v12;
	else
		ifdef DYNAMIC_RT
			ADD_LIB+=;Lib.pc/bin/Dist.v9;
		else
			ADD_LIB+=;Lib.pc/bin/Dist.v9.static;
		endif
	endif
endif # VS14

ifdef DEBUG
	MS_LIBS+= LibJPEGd.lib LibPNGd.lib 
else
	MS_LIBS+= LibJPEG.lib LibPNG.lib
endif

VPATH:=GRServer/Core GRServer/Server/Core Include GRServer/Server/Core/expat GRServer/Server/Core/sqlite GRServer/Server/Core/jhead GRServer/Server/Resource

ifneq ($(findstring ENCODE_CONNECTION, $(FEATURES)),)
MS_CFLAGS_ADD= /D "LTM_DESC"
ADD_INCLUDE+=;Lib.pc/crypt
endif


all: $(OUT_DIR)/$(PROGRAM).exe
pch: $(INT_DIR)/stdafx.obj

$(INT_DIR)/grserver.res: GRServer/Server/Resource/stdObjects.bin
GRServer/Server/Resource/stdObjects.bin: GRServer/Server/Core/objects/stdObjects.xml
	@compress $+ $@

$(OUT_DIR)/$(PROGRAM).exe: MS_LIBS+=Ws2_32.lib

include ../make.pc

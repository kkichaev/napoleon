include ../make.vars
include make.vars

SOURCE=packet pktops servobj socket stdobjs thread token AESEnc C32 OutStream Streamer Reflection \
   dateparse dbfapp dbfchfil dbfcnstr dbfdelre dbffill \
   dbfgetfl dbfgo dbfgtfr dbfgthd dbfop dbfopen dbfrdrec dbfrecl dbfwrrc dbfwrrec filefield getwdh \
   adler32 compress crc32 deflate inffast inflate inftrees trees zutil

ifdef DYNAMIC_RT
PROGRAM=Lib.pc.rt$(X64_ADD)
else
PROGRAM=Lib.pc$(X64_ADD)
endif

ADD_INCLUDE=Lib.pc/Include
VPATH:=Lib.pc/Core Lib.pc/Zlib Lib.pc/Dbf Include

ifdef CORE_PATH
addPath = $(foreach vpth $(VPATH) $(CORE_PATH)/$(vpth))
VPATH += addPath 
endif

ifneq ($(findstring ENCODE_CONNECTION, $(FEATURES)),)
include cryptlib.mk
MS_CFLAGS_ADD=/D "LTC_SOURCE" /D "LTM_DESC"
SOURCE+=$(CRYPT_SRC)
ADD_INCLUDE+=;Lib.pc/crypt
VPATH+= Lib.pc/crypt
endif

all: $(OUT_DIR)/$(PROGRAM).lib
pch: $(INT_DIR)/stdafx.obj

include ../make.pc


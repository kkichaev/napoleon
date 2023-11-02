include ../make.vars
include make.vars

SOURCE=C32 CalcTxtHeight CheckTable ChgOrient Compressor DataReader DBImpl IsFileExist GetString SQLTable Streamer Reflection ResetTime \
       MakeCall NetExchange Network PhoneLine SendSMS TreeNode GetDeviceID GetValue GetVersion HTable OutStream \
       PaintScale Power PowerMgm SetSysFont Str2List Version \
       adler32 compress crc32 deflate inffast inflate inftrees trees zutil 

ifdef APPS_NEW
SOURCE+=Distance
else
ifneq ($(findstring GPS_POS, $(FEATURES)),)
SOURCE+=GPSUnit Distance
endif
endif

PROGRAM=Lib

ADD_INCLUDE=Lib/NplDB

VPATH:=Lib/Core Lib/Zlib Include

all: $(OUT_DIR)/$(PROGRAM).lib
pch: $(INT_DIR)/stdafx.obj

include ../make.ce


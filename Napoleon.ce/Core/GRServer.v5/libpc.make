OUT=$(OUT_DIR)/libpc.a
INT_DIR=$(OUT_DIR)/libpc
CPP=g++
CC=gcc

INCLUDES=./Lib.pc/Include ./Lib.pc/Zlib ./Include ./Lib.pc/Unix
DEFINES=UNIX

CFLAGS+=-fPIC $(patsubst %,-I%, $(INCLUDES)) $(patsubst %,-D %, $(DEFINES))

SOURCES=pktops.cpp \
stdobjs.cpp  \
packet.cpp \
servobj.cpp \
filefield.cpp \
token.cpp \
AES.cpp \
C32.cpp \
OutStream.cpp \
dateparse.cpp \
stdafx.cpp \
dbfwrrec.cpp \
dbfop.cpp \
dbfapp.cpp \
dbfgo.cpp \
dbfgetfl.cpp \
dbfgthd.cpp \
dbfdelre.cpp \
getwdh.cpp \
dbfwrrc.cpp \
dbfrdrec.cpp \
dbffill.cpp \
dbfcnstr.cpp \
dbfopen.cpp \
dbfgtfr.cpp \
dbfchfil.cpp \
dbfrecl.cpp \
uthread.cpp \
UnixCompat.cpp \
usocket.cpp \
compress.c \
inftrees.c \
crc32.c \
trees.c \
inffast.c \
adler32.c \
zutil.c \
inflate.c \
deflate.c

# socket.cpp thread.cpp 

OBJECTS=$(patsubst %,$(INT_DIR)/%,$(subst .c,.o,$(subst .cpp,.o,$(SOURCES))))

#$(error out dir $(OBJECTS))

VPATH=./Lib.pc/Core ./Lib.pc/Dbf ./Lib.pc/Unix ./Lib.pc/Zlib ./Include

$(INT_DIR)/%.o: %.cpp
	mkdir -p $(@D)
	$(CPP) -c -o $@ $< $(CFLAGS) 

$(INT_DIR)/%.o: %.c
	mkdir -p $(@D)
	$(CC) -c -o $@ $< $(CFLAGS) 

$(OUT):$(OBJECTS)
	ar rcs $@ $^
OUT_DIR=./bin

OUT=$(OUT_DIR)/grsmanager
INT_DIR=$(OUT_DIR)/grsmanager.bin
CPP=g++
CC=gcc


VPATH=./fcgi

INCLUDES=include /usr/include/postgresql
DEFINES=UNIX USE_CURL

CFLAGS+=$(patsubst %,-I%, $(INCLUDES)) $(patsubst %,-D %, $(DEFINES))  -g

SOURCES= fcgiapp.c \
 fcgio.cpp \
 fcgimanager.cpp \
 os_unix.c \
 cmdmanager.cpp \
 dbmanager.cpp \
 grsmanager.cpp \
 config.cpp

OBJECTS=$(patsubst %,$(INT_DIR)/%,$(subst .c,.o,$(subst .cpp,.o,$(SOURCES))))

$(INT_DIR)/%.o: %.c
	mkdir -p $(@D)
	$(CC) -c -o $@ $< $(CFLAGS) 

$(INT_DIR)/%.o: %.cpp
	mkdir -p $(@D)
	$(CPP) -c -o $@ $< $(CFLAGS) 

all:$(OUT)

$(OUT): $(OBJECTS)
	gcc -o $@ $^ -lstdc++ -L /usr/lib/x86_64-linux-gnu -lpq -lpthread



 
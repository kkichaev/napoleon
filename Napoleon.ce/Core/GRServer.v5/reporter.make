OUT=$(OUT_DIR)/python.so
INT_DIR=$(OUT_DIR)/python
CPP=g++
CC=gcc

PKG_CONFIG_PATH=./python/lib/pkgconfig
VPATH=./Plugins/Reporter3.9/Core

INCLUDES=./Lib.pc/Include ./Lib.pc/Zlib ./Include ./Lib.pc/Unix ./Plugins/Reporter3.9/Core ./python/include/python3.11
DEFINES=UNIX USE_CURL

#CFLAGS+=$(patsubst %,-I%, $(INCLUDES)) $(shell pkg-config --cflags python-3.11-embed) $(patsubst %,-D %, $(DEFINES)) -fPIC
CFLAGS+=$(patsubst %,-I%, $(INCLUDES)) $(patsubst %,-D %, $(DEFINES)) -fPIC

SOURCES= dllmain.cpp \
 Config.cpp \
 Reporter.cpp \
 PyServer.cpp \
 PyObjList.cpp \
 PythonObject.cpp \
 PyCurl.cpp 
 

# socket.cpp thread.cpp 

OBJECTS=$(patsubst %,$(INT_DIR)/%,$(subst .c,.o,$(subst .cpp,.o,$(SOURCES))))

#$(error out dir $(OBJECTS))

$(INT_DIR)/%.o: %.cpp
	mkdir -p $(@D)
	$(CPP) -c -o $@ $< $(CFLAGS) 

$(OUT):$(OBJECTS)
	gcc  -Wl,--whole-archive ./bin/libpc.a ./python/lib/libpython3.11.a -Wl,--no-whole-archive -shared -o $@ $^ 
	cp $@ /home/ert/prog/grs/

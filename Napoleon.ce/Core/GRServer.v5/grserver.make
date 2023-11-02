OUT=$(OUT_DIR)/grserver
INT_DIR=$(OUT_DIR)/grserver.bin
CPP=g++
CC=gcc

INCLUDES=./Lib.pc/Include ./Lib.pc/Zlib ./Include ./Lib.pc/Unix ./Include ./Server/Core ./curl/include
DEFINES=UNIX HTTP_SERVER VERSION_5

CFLAGS+=$(patsubst %,-I%, $(INCLUDES)) $(patsubst %,-D %, $(DEFINES))

SOURCES=kvtable.cpp\
    reqhandler.cpp\
    runaction.cpp\
    commands.cpp\
    parse.cpp\
    addon.cpp\
    reporter.cpp\
    log.cpp\
    http_server.cpp\
    srvupdate.cpp\
    tholder.cpp\
    httpreq.cpp\
    constloader.cpp\
    msgtable.cpp\
    filter.cpp\
    actloader.cpp\
    datasource.cpp\
    serversrc.cpp\
    json.cpp\
    chgdef.cpp\
    sqsource.cpp\
    dbftblset.cpp\
    objdef.cpp\
    base64.cpp\
    backup.cpp\
    dispatcher.cpp\
    features.cpp\
    csvsource.cpp\
    srvutility.cpp\
    session.cpp\
    pricetable.cpp\
    userver.cpp\
    dofunc.cpp\
    dbfsource.cpp\
    srvdata.cpp\
    expat/xmltok_impl.c\
    expat/xmltok_ns.c\
    expat/xmltok.c\
    expat/xmlrole.c\
    expat/xmlparse.c\
    xml.cpp\
    dbfwriter.cpp\
    udisp.cpp\
    sqlite/sqlite3.c\
    xmlsource.cpp\
    license.cpp\
    sessobj.cpp\
    dbfcost.cpp\
    objects.cpp\
    sequence.cpp\
    scheduler.cpp\
    evtloader.cpp\
    action.cpp\
    config.cpp\
    event.cpp\
    auth.cpp\
    datactrl.cpp\
    loaders.cpp\
    folderset.cpp\
    curl_service.cpp\
    stdafx.cpp


#     tray.cpp plugcfg.cpp grserver.cpp joinsrv.cpp


OBJECTS=$(patsubst %,$(INT_DIR)/%,$(subst .c,.o,$(subst .cpp,.o,$(SOURCES))))

#$(error out dir $(OBJECTS))

VPATH=./Server/Core ./Include ./Server/Core/objects

$(INT_DIR)/%.o: %.cpp
	mkdir -p $(@D)
	$(CPP) -c -o $@ $< $(CFLAGS) 

$(INT_DIR)/%.o: %.c
	mkdir -p $(@D)
	$(CC) -c -o $@ $< $(CFLAGS) 

#-rdynamic for plugins
$(OUT):$(OBJECTS) $(INT_DIR)/stdObjects.o $(OUT_DIR)/libpc.a
	$(CPP) -rdynamic -L$(OUT_DIR) -L./curl/builds -o $@ $^ -lpc -lcurl -lssl -lcrypto -lpthread -ldl -luuid
	cp $@ /home/ert/prog/grs/

$(INT_DIR)/stdObjects.o: stdObjects.xml
	ld -r -b binary $< -o $@ 


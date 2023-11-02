# -*- coding: cp1251 -*-

import time
import datetime
import sys
reload(sys);
sys.setdefaultencoding("cp1251")

def run(server):

    #source = server.Params[0]
    #print source.formName
    userid = server.CurrentUser().id

    print userid + ' starting ' + str(datetime.datetime.now())

    type = "ReturnResult[created:s,number:s,message:s,status:s]"
    server.RegisterType(type)
    objList = server.New("ReturnResult")

    #server.Put(objList)   
    #print userid + ' done ' + str(datetime.datetime.now())
    #return

    res = None
    connectStr = server.Config('1C_Connect')
    conn = server.GetCachedCOM(connectStr)
    if conn == None:
       o = server.CreateObject("V83.COMConnector") 
       conn = None
       try:
           conn = o.Connect(connectStr)
           print userid + ' connected ' + str(datetime.datetime.now())

       except RuntimeError as err:
           objRes = objList.New()
           objRes.status = 'fail'
           objRes.message = str(err)
           server.Put(objList)
		   
           print str(err)

           return
    else:
        print userid + ' got cached ' + str(datetime.datetime.now())

    if conn == None :
        objRes = objList.New()
        objRes.status = 'fail'
        objRes.message = 'Ошибка при подключении к 1с'
        server.Put(objList)

        print str(err)
        return

    try:
        pdaHandler = getattr(conn, 'РаботаСКПК')
        res = pdaHandler.ReturnHandler(userid)
        print userid + ' handled ' + str(datetime.datetime.now())

    except RuntimeError as err:
        objRes = objList.New()
        objRes.status = 'fail'
        objRes.message = str(err)
        server.Put(objList)
        print "fail handling: " + str(err)

        return
               
    i = 0
    till = 0 if res == None else res.Count()


    archNumber = server.New('ArchiveReturnNumbers')
    while i < till :
        objRes = objList.New()
        src = res.Get(i)
#         objRes.dlvdate = src.dlvdate

        objRes.created = src.created
#         objRes.balance = src.balance
        objRes.number = src.number
        objRes.status = src.status
        objRes.message = src.message

        ao = archNumber.New()
        ao.userid = userid
        ao.created = src.created
        ao.number = src.number
        
        i += 1


    server.Put(objList)
    server.Write(archNumber)

    print userid + ' done ' + str(datetime.datetime.now())

    #server.PutCOMToCache(connectStr, conn)


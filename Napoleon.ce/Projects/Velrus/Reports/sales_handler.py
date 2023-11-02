# -*- coding: cp1251 -*-

import time
import datetime
import sys

#reload(sys);

def run(server):

    #source = server.Params[0]
    #print source.formName

    userid = server.CurrentUser().id

    print(userid + ' starting ' + str(datetime.datetime.now()))

    type = "SalesResult[status:n,,message:s]"
    server.RegisterType(type)

    objList = server.New("SalesResult")

    res = None
    connectStr = server.Config('1C_Connect')
    connector = server.Config('1C_COM_Connector')
    
    conn = server.GetCachedCOM(connectStr)
    if conn == None:
       print(connector)
       o = server.CreateObject(connector) 
       if o == None:
           objRes = objList.New()
           objRes.status = 0
           objRes.message = "No com"
           server.Put(objList)
		   
           print("No com")

           return

       conn = None
       try:
           print(userid + ' connecting ' + str(datetime.datetime.now()))
           conn = o.Initialize(o.RMTrade,connectStr,"NO_SPLASH_SHOW")
           print(userid + ' connected ' + str(datetime.datetime.now()))

       except RuntimeError as err:
           objRes = objList.New()
           objRes.status = 0
           objRes.message = str(err)
           server.Put(objList)
		   
           print(str(err))

           return
    else:
        print( userid + ' got cached ' + str(datetime.datetime.now()))

    if conn == None :
        objRes = objList.New()
        objRes.status = 0
        objRes.message = 'Ошибка при подключении к 1с'
        server.Put(objList)

        print(str(err))
        return

    try:
         o.SalesHandler(userid)
         print(userid + ' handled '  + str(datetime.datetime.now()))

         objRes = objList.New()
         objRes.status = 1
       
    except RuntimeError as err:
        objRes = objList.New()
        objRes.status = 0
        objRes.message = str(err)
        server.Put(objList)
        print("fail handling: " + str(err))

        return
    except: 
        err = sys.exc_info()[1]

        objRes = objList.New()
        objRes.status = 0
        objRes.message = str(err)
        server.Put(objList)
        print("Unexpected error:", err)

        return
               
    opd = server.Get("OrderProceeded")
    
    server.Put(objList)
    server.Put(opd)
        
    print(userid + ' done ' + str(datetime.datetime.now()))

    server.PutCOMToCache(connectStr, conn)


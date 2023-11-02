# -*- coding: cp1251 -*-

from importlib import reload
import time
import datetime
import sys
reload(sys)

# sys.setdefaultencoding("cp1251")

def run(server):
    params = server.Params
    param = params[0]
    
    print ('put location starting ' + str(datetime.datetime.now()))
    
    connectStr = server.Config('1C_Connect')
    conn = server.GetCachedCOM(connectStr)
    if conn == None:
       o = server.CreateObject("V82.COMConnector") 
       conn = None
       try:
           conn = o.Connect(connectStr)
           print ('put location  connected ' + str(datetime.datetime.now()))

       except RuntimeError as err:
           print (str(err))
           return
    else:
        print (' got cached ' + str(datetime.datetime.now()))

    if conn == None :
        print ('Connect error')
        return

    try:
        orgs = getattr(conn.Catalogs, 'Контрагенты')
        for orgLoc in param.orgs:
            uid = conn.NewObject('UUID', conn.String(orgLoc.id))
            org = orgs.GetRef(uid)
            if not org.IsEmpty() :
                oobj = org.GetObject()
                setattr(oobj, 'ВА_Широта', orgLoc.latitude)
                setattr(oobj, 'ВА_Долгота', orgLoc.longitude)
 
                oobj.Write()
            
    except RuntimeError as err:
        print ("fail handling: " + str(err))
        return

    server.PutCOMToCache(connectStr, conn)
    print ('put location done ' + str(datetime.datetime.now()))

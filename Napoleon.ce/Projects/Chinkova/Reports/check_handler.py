# -*- coding: cp1251 -*-

from importlib import reload
import logging
import time
import datetime
import sys
import traceback

reload(sys);

def addError(server, message, type):
    cfObjc = server.New('CheckConfirm')
    objRes = cfObjc.New()
    objRes.status = -1
    objRes.remark = message
    objRes.type = -1
    server.Put(cfObjc)
    

def makeCheck(server, isRequestChek):
    # source = server.Params[0]
    # print source.formName

    userid = server.CurrentUser().id

    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    
    logging.debug("starting userid " + userid + " isRequestChek=" + str(isRequestChek))


    # server.Put(objList)   
    # print userid + ' done ' + str(datetime.datetime.now())
    # return

    res = None
    connectStr = server.Config('1C_Connect')
    conn = server.GetCachedCOM(connectStr)
    if conn == None:
       objStr = server.Config('1C_Object')
       logging.debug('COM object:' + objStr)
       o = server.CreateObject(objStr) 
       conn = None
       try:
           conn = o.Connect(connectStr)
           logging.debug(' connected ')

       except:
           ertype, val, tb = sys.exc_info()
           strErr = traceback.format_exception(ertype, val, tb)
           addError(server, strErr, isRequestChek)
           logging.debug(strErr)
    else:
        logging.debug(" got cached ")

    if conn == None :
        strErr = 'Ошибка при подключении к 1с'
        addError(server, strErr, isRequestChek)
        logging.debug(strErr)
        return

    try:
        res = conn.Napoleon.ChekHandler(userid, isRequestChek)
        logging.debug(' handled ')

    except Exception as err:
        strErr = str(err)
        addError(server, strErr, isRequestChek)
        logging.debug("fail handling: " + strErr)
        return

    td = datetime.date.today()
    where = '"userid"=' + "'" + userid + "' and " + '"handled" > ToDate("' + td.strftime("%d/%m/%Y 0:0:0") + '")';
    cfObjc = server.Get('CheckConfirm', where)
    server.Put(cfObjc)
               
    logging.debug('done ' + userid)

    server.PutCOMToCache(connectStr, conn)

def run(server):
    makeCheck(server, 1)



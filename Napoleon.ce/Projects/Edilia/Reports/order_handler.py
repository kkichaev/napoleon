# -*- coding: cp1251 -*-

import time
import datetime
import sys
reload(sys);
sys.setdefaultencoding("cp1251")

#connectStr = 'File="D:\\Works\\1C\\ДнепровскиеЛакомства";Usr="Денис";Pwd=""'

# doc status
#  created
#  ordstatus {repeat, saved, handled, fail]
#  dlvstatus [saved, handled, fail]
#  dlvnumber
#  dlvdate
#  dlvitems[id,qty,sum]}
#  message
#  orgid
#  balance

#add
#  dlvpaydate
#  dlvsumd
#  incass_num
#  incass_date
#  incass_created
#  incass_move[num,sum,sumd] - dlvdata

## инкассация пишет orgid, balance, incass_*
## заявка с инкассацией все поля кроме incass_created

def UpdateBalance(balance, src, dlvKey):
    for obj in balance:
        if obj.id == src.orgid:
            obj.sum = src.balance
            if obj.num in dlvKey:
                obj.sumd = dlvKey[obj.num]
                del dlvKey[obj.num]
                updated = True
                
    for k,v in dlvKey.iteritems():
        obj = balance.New()
        obj.id = src.orgid
        obj.sum = src.balance
        obj.sumd = v
        obj.num = k

def UpdateDlvMove(items, src, sumi):
    for obj in items:
        if (obj.type == "Incass" or obj.type == "Order") and obj.num == src.incass_num:
            obj.sum = sumi
            return
        
    obj = items.New()
    obj.sum = sumi
    obj.num = src.incass_num
    obj.date = src.incass_date
    if src.created != None and len(src.created) != 0 :
        obj.type = "Incass"
        obj.created = datetime.datetime.strptime(src.created, '%Y%m%d%H%M%S')
    else :
        obj.type = "Incass"
        if src.incass_created != None and len(src.incass_created) > 0:
            obj.created = datetime.datetime.strptime(src.incass_created, '%Y%m%d%H%M%S')

def UpdateDeliveryBalance(dlvMove, balance, src):
    dlvKey = dict()
    dlvData = dict()
    i = 0
    till = src.incass_move.Count()
    while i < till:
        el = src.incass_move.Get(i)
        dlvKey[el.num] = el.sumd
        dlvData[el.num] = el.sum
        i += 1
        
    UpdateBalance(balance, src, dlvKey)
    for obj in dlvMove:
        if obj.num in dlvData:
            UpdateDlvMove(obj.items, src, dlvData[obj.num])
            del dlvData[obj.num]
    
    for k,v in dlvData.iteritems():
        obj = dlvMove.New()
        obj.id = src.orgid
        obj.num = k
        UpdateDlvMove(obj.items, src, v)

def FindDelivery(dlv, src):
    finded = False
    ret = None
    for obj in dlv:
        if obj.number == src.dlvnumber:
            ret = obj
            ret.items.Clear()
            ret.sumd = src.dlvsumd 
            ret.created = datetime.datetime.strptime(src.created, '%Y%m%d%H%M%S')           
            finded = True
            break
        
    if not finded:
        ret = dlv.New()
        ret.id = src.orgid
        ret.date = src.dlvdate
        ret.number = src.dlvnumber
        ret.payDate = src.dlvpaydate
        ret.sumd = src.dlvsumd
        ret.created = datetime.datetime.strptime(src.created, '%Y%m%d%H%M%S')
        
    return ret

def run(server):

    #source = server.Params[0]
    #print source.formName

    userid = server.CurrentUser().id

    print userid + ' starting ' + str(datetime.datetime.now())

    type = "OrderResult[created:s,orgid:s,ordnumber:s,ordstatus:s,doctype:s,dlvstatus:s,dlvnumber:s,message:s]"
    server.RegisterType(type)

    type = "RemovedReturns[created:s]"
    server.RegisterType(type)
    objList = server.New("OrderResult")

    #server.Put(objList)   
    #print userid + ' done ' + str(datetime.datetime.now())
    #return

    res = None
    connectStr = server.Config('1C_Connect')
    connector = server.Config('1C_COM_Connector')
    conn = server.GetCachedCOM(connectStr)
    if conn == None:
       o = server.CreateObject(connector) 
       conn = None
       try:
           conn = o.Connect(connectStr)
           print userid + ' connected ' + str(datetime.datetime.now())

       except RuntimeError as err:
           objRes = objList.New()
           objRes.ordstatus = 'fail'
           objRes.message = str(err)
           server.Put(objList)
		   
           print str(err)

           return
    else:
        print userid + ' got cached ' + str(datetime.datetime.now())

    if conn == None :
        objRes = objList.New()
        objRes.ordstatus = 'fail'
        objRes.message = 'Ошибка при подключении к 1с'
        server.Put(objList)

        print str(err)
        return

    try:
#        npl = getattr(conn.Catalogs, 'ВнешниеОбработки').FindByDescription("Napoleon")
#        tempFile = conn.GetTempFileName()
#        binData = getattr(npl, 'ХранилищеВнешнейОбработки').Get()
#        binData.Write(tempFile)
#        handler = conn.ExternalDataProcessors.Create(tempFile, False)
#        res = handler.OrderHandler(userid)
    
        res = conn.Napoleon.OrderHandler(userid)
        print userid + ' handled ' + str(datetime.datetime.now())

    except RuntimeError as err:
        objRes = objList.New()
        objRes.ordstatus = 'fail'
        objRes.message = str(err)
        server.Put(objList)
        print "fail handling: " + str(err)

        return
               
    i = 0
    till = 0 if res == None else res.Count()

    dlvMove = None
    balance = None
    dlv = None
    rmvReturns = None
    
    while i < till :
        objRes = objList.New()
        src = res.Get(i)
        
        if src.ret_rmv_created != None:
            if rmvReturns == None: 
                rmvReturns = server.New('RemovedReturns')
            rdoc = rmvReturns.New()
            rdoc.created = src.ret_rmv_created
            i += 1
            continue
        
        objRes.created = src.created
        objRes.ordnumber = src.ordnumber
        objRes.orgid = src.orgid
        objRes.ordstatus = src.ordstatus
        objRes.message = src.message
        objRes.doctype = src.doctype
        
        if src.orgid == None: 
            i += 1
            continue
        
#        if balance == None: balance = server.Get("OrgBalance")
#        dlvKey = dict()
#        if src.dlvstatus == 'handled' or src.dlvstatus == 'saved' :
#            objRes.dlvstatus = src.dlvstatus
#            objRes.dlvnumber = src.dlvnumber
#            dlvKey[src.dlvnumber] = src.dlvsumd

#        UpdateBalance(balance, src, dlvKey)
        
#        if src.dlvitems != None and src.dlvitems.Count() > 0 :
#            if dlv == None: dlv = server.Get("DeliveryDay")
    
#            delivery = FindDelivery(dlv, src)            
    
#            j = 0
#            jtill = src.dlvitems.Count()
#            while j < jtill :
#                jSrc = src.dlvitems.Get(j)
                
#                dlvi = delivery.items.New()
#                dlvi.qty = jSrc.qty
#                dlvi.id = jSrc.id
#                dlvi.sum = jSrc.sum
                
#                j += 1

#        if src.incass_move != None and src.incass_move.Count() > 0:
#            if dlvMove == None: dlvMove = server.Get("DailyDeliveryBalanceData")
#            UpdateDeliveryBalance(dlvMove, balance, src)
            
        i += 1

    server.Put(objList)

#    whereStmt = '"userid"=' + "'" + userid + "'"
#    if dlv != None:
#        server.Remove(dlv.GetName, whereStmt)
#        server.Write(dlv)
#        server.Put(dlv)

#    if balance != None:
#        server.Remove(balance.GetName, whereStmt)
#        server.Write(balance)
#        server.Put(balance)

#    if dlvMove != None:
#        server.Remove(dlvMove.GetName, whereStmt)
#        server.Write(dlvMove)
#        server.Put(dlvMove)
    
#    if rmvReturns != None:
#        server.Put(rmvReturns)
        
    print userid + ' done ' + str(datetime.datetime.now())

    server.PutCOMToCache(connectStr, conn)


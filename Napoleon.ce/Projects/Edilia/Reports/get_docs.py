# -*- coding: cp1251 -*-

import time
import datetime
import sys
from pydoc import Doc
reload(sys);
sys.setdefaultencoding("cp1251")

def fillAgentLists(agent, orgsFld, orgs):
    ca = getattr(agent, 'Контрагенты')
    count = ca.Count()
    ctr = 0
    while ctr < count:
        caobj = ca.Get(ctr)
        corg = getattr(caobj, 'Контрагент')
        if corg.IsFolder == True :
            orgsFld.Add(corg)
        else:
            orgs.Add(corg)
        ctr += 1

def putTotalData(objRes, agent, conn, queryDlv, queryPay):
    prmOrg = getattr(agent,'Организация')    
    prmAgent = getattr(agent,'Агент')    
    orgsFld = conn.NewObject('СписокЗначений')
    orgs = conn.NewObject('СписокЗначений')
    
    fillAgentLists(agent, orgsFld, orgs)
    
    queryDlv.SetParameter('ПапкаКонтрагентов', orgsFld)
    queryDlv.SetParameter('Контрагенты', orgs)
    queryDlv.SetParameter('Организация', prmOrg)
    queryDlv.SetParameter('Агент', prmAgent)
    selq =  queryDlv.Execute().Choose()
    if selq.Next() > 0 :
        objRes.deliveries = getattr(selq, 'СуммаДокумента')
 
    queryPay.SetParameter('ПапкаКонтрагентов', orgsFld)
    queryPay.SetParameter('Контрагенты', orgs)
    queryPay.SetParameter('Организация', prmOrg)
    queryPay.SetParameter('Агент', prmAgent)
    selq =  queryPay.Execute().Choose()
    if selq.Next():
        objRes.incass = getattr(selq, 'СуммаДокумента')
    

def putDocs(dlvList, payList, agent, conn, queryDlv, queryPay, userid):
    prmOrg = getattr(agent,'Организация')    
    prmAgent = getattr(agent,'Агент')    
    orgsFld = conn.NewObject('СписокЗначений')
    orgs = conn.NewObject('СписокЗначений')
    
    fillAgentLists(agent, orgsFld, orgs)

    queryDlv.SetParameter('ПапкаКонтрагентов', orgsFld)
    queryDlv.SetParameter('Контрагенты', orgs)
    queryDlv.SetParameter('Организация', prmOrg)
    queryDlv.SetParameter('Агент', prmAgent)
    selq =  queryDlv.Execute().Choose()
    while selq.Next() :
        
#         DocsDlvResult[userid:s,id:s,date:dt,created:s,items[id:s,qty:n(3),sum:n(2)]]

        objRes = dlvList.New()
        doc = getattr(selq, 'Ссылка')
        docCA = getattr(doc, 'Контрагент')
        
        objRes.userid = userid
        objRes.created = getattr(selq, 'КодДокумента')
        objRes.id = conn.String(docCA.UUID())
        objRes.date = doc.Date
        objRes.number = doc.Number 
        
        items = getattr(doc, 'Товары')
        count = items.Count()
        ctr = 0
        while ctr < count:
            item = items.Get(ctr)
            prc = getattr(item, 'Номенклатура')
            coef = getattr(item, 'Коэффициент')
            
            dest = objRes.items.New()
            dest.id = conn.String(prc.UUID())
            dest.qty = getattr(item, 'Количество') * coef
            dest.sum = getattr(item, 'Сумма')
            ctr += 1

    queryPay.SetParameter('ПапкаКонтрагентов', orgsFld)
    queryPay.SetParameter('Контрагенты', orgs)
    queryPay.SetParameter('Организация', prmOrg)
    queryPay.SetParameter('Агент', prmAgent)
    selq =  queryPay.Execute().Choose()
    while selq.Next() :
        
#         DocsPayResult[userid:s,id:s,date:dt,created:s,sum:n(2)]
        
        objRes = payList.New()
        doc = getattr(selq, 'Ссылка')
        docCA = getattr(doc, 'Контрагент')
        
        objRes.userid = userid
        objRes.created = getattr(selq, 'КодДокумента')
        objRes.id = conn.String(docCA.UUID())
        objRes.date = doc.Date
        objRes.sum = getattr(doc, 'СуммаДокумента')
        objRes.number = doc.Number 

def makeDlvQueryText(detailed, dateStr):
    if detailed == 0:
        return """
ВЫБРАТЬ
    СУММА(Документ.СуммаДокумента) КАК СуммаДокумента
ИЗ
    Документ.РеализацияТоваровУслуг КАК Документ
ГДЕ
    Документ.Менеджер = &Агент
    И Документ.Проведен = ИСТИНА
    И Документ.Организация = &Организация И Документ.Дата""" + dateStr

    return """
ВЫБРАТЬ
    Документ1с.Ссылка КАК Ссылка,
    ВложенныйЗапрос.КодДокумента
ИЗ
    Документ.РеализацияТоваровУслуг КАК Документ1с
        ЛЕВОЕ СОЕДИНЕНИЕ (ВЫБРАТЬ
            НаполеонПринятыеДокументы.Документ КАК НаполеонДокумент,
            НаполеонПринятыеДокументы.КодДокумента КАК КодДокумента
        ИЗ
            РегистрСведений.НаполеонПринятыеДокументы КАК НаполеонПринятыеДокументы
        ГДЕ
            НаполеонПринятыеДокументы.Агент = &Агент
            И НаполеонПринятыеДокументы.Документ.Дата""" + dateStr + """) КАК ВложенныйЗапрос
        ПО Документ1с.Сделка = ВложенныйЗапрос.НаполеонДокумент
ГДЕ
    Документ1с.Менеджер = &Агент
    И Документ1с.Проведен = ИСТИНА
    И Документ1с.Дата""" + dateStr
    
def makePayQueryText(detailed, dateStr):
    if detailed == 0:
        return """
ВЫБРАТЬ
    СУММА(ДвиженияДенежныхСредств.Сумма) КАК СуммаДокумента
ИЗ
    РегистрНакопления.ДвиженияДенежныхСредств КАК ДвиженияДенежныхСредств
ГДЕ
    ДвиженияДенежныхСредств.ДокументРасчетовСКонтрагентом.Менеджер = &Агент
    И ДвиженияДенежныхСредств.Период""" + dateStr

    return """
ВЫБРАТЬ РАЗЛИЧНЫЕ
    ДвиженияДенежныхСредств.Регистратор.Ссылка КАК Ссылка,
    ВложенныйЗапрос.КодДокумента
ИЗ
    РегистрНакопления.ДвиженияДенежныхСредств КАК ДвиженияДенежныхСредств
        ЛЕВОЕ СОЕДИНЕНИЕ (ВЫБРАТЬ
            НаполеонПринятыеДокументы.Документ КАК НаполеонДокумент,
            НаполеонПринятыеДокументы.КодДокумента КАК КодДокумента
        ИЗ
            РегистрСведений.НаполеонПринятыеДокументы КАК НаполеонПринятыеДокументы
        ГДЕ
            НаполеонПринятыеДокументы.Агент = &Агент
            И НаполеонПринятыеДокументы.Документ.Дата""" + dateStr + """) КАК ВложенныйЗапрос
        ПО (ДвиженияДенежныхСредств.Регистратор.Ссылка = ВложенныйЗапрос.НаполеонДокумент
                ИЛИ ДвиженияДенежныхСредств.Регистратор.ДокументОснование = ВложенныйЗапрос.НаполеонДокумент)
ГДЕ
    ДвиженияДенежныхСредств.ДокументРасчетовСКонтрагентом.Менеджер = &Агент
    И ДвиженияДенежныхСредств.Период""" + dateStr

# def makePayQueryText(detailed, dateStr):
#     if detailed == 0:
#         return """
# ВЫБРАТЬ
#     СУММА(Документ.СуммаДокумента) КАК СуммаДокумента
# ИЗ
#     Документ.ПриходныйКассовыйОрдер КАК Документ
# ГДЕ
#     (Документ.Контрагент В ИЕРАРХИИ (&ПапкаКонтрагентов) ИЛИ Документ.Контрагент В (&Контрагенты))
#     И Документ.Организация = &Организация И Документ.Дата""" + dateStr
# 
#     return """
# ВЫБРАТЬ
#     Документ1с.Ссылка КАК Ссылка,
#     ВложенныйЗапрос.КодДокумента
# ИЗ
#     Документ.ПриходныйКассовыйОрдер КАК Документ1с
#         ЛЕВОЕ СОЕДИНЕНИЕ (ВЫБРАТЬ
#             НаполеонПринятыеДокументы.Документ КАК НаполеонДокумент,
#             НаполеонПринятыеДокументы.КодДокумента КАК КодДокумента
#         ИЗ
#             РегистрСведений.НаполеонПринятыеДокументы КАК НаполеонПринятыеДокументы
#         ГДЕ
#             НаполеонПринятыеДокументы.Агент = &Агент
#             И НаполеонПринятыеДокументы.Документ.Дата""" + dateStr + """) КАК ВложенныйЗапрос
#         ПО Документ1с.Ссылка = ВложенныйЗапрос.НаполеонДокумент или Документ1с.ДокументОснование = ВложенныйЗапрос.НаполеонДокумент
# ГДЕ
#     (Документ1с.Контрагент В ИЕРАРХИИ (&ПапкаКонтрагентов)
#             ИЛИ Документ1с.Контрагент В (&Контрагенты))
#     И Документ1с.Организация = &Организация
#     И Документ1с.Дата""" + dateStr

class Result:
    __slots__ = ('objList', 'dlvList', 'payList')
    
    def __init__(self):
        self.objList = None
        self.dlvList = None
        self.payList = None

# param(start, end, detailed, users)
def loadData(server, param):
    connectStr = server.Config('1C_Connect')
    connector = server.Config('1C_COM_Connector')
    conn = server.GetCachedCOM(connectStr)
    if conn == None:
       o = server.CreateObject(connector) 
       conn = None
       try:
           conn = o.Connect(connectStr)
           print 'get docs connected ' + str(datetime.datetime.now())

       except RuntimeError as err:
           print str(err)
           return None
    else:
        print ' got cached ' + str(datetime.datetime.now())

    if conn == None :
        return None

    res = Result()
    try:
        users = list()
        for agent in param.users:
            users.append(agent.id)
        
        dateStr = " МЕЖДУ ДАТАВРЕМЯ({0}) И ДАТАВРЕМЯ({1})".format(param.start.strftime('%Y,%m,%d'), param.end.strftime('%Y,%m,%d'))
        queryDlv = conn.NewObject('Запрос')
        queryDlv.Text = makeDlvQueryText(param.detailed, dateStr)

        queryPay = conn.NewObject('Запрос')
        queryPay.Text = makePayQueryText(param.detailed, dateStr)

        if param.detailed == 0 :
            type = "DocsTotalResult[id:s,incass:n(2),deliveries:n(2)]"
            server.RegisterType(type)
            res.objList = server.New("DocsTotalResult")
        else :
            type = "DocsDlvResult[userid:s,id:s,date:dt,number:s,created:s,items[id:s,qty:n(3),sum:n(2)]]"
            server.RegisterType(type)
            res.dlvList = server.New('DocsDlvResult')

            type = "DocsPayResult[userid:s,id:s,date:dt,number:s,created:s,sum:n(2)]"
            server.RegisterType(type)
            res.payList = server.New('DocsPayResult')

                
        agents = getattr(conn.Catalogs, 'НаполеонАгенты')
        sel = agents.Select()
        while sel.Next() > 0 :
            if sel.DeletionMark == True: continue
            agent = getattr(sel, 'Агент')
            uid = conn.String(agent.UUID())
            if uid in users:
                print "get docs agent " + agent.Description
                if param.detailed == 0 :
                    objRes = res.objList.New()
                    objRes.id = uid
                    putTotalData(objRes, sel, conn, queryDlv, queryPay)
                else:
                    putDocs(res.dlvList, res.payList, sel, conn, queryDlv, queryPay, uid)
            
    except RuntimeError as err:
        print "fail handling: " + str(err)
        return None
    
    server.PutCOMToCache(connectStr, conn)
    return res
        
def run(server):
    params = server.Params
    param = params[0]
    
    print 'get docs starting ' + str(datetime.datetime.now())
    
    res = loadData(server, param)
    
    if res != None:
        if res.objList != None: server.Put(res.objList)
        if res.payList != None: server.Put(res.payList)
        if res.dlvList != None: server.Put(res.dlvList)
    
    print 'get docs ending ' + str(datetime.datetime.now())

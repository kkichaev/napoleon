# -*- coding: cp1251 -*-
from datetime import datetime
from importlib import reload
import logging
import sys
from typing import Iterable, List

import xlsxwriter as xl
import tempfile
import os

from xlsxwriter import worksheet
from xlsxwriter import workbook
from xlsxwriter.format import Format

class AgentData:
    class ActionData :
        def __init__(self, src) -> None:
            self.name = src.name
            self.id = src.id
            self.type = src.type
            self.descr = src.descr
            self.item = src.itemId
            self.items = src.items.split(',')

    class OrderItem :
        def __init__(self, src) -> None:
            self.name = src.name
            self.qty = src.qty
            self.qtyInPack = src.qtyInPack
            self.id = src.id
            self.cost = src.cost
            self.action = src.action

            # print(self.name, self.action, self.id, self.cost)

    class DocData:
        def __init__(self, src) -> None:
            self.created = src.created
            self.actions = {}
            self.items = []
            for a in src.actions:
                self.actions[a.id] = AgentData.ActionData(a)

        def addOrder(self, order):
            self.date = order.date
            self.name = order.name
            self.address = order.address
            for oi in order.items:
                self.items.append(AgentData.OrderItem(oi))

        def getItemData(self, itemId, action):
            for item in self.items:
                if item.id == itemId and item.action == action:
                    return [item.name, item.cost, item.qty, item.cost * item.qty]
            return [itemId, '?', "?", '?']

    def __init__(self, obj) -> None:
        self.name = obj.agent
        self.docs = dict()
        self.addActionDoc(obj)

    def addActionDoc(self, src):
        self.docs[src.created] = AgentData.DocData(src)

    def addOrder(self, order):
        if order.created in self.docs:
            self.docs[order.created].addOrder(order)


def loadData(params, server):

    data = dict()

    uids = []
    uidFIlter = ''
    for uid in params.userids:
        uids.append(uid.id)
        uidFIlter += "'" + uid.id + "',"

    uidFIlter = uidFIlter[:-1]

    stmt = '''
    select ag."name" as agent, oa."id", oa."items", act."name", act.descr, act."type", act."itemId",
    "Order$userid" as "userid", "Order$created" as "created" 
    from "Order$actions" oa, "Action" act, "Agents" ag
    where oa."id" = act."id" and ag."id" = oa."Order$userid" and
    "Order$userid" in ({0}) and
    "Order$created" >= ToDate("{1}") and "Order$created" <= ToDate("{2}")
    order by "userid", "created"
    '''.format(
        uidFIlter,
        params.start.strftime("%d/%m/%Y"),
        params.finish.strftime("%d/%m/%Y 23:59:59")
    )

    actions = server.Query(stmt, "ActionData[userid:s,agent:s,created:dt,actions(userid,created)[id:s,items:s,itemId:s,name:s,descr:s,type:n]]")
    for a in actions:
        if not a.userid in data:
            data[a.userid] = AgentData(a)
        else: 
            data[a.userid].addActionDoc(a)

    stmt = '''
    select o.name, o.address, o."date", o."userid", o."created",  
    oi."id" as id_i, oi.qty, oi.name as name_i, oi.qtyInPack, oi.cost, oi.action 
    from 
    (select o."created", o."userid", o."id", org.name, o."date", org.address from "Order" o 
        left join org on o."id" = org.id) o,
    (select o."action",  o."id", o."qty", o."cost", p.qtyInPack, p.name, o."Order$created" as created, o."Order$userid" as userid from "Order$items" o
        left join (select id, inpack as qtyInPack, name from price) p
        on o."id" = p."id") oi,
    (select distinct "Order$userid" as userid, "Order$created" as created from "Order$actions") oa	   
    where o."userid" = oi.userid and o."created" = oi.created and o."userid" = oa.userid and o."created" = oa.created
    and o."userid" in ({0}) and
    o."created" >= ToDate("{1}") and o."created" <= ToDate("{2}")

    order by "userid", "created"
    '''.format(
        uidFIlter,
        params.start.strftime("%d/%m/%Y"),
        params.finish.strftime("%d/%m/%Y 23:59:59")
    )

    docs = server.Query(stmt, "OrderData[userid:s,created:dt,date:dt,name:s,address:s,items(userid,created)[id@id_i:s,qty:n(3),action:s,name@name_i:s,qtyInPack:n(3),cost:n(2)]]")
    for d in docs:
        if d.userid in data:
            data[d.userid].addOrder(d)

    return sorted(list(data.values()), key=lambda x: x.name)


def printOut(params, data : List[AgentData], name, server):
    def putFileToServer(fileName, objectName, server):
        file = open(fileName, 'rb')
        bytesOut = file.read(-1)
        file.close()

        server.RegisterType("Result[name:s,file:b]")
        outObj = server.New("Result")
        obj = outObj.New()
        obj.name = objectName
        obj.file = bytesOut

        server.Put(outObj)

    def printHead(wb: workbook.Workbook, sheet : worksheet.Worksheet, crow: int, ccol: int, values: List[str]):
        fmt = wb.add_format({'bold' : True})
        fmt.set_border() 

        col = ccol
        for v in values:
            sheet.write(crow, col, v, fmt)
            col += 1   

    def printValues(sheet : worksheet.Worksheet, fmt : Format, crow : int, ccol: int, values):
        col = ccol
        for v in values:
            sheet.write(crow, col, v, fmt)
            col += 1   


    crow = 0
    tFile = os.path.join(tempfile.gettempdir(), name)  
    wb = xl.Workbook(tFile)
    sheet = wb.add_worksheet()

    bold = wb.add_format({'bold' : True})

    cellFmt = wb.add_format()
    cellFmt.set_border()
    cellFmt.set_text_wrap()

    sheet.write(crow, 0, 'Отчет по акционным заказам', bold)
    crow += 1
    sheet.write(crow, 0, '{0}: c {1} по {2}'.format('период', params.start.strftime('%d.%m.%Y'), params.finish.strftime('%d.%m.%Y')), bold)
    crow += 1

    # sheet.set_row_pixels(2, 310)
    sheet.set_column(0, 0, 6)
    sheet.set_column(1, 1, 12)
    sheet.set_column(2, 2, 30)
    sheet.set_column(3, 3, 30)
    sheet.set_column(4, 4, 30)
    sheet.set_column(5, 5, 30)
    sheet.set_column(6, 6, 30)

    index = 1
    for di in data:
        sheet.write(crow, 0, 'Агент ' + di.name, bold)
        crow += 1
        head = ['№', 'Дата', "Клиент", "Адрес", "Название акции", "Описание", "Товар", "Цена","Количество","Сумма"]
        printHead(wb, sheet, crow, 0, head)
        crow += 1

        for doc in di.docs.values():
            # if doc.date == None: continue

            values = [index, doc.date.strftime('%d.%m.%Y'), doc.name, doc.address]
            for action in doc.actions.values():
                aval = values.copy()
                aval.extend ([action.name, action.descr])

                if action.type == 0 : # Gift
                    tval = aval.copy()
                    print(action.id, action.item)
                    tval.extend(doc.getItemData(action.item, action.id))
    
                    tval[0] = index
                    printValues(sheet, cellFmt, crow, 0, tval)
                    crow += 1
                    index += 1                       
                else:
                    for ai in action.items:
                        tval = aval.copy()
                        tval.extend(doc.getItemData(ai, ""))

                        tval[0] = index
                        printValues(sheet, cellFmt, crow, 0, tval)
                        crow += 1
                        index += 1                       





    wb.close()
    putFileToServer(tFile, name, server)


def run(server):
    
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    
    logging.info('start')

    params = server.Params[0]
    logging.info("params " + str(params))

    data = loadData(params, server)
    printOut(params, data, 'actions_report', server)

    logging.info('end')

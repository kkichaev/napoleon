from xlbuilder import XlBuilder

import sys
import datetime
from datetime import date, timedelta
import logging

from xlsxwriter.worksheet import Worksheet
from xlsxwriter.utility import xl_rowcol_to_cell

from common import unpackUserid

class ReportData:
    def __init__(self) -> None:
        self.items = list()
        self.data : dict[str, AgentData]
        self.data = dict()

class AgentData:
    def __init__(self, userid:str, name:str) -> None:
        self.data:dict[str, dict[str, OrgData]]
        self.data = dict()
        self.name = name
        self.id = userid

    def __str__(self) -> str:
        return "AgentData:\t{0}\t:{1}".format(self.id, self.name)

class OrgData:
    def __init__(self, id:str, name:str, formatTT:str) -> None:
        self.data:dict[date, list[str]]
        self.data = dict()
        self.name = name
        self.id = id
        self.matrix = []
        self.formatTT = formatTT

def loadData(params, server) -> ReportData:
    price = server.Get("ManagerPrice", "", "id")
    stmt = '''
select d.org, d.id as orgid, d.address2, d.city, d.brand, d.created, d.agent, d.formatTT, di.id as id_i, d.userid 
from
 (select d.id, d.userid, d.created, d.date, a.name as agent, o.name as org,  o.city, o.address2, o.brand, o.formatTT from "OrgRemnants" d, Org o, Agents a
    where d.id = o.id and d.userid = a.id and o.brand = "{3}") d,
 (select id, OrgRemnants$userid as userid, OrgRemnants$date as created from OrgRemnants$items where qty > 0) di
where d.userid = di.userid and d.date = di.created
      and d.created >= ToDate("{0}") and d.created < ToDate("{1}") and d."userid" in ({2})
order by d.userid, d.created    
  '''.format(
        params.start.strftime('%d.%m.%Y'),
        (params.finish + datetime.timedelta(days=1)).strftime('%d.%m.%Y'),
        unpackUserid(params.userids),
        params.brand)
    
    docs = server.Query(stmt, 'Docs[org:s,orgid:s,userid:s,address2:s,city:s,brand:s,created:dt,agent:s,formatTT:s,items(userid,created)[id@id_i:s]]')

    stmt = '''
    select id, formatTT, priority from matrix m, (select distinct formatTT from org where brand = "{0}" ) fmt 
        join matrix$items mi on m.name=mi.[Matrix$name] 
        where m.name = fmt.formatTT and not id in (select id from price where name='_Нет товара_') 
    '''.format(params.brand)

    matrixItems = server.Query(stmt, 'Mtx[formatTT:s,priority:n,items(formatTT)[pid@id:s]]')

    matrixDict = dict()
    
    itemsCount : dict[str,int]
    itemsCount = dict()

    for mtx in matrixItems:
        matrixDict[mtx.formatTT] = mtx
        for mi in mtx.items:
            if not mi.pid in itemsCount:
                itemsCount[mi.pid] = 0

            itemsCount[mi.pid] = itemsCount[mi.pid] + 1

    items1 : list[str]
    items1 = list()

    items2 : list[str]
    items2 = list()

    for k in itemsCount:
        if k in price:
            if itemsCount[k] > 1:
                items1.append(k)
            else:
                items2.append(k)    

    items1 = sorted(items1, key = lambda x: price[x].name if x in price else x)
    items2 = sorted(items2, key = lambda x: price[x].name if x in price else x)

    items : list[str]
    items = list()

    items.extend(items1)
    items.extend(items2)

    result:ReportData
    result = ReportData()

    for d in docs:
        if not d.userid in result.data:
            result.data[d.userid] = AgentData(d.userid, d.agent)

        agentData:AgentData()
        agentData = result.data[d.userid]

        if not d.orgid in agentData.data:
            agentData.data[d.orgid] = OrgData(d.orgid, "%s %s" % (d.org, d.address2), d.formatTT)

        dataitems:OrgData
        dataitems = agentData.data[d.orgid]

        if not d.created.date() in dataitems.data:
            dataitems.data[d.created.date()] = list()

        listitems:list[str]
        listitems = dataitems.data[d.created.date()]

        for i in d.items:
            if not i.id in listitems:
                listitems.append(i.id)

    result.items = items
    result.price = price
    result.matrix = matrixDict
    result.items1Count = len(items1)

    return result

class XlBuilderEx(XlBuilder):
    values = []

    def __init__(self, name) -> None:
        super().__init__(name)

        self.priceFmt = self.wb.add_format()
        self.priceFmt.set_border()
        self.priceFmt.set_align('left')
        self.cellFmt.set_align('center')

        self.fmtHead2 = self.wb.add_format({'bold' : True})
        self.fmtHead2.set_align('center')
        self.fmtHead2.set_align('bottom')
        self.fmtHead2.set_rotation(90)
        self.fmtHead2.set_border()
        self.fmtHead2.set_text_wrap(True)  
        self.fmtHead2.set_bg_color('#F2f2f2')

        self.fmtHead0 = self.wb.add_format({'bold' : True})
        self.fmtHead0.set_align('left')
        self.fmtHead0.set_align('bottom')
        self.fmtHead0.set_border()
        self.fmtHead0.set_text_wrap(True)  
        self.fmtHead0.set_bg_color('#F2f2f2')

        self.valueFmt = self.wb.add_format()
        self.valueFmt.set_align('right')
        self.valueFmt.set_border()

        self.priorityFmt = self.wb.add_format({'bold' : True})
        self.priorityFmt.set_align('center')
        self.priorityFmt.set_align('bottom')
        self.priorityFmt.set_rotation(90)
        self.priorityFmt.set_border()
        self.priorityFmt.set_text_wrap(True)  
        self.priorityFmt.set_bg_color('#FFFF00')

        self.priorityValFmt = self.wb.add_format()
        self.priorityValFmt.set_align('right')
        self.priorityValFmt.set_border()
        self.priorityValFmt.set_bg_color('#FFFF00')

        self.valueRedFmt = self.wb.add_format()
        self.valueRedFmt.set_align('right')
        self.valueRedFmt.set_border()
        self.valueRedFmt.set_bg_color('#FF0000')

        self.valuePinkFmt = self.wb.add_format()
        self.valuePinkFmt.set_align('right')
        self.valuePinkFmt.set_border()
        self.valuePinkFmt.set_bg_color('#FFC0CB')

        self.valueDeficitFmt = self.wb.add_format()
        self.valueDeficitFmt.set_align('right')
        self.valueDeficitFmt.set_border()
        self.valueDeficitFmt.set_font_color('#FF0000')

        self.valueDeficitRedFmt = self.wb.add_format()
        self.valueDeficitRedFmt.set_align('right')
        self.valueDeficitRedFmt.set_font_color('#000000')
        self.valueDeficitRedFmt.set_bg_color('#FF0000')

        self.valueDeficitGreenFmt = self.wb.add_format()
        self.valueDeficitGreenFmt.set_align('right')
        self.valueDeficitGreenFmt.set_font_color('#51FF00')
        self.valueDeficitGreenFmt.set_bg_color('#CAEEC2')

        self.valueDeficitOrangeFmt = self.wb.add_format()
        self.valueDeficitOrangeFmt.set_align('right')
        self.valueDeficitOrangeFmt.set_font_color('#9C5700')
        self.valueDeficitOrangeFmt.set_bg_color('#FFEB9C')

        self.borderLeft = self.wb.add_format({'left': 5, 'right' : 1, 'top' : 1, 'bottom' : 1})
        self.borderRight = self.wb.add_format({'left': 1, 'right' : 5, 'top' : 1, 'bottom' : 1})
        self.borderTop = self.wb.add_format({'left': 1, 'right' : 1, 'top' : 5, 'bottom' : 1})
        self.borderBottom = self.wb.add_format({'left': 1, 'right' : 1, 'top' : 1, 'bottom' : 5})
        self.borderLeftTop = self.wb.add_format({'left': 5, 'right' : 1, 'top' : 5, 'bottom' : 1})
        self.borderRigthTop = self.wb.add_format({'left': 1, 'right' : 5, 'top' : 5, 'bottom' : 1})
        self.borderLeftBottom = self.wb.add_format({'left': 5, 'right' : 1, 'top' : 1, 'bottom' : 5})
        self.borderRightBottom = self.wb.add_format({'left': 1, 'right' : 5, 'top' : 1, 'bottom' : 5})

    def setBoderOnRange(self, sheet, r1, c1, r2, c2):
        c = c1
        
        sheet.conditional_format( xl_rowcol_to_cell(r1, c1) , { 'type' : 'no_errors' , 'format' : self.borderLeftTop})
        sheet.conditional_format( xl_rowcol_to_cell(r1, c2) , { 'type' : 'no_errors' , 'format' : self.borderRigthTop}) 
        sheet.conditional_format( xl_rowcol_to_cell(r2, c1) , { 'type' : 'no_errors' , 'format' : self.borderLeftBottom})
        sheet.conditional_format( xl_rowcol_to_cell(r2, c2) , { 'type' : 'no_errors' , 'format' : self.borderRightBottom})

        while c1 <= c2:
            sheet.conditional_format( xl_rowcol_to_cell(r1, c1) , { 'type' : 'no_errors' , 'format' : self.borderTop})
            sheet.conditional_format( xl_rowcol_to_cell(r2, c1) , { 'type' : 'no_errors' , 'format' : self.borderBottom})
            c1 += 1

        c1 = c

        while r1 <= r2:
            sheet.conditional_format( xl_rowcol_to_cell(r1, c1) , { 'type' : 'no_errors' , 'format' : self.borderLeft})
            sheet.conditional_format( xl_rowcol_to_cell(r1, c2) , { 'type' : 'no_errors' , 'format' : self.borderRight})
           
            r1 += 1    

    def printCellValue(self, crow, ccel, value, format):
        # if ccel == 0:
        #     format = self.priceFmt
        # else:
        #     format = self.valuePinkFmt

        #     if value != '':
        #         format = self.valueFmt    
        #     elif self.redRowIndex > 3 and crow < self.redRowIndex and (((len(self.values) > ccel+1) and (self.values[ccel+1] == '')) or (len(self.values) > 0 and (self.values[ccel-1] == ''))):
        #         if (len(self.head) > ccel):
        #             h1 = self.head[ccel] 
        #             hleft = self.head[ccel-1] if len(self.head) > 0 and self.values[ccel-1] == '' else ''
        #             hright = self.head[ccel+1] if len(self.head) > ccel + 1  and self.values[ccel+1] == '' else ''

        #             if h1 == hleft or h1 == hright:
        #                 format = self.valueRedFmt

        format = self.valuePinkFmt if value == '' else self.priceFmt
        return super().printCellValue(crow, ccel, value, format)    

    def printHead1(self, crow, heads, sheet):
        super().printHead(crow, heads)
        v = ""
        xx = 1

        for x in range(0, len(heads)):
            if v == "":
                v = heads[x]
            
            if x > 0:
                if v != heads[x]:
                    sheet.merge_range(0,xx,0,x-1,v,self.boldHead)
                    xx = x
                elif x == len(heads)-1:
                    sheet.merge_range(0,xx,0,x,v,self.boldHead)
                    
                v = heads[x]    

    def printHead2(self, crow, heads, priority):
        cc = 0

        for i in range(0, len(heads)):
            fmt = self.fmtHead2

            if priority[i] > 0:
                fmt = self.priorityFmt

            if i == 0:
                fmt = self.fmtHead0    

            super().printHeadValue(crow, cc, heads[i], fmt)
            cc += 1          

    def printHeadValue(self, crow, ccel, value, format):
        if crow > 0:
            format = self.fmtHead2
        if ccel == 0:
            format = self.fmtHead0

        super().printHeadValue(crow, ccel, value, format)

    def printValues(self, crow, values, cc=0):
        self.values = values
        return super().printValues(crow, values, cc)    

def printOut(params, data:ReportData, name:str, server):
    def initHeadData() ->list[str]:
        res = list()
        res.append("")
        return res

    def setColumns(sheet:Worksheet):
        sheet.set_column('A:A', 100)
        sheet.set_column('B:ZZ', 4)
    
    xl:XlBuilderEx
    xl = XlBuilderEx(name)
    xl.redRowIndex = 3 + data.items1Count 

    sheet = xl.sheet
    sheet.name = 'Представлненность по сети'

    setColumns(sheet)

    crow = 3
    needPrintHead = False

    head1:list[str]
    head1 = initHeadData()

    head2:list[str]
    head2 = initHeadData()
    head2[0] = "%s - %s" % (params.start.strftime('%d.%m.%Y'), params.finish.strftime('%d.%m.%Y'))
    xl.head = head2

    head3:list[str]
    head3 = initHeadData()
    head3[0] = params.brand

    plan = list()
    priority = list()
    priority.append(0)

    for priceItem in data.items:
        if not priceItem in data.price:
            continue

        itemRow:list[str]
        itemRow = list()
        itemRow.append(data.price[priceItem].name)
        
        agentDataList:list[AgentData]
        agentDataList = sorted(data.data.values(), key=lambda x: x.name)

        for ak in agentDataList:
            orgDataList:list[OrgData]
            orgDataList = sorted(data.data[ak.id].data.values(), key=lambda x: x.name)

            for ok in orgDataList:
                dataKeys:list[date]
                dataKeys=sorted(data.data[ak.id].data[ok.id].data.keys())

                for dk in dataKeys:
                    if not needPrintHead:
                        head1.append(ak.name)
                        head2.append(ok.name)
                        head3.append(dk.strftime("%d.%m.%Y"))

                        if ok.formatTT in data.matrix:
                            mtx = data.matrix[ok.formatTT]
                            plan.append(len(mtx.items))
                            priority.append(mtx.priority)
                        else:
                            plan.append(0)
                            priority.append(0)

                    items:list[str]
                    items=data.data[ak.id].data[ok.id].data[dk]
                    itemRow.append(1 if priceItem in items else "")

        xl.printValues(crow, itemRow)
        crow += 1                 

        needPrintHead = True   

    sheet.write(crow, 0, "Факт",  xl.valueFmt)

    for xc in range(1, len(plan) + 1):
        sheet.write_formula(crow, xc, '{=SUM(%s:%s)}' % (xl_rowcol_to_cell(3, xc),
            xl_rowcol_to_cell(crow-1, xc)), xl.valueFmt)

    crow += 1

    sheet.write(crow, 0, "План",  xl.valueFmt)
    for xc in range(1, len(plan) + 1):
        fmt = xl.valueFmt

        if priority[xc] > 0:
            fmt = xl.priorityValFmt

        sheet.write(crow, xc, plan[xc-1],  fmt)

    crow += 1

    sheet.write(crow, 0, "Дефицит",  xl.valueDeficitFmt)

    for xc in range(1, len(plan) + 1):
        sheet.write_formula(crow, xc, '{=%s-%s}' % (xl_rowcol_to_cell(crow-2, xc),
            xl_rowcol_to_cell(crow-1, xc)), xl.valueFmt)
        
        sheet.conditional_format(xl_rowcol_to_cell(crow, xc), {'type':     'cell',
                                        'criteria': '>=',
                                        'value':    0,
                                        'format':   xl.valueDeficitGreenFmt}) 

        sheet.conditional_format(xl_rowcol_to_cell(crow, xc), {'type':     'cell',
                                        'criteria': '>=',
                                        'value':    -2,
                                        'format':   xl.valueDeficitOrangeFmt})          

        sheet.conditional_format(xl_rowcol_to_cell(crow, xc), {'type':     'cell',
                                        'criteria': '<=',
                                        'value':    -3,
                                        'format':   xl.valueDeficitRedFmt})                                                                                  
    
    xl.printHead1(0, head1, sheet)
    xl.printHead2(1, head2, priority)

    xx = 1
    prev = ''
    for x in range(1, len(head2)):
        if prev == "":
            prev = head2[x]
        else:
            if prev == head2[x] and x == len(head2)-1:
                xl.setBoderOnRange(sheet, 1, xx, crow, x)
                xx = x

            elif prev != head2[x]:
                prev = head2[x]

                if (x - xx) > 1:
                    xl.setBoderOnRange(sheet, 1, xx, crow, x-1)   

                xx = x 
                    

        prev = head2[x]

    xl.printHead(2, head3)

    sheet.set_row(1, 170)
    sheet.set_row(2, 61)

    xl.toObject(server)

def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s',
                        datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)
    logging.info('start report')

    params = server.Params[0]
    logging.info("params " + str(params))

    data = loadData(params, server)
    printOut(params, data, 'representation_report.xlsx', server)

    logging.info('end')

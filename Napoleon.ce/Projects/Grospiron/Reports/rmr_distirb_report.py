from xlbuilder import XlBuilder

import sys
import datetime
from datetime import date, timedelta
import logging

from xlsxwriter.worksheet import Worksheet

from common import daterange, unpackUserid

class DocData:
    def __init__(self, src) -> None:
        self.city = src.city
        self.agent = src.agent
        self.brand = src.brand
        self.name = src.name
        self.address = src.address2
        self.date = src.created
        self.formatTT = src.formatTT

        self.items : set[str]
        self.items = set()
        for ii in src.items:
            self.items.add(ii.id)

class ReportData:
    def __init__(self, agents:list[str], matrix:list[object], price:dict[str,object], data:dict[date,dict[str,list[DocData]]]) -> None:
        self.agents = agents
        self.matrix : dict[str,set[str]]
        self.matrix = dict()

        for m in matrix:
            items = set()
            for mi in m.items:
                items.add(mi.id)
            self.matrix[m.name] = items

        self.price = price
        self.data = data

def loadData(params, server):
    price = server.Get("ManagerPrice", "", "id")
    matrix = server.Get("Matrix", "")

    stmt = '''
select d.name, d.address2, d.city, d.brand, d.created, d.agent, d.formatTT, di.id as id_i 
from
 (select d.id, d.userid, d.created, d.date, a.name as agent, o.name, o.city, o.address2, o.brand, o.formatTT from "OrgRemnants" d, Org o, Agents a
    where d.id = o.id and d.userid = a.id) d,
 (select id, OrgRemnants$userid as userid, OrgRemnants$date as created from OrgRemnants$items where qty > 0) di
where d.userid = di.userid and d.date = di.created
      and d.created >= ToDate("{0}") and d.created < ToDate("{1}") and d."userid" in ({2})
order by d.userid, d.created    
  '''.format(
        params.start.strftime('%d.%m.%Y'),
        (params.finish + datetime.timedelta(days=1)).strftime('%d.%m.%Y'),
        unpackUserid(params.userids))
    
    data : dict[date,dict[str,list[DocData]]]
    data = dict()

    agents = list()
    docs = server.Query(stmt, 'Docs[name:s,address2:s,city:s,brand:s,created:dt,agent:s,formatTT:s,items(userid,created)[id@id_i:s]]')

    for d in docs:
        cdate = d.created.date()
        agent = d.agent
        if not agent in agents: agents.append(agent)

        if not cdate in data:
            data[cdate] = dict()

        agentData = data[cdate]
        if not agent in agentData:
            agentData[agent] = list()

        agentData[agent].append(DocData(d))

    return ReportData(sorted(agents), matrix, price, data)

def printOut(params, data:ReportData, name:str, server):
    def printHead(crow1, crow2):
        head = ['Город',"Мерчендайзер","Наименование торговой сети","Наименование ТТ","Адрес","Дата отчета"]
        xl.sheet = sheet
        xl.printHead(crow1, head + ['SKU','Наличие на полке'])
        crow1 += 1

        xl.sheet = sheet2
        xl.printHead(crow2, head + ['План по SKU', 'Наличие на полке'])
        crow2 += 1

        return (crow1, crow2)

    def getDocMatrix(doc:DocData) -> set[str]:
        if doc.formatTT in data.matrix:
            return data.matrix[doc.formatTT] 

        return data.price.keys()

    def printData1(crow, doc:DocData, matrix:set[str],price:dict[str,object], values:list):
        xl.sheet = sheet

        for id in matrix:
            name = id
            if id in price: name = price[id].name

            exists = 'Да' if id in doc.items else 'Нет'

            dvalues = values + [name, exists]
            xl.printValues(crow, dvalues)

            crow += 1
        return crow

    def printData2(crow, doc:DocData, matrix:set[str],price:dict[str,object], values:list):
        xl.sheet = sheet2

        dvalues = values + [len(matrix), len(doc.items & matrix)]
        xl.printValues(crow, dvalues)
        return crow + 1

    def setColumns(sheet:Worksheet):
        sheet.set_column('A:D', 20)
        sheet.set_column('E:E', 40)
        sheet.set_column('F:F', 10)

    xl = XlBuilder(name)
    sheet = xl.sheet
    sheet.name = 'Отчет 1'
    sheet2 = xl.wb.add_worksheet('Отчет 2')

    setColumns(sheet)
    setColumns(sheet2)

    crow1 = 0
    crow2 = 0

    docs = data.data
    for cd in daterange(params.start.date(), params.finish.date()):
        if not cd in docs:
            continue

        crow1, crow2 = printHead(crow1, crow2)

        agentData = docs[cd]
        for agent in data.agents:
            if not agent in agentData:
                continue

            for doc in agentData[agent]:
                values = [doc.city, agent, doc.brand, doc.name, doc.address, doc.date.strftime('%d.%m.%Y')]
                matrix = getDocMatrix(doc)

                crow1 = printData1(crow1, doc, matrix, data.price, values)
                crow2 = printData2(crow2, doc, matrix, data.price, values)




    xl.toObject(server)



def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s',
                        datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)
    logging.info('start report')

    params = server.Params[0]
    logging.info("params " + str(params))

    data = loadData(params, server)
    printOut(params, data, 'DistribReport.xlsx', server)

    logging.info('end')

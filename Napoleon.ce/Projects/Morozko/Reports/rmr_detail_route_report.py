
import logging
import sys

from typing import NewType
from datetime import timedelta, datetime
from importlib import reload
from quest_rep import QuestHelper
from xlbuilder import XlBuilder
from xlsxwriter import worksheet
from xlsxwriter import workbook
from xlsxwriter.format import Format
from xml.dom import xmlbuilder
from xlsxwriter.utility import xl_rowcol_to_cell
from grsoft.route import AgentRoute

Document = NewType('Document', object)
Order = NewType('Order', object)
Visit = NewType('Visit', object)

class ReportData:
  def __init__(self) -> None:
    self.agent: str = ""
    self.rows: list['Row'] = []

  def add(self, doc: Document) -> 'Row':
    ret = Row(doc)
    self.rows.append(ret)
    return ret

class Row:
  def __init__(self, doc: Document) -> None:
    self.start: datetime = None
    self.finish: datetime = None
    self.orders: dict[str,datetime] = {}
    self.pos: int = -1
    self.num: int

    for i in doc.items:
      if i.state != 1:
        continue

      if self.start == None or i.date < self.start:
        self.start = i.date

      if self.finish == None or i.date > self.finish:
        self.finish = i.date  

  def values(self)->list[str]:
    return [self.num,'',self.start, self.finish,'',self.pos, 'Плановый' if self.pos > 0 else 'Внеплановый', self.res,
            self.org, self.address, self.sum]      

def loadData(params, server) -> ReportData:
  ret: ReportData = ReportData()

  if len(params.userids) == 0:
    return ret
  
  uidFIlter = "'" + params.userids[0].id + "'"
  
  routePos:dict[str, int] = {}

  route = AgentRoute(server, params.userids[0].id)

  pos = 1

  for o in route.getDayRoute(params.start):
    routePos[o.id] = pos
    pos += 1

  server.ChangeUser(uidFIlter)
  ret.agent = server.CurrentUser().name
  orgs = server.Get('Org', '', 'id')
  server.RestoreUser()

  start = params.start.strftime("%d/%m/%Y")
  finish = params.start.strftime("%d/%m/%Y 23:59:59")

  ret.start = params.start
  ret.finish = params.finish

  where = '"userid" = {0} and "created" >= ToDate("{1}") and "created" <= ToDate("{2}")'.format(uidFIlter,start, finish)

  scripts = server.Get("ScriptDoc", where)
  orders = server.Get('Order', where)
  visit = server.Get('VisitInfo', where)

  ordersMap: dict[datetime, Order] = {}

  for d in orders:
    ordersMap[d.created] = d

  visitMap: dict[datetime, Visit] = {}

  for d in visit:
    visitMap[d.created] = d

  num: int = 1
  inRoute: int = 0
  cc: list[str]= []
  co: list[str] = []

  for d in scripts:
    oc = 0
    if not d.id in cc:
      cc.append(d.id)

    r = ret.add(d)  
    r.num = num
    num += 1
    comment = ''
    sum = 0

    if d.id in routePos:
      r.pos = routePos[d.id]
      inRoute += 1

    for i in d.items:
      if i.state == 1 and i.type == 'Order':
        oc += 1

        if i.date in ordersMap:
          if not d.id in co:
            co.append(d.id)

          for x in ordersMap[i.date].items:
            sum += x.qty * x.cost

      if i.state == 1 and i.type == 'Visit' and i.date in visitMap :
        if len(comment) > 0 : comment += ', '
        comment += visitMap[i.date].remark

    r.res = 'Зак: %d' % oc if oc > 0 else comment
    r.sum = sum
    r.org = orgs[d.id].name if d.id in orgs else 'Контрагент с кодом %s' % o.id
    r.address = orgs[d.id].address if d.id in orgs else 'Контрагент с кодом %s' % o.id

  ret.progress = inRoute / len(routePos) if len(routePos) > 0 else 0
  ret.order_progress = len(co) / len(cc) if len(cc) > 0 else 0 

  return ret

class XlBuilderEx(XlBuilder):
  def __init__(self, name) -> None:
    super().__init__(name)
    self.time_format: Format = self.wb.add_format({'num_format': 'hh:mm', 'border' : True})
    self.currency_format = self.wb.add_format({'num_format': '#,##0.00', 'border' : True})
    
  def printCellValue(self, crow, ccel, value, format):
    if ccel > 1 and ccel < 5:
      format = self.time_format

    if crow > 6 and ccel == 1:
      self.sheet.write_formula(xl_rowcol_to_cell(crow, ccel), '{=%s-%s}' % (xl_rowcol_to_cell(crow, ccel+1), xl_rowcol_to_cell(crow-1, ccel+2)), self.time_format)
      return
    
    if ccel == 4:
      self.sheet.write_formula(xl_rowcol_to_cell(crow, ccel), '{=%s-%s}' % (xl_rowcol_to_cell(crow, ccel-1), xl_rowcol_to_cell(crow, ccel-2)), self.time_format)
      return
    
    if ccel == 5 and value == -1:
      value = ''

    if ccel == 10:
      format = self.currency_format
    
    self.sheet.write(crow, ccel, value, format)  

def printOut(data:ReportData, name:str, href, server) -> None:
  xl = XlBuilderEx(name)
  percent_format = xl.wb.add_format({'num_format': '0%'})
  time_format: Format = xl.wb.add_format({'num_format': 'hh:mm'})

  head = ['', 'В пути', 'Начало', 'Конец', 'В точке', 'Порядок маршрута', 'П_В', 'Результат', 'Имя_Т Т', 'Адрес_Т Т', 'Итог']
  xl.sheet.write(0, 0, "Маршрут") 
  xl.sheet.write(0, 3, "Выполнение маршрута") 
  xl.sheet.write(0, 6, data.progress, percent_format) 
  xl.sheet.write(1, 3, "Эффективных визитов") 
  xl.sheet.write(1, 6, data.order_progress, percent_format) 
  xl.sheet.write(2, 0, "Агент: %s" % data.agent ) 
  xl.sheet.write(2, 3, "Работа в ТТ:") 

  lastRow = 6 + len(data.rows)

  xl.sheet.write_formula(2,6, '{=SUM(E7:E%d)}' % lastRow, time_format)
  xl.sheet.write_formula(2,7, '{=IFERROR(G3/(D%d-C7),0)}' % lastRow, percent_format)
  xl.sheet.write(3, 3, "В пути:") 
  xl.sheet.write_formula(3,6, '{=SUM(B8:B%d)}' % lastRow, time_format)
  xl.sheet.write_formula(3,7, '{=IFERROR(G4/(D%d-C7),0)}' % lastRow, percent_format)
  xl.sheet.set_column(0, 0, 20)
  xl.sheet.set_column(1, 5, 10)
  xl.sheet.set_column(6, 7, 20)
  xl.sheet.set_column(8, 9, 40)
  
  xl.printHead(5, head)
  row = 6
  for r in data.rows:
    xl.printValues(row, r.values())
    row += 1

  xl.toObject(server)

def run(server):
  logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s',
                      datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)
  logging.info('start')

  params = server.Params[0]
  logging.info("params " + str(params))

  data = loadData(params, server)
  printOut(data, 'visit_script.xlsx', params.hrefBase, server)

  logging.info('end')

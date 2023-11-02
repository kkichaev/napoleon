
import logging
import sys

from typing import NewType
from datetime import timedelta, datetime, time
from importlib import reload
from quest_rep import QuestHelper
from xlbuilder import XlBuilder
from xlsxwriter import worksheet
from xlsxwriter import workbook
from xlsxwriter.format import Format
from xml.dom import xmlbuilder
from xlsxwriter.utility import xl_rowcol_to_cell
from grsoft.route import AgentRoute
from manager import summary

Document = NewType('Document', object)
Order = NewType('Order', object)
Visit = NewType('Visit', object)

class ReportData:
  def __init__(self) -> None:
    self.rows: list['Row'] = []

class Row:
  def __init__(self) -> None:
    self.agent: str = ""
    self.start: str = ""
    self.finish: str = ""
    self.avg: str = ""
    self.plan: int = 0
    self.fact: int = 0
    self.factWithOrders: int = 0
    self.factWithRouteOrders: int = 0
    self.outPlan: int = 0
    self.humanDays: int = 0
    self.distance: int = 0
    self.sum: float

  def values(self)->list[str]:
    return [self.agent, self.start, self.finish, '','', self.avg, self.plan, self.fact, self.factWithOrders, '', 
        self.factWithRouteOrders, '', self.outPlan, self.humanDays, self.distance, self.sum]      

def loadData(params, server) -> ReportData:
  ret: ReportData = ReportData()

  for uid in params.userids:
    row: Row = Row()
    ret.rows.append(row)

    server.ChangeUser("'%s'" % uid.id)
    row.agent = server.CurrentUser().name
    server.RestoreUser()

    s = params.start

    start: dict[datetime, datetime] = {}
    finish: dict[datetime,  datetime] = {}
    worktime: dict[datetime, list[datetime]] = {}

    route: AgentRoute = AgentRoute(server, uid.id)
    plan: int = 0
    fact: int = 0
    factWithOrders: int = 0
    factWithRouteOrders: int = 0
    outPlan: int = 0
    humanDays: int = 0
    distance: int = 0
    sum: float = 0.0

    while s <= params.finish:
      where = '"userid" = {0} and "created" >= ToDate("{1}") and "created" <= ToDate("{2}")'.format(
        "'%s'" % uid.id, s.strftime("%d/%m/%Y"), s.strftime("%d/%m/%Y 23:59:59"))
      scripts = server.Get("ScriptDoc", where)
      orders = server.Get('Order', where)

      ordsMap: dict[datetime, Order] = dict()

      for o in orders:
        ordsMap[o.created] = o

      if len(scripts) > 0 : 
        humanDays += 1
        distance += summary.computeDist(server, s, uid.id) / 1000

      dayRoute = route.getDayRoute(s)
      fact += len(scripts)
      plan += len(dayRoute)
      routeIds: set[str] = set()

      for r in dayRoute:
        routeIds.add(r.id)

      if len(scripts) > 0:
        start[s] = None
        finish[s] = None 
        worktime[s] = []

        for doc in scripts:
          hasOrder: bool = False

          if not doc.id in routeIds: outPlan += 1

          for i in doc.items:
            if i.state != 1: continue

            if start[s] == None or i.date.time() < start[s]:
              start[s] = i.date.time()

            if finish[s] == None or i.date.time() > finish[s]:
              finish[s] = i.date.time() 
          
            worktime[s].append(i.date)

            if i.type == 'Order':
              if not hasOrder: hasOrder = True

              if i.date in ordsMap:
                for oi in ordsMap[i.date].items:
                  sum += (oi.cost * oi.qty)
                  print(oi.cost, oi.qty, sum)

          if hasOrder : 
            factWithOrders += 1

            if doc.id in routeIds: factWithRouteOrders += 1

      s += timedelta(days=1)
    
    row.start = avgTime(start.values())
    row.finish = avgTime(finish.values())

    wt: timedelta = timedelta(0)
    count = 0

    for d in worktime:
      wt += max(worktime[d])-min(worktime[d])
      count += 1

    avg = wt / count if count > 0 else 0
    times = str(avg).split(':')

    if len(times) > 1:
      row.avg = '%s:%s' % (times[0], times[1])

    row.plan = plan  
    row.fact = fact
    row.factWithOrders = factWithOrders
    row.factWithRouteOrders = factWithRouteOrders
    row.outPlan = outPlan
    row.humanDays = humanDays
    row.distance = distance
    row.sum = sum

  ret.rows.sort(key=lambda x: x.agent)
  return ret

def avgTime(times:list[datetime]) -> str:
  sum = timedelta(0)

  for t in times:
    sum += timedelta(hours=t.hour, minutes=t.minute, seconds=t.second)

  tc = len(times)

  if tc > 0:
    avg = sum / tc
    numbers = str(avg).split(':')
    return '%s:%s' % (numbers[0], numbers[1])
  
  return ''

class XlBuilderEx(XlBuilder):
  def __init__(self, name) -> None:
    super().__init__(name)
    self.time_format: Format = self.wb.add_format({'num_format': 'hh:mm', 'border' : True, 'align': 'right'})
    self.currency_format = self.wb.add_format({'num_format': '#,##0.00', 'border' : True})
    self.percent_format = self.wb.add_format({'num_format': '0%', 'border' : True})
    self.text_format = self.wb.add_format({"align": "left", 'border' : True})
    self.distance_format = self.wb.add_format({'num_format': '#0', "align": "left", 'border' : True})
    self.money_format = self.wb.add_format({'num_format': '#,##0.00 [$₽-419]',  'border' : True})
    self.cellFmt.set_align('right')
    
  def printCellValue(self, crow, ccel, value, format):
    if ccel >= 1 and ccel < 4:
      format = self.time_format

    if ccel == 3:
      self.sheet.write_formula(xl_rowcol_to_cell(crow, ccel), '{=ABS(%s-%s)}' % (xl_rowcol_to_cell(crow, ccel-1), xl_rowcol_to_cell(crow, ccel-2)), self.time_format)
      return
    
    if ccel == 4:
      self.sheet.write_formula(xl_rowcol_to_cell(crow, ccel), '{=ABS(%s-%s)}' % (xl_rowcol_to_cell(crow, ccel-1), xl_rowcol_to_cell(crow, ccel+1)), self.time_format)
      return
    
    if ccel == 9:
      self.sheet.write_formula(xl_rowcol_to_cell(crow, ccel), '{=IFERROR(%s/%s,0)}' % (xl_rowcol_to_cell(crow, ccel-1), xl_rowcol_to_cell(crow, ccel-2)), self.percent_format)
      return
    
    if ccel == 11:
      self.sheet.write_formula(xl_rowcol_to_cell(crow, ccel), '{=IFERROR(%s/%s,0)}' % (xl_rowcol_to_cell(crow, ccel-1), xl_rowcol_to_cell(crow, ccel-5)), self.percent_format)
      return
    
    if ccel == 0:
      format = self.text_format

    if ccel == 14:
      format = self.distance_format  

    if ccel == 15:
      format = self.money_format    

    self.sheet.write(crow, ccel, value, format)  

def printOut(data:ReportData, name:str, href, server) -> None:
  xl = XlBuilderEx(name)
  xl.sheet.set_column(0, 0, 30)
  xl.sheet.write(0, 0, "Итоговый отчет") 

  head = ['ТА', 'Начало', 'Конец', 'На маршруте', 'Передвижения', 'Ср время визита',
           'План маршрута ТТ', 'Факт визиты', 'Эфф. Визиты всех ТТ', 'Strike Rate всех тт', 'Эфф. визиты плановых ТТ', 'Strike Rate плановых ТТ ',
           'Внеплан. ТТ','Человеко-дни', 'Средний пробег в день', 'Сумма Руб']
  
  xl.printHead(2, head)

  row = 3
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

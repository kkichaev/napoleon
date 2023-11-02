
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

Document = NewType('Document', object)

class ReportData:
  def __init__(self) -> None:
    self.rows : dict[str, Row]= {}
    self.agents : dict[str, str] = {}

  def head(self)-> dict[str]:
    ret : list[str]= []

    s = self.start
    f = self.finish + timedelta(days=1)
    
    while s < f:
      ret.append(s.strftime("%d.%m"))
      s += timedelta(days=1)

    return ret
  
  def keys(self)-> list[datetime]:
    ret = []

    s = self.start
    f = self.finish + timedelta(days=1)
    
    while s < f:
      ret.append(s)
      s += timedelta(days=1)

    return ret

  def add(self, doc : Document) -> None:
    if not doc.userid in self.rows:
      self.rows[doc.userid] = Row(self, self.agents[doc.userid])

    self.rows[doc.userid].add(doc)

  def getRows(self) -> list['Row']:
    ret = []

    for r in self.rows.values():
      ret.append(r)

    ret.sort(key=lambda x: x.agent)

    return ret

class Data:
  def __init__(self, report : ReportData = None, agent = None) -> None:
    self.start : datetime = None
    self.finish : datetime = None
    self.visited : list[str] = []
    self.order : list[str]= []
    self.sum : float = 0
    self.report : ReportData = report
    self.agent = agent

  def getOrderSum(self, created : datetime, userid : str) -> float:
    order = None
    ret = 0

    if userid in self.report.orders and created in self.report.orders[userid]:
      order = self.report.orders[userid][created]

    if order != None:
      for i in order.items:
        ret += i.cost * i.qty

    return ret
  
  def add(self, doc : Document) -> None:
    
    if not doc.id in self.visited:
      self.visited.append(doc.id)

    for i in doc.items:
      if i.state == 1:
        if self.start == None or self.start > i.date:
          self.start = i.date

        if self.finish == None or self.finish < i.date:
          self.finish = i.date  

        if i.type == 'Order' and not doc.id in self.order:
          self.order.append(doc.id)
          self.sum = self.getOrderSum(i.date, doc.userid)

class Row:
  def __init__(self, report : ReportData, agent : str) -> None:
    self.data : dict[str, Data] = {}
    self.agent : str = agent
    self.report : ReportData = report

  def add(self, doc : Document):
    key = doc.created.strftime("%d/%m/%Y")

    if not key in self.data:
      self.data[key] = Data(self.report, self.agent)

    self.data[key].add(doc) 

  def get(self, date : datetime) -> list[Data]:    
    key = date.strftime("%d/%m/%Y")

    if not key in self.data:
      return None

    return self.data[key] 

def loadData(params, server) -> ReportData:
  uids = []
  uidFIlter = ''
  for uid in params.userids:
    uids.append(uid.id)
    uidFIlter += "'" + uid.id + "',"

  uidFIlter = uidFIlter[:-1]
  start = params.start.strftime("%d/%m/%Y")
  finish = params.finish.strftime("%d/%m/%Y 23:59:59")

  ret = ReportData()
  ret.start = params.start
  ret.finish = params.finish

  where = '"userid" in ({0}) and "created" >= ToDate("{1}") and "created" <= ToDate("{2}")'.format(uidFIlter,start, finish)
      
  scripts = server.Get("ScriptDoc", where)
  orders = server.Get('Order', where)

  ret.orders = {}

  for d in orders:
    if not d.userid in ret.orders:
      ret.orders[d.userid] = {}

    ret.orders[d.userid][d.created] = d

  for d in scripts:
    if not d.userid in ret.agents:  
      server.ChangeUser("'" + d.userid + "'")
      ret.agents[d.userid] = server.CurrentUser().name
      server.RestoreUser()

    ret.add(d)  
      
  return ret

def printOut(data:ReportData, name:str, href, server) -> None:
  xl = XlBuilder(name)
  
  head = ['ТА', 'Данные']
  head.extend(data.head())
  xl.sheet.write(0, 0, "Динамика по дням") 
  xl.sheet.write(1, 2, "Дата") 

  xl.printHead(2, head)

  xl.sheet.set_column(0, 1, 20)
  

  data_title_format = xl.wb.add_format(
    {
        "align": "center",
    }
  )

  merge_format = xl.wb.add_format(
    {
        "align": "center",
        "valign": "vcenter",
    }
  )

  value_format = xl.wb.add_format(
    {
        "align": "right",
    }
  )
  
  row = 3
  c = 0
  
  for i in data.getRows():
    xl.sheet.merge_range(row, 0, row + 5, 0, i.agent, merge_format)
    xl.sheet.write(row, 1, "Факт. Визиты", data_title_format) 
    xl.sheet.write(row + 1, 1, "ЭФФ Визиты ТА", data_title_format) 
    xl.sheet.write(row + 2, 1, "Strike Rate ", data_title_format) 
    xl.sheet.write(row + 3, 1, "Начало Визиты", data_title_format) 
    xl.sheet.write(row + 4, 1, "Конец Визиты", data_title_format) 
    xl.sheet.write(row + 5, 1, "Сумма РУБ", data_title_format) 

    c = 2

    for k in data.keys():
      d = i.get(k)

      if d:
        xl.sheet.write(row, c, len(d.visited), value_format) 
        xl.sheet.write(row + 1, c, len(d.order), value_format) 
        xl.sheet.write_formula(row+2, c, '{=IFERROR(ROUND(%s/%s*100,0), 0)}' % (xl_rowcol_to_cell(row+1, c), xl_rowcol_to_cell(row, c)), value_format)
        xl.sheet.write(row + 3, c, d.start.strftime("%H:%M"), value_format) 
        xl.sheet.write(row + 4, c, d.finish.strftime("%H:%M"), value_format) 
        xl.sheet.write(row + 5, c, d.sum, value_format) 

      c += 1
    row += 6

  xl.setBoderOnRange(xl.sheet,3,0,row,c)
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

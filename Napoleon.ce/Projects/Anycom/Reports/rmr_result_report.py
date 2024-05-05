
import logging
import sys

from typing import NewType
from datetime import timedelta, datetime, time, date
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
    self.onRoute = ""
    self.movement = ""
    self.avg: str = ""
    self.plan: int = 0
    self.fact: int = 0
    self.factWithOrders: int = 0
    self.factWithRouteOrders: int = 0
    self.outPlan: int = 0
    self.humanDays: int = 0
    self.distance: int = 0
    self.sum: float = 0

  def values(self)->list[str]:
    return [self.agent, self.start, self.finish, self.onRoute, self.movement, self.avg, self.plan, self.fact, self.factWithOrders, '', 
        self.factWithRouteOrders, '', self.outPlan, self.humanDays, self.distance, self.sum]      

def secondsToTime(secs : float) ->time:
  h = int(secs // 3600)
  ms = int(secs % 3600)
  return time(h, ms // 60, ms % 60, 0)

def timeToSeconds(dt:datetime or time) -> int:
  tm = dt.time() if isinstance(dt, datetime) else dt
  return tm.hour * 3600 + tm.minute* 60 + tm.second

class AgentStat:
  
  def __init__(self, row) -> None:
    self.avg_time :int = int(row.avg_time)
    self.start:time = secondsToTime(row.start)
    self.finish:time = secondsToTime(row.finish)
    self.day_time:int = int(row.day_time)

  def __repr__(self) -> str:
    return "Avg {}, dayTime {}, start {}, finish {}".format(self.avg_time, self.day_time, self.start.strftime('%H:%M'), self.finish.strftime('%H:%M'))

class DocData:
  def __init__(self, row) -> None:
    self.id : str = row.id
    self.sum :float = row.sum

  def __repr__(self) -> str:
    return "Id {}, sum {}" .format(self.id, self.sum)


def loadData(params, server) -> ReportData:
  def agentsStat(stmtDocs:str) ->dict[str,AgentStat] :
    stmt = '''
select avg_time, atime.userid, start, finish, day_time 
from
(-- продолжительность визита
select sum(visit_time) / count(visit_time) avg_time, sdi.userid from (
(select (max("date") - min("date")) / 10000000 as visit_time,"ScriptDoc$userid" userid, "ScriptDoc$created" created 
from "ScriptDoc$items" where "state" = 1 group by userid, created) sdi
inner join
({0}) scr
 on sdi.userid = scr.userid and sdi.created = scr.created
) group by sdi.userid) atime,
(-- начало, конец работы по дням
select sum(start_date) / count(finish_date) as start ,sum(finish_date) / count(finish_date) as finish, sdi.userid
from (
(select ((max("date") / 10000000) % (24 * 3600)) finish_date, ((min("date") / 10000000) % (24 * 3600)) start_date
,"ScriptDoc$userid" userid, "ScriptDoc$created" created, ("ScriptDoc$created" / (10000000 *24 * 3600)) cr_date  
from "ScriptDoc$items" where "state" = 1 group by "ScriptDoc$userid", cr_date) sdi
inner join
({0}) scr
 on sdi.userid = scr.userid and sdi.created = scr.created
) group by sdi.userid) sf,
( -- среднее дневное время 
select sum(day_time) / count(day_time) day_time, userid from (
select sum(visit_time) day_time, sdi.userid, sdi.created / (10000000*24*3600) cr_day from (
(select (max("date") - min("date")) / 10000000 as visit_time,"ScriptDoc$userid" userid, "ScriptDoc$created" created 
from "ScriptDoc$items" where "state" = 1 group by userid, created) sdi
inner join
({0}) scr
 on sdi.userid = scr.userid and sdi.created = scr.created) group by sdi.userid, cr_day
) group by userid) dtm
where atime.userid = sf.userid and dtm.userid = atime.userid
''' .format(stmtDocs)
    
    # print(stmt)
    res : dict[str, AgentStat] = {}
    
    for di in server.Query(stmt, 'AgentStat[userid:s,avg_time:n,start:n,finish:n,day_time:n]') or []:
      res[di.userid] = AgentStat(di)

    return res
  
  def docsStat(where) -> dict[str, dict[date, list[DocData]]]:
    stmt = '''
select sd.id, sd.cr_day * 24 * 3600 * 10000000 cr_day, sd.userid, o.sum
from
(select distinct "id" as id, ("created" / (10000000 *24 * 3600)) cr_day, "userid" as userid from "ScriptDoc"
where {}
) sd
left join
(select sum("qty" * "cost") as sum, "Order$userid" as userid, ("Order$created" / (10000000 * 24 * 3600)) as cr_day, o."id" as id
from "Order$items" oi, "Order" o
where oi."Order$userid" = o."userid" and  "Order$created" = o."created"
group by "Order$userid", cr_day, o."id") o
on sd.userid = o.userid and sd.cr_day = o.cr_day and sd.id = o.id 
order by sd.userid, sd.cr_day
'''.format(where)
    res : dict[str, dict[date, list[DocData]]] = {}
    
    for di in server.Query(stmt, 'DocData[userid:s,id:s,cr_day:dt,sum:n(2)]') or []:
      if not di.userid in res: res[di.userid] = {}
      daysdata = res[di.userid]
      cday = di.cr_day.date()
      if not cday in daysdata: daysdata[cday] = []
      daysdata[cday].append(DocData(di))

    return res
  

  def distance(params, server) -> dict[str, dict[date, float]]:
    startTime = timeToSeconds(params.start)
    finishTime = timeToSeconds(params.finish)
    stmt = '''
select "latitude", "longitude", "userid","date"
from "GPSPos" gp 
where "isGSM" = 0
and "date" >= ToDate('{0}') and "date" <= ToDate('{1}') 
and ("date" % (24 * 3600 * 10000000)) > ({2} * 10000000) and ("date" % (24 * 3600 * 10000000)) < ({3} * 10000000)
and "userid" in ({4})
order by "userid", "date" 
'''.format(
      params.start.strftime("%d/%m/%Y"), params.finish.strftime("%d/%m/%Y 23:59:59")
      ,startTime, finishTime
      ,','.join(["'{}'".format(x.id) for x in params.userids])
    )

    ret :dict[str, dict[date, float]] = {}
    uid = None
    cdate = None
    distance = 0.0
    lastPoint = None
    for di in server.Query(stmt, 'RouteData[userid:s,date:dt,latitude:n(5),longitude:n(5)]') or []:
      if di.userid != uid:
        if cdate: ret[uid][cdate] = distance

        uid:str = di.userid
        ret[uid] = {}
        cdate = None
      
      dt = di.date.date()
      if dt != cdate:
        if cdate: ret[uid][cdate] = distance
        distance = 0.0
        cdate = dt
        lastPoint = None

      if lastPoint: 
        cdist = server.EathDistance(lastPoint[0], lastPoint[1], di.latitude, di.longitude)
        distance += cdist
      lastPoint = (di.latitude, di.longitude)

      
    if cdate: ret[uid][cdate] = distance

    return ret
  
  def setStat(uid:str, row:Row):
    if uid in agentStat:
      stat = agentStat[uid]
      row.start = stat.start.strftime('%H:%M')
      row.finish = stat.finish.strftime('%H:%M')
      secs = timeToSeconds(stat.finish) - timeToSeconds(stat.start)
      
      ms = secs % 3600
      row.onRoute = "{:0>2}:{:0>2}".format(secs // 3600, ms // 60)

      secs = secs - stat.day_time
      ms = secs % 3600
      row.movement = "{:0>2}:{:0>2}".format(secs // 3600, ms // 60)

      secs = stat.avg_time
      ms = secs % 3600
      row.avg = "{:0>2}:{:0>2}".format(secs // 3600, ms // 60)
  
  
  def setRouteDate(uid:str, row:Row, route:AgentRoute, start:datetime, finish:datetime):
    if not uid in docData:
      return

    adata = docData[uid]
    rdata :dict[date,float] = agentDst[uid] if uid in agentDst else {}

    movements:list[float] = []
    routePlane = 0
    days = 0
    ordSum = 0
    fact = 0
    orderFact = 0
    factRoute = 0
    orderRoute = 0
    outPlan = 0

    ct = start.date()
    while ct <= finish.date():
      orgs = route.getDayRoute(ct)
      oset = set()
      for oi in orgs: oset.add(oi.id)

      routePlane += len(oset)
      if ct in adata:
        days += 1
        if ct in rdata:
          movements.append(rdata[ct])

        dayDocs = adata[ct]
        fact += len(dayDocs)
        for di in dayDocs:
          ordSum += di.sum
          if di.sum > 0: orderFact += 1

          if di.id in oset:
            factRoute += 1
            if di.sum > 0: orderRoute += 1
          else: outPlan += 1


      ct = ct + timedelta(days=1)

    row.plan = routePlane
    row.fact = fact
    row.factWithOrders = orderFact
    row.factWithRouteOrders = orderRoute
    row.outPlan = outPlan
    row.humanDays = days
    row.sum = ordSum
    row.distance = 0 if len(movements) == 0 else (sum(movements) / len(movements)) / 1000


  where = '"userid" in ({0}) and "created" >= ToDate("{1}") and "created" <= ToDate("{2}")'.format(
        ','.join(["'{}'".format(x.id) for x in params.userids])
        ,params.start.strftime("%d/%m/%Y")
        ,params.finish.strftime("%d/%m/%Y 23:59:59")
  )

#   docsStmt = '''
# select "Scriptdoc$created" created,  "Scriptdoc$userid" userid, max("date") mdate 
# from "ScriptDoc$items"
# where {}
# group by "Scriptdoc$userid", "Scriptdoc$created" 
# having (max("date") / (10000000 * 24 * 3600) = "Scriptdoc$created" / (10000000 * 24 * 3600))''' . format(where)
  docsStmt = '''
select "Scriptdoc$created" created,  "Scriptdoc$userid" userid, max("date") mdate 
from "ScriptDoc$items"
where {}
group by "Scriptdoc$userid", "Scriptdoc$created" ''' . format(where)
  agentStat = agentsStat(docsStmt)
  docData = docsStat(where)
  agentDst = distance(params, server)
  agents = server.Get('Agents','','id')

  ret = ReportData()
  for a in params.userids:
    uid = a.id
    name = agents[uid].name if uid in agents else '<{}>' .format(uid)

    row  = Row()
    row.agent = name
    setStat(uid, row)

    route: AgentRoute = AgentRoute(server, uid)
    setRouteDate(uid, row, route, params.start, params.finish)

    ret.rows.append(row)


  # print('Stat',agentStat)
  # print('Docst', docData)
  # print('Distance',distance)

  return ret


class XlBuilderEx(XlBuilder):
  def __init__(self, name) -> None:
    super().__init__(name)
    self.time_format: Format = self.wb.add_format({'num_format': 'hh:mm', 'border' : True, 'align': 'right'})
    self.currency_format = self.wb.add_format({'num_format': '#,##0.00', 'border' : True})
    self.percent_format = self.wb.add_format({'num_format': '0%', 'border' : True})
    self.text_format = self.wb.add_format({"align": "left", 'border' : True})
    self.distance_format = self.wb.add_format({'num_format': '#0', "align": "right", 'border' : True})
    self.money_format = self.wb.add_format({'num_format': '#,##0.00 [$₽-419]',  'border' : True})
    self.cellFmt.set_align('right')
    self.time_format_red = self.wb.add_format({'num_format': 'hh:mm', 'border' : True, 'align': 'right', 'font_color':'#9C0006'})
    
  def printCellValue(self, crow, ccel, value, format):
    if ccel >= 1 and ccel < 4:
      format = self.time_format

    # if ccel == 3:
    #   self.sheet.write_formula(xl_rowcol_to_cell(crow, ccel), '{=ABS(%s-%s)}' % (xl_rowcol_to_cell(crow, ccel-1), xl_rowcol_to_cell(crow, ccel-2)), self.time_format)
    #   return
    
    # if ccel == 4:
    #   self.sheet.write_formula(xl_rowcol_to_cell(crow, ccel), '{=ABS(%s-%s)}' % (xl_rowcol_to_cell(crow, ccel-1), xl_rowcol_to_cell(crow, ccel+1)), self.time_format)
    #   return
    
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
  xl.sheet.set_column(15, 15, 13)
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

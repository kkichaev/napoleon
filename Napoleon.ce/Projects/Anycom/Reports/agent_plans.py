import logging
import sys

from xlbuilder import XlBuilder
from datetime import datetime
import calendar

from xlsxwriter.utility import xl_rowcol_to_cell

class FolderTree:

  def __init__(self, server) -> None:
    self.fldIndex : dict[str,int] = {}
    self.folders = server.Get('ManagerFolder', '') 
    idx = 0    
    for fi in self.folders:
      self.fldIndex[fi.fid] = idx
      idx += 1

  def getName(self, id:str) -> str:
    if not id in self.fldIndex: return id
    return self.folders[self.fldIndex[id]].name

  def getFolders(self, id:str) -> set[str] :
    ret : set[str] = set()

    if not id in self.fldIndex: return ret
    
    idx = self.fldIndex[id]
    cf = self.folders[idx]
    ret.add(cf.fid)

    level = cf.level
    while idx < len(self.fldIndex) - 1:
      idx+=1
      cf = self.folders[idx]
      if cf.level <= level: break
      ret.add(cf.fid)

    return ret


class PlanRow:
  def __init__(self, src, folders:FolderTree) -> None:
    self.id:set[str] = folders.getFolders(src.id)
    self.name = folders.getName(src.id)
    self.akb:int = (int)(src.akb + 0.05)
    self.order:float = src.order
    self.f_akb:set[str] = set()
    self.f_order = 0.0

  def addDlv(self, src):
    if src.fid in self.id:
      self.f_akb.add(src.oid)
      self.f_order += src.sum

  def __repr__(self) -> str:
    return "akb {}/{} order {}/{}".format(self.akb, len(self.f_akb), self.order, self.f_order)


class PlanData:
  def __init__(self, server) -> None:
    self.agents = server.Get('Agents', '')
    self.plans : dict[str, list[PlanRow]] = {}

  def addPlan(self, src, folders:FolderTree):
    if not src.userid in self.plans:
      self.plans[src.userid] :list[PlanRow] = []

    plans = self.plans[src.userid]
    for si in src.plans:
      plans.append(PlanRow(si, folders))

  def addDlv(self, src, uids:set[str]):
    for userid in uids:
      if userid in self.plans:
        for pi in self.plans[userid]:
          pi.addDlv(src)

  def __repr__(self) -> str:
    return self.plans.__repr__()

def add_months(sourcedate, months):
    month = sourcedate.month - 1 + months
    year = sourcedate.year + month // 12
    month = month % 12 + 1
    day = min(sourcedate.day, calendar.monthrange(year,month)[1])
    return datetime(year, month, day)

def loadData(params, server) -> PlanData:
  start = datetime(params.start.year, params.start.month, 1)
  finish = add_months(start, 1)

  uids = ','.join(["'" + x.id + "'" for x in params.userids])
  where = "[begin] >= ToDate('{}') and [begin] < ToDate('{}') and userid in ({})".format (
     start.strftime("%d/%m/%Y")
     ,finish.strftime("%d/%m/%Y")
     ,uids
  )

  res = PlanData(server)

  folders = FolderTree(server)
  for pi in server.Get('AgentPlan', where):
    res.addPlan(pi, folders)
  
  adata : dict[str, set[str]] = {}
  for ai in server.Get('AgentData', "[type]='Org'"):
    if not ai.id in adata: adata[ai.id] = set()
    adata[ai.id].add(ai.userid)

  stmt = '''
select p.fid, sum([sum]) [sum], oid from
(select di.id, sumWOTax [sum], d.id as oid from Delivery d, Delivery$items di
where d.uid = di.Delivery$uid 
and d.date >= ToDate('{}') and d.date <= ToDate('{}')) d, 
Price p where p.id = d.id group by oid, p.fid''' . format (
  start.strftime("%d/%m/%Y")
  ,finish.strftime("%d/%m/%Y"))

  for di in server.Query(stmt, 'DlvStat[fid:s,oid:s,sum:n(2)]'):
    if di.oid in adata:
      res.addDlv(di, adata[di.oid])


  # print(res)

  return res

def printOut(data:PlanData, params, server):
  def writeSum(crow:int, cc:int, lenPlans:int, format=None) -> None :
    xlb.sheet.write_formula(crow, cc, '=SUM({}:{})'.format(
      xl_rowcol_to_cell(crow+1, cc)
      ,xl_rowcol_to_cell(crow+lenPlans, cc))
      ,cell_format=format
    )
    xlb.setBoderOnRange
    
  def writePrc(crow:int, cc:int, format=None) -> None :
    xlb.sheet.write_formula(crow, cc, '={}/{}'.format(
      xl_rowcol_to_cell(crow, cc-1)
      ,xl_rowcol_to_cell(crow, cc-2))
      ,cell_format=format
    )

  xlb = XlBuilder("agent_plans.xlsx"
                  , bg_head='#bcd6ee'
                  ,border_color='#BFBFBF'
  )
  pcFormat = xlb.wb.add_format({"num_format": "0%", 'font_size':'9', 'border':True, 'border_color':'#BFBFBF'})
  cellFormat = xlb.wb.add_format({'font_size':'9', 'border':True, 'border_color':'#BFBFBF'})
  agentFmt = xlb.wb.add_format({"bold": True, 'bg_color' : '#deebf6', 'border':True, 'border_color':'#BFBFBF'})
  rightAlign = xlb.wb.add_format({"align": "right", 'font_size':'9'})
  agentPrcFmt = xlb.wb.add_format({"bold": True, 'bg_color' : '#deebf6', 'border':True, 'border_color':'#BFBFBF',"num_format": "0%"})
  
  xlb.sheet.write(0, 0, "План по продажам за {}" . format(params.start.strftime('%m.%Y')))

  crow = 1
  head = ['ФИО агента', 'План продаж, руб', 'Сумма факт.выполнения ', 'План продаж % выполнения', 'АКБ', 'Факт.выполнения АКБ', 'АКБ % Выполнения']
  xlb.printHead(crow, head)
  crow += 1

  for agent in sorted(data.agents, key=lambda x:x.name):
    if not agent.id in data.plans: continue
    agentPlans = data.plans[agent.id]

    values = [agent.name]
    xlb.printValues(crow, values, format=agentFmt)
    for clmn in [1,2,4,5]:
      writeSum(crow, clmn, len(agentPlans), format=agentFmt)

    for clmn in [3,6]: writePrc(crow, clmn, format=agentPrcFmt)

    grpOptions = {'level':1}
    crow += 1
    for pi in agentPlans:
      values = [(pi.name,rightAlign), pi.order, pi.f_order, '', pi.akb, len(pi.f_akb), '']
      xlb.printValues(crow, values, format=cellFormat)
      for clmn in [3,6]: 
        if (clmn == 3 and pi.order > 0) or (clmn ==6 and pi.akb > 0):
          writePrc(crow, clmn, format=pcFormat)
      xlb.sheet.set_row(crow, None, None, grpOptions)
      crow += 1

  xlb.sheet.set_column('A:A', 24)
  xlb.sheet.set_column('B:B', 16)
  xlb.sheet.set_column('C:C', 12)
  xlb.sheet.set_column('D:G', 8)
  xlb.toObject(server)
  
def run(server):
  logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)    
  logging.info('start report')

  params = server.Params[0]
  logging.info("params " + str(params))

  data = loadData(params, server)
  printOut(data, params, server)

  logging.info('end')
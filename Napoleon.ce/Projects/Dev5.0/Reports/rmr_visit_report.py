import logging
import sys

from grsoft.reports.xlbase import XlBuilder
from grsoft.reports import makeDocFilter
from grsoft.objects.documents import makeDocQuery


def loadData(params, server):
  where = makeDocFilter(params)

  stmt = makeDocQuery(where)

  allStmt = '''
    SELECT docs.*, o."name" as org_name, sd.script_name, sd.scitem_name, a."name" as agent, sci.route as route
    FROM ({0}) docs LEFT JOIN "Org" o on docs.id = o."id" 
    LEFT JOIN 
    (SELECT sdf."name" as script_name, sdfi."name" as scitem_name, sdi."date" as created, sd."userid" as userid
      FROM "ScriptDef" sdf, "ScriptDef$items" sdfi, "ScriptDoc" sd, "ScriptDoc$items" sdi
      WHERE sdf."id" = sd."scriptId" and sdf."id" = sdfi."ScriptDef$id" and sd."created" = sdi."ScriptDoc$created" and sd."userid" = sdi."ScriptDoc$userid" and sdfi."id" = sdi."itemID"
    ) sd ON docs.created = sd.created and docs.userid = sd.userid
    LEFT JOIN "Agents" a on docs.userid = a."id"
    LEFT JOIN (select 1 as route, ("Schedule$date" / 10000000)/(24 * 3600) as date, "id" as id, "Schedule$userid" as userid from "Schedule$items") sci 
    on docs.cr_day = sci.date and docs.userid = sci.userid and docs.id = sci.id
    ORDER BY docs.userid, docs.created
    '''.format(stmt)
  
  docs = server.Query(allStmt, 
     'Docs[agent:s,userid:s,docs(userid)[org_name:s,script_name:s,scitem_name:s,created:dt,date:dt,remark:s,sended:dt,sum:n(2),docname:s,route:n]]')
  # print("S1", stmt.encode('utf-8'))


  return docs or []


def printOut(data, params, server):
  xl = XlBuilder('visits.xlsx')
  
  xl.formats.cell.set_text_wrap(True)

  DATE_FMT = "%d.%m.%Y %H:%M"
  for a in data :
    agent = a.agent if len(a.agent) > 0 else '<{0}>'.format(a.userid)
    sheet = xl.addWorkSheet(agent)


    sheet.set_column('A:A', 16)
    sheet.set_column('B:B', 60)
    sheet.set_column('C:D', 30)
    sheet.set_column('E:E', 6)
    sheet.set_column('F:F', 16)
    sheet.set_column('G:G', 10)
    sheet.set_column('H:H', 26)

    crow = xl.printTitle('Отчет по посещениям {0}'.format(agent), params, 0)
  
    head = ["Дата", "Контрагент", "Тип посещения", "Сценарий", "План", "Дата передачи", "Сумма", "Комментарий"]
    xl.printHead(crow, head)
    crow += 1

    print('Docs', len(a.docs))
    for d in a.docs:
      visitType = d.scitem_name if len(d.scitem_name) > 0 else d.docname
      plan = 'нет' if d.route != 1 else ''
      sum = '' if d.sum == 0 else d.sum
      data = [d.created.strftime(DATE_FMT), d.org_name, visitType, d.script_name, plan, d.sended.strftime(DATE_FMT), 
              (sum, xl.formats.sum), d.remark]
      xl.printValues(crow, data)
      crow += 1

  xl.toObject(server)

def run(server):
  logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)    
  logging.info('start report')

  params = server.Params[0]
  logging.info("params " + str(params))

  data = loadData(params, server)
  printOut(data, params, server)

#   XLBuilder().workbookToObject(wb, "rmr_visit_report.xlsx", server)                
  logging.info('end')

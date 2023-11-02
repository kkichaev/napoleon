import logging
import sys
from grsoft.reports import makeDocFilter
from grsoft.reports.xlbase import XlBuilder


def loadData(params, server):
  where = makeDocFilter(params, "ot")

  stmt = '''
  SELECT ot."userid", ot."created", "finish", "orgid", "start", "text" ,td."created" as tdcreated, td."remark" as tdremark, td."userid" as tduser
    , o."name" as oname, o."address" as oaddress, a."name" as aname, dm."name" as mname
  FROM "OrgTask" ot
  LEFT JOIN "TaskDone" td on ot."id" = td."idTask"
  LEFT JOIN "Org" o on ot."orgid" = o."id"
  LEFT JOIN "Agents" a on ot."userid" = a."id"
  LEFT JOIN "DivisionManager" dm on ot."manager" = dm."id"
  WHERE {0}
  ORDER BY ot."userid"
  '''.format(where)

  docs = server.Query(stmt, 'DTd[userid:s,created:dt,finish:dt,orgid:s,start:dt,text:s,tdcreated:dt,tdremark:s,tduser:s,oname:s,oaddress:s,aname:s,mname:s]') or []
  return docs

def printOut(data, params, server):
  xl = XlBuilder('tasks.xlsx')

  xl.formats.cell.set_text_wrap(True)

  cuser = None
  crow = 0
  for d in data:
    if d.userid != cuser:
      cuser = d.userid

      sheet = xl.addWorkSheet(d.aname)
      sheet.set_column('A:B', 40)
      sheet.set_column('C:C', 10)
      sheet.set_column('D:D', 15)
      sheet.set_column('F:F', 15)
      sheet.set_column('G:G', 15)
      sheet.set_column('H:H', 10)
      sheet.set_column('I:I', 15)
      sheet.set_column('J:J', 10)

      crow = xl.printTitle('Отчет по задачам {0}'.format(d.aname), params, 0)
      
      heads = ["Название магазина", "Адрес", "Период", "Задача", "Выполнена (да /нет)", "Комментарий сотрудника", 'Агент', 
               'Дата выполнения', 'Постановщик', 'Дата   создания']
      xl.printHead(crow, heads)
      crow += 1
    
    ps = "{0}-{1}".format(d.start.strftime("%d.%m.%Y"), d.finish.strftime("%d.%m.%Y"))
    isDone = 'Да' if len(d.tduser) > 0 else ''
    dDone = d.tdcreated.strftime("%d.%m.%Y") if len(d.tduser) > 0 else ''
    values = [d.oname, d.oaddress, ps, d.text, isDone, d.tdremark, d.aname, dDone,  d.mname, d.created.strftime("%d.%m.%Y")]
    xl.printValues(crow, values)
    crow += 1

  xl.toObject(server)

def run(server):
  logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)    
  logging.info('start report')

  params = server.Params[0]
  logging.info("params " + str(params))

  data = loadData(params, server)
  printOut(data, params, server)

  logging.info('end')
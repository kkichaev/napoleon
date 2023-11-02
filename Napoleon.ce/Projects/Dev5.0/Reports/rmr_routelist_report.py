import logging
import sys
from grsoft.reports.xlbase import XlBuilder

class ItemsData:
    pass

class AgenPage:
    def __init__(self) -> None:
        self.weeks = {}

    def add(self, doc):
        if not doc.weekIndex in self.weeks:
            self.weeks[int(doc.weekIndex)] = [None] * 7

        dayIdx = int(doc.dayOfWeek)
        if dayIdx == 0 : dayIdx = 7
        dayIdx = dayIdx - 1
        self.weeks[int(doc.weekIndex)][dayIdx] = []

        for i in doc.items:
            self.weeks[int(doc.weekIndex)][dayIdx].append(i.name)

class ReportData:
    def __init__(self) -> None:
        self.pages = {}

    def add(self, doc):
        if not doc.agent in self.pages:
            self.pages[doc.agent] = AgenPage()

        self.pages[doc.agent].add(doc)    

def loadData(arg, server)->ReportData:
    usrids = ','.join(["'"+x.id+"'" for x in arg.userids])

    stmt = '''
        SELECT i."RouteTemplate$weekIndex" as weekIndex, i."RouteTemplate$dayOfWeek" as dayOfWeek, i."RouteTemplate$userid" as userid, 
            o."name" as org, ag."name" as agent
            FROM "RouteTemplate$items" as i 
            LEFT JOIN "Org" as o on i."id" = o."id" 
            LEFT JOIN "Agents" as ag on i."RouteTemplate$userid" = ag."id"
            WHERE i."RouteTemplate$userid" in ({0})
            ORDER BY ag."name", i."RouteTemplate$weekIndex", i."RouteTemplate$dayOfWeek", i."index"
    '''.format(usrids)

    docs = server.Query(stmt, 'Ds[weekIndex:n,dayOfWeek:n,userid:s,agent:s,items(weekIndex,dayOfWeek,userid)[name@org:s]]')
    
    res = ReportData()

    for d in docs:
        res.add(d)

    return res

def printOut(data:ReportData, name:str, server) -> None:
    xl = XlBuilder(name)
    head = ["№","Понедельник","Вторник","Среда","Четверг","Пятница","Суббота","Воскресенье"]

    for agent in dict(sorted(data.pages.items())):
        sheet = xl.addWorkSheet(agent)
        sheet.set_column(0, 0, 5)
        sheet.set_column(1, 8, 24)

        row = xl.printTitle("Маршуртный лист: {0}".format(agent), None, 0)

        weeks = dict(sorted(data.pages[agent].weeks.items()))
        # row = 2
        
        fmt = xl.wb.add_format()
        fmt.set_text_wrap(True)

        for wx in weeks:
            sheet.write(row,0,"Неделя: {0}".format(wx), xl.formats.normal)
            row += 1
            xl.printHead(row, head)
            row += 1
            route = weeks[wx]
            maxRow = row

            for dx in range(0,len(route)):
                idx = 1
                rrow = row
                rt = route[dx]
                if rt == None : continue

                for r in rt:
                    sheet.write(rrow,0,idx)
                    sheet.write(rrow,dx + 1,r,fmt)
                    rrow += 1
                    idx += 1

                if maxRow < rrow : maxRow = rrow

            xl.setBoderOnRange(sheet, row, 0, maxRow, 8)
            row = maxRow + 1    
    
    xl.toObject(server)

def run(server):
  logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s',
                      datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)
  logging.info('start')

  params = server.Params[0]
  logging.info("params " + str(params))

  data = loadData(params, server)
  printOut(data, 'rmr_routelist_report', server)

  logging.info('end')
import datetime
import logging
import sys

from xlsxbuilder import XlsxBuilder
from xlsxwriter.utility import xl_col_to_name, xl_rowcol_to_cell

class OrgData:
    def __init__(self, row) -> None:
        self.id : str = row.id
        self.name : str = row.name
        self.address : str = row.address
        self.cid : str = row.cid
        self.visits : set[datetime.date] = set()

    def __hash__(self) -> int:
        return self.id

    def __eq__(self, __o: object) -> bool:
        if isinstance(__o, OrgData) :
            return self.id == __o.id
        return False

    def add(self, visit:datetime.date) -> None:
        self.visits.add(visit)

    def visit(self, date:datetime.date) -> str:
        return 1 if date in self.visits else ""


class Data:
    def __init__(self) -> None:
        self.data : dict[str, OrgData] = {}

    def add(self, row) -> None:        
        if not row.id in self.data:
            self.data[row.id] = OrgData(row)

        self.data[row.id].add(row.created.date())


def loadData(server) ->Data:
    params = server.Params[0]

    uids = ""
    for u in params.userids: uids += "'{0}',".format(u.id)

    stmt = '''select o.id, o.name, o.cid, o.address, s.created from Org o, ScriptDoc s, ScriptDef def
    where o.id = s.id and s.scriptid = def.id
        and "created" >= ToDate("{0}") and "created" < ToDate("{1}")
        and def.cdefid = '{2}'
        and s.userid in ({3})
    '''.format(params.start.strftime("%d.%m.%Y"), (params.finish+datetime.timedelta(days=1)).strftime("%d.%m.%Y"), params.cid, uids[:-1])

    rows = server.Query(stmt, "Data[id:s,name:s,cid:s,address:s,created:dt]")
    
    res = Data()

    for r in rows:
        res.add(r)

    return res

def doReport(server, data:Data):
    params = server.Params[0]
    
    start = params.start.date()
    finish = params.finish.date()+datetime.timedelta(days=1)

    rows = sorted(data.data.values(), key=lambda x: (x.name, x.address))

    dates = []
    x = start
    while x < finish:
        dates.append(x)
        x += datetime.timedelta(days = 1)

    xl = XlsxBuilder('visit_detail')
    head = ['Наименование ТС', 'Город', 'Адрес']
    head.extend(x.strftime("%d.%m.%Y") for x in dates)
    head.append('ИТОГО ВИЗИТОВ')

    crow = 1
    xl.printHead(crow, head)

    startRow = crow + 1
    for r in rows:
        crow += 1

        values = [r.name, r.cid, r.address]
        values.extend(r.visit(x) for x in dates)
        xl.printValues(crow, values)

        xl.sheet.write_formula(crow, len(dates) + 3, 
            '=SUM(%s:%s)' % (xl_rowcol_to_cell(crow, 3), xl_rowcol_to_cell(crow, len(dates) + 1))
            , xl.cellFmt 
        )

    crow += 1
    for x in range(0, len(dates) + 1):
        cc = x + 3
        xl.sheet.write_formula(crow, cc, 
            '=SUM(%s:%s)' % (xl_rowcol_to_cell(startRow, cc), xl_rowcol_to_cell(crow - 1, cc))
            , xl.boldHead 
        )

    sheet = xl.sheet
    sheet.set_column('A:A', 15)
    sheet.set_column('B:B', 40)
    sheet.set_column('C:C', 40)
    sheet.set_column('%s:%s' % (xl_col_to_name(3), xl_col_to_name(3 + len(dates) + 1)), 11)

    sheet.merge_range('A{0}:C{0}'.format(crow+1), 'Общий итог', xl.boldHead)

    xl.toObject(server)

def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)    
    logging.info('start report')
    
    data = loadData(server)
    doReport(server, data)

    logging.info('end')        
import logging
import sys

from grsoft.objects.division import Divisions, Division
from grsoft.objects.documents import makeDocQuery, Order
from grsoft.reports import makeDocFilter
from grsoft.reports.xlbase import XlBuilder


class Scheduler:
    def __init__(self, params, server) -> None:
        where = makeDocFilter(params, None, "Schedule$date", "Schedule$userid")

        stmt = '''
        select "Schedule$userid" as userid, si."id", ("Schedule$date" / 10000000)/(24 * 3600) as day, Schedule$date as crd from "Schedule$items" si
        where {0}
        '''.format(where)

        items:dict[str,dict[int,set[str]]] = {}
        docs = server.Query(stmt, 'SchI[userid:s,day:n,crd:n,items(userid,day)[id:s]]')
        for d in docs:
            if not d.userid in items:
                items[d.userid] = {}
            
            dd = items[d.userid]
            if not d.day in dd:
                dd[d.day] = set()

            els = dd[d.day]
            for di in d.items:
                els.add(di.id)

        self.items = items
        # print(items.keys())
    
class SummaryData:
    def __init__(self) -> None:
        self.visits:int = 0
        self.orders:int = 0
        self.sum:float = 0.0
        
        self.route:int = 0
        self.inRoute:int = 0

    def add(self, src) -> None:
        self.visits += len(src.visited)
        self.orders += src.orders

        self.route += len(src.route)
        inRoute = len(src.route.intersection(src.visited))
        self.inRoute += inRoute

        self.sum += src.sum

    def progress(self) -> float:
        return 0 if self.route == 0 else self.inRoute / self.route

    def ordPrc(self) -> float:
        return 0 if self.visits == 0 else self.orders / self.visits


class AgentData:

    class Data:
        def __init__(self, route:set[str]) -> None:
            self.sum = 0
            self.visited:set[str] = set()
            self.route:set[str] = route
            self.orders:int = 0

            # print("V", len(self.route))

        def add(self, src, addSum:bool) -> None:
            if addSum:
                self.sum += src.sum
                self.orders += 1
            self.visited.add(src.id)

    def __init__(self, sch:dict[int,set[str]]) -> None:
        self.data:dict[int,AgentData.Data] = {}
        for k, v in sch.items():
            self.data[k] = AgentData.Data(v)

    def add(self, src, sumDoc:str) -> None:
        day = src.cr_day

        if not day in self.data:
            self.data[day] : AgentData.Data = AgentData.Data(set())

        self.data[day].add(src, src.docname == sumDoc)

    def summary(self) -> SummaryData:
        out = SummaryData()

        for d in self.data.values():
            out.add(d)

        return out


def loadData(params, server) :
    divisions = Divisions(server)

    root = divisions.get(params.divid) or Division()

    scheduler = Scheduler(params, server)

    odoc = Order().docName()

    filter = makeDocFilter(params)
    docstmt = makeDocQuery(filter)

    stmt = '''
    SELECT docs.* FROM 
    (SELECT count(*) as doccount, sum(sum) as sum, userid, docname, id, cr_day FROM ( {0} )  docs 
    GROUP BY userid, id, docname, cr_day) docs
    ORDER BY userid, cr_day, id
    '''.format(docstmt)

    docs = server.Query(stmt, 'DocsData[doccount:n,sum:n(2),userid:s,docname:s,id:s,cr_day:n(8)]')

    data:dict[str,AgentData] = {}
    for k, v in scheduler.items.items():
        data[k] = AgentData(v)

    for d in docs: 
        if not d.userid in data: 
            sch:dict[int,set[str]] = {}
            data[d.userid] = AgentData(sch)

        data[d.userid].add(d, odoc)

    ret: dict[str, SummaryData] = {}
    for k, v in data.items():
        ret[k] = v.summary()

    return (ret, root)

class DivisionSummary :
    def __init__(self) -> None:
        self.visits:int = 0
        self.orders:int = 0
        self.sum:float = 0.0
        
        self.route:int = 0
        self.inRoute:int = 0
        self.agents:int = 0

    def add(self, src:SummaryData) -> None:
        self.agents += 1
        self.visits += src.visits
        self.orders += src.orders
        self.sum += src.sum
        
        self.route += src.route
        self.inRoute += src.inRoute

    def addDS(self, src) -> None:
        self.agents += src.agents
        self.visits += src.visits
        self.orders += src.orders
        self.sum += src.sum

        self.route += src.route
        self.inRoute += src.inRoute


    def progress(self) -> float:
        return 0 if self.route == 0 else self.inRoute / self.route

    def ordPrc(self) -> float:
        return 0 if self.visits == 0 else self.orders / self.visits

def printOut(data:dict[str, SummaryData], division:Division, params, server):

    def printDivision(d:Division, xl:XlBuilder, row:int) -> tuple[int,DivisionSummary]:
        
        ds = DivisionSummary()
        
        crow = row
        for a in d.agents:
            crow += 1
            
            sum = data[a.id] if a.id in data else SummaryData()
            ds.add(sum)

            values = [a.name, sum.visits, sum.orders, (sum.sum, xl.formats.sum), 
                      (sum.ordPrc(), xl.formats.percent), (sum.progress(), xl.formats.percent)]
            xl.printValues(crow, values)

        for dv in d.childs:
            crow += 1
            crow, dsum = printDivision(dv, xl, crow)
            ds.addDS(dsum)

        values = [(d.name, xl.formats.bold), ds.visits, ds.orders, (ds.sum, xl.formats.sum), 
                  (ds.ordPrc(), xl.formats.percent), (ds.progress(), xl.formats.percent)]
        xl.printValues(row, values)
    
        return (crow, ds)
    
    xl = XlBuilder('summary.xlsx')
    xl.formats.percent = xl.cellFormat(lambda x: x.set_num_format("0.00%"))
    sheet = xl.addWorkSheet('Отчет')
    sheet.set_column('A:A', 50)
    sheet.set_column('B:F', 12)

    crow = xl.printTitle("Итоговый отчет подразделения: {0}".format(division.name), params, 0)

    heads = ['Подразделение / агент', 'визиты', 'заявки', 'сумма', 'процент заявок', 'прогресс']
    xl.printHead(crow, heads)
    crow += 1

    printDivision(division, xl, crow)

    xl.toObject(server)
    

def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)    
    logging.info('start report')

    params = server.Params[0]
    logging.info("params " + str(params))

    data, division = loadData(params, server)
    printOut(data, division, params, server)

    logging.info('end')
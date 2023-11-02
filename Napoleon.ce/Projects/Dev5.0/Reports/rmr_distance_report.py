
from datetime import date, datetime, timedelta, time
import logging
import sys

from grsoft.reports import makeDocFilter
from grsoft.objects.documents import makeDocQuery
from grsoft.reports.xlbase import XlBuilder

class TimeValidator:

    def __init__(self, params, server) -> None:
        self.start:datetime = params.start
        self.finish:datetime = params.finish
        self.nocheck = params.start.time() == params.finish.time() == datetime.min.time()

    def validTime(self, dt:datetime, _:str) -> bool:
        if self.nocheck: return True
        tm = dt.time()
        if self.start.time() > tm: return False
        return self.finish.time() > tm

class DocsTimeValidator(TimeValidator):

    def __init__(self, params, server) -> None:
        super().__init__(params, server)

        self.data:dict[str,dict[date,tuple[time,time]]] = {}

        where = makeDocFilter(params)
        stmt = makeDocQuery(where)
        docs = server.Query(stmt, 'Dtdt[userid:s,created:dt]') or []

        for d in docs:
            if not d.userid in self.data:
                self.data[d.userid] = {}
            
            docdate = d.created.date()
            doctime = d.created.time()

            ddate = self.data[d.userid]
            if not docdate in ddate:
                ddate[docdate] = (d.created.time(), d.created.time())
            else:
                mint, maxt = ddate[docdate]
                if doctime < mint : mint = doctime
                elif maxt < doctime: maxt = doctime

                ddate[docdate] = (mint, maxt)

    def validTime(self, dt: datetime, userid:str) -> bool:
        if userid in self.data:
            dd = dt.date()
            ddate = self.data[userid]
            if dd in ddate:
                dtime = dt.time()
                mint, maxt = ddate[dd]

                if dtime < mint : return False
                return dtime < maxt

        return False

class AgentData:

    def __init__(self, src) -> None:
        self.name:str = src.agent
        self.id:str = src.userid

        self.cdate:date = src.date.date()
        self.lat:float = src.latitude
        self.lon:float = src.longitude

        self.dateDist:dict[date,float] = {self.cdate:0}

    def add(self, src, server) -> None:
        dt = src.date.date()

        if self.cdate != dt:
            self.cdate = dt
            self.dateDist[self.cdate] = 0
        else:
            dist = server.EathDistance(self.lat, self.lon, src.latitude, src.longitude) # + self.dateDist[self.cdate]
            self.dateDist[self.cdate] += dist

        self.lat = src.latitude
        self.lon = src.longitude

    def distance(self, dt:date) -> float:
        if not dt:
            dist = 0
            for d in self.dateDist.values(): dist += d
            return dist / 1000
        return 0 if not dt in self.dateDist else self.dateDist[dt] / 1000


def loadData(params, server) -> list[AgentData] :
    # params.gsm
    # params.timeFromDocs
    # params.[start|finish] time

    where = makeDocFilter(params, "gp", "date")
    if params.gsm == 0: where += ' AND isGSM=0'

    stmt = '''
    SELECT "userid", "latitude", "longitude", "date", a."name" as agent 
    FROM "GPSPos" gp
    LEFT JOIN "Agents" a on gp."userid" = a."id"
    WHERE {0}
    ORDER BY "date"
    '''.format(where)

    validator = TimeValidator(params, server) if params.timeFromDocs == 0 else DocsTimeValidator(params, server)

    data:dict[str, AgentData] = {}
    docs = server.Query(stmt, 'Dpd[userid:s,latitude:n(5),longitude:n(5),date:dt,agent:s]') or []
    for d in docs:
        if not validator.validTime(d.date, d.userid): 
            continue

        if not d.userid in data:
            data[d.userid] = AgentData(d)
        else:
            data[d.userid].add(d, server)

    return sorted(data.values(), key=lambda x: x.name)

def printOut(data:list[AgentData], params, server) -> None:

    xl = XlBuilder('distance.xlsx')
    distFmt = xl.wb.add_format({'num_format':'0.0','align':'right','border':True})
    wrkDayFormat = xl.wb.add_format({'border':True})
    wrkDayFormat.set_bg_color('silver')
    weekDayFormat = xl.wb.add_format({'bg_color':'red', 'border':True})

    sheet = xl.addWorkSheet("Отчет")
    sheet.set_column('A:B', 44)

    crow = xl.printTitle('Отчет по пробегу', params, 0)

    heads = ['Агент', 'Расстояние']
    xl.printHead(crow, heads)
    crow += 1

    day_array = ['Вс', 'Пн', 'Вт','Ср','Чт','Пт','Сб']
    cd = params.start.date()
    while cd <= params.finish.date():
        dc = int(cd.strftime("%w"))
        value = "{0} ({1})".format(day_array[dc], cd.strftime("%d.%m.%Y"))
        isWeekend = (dc ==0 or dc == 6)
        sheet.merge_range(crow, 0, crow, 1, value, weekDayFormat if isWeekend else wrkDayFormat)
        crow += 1

        for a in data:
            values = [a.name, (a.distance(cd), distFmt)]
            xl.printValues(crow, values)

            crow += 1

        cd = cd + timedelta(days=1)

    sheet.merge_range(crow, 0, crow, 1, 'Итого', xl.formats.head)
    crow += 1
    heads = ['Агент', 'Расстояние']
    xl.printHead(crow, heads)
    crow += 1
    for a in data:
        values = [a.name, (a.distance(None), distFmt)]
        xl.printValues(crow, values)

        crow += 1

    xl.toObject(server)


def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    
    logging.debug("starting")

    # locale.setlocale(locale.LC_ALL, 'american')
    params = server.Params[0]
    logging.info("params " + str(params))

    data = loadData(params, server)
    printOut(data, params, server)

    logging.info("ended")

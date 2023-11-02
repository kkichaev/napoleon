from xlbuilder import XlBuilder

import sys
import datetime
from datetime import date, timedelta
import logging

from xlsxwriter.worksheet import Worksheet

from common import daterange, unpackUserid


class AgentScheduler:
    def __init__(self, server, params) -> None:
        scheduler = server.Get('ServerConfig', "key='SheduleStart'","userid")

        self.data : dict[str, dict[str, list[date]]] # userid -> day -> [date]
        self.data = dict()
        for uids in params.userids:
            id = uids.id
            schStart = datetime.datetime.strptime(scheduler[id].value, '%Y-%m-%d').date() if id in scheduler else date(2021, 1, 1)

            self.data[id] = self.dumpDays(schStart, params)

    def getUserSchedule(self, userid) -> dict[str, list[date]]:
        if userid in self.data: return self.data[userid]
        return dict()

    def dumpDays(self, schStart, params) -> dict[str, list[date]] :
        ret = dict()
        dayNames = ["Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"]

        cd = params.start.date()
        wd = cd.weekday()
        days = (cd - schStart).days
        for cd in daterange(params.start.date(), params.finish.date()):
            weekIndex = (int)((days / 7) % 4) + 1

            day1 = dayNames[wd % 7]
            day2 = str(weekIndex) + day1
            
            if not day1 in ret: ret[day1] = list()
            ret[day1].append(cd)

            if not day2 in ret: ret[day2] = list()
            ret[day2].append(cd)

            wd += 1
            days += 1

        return ret

class RouteData:
    def __init__(self, src) -> None:
        self.brand = src.brand
        self.name = src.name
        self.address = src.address2
        self.agent = src.agent
        self.city = src.city

        self.id = src.id
        self.userid = src.userid

        self.dates : dict[date,str] 
        self.dates = dict()

        self.days : dict[str, set[date]]
        self.days = dict()

        self.visited : dict[str, set[date]]
        self.visited = dict()

    def addDoc(self, doc):
        ddate = doc.created.date()
        if ddate in self.dates: 
            day = self.dates[ddate]
            if not day in self.visited: 
                self.visited[day] = set()

            self.visited[day].add(ddate)

    def addDays(self, day:str, scheduler:AgentScheduler):
        usch = scheduler.getUserSchedule(self.userid)
        if day in usch:
            wrday = day
            if day[0].isdigit(): wrday = day[1:]

            if not wrday in self.days: 
                self.days[wrday] = set()

            daySet = self.days[wrday]
            for d in usch[day]:
                self.dates[d] = wrday
                daySet.add(d)

    def dayDict(self) -> dict[str,str]:
        return {"Понедельник":"пн", "Вторник":"вт", "Среда":"ср", "Четверг":"чт", 
            "Пятница":"пт", "Суббота":"сб", "Воскресенье":"вс"}

    def visitDays(self) -> str:
        dayNames = self.dayDict()

        ret = ""
        for d in self.days.keys():
            ret += dayNames[d] + ","

        return ret[:-1]

    def daysPlan(self) -> list[object]:
        ret = []
        count = 0
        for d in self.dayDict().keys():
            cc = len(self.days[d]) if d in self.days else 0
            ret.append(cc)
            count += cc
        ret.append(count)
        return ret

    def daysFact(self) -> list[object]:
        ret = []
        count = 0
        for d in self.dayDict().keys():
            cc = len(self.visited[d]) if d in self.visited else 0
            ret.append(cc)
            count += cc
        ret.append(count)
        return ret



def loadData(params, server) -> list[RouteData]:
    schedule = AgentScheduler(server, params)
    
    data : dict[str, RouteData]
    data = dict()

    stmt = '''
    select o.id, o.brand, o.name, o.address2, a.name as agent, o.userid, o.city, r.OrgFolder$name as day
    from Org o, Agents a, OrgFolder$items as r
    where o.userid = a.id and o.id = r.name
    '''

    routes = server.Query(stmt, 'Route[id:s,brand:s,name:s,address2:s,agent:s,userid:s,city:s,day:s]')

    for ri in routes:
        if not ri.id in data:
            data[ri.id] = RouteData(ri)
        data[ri.id].addDays(ri.day, schedule)

    stmt = '''
    select id, created from ScriptDoc d
    where d.created >= ToDate("{0}") and d.created < ToDate("{1}") and d."userid" in ({2})
    '''.format(
        params.start.strftime('%d.%m.%Y'),
        (params.finish + datetime.timedelta(days=1)).strftime('%d.%m.%Y'),
        unpackUserid(params.userids))

    docs = server.Query(stmt, 'Docs[id:s,created:dt]')
    for d in docs:
        if d.id in data: data[d.id].addDoc(d)

    return data.values()

def printOut(params, data : list[RouteData], name : str, server):
    xl = XlBuilder(name)
    sheet = xl.sheet

    sheet.set_column('A:B',15)
    sheet.set_column('C:C',25)
    sheet.set_column('E:F',15)

    sheet.set_column('G:M',4)
    sheet.set_column('O:U',4)

    head = ['Торговая сеть', "Название", "Адрес", "Дни посещения", "ФИО мерчендайзера", "Город", 
    "пн", "вт", "ср", "чт","пт","сб","вс", "План посещения в неделю", "пн", "вт", "ср", "чт","пт","сб","вс", "Факт по маршруту"]
    
    crow = 0
    sheet.write(crow, 0, "с {0} по {1}".format(params.start.strftime('%d.%m.%Y'), params.finish.strftime('%d.%m.%Y')), xl.bold)
    crow += 1

    xl.printHead(crow, head)
    crow += 1

    for d in data:
        values = [d.brand, d.name, d.address, d.visitDays(), d.agent, d.city] + d.daysPlan() + d.daysFact()

        xl.printValues(crow, values)
        crow += 1

    xl.toObject(server)

def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s',
                        datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)
    logging.info('start report')

    params = server.Params[0]
    logging.info("params " + str(params))

    data = loadData(params, server)
    printOut(params, data, 'RouteDetail.xlsx', server)

    logging.info('end')

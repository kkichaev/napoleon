from xlbuilder import XlBuilder

import sys
import datetime
from datetime import date
import logging

from common import daterange, unpackUserid

class OrgData:
    def __init__(self, src) -> None:
        self.id = src.id
        self.name = src.name
        self.address = src.address2
        self.city = src.city


class AgentReportData:
    def __init__(self, data) -> None:
        self.visited: dict[str, OrgData]
        self.route: dict[str, OrgData]

        self.route = dict()
        self.visited = dict()
        self.compleete = 0

        self.agent = data[0]

        for od in data[1]:
            self.route[od.id] = od

    def addDoc(self, doc):
        if not doc.id in self.visited:
            self.visited[doc.id] = OrgData(doc)

        compl = True
        for di in doc.items:
            if di.state != 1:
                compl = False
                break

        if compl:
            self.compleete += 1


class AgentRoute:
    def __init__(self, src) -> None:
        self.data: list[OrgData]

        res: list[OrgData]
        res = list()

        for si in src.orgs:
            res.append(OrgData(si))

        self.day = src.day
        self.data = res
        self.agent = src.agent


def loadData(params, server) -> dict[date, dict[str, AgentReportData]]:
    agents = server.Get("Agents", "", "id")

    def loadSchedule() -> dict[str, date]:
        stmt = '''
    select userid, value from ServerConfig where key = 'SheduleStart' 
    '''

        res = dict()
        docs = server.Query(stmt, 'Schedule[userid:s,value:s]')
        if docs != None:
            for d in docs:
                res[d.userid] = datetime.datetime.strptime(
                    d.value, '%Y-%m-%d').date()

        return res

    def findRoute(routes: dict[str, list[AgentRoute]], schedule: dict[str, date], cdate: date, userid: str):
        days = ["Понедельник", "Вторник", "Среда",
                "Четверг", "Пятница", "Суббота", "Воскресенье"]
        ret = list()
        agent = None
        
        if userid in agents:
            agent = agents[userid].name

            if userid in routes:
                weekIndex = 0
                if userid in schedule:
                    d = cdate - schedule[userid]
                    weekIndex = (int)((d.days / 7) % 4) + 1

                dayname = days[cdate.weekday()]
                dayname1 = str(weekIndex) + dayname

                for ar in routes[userid]:
                    if ar.day == dayname:
                        ret = ar.data
                        agent = ar.agent
                        break
                    if ar.day == dayname1:
                        ret = ar.data
                        agent = ar.agent
                        break

        return (agent, ret)

    def loadRoute() -> dict[str, list[AgentRoute]]:
        stmt = '''
      select r.agent, r.userid, r.day as day, ri.id, ri.name, ri.address2, ri.city 
      from
        (select a.name as agent, of.userid, of.name as day from OrgFolder of, Agents a where of.userid = a.id) r,
        (select of.name as id, o.name, o.city, o.address2, of.OrgFolder$userid as userid, of.OrgFolder$name as day from OrgFolder$items of, Org o where of.name = o.id) ri
      where r.userid = ri.userid and r.day = ri.day
      order by r.agent, day
      '''

        rsrc = server.Query(
            stmt, 'Route[agent:s,userid:s,day:s,orgs(agent,day)[id:s,name:s,address2:s,city:s]]')
        route: dict[str, list[AgentRoute]]
        route = dict()

        for ri in rsrc:
            if not ri.userid in route:
                route[ri.userid] = list()

            route[ri.userid].append(AgentRoute(ri))

        return route

    stmt = '''
    select d.userid, d.id, d.created, o.name, o.address2, o.city, di.state, di.date  
    from ScriptDoc d, Org o, ScriptDoc$items di
    where d.id = o.id and d.userid = di.ScriptDoc$userid and d.created = di.ScriptDoc$created
      and d.created >= ToDate("{0}") and d.created < ToDate("{1}") and d."userid" in ({2})
    order by d.created, d.userid
  '''.format(
        params.start.strftime('%d.%m.%Y'),
        (params.finish + datetime.timedelta(days=1)).strftime('%d.%m.%Y'),
        unpackUserid(params.userids))

    routes = loadRoute()
    schedule = loadSchedule()

    docs = server.Query(
        stmt, 'Docs[userid:s,id:s,created:dt,name:s,address2:s,city:s,items(userid,created)[state:n]]')

    
    data = dict()
    for d in docs:
        cdate = d.created.date()
        if not cdate in data:
            data[cdate] = dict()

        adic: dict[str, AgentReportData]
        adic = data[cdate]
        if not d.userid in adic:
            a, r = findRoute(routes, schedule, cdate, d.userid)

            if a != None:
                adic[d.userid] = AgentReportData([a,r])

        if d.userid in adic:
            adic[d.userid].addDoc(d)

    return data

def printOut(params, data: dict[date, dict[str, AgentReportData]], name, server):
    def extractAgents() -> list[str]:
        ret = list()
        ids = list()

        for vd in data.values():
            for auid, ad in vd.items():
                if not auid in ids:
                    ret.append((ad.agent, auid))
                    ids.append(auid)

        return sorted(ret, key=lambda x: x[0])

    def dumpOrgs(keys: str, orgs: dict[str, OrgData]):
        ret = ''
        for id in keys:
            if id in orgs:
                o = orgs[id]
                ret += o.name + ',' + o.address + ' '
        return ret

    agents = extractAgents()

    xl = XlBuilder(name)

    sheet = xl.sheet
    xl.cellFmt.set_text_wrap()
    percent_fmt = xl.wb.add_format({'num_format': '0.00%'})
    percent_fmt.set_border()

    sheet.set_column('A:A', 15)
    sheet.set_column('B:E', 10)
    sheet.set_column('F:F', 40)
    sheet.set_column('H:H', 40)
    sheet.set_column('G:G', 10)
    sheet.set_column('I:I', 10)
    sheet.set_column('J:J', 10)

    crow = 0
    sheet.write(crow, 0, 'Посещения', xl.bold)
    crow += 1
    sheet.write(crow, 0, 'с {0} по {1}'.format(params.start.strftime(
        '%d.%m.%Y'), params.finish.strftime('%d.%m.%Y')), xl.bold)

    for cd in daterange(params.start.date(), params.finish.date()):
        if not cd in data:
            continue

        sheet.write(crow, 0, cd.strftime('%d.%m.%Y'))
        agentData = data[cd]

        crow += 1

        head = ["Агент", "Визиты", "План по маршруту", "Факт по маршруту", "Не посетил", "Название и адрес непосещенных ТТ",
                "Не по маршруту", "Название и адрес ТТ не по маршрута", "Количество завершенных визытов", "Прогресс"]

        xl.printHead(crow, head)

        crow += 1

        totProgress = 0
        totVisited = 0
        totPlan = 0
        totCompleete = 0
        totInRoute = 0
        totOutRoute = 0

        for agent in agents:
            values = []
            a = agent[0]
            
            uid = agent[1]

            progress = 0.0
            
            if uid in agentData:
                dailyData = agentData[uid]
                values.append(dailyData.agent)

                inRouteOrgs = dailyData.route.keys() & dailyData.visited.keys()
                outRouteOrgs = dailyData.visited.keys() - inRouteOrgs
                notVisitedOrgs = dailyData.route.keys() - inRouteOrgs

                visited = len(dailyData.visited)
                plan = len(dailyData.route)
                inroute = len(inRouteOrgs)
                outRoute = len(outRouteOrgs)

                progress = 0.0 if plan == 0 else inroute / plan
                totProgress += progress
                totVisited += visited
                totPlan += plan
                totInRoute += inroute
                totOutRoute += outRoute
                totCompleete += dailyData.compleete

                values.extend([visited, plan, inroute, plan - inroute,
                               dumpOrgs(notVisitedOrgs, dailyData.route),
                               outRoute,
                               dumpOrgs(outRouteOrgs, dailyData.visited),
                               dailyData.compleete,
                               ])

                xl.printValues(crow, values)
                sheet.write(crow, 9, progress, percent_fmt)
                crow += 1

        totProgress = 0 if len(agents) == 0 else totProgress / len(agents)
        values = ['Итого', totVisited, totPlan, totInRoute, totPlan - totInRoute, '', totOutRoute, '',
                  totCompleete]
        xl.printValues(crow, values)
        sheet.write(crow, 9, totProgress, percent_fmt)
        crow += 1

    xl.toObject(server)


def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s',
                        datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)
    logging.info('start report')

    params = server.Params[0]
    logging.info("params " + str(params))

    data = loadData(params, server)
    printOut(params, data, 'VisitReport.xlsx', server)

    logging.info('end')

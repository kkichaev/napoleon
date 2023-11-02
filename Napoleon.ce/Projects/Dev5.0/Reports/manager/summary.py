import sys
import time
from datetime import timedelta
from datetime import datetime
from datetime import date
from decimal import *

from .document import docTypes
from .document import Order

import tempfile
import io
from . import coordutils
import importlib


class SummaryData:
    agentId = None
    divId = None    
    plan = None;
    plannedVisits = 0
    visited = None
    orders = 0
    summa = 0.0
    progress = 0.0
    visitedWithOrder = None
    plannedOrders = 0
    
    def __init__(self, server, agent_id:str, for_date:date, plan:list[str]):
        self.agentId = agent_id
        self.plan = plan
        self.visited = set()
        self.visitedWithOrder = set()
        self.plannedOrders = 0

    def addOrder(self, order):
        self.orders += 1
        self.add(order)
        self.summa += order.sum()
        self.visitedWithOrder.add(order.id)
    
    def add(self, doc):
        self.visited.add(doc.id)
        
    def updatePlan(self):
        for orgId in self.visited:
            if orgId in self.plan:
                self.plannedVisits += 1
                
        for orgId in self.visitedWithOrder:
            if orgId in self.plan:
                self.plannedOrders += 1        

class DivisionData:
    id = 0
    parent = -1
    description = ""
    name = ""
    agents = []

    def load(self, d):
        self.id = d.id
        self.parent = d.parent
        self.description = d.description
        self.name = d.name
        self.agents = []

        for a in d.agents:
            self.agents.append(a.id)

    def __repr__(self):
        return "id:" + str(self.id) + " name:" + self.name + " parent:" + str(self.parent);

class AgentData:

    def __init__(self) -> None:
        self.id = ""
        self.name = ""
        self.phone = ""
        self.login = ""
        self.password = ""
        
        self.date = datetime(1970, 1, 2)

        self.route:dict[datetime.date, list[str]] = dict()

    def load(self, src):
        self.id = src.id
        self.name = src.name
        self.login = src.login
        self.password = src.password

    def addRoute(self, src):
        rt:list[str] = []
        for i in src.items:
            rt.append(i.id)

        self.route[src.date] = rt

    def getRoute(self, data:date) -> list[str]:
        return self.route[data] if data in self.route else []

def getDivisionAgents(server, division, agents:list[str], divisions:list[DivisionData]):
    if division == None or len(division) == 0:
        return

    locDivisions = []
    for d in division :
        if divisions != None :
            dd = DivisionData()
            dd.load(d)

            if len(divisions) == 0 :
                dd.parent = -1
            divisions.append(dd)
            locDivisions.append(dd)
        for a in d.agents :
            agents.append(a.id)

    for dd in locDivisions :
        getDivisionAgents(server, server.Get("Division", '"parent"=' + str(dd.id)), agents, divisions)

      
def loadAgents(server, division, divisions:list[DivisionData], curDate:date, endDate:date) -> dict[str, AgentData]:
    ret:dict[str, AgentData] = dict()
    
    agents = server.Get("Agents", "")
    
    divagents : list[str] = []
    getDivisionAgents(server, division, divagents, divisions)
    
    where = ""
    for a in agents:
        if a.id in divagents:
            ad = AgentData()
            ad.load(a)
            ret[a.id] = ad
            where += "'%s'," % a.id

    if len(where) > 0:
        where = '"userid" in (%s) and ' % where[:-1]

    where += '"date">=ToDate("%s") and "date"<=ToDate("%s")' % \
        curDate.strftime("%d/%m/%Y 0:0:0") \
        ,endDate.strftime("%d/%m/%Y 23:59:59")

    data = server.Get("UserActivity", "")
    for ui in data:
        if ui.id in divagents and ui.id in ret:
            ret[ui.id].date = ui.date

    data = server.Get("UserInfo", "")
    for ui in data:
        if ui.userid in divagents and ui.userid in ret:
            ret[ui.userid].phone = ui.phone
    
    data = server.Get('Schedule', where)
    for ui in data:
        if ui.userid in ret:
            ret[ui.userid].addRoute(ui)

    return ret    

def putDivision(server, divisions):
    divObj = server.New("Division")
    for src in divisions:
        dest = divObj.New()
        dest.id = src.id;
        dest.parent = src.parent
        dest.name = src.name
        dest.description = src.description

        for srca in src.agents:
            desta = dest.agents.New()
            desta.id = srca
    server.Put(divObj)

def putAgents(server, agents):
    server.RegisterType("ManagerAgent[id:s,name:s,date:dt,phone:s,login:s,password:s]")
    destA = server.New("ManagerAgent")
    for src in agents.values():
        dest = destA.New()
        dest.id = src.id
        dest.name = src.name
        dest.date = src.date
        dest.phone = src.phone
        dest.login = src.login
        dest.password = src.password
    server.Put(destA)
    
def makeQuery(agentQuery, for_date):
    q = '"created" >= ToDate("{0}") and "created" <= ToDate("{1}")'.format(
        for_date.strftime("%d/%m/%Y 0:0:0"),
        for_date.strftime("%d/%m/%Y 23:59:59"))
    q += ' and ' + agentQuery

    return q;

def dailyReport(server, agents_list:dict[str,AgentData], for_date:date ):

    agentQuery = '"userid" in('

    agents_dict = dict()    
    for agent_id, agentDate in agents_list.items():
        agents_dict[agent_id] = SummaryData(server, agent_id, for_date, agentDate.getRoute(for_date))

        agentQuery += "'" + agent_id + "',";
        
    agentQuery = agentQuery[:-1] + ")"

    where = makeQuery(agentQuery,for_date)
    
    for dt in docTypes:
        docs = dt.docList(server, where)
        if dt.docWraper == Order or issubclass(dt.docWraper, Order)  :
            for doc in docs:
                agents_dict[doc.userid].addOrder(doc)
        else:
            for doc in docs:
                agents_dict[doc.userid].add(doc)

        # print(dt.objectName, len(docs.servObject) if docs.servObject != None else 0)        
    for data in agents_dict.values():
        data.updatePlan()

    return agents_dict

def computeDist(server, curdate, userid):
    q = '"date" >= ToDate("{0}") and "date" <= ToDate("{1}") and "userid"=\'{2}\''.format(
        curdate.strftime("%d/%m/%Y 0:0:0"),
        curdate.strftime("%d/%m/%Y 23:59:59"), userid)
    
    gpspos = server.Get("GPSPos",q)
    
    lastpos = None
    distance = 0
    
    for pos in gpspos:
        if lastpos == None:
            lastpos = pos
            continue
        distance += coordutils.distance(lastpos.latitude, lastpos.longitude, pos.latitude, pos.longitude)
        
    return distance
    
def run(server):
    print("start\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S'))
    importlib.reload(sys)
    #sys.setdefaultencoding("cp1251")

    params = server.Params
   
    if params == None:
        print("Params is empties")
        return
   
    user = server.CurrentUser()
    where = '"login"=' + "'" + str(user.id) + "'"
    divMgr = server.Get("DivisionManager", where)
    if divMgr == None:
        print("No manager")
        return

    divisions = list()
    rootDivision = server.Get("Division", '"id"=' + str(divMgr[0].division))

    curDate = params[0].start_date
    endDate = params[0].end_date

    divAgents = loadAgents(server, rootDivision, divisions, curDate, endDate)

   
    putDivision(server, divisions)
    putAgents(server, divAgents)


    server.RegisterType("TypeName[id:s,start_date:dt,end_date:dt,visits:n(0),orders:n(0),sum:n(2),progress:n(0),dist:n(3),order_progress:n(0)]")
    objList = server.New("TypeName")

   #print "period: ", curDate, endDate
   
   #endDate = datetime.strptime('05032015', "%d%m%Y")
   #curDate = datetime.strptime('01032015', "%d%m%Y")
   
    while curDate < endDate: 
        dayRep = dailyReport(server, divAgents, curDate)
        for key, value in dayRep.items():
            obj = objList.New()
            obj.id = key
            obj.start_date = curDate
            obj.end_date = curDate + timedelta(1)
            obj.visits = len(value.visited)
            obj.orders = value.orders
            obj.sum = value.summa
            obj.dist = computeDist(server,curDate,key) / 1000

            if len(value.plan) > 0:
                obj.progress = int(value.plannedVisits / float(len(value.plan)) * 100)
                obj.order_progress = int(value.plannedOrders / float(len(value.plan)) * 100)
            else:
                obj.progress = 0
                obj.order_progress = 0
               
        curDate = curDate + timedelta(1)

    server.Put(objList)

    print("finish\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S'))

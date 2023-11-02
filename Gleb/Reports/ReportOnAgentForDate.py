# -*- coding: cp1251 -*-


import sys
import time
from datetime import timedelta
from datetime import datetime
from decimal import *

import tempfile
import io


class reportRow:
    agentId = None
    divId = None    
    plan = None;
    plannedVisits = 0
    visited = None
    orders = 0
    summa = 0.0
    progress = 0.0

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
     id = ""
     name = ""
     phone = ""
     date = datetime(1970, 1, 2)

     def load(self, src):
         self.id = src.id
         self.name = src.name


days = {"Monday" : "Понедельник", 
        "Tuesday" : "Вторник",
        "Wednesday" : "Среда",
        "Thursday" : "Четверг",
        "Friday" : "Пятница",
        "Saturday" : "Суббота",
        "Sunday" : "Воскресенье" }

scheduleStart = dict()

def getWeekIndex(server, data, agentid):  
    scStart = None
     
    if agentid in scheduleStart: 
        scStart = scheduleStart[agentid]
    else :
        where = '"userid"' + " in ('" + agentid + "')"
        cfg = server.Get("ServerConfig", where)
        
        if cfg != None:
            for c in cfg:
                if c.key == 'SheduleStart':
                    strptime = lambda date_string, format: datetime(*(time.strptime(date_string, format)[0:6])) 
                    scStart = strptime (c.value, '%Y-%m-%d')
                    break
                
        scheduleStart[scStart] = scStart;
    
    result = -1
    
    if scStart != None:
        d = data - scStart
        result = ((d.days / 7) % 4) + 1;
    
    return result    


def getDivisionAgents(server, division, agents, divisions):
    if division == None or len(division) == 0:
        return

    locDivisions = []
    for d in division :
        if divisions != None :
            dd = DivisionData()
            dd.load(d)
            # для первого подразделения изменим parent
            if len(divisions) == 0 :
                dd.parent = -1
            divisions.append(dd)
            locDivisions.append(dd)
        for a in d.agents :
            agents.append(a.id)

    for dd in locDivisions :
        getDivisionAgents(server, server.Get("Division", '"parent"=' + str(dd.id)), agents, divisions)

      
def loadAgents(server, division, divisions):
    ret = dict()
    
    agents = server.Get("Agents", "")
    
    divagents = []
    getDivisionAgents(server, division, divagents, divisions)
    divagents = set(divagents)
    
    for a in agents:
        if a.id in divagents:
            ad = AgentData()
            ad.load(a)
            ret[a.id] = ad

    data = server.Get("UserActivity", "")
    for ui in data:
        if ui.id in divagents:
            ret[ui.id].date = ui.date

    data = server.Get("UserInfo", "")
    for ui in data:
        if ui.userid in divagents:
            ret[ui.userid].phone = ui.phone
            
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
    server.RegisterType("AgentInfo[id:s,name:s,date:dt,phone:s]")
    destA = server.New("AgentInfo")
    for src in agents.itervalues():
        dest = destA.New()
        dest.id = src.id
        dest.name = src.name
        dest.date = src.date
        dest.phone = src.phone
    server.Put(destA)

def plannedOrgs(server,agent_id, for_date):
   orgFolder = server.Get("OrgFolder", '"userid"'+" in ('"+agent_id+ "')")
   plans = list()
   d = days[for_date.strftime("%A")]
   widx = getWeekIndex(server, for_date, agent_id)
   
   for of in orgFolder:
      if of.name == d or of.name == str(widx) + d:
         for i in of.items:            
            plans.append(i.name)
            
   return plans

    
def makeQuery(agentQuery, for_date):
    q = '"created" >= ToDate("{0}") and "created" <= ToDate("{1}")'.format(
        for_date.strftime("%d/%m/%Y 0:0:0"),
        for_date.strftime("%d/%m/%Y 23:59:59"))
    q += ' and ' + agentQuery

    return q;

def dailyReport(server, agents_list, for_date ):

    agentQuery = '"userid" in('

    agents_dict = dict()    
    for agent_id in agents_list.iterkeys():
        agents_dict[agent_id] = reportRow()
        agents_dict[agent_id].plan = plannedOrgs(server,agent_id, for_date)
        agents_dict[agent_id].visited = set()

        agentQuery += "'" + agent_id + "',";
        
    agentQuery = agentQuery[:-1] + ")"

    query_condition = makeQuery(agentQuery,for_date)

    orders = server.Get("Order", query_condition)
    visits = server.Get("Visit", query_condition)
    remnants = server.Get("OrgRemnants", query_condition)
    monitorings = server.Get("Monitoring", query_condition)

    for o in orders:
        agent_id = o.userid
        agents_dict[agent_id].orders += 1
        for i in o.items:
            agents_dict[agent_id].summa += i.cost * i.qty
        agents_dict[agent_id].visited.add(o.id)

    for v in visits:
        agent_id = v.userid
        agents_dict[agent_id].visited.add(v.id)           

    for r in remnants:
        agent_id = r.userid
        agents_dict[agent_id].visited.add(r.id)

    if  monitorings != None:
        for m in monitorings:
            agent_id = r.userid
            agents_dict[agent_id].visited.add(m.id)
               
    for agent_id in agents_list.iterkeys():
        for p in agents_dict[agent_id].plan:
            if p in agents_dict[agent_id].visited:
               agents_dict[agent_id].plannedVisits += 1    

    return agents_dict



    
def run(server):

   reload(sys);
   sys.setdefaultencoding("cp1251")

   print " "
   print " "
   print " "
   print " "
   print " "
   
   # get list of all params
   params = server.Params
##   print "Print params: ", params

   # create new type

   user = server.Get("user")[0]
   if user.division < 0 :
       print "Isn't manager"
       return

   print params[0]

   divisions = list()
   rootDivision = server.Get("Division", '"id"=' + str(user.division))

   divAgents = loadAgents(server, rootDivision, divisions)

   print "loadAgents done"
   
   putDivision(server, divisions)
   putAgents(server, divAgents)

   print "putAgents done"

   server.RegisterType("TypeName[id:s,start_date:dt,end_date:dt,visits:n(0),orders:n(0),sum:n(2),progress:n(3)]")
   objList = server.New("TypeName")

   curDate = params[0].start_date
   endDate = params[0].end_date
   
   while curDate < endDate: 
       dayRep = dailyReport(server, divAgents, curDate)
       print curDate
       for key, value in dayRep.iteritems():
           print key, " - ",  len(value.plan), " - ", value.plannedVisits, " - ", len(value.visited), " - ", value.orders, " - ", value.summa

           obj = objList.New()
           obj.id = key
           obj.start_date = curDate
           obj.end_date = curDate + timedelta(1)
           obj.visits = len(value.visited)
           obj.orders = value.orders
           obj.sum = value.summa
           
           if len(value.plan) > 0:
               obj.progress = value.plannedVisits / len(value.plan)
           else:
               obj.progress = 0
               
       curDate = curDate + timedelta(1)

   server.Put(objList)

   print "done agent list test"

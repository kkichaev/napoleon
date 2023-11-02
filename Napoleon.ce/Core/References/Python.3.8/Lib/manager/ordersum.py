# -*- coding: cp1251 -*-


import sys
import time
from datetime import timedelta
from datetime import datetime
from decimal import *

from .document import docTypes
from .document import Order

import tempfile
import io
from . import coordutils
import importlib

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
    login = ""
    password = ""
     
    date = datetime(1970, 1, 2)

    def load(self, src):
        self.id = src.id
        self.name = src.name
        self.login = src.login
        self.password = src.password

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
        if ui.id in divagents and ui.id in ret:
            ret[ui.id].date = ui.date

    data = server.Get("UserInfo", "")
    for ui in data:
        if ui.userid in divagents and ui.userid in ret:
            ret[ui.userid].phone = ui.phone
    
    return ret
    
def collectOrderItems(server, uids):
    curDate = datetime.now().date()
    finish = curDate + timedelta(days=1)
    start = curDate.replace(day=1)
    
    where = '"userid" in ({0}) and "created" >= ToDate("{1}") and "created" < ToDate("{2}")'.format(
        uids, start.strftime("%d/%m/%Y 0:0:0"), finish.strftime("%d/%m/%Y 0:0:0"))
    
    data = dict() #data - userid - sum
    
    orders = server.Get("Order", where)
    #print where
    #print len(orders)
    
    for o in orders:
        d = o.created.date()
        if not d in data:
            data[d] = dict()
            
        data_data = data[d]
        
        if not o.userid in data_data:
            data_data[o.userid] = 0
        
        s = 0;
        
        for i in o.items:
            s += i.cost * i.qty 
                
        data_data[o.userid] += s
    
    return data

def userids(agents):
    ret = ""
    
    for a in agents:
        if len(ret) > 0:
            ret += ", "
            
        ret += "'" + a + "'"
    
    return ret
             
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

    divAgents = loadAgents(server, rootDivision, divisions)
   
    data = collectOrderItems(server, userids(divAgents))
    
    server.RegisterType("OrderSum[userid:s,date:dt,sum:n(2)]")
    objList = server.New("OrderSum")
   
    #data - userid - sum
   
    for d in data:
        data_userid = data[d]
        
        for u in data_userid:
            sum = data_userid[u]
            
            obj = objList.New()
            obj.userid = u
            obj.date = datetime.combine(d, datetime.min.time())
            obj.sum = sum

    server.Put(objList)

    print("finish\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S'))

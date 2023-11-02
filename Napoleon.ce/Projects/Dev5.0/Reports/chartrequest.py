# -*- coding: cp1251 -*-

import sys
from importlib import reload
from datetime import timedelta, datetime
from manager.summary import loadAgents  # @UnresolvedImport
from manager.document import docTypes

class ItemTopSale:
  id =""
  qty = 0
  
  def __init__(self):
    self.id = ""
    self.qty = 0
  
  def __str__(self):
    return "{0} - {1}".format(self.id, self.qty)
    
class ItemOrderSum:
  date = None
  sum = 0
  
  def __init__(self):
    self.date = ""
    self.sum = 0
  
  def __str__(self):
    return "{0} - {1}".format(self.date, self.sum)    
    
class ItemVisitCount:
  date = None
  count = 0
  
  def __init__(self):
    self.date = ""
    self.count = 0
  
  def __str__(self):
    return "{0} - {1}".format(self.date, self.count)       
        
def run(server):
    print("start\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S'))
    reload(sys)
    #sys.setdefaultencoding("cp1251") 

    user = server.CurrentUser()
    price = server.Get("Price", "setqtyfilter(false)", "id")
    
    server.RegisterType("AgentTopSale[id:s,name:s,qty:n(3)]]")
    output = server.New("AgentTopSale")
    
    where = '"userid" ={0} and "created" >= ToDate("{1}")'.format("'"+user.id+"'", server.Params[0].start.strftime("%d/%m/%Y 0:0:0"))
    orders = server.Get("Order", where)  
    
    map = dict()
    mapSum = dict()
    
    if orders != None:
        for o in orders:
          for i in o.items:
            if not i.id in map:
              t = ItemTopSale()
              t.id = i.id
              map[i.id] = t
    
            date = o.created.date()
            if not date in mapSum:
              t = ItemOrderSum()
              t.date = date
              mapSum[date] = t
              
            map[i.id].qty += i.qty
            mapSum[date].sum += i.qty * i.cost
    
    data = sorted(map.values(), key=lambda x: x.qty, reverse=True)
    data = data[:5]
    
    for d in data:
      i = output.New()
      i.id = d.id
      i.name = price[d.id].name if d.id in price else d.id
      i.qty = d.qty
      
    server.Put(output)
    
    #Продажи
    server.RegisterType("AgentOrderSum[date:dt,sum:n(2)]")
    output = server.New("AgentOrderSum")    
    
    d = server.Params[0].start.date()
    
    while d <= datetime.now().date(): 
      if not d in mapSum:
        t = ItemOrderSum()
        t.date = d
        t.sum = 0;
        mapSum[d] = t
        
      d = d + timedelta(days=1)

    data = sorted(mapSum.values(), key=lambda x: x.date, reverse=True)
    
    for d in data:
      i = output.New()
      i.date = d.date
      i.sum = d.sum
      
    server.Put(output)
    
    #АКБ
    server.RegisterType("AgentAKBData[id:s,alldoc:n,inroute:n]")
    output = server.New("AgentAKBData")
    
    data = list()
    
    orgs = server.Get("Org", "", "id")
    route = server.Get("RouteTemplate", "PARAMS:"+user.id)
    if not route: route = []

    orgdict = dict()
    
    for id in orgs:
      orgdict[id] = 0
    
    routedict = dict()
    
    for of in route:
      for i in of.items:
        routedict[i.id] = 0
    
    visitMap = dict()
    
    for dt in docTypes:
      docs = dt.docList(server, where)

      for d in docs:
        key = d.created.strftime("%d/%m/%Y")
        
        if not key in visitMap:
          visitMap[key] = list()
        
        if not d.id in visitMap[key]:
          visitMap[key].append(d.id)
        
        if d.id in orgdict:
          orgdict[d.id] = 1
        if d.id in routedict:
          routedict[d.id] = 1
    
    odcnt = 0.0;
    for c in orgdict.values():
      odcnt += c
    
    rtcnt = 0.0
    for r in routedict.values():
      rtcnt += r
    
    obj = output.New()
    obj.id = "1"
    obj.alldoc = int((odcnt / len(orgdict)) * 100) if len(orgdict) > 0 else 0
    obj.inroute = int((rtcnt / len(routedict)) * 100) if len(routedict) > 0 else 0
    
    server.Put(output)
    
    #Визиты
    server.RegisterType("AgentVisit[date:dt,count:n]")
    output = server.New("AgentVisit")    
    
    d = server.Params[0].start.date()
    
    data = list()
    
    count = 50;
    
    if docs != None:
      while d <= datetime.now().date(): 
        t = ItemVisitCount()
        t.date = d
        t.count = 0;
        
        key = d.strftime("%d/%m/%Y")
        
        if key in visitMap:
          t.count = len(visitMap[key])
          
        data.append(t)
        
        d = d + timedelta(days=1)

    for d in data:
      i = output.New()
      i.date = d.date
      i.count = d.count
      
    server.Put(output)
    
    print("finish\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S'))

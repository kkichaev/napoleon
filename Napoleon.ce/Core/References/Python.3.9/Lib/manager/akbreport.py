# -*- coding: cp1251 -*-
import sys
import time
from datetime import timedelta
from datetime import datetime
from decimal import *

from .document import docTypes
from .document import Order
from manager.summary import loadAgents
from .document import docTypes


import tempfile
import io
from . import coordutils
import importlib

def collectStory(server, uids, date, output):
    finish = datetime.now().date() - timedelta(days=30)
    data = list()
    
    fstr = finish.strftime("%d/%m/%Y 0:0:0")
    
    for user in uids:
      server.ChangeUser("'" + user + "'")
      orgs = server.Get("Org", "", "id")
      route = server.Get("OrgFolder", "")
      server.RestoreUser()
    
      orgdict = dict()
      
      for id in orgs:
        orgdict[id] = 0
      
      routedict = dict()
      
      for of in route:
        for i in of.items:
          routedict[i.name] = 0
      
      where = '"userid"={0} and "created" >= ToDate(\'{1}\')'.format("'" + user + "'", fstr)

      for dt in docTypes:
        docs = dt.docList(server, where)

        for d in docs:
          if d.id in orgdict:
            orgdict[d.id] = 1
          if d.id in routedict:
            routedict[d.id] = 1
      
      odcnt = 0.0;
      for c in list(orgdict.values()):
        odcnt += c
      
      rtcnt = 0.0
      for r in list(routedict.values()):
        rtcnt += r
      
      obj = output.New()
      obj.userid = user
      obj.alldoc = int((odcnt / len(orgdict)) * 100) if len(orgdict) > 0 else 0
      obj.inroute = int((rtcnt / len(routedict)) * 100) if len(routedict) > 0 else 0
    
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
   
    server.RegisterType("AKBData[userid:s,alldoc:n,inroute:n]")
    output = server.New("AKBData")
    collectStory(server, divAgents, range, output)
    server.Put(output)

    print("finish\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S'))

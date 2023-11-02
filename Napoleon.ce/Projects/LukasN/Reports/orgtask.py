# -*- coding: cp1251 -*-

# types write string without space
# s - string
# n(prec) - double(number), prec == 0  integer
# n - integer
# d - date
# t - time
# dt - datetime
# b - binary
#

import datetime
import io
from orgtaskutil import *


import sys;
reload(sys);
sys.setdefaultencoding("cp1251")
    
def run(server):

    print "start"
    
    type = "Result[id:s,name:s,done:n,missed:n]"
    server.RegisterType(type)
    objList = server.New("Result")
    
    param = server.Params[0];
    uidFilter = '"userid" in ' + "('" + param.agentID + "')"
    orgs = server.Get("Org", uidFilter, "id")
    
    init(server)
    
    for o in orgs.values() :
        obj = objList.New()
        obj.id = o.id
        obj.name = o.name + " (" + o.address + ")" 
        done = len(taskDone)
        
        if o.id in taskDone:
            obj.done = len(taskDone[o.id])
        
        if o.id in taskMissed:    
            obj.missed = len(taskMissed[o.id])

    
    server.Put(objList)
   
    print "done"

   

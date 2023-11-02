# -*- coding: cp1251 -*-


import datetime
import io
from orgtaskutil import *
from grsoft_reporter import module_info

def get_module_info():
    mi = module_info(__name__)

    mi.parameters.append(module_info.param("agentID", module_info.param.TYPE_AGENT_ID))

    return mi

def run(server):

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
        obj.name = o.name
        done = len(taskDone)
        
        if o.id in taskDone:
            obj.done = len(taskDone[o.id])
        
        if o.id in taskMissed:    
            obj.missed = len(taskMissed[o.id])

    
    server.Put(objList)

   

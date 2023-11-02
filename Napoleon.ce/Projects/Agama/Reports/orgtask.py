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

from datetime import datetime
from manager.task import taskReport
from manager.task import init
    
def run(server):
    print "start\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S')
    
    param = server.Params[0];
    if param.mode == "report" :
        taskReport(server, param)
    else :
        taskList(server, param)
    
    print "finish\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S')
   
def taskList(server, param):
    type = "Result[id:s,ido:s,name:s,address:s,done:n,missed:n,f1:s,f2:s,rc:s]"
    server.RegisterType(type)
    objList = server.New("Result")
    
    allorgs = dict()
    
    for u in param.agentID.split(','):
      server.ChangeUser("'" + u + "'")
      orgs = server.Get("Org", "", "id")
      server.RestoreUser()
      
      for k in orgs.keys():
        if orgs[k].agent == u and not k in allorgs:
          allorgs[k] = orgs[k]
    
    taskDone, taskMissed, taskDoneIds, taskMissedIds = init(server)
    
    for o in allorgs.values() :
        obj = objList.New()
        obj.id = o.id
        obj.ido = o.ido
        obj.name = o.name
        obj.address = o.address
        obj.f1 = o.filter1
        obj.f2 = o.filter2
        obj.rc = o.realClient
        
        if o.id in taskDone:
            obj.done = len(taskDone[o.id])
        
        if o.id in taskMissed:    
            obj.missed = len(taskMissed[o.id])

    server.Put(objList)
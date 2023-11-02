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
    
    objList = server.New("Org")
    
    allorgs = dict()
    
    for u in param.userid.split(','):
      server.ChangeUser("'" + u + "'")
      orgs = server.Get("Org", "", "id")
      server.RestoreUser()
      
      for k in orgs:
        if not k in allorgs:
          allorgs[k] = orgs[k]
    
    for o in allorgs.values() :
        obj = objList.New()
        obj.id = o.id
        obj.name = o.name
        obj.address = o.address

    server.Put(objList)
    
    print "finish\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S')
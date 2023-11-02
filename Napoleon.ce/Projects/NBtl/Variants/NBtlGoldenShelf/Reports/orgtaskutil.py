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
from datetime import datetime
import io


import sys;
reload(sys);
sys.setdefaultencoding("cp1251")

def scope(val):
    return "'" + val + "'"

class TaskInfo:
    text = ""
    comment = ""

def getTaskById(server,id):
    result = None
    sql = '"id"=' + scope(id)
    task = server.Get("OrgTask", sql )
    
    if len(task) > 0:
        result = task[0]
        
    return  result   

def getListFromDict(orgid, id, data, check):
    if orgid in data:
        result = data[orgid]
    else:
        result = list()
        data[orgid] = result
         
    check.add(id) 
    
    return result   
    
def init(server):
    taskDone = dict()
    taskMissed = dict()
    taskMissedIds = set()
    taskDoneIds = set()
    
    param = server.Params[0];
    start = param.start.strftime('%d/%m/%Y')
    finish = param.finish.strftime('%d/%m/%Y')
    userid = param.agentID
    sql = '"userid"=' + scope(userid) + ' and "created" >= ToDate(' + scope(start) + ') and "created" <= ToDate(' + scope(finish) +')'
    task = server.Get("TaskDone", sql)
    print task 
    print start
    print finish
    for t in task:
        for i in t.items:
            ti = TaskInfo()
            task = getTaskById(server, i.id)
            
            if task != None:
                ti.text = task.text
                ti.comment = i.text
                
                if i.done == 1:
                    print "task done!"
                    lst = getListFromDict(task.orgid, task.id, taskDone, taskDoneIds)
                else:
                    lst = getListFromDict(task.orgid, task.id, taskMissed, taskMissedIds)

                lst.append(ti)
                 
    sql = '"userid"=' +\
        scope(userid) + ' and "start" >= ToDate(' + scope(start) + ') and "start" <= ToDate(' + scope(finish) +')'
    task = server.Get("OrgTask", sql)   
    
    for t in task:
        if not (t.id in taskMissedIds or t.id in taskDoneIds):
            ti = TaskInfo()
            ti.text = t.text 
            lst = getListFromDict(t.orgid, t.id, taskMissed, taskMissedIds)
            lst.append(ti)    
            
    return taskDone, taskMissed, taskDoneIds, taskMissedIds        

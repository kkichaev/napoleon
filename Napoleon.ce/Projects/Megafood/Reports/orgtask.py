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

from task import taskReport
from task import taskList
from task import taskReportXLS

    
def run(server):

    print "start"
    
    param = server.Params[0];
    if param.mode == "report" :
        taskReport(server, param)
    elif param.mode == "reportXLS" :
        taskReportXLS(server, param)
    else :
        taskList(server, param)
    
    print "done"
   

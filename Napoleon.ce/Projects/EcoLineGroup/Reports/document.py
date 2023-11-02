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
import project

def getDocList(server):
    userid = server.Params[0].userid;
    d1 = server.Params[0].date
    d2 = d1
     
    result = list()
         
    where = '"userid"="{0}" and "created" >= ToDate("{1}") and "created" <= ToDate("{2}")'.format(
        userid, d1.strftime("%d/%m/%Y 0:0:0"),d2.strftime("%d/%m/%Y 23:59:59"))
    
    for o in project.doclist: 
        obj = server.Get(o.OBJECT_NAME, where)
        result.append(obj)
   
    return result

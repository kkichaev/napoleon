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

import manager.mapgis
import manager.userlog
import manager.orglist
import manager.pricelist
import summary_monitor

import manager.document

from datetime import datetime
from manager.summary import plannedOrgs

class AddDocLoader:
    server = None
    ids = None
    
    def __init__(self, server):
        self.server = server
        self.ids = list()
        
    def add(self, id):
        if not id in self.ids: 
            self.ids.append(id)
    
    def docName(self): return ""
    def fieldName(self): return "id"
    def quoteField(self, field):
        return "'" + field + "'"
            
    def putSrv(self):
        if len(self.ids) > 0:
            where = '"' + self.fieldName() + '" in ('
            for id in self.ids:
                where += self.quoteField(id) + ","
            
            where = where[:-1] + ')'
            docs = self.server.Get(self.docName(), where)
            if docs != None :
                self.server.Put(docs)
        
class AddScritpDef(AddDocLoader):
    def __init__(self, server):
        AddDocLoader.__init__(self, server)
       
    def docName(self): return "ScriptDef"
    def quoteField(self, field): return str(field)
        
    def process(self, doc):
        if isinstance(doc, manager.document.ScriptDoc):
            self.add(doc.scriptId)
           
           
class AddQuestion(AddDocLoader):
    def __init__(self, server):
        AddDocLoader.__init__(self, server)
        
    def docName(self): return "Question"            
    def fieldName(self): return "idquest"
    
    def process(self, doc):
        if isinstance(doc, manager.document.AnswerDoc):
            self.add(doc.question)
  
class NotVisited:
    server = None
    plans = None
    userid = None 
    orgs = None
    date = None

    def __init__(self, server, userid, date, orgs):  
        self.server = server
        self.userid = userid
        self.plans = plannedOrgs(server, userid, date)
        self.orgs = orgs
        self.date = date

    def process(self, doc):
        if doc.id in self.plans:
            self.plans.remove(doc.id)
        
    def putSrv(self):
        if len(self.plans) > 0 :
            objs = self.server.New("NotVisitedOrg")
            for id in self.plans:
                obj = objs.New()
                obj.id = id
                obj.date = self.date
                obj.userid = self.userid
                
                self.orgs.process(obj)
                
            self.server.Put(objs)
                
            

def run(server):
    print( "start\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S'))
    process(server)
    print ("finish\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S'))
    
def getGps(server):
    param = server.Params[0]
    userid = param.userid
    date = param.date
    
    print( "userid: ", userid, "date:", date)
    where = '"userid"=\'{0}\' and "date" >= ToDate("{1}") and "date" <= ToDate("{2}")'.format(
        userid, date.strftime("%d/%m/%Y 0:0:0"), date.strftime("%d/%m/%Y 23:59:59")) 

    return server.Get("GPSPos", where) 
    
def process(server):
    param = server.Params[0]
    userid = param.userid
    date = param.date
    
    user = server.CurrentUser()
    where = '"login"=' + "'" + str(user.id) + "'"
    divMgr = server.Get("DivisionManager", where)
    
    where = summary_monitor.makeQuery('"userid"=\'{0}\''.format(userid), date, divMgr[0].suppl)
    whereScript = '"created" in (select "created" from "ScriptDoc" sd where sd."created" >= ToDate("{0}") and '\
                  'sd."created" < ToDate("{1}") and sd."scriptId" in (select "id" from "ScriptDef" where "suppl" = \'{2}\')) '\
                  'and "userid"=\'{3}\''.format(date.strftime("%d/%m/%Y 0:0:0"), date.strftime("%d/%m/%Y 23:59:59"), divMgr[0].suppl, userid)

    docList = manager.document.getDocListW(server, lambda x: where if x.objectName != "ScriptDoc" else whereScript)
    param = server.Params[0]
    userid = param.userid
    date = param.date
    
    orgs = manager.orglist.OrgList(server) 
    obj = [manager.mapgis.MapGis(server, orgs), manager.pricelist.PriceList(server), AddScritpDef(server), 
           AddQuestion(server), NotVisited(server, userid, date, orgs), orgs]
    
    if docList != None:
        for doc in docList:
            if doc.servObject != None:
                server.Put(doc.servObject)
                for d in doc:
                    for o in obj:
                        o.process(d)

    for o in obj:
        o.putSrv()
    
    server.Put(getGps(server))
    manager.userlog.putSrv(server)  
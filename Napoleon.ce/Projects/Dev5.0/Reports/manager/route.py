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

from . import mapgis
from . import userlog
from . import orglist
from . import pricelist

from . import document

from datetime import datetime
from .summary import plannedOrgs

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
        if isinstance(doc, document.ScriptDoc):
            self.add(doc.scriptId)
           
           
class AddQuestion(AddDocLoader):
    def __init__(self, server):
        AddDocLoader.__init__(self, server)
        
    def docName(self): return "Question"            
    def fieldName(self): return "idquest"
    
    def process(self, doc):
        if isinstance(doc, document.AnswerDoc):
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
    print("start\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S'))
    process(server)
    print("finish\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S'))
    
def getGps(server):
    param = server.Params[0]
    userid = param.userid
    date = param.date
    
    print("userid: ", userid, "date:", date)
    where = '"userid"=\'{0}\' and "date" >= ToDate("{1}") and "date" <= ToDate("{2}")'.format(
        userid, date.strftime("%d/%m/%Y 0:0:0"), date.strftime("%d/%m/%Y 23:59:59")) 

    return server.Get("GPSPos", where) 
    
def process(server):
    docList = document.getDocList(server)
    param = server.Params[0]
    userid = param.userid
    date = param.date
    
    orgs = orglist.OrgList(server) 
    obj = [mapgis.MapGis(server, orgs), pricelist.PriceList(server), AddScritpDef(server), 
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
    userlog.putSrv(server)  
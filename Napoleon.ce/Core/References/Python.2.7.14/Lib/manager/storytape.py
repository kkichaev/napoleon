# -*- coding: cp1251 -*-
import sys
import time
from datetime import timedelta
from datetime import datetime
from decimal import *

from document import docTypes
from document import Order
from manager.summary import loadAgents

import tempfile
import io
import coordutils

def updateVisitItem(p, t, v, f):
    t.name = f.name if f != None else 'Посещение'
    t.text = 'ОК'

def updateRemnantsItem(p, t, d, f):
    t.name = 'Снятие остатков'
    t.text = 'ОК'
  
def updateAnswerItem(p, t, d, f):
    t.name = 'Анкета'
    t.text = 'ОК'

def updateOrderItem(p, t, o, f):
    t.name = 'Заявка'
    s = 0.0;
  
    for i in o.items:
        s += i.cost * i.qty
  
    t.text = "{0:.2f}".format(s) + ' р.'  
  
def updateSalesItem(p, t, o, f):
    t.name = 'Продажа'
    s = 0.0;
  
    for i in o.items:
        s += i.cost * i.qty
  
    t.text = "{0:.2f}".format(s) + ' р.' 

def updateIncassItem(p, t, i, f):
    t.name = 'Инкассация'
    t.text = "{0:.2f}".format(i.sum) + ' р.'  
  
def updateItem(p, i, d, f):
    print "i.name", i.name

    if i.name == 'Visit':
        updateVisitItem(p, i, d, f)
    elif i.name == 'OrgRemnants':
        updateRemnantsItem(p, i, d, f)
    elif i.name == 'Order':
        updateOrderItem(p, i, d, f)
    elif i.name == 'Answer':
        updateAnswerItem(p, i, d, f)
    elif i.name == 'Incass':
        updateIncassItem(p, i, d, f)
    elif i.name == 'Sales': 
        updateSalesItem(p, i, d, f)
    
def docIntoScript(data, name, doc):
    return (name in data) and (doc.userid in data[name]) and (doc.created in data[name][doc.userid])
  
def visits_hash(server, where, useFoto):
    
    ds = None
    
    if not useFoto:
        ds = server.Get('VisitInfo', where)
    else:
        ds = server.Get('VisitPreview', where)

    ret = dict()  # userid - created - doc
    
    if not ds == None:
        for d in ds:
            if not d.userid in ret:
                ret[d.userid] = dict()
            
            ret[d.userid][d.created] = d

    return ret

class AddUserDocs:
    def addDocs(self, server, where, outHelper):
        pass

class OutputHelper:
    __slots__ = ['scriptdoc', 'output', 'orgs', 'uids']
    
    def __init__(self, scriptdoc, output, orgs, uids):
        self.scriptdoc = scriptdoc
        self.output = output
        self.orgs = orgs
        self.uids = uids
        
    def addDoc(self, doc, docName):
        if docIntoScript(self.scriptdoc, docName, doc):
            return None
        
        obj = self.output.New()
        obj.created = doc.created
        obj.userid = doc.userid
        obj.username = self.uids[doc.userid].name
        obj.sended = doc.sended if doc.sended != None else doc.created
        
        item = obj.items.New()
        item.created = doc.created;
        item.name = docName
        
        updateItem(obj, item, doc, None)
        
        obj.id = doc.id
        obj.org = self.orgs[doc.id].name if doc.id in self.orgs else doc.id    

        return obj

def collectStory(server, uids, date, output, useFoto, addUserDocs):
    finish = datetime.now().date() - timedelta(days=3)
    data = list()
    
    scriptdoc = dict()  # doctype - userid - created
    fstr = finish.strftime("%d/%m/%Y 0:0:0")
    where = '"created" >= ToDate(\'{0}\')'.format(fstr);
    visit_hash = visits_hash(server, where, useFoto)
    
    
    for user in uids:
      server.ChangeUser("'" + user + "'")
      orgs = server.Get("Org", "", "id")
      script_def = server.Get("ManagerScriptDef", "", "id")
      server.RestoreUser()
    
      outHelper = OutputHelper(scriptdoc, output, orgs, uids)    
    
      where = '"userid"=\'{0}\' and "created" >= ToDate(\'{1}\')'.format(user, fstr)
      scripts = server.Get("ScriptDoc", where)
     
      if scripts != None:
        for s in scripts:
          obj = outHelper.addDoc(s, script_def[s.scriptId].name if s.scriptId in script_def else 'ScriptDoc')
          if obj == None: continue
        
          for x in range(len(s.items)):
            i = s.items[x]
            if not i.type in scriptdoc:
              scriptdoc[i.type] = dict()
          
            if not s.userid in scriptdoc[i.type]:
              scriptdoc[i.type][s.userid] = list()
            
            scriptdoc[i.type][s.userid].append(i.date)
          
            iw = '"userid"=\'{0}\' and "created"=ToDate(\'{1}\')'.format(user, i.date.strftime('%d/%m/%Y %H:%M:%S'))
          
            if i.type != 'Visit':
              ds = server.Get(i.type, iw)
          
            iw = 'id={0}'.format(s.scriptid)
            df = server.Get('ScriptDef', iw)
          
            dfi = None
          
            if len(df) > 0:
              dfd = df[0]
              dfi = dfd.items[x] if x < len(dfd.items) else None
          
            if i.type == "Visit":
              d = None
            
              if s.userid in visit_hash and i.date in visit_hash[s.userid]:
                d = visit_hash[s.userid][i.date]
                
                item = obj.items.New()
                item.name = i.type
                item.created = d.created;
                
                if d.items != None:
                  for i in d.items:
                    if len(i.smallPhoto) > 0:
                      photo = obj.photo.New()
                      photo.name = i.name
                      photo.pic = i.smallPhoto
                    
                updateItem(obj, item, d, dfi)
            elif ds != None:
              for d in ds:
                item = obj.items.New()
                item.name = i.type
                item.created = d.created;
              
                updateItem(obj, item, d, dfi)
        
      
      orders = server.Get("Order", where)
      
      for o in orders:
          obj = outHelper.addDoc(o, 'Order')
          if obj == None: continue
          
      sales = server.Get("Sales", where)
      if sales != None:
        for s in sales:
          obj = outHelper.addDoc(s, 'Sales')
          if obj == None: continue
          
      incass = server.Get("Incass", where)
      for i in incass:
        obj = outHelper.addDoc(i, 'Incass')
        if obj == None: continue

      remnants = server.Get("OrgRemnants", where)
      
      for r in remnants:
        obj = outHelper.addDoc(r, 'OrgRemnants')
        if obj == None: continue
        
      answer = server.Get("Answer", where)
      for a in answer:
        obj = outHelper.addDoc(a, 'Answer')
        if obj == None: continue
      
      if user in visit_hash:
        for v in visit_hash[user].values():
          obj = outHelper.addDoc(v, 'Visit')
          if obj == None: continue
          
          if v.items != None:
              for i in v.items:
                  if len(i.smallPhoto) > 0:
                    photo = obj.photo.New()
                    photo.name = i.name
                    photo.pic = i.smallPhoto

      if addUserDocs != None:
          addUserDocs.addDocs(server, where, outHelper)  
    
def run(server, useFoto=True, addUserDocs=None):
    print "start\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S')
    reload(sys)
    sys.setdefaultencoding("cp1251")

    params = server.Params
   
    if params == None:
        print "Params is empties"
        return
   
    user = server.CurrentUser()
    where = '"login"=' + "'" + str(user.id) + "'"
    divMgr = server.Get("DivisionManager", where)
    if divMgr == None:
        print "No manager"
        return

    divisions = list()
    rootDivision = server.Get("Division", '"id"=' + str(divMgr[0].division))

    divAgents = loadAgents(server, rootDivision, divisions)
   
    range = params[0].range
    
    server.RegisterType("StoryTape[created:dt,sended:dt,userid:s,username:s,id:s,org:s,items[created:dt,name:s,text:s],photo[name:s,pic:b]]")
    output = server.New("StoryTape")
    collectStory(server, divAgents, range, output, useFoto, addUserDocs)
    server.Put(output)

    print "finish\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S')

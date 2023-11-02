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

from datetime import timedelta
from datetime import datetime

import tempfile
import io


import sys;
reload(sys);
sys.setdefaultencoding("cp1251")

class Report:
    name = None
    address = None
    categ = None
    phone = None
    email = None
    cheif = None
    contact = None
    dealers = None
    beforevisit = None
    concurent = None
    visits = None
    target = None
        
    def __init__(self):
        self.name = ""
        self.address = ""
        self.categ = ""
        self.phone = ""
        self.email = ""
        self.cheif = ""
        self.contact = ""
        self.dealers = ""
        self.beforevisit = ""
        self.concurent = ""
        self.visits = ""
        self.target = ""
        

def inflateParams(server):
    return server.Params[0].id
 
def visits(vs, br):
    result = ""
    for v in vs:
        if len(result) > 0:
            result += br
        
        result += v.remark
        
    return result    
         
def dealers(list, data, br):
    result = ""
    
    for i in list:
        if i.id in data:
            if (len(result) > 0):
               result += br
            result += data[i.id].name    
    
    return result

def target(notes):
    result = ""
    
    if len(notes) > 0:
        result = notes[0].date.strftime('%d/%m/%Y') + '  ' + notes[0].text
        
    return result

def setOrgData(rpt, od):
    if(len(od) > 0):
        i = od[0]
        rpt.beforevisit = i.befvisit
        rpt.concurent = i.concurents
                
def loadData(server, br):
    id = inflateParams(server)
    
    orgs = server.Get("CommonOrgs","","id")
    ot = server.Get("OrgType", "", "id")
    dls = server.Get("Dealer", "", "id")
    vst = server.Get("VstQuery", id)
    on = server.Get("OrgNotes", "\"id\"='{0}'".format(id))
    od = server.Get("OrgData", "\"id\"='{0}'".format(id))
    
    r = Report()
    
    if id in orgs:
        o = orgs[id]
        r.name = o.name 
        r.address = o.address
        r.categ = ot[o.orgType].name if o.orgType in ot else "";
        r.phone = o.cheifPhone
        r.email = o.email
        r.cheif = o.cheif
        r.contact = o.contact
        r.dealers = dealers(o.dealers, dls, br)
        r.visits = visits(vst, br)
        r.target = target(on)
        setOrgData(r,od)
    
    return r
   

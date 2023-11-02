# -*- coding: cp1251 -*-

from stobj import StObj
from objects import *
from robj import RObj
 
__data__ = None
 
class OrgList(RObj, StObj):
    outlist = None
    
    def __init__(self, server):
        RObj.__init__(self, server)
        StObj.__init__(self)
        
        init(server)
        self.outlist = server.New(Org.OBJECT_NAME)
            
    def put(self, id):
        if id in __data__ and self.check(id):    
            self.outlist.Add(__data__[id])
            
    def process(self, doc):
        self.put(doc.id)
        
    def putSrv(self):
        self.server.Put(self.outlist)    
        
def init(server):
    global __data__
    if __data__ == None:
        userid = server.Params[0].userid
        uidFilter = "\"{0}\"='{1}'".format(USERID, userid)
        __data__ = server.Get(Org.OBJECT_NAME, uidFilter, Org.KEY_FIELD)
     
        if len(__data__) == 0:
            __data__ = server.Get(Org.OBJECT_NAME, "", Org.KEY_FIELD)
             
def name(id):
    result = ""
    global __data__
     
    if id in __data__:
        result = __data__[id].name
    else:
        result = "контрагент код<{0}>".format(id);         
         
    return result        
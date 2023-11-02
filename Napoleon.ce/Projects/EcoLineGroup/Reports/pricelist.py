# -*- coding: cp1251 -*-
from objects import *
from robj import RObj
from stobj import StObj

__data__ = None
 
class PriceList(RObj, StObj):
    outlist = None
    
    def __init__(self, server):
        RObj.__init__(self, server)
        StObj.__init__(self)
        
        init(server)
        self.outlist = server.New(Price.OBJECT_NAME)
            
    def put(self, id):
        if id in __data__ and self.check(id):
            self.outlist.Add(__data__[id])
            
    def process(self, doc):
        if hasPriceItem(doc.GetName):
            for i in doc.items:
                self.put(i.id)
        
    def putSrv(self):
        self.server.Put(self.outlist)    
        
def init(server):
    global __data__
    if __data__ == None:
        userid = server.Params[0].userid
        uidFilter = "\"{0}\"='{1}'".format(USERID, userid)
        __data__ = server.Get(Price.OBJECT_NAME, uidFilter, Price.KEY_FIELD)
     
        if len(__data__) == 0:
            __data__ = server.Get(Price.OBJECT_NAME, "", Price.KEY_FIELD)
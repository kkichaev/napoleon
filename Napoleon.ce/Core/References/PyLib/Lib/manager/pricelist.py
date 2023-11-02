# -*- coding: cp1251 -*-
from objects import Price, hasPriceItem
from robj import RObj
from stobj import StObj

 
class PriceList(RObj, StObj):
    outlist = None
    __data__ = None
    
    def __init__(self, server):
        RObj.__init__(self, server)
        StObj.__init__(self)
        
        userid = "'" + server.Params[0].userid + "'"
        server.ChangeUser(userid)
        self.__data__ = server.Get(Price.OBJECT_NAME, "", Price.KEY_FIELD)
        server.RestoreUser()

        self.outlist = server.New(Price.OBJECT_NAME)
            
    def put(self, id):
        if id in self.__data__ and self.check(id):
            self.outlist.Add(self.__data__[id])
            
    def process(self, doc):
        if hasPriceItem(doc.GetName):
            for i in doc.items:
                self.put(i.id)
        
    def putSrv(self):
        self.server.Put(self.outlist)    

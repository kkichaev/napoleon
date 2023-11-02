# -*- coding: cp1251 -*-

from .stobj import StObj
from .objects import Org, PotenzialOrg
from .robj import RObj
 
 
class OrgList(RObj, StObj):
    outlist = None
    __data__ = None
    
    def __init__(self, server, userid=None):
        RObj.__init__(self, server)
        StObj.__init__(self)
        
        if userid == None:
            userid = "'" + server.Params[0].userid + "'"
        
        server.ChangeUser(userid)
        self.__data__ = server.Get(Org.OBJECT_NAME, "", Org.KEY_FIELD)             
        porgs = server.Get(PotenzialOrg.OBJECT_NAME, "", PotenzialOrg.KEY_FIELD)
        server.RestoreUser()
    
        if porgs != None :
            for ido in porgs.keys() :
                self.__data__[ido] = porgs[ido]  

        self.outlist = server.New(Org.OBJECT_NAME)
    
    def put(self, id):
        if self.__data__ != None and id in self.__data__ and self.check(id):
            self.outlist.Add(self.__data__[id])
            
    def process(self, doc):
        self.put(doc.id)
        
    def putSrv(self):
        self.server.Put(self.outlist)
    
    def name(self, id):
        result = ""
        
        if self.__data__ != None and id in self.__data__:
            result = self.__data__[id].name
        else:
            result = "контрагент код<{0}>".format(id);         
             
        return result

    def address(self, id):
        result = ""
        
        if self.__data__ != None and id in self.__data__:
            result = self.__data__[id].address
        else:
            result = "";         
             
        return result        
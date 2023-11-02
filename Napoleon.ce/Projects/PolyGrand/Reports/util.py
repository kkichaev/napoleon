# -*- coding: cp1251 -*-

class UnknownOrg:
    id = ""
    name = ""
    address = ""
    
    orgs = {}
    
    @staticmethod
    def get(id, orgs):
        if id in orgs:
            return orgs[id]
        
        org = UnknownOrg()
        org.id = id
        org.name = "Контрагент с кодом <" + id + ">"
        
        orgs[id] = org
        return org
    
    def __str(self):
        return name


def scope(val):
    return "'" + val + "'"
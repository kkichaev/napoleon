# -*- coding: cp1251 -*-

USERID = "userid"
ID = "id"

class Org:
    OBJECT_NAME = "Org"  
    KEY_FIELD = "id"
    
class Order:
    OBJECT_NAME = "Order"  
    TITLE = "Заявка"

class VisitInfo:
    OBJECT_NAME = "VisitInfo"
    TITLE = "Посещение"
    
class OrgRemnants:
    OBJECT_NAME = "OrgRemnants"
    TITLE = "Остатки"    
    
class Price:
    OBJECT_NAME = "Price"
    KEY_FIELD = "id"    

class Distribution:
    OBJECT_NAME = "DistrDoc"
    TITLE = "Дистрибуция"    
    
def hasPriceItem(docname):
    return docname == Order.OBJECT_NAME or \
           docname == OrgRemnants.OBJECT_NAME    
           
def objTitle(name):
    result = "not implemented yet for type: " + name
        
    if name == Order.OBJECT_NAME:
        result = Order.TITLE
    elif name == VisitInfo.OBJECT_NAME:
        result = VisitInfo.TITLE    
    elif name == OrgRemnants.OBJECT_NAME:
        result = OrgRemnants.TITLE
        
    return result              
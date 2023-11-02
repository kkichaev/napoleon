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

docTypes = list()
__inited__ = False


class BaseDocument:
    doc = None
    def __init__(self, doc):
        self.doc = doc
        
    def __getattr__(self, name):
        return getattr(self.doc, name)   

    def sum(self):
        return 0

class DocListIterator:
    objIterator = None
    docWraper = None
    
    def __init__(self, servObject, docWraper):
        self.objIterator = servObject.__iter__()
        self.docWraper = docWraper
        
    def __iter__(self):
        return self
    
    def next(self):
        nextObj = self.objIterator.next()
        return self.docWraper(nextObj)

class EmptyIterator:
    def __iter(self):
        return self
    
    def next(self):
        raise StopIteration

class DocList:
    __slots__ = ['servObject', 'docWraper']
    
    def __init__(self, serverObject, docWraper):
        self.servObject = serverObject
        self.docWraper = docWraper
        
    def __iter__(self):
        if self.servObject == None :
            return EmptyIterator()
        
        return DocListIterator(self.servObject, self.docWraper)

class DocType:
    docWraper = None
    objectName = None
    title = None
    
    def __init__(self, objectName, title, docWraper):
        self.docWraper = docWraper
        self.objectName = objectName
        self.title = title
        
    def docList(self, server, where):
        return DocList(server.Get(self.objectName, where), self.docWraper)


class Order(BaseDocument):
    def sum(self):
        sm = 0
        for i in self.doc.items:
            sm += i.qty * i.cost
            
        return sm
    
class Incass(BaseDocument):
    def sum(self):
        return self.doc.sum
        
class ScriptDoc(BaseDocument):
    pass    

class AnswerDoc(BaseDocument):
    pass    
    
def getDocList(server):
    userid = server.Params[0].userid;
    d1 = server.Params[0].date
    d2 = d1
    
    where = '"userid"={0} and "created" >= ToDate("{1}") and "created" <= ToDate("{2}")'.format(
        "'" + userid + "'", d1.strftime("%d/%m/%Y 0:0:0"),d2.strftime("%d/%m/%Y 23:59:59"))
    
    return getDocListW(server, lambda x : where)
    
def getDocListW(server, getWhere):
    result = list()
         
    for dt in docTypes:
        where = getWhere(dt)

#        print dt.objectName
#        print where

        docs = dt.docList(server, where)
        if docs.servObject != None and len(docs.servObject) > 0:
            print dt.objectName
        result.append(docs)
    
    return result    

if not __inited__:
    __initted = True
    
    docTypes.append(DocType("Order", "Заявка", Order))
    docTypes.append(DocType("VisitInfo", "Посещение", BaseDocument))
    docTypes.append(DocType("OrgRemnants", "Остатки", BaseDocument))
    docTypes.append(DocType("Monitoring", "Мониторинг", BaseDocument))
    docTypes.append(DocType("Incass", "Инкассация", Incass))
    docTypes.append(DocType("Answer", "Анкета", AnswerDoc))
    docTypes.append(DocType("Sales", "Продажа", Order))
    docTypes.append(DocType("Pko", "ПКО", BaseDocument))
    docTypes.append(DocType("Returns", "Возврат", BaseDocument))
    docTypes.append(DocType("ScriptDoc", "Сценарий", ScriptDoc))

# -*- coding: cp1251 -*-
from datetime import datetime
from datetime import timedelta
    
def copyDoc(destDocs, src, fields):
    dest = destDocs.New()
    
    for f in fields:
#         print f.Name + " " + str(f.Type)
        if destDocs.HaveField(f.Name):
            if f.Type == 4:
                destcol = getattr(dest, f.Name)
                srccol = getattr(src, f.Name)
                srcF = srccol.Fields()
                for srcI in srccol:
                    copyDoc(destcol, srcI, srcF)
            else:
                setattr(dest, f.Name, getattr(src, f.Name))    
    
def run(server):

    print "start"
    
    used = dict()
    allPOD = server.Get('All_POD', '')
    if allPOD != None:
        for doc in allPOD:
            type = doc.type
            if len(type) == 0: type = 'Order'
            if not type in used:
                used[type] = dict()
            used[type][doc.created] = True

    DAYS_BEFORE = 5
    startDate =  datetime.now() + timedelta(days = -DAYS_BEFORE)
    
    toDate = 'ToDate("{0}")' . format(startDate.strftime("%d/%m/%Y"))
    docType = "Order"
    
#     for docType in ['Returns']:
    for docType in ['Order', 'Incass', 'Returns']:
        
        where = '"created" >= ' + toDate
        typeWhere = '"type" = ' + "'" + docType + "'"
        if docType == 'Order':
            typeWhere += ' or "type"=' + "''" 
        where += ' and not ("created" in (select "created" from "ArchiveOrderProceeded" where ' + typeWhere + ' and "created" >= ' + toDate + "))"
        
        usedDocs = dict() if not docType in used else used[docType]
        docs = server.Get(docType, where)
        destWr = dict()
        if docs != None and len(docs) > 0:
                        
            print 'Get ' + docType +  " " + str(len(docs))
            fields = docs.Fields()
            for src in docs:
                if src.created in usedDocs: continue
                
                userid = src.userid
                if not userid in destWr:
                    destWr[userid] = server.New("DBF" + docType)
                wrDocs = destWr[userid]
                
#                 print "Copy doc"
                copyDoc(wrDocs, src, fields)
            
            if len(destWr) > 0:
                print "Write  " + docType +  ' ' + str(len(docs))
                for k, v in destWr.iteritems():
                    server.ChangeUser("'" + k + "'")
                    server.Write(v)
                    server.RestoreUser()
            
    
    print "done"
   

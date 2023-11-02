# -*- coding: cp1251 -*-

from datetime import datetime
from datetime import timedelta

class DebtKey:
    __slots_ = ['ido', 'firm']
    
    def __init__(self, doc):
        self.ido = doc.ido
        self.firm = doc.firm
        
    def __hash__(self):
        return hash((self.ido, self.firm))
    
    # def __cmp__(self, other):
    #     val = cmp(self.ido, other.ido)
    #     return val if val != 0 else cmp(self.firm, other.firm)
 
# def cmpDocs(x, y):
#     val = cmp(x.date, y.date)
#     return val if val != 0 else cmp(x.number, y.number)

def docSum(doc):
    sum = 0
    for i in doc.items:
        sum += i.sum
    return sum        

def run(server):
    print ("Start")
    
    debts = dict()
    pays = server.Get('AllPayments', '')
    for pdoc in pays:
        key = DebtKey(pdoc)
        debts[key] = pdoc.sum

    ddocs = dict()
    startDate = datetime.now() + timedelta(days = -180)
    where = '"created" >= ToDate("{0}")' . format(startDate.strftime('%d/%m/%Y'))
    dlvdocs = server.Get('Delivery', where)
    for ddoc in dlvdocs:
        key = DebtKey(ddoc)
        if not key in ddocs:
            ddocs[key] = list()
            
        ddocs[key].append(ddoc)
    
    wrSums = server.New('DlvDebet')    
    for k,v in ddocs.items():
        sum = 0 if not k in debts else debts[k]
        docs = sorted(v, key=lambda x : (x.date, x.number), reverse=True)
        
#         print str(sum) + ' ' + str(len(docs))
        
        for d in docs:
            if sum == 0: break
            
            sumD = docSum(d)
            if sumD > sum: sumD = sum
            
            wr = wrSums.New()
            wr.number = d.number
            wr.id = d.id
            wr.sum = sumD
            sum = sum - sumD
            
    if len(wrSums) > 0:
        server.Remove('DlvDebet', '')
        server.Write(wrSums)
        
    print ('Done')
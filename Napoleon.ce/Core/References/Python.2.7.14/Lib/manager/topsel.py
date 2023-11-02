# -*- coding: cp1251 -*-

import sys
from datetime import timedelta, datetime
from manager.summary import loadAgents  # @UnresolvedImport

def queryOrders(server, uids, rng):
    finish = datetime.now().date() + timedelta(days=1)
    start = finish - timedelta(days=rng+1);
    
    where = '"userid" in ({0}) and "created" >= ToDate("{1}") and "created" < ToDate("{2}")'.format(
        uids, start.strftime("%d/%m/%Y 0:0:0"), finish.strftime("%d/%m/%Y 0:0:0"))
    
    return server.Get("Order", where)
    
def collectOrderItems(server, uids, rng):
    orders = queryOrders(server, uids, rng)
    
    data = dict() #data - userid - id - qty
    
    for o in orders:
        d = o.created.date()
        if not d in data:
            data[d] = dict()
            
        data_data = data[d]
        
        if not o.userid in data_data:
            data_data[o.userid] = dict()
            
        data_userid = data_data[o.userid]
                  
        for i in o.items:
            if not i.id in data_userid:
                data_userid[i.id] = 0
            
            data_userid[i.id] += i.qty
            
    return data

def userids(agents):
    ret = ""
    
    for a in agents:
        if len(ret) > 0:
            ret += ", "
            
        ret += "'" + a + "'"
    
    return ret
    
class Goods:
    __slots__= ["userid", "items"]
  
    def __init__(self, userid):
        self.items = dict()
        self.userid = userid
    
    def add_item(self, id, name, qty):
        if not id in self.items:
            self.items[id] = GoodsItem(id,name)
      
        self.items[id].qty = self.items[id].qty + qty
    
    def getTop10(self):
        res = list()
        res.extend(self.items.values())
    
        res = sorted(res, cmp=lambda lhs, rhs: cmp(rhs.qty, lhs.qty))
        res = res[:10]
        return res
    
class GoodsItem:
    __slots__=["id","name","qty"]
  
    def __init__(self, id, name):
        self.id = id
        self.name = name
        self.qty = 0

def put_data(data, objList, period):
    for g in data.values():
        top10 = g.getTop10()
      
        obj = objList.New()
        obj.userid = g.userid
        obj.period = period
      
        for gi in top10:
            objItem = obj.items.New()
            objItem.id = gi.id
            objItem.name = gi.name
            objItem.qty = gi.qty
        
def run(server):
    print "start\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S')
    reload(sys)
    sys.setdefaultencoding("cp1251")  # @UndefinedVariable

    params = server.Params
   
    if params == None:
        print "Params is empties"
        return
   
    user = server.CurrentUser()
    where = '"login"=' + "'" + str(user.id) + "'"
    divMgr = server.Get("DivisionManager", where)
    
    if divMgr == None:
        print "No manager"
        return

    divisions = list()
    rootDivision = server.Get("Division", '"id"=' + str(divMgr[0].division))

    divAgents = loadAgents(server, rootDivision, divisions)
    data = collectOrderItems(server, userids(divAgents), 7 * 4)
    
    price = server.Get("ManagerPrice", "", "id")
    
    server.RegisterType("TopSel[userid:s,period:n(0),items[id:s,name:s,qty:n(3)]]")
    objList = server.New("TopSel")
   
    #data - userid - id - qty
    
    resMonth = dict()  # userid - UserItem
    resWeek = dict() 
    resDay = dict()
    
    today = datetime.now()
    
    allUserMoth = Goods("")
    allUserWeek = Goods("")
    allUserDay = Goods("")
    
    for d in data:
        data_userid = data[d]
        days = (today - datetime.combine(d, datetime.min.time())).days
      
        for u in data_userid:
      
            if not u in resMonth:
                resMonth[u] = Goods(u)
        
            goodsMonth = resMonth[u]
        
            if not u in resWeek:
                resWeek[u] = Goods(u)
          
            goodsWeek = resWeek[u]

            if not u in resDay:
                resDay[u] = Goods(u)
          
            goodsDay = resDay[u]  
      
            data_id = data_userid[u]
        
            for i, q in data_id.iteritems():
                pn = price[i].name if i in price else "Товар с кодом <{0}>".format(i)
                
                goodsMonth.add_item(i, pn, q)
                allUserMoth.add_item(i, pn, q)
              
                if days == 0:
                    goodsDay.add_item(i, pn, q)
                    allUserDay.add_item(i, pn, q)
                
                if days < 7:
                    goodsWeek.add_item(i, pn, q)
                    allUserWeek.add_item(i, pn, q)
        
    resMonth[""] = allUserMoth
    resWeek[""] = allUserWeek
    resDay[""] = allUserDay
    
    put_data(resMonth, objList, 4)
    put_data(resWeek, objList, 1)
    put_data(resDay, objList, 0)
        
    server.Put(objList)
    
    print "finish\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S')

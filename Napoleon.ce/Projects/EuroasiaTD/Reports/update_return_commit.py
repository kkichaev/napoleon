# -*- coding: cp1251 -*-


def run(server):
    uid = server.CurrentUser().id
    obj = server.Get('ReturnCommitKIS')
    if obj != None and len(obj) :
        dest = server.New('ReturnCommit')
        for o in obj:
            d = dest.New()
            d.userid = uid
            d.id = o.id
            d.created = o.created
            
            for i in o.items:
                di = d.items.New()
                di.id = i.id
                di.cost = i.cost
                di.remark = i.remark
                di.bestBefore = i.bestBefore
                di.qty = i.qty
                
        server.Write(dest)

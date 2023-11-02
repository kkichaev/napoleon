# -*- coding: cp1251 -*-
from datetime import datetime, timedelta
from manager.summary import getDivisionAgents

def run(server):
    print ("start\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S'))
    
    dateParam = (datetime.now() + timedelta(days=-7)).strftime('%d/%m/%Y')

    user = server.CurrentUser()
    where = '"login"=' + "'" + str(user.id) + "'"
    divMgr = server.Get("DivisionManager", where)
    if divMgr == None:
        print ("No manager")
        return

    divisions = list()
    rootDivision = server.Get("Division", '"id"=' + str(divMgr[0].division))

    agents = []
    getDivisionAgents(server, rootDivision, agents, divisions)
    
    agentParam = ""
    for aid in agents:
        agentParam += "'" + aid + "',"
    
    agentParam = agentParam[:-1]
    
    amm = server.Get("AgentManagerMemo", dateParam + ";" + agentParam)
    if amm != None:
        server.Put(amm)
    else:
        print("No agent memos")
    
    print ("finish\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S'))

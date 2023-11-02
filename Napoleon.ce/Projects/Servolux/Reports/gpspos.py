import logging
import sys
import locale
import datetime

from grsoft import gpspos

def run(server):
    param = server.Params[0]

    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    
    logging.debug("starting userid")

    locale.setlocale(locale.LC_ALL, 'american')
    logging.info("params " + str(param))

    start = datetime.datetime.strptime(param.start, "%d/%m/%Y %H:%M:%S")
    finish = datetime.datetime.strptime(param.finish, "%d/%m/%Y %H:%M:%S")
    
    server.RegisterType("Result[userid:s,length:n]")
    outObj = server.New("Result")
    
    res = []
    for agent in param.agents.split(","):
        if len(agent) > 0 :
            ln = gpspos.GPSRouteLength(server, agent, start, finish)
            out = outObj.New()
            
            out.length = ln
            out.userid = agent
        
    server.Put(outObj)
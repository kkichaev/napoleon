# -*- coding: cp1251 -*-

from importlib import reload
import sys
import logging
import locale

reload(sys)
#sys.setdefaultencoding("cp1251")


def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    
    logging.debug("starting")

    locale.setlocale(locale.LC_ALL, 'american')
    params = server.Params[0]
    logging.info("params " + str(params))

    q = '"created" >= ToDate("{0}") and "created" < ToDate("{1}")'.format(
        params.start.strftime("%d/%m/%Y 0:0:0"),
        params.finish.strftime("%d/%m/%Y 0:0:0"))
    
    dest = server.New('ScriptUnload')
    docs = server.Get('ScriptDoc', q)
    if docs != None:
        count = 0
        for d in docs:
           finish = d.created
           if d.items != None:
               for si in d.items:
                   if si.state == 1 and si.date > finish:
                       finish = si.date
                       
           ddoc = dest.New()

           ddoc.id = d.id
           ddoc.userid = d.userid
           ddoc.start = d.created
           ddoc.finish = finish
           count += 1
        server.Write(dest)
        
        type = "ScriptUnloadResult[count:n]"
        server.RegisterType(type)
        result = server.New('ScriptUnloadResult')
        dres = result.New()
        dres.count = count
        server.Put(result)

    logging.info("ended")    
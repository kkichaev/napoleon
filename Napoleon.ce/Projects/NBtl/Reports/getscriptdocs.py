# -*- coding: cp1251 -*-
from importlib import reload
import sys;
import logging

from manager import pricelist

def process(server, params):
    where = 'created=ToDate("{0}") and userid="{1}"'
    scripts = server.Get("ScriptDoc", where.format(params.created.strftime("%d/%m/%Y %H:%M:%S"), params.userid))
    plist = pricelist.PriceList(server)

    if len(scripts) > 0:
        scr = scripts[0]
        scriptdef = server.Get("ScriptDef", "id={0}".format(scr.scriptId))
        
        server.Put(scripts)
        server.Put(scriptdef)

        for si in scr.items:
            type = "VisitInfo" if si.type == "Visit" else si.type
            docs = server.Get(type, where.format(si.date.strftime("%d/%m/%Y %H:%M:%S"), params.userid))
            
            if len(docs) > 0:
                plist.process(docs[0])
                plist.putSrv()
                server.Put(docs)

def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    
    logging.debug("starting")
    params = server.Params[0]
    logging.info("params " + str(params))
    process(server, params)
    logging.info("ended")
# -*- coding: cp1251 -*-
from importlib import reload
from datetime import datetime, timedelta
import calendar
import sys
import logging

def add_months(sourcedate, months):
    month = sourcedate.month - 1 + months
    year = sourcedate.year + month // 12
    month = month % 12 + 1
    day = min(sourcedate.day, calendar.monthrange(year,month)[1])
    return datetime(year, month, day)

def process(server, params):
    stmt = '''
        select sd.created as created, sd.userid as userid, sd.id as orgid, org.name || " " || org.address as orgname, 
        def.name as scriptname,def.id as scriptid
        from ScriptDoc sd, (select created, id, userid from ScriptDoc where created >= ToDate("{0}")) sd2
        left join org on org.id = sd.id  
        left join ScriptDef def on def.id = sd.scriptId 
        where sd.created = sd2.created and sd.userid = sd2.userid  and sd.userid="{1}"
    '''.format(add_months(datetime.today(), -3).strftime('%d.%m.%Y'), params.userid)
    
    docs = server.Query(stmt, 'LastScript[created:dt,userid:s,orgid:s,orgname:s,scriptname:s,scriptid:n]')
    server.Put(docs)

def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    
    logging.debug("starting")
    params = server.Params[0]
    logging.info("params " + str(params))
    process(server, params)
    logging.info("ended")
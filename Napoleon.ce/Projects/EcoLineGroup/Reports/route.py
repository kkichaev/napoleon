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

import datetime
import util
import mapgis
import userlog
import orglist
import pricelist

from datetime import datetime
from document import *
from objects import *

def run(server):
    print "start\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S')
    process(server)
    print "finish\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S')

def process(server):
    docList = getDocList(server)
    obj = [mapgis.MapGis(server), orglist.OrgList(server), pricelist.PriceList(server)]
    
    for doc in docList:
        server.Put(doc)
        for d in doc:
            for o in obj:
                o.process(d)

    for o in obj:
        o.putSrv()

    userlog.putSrv(server)  
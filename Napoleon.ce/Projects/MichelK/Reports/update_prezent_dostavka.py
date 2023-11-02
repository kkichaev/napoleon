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
from datetime import datetime
import io
import sys
import tempfile
import zlib
import os

reload(sys);
sys.setdefaultencoding("cp1251")

def run(server):
    print "start", __name__, datetime.now().strftime('%d/%m/%Y %H:%M:%S')
 
    photos = server.Get("UpdCrc")
    price = server.Get("DPrice", "", "id")
    
    priceIds = list()
    priceIds.extend(price.keys())
    
    filterPhotos = list()
    
    for p in photos:
        for i in p.items:
            if i.id in priceIds:
                filterPhotos.append(p)
                break
    
    exchageFolder = server.ExchangeFolder()

    param = server.Params[0]
    
    inp = dict()    
    for p in param.items:
        name = p.photopath.replace("\\", "/")
        inp[name[name.rfind('/') + 1:]] = p.crc    

    outs = server.New("PricePhoto")

    for obj in filterPhotos:
        name = obj.name.replace("\\", "/")
        name = name[name.rfind('/') + 1:]
        if not (name in inp) or inp[name] != obj.crc :
            out = outs.New()
            out.name = obj.name
            out.crc = obj.crc
            for oi in obj.items :
               doi = out.items.New()
               doi.id =	oi.id

    print "Post ",  len(outs), " obejcts"

    if len(outs) > 0 :
        server.Post(outs)

    print "done ", __name__, datetime.now().strftime('%d/%m/%Y %H:%M:%S')

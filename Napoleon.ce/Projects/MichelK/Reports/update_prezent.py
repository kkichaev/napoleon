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

from importlib import reload
import datetime
from datetime import datetime
import io
import sys
import tempfile
import zlib
import os

reload(sys);
#sys.setdefaultencoding("cp1251")

exchangeFolder = None
photos = None

def getPath(obj):
    global exchangeFolder

    path = obj.name.replace("/", "\\")
    if not (path[0] == '\\' or path[1] == ':') :
        path = exchageFolder + path

    return path    

def updateCrc(server):
    global photos

    if photos == None:
        return

    for obj in photos:
        path = getPath(obj)

        if os.path.isfile(path):
           fh = open(path, "rb")
           obj.crc = ("%X" % zlib.crc32(bytes(fh.read()))).replace("-", "")
           fh.close()
        else:
           print("no file '%s'" % path)

    server.Write(photos)
    print("end crc ", datetime.now().strftime('%d/%m/%Y %H:%M:%S'))



def run(server):
    print("start", __name__, datetime.now().strftime('%d/%m/%Y %H:%M:%S'))
 
    global photos
    global exchageFolder

    photos = server.Get("UpdCrc")
    exchageFolder = server.ImageFolder()
    print('img',exchageFolder)

    param = server.Params[0]

    if param.cmd == "update_crc" :
       updateCrc(server)
       return
    
    inp = dict()    
    for p in param.items:
        name = p.photopath.replace("\\", "/")
        inp[name[name.rfind('/') + 1:]] = p.crc    

#    print "inp ", inp

    outs = server.New("PricePhoto")

    for obj in photos:
        name = obj.name.replace("\\", "/")
        name = name[name.rfind('/') + 1:]
        if not (name in inp) or inp[name] != obj.crc :
            out = outs.New()
            out.name = obj.name
            out.crc = obj.crc
            for oi in obj.items :
               doi = out.items.New()
               doi.id =	oi.id

    print("Post ",  len(outs), " obejcts")

    if len(outs) > 0 :
        server.Post(outs)

#    print "outs ", outs
    print("done ", __name__, datetime.now().strftime('%d/%m/%Y %H:%M:%S'))

import datetime
from datetime import datetime
import zlib
import os



def updateCrc(server, photos, exchangeFolder):
    def getPath(obj):

        path = obj.name.replace("/", "\\")
        if not (path[0] == '\\' or path[1] == ':') :
            path = exchangeFolder + path

        return path

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
 
    photos = server.Get("UpdCrc")
    exchangeFolder = server.ImageFolder()
    print('img',exchangeFolder)

    param = server.Params[0]

    if param.cmd == "update_crc" :
    #    updateCrc(server, photos, exchangeFolder)
       return
    
    print('Req ids', len(param.items))

    photos = {}

    # stmt = '''select ppi.PricePhoto$name as name, ppi.id, pp.crc 
    #     from PricePhoto pp, PricePhoto$items ppi on pp.name = ppi.PricePhoto$name'''
    # phdata = server.Query(stmt, "Data[id:s,name:s,crc:s]")
    # for di in phdata:
    #     photos[di.id] = di

    class DataObj:
        def __init__(self, ii, di) -> None:
            self.id = ii.id
            self.name = di.name
            self.crc = di.crc

    phdata = server.Get('UpdCrc', '')
    for di in phdata:
        for ii in di.items:
            photos[ii.id] = DataObj(ii, di)
    print('Load photos', len(photos))

    outs = server.New("PricePhoto")
    for reqi in param.items:
        if not reqi.id in photos: continue
        di = photos[reqi.id]

        # print(di.crc, reqi.crc)

        if di.crc != reqi.crc:
            out = outs.New()
            out.name = di.name
            out.crc = di.crc

            doi = out.items.New()
            doi.id = di.id


    print("Post ",  len(outs), " objects")

    if len(outs) > 0 :
        phC = server.New('SendPhotoCount')
        phC.New().count = len(outs)
        server.Put(phC)

        server.Post(outs)

#    print "outs ", outs
    print("done ", __name__, datetime.now().strftime('%d/%m/%Y %H:%M:%S'))

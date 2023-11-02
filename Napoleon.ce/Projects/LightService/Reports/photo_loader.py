# -*- coding: cp1251 -*-

import sys
import os.path

def run(server):
    print('photo_loader start')

    params = server.Params[0]

    photos = server.Get('PricePhotoData', '', 'id')

    ids = params.ids.split(',')
    
#    print (ids)
    
    outP = server.New('PricePhoto')
    exchFolder = server.ExchangeFolder()

    for id in ids:
        if id in photos:
            path = photos[id].path
            fileName = exchFolder + path
            if not os.path.isfile(fileName): continue

            try:
                imgF = open(fileName, 'rb')
                img =  imgF.read()
                imgF.close()

                out = outP.New()
                out.photo = img
                outI = out.items.New()
                outI.id = id

            except :
                print("Error:", sys.exc_info()[0])


    server.Post(outP)
    print('photo_loader end ' + str(len(outP)))
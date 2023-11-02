# -*- coding: cp1251 -*-
import os
import sys
import fnmatch
import ftpConnect
import logging

def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    
    logging.debug("starting")

    folder = server.ExchangeFolder() + "\\out";
    if not os.path.exists(folder):
        return
    
    destFolder = ftpConnect.orderFolder(server)
    ftp = ftpConnect.makeFTPConnect(server)
    ftp.cwd(destFolder)
    
    for file in os.listdir(folder):
        if fnmatch.fnmatch(file, 'zz*.xml'):
            try:
                logging.debug('put file ' + file)

                fileName = folder + '\\' + file
                fd = open(fileName, 'rb')
                ftp.storbinary("STOR " + file, fd, 1024)
                ftp.sendcmd('SITE chmod 777 ' + file)
                fd.close()
                
                os.remove(fileName)
            except Exception as err:
                logging.error(err)
                
    ftp.close()
    
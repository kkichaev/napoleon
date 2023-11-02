# -*- coding: cp1251 -*-

from ftplib import FTP

def makeFTPConnect(server):
    site = server.Config('FTPSite')
    login = server.Config('FTPLogin')
    password = server.Config('FTPPassword')
    ftp = FTP(site, login, password)
    return ftp

def orderFolder(server):
    return server.Config('FTPOutFolder') + '/ORDERS'

def inputFolder(server):
    return server.Config('FTPInFolder')
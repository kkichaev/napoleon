#!/usr/bin/python

import smtplib
import shutil
import stat
import os
import time
import zipfile
import subprocess

from stat import *
from email.mime.text import MIMEText

SRC_DIR = "/data/ftp/pub/crash" 
DST_DIR = "/data/share/crash"

tm = 0
for a in os.listdir(DST_DIR):
    lt = os.stat(os.path.join(DST_DIR, a))[ST_MTIME]
    if lt > tm:
        tm = lt

src = dict()
for a in os.listdir(SRC_DIR):
    fl = os.path.join(SRC_DIR,a)
    if os.path.isfile(fl):
        lt = os.stat(fl)[ST_MTIME]
        if tm < lt:
            print( tm, lt)
            src[a] = fl
            
letter = '<html><h2>Congratulations! You have new crash!</h2>'
body = ""
SRV_DIR = "file://///server/data/crash/"
for f, pf in src.items():
    shutil.copy2(pf, DST_DIR) 
    fl = os.path.join(DST_DIR, f)
    f = f.replace('.','')
    body += '------------------------------------------------------------<br>' 
    body += '<a href="'+SRV_DIR + f + '.zip">File attached</a><br>'
    flz = os.path.join(DST_DIR, f + ".zip")
    os.rename(fl, flz)
    os.chmod(flz, stat.S_IRWXU | stat.S_IRWXG | stat.S_IRWXO)
    p = subprocess.Popen(['unzip', '-c', flz, 'crash.log' ], stdout=subprocess.PIPE)
    p.wait()
    
    for line in p.stdout:
        body += line+'<br>'
        
    body += '------------------------------------------------------------\n'

letter = letter + body +'<br>With best wishes, srv<html>'    

if len(src.items()) == 0:
    quit();
    
msg = MIMEText(letter, 'html')
me = 'crash@report.com' 
you = 'kkichaev@grsoft.ru' 
msg['Subject'] = 'crash report'
msg['From'] = me
msg['To'] = you

s = smtplib.SMTP('localhost')
s.sendmail(me, [you], msg.as_string())

print( msg.as_string())
s.quit()

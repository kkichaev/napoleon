# -*- coding: cp1251 -*-

import logging
import time
import datetime
import sys
from importlib import reload
reload(sys)
#sys.setdefaultencoding("cp1251")

def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    
    logging.debug("starting ")

    param = server.Params[0]
    logging.debug("param " + str(param))
    
    server.RegisterType("PinRegAnswer[registred:n,message:s]")
    answObj = server.New('PinRegAnswer')
    answ = answObj.New()

    answ.registred = 0
    answ.message = "Ошибка при регистрации"
    
    user = server.CurrentUser()
    imei = user.progID

    if len(imei) == 0:
        answ.message = 'Не могу определить IMEI телефона'


    haveError = False
    needWriteUpd = False
    
    upd = server.Get('UserPinData', '"userid"=' + "'" + user.id + "'")
    if upd != None and len(upd) > 0:
        updObj = upd[0]
        if updObj.pinHash != param.hash:
            if len(updObj.pinHash) > 0 and updObj.resetPin == 0:
                answ.message = 'ПИН-код не совпадает'
                haveError = True
        
        if not haveError:
            updObj.pinHash = param.hash
            updObj.resetPin = 0
            needWriteUpd = True
    else:
        upd = server.New('UserPinData')
        updObj = upd.New()
        updObj.pinHash = param.hash
        updObj.userid = user.id
        updObj.authByPin = 0
        updObj.resetPin = 0
        needWriteUpd = True
        
    if not haveError:
        agents = server.Get('Agents', '', 'id')
        agent = agents[user.id]    
    
        if len(agent.progid) > 0 :
            if imei != agent.progid:
                answ.message = 'IMEI телефона не совпадает'
                haveError = True
        else:
            agent.progid = imei
            server.Write(agents)
    
    if not haveError: 
        if needWriteUpd: 
            server.Write(upd)
            server.Put(upd)
            
        answ.registred = 1
        answ.message = 'Пользователь зарегистрирован в системе'
    
    server.Put(answObj)
    
    logging.debug("end " + str(answ))

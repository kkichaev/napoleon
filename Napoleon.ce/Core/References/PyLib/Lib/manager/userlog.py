# -*- coding: cp1251 -*-

import datetime
from datetime import datetime

def putSrv(server):
    server.RegisterType("TextLog[date:dt,text:s,userid:s]")
    outObj = server.New("TextLog")
    userid = server.Params[0].userid;
    date = server.Params[0].date
    
    where = '"userid"=\''+userid+'\' and ' + '"date" >= ToDate("{0}") and "date" <= ToDate("{1}")'.format(
              date.strftime("%d/%m/%Y 0:0:0"), date.strftime("%d/%m/%Y 23:59:59"))
    
    ulog = server.Get("UserLog", where)
    
    for log in ulog:
        if log.action == 0:
            continue;
        
        u = outObj.New()
        u.date = log.date
        u.userid=userid
            
        if log.action == 1:
           u.text = "GPS - Включен"
        elif log.action == 2:
           u.text = "GPS - Выключен"
        elif log.action == 3:   
           u.text = "Время изменено ({0})".format(log.comments)
        elif log.action == 4:
           u.text = "КПК - Включен"
        elif log.action == 5:
           u.text = "КПК - Выключен"
        elif log.action == 6:
           u.text = "Сбой программы"
        elif log.action == 7:
           u.text = "Наполеон - Запуск"
        elif log.action == 8:
           u.text = "Наполеон - Выход"
        elif log.action == 9:
           u.text = 'КПК статус: {0}'.format(log.comments)    
        elif log.action == 10:
           u.text = 'Фоновая синхронизация'
        elif log.action == 11:
           u.text = 'Очистка базы'    
        else:
         u.text = 'Событие неопределено: {0}'.format(log.action)       
           
    server.Put(outObj)      
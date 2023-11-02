# -*- coding: cp1251 -*-

import logging
import sys
import traceback
from datetime import datetime
import calendar

def add_months(sourcedate, months):
    month = sourcedate.month - 1 + months
    year = sourcedate.year + month // 12
    month = month % 12 + 1
    day = min(sourcedate.day, calendar.monthrange(year,month)[1])
    return datetime(year, month, day)


def handling(conn, result, params):
   endDate = params.end
   if endDate == None: endDate = datetime.now()
   startDate = params.start
   if startDate == None: startDate = add_months(endDate, -2)

   dateStr = " ДАТАВРЕМЯ({0}), ДАТАВРЕМЯ({1})".format(startDate.strftime('%Y,%m,%d'), endDate.strftime('%Y,%m,%d'))

   agents = getattr(conn.Catalogs, 'Пользователи')
   query = conn.NewObject('Запрос')

   query.Text = """
ВЫБРАТЬ
	МаршрутыТорговыхПредставителейМаршрут.Контрагент КАК Org,
	МаршрутыТорговыхПредставителейМаршрут.АдресДоставки КАК Address,
	МаршрутыТорговыхПредставителейМаршрут.Широта КАК Lat,
	МаршрутыТорговыхПредставителейМаршрут.Долгота КАК Lon,
	ЕСТЬNULL(Обороты.СтоимостьОборот, 0) КАК Income
ИЗ
	Справочник.МаршрутыТорговыхПредставителей.Маршрут КАК МаршрутыТорговыхПредставителейМаршрут
		ЛЕВОЕ СОЕДИНЕНИЕ (ВЫБРАТЬ
			ПродажиОбороты.Контрагент КАК Контрагент,
			СУММА(ПродажиОбороты.СтоимостьОборот) КАК СтоимостьОборот
		ИЗ
			РегистрНакопления.Продажи.Обороты(""" + dateStr + """, Регистратор, ) КАК ПродажиОбороты
		
		СГРУППИРОВАТЬ ПО
			ПродажиОбороты.Контрагент) КАК Обороты
		ПО МаршрутыТорговыхПредставителейМаршрут.Контрагент = Обороты.Контрагент
ГДЕ
	МаршрутыТорговыхПредставителейМаршрут.Ссылка.ТорговыйПредставитель = &Агент
"""   
# ВЫБРАТЬ
# 	МаршрутыТорговыхПредставителейМаршрут.Контрагент КАК Org,
# 	МаршрутыТорговыхПредставителейМаршрут.АдресДоставки КАК Address,
# 	МаршрутыТорговыхПредставителейМаршрут.Широта КАК Lat,
# 	МаршрутыТорговыхПредставителейМаршрут.Долгота КАК Lon
# ИЗ
# 	Справочник.МаршрутыТорговыхПредставителей.Маршрут КАК МаршрутыТорговыхПредставителейМаршрут
# ГДЕ
# 	МаршрутыТорговыхПредставителейМаршрут.Ссылка.ТорговыйПредставитель = &Агент


   for id in params.ids.split(',') :
      uid = conn.NewObject('УникальныйИдентификатор', id);      
      agent = agents.GetRef(uid)
      query.SetParameter('Агент', agent)
#      query.SetParameter('StartDate', startDate)
#      query.SetParameter('EndDate', startDate)

      sel =  query.Execute().Choose()
      res = result.New()
      res.uid = id
      while sel.Next() :
         orgi = res.orgs.New()
         orgi.id = getattr(sel.Org, 'Код')
         orgi.name = getattr(sel.Org, 'Наименование')
         orgi.address = sel.Address
         orgi.lat = float(sel.Lat.replace(',','.'))
         orgi.lon = float(sel.Lon.replace(',','.'))
         orgi.income = sel.Income
         #print(orgi.name, orgi.income)
         # orgi.expense = sel.Expense


def connectingAndHandling(server, params, result):
    connectStr = server.Config('ConnectStr1C')
    conn = server.GetCachedCOM(connectStr)
    if conn == None:
       objStr = server.Config('ComObject1C')
       logging.debug('COM object:' + objStr)
       o = server.CreateObject(objStr) 
       conn = None
       try:
           conn = o.Connect(connectStr)
           logging.debug(' connected ')

       except:
           ertype, val, tb = sys.exc_info()
           strErr = traceback.format_exception(ertype, val, tb)
           res = result.New()
           res.error = 1
           res.errMsg = strErr
           logging.debug(str(strErr).replace('\\n', '\n'))
    else:
       logging.debug(" got cached ")

    if conn == None :
      strErr = 'Ошибка при подключении к 1с'
      res = result.New()
      res.error = 1
      res.errMsg = strErr
      logging.debug(strErr)
      return

    try:
       handling(conn, result, params)
       logging.debug(' handled ')

    except Exception as err:
      ertype, val, tb = sys.exc_info()
      strErr = traceback.format_exception(ertype, val, tb)
      res = result.New()
      res.error = 1
      res.errMsg = strErr
      logging.debug(str(strErr).replace('\\n', '\n'))
      return

    server.PutCOMToCache(connectStr, conn)   

def run(server):
   logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    

   params = server.Params[0]

   logging.info('start ' + str(params))
   result = server.New('AgentRouteQueryResult')
   connectingAndHandling(server, params, result)

   if len(result) > 0:
      server.Post(result)

   logging.info('end')

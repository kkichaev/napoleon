# -*- coding: cp1251 -*-

import sys
from importlib import reload
import logging
import locale
from manager import coordutils

reload(sys)

class AgentGps:
   def __init__(self, id):
      self.id = id
      self.items = dict()

   def add(self, gps):
      dt = gps.date.date()
      if not dt in self.items:
         self.items[dt] = list()
      self.items[dt].append(gps)

   def distance(self, dateItems):
      result = 0

      lastpos = None

      for g in sorted(dateItems, key=lambda el: el.date):
         if lastpos == None:
            lastpos = g
            continue

         result += coordutils.distance(lastpos.latitude, lastpos.longitude, g.latitude, g.longitude)
         lastpos = g

      return result

   def getItems(self):
      ret = []
      for k,v in self.items.items():
         ret.append((k, self.distance(v)))
      return ret


class Data:
  def __init__(self):
      self.items = dict()

  def add(self, gps):
      if not gps.userid in self.items:
          self.items[gps.userid] = AgentGps(gps.userid)

      self.items[gps.userid].add(gps)

  def getItems(self):
      result = []

      for k,v in self.items.items():
         for di in v.getItems():
            result.append((k, di[0], di[1]))

      return result


def run(server):
   logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s',
                     datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)
   logging.debug("starting")

   locale.setlocale(locale.LC_ALL, 'american')
   params = server.Params[0]

   prmDic =dict()
   for p in params.param.split(','):
      kv = p.split(':')
      prmDic[kv[0]] = kv[1]

   logging.info("params " + str(prmDic))

   useGSM = 'and isGSM=0' if int(prmDic['gsm']) == 0 else ''

   where = '"date" >= ToDate("{0}") and "date" <= ToDate("{1}") {2}'.format(
      prmDic['start'], prmDic['finish'], useGSM)
   # print(where)
   gps = server.Get("GPSPos", where)

   data = Data()
   if gps != None:
      print(len(gps))
      for g in gps:
         data.add(g)

   server.RegisterType("ReportData[userid:s,date:d,length:n(2)]")
   res = server.New('ReportData')
   for d in data.getItems():
      dest = res.New()
      dest.userid = d[0]
      dest.date = d[1]
      dest.length = d[2]

   server.Post(res)

   logging.info("ended")

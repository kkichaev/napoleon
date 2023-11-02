from ast import Not
from datetime import datetime
from importlib import reload
import logging
import sys
from typing import Iterable, List

from quest_rep import QuestHelper

import xlsxwriter as xl
import tempfile
import os

from xlbuilder import XlBuilder

from xlsxwriter import worksheet
from xlsxwriter import workbook
from xlsxwriter.format import Format
from xml.dom import xmlbuilder


class RowData:
   def __init__(self, doc) -> None:
      self.doc = doc
      self.purchase : dict[str,float] = {}
      self.purchSum = 0
      self.sell = 0.0
      self.count = 0

   def addPurchase(self, doc):
      if doc.id in self.purchase: self.purchase[doc.id] += doc.weight
      else: self.purchase[doc.id] = doc.weight
      self.purchSum += doc.sum

   def getPurchase(self, id:str) -> float:
      return self.purchase[id] if id in self.purchase else 0

   def purchaseSum(self) -> float:
      return self.purchSum

   def lastDoc(self) -> datetime:
      ret = self.doc.created
      for ri in self.doc.items:
         if ri.created > ret: ret = ri.created
      return ret

   def passport(self) ->str:
      return "{0} № {1} выдан {2}, {3} ({4})".format(
         self.doc.passportSeria, self.doc.passportNumber, self.doc.passportIssue.strftime(r'%d/%m/%Y'),
         self.doc.issueOrg, self.doc.issueCode
      )

   def addSelling(self, doc):
      self.sell += doc.sum
      self.count += doc.count


class DocKey:
   def __init__(self, doc) -> None:
      self.id = doc.id
      self.fio = doc.fio
      self.phone = doc.phone

   def __repr__(self) -> str:
      return 'userid: {0}, fio {1}, phone {2}'.format(self.id, self.fio, self.phone)
   
   def __hash__(self) -> int:
      return hash(self.fio) | hash(self.phone) | hash(self.id)

   def __eq__(self, __o: object) -> bool:
      return isinstance(__o, DocKey) and self.id == __o.id and self.phone == __o.phone and self.fio == __o.fio


class ReportData:
   def __init__(self, purchase:list[object]) -> None:
      self.data : dict[DocKey,RowData] = {}
      self.purchase = purchase

   def addScriptDoc(self, script:object) -> RowData:
      key = DocKey(script)
      if not key in self.data:
         self.data[key] = RowData(script)

      return self.data[key]


def loadData(params, server) -> ReportData:
   class ScDocKey:
      def __init__(self, uid, created) -> None:
         self.created = created
         self.userid = uid

      def __repr__(self) -> str:
         return 'userid: {0}, created {1}'.format(self.userid, self.created.strftime(r"%d/%m/%Y %H:%M:%S"))
      
      def __hash__(self) -> int:
         return hash(self.userid) | hash(self.created)

      def __eq__(self, __o: object) -> bool:
         return isinstance(__o, ScDocKey) and self.created == __o.created and self.userid == __o.userid

   def loadPurchaseTemplate(server) ->list[object] :
      stmt = 'select p.id, p.pos, prc.name from PurchaseTemplate p, Price prc where p.id = prc.id'
      docs = server.Query(stmt, 'DD[id:s,pos:n,name:s]')
      
      ret = []
      for d in docs: 
         ret.append(d)
      
      return ret

   def loadScripts(uids:str, start:str, finish:str, server) -> tuple[ReportData, dict[DocKey, RowData]] :
      purchase = loadPurchaseTemplate(server)
      ret  = ReportData(purchase)

      dataDic : dict[DocKey, RowData] = {}

      stmt = '''
   select s.*, si.date as d_created, a.name as agent, o.name as org, o.orgType as orgType,o.orgFormat as orgFormat, d.name as division, 
      o.longitude, o.latitude
   from ScriptDoc s, ScriptDoc$items si, Agents a, Org o, Division d, Division$agents da
   where s.userid = si.ScriptDoc$userid and s.created = si.ScriptDoc$created and a.id = s.userid 
      and o.id = s.id and da.id = s.userid and d.id = da.Division$id 
      and s.userid in({0}) and s.created >= ToDate("{1}") and s.created <= ToDate("{2}") 
   order by userid, created   
      '''.format(uids, start, finish)
      docs = server.Query(stmt, 'Docs[latitude:n(5),longitude:n(5),created:dt,id:s,userid:s,fio:s,issueCode:s,issueOrg:s,passportIssue:d,payType:s,phone:s,clientType:s,passportNumber:s,passportSeria:s,agent:s,org:s,orgType:s,orgFormat:s,division:s,address:s,items(userid,created)[created@d_created:dt]]')

      for d in docs:

         rd = ret.addScriptDoc(d)

         for di in d.items:
            key = ScDocKey(d.userid, di.created)
            dataDic[key] = rd

      return (ret, dataDic)

   def loadPurchase(uids:str, start:str, finish:str, server, dataDic:dict[DocKey, RowData]) -> None :
      stmt = '''
         select id, weight, cost as sum, s.PurchaseDoc$created as created, s.PurchaseDoc$userid as userid from PurchaseDoc$items s
         where s.PurchaseDoc$userid in({0}) and s.PurchaseDoc$created >= ToDate("{1}") and s.PurchaseDoc$created <= ToDate("{2}") 
      '''.format(uids, start, finish)

      docs = server.Query(stmt, 'Di[id:s,weight:n(3),sum:n(2),userid:s,created:dt]')

      for d in docs:
         key = ScDocKey(d.userid, d.created)
         if key in dataDic:
            dataDic[key].addPurchase(d)

   def loadSelling(uids:str, start:str, finish:str, server, dataDic:dict[DocKey, RowData]) -> None:
      stmt ='''
      select s.SellingDoc$created as created, s.SellingDoc$userid as userid, sum(s.qty * s.cost) as sum, sum(s.qty) as count
         from SellingDoc$items s
         where s.SellingDoc$userid in({0}) and s.SellingDoc$created >= ToDate("{1}") and s.SellingDoc$created <= ToDate("{2}") 
         group by s.SellingDoc$userid, s.SellingDoc$created
      '''.format(uids, start, finish)

      docs = server.Query(stmt, 'Ds[sum:n(2),count:n(3),userid:s,created:dt]')

      for d in docs:
         key = ScDocKey(d.userid, d.created)
         if key in dataDic:
            dataDic[key].addSelling(d)


   uids = []
   uidFIlter = ''
   for uid in params.userids:
      uids.append(uid.id)
      uidFIlter += "'" + uid.id + "',"

   uidFIlter = uidFIlter[:-1]
   start = params.start.strftime(r"%d/%m/%Y")
   finish = params.finish.strftime(r"%d/%m/%Y 23:59:59")

   (ret, dataDic) = loadScripts(uidFIlter, start, finish, server)

   loadPurchase(uidFIlter, start, finish, server, dataDic)
   loadSelling(uidFIlter, start, finish, server, dataDic)

   return ret

def printOut(data:ReportData, name:str, href, server) -> None:

   xl = XlBuilder(name)

   crow = 1

   head = ['Регион', 'Выездной менеджер','Номер точки в системе', 'Название точки', 'Формат точки', 'Тип точки', 
      'Адрес', 'Координаты', 'ФИО клиента', 'Паспортные данные клиента', 'Телефон', 'Должность']

   purchase = sorted(data.purchase, key=lambda x:x.pos)
   rows = sorted(data.data.values(), key=lambda x : (x.doc.id, x.doc.agent))
   for i in purchase:
      head.append('Закуплено ' + i.name)

   head.extend(['Стоимость керамики и металла, руб.', 'Продано сопутствующих товаров, шт.',
      'Стоимость сопутствующих товаров, руб.'])

   xl.sheet.set_column(0, len(head), 14)

   xl.printHead(crow, head)
   crow += 1

   for ri in rows:
      doc = ri.doc

      location = '=HYPERLINK("https://maps.yandex.ru/?ll={0},{1}&z=18&pt={0},{1},comma", "{2}")'.format(
         doc.longitude, doc.latitude, str(doc.longitude) + ', ' + str(doc.latitude))

      lastDoc = ri.lastDoc()
      delta = lastDoc - doc.created
      values = [doc.division, doc.agent, doc.id, doc.org, doc.orgFormat, doc.orgType, doc.address, location,
         doc.fio, ri.passport(), doc.phone, doc.clientType]

      for pi in purchase:
         values.append(ri.getPurchase(pi.id))

      values.extend([ri.purchaseSum(), ri.count, ri.sell])

      xl.printValues(crow, values)
      crow += 1

   xl.toObject(server)

def run(server):
  logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s',
                      datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)
  logging.info('start')

  params = server.Params[0]
  logging.info("params " + str(params))

  data = loadData(params, server)
  printOut(data, 'visit_script.xlsx', params.hrefBase, server)

  logging.info('end')

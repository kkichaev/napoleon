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
      self.answers : list[object] = []

   def addPurchase(self, doc):
      self.purchase[doc.id] = doc.weight
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
      self.sell = doc.sum
      self.count = doc.count

   def addAnswerDoc(self, doc):
      for ai in doc.items:
         self.answers.append(ai)

   def getAnswers(self, id:str) -> list[object] :
      ret : list[object] = []

      for ai in self.answers:
         if ai.iditem == id:
            ret.append(ai)

      return ret

class ReportData:
   def __init__(self, data:list[RowData], quest:object, purchase:list[object], pictures:dict[str,object]) -> None:
      self.data = data
      self.purchase = purchase
      
      self.quest : list[object] = []
      if quest != None and len(quest): self.quest = quest[0].items

      self.pictures = pictures


def loadData(params, server) -> ReportData:
   class DocKey:
      def __init__(self, uid, created) -> None:
         self.created = created
         self.userid = uid

      def __repr__(self) -> str:
         return 'userid: {0}, created {1}'.format(self.userid, self.created.strftime(r"%d/%m/%Y %H:%M:%S"))
      
      def __hash__(self) -> int:
         return hash(self.userid) | hash(self.created)

      def __eq__(self, __o: object) -> bool:
         return isinstance(__o, DocKey) and self.created == __o.created and self.userid == __o.userid


   def loadScripts(uids:str, start:str, finish:str, server) -> tuple[list[RowData], dict[DocKey, RowData]] :
      ret : list[RowData] = []
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
      docs = server.Query(stmt, 
         'Docs[latitude:n(5),longitude:n(5),created:dt,finish:dt,id:s,userid:s,fio:s,issueCode:s,issueOrg:s,passportIssue:d,payType:s,phone:s,clientType:s,passportNumber:s,passportSeria:s,agent:s,org:s,orgType:s,orgFormat:s,division:s,address:s,items(userid,created)[created@d_created:dt]]')

      for d in docs:
         # print(d.org)
         data = RowData(d)
         ret.append(data)

         for di in d.items:
            key = DocKey(d.userid, di.created)
            dataDic[key] = data

      return (ret, dataDic)

   def loadPurchaseTemplate(server) ->list[object] :
      stmt = 'select p.id, p.pos, prc.name from PurchaseTemplate p, Price prc where p.id = prc.id'
      docs = server.Query(stmt, 'DD[id:s,pos:n,name:s]')
      
      ret = []
      for d in docs: 
         ret.append(d)
      
      return ret

   def loadPurchase(uids:str, start:str, finish:str, server, dataDic:dict[DocKey, RowData]) -> None :
      stmt = '''
   select id, weight, cost as sum, s.PurchaseDoc$created as created, s.PurchaseDoc$userid as userid from PurchaseDoc$items s
   where s.PurchaseDoc$userid in({0}) and s.PurchaseDoc$created >= ToDate("{1}") and s.PurchaseDoc$created <= ToDate("{2}") 
      '''.format(uids, start, finish)

      docs = server.Query(stmt, 'Di[id:s,weight:n(3),sum:n(2),userid:s,created:dt]')

      for d in docs:
         key = DocKey(d.userid, d.created)
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
         key = DocKey(d.userid, d.created)
         if key in dataDic:
            dataDic[key].addSelling(d)

   def loadAnswerDoc(uids:str, start:str, finish:str, server, dataDic:dict[DocKey, RowData]) -> None :
      where = 'userid in({0}) and created >= ToDate("{1}") and created <= ToDate("{2}")'.format(uids, start, finish)

      docs = server.Get('Answer', where)

      for d in docs:
         key = DocKey(d.userid, d.created)
         if key in dataDic:
            dataDic[key].addAnswerDoc(d)


   uids = []
   uidFIlter = ''
   for uid in params.userids:
      uids.append(uid.id)
      uidFIlter += "'" + uid.id + "',"

   uidFIlter = uidFIlter[:-1]
   start = params.start.strftime(r"%d/%m/%Y")
   finish = params.finish.strftime(r"%d/%m/%Y 23:59:59")

   (ret, dataDic) = loadScripts(uidFIlter, start, finish, server)

   activeQuest = server.Get('Question', "idquest = (select sdi.condParam  from ScriptDef sd, ScriptDef$items sdi where sd.id = sdi.ScriptDef$id and sd.active = 1 and sdi.curType = 'Answer')")
   purchase = loadPurchaseTemplate(server)

   loadPurchase(uidFIlter, start, finish, server, dataDic)
   loadSelling(uidFIlter, start, finish, server, dataDic)
   loadAnswerDoc(uidFIlter, start, finish, server, dataDic)

   where = '"created" >= ToDate("{0}") and "created" <= ToDate("{1}")'.format(start, finish)
   pictures = server.Get("PicStoreSrc", where, "id")

   return ReportData(ret, activeQuest, purchase, pictures)

def printOut(data:ReportData, name:str, href, server) -> None:
   def countColumns(item:object, data:list[RowData]) -> int:
      if item.type == QuestHelper.LIST_TYPE or item.type == QuestHelper.NUMBER_LIST_TYPE:
         return len(item.values)
      
      if item.type == QuestHelper.PHOTO_TYPE:
         count = 1
         for r in data:
            phc = len(r.getAnswers(item.iditem))
            if count < phc: count = phc
         return count

      return 1

   def questItemTitle(item:object, index:int) ->str:
      if item.type == QuestHelper.PHOTO_TYPE : return str(index)
      if (item.type == QuestHelper.LIST_TYPE or item.type == QuestHelper.NUMBER_LIST_TYPE) and index <= len(item.values):
         return item.values[index - 1].value

      return ""

   def appendQuestColumns(head:list[str], data:list[RowData], quest:list[object]) -> dict[str,int]:
      ret : dict[str, int] = {}
      for qi in quest:
         count = countColumns(qi, data)
         ret[qi.iditem] = count

         for i in range(count):
            title = qi.text
            if count > 1: title += ' ' + questItemTitle(qi, i+1)
            head.append(title)

      return ret
   
   def getQuestData(data:RowData, quest:list[object], columns:dict[str,int], pics:dict[str,object]) -> list[object]:
      ret = []

      for qi in quest:
         rows = data.getAnswers(qi.iditem)

         if qi.type == QuestHelper.PHOTO_TYPE:
            tc = columns[qi.iditem]
            for ri in rows:
               val = '=HYPERLINK("{0}{1}", "Фото")'.format(href, pics[ri.answer].name) if ri.answer in pics else "Фото не найдено!"
               ret.append(val)
               tc -= 1
            while tc > 0: 
               ret.append('')
               tc -= 1
            
         elif qi.type == QuestHelper.LIST_TYPE or qi.type == QuestHelper.NUMBER_LIST_TYPE:
            for vi in qi.values:
               val = ''
               for ri in rows:
                  if ri.answer == vi.value:
                     val = 'X'
                     break
               ret.append(val)
         else:
            if len(rows):
               ret.append(rows[0].answer)
            else:
                ret.append('')

      return ret

   xl = XlBuilder(name)

   crow = 1

   head = ['Регион', 'Выездной менеджер', 'Дата визита', 'Время начала визита', 'Время окончания визита', 
      'Длительность визита', 'Номер точки в системе', 'Название точки','Формат точки', 'Тип точки', 
      'Адрес', 'Координаты', 'ФИО клиента', 'Паспортные данные клиента', 'Телефон', 'Должность']

   quest = sorted(data.quest, key=lambda x:x.number)
   purchase = sorted(data.purchase, key=lambda x:x.pos)
   rows = sorted(data.data, key=lambda x : (x.doc.division, x.doc.agent, x.doc.created))
   for i in purchase:
      head.append('Закуплено ' + i.name)

   head.extend(['Способ оплаты', 'Стоимость керамики и металла, руб.', 'Продано сопутствующих товаров, шт.',
      'Стоимость сопутствующих товаров, руб.'])

   qcolumns = appendQuestColumns(head, rows, quest)

   totColumn = len(head)
   xl.sheet.set_column(0, totColumn, 14)

   xl.printHead(crow, head)
   crow += 1

   for ri in rows:
      doc = ri.doc

      location = '=HYPERLINK("https://maps.yandex.ru/?ll={0},{1}&z=18&pt={0},{1},comma", "{2}")'.format(
         doc.longitude, doc.latitude, str(doc.longitude) + ', ' + str(doc.latitude))

      lastDoc = ri.lastDoc()
      delta = doc.finish - doc.created
      values = [doc.division, doc.agent, doc.created.strftime(r'%d/%m/%Y'), doc.created.strftime(r'%H:%M:%S'), 
         doc.finish.strftime(r'%H:%M:%S'), str(delta), doc.id, doc.org, doc.orgFormat, doc.orgType, doc.address, location,
         doc.fio, ri.passport(), doc.phone, doc.clientType]

      for pi in purchase:
         values.append(ri.getPurchase(pi.id))

      values.extend([doc.payType, ri.purchaseSum(), ri.count, ri.sell])
      values.extend(getQuestData(ri, quest, qcolumns, data.pictures))
      
      while len(values) < totColumn: values.append('')

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

from ast import Not
from datetime import datetime, timedelta
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
   def __init__(self) -> None:
      self.docs : list[object] = []

   def addDoc(self, doc) -> None:
      self.docs.append(doc)

   def firstDoc(self) -> datetime:
      ret = self.docs[0].created
      for ri in self.docs:
         if ri.created < ret: ret = ri.created
      return ret

   def docLastTime(self, doc) -> datetime:
      ret = doc.finish
      for ri in doc.items:
         if ri.created > ret: ret = ri.created
      return ret


   def lastDoc(self) -> datetime:
      ret = self.docs[0].finish
      for di in self.docs:
         lastDoc = self.docLastTime(di)
         if lastDoc > ret: ret = lastDoc

      return ret

   def avgVisit(self) -> timedelta:
      ret = timedelta()

      for di in self.docs:
         lastDoc = self.docLastTime(di)

         ret += lastDoc - di.created

      ret /=  len(self.docs)
      return ret - timedelta(microseconds=ret.microseconds)

   def avgInterval(self) -> timedelta:
      if len(self.docs) == 1: return timedelta()

      ret = timedelta()

      cur = self.docs[0].created

      for di in self.docs:
         ret += di.created - cur
         cur = self.docLastTime(di)

      ret /=  len(self.docs) - 1
      return ret - timedelta(microseconds=ret.microseconds)



class DocKey:
   def __init__(self, doc) -> None:
      self.division : str = doc.division
      self.userid: str = doc.userid

   def __repr__(self) -> str:
      return 'userid: {0}, division {1}'.format(self.userid, self.division)
   
   def __hash__(self) -> int:
      return hash(self.userid) | hash(self.division)

   def __eq__(self, __o: object) -> bool:
      return isinstance(__o, DocKey) and self.division == __o.division and self.userid == __o.userid

class ReportData:
   def __init__(self) -> None:
      self.data : dict[DocKey, RowData] = {}

   def addDoc(self, doc) -> None:
      key = DocKey(doc)
      if not key in self.data: self.data[key] = RowData()
      self.data[key].addDoc(doc)


def loadData(params, server) -> ReportData:

   uids = []
   uidFIlter = ''
   for uid in params.userids:
      uids.append(uid.id)
      uidFIlter += "'" + uid.id + "',"

   uidFIlter = uidFIlter[:-1]
   start = params.start.strftime(r"%d/%m/%Y")
   finish = params.finish.strftime(r"%d/%m/%Y 23:59:59")

   stmt = '''
select s.*, si.date as d_created, a.name as agent, d.name as division
from ScriptDoc s, ScriptDoc$items si, Agents a, Division d, Division$agents da
where s.userid = si.ScriptDoc$userid and s.created = si.ScriptDoc$created and a.id = s.userid 
   and da.id = s.userid and d.id = da.Division$id 
   and s.userid in({0}) and s.created >= ToDate("{1}") and s.created <= ToDate("{2}") 
order by userid, created   
   '''.format(uidFIlter, start, finish)

   # print(stmt)
   docs = server.Query(stmt, 'Docs[created:dt,finish:dt,userid:s,agent:s,division:s,items(userid,created)[created@d_created:dt]]')

   ret = ReportData()

   for d in docs:
      ret.addDoc(d)

   return ret

def printOut(data:ReportData, name:str, href, server) -> None:
   xl = XlBuilder(name)

   crow = 1

   head = ['Регион', 'Выездной менеджер', 'Дата визита', 'Количество визитов', 'Время начала первого визита', 
      'Время окончания последнего визита', 'Средняя длительность визита', 'Среднее время между визитами']

   rows = sorted(data.data.values(), key=lambda x : (x.docs[0].division, x.docs[0].agent))

   xl.sheet.set_column(0, len(head), 14)

   xl.printHead(crow, head)
   crow += 1

   for ri in rows:
      doc = ri.docs[0]

      firstDoc = ri.firstDoc()
      lastDoc = ri.lastDoc()
      avgVisit = ri.avgVisit()
      avgInt = ri.avgInterval()

      values = [doc.division, doc.agent, doc.created.strftime(r'%d/%m/%Y'), len(ri.docs),
         firstDoc.strftime(r'%H:%M:%S'), lastDoc.strftime(r'%H:%M:%S'), str(avgVisit), str(avgInt)
         ]

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

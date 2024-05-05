# -*- coding: cp1251 -*-
from typing import Sequence, TypedDict
import datetime
import sys

import logging
import locale
from datetime import timedelta

from xlbuilder import XlBuilder
from xlsxwriter.utility import xl_rowcol_to_cell

class DocData:
    def __init__(self, doc, start : datetime, end : datetime, move : datetime.timedelta) -> None:
        self.doc = doc
        self.start = start
        self.end = end
        self.move = move

class Agents(TypedDict):
    agent : str
    docs : Sequence[DocData]

def loadData(server, params) -> Agents:
    
    def unpack(list):
        res = ''
        for i in list:
            res += "'{}',".format(i.id)
        return res[:-1]

    users = {}    

    stmt = '''
    select d.*, di.date_i as date_i from 
        (select distinct userid, created, id from 
            ScriptDoc ) d,
        (select di.ScriptDoc$userid as userid, di.ScriptDoc$created as created, di.state as state, 
            ifnull(vi.date, di.date) date_i from ScriptDoc$items di left join Visit$items vi 
                on di.date = vi.Visit$date and di.ScriptDoc$userid = vi.Visit$userid) di
        where d.userid = di.userid and d.created = di.created and di.state = 1
            and d.created >= ToDate("{0}") and d.created < ToDate("{1}") and d."userid" in ({2})
        order by d.userid, d.created, date_i
    '''.format(
      params.start.strftime('%d.%m.%Y'), 
      (params.finish + datetime.timedelta(days=1)).strftime('%d.%m.%Y'),
      unpack(params.userids))
    
    docs = server.Query(stmt, "Docs[id:s,name:s,userid:s,created:dt,city:s,address2:s,docs(userid,created)[date@date_i:dt]]")

    data = Agents()
    curDay = None
    prevDoc = None

    for d in docs:
      if not d.userid in users:  
        server.ChangeUser("'" + d.userid + "'")
        users[d.userid] = server.CurrentUser().name
        server.RestoreUser()
        orgs = server.Get("Org", "", "id")
        porg = server.Get("PotenzialOrg", "", "id")
        orgs.update(porg)
    
      agent = users[d.userid]

      if d.id in orgs:
        d.name = orgs[d.id].name
        d.address2 = orgs[d.id].address
      
      key = agent
      if not key in data:
          data[key] = list[DocData]()

      moveTime = None
      if curDay == None or curDay != d.created.date(): 
          curDay = d.created.date()
      else:
          moveTime = d.created - prevDoc if d.created > prevDoc else timedelta(seconds=0)


      docList = data[key]
      
      startTime = None
      endTime = None
      
      for di in d.docs:
          if startTime == None: 
              startTime = di.date
              endTime = di.date
          if startTime > di.date: startTime = di.date
          if endTime < di.date: endTime = di.date

      docList.append(DocData(d, startTime, endTime, moveTime))
      prevDoc = endTime
    

    return data
class XlBuilderEx(XlBuilder):
    
    def __init__(self, name) -> None:
        super().__init__(name)
        self.dateTimeFmt = self.wb.add_format({'num_format': 'hh:mm','align': 'left'})
        self.dateTimeFmt.set_border()

    def printCellValue(self, crow, ccel, value, format):
        if ccel > 2:
            format = self.dateTimeFmt

        super().printCellValue(crow, ccel, value, format)

def printOut(params, data: Agents, name, server):
    def timedeltaToStr(td:datetime.timedelta):
        if td == None:
            return ''
        
        res = '' if td == None else td.total_seconds() / 60 / 60 / 24
        return res

    def writeFormulaCell(sheet, start_row, end_row, format):
      for col in range(5,8) :
         sheet.write_formula(end_row, col, '{=SUM(%s:%s)}' % (xl_rowcol_to_cell(start_row, col),
               xl_rowcol_to_cell(end_row-1, col)), format)

      #   sheet.write_formula(end_row, 6, '{=SUM(%s:%s)}' % (xl_rowcol_to_cell(start_row, 6),
      #       xl_rowcol_to_cell(end_row-1, 6)), format)    

      #   sheet.write_formula(end_row, 6, '{=SUM(%s:%s)}' % (xl_rowcol_to_cell(start_row, 6),
      #       xl_rowcol_to_cell(end_row-1, 6)), format)    

    xl = XlBuilderEx('WorkTime.xlsx')
    
    sheet = xl.sheet
    sheet.set_column('A:A', 11)
    sheet.set_column('B:B', 20)
    sheet.set_column('C:C', 30)
    sheet.set_column('D:F', 12)

    crow = 0
    sheet.write(crow, 0, 'Время работы в точке', xl.bold)
    crow += 1
    
    cdate = None

    for k, v in data.items():
        sheet.write(crow, 0, k, xl.bold)
        crow += 1

        started = False
        moveTotal = datetime.timedelta()
        workTotal = datetime.timedelta()
        sr = crow

        for d in v:
            if cdate == None or cdate != d.doc.created.date():
                if started:
                    writeFormulaCell(sheet, sr, crow, xl.dateTimeFmt)
                    moveTotal = datetime.timedelta()
                    workTotal = datetime.timedelta()
                    crow += 1
                    sr = crow + 2

                cdate = d.doc.created.date()
                sheet.write(crow, 0, cdate.strftime('%d.%m.%Y'), xl.bold)
                crow+=1

                head = ['Дата', 'Название ТТ', "Адрес ТТ", "Время создаяния сценария", "Время начала визвта в ТТ", 
                "Время оконания визита в ТТ", "Итого время в ТТ", "Итого время передвижения"]
                xl.printHead(crow, head)
                crow += 1
                started = True
                
                
            work = d.end - d.start
            workTotal += work
            if d.move != None: moveTotal += d.move

            values = [cdate.strftime('%d.%m.%Y'), d.doc.name, d.doc.address2, d.doc.created.strftime('%d.%m %H:%M'),
                d.start.strftime('%d.%m %H:%M'), d.end.strftime('%d.%m %H:%M'), 
                timedeltaToStr(work), timedeltaToStr(d.move)]
            xl.printValues(crow, values)
            crow += 1

        if started:
            writeFormulaCell(sheet, sr, crow, xl.dateTimeFmt)
            crow += 1

    xl.toObject(server)

def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    
    logging.debug("starting")

    locale.setlocale(locale.LC_ALL, 'american')
    params = server.Params[0]
    logging.info("params " + str(params))
    report = loadData(server, params)

    printOut(params, report, "WorkTime", server)

    logging.info("ended")

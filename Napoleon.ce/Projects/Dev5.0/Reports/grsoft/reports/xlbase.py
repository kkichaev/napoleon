from datetime import datetime
import os
import random
import string
import tempfile
from typing import Any, Callable

import xlsxwriter
from xlsxwriter.utility import xl_rowcol_to_cell
from xlsxwriter.worksheet import Worksheet
from xlsxwriter.format import Format

class Formats:
    def __init__(self) -> None:
        self.formats :dict[str,Format] = {}

    def __getattr__(self, __name: str) -> Format:
        if __name in self.__dict__:
            return self.__dict__[__name]
        raise AttributeError

class XlBuilder :
    def __init__(self, name) -> None:
        self.name = name

        fname = ''.join(random.choice(string.ascii_lowercase) for i in range(16)) + name
        self.tFile = os.path.join(tempfile.gettempdir(), fname)  
        self.wb = xlsxwriter.Workbook(self.tFile)
        
        self.formats = Formats()

        self.formats.head = self.headerFormat()
        self.formats.cell = self.cellFormat()
        self.formats.sum = self.cellFormat(lambda x: x.set_num_format('0.00'))

        self.formats.header = self.wb.add_format({'bold' : True, 'font_size': 18})
        self.formats.normal = self.wb.add_format()
        
        self.formats.bold = self.cellFormat(lambda x: x.set_bold())
        self.formats.border = self.wb.add_format({'left': 1, 'right' : 1, 'top' : 1, 'bottom' : 1})

    def cellFormat(self, modifier:Callable[[Format],None] = None) -> Format:
        cf = self.wb.add_format()
        cf.set_border()
        if modifier: modifier(cf)
        return cf

    def headerFormat(self, modifier:Callable[[Format],None] = None) -> Format :
        cf = self.wb.add_format({'bold' : True})
        cf.set_text_wrap(True)
        cf.set_border()
        cf.set_bg_color('#F2f2f2')
        cf.set_align('center')
        cf.set_align('vcenter')

        if modifier: modifier(cf)
        return cf
    
    def printTitle(self, title:str, params, crow:int) -> int:
        self.sheet.write(crow, 0, title, self.formats.header)
        crow += 1
        if params:
            self.sheet.write(crow, 0, 'Период: {0} - {1}'.format(params.start.strftime("%d.%m.%Y"), params.finish.strftime("%d.%m.%Y")), self.formats.normal)
            crow += 1

        return crow


    def addWorkSheet(self, name:str):
        self.sheet = self.wb.add_worksheet(name)
        return self.sheet

    def printHead(self, crow, heads, cc = 0, onPrintCell:Callable[[Worksheet,int,int,Any,Any], None] = None):
        for v in heads:
            val = v
            format = self.formats.head
            if type(v) is tuple :
                val = v[0]
                format = v[1]

            self.printHeadValue(crow, cc, val, format, onPrintCell)
            cc += 1

    def printHeadValue(self, crow, ccel, value, format, onPrintCell:Callable[[Worksheet,int,int,Any,Any], None] = None):
        if onPrintCell: 
            onPrintCell(self.sheet, crow, ccel, value, format)
        else:
            self.sheet.write(crow, ccel, value, format)

    def printValues(self, crow, values, cc = 0):
        for v in values:
            if type(v) is tuple :
                self.printCellValue(crow, cc, v[0], v[1])    
            else:
                self.printCellValue(crow, cc, v, self.formats.cell)    
            cc += 1

    def printCellValue(self, crow, ccel, value, format):
        self.sheet.write(crow, ccel, value, format)

    def toObject(self, server):
        self.wb.close()
        file = open(self.tFile, 'rb')
        bytesOut = file.read(-1)
        file.close()

        server.RegisterType("Result[name:s,file:b]")
        outObj = server.New("Result")
        obj = outObj.New()
        obj.name = self.name
        obj.file = bytesOut

        server.Put(outObj)

    def setBoderOnRange(self, sheet, r1, c1, r2, c2):
        while r1 < r2:
            c = c1
            while c < c2:
                sheet.conditional_format( xl_rowcol_to_cell(r1, c) , { 'type' : 'no_errors' , 'format' : self.formats.border})
                c += 1
            r1 += 1

        
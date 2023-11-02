import os
import tempfile

import xlsxwriter
from xlsxwriter.utility import xl_rowcol_to_cell

class XlBuilder :
    def __init__(self, name) -> None:
        self.name = name
        self.tFile = os.path.join(tempfile.gettempdir(), name)  
        self.wb = xlsxwriter.Workbook(self.tFile)
        
        self.boldHead = self.wb.add_format({'bold' : True})
        self.boldHead.set_text_wrap(True)
        self.boldHead.set_border()
        self.boldHead.set_bg_color('#F2f2f2')
        self.boldHead.set_align('center')
        self.boldHead.set_align('vcenter')

        self.bold = self.wb.add_format({'bold' : True})

        self.cellFmt = self.wb.add_format()
        self.cellFmt.set_border()

        self.border = self.wb.add_format({'left': 1, 'right' : 1, 'top' : 1, 'bottom' : 1})

        self.sheet = self.wb.add_worksheet()

    def printHead(self, crow, heads, cc = 0):
        for v in heads:
            self.printHeadValue(crow, cc, v, self.boldHead)
            cc += 1

    def printHeadValue(self, crow, ccel, value, format):
        self.sheet.write(crow, ccel, value, format)

    def printValues(self, crow, values, cc = 0):
        for v in values:
            if isinstance(v, tuple) :
                self.printCellValue(crow, cc, v[0], v[1])                    
            else :
                self.printCellValue(crow, cc, v, self.cellFmt)    
            cc += 1

    def printCellValue(self, crow, ccel, value, format):
        self.sheet.write(crow, ccel, value, format)

    def setBoderOnRange(self, sheet, r1, c1, r2, c2):
        while r1 < r2:
            c = c1
            while c < c2:
                sheet.conditional_format( xl_rowcol_to_cell(r1, c) , { 'type' : 'no_errors' , 'format' : self.border})
                c += 1
            r1 += 1    

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

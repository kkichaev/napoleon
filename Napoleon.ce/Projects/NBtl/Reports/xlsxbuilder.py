import os
import tempfile

import xlsxwriter

class XlsxBuilder :
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

        self.sheet = self.wb.add_worksheet()

    def printHead(self, crow, heads, cc = 0):
        for v in heads:
            self.printHeadValue(crow, cc, v, self.boldHead)
            cc += 1

    def printHeadValue(self, crow, ccel, value, format):
        self.sheet.write(crow, ccel, value, format)

    def printValues(self, crow, values, cc = 0):
        for v in values:
            self.printCellValue(crow, cc, v, self.cellFmt)    
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

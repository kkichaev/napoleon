from grsoft.xl_base import XLBuilder

class XLBuilderCommon(XLBuilder):
  HEAD   = 'FFF2F2F2'
  HEIGHT_HEAD_ROW = 30
  
  def paintHeadCell(self, cell):
    XLBuilder.paintHeadCell(self, cell)
    self.setBackColor(cell,XLBuilderCommon.HEAD)
  
  def makeHead(self, sheet, row, titles, wrap_text = False, startColumn = 0):
    XLBuilder.makeHead(self, sheet, row, titles, wrap_text, startColumn)
    sheet.row_dimensions[sheet.cell(row=row, column=0).row].height = XLBuilderCommon.HEIGHT_HEAD_ROW
  
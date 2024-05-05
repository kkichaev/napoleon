from importlib import reload
import logging
import sys

from grsoft.reports import makeDocFilter
from grsoft.reports.xlbase import XlBuilder

class QuestXLBuilder(XlBuilder):
    def __init__(self, name) -> None:
        super().__init__(name)

        self.formats.headVert = self.headerFormat(lambda x: x.set_rotation(90))
        self.formats.headLeft = self.headerFormat(lambda x: x.set_align('left'))

        self.formats.cell.set_text_wrap()

class QBuilder:

    SET_TYPE = 2
    DATASET_TYPE = 5 # not impl
    PHOTO_TYPE = 7
    ORG_DATASET_TYPE = "Организация"
    PRICE_DATASET_TyPE = 'Прайс'
    NUMBER_LIST_TYPE = 8 # not checked

    class QItem:
        def __init__(self, src) -> None:
            self.name = src.text
            self.number = src.number
            self.id = src.iditem

        def count(self) -> int: return 1
        
        def printHead(self, xl:XlBuilder, row:int, col:int, index:int, horizontal:bool) -> int:
            if horizontal:
                xl.sheet.merge_range(row, col, row+1, col, self.name, xl.formats.head)
                return col + 1
            
            xl.sheet.write(row, col, index, xl.formats.head)
            col += 1
            xl.sheet.merge_range(row, col, row, col + 1, self.name, xl.formats.headLeft)
            return  row + 1
        
        def getValue(self, item) :
            return item.answer

        def printValues(self, doc, xl:XlBuilder, row:int, col:int, horizontal:bool) -> int:
            val = ''
            for ii in doc.items:
                if ii.iditem != self.id: continue

                val = self.getValue(ii)
                break

            xl.printValues(row, [val], col)
            return col + 1 if horizontal else row + 1

        
        @staticmethod
        def create(server, src, images:dict[str,int], hrefBase:str):
            if src.type == QBuilder.SET_TYPE:
                return QBuilder.QSetItem(src)
            elif src.type == QBuilder.NUMBER_LIST_TYPE:
                return QBuilder.QNumList(src)
            elif src.type == QBuilder.PHOTO_TYPE:
                return QBuilder.QPhoto(src, images, hrefBase)
            elif src.type == QBuilder.DATASET_TYPE:
                return  QBuilder.QDataSet(server, src)
            return QBuilder.QItem(src)

    class QSetItem(QItem):

        def __init__(self, src) -> None:
            super().__init__(src)
            self.values:list[str] = [x.value for x in src.values]

        def count(self) -> int: return len(self.values)
        
        def printHead(self, xl: XlBuilder, row: int, col: int, index:int, horizontal:bool) -> int:
            count = self.count()
            if horizontal:
                if count > 1:
                    xl.sheet.merge_range(row, col, row, col + self.count() - 1, self.name, xl.formats.head)
                else:
                    xl.sheet.write(row, col, self.name, xl.formats.head)
                row += 1
                for v in self.values:
                    xl.sheet.write(row, col, v, xl.formats.head)
                    col += 1
                return col
            
            if count > 1:
                xl.sheet.merge_range(row, col, row + self.count() - 1, col, index, xl.formats.headLeft)
                col += 1
                xl.sheet.merge_range(row, col, row + self.count() - 1, col, self.name, xl.formats.headLeft)
            else:
                xl.sheet.write(row, col, index, xl.formats.headLeft)
                col += 1
                xl.sheet.write(row, col, self.name, xl.formats.headLeft)

            col += 1
            for v in self.values:
                xl.sheet.write(row, col, v, xl.formats.headLeft)
                row += 1
            return row
        
        def printValues(self, doc, xl: XlBuilder, row: int, col: int, horizontal:bool) -> int:
            cells = self.count()
            values = ['' for x in range(cells)]
            for ii in doc.items:
                if ii.iditem != self.id: continue

                try:
                    idx = self.values.index(ii.answer)
                    values[idx] = 'X'
                except: pass

            if horizontal:
                xl.printValues(row, values, col)
                return col + cells
             
            for i in range(cells):
                xl.sheet.write(row, col, values[i], xl.formats.cell)
                row += 1
            return row
            
    
    class QNumList(QSetItem):
        def printValues(self, doc, xl: XlBuilder, row: int, col: int, horizontal:bool) -> int:
            cells = self.count()
            values = ['' for x in range(cells)]
            for ii in doc.items:
                if ii.iditem != self.id: continue

                try:
                    idx = self.values.index(ii.answer)
                    values[idx] = ii.remark
                except: pass

            if horizontal:
                xl.printValues(row, values, col)
                return col + cells
             
            for i in range(cells):
                xl.sheet.write(row, col, values[i], xl.formats.cell)
                row += 1
            return row
        
    class QDataSet(QItem):

        def __init__(self, server, src) -> None:
            super().__init__(src)

            self.server = server
            name = ''
            for v in src.values:
                if v.value == QBuilder.ORG_DATASET_TYPE:
                    name = 'Org'
                elif v.value == QBuilder.PRICE_DATASET_TyPE:
                    name = 'Price'
            self.name = name
            self.items = {}

        def getValue(self, item):
            val = item.answer
            if not val in self.items:
                obj = self.server.Get(self.name, "id='%s'" % val) or []
                if len(obj) > 0 :
                    val = obj[0].name
                self.items[item.answer] = val
            return val

    class QPhoto(QItem):

        def __init__(self, src, images:dict[str,int], hrefBase:str) -> None:
            super().__init__(src)
            
            self.hrefBase = hrefBase
            if src.iditem in images:
                self.photos = images[src.iditem]
            else:
                self.photos = 0

        def count(self) -> int: return self.photos

        def printHead(self, xl: XlBuilder, row: int, col: int, index:int, horizontal:bool) -> int:
            count = self.count()
            if horizontal:
                if count > 1:
                    xl.sheet.merge_range(row, col, row, col + count - 1, self.name, xl.formats.head)
                else:
                    xl.sheet.write(row, col, self.name, xl.formats.head)
                row += 1
                for i in range(self.photos):
                    xl.sheet.write(row, col, "Фото  {0}".format(i+1), xl.formats.head)
                    col += 1

                return col
            
            if count > 1:
                xl.sheet.merge_range(row, col, row + self.count() - 1, col, index, xl.formats.headLeft)
                col += 1
                xl.sheet.merge_range(row, col, row + self.count() - 1, col, self.name, xl.formats.headLeft)
            else:
                xl.sheet.write(row, col, index, xl.formats.headLeft)
                col += 1
                xl.sheet.write(row, col, self.name, xl.formats.headLeft)
            col += 1
            for i in range(self.photos):
                xl.sheet.write(row, col, "Фото  {0}".format(i+1), xl.formats.headLeft)
                row += 1
            return row
            
        
        def printValues(self, doc, xl: XlBuilder, row: int, col: int, horizontal:bool) -> int:
            cells = self.count()
            values = ['' for x in range(cells)]

            idx = 0
            for ii in doc.items:
                if ii.iditem != self.id: continue

                href =  ii.href if len(ii.href) > 0 else self.hrefBase + ii.photo
                try:
                    values[idx] = '=HYPERLINK("{0}", "{1}")'.format(href, 'фото')
                except:
                    print(len(values),idx,doc.docid,doc.userid,doc.created)
                idx += 1

            if horizontal:
                xl.printValues(row, values, col)
                return col + cells
            
            for i in range(cells):
                xl.sheet.write(row, col, values[i], xl.formats.cell)
                row += 1
            return row

    class QDrawer:

        def __init__(self, server, quest, images:dict[str,int], hrefBase:str) -> None:
            self.id = quest.idquest
            self.name = quest.name

            self.items:list[QBuilder.QItem] = []
            for qi in quest.items:
                self.items.append(QBuilder.QItem.create(server, qi, images, hrefBase))

            self.items = sorted(self.items, key=lambda x: x.number)

        def count(self) -> int:
            ret = 0
            for q in self.items: ret += q.count()
            return ret

        def printHorizontal(self, xl:XlBuilder, row:int, col:int) -> int:
            cells = self.count()

            ecol = cells +  col
            if cells == 1 :
                xl.sheet.merge_range(row, col, row+2, col, self.name, xl.formats.head)
            else :
                xl.sheet.merge_range(row, col, row, cells + col - 1, self.name, xl.formats.head)
                row += 1
                index = 1
                for q in self.items:
                    col = q.printHead(xl, row, col, index, True)
                    index += 1
            return ecol

        def printVertical(self, xl:XlBuilder, row:int, col:int) -> int:
            cells = self.count()
            if cells > 1:
                xl.sheet.merge_range(row, col, row+cells-1, col, self.name, xl.formats.headVert)
            else:
                xl.sheet.write(row, col, self.name, xl.formats.headVert)

            erow = cells + row
            col += 1
            index = 1
            for q in self.items:
                row = q.printHead(xl, row, col, index, False)
                index += 1
            
            return erow

        def printHeader(self, xl:XlBuilder, row:int, col:int, horizontal:bool) -> int:
            if horizontal: 
                return self.printHorizontal(xl, row, col)
            return self.printVertical(xl, row, col)

        def printValues(self, doc, xl:XlBuilder, row:int, col:int, horizontal:bool) -> int:
            cells = self.count()
            if horizontal:
                ecol = cells + col

                if doc.question == self.id:
                    for q in self.items:
                        col = q.printValues(doc, xl, row, col, horizontal)
                else:
                    xl.printValues(row, ['' for _ in range(0, cells)], col)
                return ecol
                        
            erow = cells + row

            if doc.question == self.id:
                for q in self.items:
                    row = q.printValues(doc, xl, row, col, horizontal)
            else:
                for _ in range(cells):
                    xl.sheet.write(row, col, '', xl.formats.cell)
                    row += 1

            return erow           


        
    def __init__(self, server, params, horizontal=True) -> None:
        def imageCount(docFilter:str) -> dict[str,int]:
            res:dict[str,int] = {}

            stmt = '''
            SELECT max(photos) photos, id from
                (SELECT count(*) photos, "Answer$userid", "Answer$created", "idItem" as id 
                FROM "Answer$items" ai, "Answer" a 
                    WHERE type = 7 and ai."Answer$userid" = a."userid" and ai."Answer$created" = a."created" {0}
                    GROUP BY "Answer$userid", "Answer$created", ai.id) 
            GROUP BY id
            '''.format(docFilter)

            docs = server.Query(stmt, 'Img[photos:n,id:s]') or []
            for d in docs:
                res[d.id] = int(d.photos)

            return res
            
        docFilter = makeDocFilter(params, "a")
        if len(docFilter): docFilter = " AND " + docFilter
        images = imageCount(docFilter)

        qf = [x.id for x in params.quests]
        qfilter = '"idquest" in ({0})'.format(",".join(qf))
        
        items: list[QBuilder.QDrawer] = []
        quests = server.Get('Question', qfilter) or []
        for q in quests:
            items.append(QBuilder.QDrawer(server, q, images, params.hrefBase))

        self.items = sorted(items, key=lambda x: x.name)
        self.horizontal = horizontal

    def printHeader(self, xl:XlBuilder, row:int, col:int) -> None:
        for q in self.items:
            if self.horizontal:
                col = q.printHeader(xl, row, col, self.horizontal)
            else:
                row = q.printHeader(xl, row, col, self.horizontal)


    def printValues(self, doc, xl:XlBuilder, row:int, col:int) -> None:
        for q in self.items:
            if self.horizontal:
                col = q.printValues(doc, xl, row, col, self.horizontal)
            else:
                row = q.printValues(doc, xl, row, col, self.horizontal)



def loadData(params, server):

    docFilter = makeDocFilter(params, "a")
    if len(docFilter): docFilter = " AND " + docFilter
    qf = [x.id for x in params.quests]
    docFilter  += ' AND a."question" in ({0})'.format(",".join(qf))

    stmt = '''
    SELECT docs.*, o."name" as orgname, a."name" agent , o."address" as address
        , o."latitude" olat, o."longitude" olon, ol."latitude" lat, ol."longitude" lon
    FROM
    (SELECT ai.*, a."id" as docid, a."question", a."userid", ps."name" as photo, ps."href" 
      FROM "Answer$items" ai left join "PicStore" ps on ai.type = 7 and ai."answer" = ps."id", "Answer" a
      WHERE ai."Answer$created" = a."created" and ai."Answer$userid" = a."userid" {0}
    ) docs
    LEFT JOIN "Org" o on o."id" = docs.docid
    LEFT JOIN "OrgLocation" ol on o."id" = ol."id"
    LEFT JOIN "Agents" a on a."id" = docs."userid"
    ORDER BY docs."Answer$created"
    '''.format(docFilter)

    itemfmt = "[answer:s,id:s,idItem:s,remark:s,type:n,photo:s,href:s]"
    docfmt = 'Docs[created@Answer$created:dt,docid:s,question:s,userid:s,orgname:s,agent:s,address:s,olat:n(5),olon:n(5),lat:n(5),lon:n(5),items(Answer$created,userid)%s]' % itemfmt
    docs = server.Query(stmt, docfmt) or []
    print('Docs',len(docs))

    return docs

def printOut(builder:QBuilder, docs, params, server):
    xl = QuestXLBuilder('quest.xlsx')
    sheet = xl.addWorkSheet('Отчет по анкетам')

    sheet.set_column('A:B', 35)
    sheet.set_column('D:D', 45)
    sheet.set_column('E:E', 10)
    sheet.set_column('F:F', 15)

    crow = xl.printTitle('Отчет по анкетам', params, 0)

    head = ["Наименование", "Код ТТ", "Координаты", "Адрес", "Дата", "Торговый представитель"]
    xl.printHead(crow, head, 
                 onPrintCell= lambda sheet,row,col,value,format:  
                    sheet.merge_range(row,col,row+2,col,value,format)
                 )
    ccol = 6
    builder.printHeader(xl, crow, ccol)
    crow += 3

    DATE_FMT = "%d.%m.%Y %H:%M"
    for d in docs:
        lat = d.lat if d.lat != 0 else d.olat
        lon = d.lon if d.lon != 0 else d.olon

        coord = ''
        if lat != 0 or lon != 0:
            href = 'https://www.openstreetmap.org/?mlat={0}&mlon={1}#map=17/{0}/{1}'.format(
                lat, lon
            )
            coord = '=HYPERLINK("{0}", "{1}")'.format(href, 'карта')

        data = [d.orgname, d.docid, coord, d.address, d.created.strftime(DATE_FMT), d.agent]
        xl.printValues(crow, data)

        builder.printValues(d, xl, crow, ccol)
        crow += 1

    xl.toObject(server)


def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)    
    logging.info('start report')

    params = server.Params[0]
    logging.info("params " + str(params))

    data = loadData(params, server)
    builder = QBuilder(server, params)

    printOut(builder, data, params, server)

    logging.info('end')

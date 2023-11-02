import logging
import sys
from importlib import reload

from quest_rep import loadData, QBuilder, QuestXLBuilder

def printOut(builder:QBuilder, docs, params, server):
    xl = QuestXLBuilder('quest.xlsx')
    sheet = xl.addWorkSheet('Отчет по анкетам')

    sheet.set_column('A:B', 2.22)
    sheet.set_column('C:D', 15)
    sheet.set_row(2, 120)
    # sheet.set_column('D:D', 45)
    # sheet.set_column('E:E', 10)
    # sheet.set_column('F:F', 15)

    crow = xl.printTitle('Отчет по анкетам', params, 0)

    startRow = crow
    xl.sheet.merge_range(crow, 0, crow, 3, 'Клиенты', xl.formats.head)
    crow += 1

    ccol = 0
    builder.printHeader(xl, crow, ccol)

    ccol = 4
    for d in docs:
        crow = startRow
        xl.sheet.write(crow, ccol, d.orgname, xl.formats.headVert)        
        crow += 1

        builder.printValues(d, xl, crow, ccol)

        ccol += 1

    xl.toObject(server)

def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)    
    logging.info('start report')

    params = server.Params[0]
    logging.info("params " + str(params))

    data = loadData(params, server)
    builder = QBuilder(server, params, horizontal=False)

    printOut(builder, data, params, server)

    logging.info('end')

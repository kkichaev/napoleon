import logging
import sys

from grsoft.reports import makeDocFilter
from grsoft.reports.xlbase import XlBuilder

class OrgList:

  class OrgData:
    def __init__(self, src) -> None:
      self.id = src.orgid
      self.name = src.org
      self.address = src.orgadr

    def __hash__(self) -> int:
      return hash(self.id)

    def __eq__(self, __value: object) -> bool:
      return isinstance(__value, OrgList.OrgData) and  self.id == __value.id

  def __init__(self, src) -> None:
    self.orgs : list[OrgList.OrgData] = []

    orgs:list[OrgList.OrgData] = []
    for f in src: 
      for p in f.items:
        for o in p.orgs:
          el = OrgList.OrgData(o)
          if not el in orgs: orgs.append(el)

    self.orgs = sorted(orgs, key=lambda x: x.name)

  def values(self, src:list) -> list[str|float]:
    res:list[str:float] = ['' for x in range(len(self.orgs))]

    for o in src:
      try:
        fnd = OrgList.OrgData(o)
        idx = self.orgs.index(fnd)
        res[idx] = o.qty
      except:
        print("not find", o)
        
    return res

def loadData(params, server):
  where = makeDocFilter(params, "o")

  stmt = '''
    SELECT sum(docs.qty) as qty, p.price, p."id", p.folder, o."name" as org, orgid, o."address" as orgadr
    FROM 
    (SELECT oi.*, o."id" as orgid from "Order$items" oi, "Order" o 
        WHERE oi."Order$created" = o."created" AND oi."Order$userid" = o."userid" {0}
    ) docs
    LEFT JOIN 
    (SELECT p."id", f."name" as folder, p."name" as price from "Price" p  LEFT JOIN "Folder" f on f."fid" = p."fid") p on p."id" = docs."id"
    LEFT JOIN "Org" o on o."id" = docs.orgid
    WHERE NOT p."id" IS NULL 
    GROUP BY o."name", p.folder, p.price, p."id"
    ORDER BY p.folder, p.price, o."name"
  '''.format (' AND ' + where if len(where) > 0 else '')

  docs = server.Query(stmt, 'Fld[folder:s,items(folder)[id:s,price:s,orgs(id)[qty:n(3),org:s,orgid:s,orgadr:s]]]')

  return docs or []

def printOut(docs, params, server) -> None:
  orgs = OrgList(docs)

  xl = XlBuilder('orders.xlsx')
  headVert = xl.headerFormat(lambda x: x.set_rotation(90))

  sheet = xl.addWorkSheet('Отчет')

  sheet.set_column('A:A', 12)
  sheet.set_column('B:B', 70)

  crow = xl.printTitle('Отчет по заявкам', params, 0)

  headers = ['Артикул', 'Номенклатура']
  for o in orgs.orgs:
    headers.append(('{0} ({1})'.format(o.name, o.address), headVert))

  headers.append(('Итого', headVert))

  xl.printHead(crow, headers)
  crow += 1

  sheet = xl.sheet
  for f in docs:
    sheet.merge_range(crow, 0, crow, len(headers) - 1, f.folder, xl.formats.bold)
    crow += 1

    for p in f.items:
      qty = 0
      for o in p.orgs: qty += o.qty

      values = [p.id, p.price]
      values.extend(orgs.values(p.orgs))
      values.append((qty, xl.formats.bold))
      
      xl.printValues(crow, values)
      crow += 1

  xl.toObject(server)
  

def run(server):
  logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)    
  logging.info('start report')

  params = server.Params[0]
  logging.info("params " + str(params))

  data = loadData(params, server)
  printOut(data, params, server)

  logging.info('end')
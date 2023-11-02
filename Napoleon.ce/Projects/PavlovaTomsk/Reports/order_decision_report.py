import logging
import sys
from datetime import datetime

from xlbuilder import XlBuilder

class Data:
  def __init__(self, src, orgs, price, agents) -> None:
    self.date : datetime = src.created
    self.agent:str = agents[src.userid].name if src.userid in agents else src.userid
    self.org:str = orgs[src.id].name if src.id in orgs else src.id
    self.item:str = price[src.id_i].name if src.id_i in price else src.id_i
    self.qty:float = src.qty
    self.discount:float = src.discount
    self.sum:float = src.qty * src.cost
    self.sumWODsc:float = src.qty * src.costWODsc
    self.manager:str = src.name if len(src.name) > 0 else src.login

def loadData(params, server) -> list[Data]: 
  orgs = server.Get('CommonOrgs', '', 'id')
  price = server.Get("ManagerPrice", "", "id")
  agents = server.Get('Agents', '', 'id')

  uids = ",".join(["'" + x.id + "'" for x in params.userids])
  stmt = '''
select o.created, o.qty, o.userid, o.id, o.id_i, o.cost, o.discount, o.costWODsc, dm.name, dm.login, d.dodate, d.remark
from
(select o.created, o.userid, oi.qty, o.id, oi.id id_i, cost, discount, costWODsc from Order$items oi, "Order" o
where actionGift = 0 and discount <> 0 and o.userid = oi.Order$userid and o.created = oi.Order$created
and created > ToDate('{0}') and created <= ToDate('{1} 23:59:58') and userid in ({2})
) o,
Decision d, DivisionManager dm
where o.userid = d.userid and o.created = d.created and d.manager = dm.login
'''.format(
  params.start.strftime("%d.%m.%Y")
  ,params.finish.strftime("%d.%m.%Y")
  ,uids
)
#   print(stmt)

  ret:list[Data] = []
  docs = server.Query(stmt, 'DocData[created:dt,userid:s,id:s,id_i:s,qty:n(3),cost:n(2),discount:n(2),costWODsc:n(2),name:s,login:s,dodate:dt,remark:s]')
  for di in docs:
    # print(di)
    dta = Data(di, orgs, price, agents)
    ret.append(dta)

  return ret

def printOut(data:list[Data], params, server):
  xl = XlBuilder('order_discounts.xlsx')

  dtf = xl.wb.add_format({'num_format': 'dd/mm/yy hh:mm'})
  dtf.set_border()

  crow = 0
  xl.sheet.write(crow, 0, 'Утверждение заказов')
  crow += 1
  xl.sheet.write(crow, 0, 'С {0} по {1}'.format(params.start.strftime("%d.%m.%Y"),params.finish.strftime("%d.%m.%Y")))
  crow += 1

  head = ['Дата', 'Агент', 'Клиент', 'Товар', 'Кол-во в заказе', '% скидки', 'Сумма без скидки', 'Сумма со скидкой', 'Пользователь РМР']
  xl.printHead(crow, head)
  crow += 1

  for di in sorted(data, key=lambda x: (x.date, x.agent)):
    values = [(di.date, dtf), di.agent, di.org, di.item, di.qty, di.discount, di.sumWODsc, di.sum, di.manager]
    xl.printValues(crow, values)
    crow += 1

  idx = 0
  for w in [15, 30, 30, 20, 9, 9, 9, 9, 20]:
    xl.sheet.set_column(idx, idx, w)
    idx += 1

  xl.toObject(server)

def run(server):
  logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)    
  logging.info('start report')

  params = server.Params[0]
  logging.info("params " + str(params))

  data = loadData(params, server)
  printOut(data, params, server)

  logging.info('end')
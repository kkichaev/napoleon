# -*- coding: cp1251 -*-

import sys
from importlib import reload
import logging
from datetime import timedelta, datetime
from manager.summary import loadAgents  # @UnresolvedImport
from manager.document import docTypes
import sku_report 

class UserID:
  def __init__(self, id):
    self.id = id
  
class Params:
  def __init__(self):
    self.userids = []
    q = (datetime.today().month + 2) // 3
    m = q * 3 - 2
    em = m + 3
    y = datetime.today().year
    
    if em > 12:
      em = 1
      y = y + 1
    
    self.start = datetime.today().replace(day=1).replace(month = int(m)).replace(hour = 0).replace(minute=0).replace(second=0).replace(microsecond=0)
    self.finish = self.start.replace(month=int(em)).replace(year = y)
    
  def __str__(self):
    return self.userids[0].id + " start: " + str(self.start) + " finish: " + str(self.finish)
    
def run(server):
    reload(sys)
    
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)    
    logging.info('start report')

    params = Params()
    params.userids.append(UserID(server.CurrentUser().id))
    logging.info("params " + str(params))
    
    server.RegisterType("PlanData[id:s,items[name:s,plan:n(2),fact:n(2)]]")
    output = server.New("PlanData")  
    
    plan = output.New()
    plan.id = '1'
    
    data = sku_report.loadData(sku_report.ReportData(), params, server)
    
    for i in data.items:
      item = data.items[i]
      
      for i2 in item.items:
        item2 = item.items[i2]
        pi = plan.items.New()
        pi.name = item.name
        pi.plan = item2.plan
        pi.fact = item2.fact()
    
    server.Put(output)
     
    logging.info('end')

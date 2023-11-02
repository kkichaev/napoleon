import logging
from datetime import timedelta, datetime
import sys

def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    
    logging.info('start')
    process(server)
    logging.info('end')

def addSchedule(dest, src, day, userid):
    dst = dest.New()
    dst.date = day
    dst.userid = userid

    for si in src.items:
        di = dst.items.New()
        di.id = si.id
        di.timeIndex = si.index

def process(server):
    param = server.Params[0]
    userid = param.userid
    start = datetime.strptime(param.start, "%Y%m%d").date()
    finish = datetime.strptime(param.finish, "%Y%m%d").date()

    routeTeplate = server.Get("RouteTemplate",'"userid"= \'{0}\''.format(userid))

    if len(routeTeplate) == 0:
        return

    cycle = routeTeplate[0].firstDay.date() 

    print('Cycle', cycle)
    if finish < cycle:
        logging.info("finish < firstDay")
        return
    
    if start < cycle:
        start = cycle

    server.Remove("Schedule",'"date" >= ToDate("{0}") and "date" <= ToDate("{1}") and "userid"=\'{2}\''.format(
        start.strftime("%d/%m/%Y 0:0:0")
        ,finish.strftime("%d/%m/%Y 0:0:0")
        ,userid))

    scheduleObjects = server.New("Schedule")

    curDay = (start - cycle).days

    cday = start
    while cday <= finish:
        curDay = curDay % 28        
        # week index
        wi = curDay // 7 + 1

        # day index
        di = curDay % 7 + 1
        if di == 7: di = 0

        # print(wi, di)
        for ri in routeTeplate:
            if ri.weekIndex == wi and ri.dayOfWeek == di:
                # print("sch add")
                addSchedule(scheduleObjects, ri, cday, userid)

        curDay += 1
        cday = cday + timedelta(days=1)

    server.Write(scheduleObjects)
    server.Post(scheduleObjects)
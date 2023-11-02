import logging
import sys
from datetime import datetime, time, timedelta

# day of week to unix time
def makeStartingMap() -> dict[int,datetime]:

    res :dict[int,datetime] = {}

    cdate = datetime.combine(datetime.now().date(), datetime.min.time())
    
    # convert from python to .net day of the week
    wday = (cdate.weekday() + 1) % 7

    while True:
        if wday in res: break
        res[wday] = cdate
        
        wday += 1
        wday %= 7
        
        cdate = cdate + timedelta(days=1)

    print(res)
    return res

def makeScheuleEntries(startMap:dict[int,datetime], dayTime:dict[int,list[int]]) -> list[dict[str,any]]:
    res :list[dict[str,any]] = []

    for day, times in dayTime.items():
        if not day in startMap:
            print('No day', day, startMap)
            continue

        cdate = startMap[day]
        for th in times:
            h = int(th // 60)
            min = int(th % 60)
            shday = datetime.combine(cdate.date(), time(hour=h, minute=min, second=10))

            # entry = {'starting':shday.timestamp(), 'cycle':True, 'day':1}
            entry = {'starting':shday, 'cycle':True, 'day':1}
            res.append(entry)

    return res


def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)    
    logging.info('starting')

    dayTime:dict[int,list[int]] = {}
    docs = server.Get('FirstDocTime', '') or []
    for di in docs:
        day = int(di.day)
        if not day in dayTime: dayTime[day] = []
        dtm = dayTime[day]

        if not di.time in dtm: dtm.append(di.time)

    startMap = makeStartingMap()

    entries = makeScheuleEntries(startMap, dayTime)
    
    schedule = {
        'id' : 'FirstDocCheck',
        'name' : 'FirstDoc checker',
        'description' : 'Check is first doc presents',
        'module' : 'check_firstdoc',
        'params' : '',
        'entries' : entries
    }

    print('Write FD scheduler', schedule)
    server.Schedule(schedule, True)

    logging.info('ending')
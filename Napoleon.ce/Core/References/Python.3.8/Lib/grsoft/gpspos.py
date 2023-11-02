from datetime import datetime
from datetime import timedelta
from manager import coordutils

def GPSRouteLength(server, userid, start, finish, startTime = None, endTime = None):
    q = '"date" >= ToDate("{0}") and "date" <= ToDate("{1}") and "userid"=\'{2}\' and "isGSM" = 0'.format(
        start.strftime("%d/%m/%Y 0:0:0"),
        finish.strftime("%d/%m/%Y 23:59:59"), userid)
    
    gpspos = server.Get("GPSPos",q)

    lastpos = None
    distance = 0

    td1 = timedelta(0, 0)
    td2 = timedelta(23, 59)
    
    print(len(gpspos))
    if startTime != None:
        td1 = startTime
    if endTime != None:
        td2 = endTime
        
    for pos in gpspos:
        date = pos.date.replace(hour=0, minute=0, second=0, microsecond=0)
        if pos.date < (date + td1) or pos.date > (date + td2):
            continue
          
        if lastpos == None:
            lastpos = pos
            continue
        distance += coordutils.distance(lastpos.latitude, lastpos.longitude, pos.latitude, pos.longitude)
        lastpos = pos

    return distance

    
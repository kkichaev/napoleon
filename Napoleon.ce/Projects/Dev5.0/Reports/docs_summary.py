from datetime import date, datetime, timedelta, time, timezone
import logging
import sys

from grsoft.reports import makeDocFilter
from grsoft.objects.documents import makeDocQuery

class Params:
    def __init__(self, src) -> None:
        self.start = datetime.strptime(src.start, "%Y%m%d")
        self.finish = datetime.strptime(src.finish, "%Y%m%d")
        self.userids = []

def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    
    logging.debug("starting")

    # locale.setlocale(locale.LC_ALL, 'american')
    paramSrv = server.Params[0]
    params = Params(paramSrv)
    logging.info("params " + str(params))

    filter = makeDocFilter(params, userField=None)
    stmt = makeDocQuery(filter)

    stmt = '''
    SELECT ud.agents, ud.cr_day, dd.docs, 133200000000000000 AS day FROM
        (SELECT count(distinct userid) as agents, cr_day FROM 
            ( {0} )  GROUP BY cr_day) ud,
        (SELECT count(*) as docs, cr_day FROM
            ( {0} )  GROUP BY cr_day) dd
        WHERE ud.cr_day = dd.cr_day
    ''' .format(stmt)

    # print(stmt)

    docs = server.Query(stmt, 'RepData[agents:n,docs:n,cr_day:n,day:d]')
    for d in docs:
        unixday = d.cr_day * 24 * 3600 - 11644473600
        d.day = datetime.fromtimestamp(unixday,  timezone.utc)
    server.Post(docs)

    logging.info("ended")

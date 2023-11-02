from datetime import date, datetime
import logging
import sys
import traceback
from sends_deviations import sendDeviations


class LastCheckTime:
    DATE_FORMAT = r"%Y%m%d%H%M%S"
    CFG_KEY = 'LastStartChecking'

    def __init__(self, server) -> None:
        self.checkDate = datetime.now().replace(hour=0,minute=0,second=0,microsecond=0)
        
        d = server.Get('ServerConfig', "[key]='{}' and userid=''".format(LastCheckTime.CFG_KEY)) or []
        for di in d:
            try:
                # print(di)
                stDate = datetime.strptime(di.value, LastCheckTime.DATE_FORMAT)
                if stDate.date() == date.today():
                    self.checkDate = stDate
            except:
                # traceback.print_exc()
                pass
            break


    def put(self, server, date:datetime):
        ds = server.New('ServerConfig')
        d = ds.New()

        d.key = LastCheckTime.CFG_KEY
        d.value = date.strftime(LastCheckTime.DATE_FORMAT)
        server.Write(ds)

    def above(self, date:datetime) -> bool :
        return self.checkDate < date

def havntDocs(server, orgs:dict[str,str], schTime:dict[str:datetime], start:datetime) -> list[str]:
    def makeUserStmt(userid:str, id:str, start:datetime, finish:datetime) -> str:
        df = r'%d/%m/%Y %H:%M:%S'
        docList = ['ScriptDoc', 'Order', 'Visit']

        stmt = ""   
        for doc in docList:
            table = '"{}"'.format(doc)
            sti = "select count(*) as ndocs, userid from {} where userid='{}' and id='{}' and created > ToDate('{}') and created < ToDate('{}')  group by userid" \
                .format(table, userid, id, start.strftime(df), finish.strftime(df))
            
            if len(stmt) > 0:
                stmt += " union all "            
            stmt += sti

        return stmt
    
    if len(orgs) == 0:
       return []
       
    stmt = ''
    for uid, id in orgs.items():
        finish = schTime[uid]
        sti = makeUserStmt(uid, id, start, finish)
        if len(stmt) > 0:
            stmt += " union all "            
        stmt += sti

    stmt = "select sum(ndocs) as ndocs, userid from ({}) d1 group by userid".format(stmt)

    ret = list(orgs.keys())
    for di in server.Query(stmt, 'HdocI[ndocs:n,userid:s]'):
        ret.remove(di.userids)

    return ret

def makeDontWorkAlert(server):
    def getFirstScheduledOrg(server, day:str, weekIndex:dict[str,int]) -> dict[str,str]:
        res : dict[str, str] = {}
        stmt = '''
select ofl.id, ofl.userid, ofl.day from
    (select name as id, pos, orgfolder$userid userid, orgfolder$name day from orgfolder$items) ofl, 
    (select min(pos) pos, orgfolder$userid userid, orgfolder$name day from OrgFolder$items where 
        orgfolder$name like '%{}' group by orgfolder$userid, orgfolder$name) mof
where ofl.userid = mof.userid and ofl.day = mof.day and ofl.pos = mof.pos and ofl.userid in ({})
'''.format(day, ','.join(["'{}'".format(x) for x in weekIndex.keys()]))

        for di in server.Query(stmt, 'FdSchI[id:s,userid:s,day:s]'):
            uid = di.userid
            if di.day == day:
                res[uid] = di.id
                continue
            if not uid in weekIndex:
                continue

            wday = str(weekIndex[uid]) + day
            if di.day == wday:
                res[uid] = di.id

        return res

    # ограничиваем проверку документами от последней проверке до текущего времени
    def getFirstDocTime(server, weekDay:int, lastCheck:LastCheckTime, now:datetime) -> dict[str, datetime]:
        res :dict[str, datetime] = {}

        docs = server.Get('FirstDocTime', "day={}".format(weekDay)) or []
        for di in docs:
            day = int(di.time)
            h = day // 60
            m = day % 60
            dt = datetime(year=now.year,month=now.month,day=now.day,hour=h, minute=m)
            if lastCheck.above(dt) and dt < now:
                res[di.userid] = dt

        return res

    def getWeekIndex(server, uids:list[str]) -> dict[str, int]:
        res : dict[str,int] = {}

        today = date.today()
        stmt = "select value, userid from serverconfig where [key]='SheduleStart' and userid in ({})".format(','.join(["'{}'".format(x) for x in uids]))

        for di in server.Query(stmt, 'SchCfgI[value:s,userid:s]'):
            try:
                stday = datetime.strptime(di.value, r'%Y-%m-%d').date()
                diff = today - stday

                res[di.userid] = ((diff.days // 7) % 4) + 1
            except:
                traceback.print_exc()

        return res

    days = ['Воскресенье', 'Понедельник', 'Вторник', 'Среда', 'Четверг', 'Пятница', 'Суббота']

    today = datetime.today()
    weekDay = (today.weekday() + 1) % 7
    curDay = days[weekDay]

    lastCheck = LastCheckTime(server)

    needCheck = getFirstDocTime(server, weekDay, lastCheck, today)
    if len(needCheck) == 0: 
       lastCheck.put(server, today)
       logging.info("No users to check")
       return

    weekIndex = getWeekIndex(server, list(needCheck.keys()))

    checkedOrgs = getFirstScheduledOrg(server, curDay, weekIndex)

    alerts = server.New('RouteDeviationCommit')
    start = today.replace(hour=0,minute=0,second=0,microsecond=0)    
    needAlert = havntDocs(server, checkedOrgs, needCheck, start)
    for uid in needAlert:
        ai = alerts.New()
        ai.userid = uid
        ai.type = 4
        ai.date = today

    lastCheck.put(server, today)

    if len(alerts) > 0:
        logging.info('Writes first doc alerts ' + str(len(alerts)))
        server.Write(alerts)

        sendDeviations(server)

def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)    
    logging.info('starting')

    makeDontWorkAlert(server)

    logging.info('ending')
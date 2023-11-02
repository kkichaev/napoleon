from datetime import datetime, date
import traceback
import requests
from google.oauth2 import service_account
import google.auth.transport.requests

import site
import sys
import logging
import json
import uuid


SCOPES = ['https://www.googleapis.com/auth/firebase.messaging']
SERVICE_FILE = 'google-services.json'

class LastCheckTime:
    DATE_FORMAT = r"%Y%m%d%H%M%S"
    CFG_KEY = 'LastStartChecking'

    def __init__(self, server) -> None:
        self.checkDate = datetime.now().replace(hour=0,minute=0,second=0,microsecond=0)
        d = server.Get('ServerConfig', "key='{}' and userid=''".format(LastCheckTime.CFG_KEY)) or []
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
    
    stmt = ''
    for uid, id in orgs.items():
        finish = schTime[uid]
        sti = makeUserStmt(uid, id, start, finish)
        if len(stmt) > 0:
            stmt += " union all "            
        stmt += sti

    stmt = "sum(ndocs) as ndocs, userid from ({}) group by userid".format(stmt)
    # print(stmt)

    ret = list(orgs.keys())
    for di in server.Query(stmt, 'HdocI[ndocs:n,userid:s]'):
        ret.remove(di.userids)

    return ret

def makeDontWorkAlert(server):
    def getFirstScheduledOrg(server, day:str, weekIndex:dict[str,int]) -> dict[str,str]:
        res : dict[str, str] = {}
        stmt = '''
select of.id, of.userid, of.day from
    (select name as id, pos, orgfolder$userid userid, orgfolder$name day from orgfolder$items) of, 
    (select min(pos) pos, orgfolder$userid userid, orgfolder$name day from OrgFolder$items where 
        orgfolder$name like '%{}' group by orgfolder$userid, orgfolder$name) mof
where of.userid = mof.userid and of.day = mof.day and of.pos = mof.pos and of.userid in ({})
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
        stmt = "select value, userid from serverconfig where key='SheduleStart' and userid in ({})".format(','.join(["'{}'".format(x) for x in uids]))
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
    # print("NeedCheck",needCheck)

    weekIndex = getWeekIndex(server, list(needCheck.keys()))
    # print("WeekIndex",weekIndex)

    checkedOrgs = getFirstScheduledOrg(server, curDay, weekIndex)

    alerts = server.New('RouteDeviation')
    start = today.replace(hour=0,minute=0,second=0,microsecond=0)    
    needAlert = havntDocs(server, checkedOrgs, needCheck, start)
    for uid in needAlert:
        ai = alerts.New()
        ai.userid = uid
        ai.type = 4
        ai.date = today

    print(alerts)

    # if len(alerts) > 0:
    #     server.Write(alerts)
    lastCheck.put(server, today)

class ManagerToken:
    def __init__(self, row) -> None:
        self.login:str = row.login
        self.token:str = row.token

    def __repr__(self) -> str:
        return "login: {}, token {}" .format(self.login, self.token)

class RouteDeviation:
    def __init__(self, row) -> None:
        self.date:datetime = row.date
        self.id:str = row.id
        self.userid:str = row.userid
        self.type:float=row.type
        self.orgName:str = row.orgName

    def __repr__(self) -> str:
        return "userid: {}, id {}, type {}" .format(self.userid, self.id, self.type)

def loadManagers(server) -> dict[float, list[ManagerToken]]:
    ret :dict[float, list[ManagerToken]] = {}
    stmt = 'select login, division, token from DivisionManager dm, MessageTokens mt where dm.login = mt.userid '
    res = server.Query(stmt, 'MgrTokn[login:s,division:n,token:s]')
    for ri in res:
        if not ri.division in ret:
            ret[ri.division] = []
        ret[ri.division].append(ManagerToken(ri))

    return ret

def loadDeviation(server) -> dict[float, list[RouteDeviation]]:
    ret:dict[float, list[RouteDeviation]] = {}

    stmt = """
select rd.*, da.Division$id from RouteDeviation rd, Division$agents da 
where rd.userid = da.id and (rd.sended is null or rd.sended <> 1)
 """
    res = server.Query(stmt,'DvItem[date:dt,id:s,type:n,userid:s,orgName:s,division@Division$id:n]')
    for ri in res:
        if not ri.division in ret:
            ret[ri.division] = []
        ret[ri.division].append(RouteDeviation(ri))

    return ret

def makeData(deviations:list[RouteDeviation]) -> dict[str, any]:
    
    df = r'%Y%m%d%H%M%S'
    ret : dict[str,any] = {}

    dates:list[str] = []
    ids:list[str] = []
    uids:list[str] = []
    types:list[str]=[]
    orgNames:list[str] = []

    for di in deviations:
        dates.append(di.date.strftime(df))
        ids.append(di.id)
        uids.append(di.userid)
        types.append(str(int(di.type)))
        orgNames.append(di.orgName)

    return {
        'date':','.join(dates)
        ,'id':','.join(ids)
        ,'userid':','.join(uids)
        ,'type':','.join(types)
        ,'orgName':','.join(orgNames)
    }

class Message:
    def __init__(self, token:str, project:str, data:dict[str,any]) -> None:

        id = str(uuid.uuid4()).replace('-', '')
        name = 'projects/{0}/messages/{1}'.format(project, id)

        self.data = {
            'name': name
            ,'data': data
            # , 'notification':{'title':title,'body':body}
            , 'token' : token
        }
    
    def compose(self) -> dict[str:any]:
        return {'validate_only':False, 'message':self.data}


def getServiceFileName() -> str:
    def get_site_packages():
        sp = site.getsitepackages()
        for si in sp:
            if si.endswith('site-packages'):
                return si + '\\'

        return sp[0] + '\\'

    return get_site_packages() + SERVICE_FILE

def _get_access_token() -> str:
    """Retrieve a valid access token that can be used to authorize requests.

    :return: Access token.
    """
    fileName = getServiceFileName()
    credentials = service_account.Credentials.from_service_account_file(fileName, scopes=SCOPES)
    request = google.auth.transport.requests.Request()
    credentials.refresh(request)
    return credentials.token

def sendMessage(project:str, token:str, message:Message):
    data = message.compose()
    
    url = 'https://fcm.googleapis.com/v1/projects/{0}/messages:send'.format(project)

    headers = {'Authorization':'Bearer ' + token, 'Content-Type':'application/json' }
    # print(data)
    resp = requests.post(url, json=data, headers=headers)
    print(resp.status_code, resp.content.decode('utf-8').replace("\n", '\n'))
    return resp.status_code < 300


def sendDeviations(server):
    file = open(getServiceFileName())
    js = json.load(file)
    file.close()
    project = js['project_id']

    dvn = loadDeviation(server)
    if len(dvn) > 0 :
        serverToken = _get_access_token()
        if len(serverToken) > 0 :
            cmtDvn = server.New('RouteDeviationCommit')
            mgr = loadManagers(server)
            for dvk,dvs in dvn.items():
                if not dvk in mgr:
                    logging.info('No manager for division {}'.format(int(dvk)))
                    continue

                managers = mgr[dvk]
                data = makeData(dvs)

                sended = True
                for mi in managers:
                    msg = Message(mi.token, project, data=data)
                    if not sendMessage(project, serverToken, msg):
                        sended = False
                        break
                
                if sended:
                    for di in dvs:
                        dest = cmtDvn.New()
                        dest.sended = 1
                        dest.userid = di.userid
                        dest.date = di.date
                        dest.orgName = di.orgName
                        dest.id = di.id
                        dest.type = di.type

            if len(cmtDvn) > 0:
                print('write cmt ', cmtDvn)
                server.Write(cmtDvn)

def run(server): 
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)    
    logging.info('start report')

    makeDontWorkAlert(server)
    # sendDeviations(server)

    logging.info('end')
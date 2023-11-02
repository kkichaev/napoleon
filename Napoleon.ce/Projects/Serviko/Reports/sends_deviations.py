from datetime import datetime, date
from google.oauth2 import service_account
import google.auth.transport.requests

import requests
import site
import sys
import logging
import json
import uuid

import logging

SCOPES = ['https://www.googleapis.com/auth/firebase.messaging']
SERVICE_FILE = 'google-services.json'

class Message:
    def __init__(self, token:str, project:str, data:list[dict[str,any]]) -> None:

        id = str(uuid.uuid4()).replace('-', '')
        name = 'projects/{0}/messages/{1}'.format(project, id)

        self.data = []
        for di in data :
            el = {
                'name': name
                ,'data': di
                # , 'notification':{'title':title,'body':body}
                , 'token' : token
            }
            self.data.append(el)
    
    def compose(self) -> list[dict[str:any]]:
        res = []

        for di in self.data:
            # print(di)
            res.append({'validate_only':False, 'message':di})

        return res

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

def sendMessage(project:str, token:str, message:Message):
    data = message.compose()
    
    url = 'https://fcm.googleapis.com/v1/projects/{0}/messages:send'.format(project)

    headers = {'Authorization':'Bearer ' + token, 'Content-Type':'application/json' }

    res = True
    # print(data)
    for d in data:
        resp = requests.post(url, json=d, headers=headers)

        if resp.status_code >= 300:
            res = False
            print('Send push error ',resp.status_code, resp.content.decode('utf-8').replace("\n", '\n'))
            break

    return res

def makeData(deviations:list[RouteDeviation]) -> list[dict[str, any]]:
    
    df = r'%Y%m%d%H%M%S'
    ret : dict[str,any] = {}

    dates:list[str] = []
    ids:list[str] = []
    uids:list[str] = []
    types:list[str]=[]
    orgNames:list[str] = []

    curLen = 0
    res = []
    for di in deviations:
        dates.append(di.date.strftime(df))
        ids.append(di.id)
        uids.append(di.userid)
        types.append(str(int(di.type)))
        orgNames.append(di.orgName)

        curLen += len(di.orgName) + len(di.userid)
        if curLen > 1000:
            res.append({
                'date':'<,>'.join(dates)
                ,'id':'<,>'.join(ids)
                ,'userid':'<,>'.join(uids)
                ,'type':'<,>'.join(types)
                ,'orgName':'<,>'.join(orgNames)
            })
            curLen = 0
            dates:list[str] = []
            ids:list[str] = []
            uids:list[str] = []
            types:list[str]=[]
            orgNames:list[str] = []

    if curLen > 0:
        res.append({
            'date':'<,>'.join(dates)
            ,'id':'<,>'.join(ids)
            ,'userid':'<,>'.join(uids)
            ,'type':'<,>'.join(types)
            ,'orgName':'<,>'.join(orgNames)
        })

    print('Make messages ', len(res))
    return res

def sendDeviations(server):
    file = open(getServiceFileName())
    js = json.load(file)
    file.close()
    project = js['project_id']

    dvn = loadDeviation(server)
    if len(dvn) == 0: return

    serverToken = _get_access_token()
    if len(serverToken) == 0 : return

    logging.info('Find ' + str(len(dvn)) + ' deviations')

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
        server.Write(cmtDvn)

def run(server): 
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout, level=logging.DEBUG)    
    logging.info('starting')

    sendDeviations(server)

    logging.info('end')
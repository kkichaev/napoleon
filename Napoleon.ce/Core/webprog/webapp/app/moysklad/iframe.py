import requests
import traceback
from datetime import datetime, timedelta
from app.moysklad import moysklad
from app.moysklad.models import BaseObject, Account, AccLog, Agent, Bundle, Org, Store, Firm, PriceType, Folder, Price, Pack, Stock, Delivery, Balance, MetaObject, Service
from app.moysklad.endpoints import getUserContext, setAppStatus
from flask import Response, abort, jsonify, render_template, request, url_for
from app.fcgi_client import FCGIManager, get_result_data
from app import db
from flask_babel import gettext as _

class IFrameData(object):
    def __init__(self, context:dict[str,any]) -> None:
        self.id = context['uid']
        self.fio = context['shortFio']
        self.account = Account.get(context['accountId'])
        if self.account.srv_token is None:
            self.account.srv_token = ''

        self.isAdmin = context['permissions']['admin']['view']

        if self.isAdmin:
            self.stores = Store.getMoySklad(self.account)
            nstores = Store.getNapoleon(self.account)
            primary = nstores[0] if len(nstores) else Store({'id':''})
            for oi in self.stores:
                oi.assigned = oi in nstores
                oi.primary = oi == primary

            self.agents = Agent.getMoySklad(self.account)
            nagents = Agent.getNapoleon(self.account)
            for oi in self.agents:
                oi.assigned = oi in nagents

            self.firms = Firm.getMoySklad(self.account)
            nfirms = Firm.getNapoleon(self.account)
            primary = nfirms[0] if len(nfirms) else Firm({'id':''})
            for oi in self.firms:
                oi.assigned = oi in nfirms
                oi.primary = oi == primary

            self.folders = Folder.getMoySklad(self.account, 'pathName=')
            nfolders = Folder.getNapoleon(self.account, '"parent"='+ "''")
            for oi in self.folders:
                oi.assigned = oi in nfolders


@moysklad.route('iframe')
def render_iframe():
    contextKey = request.args.get('contextKey')

    # context = {'uid':'uid', 'shortFio':'fio','accountId':"id",'permissions':{'admin':{'view':True}}}
    context = getUserContext(contextKey, version=1)
    if context is None:
        abort(400)
    
    data = IFrameData(context)

    return render_template('moysklad/iframe.html', data=data)

def error(account:Account, action:str, message:str, trace:str='',code=401) -> Response:
    id = account.accid if account else 'no_id'
    AccLog.addErr(account.accid, action, message, trace)

    res = jsonify({'message': message, 'error': True})
    res.status_code = code
    return res

MOY_SKLAD_DF = '%Y-%m-%d %H:%M:%S'
GRS_DATE_FORMAT = '%Y%m%d%H%M%S'
def dateFromMoySklad(dateStr:str) -> datetime:
    date = dateStr
    pidx = date.find('.')
    if pidx > 0: date = date[:pidx]

    return datetime.strptime(date, MOY_SKLAD_DF)
    
def convertDate(dateStr:str, toMoySklad:bool) ->str:
    if not toMoySklad:
        dt = dateFromMoySklad(dateStr)
        return dt.strftime(GRS_DATE_FORMAT)
    
    dt = datetime.strptime(dateStr, GRS_DATE_FORMAT)
    return dt.strftime(MOY_SKLAD_DF)

def putOrgData(account:Account, agents:list[Agent]) -> tuple[list[PriceType], int]:
    defPT : PriceType = None
    usedPt:list[PriceType] = []
    prcTypes:dict[str,PriceType] = {}
    for pi in PriceType.getMoySklad(account):
        if not defPT: 
            defPT = pi
            usedPt.append(pi)
        prcTypes[pi.id] = pi

    if defPT is None: defPT = PriceType({"id":"", "name":""})

    # filter = ';'.join(['owner=' + x.href() for x in agents])
    filter = ';'.join(['group=' + x.group.href for x in agents])
    # print(filter)

    orgs = Org.getMoySklad(account, filter)
    agentOrgData = []
    orgData = []
    for oi in orgs:
        pt = oi.priceType
        if pt == '' :
            pt = defPT.id
        else:
            pt = pt.id
            if pt in prcTypes:
                pti = prcTypes[pt]
                if not pti in usedPt: usedPt.append(pti)

        dest = {'id':oi.id, 'name':oi.name, 'address':oi.actualAddress, 'prcType':pt}
        orgData.append(dest)
        if oi.owner == '' :
            groupId = oi.group.id
            for ai in agents:
                if ai.group.id == groupId:
                    # print("Add org to ", ai.name)
                    agentOrgData.append({'id':oi.id,'userid':ai.id,'type':'Org'})
        else:
            agentOrgData.append({'id':oi.id,'userid':oi.owner.id,'type':'Org'})

    uri = '/object'
    data = [
        {'name':'Org','data':orgData,'where':''},
        {'name':'AgentData','data':agentOrgData,'where':"type='Org'"},
    ]

    fcgm = FCGIManager.get()
    _r = fcgm.send_to_server(account.srv_token, uri, 'PUT', data)
    # print('Org',res)
    # answ, data = get_result_data(res)
    return (usedPt, len(orgData))

def loadPrice(account:Account, priceTypes, topFolders:dict[str,any], agents:list[Agent]) :
    rootId = 'ROOT_FOLDER'

    def getParentId(fi) -> str:
        parent = fi.productFolder
        return rootId if parent == '' else  parent.id


    def containsFolder(tf:dict[str,any], folders:list[any], folder) -> bool:
        while True:
            pf = getParentId(folder)
            if pf in tf : return True

            tf = None
            for fi in folders:
                if fi.id == pf: 
                    folder = fi
                    tf

            if tf == None: return False
            folder = tf

    def putItemToAgents(pi:Price, agentPriceData):
        if pi.owner == '' :
            groupid = pi.group.id
            for ai in agents:
                if ai.group.id == groupid:
                    # print("Add item to ", pi.name, ai.name)
                    agentPriceData.append({'id':pi.id,'userid':ai.id,'type':'Price'})
        else:
            # print(pi.owner.id, 'has', pi.id, pi.name)
            agentPriceData.append({'id':pi.id,'userid':pi.owner.id,'type':'Price'})

    def putPriceData(pi:Price, weight:float, priceData, costData, priceTypes):
        parent = pi.productFolder
        if parent == '':
            parent = rootId
        else:
            parent = parent.id

        v = {'id':pi.id, 'fid':parent,'name':pi.name,'units':pi.makeUnits(packs), 'weight':weight}
        priceData.append(v)

        v = pi.makeCost(priceTypes)
        if len(v) > 0:
            costData.extend(v)


    def loadGoods(priceData, agentPriceData, costData, packs):
        price = Price.getMoySklad(account)
        for pi in price:
            putItemToAgents(pi, agentPriceData)
            putPriceData(pi, pi.weight, priceData, costData, priceTypes)

    def loadBundles(priceData, agentPriceData, costData, packs) -> list[Bundle]:
        price = Bundle.getMoySklad(account)
        for pi in price:
            putItemToAgents(pi, agentPriceData)
            putPriceData(pi, pi.weight, priceData, costData, priceTypes)

        return price

    def loadServices(priceData, agentPriceData, costData, packs) -> list[Bundle]:
        price = Service.getMoySklad(account)
        for pi in price:
            putItemToAgents(pi, agentPriceData)
            putPriceData(pi, 0, priceData, costData, priceTypes)

        return price

    folders = Folder.getMoySklad(account)
    folderData : list[dict[str,any]] = []

    if len(topFolders) == 0:
        folderData = [{'fid':rootId,'name':_('RootFolder'),'parent':''}]
        for fi in folders:
            parent = getParentId(fi)

            folderData.append({'fid':fi.id,'name':fi.name,'parent':parent})
    else:
        for fi in folders:
            if fi.id in topFolders:
                folderData.append({'fid':fi.id,'name':fi.name,'parent':''})
            elif containsFolder(topFolders, folders, fi):
                parent = getParentId(fi)
                folderData.append({'fid':fi.id,'name':fi.name,'parent':parent})

    priceData = []
    costData = []
    agentPriceData = []

    packs = Pack.getPacks(account)

    loadGoods(priceData, agentPriceData, costData, packs)
    bundles = loadBundles(priceData, agentPriceData, costData, packs)
    services = loadServices(priceData, agentPriceData, costData, packs)

    ptData = []
    for pi in priceTypes:
        ptData.append({'id':pi.id,'name':pi.name})

    uri = '/object'
    data = [
        {'name':'Folder','data':folderData,'where':''},
        {'name':'Price','data':priceData,'where':""},
        {'name':'PriceCost','data':costData,'where':""},
        {'name':'PriceTypes','data':ptData,'where':""},
        {'name':'AgentData','data':agentPriceData,'where':'"type"='+"'Price'"},
    ]
    
    # print('FD',len(folderData))
    # print('Prc',len(priceData))
    # print("Cost",len(costData))
    # print('PriceTypes',len(ptData))
    fcgm = FCGIManager.get()
    res = fcgm.send_to_server(account.srv_token, uri, 'PUT', data)
    
    # print('Price',res)

    return (len(priceData), bundles, services)


def loadStock(account:Account, bundles:list[Bundle], services:list[Service]) :
    qtyData = []
    stockDic = {}
    stores = []
    stockData = Stock.getMoySklad(account)
    for ri in stockData:
        stockDic[ri.id] = ri.stocks
        for rii in ri.stocks:
            stockId = rii['id']
            if not stockId in stores: stores.append(stockId)

            v = {'id':rii['id'], 'qty':rii['qty'], 'idItem':ri.id}
            qtyData.append(v)

    try:
        for bi in bundles:
            bi.loadStock(account, qtyData, stockDic)
        for si in services:
            si.loadStock(qtyData, stores)
    except:
        traceback.print_exc()    

    uri = '/object'
    data = [
        {'name':'StoreQty','data':qtyData,'where':''},
    ]
    fcgm = FCGIManager.get()
    _r = fcgm.send_to_server(account.srv_token, uri, 'PUT', data)
    # print(res)

def loadDeliveries(account:Account, agents:list[Agent]) -> int:

    def updateSumD(balance:dict[str,dict[str,any]]) -> None:
        for data in balance.values():
            sum = data['sum']
            if sum <= 0 : continue

            doclist = sorted(data['documents'], key=lambda x:x['date'], reverse=True)
            for doc in doclist:
                sumD = doc['sumDoc'] - doc['payed']
                if sumD > sum:
                    sumD = sum
                doc['sum'] = sumD
                sum -= sumD
                if sum <= 0: break
        # print(balance)
                

    DAYS = 60
    PAY_DAYS = 14
    dstart = datetime.now() - timedelta(days=DAYS)

    # filter = ';'.join(['owner=' + x.href() for x in agents])
    # filter += ';demand>' + dstart.date().strftime(MOY_SKLAD_DF) 
    filter = 'moment>' + dstart.date().strftime(MOY_SKLAD_DF) 
    dlv = Delivery.getMoySklad(account, filter)

    balanceData = []
    balance:dict[str,dict[str,any]] = {}
    blnc = Balance.getMoySklad(account, 'balance!=0')
    for bi in blnc:
        v = {'id': bi.id, 'sum': -bi.balance / 100, 'documents':[]}
        balance[bi.id] = v
        balanceData.append(v)

    dlvData = []
    for di in dlv:
        items = []
        for dii in di.positions.rows:
            qty = dii['quantity']
            sum = qty * dii['price'] / 100
            meta = MetaObject(dii['assortment'])
            id = meta.id
            mtype = meta.type
            if mtype == 'service' or mtype == 'bundle':
                id += '|' + mtype
            ii = {'id':id, 'qty':qty, 'sum':sum}
            # print('Item', ii)
            items.append(ii)

        id = di.agent.id
        docDate =  dateFromMoySklad(di.moment)
        payDate = docDate + timedelta(days=PAY_DAYS)
        v = {'id':id, 'number':di.name, 'uid':di.id,
             'date':docDate.strftime(GRS_DATE_FORMAT), 'items':items
             }
        dlvData.append(v)

        if id in balance:
            v = {'number':di.name, 'date':docDate.strftime(GRS_DATE_FORMAT), 'payDate':payDate.strftime(GRS_DATE_FORMAT),
                 'sumDoc':di.sum / 100, 'sum':0, 'title':_('Delivery'),'type':'Delivery',
                 'uid':di.id, 'payed':di.payedSum / 100
                 }
            balance[id]['documents'].append(v)

    updateSumD(balance)

    uri = '/object'
    fcgm = FCGIManager.get()
    data = [
        {'name':'Balance','data':balanceData,'where':''},
    ]
    _res = fcgm.send_to_server(account.srv_token, uri, 'PUT', data)
    # print(res)
    data = [
        {'name':'Delivery','data':dlvData},
    ]
    _res = fcgm.send_to_server(account.srv_token, uri, 'POST', data)
    # print(res)
    # print(dlvData)
    # print(balanceData)
    return len(dlvData)


@moysklad.route('put_data/<accountId>', methods=['POST'])
def putDataToServer(accountId):
    account = Account.get(accountId)
    if not account:
        return error(None, 'putDataToServer', _('No account') + accountId)
    
    fcgm = FCGIManager.get()
    # res = fcgm.send_to_server(account.srv_token, '/object/ServerConfig?userid='+"'%25ServerID%25'")
    # print(res)

    if request.origin:
        load_docs_url = request.origin + url_for('moysklad.loadDocs',accountId=accountId)
    else:
        load_docs_url = url_for('moysklad.loadDocs',accountId=accountId)
        
    data = {'userid':r'%ServerID%','key':r'%LoadDocsURI%','value':load_docs_url}
    conf_data = [{'name':'ServerConfig','data':[data]}]
    res = fcgm.send_to_server(account.srv_token, '/object', 'POST', conf_data)
    # print('REQ', res)
    # stat = {'agents':0, 'orgs':0, 'price':0, 'dlv':0}
    # return jsonify({'error':False, 'stat':stat})

    topFolders = {}
    if request.is_json:
        jreq = request.get_json()

        if jreq and 'folders' in jreq:
            for fi in jreq['folders']:
                topFolders[fi["id"]] = fi


    aset : set[str] = set()
    an = Agent.getNapoleon(account) or []
    for ai in an:
        aset.add(ai.id)

    agents : list[Agent] = []
    ams = Agent.getMoySklad(account)
    for ai in ams:
        if ai.id in aset:
            agents.append(ai)

    usedPT, orgCount = putOrgData(account, agents)
    priceCount, bundles, services = loadPrice(account, usedPT, topFolders, agents)
    loadStock(account, bundles, services)
    dlvCount = loadDeliveries(account, agents)

    # goodsData, folderData = loadPrice(account, usedPT)

    AccLog.add(account.accid, _('Put data to the server'))
    stat = {'agents':len(agents), 'orgs':orgCount, 'price':priceCount, 'dlv':dlvCount}
        
    setAppStatus('Activated', account, version=1)
    return jsonify({'error':False, 'stat':stat})


@moysklad.route('server_code/<accountId>', methods=['PUT'])
def updateServerCode(accountId):
    try:
        account = Account.get(accountId)
        if not account:
            return error(None, 'putDataToServer', _('No account') + accountId)

        data = request.get_json() or {}
        if not 'code' in data:
            return error(account, 'updateServerCode', _('No project code'))

        code = data['code']
        fcgm = FCGIManager.get()
        res = fcgm.send_to_manager('server_info', {'code':code})
        if isinstance(res, Response):
            return error(account, 'updateServerCode','Не найден код проекта ' + code)
            # return error(account, 'updateServerCode',_('No project') + ' ' + code)
        
        answ, data = get_result_data(res)
        if not answ.ok:
            return error(account, 'updateServerCode',"Не найден код проекта")
            # return error(account, 'updateServerCode',_('No project') + ' ' + code)
        account.srv_token = code
        db.session.commit()
    except:
        trb = traceback.format_exc()
        return error(account, 'updateServerCode', '', trb)
    return jsonify({'error':False})

class DocLoader:
    @staticmethod
    def objectName() -> str: return ""

    @staticmethod
    def docType() -> str: return ""

    @staticmethod
    def moySkladObject() -> str: return ""

    @staticmethod
    def makeItem(oi:dict) ->dict[str,any]:
        id : str = oi['id']
        sepIdx = id.find('|')
        if sepIdx >= 0:
            itemType = id[sepIdx+1:]
            id = id[:sepIdx]
            if itemType == 'service':
                itemRef = Service.meta(id)
            else:
                itemRef = Bundle.meta(id)
        else:            
            itemRef = Price.meta(id)
        
        item = {'quantity':oi['qty'],'price':oi['cost'] * 100.0,'assortment':itemRef}
        return item

    @staticmethod
    def makeDoc(src, fcgm:FCGIManager, account:Account) -> dict[str, any] : 
        doc = {
            'agent':Org.meta(src['id']),
            'description':src['remark'],
            'owner':Agent.meta(src['userid']),
            'shared':True,
        }
        return doc

    @classmethod
    def loadDocs(cls, account:Account, fcgm:FCGIManager) -> tuple[Response|None,int]:
        ndocs = 0
        try:
            uri = '/object/' + cls.objectName()
            res = fcgm.send_to_server(account.srv_token, uri)

            answ, data = get_result_data(res)
            docs = data.get_list(cls.objectName())
            if not answ.ok:
                return (error(account,'loadDocs',answ.message), 0)
            
            if not docs:
                return (None, 0)
            
            commitData = []
            for di in docs:
                docData = []
                try:
                    doc = cls.makeDoc(di, fcgm, account)
                    if not doc: continue
                    if not 'organization' in doc:
                        firm = fcgm.get_object(account.srv_token, 'Firm')[0]
                        doc['organization'] = Firm.meta(firm['id'])

                    docData.append(doc)

                    uri = BaseObject.MOY_SKLAD_HREF + cls.moySkladObject()
                    headers = {
                        'Authorization' : 'Bearer ' + account.json_token,
                        'Content-Type' : 'application/json'
                    }
                    res = requests.post(uri, json=docData, headers=headers)
                    if res.status_code > 300:
                        print(res.content.decode('utf-8'))
                        continue
                        # return (error(account,'loadDocs',res.content.decode('utf-8')), 0)
                    
                    response = res.json()
                    for ri in response:
                        docdata = {'created':di['created'], 'type':cls.docType(), 'userid':di['userid'], 
                                   'number':ri['name'], 'date':convertDate(ri['moment'], False)}
                        commitData.append(docdata)
                except:
                    traceback.print_exc()
                
                # print(commitData)
                
                if len(commitData) > 0:
                    uri = '/object'
                    data = [
                        {'name':'DocCommitted','data':commitData},
                        {'name':'DocProceeded','data':commitData},
                    ]
                    ndocs = len(commitData)
                    _r = fcgm.send_to_server(account.srv_token, uri, 'POST', data)
                    # print(data)
                    # print(res)
        except:
            trb = traceback.format_exc()
            return (error(account, 'loadDocs', '', trb), 0)

        return (None, ndocs)


class OrderLoader(DocLoader):
    @staticmethod
    def objectName() -> str: return "NewOrders"

    @staticmethod
    def docType() -> str: return "Order"

    @staticmethod
    def moySkladObject() -> str: return "entity/customerorder"

    @staticmethod
    def makeDoc(src, fcgm:FCGIManager, account:Account) :
        items = []
        for oi in src['items']:
            item = DocLoader.makeItem(oi)
            if len(oi['unit']) > 0:
                item['pack'] = {'id':oi['unit'] }
            items.append(item)

        order = DocLoader.makeDoc(src, fcgm, account)
        
        order.update({
            'organization':Firm.meta(src['firmCode']), 
            'positions':items,
            'store':Store.meta(src['whCode']),
            'deliveryPlannedMoment':convertDate(src['date'], True)
        })

        return order

class ReturnLoader(DocLoader):
    @staticmethod
    def objectName() -> str: return "NewReturns"

    @staticmethod
    def docType() -> str: return "Returns"

    @staticmethod
    def moySkladObject() -> str: return "entity/salesreturn"

    @staticmethod
    def makeDoc(src, fcgm:FCGIManager, account:Account) :
        items = []
        for oi in src['items']:
            item = DocLoader.makeItem(oi)
            # item = {'quantity':oi['qty'],'price':oi['cost'] * 100.0,'assortment':Price.meta(oi['id'])}
            # if len(oi['unit']) > 0:
            #     item['pack'] = {'id':oi['unit'] }
            items.append(item)

        order = DocLoader.makeDoc(src, fcgm, account)
        store = fcgm.get_object(account.srv_token, 'Stores')[0]
        
        order.update({
            'positions':items,
            'store':Store.meta(store['id'])
        })

        return order

class IncassLoader(DocLoader):
    @staticmethod
    def objectName() -> str: return "NewIncass"

    @staticmethod
    def docType() -> str: return "Incass"

    @staticmethod
    def moySkladObject() -> str: return "entity/cashin"

    @staticmethod
    def makeDoc(src, fcgm:FCGIManager, account:Account) :
        items = []

        for oi in src['items']:
            meta = Delivery.meta(oi['uid'])
            item = {'linkedSum':oi['sum'] * 100.0}
            item.update(meta)
            items.append(item)

        order = DocLoader.makeDoc(src, fcgm, account)
        
        order.update({
            'operations':items,
            'sum':src['sum'] * 100.0,
            # проведение документа
            'applicable':False,
        })

        # print('incass', order)
        return order

@moysklad.route('load_docs/<accountId>')
def loadDocs(accountId):
    account = Account.get(accountId)
    if not account:
        return error(None, 'loadDocs', _('No account'))

    fcgm = FCGIManager.get()

    firms = fcgm.get_object(account.srv_token, "Firm")
    if len(firms) == 0:
        return error(account, 'loadDocs', _('No firms'))

    stores = fcgm.get_object(account.srv_token, "Stores")
    if len(stores) == 0:
        return error(account, 'loadDocs', _('No stores'))

    res, norders = OrderLoader.loadDocs(account, fcgm)
    if res:
        return res

    res, nreturns = ReturnLoader.loadDocs(account, fcgm)
    if res:
        return res

    res, nincass = IncassLoader.loadDocs(account, fcgm)
    if res:
        return res

    text = _('Orders %d, Returns %d, Incass %d') % (norders, nreturns, nincass)
    AccLog.add(account.accid, _('Load docs'), text )
    stat = {'orders':norders, 'returns':nreturns, 'incass':nincass}
    return jsonify({'error':False, 'stat':stat})

@moysklad.route('ift')
def test_iframe():
    # context = {'uid':'uid', 'shortFio':'fio','accountId':"id",'permissions':{'admin':{'view':True}}}
    # context = getUserContext('8d260a43-df5c-11ed-0a80-06f1000048a0')

    context = {
        'uid' : '1',
        'shortFio':'test',
        'accountId':'8fd4459f-9551-11ed-0a80-0bdd00011654',
        'permissions':{'admin':{'view':True}},        
    }

    data = IFrameData(context)

    return render_template('moysklad/iframe.html', data=data)


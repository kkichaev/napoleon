from time import time
from typing import Collection, Self

import requests
import collections.abc
from app import db
from app.fcgi_client import FCGIManager, get_result_data
import urllib.parse

class Account(db.Model):
    __tablename__ = 'mskl_account'

    accid = db.Column(db.String(100), primary_key=True)
    srv_token = db.Column(db.String(100))
    json_token = db.Column(db.String(100))

    @staticmethod
    def get(accid:str) -> Self:
        return Account.query.filter_by(accid=accid).first()

class AccLog(db.Model):
    __tablename__ = 'mskl_acclog'

    id = db.Column(db.Integer, primary_key=True)
    accid = db.Column(db.String(100), index=True)
    date = db.Column(db.Integer, index=True)
    action = db.Column(db.String(100))
    text = db.Column(db.String(1000))
    error = db.Column(db.Integer)
    trace = db.Column(db.String(1000))

    # __table_args__ = (db.PrimaryKeyConstraint(accid, date,),)

    @staticmethod
    def addErr(accid:str, action:str, text:str='',trace:str='') -> None:
        a = AccLog(accid = accid, date = time(), action=action, text=text, error=1, trace=trace)
        db.session.add(a)
        db.session.commit()

    @staticmethod
    def add(accid:str, action:str, text:str='') -> None:
        a = AccLog(accid = accid, date = time(), action=action, text=text, error=0)
        db.session.add(a)
        db.session.commit()

class AccTaskStatus(db.Model):
    STATUS_RUN = 1
    STATUS_FINISH = 2
    STATUS_NONE = 0

    LOAD_DATA_TASK = 'load_data'

    __tablename__ = 'mskl_acctask'

    id = db.Column(db.Integer, primary_key=True)
    accid = db.Column(db.String(100), index=True)
    taskid = db.Column(db.String(100))
    date = db.Column(db.Integer, index=True)
    status = db.Column(db.Integer) 
    info = db.Column(db.String(1000))

    def to_dict(self):
        return {'id':self.id,'accid':self.accid,'taskid':self.taskid,'date':self.date,'status':self.status,'info':self.info}

    @staticmethod
    def get(accid:str, taskid:str) -> Self:
        return AccTaskStatus.query.filter_by(accid=accid,taskid=taskid).first()

    @staticmethod
    def task_status(accid:str, taskid:str) -> int:
        r = AccTaskStatus.get(accid, taskid)
        return AccTaskStatus.STATUS_NONE if not r else r.status
    
    @staticmethod
    def set_status(accid:str, taskid:str, status:int, info:str=""):
        st = AccTaskStatus.query.filter_by(accid=accid,taskid=taskid).first()
        if st :
            st.date = time()
            st.status = status
            st.info = info
        else :
            ac = AccTaskStatus(accid=accid,taskid=taskid,status=status,info=info,date=time())
            db.session.add(ac)
        db.session.commit()

class MetaObject:
    def __init__(self, metaSrc:dict[str,any]) -> None: 
        src = metaSrc['meta'] if 'meta' in metaSrc else metaSrc
        self.rows = []

        if 'rows' in metaSrc:
            self.rows = metaSrc['rows']

        self.type = src['type'] if 'type' in src else ''
        if 'href' in src:
            self.href = src['href']
            idx = self.href.rfind('/')
            self.id = self.href[idx+1:]
            idx = self.id.find('?')
            if idx > 0: 
                self.id = self.id[:idx]
        else:
            self.href = ''
            self.id = ''

class BaseObject:
    # MOY_SKLAD_HREF = 'https://online.moysklad.ru/api/remap/1.2/'
    MOY_SKLAD_HREF = 'https://api.moysklad.ru/api/remap/1.2/'

    def __init__(self, src:dict[str,any] = None) -> None:
        self.src = src or {}

    def __getattr__(self, name: str) -> any:
        if name in self.src:
            val = self.src[name]
            if isinstance(val, dict) and 'meta' in val:
                return MetaObject(val)
            return val
        
        return ''
        # raise AttributeError
    
    def __hash__(self) -> int:
        return hash(self.id)
    
    def __eq__(self, __value: object) -> bool:
        return self.id == __value.id

    @staticmethod
    def uriMoySklad() -> str : return ''  

    @staticmethod
    def uriParams() -> dict[str,str] : return {}

    @staticmethod
    def objectName() -> str: return ''

    @staticmethod
    def typeMoySklad() -> str: return ''

    def href(self) -> str:
        return BaseObject.MOY_SKLAD_HREF + self.uriMoySklad() + '/' + self.id
    
    @classmethod
    def meta(cls, id:str) -> dict[str,any]:
        el = cls({'id':id})
        v = {'href': el.href(), 'type':el.typeMoySklad(), 'mediaType':'application/json'}
        return {'meta':v}

    @classmethod
    def getMoySkladSlice(cls, account:Account, offset:int, filter:str = None) -> tuple[list[Self],int]:
        el:BaseObject = cls()
        uri = BaseObject.MOY_SKLAD_HREF + el.uriMoySklad()
        return el.readMoySkladSlice(uri, account, offset, filter)
    
    @classmethod
    def readMoySkladSlice(cls, uri:str, account:Account, offset:int, filter:str = None) -> tuple[list[Self],int]:
        headers:dict[str,any] = {
            "Accept-Encoding": "gzip",
            "Authorization": "Bearer " + account.json_token,
            'Content-Type': 'application/json',
        }

        ret:list[Self] = []

        size = 0
        params = cls.uriParams()

        if filter:
            params.update({'filter':filter})
            # uri += "?filter=" + filter
        if offset != 0:
            params.update({'offset':offset})
        
        res = requests.get(uri, params=params, headers=headers)
            # print(res.url)

        if res.status_code < 300:
            jres = res.json()
            if 'meta' in jres:
                rowsData = jres['meta']

                size = rowsData['size']
                offset = rowsData['offset']
                # limit = rowsData['limit']

            if 'rows' in jres:
                for ri in jres['rows']:
                    dst = cls(ri)
                    ret.append(dst)
            elif isinstance(jres, dict):
                dst = cls(jres)
                ret.append(dst)
            elif isinstance(jres, Collection):
                for ri in jres:
                    dst = cls(ri)
                    ret.append(dst)

        return (ret, size)


    @classmethod
    def getMoySklad(cls, account:Account, filter:str = None) -> list[Self]:
        el:BaseObject = cls()
        uri = BaseObject.MOY_SKLAD_HREF + el.uriMoySklad()
        return el.readMoySklad(uri, account, filter)
    
    @classmethod
    def readMoySklad(cls, uri:str, account:Account, filter:str = None) -> list[Self]:
        headers:dict[str,any] = {
            "Accept-Encoding": "gzip",
            "Authorization": "Bearer " + account.json_token,
            'Content-Type': 'application/json',
        }

        ret:list[Self] = []

        size = 0
        offset = 0
        doing = True

        while doing:
            doing = False
            params = cls.uriParams()
            if filter:
                params.update({'filter':filter})
                # uri += "?filter=" + filter
            if offset != 0:
                params.update({'offset':offset})
            
            res = requests.get(uri, params=params, headers=headers)
            # print(res.url)

            if res.status_code < 300:
                jres = res.json()
                if 'meta' in jres:
                    rowsData = jres['meta']

                    size = rowsData['size']
                    offset = rowsData['offset']
                    # limit = rowsData['limit']

                if 'rows' in jres:
                    for ri in jres['rows']:
                        dst = cls(ri)
                        ret.append(dst)
                    if len(ret) < size:
                        doing = True
                        offset = len(ret)
                elif isinstance(jres, dict):
                    dst = cls(jres)
                    ret.append(dst)
                elif isinstance(jres, Collection):
                    for ri in jres:
                        dst = cls(ri)
                        ret.append(dst)

        return ret

    
    @classmethod
    def getNapoleon(cls, account:Account, filter:str = None) -> list[Self]:
        fcgm = FCGIManager.get()
        el = cls()
        url = '/object/' + el.objectName()
        if filter and len(filter) > 0: 
            url += "?" + filter
        
        res = fcgm.send_to_server(account.srv_token, url)
        answ, data = get_result_data(res)
        
        ret:list[Self] = []
        if data:
            lst = data.get_list(el.objectName()) or []
            for src in lst:
                di = cls(src)
                ret.append(di)

        return ret
    

class Agent(BaseObject):
    @staticmethod
    def uriMoySklad() -> str: return 'entity/employee'
    
    @staticmethod
    def typeMoySklad() -> str: return 'employee'
    
    @staticmethod
    def objectName() -> str: return 'Agents'
    
class Org(BaseObject):
    @staticmethod
    def objectName() -> str: return 'Org'

    @staticmethod
    def uriMoySklad() -> str: return 'entity/counterparty'

    @staticmethod
    def typeMoySklad() -> str: return 'counterparty'

class Store(BaseObject):
    @staticmethod
    def objectName() -> str: return 'Stores'

    @staticmethod
    def uriMoySklad() -> str: return 'entity/store'

    @staticmethod
    def typeMoySklad() -> str: return 'store'

class Firm(BaseObject):
    @staticmethod
    def objectName() -> str: return 'Firm'

    @staticmethod
    def uriMoySklad() -> str: return 'entity/organization'

    @staticmethod
    def typeMoySklad() -> str: return 'organization'

class Folder(BaseObject):
    @staticmethod
    def objectName() -> str: return 'Folder'

    @staticmethod
    def uriMoySklad() -> str: return 'entity/productfolder'

    @staticmethod
    def typeMoySklad() -> str: return 'productfolder'

class Pack(BaseObject):
    @staticmethod
    def uriMoySklad() -> str: return 'entity/uom'

    @staticmethod
    def typeMoySklad() -> str: return 'uom'
    
    @staticmethod
    def getPacks(account:Account) -> dict[str,Self] :
        ret:dict[str,Self] = {}
        for pi in Pack.getMoySklad(account):
            ret[pi.id] = pi
        return ret

class PriceType(BaseObject):
    @staticmethod
    def uriMoySklad() -> str: return 'context/companysettings/pricetype'
    
    # @classmethod
    # def getMoySklad(cls, account: Account, filter: str = None) -> list[Self]:
    #     el:BaseObject = cls()
    #     uri = BaseObject.MOY_SKLAD_HREF + el.uriMoySklad() + '/default'
    #     print(uri)
    #     dflt = el.readMoySklad(uri, account)

    #     dprc = dflt[0] if len(dflt) > 0 else PriceType({"id":""})

    #     uri = BaseObject.MOY_SKLAD_HREF + el.uriMoySklad()
    #     res = el.readMoySklad(uri, account, filter)
    #     for eli in res:
    #         eli.default = eli == dprc

    #     return res

class Price(BaseObject):
    @staticmethod
    def objectName() -> str: return 'Price'

    @staticmethod
    def uriMoySklad() -> str: return 'entity/product'

    @staticmethod
    def typeMoySklad() -> str: return 'product'

    def makeUnits(self, packs:dict[str,Pack]) -> list[any]:
        ret = []
        if not 'uom' in self.src:
            ret.append({"id":'','code':'', 'name':'Базовая единица', 'inpack':1})
        else:        
            pc = self.uom.id
            name = packs[pc].name if pc in packs else ' '
            ret.append({"id":'','code':pc, 'name':name, 'inpack':1})

        if 'packs' in self.src:
            for pi in self.src['packs']:
                
                inpack = pi['quantity']
                meta = MetaObject(pi['uom'])
                pc = meta.id
                name = packs[pc].name if pc in packs else str(inpack)
                
                ret.append({"id":pi['id'], 'code':pc, 'name':name, 'inpack':inpack})

        return ret

    def makeCost(self, prcTypes:list[any], dataVersion:int = 0) -> list[any]:
        ret = []

        if 'salePrices' in self.src:
            for pi in self.src['salePrices']:
                pid = pi['priceType']['id']
                ptp = PriceType({"id":pid})
                if not ptp in prcTypes:
                    continue

                ret.append({'id':pid, 'idItem':self.id, 'cost':pi['value'] / 100.0, 'dataVersion':dataVersion})

        return ret

class Bundle(Price):
    @staticmethod
    def uriMoySklad() -> str: return 'entity/bundle'

    @staticmethod
    def typeMoySklad() -> str: return 'bundle'

    def href(self) -> str:
        res = super().href()
        idx = res.find('|')
        return res if idx < 0 else res[:idx]

    def __getattr__(self, name: str) -> any:
        res = super().__getattr__(name)
        if name == "id":
            res += "|" + Bundle.typeMoySklad()
            # print('Bundle id', res)
        return res
    
    def loadStock(self, account:Account, qtyData:list, stockData:dict, dataVersion:int = 0) :
        components = BundleComponents.load(self, account)
        stock : dict[str,float] = {}
        
        for ci in components:
            id = ci.assortment.id
            # print(id)
            if not id in stockData:
                stock.clear()
                break
            coef = ci.quantity
            for storeData in stockData[id]:
                qty = storeData['qty'] / coef
                storeId =  storeData['id']

                if not storeId in stock:
                    stock[storeId] = qty
                elif stock[storeId] > qty:
                    stock[storeId] = qty

        itemId = self.id
        for storeId,qty in stock.items():
            if qty > 0:
                v = {'id':storeId, 'qty':qty, 'idItem':itemId, 'dataVersion':dataVersion}
                qtyData.append(v)


class Service(Price):
    @staticmethod
    def uriMoySklad() -> str: return 'entity/service'

    @staticmethod
    def typeMoySklad() -> str: return 'service'

    def __getattr__(self, name: str) -> any:
        res = super().__getattr__(name)
        if name == "id":
            res += "|" + Service.typeMoySklad()
            # print('Bundle id', res)
        return res

    def href(self) -> str:
        res = super().href()
        idx = res.find('|')
        return res if idx < 0 else res[:idx]
    
    def loadStock(self, qtyData:list, stores:list[str], dataVersion:int = 0) :
        itemId = self.id
        for storeId in stores:
            v = {'id':storeId, 'qty':9999, 'idItem':itemId, 'dataVersion':dataVersion}
            # print("Service", v)
            qtyData.append(v)


class BundleComponents(BaseObject):
    @staticmethod
    def load(bundle:Bundle, account:Account) -> list[Self]:
        uri = bundle.components.href
        return BundleComponents.readMoySklad(uri, account)


class Stock(BaseObject):
    @staticmethod
    def uriMoySklad() -> str: return 'report/stock/bystore'

    @staticmethod
    def getCurrent(account:Account, dataVersion:int) -> list[dict[str:any]] | None :
        headers:dict[str,any] = {
            "Accept-Encoding": "gzip",
            "Authorization": "Bearer " + account.json_token,
            'Content-Type': 'application/json',
        }
        uri = BaseObject.MOY_SKLAD_HREF + 'report/stock/bystore/current'
        res = requests.get(uri, headers=headers)
        if res.status_code >= 300: return None
        jres = res.json()
        if not isinstance(jres, collections.abc.Sequence): return None

        data = []
        for el in jres:
            try:
                dest = {'idItem':el['assortmentId'], 'id':el['storeId'], 'qty':el['stock'], 'dataVersion':dataVersion}
                data.append(dest)
            except:
                pass

        return data


    def __init__(self, src:dict[str,any] = None) -> None:
        self.src = src or {}

        if src != None:
            self.stocks = []

            if 'meta' in src:
                meta = MetaObject(src['meta'])
                self.id = meta.id

            if 'stockByStore' in src:
                for si in src['stockByStore']:
                    meta = MetaObject(si['meta'])
                    qty = si['stock'] - si['reserve']
                    val = {'id':meta.id, 'qty': qty}
                    self.stocks.append(val)

class Delivery(BaseObject):
    @staticmethod
    def uriMoySklad() -> str: return 'entity/demand'

    @staticmethod
    def typeMoySklad() -> str: return 'demand'

    @staticmethod
    def uriParams() -> dict[str,str] : 
        return {
            'expand':'positions', 
            'limit':100,
        }

class Balance(BaseObject):
    @staticmethod
    def uriMoySklad() -> str: return 'report/counterparty'

    def __init__(self, src:dict[str,any] = None) -> None:
        self.src = src or {}

        if src != None and 'counterparty' in src:
            self.id = src['counterparty']['id']

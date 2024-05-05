from functools import wraps
import traceback
from typing import Self
from flask import current_app, jsonify, render_template, request
from flask_babel import gettext as _, force_locale, format_number

from flask_login import current_user, login_required
from app import db
from app.api import api
from app.email import send_email
from app.auth.models import User, Account
from app.api.error import bad_request, good_response
from datetime import datetime, timezone
from sqlalchemy import or_, select, func, and_
from app.fcgi_client import FCGIManager, get_result_data

CURRENCY_RUB = 'RUB'
CURRENCY_TENGE = 'KZT'
CURRENCY_DOLLAR = 'USD'

def login_account_required(func):
    
    @wraps(func)
    def decorated_view(*args, **kwargs):
        if not current_user.is_authenticated:
            return current_app.login_manager.unauthorized()
        if not current_user.account:
            return bad_request('havnt_account')

        return func(*args, **kwargs)

    return decorated_view


def dateToTimestamp(year:int,month:int,day:int) -> int :
    return datetime(year, month, day).replace(tzinfo=timezone.utc).timestamp()

def strToTimestamp(src:str) -> int:
    return datetime.strptime(src, '%Y%m%d').replace(tzinfo=timezone.utc).timestamp()

def nowTimestamp(notime:bool = True) -> int:
    dt = datetime.combine(datetime.now().date(), datetime.min.time())  if notime else datetime.now()
    return int(dt.replace(tzinfo=timezone.utc).timestamp())

def timestampToDate(ts:int) -> datetime:
    return datetime.fromtimestamp(ts, tz=timezone.utc)

def timestampToStrDate(ts:int) -> str:
    return datetime.fromtimestamp(ts, tz=timezone.utc).strftime('%Y%m%d')

class Bonus(db.Model):

    START_BONUS_TYPE = 0

    __tablename__ = 'bonus'

    id = db.Column(db.Integer, primary_key=True)

    #null - без ограничения
    start = db.Column(db.Integer)
    finish = db.Column(db.Integer)
    name = db.Column(db.String(100))
    type = db.Column(db.Integer)

    details = db.relationship('app.api.balance.BonusDetail', backref='bonus', lazy='noload')

    @staticmethod
    def get(type:int, currency:str) -> Self|None:
        # where type = &type and (start is null or start <= &now) ans (finish is null or finis >= &now)
        now = nowTimestamp()
        
        stmt = select(Bonus, BonusDetail) \
            .join(BonusDetail, Bonus.id == BonusDetail.bonusid) \
            .where(Bonus.type == type
                   ,BonusDetail.currency == currency
                   ,or_(Bonus.start.is_(None), Bonus.start <= now)
                   ,or_(Bonus.finish.is_(None), Bonus.finish >= now))
        
        res = db.session.execute(stmt).first()
        if not res:
            return None
        b, bd = res
        b.details.clear()
        b.details.append(bd)
        return b

    @staticmethod
    def populate() :
        b = Bonus.get(Bonus.START_BONUS_TYPE, CURRENCY_RUB)
        if not b:
            b = Bonus(type=Bonus.START_BONUS_TYPE)

            b.details.append(BonusDetail(currency=CURRENCY_RUB, sum=1500))
            b.details.append(BonusDetail(currency=CURRENCY_TENGE, sum=8500))
            b.details.append(BonusDetail(currency=CURRENCY_DOLLAR, sum=20))

            db.session.add(b)


class BonusDetail(db.Model):
    __tablename__ = 'bonus_details'

    id = db.Column(db.Integer, primary_key=True)
    bonusid = db.Column(db.Integer, db.ForeignKey('bonus.id'))

    currency = db.Column(db.String(3), nullable=False)
    sum = db.Column(db.Float, nullable=False)

    #bonus backref

class Tarif(db.Model):

    STANDARD = 'standard'
    BY_DOCS = 'bydocs'

    COUNT_BY_AGENTS = 0
    COUNT_BY_DOCS = 1

    __tablename__ = 'tarifs'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100))
    countMode = db.Column(db.Integer)

    details = db.relationship('app.api.balance.TarifDetail', backref='tarif', lazy='noload')

    def to_dict(self) -> dict[str,any]:
        detail = self.details[0]
        return {'id':self.id,'name':self.name, 'detail' : detail.to_dict()}

    @staticmethod
    def getCurrent(currency:str) -> list[Self]:
        ret : list[Self] = []

        now = nowTimestamp()
        stmt = select(Tarif, TarifDetail) \
            .join(TarifDetail, Tarif.id == TarifDetail.tarifid) \
            .where(TarifDetail.currency == currency, TarifDetail.date <= now) \
            .order_by(Tarif.id, TarifDetail.date.desc())

        curId = None
        for t, td in db.session.execute(stmt).all():
            if curId != t.id:
                if not td: continue
                t.details.append(td)
                ret.append(t)
                curId = t.id
        return ret

    @staticmethod
    def get(name:str, currency:str, date:datetime=None) -> Self|None:
        
        now = nowTimestamp() if not date else date.replace(tzinfo=timezone.utc).timestamp()
        stmt = select(Tarif, TarifDetail) \
            .join(TarifDetail, Tarif.id == TarifDetail.tarifid) \
            .where(Tarif.name == name, TarifDetail.currency == currency, TarifDetail.date <= now) \
            .order_by(TarifDetail.date.desc())

        # maxDetail = select(func.max(TarifDetail.date).label('date')) 
        #     .where(TarifDetail.currency == currency, TarifDetail.date <= now) \
        #     .subquery() \
        #     .alias('max_detail')        
        # stmt = select(Tarif, TarifDetail) \
        #     .join(TarifDetail, Tarif.id == TarifDetail.tarifid) \
        #     .join(maxDetail, TarifDetail.date == maxDetail.c.date, TarifDetail.currency == currency) \
        #     .where(Tarif.name == name)

        # print(stmt)
        res = db.session.execute(stmt).fetchall()
        if not res:
            return None
        
        b = None
        for ri in res:
            if not b: 
                b = ri[0]
                b.details.clear()
            b.details.append(ri[1])

        return b
    @staticmethod
    def getByName(name:str) -> Self|None:
        return Tarif.query.filter(Tarif.name == name).first()

    @staticmethod
    def populate():
        t = Tarif.getByName(Tarif.STANDARD)
        if not t:
            date = dateToTimestamp(2023, 1, 1)
            t = Tarif(name=Tarif.STANDARD, countMode=Tarif.COUNT_BY_AGENT)
            
            t.details.append(TarifDetail( date=date, currency = CURRENCY_RUB, cost=28))
            t.details.append(TarifDetail(date=date, currency = CURRENCY_TENGE, cost=160))
            t.details.append(TarifDetail(date=date, currency = CURRENCY_DOLLAR, cost=0.4))

            db.session.add(t)
        elif t.countMode == None:
            t.countMode = Tarif.COUNT_BY_AGENTS
        

        t = Tarif.getByName(Tarif.BY_DOCS)
        if not t:
            date = dateToTimestamp(2023, 1, 1)
            t = Tarif(name=Tarif.BY_DOCS, countMode=Tarif.COUNT_BY_DOCS)
            
            t.details.append(TarifDetail( date=date, currency = CURRENCY_RUB, cost=5))
            t.details.append(TarifDetail(date=date, currency = CURRENCY_TENGE, cost=30))
            t.details.append(TarifDetail(date=date, currency = CURRENCY_DOLLAR, cost=0.07))
            db.session.add(t)



class TarifDetail(db.Model):
    __tablename__ = 'tarif_details'

    id = db.Column(db.Integer, primary_key=True)
    tarifid = db.Column(db.Integer, db.ForeignKey('tarifs.id'))

    date = db.Column(db.Integer, nullable=False)
    currency = db.Column(db.String(3), nullable=False)
    cost = db.Column(db.Float, nullable=False)

    #tarif backref
    def to_dict(self) -> dict[str,any] :
        return {'date':timestampToStrDate(self.date),'currency':self.currency,'cost':self.cost}

class AccPayments(db.Model):
    __tablename__ = 'acc_payments'

    __table_args__ = (
        db.Index('acc_payment_idx', 'date', 'accountid'),
    )

    id = db.Column(db.Integer, primary_key=True)
    accountid = db.Column(db.Integer, db.ForeignKey('accounts.id'))

    # дата время платежа
    date = db.Column(db.Integer, nullable=False)
    
    # сумма которая добавляется к балансу
    sum = db.Column(db.Float, nullable=False)

    # валюта тарифа
    currency = db.Column(db.String(3), nullable=False)

    # сумма которую получили на р/счет
    sumbank = db.Column(db.Float, nullable=False)

    # курс по которому произвели пересчет
    rate = db.Column(db.Float, nullable=False)

    remark = db.Column(db.String(1000))

    bonusid = db.Column(db.Integer)

    # id плательщика (null или код партнера user.id)
    payerid = db.Column(db.Integer)

    @staticmethod
    def get(accountid:int, start:datetime, finish:datetime) -> dict[int,float] :
        data:dict[int,float] = {}

        start = start.replace(tzinfo=timezone.utc).timestamp()
        finish = finish.replace(tzinfo=timezone.utc).timestamp() + 24 * 3600
        stmt = select((AccPayments.date / (24 * 3600)).label('date'), func.sum(AccPayments.sum).label('sum')) \
            .filter(AccPayments.date >= start, AccPayments.date < finish, AccPayments.accountid == accountid) \
            .group_by((AccPayments.date / (24 * 3600)).label('date'))
        
        for r in db.session.execute(stmt):
            date = r['date'] * 24 * 3600
            data[date] = r['sum']
        return data

class AccTarif(db.Model):
    __tablename__ = 'acc_tarifs'
    __table_args__ = (
        db.Index('acc_tarif_idx', 'date', 'serverid'),
    )

    id = db.Column(db.Integer, primary_key=True)

    accountid = db.Column(db.Integer, db.ForeignKey('accounts.id'), index=True)
    
    serverid = db.Column(db.String(50))

    # дата перехода на тариф
    date = db.Column(db.Integer, nullable=False)
    
    tarifid = db.Column(db.Integer, db.ForeignKey('tarifs.id'))

    @staticmethod
    def addTarif(user:User, serverid:str, tarif_name:str) -> None:
        t = Tarif.getByName(tarif_name)
        if not t:
            print("Can't find tarif", tarif_name)
            return
        if not user.account:
            print("User havn't account", user.email)
            return
        
        date = nowTimestamp()
        acct = AccTarif(serverid=serverid, accountid=user.account.id, date=date, tarifid=t.id)
        db.session.add(acct)
        db.session.commit()

    @staticmethod
    def getJSON(accountid:int) -> list[dict[str,any]]:
        ret:list[dict[str,any]] = []
        now = nowTimestamp()

        acctarifs = AccTarifData(accountid)
        for srv, val in acctarifs.getLast().items():
            ret.append({'serverid':srv,'tarifd':val.id,'date':timestampToStrDate(val.date)})

        return ret

class TarifData:
    def __init__(self, currency:str) -> None:
        self.tarifs:dict[int,list[TarifDetail]] = {}
        for ti in TarifDetail.query.filter(TarifDetail.currency==currency).order_by(TarifDetail.date.desc()):
            if not ti.tarifid in self.tarifs:
                self.tarifs[ti.tarifid] = []
            self.tarifs[ti.tarifid].append(ti)

    def get(self, tarifid:int, date:int) -> TarifDetail:
        if tarifid in self.tarifs:
            # сортировка по убыванию, как находим дату <= стоп
            for ctf in self.tarifs[tarifid]:
                if ctf.date <= date: return ctf

        return None

class AccTarifData:
    def __init__(self, accountid:int) -> None:
        self.data:dict[str,list[AccTarif]] = {}
        for di in AccTarif.query.filter(AccTarif.accountid==accountid).order_by(AccTarif.date.desc()):
            if not di.serverid in self.data:
                self.data[di.serverid] = []
            self.data[di.serverid].append(di)
            # print('tarif', di.date)

    def getLast(self) -> dict[str, AccTarif]:
        ret:dict[str, AccTarif] = {}

        for srv, val in self.data.items():
            ret[srv] = val[0]
        return ret

    def get(self, serverid:str, date:int) -> AccTarif:
        if serverid in self.data:
            for sdi in self.data[serverid]:
                if sdi.date <= date: return sdi

        return None

class ServerWorkData:

    class Data :
        def __init__(self, src) -> None:
            self.agents = src['agents']
            self.docs = src['docs']

    def __init__(self, servers:list[str], start:datetime, finish:datetime) -> None:
        # serverid -> date -> agents
        self.data : dict[str, dict[int:ServerWorkData.Data]] = {}

        uri = '/call/docs_summary'
        param = {"start":start.strftime("%Y%m%d"), "finish":finish.strftime("%Y%m%d")}
        fcgm = FCGIManager.get()
        for si in servers:
            servdata = {}
            self.data[si] = servdata

            res = fcgm.send_to_server(si, uri, method='POST', post_data=param, no_wakeup = True)
            answ, data = get_result_data(res)
            if not answ.ok:
                print('No server ' + si + ' ' + answ.message)
                continue
            
            docs = data.get_list('RepData') if data else []
            if docs:
                # print('Server ', si, ' serverWokrData len',len(docs))
                for di in docs:
                    date = datetime.strptime(di['day'], '%Y%m%d%H%M%S')
                    day = date.replace(tzinfo=timezone.utc).timestamp()
                    servdata[day] = ServerWorkData.Data(di)

    def get(self, serverid:str, day:int) -> Data:
        if serverid in self.data:
            servdata = self.data[serverid]
            if day in servdata: return servdata[day]

        return None

class Balance(db.Model):
    __tablename__ = 'balance'

    __table_args__ = (
        db.Index('balance_idx', 'date', 'accountid'),
    )

    id = db.Column(db.Integer, primary_key=True)
    accountid = db.Column(db.Integer, db.ForeignKey('accounts.id'))
    date = db.Column(db.Integer, nullable=False)
    sum = db.Column(db.Float, nullable=False)

    def to_dict(self) -> dict[str,any]:
        return {'accountid':self.accountid, 'sum':self.sum, 'date':timestampToStrDate(self.date) }
    
    @staticmethod
    def get(accid:int):
        return Balance.query.filter(Balance.accountid == accid).order_by(Balance.date.desc()).first()

    @staticmethod
    def blocked_users() -> list[str]:
        res = []

        balance_max = select(Balance.accountid, func.max(Balance.date).label('date')) \
            .where(Balance.sum < 0) \
            .group_by(Balance.accountid) \
            .subquery()
        
        stmt_blnc = select(Balance) \
            .join(balance_max, and_(Balance.accountid == balance_max.c.accountid, Balance.date == balance_max.c.date)) \
            .subquery()
        
        stmt = select(Account.userid, stmt_blnc.c.sum) \
            .join(stmt_blnc, Account.id == stmt_blnc.c.accountid)
            
        for uid, _  in db.session.execute(stmt).all():
            res.append(uid) 
            
        return res

    @staticmethod
    def count(user:User) -> Self:
        accountid = user.account.id
        currency = user.account.currency

        nowts = nowTimestamp()
        balances:list[Balance] = []
        # print('nowts', nowts)

        b:Balance = Balance.query.filter(Balance.accountid==accountid, Balance.date<nowts).order_by(Balance.date.desc()).first()
        if not b:
            # print('add new balance')
            act = AccTarif.query.filter(AccTarif.accountid==accountid).order_by(AccTarif.date).first()
            b = Balance(accountid=accountid,date=act.date,sum=0)
            balances.append(b)
        else:
            pass
            # print('use exists balance ', b.date)

        start = datetime.fromtimestamp(b.date, tz=timezone.utc)
        finish = datetime.fromtimestamp(nowts, tz=timezone.utc)
        
        print('Count balance for user id', user.id, start, finish)
        # print(start, finish)

        payments = AccPayments.get(accountid, start, finish)

        finishStat = datetime.fromtimestamp(nowts - 24 * 3600, tz=timezone.utc)
        serverStat, servers = ServerStat.load(user.account, start, finishStat)

        cbalance = Balance(sum=b.sum,date=b.date,accountid=b.accountid)
        cdate = int(b.date)
        while cdate <= nowts:
            expense = 0
            if cdate in payments:
                expense += payments[cdate] 
            if cdate in serverStat:
                for si in  serverStat[cdate]:
                    expense -= si.expense
            cbalance.sum += expense
            cdate += 24 * 3600

            b = Balance(sum=cbalance.sum,date=cdate,accountid=cbalance.accountid)
            balances.append(b)
            cbalance.date = cdate

        # don't update current balance

        if len(balances) > 0:
            Balance.query.filter(Balance.date >= balances[0].date, Balance.accountid == accountid).delete()
            db.session.add_all(balances)

        db.session.commit()

        # update balance on server
        fcgm = FCGIManager.get()
        blocked = 0 if cbalance.sum >= 0 else 1
        fcgm.send_to_manager('set_blocked', {'blocked':blocked, 'userid':user.id})

        return cbalance
    
    @staticmethod
    def check_balance():
        now_time = nowTimestamp()
        seven_days = now_time - 3600 * 24 * 7
        one_day = now_time - 3600 * 24

        serv_stat = select(func.sum(ServerStat.expense).label('expense'), ServerStat.acctarifid) \
            .filter(ServerStat.date > seven_days) \
            .group_by(ServerStat.acctarifid)
        # alert only if user work today
        serv_one_stat = select(func.sum(ServerStat.expense).label('expense1'), ServerStat.acctarifid) \
            .filter(ServerStat.date >= one_day) \
            .group_by(ServerStat.acctarifid) 
        users = select(
            User.email, User.name, User.id.label('userid'), Account.currency, Account.locale,
                Account.id.label('accountid'), AccTarif.id.label('at_id')) \
                .filter(User.id == Account.userid, Account.id == AccTarif.accountid)
        balance = select(Balance.sum, Balance.accountid).filter(Balance.date > now_time)
        stmt = select(serv_stat.c.expense, users.c.email, users.c.name, users.c.locale, users.c.currency, users.c.userid, balance.c.sum) \
            .filter(serv_stat.c.acctarifid == users.c.at_id, \
                    balance.c.accountid == users.c.accountid, \
                    serv_one_stat.c.acctarifid == users.c.at_id, \
                    serv_one_stat.c.expense1 > 0)
        stmt = stmt.filter(serv_stat.c.expense >= balance.c.sum)
        print(':date1', seven_days, ':date2', one_day, ':date3', now_time)
        print('stmt',stmt)
        # return
    
        have_alerts = False
        info_text = 'Balance alerts\n'
        for el in db.session.execute(stmt).all():
            expense = el._mapping['expense']
            email = el._mapping['email']
            name = el._mapping['name']
            currency = el._mapping['currency']
            userid = el._mapping['userid']
            sum = el._mapping['sum']
            locale = el._mapping['locale'] or 'ru'

            have_alerts = True

            info_text += f"Email: {email}, User: {name}, currency: {currency}, week expence: {expense}, balance: {sum}\n"

            with force_locale(locale):
                body = render_template('api/balance_alert.txt', name=name, sum=format_number(sum))
                title = _('AceTeam Balance Alert')

                send_email(title
                           ,[email]
                           ,text_body=body
                           ,html_body=''
                )
                # print(body)

        # print(info_text)
        if have_alerts:
            info_addr = 'info@grsoft.ru'
            send_email('Balance alert'
                ,[info_addr]
                ,text_body=info_text
                ,html_body=''
            )


class ServerStat(db.Model):
    __tablename__ = 'server_stats'

    __table_args__ = (
        db.Index('serv_stat_idx', 'date', 'serverid'),
    )

    id = db.Column(db.Integer, primary_key=True)
    serverid = db.Column(db.String(50))
    date = db.Column(db.Integer, nullable=False)
    agents = db.Column(db.Integer)
    docs = db.Column(db.Integer)
    expense = db.Column(db.Float)

    acctarifid = db.Column(db.Integer, db.ForeignKey('acc_tarifs.id'))

    def to_dict(self) -> dict[str, any]:
        return {'serverid':self.serverid,'date':timestampToStrDate(self.date),'agents':self.agents,'expense':self.expense}

    @staticmethod
    def get(servers:list[str], start:datetime, finish:datetime) -> list[Self]:
        start = start.replace(tzinfo=timezone.utc).timestamp()
        finish = finish.replace(tzinfo=timezone.utc).timestamp() + 24 * 3600
        return ServerStat.query.filter(ServerStat.serverid.in_(servers), ServerStat.date >= start, ServerStat.date < finish).all()
    
    @staticmethod
    def load(a:Account, start:datetime, finish:datetime) -> tuple[dict[int,list[Self]], list[str]]:

        print('Load server data for accid', a.id, start, finish)

        acctarifs = AccTarifData(a.id)
        ret:dict[int,list[Self]] = {}
        servers:list[str] = list(acctarifs.data.keys())

        if start > finish: return (ret, servers)

        finishVal = finish.replace(tzinfo=timezone.utc).timestamp()
        stVal = start.replace(tzinfo=timezone.utc).timestamp()
        data = ServerStat.get(servers, start, finish)
        
        lastDate : dict[str,int] = {}
        for si in servers: lastDate[si] = 0

        # print("Load data", len(data))
        for di in data:
            cdate = int(di.date)
            serverid = di.serverid

            if not serverid in lastDate: lastDate[serverid] = cdate
            elif lastDate[serverid] < cdate: lastDate[serverid] = cdate
            
            if not cdate in ret: ret[cdate] = []
            ret[cdate].append(di)    

        # print("Check servers ", len(lastDate))
        tarifs = None
        tarif: Tarif = Tarif.getByName(Tarif.STANDARD)

        for serverid, date in lastDate.items():
            if date == 0: date = stVal
            else: date += 24 * 3600

            if date <= finishVal:
                ServerStat.query.filter(ServerStat.serverid==serverid, ServerStat.date >= date).delete()

                cdate = datetime.fromtimestamp(date, tz=timezone.utc)
                print('Query data from server id', serverid, cdate, finish)

                if not tarifs:tarifs = TarifData(a.currency)
                serverdata = ServerWorkData([serverid], cdate, finish)
                # if len(serverdata.data) > 0: print(serverdata.data)

                while date <= finishVal:
                    sdata = serverdata.get(serverid, date)
                    # if len(serverdata.data) > 0 and sdata == None:
                    #     print(date, serverdata.data)
                    #     break
                    if sdata:
                        # print('get stat')
                        atrf = acctarifs.get(serverid, date)
                        
                        if tarif.id != atrf.tarifid:
                            tarif = db.session.get(Tarif, atrf.tarifid)

                        trf = tarifs.get(atrf.tarifid, date)

                        cexp = sdata.agents * trf.cost if tarif.countMode == Tarif.COUNT_BY_AGENTS else sdata.docs * trf.cost

                        stat = ServerStat(serverid=serverid
                                          , date=date
                                          , agents=sdata.agents
                                          , docs = sdata.docs
                                          , expense=cexp
                                          , acctarifid=atrf.id)
                        
                        if not date in ret: ret[date] = []
                        ret[date].append(stat)
                        db.session.add(stat)

                    date += 24 * 3600

        db.session.commit()
        return (ret, servers)


def onAccountCreated(user:User):
    # add bonus & tarif for servers
    try:
        now =  nowTimestamp()
        a = user.account

        b = Bonus.get(Bonus.START_BONUS_TYPE, a.currency)
        accPay = AccPayments(accountid=a.id, date=now, sum=b.details[0].sum, currency=a.currency,sumbank=0, rate=1,bonusid=b.id)
        db.session.add(accPay)

        t = Tarif.get(Tarif.STANDARD, a.currency)

        fcgm = FCGIManager.get()
        srvs = fcgm.send_to_manager('server_list', {'userid':user.id})
        answ, data = get_result_data(srvs)
        if answ.ok:
            servers = data.get_list('ServersList')
            for si in servers:
                accTarif = AccTarif(accountid = a.id, date=now, tarifid = t.id, serverid=si['code'])
                db.session.add(accTarif)
            
            Balance.count(user)
    except:
        traceback.print_exc()

@api.route('/account', methods=['GET','POST'])
@login_required
def account_api():
    if request.method == 'POST':
        if current_user.account:
            return bad_request('account_already_exists')
        a = Account.from_dict(request.json)
        if not a:
            return bad_request('error_in_data')
        current_user.account = a
        
        onAccountCreated(current_user)
        db.session.commit()    

    objToSend = current_user.account.to_dict() if current_user.account else {}
    return good_response('Account', objToSend)


@api.route('/balance', methods=['GET'])
@login_account_required
def acountBalance():
    cb:Balance = Balance.get(current_user.account.id)
    objToSend = cb.to_dict() if cb else {"sum":0.0, "date": nowTimestamp() }

    if 'start' in request.args and 'finish' in request.args:
        accountid = current_user.account.id
        acctarifs = AccTarifData(accountid)
        servers:list[str] = list(acctarifs.data.keys())

        start = datetime.strptime(request.args['start'], '%Y%m%d')
        finish = datetime.strptime(request.args['finish'], '%Y%m%d')
        now = datetime.combine(datetime.now().date(), datetime.min.time())
        if finish > now: finish = now

        pays = AccPayments.get(accountid, start, finish)

        plist = []
        for day, sum in pays.items():
            plist.append({'date':timestampToStrDate(day),'sum':sum})
        objToSend['payments'] = plist
        slist = []
        for sl in ServerStat.get(servers,start, finish):
            slist.append(sl.to_dict())
        objToSend['servers'] = slist

    return good_response('Balance', objToSend)

@api.route('/tarifs', methods=['GET'])
@login_account_required
def getTarifs() :
    tarifs = []
    for ti in Tarif.getCurrent(current_user.account.currency):
        tarifs.append(ti.to_dict())

    return good_response('Tarif', tarifs)

@api.route('/tarif', methods=['GET','POST'])
@login_account_required
def manageTarif() :
    accountid = current_user.account.id
    if request.method == 'GET':
        data = AccTarif.getJSON(accountid)
        return good_response('AccTarif', data)

    if request.method == 'POST':
        
        jsdata = request.json
        if not 'date' in jsdata or not 'serverid' in jsdata or not 'tarif' in jsdata:
            return bad_request('bad_data')
        
        newdate = strToTimestamp(jsdata['date'])
        serverid = jsdata['serverid']
        tarifName = jsdata['tarif']
        tarif = Tarif.getByName(tarifName)
        if not tarif:
            return bad_request('no_tarif')

        acct = AccTarif(accountid=accountid, serverid=serverid,date=newdate,tarifid=tarif.id)
        needUpdate = True

        acctarifs = AccTarifData(accountid)
        lastTarifs = acctarifs.getLast()
        if serverid in lastTarifs:
            ct = lastTarifs[serverid]
            if ct.tarifid == tarif.id or ct.date > newdate:
                needUpdate = False
        if needUpdate :
            db.session.add(acct)
            db.session.commit()

    return good_response()

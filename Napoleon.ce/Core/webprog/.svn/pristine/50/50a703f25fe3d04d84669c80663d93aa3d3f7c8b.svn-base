from typing import Self
from app import db
from app.api import api
from app.api.error import error_response, good_response
from app.api.server_auth import server_login, current_server
from flask import request
from flask_babel import gettext as _
from time import time
import random
from app.fcgi_client import FCGIManager
import uuid
import json

@api.route('/req_connect', methods=['POST', 'GET', 'DELETE'])
@server_login
def req_connect():
    def read_data():
        data = request.get_json() or {}

        if not 'id' in data or not 'type' in data:
            error_response(400, _("req connect haven't req fields"))
            return (None, None)

        return (data['id'], data['type'])

    server_id = current_server.get()
    if request.method == 'POST' or request.method == 'DELETE':
        (userid, user_type) = read_data()
        if not userid: return

        if request.method == 'POST':
            rc = ReqConnect.new_code(server_code=server_id, user_type=user_type, userid=userid)
            scode = repr(rc.id).zfill(ReqConnect.MAX_CODE_LEN)
            return good_response('ReqConnect', {'code':scode, 'till':rc.valid_till})
        else:
            ReqConnect.remove_request(server_code=server_id,user_type=user_type,userid=userid)
            return good_response('ReqConnect', {'code':'', 'till':0, 'id':userid,'type':user_type})
    else:
        ReqConnect.remove_expired()
        
        data = []
        rows = db.session.execute(db.select(ReqConnect).filter_by(server_code=server_id)).scalars()
        for r in rows:
            scode = repr(r.id).zfill(ReqConnect.MAX_CODE_LEN)
            data.append({'id':r.userid, 'type':r.user_type, 'code':scode, 'till':r.valid_till})
    return good_response('ReqConnect', data)

@api.route('/link_user')
def link_user():
    user_code = request.args.get('code')
    user_type = request.args.get('type')
    now = int(time())

    rc = db.session.execute(db.select(ReqConnect).filter_by(id=user_code)).scalar()
    if not rc or rc.user_type != user_type or rc.valid_till < now:
        return error_response(400, _('no valid code'))

    uid = uuid.uuid4().hex
    filter = '"id"=\'%s\'' % rc.userid
    body = json.dumps([{'name':'LinkedUsers', 'where':filter, 'data':[{'id':rc.userid, 'uuid':uid}]}])
    fcgm = FCGIManager.get()
    answ = fcgm.send_to_server(rc.server_code, '/object/LinkedUsers', 'PUT', body)

    # headers = {
    #     'SERVER_CODE': rc.server_code
    # }
    # answ = send_to_server(headers, '/object/LinkedUsers', 'PUT', body)
    # i need test is the records inserted
    if b'Status: 200 OK' in answ:
        
        db.session.delete(rc)
        db.session.commit()

        data = {'id':rc.userid, 'code':uid, 'server_code':rc.server_code}
        return good_response('LinkedUsers', data)
    return answ


class ReqConnect(db.Model):
    MAX_CODE_LEN = 4
    VALID_TILL = 24 * 3600

    __tablename__ = 'req_connect'

    id = db.Column(db.Integer, primary_key=True)
    userid = db.Column(db.String(120))
    server_code = db.Column(db.String(120))
    user_type = db.Column(db.String(120))
    valid_till = db.Column(db.Integer)

    @staticmethod
    def new_value(len:int) -> int:
        res = 0
        random.seed()
        for i in range(0, len):
            res = res * 10 + random.randint(0, 9)

        return res

    @staticmethod
    def remove_request(server_code:str, userid:str, user_type:str):
        stmt = db.delete(ReqConnect).where(ReqConnect.userid==userid and ReqConnect.server_code==server_code and ReqConnect.user_type==user_type)
        db.session.execute(stmt)
        db.session.commit()

    @staticmethod
    def remove_expired():
        now = int(time())
        # table = ReqConnect.__table__
        stmt = db.delete(ReqConnect).where(ReqConnect.valid_till < now)

        db.session.execute(stmt)
        db.session.commit()


    @staticmethod
    def new_code(server_code:str, userid:str, user_type:str) -> Self:
        ReqConnect.remove_expired()

        code = 0
        while True:
            code = ReqConnect.new_value(ReqConnect.MAX_CODE_LEN)
            res = ReqConnect.query.filter(ReqConnect.id == code).first()
            if not res: break

        valid_till = int(time())+ReqConnect.VALID_TILL
        rc = ReqConnect(id=code, userid=userid, user_type=user_type, server_code=server_code, valid_till=valid_till)
        db.session.add(rc)

        db.session.commit()
        return rc
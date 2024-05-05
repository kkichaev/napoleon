from functools import wraps
import os
import traceback
from typing import Any
from flask import Blueprint, Response, current_app, make_response, request
import jwt
from time import time

import requests
from app.moysklad.models import Account, AccLog
from app import db

endpoints = Blueprint('endpoints', __name__, template_folder='templates')

# BASE_ENDPOINT = 'https://online.moysklad.ru/api/vendor/1.0'
# JSON_ENDPOINT = 'https://online.moysklad.ru/api/remap/1.2'
BASE_ENDPOINT = 'https://apps-api.moysklad.ru/api/vendor/1.0'
JSON_ENDPOINT = 'https://api.moysklad.ru/api/remap/1.2'

jti:list[str] = []

def check_token(func):
    @wraps(func)
    def decorated_view(*args, **kwargs):
        token = None
        for k, v in request.environ.items():
            if k.upper() == 'HTTP_AUTHORIZATION':
                check = str(v[0:7]).lower()
                if check == 'bearer ':
                    token = v[7:]
                    break

        if not token :
            return make_response('', 401)
        
        # print('token', token)
        data = jwt.decode(token, current_app.config['MOY_SKLAD_SK'], algorithms=["HS256"])
        if not data or not 'jti' in data or data['jti'] in jti:
            return make_response('', 401)
        
        jti.append(data['jti'])
        if len(jti) > 300:
            jti.pop(0)
        return func(*args, **kwargs)

    return decorated_view

def make_token(version:int):
    key = 'MOY_SKLAD_V2_APPUID' if version == 2 else 'MOY_SKLAD_APPUID'

    data = { 
        "sub": current_app.config[key],
        "iat" : time(),
        "exp" : time() + 300,
        "jti" : ''.join('{:02x}'.format(x) for x in (os.urandom(32)))
    }
    token = jwt.encode(data, current_app.config['MOY_SKLAD_SK'], algorithm="HS256")
    return token


@endpoints.route('apps/<appId>/<accountId>', methods=['PUT', 'DELETE', 'GET'])
@check_token
def appActivationHandler(appId:str=None, accountId:str=None):
    version = 2 if appId == current_app.config['MOY_SKLAD_V2_ID'] else 1

    def make_answer(answer:dict[str,Any]) -> Response:
        response = make_response(answer)
        response.headers['Authorization'] = 'Bearer ' + make_token(version)
        return response
    
    answer = {}
    if request.method == 'PUT':
        jsonToken = request.json['access'][0]['access_token']
        user:Account = Account.get(accountId)
        if not user:
            user = Account(accid=accountId, json_token=jsonToken)
            db.session.add(user)
        else:
            user.json_token = jsonToken
        answer['status'] = 'SettingsRequired' if not user.srv_token else 'Activated'

        AccLog.add(accountId, 'activated')
        db.session.commit()
    elif request.method == 'DELETE':
        user:Account = Account.get(accountId)
        # if user:
        #     db.session.delete(user)
        AccLog.add(accountId, 'deleted')
        db.session.commit()
    else:
        user:Account = Account.get(accountId)
        if not user:
            return make_response('', 401)

        answer['status'] = 'SettingsRequired' if not user.srv_token else 'Activated'
    return make_answer(answer)

def getAppStatus(account:Account, version:int) -> str:
    try:
        token = make_token(version)
        headers = {'Content-Type':'application/json'
                   ,'Accept':'application/json'
                   ,'Accept-Encoding':'gzip'
                   ,'Authorization':'Bearer ' + token}
        appId = current_app.config['MOY_SKLAD_ID']

        url = '{0}/apps/{1}/{2}/status'.format(BASE_ENDPOINT, appId, account.accid)

        # print(url)
        r = requests.get(url, headers=headers)
        print('getStatus',r.status_code, r.content.decode('utf-8'))
    except:
        traceback.print_exc()

def setAppStatus(status:str, account:Account, version:int) -> None:
    try:
        token = make_token(version)
        headers = {'Content-Type':'application/json'
                   ,'Accept':'application/json'
                   ,'Accept-Encoding':'gzip'
                   ,'Authorization':'Bearer ' + token}

        appId = current_app.config['MOY_SKLAD_ID']

        url = '{0}/apps/{1}/{2}/status'.format(BASE_ENDPOINT, appId, account.accid)

        data = {'status':status}
        # print(url)
        # print('setStatus', data)
        r = requests.put(url, headers=headers, json=data)
        print('setStatus',r.status_code, r.content.decode('utf-8'))
        return r
    except:
        traceback.print_exc()

def getUserContext(contextKey:str, version:int) ->dict[str,Any]:
    token = make_token(version)
    headers = {
        'Content-Type':'application/json'
        ,'Accept-Encoding':'gzip'
        ,'Authorization':'Bearer ' + token}

    url = '{0}/context/{1}'.format(BASE_ENDPOINT, contextKey)

    # print('getUserContext', url)
    res = requests.post(url, headers=headers)
    try:
        return res.json()
    except:
        return None

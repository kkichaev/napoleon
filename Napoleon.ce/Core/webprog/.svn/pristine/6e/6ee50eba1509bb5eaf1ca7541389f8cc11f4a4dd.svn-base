import base64
from app.auth.models import User
from app.token import create_token, CREATE_USER_COMMAND

from app.fcgi_client import get_result_data

import json


# def test_create_user(client):
#     # r = client.get('/auth/cnf_email/1332', headers = {'Accept':'text/html'})
#     # assert b'error in token data' in r.data
#     # r = client.get('/auth/cnf_email/1332', headers = {'Accept':'application/json'})
#     # assert b'error in token data' in r.data

#     user_dic = {'name':'test', 'surname':'test', 'email':'mosden@gmail.com', 'password':'123'}
#     token = create_token(user_dic, CREATE_USER_COMMAND)
#     r = client.get('/auth/cnf_email/' + token, headers = {'Accept':'application/json'})
#     assert b'"response":1' in  r.data

#     (answ, data) = get_result_data(r.data)
#     assert answ.ok

#     code = data.get('ServerCode', 'code')
#     assert code


# def test_token(client):
#     pwd = base64.b64encode('t@t.com:test'.encode('utf-8')).decode('utf-8')
#     headers = {
#         'Accept':'application/json',
#         'Authorization':'Basic ' + pwd,
#     }
#     r = client.get('/auth/server_token?server_code=f0f580af4f74fce5', headers=headers)
#     print(r.data)    
#     assert b'"response":1' in r.data
    
#     (_, data) = get_result_data(r.data)
#     token = data.get('Token', 'token')

#     r = client.get('/auth/server_token?server_code=f0f580af4f74fce5&new=1', headers=headers)
#     assert b'"response":1' in r.data

#     (_, data) = get_result_data(r.data)
#     token2 = data.get('Token', 'token')
#     assert token != token2

def test_req_code(client):
    headers = {
        'Accept':'application/json',
        'Authorization':'Bearer ' + "0",
    }
    r = client.post('/api/req_connect'
            ,json={'id':'1','type':'Agents'}
            ,headers=headers)

    # print(r.data)
    assert b'"response":1' in r.data
    # assert b'test' in r.data

    r = client.get('/api/req_connect', headers=headers)
    assert b'"response":1' in r.data

    data = json.loads(r.data)
    code = '0000'

    for v in data :
        if v['name'] == 'ReqConnect':
            for el in v['data']:
                code = el['code']
                break

    r = client.get('/api/bind_user?code=%s&type=Agents' % code, headers=headers)
    assert b'"response":0' in r.data

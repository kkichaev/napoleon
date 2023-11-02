import uuid
from app.api import api
from flask import request
from app.api.error import error_response
from app.fcgi_client import FCGIManager, get_result_data
from flask_login import login_required, current_user
from app.api.server_auth import server_login, current_server
from app.api.balance import Balance

@api.route('/server',methods=['GET','POST'])
@server_login
def server_info() :
    fcgm = FCGIManager.get()
    if request.method == 'GET':
        return fcgm.send_to_manager('server_info', {'code':current_server.get()})
    
    if request.json and 'name' in request.json:
        param = {'code':current_server.get(), 'name':request.json['name']}
        return fcgm.send_to_manager('update_server', param)
    return error_response(400, 'no_server_name')
    # return send('server_info', {'code':current_server.get()})

@api.route('/connection')
@server_login
def connection_info() :
    fcgm = FCGIManager.get()
    blocked = Balance.blocked_users()
    # print('Blocked', blocked)
    return fcgm.send_to_manager('connection_info', {'code':current_server.get(), 'blocked':blocked})
    # return send('connection_info', {'code':current_server.get()})

@api.route('/servers')
@login_required
def server_list():
    fcgm = FCGIManager.get()
    return fcgm.send_to_manager('server_list', {'userid':current_user.id})
    # params = {'userid':current_user.id}
    # return send('server_list', params)
    

@api.route('/token')
@login_required
def get_server_token():
    server_code = request.args.get('server_code')

    make_new = request.args.get('new') or False    
    params = {'code' : server_code, "new" : make_new, 'userid':current_user.id}
    
    fcgm = FCGIManager.get()
    return fcgm.send_to_manager('token', params)
    # return send('token', params)


def createServer(userid:str, servername:str='') -> any:
    params = {'userid' : userid, 'name':servername}
    
    fcgm = FCGIManager.get()
    res = fcgm.send_to_manager('create_server', params)

    answ, data = get_result_data(res)
    if answ.ok and data:
        code = data.get('ServerCode', 'code')
        if code:
            mgr_id = str(uuid.uuid4()).replace('-','')
            data = [
                {'name':'Division','data':[{"id":1,"name":servername}]},
                {'name':'DivisionManager','data':[{"id":mgr_id,"name":servername,'division':1}]},
            ]
            cr_res = fcgm.send_to_server(code, '/object', 'POST', data)
            print('Create server',cr_res,code)    
    
    return res

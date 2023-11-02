import base64
from app.auth.models import User
from app.token import decode_token
from app.api.servers import createServer
from flask import request, Request, jsonify
from app.api.error import bad_request, unauthorized
from app.auth import auth
from app import db
from flask_babel import gettext as _
from app import login
from flask_login import current_user, login_required, login_user, logout_user

def wants_json_response():
    return request.accept_mimetypes['application/json'] >= request.accept_mimetypes['text/html']

@login.user_loader
def load_user(id) -> User:
    return User.query.get(int(id))

@login.request_loader
def auth_from_request(request:Request) -> None|User:
    # # first, try to login using the api_key url arg
    # api_key = request.args.get('api_key')
    # if api_key:
    #     user = User.query.filter_by(api_key=api_key).first()
    #     if user:
    #         return user

    # next, try to login using Basic Auth
    api_key = request.headers.get('Authorization')
    if api_key and api_key.find('Basic') >= 0:
        api_key = api_key.replace('Basic ', '', 1)
        try:
            api_key = base64.b64decode(api_key)
        except TypeError:
            pass
        user_data = api_key.decode('utf-8').split(':')
        if len(user_data) > 1:
            user = User.from_email(user_data[0])
            if user and user.check_password(user_data[1]):
                return user

    return None

@auth.route('/logout', methods=['GET'])
@login_required
def user_do_logout():
    logout_user()

    return jsonify({})


@auth.route('/login', methods=['POST'])
def user_do_login():
    data = request.get_json() or {}
    if not 'email' in data and not 'password' in data:
        return unauthorized('no_user')
    
    u = User.from_email(data['email'])
    if not u or not u.check_password(data['password']):
        return unauthorized('no_user')
    
    remember =  data['remember'] if 'remember' in data else False
    login_user(u, remember)

    return jsonify(u.to_dict())

@auth.route('/user', methods=['GET','POST'])
@login_required
def get_user():
    if request.method == 'GET':
        return jsonify(current_user.to_dict())
    if request.json:
        current_user.update(request.json)
        db.session.commit()
    return jsonify(current_user.to_dict())


# def createServer(userid:str, servername:str='') -> any:
#     params = {'userid' : userid, 'name':servername}
    
#     fcgm = FCGIManager.get()
#     res = fcgm.send_to_manager('create_server', params)

#     answ, data = get_result_data(res)
#     if answ.ok and data:
#         code = data.get('ServerCode', 'code')
#         if code:
#             mgr_id = str(uuid.uuid4()).replace('-','')
#             data = [
#                 {'name':'Division','data':[{"id":1,"name":servername}]},
#                 {'name':'DivisionManager','data':[{"id":mgr_id,"name":servername,'division':1}]},
#             ]
#             cr_res = fcgm.send_to_server(code, '/object', 'POST', data)
#             print('Create server',cr_res,code)    
    
#     return res


@auth.route('/cnf_email/<token>')
def handle_create_user(token:str):
    data = decode_token(token)
    if not data:
        return bad_request('error_in_token_data')
        # if wants_json_response(): 
        #     return error_response(400, _('error in token data'))
        # return render_template('auth/user_create_error.html', error = _('error in token data'))
    
    u = User.from_dic(data)
    db.session.add(u)
    db.session.commit()

    servername = ""
    if 'servername' in data:
        servername = data['servername']

    res = createServer(u.id, servername)

    # params = {'userid' : u.id}
    # servername = ""
    # if 'servername' in data:
    #     servername = data['servername']
    #     params['name'] = servername
    
    # fcgm = FCGIManager.get()
    # res = fcgm.send_to_manager('create_server', params)

    # answ, data = get_result_data(res)
    # if answ.ok and data:
    #     code = data.get('ServerCode', 'code')
    #     if code:
    #         mgr_id = str(uuid.uuid4()).replace('-','')
    #         data = [
    #             {'name':'Division','data':[{"id":1,"name":servername}]},
    #             {'name':'DivisionManager','data':[{"id":mgr_id,"name":servername,'division':1}]},
    #         ]
    #         cr_res = fcgm.send_to_server(code, '/object', 'POST', data)
    #         print('Create server',cr_res,code)

    login_user(u, True)
    return res

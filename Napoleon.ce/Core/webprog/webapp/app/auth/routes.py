import base64
import time
import traceback
from app.auth.models import User, Token
from app.token import decode_token, create_token, CLEAR_PASSWORD_COMMAND
from app.api.servers import createServer
from app.fcgi_client import get_result_data
from flask import current_app, make_response, render_template, request, Request, jsonify
from app.api.error import bad_request, unauthorized, good_response
from app.auth import auth
from app import db
from flask_babel import gettext as _
from app import login
from flask_login import current_user, login_required, login_user, logout_user
from flask_babel import gettext as _, force_locale
from app.email import send_email

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

@auth.route('/new_pwd', methods=['post'])
@login_required
def change_password():
    data = request.get_json() or {}
    if not 'password' in data :
        return bad_request('bad_request')
    
    current_user.set_password(data['password'])
    db.session.commit()
    return good_response()


TOKEN_EXPIRE = 24 * 3600
@auth.route('/check_clr_email/<token>', methods=['GET'])
def handle_clear_email(token:str):
    data = decode_token(token)
    if not data or not 'id' in data or not 'handle' in data:
        return bad_request('error_in_token_data')
    
    handle = data['handle']
    token = Token.get(handle, TOKEN_EXPIRE)
    if token != None:
        return bad_request('used_token')
        
    u = db.session.get(User, data['id'])
    if not u:
        return bad_request('no_user')

    login_user(u, True)
    return good_response()

@auth.route('/clr_pwd', methods=['POST'])
def clear_password():
    data = request.get_json() or {}
    if not 'email' in data or not 'locale' in data:
        return good_response()
    
    email:str = data['email']
    locale:str = data['locale']
    u = User.from_email(email)
    if not u:
        return good_response()

    handle = "%X" % int(time.time())
    token = create_token({'id':u.id, 'handle':handle}, CLEAR_PASSWORD_COMMAND, TOKEN_EXPIRE)
    
    if current_app.config['TESTING'] :
        return current_app.url_for('auth.handle_clear_email', token=token, _external=True) + "\n"

    if not request.origin or len(request.origin) == 0 :
        return bad_request('no_origin')

    hs = request.origin.split(':')
    host = hs[0] + ':' + hs[1]
    url = host + "/clr_pwd?token=" + token

    cur_locale = 'en' if len(locale) == 0 else locale.split('-')[0]
    with force_locale(cur_locale) :
        title = _("Request password reset")
        # print(title)
        text_body = render_template('auth/clr_pwd_email.txt', user=u, url=url)
        # print(text_body)
        html_body = render_template('auth/clr_pwd_email.html', user=u, url=url)
        # print(html_body)

    # print(html_body)
    send_email(title
        ,[email] 
        ,text_body=text_body
        ,html_body=html_body
    )

    return good_response()
    

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

@auth.route('/locale', methods=['GET','POST'])
@login_required
def set_locale():
    if request.method == 'GET':
        locale = current_user.account.locale if hasattr(current_user, 'account') else ''
        return jsonify({'locale':locale})
    
    if request.json and 'locale' in request.json and hasattr(current_user, 'account'):
        current_user.account.locale = request.json['locale']
        db.session.commit()
    
    return good_response()


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

import secrets

@auth.route('/create_user_server', methods=['POST'])
def create_user_and_server():
    try:
        data = request.get_json() or {}
        if not 'email' in data or not 'name' in data:
            return bad_request('must_include_email_and_name')

        email = data['email']
        u = User.from_email(email)
        if u:
            return make_response('User already exists', 500)
        
        data['surname'] = ''
        pwd = secrets.token_urlsafe(12)
        data['password'] = pwd
        
        u = User.from_dic(data)


        # cur_locale = 'ru' if not 'locale' in data else data['locale'].split('-')[0]
        # with force_locale(cur_locale) :
        #     title = _("The AceTeam account created")
        #     # print(title)
        #     text_body = render_template('auth/password_email.txt', user=data)
        #     # print(text_body)
        #     html_body = render_template('auth/password_email.html', user=data)
        #     # print(html_body)

        # # print(html_body)
        # send_email(title
        #     ,[email] 
        #     ,text_body=text_body
        #     ,html_body=html_body
        # )
        # return jsonify({'server_code': '1234'})

        db.session.add(u)
        db.session.commit()

        servername = 'Server'
        res = createServer(u.id, servername)
    
        cur_locale = 'ru' if not 'locale' in data else data['locale'].split('-')[0]
        answ, data = get_result_data(res)
        if answ.ok and data:
            code = data.get('ServerCode', 'code')

            with force_locale(cur_locale) :
                title = _("The AceTeam account created")
                # print(title)
                text_body = render_template('auth/password_email.txt', user=data, pwd=pwd)
                # print(text_body)
                html_body = render_template('auth/password_email.html', user=data, pwd=pwd)
                # print(html_body)

            # print(html_body)
            send_email(title
                ,[email] 
                ,text_body=text_body
                ,html_body=html_body
            )

            return jsonify({'server_code': code})
        else:
            return make_response(answ.message, 500)

    except:
        msg = traceback.format_exc()
        return make_response(msg, 500)

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

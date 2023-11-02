from app.api import api
from app import db
from flask import request, current_app, render_template, jsonify
from flask_babel import gettext as _, force_locale

from app.api.error import bad_request, good_response
from app.email import send_email
from app.token import create_token, CREATE_USER_COMMAND
from app.auth.models import User

@api.route('/users', methods=['POST'])
def create_user():
    data = request.get_json() or {}
    if not 'email' in data or not 'name' in data:
        return bad_request('must_include_email_and_name')

    user = db.session.execute(db.select(User).filter_by(email = data['email'])).scalar()
    if user != None :
        return bad_request('email_already_exists')
    token = create_token(data, CREATE_USER_COMMAND, 3600)

    if current_app.config['TESTING'] :
        return current_app.url_for('handle_create_user', token=token, _external=True) + "\n"

    if not request.origin or len(request.origin) == 0 :
        return bad_request('no_origin')

    hs = request.origin.split(':')
    host = hs[0] + ':' + hs[1]
    url = host + "/confirm?token=" + token

    cur_locale = 'en' if not 'locale' in data else data['locale'].split('-')[0]
    with force_locale(cur_locale) :
        title = _("EMail confirmation")
        # print(title)
        text_body = render_template('api/confirm_email.txt', user=data, url=url)
        # print(text_body)
        html_body = render_template('api/confirm_email.html', user=data, url=url)
        # print(html_body)

    # print(html_body)
    email = data['email']
    send_email(title
        ,[email] 
        ,text_body=text_body
        ,html_body=html_body
    )

    return good_response()

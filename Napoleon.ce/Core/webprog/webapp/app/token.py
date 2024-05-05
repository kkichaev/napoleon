from time import time
import jwt

from flask import current_app

CREATE_USER_COMMAND = 'create_user'
CLEAR_PASSWORD_COMMAND = 'clear_password'

def create_token(userdata, command, expires_in = 600) -> str:
    userdata['command'] = command
    userdata['exp'] = time() + expires_in

    return jwt.encode(userdata, current_app.config['SECRET_KEY'], algorithm="HS256")

def decode_token(token: str):
    try:
        res = jwt.decode(token, current_app.config['SECRET_KEY'], algorithms=["HS256"])
        return res
    except:
        return None

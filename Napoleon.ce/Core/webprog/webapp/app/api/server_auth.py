from contextvars import ContextVar
from functools import wraps
from flask import request
from app.api.error import error_response
from flask_babel import gettext as _

current_server:ContextVar[str] = ContextVar("current_server")

def server_login(func):

    @wraps(func)
    def decorated_view(*args, **kwargs):

        # raise('test')

        cs = None
        for k, v in request.environ.items():
            if k.upper() == 'HTTP_AUTHORIZATION':
                v = str(v).lower()
                if v.startswith('bearer '): 
                    cs = v[7:]
                    break

        if not cs :
            return error_response(401, _('No server code'))
        
        current_server.set(cs)
        return func(*args, **kwargs)

    return decorated_view


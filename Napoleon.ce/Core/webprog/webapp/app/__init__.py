from flask import Flask
from config import Config
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_login import LoginManager, user_loaded_from_request, login_manager
from flask_mail import Mail
from flask_babel import Babel
from flask.sessions import SecureCookieSessionInterface
from flask import g, request, url_for, redirect, abort
from http import HTTPStatus

PROG_VERSION = '1.0.1'

db = SQLAlchemy()
migrate = Migrate()
login = LoginManager()
mail = Mail()
babel = Babel()

@babel.localeselector
def get_locale():
    # if a user is logged in, use the locale from the user settings
    user = getattr(g, 'user', None)
    if user is not None:
        return user.locale
    # otherwise try to guess the language from the user accept
    # header the browser transmits.  We support de/fr/en in this
    # example.  The best match wins.
    try:
        return request.accept_languages.best_match(['ru', 'en'])
    except:
        return 'ru'

@babel.timezoneselector
def get_timezone():
    user = getattr(g, 'user', None)
    if user is not None:
        return user.timezone

@user_loaded_from_request.connect
def user_loaded_from_request(app, user=None):
    g.login_via_request = True

@login.unauthorized_handler
def unauthorized():
    abort(HTTPStatus.UNAUTHORIZED)
    # if request.blueprint == 'api':
    #     abort(HTTPStatus.UNAUTHORIZED)
    # return redirect(url_for('auth.login'))

class CustomSessionInterface(SecureCookieSessionInterface):
    """Prevent creating session from API requests."""
    def save_session(self, *args, **kwargs):
        if g.get('login_via_request'):
            return
        return super(CustomSessionInterface, self).save_session(*args, **kwargs)

def create_app(config = Config):
    print('Create app version:', PROG_VERSION)

    app = Flask(__name__)
    app.config.from_object(config)

    db.init_app(app)
    migrate.init_app(app, db)
    login.init_app(app)
    mail.init_app(app)
    babel.init_app(app)

    app.register_blueprint(moysklad, url_prefix='/moysklad')
    app.register_blueprint(api, url_prefix='/api')
    app.register_blueprint(auth, url_prefix='/auth')
    app.register_blueprint(main)

    app.session_interface = CustomSessionInterface()

    return app

from app.main import bp as main
from app.auth import auth
from app.api import api
from app.moysklad import moysklad


from flask import Blueprint

api = Blueprint('api', __name__, template_folder='templates')

from app.api import users, servers, req_connects, balance
from app.api.payments import pays

api.register_blueprint(pays, url_prefix='/payments')
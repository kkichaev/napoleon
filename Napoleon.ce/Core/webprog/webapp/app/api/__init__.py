from flask import Blueprint

api = Blueprint('api', __name__, template_folder='templates')

from app.api import users, servers, req_connects, balance, bills, paypal, yookassa
from app.api.payments import pays
from app.api.bills import Bill

api.register_blueprint(pays, url_prefix='/payments')

Bill.init_doc_number()


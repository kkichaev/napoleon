from flask import Blueprint
from app.moysklad.endpoints import endpoints

moysklad = Blueprint('moysklad', __name__, template_folder='templates')
moysklad.register_blueprint(endpoints, url_prefix='/api/moysklad/vendor/1.0')

from app.moysklad import iframe, models

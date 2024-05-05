import os

class Config(object):
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'you-will-never-guess'
    MOY_SKLAD_SK = os.environ.get('MOY_SKLAD_SK') or 'DUMMY'
    MOY_SKLAD_APPUID = os.environ.get('MOY_SKLAD_APPUID') or 'DUMMY'
    MOY_SKLAD_ID = os.environ.get('MOY_SKLAD_ID') or 'DUMMY'

    MOY_SKLAD_V2_APPUID = os.environ.get('MOY_SKLAD_V2_APPUID') or 'DUMMY'
    MOY_SKLAD_V2_ID = os.environ.get('MOY_SKLAD_V2_ID') or 'DUMMY'

    SQLALCHEMY_DATABASE_URI  = os.environ.get('DB_URI') or 'postgresql://postgres:1@localhost:5432/postgres'
    SQLALCHEMY_TRACK_MODIFICATIONS = False

    MAIL_SERVER = os.environ.get('MAIL_SERVER')
    MAIL_PORT = int(os.environ.get('MAIL_PORT') or 25)
    MAIL_USE_TLS = os.environ.get('MAIL_USE_TLS') is not None
    MAIL_USE_SSL = os.environ.get('MAIL_USE_SSL') is not None
    MAIL_USERNAME = os.environ.get('MAIL_USERNAME')
    MAIL_PASSWORD = os.environ.get('MAIL_PASSWORD')
    MAIL_FROM = os.environ.get('MAIL_FROM')
    MAIL_DEBUG=True

    GRS_FCGI_PORT = os.environ.get('GRS_FCGI_PORT')
    GRS_FCGI_HOST = os.environ.get('GRS_FCGI_HOST')

    PAYZE_API_KEY = os.environ.get('PAYZE_API_KEY')
    PAYZE_SECRET_KEY = os.environ.get('PAYZE_SECRET_KEY')
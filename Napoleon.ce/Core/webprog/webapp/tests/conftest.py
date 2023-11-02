import os
import tempfile
import pytest
from app import create_app, db
from flask_migrate import upgrade
from config import Config
from sqlalchemy import text

class ConfigTest(Config):
    TESTING = True
    SQLALCHEMY_DATABASE_URI = 'sqlite://'

# t@t.com:test
_data_sql = '''
INSERT INTO users (email, password_hash)
VALUES
  ('t@t.com', 'pbkdf2:sha256:260000$zo9svD2HSIerGGTk$6b1ef939cd5558ff87df30d7e1596b8157e2da0182aa0debf237ae0ce54385fe');
'''

@pytest.fixture()
def app():
    # db_fd, db_path = tempfile.mkstemp()
    # ConfigTest.SQLALCHEMY_DATABASE_URI = 'sqlite:///' + db_path

    app = create_app(ConfigTest)
    app.app_context().push()
    upgrade()

    stmt = text(_data_sql)
    db.session.execute(stmt)

    yield app

    # os.close(db_fd)
    # os.unlink(db_path)

@pytest.fixture()
def client(app):
    return app.test_client()


@pytest.fixture()
def runner(app):
    return app.test_cli_runner()
from app.api.payments import pays
from app import db
from flask import request
import traceback
from time import time

class BogStatus(db.Model):
    MODE_STATUS = 1
    MODE_RETURN = 2
    __tablename__ = "bog_status"

    id = db.Column(db.Integer, primary_key=True)
    mode=db.Column(db.Integer)
    order_id = db.Column(db.String(300))
    time = db.Column(db.Integer)
    text = db.Column(db.String(3000))


@pays.route("/bog/status", methods=['POST'])
def setStatus():
    try:
        if request.json:
            order_id = request.json['order_id']
            now = int(time())

            bs = BogStatus(order_id=order_id,time=now,text=request.data.decode('utf-8'),mode=BogStatus.MODE_STATUS)
            db.session.add(bs)
            db.session.commit()
    except:
        traceback.print_exc()
        pass
    return ""

@pays.route("/bog/return", methods=['POST'])
def makeReturn():
    try:
        if request.json:
            order_id = request.json['order_id']
            now = int(time())

            bs = BogStatus(order_id=order_id,time=now,text=request.data.decode('utf-8'),mode=BogStatus.MODE_RETURN)
            db.session.add(bs)
            db.session.commit()
    except:
        traceback.print_exc()
        pass
    return ""

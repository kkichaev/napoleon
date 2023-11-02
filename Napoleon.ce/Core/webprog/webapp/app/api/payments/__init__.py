from typing import Self
from flask import Blueprint
from app import db
import time, uuid

pays = Blueprint('payments', __name__, template_folder='templates')

class Payment(db.Model):
    STATUS_INITIAL = 0
    STATUS_OK = 1
    STATUS_ERROR = 2
    STATUS_DRAFT = 3

    CURRENCY_GEL = 'GEL'
    CURRENCY_USD = 'USD'

    __tablename__ = 'payments'

    id = db.Column(db.Integer, primary_key=True)
    order_id = db.Column(db.String(100), nullable=False, index=True, unique=True)
    pay_id = db.Column(db.String(100), index=True, unique=True)
    status = db.Column(db.Integer)
    time = db.Column(db.Integer)
    text = db.Column(db.String(300))
    amount = db.Column(db.Float, nullable=False)
    currency = db.Column(db.String(3), nullable=False)
    remark = db.Column(db.String(300))

    @staticmethod
    def find(order_id:str) -> Self|None:
        return Payment.query.filter(Payment.order_id == order_id).first()

    @staticmethod
    def findPID(pay_id:str) -> Self|None:
        return Payment.query.filter(Payment.pay_id == pay_id).first()

    @staticmethod
    def create(amount:float, text:str, currency:str=CURRENCY_GEL, remark:str='') -> Self:
        now = time.time()
        oid = str(uuid.uuid4()).replace('-','')
        p = Payment(
            order_id=oid
            ,time=now
            ,status=Payment.STATUS_INITIAL
            ,text=text
            ,amount=amount
            ,currency=currency
            ,remark=remark)
        
        db.session.add(p)
        db.session.commit()

        return p


# from app.api.payments.bog import makeReturn, setStatus
from app.api.payments.payze import getPayUrl, payHook

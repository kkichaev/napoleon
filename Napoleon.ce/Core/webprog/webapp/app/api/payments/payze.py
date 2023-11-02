import time
import traceback
from app.api.payments import pays, Payment
from app import db, url_for
from app.api.error import error_response
from flask import request, current_app, Response
from urllib.parse import urlparse
import requests
from flask import render_template

class PayzeStatus (db.Model):
    id = db.Column(db.Integer, primary_key=True)
    status=db.Column(db.String(300))
    payment_id = db.Column(db.String(300))
    time = db.Column(db.Integer)
    text = db.Column(db.String(3000))

@pays.route('/payze')
def renderPayment():
    payment = None
    oid = request.args.get('order_id')
    if oid: payment = Payment.find(oid)
    return render_template('payments/payment.html', payment=payment)        

@pays.route('/payze/status')
def renderStatus():
    payment = None
    oid = request.args.get('order_id')
    status = request.args.get('status')
    if oid: payment = Payment.find(oid)
    if payment and status == '1' and payment.status != Payment.STATUS_OK:
        payment.status = Payment.STATUS_OK
        db.session.commit()

    return render_template('payments/status.html', payment=payment, status=status)


@pays.route('/payze/payment', methods=['POST'])
def getPayUrl():

    url = ""
    try:
        if request.json and 'amount' in request.json and 'order_id' in request.json:
            oh = urlparse(request.base_url)
            host = oh.hostname

            if host == 'webapp': host = 'https://aceteam.app'

            orderid = request.json['order_id']
            payment = Payment.find(orderid)
            if not payment:
                return error_response('cant_find_order')
            if payment.status == Payment.STATUS_OK:
                return error_response('just_payed')
            
            payload = {
                'method':'justPay',
                'apiKey' : current_app.config['PAYZE_API_KEY'],
                'apiSecret' : current_app.config['PAYZE_SECRET_KEY'],
                'data': {
                    'amount' : request.json['amount'],
                    'currency' : 'GEL',
                    'callback' : host + url_for('api.payments.renderStatus') + '?status=1&order_id=' + orderid,
                    'callbackError' : host + url_for('api.payments.renderStatus') + '?status=0&order_id=' + orderid,
                    'preauthorize':False,
                    'lang':'EN',
                    'hookUrl':'',
                    'hookUrlV2':host + url_for('api.payments.payHook'),
                    'hookRefund':False,
                }
            }

            print('Data',payload)
            headers = {
                "accept": "application/json",
                "content-type": "application/json"
            }
            url = 'https://payze.io/api/v1'

            response = requests.post(url, json=payload, headers=headers)
            jsn = response.json()
            if jsn and 'response' in jsn and 'transactionUrl' in jsn['response']:
                url = jsn['response']['transactionUrl']
                print('URL', url)

                trid = jsn['response']['transactionId']
                payment.pay_id = trid
                db.session.commit()
            out_r = Response(response.text, status=response.status_code)
            return out_r
    except:
        traceback.print_exc()
        pass

    return url

@pays.route('/payze/payHook', methods=['POST'])
def payHook():
    url = ""
    try:
        if request.json and 'PaymentId' in request.json and 'PaymentStatus' in request.json:
            payment_id = request.json['PaymentId']
            status = request.json['PaymentStatus']

            payment = Payment.findPID(payment_id)
            if payment and payment.status != Payment.STATUS_OK:
                if status == 'Captured': payment.status = Payment.STATUS_OK
                elif status == 'Draft': payment.status = Payment.STATUS_DRAFT
                else: payment.status = Payment.STATUS_ERROR


            now = int(time())

            bs = PayzeStatus(
                payment_id=payment_id
                ,time=now,text=request.data.decode('utf-8')
                ,status=status
            )
            db.session.add(bs)
            db.session.commit()
        else:
            print('Bad request ', request.json)
    except:
        traceback.print_exc()
        pass
    return ""


from email.utils import make_msgid
from flask_mail import Message
from app import mail
from flask import current_app

def send_email(subject, recipients, text_body, html_body, sender = None):
    def get_domain(sender): return sender[sender.find('@')+1:]

    if not sender: sender = current_app.config['MAIL_FROM']
    msg = Message(subject, sender=sender, recipients=recipients)
    msg.msgId = make_msgid(None,get_domain(sender))
    msg.body = text_body
    msg.html = html_body
    mail.send(msg)
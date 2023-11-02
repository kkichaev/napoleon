# -*- coding: cp1251 -*-
import logging
import sys

def run(server):

   params = server.Params[0]

   logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    
   
   logging.debug("starting " + str(params))

   where = "type='{0}' and id='{1}' and number='{2}' and date=ToDate('{3}')".format(
         params.type, params.id, params.number, params.date.strftime("%d/%m/%Y"))
   
   canDo = False
   docs = server.Get('WorkingDocuments', where)
   if docs != None and len(docs) > 0 :
      canDo = docs[0].userid == server.CurrentUser().id
   else: 
      canDo = True

   answerObj = server.New('ReqDocAnswer')
   answer = answerObj.New()

   if canDo:
      wrDocs = server.New('WorkingDocuments')
      wr = wrDocs.New()
      wr.type = params.type
      wr.id = params.id
      wr.number = params.number
      wr.date = params.date
      wr.status = params.status

      server.Write(wrDocs)
      answer.status = 1
   else:
      answer.message = 'Документ уже в работе'
      answer.status = 0

   server.Post(answerObj)

   logging.debug("finish " + str(answer))

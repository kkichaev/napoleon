# -*- coding: cp1251 -*-
from datetime import timedelta
from datetime import datetime

import tempfile
import io
import client_card

def setData(t, v):
    html = "<tr>"
    html += "<td>"
    html += t
    html += "</td>" 
    html += "<td>"
    html += v
    html += "</td>" 
    html += "<tr>"
    
    return html

def printOut(data):
    r = ""
    r += "<html>"
    r += "<head>"
    r += "</head>"
    r += "<body>"
    r += "<table border='1' width='100%'>"
    
    r += setData("Название учреждения", data.name)
    r += setData("Адрес", data.address)
    r += setData("Категория", data.categ)
    r += setData("Телефон", data.phone)
    r += setData("Эл.почта", data.email)
    r += setData("Ф.И.О. директора/врача", data.cheif)
    r += setData("Ф.И.О. сотрудников", data.contact)
    r += setData("Закупка у дистрибьютора", data.dealers)
    r += setData("До 1 визита(что есть в наличии, на витрине, что назначали Апи-Сан)", data.beforevisit)
    r += setData("Закупки/назначения конкуренты", data.concurent)
    r += setData("Дата.Описание визитов.", data.visits)
    r += setData("Цель на следующий визит.", data.target)
    
    r += "</table>"
    r += "</body>"
    r += "</html>"
    
    return r
    
def saveToObject(id, html, repName, server):
    server.RegisterType("Result[id:s,html:s]")
    outObj = server.New("Result")
    
    obj = outObj.New()
    obj.id = id
    obj.html = html
    
        
    fileName = tempfile.gettempdir() + '/' + repName
    f = open(fileName, 'w')
    f.write(html)
#         
    server.Put(outObj)
            
def doReport(server):
    data  = client_card.loadData(server, "<br>")
    html = printOut(data)
    saveToObject(server.Params[0].id, html, "clientcard.html", server)
   
def run(server):
   print "start\t" + __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S')
   doReport(server)
   print "finish\t" +  __name__ + "\t" + datetime.now().strftime('%d/%m/%Y %H:%M:%S')

   

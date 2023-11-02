# -*- coding: cp1251 -*-

# types write string without space
# s - string
# n(prec) - double(number), prec == 0  integer
# n - integer
# d - date
# t - time
# dt - datetime
# b - binary
#

import datetime
from datetime import datetime
import io
from orgtaskutil import *
from grsoft_reporter import module_info

def get_module_info():
    mi = module_info(__name__)

    mi.parameters.append(module_info.param("agentName", module_info.param.TYPE_STRING))
    mi.parameters.append(module_info.param("agentID", module_info.param.TYPE_AGENT_ID))
    mi.parameters.append(module_info.param("start", module_info.param.TYPE_DATE))
    mi.parameters.append(module_info.param("finish", module_info.param.TYPE_DATE))
    
    return mi


import sys;
reload(sys);
sys.setdefaultencoding("cp1251")

def makeGrouHeader(html, title, cnt):  
    html += "<tr bgcolor='#DCDCDC'>"
    html += "<td>"+title+"&nbsp;(" + str(cnt) +")</td>"
    html += "<td>Коментарий</td>"
    html += "</tr>"
    return html

def makeDataRow(html, t):
    html += "<tr>"
    text = "&nbsp;"
    comment = "&nbsp;"
    
    if len(t.text) > 0 :
        text = t.text
        
    if len(t.comment) > 0 :
        comment = t.comment
        
    html += "<td width='50%'>" + text + "</td>"
    html += "<td width='50%'>" + comment + "</td>"  
    html += "</tr>"
    
    return html

def makeHtmlData(html, title, tasklists):
    html = makeGrouHeader(html, title, len(tasklists))
                
    for t in tasklists:
        html = makeDataRow(html, t)
    
    return html
                                              
def doReport(server):
    param = server.Params[0];
    html = "<html><head>" + \
           "<meta http-equiv='content-type' content='text/html; charset=utf-8'></head>" + \
           "<body><FONT FACE='Arial'>"
    html += "<H1>Отчет по заданиям</H1><br>"  
    html += "<H2>Агент:&nbsp;" + param.agentName
    html += "&nbsp;c " + param.start.strftime('%d/%m/%Y') + "&nbsp;по&nbsp;" + param.finish.strftime('%d/%m/%Y') + "</H2>"     
    html += "</FONT></body></html>"       
    uidFilter = '"userid" in ' + "('" + param.agentID + "')"
    orgs = server.Get("Org", uidFilter, "id")
    init(server)
     
    for o in orgs.values() :
        if o.id in taskDone or o.id in taskMissed: 
            html += "<table width='100%' border='1' >"
            html += "<tr  bgcolor='#F5FFFA'>"
            html += "<td colspan='2'>" + o.name + "</td>"
            html += "</tr>"
            
            if o.id in taskDone:
                html = makeHtmlData(html, "Выполнено", taskDone[o.id])
            
            if o.id in taskMissed:    
                html = makeHtmlData(html, "Не выполнено", taskMissed[o.id])
                
            html += "</table><br>"
    return html

def run(server):
    print "start orgtask_report " + datetime.now().strftime('%d/%m/%Y %H:%M:%S')
    
    type = "Result[html:s]"
    server.RegisterType(type)
    objList = server.New("Result")
    obj = objList.New()
    obj.html = doReport(server)
    server.Put(objList)
   
    print "done orgtask_report" + datetime.now().strftime('%d/%m/%Y %H:%M:%S')

   

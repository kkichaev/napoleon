# -*- coding: cp1251 -*-

import coordutils
import math
import tempfile
from robj import RObj
import orglist
import util
from objects import *

def page(geometry, center, hint):
    html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
    html += "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width:100%;height:100%\">\n"
    html += "<head>\n"
    html += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"
    html += "<title></title>\n"
    html += "<script src=\"http://api-maps.yandex.ru/2.0/?load=package.full&lang=ru-RU\" type=\"text/javascript\"></script>\n"
    html += "<script type=\"text/javascript\">\n"
    html += "ymaps.ready(init);\n"
    html += "function init () {\n"
    html += "var myMap = new ymaps.Map('map', {\n"
    html += "center: "+str(center)+",\n"
    html += "zoom: 12\n"
    html += "});\n"
    html += "var geometry = "+geometry+",\n"
    html += "properties = {\n"
    html += "hintContent: \"Polyline\""
    html += "},\n"
    html += "options = {\n"
    html += "draggable: false,\n"
    html += "strokeColor: '#ff0000',\n"
    html += "strokeWidth: 5\n"
    html += "},\n"
    html += "polyline = new ymaps.Polyline(geometry, properties, options);\n"
    html += "myMap.geoObjects.add(polyline);\n"   
    html += hint         
    html += "}\n"
    html += "</script>\n"
    html += "</head>\n"
    html += "<body style=\"width:100%;height:100%\">\n"
    html += "<div id=\"map\" style=\"width:100%;height:100%\"></div>\n"
    html += "</body>"
    html += "</html>"
    return html

class MapGis(RObj):
    html = None
    geometry = None
    center = None
    distance = 0
    obj = None
    startRoute = None
    endRoute = None
    outObj = None
    mapHint = None
    orglist = None
    idx = None
    
    def __init__(self, server):
        RObj.__init__(self, server)
        
        date = server.Params[0].date
        userid = server.Params[0].userid;
        
        self.startRoute = None
        self.endRoute = None
        self.distance = 0
        self.mapHint = ""
        self.geometry = "[]";
        self.idx = 1
        
        server.RegisterType("Mapgis[userid:s,html:s,title:s,date:d]")
        
        self.outObj = server.New("Mapgis")
        self.obj = self.outObj.New()
        self.obj.userid = userid
        self.obj.date = date.replace(hour=0, minute=0, second=0, microsecond=0)
        
        self.makeRoute(self.gpsroute(date, userid))
        
    def gpsroute(self, date, userid):    
        where = '"userid"=\''+userid+'\' and ' + '"date" >= ToDate("{0}") and "date" <= ToDate("{1}")'.format(
            date.strftime("%d/%m/%Y 0:0:0"), date.strftime("%d/%m/%Y 23:59:59"))
        
        return self.server.Get("GPSPos", where) 
    
    def makeRoute(self, gpsroute):
        sz = len(gpsroute) 
        if sz > 0:
            hr = sz / 2
            self.center = "[" +  str(gpsroute[hr].latitude) + "," + str(gpsroute[hr].longitude) + "]"
            self.startRoute = gpsroute[0].date
            self.endRoute = gpsroute[sz - 1].date
            self.routeData(gpsroute)
            self.distance = math.trunc(self.distance/1000)
                  
    def routeData(self, gpsroute):
        self.geometry = "["
        lastPos = None
               
        for gps in gpsroute:
            if lastPos != None:
                self.distance = self.distance + coordutils.distance(lastPos.latitude, lastPos.longitude, gps.latitude, gps.longitude);
                self.geometry += ","
                
            self.geometry += "[" +  str(gps.latitude) + "," + str(gps.longitude) + "]"        
                
            lastPos = gps    
        self.geometry += "]"
            
    def postProcess(self, server, mapHint):  
        self.obj.html = page(self.geometry, self.center, mapHint)
        if self.startRoute != None:
            self.obj.title = "Трек начало: " + self.startRoute.strftime("%H:%M") + " окончание: " + self.endRoute.strftime("%H:%M") + " пробег: " + str(self.distance) + " км."
        else:    
            self.obj.title = "Нет данных по пробегу"   
            
#DEBUG SECTION START             
#        fileName = tempfile.gettempdir() + '/' + "route.html"
#        text_file = open(fileName, "w")
#        text_file.write(self.obj.html)
#        text_file.close()
#DEBUG SECTION END
        
    def process(self, doc):
        org = orglist.name(doc.id)
        self.mapHint += "myPlacemark = new ymaps.Placemark([" + str(doc.latitude) + "," + str(doc.longitude) + "], {\n" +\
                'balloonContentHeader: "' + util.escapeQuotes(org) + '",\n' +\
                'balloonContentFooter: "' + util.escapeQuotes(objTitle(doc.GetName)) + '",\n' +\
                'iconContent: "' + str(self.idx) + '"\n' +\
                '});\n'
                       
        self.mapHint += "myMap.geoObjects.add(myPlacemark);\n"
        self.idx = self.idx + 1  
        
    def putSrv(self):
        self.postProcess(self.server, self.mapHint)
        self.server.Put(self.outObj)
        
# -*- coding: cp1251 -*-
from manager.coordutils import distance as Dist
import urllib.request, urllib.parse, urllib.error
import xml.etree.ElementTree as ET

class LocationPoint:
    __slots__ = ['latitude', 'longitude']
    
    def __init__(self, lat, lng):
        self.latitude = lat
        self.longitude = lng
        
    def distance(self, point2):
        return Dist(self.latitude, self.longitude, point2.latitude, point2.longitude)

class OrgLocation:
    __slots__ = ['data']
    
    def __init__(self, server):
        self.data = server.Get('OrgLocation','','id')
        
    def getLocation(self, org):
        orgId = org.id
        if orgId in self.data: 
            pt = self.data[orgId]
            return LocationPoint(pt.latitude, pt.longitude)
        
        return self.tryFindLocation(org)

    def isGoodPrecision(self, node):
        nodes = node.findAll('precision')
        return (len(nodes) > 0)

    def tryFindLocation(self, org):
        loc = LocationPoint(0, 0)
        
        req = "http://geocode-maps.yandex.ru/1.x/?geocode=" + org.address + "&key=ANzXmEoBAAAAkjQFPwIAQphddQGLBfkqE3qCI3Vo8OCM8yYAAAAAAAAAAACYpxbA0OaEdJRuAHC50onaXfYLBQ=="
        resp = urllib.request.urlopen(req).read()
        
        root = ET.fromstring(resp)
        for node in root.findall('featureMember'):
            if self.isGoodPrecision(node):
                for pos in node.findall('pos'):
                    posText = pos.text.split(' ')
                    if len(posText) > 1:
                        loc.longitude = float(posText[0]) 
                        loc.latitude = float(posText[1]) 
                    break
                
        self.data[org.id] = loc
        return loc
        
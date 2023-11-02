# -*- coding: cp1251 -*-

from importlib import reload
from datetime import datetime
import xml.etree.ElementTree as ET
import os


import sys;
reload(sys);
#sys.setdefaultencoding("cp1251")

def createOrderFile(fileName, doc):
    root = ET.Element("Order")
    header = ET.SubElement(root, "Order-Header")
    
    ET.SubElement(header, "OrderNumber").text = doc.created.strftime('%Y%m%d%H%M%S') + str(doc.id)
    ET.SubElement(header, "CustomerId").text = doc.id
    ET.SubElement(header, "OrderDate").text = doc.created.strftime('%Y-%m-%d')
    ET.SubElement(header, "RequestDate").text = doc.date.strftime('%Y-%m-%d')
    ET.SubElement(header, "SalesmanCode").text = doc.userid
    ET.SubElement(header, "Info").text = doc.remark

    lines = ET.SubElement(root, "Order-Lines")
    ctr = 1
    for oi in doc.items :
         line = ET.SubElement(lines, "Line")
         ET.SubElement(line, "LineNumber").text = str(ctr)
#        ET.SubElement(line, "Subinventory").text = doc.whCode
         ET.SubElement(line, "ItemId").text = oi.id
         ET.SubElement(line, "Quantity").text = str(int(oi.qty))
         
         ctr += 1

    ET.ElementTree(root).write(fileName,encoding="UTF-8",xml_declaration=True)
    
def run(server):
    orderObject = server.Find('Order')
    if orderObject == None or len(orderObject) == 0:
        return
    
    folder = server.ExchangeFolder() + "\\out";
    if not os.path.exists(folder):
        os.makedirs(folder)
        
    fileBase = "\\ZZ" + datetime.now().strftime('%Y%m%d%H%M%S%f')
    
    index = 1
    for doc in orderObject:
        createOrderFile(folder + fileBase + str(index) + ".xml", doc)
        index += 1
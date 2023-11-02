# -*- coding: cp1251 -*-

from importlib import reload
from datetime import datetime, date, timedelta
import xml.etree.ElementTree as ET
import os
import fnmatch
import logging

import ftpConnect
import linecache
import codecs

import sys;
from _ctypes import Array
reload(sys);
#sys.setdefaultencoding("cp1251")

def getException():
    exc_type, exc_obj, tb = sys.exc_info()
    f = tb.tb_frame
    lineno = tb.tb_lineno
    filename = f.f_code.co_filename
    linecache.checkcache(filename)
    line = linecache.getline(filename, lineno, f.f_globals)

    return str(exc_obj) + " line: " + str(lineno) + " " + line.strip()
    

def parseFile(name):
    root = None
    try:            
        root = ET.parse(name).getroot()
    except Exception as e:
        logging.error('exception parsing ' + name + ' '  + getException())
    return root
    
def loadDelivery(server, destFolder, ftp):
    folder = ftpConnect.inputFolder(server) + '/DEBTS'
    ftp.cwd(folder)
    files = ftp.nlst()

    try:
        updated = False
        pays = server.New('Payment')
        for fname in sorted(files, reverse=True):
            if not fname.startswith('DEBTS_') : continue

            name = destFolder + '/' + fname
            if os.path.exists(name):
                break
            
            logging.debug('getting ' + fname)
            dest = open(name, 'wb')
            ftp.retrbinary('RETR %s' % fname, lambda data: dest.write(data))
            dest.close()
                        
            root = parseFile(name)
            if root == None:
                continue

            loaded = list()
            updated = True
            for org in root.findall("Customer"):
                orgId = org.find('Id').text.encode('cp1251')
                if not type(orgId) is str: orgId = orgId.decode('cp1251')

                for srcdoc in org.findall("CustomerDocs/Document"):
                    sum = float(srcdoc.find('OriginalValue').text)
                    num = srcdoc.find('InvNumber').text
                    
                    code = orgId + '|' + num
                    if code in loaded or sum == 0:
                        continue                    
                    loaded.append(code)
                    
                    doc = pays.New()
                    doc.id = orgId
                    doc.number = num
                    doc.date = datetime.strptime(srcdoc.find('InvDate').text, '%Y-%m-%d')
                    doc.sum = sum
                    
            break
        
        if updated :
            server.Remove("Payment", '"userid" is null')
            server.ChangeUser('null')
            server.Write(pays)
            server.RestoreUser()
        
    except Exception as e:
        logging.error('exception in loadCustomers '  + getException())

def loadCustomerData(customer, agents, agentOrgs, server):
    id = customer.find('Id').text
    payerid = customer.find('Payer_Id').text
    if payerid == id:
        return

    userid = customer.find('SalesmanCode').text.strip().encode('cp1251')
    if not type(userid) is str: useerid = userid.decode('cp1251')
    
    if not userid in agents:
        agent = agents.New()
        agent.name = customer.find('SalesmanName').text
        agent.id = userid
        agents['__changed'] = True
        agents[userid] = agent
    
    orgs = None    
    if not userid in agentOrgs:        
        server.ChangeUser("'" + userid + "'")
        orgs = server.Get("Org", '', 'id')
        server.RestoreUser()
        if orgs == None:
            orgs = server.NewDict("Org", 'id')
        agentOrgs[userid] = orgs
    else:
        orgs = agentOrgs[userid]
        
    if orgs == None:
        orgs = server.New('Org')
        agentOrgs[userid] = orgs
    
    status = customer.find('Status').text.lower()
    print(status)
    if status == 'i':
        orgs.RemoveObject(id)
        return
            
                
    o = None
    if not id in orgs:
        o = orgs.New()
        o.id = id
        orgs[id] = o
    else:
        o = orgs[id]
        
    o.name = customer.find('Name').text
    o.address = customer.find('Address').text
    o.discount = float(customer.find('Discount').text)

def loadCustomers(server, destFolder, ftp):
    folder = ftpConnect.inputFolder(server) + '/CUSTOMERS'
    ftp.cwd(folder)
    files = ftp.nlst()
    try:
        agents = server.Get('Agents', '', 'id')
        if agents == None:
            agents = server.NewDict('Agents','id')
            agents['__changed'] = True
        else:
            agents['__changed'] = False
            
        agentOrgs = dict()
         
        for fname in sorted(files, reverse=True):
            if not fname.startswith('CUST_') : continue

            name = destFolder + '/' + fname
            if os.path.exists(name):
                break
            
            logging.debug('getting ' + fname)
            dest = open(name, 'wb')
            ftp.retrbinary('RETR %s' % fname, lambda data: dest.write(data))
            dest.close()
                        
            root = parseFile(name)
            if root == None:
                continue

            for customer in root.findall('Customer'):
                loadCustomerData(customer, agents, agentOrgs, server)
                            
            break
        
        if agents['__changed']: 
            logging.info('writing agents')
            server.Write(agents)
            
        for uid, orgs in agentOrgs.iteritems():
            userid = "'" + uid + "'"
            server.Remove("Org", '"userid"=' + userid)
            server.ChangeUser(userid)
            server.Write(orgs)
            server.RestoreUser()

    except Exception as e:
        logging.error('exception in loadCustomers '  + getException())
    
def loadCustomerDataEx(customer, agents, orgs, server):
    id = customer.find('Id').text
    payerid = customer.find('Payer_Id').text
    if payerid == id:
        return

    status = customer.find('Status').text.lower()
    if status == 'i':
        orgs.RemoveObject(id)
        return

    userid = customer.find('SalesmanCode').text.strip().encode('cp1251')
    if not type(userid) is str: userid = userid.decode('cp1251')
    
    if not userid in agents:
        agent = agents.New()
        agent.name = customer.find('SalesmanName').text
        agent.id = userid
        agents['__changed'] = True
        agents[userid] = agent
    
    o = None
    id = customer.find('Id').text
    if not id in orgs:
        o = orgs.New()
        o.id = id
        orgs[id] = o
    else:
        o = orgs[id]
        
    o.name = customer.find('Name').text
    o.address = customer.find('Address').text
    o.discount = float(customer.find('Discount').text.replace(',', '.'))


def loadCustomersEx(server, destFolder, ftp):
    folder = ftpConnect.inputFolder(server) + '/CUSTOMERS'
    ftp.cwd(folder)
    files = ftp.nlst()
    try:
        agents = server.Get('Agents', '', 'id')
        if agents == None:
            agents = server.NewDict('Agents','id')
            agents['__changed'] = True
        else:
            agents['__changed'] = False
            
        orgs = server.Get('Org', '"userid" is null', 'id')
        if orgs == None:
            orgs = server.NewDict('Org')
         
        for fname in sorted(files, reverse=True):
            if not fname.startswith('CUST_') : continue

            name = destFolder + '/' + fname
            if os.path.exists(name):
                break
            
            logging.debug('getting ' + fname)
            dest = open(name, 'wb')
            ftp.retrbinary('RETR %s' % fname, lambda data: dest.write(data))
            dest.close()
                        
            root = parseFile(name)
            if root == None:
                continue

            for customer in root.findall('Customer'):
                loadCustomerDataEx(customer, agents, orgs, server)
            
            if agents['__changed']: 
                logging.info('writing agents')
                server.Write(agents)
            
            server.Remove("Org", '"userid" is null')
            server.ChangeUser('null')
            server.Write(orgs)
            server.RestoreUser()
                
            break
    except Exception as e:
        logging.error('exception in loadCustomers '  + getException())

def loadSkladsData(server, dscArray):
    sklads = list()
    
    idx = 1
    while True:
        skladStr = "Sklad" + str(idx)
        sklVal = server.Config(skladStr)
        if sklVal == None:
            break
        
        sklValDsc = sklVal.split(';')
        dsc = 0.0
        if len(sklValDsc) > 1:
            dsc = float(sklValDsc[1]) / 100.0
        dscArray.append(dsc)
        
        sklVal = sklValDsc[0]
        sklData = sklVal.split(',')
        sklads.append(sklData)
        idx += 1
        
    if len(sklads) == 0:
        sklads.append(server.Config('Sklad'))
        
    return sklads
        

def writeSkladData(server, skladData):
    sklads = server.New('SkaldsInt')
    for el in skladData:
        val = el[0]
        sklad = sklads.New()
        sklad.key = val
        sklad.value = val 
        
    server.Remove('SkaldsInt', '')
    server.Write(sklads)

def findSklad(skladData, skladId):
    idx = 0
    for el in skladData:
        if skladId in el: return idx
        idx += 1
        
    return -1
        

def skladDiscount(skladIndex, dscArray):
    if skladIndex >= len(dscArray): return 0
    
    return 1.0 - dscArray[skladIndex];
#     if skladId == "ASU11M": return 1 - 0.5
#     if skladId == "ASU9M": return 1- 0.8
#     if skladId == "ASU6M": return 1- 0.9
#     if skladId == "ASV": return 1 - 0.98
#     return 0

def loadQty(server, destFolder, ftp, price):
#     sklads = server.Get('SkaldsInt', '')
#     skladsChanged = False
#     if sklads == None:
#         sklads = server.New('SkaldsInt')
#         skladsChanged = True

    folder = ftpConnect.inputFolder(server) + '/ONHAND'
    ftp.cwd(folder)
    files = ftp.nlst()
    
    dscArray = list()
    skladData = loadSkladsData(server, dscArray)
    writeSkladData(server, skladData)
#     skladId = server.Config('Sklad')
    
    curDate = datetime.now().date()
    curDate = datetime(2016, 11, 18).date()
    
    clearQty = list()
    loadedSklads = list()
        
    for fname in sorted(files, reverse=True):
        try:
            if not fname.startswith('ONHAND_') : continue

            name = destFolder + '/' + fname
            if os.path.exists(name):
                break
            
            strDate = fname[7:15]
            fileDate = datetime.strptime(strDate, '%Y%m%d').date()
            if fileDate < curDate:
                break

            logging.debug('getting ' + fname)
            dest = open(name, 'wb')
            ftp.retrbinary('RETR %s' % fname, lambda data: dest.write(data))
            dest.close()
            
            root = parseFile(name)
            if root == None:
                continue
            
            for item in root.findall('Item'):
                id = item.find('Id').text.encode('cp1251')
                if not type(id) is str: id = id.decode('cp1251')

                if not id in price:
                    logging.info('no item ' + id)
                    continue

                whId = item.find('Warehouse').text.encode('cp1251')
                if not type(whId) is str: whId = whId.decode('cp1251')

                sklIndex = findSklad(skladData, whId)
                if sklIndex < 0:
                    continue
#                 if whId != skladId:
#                     continue
                
                if not sklIndex in clearQty :

                    clearQty.append(sklIndex)
                    for prc in price.values() :
                        if sklIndex == 0: prc.qty = 0
                        elif sklIndex <= len(prc.whQty):
                            prc.whQty[sklIndex-1].qty = 0
                
                code = whId + '|' + id
                if code in loadedSklads:
                    continue
                loadedSklads.append(code)
                
                
#                 sklIndex = -1
#                 idx = 0
#                 for sklad in sklads:
#                     if sklad.key == whId:
#                         sklIndex = idx
#                         break
#                     idx += 1 
                    
#                 if sklIndex < 0:
#                     sklIndex = len(sklads)
#                     sklad = sklads.New()
#                     sklad.key = whId
#                     sklad.value = whId
                
                cost = float(item.find('BasePrice').text)
                dsc = skladDiscount(sklIndex, dscArray)
                if dsc != 0:
                    cost = cost * dsc
                    
                qty = float(item.find('QuantityAvailableToReserve').text)

                pItem = price[id]
                
                if sklIndex == 0:
                    pItem.qty += qty
                    if len(pItem.cost) == 0:
                        ci = pItem.cost.New()
                        ci.id = id                    
                    pItem.cost[0].cost = cost
                else:
                    while len(pItem.whQty) < sklIndex:
                        wi = pItem.whQty.New()
                        wi.id = id
                    wi = pItem.whQty[sklIndex-1] 
                    wi.qty += qty
                    wi.cost = cost
                    
        except Exception as e:
            logging.error('exception in loadQty '  + getException())

#         server.Write(sklads)
        
    server.Remove("Price", '"userid" is null')
    server.ChangeUser('null')
    server.Write(price)
    server.RestoreUser()


class Folder:
    __slots__ = ['childs', 'name', 'id']
    
    def __init__(self, id, name):
        self.childs = dict()
        self.id = id
        self.name = name 
        
    def put(self, collection, level):
        dest = collection.New()
        dest.name = self.name
        dest.id = self.id
        dest.level = level
        
        src = list()
        for ch in self.childs.values():
            src.append(ch)
        
        src.sort(key = lambda x: x.name)
        for ch in src:
            ch.put(collection, level+1)

def UpdateFolders(folders, item):
    groupid = item.find('GroupId').text
    fid = item.find('SubgroupId').text
    
    group = None
    if not groupid in folders:
        group = Folder(groupid, item.find('GroupDesc').text)
        folders[groupid] = group
    else:
        group = folders[groupid]
        
    if not fid in group.childs:
        folder = Folder(fid, item.find('SubgroupDesc').text)
        group.childs[fid] = folder
        
    return fid
        

def GetOtherFolder(folders):
    fid = 'rootId'
    if not fid in folders:
        otherFolder = Folder(fid, 'Разное')
        folders[fid] = otherFolder
        
    return fid         
        
def WriteFolders(folders, server):
    dest = server.New('SyncFolder')

    src = list()
    for f in folders.values():
        src.append(f)
    src.sort(key = lambda x: x.name)
    for f in src:
        f.put(dest, 0)
    
    server.Remove("SyncFolder", '"userid" is null')
    server.ChangeUser('null')
    server.Write(dest)
    server.RestoreUser()
    

def loadItems(server, destFolder, ftp):
    
#     folders = server.Get('ManagerFolder', '')
#     if folders == None:
#         folders = server.New('ManagerFolder')
#     if len(folders) == 0:
#         f = folders.New()
#         f.id = "TopFolder"
#         f.level = 0
#         f.name = "Товары"
#         server.ChangeUser('null')
#         server.Write(folders)
#         server.RestoreUser()
#         
#     baseFolder = folders[0]
        
    folders = dict()    
        
    price = server.Get('ManagerPrice', '', 'id')
    if price == None:
        price = server.NewDict('ManagerPrice', 'id')
    
    
    curDate = datetime.now().date()
    curDate = datetime(2016, 11, 10).date()

    loadedItems = 0
    folder = ftpConnect.inputFolder(server) + '/ITEMS'
    ftp.cwd(folder)
    files = ftp.nlst()
    try:
        for fname in sorted(files, reverse=True):
            if not fname.startswith("ITEMS_"): continue

            name = destFolder + '/' + fname
            if os.path.exists(name):
                break

            strDate = fname[6:14]
            fileDate = datetime.strptime(strDate, '%Y%m%d').date()
            if fileDate < curDate:
                break
            if loadedItems > 100:
                break

            logging.debug('getting ' + fname)
            dest = open(name, 'wb')
            ftp.retrbinary('RETR %s' % fname, lambda data: dest.write(data))
            dest.close()
            root = parseFile(name)
            if root == None:
                continue

            subgroups = dict()
            for item in root.findall('Item'):
                id = item.find('Id').text.encode('cp1251')
                if not type(id) is str: id = id.decode('cp1251')

                loadedItems += 1
                
                fid = ''
                subgroup = item.find('Subgroup')
                if subgroup == None:
                    fid = GetOtherFolder(folders)
                else:
                    sb = subgroup.text.encode('cp1251')
                    if not type(sb) is str: sb = sb.decode('cp1251')
                
                    if not sb in subgroups:
                        fid = UpdateFolders(folders, item)
                        subgroups[sb] = fid
                    else:
                        fid = subgroups[sb]
                
                prcItem = None
                if not id in price:
                    prcItem = price.New()
                    prcItem.id = id
                    price[id] = prcItem
                else:
                    prcItem = price[id]
                    
                prcItem.fid = fid
                prcItem.name = item.find('Description').text
                inPack = float(item.find('BoxQty').text)
                if inPack == 0:
                    if prcItem.qtyInPack == 0:
                        prcItem.qtyInPack = 1
                else:
                    prcItem.qtyInPack = inPack 
                
#            break
    except Exception as e:
        logging.error('exception in loadItems '  + getException())
    
    
    if len(folders) > 0:
        WriteFolders(folders, server)
    
    return price
    
    
def WriteDelivery(dlv, root, src):
    number = ''
    for el in root.findall('.//EbsOrderNumber'):
        number = el.text
        break
    
    if number in dlv:
        return

    dest = dlv.New()
    dest.date = src.date
    dest.paydate = datetime.now() + timedelta(days=100)
    
    dest.id = src.id
    dest.created = src.created
    dest.number = number
    dlv[number] = dest
    
    for item in root.findall('.//Line'):
        di = dest.items.New()
        di.id = item.find('ItemId').text
        qty = float(item.find('Quantity').text)
        di.qty = qty
        di.sum = float(item.find('Price').text) * qty
    

def UpdateDlvDocs(ddocs, sdocs):
    refDate = datetime.now() + timedelta(days=-90)
    if sdocs != None:
        for d in sdocs:
            if d.date >= refDate:
                if d.number in ddocs: 
                    continue
                
                dest = ddocs.New()
                dest.id = d.id
                dest.created = d.created
                dest.date = d.date
                dest.paydate = d.paydate
                dest.number = d.number
                
                ddocs[dest.number] = dest
                
                for i in d.items:
                    di = dest.items.New()
                    di.id = i.id
                    di.sum = i.sum
                    di.qty = i.qty
            
    return ddocs        
    
    
def loadOrderConfirms(server, destFolder, ftp):
    folder = ftpConnect.inputFolder(server) + '/ORDER_CONFS'
    ftp.cwd(folder)
    files = ftp.nlst()
    
#     docs = dict()
    
    try:
        orders = None
        dlv = None
        
        for fname in sorted(files, reverse=True):
            if not fname.startswith('ORDER_CONFS_') : continue

            name = destFolder + '/' + fname
            if os.path.exists(name):
                break

            logging.debug('getting ' + fname)
            dest = open(name, 'wb')
            ftp.retrbinary('RETR %s' % fname, lambda data: dest.write(data))
            dest.close()

            root = parseFile(name)
            if root == None:
                continue
    
            if orders == None:
                crdate = datetime.now() + timedelta(days=-6)
                where = '"created">=ToDate("' + crdate.strftime('%d.%m.%Y') + '")'
                ddocs = server.Get('Order', where)
                if ddocs == None: ddocs = server.New('Order')
                orders = dict()
                for d in ddocs:
                    orders[d.created.strftime('%Y%m%d%H%M%S')] = d
            
            cr = ''
            for el in root.findall('.//OrderNumber'): 
                cr = el.text
                break
            
            if cr in orders:
                src = orders[cr]
                
                if dlv == None:
                    dlvs = server.Get('Delivery', '"userid" is null')
                    dlv = UpdateDlvDocs(server.NewDict('Delivery', 'number'), dlvs)                        
                    

#                 dlv = None
#                 if src.userid in docs:
#                     dlv = docs[src.userid]
#                 else:
#                     dlvs = server.Get('Delivery', '"userid"=' + "'"+ src.userid + "'")
#                     dlv = UpdateDlvDocs(server.New('Delivery'), dlvs)                        
# 
#                     docs[src.userid] = dlv
                    
                WriteDelivery(dlv, root, src)
            
            if dlv != None:
                server.Remove("Delivery", '"userid" is null')
                server.ChangeUser('null')
                server.Write(dlv)
                server.RestoreUser()
            
#         for k, v in docs.iteritems():
#             uid = "'" + k + "'"
#             print uid
#             server.Remove("Delivery", '"userid"=' + uid)
#             server.ChangeUser(uid)
#             server.Write(v)
#             server.RestoreUser()
            
    except Exception as e:
        logging.error('exception in loadItems '  + getException())
    
def run(server):
    logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    
    logging.debug("starting")
    
    folder = server.ExchangeFolder() + "\\in";
    if not os.path.exists(folder):
        os.mkdir(folder)
    
    ftp = ftpConnect.makeFTPConnect(server)
    price = loadItems(server, folder, ftp)
    loadQty(server, folder, ftp, price)
    
    loadCustomersEx(server, folder, ftp)
    loadDelivery(server, folder, ftp)
    
    loadOrderConfirms(server, folder, ftp)
    
    ftp.quit()
    
    logging.debug("ending")
    

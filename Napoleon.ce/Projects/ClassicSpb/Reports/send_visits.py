# -*- coding: cp1251 -*-

import sys
import locale
import logging
import json
import binascii
import time
from datetime import datetime
from datetime import timedelta
from typing import Tuple

API_KEY = 'a604b9e115844070a492300838e15d4d'

class CloudData:
    __slots__ = ['baseUrl', 'authHeader', 'server', 'priceMap', 'userid', 'matrix', 'items']
    
    def __init__(self, server, userid):
        self.baseUrl = server.Config('CloudUrl')
        self.authHeader = 'Authorization: Token ' + server.Config('CloudToken')
        self.server = server
        
        if userid[0] != "'" : userid = "'" + userid + "'"
        self.userid = userid
        
        server.ChangeUser(self.userid)
        
        self.matrix = server.Get('FaceMatrix', '', 'name')
        self.priceMap = server.Get('SKUCloud', '', 'barcode')
        
        server.RestoreUser()

    def GetCloudItemMap(self):
        ret = dict()
        
        for k, v in self.priceMap.items():
            ret[v.id] = k
        
        return ret
    
    def PrepareMHLItems(self, matrixName, answ, items):

        if not matrixName in self.matrix:
            answ.addAnswer(-1, "no face matrix " + matrixName, 'PrepareMHLItems', '')
            return ""
        
        matrix = self.matrix[matrixName]
        
        itemsStr = ""
        
        for mi in matrix.items:
            if len(mi.barcode) == 0 or (not mi.barcode in self.priceMap): continue
            pid = self.priceMap[mi.barcode].id
            itemsStr += mi.barcode
            if pid == 0: continue
            
            items.append(pid)
            
        if len(items) == 0:
            answ.addAnswer(-1, "no cloud items in face matrix " + matrixName, 'PrepareMHLItems', '')
            return ""
            
        return itemsStr
    
    def CreateData(self, curl, url, obj):
#        data = json.dumps(obj, encoding='cp1251').decode('cp1251').encode('utf8')
        data = json.dumps(obj)
        
#        print (data)
        
        headers = list()
        headers.append(self.authHeader)
        headers.append('content-type: application/json')
        
        surl =self.baseUrl + url
        if not surl.endswith('/'): surl += '/'

        result = curl.UrlPost(surl, headers, data)
        return result
        
    def GetData(self, curl, url):
        surl = self.baseUrl + url
        if not surl.endswith('/'): surl += '/'
        
        if curl == None:
            curl = self.server.Curl()
        
        result = curl.UrlGet(surl, self.authHeader)        
        if result.IsSuccess:
            return json.loads(result.Response)
        
        return None
    
    def GetDataResult(self, curl, url):
        surl = self.baseUrl + url
        if not surl.endswith('/'): surl += '/'
        
        if curl == None:
            curl = self.server.Curl()
        
        return curl.UrlGet(surl, self.authHeader)        

    def UpdateData(self, curl, url, obj):
#        data = json.dumps(obj, encoding='cp1251').decode('cp1251').encode('utf8')
        data = json.dumps(obj)

#        print(data)
        
        headers = list()
        headers.append(self.authHeader)
        headers.append('content-type: application/json')
        
        surl =self.baseUrl + url
        if not surl.endswith('/'): surl += '/'
        return curl.UrlPut(surl, headers, data)
    
    def LoadMHL(self, orgId, matrixName, mhlId, curCrc, answ):
#         logging.info('Load mhl ' + matrixName)
#         print 'Load mhl ' + matrixName
        
        self.items = list()
        itemsStr = self.PrepareMHLItems(matrixName, answ, self.items) 
        if len(itemsStr) == 0 :
            answ.addAnswer(-1, 'No mhl items', 'LoadMHL::CreateMhl', '')
            return -1, ""
        
        crcStr = hex(binascii.crc32(bytearray(itemsStr, 'utf-8')) & 0xffffffff)
#         print curCrc + " " + crcStr + " " + str(mhlId)

        mhl = self.baseUrl + '/mhl/'
        
        # if have - remove it
        if mhlId != None:
            if crcStr == curCrc:
                return mhlId, curCrc
            
            print('Create new matrix')
            curl = self.server.Curl()
            url = mhl + str(int(mhlId)) + '/'
            curl.UrlDelete(url, self.authHeader)
    
        curl = self.server.Curl()
        obj = dict()
        obj['name'] = 'mhl ' + orgId

        result = self.CreateData(curl, '/mhl/', obj)
        
        if not result.IsSuccess :
            answ.addAnswer(result.Code, result.Response, 'LoadMHL::CreateMhl', '')
            # answ.addAnswer(curl.Code, curl.Response.decode('utf8').encode('cp1251'), 'LoadMHL::CreateMhl', '')
            return -1, crcStr
        
        obj = json.loads(result.Response)
        mhlId = obj['id']
        
#         logging.info('Create mhl items for ' + str(mhlId))
#         print 'Create mhl items for ' + str(mhlId)
        
        url = self.baseUrl + '/mhl_record/'
        for pid in self.items:
            pid = int(pid)
            obj = dict()
            obj['musthavelist'] = mhlId
            obj['name'] = 'sku ' + str(pid)
            obj['sku'] = pid
            
            result = self.CreateData(curl, '/mhl_record/', obj)

            if not result.IsSuccess :
                answ.addAnswer(result.Code, result.Response, 'LoadMHL::CreateMhlRecord', '')
                # answ.addAnswer(curl.Code, curl.Response.decode('utf8').encode('cp1251'), 'LoadMHL::CreateMhlRecord', '')
                return -1, crcStr
            
        return mhlId, crcStr
                
    def UploadPhoto(self, photoData, index, answ):
        post = []
        post.append(['name', str(index) + ".jpg", photoData, 'image/jpeg'])

        url =self.baseUrl + '/uploads/'
        post = dict()
        post["datafile"] = [str(index) + ".jpg", photoData]
#         post["datafile"] = photoData
        
        curl = self.server.Curl()
        result = curl.UrlPost(url, self.authHeader, None, post)
        
        if not result.IsSuccess :
            answ.addAnswer(result.Code, result.Response, 'UploadPhoto', '')
            # answ.addAnswer(curl.Code, curl.Response.decode('utf8').encode('cp1251'), 'UploadPhoto', '')
            return -1

        obj = json.loads(result.Response)
        return obj['id']
        
        
                
    def FindOrCreateDisplay(self, orgId, matrixName, answ):
#         logging.info('Load display')
#         print 'Load display'
        
        display = self.server.Get('CloudDisplays', '"id" = ' + "'" + orgId + "'")
        if display != None and len(display) > 0:
            curl = self.server.Curl()
            dispObj = display[0]
            
            durl = '/displays/' + str(int(dispObj.display))
            
#             logging.info('Try get display ' + str(int(dispObj.display)))
#             print 'Try get display ' + str(int(dispObj.display))
            
            dobj = self.GetData(curl, durl)
            if dobj != None:
                curMhlId = dobj['musthavelist']
                mhlId, crc = self.LoadMHL(orgId, matrixName, curMhlId, dispObj.matrixCRC, answ)
                if mhlId == -1:
                    return -1
                
                if crc != dispObj.matrixCRC:
                    dispObj.matrixCRC = crc
                    self.server.Write(display)
                
                if curMhlId != mhlId:    
                    dobj['musthavelist'] = mhlId
                    dobj.pop('planogram', None)
                    
                    result = self.UpdateData(curl, durl, dobj)
#                     logging.info('Update display ' + str(int(dispObj.display)))
#                     print 'Update display ' + str(int(dispObj.display))
                
                    if not result.IsSuccess:
                        answ.addAnswer(result.Code, result.Response, 'FindOrCreateDisplay::UpdateDiplay', '')
                        # answ.addAnswer(curl.Code, curl.Response.decode('utf8').encode('cp1251'), 'FindOrCreateDisplay::UpdateDiplay', '')
                        return -1
                
                return dobj['id']
         
        mhlId, crc = self.LoadMHL(orgId, matrixName, None, "", answ)        
#         
        obj = dict()
        obj['name'] = 'display ' + orgId
        obj['musthavelist'] = mhlId
         
        curl = self.server.Curl()
#         print obj
        result = self.CreateData(curl, '/displays/', obj)
        if not result.IsSuccess :
            answ.addAnswer(result.Code, result.Response, 'FindOrCreateDisplay::CreateDisply', '')
            # answ.addAnswer(curl.Code, curl.Response.decode('utf8').encode('cp1251'), 'FindOrCreateDisplay::CreateDisply', '')
            return -1
 
        obj = json.loads(result.Response)
        dispId = obj['id']
        
        display = self.server.New('CloudDisplays')
        dobj = display.New()
        dobj.display = dispId
        dobj.id = orgId
        dobj.matrixCRC = crc
         
        self.server.Write(display)

#         logging.info('Create display ' + str(dispId))
#         print 'Create display ' + str(dispId)
        
        return dispId
        
    def Recognize(self, photosList, repList, dispId, answ, agentId, agentName):
        metadata = dict()
#         metadata['agent_id'] = agentId
#         metadata['agent_name'] = agentName
        metadata['agent_name'] = agentId
        metadata['agent_id'] = agentName
        
        obj = dict()
        obj['images'] = photosList
        obj['report_types'] = repList
        obj['display'] = dispId
        obj['metadata'] = metadata
        
        curl = self.server.Curl()
        url = '/recognize/'
        result = self.CreateData(curl, url, obj)
        
        if not result.IsSuccess :
            answ.addAnswer(result.Code, result.Response, 'Recognize', '')
            # answ.addAnswer(curl.Code, curl.Response.decode('utf8').encode('cp1251'), 'Recognize', '')
            return None
        
        return json.loads(result.Response)

    def WaitForReport(self, repId, answ, debug=False):
        
        url = '/reports/' + str(int(repId)) + '/'
#         print 'rep url ' + url 
        curl = self.server.Curl()
        
        while True:
            result = self.GetDataResult(curl, url)
            
            if not result.IsSuccess :
                answ.addAnswer(result.Code, result.Response, 'WaitForReport', str(int(repId)))
                # answ.addAnswer(curl.Code, curl.Response.decode('utf8').encode('cp1251'), 'WaitForReport', str(int(repId)))
                return None
            
#             if debug : print curl.Response
            
            obj = json.loads(result.Response)
            if obj['status'] == 'READY':
                return obj['json']
            
            if obj['status'] == 'ERROR':
                answ.addAnswer(400, obj['error'], 'WaitForReport', str(int(repId)))
                return None
            
            time.sleep(1)
        
 
class AnswerHandler:
    __slots__ = ['answer', 'userid', 'id', 'created', 'answ', 'code']
    
    def __init__(self, server, userid):
        self.answer = server.New('CloudHanderErrors')
        self.userid = userid
        self.id = ''
        self.created = datetime.now()
        self.answ = ''
        self.code = 0
        
    def setDoc(self, doc):
        self.id = doc.id
        self.created = doc.created
        
    def addAnswer(self, code, answer, tag, repName):
        answ = self.answer.New()
        answ.userid = self.userid
        answ.id = self.id
        answ.created = self.created
        answ.handled = datetime.now()
        answ.repName = repName
        answ.code = code
        answ.answ = answer
        answ.tag = tag
        self.answ = answer
        self.code = code
 
def GetReports(server, cloudData, orgId, repName, reports, answ, needPrevious): 
    if not repName in reports:
        answ.addAnswer(-1, 'No report ' + repName, 'GetReports', repName)
        return None, None
        
    repId = reports[repName]
    fcRepData = cloudData.WaitForReport(repId, answ, needPrevious)
    if fcRepData == None:
        return None, None
    
    where = '"id"=' +"'" + orgId + "'" +' and "repName"=' + "'" + repName + "'"
    repData = server.Get('CloudReports', where)
    if repData == None:
        repData = server.New('CloudReports')
        
    if len(repData) > 0 :
        curData = repData[0]
    else:
        curData = repData.New()
    
    prevId = 0
    curData = repData[0]
    
    now = datetime.now().date()
    if curData.repCreated.date() < now:
        prevId = curData.repID
        curData.prevID = curData.repID
        curData.prevCreated = curData.repCreated
    elif curData.prevID > 0:
        prevId = curData.prevID
    curData.repID = repId
    curData.repCreated = datetime.now()
    curData.repName = repName
    curData.id = orgId
    
    server.Write(repData)

    fcPrev = None
    if needPrevious and prevId > 0:
        fcPrev = cloudData.WaitForReport(prevId, answ)        
    return fcRepData, fcPrev

def getMissimg(cloudData, mhlRecCache, mhlRep, cldItems):            
    missing = list()
#     print 'get mhl count ' + str(len(mhlRep['records']))
#     print mhlRep['records']        
    for repItem in mhlRep['records']:
        if repItem['compliant']: continue
        
        cldId = 0
        mhlId = repItem['mhl_record_id']
#         if not mhlId in mhlRecCache:
        url = '/mhl_record/' + str(int(mhlId)) + '/'
        logging.info('ask ' + url)
#         print 'ask ' + url
        
        mhlObj = cloudData.GetData(None, url)
        
        if mhlObj != None:
            cldId = mhlObj['sku']
            print( 'get ' + str(cldId))
#             mhlRecCache[mhlId] = cldId
        else:
            print( 'get none')
#         else:
#             cldId = mhlRecCache[mhlId]
            
        if cldId in cldItems:
            missing.append(cldItems[cldId])
            
    return missing

def UploadPhotos(visit, curl, answ, logging):

    url = 'https://ai.shop-survey.ru/api/v1/reports/create/?api_key={0}'.format(API_KEY)
    header = 'Accept: application/json'
    
    index = 1
    files = []
    for vi in visit.items:
        fileName = str(index) + ".jpg"
        files.append(['files', fileName, vi.id, 'image/jpeg'])

    msg = 'Upload photos ' + str(len(files))
    logging.info(msg)
    result = curl.UrlPost(url, header, {"application":"Napoleon"}, files)

    print(result.Response)
    if not result.IsSuccess :
        answ.addAnswer(result.Code, result.Response, 'UploadPhoto', '')
        return -1

    obj = json.loads(result.Response)
    return obj['id']

def GetReport(curl, reportId, logging):
    header = 'Accept: application/json'
    url = 'https://ai.shop-survey.ru/api/v1/reports/{0}/?api_key={1}'.format(reportId, API_KEY)
    
    while True:
        result = curl.UrlGet(url, header)
        if not result.IsSuccess:
            return None
    
        obj = json.loads(result.Response)
        if obj['status'] == 'complete':
            return obj['products']

        time.sleep(1)

def CountPrevFacing(server, prevDocWhere):
    prevF = server.Get('Facing', prevDocWhere)
    if prevF == None: return None

    prevTotal = 0
    created = None
    for doc in prevF:
        if created == None or created < doc.created:
            created = doc.created
            prevTotal = 0
            for di in doc.items:
                prevTotal += di.qty
    return prevTotal

def FindMissing(orgMatrix, usedItems, fdoc, visitAnsw, userid) -> list[str]:
    missed = []
    for mitem in orgMatrix.values():
        if mitem.id in usedItems: continue

        visItem = fdoc.items.New()
        visItem.id = mitem.id
        visItem.qty = 0
        
        # check missing from facing
        misItem = visitAnsw.missing.New()
        misItem.barcode = mitem.barcode
        logging.info(userid + ' not in facing ' + mitem.barcode)

        missed.append(mitem.barcode)

    return missed

def CountReportItems(fdoc, report, clickDoc, priceBc : dict[str,str], orgMatrix) ->tuple[int, list[str]] : 
    cldoc = None
    facingTotal = 0
    usedItems = list()

    for repItem in report:
            
        barcode = repItem['product_code']
        if not barcode in priceBc: continue

        id = priceBc[barcode]
        iqty = repItem['faces_count']
        
        if cldoc == None:
            cldoc = clickDoc.New()
            cldoc.id = fdoc.id
            cldoc.created = fdoc.created
            cldoc.date = fdoc.date
        visItem = cldoc.items.New()
        visItem.id = id
        visItem.qty = iqty


        if id in orgMatrix:
            usedItems.append(id)
                
            visItem = fdoc.items.New()
            visItem.id = priceBc[barcode]
            iqty = repItem['faces_count']
            visItem.qty = iqty
            facingTotal += iqty

    return (facingTotal, usedItems)

def SendMissingData(missed : list[str], reportId: int, curl):
    url = 'https://ai.shop-survey.ru/api/v1/reports/problem/create/?api_key={0}'.format(API_KEY)
    header = 'Accept: application/json'
    
    post = []
    post.append({"report_id":reportId})
    for vi in missed:
        post.append({"products":vi})

    msg = 'Sends problems items ' + str(len(missed))
    logging.info(msg)
    result = curl.UrlPost(url, header, post, None)

    logging.info(result.Response)

def run(server):    
    logging.basicConfig(format='%(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    

    locale.setlocale(locale.LC_ALL, 'american')

    checkDate = datetime.now() + timedelta(minutes = -1)
    
    userid = "'" + server.CurrentUser().id + "'"

    logging.info(userid + ' starting')
    where = '"userid"=' + userid + ' and "sended" > ToDate("{0}") and "needSend" = 1' .format(
        checkDate.strftime("%d/%m/%Y %H:%M:%S")) 
    
    docs = server.Get('Visit', where)
    if docs == None:
        logging.info(userid + ' No docs ' + where)
        return

    curl = server.Curl()

    answ = AnswerHandler(server, server.CurrentUser().id)
    agents = server.Get('Agents','','id')
    agentId = server.CurrentUser().id
    agentName = agents[agentId].name if agentId in agents else agentId

    vansw = server.New('VisitCloudResponse')

    server.ChangeUser(userid)
    orgs = server.Get('Org', '', 'id')
    priceList = server.Get('ManagerPrice', '')
    matrixes = server.Get('FaceMatrix', '', 'name')
    server.RestoreUser()
    
    priceBc : dict[str,str] = dict()
    for pi in priceList:
        priceBc[pi.barcode] = pi.id

    fdocList = server.New('Facing')
    clickDoc = server.New('ClickFacing')
    
    for doc in docs:
        if not doc.id in orgs: 
            continue

        if len(doc.items) == 0:
            logging.info('No photos')
            continue

        prevDate = doc.created + timedelta(days=-10)
        prevDocWhere = '"created" < ToDate("{0}") and "created" > ToDate("{1}") and "id"={2}'.format(
                        doc.created.strftime("%d/%m/%Y"),
                        prevDate.strftime("%d/%m/%Y"),
                        "'" + doc.id + "'")        
        visitAnsw = vansw.New()
        visitAnsw.userid = server.CurrentUser().id
        visitAnsw.created = doc.created
        visitAnsw.answ = ""
        visitAnsw.code = 0
        visitAnsw.id = doc.id
        
        answ.setDoc(doc)
        
        matrixName = orgs[doc.id].faceMatrix
        if not matrixName in matrixes:
            message = "Can't find matrix " + matrixName
            visitAnsw.answ = message
            visitAnsw.code = -1
            logging.info(message)
            continue

        orgMatrix = dict()
        for mi in matrixes[matrixName].items:
            orgMatrix[mi.id] = mi

        logging.info(userid + ' upload photos')
        reportId = UploadPhotos(doc, curl, answ, logging)
        logging.info(userid + ' geting report ' + str(reportId))
        
        if reportId <= 0: 
            continue

        report = GetReport(curl, reportId, logging)
        logging.info(userid + ' got report ' + str(reportId))
        if report == None:
            visitAnsw.answ = 'Error while geting report'
            visitAnsw.code = -1
            logging.info(userid + ' error get report')
            continue
        
    # product_code, faces_count
        fdoc = fdocList.New()
        fdoc.id = doc.id
        fdoc.created = doc.created
        fdoc.date = doc.date

        facingTotal, usedItems = CountReportItems(fdoc, report, clickDoc, priceBc, orgMatrix)
        
        # append items from matrix
        missed = FindMissing(orgMatrix, usedItems, fdoc, visitAnsw, userid)
        if len(missed) > 4:
            SendMissingData(missed, reportId, curl)

        prevTotal = CountPrevFacing(server, prevDocWhere)
        if prevTotal != None:
            logging.info(str(facingTotal) + ' ' + str(prevTotal))
            visitAnsw.changes = facingTotal - prevTotal


    if len(vansw) > 0:    
        server.Put(vansw)
        server.Write(vansw)
    
    if len(answ.answer) > 0:
        server.Write(answ.answer)
    
    if len(fdocList) > 0 or len(clickDoc) > 0:
        server.ChangeUser(userid)
        if len(clickDoc) > 0: server.Write(clickDoc)
        if len(fdocList) > 0: server.Write(fdocList)
        server.RestoreUser()
    

    logging.info(userid + ' finish')
    return
    
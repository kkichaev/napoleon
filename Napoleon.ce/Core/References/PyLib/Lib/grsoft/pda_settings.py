# -*- coding: cp1251 -*-

# import sys;
# 
# reload(sys);
# sys.setdefaultencoding("cp1251")

class ChangeData:
    __slots__ = ['created', 'id', 'oldvalue', 'newvalue']
    

class UserData:
    __slots__ = ['data', 'changes']
    
    def __init__(self):
        self.data = dict()
        self.changes = list()


keys = { '_PDA_Model' : 'Модель КПК', '_PDA_ID' : 'MAC-адрес' , 'login' : 'Логин', 'passw' : 'Пароль', 'port' : 'Порт', 
        'address' : 'IP-адрес', 'address2' : 'IP-адрес 2', 'saveReportsToCard' : 'Сохранять фото посещений на карте памяти', 
        'androidPhoto' : 'Фотографировать приложением андроид', 'gpsFrequience' : 'Время опроса', 'gpsDistance' : 'Изменение дистанции',
        'dataSendInBackground':'Фоновая синхронизация','gpsSendInterval':'Интервал GPS','waitGpsCoordOnRequest':'Ожидание коорд',
        'gps_valid_in_org' : 'Помнить координаты', 'dataDirShare' : 'dataDirShare'
        }

boolParams = ['dataSendInBackground', 'androidPhoto', 'saveReportsToCard', 'dataDirShare']
mlsToMin = ['gps_valid_in_org', 'gpsFrequience']

def GetHumanKey(key):
    return keys[key] if key in keys else ""

def BoolValue(value, trueStr, falseStr):
    return trueStr if value else falseStr

def GetHumanValue(key, value):
    if key in boolParams: return BoolValue(value, "да", "нет")
    if key in mlsToMin : return "" if value == "" else int(value) / (1000 * 60) 
    return value

def GetSettingInfo(server, agents, start, finish):
    userid = ""
    for ai in agents.split(','): userid += "'" + ai + "',"
    userid = userid[:-1]
    
    where = start + ";" + finish + ";" + userid
    docs = server.Get('PDASettingsData', where)
    
    result = dict()
    
    curUser = None
    userdata = None
    curdate = None
    curvalues = None
    for doc in docs:
        if curUser == None or curUser != doc.userid:
            curUser = doc.userid
            if userdata != None:
                result[curUser] = userdata
            userdata = UserData()
            curdate = None
            curvalues = dict()
            
        if curdate == None or curdate == doc.created:
            curdate = doc.created
            userdata.data[doc.id] = doc.value
            curvalues[doc.id] = doc.value
        elif curvalues[doc.id] != doc.value:
            cd = ChangeData()
            cd.created = doc.created
            cd.id = doc.id
            cd.newvalue = curvalues[doc.id]
            cd.oldvalue = doc.value
            userdata.changes.append(cd)
            
            curvalues[doc.id] = doc.value
    
    if userdata != None:
        result[curUser] = userdata
    return result    
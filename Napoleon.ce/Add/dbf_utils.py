import subprocess
import sys
import io
import random
from dbfpy import dbf

class Field :
    name = ""
    type = ""
    width = 0
    prec = 0
    
    DEFAULT_STRING_LENGTH = 200
    DEFAULT_NUMBER_LENGTH = 10
    
    # name;data;type;width;prec
    #
    # type is string|numeric|date|hex|timestamp|time
    #
    def __init__(self, fldStr):
        data = fldStr.split(';')
        if len(data) >= 5 :
            src = data[1] if len(data[1]) > 0 else data[0]
            self.name = src.upper()[:8]
            self.width = int(data[3])
            self.prec = int(data[4])
            if data[2] == 'string' :
                self.type = 'C'
                if self.width == 0 :
                    self.width = Field.DEFAULT_STRING_LENGTH
            elif data[2] == 'numeric' :
                self.type = 'N'
                if self.width == 0 :
                    self.width = Field.DEFAULT_NUMBER_LENGTH
            elif data[2] == 'date' :
                self.type = 'D'
                self.width = 8
            elif data[2] == 'hex':
                self.type = 'C'
                self.width = 6
            elif data[2] == 'timestamp':
                self.type = 'C'
                self.width = 8
            elif data[2] == 'time':
                self.type = 'C'
                self.width = 5

def GetExpFolder():
    folder = './'
    for line in io.open('GRServer.ini'):
        if line.find('exchangeFolder') >= 0 :
            vals = line.strip().split('=')
            folder = vals[1].strip().replace('\\', '/')
            if not folder.endswith('/') :
                folder += '/'

    return folder

def LoadTableFields(objectName):
    p = subprocess.Popen(["GRServer.exe", "--run-tray", "GetTableDef",objectName], stderr=subprocess.PIPE)
    p.wait()
    
    fields = []
    for line in p.stderr:
        fld = Field(line.strip())
        if len(fld.type) > 0 :
            fields.append(fld) 

    return fields

def CreateTable(fields, folder, tableName):
    baseName = folder + tableName
    if baseName.upper().endswith('.DBF') == False :
        baseName += '.DBF'
    db = dbf.Dbf(baseName, new=True)
    for fld in fields :
        db.addField((fld.name, fld.type, fld.width, fld.prec))
        
    return db


def UpdateTable(folder, tableName, addFields = None, removeFields = None, addFunc = None):
    TEMP_BASE = 'TEMPD.DBF'
    
    copiedFields = []
    addNames = []
    if not addFields is None: 
        for f in addFields: 
            addNames.append(f.name)
            
    dbRef = dbf.Dbf(folder + tableName, readOnly=True)
    dbDest = dbf.Dbf(folder + TEMP_BASE, new=True)
    
    for field in dbRef.header.fields:
        if (not removeFields == None and  field.name in removeFields) or field.name in addNames : 
            continue
        copiedFields.append(field)
        dbDest.addField(field)
        
    if not addFields is None: 
        for field in addFields:
            dbDest.addField((field.name, field.type, field.width, field.prec))
        
    for rec in dbRef:
        drec = dbDest.newRecord()
        
        for field in copiedFields:
            drec[field.name] = rec[field.name]
            
        if not addFunc == None:
            for field in addFields: 
                addFunc(drec, rec, field) 
        
        drec.store()
    
    dbRef.close()
    dbDest.close()

#def addFunc(drec, srec, field):
#    drec[field.name] = srec['ID']
    

#folder = GetExpFolder()
#UpdateTable(folder, 'WAREHOUS.DBF', removeFields = ['PATH'], 
#            addFields=[Field('test;test;string;20;0')],
#            addFunc = addFunc 
#            )

# objFields = LoadTableFields(sys.argv[1])
# if len(objFields) > 0 :
#     db = CreateTable(objFields, folder, sys.argv[2])
#     
#     colors = ['0', '88', 'FF00', 'FF0000' ]    
#     random.seed()
#     
#     dbRef = dbf.Dbf(folder + 'WAREHOUS.DBF', readOnly=True)
#     for srec in dbRef :
#         drec = db.newRecord()
#         drec["ID"] = srec["ID"]
#         drec["COLOR"] = colors[random.randint(0, len(colors) - 1)]
#         drec.store()
#     
#     db.close()
#     dbRef.close()

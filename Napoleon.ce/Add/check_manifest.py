import os
from os.path import join
import shlex
import io
import re
import xml.etree.ElementTree as ET
# from Tkinter import Tk
import tkinter as tk

classes = {}
minSDK = 14
filePath = re.compile(".*projectDir.*new\sFile\('(.*)'\)")

class Class:
    name = ""
    childs = None
    
    def __init__(self, name):
        self.name = name
        self.childs = []
        
    def addChild(self, child):
        for cl in self.childs:
            if cl.name == child.name :
                return
        self.childs.append(child)
        
    def getChilds(self):
#         print self.name + " have " + str(len(self.childs)) + " childs "
        
        ret = []
        for k in self.childs:
            ret.append(k.name)
            ret.extend(k.getChilds())
            
        return ret

def parseFile(fileName):
    package = None
    className = None
    parent = None
    imports = {}
    #print "Parse file " + fileName
    
    hFile = io.open(fileName, 'rt')
    lex = shlex.shlex(hFile)
    lex.wordchars += '._'
    for token in lex:
        if token == "package" :
            package = lex.get_token()
        elif token == "import":
            imp = lex.get_token()
            if imp.find('.') < 0 :
                imp = lex.get_token()
            names = imp.rsplit('.', 1) 
            if len(names) > 1 and  not names[1] == '*' and not names[1] in imports :
                imports[names[1]] = imp
#             else :
#                 print "no import " + imp + " file " + fileName
        elif token == "public" :
            tkn = lex.get_token() 
            if tkn == "abstract" : tkn = lex.get_token()
            if tkn == "class" : className = lex.get_token()
        elif token == "extends" :
            pn = lex.get_token()
            if not pn.find('.') == -1 :
                parent = pn
            elif pn in imports:
                parent = imports[pn]
            else :
                parent = package + '.' + pn
        elif token == '{':
            break
    if parent == None :
        parent = 'java.lang.Object'
        
    if className == None :
        return (className, None)
    return (package + '.' + className, parent) 
        
def loadSources(dirName):
    global classes
    for root, dirs, files in os.walk(dirName) :
        for name in files:
            if name.endswith('.java') :
                fileName = join(root, name)
                
                className, parent = parseFile(fileName)
                if className == None :
                    continue

                cls = None                
                if not className in classes:
                    cls = Class(className)
                    classes[className] = cls
                else : 
                    cls = classes[className]
                
                if parent in classes :
                    classes[parent].addChild(cls)
                else:
                    pc = Class(parent)
                    pc.addChild(cls)
                    classes[parent] = pc
                    
#                 if parent == 'android.app.Activity':
#                     print className


def readProject(dirName):
    projFile = dirName + '/project.properties'
    if os.path.isfile(projFile) :
        for line in io.open(projFile):
            if not line.find("android.library.reference") == -1 :
                refs = line.split('=')
                baseDir = refs[1].replace('\n', '')
#                 print "Read project " + baseDir
                readProject(dirName + '/' + baseDir)
    
    loadSources(dirName + '/src')
    
def getActivitiesList():
    ret = []
        
    readProject('.')

    activities = ['android.app.Activity', 'android.support.v4.app.FragmentActivity', 'android.app.TabActivity', 'android.app.ListActivity']

    for act in activities:
        if not act in classes:
            pass
#             print "No class " + act
        else :
            cls = classes[act]
            ret.extend(cls.getChilds())
    return ret

def readManifestActivities(root):
    ret = []
    package = root.attrib['package']
    for activity in root.iter('activity'):
        name = activity.attrib['{http://schemas.android.com/apk/res/android}name']
        if name.startswith('.') :
            name = package + name
        elif name.find('.') < 0 :
            name = package + '.' + name
            
        ret.append(name)
    
    global minSDK
    if minSDK == 0:
        for sdk in root.iter('uses-sdk'):
            minSDK = int( sdk.attrib['{http://schemas.android.com/apk/res/android}minSdkVersion'])
        
    return ret

def readStudioProject(dirName):
    projFile = dirName + '/settings.gradle'
    if os.path.isfile(projFile) :
        for line in io.open(projFile):
            m = filePath.match(line)
            if m != None:
                baseDir = m.group(1)
                if baseDir.find('itext') != -1:
                    continue 
#                print "Read project " + baseDir
                readProject(dirName + '/' + baseDir)
    
    loadSources(dirName + '/app/src/main/java')

def getActivitiesListStudio():
    ret = []
        
    readStudioProject('.')

    activities = ['android.app.Activity', 'androidx.fragment.app.FragmentActivity', 'android.app.TabActivity', 'android.app.ListActivity']

    for act in activities:
        if not act in classes:
            pass
#             print "No class " + act
        else :
            cls = classes[act]
            ret.extend(cls.getChilds())
    return ret

if os.path.exists('gradlew'):
    root = ET.parse('app/src/main/AndroidManifest.xml').getroot()
    manifestAct = readManifestActivities(root)
    activities = getActivitiesListStudio()
else:
    root = ET.parse('AndroidManifest.xml').getroot()
    manifestAct = readManifestActivities(root)
    activities = getActivitiesList()

foundServiceW = False
foundService = False
for serv in root.iter('service'):
    name = serv.attrib['{http://schemas.android.com/apk/res/android}name']
    if name.endswith('NapoleonServiceW') :
        foundServiceW = True
    if name.endswith('NapoleonService') :
        foundService = True

done = True

result = ""

notWarryActivities = ['com.grsoft.util.SettingActivity', 'com.grsoft.view.BaseActivity', 'com.grsoft.view.RegDurationActivity']

for act in activities:
    if act not in manifestAct:
        if act.endswith('BaseFragmentActivity') : continue

#        cls = classes[act]
#        print act, cls.childs
#        if len(cls.childs) <= 2 or act.endswith('W') :
        if not (act in notWarryActivities) or act.endswith('W') :
            done = False
            configStr = 'keyboard|keyboardHidden|orientation'
            if minSDK > 12: 
                configStr += '|screenSize'
            str =  '         <activity android:name="' + act + '" android:configChanges="' + configStr + '"/>'
            result += str + '\n'
            print( str)

if not foundServiceW :
    str = '         <service android:name="com.grsoft.util.NapoleonServiceW"/>'
    result += str + '\n'
    print( str)
    done = False         
if not foundService :
    str = '         <service android:name="com.grsoft.util.NapoleonService"/>'
    result += str + '\n'
    print (str)
    done = False         

r = tk.Tk()
r.withdraw()
r.clipboard_clear()
r.clipboard_append(result)
r.destroy()    

exit((0 if done else 1))
    
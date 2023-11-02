# -*- coding: cp1251 -*-

import tempfile
import zipfile
import os
import sys
import site
import types

MODULE_INFO_TYPE = "ModuleInfoType[name:s,kind:s,flavor:s,description:s,parameters[name:s,type:n,flags:n]]"

class module_info:
    class param:
        # типы параметров
        TYPE_STRING = 0
        TYPE_AGENT_ID = 1
        TYPE_DATE = 2
        TYPE_TIME = 3
        TYPE_TIMESTAMP = 4

        # флаги параметров
        FLAGS_NONE = 0

        # аттрибуты параметров
        name = ""
        type = TYPE_STRING
        flags = FLAGS_NONE

        def __init__(self, name, type=TYPE_STRING):
            self.name = name
            self.type = type

        def add_to_result(self, res):
            prm = res.parameters.New()
            prm.name = self.name
            prm.type = self.type
            prm.flags = self.flags


    # аттрибуты модулей    
    name=""
    parameters=[]
    kind = "report"
    flavor = ""
    description = ""

    def __init__(self, name):
        self.name = name
        self.parameters = []

    def __str__(self):
        str = "ModuleInfo Name: " + self.name + " Kind: " + self.kind
        return str

    def add_to_result(self, result):
        res = result.New()
        res.name = self.name
        res.kind = self.kind
        res.description = self.description
        res.flavor = self.flavor
        for p in self.parameters:
            p.add_to_result(res)

def doInstall(result, name, filedata):
    tempdir = tempfile.gettempdir()

    ri = result.New()

    ri.name = name
    ri.result = 1

    try:
        print "copy " + name + " to " + tempdir

        filename = tempdir + "/" + name
        f = open(filename, 'wb')
        f.write(filedata)
        f.close()

        print "extract data..."
        zf = zipfile.ZipFile(filename, 'r')
        zf.extractall(tempdir)
        zf.close()

        dirname = filename[:filename.rfind('.')]
        cwd = os.getcwd()
        os.chdir(dirname)
        sys.argv = ['', 'install']
        execfile('setup.py')
        os.chdir(cwd)
    except:
        ri.result = 0
        #print str(sys.exc_info()[1])
        ri.message = str(sys.exc_info()[1])

def doModuleList(result):
    dirname = os.path.dirname(__file__)
    for f in os.listdir(dirname) :
        if os.path.isfile(dirname + "/" + f) and f.endswith('.py'):
            doTestModule(result, f[:-3])
        elif os.path.isdir(dirname + "/" + f) :
            doTestModule(result, f)

def doTestModule(result, module_name):
    try:
        test_module = __import__(module_name)
        test_attr = getattr(test_module, 'get_module_info', None)
        if test_attr != None and isinstance(test_attr, types.FunctionType):
            mi = test_module.get_module_info()
            if mi != None:
                mi.add_to_result(result)
    except:
        print sys.exc_info()[1]        
        pass

        
def run(server):
    param = server.Params[0]
    if param.command.lower() == "install" :
        server.RegisterType("InstallResult[name:s,result:n,message:s]")
        result = server.New("InstallResult")

        for item in param.data:
            doInstall(result, item.name, item.file)

        server.Put(result)
    elif param.command.lower() == "list" :
        server.RegisterType(MODULE_INFO_TYPE)
        result = server.New("ModuleInfoType")

        doModuleList(result)
        server.Put(result)

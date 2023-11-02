from distutils.core import setup, Extension

module1 = Extension('grserver',
                    define_macros = [('UNIX', '1')],
                    include_dirs = ['/home/ert/Documents/Napoleon/Include', '/home/ert/Documents/Napoleon/Lib.pc/Include', '/home/ert/Documents/Napoleon/Lib.pc/Unix', '/home/ert/Documents/Napoleon/GRServer/Include', '/home/ert/Documents/Python-2.7/Include'],
                    libraries = ['pc'],
                    library_dirs = ['/home/ert/Documents/Napoleon/Build/bin/Release'],
                    sources = ['Source/Python.cpp'])

setup (name = 'grserver',
       version = '1.0',
       description = 'GRServer package',
       ext_modules = [module1])

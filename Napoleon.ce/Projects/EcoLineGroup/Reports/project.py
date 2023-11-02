# -*- coding: cp1251 -*-
from objects import *

doclist = list()

__inited__ = False

if not __inited__:
    __initted = True
    
    doclist.append(Order)
    doclist.append(VisitInfo)
    doclist.append(OrgRemnants)
    doclist.append(Distribution)
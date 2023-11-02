# -*- coding: cp1251 -*-

import manager.summary

from docs import InitDocuments

def run(server):
   InitDocuments()

   manager.summary.run(server)

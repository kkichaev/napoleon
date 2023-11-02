class RObj(object):
    server = None
    def __init__(self, server):
        self.server = server
        
    def process(self, doc):
        print(doc)
        
    def putSrv(self):
        print(self)    
        
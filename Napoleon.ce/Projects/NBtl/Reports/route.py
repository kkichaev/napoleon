import manager.route

from docs import InitDocuments

def run(server):
    InitDocuments()
    manager.route.run(server)
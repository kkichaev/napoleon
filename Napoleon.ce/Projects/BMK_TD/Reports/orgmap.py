class OrgMap:
  def __init__(self, server):
    self.server = server
    self.orgs = {}
    self.server = server
    
    porg = server.Get("PotenzialOrg", 'not "userid" is null', "id")
    self.orgs.update(porg)
    
  def getOrg(self, id, userid):
    if not id in self.orgs:
      self.server.ChangeUser(userid)
      aorgs = self.server.Get("Org", '', 'id')
      self.server.RestoreUser()
      
      self.orgs.update(aorgs)
    
    if not id in self.orgs:
      o = self.server.New("Org").New()
      o.name = "Контрагент с кодом <{0}>".format(id)
      o.id = id
      self.orgs[id] = o

    return self.orgs[id]
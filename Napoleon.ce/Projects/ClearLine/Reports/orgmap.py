class OrgMap:
  def __init__(self, server):
    self.server = server
    self.orgs = {}
    self.porgs = []
    self.server = server
    
    # porg = server.Get("PotenzialOrg", 'not "userid" is null', "id")
    # for po in porg.values():
    #     if len(po.orgCreateInn) > 0:
    #         po.name += " / " + po.orgCreateInn
    # self.orgs.update(porg)
    
  def getOrg(self, id, userid):
    if not id in self.orgs:
      self.server.ChangeUser(userid)
      aorgs = self.server.Get("Org", '', 'id')
      self.server.RestoreUser()
      
      self.orgs.update(aorgs)
    
    if not id in self.orgs:
      if not userid in self.porgs:
        self.porgs.append(userid)
        porg = self.server.Get("PotenzialOrg", '"userid"=' + "'" + userid + "'", "id")
        if porg != None:
          for po in porg.values():
              if len(po.orgCreateInn) > 0:
                  po.name += " / " + po.orgCreateInn
          self.orgs.update(porg)

    if not id in self.orgs:
      o = self.server.New("Org").New()
      o.name = "Контрагент с кодом <{0}>".format(id)
      o.id = id
      self.orgs[id] = o

    return self.orgs[id]
class Division:
    class Agent:
        def __init__(self, src) -> None:
            self.name:str = src.agent
            self.id:str = src.userid

        def __repr__(self) -> str:
            return str(self.__dict__)


    def __init__(self, src) -> None:
        self.id:int = int(src.divisionid)
        self.parent:int = int(src.parent)
        self.name:str = src.division

        self.childs:list[Division] = []

        agents:list[Division.Agent] = []
        for a in src.agents:
            if len(a.userid) > 0:
                agents.append(Division.Agent(a))

        self.agents = sorted(agents, key=lambda x: x.name)

    def addChild(self, d) -> None:
        self.childs.append(d)

    def __repr__(self) -> str:
        return str(self.__dict__)

    def sort(self) -> None:
        self.childs = sorted(self.childs, key=lambda x: x.name)
        for d in self.childs:
            d.sort()
        

class Divisions:
    def __init__(self, server) -> None:
        stmt = '''
        SELECT di."id" userid, d."id" divisionid, d."parent", a."name" as agent, d."name" as division
        FROM "Division" d 
        LEFT JOIN "Division$agents" di on d."id" = di."Division$id" 
        LEFT JOIN "Agents" a on a."id" = di."id"
        '''

        docs = server.Query(stmt, 'Dvs[division:s,divisionid:n,parent:n,agents(divisionid)[userid:s,agent:s]]')

        self.divisions:dict[int,Division] = {}
        for d in docs:
            dv = Division(d)
            self.divisions[dv.id] = dv

        for d in self.divisions.values():
            if d.parent in self.divisions: self.divisions[d.parent].addChild(d)

        for d in self.divisions.values():
            d.sort()

    def get(self, id:int) -> Division|None:
        return self.divisions[id] if id in self.divisions else None

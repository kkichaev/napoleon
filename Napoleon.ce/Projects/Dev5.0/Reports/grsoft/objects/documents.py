class BaseDocument:

    # userid, created, id, date, remark, sended, sum, docname, cr_day

    def makeSelect(self, where:str) -> str :
        stmt = '''
            SELECT "userid" as userid, "created" as created, dt."id" as id
            , "date" as date, "remark" as remark, "sended" as sended, {0} as sum, '{1}' as docname 
            , ("created" / 10000000) / (24 * 3600) as cr_day
            FROM "{2}" as dt     
            '''.format(self.sum(), self.docName(), self.table())

        chTable = self.childTable()
        if chTable :
            stmt += '''
                , "{0}{1}" di
                WHERE di."{0}$userid" = dt."userid" AND di."{0}$created" = dt."created" {2}
                GROUP BY di."{0}$userid", di."{0}$created"
                ''' .format (self.table(), '$' + chTable, "AND " + where if len(where) > 0 else "")
        else:
            if len(where) > 0:
                stmt += "WHERE " + where
    
        return stmt

    def sum(self) -> str : return "0"  
    def docName(self) -> str : return "None"
    def table(self) -> str : return "None"
    def childTable(self) -> str : return None


class Visit(BaseDocument):
    def docName(self) -> str: return "Фотоотчет"
    def table(self) -> str: return "Visit"

class Answer(BaseDocument):
    def docName(self) -> str: return "Анкета"
    def table(self) -> str: return "Answer"

class Order(BaseDocument):
    def sum(self) -> str: return 'sum("cost"*"qty")'
    def docName(self) -> str: return "Заявка"
    def table(self) -> str: return "Order"
    def childTable(self) -> str: return "items"

class Returns(BaseDocument):
    # def sum() -> str: "sum(cost*qty)"
    def docName(self) -> str: return "Возврат"
    def table(self) -> str: return "Returns"
    def childTable(self) -> str: return "items"


# userid, created, id, date, remark, sended, sum, docname, cr_day
def makeDocQuery(filter:str="", documents:list[BaseDocument]=None) -> str:
    stmt = ""
    docs : list[BaseDocument] =  documents or [Visit(), Answer(), Order(), Returns()]

    for d in docs :
        # print(d.docName(), d.childTable(), d.table())
        if len(stmt) > 0 :
            stmt += " UNION ALL "
        s1 = d.makeSelect(filter)
        stmt += s1

    return stmt
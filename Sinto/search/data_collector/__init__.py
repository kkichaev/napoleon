import sqlite3
from sqlite3.dbapi2 import Connection
from sys import maxsize
from typing import  NewType
import re

BASE_DATA_FOLDER = 'data'

# categories
QUERY = "query"
MOTHERBOARD = 'motherboard'
CPU = 'cpu'
MEMORY = 'memory'
VIDEOCARD = 'videocard'

# блок питания
POWER_SUPPLY = 'power_supply'

# корпус 
BOX = 'box'

HDD = 'hdd'
MONITOR = 'monitor'

def strip(data:str) ->str:
    return data.strip().replace('&nbsp;', '')

def stripTags(data:str) ->str:
    return re.sub('<.+?>',' ',data).strip()

def chekBarndDataIntersects(db:Connection):
    stmt = '''
    select b.value from
        (select distinct value, "key" from data where "key" = 'brand') b,
        (select distinct value, "key" from data where "key" <> 'brand') d
    where b.value = d.value
    '''

    for r in db.execute(stmt):
        print('Intersects brand with data ', r[0])

def updateRatings(db:Connection):
    stmt = '''
    select max(ord) rating, category from data, head, urls
        where data.id = head.id and head.url = urls.url
    group by category
    '''

    rows = db.execute(stmt)
    for r in rows:
        rate = 2 << r[0]

        db.execute("replace into ratings (section, rating, category) values ('model',?,?)", (rate, r[1]))
        db.execute("replace into ratings (section, rating, category) values ('brand',?,?)", (rate *2, r[1]))

    db.commit()

def openDB() -> Connection:
    con = sqlite3.connect('search.db')

    cursor = con.cursor()
    cursor.execute('PRAGMA foreign_keys = ON')

    cursor = con.cursor()

    stmt = '''
    CREATE TABLE IF NOT EXISTS urls(
        seller text, 
        category text,
        url text, 
        file text,
        loaded integer,
        handled integer,
        parsed integer,
        primary key(url))
    '''
    cursor.execute(stmt)

    stmt = '''
    CREATE TABLE IF NOT EXISTS head(
        id integer primary key autoincrement, 
        url text REFERENCES urls(url) ON DELETE RESTRICT, 
        category text, 
        brand text,
        name text,
        UNIQUE(url))
    '''
    cursor.execute(stmt)

    stmt = '''
    CREATE TABLE IF NOT EXISTS data(
        id integer REFERENCES head(id) ON DELETE RESTRICT, 
        key text, 
        value text,
        ord integer)
    '''
    cursor.execute(stmt)

    stmt = '''
    CREATE TABLE IF NOT EXISTS suffix(
        match text primary key,
        len integer,
        value text)
    '''
    cursor.execute(stmt)

    stmt = '''
    CREATE TABLE IF NOT EXISTS ratings(
        section text,
        category text,
        rating real,
        primary key(section, category))
    '''

    # insert into suffix (match, len) values ('wifi', 2)

    cursor.execute(stmt)
    stmt = '''
    CREATE TABLE IF NOT EXISTS synonyms(
        src text primary key,
        dest text)
    '''
    cursor.execute(stmt)

    synm = [
        ('мб', 'mb'),
        ('мбайт', 'mb'),
        ('мегабайт', 'mb'),
        ('mbyte', 'mb'),
        ('гб', 'gb'),
        ('гбайт', 'gb'),
        ('гигабайт', 'gb'),
        ('gbyte', 'gb'),
        ('°с', '°c'),
        ('бит', 'bit'),
        ('гбит', 'gbit'),
        ('мбит', 'mbit'),
        ('o.c', 'mhz'),        
        ('мгц', 'mhz'),        
        ('ггц', 'ghz'),        
    ]
    for el in synm:
        con.execute('replace into synonyms (src, dest) values (?,?)', el)

    con.commit()

    return con

# (match -> suffix offset, match->commonsuffix)
def loadSuffixes(con:Connection) -> tuple[dict[str,int], dict[str,str]]:
    matcher : dict[str, int] = dict()
    suffix : dict[str, str] = dict()
    for r in con.execute('select match, len, value from suffix'):
        suf = r[2]
        m = r[0]
        if suf == None or len(suf) == 0:
            matcher[m] = r[1]
        else:
            suffix[m] = suf

    return (matcher, suffix)

class SuffixMatcher:
    def subKey(self, key:str) ->str :
        return key[:4]

    def __init__(self, con:Connection) -> None:
        matcher, self.suffix = loadSuffixes(con)

        stmt = '''select src, dest from synonyms'''

        self.synonyms : dict[str, str] = dict()
        for r in con.execute(stmt):
            self.synonyms[r[0]] = r[1]

        self.matcher : dict[str,dict[str,int]] = dict()
        for k,v in matcher.items():
            subKey = self.subKey(k)
            if not subKey in self.matcher: self.matcher[subKey] = dict()
            self.matcher[subKey][k] = v

    def sub(self, value:str) -> list[str]:
        ret : list[str] = list()
        matching = False

        if value in self.synonyms: value = self.synonyms[value]

        subKey = self.subKey(value)
        if subKey in self.matcher:
            for m, mLen in self.matcher[subKey].items():
                if value == m:
                    matching = True
                    ret.append(value[:-mLen])
                    ret.append(value[-mLen:])
                    # print('match ', value, m, mLen)
                    break
        if not matching:
            for m, s in self.suffix.items():
                if value.endswith(s) and re.match(m, value) != None:
                    matching = True
                    mLen = len(s)
                    ret.append(value[:-mLen])
                    ret.append(value[-mLen:])
                    # print('suffix ', value, s)
                    break
        if not matching:
            ret.append(value)

        return ret

def normalizeValues(con:Connection):
    def updateGroup(id, key, values:list[str]):
        ord = 1
        con.execute('delete from data where id = ? and key = ?', (id, key))

        for v in values:
            v = v.strip()
            if len(v) != 0:
                con.execute('insert into data (id, key, value, ord) values(?,?,?,?)', (id, key, v, ord))
                ord += 1

        # print('Update ', id, key, values)
        con.commit()

    matcher = SuffixMatcher(con)
    # matcher, suffix = loadSuffixes(con)
    if len(matcher.matcher) == 0 and len(matcher.suffix) == 0: return

    count = 1
    curKey = None
    groupValues : list[str] = list()
    needUpdate = False
    for r in con.execute('select id, key, value from data order by id, key'):
        key = str(r[0]) +'<sep>' + r[1]
        value = r[2]

        if curKey != key:
            if needUpdate: 
                keys = curKey.split('<sep>')
                id = int(keys[0])
                updateGroup(id, keys[1], groupValues)
            groupValues = []
            needUpdate = False
            curKey = key

        data = matcher.sub(value)
        if len(data) > 1: 
            needUpdate = True
            # print(value, data)
        groupValues.extend(data)

        if (count % 1000) == 0 : print(count)
        count += 1


def updateSuffix(con:Connection) :
    newVariants : dict[str,set[str]] = {}
    varCount : dict[str,int] = {}

    commonSuffix : set[str] = set()
    existsVariants: dict[str, set[str]] = {}


    def inspectSuffix():
        # brands : set[str] = set()
        # stmt = 'select distinct barnd from head'
        # for r in con.execute(stmt):
        #     brands.add(r[0].lower())

        stmt = 'select distinct value from data where length(value) > 1 order by value'

        probe : list[str] = list()
        for r in con.execute(stmt):
            val = r[0]
            
            if len(probe) == 0: 
                probe.append(val)
                continue
            
            # check only
            if re.match(r'.*\d\D.*|.*\D\d.*',val) == None:
                probe.append(val)
                continue

            checkBase = probe[-1]
            if val.startswith(checkBase):
                valRest = val[len(checkBase):]
                if valRest[0].isdigit() or valRest[0] == '.': 
                    probe.append(val)
                    continue
                if not checkBase in newVariants: newVariants[checkBase] = set()
                newVariants[checkBase].add(valRest)
            else: 
                probe = probe[:-1]

        # print(suffix)
        # return suffix

    def load():
        matcher, suffixes = loadSuffixes(con)
        for s in suffixes.values(): commonSuffix.add(s)
        for m, ln in matcher.items():
            mbase = m[:-ln]
            if not mbase in existsVariants: existsVariants[mbase] = set()
            existsVariants[mbase].add(m[-ln:])

    def saveSuffixes():
        stmt = 'replace into suffix (match, len, value) values (?,?,?)'

        # update var counts from exists suffix
        for suf in existsVariants.values():
            for s in suf:
                if s in varCount: varCount[s] += 1
                else: varCount[s] = 1

        SUFFIX_THRESHOLD = 6
        for k, v in varCount.items():
            if v >= SUFFIX_THRESHOLD:
                if k.startswith('ddr'): continue

                commonSuffix.add(k)
                match = '.*\D' if k[0].isdigit() else '.*\d'
                match += k + '(\s|$)'

                con.execute(stmt, (match, len(k), k))
        for k, suffixes in newVariants.items():
            if k in existsVariants: existsVariants[k].union(suffixes)
            else: existsVariants[k] = suffixes

        # recreate suffix
        print(existsVariants)
        con.execute('delete from suffix where value is null')
        for k, suffixes in existsVariants.items():
            for v in suffixes.difference(commonSuffix):
                con.execute(stmt, (k+v, len(v), None))

        con.commit()
        # print(len(allVariants), len(varCount))
        # print(allVariants, varCount)

    def updateVariants(curValue:str, variants:list[str]):
        if curValue == None or len(variants) == 0: return
        newVariants[curValue] = variants

    load()

    stmt = '''
select distinct data.value, d.value from data,  
    (select d1.id, d1.key, d.value, d.ord from
        (select id, key, count(data.value) as ctr from data group by id, key having count(data.value) > 1) d1,
        (select id, value, ord, key from data where length(value) > 1) d
    where d1.id = d.id and d1.key = d.key) d
where data.id = d.id and data.key = d.key and data.ord = d.ord + 1
order by d.value
    '''

    curValue = None
    variants = None
    
    for r in con.execute(stmt):
        val = r[1]
        variant = r[0]

        if variant in commonSuffix: continue
        if val in existsVariants and variant in existsVariants[val]: continue

        mayBeVariant = val[-1].isdigit() and not variant[0].isdigit()
        
        if not mayBeVariant: continue

        if not variant in varCount: varCount[variant] = 1
        else: varCount[variant] += 1

        if curValue == None or curValue != val: 
            updateVariants(curValue, variants)
            curValue = val
            variants = set()
        variants.add(variant)

    updateVariants(curValue, variants)
    inspectSuffix()
    # print(varCount)
    # print(newVariants)
    saveSuffixes()

def rectifyValue(value:str, key:str, category:str, matcher:SuffixMatcher) -> list[str]:
    value = value.lower()
    value = re.sub(r'(\d+),(\d{1,2})([^\d]+|$)',r'\1.\2\3', value) # replace 3,50 to 3.50
    value = re.sub(r'-|\+|;|\*|:| x |[][(){}\s/\\]',' ',value)
    value = re.sub(r'(\. )|(, )', ' ', value)

    value = value.replace('°с', '°c')

    # remove last dot
    value = re.sub('\.( )*$','',value)

    checkEcc = key.find('ecc') >= 0 and category == MEMORY
    res : list[str] = list()
    for v in re.split(' +', value.strip()):
        if len(v) < 1 : continue
        if checkEcc and v.find('не') == -1: v = 'ecc'
        res.extend(matcher.sub(v))
        
        if category == QUERY and v.startswith("lga"):
            res.append('lga')
            res.append(v[3:])
    
    # print(res)
    return res

def putData(con:Connection, url:str, category:str, brand:str, model:str, data:list[dict[str,str]]):
    matcher = SuffixMatcher(con)

    def prepareKey(key:str):
        return key.lower().replace('количество','').replace('кол-во','').strip()

    def modelToParams(model:str, category:str) -> list[str]:
        value = model.replace('Характеристики', '').replace('Материнская плата', '')
        res = rectifyValue(value, "model", category, matcher)
        
        if category == MEMORY:
            for v in res:
                match = re.match(r'ddr\d.*', v)
                if match != None:
                    res.remove(match.group(0))

        return res

    cursor = con.cursor()
    id = None
    for row in con.execute('select id from head where url = ?', (url,)):
        id = row[0]
    if id == None :
        stmt = "insert into head(url, category, brand, name) values(?,?,?,?)"
        cursor.execute(stmt, (url, category, brand, model))
        id = cursor.lastrowid

    # print('id',id)
    cursor.execute('delete from params where id = ?', (id,))
    cursor.execute('delete from data where id = ?', (id,))

    stmt = "insert into data(id, key, value, ord) values (?,?,?,?)"
    for di in data:
        k = prepareKey(di['key'])
        value = di['value']
        ord = 1
        for v in rectifyValue(value, k, category, matcher):
            cursor.execute(stmt, (id, k, v, ord))
            ord += 1

    # put model
    ord = 1
    for mi in modelToParams(model.lower(), category):
        if len(mi) < 1: continue
        cursor.execute(stmt, (id, 'model', mi, ord))
        ord += 1

    # put brand
    cursor.execute(stmt, (id, 'brand', brand.lower(), 1))

    stmt = "update urls set parsed = strftime('%s', 'now') where url = '{0}'".format(url)
    cursor.execute(stmt)
    con.commit()    

class Logger:
    def __init__(self, fileName) -> None:
        self.name = fileName

    def add(self, message):
        f = open(self.name, 'a')
        f.write(message + "\n")
        f.close()


CategoryId = NewType('CategoryId', str)

class UrlData:
    def __init__(self, url:str, category:str, seller:str, rating:float, brand:str, model:str) -> None:
        self.url = url
        self.rating = "{:0.2f}".format(rating)
        self.category = category
        self.seller = seller
        self.brand = brand
        self.model = model.replace('Характеристики ', '')
    
    def __repr__(self) -> str:
        return self.__str__()

    def __str__(self) -> str:
        return str(self.__dict__)

def getUrls2(db: Connection, query:str) -> list[UrlData]:
    def makeSubquery(value:str, suffix:str):
        return '''
   union all
   select d1.id, d1.value, d1.rating + ifnull(d2.rating, 0) as rating from
     (select id, key, value, ifnull(rating, 1.0) as rating, ord from 
        (select data.id, key, value, ord, category from data, head, urls where data.id = head.id and head.url = urls.url) data 
           left join ratings on key = section and data.category = ratings.category
      where value = '{0}') d1 
   left join
     (select id, key, value, ifnull(rating, 1.0) as rating, ord from 
        (select data.id, key, value, ord, category from data, head, urls where data.id = head.id and head.url = urls.url) data 
           left join ratings on key = section and data.category = ratings.category
     where value = '{1}') d2 
   on d1.id = d2.id and d1.key = d2.key and d2.ord = d1.ord + 1
        '''.format(value, suffix)

    ret : list[UrlData] = list()

    matcher = SuffixMatcher(db)
    qtokens = rectifyValue(query, "", QUERY, matcher)

    suffix : set[str ]= set()
    for r in db.execute('select value from suffix where not value is null'):
        suffix.add(r[0])
        # print(r[0])

    # print(suffix)
    subquery = ''

    tparams = []
    for token in qtokens:
        if token in suffix and len(tparams) > 0:
            value = tparams[-1]
            del tparams[-1]
            value = value[1:-1]
            sq = makeSubquery(value, token)
            subquery += sq
            # print(value, token)
        else:
            tparams.append("'" + token + "'")
    stmt = '''
select urls.url, seller, rating, brand, name, category from head, urls, (
select id, sum(rating) as rating from(
   select id, value, max(ifnull(rating, 1.0)) as rating from
    (select data.id, key, value, urls.url, urls.category from data, head, urls 
       where data.id = head.id and head.url = urls.url and value in ({0})
     ) data   
   left join ratings 
   on data.key = ratings.section and ratings.category = data.category group by id, value 
   {1}
   )
group by id) data
where data.id = head.id and urls.url = head.url
order by rating desc
        '''.format(','.join(tparams), subquery)

    # print(stmt)

    cursor = db.cursor()
    # print (stmt)
    res = cursor.execute(stmt)
    minRating = None
    delta = 0.1
    for row in res:
        rating = row[2]
        if minRating == None: minRating = (1 - delta) * rating
        elif rating < minRating: break

        category = row[5]
        u = UrlData(row[0], category, row[1], rating, row[3], row[4] )
        ret.append(u)

    return sorted(ret, key=lambda x:x.rating, reverse=True)

def softSearch(db: Connection, query:str) -> list[UrlData]:
    ret : list[UrlData] = list()

    matcher = SuffixMatcher(db)
    qtokens = rectifyValue(query, "", QUERY, matcher)

    haveBrand = None
    brandFilter = ''
    tparams = []
    for token in qtokens:
        if haveBrand == None:
            stmt = '''
            select count(*) from data d  where value = '{0}' and "key" = 'brand'
            '''.format(token)

            for r in db.execute(stmt):
                haveBrand = r[0] > 0
        if haveBrand:
            brandFilter = " and h.brand like '{0}'".format(token)
            break

        tparams.append("'" + token + "'")

    stmt = '''
select sum(r.rating / (1 << d.ord - 1)) as rating, count(d.id) as matchCount,
    u.url, u.seller, u.category, h.brand, h.name
    from data d, head h, urls u, ratings r
where d.id = h.id and h.url = u.url and r.section = d.key and r.category = u.category 
    and d.value in ({0}) {1}
group by u.url
having min(d.ord) = 1
order by rating desc
        '''.format(','.join(tparams),brandFilter)

    print (stmt)
    res = db.execute(stmt)
    
    haveMatched = None
    tokens = len(tparams)
    minRating = 0

    delta = 0.1
    for row in res:
        rating = row[0]
        matchCount = row[1]
        if haveMatched == None:
            haveMatched = matchCount == tokens
            minRating = (1 - delta) * rating
        
        if haveMatched:
            if matchCount != tokens: break
        if rating < minRating: break

        url = row[2]
        seller = row[3]
        category = row[4]
        brand = row[5]
        model = row[6]
        u = UrlData(url, category, seller, rating, brand, model )
        ret.append(u)

    return sorted(ret, key=lambda x:x.rating, reverse=True)

def filterTokens(db: Connection, tokens : list[str]) -> list[str] :
    stmt = '''
    select value, min(ord) from data where value in ({0}) group by value
    '''.format(','.join(tokens))

    exists_t = []
    res = db.execute(stmt)
    for row in res:
        exists_t.append(row[0])

    ret = []
    for x in tokens:
        if x[1: -1] in exists_t: ret.append(x)

    return ret


def getUrls(db: Connection, query:str) -> list[UrlData]:

    ret : list[UrlData] = list()

    matcher = SuffixMatcher(db)
    qtokens = rectifyValue(query, "", QUERY, matcher)

    # suffix : set[str ]= set()
    # for r in db.execute('select value from suffix where not value is null'):
    #     suffix.add(r[0])
    #     # print(r[0])

    tparams = []
    for token in qtokens:
        tparams.append("'" + token + "'")

    tparams = filterTokens(db, tparams)
    if len(tparams) == 0:
        return ret


    stmt = ''
    if len(tparams) == 1 :
        stmt = '''
    select rating, matchCount, h.url, seller, h.category, brand, name from
        (select d.id, rating, 1 as matchCount from data d, head h, ratings r
            where d.id = h.id and h.category = r.category and r.section = d."key" and d."key" = 'brand' and d.value = {0}
        union all
        select d.id, rating, 1 as matchCount from data d, head h, ratings r
            where d.id = h.id and h.category = r.category and r.section = d."key" and d."key" = 'model' and d.ord = 1 
                and d.value = {0}) d,   
        head h, urls u
    where d.id = h.id and h.url = u.url 
    order by rating desc, matchCount desc
    '''.format(tparams[0])
    else:
        brandStmt = '''
    select d.id, d.rating + ifnull(v.rating,0) as rating, d.matchCount + ifnull(v.matchCount, 0) as matchCount from
    (select b.id, b.rating + d.rating as rating, 2 as matchCount from
        (select d.id, rating from data d, head h, ratings r
            where d.id = h.id and h.category = r.category and r.section = d."key" and d."key" = 'brand' and d.value = {0}) b,
        (select d.id, rating from data d, head h, ratings r
            where d.id = h.id and h.category = r.category and r.section = d."key" and d."key" = 'model' and d.value = {1} and d.ord = 1) d
    where b.id = d.id) d
    left join 
    (select sum(r.rating / (1 << d.ord - 1)) as rating, count(d.id) as matchCount, d.id from data d, head h, ratings r
        where d.id = h.id and r.section = d."key" and r.category = h.category and d.ord <> 1 and d."key" = 'model' 
            and d.value in ({2}) group by d.id) v
    on d.id = v.id '''.format(tparams[0], tparams[1], "'not a value'" if len(tparams) < 3 else ','.join(tparams[2:]))

        noBrandStmt = '''
    select d.id, d.rating + ifnull(v.rating,0) as rating, d.matchCount + ifnull(v.matchCount, 0) as matchCount from
    (select d.id, rating, 1 as matchCount from data d, head h, ratings r
        where d.id = h.id and h.category = r.category and r.section = d."key" and d."key" = 'model' and d.value = {0} and d.ord = 1) d
    left join 
    (select sum(r.rating / (1 << d.ord - 1)) as rating, count(d.id) as matchCount, d.id from data d, head h, ratings r
        where d.id = h.id and r.section = d."key" and r.category = h.category and d.ord <> 1 and d."key" = 'model' 
            and d.value in ({1}) group by d.id) v
    on d.id = v.id '''.format(tparams[0], ','.join(tparams[1:]))

        stmt = '''
        select rating, matchCount, h.url, seller, h.category, brand, name from
            ({0}
            union all
            {1}) d,   
            head h, urls u
        where d.id = h.id and h.url = u.url 
        order by rating desc, matchCount desc
        '''.format(brandStmt, noBrandStmt)

    # print (stmt)
    res = db.execute(stmt)
    
    matched = None
    delta = 0.1
    for row in res:
        rating = row[0]
        matchCount = row[1]
        if matched == None:
            matched = matchCount
        if matchCount < matched: break        

        url = row[2]
        seller = row[3]
        category = row[4]
        brand = row[5]
        model = row[6]
        u = UrlData(url, category, seller, rating, brand, model )
        ret.append(u)

    return sorted(ret, key=lambda x:x.rating, reverse=True)
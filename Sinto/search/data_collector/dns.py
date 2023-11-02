import codecs
from sqlite3.dbapi2 import Connection
import re

from . import MEMORY, Logger, putData, stripTags

SELLER = 'dns'

def Parse(fileName:str, log:Logger, category:str):
    # valueRe = re.compile("(<.+?>)([^<>]+)", re.DOTALL)
    brandRe = re.compile("PRODUCT_BRAND.+?>(.+?)</a", re.DOTALL)
    modelRe = re.compile("product-card-top__title.+?>(.+?)</h")

    def extractModel(data) :
        value = ""
        if not '>' in data: return data

        return stripTags(data)

    f = codecs.open(fileName, 'r', 'utf8')
    tstr = f.read()
    f.close()

    brandData = brandRe.search(tstr)
    if brandData == None:
        print('No brand', fileName)
        log.add('No brand ' + fileName)
        return (None, None, None)
    brand = stripTags(brandData.group(1))
    if len(brand) == 0:
        print("No brand parsed", fileName)
        log.add('No brand parsed ' + fileName)
        return (None, None, None)
    modelData = modelRe.search(tstr)
    if modelData == None:
        print("No model", fileName)
        log.add('No model ' + fileName)
        return (None, None, None)
    model = extractModel(modelData.group(1))
    if len(model) == 0:
        print('No model parsed', fileName)
        log.add('No model parsed ' + fileName)
        return (None, None, None)

    ret = []
    matchData = re.compile('<td.+?dots.+?>(.+?)</td><td>(.+?)</td', re.DOTALL)
    for m in matchData.finditer(tstr):
        # print("gr1",m.group(1))
        key = stripTags(m.group(1))
        # print("gr2",m.group(2))
        value = stripTags(m.group(2))
        # print(key,value)

        ret.append({"key":key, "value":value})

    if category == MEMORY:
        match = re.search('PC\d-(\d{4,})|PC(\d{4,})', tstr)
        if match != None:
            val = match.group(1)
            if val == None: val = match.group(2)
            if val != None: 
                ret.append({'key':'capacity', 'value':val})

    return (brand, model, ret)

def handleData(con:Connection, log:Logger, category:str):
    stmt = "select file, url from urls where seller='{0}' and parsed is null".format(
        SELLER)

    cursor = con.cursor()
    for row in cursor.execute(stmt):
        url = row[1]
        print("Parse " + url)
        brand, model, data = Parse(row[0], log, category)
        if brand != None:
            # print(url, brand, model, data)
            putData(con, url, category, brand, model, data)
        # break

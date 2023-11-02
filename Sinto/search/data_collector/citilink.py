import codecs
from . import BOX, CPU, MEMORY, POWER_SUPPLY, VIDEOCARD, MOTHERBOARD, putData, strip
from . import Logger
from sqlite3.dbapi2 import Connection
import re
import json

SELLER = 'citilink'

matchName = re.compile('application/ld\+json.+?>(.+?)</', re.DOTALL)
matchData = re.compile('Specifications__column_name.+?>(.+?)<.+?Specifications__column_value.+?>(.+?)<', re.DOTALL)

def Parse(fileName:str, log:Logger):
    f = codecs.open(fileName, 'r', 'utf8')
    fstr = f.read()
    f.close()

    m = matchName.search(fstr)
    if m == None:
        log.add("No application JSON {0}".format(fileName))
        return (None, None, None)

    str = m.group(1)
    info = json.loads(str)
    name = 'name' if 'name' in info else 'mpn'
    if 'brand' in info and name in info:
        data = []
        for m in matchData.finditer(fstr):
            val = {"key":strip(m.group(1)), "value": strip(m.group(2))}
            data.append(val)

        return (info['brand'], info[name], data)

    log.add('No brand or name in {0}'.format(fileName))
    return (None, None, None)

def handleData(con:Connection, log:Logger, category:str):
    stmt = "select file, url from urls where seller='{0}' and parsed is null".format(
        SELLER)

    cursor = con.cursor()
    for row in cursor.execute(stmt):
        url = row[1]
        fileName = row[0]
        print("Parse " + url)
        brand, model, data = Parse(fileName, log)
        if brand != None:
            # print(data)
            if len(data) == 0:
                log.add("No data in " + fileName)
                continue
            putData(con, url, category, brand, model, data)

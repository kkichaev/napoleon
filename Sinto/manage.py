#!/usr/bin/env python
"""Django's command-line utility for administrative tasks."""
import os
from re import I
from sqlite3.dbapi2 import Connection
import sys


def main():
    """Run administrative tasks."""
    os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'sinto.settings')
    try:
        from django.core.management import execute_from_command_line
    except ImportError as exc:
        raise ImportError(
            "Couldn't import Django. Are you sure it's installed and "
            "available on your PYTHONPATH environment variable? Did you "
            "forget to activate a virtual environment?"
        ) from exc
    execute_from_command_line(sys.argv)

def removeParsed(db:Connection, url:str):
    cursor = db.cursor()

    cursor.execute("delete from data where id in (select id from head where url = ?)", (url,))
    cursor.execute("delete from head where url = ?",(url,))
    cursor.execute("update urls set parsed = null where url = ?'",(url,))

def removeCategory(db:Connection, category:str):
    con = db.cursor()

    stmt = "delete from data where id in (select id from head where url in (select url from urls where category=?))"
    con.execute(stmt, (category,))

    stmt = "delete from params where id in (select id from head where url in (select url from urls where category=?))"
    con.execute(stmt, (category,))

    stmt = "delete from head where url in (select url from urls where category=?)"
    con.execute(stmt, (category,))
    
    stmt = "update urls set parsed = null where category = ?"
    con.execute(stmt, (category,))

def categoryValues(db:Connection, category:str):
    stmt = '''
        select value, value_ctr, ctr, value_ctr / ctr as part, data.category from
            (select value, count(distinct head.url) * 1.0 as value_ctr, category from data, head, urls 
                where data.id = head.id and head.url = urls.url
                group by category, value) data,
            (select count(url) as ctr, category from urls group by category) ctr
        where data.category = ctr.category 
            and data.category='{0}'
            and part > 0.01 and part < 0.34
    '''.format(category)

from search.data_collector.dns import handleData as dnsHandle
from search.data_collector.citilink import handleData as citilinkHandle
from search.data_collector import openDB, Logger
def updateCategory():
    db = openDB()
    category = 'cpu'
    logger = Logger('log.txt')
    removeCategory(db, category)

    dnsHandle(db, logger, category)
    citilinkHandle(db, logger, category)


if __name__ == '__main__':
    main()

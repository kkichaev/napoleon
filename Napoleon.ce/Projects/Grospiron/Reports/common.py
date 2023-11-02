import datetime


def daterange(start_date, end_date):
    for n in range(int((end_date - start_date).days + 1)):
        yield start_date + datetime.timedelta(n)

def unpackUserid(list):
    res = ''
    for i in list:
        res += "'{}',".format(i.id)
    return res[:-1]

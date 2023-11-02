def makeDocFilter(params, prefix=None, periodField:str="created", userField="userid") -> str:
    if userField:
        uids = ["'"+x.id+"'" for x in params.userids]
    else: uids = []
    prefix = prefix + "." if prefix else ""

    filter = '{0}"{3}" >= ToDate("{1}") AND {0}"{3}" <= ToDate("{2} 23:59:59")' .format(
        prefix, params.start.strftime("%d/%m/%Y"),  params.finish.strftime("%d/%m/%Y"), periodField
    )

    if len(uids) > 0:
        filter += ' AND %s"%s" in (%s)' % (prefix, userField, ",".join(uids))

    return filter

# -*- coding: cp1251 -*-
import time
import datetime

class AgentRoute:
    __slots__ = ('orgs', 'route', 'userid', 'sheduleStart')
    
    days = ["Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье" ]
    
    def __init__(self, server, userid):
        self.userid = userid
        self.sheduleStart = None
        
        uid = "'" + userid + "'"
        server.ChangeUser(uid)
        self.orgs = server.Get("Org", "", "id")
        self.route = server.Get("OrgFolder", "", "name")
        server.RestoreUser()
        
        cfg = server.Get("ServerConfig", '"userid"=' + uid + ' and "key"=' + "'SheduleStart'")
        if cfg != None and len(cfg) > 0 and len(cfg[0].value) > 0:
            self.sheduleStart = datetime.datetime(*(time.strptime(cfg[0].value, '%Y-%m-%d')[0:6])).date()
        

    def __makeList(self, dayRoute):
        ret = list()
    
        for dr in dayRoute.items:
            if dr.name in self.orgs:
                ret.append(self.orgs[dr.name])
        return ret

    def __weekIndex(self, day):
        if self.sheduleStart != None:
            d = day - self.sheduleStart
            return ((d.days / 7) % 4) + 1;
        return 0
    
    def getDayRoute(self, day):
        curDay = AgentRoute.days[day.weekday()]
        if curDay in self.route:
            return self.__makeList(self.route[curDay])
        
        curDay = str(self.__weekIndex(day.date())) + curDay
        if curDay in self.route:
            return self.__makeList(self.route[curDay])
        
        return list()
        
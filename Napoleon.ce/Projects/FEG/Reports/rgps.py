import sys
import logging
import traceback


class LocationData:

   WRONG_ACCURACY_TREASURE = 900
   WRONG_SPEED_TREASURE = 300           # 300 km/h is bad
   WRONG_ACELERATE_TREASURE = 15        # 1.5 g is bad
   BASE_ACELERATE_TREASURE = 0.01
   GOOD_POINT_INTERVAL = 3600           # we read wrongs data till find good, the good point need be inside good oint interval
   TRINAGLE_DISTANCE_COEF = 5           # point b is good if Distance(ab) + Distance(bc) <= 5 * Distance(ac)

   __slots__ = ['date', 'latitude', 'longitude', 'distance', 'speed', 'accelerate', 'accuracy', 'stltime', 'isMock', 'isGSM', 'server', 'prev']

   def __init__(self, gpsPos, server):
      self.server = server
      self.latitude = gpsPos.latitude
      self.longitude = gpsPos.longitude
      self.accuracy = gpsPos.accuracy
      self.date = gpsPos.date
      self.stltime = gpsPos.stltime
      self.isMock = gpsPos.isMock
      self.isGSM = gpsPos.isGSM

      self.prev = None
      self.speed = 0
      self.accelerate = 0
      self.distance = 0

   def setPrev(self, gpsPosPrev):
      ret = self.prev
      self.prev = gpsPosPrev

      self.speed = 0
      self.accelerate = 0
      self.distance = 0
      
      if gpsPosPrev != None :
         self.distance = self.server.EathDistance(self.latitude, self.longitude, gpsPosPrev.latitude, gpsPosPrev.longitude)
         ts = (self.date - gpsPosPrev.date).total_seconds()

         
         if ts > 0 :
            self.speed = self.distance / ts * 3.6 # km / h
            self.accelerate = abs(self.speed - gpsPosPrev.speed) / (ts * 3.6)

      return ret

   def isBad(self) : return self.accuracy >= LocationData.WRONG_ACCURACY_TREASURE
   def isBase(self) : return self.accelerate < LocationData.BASE_ACELERATE_TREASURE and self.speed < LocationData.WRONG_SPEED_TREASURE
   def isWrong(self, ptCheck):
      if ptCheck == None : return not self.isBase()

      dist = self.calcDistance(ptCheck)
      spd = 0
      ts = abs((ptCheck.date - self.date).total_seconds());

      if ts > 0:
            spd = dist / ts * 3.6; # km / h

      if spd > LocationData.WRONG_SPEED_TREASURE: return True

      ac = abs(self.speed - spd) / (ts * 3.6); # km / h
      return ac > LocationData.WRONG_ACELERATE_TREASURE

   def calcDistance(self, pt) :
      return self.server.EathDistance(self.latitude, self.longitude, pt.latitude, pt.longitude)

class DateData:
   __slots__ = ['data']  # date => [locationData]

   def __init__(self):
      self.data = dict()

   def put(self, gpsPos, server):
      date = gpsPos.date.date()
      if not date in self.data:
         self.data[date] = list()

      self.data[date].append(LocationData(gpsPos, server))

   def rectificate(self):
      for k, v in self.data.items():
         self.data[k] = self.rectificateDayRoute(v)

   def removeWrong(self, li, checkSet):
      basePoint = checkSet[0]
      prevPoint = basePoint
      while True:
         try:
            pt = next(li)
            if pt.isBad(): continue
            
            pt.setPrev(prevPoint)
            prevPoint = pt
            
            checkSet.append(pt)
            if pt.isBase(): break
         except StopIteration:
            break
         except:
            traceback.print_exc()
            break

      ret = list()
      ret.append(basePoint)

      for idx in range(1, len(checkSet) - 1) :
         p1 = checkSet[idx]
         if basePoint.isWrong(p1): continue

         p2 = checkSet[idx + 1]
         d1 = basePoint.calcDistance(p1)
         d2 = p1.calcDistance(p2)
         d3 = basePoint.calcDistance(p2)

         if (d1 + d2) > LocationData.TRINAGLE_DISTANCE_COEF * d3 : continue # p1 is wrong

         # add it to list and use it as base point
         ret.append(p1)
         basePoint = p1

      p = checkSet[len(checkSet) - 1]
      checkSet.clear()

      if p.isBase():
         # ret.append(p)
         checkSet.append(p)

      return ret

   def rectificateDayRoute(self, posList):
      res = list()
      
      li = iter(sorted(posList, key = lambda val : val.date))
      prev = None

      curSet = list()

      try:
         while True:
            pt = next(li)

            if prev != None: pt.setPrev(prev)
            if pt.isBad() : continue

            prev = pt
            if pt.isBase() : 
               res.extend(curSet)
               curSet = list()
               curSet.append(pt)
            else :
               if len(curSet) == 0: continue
               curSet.append(pt)
            
            if pt.isWrong(None) and len(curSet) > 0 :
               tlist = self.removeWrong(li, curSet)
               res.extend(tlist)

      except StopIteration: pass
      except: 
         traceback.print_exc()
         pass

      res.extend(curSet)
      return res
      

class AgentData:
   __slots__ = ['data']  # id => dateData

   def __init__(self):
      self.data = dict()

   def put(self, gpsPos, server):
      uid = gpsPos.userid
      if not uid in self.data:
         self.data[uid] = DateData()
      self.data[uid].put(gpsPos, server)

   def rectificate(self):
      for k, v in self.data.items():
         v.rectificate()

def rectificate(gpsPos, server) :
   adata = AgentData()

   for pos in gpsPos: adata.put(pos, server)

   adata.rectificate()

   return adata

# params users:[id], start, finish
def run(server):
   logging.basicConfig(format='%(module)s %(asctime)s.%(msecs)03d %(message)s', datefmt='%d.%m.%Y %H:%M:%S', stream=sys.stdout,level=logging.DEBUG)    
   logging.debug("starting")

   param = server.Params[0]
   uids = '"userid" in ('

   for uid in param.users:
      uids += "'" + uid.id + "',"

   #useGSM = ' and "isGSM"=0'
   useGSM = ''
   where = uids[:-1] + ') and "date" >= ToDate("{0}") and "date" < ToDate("{1}") {2}'.format(
         param.start.strftime("%d/%m/%Y %H:%M:%S"), param.finish.strftime("%d/%m/%Y %H:%M:%S"), useGSM) 
   

   gps = server.Get("GPSPos", where)

   print(where)

   rgps = rectificate(gps, server)

   outList = server.New('RGPS')
   for k, v in rgps.data.items():
      out = outList.New()
      out.userid = k

      for d, dv in v.data.items():
         outI = out.dates.New()
         outI.date = d

         prevP = None
         for gp in dv:
            posI = outI.track.New()
            
            if prevP != None: gp.setPrev(prevP)
            prevP = gp

            posI.latitude = gp.latitude
            posI.longitude = gp.longitude
            posI.date = gp.date
            posI.distance = gp.distance
            posI.speed = gp.speed
            posI.accelerate = gp.accelerate
            posI.accuracy = gp.accuracy
            posI.stltime = gp.stltime
            posI.isMock = gp.isMock
            posI.isGSM = gp.isGSM

   server.Put(outList)

   logging.info("ended")

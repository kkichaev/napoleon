using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Ads2017
{
   public class MapData
   {
      public List<GPSPos> points = new List<GPSPos>();
      public List<object> stops = new List<object>();
      public List<object> executed = new List<object>();
      public List<object> pendings = new List<object>();
      public List<object> stepoints = new List<object>();
      public List<object> userlocation = new List<object>();

      public double distance = 0.0;

      public bool HasData()
      {
         return points.Count > 0 ||
            stops.Count > 0 ||
            executed.Count > 0 ||
            stepoints.Count > 0 ||
            userlocation.Count > 0;
      }
    }
}

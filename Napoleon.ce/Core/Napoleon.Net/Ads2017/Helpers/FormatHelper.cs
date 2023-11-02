using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Ads2017
{
   class FormatHelper
   {
      public static FormatHelper instance = new FormatHelper();

      public string Range(DateTime d1, DateTime d2)
      {
         String fmt = "{0: dd.MM HH:mm}";
         string s1 = d1.Year < 2000 ? string.Empty : String.Format(fmt, d1);
         string s2 = d2.Year < 2000 ? string.Empty : String.Format(fmt, d2);

         return string.Format("{0} - {1}", s1, s2);
      }

      public static FormatHelper Instance { get { return instance; } }

      public String DistanceHuman(int distance)
      {
         int km = distance / 1000;
         int met = distance % 1000;

         return km > 0 ? String.Format("{0}{2} {1}{3}", 
            km, 
            met,
            ((App)System.Windows.Application.Current).resource.GetString("km"),
            ((App)System.Windows.Application.Current).resource.GetString("m") ) 
               : 
            String.Format("{0}{1}", met, ((App)System.Windows.Application.Current).resource.GetString("m"));
      }
   }
}

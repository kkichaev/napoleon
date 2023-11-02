using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GRSoft.NapoleonManager
{
   class ScriptOverviewEx : ScriptOverview
   {
      protected override void VisitTime(ScriptDoc sd, StringBuilder sb)
      {
         TimeSpan ts = new TimeSpan(sd.finish.Ticks);
         ts -= new TimeSpan(sd.Start.Ticks);

         sb.AppendFormat("Время визита:\t{0} - {1} ({2} мин)",
            sd.Start.ToShortTimeString(),
            sd.End.ToShortTimeString(),
            (int)ts.TotalMinutes);
      }
   }
}

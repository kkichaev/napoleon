using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class ScriptOverviewEx : ScriptOverview
   {
      public override void SetData(Network.DataObject dataObject)
      {
         Dispatch sd = dataObject as Dispatch;
         if( sd != null )
         {
            StringBuilder sb = new StringBuilder();
            VisitTime(sd, sb);

            DVisit vis = sd.VisitObj;

            if (vis != null)
            {
               sb.AppendLine();
               sb.AppendFormat("Примечание с фотками {0}", vis.remark);
            }
            
            List<GRSoft.Network.DataObject> incasses = sd.GetDocumentsOfType(DIncass.OBJECT_NAME);
            sb.AppendLine();
            for (int i = 0, j = 0; i < incasses.Count; ++i)
            {
                DIncass id = incasses[i] as DIncass;
                if (id != null)
                {
                    //sb.AppendLine();
                    sb.AppendFormat("Инкассация {0}:\t{1}", ++j, id.sum.ToString("C", Config.GetCultureInfo()));
                }
            }

            List<GRSoft.Network.DataObject> orders = sd.GetDocumentsOfType(DTask.OBJECT_NAME);
            sb.AppendLine();
            for (int i = 0; i < orders.Count; ++i)
            {
               DTask o = orders[i] as DTask;
                if (o != null)
                {
                    //sb.AppendLine();
                    sb.AppendFormat("Задача: {0}", o.disprem);
                    if (o != null && o.remark.Length > 0)
                    {
                       sb.AppendLine();
                       sb.AppendFormat("Задача выполена: {0}", o.remark);
                    }

                    sb.AppendLine();
                }
            }

            text.Text = sb.ToString();
         }
      }

      protected override void VisitTime(ScriptDoc sd, StringBuilder sb)
      {
         Dispatch d = sd as Dispatch;

         if (d != null)
         {
            List<Dispatch.Dispatchtimes> list = new List<Dispatch.Dispatchtimes>();
            list.AddRange(d.times);
            list.Sort((x, y) => { return x.start.CompareTo(y.start); });

            if (list.Count > 0)
            {
               DateTime s = list[0].start;
               DateTime f = list[list.Count - 1].finish;

               TimeSpan ts = new TimeSpan(f.Ticks);
               ts -= new TimeSpan(s.Ticks);

               sb.AppendFormat("Время визита:\t{0} - {1} ({2} мин)",
                  s.ToShortTimeString(),
                  f.ToShortTimeString(),
                  (int)ts.TotalMinutes);
            }
         }
      }
   }
}

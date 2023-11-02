using GRSoft.NapoleonManager.Properties;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   partial class ScriptDocuments
   {
      static void InitDocuments()
      {
         docs = new List<ScriptDocument>();
         docs.Add(new OrderDoc());
         docs.Add(new IncassDoc());
         docs.Add(new VisitDoc());
         docs.Add(new RemnantsDoc());
         docs.Add(new MonitoringDoc());
         docs.Add(new RivalMonitorDoc());
         docs.Add(new MerchendizingDoc());
      }
   }

   internal class MerchendizingDoc : ScriptDocument
   {
      internal MerchendizingDoc()
         : base("Merch", "Мерчендайзинг", Resources.distrib_doc)
      {
      }

   }
   internal class RivalMonitorDoc : ScriptDocument
   {
      internal RivalMonitorDoc()
         : base("RivalMonitoring", "Мониторинг конкурентов", Resources.monitor_doc)
      {
      }
   }
}

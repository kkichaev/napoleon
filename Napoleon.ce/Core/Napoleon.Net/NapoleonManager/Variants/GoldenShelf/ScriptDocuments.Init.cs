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
         docs.Add(new VisitDoc());
         docs.Add(new ReturnDoc());
         docs.Add(new PlanogramDoc());
         docs.Add(new CMonitoringDoc());
      }
   }

   internal class ContractDoc : ScriptDocument
   {
      private static readonly string TITLE = "Контракт";

      internal ContractDoc(ContractDef c)
         : base("Contract", string.Format("{0}({1} '{2:dd/MM/yyyy} - '{3:dd/MM/yyyy})", TITLE, c.name, c.start, c.finish), Resources.order_doc, c.id)
      {
      }
   }

   internal class PlanogramDoc : ScriptDocument
   {
      private static readonly string TITLE = "Планограмма";

      internal PlanogramDoc()
         :base("Planogram", TITLE, Resources.view_statistics)
      { 
      }
   }

   internal class CMonitoringDoc : ScriptDocument
   { 
      private static readonly string TITLE = "Мониторинг";

      internal CMonitoringDoc()
         : base("CMonitoring", TITLE, Resources.monitor_doc)
      { 
      }
   }
}

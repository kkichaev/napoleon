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
         docs.Add(new InvAuditDoc());
      }
   }

   internal class InvAuditDoc : ScriptDocument
   {
      internal InvAuditDoc()
         : base("InvAudit", "Аудит оборудования", Resources.inv_doc)
      {
      }
   }

}

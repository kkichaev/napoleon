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
         docs.Add(new TaskDoc());
         docs.Add(new InvEquDoc());
      }
   }

   internal class InvEquDoc : ScriptDocument
   {
      internal InvEquDoc()
           : base("InvEqu", "Контроль оборудования", Resources.audit_frg)
      {
      }
   }
}

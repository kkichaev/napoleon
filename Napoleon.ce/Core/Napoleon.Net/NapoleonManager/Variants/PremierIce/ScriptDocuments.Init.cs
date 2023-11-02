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
         docs.Add(new OrderDoc());
         docs.Add(new IncassDoc());
         docs.Add(new VisitDoc());
         docs.Add(new RemnantsDoc());

         docs.Add(new ReturnDoc());

         docs.Add(new InvDoc());
         docs.Add(new InvEquDoc());
      }
   }

   internal class InvDoc : ScriptDocument
   {
      internal InvDoc()
         : base("InvFrg", "Инвентаризация", Resources.inv_frg)
      {
      }
   }

   internal class InvEquDoc : ScriptDocument
   {
      internal InvEquDoc()
         : base("InvEqu", "Учет оборудования", Resources.audit_frg)
      {
      }
   }
}

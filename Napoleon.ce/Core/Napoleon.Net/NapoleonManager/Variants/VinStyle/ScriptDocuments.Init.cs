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
         docs.Add(new DistrDoc());
         docs.Add(new VisitDoc());
         docs.Add(new RemnantsDoc());
         docs.Add(new DMPDoc());
      }
   }

   internal class DistrDoc : ScriptDocument
   {
      internal DistrDoc()
         : base("OrgDistrib", "Дистриб.", Resources.distrib_doc)
      {
      }
   }

   internal class DMPDoc : ScriptDocument
   {
      internal DMPDoc()
         : base("DMP", "ДМП", Resources.dmp_doc)
      {
      }
   }
}

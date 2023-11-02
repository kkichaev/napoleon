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
         docs.Add(new ChekDoc());
         docs.Add(new VisitDoc());
         docs.Add(new RemnantsDoc());
         docs.Add(new ReturnDoc());
      }
   }

   internal class ChekDoc : ScriptDocument
   {
      internal ChekDoc()
         : base("RequestChek", "Чек", Resources.incass_doc)
      {
      }
   }
}

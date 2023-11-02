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
         docs.Add(new ScanLocationDoc());
         docs.Add(new Offer());
         docs.Add(new ReturnDoc());
      }
   }

   class Offer : ScriptDocument
   {
      internal Offer() :
         base("Offer", "Ком.предложение", Resources.return_doc)
      {
      }
   }
}

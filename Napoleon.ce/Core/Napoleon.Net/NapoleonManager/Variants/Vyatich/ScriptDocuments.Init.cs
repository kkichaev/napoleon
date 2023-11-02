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
         docs.Add(new PlanogramDoc());
#if RETURN_DOC
            docs.Add(new ReturnDoc());
#endif
      }
   }

    internal class PlanogramDoc : ScriptDocument
    {
        internal PlanogramDoc()
           : base("PlanogramDoc", "Планограмма", GRSoft.NapoleonManager.Properties.Resources.planogram_edit)
        {
        }
    }

}

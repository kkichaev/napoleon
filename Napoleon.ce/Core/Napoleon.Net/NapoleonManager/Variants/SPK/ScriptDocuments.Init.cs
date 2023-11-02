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
         docs.Add(new ReturnDoc());
         docs.Add(new LayoutDoc());
      }
   }

   internal class LayoutDoc : ScriptDocument
   {
      internal LayoutDoc()
         : base(GRSoft.NapoleonManager.Layout.OBJECT_NAME, "Выкладка", Resources.merch)
      {
      }
   }
}

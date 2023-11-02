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
         docs.Add(new VisitDoc());
         docs.Add(new RemnantsDoc());
         docs.Add(new ReturnDoc());
         docs.Add(new EquipmentDoc());
      }
   }

   internal class EquipmentDoc : ScriptDocument
   {
      internal EquipmentDoc()
         : base("Equipment", "Оборудование", Resources.distrib_doc)
      {
      }
   }
}

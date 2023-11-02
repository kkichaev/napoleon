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
         docs.Add(new SalesDoc());
         docs.Add(new IncassDoc());
         docs.Add(new GPSLocationDoc());
      }
   }

   internal class GPSLocationDoc : ScriptDocument
   {
      internal GPSLocationDoc()
         : base("OrgLocation", "Запрос координат", GRSoft.NapoleonManager.Properties.Resources.ic_add_location)
      {
      }
   }
}

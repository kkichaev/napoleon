using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class OrgLocation : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgLocation";

      [KeyField]
      public string id = "";

      [Precision(5)]
      public double longitude = 0;

      [Precision(5)]
      public double latitude = 0;
   }

   public class OrgLocations : DataSet<string, OrgLocation>
   {
      public static OrgLocations GetDataSet()
      {
         OrgLocations ol = DataModule.Get(OrgLocation.OBJECT_NAME) as OrgLocations;
         if (ol == null)
            ol = new OrgLocations();

         return ol;
      }

      public OrgLocation GetLocation(string id)
      {
         if (ContainsKey(id))
            return this[id];
         return null;
      }

      protected OrgLocations()
         : base(OrgLocation.OBJECT_NAME, true)
      {
      }
   }
}

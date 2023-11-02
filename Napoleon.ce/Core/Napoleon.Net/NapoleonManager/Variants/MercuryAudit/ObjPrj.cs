using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class ObjPrj
   {
   }

   public partial class Price
   {
      public int own = 0;
      public int rem = 0;
      public int pos = 0;
   }

   public class OrgDistrib : BaseDocument
   {
      public static readonly string OBJECT_NAME = "OrgDistrib";

      public class OrgDistribItem : DataObject
      {
         [Reference("ManagerPrice,Price", "id", typeof(Price))]
         public Price item = null;

         public string id = "";
         public double qty = 0.0;
      }

      [ItemType(typeof(OrgDistribItem))]
      public List<OrgDistribItem> items = null;

      public string numberho = string.Empty;
   }
}

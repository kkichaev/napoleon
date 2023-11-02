using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class PrjObj
   {
   }

   public class Facing : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Facing";

      [ItemType(typeof(FacingItem))]
      public List<FacingItem> items = new List<FacingItem>();
   }

   public class FacingItem : DataObject
   {
      public string id = string.Empty;
      public double qty = 0.0;

      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;

      public string Item { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }
      public double Qty { get { return qty; } }
   }

   public class Revised : DataObject
   {
      public static new readonly string OBJECT_NAME = "Revised";
      public DateTime created = DateTime.Now;
      public DateTime facing = DateTime.Now;
      public string id = string.Empty;
      public string id_i = string.Empty;
      public double qty = 0.0;
      public string userid = string.Empty;
   }
}

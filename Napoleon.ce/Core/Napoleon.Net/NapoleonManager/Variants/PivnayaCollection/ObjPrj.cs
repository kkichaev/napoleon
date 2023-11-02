using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class ObjPrj
   {
   }

   public class InvAudit : BaseDocument
   {
      public static readonly string OBJECT_NAME = "InvAudit";

      public DateTime penult = DateTime.Now;
      public DateTime last = DateTime.Now;

      [ItemType(typeof(InvAuditItem))]
      public List<InvAuditItem> items = null;
   }

   public class Inventory : DataObject
   {
      public static readonly string OBJECT_NAME = "Inventory";
      [KeyField]
      public string id = "";
      public string name = "";
      public int tare = 0;
   }

   public partial class InvAuditItem : DataObject
   {
      public string id = string.Empty;

      [Reference("Inventory","id")]
      public Inventory inv = null;

      public double qty = 0;
      public double fact = 0;
      public int clear = 0;
      public double good = 0;
   }

   public class Tare : BaseDocument
   {
      public static readonly string OBJECT_NAME = "TareDoc";

      public class Item : DataObject
      {
         public string id = string.Empty;

         [Reference("Inventory", "id")]
         public Inventory inv = null;

         public double qty = 0;
         public double fact = 0;

         public string Name { get { return inv == null ? "" : inv.name; } }
         public double Fact { get { return fact; } }
         public double Qty { get { return qty; } }
      }


      public List<Item> items = new List<Item>();
   }

}

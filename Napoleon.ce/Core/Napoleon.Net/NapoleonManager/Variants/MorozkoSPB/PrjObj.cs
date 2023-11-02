using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class PrjObj
   {
   }

   public class MonitoringW : BaseDocument
   {
      public static readonly String OBJECT_NAME = "Monitoring";

      public partial class Item : DataObject
      {
         [Reference("ManagerPrice,Price,RivalPrice", "id", typeof(Price))]
         public Price item = null;
         public string id = string.Empty;

         public double cost = 0.0;
         public double cost1 = 0.0;
         public double cost2 = 0.0;
      }

      [ItemType(typeof(Item))]
      public List<Item> items = null;
   }

   public class RivalMonitoring : MonitoringW
   {
      public static new readonly String OBJECT_NAME = "RivalMonitoring";

   }

   public class Merchendizing : BaseDocument
   {
      public static readonly String OBJECT_NAME = "Merch";

      public partial class Item : DataObject
      {
         [Reference("ManagerPrice,Price", "id", typeof(Price))]
         public Price item = null;
         public string id = string.Empty;

         public double qty = 0.0;
         public double system = 0.0;
      }

      public partial class MFolder : DataObject
      {
         [Reference("ManagerFolder", "id", typeof(ManagerFolder))]
         public ManagerFolder item = null;
         public string id = string.Empty;

         public double mine = 0.0;
         public double their = 0.0;
      }

      [ItemType(typeof(Item))]
      public List<Item> items = null;

      [ItemType(typeof(MFolder))]
      public List<MFolder> folders = null;
   }

   public class Supplier : DataObject, IComparable<Supplier>
   {
      public static readonly string OBJECT_NAME = "Suppliers";

      [KeyField]
      public string id = "";

      public string name = "";

      public override string ToString()
      {
         return name;
      }


      public int CompareTo(Supplier other)
      {
         return name.CompareTo(other.name);
      }
   }

   public partial class ScriptDef
   {
      public string suppl = "";

      [Reference("Suppliers", "suppl")]
      public Supplier suplier = null;

      public string Suppl { get { return suplier == null ? "<Для всех>" : suplier.name; } }
   }

   public partial class DivisionManager
   {
      public string suppl = "";
   }

}

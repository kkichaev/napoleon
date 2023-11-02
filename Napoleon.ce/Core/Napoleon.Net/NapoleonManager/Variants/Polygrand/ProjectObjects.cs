using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class AgentRoute : DataObject
   {
      public DateTime changed = DateTime.Now;
   }

   public partial class AgentRouteItem : DataObject
   {
      public int isNew = 0;
      public string task = string.Empty;

      public String Org { get { return org.Name; } }
      public String Task { get { return task; } }
   }

   class ManagerRoute : AgentRoute
   {
      public static new readonly string OBJECT_NAME = "ManagerRoute";

      public DateTime approve = DateTime.MinValue;

      [ItemType(typeof(ManagerRouteItem))]
      public new List<ManagerRouteItem> items = null;
   }

   public class ManagerRouteItem : AgentRouteItem
   {
      public string taskId = string.Empty;
      //public int pos = -1;
   }

   public class ItemGroupsAssign : DataObject
   {
      public static readonly string OBJECT_NAME = "ItemGroupsAssign";

      public String id = "";
      public String userid = "";
   }

   public class ItemGroup : DataObject
   {
      public static readonly string OBJECT_NAME = "ItemGroups";

      public String id = "";
      public String name = "";

      [ItemType(typeof(Item))]
      public List<Item> items = new List<Item>();

      public string Name { get { return name; } set { name = value; } }
      public string userid = string.Empty;

      public class Item : DataObject
      {
         public String id = "";

         [Reference("ManagerPrice", "id")]
         public Price price;

         public string Name { get { return price == null ? "" : price.Name; } }
         public string Qty { get { return price == null ? "" : price.qty.ToString(); } }

         public Item() { }
         public Item(Price p) { id = p.id; this.price = p; }
      }
   }
}

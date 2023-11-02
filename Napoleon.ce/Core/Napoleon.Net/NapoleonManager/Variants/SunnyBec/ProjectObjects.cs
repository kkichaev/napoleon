using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public class ItemGroup : DataObject
   {
      public static readonly string OBJECT_NAME = "ItemGroups";

      public String id = "";
      public String name = "";
      public String userid = "";

      [ItemType(typeof(Item))]
      public List<Item> items = new List<Item>();

      public string Name { get { return name; } set { name = value; } }

      public class Item : DataObject
      {
         public String id = "";

         [Reference("ManagerPrice", "id")]
         public Price price;

         public string Name { get { return price == null ? "" : price.Name; } }

         public Item() { }
         public Item(Price p) { id = p.id; this.price = p; }
      }
   }
}

using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public class PriceMonOrgDoc : BaseDocument
   {
      public static readonly string OBJECT_NAME = "PriceMonOrgDoc";

      public class Item : DataObject
      {
         public string id;

         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price item = null;

         public double cost;

         public double Cost { get { return cost; } }
         public string Name { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }
      }

      public List<Item> items = new List<Item>();
   }

   public class PriceMonOrgs : DataObject
   {
      public static readonly string OBJECT_NAME = "PriceMonOrgs";

      public string id;

      [Reference("Org", "id", typeof(Org))]
      public Org org = null;
   }

   public class PriceMonItem : DataObject
   {
      public static readonly string OBJECT_NAME = "PriceMonItems";

      public string id;

      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;
   }
}

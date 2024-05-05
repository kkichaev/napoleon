using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class AgentActions : DataObject
   {
      public static readonly string OBJECT_NAME = "AgentActions";

      public string userid = "";
      public string id = "";

      [Reference("OrderAction", "id")]
      public OrderAction action = null;
   }

   public class OrgCluster : DataObject, IComparable<OrgCluster>
   {
      public static readonly string OBJECT_NAME = "OrgCluster";

      public string name = "";
      public string id = "";

      public int CompareTo(OrgCluster other)
      {
         return name.CompareTo(other.name);
      }

      public override string ToString()
      {
         return name;
      }
   }

   public class OrderAction : DataObject
   {
      public static readonly string OBJECT_NAME = "OrderAction";
      public static readonly int SET_GIFT_DISCOUNT = 0;
      public static readonly int SUM_GIFT = 1;

      public class Item : DataObject
      {
         public string id = string.Empty;
         public double qty = 0.0;

         [Reference("ManagerPrice,Price", "id", typeof(Price))]
         public Price item = null;

         public String Name { get { return item.Name; } }
         public double Qty { get { return qty; } set { qty = value; } }
      }

      [KeyField]
      public string id = string.Empty;
      public DateTime start = DateTime.MinValue;
      public DateTime finish = DateTime.MinValue;
      public string name = string.Empty;
      public string descr = string.Empty;

      public string org = "";
      public string cluster = "";
      public double sum = 0;
      public double discount = 0;
      public int gift = 0;
      public int applyManyTimes = 0;

      public int kind = SET_GIFT_DISCOUNT;

      public List<Item> items = new List<Item>();
      public List<Item> gifts = new List<Item>();

      public DateTime created = DateTime.MinValue;
      public int rem = 0;

      public string Name { get { return name; } }
      public DateTime Start { get { return start; } }
      public DateTime Finish { get { return finish; } }
      public string Descr { get { return descr; } }

      public override string ToString()
      {
         return name;
      }
   }
}

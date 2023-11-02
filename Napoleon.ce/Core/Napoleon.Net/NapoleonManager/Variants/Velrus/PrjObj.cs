using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class OrderAction : DataObject
   {
      public static readonly string OBJECT_NAME = "OrderAction";

      public class ActionItem : Item
      {
         public string iditem = string.Empty;
         public List<Item> bonus = new List<Item>();
      }

      public class Item : DataObject
      {
         public string id = string.Empty;
         public double qty = 0.0;

         [Reference("ManagerPrice,Price", "id", typeof(Price))]
         public Price item = null;

         public String Name { get { return item.Name; } }
         public double Qty { get { return qty; } set { qty = value; } }
      }

      public string id = string.Empty;
      public DateTime start = DateTime.MinValue;
      public DateTime finish = DateTime.MinValue;
      public string name = string.Empty;
      public string descr = string.Empty;
      public List<ActionItem> items = new List<ActionItem>();
      public DateTime created = DateTime.MinValue;
      public int rem = 0;

      public string Name { get { return name; } }
      public DateTime Start { get { return start; } }
      public DateTime Finish { get { return finish; } }
      public string Descr { get { return descr; } }
   }
}

using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public partial class OrgLocation : DataObject
   {
      public DateTime date = DateTime.Now;
   }

   public partial class Org :DataObject
   {
      public string category = "";
      public string segment = "";
   }

   public partial class Order : BaseDocument
   {
      public double incass;
      public string dlvNumber = "";
      public string incassNumber = "";
      public double incassSum;
   }
   
   public partial class Incass : BaseDocument
   {
      // то что приходит из 1с
      public string incassNumber = "";
      public double incassSum;
      public Order refDoc = null;
   }

   public partial class OrderItem : DataObject
   {
      public double dlvqty;

      public double DlvQty { get { return dlvqty; } }
   }

   public class GetDocsReportParam : DataObject
   {
      public DateTime start;
      public DateTime end;

      public int detailed = 0;

      public List<Agent> users = new List<Agent>();
   }

   public class DocsTotalResult : DataObject
   {
      public static readonly string OBJECT_NAME = "DocsTotalResult";

      public double incass;
      public double deliveries;

      [KeyField]
      public string id;
   }

   public class OrderItemTotalEx : OrderItemTotal
   {
      public OrderItemTotalEx(List<OrderItem> items)
         : base(items)
      {
         foreach (OrderItem oi in items)
            dlvqty += oi.dlvqty;
      }
   }

   public class DocsDlvResult : DataObject
   {
      public static readonly string OBJECT_NAME = "DocsDlvResult";

      public string id;
      public string userid;
      public DateTime date;
      public string created;
      public string number;

      public class Item : DataObject
      {
         public string id;
         public double qty;
         public double sum;
      }

      public List<Item> items = new List<Item>();
   }

   public class DocsPayResult : DataObject
   {
      public static readonly string OBJECT_NAME = "DocsPayResult";

      public string id;
      public string userid;
      public DateTime date;
      public string created;
      public string number;

      public double sum;
   }

   public class ReturnFolders : DataObject
   {
      public static readonly string OBJECT_NAME = "ReturnFolders";

      public string userid;
      
      [KeyField]
      public string fid;
   }
}

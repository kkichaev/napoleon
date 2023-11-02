using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class ItemsAudit : BaseDocument
   {
      public static readonly String OBJECT_NAME = "ItemsAudit";

      [ItemType(typeof(Item))]
      public List<Item> items = null;


      public class Item : GRSoft.Network.DataObject
      {
         public string id = "";
         public int repr = 0;
         public int pack = 0;
         public int block = 0;
         public int price = 0;

         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price item = null;

         public string Name { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }

         public bool Repr { get { return repr == 1; } }
         public bool Pack { get { return pack == 1; } }
         public bool Block { get { return block == 1; } }
         public bool Price { get { return price == 1; } }
      }
   }

   public class RfrgAudit : BaseDocument
   {
      public static readonly String OBJECT_NAME = "RdrgAudit";

      public int exclusive = 0;

      [ItemType(typeof(Item))]
      public List<Item> items = null;


      public class Item : GRSoft.Network.DataObject
      {
         public string doc_id = "";
         public string fact_id = "";
         public string fact_rfid = "";
         public string model = "";
         public string descr = "";

         public string DocID { get { return doc_id; } }
         public string FactID { get { return fact_id; } }
         public string RFID { get { return fact_rfid; } }
         public string Model { get { return model; } }
         public string Descr { get { return descr; } }
      }
   }
}

using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Security.Permissions;

namespace GRSoft.NapoleonManager
{
   public partial class ScriptDef
   {
      public int active = 0;

      public bool Active { get { return active != 0; } set { active = value ? 1 : 0; } }
   }

   public class UnusedScripts : DataObject
   {
      public readonly static string OBJECT_NAME = "UnusedScripts";

      [KeyField]
      public int id = 0;
   }

   public class UnusedQuestions : DataObject
   {
      public readonly static string OBJECT_NAME = "UnusedQuestions";

      [KeyField]
      public string id = "";
   }

   public class ScriptDefActive : DataObject
   {
      public readonly static string OBJECT_NAME = "ScriptDefActive";

      [KeyField]
      public int id = 0;
      public int active = 0;

      public ScriptDefActive(ScriptDef src, bool active)
      {
         id = src.id;
         this.active = active ? 1 : 0;
      }
   }

   public class PayType : DataObject, IComparable<PayType>
   {
      public readonly static string OBJECT_NAME = "PayTypes";

      [KeyField]
      public string id = "";
      public string name = "";
      public int pos = 0;
      public string Name
      {
         get { return name; }
         set { name = value; }
      }

      public int CompareTo(PayType other)
      {
         return pos - other.pos;
      }
   }

   public class ClientType : PayType
   {
      public new readonly static string OBJECT_NAME = "ClientTypes";
   }

   public class FormatTT : PayType
   {
      public new readonly static string OBJECT_NAME = "FormatTT";

      public class Item : PayType
      {

      }

      [ItemType(typeof(Item))]
      public List<Item> items = new List<Item>();
   }

   public class PurchaseTemplate : DataObject, IComparable<PurchaseTemplate>
   {
      public readonly static string OBJECT_NAME = "PurchaseTemplate";

      public string id = "";
      public int pos = 0;


      [Reference("ManagerPrice,Price", "id", typeof(Price))]
      public Price item = null;

      public string Name {  get { return item == null ? id : item.name; } }

      public int CompareTo(PurchaseTemplate other)
      {
         return pos - other.pos;
      }
   }

   public class Purchase : BaseDocument
   {
      public static new readonly string OBJECT_NAME = "PurchaseDoc";

      public override double Sum()
      {
         double sum = 0;
         foreach(Item i in items)
         {
            sum += i.cost;
         }
         return sum;
      }

      public class Item : DataObject
      {
         [Reference("ManagerPrice,Price", "id", typeof(Price))]
         public Price item = null;
         public string id = "";

         public double qty = 0;
         public double cost = 0;
         public double weight = 0;

         public double Weight{ get { return weight; } }

         //Свойства для отображение в гриде
         //Количество
         //Цена
         public double Cost { get { return cost; } }
         public string Name { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }

         public string ID { get { return id; } }
      }

      public List<Item> items = new List<Item>();
   }

   public class Selling : Order
   {
      public static new readonly string OBJECT_NAME = "SellingDoc";
   }

   public partial class ScriptDoc : BaseDocument
   {
      public DateTime finish = DateTime.MinValue;
   }

   public class BNOper : DataObject 
   { 
      public static readonly string OBJECT_NAME = "BNOper";

      [KeyField]
      public string id = string.Empty;
      public string mo = string.Empty;
      public string tu = string.Empty;
      public string we = string.Empty;
      public string th = string.Empty;
      public string fr = string.Empty;
      public string sa = string.Empty;
      public string su = string.Empty;
   }

   public class NeedRemove : DataObject
   {
      public static readonly string OBJECT_NAME = "NeedRemove";

      public string userid = string.Empty;
      public DateTime created = DateTime.MinValue;
      public DateTime docCreated = DateTime.MinValue;
      public string id = string.Empty;
      public string doctype = string.Empty;
   }

   public partial class ScriptDoc
   { 
      public string fio = string.Empty;
      public string phone = string.Empty;
      public DateTime visitDoc = DateTime.MinValue;
   }
}
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{

   public class IshimPlan : DataObject
   {
      public static readonly string OBJECT_NAME = "IshimPlan";

      public DateTime begin = DateTime.Now;

      public class Item : DataObject
      {
         public string id = "";
         
         [Reference("ManagerPrice,CommonPrice,Price", "id", typeof(Price))]
         public Price price = null;

         public string Name { get { return price == null ? "" : price.name; } }
      }

      public  class PlanItem : DataObject, IComparable<PlanItem>
      {
         public string id = "";
         public string name = "";

         public List<Item> items = new List<Item>();

         public int CompareTo(PlanItem other)
         {
            return name.CompareTo(other.name);
         }
      }

      public List<PlanItem> plans = new List<PlanItem>();

      public bool Empty
      {
         get
         {
            return plans.Count == 0;
         }
      }
   }

   public class AgentPlan : DataObject
   {
      public static readonly string OBJECT_NAME = "AgentPlan";


      public DateTime begin = DateTime.Now;
      public string userid = "";

      public class Item : DataObject
      {
         public string id = "";
         public double weight = 0;
         public int akb = 0;

         public bool Empty {  get { return weight == 0 && akb == 0; } }
      }

      public List<Item> items = new List<Item>();
   }

   class Sklad : DataObject
   {
      public static readonly string OBJECT_NAME = "Sklads";

      public string name = "";
      
      [KeyField]
      public string id = "";

      public string Name { get { return name; } }
      public string ID { get { return id; } }
   }

   class PriceSklads : DataObject
   {
      public static readonly string OBJECT_NAME = "PriceSklads";

      public string idwh = "";

      [KeyField]
      public string id = "";
   }

   class PriceFolderOrder : DataObject
   {
      public static readonly string OBJECT_NAME = "PriceFolderOrder";

      [KeyField]
      public string id = "";
      public string fid = "";
      public int ord = 0;
   }

   public partial class Price : DataObject
   {
      public int ord = -1;
   }
   
   public partial class ManagerFolder : DataObject
   {
      public int hidden = 0;
   }

   public class OrgProp : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgProp";

      [KeyField]
      public string id = string.Empty;

      public string userid = string.Empty;
      public int script = -1;
      public string matrix = string.Empty;
   }

   public class StringCause : DataObject
   {
      public static readonly string OBJECT_NAME = "StringCause";

      [KeyField]
      public string id = string.Empty;
      public string text = string.Empty;
      public int rem = 0;

      public String Text 
      {
         get 
         {
            return text;
         }

         set
         {
            text = value;
         }

      }
   }
}

using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class ReturnCause : DataObject
   {
      public static readonly String OBJECT_NAME = "ReturnCause";

      [KeyField]
      public string id = string.Empty;
      public string text = string.Empty;
      public int rem = 0;

      public string Text { get { return text; } set { text = value; } }
   }

   public class BonusDef : GRSoft.Network.DataObject, IComparable<BonusDef>
   {
      public static readonly string OBJECT_NAME = "ActionDef";

      [KeyField]
      public string id = string.Empty;

      public DateTime start = DateTime.Now;
      public DateTime till = DateTime.Now;

      [Reference("ManagerPrice", "iditem")]
      public Price item;
      public String iditem = string.Empty;

      public double qty = 0.0;

      public DateTime Start
      {
         get { return start; }
         set { start = value; }
      }
      public DateTime Till
      {
         get { return till; }
         set { till = value; }
      }

      public String Caption { 
         get 
         {
            StringBuilder result = new StringBuilder();

            if (item != null)
               result.Append(item.Name);
            else
               result.Append("Код товара<").Append(iditem).Append(">");

            result.Append(" кол-во ").Append(qty);

            return result.ToString();
         } 
      }

      #region Члены IComparable<BonusDef>

      public int CompareTo(BonusDef other)
      {
         return start.CompareTo(other.start);
      }

      #endregion
   }
   public class Bonus : Order
   {
      public static readonly string OBJ_NAME = "Action";
   }

   public partial class Incass
   {
      public string dover = string.Empty;
   }

   public partial class ReturnItem
   {
      public string cause = string.Empty;

      public double Cost
      {
         get
         {
            return cost;
         }
      }

      public string Cause { get { return cause; } }
   }

   public partial class Order
   {
      public string firmCode = "";
      public string whCode = "";
      public int supplyer = 0;
      public int sumType = 0;
      public string prcType = "";
   }

   public partial class OrderItem
   {
      public double discount = 0.0;
      public double costWODsc = 0.0;
      public int actionGift = 0;

      public double Discount { get { return discount; } }
      public double SumWODsc { get { return costWODsc * qty; } }

      public double CostWODsc { get { return costWODsc; } }
      public bool Action { get { return actionGift != 0; } }
   }

   public class Decision : DataObject
   {
      public static readonly string OBJECT_NAME = "Decision";

      public static readonly int RET_DOC = 0;
      public static readonly int ORD_DOC = 1;

      public static readonly int NONE = 0;
      public static readonly int APPROVE = 1;
      public static readonly int REJECT = 2;

      public DateTime created;
      public DateTime dodate;
      public string userid = string.Empty;
      public int value = NONE;
      public string remark = string.Empty;

      public string manager = "";

      public int doctype = RET_DOC;
   }
}

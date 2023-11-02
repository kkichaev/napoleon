using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
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

   public class VisitWithPhoto : BaseDocument
   {
      public static readonly string OBJECT_NAME = "VisitWithPhoto";
   }
}

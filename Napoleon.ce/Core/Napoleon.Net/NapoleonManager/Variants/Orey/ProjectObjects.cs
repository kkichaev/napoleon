using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public class BonusDef : GRSoft.Network.DataObject, IComparable<BonusDef>
   {
      public static readonly string OBJECT_NAME = "BonusDef";

      [KeyField]
      public string id = string.Empty;

      public DateTime start = DateTime.Now;
      public DateTime till = DateTime.Now;

      [Reference("ManagerPrice", "iditem")]
      public Price item;
      public String iditem = string.Empty;

      public double sum = 0.0;
      public int type = 0;
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

      [ItemType(typeof(BonusDefItem))]
      public List<BonusDefItem> items = new List<BonusDefItem>();

      public String Caption { 
         get 
         {
            StringBuilder result = new StringBuilder();

            if(type == BonusPrice.CODE)
            {
               if (item != null)
                  result.Append(item.Name);
               else
                  result.Append("Код товара<").Append(iditem).Append(">");

               result.Append(" кол-во ").Append(qty);
            }
            else if (type == BonusSum.CODE) 
            {
               result.Append("Сумма заказа ").Append(sum);
            }

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

   public class BonusDefItem : GRSoft.Network.DataObject
   {
      [Reference("ManagerPrice", "id")]
      public Price price;

      public int qty;

      public int Qty
      {
         get { return qty; }
         set { qty = value; }
      }
      
      public string ItemName { get { return null == price ? string.Empty : price.Name; } }
   }

   public class Bonus : Order
   {
      public static readonly string OBJ_NAME = "Bonus";
   }
}

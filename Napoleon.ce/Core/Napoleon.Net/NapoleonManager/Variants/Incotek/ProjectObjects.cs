using GRSoft.NapoleonManager.Properties;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class Storcheck : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Storcheck";

      public int ho_best = 0;
      public int showcase_best = 0;
      public int corp_block = 0;
      public int posm = 0;

      public int share_ki = 0;
      public int share_pf = 0;

      public class Item : GRSoft.Network.DataObject
      {
         public string id = "";

         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price price = null;
      }

      public List<Item> items = new List<Item>();
   }

   public class StorcheckActions : GRSoft.Network.DataObject, IComparable<StorcheckActions>
   {
      public static readonly string OBJECT_NAME = "StorcheckActions";

      [KeyField]
      public DateTime date = DateTime.Now;

      public class Item : GRSoft.Network.DataObject, IComparable<Item>
      {
         public string name = "";

         public string Name { get { return name; } set { name = value; } }

         public int CompareTo(Item other)
         {
            return name.CompareTo(other.name);
         }
      }

      public DateTime Date { get { return date; } }

      public List<Item> items = new List<Item>();

      public int CompareTo(StorcheckActions other)
      {
         return date.CompareTo(other.date);
      }
   }

   public class StorcheckGoods : GRSoft.Network.DataObject, IComparable<StorcheckGoods>
   {
      public static readonly string OBJECT_NAME = "StorcheckGoods";

      public static readonly int NEW_GOODS_FOLDER = 1;
      public static readonly int TOP_30_FOLDER = 2;

      [KeyField]
      public DateTime date = DateTime.Now;

      public class Item : GRSoft.Network.DataObject
      {

         public string id = "";

         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price price = null;

         public int folder = 0;
      }

      public DateTime Date { get { return date; } }

      public List<Item> items = new List<Item>();

      public int CompareTo(StorcheckGoods other)
      {
         return date.CompareTo(other.date);
      }
   }

   class MatrixOrder : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "MatrixOrder";

      public class Item : GRSoft.Network.DataObject, IComparable<Item>
      {
         public string name = "";
         public int order = 0;

         #region IComparable<Item> Members

         public int CompareTo(Item other)
         {
            return order - other.order;
         }

         #endregion
      }

      public string userid = "";

      [ItemType(typeof(Item))]
      public List<Item> items = new List<Item>();
   }

   internal class StorcheckDoc : ScriptDocument
   {
      internal StorcheckDoc()
         : base("Storcheck", "Сторчек", Resources.checkbox)
      {
      }
   }
}

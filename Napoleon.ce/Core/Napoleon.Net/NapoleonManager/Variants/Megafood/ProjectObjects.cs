using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public partial class ScriptDef
   {
      [Reference("OrgType", "type")]
      public OrgType orgType = null;

      public string type = "";

      public string Type { get { return orgType == null ? "<Для всех>" : orgType.name; } }
   }

   public partial class Org
   {
      string ot;

      public string Type { get { return ot; } set { ot = value; } }
   }

   public class OrgTypeBinding : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgTypeBinding";

      [KeyField]
      public string id = "";

      public string type = "";

      //[Reference("OrgType", "type")]
      //public OrgType orgType = null;
   }

   public class FocusMatrix : DataObject
   {
      public static readonly string OBJECT_NAME = "FocusMatrix";

      [KeyField]
      public string type = "";

      public class Item : DataObject
      {
         public string id = "";

         [Reference("ManagerPrice", "id")]
         public Price price = null;

         public string Name { get { return price == null ? "Товар с кодом <" + id + ">" : price.Name; } }
      }

      [Reference("OrgType", "type")]
      public OrgType orgType = null;

      public string Name { get { return orgType == null ? "" : orgType.name; } }

      public List<Item> items = new List<Item>();
   }

   public class FocusRejectReason : DataObject
   {
      public static readonly string OBJECT_NAME = "FocusRejectReason";

      [KeyField]
      public string id = "";

      public string name = "";

      public string Name { get { return name; } set { name = value; } }
   }

   public class OrgType : DataObject, IComparable<OrgType>
   {
      public static readonly string OBJECT_NAME = "OrgType";

      [KeyField]
      public string id = "";

      public string name = "";

      public string Name { get { return name; } }
      public string ID { get { return id; } }

      public override string ToString()
      {
         return name;
      }

      public int CompareTo(OrgType other)
      {
         return name.CompareTo(other.name);
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
}

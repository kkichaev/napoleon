using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class Barcode : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Barcode";

      [ItemType(typeof(BarcodeItem))]
      public List<BarcodeItem> items = new List<BarcodeItem>();
   }

   public class BarcodeItem : DataObject
   {
      public string id = string.Empty;

      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;

      public string Item { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }
   }

   public class Facing : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Facing";

      [ItemType(typeof(FacingItem))]
      public List<FacingItem> items = new List<FacingItem>();
   }

   public class FacingItem : DataObject
   {
      public string id = string.Empty;
      public double qty = 0.0;

      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;

      public string Item { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }
      public double Qty { get { return qty; } }
   }

   public partial class ScriptDoc
   {
      public bool HaveFacing()
      {
         foreach (ScriptDocItem sd in items)
         {
            if (sd.type == Facing.OBJECT_NAME)
               return true;
         }
         return false;
      }
   }

   public class Revised : DataObject
   {
      public static readonly string OBJECT_NAME = "Revised";
      public DateTime created = DateTime.Now;
      public DateTime facing = DateTime.Now;
      public string id = string.Empty;
      public string id_i = string.Empty;
      public double qty = 0.0;
      public string userid = string.Empty;
   }

   public class Supplier : DataObject, IComparable<Supplier>
   {
      public static readonly string OBJECT_NAME = "Suppliers";

      [KeyField]
      public string id = "";

      public string name = "";

      public override string ToString()
      {
         return name;
      }


      public int CompareTo(Supplier other)
      {
         return name.CompareTo(other.name);
      }
   }

   public partial class ScriptDef
   {
      public string suppl = "";
      
      [Reference("Suppliers", "suppl")]
      public Supplier suplier = null;

      public string Suppl { get { return suplier == null ? "<Для всех>" : suplier.name; } }
   }

   public partial class ScriptDefItem
   {
      public int needSend = 0;
      public int allowGallery = 0;
   }

   public partial class DivisionManager
   {
      public string suppl = "";
   }

   public class Dogovor : DataObject
   {
      public static readonly string OBJECT_NAME = "MgrDogovors";

      [KeyField]
      public string id = "";
      public string firm = "";
      public string idOrg = "";
      public int bonus = 0;
   }

   public class AgentDogovors : DataObject
   {
      public static readonly string OBJECT_NAME = "AgentDogovors";
      public string userid = "";
      
      [KeyField]
      public string id = "";
   }

   public partial class Org
   {
      public string ido = "";
   }

   public class InvFrg : BaseDocument
   {
      public static readonly string OBJECT_NAME = "InvFrg";

      [ItemType(typeof(InvFrgItem))]
      public List<InvFrgItem> items = new List<InvFrgItem>();
   }

   public class InvFrgItem : DataObject
   {
      public string id = string.Empty;
      public string barcode = string.Empty;
      public string number = string.Empty;
      public string name = string.Empty;

      public string Item { get { return name; } }
      public string Barcode { get { return barcode; } }
      public string Number { get { return number; } }
   }

   public class InvEqu : BaseDocument
   {
      public static readonly string OBJECT_NAME = "InvEqu";

      [ItemType(typeof(InvEquItem))]
      public List<InvEquItem> items = new List<InvEquItem>();
   }

   public class InvEquItem : DataObject
   {
      public string id = string.Empty;
      public string barcode = string.Empty;
      public string number = string.Empty;
      public string name = string.Empty;
      public int check = 0;

      public string Item { get { return name; } }
      public string Barcode { get { return barcode; } }
      public string Number { get { return number; } }
      public int Check { get { return check; } }
   }
}

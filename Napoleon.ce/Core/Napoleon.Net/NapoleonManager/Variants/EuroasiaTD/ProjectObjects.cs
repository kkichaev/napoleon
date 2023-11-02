using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class BankIncass : BaseDocument
   {
      public static readonly string OBJECT_NAME = "BankIncass";

      public double sum = 0;

      public byte[] photo = null;

      public override double Sum() { return sum; }

      internal override Org Org
      {
         get
         {
            return Org.Empty;
         }
      }

      public DateTime visitDoc = DateTime.MinValue;
   }

   public partial class DivisionManager
   {
      public string guid = "";
   }

   class Distrib : BaseDocument
   {
      public static readonly String OBJECT_NAME = "OrgDistrib";

      public partial class Item : DataObject
      {
         [Reference("ManagerPrice,Price", "id", typeof(Price))]
         public Price item = null;
         public string id = string.Empty;
         public double cost = 0.0;
         public double qty = 0.0;
         public string remark = string.Empty;
      }

      [ItemType(typeof(Item))]
      public List<Item> items = null;
   }

   public class Monitor : DataObject
   {
      public static readonly string OBJECT_NAME = "Monitor";

      [KeyField]
      public string userid = string.Empty;

      public class FolderItem : DataObject
      {
         public string id = "";

         public FolderItem(ManagerFolder f) { this.id = f.id; }
         public FolderItem() { }
      }

      public List<FolderItem> folders = new List<FolderItem>();

      public class ScriptItem : DataObject
      {
         public int id = 0;

         public ScriptItem(ScriptDef sd) { this.id = sd.id; }
         public ScriptItem() { }
      }
      public List<ScriptItem> scripts = new List<ScriptItem>();

      public bool HaveScript(ScriptDef sd)
      {
         foreach (ScriptItem si in scripts)
            if (si.id == sd.id)
               return true;

         return false;
      }

      public bool HaveFolder(string folder)
      {
         foreach (FolderItem fi in folders)
         {
            if (fi.id == folder)
               return true;
         }

         return false;
      }
   }
}

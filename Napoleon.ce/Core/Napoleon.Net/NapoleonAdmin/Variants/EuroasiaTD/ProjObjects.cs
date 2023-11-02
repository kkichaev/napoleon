using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonAdmin
{
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

      public bool HaveFolder(ManagerFolder folder)
      {
         foreach(FolderItem fi in folders)
         {
            if (fi.id == folder.id)
               return true;
         }

         return false;
      }
   }

   public class ManagerFolder : DataObject, IComparable<ManagerFolder>
   {
      public static readonly string OBJECT_NAME = "ManagerFolder";

      public ManagerFolder() { }

      [KeyField]
      public string id = string.Empty;
      public int level = 0;
      public string name = string.Empty;
      public string userid = string.Empty;

      public int CompareTo(ManagerFolder other)
      {
         return name.CompareTo(other.name);
      }
   }

   public partial class DivisionManager
   {
      public string guid = "";

      public void SetGuid()
      {
         guid = Guid.NewGuid().ToString().Replace("-", "");
      }
   }

   public partial class ScriptDefItem : DataObject
   {
      public String curType = "";
      public String name = "";
      public int nextDoc;
      public int condition;
      public String condParam = "";

      public String Name
      {
         get { return (name.Length > 0) ? name : curType; }
      }
   }

   public partial class ScriptDef : DataObject
   {
      static public string OBJECT_NAME = "ScriptDef";

      [KeyField]
      public Int32 id = -1;
      public String userid = "";
      public String name = "";

      [ItemType(typeof(ScriptDefItem))]
      public List<ScriptDefItem> items;

      public string Name
      {
         get
         {
            if (name.Length == 0 && items.Count > 0)
               return items[0].Name;

            return name;
         }
      }

      public override string ToString() { return Name; }

      public string DocsStr
      {
         get
         {
            StringBuilder sb = new StringBuilder();

            if (items != null)
               foreach (ScriptDefItem item in items)
                  sb.Append(item.Name).Append(", ");

            if (sb.Length > 2)
               sb.Remove(sb.Length - 2, 2);

            return sb.ToString();
         }
      }

   }

}

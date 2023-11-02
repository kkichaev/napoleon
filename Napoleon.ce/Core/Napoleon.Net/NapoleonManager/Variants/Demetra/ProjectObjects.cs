using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class DistribGroup : DataObject, IComparable<DistribGroup>
   {
      public static readonly string OBJECT_NAME = "DistribGroup";

      public string name = "";

      [KeyField]
      public string id = "";

      public int pos = 0;

      public FmDistribGroupEditor owner = null;

      public class Item : DataObject
      {
         public string id = "";

         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price item = null;

         public string Name { get { return item == null ? "Товар с кодом '" + id + "'" : item.name; } }
      }

      public List<Item> items = new List<Item>();
      public string Name
      { 
         get { return name; } 
         set 
         {
            if (name != value)
            {
               name = value;
               if (owner != null)
                  owner.Changed(this);
            }
         } 
      }

      public int CompareTo(DistribGroup other)
      {
         return pos - other.pos;
      }
   }

   partial class Org : DataObject
   {
      public class Rfrg : DataObject
      {
         public string id = "";
         public string name = "";
      }
      public List<Rfrg> refregerators = new List<Rfrg>();
   }

   public class DistrGroupsAssign : DataObject
   {
      public static readonly string OBJECT_NAME = "DistrGroupsAssign";

      public String id = "";
      public String userid = "";
   }

   public class DistribGroupDoc : BaseDocument
   {
      public static readonly string OBJECT_NAME = "DistribGroupDoc";

      public class Item : DataObject
      {
         public string id = "";
         [Reference("DistribGroup", "id")]
         public DistribGroup item = null;
         public int exists;

         public string Name { get { return item == null ? "" : item.name; } }
         public bool Exists { get { return exists != 0; } }
      }

      public List<Item> items = new List<Item>();
   }

   internal class DistrScriptDoc : ScriptDocument
   {
      internal DistrScriptDoc()
         : base("DistribGroupDoc", "Дистриб.", GRSoft.NapoleonManager.Properties.Resources.distrib_doc)
      {
      }
   }
}

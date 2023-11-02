using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class UnusedScripts : DataObject
   {
      public readonly static string OBJECT_NAME = "UnusedScripts";

      [KeyField]
      public int id = 0;
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

   public partial class ScriptDoc
   {
      public int fake = 0;
   }


   public partial class DivisionManager
   {
      public string suppl = "";
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

   public partial class Visit
   {
      public partial class VisitItem
      {
         public VisitItem Clone()
         {
            return (VisitItem)MemberwiseClone();
         }
      }

      public Visit Clone()
      {
         Visit res = (Visit)MemberwiseClone();
         res.org = null;
         List<Visit.VisitItem> items = new List<VisitItem>();
         this.items.ForEach((x)=>items.Add(x.Clone()));
         res.items = items;

         return res;
      }
   }

   public partial class Answer
   {
      public Answer Clone()
      {
         Answer res = (Answer)MemberwiseClone();
         res.org = null;
         List<AnswerItem> items = new List<AnswerItem>();
         this.items.ForEach((x)=>items.Add(x.Clone()));
         res.items = items;
         return res;
      }
   }

   public partial class OrgRemnants
   {
      public OrgRemnants Clone()
      {
         OrgRemnants res = (OrgRemnants)MemberwiseClone();
         res.org = null;
         List<OrgRemnantsItem> items = new List<OrgRemnantsItem>();
         this.items.ForEach((x) => items.Add(x.Clone()));
         res.items = items;
         return res;
      }
   }

   public partial class AnswerItem
   {
      public AnswerItem Clone()
      {
         return (AnswerItem)MemberwiseClone();
      }
   }

   public partial class OrgRemnantsItem
   {
      public OrgRemnantsItem Clone()
      {
         return (OrgRemnantsItem)MemberwiseClone();
      }
   }

   public partial class QuestionItemValue
   {
      public override string ToString()
      {
         return value;
      }
   }

   public partial class OrgRemnantsItem
   {
      public double face = 0.0;
      public int format = 0;
      public double cost = 0.0;
      public int promo = 0;
      public string oos = "";

      public double Face {
         get { return face; }
         set { face = value; }
      }

      public double Cost { 
         get { return cost; }
         set { cost = value; }
      }

      public int Format
      {
         get { return format; }
         set { format = value; }
      }

      public int Promo
      {
         get { return promo; }
         set { promo = value; }
      }

      public string OOS
      {
         get { return oos; }
         set { oos = value; }
      }
   }

   public class VisitItemDoc : Visit.VisitItem
   {
      public readonly static string OBJECT_NAME = "VisitItemDoc";

      public string __nameBase = string.Empty;
      public DateTime __date = DateTime.MinValue;
   }

   public partial class Org : DataObject
   {
      public int rem = 0;
   }
}

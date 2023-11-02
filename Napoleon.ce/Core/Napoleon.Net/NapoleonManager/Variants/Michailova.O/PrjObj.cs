using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public class OrgType : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgType";

      [KeyField]
      public string id = string.Empty;
      public string name = string.Empty;
      public int rem = 0;
      public string matrix = string.Empty;

      [ItemType(typeof(OrgTypeItem))]
      public List<OrgTypeItem> items = new List<OrgTypeItem>();

      public class OrgTypeItem : DataObject
      {
         public string id = string.Empty;
      }

      public string Name { get { return name; } }
      public string Matrix { get { return matrix; } set { matrix = value; } }
   }

   public partial class Matrix
   {
      public override string ToString()
      {
         return name;
      }
   }

   public class OrgDistrict : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgDistrict";

      [KeyField]
      public string id = string.Empty;
      public string userid = string.Empty;
      public int rejret = 0;
   }
}

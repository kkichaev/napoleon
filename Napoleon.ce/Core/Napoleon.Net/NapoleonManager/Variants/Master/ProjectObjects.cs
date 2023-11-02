using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class OrgType : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgType";

      [KeyField]
      public string id = string.Empty;
      public string name = string.Empty;

      public string ID { get { return id; } }
      public string Name { get { return name; } }
   }

   public class OrgMem : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgMem";

      [KeyField]
      public string id = string.Empty;
      public string type = string.Empty;
   }

   public class CheckPhoto : DataObject
   {
      public static readonly string OBJECT_NAME = "CheckPhoto";
      public DateTime date;
      public string userid = string.Empty;
      public string id = string.Empty;
   }
}

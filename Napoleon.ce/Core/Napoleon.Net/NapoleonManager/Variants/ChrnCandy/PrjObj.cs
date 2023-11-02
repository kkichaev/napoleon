using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class OrgType : DataObject 
   {
      public static readonly string OBJECT_NAME = "OrgType";

      [KeyField]
      public string id = "";
      public string name = "";
   }

   class OrgMatrix : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgMatrix";

      [KeyField]
      public string id = "";

      [Reference("OrgType", "id")]
      public OrgType type = null;

      public string mtx = "";

      public string Name { get { return type != null ? type.name : string.Empty; } }
      public string Matrix { get { return mtx; } set { mtx = value; } }
   }
}

using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class NetOrg : DataObject
   {
      public static readonly string OBJECT_NAME = "NetOrg";

      [KeyField]
      public string id = string.Empty;
      public string name = string.Empty;

      public override string ToString()
      {
         return name;
      }
   }
}

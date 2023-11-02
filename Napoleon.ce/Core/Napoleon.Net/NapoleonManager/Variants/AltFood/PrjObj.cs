using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class PrjObj
   {
   }

   class RejectCause : DataObject
   {
      public static readonly String OBJECT_NAME = "RejectCause";

      [KeyField]
      public string id = string.Empty;
      public string text = string.Empty;
      public int rem = 0;

      public string Text { get { return text; } set { text = value; } }
   }

}

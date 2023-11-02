using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class ReturnCause : DataObject
   {
      public static readonly String OBJECT_NAME = "ReturnCause";

      [KeyField]
      public string id = string.Empty;
      public string text = string.Empty;
      public int rem = 0;

      public string Text { get { return text; } set { text = value; } }
   }

   partial class Incass
   {
      [ItemType(typeof(IncassPayItem))]
      public List<IncassPayItem> items = new List<IncassPayItem>();

      public class IncassPayItem : DataObject
      {
         public string number = string.Empty;
      }
   }
}

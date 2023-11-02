using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class OrderRemark : GRSoft.Network.DataObject
   {
      public readonly static string OBJECT_NAME = "OrderRemark";
      [KeyField]
      public string id = string.Empty;
      public string text = string.Empty;
      public int rem = 0;
      public int pos = 0;

      public string Text { get { return text; } set { text = value; } }
      public int Pos { get { return pos; } set { pos = value; } }
   }

   public partial class MonitoringItem
   {
      private static readonly int NON_SET = -1;
      public int pos = NON_SET;
   }

}

using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class InvEqu : BaseDocument
   {
      public static readonly string OBJECT_NAME = "InvEqu";

      [ItemType(typeof(InvEquItem))]
      public List<InvEquItem> items = new List<InvEquItem>();
      public DateTime visitDoc;
}

   public class InvEquItem : DataObject
   {
      public string id = string.Empty;
      public string barcode = string.Empty;
      public string number = string.Empty;
      public string name = string.Empty;
      public int check = 0;

      public string ID { get { return id; } }
      public string Item { get { return name; } }
      public string Barcode { get { return barcode; } }
      public string Number { get { return number; } }
      public int Check { get { return check; } }
   }

   public partial class ScriptDef
   {
      public int filter = 0;
      public int tareType = 1;
   }
}

using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public class PlanogramDef : DataObject
   {
      public class PlanogramDefItem : DataObject
      {
         public string id = string.Empty;
         public string name = string.Empty;
         public byte[] photo = null;
      }
      public readonly static string OBJECT_NAME = "PlanogramDef";

      [KeyField]
      public string id = string.Empty;

      [ItemType(typeof(PlanogramDefItem))]
      public List<PlanogramDefItem> items = new List<PlanogramDefItem>();

   }

   public class Planogram : BaseDocument
   {
      public readonly static string OBJECT_NAME = "Planogram";

      public int approved = 0;
   }

   public partial class Price : DataObject
   {
      public int ntz = 0;
   }
}

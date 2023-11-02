using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class ObjPrj
   {
   }

   public class InvAudit : BaseDocument
   {
      public static readonly string OBJECT_NAME = "InvAudit";

      public DateTime penult = DateTime.Now;
      public DateTime last = DateTime.Now;

      [ItemType(typeof(InvAuditItem))]
      public List<InvAuditItem> items = null;
   }

   public partial class InvAuditItem : DataObject
   {
      public string id = string.Empty;
      public string name = string.Empty;
      public double qty = 0;
      public double fact = 0;
      public int clear = 0;
      public double good = 0;
   }

   public partial class OrgRemnantsItem
   {
      public double tara = 0;

      public double Tara { get { return tara; } }
   }
}

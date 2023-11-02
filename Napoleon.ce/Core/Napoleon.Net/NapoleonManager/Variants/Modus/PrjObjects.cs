using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   public class TaskBegin : BaseDocument
   {
      public static string OBJECT_NAME = "TaskBegin";
   }

   public class TaskEnd : BaseDocument
   {
      public static string OBJECT_NAME = "TaskEnd";
   }

   public partial class MerchEnd : BaseDocument
   {
      public static string OBJECT_NAME = "MerchEnd";

      [ItemType(typeof(MerchEndItem))]
      public List<MerchEndItem> items = null;
   }

   public partial class MerchEndItem : DataObject
   {
      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;
      public double start = 0.0;
      public double finish = 0.0;
   }

   public partial class ATask : BaseDocument
   {
      public static string OBJECT_NAME = "ATask";
      [KeyField]
      public string taskid = string.Empty;
   }

   public partial class MTask : ATask
   {
      public static new string OBJECT_NAME = "MTask";
   }

   public partial class OrgRemnantsItem
   {
      public double qtyWh = 0.0;
      public double qtySh = 0.0;

      public double QtyWh { get { return qtyWh; } }
      public double QtySh { get { return qtySh; } }
   }

   public partial class Visit
   {
      public int actgs = 0;
   }

   public class Facing : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Facing";

      public double qty = 0.0;
   }
}

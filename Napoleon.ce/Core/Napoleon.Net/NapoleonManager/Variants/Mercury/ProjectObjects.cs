using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public class AgentPlanItem : DataObject
   {
      public string id;
      public double valueSum;
      public double valueWeight;
   }

   public class AgentPlan : DataObject
   {
      public static readonly string OBJECT_NAME = "AgentPlan";
      public string userid = string.Empty;

      [Reference("Agents", "userid")]
      public Agent agent = null;

      public DateTime begin = DateTime.MinValue;
      public DateTime end = DateTime.MinValue;

      [ItemType(typeof(AgentPlanItem))]
      public List<AgentPlanItem> items = null;
   }

   public class InvAudit : BaseDocument
   {
      public static readonly string OBJECT_NAME = "InvAudit";

      [ItemType(typeof(InvAuditItem))]
      public List<InvAuditItem> items = null;
   }

   public class InvAuditItem : DataObject
   {
      public string id = string.Empty;
      public int isnew = 0;
   }
}

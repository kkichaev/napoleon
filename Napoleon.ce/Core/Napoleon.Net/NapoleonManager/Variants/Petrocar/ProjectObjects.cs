using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Threading;
using System.Runtime.InteropServices;
using System.IO;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{

   class PCAgentMonthlyPlans : DataObject
   {
      public static readonly string OBJECT_NAME = "PCAgentMonthlyPlans";

      public string userid = "";

      [Reference("Agents", "userid")]
      public Agent agent = null;

      public DateTime date = DateTime.Now;
      public double plan = 0;
      public double pdz = 0;
   }

   public class AgentPlanItem : GRSoft.Network.DataObject
   {
      public string id;
      public int type;
      public double valueSum;
      public double valueQty;
   }

   public class AgentPlan : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "AgentPlan";
      public string userid = string.Empty;

      [Reference("Agents", "userid")]
      public Agent agent = null;

      public DateTime begin = DateTime.MinValue;
      public DateTime end = DateTime.MinValue;

      [ItemType(typeof(AgentPlanItem))]
      public List<AgentPlanItem> groups = null;
   }
}

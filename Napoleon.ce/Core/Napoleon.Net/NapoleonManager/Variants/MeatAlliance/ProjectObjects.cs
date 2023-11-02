using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public class AgentPlanItem : GRSoft.Network.DataObject
   {
      public string id;
      public int type;
      public double valueSum;
      public double valueWeight;
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
   
   class ReturnsEx : Returns
   {
      public int forsake;

      public bool Collect { get { return forsake == 0; } set { forsake = value ? 0 : 1; } }
   }

   class RetNtfy : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "RetNtfy";

      public DateTime created = DateTime.MinValue;
      public string userid = string.Empty;
   }

}

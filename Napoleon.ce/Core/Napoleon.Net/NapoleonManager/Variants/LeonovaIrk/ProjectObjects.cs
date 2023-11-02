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

   public class IncassItem : GRSoft.Network.DataObject
   {
      public string number = string.Empty;
      public double sum = 0.0;
      public DateTime date = DateTime.MinValue;
   }

   public partial class Incass
   {
      [ItemType(typeof(IncassItem))]
      public List<IncassItem> items = null;
   }

   public class OrgType : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgType";

      [KeyField]
      public string id = string.Empty;

      public string name = string.Empty;

      public override string ToString()
      {
         return name;
      }
   }

   public partial class Org : DataObject
   {
      public string orgtype = string.Empty;

      [Reference("OrgType", "id")]
      public OrgType orgType = null;
   }

   public class RouteSaveInfo : DataObject
   {
       public static readonly string OBJECT_NAME = "RouteSaveInfo";

       [KeyField]
       public string userid = string.Empty;
       public DateTime date = DateTime.MinValue;
   }
}

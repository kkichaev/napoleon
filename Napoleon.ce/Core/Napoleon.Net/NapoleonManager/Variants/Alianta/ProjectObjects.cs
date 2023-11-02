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

   partial class AgentAssortMtx : DataObject
   {
      public static readonly string OBJECT_NAME = "AgentAssortMtx";

      [KeyField]
      public String userid;
      public String matrix;

      [Reference("Agents", "userid")]
      public Agent agent = null;
   }

   public class AliantaOffer : BaseDocument
   {
      public static readonly string OBJECT_NAME = "AliantaOffer";

      public double discount = 0;

      public class DocItem : DataObject
      {
         public String id = "";
         [Reference("ManagerPrice,Price", "id", typeof(Price))]
         public Price item = null;

         public double cost = 0;
         public double discount = 0;
         public double priceCost = 0;

         public string Discount { get { return discount.ToString("N2") + "%"; } }
         public string SCost { get { return cost.ToString("C", Config.GetCultureInfo()); } }
         public string Item { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }
      }

      public List<DocItem> items = new List<DocItem>();
   }
}

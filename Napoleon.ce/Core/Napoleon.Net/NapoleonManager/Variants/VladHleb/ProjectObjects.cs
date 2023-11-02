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

   public class RouteTemplateItem : DataObject, IComparable<RouteTemplateItem>
   {
      public static readonly int ALL_WEEKS = 0xF;
      public static readonly int WEEK1 = 0x1;
      public static readonly int WEEK2 = 0x2;
      public static readonly int WEEK3 = 0x4;
      public static readonly int WEEK4 = 0x8;

      [Reference("Org,PotenzialOrg,CommonOrgs", "id", typeof(Org))]
      public Org org = null;

      public string weekDay = "";
      public string id = "";
      public int pos;
      public int weekMask = ALL_WEEKS;

      public void SetMask(bool value, int week)
      {
         if (value)
            weekMask |= week;
         else
            weekMask &= (~week);
      }

      public int CompareTo(RouteTemplateItem other)
      {
         return pos - other.pos;
      }
   }

   public class RouteTemplate : DataObject
   {
      public static readonly string OBJECT_NAME = "RouteTemplate";

      [KeyField]
      public string name = "";
      
      public List<RouteTemplateItem> items = new List<RouteTemplateItem>();

      internal void FillItems(OrgFolder of, Utils.WeekDay wd, int flags)
      {
         of.items = new List<OrgFolderItem>();
         string day = wd.FullName;
         foreach(RouteTemplateItem i in items)
         {
            if( i.weekDay == day && (i.weekMask & flags) != 0 )
            {
               OrgFolderItem ofi = new OrgFolderItem();
               ofi.name = i.id;
               ofi.pos = i.pos;
               ofi.org = i.org;
               of.items.Add(ofi);
            }
         }
      }
   }
}

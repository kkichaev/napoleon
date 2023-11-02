using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class WaybillRoute : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "Route";

      public string id = string.Empty;
      public DateTime created = DateTime.MinValue;
      public string userid = string.Empty;
      public DateTime start = DateTime.MinValue;
      public DateTime finish = DateTime.MinValue;
   }

   class PlanRoute : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "Route";

      public String id = "";
      public String userid = "";
      public DateTime created = DateTime.MinValue;
      public DateTime start = DateTime.MinValue;
      public DateTime finish = DateTime.MinValue;

   }

   class RouteItem : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "RouteItem";

      public string route = string.Empty;
      public string itemid = string.Empty;
      public string id = string.Empty;
      public int pos = 0;
      public string remark = string.Empty;

      public List<ItemDef> docs = new List<ItemDef>();

      public class ItemDef : GRSoft.Network.DataObject
      {
         public string id = string.Empty;
         public string number = string.Empty;
         public int pos = 0;
         public string remark = string.Empty;
         public string type = string.Empty;
         public string title = string.Empty;
      }
   }

   public class Dispatch : ScriptDoc
   {
      public static readonly new string OBJECT_NAME = "Dispatch";

      [ItemType(typeof(Dispatchtimes))]
      public List<Dispatchtimes> times = new List<Dispatchtimes>();

      public class Dispatchtimes : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
      }

      public DateTime visit = DateTime.MinValue;

      private DVisit dvisit = null;
      private bool dvisInited = false;

      public DVisit VisitObj
      {
         get
         {
            if (!dvisInited)
            {
               dvisInited = true;
               IDataSet ds = DataModule.Get(DVisit.OBJECT_NAME);

               foreach (DataObject d in ds.Data)
               {
                  DVisit v = d as DVisit;

                  if (v != null && v.created.Equals(visit))
                  {
                     dvisit = v;
                     break;
                  }
               }
            }
            
            return dvisit;
         }
      }
   }

   public class DShipment : BaseDocument
   {
      public static readonly string OBJECT_NAME = "DShipment";

      [ItemType(typeof(DShipmentItem))]
      public List<DShipmentItem> items = new List<DShipmentItem>();

      public override double Sum()
      {
         double result = 0.0;

         foreach (DShipmentItem i in items)
            result += i.outqty * i.cost;

         return result;
      }
   }

   public class DShipmentItem : GRSoft.Network.DataObject
   {
      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;
      public string id = string.Empty;
      public double inqty = 0.0;
      public double outqty = 0.0;
      public double cost = 0.0;
      public string remark = string.Empty;
      public string cause = string.Empty;
      public int pos = 0;

      public int Pos { get { return pos; } }
      public string Item { get { return item == null ? string.Format("Товар с кодом <{0}>", id) : item.Name; } }
      public double Inqty { get { return inqty; } }
      public double Outqty { get { return outqty; } }
      public double Cost { get { return cost; } }
      public string Remark { get { return remark; } }
      public string Cause { get { return cause; } }
   }

   public class DReturn : DShipment
   {
      public static readonly new string OBJECT_NAME = "DReturn";
   }

   public class DIncass : BaseDocument
   {
      public static readonly string OBJECT_NAME = "DIncass";
      public double sum = 0.0;

      public override double Sum()
      {
         return sum;
      }
   }

   public class DTask : BaseDocument
   {
      public static readonly string OBJECT_NAME = "DTask";
      public string disprem = string.Empty;
   }

   public class DVisit : Visit
   {
      public static new readonly string OBJECT_NAME = "DVisit";
   }

   public class ChatData : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "ChatData";

      public string id = string.Empty;
      public DateTime created = DateTime.MinValue;
      public string text = string.Empty;
      public string userid = string.Empty;
      public string group = string.Empty;

      [Reference("Agents", "userid")]
      public Agent who = null;

      public override string ToString()
      {

         string an = string.Empty;

         if(!CurrentUser.user.User.id.Equals(userid))
            an =  who == null ? userid : who.Name;

         StringBuilder sb = new StringBuilder();

         sb.Append(created.ToString());
         sb.Append("&nbsp;&nbsp;&nbsp;&nbsp;<font color='blue'>").Append(an).Append("</font>&nbsp;");
         sb.Append(text);

         return sb.ToString();
      }
   }

   public class ChatQuery : ChatData
   {
      public static readonly new string OBJECT_NAME = "ChatQuery";
   }

   public class ChatGroup : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "ChatGroup";

      [KeyField]
      public string id = string.Empty;
      public string owner = string.Empty;
      public string title = string.Empty;
      public int rem = 0;

      [ItemType(typeof(ChatGroupItem))]
      public List<ChatGroupItem> items = new List<ChatGroupItem>();

      public class ChatGroupItem : GRSoft.Network.DataObject
      {
         public string id = string.Empty;

         [Reference("Agents", "id")]
         public Agent agent = null;
      }

      public override string ToString() { return title; }

      public override bool Equals(object obj)
      {
         ChatGroup g = obj as ChatGroup;

         if (g != null)
            return id.Equals(g.id);
         else
            return base.Equals(obj);
      }

      public override int GetHashCode()
      {
         return base.GetHashCode();
      }
   }

   public class ChatUser : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "ChatUser";

      [KeyField]
      public string id = string.Empty;
      public string name = string.Empty;
      public string incsnd = string.Empty;
      public string outsnd = string.Empty;
   }

}

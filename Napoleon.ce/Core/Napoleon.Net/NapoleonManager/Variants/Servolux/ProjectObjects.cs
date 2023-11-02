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
   class ManagerLog : DataObject
   {
      public static readonly string OBJECT_NAME = "ManagerLog";
   
      public DateTime created = DateTime.Now;

      public string agentid = "";
      public string id = "";
      public string item = "";
      public string type = "";
      public string newValue = "";
      public string oldValue = "";
   }

   class MerchRouteForAgent : DataObject
   {
      public static readonly string OBJECT_NAME = "MerchRouteForAgent";

      public string userid = "";
      public string id = "";
      public string day = "";
   }

   class RejectAct : BaseDocument
   {
      public static readonly string OBJECT_NAME = "RejectAct";
      public class Item : OrderItem
      {
         public string number = "";
         public string remark = "";
         public DateTime date = DateTime.Now;
         public string party = "";
         public DateTime expired = DateTime.Now;

         public string Party { get { return party; } }
      }

      public List<Item> items = new List<Item>();
   }

   class OrderBundle : BaseDocument
   {
      public static readonly string OBJECT_NAME = "OrderBundle";
      public class Item : DataObject
      {
         public DateTime created = DateTime.Now;
      }

      public double sum = 0;
      public override double Sum() { return sum; }

      public List<Item> items = new List<Item>();
      public List<Order>   documents()
      {
         List<Order> ret = new List<Order>();

         List<DateTime> used = new List<DateTime>();
         items.ForEach(x=>used.Add(x.created));

         IDataSet ds = DataModule.Get(Order.OBJECT_NAME);
         foreach(DataObject d in ds.Data)
         {
            Order o = (Order)d;
            if(used.Contains(o.created))
               ret.Add(o);
         }

         return ret;
      }
   }

   class ManagerLogList : SimpleDataSet<ManagerLog>
   {
      public ManagerLogList() : base(ManagerLog.OBJECT_NAME, false) { }

      public void PutLog(string agentid, string orgid, string item, string type, string newValue, string oldvalue)
      {
         foreach(ManagerLog mi in this.Data)
         {
            if(mi.agentid == agentid && mi.id == orgid && mi.item == item && mi.type == type)
            {
               mi.newValue = newValue;
               return;
            }
         }

         ManagerLog ml = new ManagerLog();
         ml.agentid = agentid;
         ml.id = orgid;
         ml.item = item;
         ml.type = type;
         ml.newValue = newValue;
         ml.oldValue = oldvalue;

         Add(ml);
      }
   }

   class ServoluxShedule : BaseDocument
   {
      public static readonly string OBJECT_NAME = "ServoluxShedule";
      public static readonly string AGENT_ROUTE_TYPE = "agents";
      public static readonly string MERCH_ROUTE_TYPE = "merch";

      public string routeType = "";
   }

   public class ModifySheduleEventArgs
   {
      public string oldValue;
      public string newValue;

      public ModifySheduleEventArgs(string o, string n)
      {
         oldValue = o;
         newValue = n;
      }
   }

   public delegate void ModifySheduleEventHander(object sender, ModifySheduleEventArgs arg);

   public class AgentOrgs : DataObject
   {
      public static readonly string OBJECT_NAME = "AgentOrgs";

      [KeyField]
      public string id = "";
      public string userid = "";
   }

   public class ServoluxSheduleItem : DataObject, IComparable<ServoluxSheduleItem>
   {
      public static readonly string OBJECT_NAME = "ServoluxSheduleItem";

      static readonly int VISIT = 1;
      static readonly int VISIT_MERCH = 2;
      static readonly int MERCH = 3;
      static readonly int DISP = 4;
      static readonly int DISP_MERCH = 5;
      //static readonly int MERCH_ADD = 6;
      static readonly int VISIT_DISP = 7;
      static readonly int CALL = 8;
      static readonly int VISIT_CALL = 9;

      static readonly int CICLE_NONE = 0;
      static readonly int CICLE_ODD = 1;
      static readonly int CICLE_EVEN = 2;
      static readonly int CICLE_2 = 3;
      static readonly int CICLE_4 = 5;


      public static event ModifySheduleEventHander Modifed;

      //[Reference("Org,PotenzialOrg,CommonOrg,CommonOrgs", "id", typeof(Org))]
      public Org org = null;

      public string userid = "";
      public DateTime created = DateTime.Now;

      [KeyField]
      public string id = "";
      public string agentAdd = "";
      public string dispatcher = "";
      public string merch = "";
      public string merchAdd = "";
      
      public int cycleType = 0;

      public int mon = 0;
      public int tue = 0;
      public int wed = 0;
      public int thu = 0;
      public int fri = 0;
      public int sat = 0;
      public int sun = 0;

      public bool dirty = false;

      public string Name { get { return org == null ? "" : org.name; } }
      public string Address { get { return org == null ? "" : org.address; } }
      public string Agent { get { return org == null ? "" : org.userid; } }

      //public string AgentAdd 
      //{ 
      //   get { return agentAdd; } 
      //   set 
      //   {
      //      if (value == null)
      //         value = "";
      //      if (agentAdd != value)
      //      {
      //         string ov = ValueToString();
      //         agentAdd = value;
      //         SayModifed(ov);
      //      }
      //   } 
      //}

      public int Cicle 
      { 
         get { return cycleType; } 
         set {
            if (cycleType != value)
            {
               string ov = ValueToString();
               cycleType = value;
               SayModifed(ov);
            }
         } 
      }

      public string DCode
      {
         get { return dispatcher; } 
         set {
            if (value == null)
               value = "";
            if (dispatcher != value)
            {
               string ov = ValueToString();
               dispatcher = value;
               SayModifed(ov);
            }
         } 
      }

      public string MRCode
      { 
         get { return merch; } 
         set 
         {
            if (value == null)
               value = "";
            if (value != merch)
            {
               string ov = ValueToString();
               merch = value;
               SayModifed(ov);
            }
         } 
      }
      
      //public string MRAdd
      //{ 
      //   get { return merchAdd; } 
      //   set 
      //   {
      //      if (value == null)
      //         value = "";
      //      if (merchAdd != value)
      //      {
      //         string ov = ValueToString();
      //         merchAdd = value;
      //         SayModifed(ov);
      //      }
      //   } 
      //}

      public int Mon { get { return mon; } set { if (mon != value) { string ov = ValueToString(); mon = value; SayModifed(ov); } } }
      public int Tue { get { return tue; } set { if (tue != value) { string ov = ValueToString(); tue = value; SayModifed(ov); } } }
      public int Wed { get { return wed; } set { if (wed != value) { string ov = ValueToString(); wed = value; SayModifed(ov); } } }
      public int Thu { get { return thu; } set { if (thu != value) { string ov = ValueToString(); thu = value; SayModifed(ov); } } }
      public int Fri { get { return fri; } set { if (fri != value) { string ov = ValueToString(); fri = value; SayModifed(ov); } } }
      public int Sat { get { return sat; } set { if (sat != value) { string ov = ValueToString(); sat = value; SayModifed(ov); } } }
      public int Sun { get { return sun; } set { if (sun != value) { string ov = ValueToString(); sun = value; SayModifed(ov); } } }

      public string ValueToString()
      {
         string ret = agentAdd + "," + dispatcher + "," + merch + "," + merchAdd + "," + cycleType.ToString() + "," +
            mon.ToString() + "," + tue.ToString() + "," + wed.ToString() + "," + thu.ToString() + "," + fri.ToString() + ","
             + sat.ToString() + "," + sun.ToString();

         return ret;
      }

      void SayModifed(string oldValue)
      {
         dirty = true;
         if (Modifed != null)
         {
            string newValue = ValueToString();
            Modifed.Invoke(this, new ModifySheduleEventArgs(oldValue, newValue));
         }
      }

      bool DaysHaveValue(int val)
      {
         return mon == val || tue == val || wed == val || thu == val || fri == val || sat == val || sun == val;
      }

      public int CompareTo(ServoluxSheduleItem other)
      {
         return Name.CompareTo(other.Name);
      }

      void AddDay(Dictionary<string, AgentRouteData> data, string user, string day, string letter)
      {
         if(user.Length > 0)
         {
            if (data.ContainsKey(user) == false)
               data.Add(user, new AgentRouteData());
            AgentRouteData days = data[user];
            if (!days.HaveDay(day))
               days.Add(day, letter);
         }
      }

      void AddDayToUser(Dictionary<string, AgentRouteData> data, int dayValue, string day, int weekNo)
      {
         bool odd = ((weekNo % 2) == 1);
         bool inCicle = Cicle == CICLE_NONE || (Cicle == CICLE_ODD && odd) || (Cicle == CICLE_EVEN && !odd) || 
            (Cicle == CICLE_2 && (weekNo == 1 || weekNo == 3)) || (Cicle == CICLE_4 && weekNo == 1);
         if(dayValue == VISIT)
         {
            if(inCicle)
            {
               AddDay(data, Agent, day, "В");
               //AddDay(data, AgentAdd, day, "В");
               AddDay(data, merch, day, "В");
            }
         }
         else if (dayValue == VISIT_CALL)
         {
            if(Cicle == CICLE_NONE)
            {
               if (!odd)
               {
                  AddDay(data, Agent, day, "В");
               }
               else
               {
                  AddDay(data, Agent, day, "З");
               }
            } else
            {
               if(inCicle)
               {
                  AddDay(data, Agent, day, "В");
               }
               else
               {
                  AddDay(data, Agent, day, "З");
               }
            }
         }
         else if (dayValue == CALL)
         {
            if (inCicle)
            {
               AddDay(data, Agent, day, "З");
               //AddDay(data, AgentAdd, day, "З");
            }
         }
         else if (dayValue == VISIT_MERCH)
         {
            if(inCicle)
            {
               AddDay(data, Agent, day, "ВМ");
               //AddDay(data, AgentAdd, day, "В");
               //AddDay(data, MRCode, day, "М");
            }
         } else if(dayValue == MERCH)
         {
            if (inCicle)
            {
               //AddDay(data, Agent, day, "М");
               //AddDay(data, AgentAdd, day, "М");
               AddDay(data, MRCode, day, "М");
            }
         }
         //else if (dayValue == MERCH_ADD)
         //{
         //   if (inCicle)
         //   {
         //      AddDay(data, Agent, day, "М");
         //      //AddDay(data, MRAdd, day, "М");
         //   }
         //}
         else if (dayValue == DISP)
         {
            if (inCicle)
            {
               AddDay(data, Agent, day, "Д");
               //AddDay(data, AgentAdd, day, "Д");
               AddDay(data, MRCode, day, "Д");
               AddDay(data, DCode, day, "Д");
            }
         }
         else if (dayValue == DISP_MERCH)
         {
            if (Cicle == CICLE_NONE)
            {
               AddDay(data, Agent, day, "М");
               //AddDay(data, AgentAdd, day, "М");
               AddDay(data, MRCode, day, "М");
               AddDay(data, DCode, day, "Д");
            }
            else if (inCicle)
            {
               AddDay(data, Agent, day, "Д");
               //AddDay(data, AgentAdd, day, "Д");
               AddDay(data, MRCode, day, "Д");
               AddDay(data, DCode, day, "Д");
            }
            else
            {
               AddDay(data, Agent, day, "М");
               //AddDay(data, AgentAdd, day, "М");
               AddDay(data, MRCode, day, "М");
            }
         }
         else if (dayValue == VISIT_DISP)
         {
            //if (Cicle == CICLE_NONE)
            //{
            //   AddDay(data, Agent, day, "В");
            //   //AddDay(data, AgentAdd, day, "В");
            //   //AddDay(data, MRCode, day, "В");
            //   AddDay(data, DCode, day, "Д");
            //}
            //else 
            if (inCicle)
            {
               AddDay(data, Agent, day, "В");
               //AddDay(data, AgentAdd, day, "В");
               //AddDay(data, MRCode, day, "В");
               AddDay(data, DCode, day, "В");
            }
            else
            {
               AddDay(data, Agent, day, "Д");
               //AddDay(data, AgentAdd, day, "Д");
               //AddDay(data, MRCode, day, "Д");
               AddDay(data, DCode, day, "Д");
            }
         }
      }

      public Dictionary<string, AgentRouteData> GetUserDayList()
      {
         Dictionary<string, AgentRouteData> ret = new Dictionary<string, AgentRouteData>();

         int ctr = 0;
         int[] days = new int[] {mon, tue, wed, thu, fri, sat, sun};
         foreach (int dv in days)
         {
            string day = WeekDay.fullnames[ctr++];
            for (int i = 1; i <= 4; i++)
               AddDayToUser(ret, dv, i.ToString() + day, i);
         }

         return ret;
      }

      public bool DispError { get { return dispatcher.Length == 0 && (DaysHaveValue(DISP) || DaysHaveValue(DISP_MERCH) || DaysHaveValue(VISIT_DISP)); } }
      public bool CicleError { get { return DaysHaveValue(VISIT_DISP) && Cicle == CICLE_NONE; } }
      public bool MerchError { get  { return merch.Length == 0 && (DaysHaveValue(MERCH)); } }
      //public bool MerchError { get { return merch.Length == 0 && (DaysHaveValue(DISP_MERCH) || DaysHaveValue(VISIT_MERCH)); } }
      //public bool MerchAddError { get { return merchAdd.Length == 0 && DaysHaveValue(MERCH_ADD); } }

      //public bool HaveError { get { return DispError || MerchError || MerchAddError; } }
      public bool HaveError { get { return DispError || MerchError; } }

      public bool NotInRoute { get { return mon == 0 && tue == 0 && wed == 0 && thu == 0 && fri == 0 && sat == 0 && sun == 0; } }

      internal void CheckValues(Dictionary<int, bool> used)
      {
         if(used.ContainsKey(mon) == false)
         {
            mon = 0;
         }
         if (used.ContainsKey(tue) == false)
         {
            tue = 0;
         }
         if (used.ContainsKey(wed) == false)
         {
            wed = 0;
         }
         if (used.ContainsKey(thu) == false)
         {
            thu = 0;
         }
         if (used.ContainsKey(fri) == false)
         {
            fri = 0;
         }
         if (used.ContainsKey(sat) == false)
         {
            sat = 0;
         }
         if (used.ContainsKey(sun) == false)
         {
            sun = 0;
         }
      }
   }

   public class RouteDayData
   {
      public string routeLetter = "";
      public string day = "";
   }

   public class AgentRouteData : List<RouteDayData>
   {
      public bool HaveDay(string day)
      {
         foreach (RouteDayData rd in this)
            if (rd.day == day)
               return true;
         return false;
      }

      public void Add(string day, string letter)
      {
         RouteDayData rd = new RouteDayData();
         rd.day = day;
         rd.routeLetter = letter;
         Add(rd);
      }
   }

   
   public partial class Agent : GRSoft.Network.DataObject
   {
      public int isMerch = 0;
      public int isDsp = 0;
   }

   public partial class OrderItem : GRSoft.Network.DataObject
   {
      public double agentQty;
   }

   public partial class Org : GRSoft.Network.DataObject
   {
      public string ido;
      public int noDrop = 0;
      public string formatTT = "";
      public string idChannel = "";
      public string idRetailer = "";
      //public string userid;
   }

   class DisabledFirms : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "DisabledFirms";

      [KeyField]
      public string id = "";
   }

   public class OrgDogovor : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "ManagerOrgDogovor";

      public string ido = "";
      public string id = "";
      public string firm = "";
      public string name = "";
   }

   //class AgentDailyPlans : GRSoft.Network.DataObject
   //{
   //   public static readonly string OBJECT_NAME = "Plans";

   //   public string firm = "";
   //   public string id = "";
   //   public DateTime date = DateTime.Now;
   //   public string group = "";

   //   public double qty = 0;

   //   public string userid = "";
   //}

   class AgentPlanNew : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "PlanNew";

      public string firm = "";
      public DateTime date = DateTime.Now;
      public string userid = "";
      public int isMonthly = 0;

      public class Item : GRSoft.Network.DataObject
      {
         public String id = "";
         public double qty = 0;
      }

      public List<Item> items = new List<Item>();
   }

   class PlanGroup : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "PlanGroups";

      public string id = "";
      public string group = "";
      public double inPack = 0;
   }

   public class PriceType : GRSoft.Network.DataObject, IComparable<PriceType>
   {
      public static readonly string OBJECT_NAME = "PriceType";

      [KeyField]
      public string id = "";
      public string name = "";

      public override string ToString() { return name; }

      public int CompareTo(PriceType other) { return name.CompareTo(other.name); }

      public string ID { get { return id; } }
      public string Name { get { return name; } }
   }

   public class DistribOrg : BaseDocument
   {
      public static readonly string OBJECT_NAME = "DistribDoc";

      public string priceType = "";

      [Reference("PriceType", "priceType")]
      public PriceType pType = null;

      public string thermalState = "";

      public class Item : GRSoft.Network.DataObject
      {
         public string id = "";

         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price item = null;

         public int exists = -1;

         public string Name { get { return item.Name; } }
         public bool NoHave { get { return exists == 0; } }
         public bool Exists { get { return exists == 1; } }
      }

      public List<Item> items = new List<Item>();
   }

   class PlanChanges : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "PlanChanges";

      public String id = "";
      public String firm = "";

      public double qty = 0;
      public string userid = "";

      public DateTime date = DateTime.Now.Date;
      public DateTime created = DateTime.Now;

      public PlanChanges() { }

      public PlanChanges(Factory f, Agent a, Price p, double qty, DateTime date)
      {
         id = p.id;
         firm = f.id;
         userid = a.id;

         this.qty = qty;
         this.date = date;
         this.created = DateTime.Now;
      }
   }

   public partial class Matrix : DataObject
   {
      public static readonly string ORG_MATRIX = "OrgMatrix";

      public string firm = string.Empty;

      public override string ToString()
      {
         return name;
      }
   }

   public partial class MatrixItem
   {
      public int mustBe = 0;
   }

   public class PlanNew : DataObject
   {
      public static readonly string OBJECT_NAME = "PlanNew";

      public DateTime date = DateTime.MinValue;
      public string firm = string.Empty;
      public string userid = string.Empty;
      public int isMonthly = 0;

      public class Item : DataObject
      {
         public string id = string.Empty;
         public double qty = 0.0;
      }

      public List<Item> items = new List<Item>();
   }

   class IDMTX : DataObject
   {
      public static readonly string IDMTX_OBJ_NAME = "IdMtx";

      [KeyField]
      public string id = string.Empty;
      [KeyField]
      public string firm = string.Empty;
      public string mtx = string.Empty;
   }

   class ObjectMatrix : IDMTX
   {
      public static readonly string IDOMTX_OBJ_NAME = "IdoMtx";

      public static readonly string CHANNEL_OBJ = "channel";
      public static readonly string RETAIL_OBJ = "retail";
      public static readonly string ORG_TYPE_OBJ = "orgtype";
      public static readonly string ORG_OBJ = "org_obj";

      //[KeyField]
      //public string id = string.Empty;
      //[KeyField]
      //public string firm = string.Empty;
      //public string mtx = string.Empty;

      public string objectType = "";
   }


   class DaysGoods : DataObject
   {
      public static readonly string OBJECT_NAME = "DaysGoods";

      public string id = "";
      public string firm = "";
      public int isOrg = 0;

      public List<MatrixItem> items = new List<MatrixItem>();

      public string Key { get { return firm + id; } }
   }

   class ReturnCause : DataObject
   {
      public static readonly string OBJECT_NAME = "ReturnCause";

      [KeyField]
      public string id = "";

      public string name = "";
      public int needPhoto = 0;
      public string idType = "";

      [Reference("PriceType", "idType")]
      public PriceType type;

      public string Name 
      { 
         get { return name; }
         set
         {
            if (name != value)
            {
               name = value;
               if (Owner != null)
                  Owner.SetDirty();
            }
         }
      }

      public string IdType { get { return idType; } set { idType = value; } }
      public PriceType Type { get { return type; } set { type = value; idType = value.id; } }

      public bool Photo 
      { 
         get { return needPhoto != 0; } 
         set
         { 
            needPhoto = value ? 1 : 0;
            if (Owner != null)
               Owner.SetDirty();
         } 
      }

      public string ID { get { return id; } }

      public FmReturnCauseEditor Owner { get; set; }
   }

   public class ReturnLimit : DataObject
   {
      public static readonly string OBJECT_NAME = "ReturnLimit";
      public static readonly int LIMIT_SUM = 0;
      public static readonly int LIMIT_WEIGHT = 1;


      [KeyField]
      public DateTime start = DateTime.Now;

      // дата окончания включается в интервал
      public DateTime end = DateTime.Now;

      [KeyField]
      public string priceType = "";

      [Reference("PriceType", "priceType")]
      public PriceType type;

      [KeyField]
      public string userid = "";

      public int limit = 0;
      public int limitType = LIMIT_SUM;

      public int canOverlimit = 0;

      public string Type { get { return type == null ? "" : type.name; } }
      public DateTime Begin { get { return start; } }
      public DateTime End { get { return end; } }

      public int LimitSum { get { return limitType == LIMIT_SUM ? limit : 0; } }
      public int LimitWeight { get { return limitType == LIMIT_WEIGHT ? limit : 0; } }

      public bool CanOverLimit { get { return canOverlimit != 0; } set { canOverlimit = value ? 1 : 0; } }

      public void SetFrom(ReturnLimit src)
      {
         userid = src.userid;
         start = src.start;
         end = src.end;
         limit = src.limit;
         limitType = src.limitType;
         type = src.type;
         priceType = src.priceType;
         canOverlimit = src.canOverlimit;
      }
   }

   public class ReturnRequest : BaseDocument
   {
      public static readonly string OBJECT_NAME = "ReturnRequest";
      public static readonly string OBJECT_WR_NAME = "ReturnRequestMgr";

      public string firmCode = "";
      public int accepted = 0;
      // имеет корректное значение только если accepted = 1
      public DateTime svChanged;
      public DateTime visitDoc;
      public string svid = "";

      public bool Modified { get; set; }

      public Boolean Accepted { get { return accepted != 0;} }

      public string Handled { get { return accepted == 0 ? "" : svChanged.ToString("dd.MM.yy HH:mm"); } }

      public string FactoryName { get { return Factory.Get(firmCode).name; } }

      public List<RRItem> items = new List<RRItem>();

      public class RRItem : DataObject
      {
         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price item = null;
         public string id = "";

         public int flags = 0;

         [Precision(3)]
         public double qty = 0;
         public double cost = 0;
         public double sum = 0;

         public string cause = "";
         public string uid = "";
         public string svCause = "";
         public DateTime mfrDate;

         public List<ReturnDlv> items = new List<ReturnDlv>();

         //Свойства для отображение в гриде
         //Количество
         public double Qty { get { return qty; } }
         public double SvQty 
         { 
            get 
            {
               double svQty = 0;
               foreach (ReturnDlv rd in items)
                  svQty += rd.svQty;
               return svQty; 
            } 
         }

         //Цена
         public double Cost { get { return (cost == 0) ? sum / qty : cost; } }
         virtual public string SCost { get { return Cost.ToString("C", Config.GetCultureInfo()); } }
         public double Sum { get { return (sum == 0) ? cost * qty : sum; } }
         public string SSum { get { return Sum.ToString("C", Config.GetCultureInfo()); } }

         //Наименование
         public string Item { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }

         public double Weight { get { return item == null ? 0 : item.weight * qty; } }

         public string ID { get { return id; } }
      }

      public class ReturnDlv : DataObject
      {
         public string number = "";
         public DateTime date;
         
         [Precision(3)]
         public double qty;

         [Precision(2)]
         public double cost;

         [Precision(3)]
         public double svQty;

         public string remark = "";
      }
   }

   public partial class OrgFolderItem
   {
      public string kind = "";
   }

   public partial class ScriptDef
   {
      public string kind = "";
      public int isMain = 0;
      public string channel = "";

      public string Kind { get { return kind; } }
   }

   public class NoOrderReason : DataObject
   {
      public static readonly string OBJECT_NAME = "NoOrderReason";

      [KeyField]
      public string id = "";

      public string name = "";

      public string Name { get { return name; } set { name = value; } }
   }

   public partial class ScriptDoc 
   {
      public string noOrderReason = "";
      public int isMain = 0;
   }

   public class OrgType : DataObject, IComparable<OrgType>
   {
      public static readonly string OBJECT_NAME = "OrgTypes";

      [KeyField]
      public string id = "";
      public string name = "";

      public int CompareTo(OrgType other)
      {
         return name.CompareTo(other.name);
      }
   }

   public class SalesChannel : DataObject
   {
      public static readonly string OBJECT_NAME = "SalesChannel";

      [KeyField]
      public string id = "";
      public string name = "";

      public override string ToString()
      {
         return name;
      }
   }

   public class SalesTypes : DataObject, IComparable<SalesTypes>
   {
      public static readonly string OBJECT_NAME = "SalesTypes";

      [KeyField]
      public string id = "";
      public string name = "";

      int IComparable<SalesTypes>.CompareTo(SalesTypes other)
      {
         return name.CompareTo(other.name);
      }
   }

   public class Retailers : DataObject
   {
      public static readonly string OBJECT_NAME = "Retailers";

      [KeyField]
      public string id = "";
      public string name = "";
   }

   public class MMLFeatures : DataObject
   {
      public static readonly string OBJECT_NAME = "MMLFeatures";

      public static readonly string ORG_TYPE_KIND = "orgType";
      public static readonly string SALES_PLACE_KIND = "salesPlace";


      public string id = "";
      public string kind = "";

      public class Item : DataObject
      {
         public string id = "";
      }

      public List<Item> items = new List<Item>();

      public bool IsOrgType { get { return kind == ORG_TYPE_KIND; } }
   }

   public partial class OrderDetailRepresentation
   {
      public string RouteStepKind 
      {
         get
         {
            OrgRouteOrder oro = RouteOrder;
            if (oro != null && oro.RouteItem != null)
               return oro.RouteItem.kind;
            return "";
         }
      }
      public string ShortFirmName 
      { 
         get
         {
            string ret = "";
            string firmCode = "";
            Order o = dataObject as Order;
            if(o != null)
               firmCode = o.firmCode;
            else
            {
               ReturnRequest rr = dataObject as ReturnRequest;
               if(rr != null)
                  firmCode = rr.firmCode;
            }
            if(firmCode != "")
            {
               foreach(Factory f in Factory.GetFactories())
               {
                  if(f.id == firmCode)
                  {
                     ret = f.shortName;
                     break;
                  }
               }
            }
            return ret;
         }
      }
   }
}

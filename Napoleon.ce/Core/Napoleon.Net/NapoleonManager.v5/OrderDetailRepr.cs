using System;
using System.Globalization;
using GRSoft.NapoleonManager.Utils;
using System.Collections.Generic;
using GRSoft.Network;
namespace GRSoft.NapoleonManager
{
   public partial class OrderDetailRepresentation : ODRComapartor
   {
      //Дата создания заявки
      protected DateTime dateCreated;
      //Дата исполнения заявки
      protected DateTime dateExec;
      //Дата передачи
      protected DateTime sended;
      protected Org nOrg;
      //Организация
      protected string org = "";
      //Сумма заявки
      protected double sum;
      //Сумма инкасации
      protected double isum;
      //Тип документа
      protected ObjType doctype;
      //Адрес организации
      protected string orgAddr = "";
      //Полный объект
      public GRSoft.Network.DataObject dataObject;
      //Один день
      protected bool oneDay;
      //Заметки
      protected string notes = "";

      protected string number = "";

      protected int qty;

      int timeZone = 0;
      int serverTimeZone = 0;

      protected OrgRouteOrder routeOrder = null;

      OrdersDetail owner = null;

      Location orgLocation = null;
      Location docLocation = null;

#if Tyapkin
      private const string ONE_DAY_DATE_MAKS = "HH:mm:ss";
      private const string PERIOD_DAY_DATE_MASK = "dd.MM.yy HH:mm:ss";
#else
      private const string ONE_DAY_DATE_MAKS = "HH:mm";
      private const string PERIOD_DAY_DATE_MASK = "dd.MM.yy HH:mm";
#endif

      public OrderDetailRepresentation(BaseDocument doc, ObjType doctype, bool oneDay) :
         this(doc.created, doctype, doc.date, doc.sended, doc.Org, doc.Sum(), 0, doc.Qty, doc, oneDay, doc.remark)
      {
         timeZone = doc.timeZone;
         serverTimeZone = doc.serverTimeZone;

#if FIND_DOC_LOCAION
         docLocation = new Location(doc.latitude, doc.longitude);
         if (doc.org != null)
            orgLocation = Route.GetLocation(doc.org);
#endif
      }

      public OrderDetailRepresentation(DateTime created, ObjType doctype,
         DateTime date, DateTime sended, Org org, double sum, double isum, int qty, 
         GRSoft.Network.DataObject dataObject, bool oneDay)
      {
         this.nOrg = org;

         this.dateCreated = created;
         this.dateExec = date;
         this.sended = sended;
         this.sum = sum;
         this.isum = isum;
         this.doctype = doctype;
         this.dataObject = dataObject;
         this.oneDay = oneDay;
         this.qty = qty;

         Sales s = dataObject as Sales;
         if (s != null)
            number = s.number;

#if Agama
         if (org != null)
         {
            int uc = -1;
            Order o = dataObject as Order;
            if (o != null) uc = o.unitCode;
            else
            {
               Visit v = dataObject as Visit;
               if (v != null) uc = v.unitCode;
            }

            this.org = org.Name;
            this.orgAddr = (org.address == null) ? "" : org.address;

            if (uc != -1 && org.units != null)
            {
               foreach (Org.UnitItem item in org.units)
               {
                  if (item.id == uc)
                  {
                     this.orgAddr = item.name;
                     this.org += " " + item.name;
                     break;
                  }
               }
            }
         }
#elif DELIVERY_ADDRESS
         Order o = dataObject as Order;

         if (o != null)
         {
            this.orgAddr = o.OrgAddr;
            this.org = Config.GetConfig().isFullOrgName ?
               String.Format("{0} ({1})", org.name, o.OrgAddr) :
               org.name;
         }
         else
         {
            this.org = org.Name;
            this.orgAddr = (org.address == null) ? "" : org.address;
         }
#else
         if (org != null)
         {
            this.org = org.Name;
            this.orgAddr = (org.Address == null) ? "" : org.Address;
         }
#endif
      }

      public OrderDetailRepresentation(DateTime created, ObjType doctype,
         DateTime date, DateTime sended, Org org, double sum, double isum, int qty,
         GRSoft.Network.DataObject dataObject, bool oneDay, string notes)
         : this(created, doctype, date, sended, org, sum, isum, qty,
         dataObject, oneDay)
      {
         this.notes = notes;
      }

      public void SetOwner(OrdersDetail owner) { this.owner = owner; }

      public int Recno { get { return owner == null ? 0 : owner.IndexOf(this) + 1; } }

      public Org NOrg { get { return nOrg; } }

      public string ID { get { return nOrg == null ? "" : nOrg.id; } }

      public string Number { get { return number; } }

      public DateTime DateCreatedDT { get { return dateCreated; } }
      public DateTime DateSendedDT { get { return sended; } }
      public String DateCreated 
      { 
         get 
         {
            if( dateCreated == DateTime.MinValue || dateCreated == new DateTime(1601, 1, 1)  )
               return "";

#if USE_TIMEZONE
            TimeSpan ts = TimeZone.CurrentTimeZone.GetUtcOffset(DateTime.Now);
            DateTime dt = dateCreated.AddMinutes(timeZone).Add(ts);
#else
            DateTime dt = dateCreated;
#endif
            return dt.ToString(DateFormat);
         } 
      }

      public String DateExec 
      { 
         get 
         {
            return dateExec == DateTime.MinValue || dateExec == new DateTime(1601, 1, 1) ?
               string.Empty : dateExec.ToString("dd.MM.yy");
         }
      }

      public virtual String Sended
      {
         get
         {
            if( sended == DateTime.MinValue || sended == new DateTime(1601, 1, 1) )
               return string.Empty;
#if USE_TIMEZONE
            TimeSpan ts = TimeZone.CurrentTimeZone.GetUtcOffset(DateTime.Now);
            DateTime dt = sended.AddMinutes(serverTimeZone).Add(ts);
#else
            DateTime dt = sended;
#endif

            return dt.ToString(DateFormat);
         }
      }

      // problem with FmRoute. It contains WebView reference
      //public void CheckDocLocation(DataSet<DateTime, GPSPos> dsGPSPos)
      //{
      //   if (dsGPSPos != null && docLocation.IsEmpty)
      //   {
      //      GPSPos pos = FmRoute.GetBestLocation(dateCreated, dsGPSPos);
      //      if(pos != null)
      //      {
      //         docLocation.Latitude = pos.latitude;
      //         docLocation.Longitude = pos.longitude;
      //      }
      //   }
      //}

      public int Qty { get { return qty; } }

      public string Org { get { return org; } }
      public string Sum 
      { 
         get 
         {
            string ret = "";
            if( sum != 0.0 )
            {
               try
               {
                  ret = sum.ToString("C", Config.GetCultureInfo());
               } catch(Exception)
               {
                  ret = sum.ToString();
               }
            }
            return ret; 
         } 
      }
      public string ISum 
      { 
         get 
         {
            string ret = "";
            if (isum != 0.0)
            {
               try
               {
                  ret = isum.ToString("C", Config.GetCultureInfo());
               }
               catch (Exception)
               {
                  ret = isum.ToString();
               }
            }
            return ret;
         }
      }

      public ObjType Doctype { get { return doctype; } }
      public string OrgAddr { get { return orgAddr; } }
      public GRSoft.Network.DataObject StoreObject { get { return dataObject; } }
      private string DateFormat { get { return oneDay ? ONE_DAY_DATE_MAKS : PERIOD_DAY_DATE_MASK; } }

      public double DblSum { get { return sum; } }

      [Compare]
      public static ORDCompareCondition CC = new ORDCompareCondition();

      public string Notes { get { return notes; } }

      public double Weight
      {
         get
         {
            Order o = dataObject as Order;
            return o == null ? 0 : o.Weight;
         } 
      }

      public OrgRouteOrder RouteOrder { get { return routeOrder; } set { routeOrder = value; } }

      double distance = Double.MinValue;

      public double DocDistanceDouble
      { 
         get 
         { 
            if(distance == Double.MinValue)
            {
               distance = Double.MaxValue;
               if ( docLocation != null && !docLocation.IsEmpty && orgLocation != null && !orgLocation.IsEmpty)
                  distance = GRSoft.NapoleonManager.Location.Distance(orgLocation, docLocation);
            }

            return distance;
         } 
      }

      public string DocDistance
      {
         get
         {
            double range = DocDistanceDouble;
            if (Double.MaxValue == range)
               return "";
            return range.ToString("N2") + ((docLocation != null && !docLocation.IsEmpty && docLocation.IsGsm) ? " GSM" : "");
         }
      }

      public Location OrgLocation { get { return orgLocation; } }
      public Location DocLocation { get { return docLocation; } }

      public string GetDocTypeCaption()
      {
         string result = doctype.ToString();

         if (doctype.Val == ObjType.TObjType.Script)
         {
            DataSet<int, ScriptDef> df = DataModule.Get(ScriptDef.OBJECT_NAME) as DataSet<int, ScriptDef>;
            ScriptDoc sd = dataObject as ScriptDoc;

            if (df != null && sd != null)
            {
               if (df.ContainsKey(sd.scriptId))
                  result = df[sd.scriptId].Name;
            }
         }

         return result;
      }

      public string ScriptName { get; set; }

      public String StlTime
      {
         get
         {
            string result = string.Empty;

            BaseDocument sd = dataObject as BaseDocument;

            if (sd != null && sd.StlTime.Year != 1970)
            {
               result = sd.StlTime.ToString("HH:mm dd.MM.yy");
            }

            return result;
         }
      }

   }

   public class ODRComapartor : CmpByField<OrderDetailRepresentation>
   {
      public override int CompareTo(OrderDetailRepresentation other)
      {
         ORDCompareCondition cc = (ORDCompareCondition)GetCompareCondition();
         if (cc.Fields.Length > 0 && cc.Fields[0] == "RouteOrder")
         {
            OrgRouteOrder el1 = ((OrderDetailRepresentation)this).RouteOrder;
            OrgRouteOrder el2 = ((OrderDetailRepresentation)other).RouteOrder;
            if(el1 != null && el2 != null)
               return cc.IsAscending ? el1.CompareTo(el2) : el2.CompareTo(el1);
         }

         if (cc.Fields.Length > 0 && cc.Fields[0] == "DocDistance")
         {
            if (other == null)
               return -1;

            double val1 = ((OrderDetailRepresentation)this).DocDistanceDouble;
            double val2 = ((OrderDetailRepresentation)other).DocDistanceDouble;
            if (val1 == Double.MaxValue)
               return val2 == Double.MaxValue ? 0 : 1;
            if (val2 == Double.MaxValue)
               return -1;

            return cc.IsAscending ? (int)(val1 - val2) : (int)(val2 - val1);
         }

         const int LESS = -1;
         const int GREATER = 1;
         if (((OrderDetailRepresentation)this).Doctype.Equals(ObjType.TObjType.NotVisit) &&
            !other.Doctype.Equals(ObjType.TObjType.NotVisit))
            return GREATER;
         else if (!((OrderDetailRepresentation)this).Doctype.Equals(ObjType.TObjType.NotVisit) &&
            other.Doctype.Equals(ObjType.TObjType.NotVisit))
            return LESS;
         else
         {
            if (cc == null)
               throw new NotImplementedException();

            if (cc.Fields.Length == 1)
               return base.CompareTo(other);
            else
            {
               int result = 0;

               for (int i = 0; i < cc.Fields.Length; i++)
               {
                  result = Comparator.CompareItems(this, other, cc.Fields[i], cc.IsAscending);
                  if (result != 0)
                     return result;
               }

               return result;
            }
         }
      }
   }

   public class ORDCompareCondition : CompareCondition
   {
      private string[] fields;

      public void SetCompareCondition(string[] fields, bool isAscending)
      {
         this.fields = fields;
         this.isAscending = isAscending;
         this.fieldName = fields[0];
      }

      public string[] Fields { get { return fields; } }
   }

   public class OrgRouteOrder : IComparable
   {
      private DateTime objDate;
      private DateTime date;
      private int pos;
      bool outRoute;
      List<OrderDetailRepresentation> owner;
      OrderDetailRepresentation obj;
      ScheduleItem routeItem;

      public OrgRouteOrder(OrderDetailRepresentation obj, DateTime date, int pos, List<OrderDetailRepresentation> owner, ScheduleItem routeItem)
      {
         this.objDate = obj.DateCreatedDT;
         this.date = date;
         this.pos = pos;
         this.owner = owner;
         outRoute = false;
         this.obj = obj;
         this.routeItem = routeItem;
      }

      public ScheduleItem RouteItem { get { return routeItem; } }

      void UpdatePos()
      {
         if( pos < 0 && outRoute == false )
         {
            DateTime dt = DateTime.MinValue;
            foreach (OrderDetailRepresentation odr in owner)
            {
               OrgRouteOrder ro = odr.RouteOrder;
               if (ro == null || ro.outRoute || ro.pos < 0)
                  continue;

               DateTime cr = odr.DateCreatedDT;
               if (cr.CompareTo(objDate) < 0 && cr.CompareTo(dt) > 0)
               {
                  dt = cr;
                  pos = odr.RouteOrder.pos;
               }
            }

            outRoute = true;
         }
      }

      public int CompareTo(object obj)
      {
         OrgRouteOrder o = obj as OrgRouteOrder;
         if (o == null)
            return 1;
         int cmp = date.CompareTo(o.date);
         if (cmp != 0)
            return cmp;

         if (pos < 0)
            UpdatePos();
         if (o.pos < 0)
            o.UpdatePos();

         cmp = pos - o.pos;
         if (cmp != 0)
            return cmp;

         return objDate.CompareTo(o.objDate);
      }

      public override string ToString()
      {
         return pos < 0 || outRoute ? "" : pos.ToString();
      }
   }
}
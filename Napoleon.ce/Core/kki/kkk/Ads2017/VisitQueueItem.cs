using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;
using System.Xml;

namespace Ads2017
{
   public class VisitType : ObjType
   {
      public string typeName = null;

      public VisitType(TObjType val)
         : base(val)
      {
      }

      public VisitType(string objName)
      {
         if (!FromString(objName))
         {
            val = TObjType.NotVisit;
            typeName = objName;
         }
      }

      public override string ToString()
      {
         if (typeName != null)
            return typeName;
         return base.ToString();
      }

      public virtual bool IsStopType { get { return typeName != null; } }
   }

   public class VisitQueueItem
   {
      public DateTime startTime;
      public Org org;
      public double latitude;
      public double longitude;
      public VisitType objType;

      public DateTime endTime = DateTime.MinValue;
      public string address;

      public string factAddress;
      public bool outOfRange;

      public double sum;

      public string number;
      public string color = "green";
      public BaseDocument doc;

      public VisitQueueItem(DateTime dtVisit, Org org, double latitude, double longitude, VisitType objType)
      {
         this.startTime = dtVisit;
         this.org = org;
         this.latitude = latitude;
         this.longitude = longitude;
         this.objType = objType;
      }

      public VisitQueueItem(BaseDocument doc, VisitType objType)
      {
         this.startTime = doc.created;
         this.org = doc.org;
         this.latitude = doc.latitude;
         this.longitude = doc.longitude;
         this.objType = objType;
         this.doc = doc;
      }

      public bool HavePosition { get { return (latitude != 0 || longitude != 0); } }

      public string OrgName 
      { 
         get
         { 
            return org != null ?  org.Name :  ""; 
         }
      }

      public string StopTime
      {
         get
         {
            if (endTime == DateTime.MinValue)
               return "";
            TimeSpan ts = endTime.Subtract(startTime);
            int min = ts.Minutes;
            return (min < 60) ? min + " мин." : (min / 60) + " ч" + (min % 60) + " мин.";
         }
      }
   }

   public class CmpVisitQueueItem : IComparer<VisitQueueItem>
   {

      #region IComparer<VisitQueueItem> Members

      public int Compare(VisitQueueItem x, VisitQueueItem y)
      {
         return x.startTime.CompareTo(y.startTime);
      }

      #endregion
   }

   public class ObjType
   {
      public enum TObjType
      {
         OtOrder, OtVisit, OtOrgRemnants, DayDoc,
         PKO, Incass, OtReturn, Script, Monitoring,
         NotVisit, OutRoute, Answer, Sales, Bonus, OrderW, Move, Distr,
         Invoice, OrderCharge, ArchIncass,
         VandAudit, VandSales, VandReload, VandRestock,
         CommonAudit, PromoAudit, Merch, Actions, OrgDistrib, Procuration,
         ArchSales, ArchReturns, DefectReport, Rko, Census, ItemsAudit, RfrgAudit, Contract, Task
      }
      protected TObjType val;

      public ObjType(TObjType val)
      {
         this.val = val;
      }

      public ObjType(string objName)
      {
         FromString(objName);
      }

      protected ObjType() { }

      protected bool FromString(string objName)
      {
         switch (objName)
         {
            case "OrderCharge":
               val = TObjType.OrderCharge;
               break;
            case "Order":
               val = TObjType.OtOrder;
               break;
            case "OrgRemnants":
               val = TObjType.OtOrgRemnants;
               break;
            case "Visit":
               val = TObjType.OtVisit;
               break;
            case "PKO":
               val = TObjType.PKO;
               break;
            case "Incass":
               val = TObjType.Incass;
               break;
            case "ArchIncass":
               val = TObjType.ArchIncass;
               break;
            case "Script":
               val = TObjType.Script;
               break;
            case "NotVisit":
               val = TObjType.NotVisit;
               break;
            case "OutRoute":
               val = TObjType.OutRoute;
               break;
            case "OtReturn":
               val = TObjType.OtReturn;
               break;
            case "Monitoring":
               val = TObjType.Monitoring;
               break;
            case "Answer":
               val = TObjType.Answer;
               break;
            case "Sales":
               val = TObjType.Sales;
               break;
            case "Bonus":
               val = TObjType.Bonus;
               break;
            case "OrderW":
               val = TObjType.OrderW;
               break;
            case "Move":
               val = TObjType.Move;
               break;
            case "DistrDoc":
               val = TObjType.Distr;
               break;
            case "Remnants":
               val = TObjType.Invoice;
               break;
            case "VandAudit":
               val = TObjType.VandAudit;
               break;
            case "VandSales":
               val = TObjType.VandSales;
               break;
            case "VandRestock":
               val = TObjType.VandRestock;
               break;
            case "VandReload":
               val = TObjType.VandReload;
               break;
            case "CommonAudit":
               val = TObjType.CommonAudit;
               break;
            case "PromoAudit":
               val = TObjType.PromoAudit;
               break;
            case "Merch":
               val = TObjType.Merch;
               break;
            case "Actions":
               val = TObjType.Actions;
               break;
            case "OrgDistrib":
               val = TObjType.OrgDistrib;
               break;
            case "Procuration":
               val = TObjType.Procuration;
               break;
            case "ArchSales":
               val = TObjType.ArchSales;
               break;
            case "ArchReturns":
               val = TObjType.ArchReturns;
               break;
            case "DefectReport":
               val = TObjType.DefectReport;
               break;
            case "Rko":
               val = TObjType.Rko;
               break;
            case "ItemsAudit":
               val = TObjType.ItemsAudit;
               break;
            case "RfrgAudit":
               val = TObjType.RfrgAudit;
               break;
            case "Contract":
               val = TObjType.Contract;
               break;
            case "Task":
               val = TObjType.Task;
               break;
            default:
               return false;
         }
         return true;
      }

      public override string ToString()
      {
         switch (val)
         {
            case TObjType.OrderCharge:
               return "Заявка на борт";
            case TObjType.OtOrder:
#if MobileAssistant
               return "ЗАКАЗ";
#else
               return "Заявка";
#endif
            case TObjType.OtOrgRemnants:
               return "Съем остатков";
            case ObjType.TObjType.OtVisit:
               return "Посещение";
            case ObjType.TObjType.DayDoc:
               return "Рабочий день";
            case ObjType.TObjType.PKO:
               return "ПКО";
            case ObjType.TObjType.Incass:
               return "Инкассация";
            case ObjType.TObjType.ArchIncass:
               return "Арх.инкассация";
            case ObjType.TObjType.Script:
               return "Визит";
            case ObjType.TObjType.NotVisit:
               return "Не посетил";
            case ObjType.TObjType.OutRoute:
               return "Не по маршруту";
            case ObjType.TObjType.OtReturn:
               return "Возврат";
            case TObjType.Monitoring:
               return "Мониторинг";
            case TObjType.Answer:
               return "Анкета";
            case TObjType.Sales:
               return "Продажи";
            case TObjType.Bonus:
#if Prodo || Halygov
               return "Дегустация";
#else
               return "Бонус";
#endif
            case TObjType.OrderW:
#if Alecon
               return "Заявка(сети)";
#else
               return "Заказ покупателя";
#endif
            case TObjType.Move:
               return "Перемещение";
            case TObjType.Distr:
               return "Наличие";
            case TObjType.Invoice:
               return "Накладная";
            case TObjType.VandAudit:
               return "Ревизия";
            case TObjType.VandSales:
               return "Продажа";
            case TObjType.VandReload:
               return "Перезагрузка";
            case TObjType.VandRestock:
               return "Заявка на борт";
            case TObjType.PromoAudit:
               return "Аудит акций";
            case TObjType.CommonAudit:
               return "Общий аудит";
            case TObjType.Merch:
               return "Мерчендайзинг";
            case TObjType.Actions:
               return "Акции";
            case TObjType.OrgDistrib:
               return "Дистриб.";
            case TObjType.Procuration:
               return "Заказ доверенности";
            case TObjType.ArchReturns:
               return "Арх.возврат";
            case TObjType.ArchSales:
               return "Арх.продажи";
            case TObjType.DefectReport:
               return "Рапорт о неисправностях";
            case TObjType.Rko:
               return "РКО";
            case TObjType.Census:
               return "Census док.";
            case TObjType.ItemsAudit:
               return "Аудит товаров";
            case TObjType.RfrgAudit:
               return "Аудит холодильников";
            case TObjType.Contract:
               return "Контракт";
            case TObjType.Task:
               return "Задача";
            default: return string.Empty;
         }
      }

      public int CompareTo(ObjType ot)
      {
         return (int)val - (int)ot.val;
      }

      public bool Equals(ObjType.TObjType type)
      {
         return val == type;
      }

      public ObjType.TObjType Val { get { return val; } }
   }

   public class OrgRouteQueueItem : CmpByField<OrgRouteQueueItem>
   {
      private int pos;
      private OrgFolderItem org;
      private List<WeekDay> days = new List<WeekDay>();
      private bool w1 = false;
      private bool w2 = false;
      private bool w3 = false;
      private bool w4 = false;
      private int index;

      public OrgRouteQueueItem(int pos, OrgFolderItem org, WeekDay day)
      {
         this.pos = pos;
         this.org = org;
         index = pos;
         days.Add(day);
      }

      public OrgRouteQueueItem(int pos, OrgFolderItem org, WeekDay day, int week) :
         this(pos, org, day)
      {
         SetItemActiveForWeek(week);
      }

      public int Pos { get { return pos; } set { pos = value; } }
      public OrgFolderItem Item { get { return org; } }
      public string OrgName { get { return org == null || org.org == null ? string.Empty : org.org.Name; } }
      public string OrgID { get { return org == null || org.org == null ? string.Empty : org.org.id; } }
      public string Day
      {
         get
         {
            StringBuilder sb = new StringBuilder();
            foreach (WeekDay wd in days)
            {
               if (sb.Length > 0)
               {
                  sb.Append(", ");
               }

               sb.Append(wd.ShortName);
            }

            return sb.ToString();
         }
      }

      public bool ContainsDay(WeekDay wd)
      {
         foreach (WeekDay d in days)
            if (d.Number == wd.Number)
               return true;
         return false;
      }


      public string Address { get { return org == null || org.org == null ? string.Empty : org.org.Address; } }

      public Location Location
      {
         get
         {
            Location result = null;
            //if (org != null && org.org != null)
            //{
            //   OrgLocations ol = OrgLocations.GetDataSet();
            //   OrgLocation loc = ol.GetLocation(org.org.id);
            //   if (loc != null)
            //      result = new Location(loc.latitude, loc.longitude);
            //   else if (org.org.latitude != 0 && org.org.longitude != 0)
            //      result = new Location(org.org.latitude, org.org.latitude);
            //}

            return result;
         }
      }

      public void AddDays(List<WeekDay> day)
      {
         foreach (WeekDay d in day)
            if (!days.Contains(d))
               days.Add(d);
      }

      public List<WeekDay> GetDays()
      {
         return days;
      }

      [Compare]
      public static CompareCondition CC = new CompareCondition();

      public bool IsItemActiveForWeek(int weekNumber)
      {
         switch (weekNumber)
         {
            case 1: return w1;
            case 2: return w2;
            case 3: return w3;
            case 4: return w4;
            default: return false;
         }
      }

      public void SetItemActiveForWeek(int weekNumber)
      {
         switch (weekNumber)
         {
            case 1: w1 = true; break;
            case 2: w2 = true; break;
            case 3: w3 = true; break;
            case 4: w4 = true; break;
         }
      }

      public void SetItemForAllWeek()
      {
         w1 = w2 = w3 = w4 = true;
      }

      public bool W1 { get { return w1; } set { w1 = value; } }
      public bool W2 { get { return w2; } set { w2 = value; } }
      public bool W3 { get { return w3; } set { w3 = value; } }
      public bool W4 { get { return w4; } set { w4 = value; } }
      public int Index { get { return index; } set { index = value; } }
   }

   public class Location
   {
      private double latitude = 0.0;
      private double longitude = 0.0;
      private bool isGsm = false;
      public bool isVisitPoint = false;

      public double speed = 0.0;
      public DateTime date = DateTime.MinValue;

      public Location() { }

      public Location(double latitude, double longitude, bool isGsm, double speed, DateTime date)
      {
         this.latitude = latitude;
         this.longitude = longitude;
         this.isGsm = isGsm;
         this.speed = speed;
         this.date = date;
      }

      public Location(double lat, double lng)
      {
         this.latitude = lat;
         this.longitude = lng;
      }

      public double Latitude { get { return latitude; } set { latitude = value; } }
      public double Longitude { get { return longitude; } set { longitude = value; } }
      public bool IsGsm { get { return isGsm; } set { isGsm = value; } }

      public static double Distance(Location l1, Location l2)
      {
         return Coordutils.Distance(l1.latitude, l1.longitude, l2.latitude, l2.longitude);
      }

      public string GetAddress()
      {
         string address = "";
         try
         {
            if (addresses.ContainsKey(this))
               address = addresses[this];
            else
            {
               CultureInfo enus = CultureInfo.GetCultureInfo("en-US");
               XmlDocument doc = Route.GetYandexRequest(longitude.ToString(enus) + "," + latitude.ToString(enus));
               XmlNamespaceManager nsmgr = new XmlNamespaceManager(doc.NameTable);
               nsmgr.AddNamespace("ab", "http://maps.yandex.ru/geocoder/1.x");
               XmlNode res = doc.SelectSingleNode("//ab:GeocoderMetaData/ab:text", nsmgr);
               if (res != null)
               {
                  char[] sep = new char[] { ',' };
                  string[] v = res.InnerText.Split(sep);
                  if (v.Length > 2)
                  {
                     for (int i = 2; i < v.Length; i++)
                        address += v[i] + ",";

                     address = address.TrimEnd(sep);
                  }
                  else
                     address = res.InnerText;

                  addresses[this] = address;
               }
            }
         }
         catch (Exception)
         {
         }
         return address;
      }

      private static Dictionary<Location, string> addresses = new Dictionary<Location, string>();
   }
}

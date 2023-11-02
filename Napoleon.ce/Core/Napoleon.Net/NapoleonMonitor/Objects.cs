/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Объекты данных
 * 
 * ert   21/04/2010   creating
 */

using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using GRSoft.Network;
using System.Collections;
using System.Globalization;
using GRSoft.NapoleonManager.Utils;
using System.Drawing;
using System.IO;
using System.ComponentModel;

namespace GRSoft.NapoleonManager
{
   delegate void EmptyParamHandler();
   delegate void InvokeParamHandler(object objects);

   //Исключение когда пришел объет не того типа что ожидали (23.09.2010 kki)
   class EIncompatibilityObject : Exception { }

   //Исключение когда данные неправильны или пришли не все
   class EDataCorrupted : Exception { }

   public class ContractDef : DataObject
   {
      public static readonly string OBJECT_NAME = "ContractDef";
      private static readonly string DATE_FMT = "dd/MM/yyyy";

      [KeyField]
      public string id = "";

      public string name = "";
      public DateTime start = DateTime.Now;
      public DateTime finish = DateTime.Now;

      public class Item : DataObject
      {
         public string id;
      }

      public List<Item> items = new List<Item>();

      public String Name { get { return name; } }
      public String Start { get { return start.ToString(DATE_FMT); } }
      public String Finish { get { return finish.ToString(DATE_FMT); } }

      public override string ToString()
      {
         return string.Format("{0} ({1} - {2})", Name, Start, Finish);
      }
   }

   public class NBTLViewer : DataObject
   {
      public static readonly string OBJECT_NAME = "NBTLViewer";

      [KeyField]
      public string id = "";
      public string name = "";
      public string password = "";
      public int division = 0;

      public class Item : DataObject
      {
         public string id = "";
         public Item() { }
         public Item(string id) { this.id = id; }
      }

      public List<Item> contracts = new List<Item>();
   }

   public class DivisionManager : DataObject
   {
      public static readonly string OBJECT_NAME = "DivisionManager";

      [KeyField]
      public string login = "";

      public string password = "";
      public int division = 0;
      public string name = "";

      public string Login { get { return login; } set { login = value; } }
      public string Password { get { return password; } set { password = value; } }
      public string Name { get { return name; } set { name = value; } }

      public class Rights : DataObject
      {
         public string token = "";
         public int type = 0;
         public int right = 0;
      }
      public List<Rights> rights = new List<Rights>();
   }

   public class UserLog : DataObject
   {
      static public readonly string OBJECT_NAME = "UserLog";

      [Reference("Agents", "userid")]
      public Agent agent = null;

      /// <summary>
      /// Дата передачи
      /// </summary>
      public DateTime date = DateTime.Now;

      public string objType = "";

      /// <summary>
      /// Дата (ключ документа
      /// </summary>
      public DateTime objDate = DateTime.Now;

      public DateTime Date { get { return objDate; } }
      public string Agent { get { return (agent == null) ? "?" : agent.name; } }
      public string Action
      {
         get
         {
            switch (objType)
            {
               case "Order":
                  return "Заявка";
               case "OrgRemnants":
                  return "Съем остатков";
               case "Visit":
                  return "Посещение";
            }

            return "";
         }
      }

      public ObjType ObjType { get { return new ObjType(objType); } }
      public int action;
      public int category;
      public string comments;

      public class ActionInfo
      {
         public int action;
         public string name;
         public ActionInfo(int action, string name)
         {
            this.action = action;
            this.name = name;
         }

         public override string ToString()
         {
            return name;
         }
      }

      static ActionInfo[] logActions;
      public static ActionInfo[] LogActions
      {
         get
         {
            if (logActions == null)
            {
               logActions = new ActionInfo[] { 
                  new ActionInfo(1,"GPS - Включен"), new ActionInfo(2, "GPS - Выключен"), new ActionInfo(3, "Время изменено"),
                  new ActionInfo(4, "КПК - Включен"), new ActionInfo(5, "КПК - Выключен"), new ActionInfo(6, "Сбой программы"),
                  new ActionInfo(7, "Наполеон - Запуск"), new ActionInfo(8, "Наполеон - Выход"), new ActionInfo(9, "КПК статус:"), 
                  new ActionInfo(10, "Фоновая синхронизация"), new ActionInfo(11, "Очистка базы")};
  
            }
            return logActions;
         }
      }

      public string userAction
      {
         get
         {
            if( action == 9 )
               return String.Format("КПК статус: {0}", comments);
            if( action == 3)
               return String.Format("{0} ({1})", logActions[2].ToString(), comments);
            foreach (ActionInfo ai in logActions)
               if (ai.action == action)
                  return ai.name;
            return string.Format("Неизвестный код события({0}, требуется обновить программу)", action);
            //switch (action)
            //{ 
            //   case 1:
            //      return "GPS - Включен";
            //   case 2:
            //      return "GPS - Выключен";
            //   case 3:
            //      return "Время изменено";
            //   case 4:
            //      return "КПК - Включен";
            //   case 5:
            //      return "КПК - Выключен";
            //   case 6:
            //      return "Сбой программы";
            //   case 7:
            //      return "Наполеон - Запуск";
            //   case 8:
            //      return "Наполеон - Выход";
            //   case 9:
            //      return String.Format("КПК статус: {0}", comments);
            //   case 10:
            //      return "Фоновая синхронизация";
            //   case 11:
            //      return "Очистка базы";
            //   default:
            //      return string.Format("Неизвестный код события({0}, требуется обновить программу)", action);
            //}
         }
      }
   }

   public class SysColor : DataObject
   {
      static public readonly string OBJECT_NAME = "ColorsTable";
      public enum Type { Price = 0, Org = 1 };

      public string id = "";
      public int type = 0;
   }

   public class SysForeColor : SysColor
   {
      public int face = 0;
   }

   public class SysBackColor : SysColor
   {
      public int back = 0;
   }

   public class ObjType
   {
      public enum TObjType { OtOrder, OtVisit, OtOrgRemnants, DayDoc, 
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

   public class Folder : DataObject
   {
      public static readonly string OBJECT_NAME = "Folder";

      [KeyField]
      public string fid = "";

      public int level = 0;
      public string name = "";
      public string userid = "";
      public int id = 0;
   }

   public partial class Price : DataObject
   {
#if Vyatich
      // для Вятича проще передать обычный прайс, а код папки симитировать
      public static readonly string OBJECT_NAME = "Price";
#else
      public static readonly string OBJECT_NAME = "ManagerPrice";
#endif

      [KeyField]
      public string id = "";
      public string name = "";
      public double weight = 0;
      public double qty = 0.0;
      public int folderID = 0;

      //Поле для связи с FOLDERS 
      //25.09.2010 kki 
      // !!! Внимание !!!
      //Если вдруг это поле перестало приходить необоходимо 
      //посмотреть файл на сервере addDefs.xml там может быть установлен аттрибут этого поля 
      //hiden = true
#if Vyatich
      public string fid { get { return folderID.ToString(); } set { }  }
#else
      public string fid = string.Empty;
#endif

#if Servolux
      public string packName;
      public string thermalState;
#endif

#if Prodo  || Halygov
      public string unit = "";
#endif

#if Kolpakov
      public string ido = "";
#endif

      [DataField("qtyInPack")]
      public double inPack = 0; // кол-во в упаковке
      
      [DataField("cost.cost")]
      public double[] cost = null;

      public override string ToString() { return name; }

      // это поле отоброжает цвет на КПК (разный порядок RGB & BGR)
      public int color = 0;

      // это св-во для отображения цвета в .Net
      public Color Color
      {
         get
         {
            int r = color & 0xFF;
            int g = (color & 0xFF00) >> 8;
            int b = (color & 0xFF0000) >> 16;
            return Color.FromArgb(r, g, b);
         }

         set
         {
            // меняем местаи r & b
            int clr = value.ToArgb() & 0xFFFFFF;
            color = (((clr & 0xFF0000) >> 16) | (clr & 0xFF00) | ((clr & 0xFF) << 16));
         }
      }

      public override bool Equals(object obj)
      {
         if (obj is Price)
            return obj == null ? false : id.Equals(((Price)obj).id);
         else
            return false;
      }

      public override int GetHashCode()
      {
         return base.GetHashCode();
      }

      public string Name { 
         get 
         {
            StringBuilder result = new StringBuilder();
#if SHOW_PRICE_ID
            result.Append(id);
            result.Append("; ");
#endif
            result.Append(name);

            return result.ToString();
         } 
      }

      public static Price GetEmpty(string id) { return EmptyPrice.Get(id); }
   }

   public class EmptyPrice : Price
   {
      static Dictionary<string, Price> used = new Dictionary<string,Price>();

      public static Price Get(string id)
      {
         if (!used.ContainsKey(id))
            used[id] = new EmptyPrice(id);
         return used[id];
      }

      EmptyPrice(String id)
      {
         this.name = "Товар с кодом <" + id  + ">";
         this.id = id;
      }
   }

   public class EmptyOrg : Org
   {
      static Dictionary<string, Org> used = new Dictionary<string,Org>();

      public static Org Get(string id)
      {
         if (!used.ContainsKey(id))
            used[id] = new EmptyOrg(id);
         return used[id];
      }

      EmptyOrg(String id)
      {
         this.name = "Контрагент с кодом <" + id  + ">";
         this.id = id;
      }
   }

   public class OrgAddress : DataObject
   {
      public string name = "";
      public string id = "";
   }

   public class SimpleObject : GRSoft.Network.DataObject
   {
      [KeyField]
      public string id = string.Empty;
      public string name = string.Empty;

      public string Name { get { return name; } }

      public override string ToString() { return Name; }
   }

   public class Slsnet : SimpleObject
   {
      public static readonly string OBJECT_NAME = "Slsnet";
   }

   public class City : SimpleObject
   {
      public static readonly string OBJECT_NAME = "City";
   }

   public partial class Org : DataObject, IComparable<Org>
   {
      public static string OBJECT_NAME = "Org";
      public static string COMMON_OBJECT_NAME = "CommonOrgs";

      public static Org Empty
      {
         get { return emptyOrg; }
      }

      static Org emptyOrg = new Org();

      [KeyField]
      public string id = "";
      public string name = "";

#if OptimaV
      [DataField("postadr")]
#endif
      public string address = "";

      public int costype = 0;


      [Reference("Agents", "userid")]
      public Agent agent = null;

      [Precision(5)]
      public double longitude = 0;

      [Precision(5)]
      public double latitude = 0;

      public int type = 0;

      public string legalAddress = "";

      public string sid = string.Empty;
      public string cid = string.Empty;

      [Reference("Slsnet", "sid")]
      public Slsnet slsnet = null;

      public string Slsnet { get { return slsnet != null ? slsnet.name : sid; } }
      public string City { get { return cid; } }

#if Ishim
      public double balance = 0;
      public double Balance { get { return balance; } }
#endif

      // это поле отоброжает цвет на КПК (разный порядок RGB & BGR)
      public int color = 0;

      public virtual string Name 
      { 
          get 
          {
             PropertyInfo newName = GetType().GetProperty("NameNew");

             if (newName != null)
                return newName.GetValue(this, null).ToString();

             if (name.Length == 0)
                return "";

              string result =  Config.GetConfig().isFullOrgName ? 
                  String.Format("{0} ({1})", name, Address) 
                  : name;

              result = result.Replace('\n', ' ');

              return result;
          } 
      }

      public string Address
      {
         get
         {
            PropertyInfo newName = GetType().GetProperty("AddressNew");

            if (newName != null)
               return newName.GetValue(this, null).ToString();

            return address == null ? "" : address;
         }

         set { address = value; }
      }

      public override string ToString() { return Name; }

      public override bool Equals(object cmp)
      {
         Org org = cmp as Org;
         bool cmpi = (org != null && id.Equals(org.id));
         return (Config.GetConfig().isFullOrgName && cmpi) ? 
            Address.Equals(org.Address) : 
            cmpi;
      }

      //Чтобы убрать варнинг
      public override int GetHashCode()
      {
         return base.GetHashCode();
      }

      // это св-во для отображения цвета в .Net
      public Color Color
      { 
         get 
         {
            int r = color & 0xFF;
            int g = (color & 0xFF00) >> 8;
            int b = (color & 0xFF0000) >> 16;
            return Color.FromArgb(r, g, b);
         }

         set
         {
            // меняем местаи r & b
            int clr = value.ToArgb() & 0xFFFFFF;
            color = (((clr & 0xFF0000) >> 16) | (clr & 0xFF00) | ((clr & 0xFF) << 16));
         }
      }

#if Agama
      public class UnitItem : DataObject
      {
         public int id;
         public String name = "";
      }

      [ItemType(typeof(UnitItem))]
      public List<UnitItem> units = null;
#endif

#if Tyapkin
      [ItemType(typeof(OrgMatrixName))]
      public List<OrgMatrixName> matrixName = null;
#endif

#if Michailova_O
      public class OrgMatrix : DataObject
      {
         public static string OBJECT_NAME = "OrgMatrix";
         public string name = "";
         public string id = "";
         public string userid = "";
      }
      [ItemType(typeof(OrgMatrix))]
      public List<OrgMatrix> matrix = null;
#endif

#if Servolux
      public string ido;
#endif

#if WallStreet 
      public string category = "";
#endif

#if Prodo || Halygov
      [DataField("owner")]
      public string ownerData = "";
#endif

#if DELIVERY_ADDRESS
      [ItemType(typeof(OrgAddress))]
      public List<OrgAddress> orgAddress = new List<OrgAddress>();

      public string GetAddress(string id)
      {
         foreach (OrgAddress adr in orgAddress)
            if (adr.id == id)
               return adr.name;

         return Address;
      }
#endif

      #region Члены IComparable<Org>

      public int CompareTo(Org other)
      {
         return name.CompareTo(other.name);
      }

      #endregion

      public static Org GetEmpty(string id) { return EmptyOrg.Get(id); }
   }

#if DELIVERY_ADDRESS
   class OrgPoint : Org
   {
      public string addrId;
      public OrgPoint(Org org, string addrId)
      {
         this.addrId = addrId;
         orgAddress = org.orgAddress;
         this.address = GetAddress(addrId);

         this.name = org.name;
         this.type = org.type;
         this.latitude = org.latitude;
         this.longitude = org.longitude;
         this.agent = org.agent;
         this.color = org.color;
         this.id = org.id;
      }
   }
#endif


   public class PotenzialOrg : Org
   {
      new static public readonly string OBJECT_NAME = "PotenzialOrg";

      public string userid;
      [Reference("Region", "region", typeof(Region))]
      public Region region;
   }

   public class PODel : DataObject
   {
      public static readonly string OBJECT_NAME = "PODel";

      public string id;
      public string userid;
      public int flags;
   }

   public class OrderItem : DataObject
   {
      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;
      public string id = "";

      public int flags = 0;

      [Precision(3)]
      public double qty = 0;
      public double cost = 0;
      public double sum = 0;

      public double offTakeDiff = 0;

      public int pack = 0;
      public double taxSum = 0;

#if Kovalchuk || ASK
      [Precision(3)]
      public double qty2 = 0;
      public string remark = string.Empty;
#endif

#if BPZVostok || ADKPlus
      public string unitId = "";
#endif

#if Kolpakov
      public string ido;
#endif

      //Свойства для отображение в гриде
      //Количество
      public double Qty { get { return qty; } }
      //Цена
      public double Cost { get { return (cost == 0) ? sum / qty : cost; } }
      public string SCost { get { return Cost.ToString("C", Config.GetCultureInfo()); } }
      public double Sum { get { return (sum == 0) ? cost * qty : sum; } }

      //Наименование
      public string Item { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }
   
#if Prodo || Halygov
      public string Unit { get { return (item == null) ? "" : item.unit; } }
#endif
      public double Weight { get { return item == null ? 0 : item.weight * qty; } }

      public string ID { get { return id; } }
   }

   public class BaseDocument : DataObject
   {
      public DateTime date = DateTime.Now;
      public DateTime created = DateTime.Now;

      [Precision(5)]
      public double latitude = 0;
      [Precision(5)]
      public double longitude = 0;
      public DateTime sended = DateTime.MinValue;

      [Reference("Agents", "userid")]
      public Agent agent = null;
      public string userid = string.Empty;

      [Reference("Org,PotenzialOrg,CommonOrg,CommonOrgs", "id", typeof(Org))]
      public Org org = null;

      public string id = String.Empty;

      public int timeZone = 0;
      public int serverTimeZone = 0;

      public string remark = "";

      public virtual double Sum()
      {
         return 0;
      }

      internal virtual int Qty
      {
         get { return 0; }
      }

      internal virtual Org Org { get { return org; } }

      public DateTime Date
      {
         get
         {
            return date;
         }
      }

      public DateTime Created
      {
         get
         {
#if USE_TIMEZONE
            TimeSpan ts = TimeZone.CurrentTimeZone.GetUtcOffset(DateTime.Now);
            return created.AddMinutes(timeZone).Add(ts);
#else
            return created;
#endif
         }
      }

      public DateTime Sended
      {
         get
         {
#if USE_TIMEZONE
            TimeSpan ts = TimeZone.CurrentTimeZone.GetUtcOffset(DateTime.Now);
            return sended.AddMinutes(serverTimeZone).Add(ts);
#else
            return sended;
#endif
         }
      }

      public string AgentName { get { return agent == null ? userid : agent.Name; } }
      public string OrgName { get { return Org == null ? id : Org.name; } }

      public virtual string Remark { get { return remark; } }
   }

   public class DocumentInfo
   {
      IDataSet dataSet;
      ObjType.TObjType docType;

      public DocumentInfo(IDataSet dataSet, ObjType.TObjType docType)
      {
         this.dataSet = dataSet;
         this.docType = docType;
      }

      public IDataSet DataSet { get { return dataSet; } }
      public ObjType.TObjType Type { get { return docType; } }
   }

   public class Order : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Order";


      [DataField("params")]
      public int _params = 0;
      public bool OutOfPlan { get { return ((_params & 0x40000) != 0); } }

         
      [ItemType(typeof(OrderItem))]
      public List<OrderItem> items = new List<OrderItem>();

#if Quad
      public double debet = 0.0;
#endif

#if Agama
      public int unitCode = 0;
#endif

#if Servolux
      public static readonly string ORDER_SAVE = "OrderCommit";
      public String firmCode;
      public DateTime modify;
      public DateTime dlvDate;

      public bool HaveItem(string id)
      {
         foreach (OrderItem oi in items)
            if (oi.id == id)
               return true;

         return false;
      }
#endif

#if ClearLine
      public string stpRmt = string.Empty;

      public override string Remark
      {
         get
         {
            return string.Format("{0} {1}", stpRmt, remark).Trim();
         }
      }
#endif

#if Prodo
      public int loadedFromKIS = 0;
#endif

#if MobileAssistant
      [Reference("Dogovor", "dgv")]
      public NapoleonOrderDogorvor dogovor = null;

      internal override Org Org
      {
         get
         {
            if ((org == null || org.id.Length == 0) && dogovor != null)
            {
               org = Org.GetEmpty(dogovor.id);
               org.name = dogovor.name;
            }
            return org;
         }
      }
#endif

#if FOCUSED_GROUP
      public class FocusFolder : DataObject
      {
         public string fid = "";
         public string remark = "";
      }

      [ItemType(typeof(FocusFolder))]
      public List<FocusFolder> focusedFolders = null;
#endif

#if FOCUSED_ITEMS
      public class FocusItem : DataObject
      {
         public string id = "";
         public string remark = "";
      }

      [ItemType(typeof(FocusItem))]
      public List<FocusItem> focusedItems = null;
#endif

      // поля для дублирования
      public string ctype = "";
      public string firma = "";
      public int delay;
      public int cash;

      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }

#if DELIVERY_ADDRESS
      public string adrCode;
      public string OrgAddr { get { return org == null ? string.Empty : org.GetAddress(adrCode); } }
#else
      public string OrgAddr { get { return org == null ? string.Empty : org.Address; } }
#endif

      public double DSum { get { return Sum(); } }
      public string SSum { get { return DSum.ToString("C", Config.GetCultureInfo()); } }

      override internal int Qty
      {
         get
         {
            int q = 0;
            foreach (OrderItem item in items)
               q += (int)(item.Qty + 0.5);
            return q;
         }
      }

      override public double Sum()
      {
         double sum = 0;
         foreach (OrderItem item in items)
            sum += item.Sum;

         return sum;
      }

      public double Weight
      {
         get
         {
            double res = 0;
            foreach (OrderItem item in items)
            {
               if (item.item != null)
                  res += (item.item.weight * item.qty);
            }
            return res;
         }
      }
   }

   public partial class OrgFolderItem : DataObject
   {
      [Reference("Org,PotenzialOrg,CommonOrgs", "name", typeof(Org))]
      public Org org = null;
      public string name = "";
      public int pos;

      public override string ToString()
      {
         return (org != null) ? org.ToString() : "";
      }

      public override bool Equals(object obj)
      {
         OrgFolderItem ofi = obj as OrgFolderItem;
         return (ofi !=null && name.Equals(ofi.name));
      }

      //Чтобы убрать варнинг
      public override int GetHashCode()
      {
         return base.GetHashCode();
      }
   }

   public class OrgFolder : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgFolder";
      
      [KeyField]
      public string name = "";

      [KeyField]
      public string userid = "";

      public int id = -1;

      [Reference("Agents", "userid")]
      public Agent agent = null;

      [ItemType(typeof(OrgFolderItem))]
      public List<OrgFolderItem> items = new List<OrgFolderItem>();

      public string code = string.Empty;
      public int type = -1;
      public int last = 0;
   }

   public class Division : DataObject
   {
      public static readonly string OBJECT_NAME = "Division";

      public class DivisionAgent : DataObject
      {
         [Reference("Agents", "id")]
         public Agent agent = null;

         public string id = "";

         public string AgentName
         {
            get { return (agent == null) ? "?" : agent.name; }
         }

         public override string ToString()
         {
            return AgentName;
         }
      }

      [KeyField]
      public int id = 0;

      public string name = "";
      public string description = "";

      [Reference("Agents", "cheif")]
      public Agent cheif = null;

      [ItemType(typeof(DivisionAgent))]
      public List<DivisionAgent> agents = new List<DivisionAgent>();

      [ItemType(typeof(ManagerFolder))]
      public List<ManagerFolder> folder = new List<ManagerFolder>();

      public int parent = 0;

      protected Division parentDivision = null;
      protected List<Division> childs = new List<Division>();

      public override string ToString()
      {
         return name;
      }

      public string DivisionName
      {
         get { return name; }
      }

      public Division Parent { get { return parentDivision;  } }

      public List<Division> Childs { get { return childs;  } }

      public void SetReferences(Dictionary<int, Division> divisions)
      {
         if (divisions.ContainsKey(parent))
         {
            parentDivision = divisions[parent];
            parentDivision.childs.Add(this);
         }
      }

      public bool HaveAgent(Agent a)
      {
         return HaveAgent(a.id);
      }

      public bool HaveAgent(String id)
      {
         foreach (DivisionAgent da in agents)
            if (da.agent != null && da.agent.id == id)
               return true;

         return false;
      }

      public void Remove(Division child)
      {
         foreach (Division ch in childs)
         {
            if (ch == child)
            {
               childs.Remove(ch);
               break;
            }
         }
      }

      internal void CheckAgents()
      {
         List<DivisionAgent> remove = new List<DivisionAgent>();
         foreach (DivisionAgent da in agents)
            if (da.agent == null)
               remove.Add(da);

         foreach (DivisionAgent da in remove)
            agents.Remove(da);
      }

      public bool Remove(Dictionary<Agent, bool> agentSet)
      {
         bool ret = false;
         int i = 0;
         for (; i < agents.Count; i++ )
         {
            if (agents[i].agent != null &&
                agentSet.ContainsKey(agents[i].agent))
            {
               ret = true;
               agents.RemoveAt(i);
            }
         }

         return ret;
      }

      /// <summary>
      /// Получить список агентов вместе с агентами childs
      /// </summary>
      /// <returns>List<DivisionAgent></returns>
      public List<DivisionAgent> GetAllAgents()
      {
         List<DivisionAgent> result = new List<DivisionAgent>();

         result.AddRange(agents);
         result.AddRange(FetchChildAgents(childs));

         return result;
      }

      private List<DivisionAgent> FetchChildAgents(List<Division> childs)
      {
         List<DivisionAgent> result = new List<DivisionAgent>();

         if (childs != null)
         {
            foreach (Division child in childs)
            {
               result.AddRange(child.agents);
               result.AddRange(FetchChildAgents(child.childs));
            }
         }

         return result;
      }

      public override bool Equals(object obj)
      {
         if (obj != null && obj is Division)
            return id == ((Division)obj).id;
         return false;
      }

      public override int GetHashCode()
      {
         return base.GetHashCode();
      }
   }

   public class GPSPos : DataObject
   {
      public static readonly string OBJECT_NAME = "GPSPos";
      [Reference("Agents", "userid")]
      public Agent agent = null;

      [KeyField]
      public DateTime date = DateTime.Now;
      [Precision(5)]
      public double longitude = 0;
      [Precision(5)]
      public double latitude = 0;
      public double speed = 0;
      public int isGSM = 0;
   }

   public class Agent : DataObject, IComparable<Agent>
   {
      public static string OBJECT_NAME = "Agents";

      [KeyField]
      public string id = "";
      public string name = "";
      public string login = "";
      public string password = "";

#if CHECK_LOGIN_PROGID
      public string progid = "";
#endif

#if Volnenko
      public int sklad = 0;
#endif

//#if Servolux
//      public string Name { get { return "(" + id + ") " + name; } }
//#else
      public string Name { get { return name; } }
//#endif

#if Prodo
      public string division = "";
#endif

      public override string ToString()
      {
         return String.Format("({0}){1}", id, name);
      }

      public bool Equals(Agent agent)
      {
         return this.id == agent.id;
      }

      #region IComparable<Agent> Members

      public int CompareTo(Agent other)
      {
         return name.CompareTo(other.name);
      }

      #endregion
   }

   public class UserInfo : DataObject
   {
      public static readonly string OBJECT_NAME = "UserInfo";

      [KeyField]
      public string userid = string.Empty;
      public string phone = string.Empty;
   }

   public class Agents : DataSet<string, Agent>
   {
      public static readonly string OBJECT_NAME = "Agents";

      public Agents()
         : base(OBJECT_NAME)
      {
      }

      public Agents(bool addToDataModule)
         : base(OBJECT_NAME, addToDataModule)
      {
      }

      public static Agents GetDataSet()
      {
         if (DataModule.Get(OBJECT_NAME) == null)
         {
            return new Agents();
         }

         return (Agents)DataModule.Get(OBJECT_NAME);
      }

      public Agent Find(string login, string password)
      {
         Agent a = null;
         foreach(Agent check in Data)
         {
            if (check.login == login && check.password == password)
            {
               a = check;
               break;
            }
         }
         return a;
      }
   }

   public partial class VisitInfo : BaseDocument
   {
      public static readonly string V_OBJECT_NAME = "VisitInfo";

      [Precision(2)]
      public double rating = 0;

#if Agama
      public int unitCode = 0;
#endif

      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
      public string OrgAddr { get { return org == null ? string.Empty : org.Address; } }

#if VISIT_CAUSE   
      public string cause = "";
#endif
   }

   public class ImageUtil : VisitInfo
   {
      public static Image createImage(byte[] data)
      {

         
         Image result = null;

         if (data != null)
         {
            Stream s = new MemoryStream(data);
            using (s)
               result = new Bitmap(s);
         }

         return result;
      }
   }

   public partial class Visit : VisitInfo
   {
      public static readonly string OBJECT_NAME = "Visit";

      [Reference("ContractDef", "def", typeof(ContractDef))]
      public ContractDef contract = null;

      public class VisitItem : DataObject
      {
         public byte[] id = null;
         public int rating = 0;
         public string caption = string.Empty;
      }

      [ItemType(typeof(VisitItem))]
      public List<VisitItem> items = null;

      public void RefreshRating()
      {
         int count = 0;

         rating = 0;
         foreach (Visit.VisitItem vi in items)
            if (vi.rating > 0)
            {
               rating += vi.rating;
               count++;
            }

         if (count > 0)
            rating /= count;
      }
   }


   public partial class OrgRemnantsItem : DataObject
   {
      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;
      [Precision(3)]
      public double qty = 0;
      public string id = string.Empty;

      //Свойства для отображение в гриде
      //Количество
      public double Qty { get { return qty; } }
      //Наименование
      public string Item { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }
      public double Weight { get { return item == null ? 0 : item.weight * qty; } }
   }

   public class OrgRemnants : BaseDocument
   {
      public static readonly string OBJECT_NAME = "OrgRemnants";

      [ItemType(typeof(OrgRemnantsItem))]
      public List<OrgRemnantsItem> items = null;

      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
      public string OrgAddr { get { return org == null ? string.Empty : org.Address; } }
   }

   public class ManagerFolder : DataObject
   {
      public static readonly string OBJECT_NAME = "ManagerFolder";

      public ManagerFolder() { }

      public ManagerFolder(ManagerFolder folder)
      {
         this.id = folder.id;
         this.level = folder.level;
         this.name = folder.name;
         this.userid = folder.userid;

         foreach (KeyValuePair<string, object> f in folder.srvFields)
            this.srvFields.Add(f.Key, f.Value);
      }

      [KeyField]
      public string id = string.Empty;
      public int level = 0;
      public string name = string.Empty;
      public string userid = string.Empty;
   }

   public class MatrixItem : DataObject
   {
      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price price = null;
      public string id = String.Empty;
   }

   public partial class Matrix : DataObject
   {
      static public string OBJECT_NAME = "CommonMatrix";

      [KeyField]
      public string name = String.Empty;
#if Michailova_O
      public int common;
#endif

      [ItemType(typeof(MatrixItem))]
      public List<MatrixItem> items = null;
   }

   public class AgentMatrix : DataObject
   {
      static public string OBJECT_NAME = "AgentMatrix";
      public string userid = String.Empty;
      public string name = String.Empty;
   }

   public class Message : DataObject
   {
      static public string OBJECT_NAME = "Message";
      public DateTime date = DateTime.Now;
      public string message = string.Empty;

      public Message()
      {
         MessageObject mo = new MessageObject(""); // чтобы добавить формат в список форматов
      }
   }

   public class AgentTask : DataObject
   {
      static public string OBJECT_NAME = "AgentTask";

      public enum Flags { Done = 1, Exported = 2, SuperTask = 4 };

      [KeyField] public DateTime date = DateTime.MinValue; // дата создания (или связи со сценарием)
      public DateTime execDate = DateTime.MinValue; // дата выполнения задачи
      public DateTime appointDate = DateTime.MinValue; // дата назначения

      [Reference("Agents", "userid")] public Agent agent = null;
      [KeyField] public string userid = "";
      [Reference("Org", "id", typeof(Org))] public Org org = null;
      [KeyField] public string id = "";

      [KeyField] public string category = "";
      public string text = "";

      public int flags = 0;

      internal string AgentID { get { return agent == null ? string.Empty : agent.id; } }
      internal string OrgName { get { return org == null ? string.Empty : org.name; } }
      internal string OrgAddr { get { return org == null ? string.Empty : org.Address; } }
      internal bool IsSVTask { get { return ((flags & (int)Flags.SuperTask) != 0); } }

      public bool IsComplete { get { return ((flags & (int)Flags.Done) != 0); } }
      public string Category { get { return category; } }
      public string ADate { get { return appointDate.ToShortDateString(); } }
      internal string Date { get { return date.ToShortDateString(); } }
      public string Text { get { return text; } }
   }

   public class TaskCategory : DataObject
   {
      static public string OBJECT_NAME = "TaskCategory";
      [KeyField]
      public string name;
   }

   public class SVTask : AgentTask
   {
      static public new string OBJECT_NAME = "SVTask";
   }

   public class AgentScript : DataObject
   {
      public static readonly string OBJECT_NAME = "AgentScript";

      public string userid = string.Empty;
      public int script = -1;
   }

   public class ScriptDefItem : DataObject
   {
      public String curType = "";
      public String name = "";
      public int nextDoc;
      public int condition;
      public String condParam = "";

      public String Name
      {
         get { return (name.Length > 0) ? name : ScriptDocuments.GetName(curType); }
      }
   }

   public partial class ScriptDef : DataObject
   {
      static public string OBJECT_NAME = "ScriptDef";

      [KeyField]
      public Int32 id = -1;
      public String userid = "";
      public String name;
      public String cdefid = "";

      [ItemType(typeof(ScriptDefItem))]
      public List<ScriptDefItem> items;

      public string Name
      { 
         get 
         {
            if (name.Length == 0 && items.Count > 0)
               return items[0].Name;

            return name; 
         }
      }
      
      public string DocsStr
      {
         get
         {
            StringBuilder sb = new StringBuilder();

            if (items != null)
               foreach (ScriptDefItem item in items)
                  sb.Append(item.Name).Append(", ");

            if (sb.Length > 2)
               sb.Remove(sb.Length - 2, 2);

            return sb.ToString();
         }
      }

   }

   public class ScriptDocItem : DataObject
   {
      public static readonly int DOC_INITED = 1;
      public static readonly int DOC_NONE = 0;

      public DateTime date = DateTime.Now;
      public string type = string.Empty;
      public int state;

      public bool Inited { get { return state == DOC_INITED; } }

      DataObject doc = null;
      public DataObject Document
      {
         get
         {
            if (!Inited || doc != null)
               return doc;

            IDataSet ds = DataModule.Get(type);
            if (ds != null)
            {
               FieldInfo f = null;
               foreach (DataObject d in ds.Data)
               {
                  if (f == null && (f = GetDateField(d)) == null)
                     break;

                  DateTime cd = (DateTime)f.GetValue(d);
                  if (cd != null && cd.CompareTo(date) == 0)
                  {
                     doc = d;
                     break;
                  }
               }
            }

            return doc;
         }
      }

      private FieldInfo GetDateField(DataObject d)
      {
         FieldInfo f = d.GetType().GetField("created");
         if (f != null)
            return f;

         return d.GetType().GetField("date");
      }
   }

   public class ScriptDoc : BaseDocument
   {
      static public string OBJECT_NAME = "ScriptDoc";

      public int scriptId = 0;

      public double sum = 0;

      [ItemType(typeof(ScriptDocItem))]
      public List<ScriptDocItem> items;

      public DataObject GetDocument(string type)
      {
         foreach (ScriptDocItem i in items)
            if (i.Inited && i.type.Equals(type))
               return i.Document;

         return null;
      }

      public List<DataObject> GetDocumentsOfType(string type)
      {
          List<DataObject> result = new List<DataObject>();
          foreach (ScriptDocItem i in items)
              if (i.Inited && i.type.Equals(type))
                  result.Add(i.Document);
          return result;
      }

      public DateTime Start { get { return created; } }
      public DateTime End
      {
         get
         {
            DateTime dt = created;
            foreach (ScriptDocItem i in items)
               if (i.Inited)
                  dt = i.date;

            return dt;
         }
      }
   }

#if Happyland
   public class ScriptDocItem : DataObject
   {
      public DateTime date = DateTime.Now;
      public string type = string.Empty;
   }

   public class ScriptDoc : DataObject
   {
      static public string OBJECT_NAME = "ScriptDoc";

      [Reference("Org,PotenzialOrg,CommonOrgs", "id", typeof(Org))] public Org org = null;
      public string id = string.Empty;

      public DateTime date = DateTime.Now;
      public DateTime dateEnd = DateTime.Now;
      public DateTime sended = DateTime.Now;

      public enum Flags
      {
         Exported = 1, TaskBeforeDone = 2, TaskAfterDone = 4,
         OrderOutOfPlan = 8, IncassOutOfPlan = 0x10, PhotoBefore = 0x20, PhotoAfter = 0x40, Interrupted = 0x80
      };
      public int flags = 0;

      [Precision(5)] public double longitude = 0;
      [Precision(5)] public double latitude = 0;

      [Reference("Agents", "userid")] public Agent agent = null;
      public string userid = string.Empty;

      public string remark = "";

      public double sum = 0;

      [ItemType(typeof(ScriptDocItem))]
      public List<ScriptDocItem> items;

      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
      public string OrgName { get { return org == null ? string.Empty : org.name; } }
      public string OrgAddr { get { return org == null ? string.Empty : org.address; } }

      public double Sum { get { return sum; } }

      public bool OrderOutOfPlan { get { return ((flags & (int)Flags.OrderOutOfPlan) != 0); } }

      public double OrderSum
      {
         get
         {
            Order doc = Order;
            return (doc == null) ? 0 : doc.Sum();
         }
      }

      public double IncassSum
      {
         get
         {
            Incass doc = Incass;
            return (doc == null) ? 0 : doc.sum;
         }
      }

      public DateTime RefDocDate(string docType)
      {
         foreach (ScriptDocItem item in items)
         {
            if (item.type == docType)
               return item.date;
         }
         return DateTime.MinValue;
      }

      internal Visit[] Visits
      {
         get
         {
            Visit[] v = new Visit[2];

            DataSet<int, Visit> docs = DataModule.Get("Visit") as DataSet<int, Visit>;
            if (docs != null)
            {
               foreach (Visit d in docs.Data)
               {
                  if (d.AgentID == AgentID)
                  {
                     if (d.date == date) v[0] = d;
                     if (d.date == dateEnd) v[1] = d;
                  }
               }
            }

            return v;
         }
      }

      internal Incass Incass
      {
         get
         {
            DateTime rd = RefDocDate("Incass");
            DataSet<int, Incass> docs = DataModule.Get(Incass.OBJECT_NAME) as DataSet<int, Incass>;
            if (rd != DateTime.MinValue && docs != null)
            {
               foreach (Incass d in docs.Data)
               {
                  if (d.date == rd && d.AgentID == AgentID)
                     return d;
               }
            }

            return null;
         }
      }

      internal OrgRemnants OrgRemnants
      {
         get
         {
            DataSet<int, OrgRemnants> docs = DataModule.Get("OrgRemnants") as DataSet<int, OrgRemnants>;
            if (docs != null)
            {
               foreach (OrgRemnants d in docs.Data)
               {
                  if (d.AgentID == AgentID && d.id == id && d.date.Day == date.Day && d.date.Month == date.Month && d.date.Year == date.Year )
                     return d;
               }
            }

            return null;
         }
      }

      internal Order Order
      {
         get
         {
            DateTime rd = RefDocDate("Order");
            DataSet<int, Order> docs = DataModule.Get("Order") as DataSet<int, Order>;
            if( rd != DateTime.MinValue && docs != null)
            {
               foreach(Order d in docs.Data)
               {
                  if (d.created == rd && d.AgentID == AgentID)
                     return d;
               }
            }

            return null;
         }
      }

      int CmpTask (AgentTask x, AgentTask y)
      {
         return x.appointDate.CompareTo(y.appointDate);
      }

      public List<AgentTask> Task
      {
         get
         {
            List<AgentTask> newTask = new List<AgentTask>();
            List<AgentTask> doneTask = new List<AgentTask>();
            List<AgentTask> notExecTask = new List<AgentTask>();

            DataSet<int, SVTask> sdocs = DataModule.Get(SVTask.OBJECT_NAME) as DataSet<int, SVTask>;
            if (sdocs != null)
            {
               foreach (AgentTask d in sdocs.Data)
               {
                  if (d.id != id) continue;
                  d.flags |= (int)AgentTask.Flags.SuperTask;
                  if (d.date == date && d.AgentID == AgentID) newTask.Add(d);
                  else if (d.execDate == date && d.AgentID == AgentID && d.IsComplete) doneTask.Add(d);
                  else if (!d.IsComplete) notExecTask.Add(d);
               }
            }

            DataSet<int, AgentTask> docs = DataModule.Get(AgentTask.OBJECT_NAME) as DataSet<int, AgentTask>;
            if (docs != null)
            {
               foreach (AgentTask d in docs.Data)
               {
                  if (d.id != id) continue;
                  if (d.date == date && d.AgentID == AgentID) newTask.Add(d);
                  else if (d.execDate == date && d.AgentID == AgentID && d.IsComplete) doneTask.Add(d);
                  else if (!d.IsComplete /*&& d.appointDate.CompareTo(date) <= 0*/) notExecTask.Add(d);
               }
            }

            List<AgentTask> task = new List<AgentTask>(newTask);
            doneTask.Sort(CmpTask);
            notExecTask.Sort(CmpTask);
            task.AddRange(doneTask);
            task.AddRange(notExecTask);
            return task;
         }
      }

      public bool IsScriptComplete
      {
         get
         {
            const int MASK = (int)Flags.TaskBeforeDone | (int)Flags.TaskAfterDone | 
               (int)Flags.PhotoBefore | (int)Flags.PhotoAfter;

            return (flags & MASK) == MASK;
         }
      }

      public bool IsIncassOutOfPlan
      {
         get
         {
            return (flags & (int)Flags.IncassOutOfPlan) == (int)Flags.IncassOutOfPlan;
         }
      }

      public bool IsOrderOutOfPlan
      {
         get
         { 
            return (flags & (int)Flags.OrderOutOfPlan) == (int)Flags.OrderOutOfPlan;
         }
      }
   }
#endif

   public class Plan : DataObject
   {
      static public string OBJECT_NAME = "Plan";

      public DateTime date = DateTime.MinValue; // дата создания
      public String name = "";
      public double plan = 0;
      public double fact = 0;
      public string userid = string.Empty;

      public DateTime from = DateTime.MinValue; // с 
      public DateTime till = DateTime.MinValue; // по

      public string text = string.Empty;

      [Reference("Agents", "userid")]
      public Agent agent = null;

      public Agent Agent { get { return agent; } }
      public string From { get { return from.ToShortDateString(); } }
      public string Till { get { return till.ToShortDateString(); } }
      public string Name { get { return name; } }
      public string Text { get { return text; } }
      public double Qty { get { return plan; } }
   }

   public class CommonConfig : DataObject
   {
      public static string OBJECT_NAME = "ServerConfig";

      public string userid = string.Empty;
      public string key    = string.Empty;
      public string value  = string.Empty;
   }

   public class OrderAddConfig : DataObject
   {
      public static string OBJECT_NAME = "CommonConfig";

      [KeyField]
      public string key    = string.Empty;
      public string value  = string.Empty;
   }

   class DsCommonOrgs : DataSet<string, Org>
   {
      private static readonly string DATA_SET_NAME = "Org";
      
      private static DsCommonOrgs instance = null;

      private DsCommonOrgs()
         : base(DATA_SET_NAME)
      {
         OnAddingItem += new DataSetAddingItem(AddingItem);
      }

      private DsCommonOrgs(bool addToDataModule)
         : base(DATA_SET_NAME, addToDataModule)
      {
      }

      private void SetFilter()
      {
         if (DataModule.Get(Agent.OBJECT_NAME).Data.Count == 0)
         {
            DataModule.RefreshDataSet(DataModule.Get(Agent.OBJECT_NAME), Config.GetConfig().GetConnection(),  false, null).Join();
         }

         if (DataModule.Get(Agent.OBJECT_NAME).Data.Count == 0)
         {
            throw new Exception("В базе нет списка агентов!");
         }

         Filter = DataUtils.MakeFilterFromAgents(null, Agents.GetDataSet());
      }

      public static DsCommonOrgs GetCommonOrgs()
      {
         if (instance == null)
         {
            instance = new DsCommonOrgs();
            instance.SetFilter();
         }

         return instance;
      }

      //При добавлении в набор данных раскидваем по UserDataSetam
      private void AddingItem(object item)
      {
         Org org = item as Org;

         if (org == null)
            throw new EIncompatibilityObject();

         Agent agent = org.agent;

         if (agent == null && agent.id == null && agent.id == string.Empty)
            throw new EDataCorrupted();

         DataSet<string, Org> userDataSet = 
            (DataSet<string, Org>)DataModule.GetUserDataSet(agent.id, DATA_SET_NAME, typeof(DataSet<string, Org>));

         if (!userDataSet.ContainsKey(org.id))
         {
            userDataSet.Add(org.id, org);
         }
      }
   }

   /// <summary>
   /// Архив сообщений
   /// </summary>
   public class MessageArchive : DataObject
   {
      static public readonly string OBJECT_NAME = "MessageArchive";

      /// <summary>
      /// ID пользователя
      /// </summary>
      public string userid = string.Empty;

      /// <summary>
      /// Дата - время сообщения
      /// </summary>
      public DateTime date = DateTime.MinValue;

      /// <summary>
      /// Содержание сообощения
      /// </summary>
      public string message = string.Empty;
   }

   public class DayDoc : DataObject
   {
      static public readonly string OBJECT_NAME = "DayDoc";

      public DateTime start = DateTime.MinValue;
      public DateTime end = DateTime.MinValue;

      public int valueStart = 0;
      public int valueEnd = 0;

      [Precision(5)]
      public double longitude = 0;
      [Precision(5)]
      public double latitude = 0;

      [Precision(5)]
      public double lngEnd = 0;
      [Precision(5)]
      public double latEnd = 0;

      public byte[] photoStart = null;
      public byte[] photoEnd = null;
   }

   public class Task : DataObject
   {
      static public readonly string OBJECT_NAME = "Task";

      [Reference("Agent", "userid")] 
      public Agent agent;
      public string userid = "";

      [KeyField]
      public DateTime date = DateTime.MinValue;

      [Reference("Org,PotenzialOrg,CommonOrgs", "id", typeof(Org))]
      public Org org = null;
      public string id = "";

      public string task = "";
      public string doing = "";

      public string StrDate { get { return date.ToShortDateString(); } }
      public string StrOrg { get { return (org != null) ? org.Name : ""; } }
      public string StrTask { get { return task; } }
      public string Do { get { return doing; } }

      public int CompareTo(Task t)
      {
         int v = id.CompareTo(t.id);
         if (v != 0)
            return v;
         v = date.CompareTo(t.date);
         if (v != 0)
            return v;
         return task.CompareTo(t.task);
      }
   }

   public class PKO : BaseDocument
   {
      static public readonly string OBJECT_NAME = "Pko";

      public string number = "";
      public double sum = 0;
   
      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
      public string OrgAddr { get { return org == null ? string.Empty : org.Address; } }

      public override double Sum() { return sum; }
   }

   public class Incass : BaseDocument
   {
      static public readonly string OBJECT_NAME = "Incass";

      public double sum = 0;

      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
      public string OrgAddr { get { return org == null ? string.Empty : org.Address; } }

      public override double Sum() { return sum; }
   }

   public class AgentOrgTaskSend : DataObject
   {
      static public readonly string OBJECT_NAME = "AgentOrgTask";

      public DateTime created;
      public string id;
      public string task;
      public string done;
      public string userid;

   }

   public class AgentOrgTask : AgentOrgTaskSend
   {
      public DateTime dodate;

      public bool TaskDone { get { return done != null && done.Length > 0; } }
   }

   public partial class ReturnItem : DataObject
   {
      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;

      public String id = "";

      [Precision(3)]
      public double qty = 0;

      public double offTakeDiff = 0;
      public double cost = 0;

      public int flags;
      public DateTime expdate = DateTime.MinValue;
      public string causeid = string.Empty;

#if PoultryNSib || MeatAlliance || Antonov
      public string comment = string.Empty;
#endif

      //Свойства для отображение в гриде
      //Количество
      public double Qty { get { return qty; } }

      //Наименование
      public string Item { get { return item != null ? item.Name : "Объект с кодом <" + id + ">"; } }
   }

   public class Returns : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Returns";

      [ItemType(typeof(ReturnItem))]
      public List<ReturnItem> items = null;


      public string cause = "";

      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
      public string OrgAddr { get { return org == null ? string.Empty : org.Address; } }
      public double DSum { get { return Sum(); } }
      public string SSum { get { return DSum.ToString("C", Config.GetCultureInfo()); } }

      internal override int Qty
      {
         get
         {
            int q = 0;
            foreach (ReturnItem item in items)
               q += (int)(item.Qty + 0.5);
            return q;
         }
      }

      public override double Sum()
      {
         double result = 0.0;

         foreach (ReturnItem item in items)
         {
#if USE_COST_IN_RETURNS
            result += item.cost * item.qty;
#else
            if (item.item != null && item.item.cost != null && item.item.cost.Length > 0)
               result += item.item.cost[0] * item.qty;
#endif
         }

         return result;
      }
   }

   public class UserActivity : DataObject
   {
      public static readonly string OBJECT_NAME = "UserActivity";

      [KeyField]
      public string id = string.Empty;
      public DateTime date = DateTime.Now;
      public string version = string.Empty;
   }

   public class Question : DataObject
   {
      public static readonly string OBJECT_NAME = "Question";
      
      public const int USE_PERIOD = 1;
      public const int INWORK = 2;

      [KeyField]
      public string idquest = string.Empty;
      public string name = string.Empty;
      public DateTime from = DateTime.MinValue;
      public DateTime till = DateTime.MinValue;
      public string text = string.Empty;
      public string html = string.Empty;
      public int number = 0;

#if BTL
      public string category = string.Empty;
      public string producer = string.Empty;
#endif

      [DataField("params")]
      public int _params = 0;

      [ItemType(typeof(QuestionItem))]
      public List<QuestionItem> items = null;

      public Question Copy()
      {
         Question result = new Question();
         result.idquest = GenId();
         result.name = "Копия " + name;
         result.from = from;
         result.till = till;
         result.text = text;

         if (items != null)
         {
            result.items = new List<QuestionItem>();

            foreach(QuestionItem i in items)
               result.items.Add(i.Copy());

            result.InvalidateHtml();
         }
   
         return result;
      }

      public string Name { get { return name; } }
      public string From
      {
         get
         {
            return IsUsePeriod()
               ? from.ToShortDateString() : string.Empty;
         }
      }

      public bool IsUsePeriod()
      {
         return (_params & USE_PERIOD) == USE_PERIOD;
      }

      public string Till
      {
         get
         {
            return IsUsePeriod()
               ? till.ToShortDateString() : string.Empty;
         }
      }

      public int Number { get { return number; } }

      public void SetUsePeriod(bool val)
      {
         if (val)
            _params |= USE_PERIOD;
         else
            _params &= ~_params;
      }

      public string Text { get { return text; } }

      public void InvalidateHtml()
      {
         StringBuilder htmlPage = new StringBuilder();
         int[] color = new int[] { 0xffffff, 0xceecf5 };

         htmlPage.Append("<html>");
         htmlPage.Append("<head>");
         htmlPage.Append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n");
         htmlPage.Append("<style type=\"text/css\">\r\n");
         htmlPage.Append("input{width:100%;}\r\n");
         htmlPage.Append("select{width:100%;}\r\n");
         htmlPage.Append("</style>\r\n");
         htmlPage.Append("</head>\r\n");
         htmlPage.Append("%%script%%");
         htmlPage.Append("<body %%onload%%>");
         htmlPage.Append("<div align=\"center\">");
         htmlPage.Append("<br>");
         htmlPage.Append(StringUtil.EscapeQuotes(text));
         htmlPage.Append("</div>");
         htmlPage.Append("<form id=\"").Append(idquest).Append("\">\r\n");
         htmlPage.Append("<table \"width=100%\">\r\n");
         htmlPage.Append("<col width=\"50%\"/>\r\n");
         htmlPage.Append("<col width=\"50%\"/>\r\n");

         int i = 0;
         foreach (QuestionItem item in items)
         {
            htmlPage.Append("<tr bgcolor=\"#")
               .Append(color[i % 2 == 0 ? 0 : 1 ].ToString("x"))
               .Append("\"><td>").Append(StringUtil.EscapeQuotes(item.text))
               .Append("</td><td>").Append(item.ToHtmlControl()).Append("</td></tr>\r\n");
            i++;
         }

         htmlPage.Append("\r\n</table>");
         htmlPage.Append("%%commitbutton%%");
         htmlPage.Append("</form>");
         htmlPage.Append("</body>");
         htmlPage.Append("</html>");

         html = htmlPage.ToString();

         //File.WriteAllText("quest.html", html);
      }

      public override string ToString() { return Name; }
   }

   public class QuestionItem : DataObject
   {
      public const int TEXT = 0;
      public const int NUMBER = 1;
      public const int LIST = 2;
      public const int SET = 3;
      public const int BOOLEAN = 4;
      public const int DATASET = 5;

      public string iditem = string.Empty;
      public string id = string.Empty;
      public string text = string.Empty;
      public int type = 0;
      public int number;
      public int optional = 0;

      [ItemType(typeof(QuestionItemValue))]
      public List<QuestionItemValue> values = null;

      public string Id { get { return id; } }
      public int Number { get {return number; } }
      public string Text { get { return text; } }
      public string TypeStr { get { return TypeToStr(type); } }

      public QuestionItem Copy()
      {
         QuestionItem result = new QuestionItem();

         result.iditem = Question.GenId();
         result.id = id;
         result.text = text;
         result.type = type;
         result.number = number;

         if(values != null){
            result.values = new List<QuestionItemValue>();

            foreach (QuestionItemValue val in values)
               result.values.Add(val.Copy());
         }

         return result;
      }
      
      public static string TypeToStr(int code)
      {
         switch(code)
         {
            case TEXT : return "Текст";
            case NUMBER : return "Число";
            case LIST: return "Список";
            case SET : return "Множество";
            case BOOLEAN : return "Логическое";
            case DATASET : return "Справочник";
            default: return "Тип неопределен";      
         }
      }

      internal string ToHtmlControl()
      {
         int index = 1;
         switch (type)
         {
            case TEXT:
               StringBuilder textText = new StringBuilder();
               textText.Append("<input type=\"text\" ")
                  .Append("id=\"").Append(iditem).Append("_0\" ")
                  .Append("name=\"").Append(id).Append("_0\" ")
                  .Append("value=\"\">");

               return textText.ToString();
            case NUMBER:
               StringBuilder textNumber = new StringBuilder();
               textNumber.Append("<input type=\"number\" ")
                  .Append("onKeyPress=\"return numbersonly(this, event)\" ")
                  .Append("id=\"").Append(iditem).Append("_0\" ")
                  .Append("name=\"").Append(id).Append("_0\" ")
                  .Append("value=\"\">");

               return textNumber.ToString();

            case LIST:
               StringBuilder list = new StringBuilder();

               foreach (QuestionItemValue val in values)
               { 
                  string capt = StringUtil.EscapeQuotes(val.value);
                  list.Append("<input type=\"checkbox\" ")
                     .Append("id=\"").Append(iditem).Append("_").Append(index).Append("\" ")
                     .Append("name=\"").Append(id).Append("_").Append(index).Append("\" ")
                     .Append("value=\"").Append(capt).Append("\">").Append(capt).Append("<br>");
                  index++;
               }
               
               return list.ToString();

            case SET:
               StringBuilder set = new StringBuilder();
               
               foreach (QuestionItemValue val in values)
               {
                  string capt = StringUtil.EscapeQuotes(val.value);
                  set.Append("<input type=\"radio\" ")
                     .Append("id=\"").Append(iditem).Append("_").Append(index).Append("\" ")
                     .Append("name=\"").Append(id).Append("_0\" ")
                     .Append("value=\"").Append(capt).Append("\">").Append(capt).Append("<br>");
                  index++;
               }
               
               return set.ToString();

            case BOOLEAN:
               StringBuilder boolean = new StringBuilder();

               if (values.Count == 2)
               {
                  QuestionItemValue trueVal = values[0];
                  QuestionItemValue falseVal = values[1];
                  
                  boolean.Append("<input type=\"radio\" ")
                     .Append("id=\"").Append(iditem).Append("_").Append(index).Append("\" ")
                     .Append("name=\"").Append(id).Append("_0\" ")
                     .Append("value=\"").Append(StringUtil.EscapeQuotes(trueVal.value))
                     .Append("\">").Append(StringUtil.EscapeQuotes(trueVal.value)).Append("<br>");
                  index++;
                  boolean.Append("<input type=\"radio\" ")
                     .Append("id=\"").Append(iditem).Append("_").Append(index).Append("\" ")
                     .Append("name=\"").Append(id).Append("_0\" ")
                     .Append("value=\"").Append(StringUtil.EscapeQuotes(falseVal.value))
                     .Append("\">").Append(StringUtil.EscapeQuotes(falseVal.value)).Append("<br>");
               }

               return boolean.ToString();

            case DATASET:
               StringBuilder dataset = new StringBuilder();

               if (values.Count == 1)
                  dataset.Append("%%dataset%%").Append(values[0].value)
                     .Append("%%datasetname%%").Append(iditem).Append("_0");

               return dataset.ToString();


            default: return "Тип неопределен";
         }
      }
   }

   public class QuestionItemValue : DataObject
   {
      public string value;

      public QuestionItemValue Copy()
      {
         QuestionItemValue result = new QuestionItemValue();
         result.value = value;

         return result;
      }
   }

   public class AgentQuest : DataObject
   {
      public static readonly string OBJECT_NAME = "AgentQuest";

      public string userid = string.Empty;
      [KeyField]
      public string idquest = string.Empty;
   }

   public class Answer : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Answer";

      [Reference("Question", "question")]
      public Question quest = null;


      [ItemType(typeof(AnswerItem))]
      public List<AnswerItem> items = null;

      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
   }

   public class AnswerItem : DataObject
   {
      public string id = string.Empty;
      public string answer = string.Empty;
      public int type = -1;
      public string remark = string.Empty;
      public string iditem = string.Empty;

      public string Id { get { return id; } }
      public string Answer { get { return answer; } }
   }

   public class PhotoPriceItem : DataObject
   {
      public string id = string.Empty;
      public PhotoPriceItem() { }
      public PhotoPriceItem(String id) { this.id = id; }
   }
   
   public class PricePhoto : DataObject
   {
      public static readonly string OBJECT_NAME = "MgrPricePhoto";
      public static readonly string SERVER_PATH = @"photo\";
      
      [KeyField]
      public string name = string.Empty;

      [ItemType(typeof(PhotoPriceItem))]
      public List<PhotoPriceItem> items = null;
      public byte[] photo = null;
      
      private string localPath = string.Empty;
      public string LocalPath
      {
         get { return localPath; }
         set { localPath = value; }
      }

      public string Name
      {
         get
         {
            return name;
         }

         set
         {
            if (value.StartsWith(SERVER_PATH))
               name = value;
            else
               name = SERVER_PATH + value;
         }
      }
   }

   public class Sales : Order
   {
      public new static readonly string OBJECT_NAME = "Sales";
   }

   public class Category : DataObject
   {
      public static readonly string OBJECT_NAME = "Category";

      [KeyField]
      public string id = string.Empty;
      public string name = string.Empty;

      public string Name { get { return name; } }

      public override string ToString()
      {
         return Name;
      }
   }

   public class Producer : DataObject
   {
      public static readonly string OBJECT_NAME = "Producer";

      [KeyField]
      public string id = string.Empty;
      public string name = string.Empty;

      public string Name { get { return name; } }

      public override string ToString()
      {
         return Name;
      }
   }

   public class LiveArea : GRSoft.Network.DataObject
   {
      [KeyField]
      public string id = string.Empty;
      public string name = string.Empty;
      public string code = string.Empty;

      public string Name { get { return name; } }
      public string Code { get { return code; } }
      public string Id { get { return id; } }

      public override string ToString()
      {
         return Name;
      }
   }

   public class Region : LiveArea, IComparable
   {
      public static readonly string OBJECT_NAME = "Region";
      public string region1 = string.Empty;
      public string region2 = string.Empty;

      [Reference("Region1", "region1", typeof(Region1))]
      public Region1 r1;

      [Reference("Region2", "region2", typeof(Region2))]
      public Region2 r2;

      #region IComparable Members

      public int CompareTo(object obj)
      {
         return name.CompareTo(((Region)obj).name);
      }

      #endregion
   }

   public class Region1 : LiveArea
   {
      public static readonly string OBJECT_NAME = "Region1";
      public string region2;
   }

   public class Region2 : LiveArea
   {
      public static readonly string OBJECT_NAME = "Region2";
      public BindingList<Region1> childs = new BindingList<Region1>();
   }

   public class DeliveryItem : DataObject
   {
      public string id;
      [Precision(3)]
	   public double qty;
	   public double sum;
   }

   public class Delivery : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Delivery";

      public double sumD;

	   public string number = "";
      public DateTime payDate = DateTime.MinValue;

      [ItemType(typeof(DeliveryItem))]
	   public List<DeliveryItem> items;
   	
	   public double sum()
      {
         double result = 0;
   		
		   if(items != null)
			   foreach (DeliveryItem item in items)
				   result += item.sum;
   		
		   return result;
	   }
   };

   public class OrderCommitted : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "OrderCommitted";
      
      public DateTime created;
      public string number;
      public string userid;
   }

   public class OrderW : Order
   {
      public static readonly new string OBJECT_NAME = "OrderW";
   }

   public class OrderCharge : Order
   {
      public static readonly new string OBJECT_NAME = "OrderCharge";
   }

#if MOVEMENT_DOC
   public class MoveDoc : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Move";

      public string src = null;
      public string dst = null;

      [ItemType(typeof(MoveItem))]
      public List<MoveItem> items = null;

      internal override int Qty
      {
         get
         {
            int q = 0;
            foreach (MoveItem item in items)
               q += (int)(item.qty + 0.5);
            return q;
         }
      }

      public override string Remark
      {
         get
         {
            DataSet<string, Sklad> dsSklad = (DataSet<string, Sklad>)DataModule.Get(Sklad.OBJECT_NAME);
            const string KEY = "Склады";
            string s = string.Empty;
            string d = string.Empty;

            if (dsSklad.ContainsKey(KEY))
            {
               string[] val = dsSklad[KEY].value.Split(';');

               for (int i = 0; i < val.Length; i++)
               {
                  string[] p = val[i].Split('\t');

                  if (p.Length == 2)
                  { 
                     if(src.Equals(p[1]))
                        s = p[0];
                     if (dst.Equals(p[1]))
                        d = p[0];
                  }
               }

            }

            return string.Format("{0} - {1}({2})", s.Length == 0 ? src: s,
               d.Length == 0 ? dst : d, remark);
         }
      }

      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }

      public override double Sum()
      {
         double sum = 0;
         foreach (MoveItem item in items)
            sum += item.Sum;

         return sum;
      }
   }


   public class MoveItem : DataObject
   {
      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;
      public string id = "";

      [Precision(3)]
      public double qty = 0;

      //Наименование
      public string Item { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }
      //Количество
      public double Qty { get { return qty; } }
      public double Cost { get { return item != null && item.cost.Length > 0 ? item.cost[0] : 0.0; } }
      public double Sum { get { return Qty * Cost; } }
   }

   public class Sklad : DataObject
   {
      public static readonly string OBJECT_NAME = "Sklads";
      [KeyField]
      public string key= string.Empty;
      public string value = string.Empty;
   }
#endif

#if DISTR_DOC
   public class Distr : DataObject
   {
      public static readonly string OBJECT_NAME = "DistrDoc";
      public DateTime created = DateTime.MinValue;
      public DateTime sended = DateTime.MinValue;

      [Reference("Org,PotenzialOrg,CommonOrg,CommonOrgs", "id", typeof(Org))]
      public Org org = null;
      public string id = String.Empty;

      [Reference("Agents", "userid")]
      public Agent agent = null;

      [ItemType(typeof(DistrItem))]
      public List<DistrItem> items = null;
      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }

   }

   public class DistrItem : DataObject
   {
      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;
      public int exists = 0;

      //Свойства для отображение в гриде
      //Количество
      public double Qty { get { return exists; } }
      //Наименование
      public string Item { get { return item != null ? item.Name : string.Empty; } }
   }
#endif

#if INVOICE_DOC
   public class InvoiceItem : DataObject
   {
      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;
      [Precision(3)]
      public double qty = 0;
      [Precision(3)]
      public double weight = 0;

      //Свойства для отображение в гриде
      //Количество
      public double Qty { get { return qty; } }
      //Наименование
      public string Item { get { return item != null ? item.Name : string.Empty; } }
      public double Weight { get { return weight; } }
   }


   public class Invoice : DataObject
   {
      public static readonly string OBJECT_NAME = "Invoice";

      public DateTime date = DateTime.Now;
      public DateTime created = DateTime.Now;
      [Reference("Agents", "userid")]
      public Agent agent = null;
      [Reference("Org", "id", typeof(Org))]
      public Org org = null;
      public string id = String.Empty;
      [Precision(5)]
      public double latitude = 0;
      [Precision(5)]
      public double longitude = 0;
      public DateTime sended = DateTime.MinValue;

      [ItemType(typeof(InvoiceItem))]
      public List<InvoiceItem> items = null;

      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
      public string OrgName { get { return org == null ? string.Empty : org.name; } }
      public string OrgAddr { get { return org == null ? string.Empty : org.address; } }
   }
#endif

   public class ManagerQuestion : Question
   {
      public static readonly new string OBJECT_NAME = "ManagerQuestion";
   }

   public class TaskDone : BaseDocument
   {
      public static readonly string OBJECT_NAME = "TaskDone";
   }

   public class OrgMatrix : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "OrgMatrix";

      [KeyField]
      public string id = string.Empty;

      [KeyField]
      public string cdef = string.Empty;
      public string name = string.Empty;

      public ContractDef contract = null;

      public string Name { get { return name; } }
      public string Contract { get { return contract == null ? "" : contract.name; } }

      public override string ToString() { return Name; }
   }

   public class DocFilterHelper
   {
      Dictionary<int, ScriptDef> scripts = new Dictionary<int,ScriptDef>();
      Dictionary<string, Dictionary<DateTime, bool>> allowedDocs = new Dictionary<string, Dictionary<DateTime, bool>>();

      public DocFilterHelper(DataSet<int, ScriptDef> scriptDefs)
      {
         foreach(ScriptDef sd in scriptDefs.Data)
         {
            if (CurrentUser.user.HaveContract(sd.cdefid))
               scripts.Add(sd.id, sd);
         }
      }

      public void SetScripts(DataSet<int, ScriptDoc> docs)
      {
         List<int> needRemove = new List<int>();

         foreach (KeyValuePair<int, ScriptDoc> kv in docs)
         {
            int scrId = kv.Value.scriptId;
            if (!scripts.ContainsKey(scrId))
            {
               needRemove.Add(kv.Key);
               continue;
            }

            foreach(ScriptDocItem sdi in kv.Value.items)
            {
               if (sdi.Inited == false)
                  continue;

               Dictionary<DateTime, bool> adocs = null;
               if (!allowedDocs.ContainsKey(sdi.type))
               {
                  adocs = new Dictionary<DateTime, bool>();
                  allowedDocs[sdi.type] = adocs;
               }
               else
                  adocs = allowedDocs[sdi.type];

               adocs[sdi.date] = true;
            }
         }

         foreach (int key in needRemove)
            docs.Remove(key);
      }

      public void Update(IDataSet docs)
      {
         List<int> needRemove = new List<int>();
         Dictionary<DateTime, bool> adocs = null;
         if (allowedDocs.ContainsKey(docs.Name))
            adocs = allowedDocs[docs.Name];
         else
            adocs = new Dictionary<DateTime, bool>();

         IEnumerator e = docs.GetEnumerator();
         PropertyInfo pi = null, vi = null;
         while (e.MoveNext())
         {
            if (pi == null)
               pi = e.Current.GetType().GetProperty("Key");
            if( vi == null )
               vi = e.Current.GetType().GetProperty("Value");


            if (pi == null || vi == null)
               break;

            BaseDocument bd = (BaseDocument)vi.GetValue(e.Current, null);
            if (bd == null)
               continue;
            if (adocs.ContainsKey(bd.created) == false)
               needRemove.Add((Int32)pi.GetValue(e.Current, null));
         }
         //foreach (KeyValuePair<int, BaseDocument> bd in docs)
         //   if (adocs.ContainsKey(bd.Value.created) == false)
         //      needRemove.Add(bd.Key);

         foreach (int key in needRemove)
            docs.Remove(key);
      }
   }

   public class Contract : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Contract";

      [ItemType(typeof(ContractItem))]
      public List<ContractItem> items = new List<ContractItem>();
   }

   public class ContractItem : GRSoft.Network.DataObject
   {
      public string id = string.Empty;
      public double qty = 0.0;
      public double face = 0.0;

      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;

      public string Name { get { return item.Name; } }
      public double Qty { get { return qty; } }
   }
}

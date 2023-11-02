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
//using GRSoft.NapoleonManager.Utils;
using System.Drawing;
using System.IO;
using System.ComponentModel;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   delegate void EmptyParamHandler();
   delegate void InvokeParamHandler(object objects);

   //Исключение когда пришел объет не того типа что ожидали (23.09.2010 kki)
   class EIncompatibilityObject : Exception { }

   //Исключение когда данные неправильны или пришли не все
   class EDataCorrupted : Exception { }

   public class DivisionManager : DataObject
   {
      public static readonly string OBJECT_NAME = "DivisionManager";

      [KeyField]
      public string login = "";

      public string password = "";
      public int division = 0;

      public string Login { get { return login; } set { login = value; } }
      public string Password { get { return password; } set { password = value; } }
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

      public string userAction
      {
         get
         {
            switch (action)
            { 
               case 1:
                  return "GPS - Включен";
               case 2:
                  return "GPS - Выключен";
               case 3:
                  return "Время изменено";
               case 4:
                  return "КПК - Включен";
               case 5:
                  return "КПК - Выключен";
               case 6:
                  return "Сбой программы";
               case 7:
                  return "Наполеон - Запуск";
               case 8:
                  return "Наполеон - Выход";
               case 9:
                  return String.Format("КПК статус: {0}", comments);
               default:
                  return string.Empty;
            }
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
         Invoice}
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
            default:
               return false;
         }
         return true;
      }

      public override string ToString()
      {
         switch (val)
         {
            case TObjType.OtOrder:
               return "Заявка";
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
               return "Бонус";
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

   public class Price : DataObject
   {
      public static readonly string OBJECT_NAME = "ManagerPrice";

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
      public string fid = string.Empty;

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
   }

   public class OrgAddress : DataObject
   {
      public string name = "";
      public string id = "";
   }

   public class Org : DataObject, IComparable<Org>
   {
      public static string OBJECT_NAME = "Org";
      public static string COMMON_OBJECT_NAME = "CommonOrgs";

      [KeyField]
      public string id = "";
      public string name = "";
      public string address = "";

      [Reference("Agents", "userid")]
      public Agent agent = null;

      [Precision(5)]
      public double longitude = 0;

      [Precision(5)]
      public double latitude = 0;

      public int type = 0;

#if Ishim
      public double balance = 0;
      public double Balance { get { return balance; } }
#endif

      // это поле отоброжает цвет на КПК (разный порядок RGB & BGR)
      public int color = 0;

      public string Name 
      { 
          get 
          { 
              return Config.GetConfig().isFullOrgName ? 
                  String.Format("{0} ({1})", name, Address) 
                  : name; 
          } 
      }

      public string Address
      {
         get
         {
            return address == null ? "" : address;
         }
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

      [Precision(3)]
      public double qty = 0;
      public double cost = 0;
      public double sum = 0;

      public double offTakeDiff = 0;

      //Свойства для отображение в гриде
      //Количество
      public double Qty { get { return qty; } }
      //Цена
      public double Cost { get { return (cost == 0) ? sum / qty : cost; } }
      public string SCost { get { return Cost.ToString("C", Config.GetCultureInfo()); } }
      public double Sum { get { return (sum == 0) ? cost * qty : sum; } }

      //Наименование
      public string Item { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }
   }

   public class Order : DataObject
   {
      public static readonly string OBJECT_NAME = "Order";

      public DateTime date = DateTime.Now;
      public DateTime created = DateTime.Now;
      [Precision(5)]
      public double latitude = 0;
      [Precision(5)]
      public double longitude = 0;
      public DateTime sended = DateTime.MinValue;

      [DataField("params")]
      public int _params = 0;
      public bool OutOfPlan { get { return ((_params & 0x40000) != 0); } }

      [Reference("Agents", "userid")]
      public Agent agent = null;

      [Reference("Org", "id", typeof(Org))]
      public Org org = null;
      public string id = String.Empty;

      [ItemType(typeof(OrderItem))]
      public List<OrderItem> items = null;

#if Agama
      public int unitCode = 0;
#endif

#if FOCUSED_GROUP
      public class FocusItem : DataObject
      {
         public string fid = "";
         public string remark = "";
      }

      [ItemType(typeof(FocusItem))]
      public List<FocusItem> focusedFolders = null;
#endif

      public string remark = "";

      // поля для дублирования
      public string ctype = "";
      public string firma = "";
      public int delay;
      public int cash;

      public DateTime Date { get { return date; } }
      public DateTime Created { get { return created; } }
      public string AgentName { get { return agent == null ? string.Empty : agent.name; } }
      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
      public string OrgName { get { return org == null ? string.Empty : org.name; } }

#if DELIVERY_ADDRESS
      public string adrCode;
      public string OrgAddr { get { return org == null ? string.Empty : org.GetAddress(adrCode); } }
#else
      public string OrgAddr { get { return org == null ? string.Empty : org.address; } }
#endif

      public double DSum { get { return Sum(); } }
      public string SSum { get { return DSum.ToString("C", Config.GetCultureInfo()); } }
      public DateTime Sended { get { return sended; } }

      internal int Qty
      {
         get
         {
            int q = 0;
            foreach (OrderItem item in items)
               q += (int)(item.Qty + 0.5);
            return q;
         }
      }

      internal double Sum()
      {
         double sum = 0;
         foreach (OrderItem item in items)
            sum += item.Sum;

         return sum;
      }

      internal double Weight()
      {
         double res = 0;
         foreach (OrderItem item in items)
         {
            if( item.item != null )
               res += (item.item.weight * item.qty);
         }
         return res;
      }
   }

   public class OrgFolderItem : DataObject
   {
      [Reference("Org,PotenzialOrg", "name")]
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
      public string name = "";
      public int id = -1;

      [Reference("Agents", "userid")]
      public Agent agent = null;

      [ItemType(typeof(OrgFolderItem))]
      public List<OrgFolderItem> items = null;

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

      public Division parentDivision = null;

      public List<Division> childs = new List<Division>();

      public override string ToString()
      {
         return name;
      }

      public string DivisionName
      {
         get { return name; }
      }

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
         foreach (DivisionAgent da in agents)
            if (da.agent == a)
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

   public class Agent : DataObject
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

      public string Name { get { return name; } }

      public override string ToString()
      {
         return String.Format("({0}){1}", id, name);
      }

      public bool Equals(Agent agent)
      {
         return this.id == agent.id;
      }
   }

   public class UserInfo : DataObject
   {
      public static readonly string OBJECT_NAME = "UserInfo";

      [KeyField]
      public string userid = string.Empty;
      public string phone = string.Empty;
   }

   class Agents : DataSet<string, Agent>
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

   public class VisitInfo : DataObject
   {
      public static readonly string V_OBJECT_NAME = "VisitInfo";
      public DateTime date = DateTime.Now;
      [Reference("Agents", "userid")]
      public Agent agent = null;
      public string userid = "";

      [Reference("Org,PotenzialOrg", "id", typeof(Org))]
      public Org org = null;
      public string id = String.Empty;

      public string remark = string.Empty;
      [Precision(5)]
      public double latitude = 0;
      [Precision(5)]
      public double longitude = 0;
      public DateTime sended = DateTime.MinValue;

      public DateTime created = DateTime.MinValue;

      [Precision(2)]
      public double rating = 0;

#if Agama
      public int unitCode = 0;
#endif

      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
      public string OrgName { get { return org == null ? string.Empty : org.name; } }
      public string OrgAddr { get { return org == null ? string.Empty : org.address; } }

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

   public class Visit : VisitInfo
   {
      public static readonly string OBJECT_NAME = "Visit";

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


   public class OrgRemnantsItem : DataObject
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
   }

   public class OrgRemnants : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgRemnants";

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

      [ItemType(typeof(OrgRemnantsItem))]
      public List<OrgRemnantsItem> items = null;

      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
      public string OrgName { get { return org == null ? string.Empty : org.name; } }
      public string OrgAddr { get { return org == null ? string.Empty : org.address; } }
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

   public class Matrix : DataObject
   {
      static public string OBJECT_NAME = "CommonMatrix";

      public string name = String.Empty;

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
      internal string OrgAddr { get { return org == null ? string.Empty : org.address; } }
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

   //public class ScriptDefItem : DataObject
   //{
   //   public String curType = "";
   //   public String name = "";
   //   public int nextDoc;
   //   public int condition;
   //   public String condParam = "";

   //   public String Name
   //   {
   //      get { return (name.Length > 0) ? name : ScriptDocuments.GetName(curType); }
   //   }
   //}

   //public class ScriptDef : DataObject
   //{
   //   static public string OBJECT_NAME = "ScriptDef";

   //   [KeyField]
   //   public Int32 id = -1;
   //   public String userid = "";
   //   public String name;

   //   [ItemType(typeof(ScriptDefItem))]
   //   public List<ScriptDefItem> items;

   //   public string Name
   //   { 
   //      get 
   //      {
   //         if (name.Length == 0 && items.Count > 0)
   //            return items[0].Name;

   //         return name; 
   //      }
   //   }
      
   //   public string DocsStr
   //   {
   //      get
   //      {
   //         StringBuilder sb = new StringBuilder();

   //         if (items != null)
   //            foreach (ScriptDefItem item in items)
   //               sb.Append(item.Name).Append(", ");

   //         if (sb.Length > 2)
   //            sb.Remove(sb.Length - 2, 2);

   //         return sb.ToString();
   //      }
   //   }

   //}

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

   public class ScriptDoc : DataObject
   {
      static public string OBJECT_NAME = "ScriptDoc";

      [Reference("Org,PotenzialOrg", "id", typeof(Org))]
      public Org org = null;
      public string id = string.Empty;
      public int scriptId;

      [Precision(5)]
      public double longitude = 0;
      [Precision(5)]
      public double latitude = 0;

      [Reference("Agents", "userid")]
      public Agent agent = null;
      public string userid = string.Empty;

      public DateTime created = DateTime.MinValue;
      public DateTime sended = DateTime.MinValue;

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

      [Reference("Org,PotenzialOrg", "id", typeof(Org))] public Org org = null;
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

      [Reference("Org,PotenzialOrg", "id", typeof(Org))]
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

   public class PKO : DataObject
   {
      static public readonly string OBJECT_NAME = "Pko";

      [Reference("Org", "id", typeof(Org))]
      public Org org = null;
      public string id = "";

      public DateTime date = DateTime.Now;
      public DateTime created = DateTime.Now;
      [Precision(5)]
      public double latitude = 0;
      [Precision(5)]
      public double longitude = 0;
      public DateTime sended = DateTime.MinValue;

      public string number = "";
      public double sum = 0;
   
      [Reference("Agents", "userid")]
      public Agent agent = null;

      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
      public string OrgName { get { return org == null ? string.Empty : org.name; } }
      public string OrgAddr { get { return org == null ? string.Empty : org.address; } }

      public double Sum { get { return sum; } }
   }

   public class Incass : DataObject
   {
      static public readonly string OBJECT_NAME = "Incass";

      [Reference("Org", "id", typeof(Org))]
      public Org org = null;
      public string id = "";

      public DateTime date = DateTime.Now;
      public DateTime created = DateTime.Now;
      public DateTime sended = DateTime.Now;
      public double sum = 0;
      public string remark = "";
      [Precision(5)]
      public double latitude = 0;
      [Precision(5)]
      public double longitude = 0;

      [Reference("Agents", "userid")]
      public Agent agent = null;

      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
      public string OrgName { get { return org == null ? string.Empty : org.name; } }
      public string OrgAddr { get { return org == null ? string.Empty : org.address; } }

      public double Sum { get { return sum; } }
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

   public class ReturnItem : DataObject
   {
      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;

      public String id = "";

      [Precision(3)]
      public double qty = 0;

      public double offTakeDiff = 0;
      public double cost = 0;

      //Свойства для отображение в гриде
      //Количество
      public double Qty { get { return qty; } }

      //Наименование
      public string Item { get { return item != null ? item.Name : "Объект с кодом <" + id + ">"; } }
   }

   public class Returns : DataObject
   {
      public static readonly string OBJECT_NAME = "Returns";

      public DateTime date = DateTime.Now;
      public DateTime created = DateTime.Now;
      [Precision(5)]
      public double latitude = 0;
      [Precision(5)]
      public double longitude = 0;
      public DateTime sended = DateTime.MinValue;

      [Reference("Agents", "userid")]
      public Agent agent = null;

      [Reference("Org", "id", typeof(Org))]
      public Org org = null;
      public string id = String.Empty;

      [ItemType(typeof(ReturnItem))]
      public List<ReturnItem> items = null;


      public string remark = "";
      public string cause = "";

      public DateTime Date { get { return date; } }
      public DateTime Created { get { return created; } }
      public string AgentName { get { return agent == null ? string.Empty : agent.name; } }
      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
      public string OrgName { get { return org == null ? string.Empty : org.name; } }
      public string OrgAddr { get { return org == null ? string.Empty : org.address; } }
      public double DSum { get { return Sum(); } }
      public string SSum { get { return DSum.ToString("C", Config.GetCultureInfo()); } }
      public DateTime Sended { get { return sended; } }

      internal int Qty
      {
         get
         {
            int q = 0;
            foreach (ReturnItem item in items)
               q += (int)(item.Qty + 0.5);
            return q;
         }
      }

      internal double Sum()
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

   internal class UserActivity : DataObject
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

      public void SetUsePeriod()
      {
         _params |= USE_PERIOD;
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

   internal class Answer : DataObject
   {
      public static readonly string OBJECT_NAME = "Answer";

      public DateTime created = DateTime.MinValue;
      [Reference("Agents", "userid")]
      public Agent agent = null;

      public String userid = "";

      [Reference("Question", "question")]
      public Question quest = null;

      [Reference("Org,PotenzialOrg,CommonOrgs", "id", typeof(Org))]
      public Org org = null;
      public string id = string.Empty;
      public DateTime sended = DateTime.MinValue;

      [ItemType(typeof(AnswerItem))]
      public List<AnswerItem> items = null;

      [Precision(5)]
      public double longitude = 0;

      [Precision(5)]
      public double latitude = 0;

      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
   }

   internal class AnswerItem : DataObject
   {
      public string id = string.Empty;
      public string answer = string.Empty;
      public int type = -1;
      public string remark = string.Empty;
      public string iditem = string.Empty;

      public string Id { get { return id; } }
      public string Answer { get { return answer; } }
   }

   class PhotoPriceItem : DataObject
   {
      public string id = string.Empty;
      public PhotoPriceItem() { }
      public PhotoPriceItem(String id) { this.id = id; }
   }
   
   class PricePhoto : DataObject
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

   class Sales : Order
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
   public class MoveDoc : DataObject
   {
      public static readonly string OBJECT_NAME = "Move";

      [Reference("Agents", "userid")]
      public Agent agent = null;
      public DateTime date = DateTime.Now;
      public DateTime created = DateTime.Now;
      public DateTime sended = DateTime.MinValue;
      [Reference("Org", "id", typeof(Org))]
      public Org org = null;
      public string id = String.Empty;
      public string src = null;
      public string dst = null;
      [Precision(5)]
      public double latitude = 0;
      [Precision(5)]
      public double longitude = 0;
      public string remark = string.Empty;

      [ItemType(typeof(MoveItem))]
      public List<MoveItem> items = null;

      internal int Qty
      {
         get
         {
            int q = 0;
            foreach (MoveItem item in items)
               q += (int)(item.qty + 0.5);
            return q;
         }
      }

      public string Remark
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

      internal double Sum()
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

      [Reference("Org,PotenzialOrg,CommonOrgs", "id", typeof(Org))]
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
}

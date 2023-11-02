using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Reflection;
using System.Drawing;
using System.Collections;

namespace GRSoft.Ads
{
   public delegate void EmptyInvoker();
   public delegate void Invoker(object param);
   public delegate bool IsOK(object param);

   internal class DsProfession 
      : StrKeyDataSet<Profession> 
   {
      public DsProfession(bool add)
         :base(Profession.OBJECT_NAME, add)
      {
      }
   }

   internal class DsStuff : DataSet<int, Stuff>
   {
      public DsStuff(bool add)
         : base(Stuff.OBJECT_NAME, add)
      { 
      }
   }

   internal class DsDistrict
      : StrKeyDataSet<District>
   {
      public DsDistrict(bool add)
         : base(District.OBJECT_NAME, add)
      { 
      }
   }

   internal class DsClient
      : StrKeyDataSet<Client>
   {
      public DsClient(bool add)
         : base(Client.OBJECT_NAME, add)
      {
      }

      public override bool KeepData { get { return true; } }
   }

   internal class DsBrigade
      : StrKeyDataSet<Brigade>
   {
      public DsBrigade(bool add)
         : base(Brigade.OBJECT_NAME, add)
      { 
      }
   }

   internal class DsOrder
      : DataSet<DateTime, Order>
   {
      public DsOrder(bool add)
         : base(Order.OBJECT_NAME, add)
      { 
      }
   }

   internal class DsOrderDel
      : DataSet<DateTime, OrderDel>
   {
      public DsOrderDel(bool add)
         : base(OrderDel.OBJECT_NAME, add)
      {
      }
   }

   public class DsOrderRcv
      : DataSet<Int32, OrderRcv>
   {
      public DsOrderRcv(bool add)
         : base(OrderRcv.OBJECT_NAME, add)
      {
      }
   }

   internal class DsUserOrder
      : DataSet<int, UserOrder>
   {
      public DsUserOrder(bool add)
         : base(UserOrder.OBJECT_NAME, add)
      { 
      }
   }

   internal class DsJobType
      : DataSet<Int32, JobType>
   {
      public DsJobType(bool add)
         : base(JobType.OBJECT_NAME, add)
      {
      }
   }

   internal class StrKeyDataSet<DataRepType>
      : DataSet<string, DataRepType>
   {
      public StrKeyDataSet(String name, bool add)
         : base(name, add)
      { }

      public string GetNextKey()
      {
         FieldInfo[] fields = typeof(DataRepType).GetFields(BindingFlags.Public | BindingFlags.Instance);

         FieldInfo keyField = null;

         foreach (FieldInfo fi in fields)
         {
            object[] attrs = fi.GetCustomAttributes(typeof(KeyFieldAttribute), false);

            if (attrs.Length == 1)
            {
               keyField = fi;
               break;
            }
         }

         if (keyField != null)
         {
            string key = System.Guid.NewGuid().ToString();
            return key.Replace("-", ""); ;
         }
         else
            throw new ArgumentException(ElementType.ToString() + " не подоходит для GetNextKey");
      }
   }

   internal class DsDivision : 
      DataSet<Int32, Division>
   {
      public DsDivision(bool add)
         : base(Division.OBJECT_NAME, add)
      {
      }
   }

   internal class DsDivisionManager :
      DataSet<string, DivisionManager>
   {
      public DsDivisionManager(bool add)
         : base(DivisionManager.OBJECT_NAME, add)
      { 
      }
   }

   internal class DsGPSPos :
      DataSet<DateTime, GPSPos>
   {
      public DsGPSPos(bool add)
         : base(GPSPos.OBJECT_NAME, add)
      { 
      }
   }

   internal class DsKladr :
      DataSet<string, Kladr>
   {
      public DsKladr(bool add)
         : base(Kladr.OBJECT_NAME, add)
      {
      }
   }

   internal class DsStreet :
      DataSet<string, Street>
   {
      public DsStreet(bool add)
         : base(Street.OBJECT_NAME, add)
      {
      }
   }

   internal class DsFolders :
      DataSet<string, Folder>
   {
      public DsFolders(bool add)
         : base(Folder.OBJECT_NAME, add)
      {
      }
   }

   internal class DsWarehouse :
      DataSet<string, Warehouse>
   {
      public DsWarehouse(bool add)
         : base(Warehouse.OBJECT_NAME, add)
      { 
      }
   }

   internal class DsMessage :
      DataSet<int, Message>
   {
      public DsMessage(bool add)
         : base(Message.OBJECT_NAME, add)
      { 
      }
   }

   internal class DsMessageArchive :
      DataSet<int, MessageArchive>
   {
      public DsMessageArchive(bool add)
         : base(MessageArchive.OBJECT_NAME, add)
      { 
      }
   }

   internal class DsWorkDay :
      DataSet<int, WorkDay>
   {
      public DsWorkDay(bool add)
         : base(WorkDay.OBJECT_NAME, add)

      { 
      }
   }

   internal class DsUserLog :
      DataSet<int, UserLog>
   {
      public DsUserLog(bool add)
         : base(UserLog.OBJECT_NAME, add)
      { 
      }
   }

   internal class DsCommonConfig :
      DataSet<int, CommonConfig>
   {
      public DsCommonConfig(bool add)
         : base(CommonConfig.OBJECT_NAME)
      { 
      }
   }

   internal class DsSchedule :
      DataSet<int, Schedule>
   {
      public DsSchedule(bool add)
         : base(Schedule.OBJECT_NAME)
      { 
      }
   }

   internal class DsWorkType :
      StrKeyDataSet<WorkType>
   {
      public DsWorkType(bool add)
         : base(WorkType.OBJECT_NAME, add)
      { 
      }
   }

   internal class DsCounter :
      DataSet<string, Counter>
   {
      public DsCounter(bool add)
         : base(Counter.OBJECT_NAME, add)
      { 
      }
   }

   public class Profession : DataObject
   {
      public static string OBJECT_NAME = "Profession";
      
      [KeyField]
      public string id = "";
      public string name = "";

      public string Id { get { return id; } }
      public string Name { get { return name; } }

      public override string ToString()
      {
         return Name;
      }
   }

   public class Stuff : DataObject
   {
      public static string OBJECT_NAME = "Stuff";

      [KeyField]
      public Int32 id = -1;
      public string firstname = "";
      public string lastname = "";
      public string middlename = "";
      public string address = "";
      public string phone = "";

      [Reference("Profession", "profession")]
      public Profession profession = null;
      
      public int rank = 0;

      public string FIO 
      { 
         get 
         {
            StringBuilder result = new StringBuilder(lastname);
            if (firstname.Length > 0)
               result.Append(" ").Append(firstname.ToUpper()[0]).Append(".");
            if (middlename.Length > 0)
               result.Append(" ").Append(middlename.ToUpper()[0]).Append(".");
            
            return result.ToString(); 
         } 
      }

      public string Address { get { return address; } }
      public string Phone { get { return phone; } }
      public Profession Profession { get { return profession; } }
      public int Rank { get { return rank; } }
   }

   public class District : DataObject
   {
      public static string OBJECT_NAME = "District";

      [KeyField]
      public string id = "";
      public string name = "";

      public string Id { get { return id; } }
      public string Name { get { return name; } }

      public override string ToString()
      {
         return Name;
      }
   }

   public class ClientContact : DataObject
   {
      public string name = "";
      public string phone = "";

      public string Name { get { return name; } }
      public string Phone { get { return phone; } }
   }

   public class Client : DataObject
   {
      public static string OBJECT_NAME = "Client";

      [KeyField]
      public string id = "";
      public string name = "";
      public string address = "";

      [ItemType(typeof(ClientContact))]
      public List<ClientContact> contacts = null;

      public string Name { get { return name; } }
      public string Address { get { return address; } }

      public override string ToString()
      {
         return Name;
      }
   }

   public class BrigadeDistrict : DataObject
   {
      public string id = "";
      [Reference("District", "id")]
      public District district = null;
      public string Name { get { return district == null ? "Неизвестно" : district.Name; } }
   }

   public class BrigadeStuff : DataObject
   {
      public Int32 id = -1;
      [Reference("Stuff", "id")]
      public Stuff stuff = null;
   }

   public class BrigadeAddress : DataObject
   {
      public string address = "";
      public double latitude;
      public double longitude;

      public string Address { get { return address; } }

      public override string ToString()
      {
         return Address;
      }
   }

   public class Brigade : DataObject, IComparable
   {
      public static string OBJECT_NAME = "Agents";

      [KeyField]
      public string id = "";
      public string name = "";
      public string login = "";
      public string password = "";

      [ItemType(typeof(BrigadeDistrict))]
      public List<BrigadeDistrict> region = new List<BrigadeDistrict>();

      [ItemType(typeof(BrigadeStuff))]
      public List<BrigadeStuff> stuff = new List<BrigadeStuff>();

      [ItemType(typeof(BrigadeAddress))]
      public List<BrigadeAddress> address = new List<BrigadeAddress>();

      [Reference("JobType", "jobtype")]
      public JobType jobtype = null;

      public string prefix = "";

      public string Login { get { return login; } }
      public string Password { get { return password; } }
      public string Name { get { return name; } }
      public JobType JobType { get { return jobtype; } set { jobtype = value; }  }

      public override string ToString()
      {
         return Name;
      }

      public Color JobTypeColor { get { return JobType != null ? JobType.Color : Color.Black; } }
      public string JobTypeText { get { return JobType != null ? JobType.Name : string.Empty; } }

      public bool hasService(District d)
      {
         foreach (BrigadeDistrict bd in region)
         {
            if (bd.id.Equals(d.id))
               return true;
         }

         return false;
      }

      public string Prefix { get { return prefix; } }

      public override bool Equals(object obj)
      {
         if (obj is Brigade)
            return id.Equals(((Brigade)obj).id);
         else
            return false;
      }

      public override int GetHashCode()
      {
         return base.GetHashCode();
      }

      #region IComparable Members

      public int CompareTo(object obj)
      {
         return obj == null ? -1 : Name.CompareTo(((Brigade)obj).Name);
      }

      #endregion
   }

   public class OrderItem : DataObject
   {
      [Reference("Warehouse", "priceid", typeof(Warehouse))]
      public Warehouse item = null;
      public double qty = 0.0;

      [Caption("Наименование", 10.0)]
      public string Name
      {
         get
         {
            return item == null ? string.Empty : item.name;
         }
      }

      [Caption("Кол-во", 10.0)]
      public double Qty { get { return qty; } }
      [Caption("Цена",10.0)]
      public string Cost { get { return item == null ? "0,00" : item.Cost; } }
   }

   public class OrderWorkType : DataObject
   {
      [Reference("WorkType", "id", typeof(WorkType))]
      public WorkType item = null;
   }

   public class WorkType : DataObject
   {
      public static readonly string OBJECT_NAME = "WorkType";

      [KeyField]
      public string id;
      public string name;

      public string Id { get { return id; } }
      public string Name { get { return name; } }

      public override string ToString()
      {
         return Name;
      }
   }

   public class Order : DataObject
   {
      public static string OBJECT_NAME = "Order";
      public static string N_A = "не назначен";

   	public static readonly int DOING_PARAMS = 0x40000;
      public static readonly int DONE_PARAMS = 0x80000;
      public static readonly int REJECTED = 0x100000;

      [KeyField]
      public DateTime created = new DateTime(DateTime.Now.Year,
         DateTime.Now.Month, DateTime.Now.Day, DateTime.Now.Hour,
         DateTime.Now.Minute, DateTime.Now.Second);

      [Reference("Agents", "userid")]
      public Brigade brigade = null;
      
      [Reference("Client", "client")]
      public Client client = null;

      public DateTime planbegin = DateTime.Now;
      public DateTime planend = DateTime.Now;
      
      public string text = "";
      public string address = "";

      [DataField("params")]
      public int _params = 0;

      public string number = "";

      [ItemType(typeof(OrderWorkType))]
      public List<OrderWorkType> wtypes = null;
     
      [Caption("Бригада", 10.0)]
      public string BrigadeName { get { return brigade == null ? N_A : brigade.Name; } }
      [Caption("Клиент", 15.0)]
      public string ClientName { get { return client == null ? N_A : client.Name; } }
      [Caption("Создана", 17.0)]
      public DateTime Created { get { return created; } }
      [Caption("Содержание", 20.0)]
      public string Text { get { return text; } }
      [Caption("Адрес", 20.0)]
      public string Address { get { return address; } }
      [Caption("Время план", 13.0)]
      public string WorkTimeStr 
      { 
         get 
         {
            return String.Format("{0} - {1}",
               planbegin.ToShortTimeString(), planend.ToShortTimeString());
         }
      }

      public bool Doing { get { return (_params & DOING_PARAMS) == DOING_PARAMS; } }
      public bool Done { get { return (_params & DONE_PARAMS) == DONE_PARAMS; } }
      [Caption("Номер", 15.0)]
      public string Number { get { return number; } }
      public bool Rejected { get { return (_params & REJECTED) == REJECTED; } }
      public bool Missed 
      { 
         get 
         {  
            bool expired = FmMain.DateTimeIsExpired(planbegin.Add(
               new TimeSpan(0, Config.GetConfig().orderMissedInterval, 0)));

            return !Done && !Doing && expired; 
         } 
      }

      public override bool Equals(object obj)
      {
         Order o = obj as Order;

         if (obj != null)
         {
            return brigade.Equals(o.brigade) && created.Equals(o.created);
         }
         else
            return false;
      }

      public override int GetHashCode()
      {
         return base.GetHashCode();
      }
   }

   public class OrderRcv : Order, IOrderItems
   {
      private static readonly DateTime MIN_VALUE = DateTime.Parse("01.01.1601 0:00:00");
      public DateTime factbegin = MIN_VALUE;
      public DateTime factend = MIN_VALUE;
      public double latitude;
      public double longitude;
      public string remark;

      [Caption("Время факт", 13.0)]
      public string FactTimeStr
      {
         get
         {
            if (factbegin == MIN_VALUE ||
               factend == MIN_VALUE)
               return string.Empty;
            else
               return String.Format("{0} - {1}",
                  factbegin.ToShortTimeString(), factend.ToShortTimeString());
         }
      }

      [Caption("Отчет", 20.0)]
      public string Remark { get { return remark; } }

      [ItemType(typeof(OrderItem))]
      public List<OrderItem> items = null;

      #region IOrderItems Members

      public List<OrderItem> Items
      {
         get { return items; }
      }

      #endregion

      internal string ShortInfo
      {
         get
         {
            StringBuilder result = new StringBuilder();

            if(Number.Trim().Length > 0)
               result.Append(Number).Append("\r\n");

            if(Address.Trim().Length > 0)
               result.Append(Address).Append("\r\n");

            if(Text.Trim().Length > 0)
               result.Append(Text).Append("\r\n");

            return result.ToString();
         }
      }
   }

   class Division : DataObject
   {
      public static string OBJECT_NAME = "Division";

      [KeyField]
      public Int32 id;

      public string name = "";

      [ItemType(typeof(DivisionAgent))]
      public List<DivisionAgent> agents = new List<DivisionAgent>();

      public class DivisionAgent : DataObject
      {
         [Reference("Agents", "id")]
         public Brigade brigade = null;

         public string id = "";
      }
   }

   class DivisionManager : DataObject
   {
      public static string OBJECT_NAME = "DivisionManager";

      [KeyField]
      public string login = "";
      public int division;
      public string password = "";
   }

   public class GPSPos : DataObject
   {
      public static string OBJECT_NAME = "GPSPos";

      [Reference("Agents", "userid")]
      public Brigade brigade = null;

      [KeyField]
      public DateTime date = DateTime.Now;
      [Precision(5)]
      public double longitude = 0;
      [Precision(5)]
      public double latitude = 0;
      public double speed = 0;
      public int isGSM = 0;
   }

   public class OrderDel : DataObject
   {
      public static string OBJECT_NAME = "OrderDel";

      [KeyField]
      public DateTime created = DateTime.Now;

      [DataField("params")]
      public int _params = 0;
      public string userid = string.Empty;
   }

   public class Kladr : DataObject
   {
      public static string OBJECT_NAME = "Kladr";

      [KeyField]
      public string code;
      public string name;
      public string socr;
   }

   public class Street : Kladr
   {
      new public static string OBJECT_NAME = "Street";

      public string Socr { get { return socr; } }
      public string Name { get { return name; } }
   }

   public interface IOrderItems
   {
      List<OrderItem> Items { get; }
   }

   public class UserOrder : DataObject, IOrderItems
   {
      public static readonly string OBJECT_NAME = "UserOrder";

      [Reference("Agents", "userid")]
      public Brigade brigade = null;

      [KeyField]
      public DateTime created = DateTime.Now;
      public DateTime date = DateTime.Now;
      public string remark = "";

      [ItemType(typeof(OrderItem))]
      public List<OrderItem> items = null;

      public string number = "";
      public string city = "";
      public string street = "";
      public string house = "";
      public string flat = "";
      public int unread = 0;
      public string userid = "";

      public string BrigadeName { get { return brigade == null ? Order.N_A : brigade.Name; } }
      public DateTime Created { get { return created; } }
      public DateTime Date { get { return date; } }
      public string Remark { get { return remark; } }
      public string Number { get { return number; } }
      public string Address 
      { 
         get 
         { 
            StringBuilder result = new StringBuilder();
            return result.Append(city).Append(" ")
               .Append(street).Append(" ").Append(house)
               .Append(" ").Append(flat).ToString();
         }
      }

      public bool IsMarkAsRead()
      {
         return unread == 1;
      }

      internal void SetAsRead()
      {
         unread = 1;
      }

      #region IOrderItems Members

      public List<OrderItem> Items
      {
         get { return items;  }
      }

      #endregion
   }

   public class Folder : DataObject
   {
      public static string OBJECT_NAME = "Folders";

      [KeyField]
      public string id = "";

      public string parent = "";
      public int type;
      public string name = "";
   }

   public class Warehouse : DataObject
   {
      public static string OBJECT_NAME = "Warehouse";

      [KeyField]
      public string id = "";
      public string folder = "";
      public string name = "";
      public double cost = 0.0;
      public double qty = 0.0;
      public string Name { get { return name; } }
      public string Cost { get { return cost.ToString("0.00"); } }
      public string Qty { get { return qty.ToString(); } }
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

   public class WorkDay : DataObject
   {
      static public readonly string OBJECT_NAME = "WorkDay";

      public DateTime date = DateTime.MinValue;
      public DateTime begin = DateTime.MinValue;
      public DateTime end = DateTime.MinValue;
      public int distance = 0;

      public string Begin
      {
         get
         {
            return begin == new DateTime(2000, 1, 1) ?
               string.Empty : begin.ToString("HH:mm");
         }
      }

      public string End
      {
         get
         {
            return end < new DateTime(2000, 1, 1) ?
               string.Empty : end.ToString("HH:mm");
         }
      }
   }

   public class JobType : DataObject, IComparable
   {
      static public readonly string OBJECT_NAME = "JobType"; 

      [KeyField]
      public Int32 id = -1;

      public string name = "";
      public int color;

      public Color Color
      {
         get
         {
            int b = color & 0xFF;
            int g = (color & 0xFF00) >> 8;
            int r = (color & 0xFF0000) >> 16;
            return Color.FromArgb(r, g, b);
         }

         set
         {
            color = value.ToArgb();
         }
      }

      public string Name
      {
         get { return name; }
      }

      public override string ToString()
      {
         return Name;
      }

      public int CompareTo(object obj)
      {
         return Name.CompareTo(((JobType)obj).Name);
      }
   }

   public class UserLog : DataObject
   {
      static public readonly string OBJECT_NAME = "UserLog";

      [Reference("Agents", "userid")]
      public Brigade agent = null;

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
                  return String.Format("КПК статус: {0}",comments);
               case 1024:
                  return "Работа начата";
               case 1025:
                  return "Работа окончена";
               default:
                  return string.Empty;
            }
         }
      }

      public String TimeStr { get { return date.ToString("HH:mm"); } }
   }

   public class CommonConfig : DataObject
   {
      public static readonly string OBJECT_NAME = "ServerConfig";

      [KeyField]
      public string userid = string.Empty;
      [KeyField]
      public string key = string.Empty;
      public string value = string.Empty;
   }

   public class Caption : Attribute
   {
      private string value;
      private double width;

      public Caption(string value, double width)
      {
         this.value = value;
         this.width = width;
      }

      public string Value { get { return value; } }
      public double Width { get { return width; } }
   }

   public class Schedule : DataObject
   {
      public enum Status { Active = 0, Reserved = 1, Disabled = 2};
      public static readonly string OBJECT_NAME = "Schedule";
      
      [Reference("Agents", "brigade")]
      public Brigade brigade;
      
      public DateTime date = DateTime.Now;
      public int status;

      [ItemType(typeof(ScheduleDistrict))]
      public List<ScheduleDistrict> districts = new List<ScheduleDistrict>();
      
      public string address;
      public double latitude;
      public double longitude;

      public class ScheduleDistrict : DataObject
      {
         [Reference("District", "district")]
         public District district;
         public District District { get { return district;} }
      }

      public string DateStr { get { return date.ToString("dd.MM.yyyy"); } }
      public string StatusStr { get { return StatusToStr((Status)status); } }

      public static string StatusToStr(Status status)
      {
         switch (status)
         {
            case Status.Active:
               return "Работает";
            case Status.Reserved:
               return "В резерве";
            case Status.Disabled:
               return "Не работает";
            default :
               return "Ошибка";
         }
      }

      public bool hasService(District d)
      {
         if (d != null && d.id != null)
            foreach (ScheduleDistrict bd in districts)
            {
               if (bd != null &&
                     bd.district != null && 
                     bd.district.id != null &&
                     bd.district.id.Equals(d.id))
                  return true;
            }

         return false;
      }
   }

   public class Counter : DataObject
   {
      public static readonly string OBJECT_NAME = "Counter";

      [KeyField]
      public string name = string.Empty;

      public string Name { get { return name; } }
   }
}

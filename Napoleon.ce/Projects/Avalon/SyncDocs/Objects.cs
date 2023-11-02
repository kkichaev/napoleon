using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace SyncDocs
{
   public class SyncObjects : DataObject
   {
      public static readonly string OBJECT_NAME = "SyncObjectData";

      public static readonly string AGENTS = "Agents";
      public static readonly string ORGS = "Org";
      public static readonly string PRICE = "Price";

      public string srcId = "";
      public string destId = "";
      public string type = "";
   }

   public class IDNameObject : DataObject, IComparable
   {
      [KeyField]
      public string id = "";
      public string name = "";

      public string ID { get { return id; } }
      public string Name { get { return name; } }


      public override string ToString() { return Name; }

      public int CompareTo(object other)
      {
         return name.CompareTo(((IDNameObject)other).name);
      }
   }

   public class Agent : IDNameObject, IComparable<Agent>
   {
      public static string OBJECT_NAME = "Agents";

      public string login = "";
      public string password = "";

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


   public partial class Org : IDNameObject, IComparable<Org>
   {
      public static string OBJECT_NAME = "Org";
      public static string COMMON_OBJECT_NAME = "CommonOrgs";

      public string address = "";

      public string Address
      {
         get
         {
            return address == null ? "" : address;
         }

      }

      public override bool Equals(object cmp)
      {
         Org org = cmp as Org;
         return (org != null && id.Equals(org.id));
      }

      //Чтобы убрать варнинг
      public override int GetHashCode()
      {
         return base.GetHashCode();
      }

      #region Члены IComparable<Org>

      public int CompareTo(Org other)
      {
         return name.CompareTo(other.name);
      }

      #endregion

      public static Org GetEmpty(string id) { return EmptyOrg.Get(id); }
   }

   public class EmptyOrg : Org
   {
      static Dictionary<string, Org> used = new Dictionary<string, Org>();

      public static Org Get(string id)
      {
         if (!used.ContainsKey(id))
            used[id] = new EmptyOrg(id);
         return used[id];
      }

      EmptyOrg(String id)
      {
         if(id.Length != 0)
            this.name = "Контрагент с кодом <" + id + ">";
         else
            this.name = "";
         this.id = id;
      }
   }


   public partial class Price : IDNameObject, IComparable<Price>
   {
      public static readonly string OBJECT_NAME = "ManagerPrice";

      public double weight = 0;
      public double qty = 0.0;
      public int folderID = 0;

      public string fid = string.Empty;

      public override bool Equals(object obj)
      {
         if (obj is Price)
            return obj == null ? false : id.Equals(((Price)obj).id);
         else
            return false;
      }

      public static Price GetEmpty(string id) { return EmptyPrice.Get(id); }
      public int CompareTo(Price other) { return name.CompareTo(other.name); }
   }

   public class EmptyPrice : Price
   {
      static Dictionary<string, Price> used = new Dictionary<string, Price>();

      public static Price Get(string id)
      {
         if (!used.ContainsKey(id))
            used[id] = new EmptyPrice(id);
         return used[id];
      }

      EmptyPrice(String id)
      {
         this.name = "Товар с кодом <" + id + ">";
         this.id = id;
      }
   }

   public class ItemBase : GRSoft.Network.DataObject
   {
      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;

      public string id = "";
      public int cell = 0;

      public int Cell { get { return cell; } }
      public string Name { get { return id.Length == 0 ? "нет" :
         item != null ? item.Name : "товар с кодом <" + id + ">"; } }

      public virtual double Cost { get { return 0; } }
      public virtual int Qty { get { return 0; } }
      public virtual int Limit { get { return 0; } }
      public virtual int Chek { get { return 0; } }
      public virtual int Load { get { return 0; } }
      public virtual int Unload { get { return 0; } }
   }

   public class BaseDocument : DataObject
   {
      public DateTime date = DateTime.Now;
      public DateTime created = DateTime.MinValue;

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
            return created == DateTime.MinValue ? date : created;
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
      public string Address { get { return Org == null ? "" : Org.Address; } }

      public virtual string Remark { get { return remark; } }
      public string AgentID { get { return agent == null ? string.Empty : agent.id; } }
   }


   public class VandAudit : BaseDocument
   {
      public static readonly String OBJECT_NAME = "Audit";

      [ItemType(typeof(Item))]
      public List<Item> items = new List<Item>();


      public class Item : ItemBase
      {
         public int qty = 0;
         public int limit = 0;
         public double cost = 0;

         public override int Qty { get { return qty; } }
         public override int Limit { get { return limit; } }
         public override double Cost { get { return cost; } }
      }
   }

   public class VandSales : BaseDocument
   {
      public static readonly String OBJECT_NAME = "VandSell";

      [ItemType(typeof(Item))]
      public List<Item> items = new List<Item>();


      public class Item : ItemBase
      {
         public int chek = 0;
         public int load = 0;
         public int unload = 0;
         public double cost = 0;

         public override int Chek { get { return chek; } }
         public override int Load { get { return load; } }
         public override int Unload { get { return unload; } }
         public override double Cost { get { return cost; } }
      }

      public override double Sum()
      {
         double sum = 0;
         foreach (Item i in items)
            sum += i.chek * i.cost;
         return sum;
      }
   }

   public class VandReload : BaseDocument
   {
      public static readonly String OBJECT_NAME = "VandReload";

      [ItemType(typeof(Item))]
      public List<Item> items = new List<Item>();


      public class Item : ItemBase
      {
         public int qty = 0;
         public double cost = 0;

         public override int Qty { get { return qty; } }
         public override double Cost { get { return cost; } }
      }
   }

   class VandRestock : BaseDocument
   {
      public static readonly String OBJECT_NAME = "Restock";

      [ItemType(typeof(Item))]
      public List<Item> items = new List<Item>();

      internal override Org Org { get { return EmptyOrg.Get(""); } }

      public class Item : ItemBase
      {
         public int qty = 0;
         public override int Qty { get { return qty; } }
      }
   }


   public delegate void EmptyParamHandler();
}

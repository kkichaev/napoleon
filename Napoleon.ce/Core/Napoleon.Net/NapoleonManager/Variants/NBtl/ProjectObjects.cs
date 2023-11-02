using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public class VisitPlanFact : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "VisitPlanFact";

      public string userid = "";
      public DateTime date = DateTime.Now;
      [Reference("Agents", "userid")]
      public Agent agent = null;

      public class Item : GRSoft.Network.DataObject
      {
         public string id = "";
         [Reference("Org,CommonOrgs", "id")]
         public Org org = null;

         public int plan = 0;
         public int miss = 0;
         public string comment = "";

         public string Org { get { return org == null ? id : org.name; } }
         public string Address { get { return org == null ? "" : org.Address; } }
         public int Plan { get { return plan; } }
         public int Miss 
         { 
            get { return miss; } 
            set 
            { 
               if(miss < plan) 
                  miss = value; 
            } 
         }
         public int PlanTotal { get { return plan - miss; } }
         public string Comment { get { return comment; } set { comment = value; } }
      }

      public List<Item> items = new List<Item>();
   }

   public partial class DialogUtil
   {
      private const string FIELD_SHOULD_HAVE_VALUE = "поле необходимо заполнить";

      public static void HaveToValueMsg(IWin32Window owner)
      {
         MessageBox.Show(owner, FIELD_SHOULD_HAVE_VALUE, DialogUtil.TITLE_ERR, MessageBoxButtons.OKCancel, MessageBoxIcon.Error);
      }
   }

   public partial class Org
   {
      public string sid = string.Empty;
      public string cid = string.Empty;

      [Reference("Slsnet", "sid")]
      public Slsnet slsnet = null;

      public string Slsnet { get { return slsnet != null ? slsnet.name : sid; } }
      public string City { get { return cid; } }

      public string AddressNew
      {
         get
         {
            return address == null ? "" : cid + ", " + address;
         }
      }

      public String goodsMatrix = "";

      public string GoodsMatrix { get { return goodsMatrix; } set { goodsMatrix = value; } }

      public string nameLPR = string.Empty;
      public string contactsLPR = string.Empty;
      public string bithdayLPR = string.Empty;
      public string visitDays = string.Empty;
      public string responsible = string.Empty;
      public string equipment = string.Empty;
      public string matrixsku = string.Empty;
      public string orderday = string.Empty;
      public string promoplan = string.Empty;
      public string providerNumber = string.Empty;
      public string code = string.Empty;
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

   public class CMonitoring : BaseDocument
   {
      public static readonly string OBJECT_NAME = "CMonitoring";

      public class Item : GRSoft.Network.DataObject
      {
         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price item = null;
         public string id = "";

         public double cost = 0;

         public string Name { get { return item != null ? item.Name : "товар с кодом <" + id + ">"; } }
         public double Cost { get { return cost; } }
      }

      public List<Item> items = new List<Item>();
   }

   public partial class Matrix
   {
      [KeyField]
      public string cdef = string.Empty;

      public override string ToString()
      {
         return name;
      }
   }

   public class SimpleObject : GRSoft.Network.DataObject
   {
      [KeyField]
      public string id = string.Empty;
      public string name = string.Empty;

      public string Name { get { return name; } }

      public override string ToString()  { return Name;  }
   }

   public class GoodsValues : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "GoodsValues";

      public GoodsValues() { }
      
      public GoodsValues(Org o)
      {
         org = o;
         id = o.id;
      }

      [KeyField]
      public string id = string.Empty;

      [Reference("Org,CommonOrgs", "id")]
      public Org org = null;

      public List<Item> items = new List<Item>();
      
      public class Item : GRSoft.Network.DataObject, IComparable<Item>
      {
         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price price = null;

         public string id = "";
         public int redLine = 0;
         public int greenLine = 0;

         public string Name { get { return price.Name; } }

         public int RedLine { get { return redLine; } set { redLine = value; } }
         public int GreenLine { get { return greenLine; } set { greenLine = value; } }

         public int CompareTo(Item other)
         {
            return Name.CompareTo(other.Name);
         }
      }
   }

   public partial class Slsnet : SimpleObject
   {
      public static readonly string OBJECT_NAME = "Slsnet";

      public int plan = 0;

      public int Plan { get { return plan; } }
      public string ID { get { return id; } set { id = value; } }
   }

   public class City : SimpleObject
   {
      public static readonly string OBJECT_NAME = "City";
   }

   public class OrgAssign : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "OrgAssign";

      [KeyField]
      public string id = string.Empty;
      [Reference("Agents", "id")]
      public Agent agent = null;
      public List<OrgAssignItem> items = null;
   }

   public class OrgAssignItem : GRSoft.Network.DataObject
   {
      public string id = string.Empty;
      [Reference("Org", "id")]
      public Org org = null;
   }

   public class ScriptAssign : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "ScrAssign";

      [KeyField]
      public string id = string.Empty;
      [Reference("Slsnet", "id")]
      public Slsnet slsnet = null;
      public List<ScriptAssignmentItem> items = null;
   }

   public class ScriptAssignmentItem : GRSoft.Network.DataObject
   {
      public int id = -1;
      [Reference("ScriptDef", "id")]
      public ScriptDef script = null;
   }

   public partial class ScriptDef
   {
      public string cdefid = string.Empty;
   }

   public partial class OrgFolderItem
   {
      public class ScriptItem : GRSoft.Network.DataObject
      {
         public int id = 0;
      }
      public List<ScriptItem> scripts = new List<ScriptItem>();
      public string itemId = "";
   }

   public class ContractDef : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "ContractDef";
      private static readonly string DATE_FMT = "dd/MM/yyyy";

      [KeyField]
      public string id = string.Empty;
      public DateTime start = DateTime.MinValue;
      public DateTime finish = DateTime.MinValue;
      public string name = string.Empty;
      public List<ContractIDeftem> items = null;
      public byte[] photo = null;
      public List<ContractOrgImg> orgImg = null;

      public String Name { get { return name; } }
      public String Start { get { return start.ToString(DATE_FMT); } }
      public String Finish { get { return finish.ToString(DATE_FMT);} }
      public Image Photo 
      { 
         get 
         {
            Bitmap result = null;

            if (photo != null)
               using (Stream stream = new MemoryStream(photo))
                  result = new Bitmap(stream);

            return result;
         } 
      } 

      public override string ToString()
      {
         return string.Format("{0} ({1} - {2})", Name, Start, Finish);
      }
   }

   public class ContractOrgImg : GRSoft.Network.DataObject
   {
      public byte[] photo = null;
      public string id = string.Empty;
      public string href = "";
      public string name = "";

      public string Name { get { return name; } set { name = value; } }
      public string HRef { get { return href; } set { href = value; } }
   }

   public class ContractIDeftem : GRSoft.Network.DataObject
   {
      public string id = string.Empty;

      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;
   }

   public class Contract : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Contract";

      [ItemType(typeof(ContractItem))]
      public List<ContractItem> items = new List<ContractItem>();

      public Contract Clone()
      {
         Contract res = (Contract)MemberwiseClone();
         List<ContractItem> items = new List<ContractItem>();
         this.items.ForEach((x) => items.Add(x.Clone()));
         res.items = items;
         return res;
      }
   }

   public class ContractItem : GRSoft.Network.DataObject
   {      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price item = null;

      public string id = string.Empty;
      public double qty = 0.0;
      public double face = 0.0;

      public ContractItem Clone() { return (ContractItem)MemberwiseClone();  }

      public string Name { get { return item.Name; } }
      public double Qty { get { return qty; } }
   }

   public partial class Price : GRSoft.Network.DataObject
   {
      public static readonly String GOODS_FILTER = "\"isGoods\"=1 and \"rem\"=0";
      public string cdef = string.Empty;
      public int my = 0;
      public string group = string.Empty;
      public int isGoods = 0;
      public int rem = 0;
      public string categ = string.Empty;
      public string product = string.Empty;
      public string code = string.Empty;
      public string barcode = string.Empty;

      public string ID { get { return id; } set { id = value; } }
   }

   public class PartShelf : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "PartShelf";

      public string cid = string.Empty;
      public string sid = string.Empty;
      public double part = 0.0;
   }

   public partial class ReturnItem
   {
      public string comment = string.Empty;
   }

   public partial class Visit
   {
      [Reference("ContractDef", "def", typeof(ContractDef))]
      public ContractDef contract = null;
      public string def = "";
   }

   public class ReturnCause : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "ReturnCause";
      [KeyField]
      public string id = string.Empty;

      public string agent = string.Empty;
      public string report = string.Empty;
      public int notprint = 0;

      public string Agent { get { return agent; } set { agent = value; } }
      public string Report { get { return report; } set { report = value; } }
      public bool NotPrint { get { return notprint >= 1; } set { notprint = value ? 1 : 0; } }
   }

   public partial class BtlPlan : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "BtlPlan";

      public string id = string.Empty;
      public string cid = string.Empty;

      public double face = 0.0;

      [ItemType(typeof(BtlPlanItem))]
      public List<BtlPlanItem> items = new List<BtlPlanItem>();

      public partial class BtlPlanItem : GRSoft.Network.DataObject
      {
         public string id = string.Empty;
         public double face = 0.0;
      }
   }

   public partial class GroupGoods : Price
   {
      public static new readonly string OBJECT_NAME = "GroupGoods";
   }

   public class GoodsMatrix : Matrix
   {
      public static new readonly string OBJECT_NAME = "GoodsMatrix";

      public string Name { get { return name; } }
   }

   public class AgentPlan : GRSoft.Network.DataObject
   {
      public static readonly string OBJECT_NAME = "AgentPlan";

      [KeyField]
      public string id = string.Empty;
      public double plan = 0.0;

      [Reference("Agents", "id")]
      public Agent agent = null;

      public string Agent { get { return agent.Name; } }
      public double Plan { get { return plan; } set { plan = value; } }
   }

   public class ReturnOnDelivery : Returns
   {
      public static readonly new string OBJECT_NAME = "ReturnOnDelivery";
   }

   public class Distrib : BaseDocument
   {
      public static readonly string OBJECT_NAME = "Distrib";

      public Distrib Clone()
      {
         Distrib res = (Distrib)MemberwiseClone();
         res.org = null;
         List<Item> items = new List<Item>();
         this.items.ForEach((x) => items.Add(x.Clone()));
         res.items = items;

         return res;
      }

      public class Item : Network.DataObject
      {
         public String id = "";

         [Reference("ManagerPrice", "id", typeof(Price))]
         public Price item = null;
         public int exist = 0;

         public Item Clone() { return (Item)MemberwiseClone();  }

         public string Name { get { return item == null ? "Товар с кодом <" + id + ">" : item.Name; } }
         public int Exists 
         { 
            get { return exist; }
            set 
            { 
               exist = value; 
            } 
         }
      }

      [ItemType(typeof(Item))]
      public List<Item> items = new List<Item>();
   }

   public partial class QuestionItemValue
   {
      public String value2 = "";
   }

   public class NBTLViewer : Network.DataObject
   {
      public static readonly string OBJECT_NAME = "NBTLViewer";

      [KeyField]
      public string id = "";

      public string name = "";

      public string password = "";
      public int division = 0;

      public class Item : Network.DataObject
      {
         public string id = "";
      }

      public List<Item> contracts = new List<Item>();
   }

   public partial class Visit
   {
      public partial class VisitItem
      {
         public VisitItem Clone()
         {
            return (VisitItem)MemberwiseClone();
         }

         public byte[] smallPhoto;
      }

      public Visit Clone()
      {
         Visit res = (Visit)MemberwiseClone();
         res.org = null;
         List<Visit.VisitItem> items = new List<VisitItem>();
         this.items.ForEach((x) => items.Add(x.Clone()));
         res.items = items;

         return res;
      }
   }

   public partial class Answer
   {
      public Answer Clone()
      {
         Answer res = (Answer)MemberwiseClone();
         res.org = null;
         List<AnswerItem> items = new List<AnswerItem>();
         this.items.ForEach((x) => items.Add(x.Clone()));
         res.items = items;
         return res;
      }
   }

   public partial class OrgRemnants
   {
      public OrgRemnants Clone()
      {
         OrgRemnants res = (OrgRemnants)MemberwiseClone();
         res.org = null;
         List<OrgRemnantsItem> items = new List<OrgRemnantsItem>();
         this.items.ForEach((x) => items.Add(x.Clone()));
         res.items = items;
         return res;
      }
   }

   public partial class AnswerItem
   {
      public AnswerItem Clone()
      {
         return (AnswerItem)MemberwiseClone();
      }
   }

   public partial class OrgRemnantsItem
   {
      public OrgRemnantsItem Clone()
      {
         return (OrgRemnantsItem)MemberwiseClone();
      }
   }


   public class VisitItemDoc : Visit.VisitItem
   {
      public readonly static string OBJECT_NAME = "VisitItemDoc";

      public string __nameBase = string.Empty;
      public DateTime __date = DateTime.MinValue;
   }

   public class PLU : Network.DataObject
   {
      public static readonly string OBJECT_NAME = "PLU";

      [KeyField]
      public string item = "";
      [KeyField]
      public string sls = "";
      [KeyField]
      public string code = "";

      public string Item { get { return item; } set { item = value; } }
      public string SLS { get { return sls; } set { sls = value; } }
      public string Code { get { return code; } set { code = value; } }
   }

   public partial class QuestionItem
   {
      public string altText = string.Empty;
      public string clients = string.Empty;
   }
}

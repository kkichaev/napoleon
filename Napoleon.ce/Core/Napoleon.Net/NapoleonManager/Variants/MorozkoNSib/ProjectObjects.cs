using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
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

   public class Slsnet : SimpleObject
   {
      public static readonly string OBJECT_NAME = "Slsnet";
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
      public override string ToString() { return Name; }
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
      public Image Photo { get 
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

   public partial class Price : GRSoft.Network.DataObject
   {
      public string cdef = string.Empty;
      public int my = 0;
      public string group = string.Empty;
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
   }

   public partial class ScriptDef
   {
      public string cdefid = string.Empty;
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

      [KeyField]
      public string id = string.Empty;
      public double face = 0.0;
   }
}

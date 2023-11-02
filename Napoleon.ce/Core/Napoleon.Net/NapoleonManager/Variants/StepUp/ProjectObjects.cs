using GRSoft.Network;
using System;
using System.Collections.Generic;
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

      [Reference("City", "cid")]
      public City city = null;

      public string Slsnet { get { return slsnet != null ? slsnet.name : sid; }}
      public string City { get { return city != null ? city.name : cid; } }
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

      public String Name { get { return name; } }
      public String Start { get { return start.ToString(DATE_FMT); } }
      public String Finish { get { return finish.ToString(DATE_FMT);} }

      public override string ToString()
      {
         return string.Format("{0} ({1} - {2})", Name, Start, Finish);
      }
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
      public double Face { get { return face; } }
   }

}

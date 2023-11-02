using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class ContractParams : UserControl
   {
      private DataSet<string, ContractDef> dsContract;
      SimpleDataSet<OrgMatrix> dsOrgMatrix;
      private DataSet<string, Price> dsPrice;


      public ContractParams()
      {
         InitializeComponent();
         dsContract = (DataSet<string, ContractDef>)DataModule.Get(ContractDef.OBJECT_NAME) ?? 
            new DataSet<string, ContractDef>(ContractDef.OBJECT_NAME);
         dsOrgMatrix = new SimpleDataSet<OrgMatrix>(OrgMatrix.OBJECT_NAME, false);
         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);

#if NbtlMonitor
         ShowMatrix(false);
#endif
      }

      public void ShowMatrix(bool show)
      {
         if (label3.Visible != show)
         {
            label3.Visible = show;
            lbMatrix.Visible = show;
            label4.Visible = show;
            lbItems.Visible = show;
            cbPhoto.Visible = show;

            Rectangle b = lbContracts.Bounds;
            b.Height = show ? label3.Top - lbContracts.Top - 3 : lbItems.Bottom - lbContracts.Top;
            lbContracts.Bounds = b;
         }
      }


      public void Update(List<IDataSet> upd, DateTime start, DateTime finish)
      {
         string CONTRACT_FILTER = "\"start\" <= ToDate('{0:dd/MM/yyyy}') and \"finish\" >= ToDate('{1:dd/MM/yyyy}')";

#if NbtlMonitor
         CONTRACT_FILTER += " and id in (" + ((MainFormEx)MainForm.Instance).ViewerContracts() + ")";
#endif

         dsContract.Filter = string.Format(CONTRACT_FILTER, finish.AddDays(1), start);
         dsOrgMatrix.Filter = "\"cdef\" in (select \"id\" from contractdef c where " + dsContract.Filter + ")";

         if (dsPrice.Count == 0)
            upd.Add(dsPrice);
         
         upd.Add(dsContract);
         upd.Add(dsOrgMatrix);
      }

      public void DataLoaded()
      {
         List<ContractDef> list = new List<ContractDef>();
         list.AddRange(dsContract.Values);
         list.Sort((lhs, rhs) => { return lhs.start.CompareTo(rhs.start); });
         lbContracts.Items.Clear();
         lbContracts.Items.AddRange(list.ToArray());

         if(lbContracts.Items.Count > 0)
            lbContracts.SelectedIndex = 0;

      }

      void UpdateItems(ContractDef cd)
      {
         lbItems.Items.Add("<Все>");

         foreach (ContractIDeftem ci in cd.items)
         {
            if (ci.item != null && ci.item.my == 1)
               lbItems.Items.Add(ci.item.name);
         }

         lbItems.SelectedIndex = 0;
      }

      void OnContractChaned(ContractDef cd)
      {
         lbMatrix.Items.Clear();
         lbItems.Items.Clear();

         Dictionary<string, bool> have = new Dictionary<string, bool>();
         lbMatrix.Items.Add("<Без матрицы>");
         foreach (OrgMatrix om in dsOrgMatrix.Data)
            if (om.cdef != cd.id || have.ContainsKey(om.name))
               continue;
            else
            {
               have[om.name] = true;
               lbMatrix.Items.Add(om.name);
            }

         lbMatrix.SelectedIndex = 0;
         UpdateItems(cd);

      }

      private void lbContracts_SelectedIndexChanged(object sender, EventArgs e)
      {
         ContractDef cd = lbContracts.SelectedItem as ContractDef;
         if (cd == null)
            return;

         OnContractChaned(cd);
      }
   }
}

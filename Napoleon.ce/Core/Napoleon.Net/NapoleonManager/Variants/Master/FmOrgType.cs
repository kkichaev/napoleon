using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgType : Form
   {
      private DataSet<string, OrgType> dsOrgType;
      private DataSet<string, OrgType> dsDelOrgType;
      
      private BindingList<OrgType> data = new BindingList<OrgType>();

      public FmOrgType()
      {
         InitializeComponent();
         grid.AutoGenerateColumns = false;
         dsOrgType = (DataSet<string, OrgType>)DataModule.Get(OrgType.OBJECT_NAME) ?? new DataSet<string, OrgType>(OrgType.OBJECT_NAME);
         grid.DataSource = data;
         data.ListChanged += (s, e) => { btnSave.Enabled = true; };
         dsDelOrgType = new DataSet<string, OrgType>(OrgType.OBJECT_NAME, false);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsOrgType);

         FmWait.StdDataRefresh(this, upd, doLoadData);
      }

      private void doLoadData() 
      {
         data.Clear();

         foreach (OrgType ot in dsOrgType.Data)
            data.Add(ot);

         btnSave.Enabled = false;
         dsDelOrgType.Clear();
      }

      private void btnNew_Click(object sender, EventArgs e)
      {
         FmInputDlg dlg = new FmInputDlg();

         if (dlg.ShowDialog() == DialogResult.OK)
            data.Add(createOrgType(dlg.Value));
      }

      private void FmOrgType_Load(object sender, EventArgs e)
      {
         btnRefresh_Click(btnRefresh, EventArgs.Empty);
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (DialogUtil.AskToDel(this))
         {
            foreach (DataGridViewRow r in grid.SelectedRows)
            {
               OrgType ot = (OrgType)r.DataBoundItem;
               dsDelOrgType.Add(ot.id, ot);
               data.Remove(ot);
            }
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         FmInputDlg dlg = new FmInputDlg();
         
         if (grid.SelectedRows.Count > 0)
         {
            DataGridViewRow r = grid.SelectedRows[0];
            OrgType ot = (OrgType)r.DataBoundItem;
            dlg.Value = ot.name;

            if (dlg.ShowDialog() == DialogResult.OK) 
            { 
               ot.name = dlg.Value;
               btnSave.Enabled = true;
            }
         }
      }

      private OrgType createOrgType(string name)
      {
         OrgType result = new OrgType();
         result.id = GRSoft.Network.DataObject.GenId();
         result.name = name;

         return result;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> remSet = new List<IDataSet>();

         DataSet<string, OrgType> wrds = new DataSet<string, OrgType>(OrgType.OBJECT_NAME, false);

         foreach (OrgType ot in data)
            wrds.Add(ot.id, ot);

         wrSet.Add(wrds);
         remSet.Add(dsDelOrgType);

         DataModule.UpdateDataSet(wrSet, remSet, null, Config.GetConfig().GetConnection());
      }

      private void FmOrgType_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            btnSave.PerformClick();
      }
   }
}

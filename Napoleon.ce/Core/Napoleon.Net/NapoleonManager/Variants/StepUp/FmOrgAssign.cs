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
   public partial class FmOrgAssign : Form
   {
      private DataSet<string, Org> dsOrg;
      private DataSet<string, OrgAssign> dsOrgAssignment;

      public FmOrgAssign()
      {
         InitializeComponent();
         dgvOrg.AutoGenerateColumns = false;

         dsOrg = (DataSet<string, Org>) DataModule.Get(Org.OBJECT_NAME) ?? new DataSet<string, Org>(Org.OBJECT_NAME);
         dsOrgAssignment = (DataSet<string, OrgAssign>)DataModule.Get(OrgAssign.OBJECT_NAME) ?? new DataSet<string, OrgAssign>(OrgAssign.OBJECT_NAME);

         LoadAgents();
         DoLoadData();

         btnSave.Enabled = false;
      }

      private void LoadAgents()
      {
         List<Agent> list = new List<Agent>();
         Manager mc = CurrentUser.user as Manager;

         if (mc != null)
         {
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;

               list.Add(da.agent);
            }
         }

         list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });
         cbAgents.Items.AddRange(list.ToArray());
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsOrg);
         dsOrg.Filter = "\"id\" is null or \"id\" is not null";
         upd.Add(dsOrgAssignment);
         FmWait.StdDataRefresh(this, upd, DoLoadData, btnRefresh);
       }

      private void DoLoadData()
      {
         DoLoadData(string.Empty);
      }

      private void DoLoadData(string filter)
      {
         List<Org> list = new List<Org>();

         foreach (Org o in dsOrg.Values)
         {
            if (filter.Length == 0 || o.Name.ToUpper().Contains(filter.ToUpper()))
               list.Add(o);
         }

         list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });

         dgvOrg.DataSource = list;
      }

      private void FmOrgAssign_Load(object sender, EventArgs e)
      {
         if (dsOrgAssignment.Count == 0)
            btnRefresh.PerformClick();
      }

      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         lbAgentOrgs.Items.Clear();

         ToolStripComboBox cb = (ToolStripComboBox)sender;
         OrgAssign ass = GetCurAssignment();
         if (ass != null) 
         {
            if (ass.items != null)
            {
               List<Org> list = new List<Org>();
               List<OrgAssignItem> needRemove = new List<OrgAssignItem>();
               foreach (OrgAssignItem i in ass.items)
                  if (i.org != null)
                     list.Add(i.org);
                  else
                     needRemove.Add(i);

               ass.items.RemoveAll((x) => needRemove.Contains(x));

               list.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });
               lbAgentOrgs.Items.AddRange(list.ToArray());
            }
         }

         dgvOrg.Refresh();
      }

      private OrgAssign GetCurAssignment()
      {
         return GetCurAssignment(false);
      }

      private OrgAssign GetCurAssignment(bool init)
      {
         OrgAssign result = null;
         Agent selAgent = cbAgents.SelectedItem as Agent;

         if (selAgent != null && dsOrgAssignment.ContainsKey(selAgent.id))
            result = dsOrgAssignment[selAgent.id];

         if (init && result == null && selAgent != null) 
         {
            result = new OrgAssign();
            result.id = selAgent.id;
            result.agent = selAgent;
            result.items = new List<OrgAssignItem>();

            dsOrgAssignment.Add(result.id, result);
         }

         return result;
      }

      private void dgvOrg_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         
         if (e.RowIndex >= 0)
         {
            Org o = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as Org;
            OrgAssign oa = GetCurAssignment(true);
            
            if (o != null && oa != null && !OrgIsAssign(oa, o.id))
            {
               lbAgentOrgs.Items.Add(o);

               OrgAssignItem i = new OrgAssignItem();
               i.id = o.id;
               i.org = o;
               oa.items.Add(i);

               btnSave.Enabled = true;
            }
         }
      }

      private bool OrgIsAssign(OrgAssign oa, string id)
      {
         bool result = false;
         foreach (OrgAssignItem i in oa.items)
         {
            if (i.id.Equals(id))
            {
               result = true;
               break;
            }
         }

         return result;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         if (Save())
            btnSave.Enabled = false;
         else
            DialogUtil.UpdateErrMsg(this);
      }

      private bool Save()
      {
         DataSet<string, OrgAssign> dsRem = new DataSet<string, OrgAssign>(OrgAssign.OBJECT_NAME, false);
         DataSet<string, OrgAssign> dsWr = new DataSet<string, OrgAssign>(OrgAssign.OBJECT_NAME, false);

         foreach (OrgAssign oa in dsOrgAssignment.Values)
         {
            if (oa.items.Count > 0)
               dsWr.Add(oa.id, oa);
            else
               dsRem.Add(oa.id, oa);
         }

         List<IDataSet> wrSet = new List<IDataSet>();
         List<IDataSet> rmSet = new List<IDataSet>();

         if (dsWr.Count > 0)
            wrSet.Add(dsWr);

         if (dsRem.Count > 0)
            rmSet.Add(dsRem);

         return DataModule.UpdateDataSet(wrSet, rmSet, null, Config.GetConfig().GetConnection());
      }

      private void lbAgentOrgs_DoubleClick(object sender, EventArgs e)
      {
         Org org = ((ListBox)sender).SelectedItem as Org;
         OrgAssign oa = GetCurAssignment();

         if (org != null && oa != null)
         {
            foreach (OrgAssignItem i in oa.items)
            {
               if (i.id.Equals(org.id))
               {
                  oa.items.Remove(i);
                  break;
               }
            }

            lbAgentOrgs.Items.Remove(org);
            btnSave.Enabled = true;
         }
      }

      private void edFilter_TextChanged(object sender, EventArgs e)
      {
         DoLoadData(((ToolStripTextBox)sender).Text.Trim()) ;
      }

      private void btnClearFilter_Click(object sender, EventArgs e)
      {
         edFilter.Text = string.Empty;
      }

      private void dgvOrg_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         Org o = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as Org;
         OrgAssign oa = GetCurAssignment();

         if (o != null && oa != null && OrgIsAssign(oa, o.id))
            e.CellStyle.BackColor = Color.Gray;
         else
            e.CellStyle.BackColor = Color.White;
      }

      private void btnOrg_Click(object sender, EventArgs e)
      {
         new FmOrg().Show();
      }
   }
}

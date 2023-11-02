using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class Users : Form
   {
      DataSet<string, Agent> dsAgent;
      DataSet<string, Agent> dsDelAgent;
      DataSet<int, Division> dsDivision;
      DataSet<string, LicensedUser> dsLisensedUsers;
      private DataSet<string, LicenseCountEx> dsLicenseCountEx;

      public Users()
      {
         InitializeComponent();
         dsAgent = (DataSet<string, Agent>)DataModule.Get(Agent.OBJECT_NAME) ??
            new DataSet<string, Agent>(Agent.OBJECT_NAME);
         dsDelAgent = new DataSet<string, Agent>(Agent.OBJECT_NAME, false);
         dsDivision = (DataSet<int, Division>)DataModule.Get(Division.OBJECT_NAME) ??
            new DataSet<int, Division>(Division.OBJECT_NAME);
         dsLisensedUsers = new DataSet<string, LicensedUser>(LicensedUser.OBJECT_NAME) ??
            new DataSet<string, LicensedUser>(LicensedUser.OBJECT_NAME);
         dsLicenseCountEx = (DataSet<string, LicenseCountEx>)DataModule.Get(LicenseCountEx.OBEJCT_NAME) ?? 
            new DataSet<string, LicenseCountEx>(LicenseCountEx.OBEJCT_NAME);
         dgvUsers.DataSource = new AgentBindingList();
         btnSave.Enabled = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsAgent);
         list.Add(dsDivision);
         list.Add(dsLisensedUsers);
         list.Add(dsLicenseCountEx);

         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);
         
         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               list, FmWait.ProgressIndicator));
      }

      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();

         this.Invoke(new InvokeDelegate(delegate
         {
            FmWait.CloseForm();

            ReloadData();
            btnRefresh.Enabled = true;
         }));
      }

      private void DataConnectionError(EDataResponse e)
      {
         Utils.DataConnectionError(this, btnRefresh, e.Msg);
      }

      private void ReloadData()
      {
         BindingList<Agent> list = (BindingList<Agent>)dgvUsers.DataSource;

         list.Clear();

         foreach (Agent a in dsAgent.Values)
         {
            if(dsLisensedUsers.ContainsKey(a.id))
               a.license = true;

            list.Add(a);
         }

         dgvUsers.Sort(dgvUsers.Columns[dgvUsersName.Name], ListSortDirection.Ascending);
         UpdateStatusLabel();
      }

      private void UpdateStatusLabel()
      {
         int used = 0;

         foreach(Agent a in (AgentBindingList)dgvUsers.DataSource)
            if(a.license)
               used++;

         int cnt = 0;

         if (dsLicenseCountEx.ContainsKey(LicensedUsers.ADSLIGHT.Type))
            cnt = dsLicenseCountEx[LicensedUsers.ADSLIGHT.Type].count;
         label.Text = "Всего : " + cnt + " / " + used; 
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         EditUser editUser = new EditUser();

         if (editUser.ShowDialog() == DialogResult.OK)
         {
            Agent a = new Agent();
            a.id = Utils.GUID;
            a.name = editUser.AgentName;
            a.login = editUser.AgentLogin;
            a.password = editUser.AgentPwd;

            ((AgentBindingList)dgvUsers.DataSource).Add(a);
            dsAgent.Add(a.id, a);

            if (dsDivision.ContainsKey(SelectedDivisionId))
            {
               Division d = dsDivision[SelectedDivisionId];
               Division.DivisionAgent da = new Division.DivisionAgent();
               da.agent = a;
               da.id = a.id;
               d.agents.Add(da);
            }

            btnSave.Enabled = true;
         }
      }

      private int SelectedDivisionId
      {
         get
         {
            const int TOP_LEVEL_DIVISION_ID = 1;
            return TOP_LEVEL_DIVISION_ID; 
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         List<IDataSet> rem = new List<IDataSet>();

         if (dsAgent.Count > 0)
            upd.Add(dsAgent);

         upd.Add(dsDivision);

         if(dsLisensedUsers.Count > 0)
            upd.Add(dsLisensedUsers);

         if (dsDelAgent.Count > 0)
            rem.Add(dsDelAgent);

         DataModule.UpdateDataSet(upd, rem, null, Config.GetConfig().GetConnection());

         btnSave.Enabled = false;

      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (Utils.AskToApplyDelete() == DialogResult.OK)
         {
            DataGridViewRow row = dgvUsers.CurrentRow;
            Agent a = row.DataBoundItem as Agent;

            if (a != null)
               dsDelAgent.Add(a.id, a);

            dgvUsers.Rows.Remove(row);
            btnSave.Enabled = true;

            if (dsDivision.ContainsKey(SelectedDivisionId))
            {
               Division d = dsDivision[SelectedDivisionId];

               foreach (Division.DivisionAgent da in d.agents)
                  if(da.id.Equals(a.id))
                  {
                     d.agents.Remove(da);
                     break;
                  }
            }
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         EditUser editUser = new EditUser();
         DataGridViewRow row = dgvUsers.CurrentRow;

         if (row != null)
         {
            Agent a = row.DataBoundItem as Agent;

            if ( a != null)
            {
               editUser.AgentName = a.name;
               editUser.AgentLogin = a.login;
               editUser.AgentPwd = a.password;

               if (editUser.ShowDialog() == DialogResult.OK)
               {
                  a.name = editUser.AgentName;
                  a.login = editUser.AgentLogin;
                  a.password = editUser.AgentPwd;

                  btnSave.Enabled = true;
               }
            }
         }
      }

      private void Users_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && Utils.AskToSaveChangingData() == DialogResult.OK)
            btnSave_Click(btnSave, EventArgs.Empty);
      }

      private void dgvUsers_CellContentClick(object sender, DataGridViewCellEventArgs e)
      {
         if (e.ColumnIndex == dgvUsers.Columns[dgvUsersLicence.Name].Index)
         {
            dgvUsers.CommitEdit(DataGridViewDataErrorContexts.Commit);
            Agent a = dgvUsers.Rows[e.RowIndex].DataBoundItem as Agent;

            if (a != null)
            {
               LicensedUser lu = new LicensedUser();
               lu.id = a.id;
               lu.agent = a;

               if (a.license == true && !dsLisensedUsers.ContainsKey(lu.id))
                  dsLisensedUsers.Add(lu.id, lu);
               else if (a.license == false && dsLisensedUsers.ContainsKey(lu.id))
                  dsLisensedUsers.Remove(lu.id);
            }

            btnSave.Enabled = true;
         }
      }

      private void Users_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void dgvUsers_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         DataGridView grid = (DataGridView)sender;
         if (grid.IsCurrentCellDirty)
         {
            grid.CommitEdit(DataGridViewDataErrorContexts.Commit);
            btnSave.Enabled = true;
         }
      }
   }

   class AgentBindingList : SortBindingList<Agent>  { }
}

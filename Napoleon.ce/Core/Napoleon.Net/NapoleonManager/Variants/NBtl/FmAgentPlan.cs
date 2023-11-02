using GRSoft.NapoleonManager.Utils;
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
   public partial class FmAgentPlan : Form
   {
      private Agents dsAgents;
      private DataSet<string, AgentPlan> dsPlan;
      private DataSet<string, AgentPlan> dsPlanChanged;

      public FmAgentPlan()
      {
         InitializeComponent();

         dsAgents = CurrentUser.user.GetAgents();
         dsPlan = (DataSet<string, AgentPlan>)DataModule.Get(AgentPlan.OBJECT_NAME) ?? new DataSet<string, AgentPlan>(AgentPlan.OBJECT_NAME);
         dsPlanChanged = new DataSet<string, AgentPlan>(AgentPlan.OBJECT_NAME, false);
         btnSave.Enabled = false;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsAgents);
         list.Add(dsPlan);

         FmWait.StdDataRefresh(this, list, DoLoadData);
      }

      private void DoLoadData()
      {
         Manager m = CurrentUser.user as Manager;
         List<AgentPlan> data = new List<AgentPlan>();

         if (m != null) 
         {
            Agents aa = m.GetAgents();

            foreach (Agent a in aa.Values)
            {
               if (dsPlan.ContainsKey(a.id))
                  data.Add(dsPlan[a.id]);
               else
               {
                  AgentPlan ap = new AgentPlan();
                  ap.id = a.id;
                  ap.agent = a;
                  ap.plan = 0.0;

                  data.Add(ap);
               }
            }
         }

         data.Sort((x,y)=>{return x.agent.Name.CompareTo(y.agent.Name);});
         grid.DataSource = data;
      }

      private void FmAgentPlan_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void grid_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         if (e.RowIndex >= 0)
         {
            AgentPlan ap = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as AgentPlan;

            if (ap != null)
            {
               dsPlanChanged[ap.id] = ap;
               btnSave.Enabled = true;
            }
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wr = new List<IDataSet>();
         wr.Add(dsPlanChanged);
         if (DataModule.WriteDataSet(wr, Config.GetConfig().GetConnection()))
         {
            dsPlanChanged.Clear();
            btnSave.Enabled = false;
         }
         else
            DialogUtil.UpdateErrMsg(this);
      }
   }
}

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
   public partial class FmTaskList : Form
   {
      static FmTaskList instance = null;

      SimpleDataSet<NapoleonTask> dsTask;
      SimpleDataSet<NapoleonTaskResponse> dsResponce;

      List<Agent> agents = new List<Agent>();

      public FmTaskList()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;

         dsResponce = new SimpleDataSet<NapoleonTaskResponse>(NapoleonTaskResponse.OBJECT_NAME, false);
         dsTask = new SimpleDataSet<NapoleonTask>(NapoleonTask.OBJECT_NAME, false);
      }

      public static void Open(DateTime start, DateTime end)
      {
         if (instance == null)
         {
            instance = new FmTaskList();
            instance.dtpStart.Value = start;
            instance.dtpEnd.Value = end;
            instance.Show();
         }
         else
         {
            instance.dtpStart.Value = start;
            instance.dtpEnd.Value = end;
            instance.RefreshData();
            instance.BringToFront();
         }
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      protected override void OnKeyDown(KeyEventArgs e)
      {
         if( e.KeyCode == Keys.F5 && e.Modifiers == 0 )
         {
            RefreshData();
            return;
         }
         base.OnKeyDown(e);
      }
   
      void RefreshData()
      {
         if (agents.Count == 0)
         {
            foreach(Agent a in (CurrentUser.user as Manager).GetAgents().Data)
               agents.Add(a);
         }

         string agentFilter = DataUtils.MakeFilterFromAgents(null, agents);
         string filter = agentFilter + String.Format(" and \"end\" >= ToDate('{0:dd/MM/yyyy}') and \"end\" < ToDate('{1:dd/MM/yyyy}')", dtpStart.Value, dtpEnd.Value.AddDays(1));
         dsTask.Filter = filter;

         dsResponce.Filter = "\"id\" in (select \"id\" from \"NapoleonTask\" where " + filter + ")";

         List<IDataSet> updSets = new List<IDataSet>();
         updSets.Add(dsTask);
         updSets.Add(dsResponce);
         FmWait.StdDataRefresh(this, updSets, DoLoadData, btnRefresh);
      }

      void DoLoadData()
      {
         Dictionary<string, bool> answered = new Dictionary<string, bool>();
         foreach (NapoleonTaskResponse tr in dsResponce.Data)
            answered[tr.id] = true;

         List<DataItem> data = new List<DataItem>();
         foreach (Agent a in agents)
            data.Add(new DataItem(a, dsTask, answered));

         data.Sort();
         dgvItems.DataSource = data;
      }


      class DataItem : IComparable<DataItem>
      {
         bool check = false;
         Agent agent;
         int done = 0;
         int active = 0;

         public DataItem(NapoleonManager.Agent a, SimpleDataSet<NapoleonTask> dsTask, Dictionary<string, bool> answered)
         {
            this.agent = a;

            foreach(NapoleonTask nt in dsTask.Data)
               if( nt.userid == a.id )
               {
                  if (answered.ContainsKey(nt.id))
                     done++;
                  else
                     active++;
               }
         }

         public bool Checked { get { return check; } set { check = value; } }
         public string Name { get { return agent.Name; } }
         public int Done { get { return done; } }
         public int Active { get { return active; } }

         public int CompareTo(DataItem other)
         {
            return Name.CompareTo(other.Name);
         }

         public Agent Agent { get { return agent; } }
      }

      private void dgvItems_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         DataItem di = dgvItems.Rows[e.RowIndex].DataBoundItem as DataItem;
         e.CellStyle.BackColor = di.Checked ? Color.LightGray : dgvItems.DefaultCellStyle.BackColor;
      }

      private void btnTask_Click(object sender, EventArgs e)
      {
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);

         List<Agent> selected = new List<Agent>();
         foreach(DataItem di in (List<DataItem>)dgvItems.DataSource)
         {
            if (di.Checked)
               selected.Add(di.Agent);
         }

         if( selected.Count == 0 )
         {
            MessageBox.Show("Отметьте, пожалуста, агентов для назначения задач", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Warning);
         }
         else
         {
            FmTaskTemplates.Open(selected);
         }
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void cbSelAgents_CheckedChanged(object sender, EventArgs e)
      {
         List<DataItem> data = (List<DataItem>)dgvItems.DataSource;
         foreach (DataItem item in data)
            item.Checked = cbSelAgents.Checked;
         dgvItems.Refresh();
      }
   }
}

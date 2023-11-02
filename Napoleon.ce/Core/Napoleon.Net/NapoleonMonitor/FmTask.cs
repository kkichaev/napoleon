using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Collections;

namespace GRSoft.NapoleonManager
{
   public partial class FmTask : Form
   {
      private Agent agent = null;

      private DataSet<string, Org> dsOrg;
      private DataSet<string, PotenzialOrg> dsPtnzOrg;
      private DataSet<int, Task> dsTask = new DataSet<int,Task>(Task.OBJECT_NAME, false);

      private DataSet<string, Task> updated = new DataSet<string, Task>(Task.OBJECT_NAME, false);
      private DataSet<string, Task> removed = new DataSet<string, Task>(Task.OBJECT_NAME, false);

      BindingSource bs = new BindingSource();

      protected FmTask()
      {
         InitializeComponent();
      }

      public void RefreshData()
      {
         if( agent == null )
            return;

         Text = "Задачи для  " + agent.Name;

         const string USERID_IN_STR = "\"userid\" in ('{0}')";
         dsOrg = DataModule.GetUserDataSet(agent.id, Org.OBJECT_NAME, typeof(DataSet<string, Org>)) as DataSet<string, Org>;
         dsPtnzOrg = DataModule.GetUserDataSet(agent.id, PotenzialOrg.OBJECT_NAME, typeof(DataSet<string, PotenzialOrg>)) as DataSet<string, PotenzialOrg>;

         updated.Clear();
         removed.Clear();

         btnSave.Enabled = false;

         DataModule.OnDataResponceError += new EventDataResponseError(DataModule_OnDataResponceError);
         DataModule.DataProcessed += new EventHandler(DataLoaded);
         dsTask.Filter = String.Format(USERID_IN_STR, agent.id);
         dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agent.id), dsOrg.Name);
         //dsOrg.Filter = String.Format(USERID_IN_STR, agent.id);

         dsPtnzOrg.Filter = String.Format(USERID_IN_STR, agent.id);
         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), new object[] { dsOrg, dsTask, dsPtnzOrg }, FmWait.ProgressIndicator);
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.DataProcessed -= new EventHandler(DataLoaded);
         DataModule.OnDataResponceError -= new EventDataResponseError(DataModule_OnDataResponceError);
      }

      private void DataLoaded(object sender, EventArgs e)
      {
         DataModule.DataProcessed -= new EventHandler(DataLoaded);
         BeginInvoke(new EmptyParamHandler(FillData));
      }

      static int CmpTask(Task _l, Task _r)
      {
         return _l.date.CompareTo(_r.date);
      }
      void FillData()
      {
         List<Task> data = new List<Task>();
         foreach (Task t in dsTask.Data)
         {
            data.Add(t);
         }
         data.Sort(CmpTask);

         bs.DataSource = data;
         dgvTask.DataSource = bs;
      }

      protected Agent CurAgent
      { 
         set
         {
            agent = value;
         } 
      }

      public static void Show(Agent a)
      {
         FmTask t = new FmTask();
         t.CurAgent = a;
         t.RefreshData();
         t.Show();
      }

      private void ClearDirty()
      {
         btnSave.Enabled = false;

         removed.Clear();
         updated.Clear();
      }

      private void MarkDirty()
      {
         btnSave.Enabled = true;
      }

      private void AddRemoved(Task t)
      {
         foreach (KeyValuePair<string, Task> kv in updated)
         {
            if (kv.Value.CompareTo(t) == 0)
            {
               updated.Remove(kv.Key);
               break;
            }
         }

         string key = t.id + t.date.ToString("yyyy-MM-dd HH:mm:ss");
         removed[key] = t;

         MarkDirty();
      }

      private void AddUpdated(Task t)
      {
         string key = t.id + t.date.ToString("yyyy-MM-dd HH:mm:ss");
         updated[key] = t;

         MarkDirty();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         FmTaskAdd ta = new FmTaskAdd();
         ta.Init(dsOrg, dsPtnzOrg);

         if (ta.ShowDialog() == DialogResult.OK)
         {
            Task t = ta.Task;
            if (t != null)
            {
               t.agent = agent;
               t.userid = agent.id;
               AddUpdated(t);
               bs.Add(t);
            }
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         Config c = Config.GetConfig();
         DBConnection conn = c.GetConnection();

         List<IDataSet> upd = new List<IDataSet>();
         if( updated.Count > 0 ) upd.Add(updated);

         List<IDataSet> rmv = new List<IDataSet>();
         if( removed.Count > 0 ) rmv.Add(removed);
         if (DataModule.UpdateDataSet(upd, rmv, null, conn, agent.id) == false)
         {
            MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return;
         }

         ClearDirty();
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (dgvTask.CurrentRow != null &&
            MessageBox.Show("Удалить задачу?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
         {
            Task t = bs[dgvTask.CurrentRow.Index] as Task;
            AddRemoved(t);
            bs.Remove(t);
         }
      }

      private void dgvTask_SelectionChanged(object sender, EventArgs e)
      {
         string doingV = "";
         string taskV = "";
         if (dgvTask.CurrentRow != null)
         {
            Task t = bs[dgvTask.CurrentRow.Index] as Task;
            task.Enabled = (t.doing.Length == 0);

            doingV = t.doing;
            taskV = t.task;
         }
         doing.Text = doingV;
         task.Text = taskV;
      }

      private void task_Leave(object sender, EventArgs e)
      {
         task.TextChanged -= new EventHandler(task_TextChanged);
      }

      private void task_Enter(object sender, EventArgs e)
      {
         task.TextChanged += new EventHandler(task_TextChanged);
      }

      void task_TextChanged(object sender, EventArgs e)
      {
         if (dgvTask.CurrentRow == null)
            return;
         Task t = bs[dgvTask.CurrentRow.Index] as Task;
         t.task = task.Text;
         AddUpdated(t);         
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void dgvTask_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         Task t = bs[e.RowIndex] as Task;
         if( t.org is PotenzialOrg )
            e.CellStyle.ForeColor = Color.Red;
      }
   }
}

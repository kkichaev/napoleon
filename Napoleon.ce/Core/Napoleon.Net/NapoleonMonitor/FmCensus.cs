using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.UILib;

namespace GRSoft.NapoleonManager
{
   public partial class FmCensus : Form
   {
      bool inited = false;

      DataSet<string, PotenzialOrg> porgs = new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME, false);
      protected DataSet<int, AgentOrgTask> tasks = new DataSet<int, AgentOrgTask>(AgentOrgTask.OBJECT_NAME, false);
      //DataSet<int, PODel> delOrgs = new DataSet<int, PODel>(PODel.OBJECT_NAME, false);
      private SearchEngine searchEngine;
      private GridBoundedObjectComparer gridComparer;


      public FmCensus()
      {
         InitializeComponent();
         dgvAgents.AutoGenerateColumns = false;
         dgvOrgs.AutoGenerateColumns = false;
         dgvTask.AutoGenerateColumns = false;

         searchEngine = new SearchEngine(new FindDataGridObject(dgvOrgs, 0));
         gridComparer = CreateComparer();


#if CAN_ADD_ORG_TO_CENSUS
#else
         tbAddOrg.Visible = false;
         btnEdit.Visible = false;
#endif
         
      }

      internal DateTime From { get { return dtpBeginDate.Value; } set { dtpBeginDate.Value = value; } }
      internal DateTime Till { get { return dtpEndDate.Value; } set { dtpEndDate.Value = value; } }

      internal void AdjustRangeButton(bool isToday)
      {
         tsbSelectRange.Image = isToday ? tsmiToday.Image : tsmiRange.Image;
         tsmiToday.Checked = isToday;
         tsmiRange.Checked = !isToday;
         tsbSelectRange.ToolTipText = (isToday) ? tsmiToday.Text : tsmiRange.Text;
         dtpEndDate.Enabled = !isToday;
      }

      protected virtual GridBoundedObjectComparer CreateComparer()
      {
         return new OrgGridComparer();
      }

      class OrgGridComparer : GridBoundedObjectComparer
      {

      }
      //Переключение условий выборки по щелчку на кнопку
      private void tsbSelectRange_Click(object sender, EventArgs e)
      {
         if (tsmiToday.Checked)
            tsmiRange_Click(sender, e);
         else
            tsmiToday_Click(sender, e);
      }

      //Условие выборки "за сегодня"
      private void tsmiToday_Click(object sender, EventArgs e)
      {
         AdjustRangeButton(true);
      }

      //Условие выборки "за период"
      private void tsmiRange_Click(object sender, EventArgs e)
      {
         AdjustRangeButton(false);
      }

      protected void btnRefresh_Click(object sender, EventArgs e)
      {
         LoadData();
      }

      private void btnDelOrg_Click(object sender, EventArgs e)
      {
         DataGridViewRow orgRow = dgvOrgs.CurrentRow;
         DataGridViewRow agentRow = dgvAgents.CurrentRow;

         if (orgRow != null && agentRow != null && 
               MessageBox.Show("Запись будет удалена, удалить?", "Внимание", MessageBoxButtons.OKCancel, 
                  MessageBoxIcon.Warning) == DialogResult.OK)
         {
            OrgData od = orgRow.DataBoundItem as OrgData;
            AgentData ad = agentRow.DataBoundItem as AgentData;

            if (od != null && ad != null && 
               od.Org.GetType()==typeof(PotenzialOrg))
            {
               PODel poDel = new PODel();
               poDel.id = od.Org.id;
               poDel.userid = ad.Agent.id;

               DataSet<int, PODel> upd = new DataSet<int, PODel>(PODel.OBJECT_NAME, false);
               upd[1] = poDel;

               List<IDataSet> wrSet = new List<IDataSet>();
               wrSet.Add(upd);

               DataSet<string, PotenzialOrg> del = new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME, false);
               del[od.Org.id] = (PotenzialOrg)od.Org;

               List<IDataSet> rmvSet = new List<IDataSet>();
               rmvSet.Add(del);

               SaveDataSelection();

               if (DataModule.UpdateDataSet(wrSet, rmvSet, null, Config.GetConfig().GetConnection(), ad.Agent.id))
               {
                  ad.RemoveOrg(od.Org);
                  dgvOrgs.DataSource = ad.OrgData;
                  porgs.Remove(od.Org.id);
                  RefreshData();
               }
               else
               {
                  MessageBox.Show("Ошибка при удалении организации.");
               }

               RestoreDataSelection();
            }
         }
      }

      private void btnAddTask_Click(object sender, EventArgs e)
      {
         DataGridViewRow curRow = dgvOrgs.CurrentRow;
         if (curRow != null)
         {
            OrgData od = curRow.DataBoundItem as OrgData;
            FmAgentOrgTask ft = new FmAgentOrgTask();
            
            ft.Data = od;
            DialogResult res = ft.ShowDialog();
            if (res == DialogResult.OK)
            {
               AgentData ad = dgvAgents.CurrentRow.DataBoundItem as AgentData;

               AgentOrgTask ot = new AgentOrgTask();
               ot.id = od.Org.id;
               ot.created = DateTime.Now;
               ot.task = ft.Task;
               ot.userid = ad.Agent.id;

               DataSet<int, AgentOrgTaskSend> upd = new DataSet<int, AgentOrgTaskSend>(AgentOrgTaskSend.OBJECT_NAME, false);
               upd[1] = ot;
               List<IDataSet> wrSet = new List<IDataSet>();
               wrSet.Add(upd);

               if (DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()))
               {
                  ad.AddTask(od.Org, ot);
                  od.MakeTaskNodes(dgvTask);
                  dgvOrgs.Refresh();
                  dgvAgents.Refresh();
                  tasks.Add(tasks.Count, ot);
               }
               else
               {
                  MessageBox.Show("Ошибка при записи задачи");
               }
            }
         }
      }

      private void FmCensus_Activated(object sender, EventArgs e)
      {
         if (!inited)
         {
            LoadData();
            inited = true;
         }
      }

      private void LoadData()
      {
         DateTime sd = dtpBeginDate.Value;
         if( sd.CompareTo(dtpEndDate.Value) > 0 )
         {
            dtpBeginDate.Value = dtpEndDate.Value;
            dtpEndDate.Value = sd;
         }

         Manager m = CurrentUser.user as Manager;

         string filter = DataUtils.MakeFilterFromAgents(null, m.Division.GetAllAgents());
         porgs.Filter = filter;
         //delOrgs.Filter = filter;
         
         string dateWhere = String.Format(" and \"created\" >= ToDate('{0}') and \"created\" <= ToDate('{1}')", 
            dtpBeginDate.Value.ToString("dd-MM-yyyy 00:00:00"),
            dtpEndDate.Value.ToString("dd-MM-yyyy 23:59:59"));
         tasks.Filter = filter + dateWhere;

         List<IDataSet> upd = CreateUpdateList();

         DataModule.OnDataResponceError += new EventDataResponseError(DataModule_OnDataResponceError);
         DataModule.DataProcessed += new EventHandler(DataModule_DataProcessed);

         DBConnection conn = Config.GetConfig().GetConnection();
         DataSet<int, PODel> rmvSet = new DataSet<int, PODel>(PODel.OBJECT_NAME, false);
         rmvSet.Filter = "\"flags\" <> 0";
         DataModule.RemoveDataSet(rmvSet, conn);
         SaveDataSelection();
         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(conn, upd, FmWait.ProgressIndicator));
      }

      virtual protected List<IDataSet> CreateUpdateList()
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(porgs);
         upd.Add(tasks);
         //upd.Add(delOrgs);
         return upd;
      }

      private bool IsOrgDel(string orgid)
      { 
         //foreach(PODel poDel in delOrgs.Data)
         //{
         //   if (poDel.id.Equals(orgid))
         //      return true;
         //}

         return false;
      }

      protected virtual void RefreshDataEx(Dictionary<string, AgentData> agents) { }
      void RefreshData()
      {
         Dictionary<string, AgentData> agents = new Dictionary<string, AgentData>();
         Manager m = CurrentUser.user as Manager;

         foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
            if( a.agent != null )
               agents[a.agent.id] = new AgentData(a.agent);

         foreach (PotenzialOrg po in porgs.Data)
         {
            if (agents.ContainsKey(po.userid) && !IsOrgDel(po.id))
               agents[po.userid].AddOrg(po);
         }

         foreach (AgentOrgTask t in tasks.Data)
         {
            if( agents.ContainsKey(t.userid) && porgs.ContainsKey(t.id) )
               agents[t.userid].AddTask(porgs[t.id], t);
         }

         RefreshDataEx(agents);
         List<AgentData> asrc = new List<AgentData>();
         foreach(KeyValuePair<string, AgentData> kv in agents)
            asrc.Add(kv.Value);
         asrc.Sort();

         dgvAgents.DataSource = asrc;
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         Invoke(new InvokeDelegate(delegate() {
            RefreshData();
            RestoreDataSelection();
         }));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
      }

      private void dgvAgents_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         dgvOrgs.DataSource = null;
         DataGridViewRow curRow = dgvAgents.Rows[e.RowIndex];
         AgentData ad = curRow.DataBoundItem as AgentData;

         dgvTask.Nodes.Clear();
         orgName.Text = "";
         orgAddress.Text = "";
         dgvOrgs.DataSource = ad.OrgData;
      }

      private void dgvOrgs_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         UpdateTaskGrid(e);
      }

      protected virtual void UpdateTaskGrid(DataGridViewCellEventArgs e)
      {
         DataGridViewRow curRow = dgvOrgs.Rows[e.RowIndex];
         OrgData od = curRow.DataBoundItem as OrgData;

         od.MakeTaskNodes(dgvTask);

         orgName.Text = od.Org.name;
         orgAddress.Text = od.Org.Address + String.Format(" ({0:F5}/{1:F5})", od.Org.longitude, od.Org.latitude);
      }

      private void dgvTask_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         AgentOrgTask t = dgvTask.Rows[e.RowIndex].Tag as AgentOrgTask;
         if (t != null && !t.TaskDone)
         {
            e.CellStyle.SelectionForeColor = Color.Red;
            e.CellStyle.ForeColor = Color.Red;
         }
      }

      private void orgAddress_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         DataGridViewRow curRow = dgvOrgs.CurrentRow;
         if (curRow != null)
         {
            OrgData od = curRow.DataBoundItem as OrgData;
            FmAddrShow.AddrShow(od.Org.Address, od.Org);
         }
      }

      protected void tbAddOrg_Click(object sender, EventArgs e)
      {
         DataGridViewRow agentRow = dgvAgents.CurrentRow;

         if (agentRow == null)
            return;

         AgentData ad = agentRow.DataBoundItem as AgentData;

         if (ad == null)
            return;

         FmPtnzlOrgEdit orgEdit = CreateFmPotenzlOrgEdit();

         if (orgEdit.ShowDialog() == DialogResult.OK)
         {
            PotenzialOrg o = orgEdit.Org;
            if (o.name.Length != 0)
            {
               o.id = CreateOrgId();
               
               DataSet<int, PotenzialOrg> wrSet = new DataSet<int,PotenzialOrg>(PotenzialOrg.OBJECT_NAME, false);
               wrSet.Add(1, o);

               List<IDataSet> add = new List<IDataSet>(new IDataSet[] { wrSet });
               SaveDataSelection();
               if (DataModule.UpdateDataSet(add, null, null, Config.GetConfig().GetConnection(), ad.Agent.id))
               {
                  ad.AddOrg(o);
                  dgvOrgs.DataSource = null;
                  dgvOrgs.DataSource = ad.OrgData;
               }
               RestoreDataSelection();
            }
         }
      }

      private static string CreateOrgId()
      {
         return GRSoft.Network.DataObject.GenId();
      }

      virtual protected FmPtnzlOrgEdit CreateFmPotenzlOrgEdit()
      {
         return new FmPtnzlOrgEdit();
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         DataGridViewRow agentRow = dgvAgents.CurrentRow;

         if (agentRow == null)
            return;

         AgentData ad = agentRow.DataBoundItem as AgentData;

         if (ad == null)
            return;

         DataGridViewRow orgRow = dgvOrgs.CurrentRow;

         if (orgRow == null)
            return;

         OrgData od = orgRow.DataBoundItem as OrgData;

         if (od == null)
            return;

         FmPtnzlOrgEdit orgEdit = CreateFmPotenzlOrgEdit();
         orgEdit.Org = od.Org as PotenzialOrg;

         if (orgEdit.Org != null && orgEdit.ShowDialog() == DialogResult.OK)
         {
            PotenzialOrg o = orgEdit.Org;
            if (o.name.Length != 0)
            {
               DataSet<int, PotenzialOrg> wrSet = new DataSet<int, PotenzialOrg>(PotenzialOrg.OBJECT_NAME, false);
               wrSet.Add(1, o);

               List<IDataSet> add = new List<IDataSet>(new IDataSet[] { wrSet });
               SaveDataSelection();
               if (DataModule.UpdateDataSet(add, null, null, 
                  Config.GetConfig().GetConnection(), ad.Agent.id))
               {
                  dgvOrgs.Refresh();
               }
               RestoreDataSelection();
            }
         }
      }

      private OrgData savedOrgData = null;
      private AgentData savedAgentData = null;

      private void SaveDataSelection()
      {
         DataGridViewRow agentRow = dgvAgents.CurrentRow;

         if (agentRow != null)
            savedAgentData = agentRow.DataBoundItem as AgentData;

         DataGridViewRow orgRow = dgvOrgs.CurrentRow;

         if (orgRow != null)
            savedOrgData = orgRow.DataBoundItem as OrgData;
      }

      private void RestoreDataSelection()
      {
         if (savedOrgData != null && savedOrgData.Org != null)
         {
            for (int i = 0; i < dgvOrgs.RowCount; i++)
            {
               DataGridViewRow r = dgvOrgs.Rows[i];
               OrgData od = r.DataBoundItem as OrgData;

               if (od != null && od.Org.id.Equals(savedOrgData.Org.id))
               {
                  dgvOrgs.CurrentCell = dgvOrgs.Rows[i].Cells[0];
                  break;
               }
            }
         }

         if (savedAgentData != null && savedAgentData.Agent != null)
         {
            for (int i = 0; i < dgvAgents.RowCount; i++)
            {
               DataGridViewRow r = dgvAgents.Rows[i];
               AgentData ad = r.DataBoundItem as AgentData;

               if (ad != null && ad.Agent.id.Equals(savedAgentData.Agent.id))
               {
                  dgvAgents.CurrentCell = dgvAgents.Rows[i].Cells[0];
                  break;
               }
            }
         }
      }

      private Point mouseDown;
      private void dgvOrgs_MouseMove(object sender, MouseEventArgs e)
      {
         int distance = 0;
         if (mouseDown != null)
         {
            int x = e.X - mouseDown.X;
            int y = e.Y - mouseDown.Y;
            distance = (int)Math.Sqrt(x * x + y * y);
         }

         if (distance > 5 && 
            ((e.Button & MouseButtons.Left) == MouseButtons.Left || 
            (e.Button & MouseButtons.Right) == MouseButtons.Right))
         {
            List<OrgData> orgs = new List<OrgData>();

            foreach (DataGridViewRow r in dgvOrgs.SelectedRows)
               orgs.Add(r.DataBoundItem as OrgData);

            if (orgs.Count != 0)
            {
               DragDropObject ddo = new DragDropObject(dgvOrgs, orgs);
               dgvOrgs.DoDragDrop(ddo, DragDropEffects.Copy);
            }
         }
      }

      private void dgvOrgs_MouseDown(object sender, MouseEventArgs e)
      {
         mouseDown = new Point(e.X, e.Y);
      }

      private void dgvAgents_DragOver(object sender, DragEventArgs e)
      {
         e.Effect = DragDropEffects.Copy;
      }

      private void dgvAgents_DragDrop(object sender, DragEventArgs e)
      {
         if (e.Data.GetDataPresent(typeof(DragDropObject)))
         {
            DragDropObject ddo = (DragDropObject)e.Data.GetData(typeof(DragDropObject));

            if (ddo.Source == dgvOrgs)
            {
               Point clientPoint = dgvAgents.PointToClient(new Point(e.X, e.Y));
               int newRowIndex = dgvAgents.HitTest(clientPoint.X, clientPoint.Y).RowIndex;
               AgentData newAgent = dgvAgents.Rows[newRowIndex].DataBoundItem as AgentData;
               AgentData oldAgent = dgvAgents.CurrentRow.DataBoundItem as AgentData;

               if (newAgent != null)
               {
                  dgvAgents.CurrentCell = dgvAgents.Rows[newRowIndex].Cells[0];
                  List<OrgData> list = (List<OrgData>)ddo.Data;
                  DataSet<int, PotenzialOrg> wrSet = new DataSet<int, PotenzialOrg>(PotenzialOrg.OBJECT_NAME, false);

                  foreach (OrgData od in list)
                  {
                     od.org.agent = newAgent.Agent;
                     Org o = od.org;
                     PotenzialOrg newOrg = new PotenzialOrg();
                     newOrg.region = od.Region;
                     newOrg.name = o.name;
                     newOrg.Address = o.Address;
                     newOrg.id = CreateOrgId();
                     newOrg.agent = newAgent.Agent;
                     newOrg.userid = newAgent.Agent.id;
                     wrSet.Add(wrSet.Count, newOrg);
                     newAgent.AddOrg(newOrg);
                  }

                  DataGridViewRow agentRow = dgvAgents.CurrentRow;
                  List<IDataSet> add = new List<IDataSet>(new IDataSet[] { wrSet });
                  SaveDataSelection();

                  if (DataModule.UpdateDataSet(add, null, null,
                     Config.GetConfig().GetConnection(), newAgent.Agent.id))
                  {
                     dgvAgents_RowEnter(dgvOrgs, (DataGridViewCellEventArgs)DataGridViewCellEventArgs.Empty);
                  }
               }
            }
         }
      }

      private void btnFindOrgDown_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFindOrg.Text, Direction.DOWN);
      }

      private void btnFindOrgUp_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFindOrg.Text, Direction.UP);
      }

      private void tbFindOrg_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            searchEngine.find(tbFindOrg.Text, Direction.DOWN);
      }

      private void dgvOrgs_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         searchEngine = new SearchEngine(new FindDataGridObject(dgvOrgs, e.ColumnIndex));
         GridSort(e.ColumnIndex, gridComparer);
      }

      protected virtual void GridSort(int index, GridBoundedObjectComparer cmp)
      {
         DataUtils.GridSort<OrgData>(dgvOrgs, index, cmp);
      }
   }

   public class OrgData : IComparable<OrgData>
   {
      public Org org;
      List<AgentOrgTask> task;
      int doneCount = 0;
      public AgentData agentData;

      public OrgData(AgentData agentData, Org o, List<AgentOrgTask> t)
      {
         org = o;
         task = t;
         this.agentData = agentData;

         foreach (AgentOrgTask ot in t)
         {
            if (ot.TaskDone)
               doneCount++;
         }
      }

      public Org Org { get { return org; } }

      public string OrgName { get { return org.Name; } }
      public string TaskCount { get { return task.Count.ToString(); } }
      public string DoneCount { get { return doneCount.ToString(); } }
      public Region Region { get { return (org is PotenzialOrg) ? ((PotenzialOrg)org).region : null; } }

      internal void MakeTaskNodes(TreeGridView tgv)
      {
         TreeGridNodeCollection nodes = tgv.Nodes;
         nodes.Clear();

         foreach (AgentOrgTask t in task)
         {
            object[] data = { t.task, t.created.ToString("dd/MM/yy") };

            TreeGridNode node = nodes.Add(data);
            node.Tag = t;
            if (t.TaskDone)
            {
               data[0] = t.done;
               data[1] = t.dodate.ToString("dd/MM/yy HH:mm");
               TreeGridNode chnode = new TreeGridNode();
               chnode.CreateCells(tgv, data);
               node.Nodes.Add(chnode);
            }
         }
      }

      internal void AddTask(AgentOrgTask ot)
      {
         task.Add(ot);
         if (ot.TaskDone)
            doneCount++;
      }

      #region Члены IComparable<OrgData>

      public int CompareTo(OrgData other)
      {
         int cmp = OrgName.CompareTo(other.OrgName);
         if( cmp != 0 )
            return cmp;
         cmp = org.Address.CompareTo(other.org.Address);
         if (cmp != 0)
            return cmp;

         cmp = (org.latitude < other.org.latitude) ? -1 : (org.latitude > other.org.latitude) ? 1 : 0;
         if (cmp != 0)
            return cmp;

         return (org.longitude < other.org.longitude) ? -1 : (org.longitude > other.org.longitude) ? 1 : 0;
      }

      #endregion
   }

   public class AgentData : IComparable<AgentData>
   {
      Agent agent;
      int taskCount = 0;
      int doneCount = 0;

      Dictionary<Org, List<AgentOrgTask>> orgTask = new Dictionary<Org, List<AgentOrgTask>>();

      public AgentData(Agent a)
      {
         agent = a;
      }

      public Agent Agent { get { return agent; } }
      public string AgentName { get { return agent.Name; } }

      public string OrgCount { get { return orgTask.Count.ToString(); } }
      public string TaskCount { get { return taskCount.ToString(); } }
      public string DoneCount { get { return doneCount.ToString(); } }

      public List<OrgData> OrgData
      {
         get
         {
            List<OrgData> od = new List<OrgData>();
            foreach (KeyValuePair<Org, List<AgentOrgTask>> kv in orgTask)
               od.Add(new OrgData(this, kv.Key, kv.Value));

            od.Sort();
            return od;
         }
      }

      internal void AddOrg(Org po)
      {
         if (!orgTask.ContainsKey(po))
            orgTask.Add(po, new List<AgentOrgTask>());
      }

      internal void AddTask(Org po, AgentOrgTask t)
      {
         if (orgTask.ContainsKey(po))
         {
            taskCount++;
            if (t.TaskDone)
               doneCount++;

            orgTask[po].Add(t);
         }
      }

      internal void RemoveOrg(Org po)
      {
         if (orgTask.ContainsKey(po))
            orgTask.Remove(po);
      }

      #region Члены IComparable<AgentData>

      public int CompareTo(AgentData other)
      {
         return AgentName.CompareTo(other.AgentName);
      }

      #endregion
   }
}

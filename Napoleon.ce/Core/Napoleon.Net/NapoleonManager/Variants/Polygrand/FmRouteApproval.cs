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
   public partial class FmRouteApproval : Form
   {
      private DataSet<int, AgentRoute> dsAgentRoute;
      private DataSet<int, ManagerRoute> dsManagerRoute;
      private DataSet<string, Org> dsOrg;
      private DataSet<string, PotenzialOrg> dsPtnzOrg;
      private DataSet<int, OrgFolder> dsOrgFolder;
      private DateTime routeData = DateTime.Now;


      public FmRouteApproval()
      {
         InitializeComponent();
         dsAgentRoute = new DataSet<int, AgentRoute>(AgentRoute.OBJECT_NAME, false);
         dsManagerRoute = new DataSet<int, ManagerRoute>(ManagerRoute.OBJECT_NAME, false);

         dsPtnzOrg = DataModule.Get(PotenzialOrg.OBJECT_NAME) == null ? new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME) :
            (DataSet<string, PotenzialOrg>)DataModule.Get(PotenzialOrg.OBJECT_NAME);
         dsOrgFolder = new DataSet<int, OrgFolder>("OrgFolder", false);

         grid.DataSource = new BindingList<AgentRouteItem>();
      }

      private void FmRouteApproval_Load(object sender, EventArgs e)
      {
         List<Agent> list = new List<Agent>();
         foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
            list.Add(a);

         list.Sort(new Comparison<Agent>(delegate(Agent lhs, Agent rhs) { return lhs.Name.CompareTo(rhs.Name); }));

         cbAgent.Items.AddRange(list.ToArray());

         if (cbAgent.Items.Count > 0)
            cbAgent.SelectedIndex = 0;

         LoadData();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         lblDate.Text = string.Empty;
         Agent agent = cbAgent.SelectedItem as Agent;

         if (agent != null)
         {
            string filter = String.Format("userid='{0}' and date= ToDate('{1:dd/MM/yyyy}')",
               agent.id, dtpDate.Value);

            dsAgentRoute.Filter = filter;
            dsManagerRoute.Filter = filter;

            dsOrg = DataModule.GetUserDataSet(agent.id, "Org", typeof(DataSet<string, Org>)) as DataSet<string, Org>;
            dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agent.id), dsOrg.Name);
            dsOrgFolder.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agent.id), dsOrgFolder.Name);

            List<IDataSet> list = new List<IDataSet>();
            list.Add(dsOrg);
            list.Add(dsPtnzOrg);
            list.Add(dsOrgFolder);
            list.Add(dsAgentRoute);
            list.Add(dsManagerRoute);

            FmWait.StdDataRefresh(this, list, LoadData, btnRefresh);
         }
         else
         {
            MessageBox.Show("Выберите агента");
         }
      }

      class BehaviourStrategyFactory
      {
         public BehaviourStrategy GetStrategy(FmRouteApproval form)
         {
            if (form.dsManagerRoute.Count > 0)
               return new ManagerRouteBehaviourStrategy(form);
            else if (form.dsAgentRoute.Count > 0)
               return new AgentRouteBehaviourStrategy(form);
            else
               return new BehaviourStrategy(form);
         }

      }

      class ManagerRouteBehaviourStrategy : RouteBehaviourStrategy
      {
         public ManagerRouteBehaviourStrategy(FmRouteApproval form)
            : base(form)
         { 
         }

         public override IList GetItems()
         {
            return form.dsManagerRoute[0].items;
         }

         public override void InitFormViews()
         {
            form.lblDate.Text = form.dsManagerRoute[0].changed.ToString();
            form.routeData = form.dsManagerRoute[0].date;
            form.lblApprove.Text = form.dsManagerRoute[0].approve.ToString();
         }
      }

      class AgentRouteBehaviourStrategy : RouteBehaviourStrategy
      {
         public AgentRouteBehaviourStrategy(FmRouteApproval form)
            : base(form)
         { }

         public override void EnabledControl()
         {
            form.enableControls(true);
         }

         public override IList GetItems()
         {
            return form.dsAgentRoute[0].items;
         }

         public override void InitFormViews()
         {
            form.lblDate.Text = form.dsAgentRoute[0].changed.ToString();
            form.routeData = form.dsAgentRoute[0].date;
            form.btnApprove.Enabled = true;
         }
      }

      abstract class RouteBehaviourStrategy : BehaviourStrategy
      {
         public RouteBehaviourStrategy(FmRouteApproval form)
            :base(form){ }

         public abstract IList GetItems();

         protected override void FillData()
         {
            IEnumerator enumer = GetItems().GetEnumerator();
            BindingList<AgentRouteItem> list = (BindingList <AgentRouteItem>)form.grid.DataSource;
            list.Clear();

            while (enumer.MoveNext())
               list.Add((AgentRouteItem)enumer.Current);

            form.grid.DataSource = list;
         }
      }

      class BehaviourStrategy 
      {
         protected FmRouteApproval form;

         public BehaviourStrategy(FmRouteApproval form) {  this.form = form; }

         protected virtual void FillData() 
         {
            BindingList<AgentRouteItem> list = (BindingList<AgentRouteItem>)form.grid.DataSource;
            list.Clear();
         }

         public virtual void EnabledControl()  { form.enableControls(false); }
         public virtual void InitFormViews()
         {
            form.lblDate.Text = string.Empty;
            form.lblApprove.Text = string.Empty;
         }

         public void Apply()
         {
            FillData();
            InitFormViews();
            EnabledControl();
         }
      }

      private void LoadData()
      {
         BehaviourStrategy bh = new BehaviourStrategyFactory().GetStrategy(this);
         bh.Apply();
      }

      private void enableControls(bool val)
      {
         ToolStripItem[] items = new ToolStripItem[] { btnApprove, btnAdd, btnDel, btnTask, btnUp, btnDown };

         foreach (ToolStripItem i in items)
            i.Enabled = val;
      }

      private void grid_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         AgentRouteItem item = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as AgentRouteItem;

         if (item != null)
         {
            e.CellStyle.BackColor = item.isNew == 0 ? Color.LightGray : Color.White;
         }
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         FmSelectContrAgent.ShowForm(dsOrg, dsPtnzOrg, dsOrgFolder, AddOrg, this);
      }

      void AddOrg(object sender, Org org)
      {
         BindingList<AgentRouteItem> list = grid.DataSource as BindingList<AgentRouteItem>;

         foreach(AgentRouteItem ari in list)
            if(ari.id.Equals(org.id))
               return;

         if (list != null)
         {
            AgentRouteItem ari = new AgentRouteItem();
            ari.id = org.id;
            ari.org = org;
            ari.isNew = 1;

            list.Add(ari);
         }
      }

      private void btnApprove_Click(object sender, EventArgs e)
      {
         if (MessageBox.Show("Утвердить маршрут, дальнейшее редактирование будет невозможно?", "Вопрос", MessageBoxButtons.OKCancel,
            MessageBoxIcon.Question) != DialogResult.OK)
            return;

         Agent a = cbAgent.SelectedItem as Agent;

         if(a != null && grid.RowCount > 0)
         {
            dsManagerRoute.Clear();
            DataSet<string, OrgTask> dsOrgTask = new DataSet<string, OrgTask>(OrgTask.OBJECT_NAME, false);
            ManagerRoute mr = new ManagerRoute();
            mr.userid = a.id;
            mr.items = new List<ManagerRouteItem>();
            mr.date = routeData;
            mr.approve = DateTime.Now;

            for (int i = 0; i < grid.RowCount; i++)
            {
               AgentRouteItem ari = grid.Rows[i].DataBoundItem as AgentRouteItem;

               if (ari != null)
               {
                  ManagerRouteItem mri = new ManagerRouteItem();
                  mri.id = ari.id;
                  mri.isNew = ari.isNew;
                  mri.org = ari.org;
                  mri.task = ari.task;
                  mri.pos = i;

                  string tskid = GRSoft.Network.DataObject.GenId();
                  mri.taskId = tskid;

                  if (mri.task.Trim().Length > 0)
                  {
                     OrgTask task = new OrgTask();
                     task.id = tskid;
                     task.orgid = mri.id;
                     task.start = mr.date;
                     task.finish = mr.date;
                     task.text = mri.task;
                     task.userid = mr.userid;

                     dsOrgTask.Add(task.id, task);
                  }

                  mr.items.Add(mri);
               }
            }

            dsManagerRoute.Add(dsManagerRoute.Count, mr);
            
            List<IDataSet> wrSet = new List<IDataSet>();
            wrSet.Add(dsManagerRoute);

            if(dsOrgTask.Count > 0)
               wrSet.Add(dsOrgTask);

            if (!DataModule.UpdateDataSet
            (wrSet, null, null, Config.GetConfig().GetConnection()))
               MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
                  MessageBoxIcon.Error);

            LoadData();
         }
      }

      private void dtpDate_ValueChanged(object sender, EventArgs e)
      {
         dsManagerRoute.Clear();
         dsAgentRoute.Clear();
         LoadData();
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         BindingList<AgentRouteItem> list = grid.DataSource as BindingList<AgentRouteItem>;

         if (list != null && grid.CurrentRow != null && grid.CurrentRow.Index >= 0
            && MessageBox.Show("Запись будет удалена, удалить", "Вопрос", MessageBoxButtons.OKCancel,
            MessageBoxIcon.Question) == DialogResult.OK)
         {
            list.RemoveAt(grid.CurrentRow.Index);
         }
      }

      private void btnTask_Click(object sender, EventArgs e)
      {
         BindingList<AgentRouteItem> list = grid.DataSource as BindingList<AgentRouteItem>;
         if (list != null && grid.CurrentRow != null && grid.CurrentRow.Index >= 0)
         {
            FmTaskEdit dlg = new FmTaskEdit();
            dlg.Task = list[grid.CurrentRow.Index].Task;

            if (dlg.ShowDialog() == DialogResult.OK)
            {
               list[grid.CurrentRow.Index].task = dlg.Task;
               grid.Refresh();
            }
         }

      }

      class MoveOperatorFactory
      {
         public MoveOperatot GetOperator(FmRouteApproval form,  object item)
         {
            if (item == form.btnUp)
               return new UpMoveOperator(form);
            else if (item == form.btnDown)
               return new DownMoveOperator(form);
            else
               throw new Exception("invalid control for creating: " + item);

         }
      }

      class DownMoveOperator : MoveOperatot
      {
         public DownMoveOperator(FmRouteApproval form)
            :base(form){ }

         protected override bool AllowChange(int idx)
         {
            return idx < form.grid.RowCount - 1;
         }

         protected override int ComputeNewIdx(int idx)
         {
            return idx + 1;
         }
      }

      class UpMoveOperator : MoveOperatot
      {
         public UpMoveOperator(FmRouteApproval form)
            : base(form) { }

         protected override bool AllowChange(int idx)
         {
            return idx > 0;
         }

         protected override int ComputeNewIdx(int idx)
         {
            return idx - 1;
         }
      }

      abstract class MoveOperatot
      {
         protected FmRouteApproval form;

         public MoveOperatot(FmRouteApproval form)
         {
            this.form = form;
         }

         public void Change()
         {
            BindingList<AgentRouteItem> list = form.grid.DataSource as BindingList<AgentRouteItem>;
            if (list != null && form.grid.CurrentRow != null && form.grid.CurrentRow.Index >= 0)
            {
               int idx = form.grid.CurrentRow.Index;

               if (AllowChange(idx))
               {
                  AgentRouteItem item = list[idx];
                  list.RemoveAt(idx);
                  int idxnew = ComputeNewIdx(idx);
                  list.Insert(idxnew, item);
                  form.grid.CurrentCell = form.grid.Rows[idxnew].Cells[0];
               }
            }
         }

         protected abstract bool AllowChange(int idx);
         protected abstract int ComputeNewIdx(int idx);
      }

      protected void ChangeButton_Click(object sender, EventArgs e)
      {
         MoveOperatot mo = new MoveOperatorFactory().GetOperator(this, sender);
         mo.Change();
      }
   }
}

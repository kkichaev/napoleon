using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Reflection;
using GRSoft.NapoleonManager.Properties;

namespace GRSoft.NapoleonManager
{
   public partial class FmMain : Form
   {
      DataSet<string, Agent> dsAgent = Agents.GetDataSet();
      DataSet<int, Division> dsDivision = new DataSet<int, Division>(Division.OBJECT_NAME);
      DataSet<string, AgentInfo> dsAgentInfo = new DataSet<string, AgentInfo>(AgentInfo.OBJECT_NAME);
      DataSet<string, Question> dsQuestion = new DataSet<string, Question>(Question.OBJECT_NAME);
      DataSet<string, TaskQuery> dsTask = new DataSet<string, TaskQuery>(TaskQuery.OBJECT_NAME);
     //DataSet<string, Org> dsOrg = new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
      DataSet<string, Cagent> dsCagent = new DataSet<string, Cagent>(Cagent.OBJECT_NAME);
      DataSet<string, DivisionManager> dsDManagers = new DataSet<string, DivisionManager>(DivisionManager.OBJECT_NAME);

      public static OrgCash orgCash = new OrgCash();
      public static string managerPrefix = string.Empty;

      TimeGridAdapter data = new TimeGridAdapter();

      public FmMain()
      {
         ServerCommand.Category = "adsmanager";

         InitializeComponent();

         timeGrid.grid.TaskColor += new TaskColorEventHandler(timeGrid1_TaskColor);
         timeGrid.grid.TaskClicked += new TaskEventHandler(timeGrid1_TaskClicked);
         timeGrid.grid.TaskDblClicked += new TaskEventHandler(EditTask);
         timeGrid.grid.TaskColor += new TaskColorEventHandler(timeGrid_TaskColor);
         timeGrid.grid.ViewProperty = new AdsViewProp();
         timeGrid.grid.GridDblClicked += new GridEventHandler(NewTask);
         timeGrid.grid.CustomItemDraw += grid_CustomItemDraw;
         timeGrid.grid.GridPastHandler += grid_GridPastHandler;
         timeGrid.grid.Adapter = data;

         dsQuestion.Filter = "idquest is null or idquest is not null";
         timeGrid.grid.GridColor = Color.FromArgb(151, 151, 151);
      }

      void grid_GridPastHandler(object sender, GridPastArgs arg)
      {
         EditBuffer buffer = (EditBuffer)arg.buffer;
         Task source = buffer.DataStored as Task;
         EditBuffer.Operation oper = buffer.CurrentOper;

         if (source != null)
         {
            Agent agent = (arg.band as AgentBand).Agent;
            DateTime dt = calendar.SelectionStart;
            dt = dt.AddHours(arg.hour);

            TaskQuery task = new TaskQuery();
            task.agent = agent;
            task.userid = agent.id;
            task.start = dt;
            task.finish = dt.AddHours(1);
            task.taskid = GRSoft.Network.DataObject.GenId();
            task.created = DateTime.Now;
            task.fio = source.fio;
            task.address = source.address;
            task.text = source.text;
            task.client = source.client;
            task.phone = source.phone;

            EditTask form = new EditTask(task);

            if (form.ShowDialog() == DialogResult.OK)
            {
               BandItem item = new BandItem();
               item.Created = task.created;
               item.Start = task.start;
               item.Finish = task.finish;
               item.Stored = task;
               item.Color = TaskHelper.BkgItemColor(task.solution);
               arg.band.Items.Add(item);
               timeGrid.Invalidate(true);

               if (oper == EditBuffer.Operation.Cut && buffer.BandItem != null)
               {
                  DeleteTask(buffer.BandItem);
               }
            }
         }
      }

      void grid_CustomItemDraw(object sender, GridItemDrawArgs arg)
      {
         Task t = arg.stored as Task;

         if (t != null) 
         {
            Font textFont = new Font(timeGrid.Font, FontStyle.Regular);
            System.Drawing.StringFormat sf = new System.Drawing.StringFormat();
            sf.LineAlignment = StringAlignment.Near;
            sf.Alignment = StringAlignment.Near;
            sf.Trimming = StringTrimming.EllipsisWord;
            const int PADDING = 2;
            
            RectangleF b = new RectangleF(arg.rect.Left + PADDING, arg.rect.Top + PADDING, arg.rect.Width - PADDING, arg.rect.Height - PADDING);

            SizeF ss = new SizeF();
            ss = arg.g.MeasureString(t.text, textFont);

            b.Height = b.Height - (b.Height % ss.Height);
            arg.g.DrawString(t.text, textFont, new SolidBrush(timeGrid.ForeColor), b, sf);
         }
      }

      protected override bool ProcessCmdKey(ref System.Windows.Forms.Message msg, Keys keyData)
      {
         bool result = true;

         if (keyData == Keys.F5)
         {
            btnRefresh.PerformClick();
            result = true;
         }
         else
            result = base.ProcessCmdKey(ref msg, keyData);

         return result;
      }

      class AdsViewProp : TimeGrid.ViewProp
      {
         public override TimeGrid.ViewProp Inflate()
         {
            Config cfg = Config.GetConfig();
            this.addr = cfg.addrView;
            this.name = cfg.clitnView;
            this.text = cfg.textView;

            return base.Inflate();
         }
      }

      void timeGrid_TaskColor(object sender, TaskColorEventArgs arg)
      {
         //if (arg.Task.Solution == TaskAnswer.REJECTED)
         //   arg.Color = Color.Red;
      }

      abstract class StuffNode : TreeNode
      {
         public StuffNode(string text, int imageIndex)
            :base(text, imageIndex, imageIndex)
         { 
         }
         public abstract List<Division.DivisionAgent> Agents { get; }
         public abstract void SendMessage();
      }

      class DivisionNode : StuffNode
      {
         Division d;

         public DivisionNode(Division d)
            :base(d.name, 0)
         {
            this.d = d;
         }

         public override List<Division.DivisionAgent> Agents
         {
            get { return d.agents; }
         }

         public override void SendMessage()
         {
            FmMessage.MessageShow(d);
         }
      }

      class AgentNode : StuffNode
      {
         Division.DivisionAgent a;

         public AgentNode(Division.DivisionAgent a, AgentInfo info)
            :base(a.AgentName, 1)
         {
            this.a = a;

            if (a != null && info != null)
            {
               Text = a.AgentName + info.ToString();
               ForeColor = info.getColor();
            }
         }

         public override List<Division.DivisionAgent> Agents
         {
            get 
            {
               List<Division.DivisionAgent> result = new List<Division.DivisionAgent>();
               result.Add(a);
               return result; 
            }
         }

         public override void SendMessage()
         {
            FmMessage.MessageShow(a.agent);
         }
      }

      private void UpdateUsersTree()
      {
         tvUsers.Nodes.Clear();
         tvUsers.BeginUpdate();
         
         foreach (Division d in dsDivision.Values)
         {
            TreeNode divNode = new DivisionNode(d);
            tvUsers.Nodes.Add(divNode);

            foreach (Division.DivisionAgent a in d.agents)
               if (a != null && a.agent != null && !a.agent.Hidden)
               {
                  AgentNode agentNode = new AgentNode(a, dsAgentInfo.ContainsKey(a.id) ? dsAgentInfo[a.id] : null);
                  divNode.Nodes.Add(agentNode);
               }

            tvUsers.Sort();
         }

         if (tvUsers.Nodes.Count > 0)
         {
            tvUsers.ExpandAll();
            tvUsers.SelectedNode = tvUsers.Nodes[0];
         }

         tvUsers.EndUpdate();
      }

      void timeGrid1_TaskClicked(object sender, TaskEventArgs arg)
      {
      }

      void EditTask(object sender, TaskEventArgs arg)
      {
         if (arg != null && arg.Item != null && arg.Item.Stored != null)
         {
            Task t = arg.Item.Stored as Task;

            if (t != null) 
            {
               EditTask form = new EditTask(t);
               form.btnOK.Enabled = false;

               if (form.ShowDialog() == DialogResult.OK)
               {
                  BandItem i = arg.Item;
                  i.Start = t.start;
                  i.Finish = t.finish;

                  timeGrid.Invalidate(true);
               }
            }
         }
      }

      void timeGrid1_TaskColor(object sender, TaskColorEventArgs arg)
      {
         //if (arg.Task.text == "Есть")
         //   arg.Color = Color.Red;
      }

      private void tvUsers_AfterSelect(object sender, TreeViewEventArgs e)
      {
         data.Clear();

         if (e.Node is StuffNode)
         {
            List<Division.DivisionAgent> list = ((StuffNode)e.Node).Agents;
            list.Sort(new Comparison<Division.DivisionAgent>(delegate(Division.DivisionAgent lhs,
               Division.DivisionAgent rhs){ return lhs.AgentName.CompareTo(rhs.AgentName);}));
            foreach (Division.DivisionAgent d in list)
            {
               if (d == null || d.agent == null || d.agent.Hidden)
                  continue;

               AgentBand band = new AgentBand(d.agent);

               foreach (TaskQuery t in dsTask.Values)
               {
                  if (t.agent != null && t.agent.id.Equals(d.agent.id))
                  {
                     BandItem item = new BandItem();
                     item.Created = t.created;
                     item.Start = t.start;
                     item.Finish = t.finish;
                     item.Stored = t;
                     item.Color = TaskHelper.BkgItemColor(t.solution);

                     band.Items.Add(item);
                  }
               }

               data.AddBand(band);
            }
         }

         timeGrid.Invalidate(true);
      }

      private void miSetting_Click(object sender, EventArgs e)
      {
         if (new FmConfig().ShowDialog() == DialogResult.OK)
         {
            Config cfg = Config.GetConfig();
            timeGrid.grid.Start = 0;
            timeGrid.grid.Finish = 24;
         }
      }

      private void miClose_Click(object sender, EventArgs e)
      {
         Application.Exit();
      }

      private void btnMode_Click(object sender, EventArgs e)
      {
         foreach (Control ctrl in splitContainer2.Panel2.Controls)
            if (ctrl is Button)
               SetButtonFontStyle((Button)ctrl, FontStyle.Regular, Color.WhiteSmoke);

         if (sender is Button)
            SetButtonFontStyle((Button)sender, FontStyle.Bold, Color.Yellow);
      }

      private void SetButtonFontStyle(Button btn, FontStyle style, Color color)
      {
         Font fnt = new Font(btn.Font, style);
         btn.Font = fnt;
         btn.BackColor = color;
      }

      public static readonly string TASK_FILTER = "{0:dd/MM/yyyy};{1:dd/MM/yyyy}";

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         btnRefresh.Enabled = false;
         
         DateTime d1 = calendar.SelectionStart.Date;
         DateTime d2 = calendar.SelectionStart.Date.AddDays(1);
         dsTask.Filter = string.Format(TASK_FILTER, d1, d2);

         List<IDataSet> list = new List<IDataSet>();
        // list.Add(dsOrg);
         list.Add(dsCagent);
         list.Add(dsAgent);
         list.Add(dsDivision);
         list.Add(dsQuestion);
         list.Add(dsTask);
         list.Add(dsDManagers);

         FmWait.StdDataRefresh(this, list, DoLoadData, btnRefresh);
      }

      private void DoLoadData()
      {
         UpdateUsersTree();
         orgCash.Load();

         Config cfg = Config.GetConfig();

         if (cfg.login.Trim().Length > 0 && dsDManagers.ContainsKey(cfg.login))
         {
            DivisionManager dm = dsDManagers[cfg.login];
            managerPrefix = dm.prefix;

            bool vsbl = dm.HaveRight(RightTokens.Get("ShowADSReports"), RightActions.Write);
            btnUserLocation_.Visible = vsbl;
            miDistance.Visible = vsbl;
            miRoute.Visible = vsbl;
         }

         timeGrid.Invalidate(true);
      }

      private void Main_Load(object sender, EventArgs e)
      {
         Config cfg = Config.GetConfig();
         timeGrid.grid.Start = 0;
         timeGrid.grid.Finish = 24;

         btnRefresh.PerformClick();
         timeGrid.Focus();
      }

      private void btnUser_Click(object sender, EventArgs e)
      {
         new Users().Show();
      }

      private void NewTask(object sender, GridEventArgs arg)
      {
         if (arg.band != null && arg.band is AgentBand)
         {
            Agent agent = (arg.band as AgentBand).Agent;
            DateTime dt = calendar.SelectionStart;
            dt = dt.AddHours(arg.hour);

            TaskQuery task = new TaskQuery();
            task.agent = agent;
            task.userid = agent.id;
            task.start = dt;
            task.finish = dt.AddHours(1);
            task.taskid = GRSoft.Network.DataObject.GenId();
            task.id = task.taskid;
            task.created = DateTime.Now;

            EditTask form = new EditTask(task);

            if (form.ShowDialog() == DialogResult.OK)
            {
               BandItem item = new BandItem();
               item.Created = task.created;
               item.Start = task.start;
               item.Finish = task.finish;
               item.Stored = task;
               item.Color = TaskHelper.BkgItemColor(task.solution);
               arg.band.Items.Add(item);
               timeGrid.Invalidate(true);
            }
         }
      }

      private string GetNextTaskNumber()
      {
         return string.Format("{0}{1:MMdd}{2:D3}",managerPrefix, calendar.SelectionStart, dsTask.Count + 1);
      }

      private void miDel_Click(object sender, EventArgs e)
      {
         if (sender is ToolStripItem && Utils.AskToApplyDelete() == DialogResult.OK)
         {
            BandItem th = (BandItem)((ToolStripItem)sender).Tag;
            DeleteTask(th);
         }
      }

      private void DeleteTask(BandItem th)
      {
         Task task = th.Stored as Task;

         if (th != null)
         {
            task.rem = 1;
            List<IDataSet> list = new List<IDataSet>();
            DataSet<string, Task> ds = new DataSet<string, Task>(Task.OBJECT_NAME, false);
            ds.Add(task.taskid, task);
            list.Add(ds);

            bool result = DataModule.WriteDataSet(list, Config.GetConfig().GetConnection());

            if (!result)
            {
               task.rem = 0;
               Utils.ErrorSaveDB();
            }
            else
            {
               timeGrid.grid.Adapter.RemoveBandItem(th);
               timeGrid.Invalidate(true);
            }
         }
      }

      private void miAbout_Click(object sender, EventArgs e)
      {
         new About().Show();
      }

      private void miWiki_Click(object sender, EventArgs e)
      {
         System.Diagnostics.Process.Start(Resources.wikipagehtml);
      }

      private void tvUsers_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Button == MouseButtons.Right)
         {
            TreeNode selNode = tvUsers.GetNodeAt(e.Location);

            if (selNode != null && selNode is StuffNode)
            {
               tvUsers.SelectedNode = selNode;
               ((StuffNode)selNode).SendMessage();
            }
         }
      }

      private void miCopy_Click(object sender, EventArgs e)
      {
         timeGrid.grid.Buffer.Copy();
      }

      private void miEdit_DropDownOpening(object sender, EventArgs e)
      {
         miPast.Enabled = !(timeGrid.grid.Buffer.BandItem == null);
         miCut.Enabled = timeGrid.grid.SelectionItem != null && timeGrid.grid.SelectionItem.BandItem != null;
         miCopy.Enabled = timeGrid.grid.SelectionItem != null && timeGrid.grid.SelectionItem.BandItem != null;
      }

      private void miCut_Click(object sender, EventArgs e)
      {
         timeGrid.grid.Buffer.Cut();
      }

      private void btnQuest_Click(object sender, EventArgs e)
      {
         new FmQuestionary().Show();
      }

      private void btnTaskReport_Click(object sender, EventArgs e)
      {
         
      }

      private void btJournal_Click(object sender, EventArgs e)
      {
         new FmJournal().Show();
      }

      private void miRoute_Click(object sender, EventArgs e)
      {
         new FmRoute(string.Empty, DateTime.Now).Show();
      }

      private void miOrder_Click(object sender, EventArgs e)
      {
         new FmUserOrder().Show();
      }

      private void miJournal_Click(object sender, EventArgs e)
      {
         MessageBox.Show("Б. Журнал диспетчера (форму дам, сейчас делать не надо) ");
      }

      private void miDistance_Click(object sender, EventArgs e)
      {
         new FmGPSReport().Show();
      }

      private void miUsers_Click(object sender, EventArgs e)
      {
         new Users().Show();
      }

      private void itemContextMenuStrip_Opening(object sender, CancelEventArgs e)
      {
         BandItem b = ((TaskContextMenuStrip)sender).Task;

         cmiDel.Enabled = false;
         cmiCut.Enabled = false;
         cmiCopy.Enabled = false;
         cmiPast.Enabled = timeGrid.grid.BufferContainsItem();

         if(b != null)
         {
            TaskQuery t = b.Stored as TaskQuery;

            if (t != null)
            {
               cmiDel.Enabled = t.solution == 0;
               cmiCut.Enabled = t.solution == 0;
               cmiCopy.Enabled = t.solution == 0;
            }
         }
      }

      private void btnUserLocation_Click(object sender, EventArgs e)
      {
         new FmUserLocation().Show();
      }

      private void miPast_Click(object sender, EventArgs e)
      {
         timeGrid.grid.Buffer.Past();
      }
   }
}

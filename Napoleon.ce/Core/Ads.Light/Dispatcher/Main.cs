using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.Ads.Dispatcher.Properties;
using System.Reflection;

namespace GRSoft.Ads.Dispatcher
{
   public partial class Main : Form, IReloadData
   {
      DataSet<string, Agent> dsAgent = Agents.GetDataSet();
      DataSet<int, Division> dsDivision = new DataSet<int, Division>(Division.OBJECT_NAME);
      DataSet<string, TaskInfo> dsTaskInfo = new DataSet<string, TaskInfo>(TaskInfo.OBJECT_NAME);
      DataSet<string, AgentInfo> dsAgentInfo = new DataSet<string, AgentInfo>(AgentInfo.OBJECT_NAME);
      DataSet<string, Question> dsQuestion = new DataSet<string, Question>(Question.OBJECT_NAME);

      public Main()
      {
         InitializeComponent();

         timeGrid.TaskColor += new TaskColorEventHandler(timeGrid1_TaskColor);
         timeGrid.TaskClicked += new TaskEventHandler(timeGrid1_TaskClicked);
         timeGrid.TaskDblClicked += new TaskEventHandler(timeGrid1_TaskDblClicked);
         timeGrid.TaskColor += new TaskColorEventHandler(timeGrid_TaskColor);
         timeGrid.ViewProperty = new AdsViewProp();

         btnMode_Click(btnTask, EventArgs.Empty);

         dsQuestion.Filter = "idquest is null or idquest is not null";
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
            Text = a.AgentName + info.ToString();
            ForeColor = info.getColor();
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

      private class Data : GRSoft.Network.DataObject
      {
         public DateTime start;
         public DateTime finish;
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

      void timeGrid1_TaskDblClicked(object sender, TaskEventArgs arg)
      {
         if (dsTaskInfo.ContainsKey(arg.Task.id))
         {
            Task t = dsTaskInfo[arg.Task.id];

            EditTask editTask = new EditTask();
            editTask.AgentTask = t.agent;
            editTask.Start = t.start;
            editTask.Finish = t.finish;
            editTask.Task = t.text;
            editTask.City = t.city;
            editTask.Street = t.street;
            editTask.House = t.house;
            editTask.ClientName = t.clientname;
            editTask.ClientPhone = t.clientphone;
            editTask.Questions = t.questions;
            editTask.SaveTaskChecker = new EditTask.CheckSaveTask(delegate()
               {
                  return TryToSaveTask(t.id, t.agent, editTask);
               });

            if (editTask.ShowDialog() == DialogResult.OK)
            {
               arg.Task.text = editTask.Task;
               arg.Task.start = editTask.Start;
               arg.Task.finish = editTask.Finish;
               arg.Task.city = editTask.City;
               arg.Task.street = editTask.Street;
               arg.Task.house = editTask.House;
               timeGrid.Invalidate();
            }
         }
      }

      void timeGrid1_TaskColor(object sender, TaskColorEventArgs arg)
      {
         if (arg.Task.text == "Есть")
            arg.Color = Color.Red;
      }

      private void tvUsers_AfterSelect(object sender, TreeViewEventArgs e)
      {
         timeGrid.Bands.Clear();

         if (e.Node is StuffNode)
         {
            List<Division.DivisionAgent> list = ((StuffNode)e.Node).Agents;
            list.Sort(new Comparison<Division.DivisionAgent>(delegate(Division.DivisionAgent lhs,
               Division.DivisionAgent rhs){ return lhs.AgentName.CompareTo(rhs.AgentName);}));
            foreach (Division.DivisionAgent d in list)
            {
               AgentBand band = new AgentBand(d.agent);

               foreach (Task t in dsTaskInfo.Values)
               {
                  if (t.agent != null && t.agent.id.Equals(d.agent.id))
                  {
                     TaskHead th = new TaskHead();
                     th.id = t.id;
                     th.text = t.text;
                     th.start = t.start;
                     th.finish = t.finish;
                     th.city = t.city;
                     th.street = t.street;
                     th.house = t.house;
                     th.clientname = t.clientname;
                     th.clientphone = t.clientphone;

                     band.Tasks.Add(th);
                  }
               }

               timeGrid.Bands.Add(band);
            }
         }
         timeGrid.Invalidate();
      }

      private void miSetting_Click(object sender, EventArgs e)
      {
         Setting setting = new Setting();
         setting.settingOnChange += new Setting.SettingOnChange(
            delegate(Config cfg) 
            {
               timeGrid.HourStart = (short)(cfg.hourStart - 2);
               timeGrid.HourEnd = (short)(cfg.hourEnd + 2);
            });
         setting.Show();
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

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         btnRefresh.Enabled = false;

         Data data = new Data();
         data.start = calendar.SelectionStart.Date;
         data.finish = calendar.SelectionStart.Date.AddDays(1);

         Report rTask = new Report(TaskInfo.REPORT_NAME, data, dsTaskInfo);
         Report rAgentInfo = new Report(AgentInfo.REPORT_NAME, data, dsAgentInfo);
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsAgent);
         list.Add(rTask);
         list.Add(dsDivision);
         list.Add(rAgentInfo);
         list.Add(dsQuestion);

         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               list, FmWait.ProgressIndicator));
      }

      private void Main_Load(object sender, EventArgs e)
      {
         Config cfg = Config.GetConfig();
         timeGrid.HourStart = (short)(cfg.hourStart - 2);
         timeGrid.HourEnd = (short)(cfg.hourEnd + 2);
      }

      void DataProcessed(System.Object setnder, EventArgs e)
      {
         Utils.DataProcessed(this, btnRefresh);
      }

      private void DataConnectionError(EDataResponse e)
      {
         Utils.DataConnectionError(this, btnRefresh, e.Msg);
      }

      void IReloadData.ReloadData()
      {
         UpdateUsersTree();
      }

      private void btnUser_Click(object sender, EventArgs e)
      {
         new Users().Show();
      }

      private bool TryToSaveTask(string id, Agent agent, EditTask editTask)
      {
         bool result = false;
         
         DataSet<string, Task> taskList = new DataSet<string, Task>(Task.OBJECT_NAME, false);

         TaskInfo task = null;

         if (dsTaskInfo.ContainsKey(id))
            task = dsTaskInfo[id];
         else
         {
            task = new TaskInfo();
            dsTaskInfo.Add(id, task);
         }

         task.id = id;
         task.agent = agent;
         task.city = editTask.City;
         task.street = editTask.Street;
         task.house = editTask.House;
         task.text = editTask.Task;
         task.start = editTask.Start;
         task.finish = editTask.Finish;
         task.clientname = editTask.ClientName;
         task.clientphone = editTask.ClientPhone;
         task.questions = editTask.Questions;
         taskList.Add(task.id, task);

         List<IDataSet> list = new List<IDataSet>();
         list.Add(taskList);

         result = DataModule.UpdateDataSet(list, null, null, Config.GetConfig().GetConnection());

         if (!result)
            Utils.ErrorSaveDB();

         return result;
      }

      private void timeGrid_GridDblClicked(object sender, GridEventArgs arg)
      {
         EditTask editTask = new EditTask();
         
         if (arg.band != null && arg.band is AgentBand)
         {
            Agent agent = (arg.band as AgentBand).Agent;
            editTask.AgentTask = agent;
            DateTime dt = calendar.SelectionStart;
            dt = dt.AddHours(arg.hour);
            editTask.Start = dt;
            editTask.Finish = dt.AddHours(1);
            string id = Utils.GUID;

            editTask.SaveTaskChecker = new EditTask.CheckSaveTask(delegate()
            {
               return TryToSaveTask(id, agent, editTask);
            });

            if (editTask.ShowDialog() == DialogResult.OK)
            {
               TaskHead th = new TaskHead();
               th.id = id;
               th.text = editTask.Task;
               th.start = editTask.Start;
               th.finish = editTask.Finish;
               th.clientname = editTask.ClientName;
               th.clientphone = editTask.ClientPhone;
               arg.band.Tasks.Add(th);
               timeGrid.Invalidate();
            }
         }
      }

      private void miDel_Click(object sender, EventArgs e)
      {
         if (sender is ToolStripItem && Utils.AskToApplyDelete() == DialogResult.OK)
         {
            TaskHead th = (TaskHead)((ToolStripItem)sender).Tag;

            List<IDataSet> rmvSet = new List<IDataSet>();
            DataSet<string, Task> rmvTask = new DataSet<string, Task>(Task.OBJECT_NAME, false);
            Task task = new Task();
            task.id = th.id;
            rmvTask.Add(task.id, task);
            rmvSet.Add(rmvTask);

            bool result = DataModule.UpdateDataSet(null, rmvSet, null,
               Config.GetConfig().GetConnection());

            if (!result)
               Utils.ErrorSaveDB();
            else
            {
               Band b = timeGrid.Bands[timeGrid.Bands.IndexOf(th.Band)];
               b.Tasks.Remove(th);

               timeGrid.Invalidate();
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

      private void miTask_Click(object sender, EventArgs e)
      {
         new TaskViewProp().ShowDialog();
         timeGrid.Invalidate();
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
         timeGrid.Buffer.Copy();
      }

      private Task TaskFromTaskHead(TaskHead taskHead)
      {
         Task result = new Task();
         FieldInfo[] fis = typeof(Task).GetFields();
         
         foreach(FieldInfo fi in fis)
         {
            FieldInfo src = typeof(TaskHead).GetField(fi.Name, 
               BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.Public | BindingFlags.FlattenHierarchy);

            if(src != null)
               fi.SetValue(result, src.GetValue(taskHead));
         }

         return result;
      }

      private void miEdit_DropDownOpening(object sender, EventArgs e)
      {
         miPast.Enabled = !(timeGrid.Buffer.Task == null);
         miCut.Enabled = timeGrid.SelectionItem != null && timeGrid.SelectionItem.Task != null;
         miCopy.Enabled = timeGrid.SelectionItem != null && timeGrid.SelectionItem.Task != null;
      }

      private void miPast_Click(object sender, EventArgs e)
      {
         TimeGrid.EditBuffer.Operation oper = timeGrid.Buffer.Past(calendar.SelectionStart);
         Task task = TaskFromTaskHead(timeGrid.SelectionItem.Task);
         task.agent = ((AgentBand)timeGrid.SelectionItem.Band).Agent;
         DataSet<string, Task> ds = new DataSet<string, Task>(Task.OBJECT_NAME, false);
         ds.Add(task.id, task);
         List<IDataSet> wrSet = new List<IDataSet>();
         wrSet.Add(ds);
         List<IDataSet> rmSet = new List<IDataSet>();
         
         if(oper == TimeGrid.EditBuffer.Operation.Cut)
         {
            Task rmTask = TaskFromTaskHead(timeGrid.Buffer.Task);
            DataSet<string, Task> rmds = new DataSet<string, Task>(Task.OBJECT_NAME, false);
            rmds.Add(rmTask.id, rmTask);
            rmSet.Add(rmds);
         }

         bool result = DataModule.UpdateDataSet(wrSet, rmSet, null, Config.GetConfig().GetConnection());

        if (!result)
           Utils.ErrorSaveDB();

      }

      private void miCut_Click(object sender, EventArgs e)
      {
         timeGrid.Buffer.Cut();
      }

      private void btnQuest_Click(object sender, EventArgs e)
      {
         new FmQuestionary().Show();
      }

      private void btnTaskReport_Click(object sender, EventArgs e)
      {
         new FmTaskReport().Show();
      }
   }

   class AgentBand : Band
   {
      private Agent agent;
      public AgentBand(Agent agent)
         : base(agent.Name)
      {
         this.agent = agent;
      }

      public Agent Agent { get { return agent; } }
   }
}

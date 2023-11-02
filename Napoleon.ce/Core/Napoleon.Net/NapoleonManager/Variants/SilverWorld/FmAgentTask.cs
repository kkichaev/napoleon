using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using GRSoft.NapoleonManager.Properties;
using System.IO;

namespace GRSoft.NapoleonManager
{
   public partial class FmAgentTask : Form
   {
      SimpleDataSet<OrgTaskInfo> dsOrgTask = new SimpleDataSet<OrgTaskInfo>("Result", false);
      SimpleDataSet<OrgTaskReport> dsOrgTaskReport = new SimpleDataSet<OrgTaskReport>("Result", false);
      private static int docNumer = 0;

      public FmAgentTask()
      {
         InitializeComponent();
      }

      private void FmAgentTask_Load(object sender, EventArgs e)
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

         list.Sort(new Comparison<Agent>(delegate(Agent a1, Agent a2) { return a1.Name.CompareTo(a2.Name); }));
         cbAgent.Items.AddRange(list.ToArray());

         dtpStart.Value = DateTime.Now;
         dtpFinish.Value = DateTime.Now;
      }

      public class Data : GRSoft.Network.DataObject
      {
         public DateTime start;
         public DateTime finish;
         public string agentID = string.Empty;
         public string agentName = string.Empty;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         Agent a = cbAgent.SelectedItem as Agent;

         if (a != null)
         {
            DataModule.SetDataRepsonceHandlers(DataLoaded, DataConnectionError);
            Data data = new Data();
            data.start = dtpStart.Value.Date;
            data.finish = dtpFinish.Value.Date.AddDays(1);
            data.agentID = a.id;
            Report r = new Report("orgtask", data, dsOrgTask);
            FmWait.ShowForm(this, DataModule
               .RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator));
         }
         else
            MessageBox.Show("Выберите агента");

      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();

         Invoke(new EmptyParamHandler(delegate
         {
            FmWait.CloseForm();
            btnRefresh.Enabled = true;

            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      private void DataLoaded(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         BeginInvoke(new EmptyParamHandler(delegate { FmWait.CloseForm(); FillData(); }));
      }

      private void ReportLoaded(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         BeginInvoke(new EmptyParamHandler(delegate { FmWait.CloseForm(); ShowData(); }));
      }

      private void ShowData()
      {
         if (dsOrgTaskReport.Count > 0)
         {
            string fileName = String.Format("orgtask{0}.html", docNumer++);
            string path = System.IO.Path.GetTempPath() + fileName; //.GetTempFileName();
            using (StreamWriter sw = new StreamWriter(path))
            {

               sw.Write(dsOrgTaskReport[0].html.ToString());
               sw.Flush();
            }

            OpenLink.NewWindow(path);
         }
      }

      void FillData()
      {
         List<OrgTaskInfo> data = new List<OrgTaskInfo>();
         data.AddRange(dsOrgTask.Values);
         data.Sort(new Comparison<OrgTaskInfo>(delegate(OrgTaskInfo r1, OrgTaskInfo r2){ return r1.name.CompareTo(r2.name); }));

         dgvTask.DataSource = data;
      }

      private void dgvTask_DoubleClick(object sender, EventArgs e)
      {

      }

      private void dgvTask_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         DataGridView dgv = (DataGridView)sender;
         OrgTaskInfo data = dgv.Rows[e.RowIndex].DataBoundItem as OrgTaskInfo;
         Agent a = cbAgent.SelectedItem as Agent;

         if (data != null && a != null)
            FmAgentTaskList.ShowForm(data, dtpStart.Value.Date, dtpFinish.Value.Date, a.id);
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         Agent a = cbAgent.SelectedItem as Agent;

         if (a != null)
         {
            DataModule.SetDataRepsonceHandlers(ReportLoaded, DataConnectionError);
            Data data = new Data();
            data.start = dtpStart.Value.Date;
            data.finish = dtpFinish.Value.Date.AddDays(1);
            data.agentID = a.id;
            data.agentName = a.Name;
            Report r = new Report("orgtask_report", data, dsOrgTaskReport);
            FmWait.ShowForm(this, DataModule
               .RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator));
         }
         else
            MessageBox.Show("Выберите агента");
      }
   }

   public class OrgTaskInfo : GRSoft.Network.DataObject
   {
      public string id = string.Empty;
      public string name = string.Empty;
      public int done = 0;
      public int missed = 0;

      public string Name { get { return name; } }
      public int Done { get { return done; } }
      public int Missed { get { return missed; } }
   }

   public class OrgTaskReport : GRSoft.Network.DataObject
   {
      public string html = string.Empty;
   }

   internal class TaskDoc : ScriptDocument
   {
      internal TaskDoc()
         : base("TaskDone", "Задачи", Resources.quest_doc)
      {
      }
   }
}

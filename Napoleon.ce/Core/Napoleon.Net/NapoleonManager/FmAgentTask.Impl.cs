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
using System.Collections;
using GRSoft.NapoleonManager.Utils;
using System.Text.RegularExpressions;

namespace GRSoft.NapoleonManager
{
   public partial class FmAgentTask : Form
   {
      SimpleDataSet<OrgTaskInfo> dsOrgTask = new SimpleDataSet<OrgTaskInfo>("Result", false);
      SimpleDataSet<OrgTaskReport> dsOrgTaskReport = new SimpleDataSet<OrgTaskReport>("Result", false);
      private static int docNumer = 0;
      List<OrgTaskInfo> src = new List<OrgTaskInfo>();
      System.Windows.Forms.Timer t1 = new System.Windows.Forms.Timer();

      public void __Initing()
      {
         t1.Interval = 500;
         t1.Tick += T1_Tick;
      }

      private void T1_Tick(object sender, EventArgs e)
      {
         t1.Stop();
         string filter = tsbFind.Text;
         List<OrgTaskInfo> res = src;
         if (filter.Length != 0)
         { 
            res = new List<OrgTaskInfo>();
            string pattern = @"(.*)";
            string[] parts = filter.Split(new char[] { ' ' });
            foreach (string p in parts)
            {
               pattern += p + "(.*)";
            }

            foreach (OrgTaskInfo oi in src)
            {
               string srct = oi.name + oi.address;
               if (Regex.Match(srct, pattern, RegexOptions.IgnoreCase).Success)
               {
                  res.Add(oi);
               }
            }
         }
         dgvTask.DataSource = new SortableBindingList<OrgTaskInfo>(res);
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
         public string mode = "";
      }

      protected virtual void StartRefresh() { }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         Agent a = cbAgent.SelectedItem as Agent;

         if (a != null)
         {
            StartRefresh();
            DataModule.SetDataRepsonceHandlers(DataLoaded, DataConnectionError);
            Data data = new Data();
            data.start = dtpStart.Value.Date;
            data.finish = dtpFinish.Value.Date.AddDays(1);
            data.agentID = a.id;
            data.mode = "list";
            Report r = new Report("orgtask", data, GetResultDataSet());
            FmWait.ShowForm(this, DataModule
               .RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator));
         }
         else
            MessageBox.Show("Выберите агента");

      }

      protected virtual IDataSet GetResultDataSet()
      {
         return dsOrgTask;
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
      private void tsbFind_TextChanged(object sender, EventArgs e)
      {
         t1.Stop();
         t1.Start();
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

      protected virtual void FillData()
      {
         tsbFind.Text = "";
         dgvTask.DataSource = CollectData();
      }

      protected virtual IList CollectData()
      {
         src = new List<OrgTaskInfo>();
         foreach (OrgTaskInfo info in GetResultDataSet().Data)
            src.Add(info);

         src.Sort((v1, v2) =>
         {
            return v1.name.CompareTo(v2.name);
         });
         return new SortableBindingList<OrgTaskInfo>(src);
         //return CollectDataSource<OrgTaskInfo>(new Comparison<OrgTaskInfo>(delegate(OrgTaskInfo r1, OrgTaskInfo r2) { return r1.name.CompareTo(r2.name); })); ;
      }

      protected BindingListView<ItemType> CollectDataSource<ItemType>(Comparison<ItemType> comparator)
      {
         List<ItemType> data = new List<ItemType>();

         foreach (ItemType info in GetResultDataSet().Data)
            data.Add(info);

         data.Sort(comparator);
         DataTable dt = new DataTable("data");

         BindingListView<ItemType> result = new BindingListView<ItemType>(data);
         return result;
      }

      protected virtual object InflateDataBound(object dataBoundItem)
      {
         return ((ObjectView<OrgTaskInfo>)dataBoundItem).Object; ;
      }

      private void dgvTask_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         if (e.RowIndex >= 0)
         {
            OrgTaskInfo data = (OrgTaskInfo)dgvTask.Rows[e.RowIndex].DataBoundItem;
            Agent a = cbAgent.SelectedItem as Agent;

            if (data != null && a != null)
               FmAgentTaskList.ShowForm(data, dtpStart.Value.Date, dtpFinish.Value.Date, a.id);
         }
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         (new FmTaskReport()).Show();

         //Agent a = cbAgent.SelectedItem as Agent;

         //if (a != null)
         //{
         //   DataModule.SetDataRepsonceHandlers(ReportLoaded, DataConnectionError);
         //   Data data = new Data();
         //   data.start = dtpStart.Value.Date;
         //   data.finish = dtpFinish.Value.Date.AddDays(1);
         //   data.agentID = a.id;
         //   data.agentName = a.Name;
         //   data.mode = "report";
         //   Report r = new Report("orgtask", data, dsOrgTaskReport);
         //   FmWait.ShowForm(this, DataModule
         //      .RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator));
         //}
         //else
         //   MessageBox.Show("Выберите агента");
      }
   }

   public class OrgTaskInfo : GRSoft.Network.DataObject
   {
      public string id = string.Empty;
      public string name = string.Empty;
      public string address = "";
      public int done = 0;
      public int missed = 0;

      public string Name { get {
         return Config.GetConfig().isFullOrgName ?
             String.Format("{0} ({1})", name, address)
             : name;
         } 
      }
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

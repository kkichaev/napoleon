using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.Ads.Dispatcher
{
   public partial class FmTaskReport : Form, IReloadData
   {
      DataSet<string, TaskInfo> dsTaskInfo;

      public FmTaskReport()
      {
         InitializeComponent();
         dsTaskInfo = (DataSet<string, TaskInfo>)DataModule.Get(TaskInfo.OBJECT_NAME) ??
            new DataSet<string, TaskInfo>(TaskInfo.OBJECT_NAME);
      }

      private void FmTaskReport_Load(object sender, EventArgs e)
      {
         cbAgents.Items.Clear();
         cbAgents.Items.Add("<Все>");
         Agents dsAgents = Agents.GetDataSet();

         if (dsAgents != null && dsAgents.Data.Count > 0)
         {
            List<Agent> list = new List<Agent>();
            foreach(Agent agent in dsAgents.Data)
               list.Add(agent);

            list.Sort((lhs, rhs) => lhs.Name.CompareTo(rhs.Name));
            cbAgents.Items.AddRange(list.ToArray());
         }

         cbAgents.SelectedIndex = 0;
      }

      private class Data : GRSoft.Network.DataObject
      {
         public string agentid = string.Empty;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         Data data = new Data();
         data.agentid = cbAgents.SelectedIndex == 0 ? string.Empty : 
            ((Agent)cbAgents.Items[cbAgents.SelectedIndex]).id;
         Report rTask = new Report(TaskInfo.REPORT_NAME, data, dsTaskInfo);
         List<IDataSet> list = new List<IDataSet>();
         list.Add(rTask);
         DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);

         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               list, FmWait.ProgressIndicator));
      }

      void DataProcessed(System.Object setnder, EventArgs e)
      {
         Utils.DataProcessed(this, btnRefresh);
      }

      private void DataConnectionError(EDataResponse e)
      {
         Utils.DataConnectionError(this, btnRefresh, e.Msg);
      }

      #region IReloadData Members

      public void ReloadData()
      {
         List<TaskInfo> list = new List<TaskInfo>();
         list.AddRange(dsTaskInfo.Values);
         dgvTask.DataSource = list;
      }

      #endregion
   }
}

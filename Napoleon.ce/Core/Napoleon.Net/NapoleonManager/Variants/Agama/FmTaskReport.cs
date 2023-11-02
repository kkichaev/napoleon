using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using System.IO;
using System.Runtime.InteropServices;

namespace GRSoft.NapoleonManager
{
   public partial class FmTaskReport : Form
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);
      static int count = 1;

      public FmTaskReport()
      {
         InitializeComponent();
      }

      private void FmTaskReport_Load(object sender, EventArgs e)
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
         lbAgent.Items.AddRange(list.ToArray());

         dtpStart.Value = DateTime.Now;
         dtpFinish.Value = DateTime.Now;
      }

      private void btnSelect_Click(object sender, EventArgs e)
      {
         SelectItems(true);
      }

      private void SelectItems(bool value)
      {
         for (int i = 0; i < lbAgent.Items.Count; i++)
            lbAgent.SetItemChecked(i, value);
      }

      private void btnUnselect_Click(object sender, EventArgs e)
      {
         SelectItems(false);
      }

      public class Data : GRSoft.Network.DataObject
      {
         [ItemType(typeof(DataItem))]
         public List<DataItem> items = new List<DataItem>();
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public string mode = "hello";
      }

      public class DataItem : GRSoft.Network.DataObject
      {
         public string id = string.Empty;
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         const string REPORT_NAME = "orgtaskexcel_report";

         Data data = new Data();

         foreach (object obj in lbAgent.CheckedItems)
         {
            DataItem di = new DataItem();
            di.id = ((Agent)obj).id;
            data.items.Add(di);
         }

         data.start = dtpStart.Value.Date;
         data.finish = dtpFinish.Value.Date.AddDays(1);

         Result result = new Result();
         SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);
         Report r = new Report(REPORT_NAME, data, resultSet);

         Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
         FmWait.ShowForm(this, th);
         th.Join();
         FmWait.CloseForm();

         if (resultSet.Count > 0)
         {
            Result res = resultSet[0];
            if (res.file.Length > 0)
            {
               string fileName = Path.GetTempPath() + "\\" + REPORT_NAME + count.ToString() + ".xlsx";
               while (File.Exists(fileName))
               {
                  count++;
                  fileName = Path.GetTempPath() + "\\" + REPORT_NAME + count.ToString() + ".xlsx";
               }
               File.WriteAllBytes(fileName, res.file);
               ShellExecute(IntPtr.Zero, "open", fileName, "", "", 1);
            }
         }
         else
            MessageBox.Show("Ошибка построения отчета");

      }
   }
}

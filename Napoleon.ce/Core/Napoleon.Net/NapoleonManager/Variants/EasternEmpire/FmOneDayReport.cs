using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.IO;
using System.Runtime.InteropServices;

namespace GRSoft.NapoleonManager
{
   public partial class FmOneDayReport : Form
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);


      
      private const string REPORT_NAME = "oneday";
      private static int count = 0;
      SimpleDataSet<Result> dsResult = new SimpleDataSet<Result>("Result", false);

      public FmOneDayReport()
      {
         InitializeComponent();
      }

      private void FmOneDayReport_Load(object sender, EventArgs e)
      {
         List<Agent> list = new List<Agent>();
         foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
            list.Add(a);

         list.Sort(new Comparison<Agent>(delegate(Agent lhs, Agent rhs) { return lhs.Name.CompareTo(rhs.Name); }));
         cbAgent.Items.AddRange(list.ToArray());

         if (cbAgent.Items.Count > 0)
            cbAgent.SelectedIndex = 0;
      }

      class Data :  GRSoft.Network.DataObject
      {
         public string agent = string.Empty;
         public DateTime date = DateTime.MinValue;
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         Agent a = cbAgent.SelectedItem as Agent;

         if (a != null)
         {
            Data data = new Data();
            data.agent = a.id;
            data.date = dtpDate.Value.Date;
            Report reportResultSet = new Report(REPORT_NAME, data, dsResult);
            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(reportResultSet);
            FmWait.StdDataRefresh(this, upd, DoReport);
         }
         else
            MessageBox.Show("Выберите агента");
      }

      private void DoReport()
      {
         if (dsResult.Count > 0)
         {
            Result res = dsResult[0];
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

      }
   }
}

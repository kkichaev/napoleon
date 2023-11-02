using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrdVztRep : Form
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);
      static readonly string REPORT_NAME = "ov_report";
      static int count = 1;

      public FmOrdVztRep()
      {
         InitializeComponent();
      }

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      class Param : GRSoft.Network.DataObject
      {
         public string userid = string.Empty;
         public string agent = string.Empty;
         public DateTime start = DateTime.Now;
         public DateTime finish = DateTime.Now;
      }

      protected void MakeReport()
      {
         Agent curAgent = cbAgents.SelectedItem as Agent;

         if (curAgent == null)
            return;

         Param data = new Param();
         data.userid = curAgent.id;
         data.start = dtpStart.Value.Date;
         data.finish = dtpFinish.Value.Date;
         data.agent = curAgent.name;

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

      private void btnReport_Click(object sender, EventArgs e)
      {
         MakeReport();
      }

      public DateTime Start { set { dtpStart.Value = value; } }
      public DateTime Finish { set { dtpFinish.Value = value; } }
      
      private void FmOrdVztRep_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;

               cbAgents.Items.Add(da.agent);
            }

            if (cbAgents.Items.Count > 0)
               cbAgents.SelectedIndex = 0;
         }
      }
   }
}

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using GRSoft.Network;
using System.Threading;
using System.IO;

namespace GRSoft.NapoleonManager
{
   public partial class FmFaceReport : Form
   {
      public FmFaceReport()
      {
         InitializeComponent();
      }

      private void btnAgentRpt_Click(object sender, EventArgs e)
      {
         FaceReport.Do(dtpStart.Value.Date, dtpFinish.Value.Date.AddDays(1), 1, this);
      }
   }

   class FaceReport
   {
      class Data : GRSoft.Network.DataObject
      {
         public DateTime start;
         public DateTime finish;
         public int type;
         public int divisionID;
      }

      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);

      //static DataSet<string, MonitoringItem> dsItems = null;
      static int count = 1;

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      public static void Do(DateTime begin, DateTime end, int type, Form owner)
      {
         if (CurrentUser.user == null)
         {
            MessageBox.Show("Перед построением отчет обновите данные о подразделениях");
            return;
         }

         Data data = new Data();
         data.start = begin;
         data.finish = end;
         data.type = type;
         data.divisionID = CurrentUser.user.Division.id;
        
         Result result = new Result();
         SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);
         Report r = new Report("face_report", data, resultSet);

         Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
         FmWait.ShowForm(owner, th);
         th.Join();
         FmWait.CloseForm();

         if (resultSet.Count > 0)
         {
            Result res = resultSet[0];
            if (res.file != null && res.file.Length > 0)
            {
               string fileName = Path.GetTempPath() + "\\face_report" + count.ToString() + ".xlsx";
               while (File.Exists(fileName))
               {
                  count++;
                  fileName = Path.GetTempPath() + "\\face_report" + count.ToString() + ".xlsx";
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

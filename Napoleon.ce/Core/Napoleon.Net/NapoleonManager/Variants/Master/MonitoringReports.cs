using GRSoft.NapoleonManager.Reports.Excel;
using System.Collections.Generic;
using GRSoft.Network;
using System;
using System.Windows.Forms;
using System.Threading;
using System.IO;
using System.Runtime.InteropServices;
namespace GRSoft.NapoleonManager
{
   class MonitoringReports
   {
      [DllImport("shell32.dll")] 
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);

      //static DataSet<string, MonitoringItem> dsItems = null;
      static int count = 1;

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      public static void Do(DateTime begin, DateTime end, String agentId, Form owner)
      {
         MonReportParams.Data data = new MonReportParams.Data();
         data.date = begin;
         data.dateEnd = end;
         MonReportParams dlg = new MonReportParams(data);
         dlg.SetSelectedAgent(agentId);
         if (dlg.ShowDialog() == DialogResult.OK)
         {
            Result result = new Result();
            SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);
            Report r = new Report("monitor_report", data, resultSet);

            Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
            FmWait.ShowForm(owner, th);
            th.Join();
            FmWait.CloseForm();

            if (resultSet.Count > 0)
            {
               Result res = resultSet[0];
               if (res.file.Length > 0)
               {
                  string fileName = Path.GetTempPath() + "\\monitor_report" + count.ToString() + ".xlsx";
                  while (File.Exists(fileName))
                  {
                     count++;
                     fileName = Path.GetTempPath() + "\\monitor_report" + count.ToString() + ".xlsx";
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
}
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class TaskReport
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);

      static int count = 1;
      private static readonly string MODULE_NAME = "task_report";

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      class Data : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.Now;
         public DateTime finish = DateTime.Now;
         public string userid = string.Empty;
      }

      public static void Do(DateTime begin, DateTime end, string userid, Form owner)
      {
         Manager manager = (CurrentUser.user as Manager);

         if (manager == null)
            return;

         FmTaskReportParams dlg = new FmTaskReportParams();
         dlg.Start = begin;
         dlg.Finish = end;

         if (dlg.ShowDialog() == DialogResult.OK)
         {
            Result result = new Result();
            SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);

            Data data = new Data();
            data.start = dlg.Start;
            data.finish = dlg.Finish;
            data.userid = userid;

            Report r = new Report(MODULE_NAME, data, resultSet);
            Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
            FmWait.ShowForm(owner, th);
            th.Join();
            FmWait.CloseForm();

            if (resultSet.Count > 0)
            {
               Result res = resultSet[0];
               if (res.file.Length > 0)
               {
                  string fileName = Path.GetTempPath() + "\\"+ MODULE_NAME + count.ToString() + ".xlsx";
                  while (File.Exists(fileName))
                  {
                     count++;
                     fileName = Path.GetTempPath() + "\\"+ MODULE_NAME + count.ToString() + ".xlsx";
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

using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using GRSoft.Network;
using System.IO;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   class MerchReport
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

      public static void Do(DateTime begin, DateTime end, Form owner)
      {
         Manager manager = (CurrentUser.user as Manager);

         if (manager == null)
            return;

         MerchParams.Data data = new MerchParams.Data();
         data.begin = begin;
         data.end = end;

         StringBuilder sb = new StringBuilder();
         Agents ds = manager.GetAgents();

         if(ds != null)
            foreach (Agent a in ds.Data)
            {
               if (sb.Length > 0)
                  sb.Append(",");
               sb.Append("'").Append(a.id).Append("'");
            }

         data.userids = sb.ToString();

         MerchParams dlg = new MerchParams(data);

         if (dlg.ShowDialog() == DialogResult.OK)
         {
            Result result = new Result();
            SimpleDataSet<Result> resultSet = new SimpleDataSet<Result>("Result", false);
            const string MODULE_NAME = "merch_report";
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
                  string fileName = Path.GetTempPath() + "\\" + MODULE_NAME + count.ToString() + ".xlsx";
                  while (File.Exists(fileName))
                  {
                     count++;
                     fileName = Path.GetTempPath() + "\\" + MODULE_NAME + count.ToString() + ".xlsx";
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

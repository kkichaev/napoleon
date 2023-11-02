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
   public class VisitReport : GRSoft.Network.DataObject
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);

      static int count = 1;

      public string name = "";
      public byte[] file = null;
       [ItemType(typeof(VisitReportItem))]
      public List<VisitReportItem> items = new List<VisitReportItem>();

      public class VisitReportItem : GRSoft.Network.DataObject
      {
         public string name;
         public byte[] file = null;
      }

      public static void Do(DateTime begin, DateTime end, Form owner, string userid)
      {
         Manager manager = (CurrentUser.user as Manager);

         if (manager == null)
            return;

         Data data = new Data();
         data.begin = begin;
         data.end = end;
         data.userid = userid;
         FmVizitReportParams dlg = new FmVizitReportParams(data);

         if (dlg.ShowDialog() == DialogResult.OK)
            DoReport("visit_report", data, owner);
      }

      public class Data : GRSoft.Network.DataObject
      {
         public DateTime begin = DateTime.MinValue;
         public DateTime end = DateTime.MinValue;
         public string userid = string.Empty;
         public int photo = 1;
         public string href = Config.GetConfig().HrefBase;
      }


      public static void DoReport(string repName, GRSoft.Network.DataObject param, Form owner)
      {
         VisitReport result = new VisitReport();
         SimpleDataSet<VisitReport> resultSet = new SimpleDataSet<VisitReport>("Result", false);
         Report r = new Report(repName, param, resultSet);

         Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), r, FmWait.ProgressIndicator);
         FmWait.ShowForm(owner, th);
         th.Join();
         FmWait.CloseForm();

         if (resultSet.Count > 0)
         {
            VisitReport res = resultSet[0];
            if (res.file.Length > 0)
            {
               string fileName = Path.GetTempPath() + "\\" + repName + count.ToString() + ".xlsx";
               while (File.Exists(fileName))
               {
                  count++;
                  fileName = Path.GetTempPath() + "\\" + repName + count.ToString() + ".xlsx";
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

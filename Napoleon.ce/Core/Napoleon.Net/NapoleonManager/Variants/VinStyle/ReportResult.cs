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
   class ReportResult : GRSoft.Network.DataObject
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
         public string name = string.Empty;
         public byte[] file = null;
      }


      public static void DoReport(string repName, GRSoft.Network.DataObject param, Form owner)
      {
         ReportResult result = new ReportResult();
         SimpleDataSet<ReportResult> resultSet = new SimpleDataSet<ReportResult>("Result", false);
         Report r = new Report(repName, param, resultSet);

         DBConnection conn = Config.GetConfig().GetConnection();
         int svTm = conn.ReceiveTimeout;
         conn.ReceiveTimeout = 10 * 60 * 1000;
         Thread th = DataModule.RefreshGiveSets(conn, r, FmWait.ProgressIndicator);
         FmWait.ShowForm(owner, th);
         th.Join();
         FmWait.CloseForm();
         conn.ReceiveTimeout = svTm;

         if (resultSet.Count > 0)
         {
            ReportResult res = resultSet[0];
            if (res.file.Length > 0)
            {
               string fileName = Path.GetTempPath() + "\\" + repName + count.ToString() + ".xlsx";
               while (File.Exists(fileName))
               {
                  count++;
                  fileName = Path.GetTempPath() + "\\" + repName + count.ToString() + ".xlsx";
               }
               File.WriteAllBytes(fileName, res.file);

               foreach (VisitReportItem i in res.items)
               {
                  String f = String.Format(@"{0}\{1}.jpg", Path.GetTempPath(), i.name);

                  if (i.file != null)
                     File.WriteAllBytes(f, i.file);
               }


               ShellExecute(IntPtr.Zero, "open", fileName, "", "", 1);
            }
         }
         else
            MessageBox.Show("Ошибка построения отчета");
      }
   }

}

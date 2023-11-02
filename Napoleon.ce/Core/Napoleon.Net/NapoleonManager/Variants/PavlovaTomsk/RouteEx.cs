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
   class RouteEx : Route
   {
      [DllImport("shell32.dll")]
      static extern IntPtr ShellExecute(IntPtr hwnd, string lpOperation, string lpFile, string lpParameters, string lpDirectory, int nShowCmd);
      static readonly string REPORT_NAME = "route_report";
      static int count = 1;

      class Result : GRSoft.Network.DataObject
      {
         public string name = "";
         public byte[] file = null;
      }

      class Param : GRSoft.Network.DataObject
      {
         public string userid = string.Empty;
      }

      protected override void MakeReport()
      {
         Agent curAgent = cbAgents.SelectedItem as Agent;

         if(curAgent == null)
            return;


         Param data = new Param();
         data.userid = curAgent.id; 
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
                  fileName = Path.GetTempPath() + "\\"+ REPORT_NAME + count.ToString() + ".xlsx";
               }
               File.WriteAllBytes(fileName, res.file);
               ShellExecute(IntPtr.Zero, "open", fileName, "", "", 1);
            }
         }
         else
            MessageBox.Show("Ошибка построения отчета");
      }

      protected override void DrawCell(DataGridViewCellFormattingEventArgs e)
      {
         base.DrawCell(e);

         OrgRouteQueue data = dgvOrgs.DataSource as OrgRouteQueue;

         if (data != null)
         {
            OrgRouteQueueItem item = data[e.RowIndex];
            if (item != null && item.Item != null && item.Item.org != null)
               e.CellStyle.ForeColor = item.Item.org.Color;
         }
      }
   }
}

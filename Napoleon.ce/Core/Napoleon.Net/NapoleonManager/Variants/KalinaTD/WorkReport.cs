using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class WorkReport
   {
      public static void Do(Form owner, DateTime start, DateTime finish)
      {
         Manager manager = (CurrentUser.user as Manager);

         if (manager == null)
            return;

         FmWorkRptParam dlg = new FmWorkRptParam();
         dlg.Start = start;
         dlg.Finish = finish.AddDays(-1);

         if (dlg.ShowDialog() == DialogResult.OK)
         {
            Data data = new Data();
            data.start = dlg.Start;
            data.finish = dlg.Finish;
            data.userids = dlg.UserIDS;

            ReportResult.DoReport("work_report", data, owner);
         }
      }

      private class Data : Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public string userids = string.Empty;
      }
   }
}

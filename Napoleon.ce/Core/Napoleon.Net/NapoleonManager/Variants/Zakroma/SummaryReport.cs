using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class SummaryReport
   {
      public static void Do(Form owner, DateTime start, DateTime finish)
      {
         Manager manager = (CurrentUser.user as Manager);

         if (manager == null)
            return;

         FmSummayRptParam dlg = new FmSummayRptParam();
         dlg.Start = start;
         dlg.Finish = finish;

         if (dlg.ShowDialog() == DialogResult.OK)
         {
            Data data = new Data();
            data.start = dlg.Start;
            data.finish = dlg.Finish.AddDays(1);
            data.ids = dlg.DivIds;

            ReportResult.DoReport("summary_report", data, owner);
         }
      }

      private class Data : Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public string ids = string.Empty;
      }
   }
}

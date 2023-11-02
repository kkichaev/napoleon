using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class SyncRpt
   {
      public static void Do(Form owner, DateTime start, DateTime finish)
      {
         FmSyncParam dlg = new FmSyncParam();
         dlg.dtpStart.Value= start;
         dlg.dtpFinish.Value = finish;
         dlg.btnExcel.Click += (o, e) =>
         {
            SyncParam p = new SyncParam();
            p.start = dlg.dtpStart.Value.Date;
            p.finish = dlg.dtpFinish.Value.Date;
            p.ids = dlg.AgentIds;

            ReportResult.DoReport("sync", p, owner);
         };

         dlg.Show();
      }

   }
}

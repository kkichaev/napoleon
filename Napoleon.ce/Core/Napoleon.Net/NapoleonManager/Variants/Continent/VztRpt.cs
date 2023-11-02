using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class VztRpt
   {
      public static void Do(Form owner, DateTime start, DateTime finish)
      {
         FmVztRptDlg dlg = new FmVztRptDlg();
         dlg.datePeriodView1.Start = start;
         dlg.datePeriodView1.Finish = finish;
         dlg.btnExcel.Click += (o, e) =>
         {
            VztRptParams p = new VztRptParams();
            p.start = dlg.datePeriodView1.Start;
            p.finish = dlg.datePeriodView1.Finish;
            p.ids = dlg.AgentIds;
            p.hour = dlg.dateTimePicker1.Value.Hour;
            p.min = dlg.dateTimePicker1.Value.Minute;

            ReportResult.DoReport("vizit", p, owner);
         };
         dlg.Show();
      }
   }
}

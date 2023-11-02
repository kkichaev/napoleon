using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public class RemnantsReport
   {
      public static void Do(DateTime begin, DateTime end, Form owner)
      {
         Manager manager = (CurrentUser.user as Manager);

         if (manager == null)
            return;

         FmReportParams.ReportData data = new FmReportParams.ReportData();
         data.begin = begin;
         data.end = end;
         FmReportParams dlg = new FmReportParams(data);

         if (dlg.ShowDialog() == DialogResult.OK)
            ReportResult.DoReport("remnants_report", data, owner);
      }
   }
}

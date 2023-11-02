using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class AuditReport
   {
      public static void Do(DateTime begin, DateTime end, Form owner, string userid)
      {
         Manager manager = (CurrentUser.user as Manager);

         if (manager == null)
            return;

         FmAuditReportParams.AuditReportData data = new FmAuditReportParams.AuditReportData();
         data.begin = begin;
         data.end = end;
         data.userid = userid;

         FmAuditReportParams dlg = new FmAuditReportParams(data);

         if (dlg.ShowDialog() == DialogResult.OK)
            ReportResult.DoReport("audit_report", data, owner);
      }
   }
}

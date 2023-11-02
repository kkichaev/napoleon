using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class AuditReport
   {
      public static void Do(DateTime begin, Form owner)
      {
         Manager manager = (CurrentUser.user as Manager);

         if (manager == null)
            return;

         FmAuditParam dlg = new FmAuditParam();
         dlg.Date = begin;

         if (dlg.ShowDialog() == DialogResult.OK)
         {
            Param p = new Param();
            p.userid = dlg.Userid;
            p.date = dlg.Date;
            p.matrix = dlg.Matrix;

            ReportResult.DoReport("audit", p, owner);
         }
      }

      class Param : GRSoft.Network.DataObject
      {
         public DateTime date = DateTime.MinValue;
         public string userid = string.Empty;
         public string matrix = string.Empty;
      }
   }
}

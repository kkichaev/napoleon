using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FocusExcelReport
   {
      public static void Do(DateTime begin, DateTime end, string userid, Form owner)
      {
         Manager manager = (CurrentUser.user as Manager);

         if (manager == null)
            return;

            Param p = new Param();
            p.start = begin;
            p.finish = end;
            p.userid = userid;

            ReportResult.DoReport("focusreport", p, owner);
      }

      class Param : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public string userid = string.Empty;
      }
   }
}

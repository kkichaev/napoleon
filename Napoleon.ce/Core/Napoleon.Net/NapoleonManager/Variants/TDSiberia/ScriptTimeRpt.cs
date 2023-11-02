using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class ScriptTimeRpt
   {
      public static void Do(DateTime begin, DateTime end, Form owner)
      {
         Manager manager = (CurrentUser.user as Manager);

         if (manager == null)
            return;

         FmScriptTimeRptParam dlg = new FmScriptTimeRptParam();
         dlg.Start = begin;
         dlg.Finish = end;

         if (dlg.ShowDialog() == DialogResult.OK)
         {
            Param p = new Param();
            p.start = dlg.Start;
            p.finish = dlg.Finish;
            p.userids = dlg.UserIDS;

            ReportResult.DoReport("scrtime", p, owner);
         }
      }

      class Param : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public string userids = string.Empty;
      }
   }
}

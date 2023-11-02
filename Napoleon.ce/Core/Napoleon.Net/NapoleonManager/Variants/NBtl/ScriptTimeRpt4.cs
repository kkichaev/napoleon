using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class ScriptTimeRpt4
   {
      public static void Do(DateTime begin, DateTime end, Form owner)
      {
         Do(begin, end, owner, "scrtime4");
      }

      public static void Do(DateTime begin, DateTime end, Form owner, string repname)
      {
         Manager manager = (CurrentUser.user as Manager);

         if (manager == null)
            return;

         FmScriptTimeRptParam4 dlg = new FmScriptTimeRptParam4();
         dlg.Start = begin;
         dlg.Finish = end;

         if (dlg.ShowDialog() == DialogResult.OK)
         {
            Param p = new Param();
            p.start = dlg.Start;
            p.finish = dlg.Finish;
            p.userids = dlg.UserIDS;

            ReportResult.DoReport(repname, p, owner);
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

using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class ScriptTimeRpt3
   {
      public static void Do(DateTime begin, DateTime end, Form owner)
      {
         Manager manager = (CurrentUser.user as Manager);

         if (manager == null)
            return;

         FmScriptTimeRptParam3 dlg = new FmScriptTimeRptParam3();
         dlg.DoReport = Request;
         dlg.Start = begin;
         dlg.Finish = end;
         dlg.Show();
      }

      private static void Request(FmScriptTimeRptParam3 dlg)
      {
         Param p = new Param();
         p.start = dlg.Start;
         p.finish = dlg.Finish;
         p.userid = dlg.UserIDS;
         p.cid = dlg.CID.id;

         ReportResult.DoReport("scrtime3", p, dlg);
      }

      class Param : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public string userid = string.Empty;
         public string cid = string.Empty;
      }
   }
}

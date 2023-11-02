using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class ScriptTimeRpt2
   {
      public static void Do(DateTime begin, DateTime end, Form owner)
      {
         Manager manager = (CurrentUser.user as Manager);

         if (manager == null)
            return;

         FmScriptTimeRptParam2 dlg = new FmScriptTimeRptParam2();
         dlg.Start = begin;
         dlg.Finish = end;

         if (dlg.ShowDialog() == DialogResult.OK)
         {
            Param p = new Param();
            p.start = dlg.Start;
            p.finish = dlg.Finish;

            p.div = dlg.DivID;

            ReportResult.DoReport("scrtime2", p, owner);
         }
      }

      class Param : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public string div = string.Empty;
      }
   }
}

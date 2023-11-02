using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FridgeRpt
   {
      public static void Do(Form owner)
      {
         Manager manager = (CurrentUser.user as Manager);

         if (manager == null)
            return;

         FmFridgeRptParams dlg = new FmFridgeRptParams();

         if (dlg.ShowDialog() == DialogResult.OK)
         {
            Param p = new Param();
            p.userids = dlg.UserIDS;

            ReportResult.DoReport(dlg.Report, p, owner);
         }
      }

      class Param : GRSoft.Network.DataObject
      {
         public string userids = string.Empty;
      }
   }
}

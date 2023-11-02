using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class ClientCardReport
   {
      public static void Do(Form owner)
      {
         FmClientCardReportParam dlg = new FmClientCardReportParam();

         if (dlg.ShowDialog() == DialogResult.OK)
         {
            Param p = new Param();
            p.id = dlg.ID;

            ReportResult.DoReport("client_card_excel", p, owner);
         }
      }

      class Param : GRSoft.Network.DataObject
      {
         public string id = string.Empty;
      }

   }
}

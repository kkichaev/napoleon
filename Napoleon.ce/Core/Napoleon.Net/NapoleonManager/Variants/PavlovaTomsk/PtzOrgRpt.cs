using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class PtzOrgRpt
   {
      public static void Do(Form owner)
      {
         Manager manager = (CurrentUser.user as Manager);

         if (manager == null)
            return;

         StringBuilder sb = new StringBuilder();

         foreach (Division.DivisionAgent a in manager.Division.GetAllAgents())
            if (a.agent != null)
            {
               if (sb.Length > 0)
                  sb.Append(", ");
               
               sb.Append("'");
               sb.Append(a.agent.id); 
               sb.Append("'");
            }

         FmPtzOrgRptParam dlg = new FmPtzOrgRptParam();

         if (dlg.ShowDialog() == DialogResult.OK)
         {
            Param p = new Param();
            p.start = dlg.Start;
            p.finish = dlg.Finish;
            p.userids = sb.ToString();

            ReportResult.DoReport("ptzorg", p, owner);
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

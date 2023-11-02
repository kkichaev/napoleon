using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      public FmDetailEx(FmDetailData data)
         : base(data)
      {
      }

      protected override void DoClientCardReport()
      {
         Agent a = cbAgents.SelectedItem as Agent;

         if (a != null)
            ClientCardEx.DoReport(this, dtpBegin.Value.Date, dtpEnd.Value.Date, a.id);
      }
   }
}

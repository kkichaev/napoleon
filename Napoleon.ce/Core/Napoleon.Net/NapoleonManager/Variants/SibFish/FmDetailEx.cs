using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {
      public FmDetailEx(FmDetailData data)
         : base(data)
      { 
      }

      internal override OrdersDetail CreateOrderDetail() { return new ScriptDetailEx(documents); }
   }

   class ScriptDetailEx : ScriptDetail
   {
      public ScriptDetailEx() {}
      public ScriptDetailEx(List<DocumentInfo> documents) : base(documents) {}
      protected override void LoadPotenzialOrgVisit(FmDetailData cond, bool oneDay) {}
   }
}

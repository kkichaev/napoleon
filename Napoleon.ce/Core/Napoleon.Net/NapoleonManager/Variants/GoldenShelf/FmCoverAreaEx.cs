using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmCoverAreaEx : FmCoverArea
   {
      public FmCoverAreaEx(string idAgent, DateTime date)
         : base(idAgent, date)
      {
         btnOrder.Visible = false;
         btnRemnants.Visible = false;
         btnSales.Visible = false;
         btnIncass.Visible = false;
      }
   }
}

using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmOrdersReportEx : FmOrdersReport
   {
      public FmOrdersReportEx()
      {
         cbPiece.Text = "Литры";
         cbPackets.Text = "КЕГИ";
      }
   }
}

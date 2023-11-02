using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class MainEx : MainForm
   {
      protected override void SummaryReport()
      {
         new SummaryReport().Do(this, dtpBeginDate.Value.Date, GetRangeEndDate());
      }
   }
}

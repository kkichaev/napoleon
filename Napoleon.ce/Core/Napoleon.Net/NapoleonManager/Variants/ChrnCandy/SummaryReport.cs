using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class SummaryReport
   {
      public void Do(Form owner, DateTime start, DateTime finish)
      {
         Data data = new Data();
         data.start = start;
         data.finish = finish;

         ReportResult.DoReport("summary_report", data, owner);
      }

      private class Data : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
      }
   }
}

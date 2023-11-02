using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class TimeTrackingReport
   {
      class Data : GRSoft.Network.DataObject
      {
         public int month = 0;
         public int year = 0;
      }

      public static void Do(int month, int year, Form owner)
      {
         Data data = new Data();
         data.month = month;
         data.year = year;   
         ReportResult.DoReport("time_report", data, owner);
      }
   }
}

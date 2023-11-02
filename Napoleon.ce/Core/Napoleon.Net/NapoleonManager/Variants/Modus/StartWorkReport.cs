using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class StartWorkReport
   {
      public static void Do(System.Windows.Forms.Form owner)
      {
         StartWorkParams.Data data = new StartWorkParams.Data();
         StartWorkParams w = new StartWorkParams(data);
         if(w.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            ReportResult.DoReport("start_work_report", data, owner);  
      }
   }
}

using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class MonitoringConurentRep 
   {
      GenMonReport report;

      public MonitoringConurentRep()
      {
         report = new GenMonReport();
      }

      public void Do(MonReportParams.Data data, DataSet<int, Monitoring> dsMonitoring, DataSet<string, MonitoringItem> dsItems)
      {
         report.Do(data, dsMonitoring, dsItems, false);
      }

      public bool Visible
      {
         set { report.Visible = true; }
      }
   }
}

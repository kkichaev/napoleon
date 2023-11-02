using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class MonitoringOurRep
   {
      GenMonReport report;

      public MonitoringOurRep()
      {
         report = new GenMonReport();
      }

      public void Do(MonReportParams.Data data, DataSet<int, Monitoring> dsMonitoring, DataSet<string, MonitoringItem> dsItems)
      {
         report.Do(data, dsMonitoring, dsItems, true);
      }

      public bool Visible
      {
         set { report.Visible = true; }
      }
   }
}

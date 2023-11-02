using GRSoft.NapoleonManager.Reports.Excel;
using System.Collections.Generic;
using GRSoft.Network;
using System;
using System.Windows.Forms;
using System.Threading;
namespace GRSoft.NapoleonManager
{
   class MonitoringReports
   {
      static DataSet<string, MonitoringItem> dsItems;

      public static void Do(DateTime begin, DateTime end, String agentId, Form owner)
      {
         MonReportParams.Data data = new MonReportParams.Data();
         data.date = begin;
         data.dateEnd = end;
         MonReportParams dlg = new MonReportParams(data);
         dlg.SetSelectedAgent(agentId);
         if (dlg.ShowDialog() == DialogResult.OK)
         {
            dsItems = DataModule.Get(MonitoringItem.OBJECT_NAME) as DataSet<string, MonitoringItem>;
            if (dsItems == null)
               dsItems = new DataSet<string, MonitoringItem>(MonitoringItem.OBJECT_NAME);

            string filter = "";
            string uidFilter = "";
            if (data.agent != null)
               uidFilter = "userid in ('" + data.agent.id + "')";
            else if (data.division != null)
               uidFilter = FmMessageHistory.UserIdIsStr(data.division.GetAllAgents());
            else
               uidFilter = "userid in ('" + agentId + "')";
            filter = uidFilter;
            string DATA_FILTER = String.Format("{0} >= ToDate('{1:dd/MM/yyyy}') and {0} < ToDate('{2:dd/MM/yyyy}')",
               "created", data.date.Date, data.dateEnd.AddDays(1).Date);
            filter += " and " + DATA_FILTER;

            List<IDataSet> upd = new List<IDataSet>();
            DataSet<string, Org> orgs = new DataSet<string, Org>(Org.OBJECT_NAME, false);
            DataSet<string, PotenzialOrg> porgs = new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME, false);
            if (data.division != null)
            {
               orgs.Filter = uidFilter;
               porgs.Filter = uidFilter;
               upd.AddRange(new IDataSet[] { orgs, porgs });
            }

            DataSet<int, Monitoring> dset = new DataSet<int, Monitoring>(Monitoring.OBJECT_NAME, false);
            dset.Filter = filter;
            upd.Add(dset);

            if (dsItems.Count == 0)
               upd.Add(dsItems);

            Thread th = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), upd, FmWait.ProgressIndicator);
            FmWait.ShowForm(owner, th);
            th.Join();

            try
            {
               if (data.isOur)
                  DoOurReport(data, dset);
               if (data.isConcurents)
                  DoConcurentReport(data, dset);
            }
            catch (Exception)
            {
            }

            FmWait.CloseForm();
         }
      }

      static void DoOurReport(MonReportParams.Data data, DataSet<int, Monitoring> dset)
      {
         MonitoringOurRep e = new MonitoringOurRep();
         e.Do(data, dset, dsItems);
         e.Visible = true;
      }

      static void DoConcurentReport(MonReportParams.Data data, DataSet<int, Monitoring> dset)
      {
         MonitoringConurentRep e = new MonitoringConurentRep();
         e.Do(data, dset, dsItems);
         e.Visible = true;
      }
   }
}
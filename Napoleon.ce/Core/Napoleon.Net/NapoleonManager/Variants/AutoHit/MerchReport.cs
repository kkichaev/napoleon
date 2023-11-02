using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;

namespace GRSoft.NapoleonManager.Reports
{
   class MerchReport : IReport
   {
      private ReportData reportData;
      private IReportImplementation reportImplementation;

      public MerchReport(List<MerchReportData.OrgShelf> data,
            List<Org> orgs,
            List<Agent> agents,
            DataSet<string, ManagerFolder> folders,
            DivisionItem division,
            Agent agent,
            DateTime begin,
            DateTime end,
            int maxLevel,
            IReportImplementation reportImplementation)
      {
         reportData = new MerchReportData(data, orgs, agents, folders, division, agent, begin, end, maxLevel);
         this.reportImplementation = reportImplementation;
      }

      #region IReport Members

      public void Show()
      {
         reportImplementation.Show();
      }

      public void Build()
      {
         reportImplementation.Build(reportData);
      }

      #endregion
   }

   class MerchReportData : ReportData
   {
      public class OrgShelf
      {
         public Agent agent;
         public Org org;
         public ActionCategory category;
         public DateTime timeCreated;
         public double metersAll;
         public double metersOur;
         public int skuAll;
         public int skuOur;
      };

      public List<OrgShelf> data;
      public DataSet<string, ManagerFolder> dsManagerFolder;
      public List<Org> orgs = new List<Org>();
      public List<Agent> agents = new List<Agent>();
      public DivisionItem division;
      public DateTime begin;
      public DateTime end;
      public Agent agent;
      public int maxLevel = 0;

      public MerchReportData(List<OrgShelf> data,
            List<Org> orgs,
            List<Agent> agents,
            DataSet<string, ManagerFolder> folders,
            DivisionItem division,
            Agent agent,
            DateTime begin,
            DateTime end,
            int maxLevel)
      {
         this.data = data;
         this.orgs = orgs;
         this.agents = agents;
         this.dsManagerFolder = folders;
         this.division = division;
         this.begin = begin;
         this.end = end;
         this.agent = agent;
         this.maxLevel = maxLevel;
      }
   }
}

using GRSoft.Network;
using GRSoft.UILib;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      private SimpleDataSet<VisitWithPhoto> dsVisitWithPhoto = new SimpleDataSet<VisitWithPhoto>(VisitWithPhoto.OBJECT_NAME, true);

      void PutNodesData(SummaryReportData data, TreeGridNodeCollection nodes)
      {
         foreach (TreeGridNode node in nodes)
         {
            SummaryReportData.Item item = null;
            SummaryData sd = node.DataItem as SummaryData;
            if (sd != null && sd.Agent != null)
            {
               item = new SummaryReportData.Item();
               item.name = sd.Agent.Name;
               item.visit = sd.Visits;
               item.orders = sd.Orders;
               item.sum = sd.DocSum;
               item.progress = sd.ProgressValue;
            }
            else
            {
               SummaryDivisionData sdd = node.DataItem as SummaryDivisionData;
               if(sdd != null)
               {
                  item = new SummaryReportData.Item();
                  item.name = sdd.Name;
                  item.visit = sdd.Visits;
                  item.orders = sdd.Orders;
                  item.sum = sdd.DocSum;
                  item.progress = sdd.ProgressValue;
                  item.isDivision = 1;
               }
            }
            if (item != null)
               data.items.Add(item);

            if (node.Nodes.Count > 0)
               PutNodesData(data, node.Nodes);
         }
      }

      protected override void tsbMakeHtml_Click(object sender, EventArgs e)
      {
         SummaryReportData data = new SummaryReportData();

         PutNodesData(data, tgvAgentsSummary.Nodes);
         if( data.items.Count > 0 )
            data.division = data.items[0].name;

         data.start = dtpBeginDate.Value;
         data.finish = dtpEndDate.Value;
         ReportResult.DoReport("summary_report", data, this);
      }

      public class SummaryReportData : Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
         public string division = "";

         public class Item : Network.DataObject
         {
            public string name = "";
            public int visit = 0;
            public int orders = 0;
            public double sum = 0;
            public double progress = 0;
            public int isDivision = 0;
         }

         public List<Item> items = new List<Item>();
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         dsVisitWithPhoto.Filter = string.Format("{0:dd/MM/yyyy};{1:dd/MM/yyyy}", GetBeginDateForSelection(), GetRangeEndDate());
         updSets.Add(dsVisitWithPhoto);
      }

      protected override void AfterRefreshData()
      {
         base.AfterRefreshData();
      }
   }

   class DivisionSummaryEx : DivisionSummary
   {
      public DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig)
         : base(dsConfig)
      { }

      protected override void PostAddData()
      {
         IDataSet cdata = DataModule.Get(VisitWithPhoto.OBJECT_NAME);
         if (cdata != null)
            foreach (VisitWithPhoto i in cdata.Data)
               this.Add(i);
      }

      internal virtual void Add(VisitWithPhoto i)
      {
         if (i.agent != null && ContainsKey(i.AgentID))
         {
            SummaryDataEx sd = (SummaryDataEx)this[i.AgentID];
            sd.Add(i);
         }
      }
   }

   class SummaryDataEx : SummaryData
   {
      public SummaryDataEx(Agent agent, DataSet<int, CommonConfig> dsConfig) :
         base(agent, dsConfig)
      { 
      }

      public override void AddOrg(BaseDocument doc)
      {
         if (CanScripting(AgentID))
         {
            if (doc is VisitWithPhoto)
               base.AddOrg(doc);
         }else
            base.AddOrg(doc);
      }

      protected bool CanScripting(string agentID)
      {
         CommonConfig cc = ConfigUtils.GetConfig(dsConfig, ConfigKeyItems.ALLOW_SCRIPTING, agentID);
         return (cc == null) ? false : (int.Parse(cc.value) > 0);
      }

      public void Add(VisitWithPhoto doc)
      {
         AddOrg(doc);
      }
   }
}

using GRSoft.Network;
using GRSoft.UILib;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public MainFormEx()
      {
      }


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
         data.finish = dtpBeginDate.Value.AddDays(1);
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
   }
}

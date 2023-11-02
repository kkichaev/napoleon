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
         //ToolStripButton button = new System.Windows.Forms.ToolStripButton();
         //button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         //button.Image = Properties.Resources.abiword_3;
         //button.ImageTransparentColor = System.Drawing.Color.Magenta;
         //button.Name = "mtxtimw";
         //button.Size = new System.Drawing.Size(23, 22);
         //button.Text = "Oтчёт по времени пребывания в точке";
         //button.Click += new System.EventHandler((s, e) => { ScriptTimeRpt.Do(dtpBeginDate.Value.Date, dtpBeginDate.Value.Date.AddDays(1), this); });

         //tsbConfig.Items.Add(button);

         //button = new System.Windows.Forms.ToolStripButton();
         //button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         //button.Image = Properties.Resources.work_report;
         //button.ImageTransparentColor = System.Drawing.Color.Magenta;
         //button.Name = "btnWorkReport";
         //button.Size = new System.Drawing.Size(23, 22);
         //button.Text = "Отчет о работе";
         //button.Click += new System.EventHandler((o, e) => { new FmWorkReport().Show(); });

         //tsbConfig.Items.Add(button);

         ToolStripButton button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.kmenuedit;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "btnReturnReestr";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Реестр документов";
         button.Click += new System.EventHandler((o, e) => { new FmReturnReestr().Show(); });

         tsbConfig.Items.Add(button);
      }


      void PutNodesData(SummaryReportData data, TreeGridNodeCollection nodes)
      {
         foreach (TreeGridNode node in nodes)
         {
            SummaryReportData.Item item = null;
            SummaryDataEx sd = node.DataItem as SummaryDataEx;
            if (sd != null && sd.Agent != null)
            {
               item = new SummaryReportData.Item();
               item.name = sd.Agent.Name;
               item.visit = sd.Visits;
               item.orders = sd.Orders;
               item.sum = sd.DocSum;
               item.pkoCnt = sd.PkoCnt;
               item.pkoSum = sd.PkoSum;
               item.progress = sd.ProgressValue;
            }
            else
            {
               SummaryDivisionDataEx sdd = node.DataItem as SummaryDivisionDataEx;
               if(sdd != null)
               {
                  item = new SummaryReportData.Item();
                  item.name = sdd.Name;
                  item.visit = sdd.Visits;
                  item.orders = sdd.Orders;
                  item.sum = sdd.DocSum;
                  item.pkoCnt = sdd.PkoCnt;
                  item.pkoSum = sdd.PkoSum;
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
         data.finish = dtpBeginDate.Value.Date.AddDays(1);
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
            public double pkoSum = 0;
            public double pkoCnt = 0;
            public double progress = 0;
            public int isDivision = 0;
         }

         public List<Item> items = new List<Item>();
      }
   }
}

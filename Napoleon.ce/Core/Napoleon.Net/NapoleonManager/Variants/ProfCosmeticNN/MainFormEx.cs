using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      SimpleDataSet<RouteItem> dsRouteItems = new SimpleDataSet<RouteItem>(RouteItem.OBJECT_NAME);
      SimpleDataSet<Dispatch> dsDispatch = new SimpleDataSet<Dispatch>(Dispatch.OBJECT_NAME);
      SimpleDataSet<DShipment> dsDShipment = new SimpleDataSet<DShipment>(DShipment.OBJECT_NAME);
      SimpleDataSet<DIncass> dsDIncass = new SimpleDataSet<DIncass>(DIncass.OBJECT_NAME);

      public MainFormEx()
      {
         btnTask.Visible = false;
         tsbMakeHtml.Visible = false;
         btnOrderReport.Visible = false;
         btnCensus.Visible = false;
         //btnGpsReport.Visible = false;
         btnDivision.Visible = false;
         toolStripSeparator1.Visible = false;

         Column1.HeaderText = "Cдано точек в штуках";
         tgvAgentsSummaryCount.HeaderText = "Кол-во сданных накладных";
         tgvAgentsSummarySum.HeaderText = "Полученные наличные";

         ToolStripButton btn = new ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = global::GRSoft.NapoleonManager.Properties.Resources.taskdoc;
         btn.ImageTransparentColor = System.Drawing.Color.Magenta;
         btn.Name = "btnChat";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Чат";
         btn.Click += new System.EventHandler((o, e) => { new FmChat().Show(); });

         tsbConfig.Items.Add(btn);
      }

      protected override bool IsMenuItemVisible(System.Windows.Forms.ToolStripItem menu)
      {
         return menu == smiDetail || menu == smiInfo;
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         base.AddUpdateDataSet(updSets);
         const string filter = "route in (select id from route where start <= ToDate('{0}') and finish >= ToDate('{1}'))";
         dsRouteItems.Filter = string.Format(filter, dtpBeginDate.Value.Date, GetRangeEndDate());
         dsDispatch.Filter = string.Format(COMMON_FILTER_STR, "created", dtpBeginDate.Value.Date, GetRangeEndDate());
         dsDShipment.Filter = string.Format(COMMON_FILTER_STR, "created", dtpBeginDate.Value.Date, GetRangeEndDate());
         dsDIncass.Filter = string.Format(COMMON_FILTER_STR, "created", dtpBeginDate.Value.Date, GetRangeEndDate());

         updSets.Add(dsRouteItems);
         updSets.Add(dsDispatch);
         updSets.Add(dsDShipment);
         updSets.Add(dsDIncass);
      }
   }

   class SummaryDataEx : SummaryData
   {
      int dispacth_count = 0;
      int dshipment_count = 0;
      double incass_sum = 0.0;

      public SummaryDataEx(Agent agent, DataSet<int, CommonConfig> dsConfig)
         : base(agent, dsConfig)
      {
      }

      public void Add(Dispatch d)
      {
         dispacth_count++;
      }

      public void Add(DShipment d)
      {
         dshipment_count++;
      }

      public void Add(DIncass d)
      {
         incass_sum += d.sum;
      }

      public override int GetVisitCount()
      {
         return dispacth_count;
      }

      public override void CountProgress(DateTime start, DateTime end, DataSet<int, OrgFolder> dsOrgFolder)
      {
         plan = 0;

         double rc = 0;

         IDataSet cdata = DataModule.Get(RouteItem.OBJECT_NAME);

         if (cdata != null)
            rc = cdata.Count;

         if (rc != 0)
            plan = GetVisitCount() / rc * 100;
      }

      public override int GetOrders() { return dshipment_count;  }
      public override double GetSum() { return incass_sum; }
   }

   class DivisionSummaryEx : DivisionSummary
   {
      public DivisionSummaryEx (DataSet<int, CommonConfig> dsConfig)
         :base(dsConfig)
      { 
      }

      protected override void PostAddData()
      {
         base.PostAddData();

         IDataSet cdata = DataModule.Get(Dispatch.OBJECT_NAME);
         if (cdata != null)
            foreach (Dispatch i in cdata.Data)
               this.Add(i);


         cdata = DataModule.Get(DShipment.OBJECT_NAME);
         if (cdata != null)
            foreach (DShipment i in cdata.Data)
               this.Add(i);

         cdata = DataModule.Get(DIncass.OBJECT_NAME);
         if (cdata != null)
            foreach (DIncass i in cdata.Data)
               this.Add(i);
      }

      public virtual void Add(Dispatch d)
      {
         if (ContainsKey(d.AgentID))
         {
            SummaryDataEx sd = (SummaryDataEx)this[d.AgentID];
            sd.Add(d);
         }
      }

      public virtual void Add(DShipment d)
      {
         if (ContainsKey(d.AgentID))
         {
            SummaryDataEx sd = (SummaryDataEx)this[d.AgentID];
            sd.Add(d);
         }
      }

      public virtual void Add(DIncass d)
      {
         if (ContainsKey(d.AgentID))
         {
            SummaryDataEx sd = (SummaryDataEx)this[d.AgentID];
            sd.Add(d);
         }
      }
   }
}

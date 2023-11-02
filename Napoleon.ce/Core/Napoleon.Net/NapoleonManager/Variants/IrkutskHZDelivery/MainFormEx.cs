using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Reflection;
using GRSoft.UILib;
using System.Drawing;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      SimpleDataSet<RouteItem> dsRouteItems = new SimpleDataSet<RouteItem>(RouteItem.OBJECT_NAME);
      SimpleDataSet<PlanRoute> dsRoute = new SimpleDataSet<PlanRoute>(PlanRoute.OBJECT_NAME);
      SimpleDataSet<Dispatch> dsDispatch = new SimpleDataSet<Dispatch>(Dispatch.OBJECT_NAME);
      SimpleDataSet<DShipment> dsDShipment = new SimpleDataSet<DShipment>(DShipment.OBJECT_NAME);
      SimpleDataSet<DIncass> dsDIncass = new SimpleDataSet<DIncass>(DIncass.OBJECT_NAME);
      SimpleDataSet<DTask> dsTask = new SimpleDataSet<DTask>(DTask.OBJECT_NAME);

      public MainFormEx()
      {
         btnTask.Visible = false;
         tsbMakeHtml.Visible = false;
         btnOrderReport.Visible = false;
         btnCensus.Visible = false;
         //btnGpsReport.Visible = false;
         //btnDivision.Visible = false;
         toolStripSeparator1.Visible = false;
         btnSavePhoto.Visible = false;
         //tsbSelectRange.Enabled = false;
         dtpBeginDate.Enabled = false;
         tgvAgentsSummaryCount.HeaderText = "Кол-во сданных накладных";
         tgvAgentsSummarySum.HeaderText = "Полученные наличные";

         tgvAgentsSummaryCount.Visible = false;
         tgvAgentsSummarySum.Visible = false;
         btnPriceRemnants.Visible = false;

         DataGridViewTextBoxColumn clmn;
         // DataGridViewTextBoxColumn clmn = new DataGridViewTextBoxColumn();
         // clmn.Width = 100;
         // clmn.HeaderText = "ТТ в маршруте";
         // clmn.Name = "dgvTTCount";
         // clmn.DataPropertyName = "TTCount";
         // clmn.DisplayIndex = 1;

         // tgvAgentsSummary.Columns.Add(clmn);

         clmn = new DataGridViewTextBoxColumn();
         clmn.Width = 120;
         clmn.HeaderText = "Начало работы";
         clmn.Name = "dgvStartWork";
         clmn.DataPropertyName = "StartWork";
         // clmn.DisplayIndex = 3;
         clmn.DisplayIndex = 2;

         // Column1.DisplayIndex = 2;
         Column1.DisplayIndex = 1;
         Column1.Width = 100;

         tgvAgentsSummaryProgres.HeaderText = "Простой";

         tgvAgentsSummary.Columns.Add(clmn);
      }

      protected override void ShowDetail()
      {
         Type prcType = FormEntries.GetFormType(typeof(FmRoute));
         ConstructorInfo ci = prcType.GetConstructor(new Type[] { typeof(string), typeof(DateTime) });
         FmRoute route = (FmRoute)ci.Invoke(new object[] { GetSelectedAgent().id, GetStartDate() });
         route.Show();
      }

      protected override bool IsMenuItemVisible(System.Windows.Forms.ToolStripItem menu)
      {
         return menu == smiDetail || menu == smiInfo;
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         base.AddUpdateDataSet(updSets);
         const string filter = "route in (select id from route where start <= ToDate('{0}') and finish >= ToDate('{1}'))";
         dsRouteItems.Filter = string.Format(filter, dtpBeginDate.Value.Date, GetRangeEndDate().AddDays(-1));
         dsRoute.Filter = String.Format("start <= ToDate('{0}') and finish >= ToDate('{1}')", dtpBeginDate.Value.Date, GetRangeEndDate().AddDays(-1));
         dsDispatch.Filter = string.Format(COMMON_FILTER_STR, "created", dtpBeginDate.Value.Date, GetRangeEndDate());
         dsDShipment.Filter = string.Format(COMMON_FILTER_STR, "created", dtpBeginDate.Value.Date, GetRangeEndDate());
         dsDIncass.Filter = string.Format(COMMON_FILTER_STR, "created", dtpBeginDate.Value.Date, GetRangeEndDate());
         dsTask.Filter = string.Format(COMMON_FILTER_STR, "created", dtpBeginDate.Value.Date, GetRangeEndDate());

         updSets.Add(dsRouteItems);
         updSets.Add(dsDispatch);
         updSets.Add(dsDShipment);
         updSets.Add(dsDIncass);
         updSets.Add(dsRoute);
         updSets.Add(dsTask);
      }

      protected override void CellFormatting(DataGridViewCellFormattingEventArgs e)
      {
         TreeGridNode crow = (TreeGridNode)tgvAgentsSummary.Rows[e.RowIndex];
         SummaryDataEx sad = crow.DataItem as SummaryDataEx;
         if (sad != null)
         {
            if (sad.IsRouteInvalid())
              e.CellStyle.ForeColor = Color.Red;
            return;
         }
      }
   }

   class SummaryDataEx : SummaryData
   {
      int dispacth_count = 0;
      int dshipment_count = 0;
      double incass_sum = 0.0;
      List<BaseDocument> docs = new List<BaseDocument>();
      int tt_count = 0;
      DateTime start_work = DateTime.MaxValue;

      public SummaryDataEx(Agent agent, DataSet<int, CommonConfig> dsConfig)
         : base(agent, dsConfig)
      {
         IDataSet cdata = DataModule.Get(PlanRoute.OBJECT_NAME);
         String rid = null;

         if (cdata != null)
         {
            foreach (System.Object o in cdata)
            {
               KeyValuePair<int, PlanRoute> r = (KeyValuePair<int, PlanRoute>)o;

               if (r.Value != null && r.Value.userid.Equals(agent.id)) { 
                  rid = r.Value.id;
                  break;
               }
            }
         }

         cdata = DataModule.Get(RouteItem.OBJECT_NAME);

         if (rid != null && cdata != null)
         {
            List<RouteItem> items = new List<RouteItem>();

            foreach (System.Object o in cdata)
            {
               KeyValuePair<int, RouteItem> i = (KeyValuePair<int, RouteItem>)o;

               if (i.Value.route.Equals(rid))
                  items.Add(i.Value);
            }

            tt_count = items.Count;
         }
      }

      public void Add(Dispatch d)
      {
         dispacth_count++;
         AddDocument(d);
      }

      public void Add(DShipment d)
      {
         dshipment_count++;
         AddDocument(d);
      }

      public void Add(DIncass d)
      {
         incass_sum += d.sum;
         AddDocument(d);
      }

      public void Add(DTask d)
      {
         AddDocument(d);
      }

      public void AddDocument(BaseDocument d)
      {
         docs.Add(d);
         AddOrg(d);

         if (d.created < start_work)
            start_work = d.created;
      }

      public override int GetOrders() { return dshipment_count;  }
      public override double GetSum() { return incass_sum; }
      public String StartWork 
      { 
         get 
         {
            String res = "";

            if (start_work != DateTime.MaxValue)
               res = start_work.ToString("hh:mm");

            return res;
         } 
      }

      public bool IsRouteInvalid()
      {
         bool res = false;

         IDataSet cdata = DataModule.Get(PlanRoute.OBJECT_NAME);

         if (cdata != null)
         {

            String rid = null;

            foreach (System.Object o in cdata)
            {
               KeyValuePair<int, PlanRoute> r = (KeyValuePair<int, PlanRoute>)o;

               if (r.Value != null && r.Value.userid.Equals(agent.id))
                  rid = r.Value.id;

            }

            cdata = DataModule.Get(RouteItem.OBJECT_NAME);

            if (rid != null && cdata != null)
            {
               List<RouteItem> items = new List<RouteItem>();

               foreach (System.Object o in cdata)
               {
                  KeyValuePair<int, RouteItem> i = (KeyValuePair<int, RouteItem>)o;

                  if (i.Value.route.Equals(rid))
                     items.Add(i.Value);
               }

               items.Sort((x, y) => { return x.pos.CompareTo(y.pos); });
               docs.Sort((x, y) => { return x.created.CompareTo(y.created); });

               int itidx = 0;
               
               foreach (BaseDocument d in docs)
               {
                  if (items.Count > itidx)
                  {
                     string id = items[itidx].id;

                     if (d.id.Equals(id))
                        continue;

                     itidx += 1;

                     if (items.Count > itidx)
                     {
                        id = items[itidx].id;

                        if (d.id.Equals(id))
                           continue;
                     }

                     res = true;

                     break;
                  }
               }
            }
         }

         return res;
      }

      public int TTCount { get { return tt_count; } }

      public override double ProgressValue 
      { 
         get 
         {
            int res = 0;
            DateTime prev = DateTime.MinValue;

            docs.Sort((x, y) => { return x.created.CompareTo(y.created); });

            foreach (BaseDocument d in docs)
            {
               if (prev == DateTime.MinValue)
               {
                  prev = d.created;
                  res = 1;
               }
               else
               {
                  TimeSpan sp = d.created - prev;

                  if (sp.TotalHours >= 1)
                  {
                     res = 2;
                     break;
                  }
               }
 
            }

            return res; 
         } 
      }

      public override Image CreateProgressImage(DataGridViewImageColumn clmn)
      {
         return ProgressImageEx.CreateProgressImage(ProgressValue, clmn);
      }
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

         cdata = DataModule.Get(DTask.OBJECT_NAME);
         if (cdata != null)
            foreach (DTask i in cdata.Data)
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

      public virtual void Add(DTask d)
      {
         if (ContainsKey(d.AgentID))
         {
            SummaryDataEx sd = (SummaryDataEx)this[d.AgentID];
            sd.Add(d);
         }
      }
   }

   class SummaryDivisionDataEx : SummaryDivisionData
   {
      int tt_count = 0;

      public SummaryDivisionDataEx(Division division):base(division)
      {
      }

      public override Image ProgressImage
      {
         get
         {
            return null;
         }
         set
         {
            base.ProgressImage = value;
         }
      }

      public int TTCount { get { return tt_count; } }

      public override void Add(SummaryData data)
      {
         base.Add(data);

         tt_count += ((SummaryDataEx)data).TTCount;
      }
   }

}

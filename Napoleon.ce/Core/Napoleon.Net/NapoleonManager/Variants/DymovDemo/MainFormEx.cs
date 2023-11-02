using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Drawing;
using GRSoft.NapoleonManager.Utils;
using GRSoft.UILib;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      DataGridViewImageColumn clmnProgress;
      DataSet<int, OrgPlan> dsOrgPlan = new DataSet<int, OrgPlan>(OrgPlan.OBJECT_NAME);

      public MainFormEx()
      {

         const float weight = 55.3934F;
         DataGridViewTextBoxColumn clmnPlan = new DataGridViewTextBoxColumn();
         clmnPlan.HeaderText = "План";
         clmnPlan.Name = "clmnPlan";
         clmnPlan.DataPropertyName = "Plan";
         clmnPlan.FillWeight = weight;
         clmnPlan.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         tgvAgentsSummary.Columns.Add(clmnPlan);

         DataGridViewTextBoxColumn clmnFact = new DataGridViewTextBoxColumn();
         clmnFact.HeaderText = "Факт";
         clmnFact.Name = "clmnFact";
         clmnFact.DataPropertyName = "Fact";
         clmnFact.FillWeight = weight;
         clmnFact.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         tgvAgentsSummary.Columns.Add(clmnFact);

         clmnProgress = new DataGridViewImageColumn();

         clmnProgress.DataPropertyName = "ProgressImage2";
         
         DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
         dataGridViewCellStyle2.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleCenter;
         dataGridViewCellStyle2.NullValue = null;
         dataGridViewCellStyle2.Padding = new System.Windows.Forms.Padding(2, 3, 2, 3);
         clmnProgress.DefaultCellStyle = dataGridViewCellStyle2;
         clmnProgress.FillWeight = weight;
         clmnProgress.HeaderText = "% План";
         clmnProgress.Name = "clmnPropgress";
         clmnProgress.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
         clmnProgress.Width = 84;
         clmnProgress.Resizable = DataGridViewTriState.False;
         tgvAgentsSummary.Columns.Add(clmnProgress);
      }

      protected override DivisionSummary CreateDivisionSummary()
      {
         return new DivisionSummaryEx(dsConfig, clmnProgress);
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         base.AddUpdateDataSet(updSets);
         DataSet<string, Price> dsPrice = new DataSet<string, Price>(Price.OBJECT_NAME);
         dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;

         updSets.Add(dsOrgPlan);
         updSets.Insert(0, dsPrice);

         SimpleDataSet<Order> orders = (SimpleDataSet<Order>)DataModule.Get("LoadedOrders") ?? new SimpleDataSet<Order>("LoadedOrders");
         orders.KeepData = true;
         orders.Clear();

         string loaded = "";
         foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
         {
            if (loaded.Contains(a.division) == false)
            {
               if( loaded.Length > 0 )
                  orders = new SimpleDataSet<Order>("LoadedOrders", false);
               orders.Command = new ServerCommand(Commands.Impersonate(Commands.GET, a.id), orders.Name);
               updSets.Add(orders);

               loaded += "|" + a.division + "|";
            }
         }

         //foreach (Agent a in ((Manager)CurrentUser.user).GetAgents().Data)
         //{
         //   SimpleDataSet<Order> orders = DataModule.GetUserDataSet(a.id, "LoadedOrders", typeof(SimpleDataSet<Order>), true) as SimpleDataSet<Order>;
         //   updSets.Add(orders);
         //}
      }

      private DateTime begin;
      private DateTime finish;

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
         base.AdjustFilterForDS(dateBegin, dateEnd);
         string uid = DataUtils.MakeFilterFromAgents(null, dsAgents);
         const string FILTER = " and \"start\" <= ToDate('{0:dd/MM/yyyy}') and \"finish\" >= ToDate('{1:dd/MM/yyyy}')";

         dsOrgPlan.Filter = uid + string.Format(FILTER, dateEnd , dateBegin);

         begin = dateBegin.Date;
         finish = dateEnd.Date;
      }

      protected override void AfterRefreshData()
      {
         SimpleDataSet<Order> orders = (SimpleDataSet<Order>)DataModule.Get("LoadedOrders");
         foreach (Order o in orders.Data)
         {
            if (o.created >= begin && o.created < finish)
            {
               o.loadedFromKIS = 1;
               dsOrder.Add(dsOrder.Count, o);
            }
         }
      }

      public string DataUtil { get; set; }
   }

   class DivisionSummaryEx : DivisionSummary
   {
      DataGridViewImageColumn progressColumn;

      public DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig, DataGridViewImageColumn pc) : base(dsConfig)
      {
         progressColumn = pc;
      }

      protected override SummaryDivisionData CreateSummaryDivisionData(Division d)
      {
         return new SummaryDivisionDataEx(d, progressColumn);
      }

      protected override SummaryData CreateSummaryData(Agent agent, DataSet<int, CommonConfig> config)
      {
         return new SummaryDataEx(agent, config, progressColumn);
      }
   }

   class SummaryDataEx : SummaryData
   {
      Image pimage;
      DataGridViewImageColumn progressColumn;
      Dictionary<string, double> weight = new Dictionary<string, double>();
      public double planProgress = 0;
      public bool hasPlan = false;
      public double wplan = 0.0;
      public double wfact = 0.0;

      public SummaryDataEx(Agent a, DataSet<int, CommonConfig> config, DataGridViewImageColumn pc) : base(a, config) 
      {
         progressColumn = pc;
      }

      public override void Add(Order o)
      {
         orders++;
         sum += o.Sum();

         if (o.loadedFromKIS == 0)
            AddOrg(o);
         AddOrder(o.id, o.userid, o.created);

         if (!hasMissedOrder)
            hasMissedOrder = FmDetailBase.OrderMissed(o, (DataSet<int, OrderCommitted>)DataModule.Get(OrderCommitted.OBJECT_NAME));

         wfact += o.Weight;
      }

      public Image ProgressImage2
      {
         get
         {
            if( pimage == null )
               pimage = GRSoft.NapoleonManager.Utils.ProgressImage.CreateProgressImage(PlanProgress, progressColumn);
            return pimage;
         }
      }

      public double Plan { get { return Math.Round(wplan); } }
      public double Fact { get { return Math.Round(wfact); } }

      public double PlanProgress { get { return planProgress; } }

      public double CalcPartPlan(DateTime p1, DateTime p2, DateTime r1, DateTime r2)
      {
         if (r1 < p1)
            r1 = p1;

         if (r2 > p2)
            r2 = p2;

         double plan = (p2 - p1).TotalDays + 1;
         double range = (r2 - r2).TotalDays + 1;

         return range / plan;
      }

      public override void CountProgress(DateTime start, DateTime end, DataSet<int, OrgFolder> dsOrgFolder)
      {
         base.CountProgress(start, end, dsOrgFolder);

         DataSet<int, OrgPlan> dsPlan = (DataSet<int, OrgPlan>) DataModule.Get(OrgPlan.OBJECT_NAME);
         
         if (dsPlan != null)
            foreach (OrgPlan p in dsPlan.Values)
               if (p.userid.Equals(AgentID))
               {
                  if (p.value > 0 && !hasPlan)
                     hasPlan = true;

                  wplan += p.value * CalcPartPlan(p.start, p.finish, start, end);
               }

         if (wplan != 0)
            planProgress = wfact / wplan * 100;
         else
            planProgress = 0;

      }
   }

   class SummaryDivisionDataEx : SummaryDivisionData
   {
      Image pimage;
      DataGridViewImageColumn progressColumn;
      public double planProgress = 0;
      public double wplan = 0.0;
      public double wfact = 0.0;

      public SummaryDivisionDataEx(Division d, DataGridViewImageColumn pc) : base(d)
      {
         progressColumn = pc;
      }

      internal override void AddChildDivision(SummaryDivisionData chData)
      {
         base.AddChildDivision(chData);

         SummaryDivisionDataEx sde = (SummaryDivisionDataEx)chData;

         wplan += sde.wplan;
         wfact += sde.wfact;
      }

      public override void Add(SummaryData data)
      {
         base.Add(data);

         SummaryDataEx sde = (SummaryDataEx)data;

         wplan += sde.wplan;
         wfact += sde.wfact;
      }

      public Image ProgressImage2
      {
         get
         {
            if (pimage == null)
               pimage = GRSoft.NapoleonManager.Utils.ProgressImage.CreateProgressImage(PlanProgress, progressColumn);
            return pimage;
         }
      }

      public double PlanProgress 
      { 
         get 
         {
            
            double result = 0;

            if (wplan > 0)
               result = wfact / wplan * 100;

            return result; 
         } 
      }

      public double Plan { get { return Math.Round(wplan); } }
      public double Fact { get { return Math.Round(wfact); } }
   }
}
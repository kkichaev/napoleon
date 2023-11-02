
using GRSoft.Network;
using System;
using System.Collections.Generic;
using GRSoft.UILib;
using System.Globalization;
using System.Windows.Forms;
using GRSoft.NapoleonManager.Utils;
using System.Drawing;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public class DivisionSummary : Dictionary<string, SummaryData>
   {
      private DataSet<int, CommonConfig> dsConfig;

      AgentRouteSheduleHelper routeHelper = null;

      public static DivisionSummary Create(DataSet<int, CommonConfig> dsConfig)
      {
         Type prcType = FormEntries.GetFormType(typeof(DivisionSummary));
         ConstructorInfo ci = prcType.GetConstructor(BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public, null,
            new Type[] { typeof(DataSet<int, CommonConfig>) }, null);
         DivisionSummary result = (DivisionSummary)ci.Invoke(new object[] { dsConfig });

         return result;
      }

      protected DivisionSummary(DataSet<int, CommonConfig> dsConfig)
      {
         this.dsConfig = dsConfig;
      }

      public void SetRouteHelper(AgentRouteSheduleHelper routeHelper) { this.routeHelper = routeHelper; }

      //Получить суммарные данные по подразделению
      //public object[] MakeDivisionSummary(Division division, DateTime begin, DateTime end, DataGridViewImageColumn progressColumn, TreeGridView treeView)
      public SummaryDivisionData MakeDivisionSummary(Division division, DateTime begin, DateTime end, DataGridViewImageColumn progressColumn, TreeGridView treeView)
      {
         IDataSet cdata;
         Agents dsAgents = Agents.GetDataSet();
         DataSet<int, Schedule> dsSchedule = (DataSet<int, Schedule>)DataModule.Get(Schedule.OBJECT_NAME);

         Clear();

         foreach (Division.DivisionAgent agent in division.agents)
            if (agent.agent != null)
               this.Add(agent.agent);

         cdata = DataModule.Get(Order.OBJECT_NAME);
         if (cdata != null)
            foreach (Order order in cdata.Data)
               this.Add(order);

         cdata = DataModule.Get(Sales.OBJECT_NAME);
         if (cdata != null)
            foreach (Order order in cdata.Data)
               this.Add(order);


         cdata = DataModule.Get(VisitInfo.V_OBJECT_NAME);
         if (cdata != null)
         {
            foreach (VisitInfo v in cdata.Data)
               this.Add(v);
         }
         else
         {
            cdata = DataModule.Get("VisitInfo");
            if( cdata != null )
               foreach (VisitInfo v in cdata.Data)
                  this.Add(v);
         }

         cdata = DataModule.Get(OrgRemnants.OBJECT_NAME);
         if (cdata != null)
            foreach (OrgRemnants or in cdata.Data)
               this.Add(or);

         cdata = DataModule.Get(PKO.OBJECT_NAME);
         if (cdata != null)
            foreach (PKO pko in cdata.Data)
               this.Add(pko);

         cdata = DataModule.Get(Returns.OBJECT_NAME);
         if (cdata != null)
            foreach (Returns r in cdata.Data)
               this.Add(r);

         cdata = DataModule.Get(Incass.OBJECT_NAME);
         if (cdata != null)
            foreach (Incass i in cdata.Data)
               this.Add(i);

         cdata = DataModule.Get(GPSGather.OBJECT_NAME);
         if (cdata != null)
            foreach (GPSGather g in cdata.Data)
               this.Add(g);

#if QUESTION
         cdata = DataModule.Get (Answer.OBJECT_NAME);
         if (cdata != null)
            foreach (Answer answer in cdata.Data)
               this.Add(answer);
#endif


         PostAddData();

         //DivisionSumaryItem dsi = CreateDivisionSummaryItem();
         SummaryDivisionData sdd = CreateSummaryDivisionData(division);

         //DateTime begin = dtpBeginDate.Value.Date;
         //DateTime end = GetRangeEndDate();

         List<GRSoft.UILib.TreeGridNode> list = new List<GRSoft.UILib.TreeGridNode>();
         foreach (KeyValuePair<string, SummaryData> el in this)
         {
            Agent agent = null; 

            if (!dsAgents.ContainsKey(el.Key))
               continue;

            agent = dsAgents[el.Key];

            if (agent == null)
               continue;

            SummaryData data = el.Value;
            data.CountProgress(begin, end, dsSchedule);
            sdd.Add(data);

            DataSet<string, UserActivity> dsUserActivity = (DataSet<string, UserActivity>)DataModule.Get(UserActivity.OBJECT_NAME);
            string lastAccess = string.Empty;
            if (dsUserActivity != null && 
               dsUserActivity.Count > 0 && 
               agent != null && 
               dsUserActivity.ContainsKey(agent.id))
            {
               lastAccess = dsUserActivity[agent.id].date.ToString("dd.MM.yyyy HH:mm");
            }

            TreeGridNode node = new TreeGridNode();
            data.LastAccess = lastAccess;
            data.ProgressImage = data.CreateProgressImage(progressColumn); 

            node.SetValues(treeView, data);

            //node.CreateCells(treeView, agent.Name, data.GetVisitCount().ToString(), data.orders.ToString(),
            //   data.sum.ToString("C", Config.GetCultureInfo()), lastAccess,
            //   ProgressImage.CreateProgressImage(plan, progressColumn, treeView),
            //   agent.id, null, plan, data.hasMissedOrder, data.GetUniqueOrderCount());
            node.Tag = agent;
            list.Add(node);
         }

         sdd.SetAgents(list);
         sdd.ProgressImage = ProgressImage.CreateProgressImage(sdd.plan, progressColumn);
         return sdd;
         //return new object[] { list, division.name, visits.ToString(), orders.ToString(), sum.ToString(),
         //   ProgressImage.CreateProgressImage(totPlan, progressColumn, treeView), -1, null, totPlan, ord_per_day};
      }

      private void Add(GPSGather g)
      {
         if (ContainsKey(g.AgentID))
         {
            SummaryData sd = this[g.AgentID];
            sd.Add(g);
         }
      }

      virtual protected void PostAddData() { }

      virtual protected SummaryData CreateSummaryData(Agent agent, DataSet<int, CommonConfig> config)
      {
         return SummaryData.Create(agent, config);
      }

      virtual protected SummaryDivisionData CreateSummaryDivisionData(Division d)
      {
         Type prcType = FormEntries.GetFormType(typeof(SummaryDivisionData));
         ConstructorInfo ci = prcType.GetConstructor(BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public, null,
            new Type[] { typeof(Division) }, null);
         SummaryDivisionData result = (SummaryDivisionData)ci.Invoke(new object[] { d });

         return result;
      }

#if VAND_PROJECT
      private void Add(VandReload vr)
      {
         if (vr.agent != null && ContainsKey(vr.agent.id))
         {
            SummaryData sd = this[vr.agent.id];
            sd.AddOrg(vr);
         }
      }

      private void Add(VandSales vs)
      {
         if (vs.agent != null && ContainsKey(vs.agent.id))
         {
            SummaryData sd = this[vs.agent.id];
            sd.orders++;
            sd.sum += vs.Sum();
            sd.AddOrg(vs);
         }
      }

      private void Add(VandAudit va)
      {
         if (va.agent != null && ContainsKey(va.userid))
         {
            SummaryData sd = this[va.userid];
            sd.AddOrg(va);
         }
      }
#endif

#if DISTR_DOC
      private void Add(Distr d)
      {
         if (ContainsKey(d.id) == false)
         {
            SummaryData sd = this[d.AgentID];
            sd.AddOrg(d.id, d.AgentID, d.created);
         }
      }
#endif
      public void Add(Agent a)
      {
         if (ContainsKey(a.id) == false)
         {
            SummaryData sd = CreateSummaryData(a, dsConfig);
            sd.SetRouteHelper(routeHelper);
            Add(a.id, sd);
         }
      }

      public virtual void Add(Order o)
      {
         if (ContainsKey(o.AgentID))
         {
            SummaryData sd = this[o.AgentID];
            sd.Add(o);
         }
      }

      //public void Add(ScriptDoc doc)
      //{
      //   if (ContainsKey(doc.AgentID))
      //   {
      //      SummaryData sd = this[doc.AgentID];
      //      sd.AddOrg(doc.id, doc.AgentID, doc.date);
      //   }
      //}

      public void Add(VisitInfo v)
      {
         if (ContainsKey(v.AgentID))
         {
            SummaryData sd = this[v.AgentID];
            sd.Add(v);
         }
      }

      public void Add(OrgRemnants or)
      {
         if (or.agent != null && ContainsKey(or.AgentID))
         {
            SummaryData sd = this[or.AgentID];
            sd.remnants++;
            sd.AddOrg(or);
         }
      }

      internal virtual void Add(PKO pko)
      {
         if (pko.agent != null && ContainsKey(pko.AgentID))
         {
            SummaryData sd = this[pko.AgentID];
            sd.Add(pko);
         }
      }

      internal void Add(Returns r)
      {
         if (r.agent != null && ContainsKey(r.AgentID))
         {
            SummaryData sd = this[r.AgentID];
            sd.AddOrg(r);
         }
      }

      internal virtual void Add(Incass i)
      {
         if (i.agent != null && ContainsKey(i.AgentID))
         {
            SummaryData sd = this[i.AgentID];
            sd.Add(i);
         }
      }

      internal void Add(Answer answer)
      {
         if (answer.agent != null && ContainsKey(answer.AgentID))
         {
            SummaryData sd = this[answer.AgentID];
            sd.AddOrg(answer);
         }
      }
#if MOVEMENT_DOC
      internal void Add(MoveDoc mve)
      {
         if (mve.agent != null && ContainsKey(mve.AgentID))
         {
            SummaryData sd = this[mve.AgentID];
            sd.AddOrg(mve.id, mve.AgentID, mve.created);
         }
      }
#endif

#if PRICE_MONITORING
      internal void Add(Monitoring monitoring)
      {
         if (monitoring.agent != null && ContainsKey(monitoring.AgentID))
         {
            SummaryData sd = this[monitoring.AgentID];
            sd.AddOrg(monitoring);
         }
      }
#endif

#if Sibtrade
      internal void Add(Bonus bonus)
      {
         if (bonus.agent != null && ContainsKey(bonus.AgentID))
         {
            SummaryData sd = this[bonus.AgentID];
            //sd.AddOrg(bonus.id, bonus.AgentID, bonus.created);
            sd.AddOrg(bonus);
         }
      }
#endif
   }

   public class SummaryDivisionData
   {
      protected int visits, orders;

      protected double sum;

      int route = 0;
      int inRoute = 0;
      public double plan { get { return route == 0 ? 0 : (double)inRoute / route * 100; } }
      protected bool hasMissedOrder;
      protected int uniqOrders;

      protected Image progressImage;
      protected string lastAccess;


      Division division;
      protected List<TreeGridNode> agents;
      protected List<SummaryDivisionData> childs = new List<SummaryDivisionData>();

      public SummaryDivisionData(Division division)
      {
         this.division = division;
      }

      virtual public void Add(SummaryData data)
      {
         //this.plan += data.plan;
         route += data.route;
         inRoute += data.inRoute;

         visits += data.GetVisitCount();
         orders += data.GetOrders();
         sum += data.GetSum();
         uniqOrders += data.UniqOrders;
         if (data.HasMissedOrders)
            hasMissedOrder = true;
      }

      public List<TreeGridNode> Agents { get { return agents; } }
      public Division Division { get { return division; } }
      public string Name { get { return division.name; } }
      public virtual Image ProgressImage { get { return progressImage; } set { progressImage = value; } }
      public int Visits { get { return visits; } }
      public int Orders { get { return orders; } }
      public double DocSum { get { return sum; } }
      public double ProgressValue { get { return plan; } }
      public string LastAccess { get { return lastAccess; } set { lastAccess = value; } }
      public bool HasMissedOrders { get { return hasMissedOrder; } }
      public int UniqOrders { get { return uniqOrders; } }

      public int ChildsCount { get { return agents.Count + childs.Count; } }
      public List<SummaryDivisionData> Childs { get { return childs; } }

      virtual internal void AddChildDivision(SummaryDivisionData chData)
      {
         childs.Add(chData);

         visits += chData.visits;
         orders += chData.orders;
         sum += chData.sum;
         uniqOrders += chData.UniqOrders;

         inRoute += chData.inRoute;
         route += chData.route;

         //if (ChildsCount != 0)
         //   plan = (plan * (ChildsCount - 1) + chData.plan) / ChildsCount;
      }

      internal void SetAgents(List<TreeGridNode> agentList)
      {
         this.agents = agentList;
      
         //if (agents.Count == 0)
         //   plan = 0;
         //else
         //   plan /= agents.Count;
      }
   }
}
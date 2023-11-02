
using GRSoft.Network;
using System;
using System.Collections.Generic;
using GRSoft.UILib;
using System.Globalization;
using System.Windows.Forms;
using GRSoft.NapoleonManager.Utils;
using System.Drawing;
using GRSoft.NapoleonMonitor;

namespace GRSoft.NapoleonManager
{
   public class DivisionSummary : Dictionary<string, SummaryData>
   {
      private DataSet<int, CommonConfig> dsConfig;

      public DivisionSummary(DataSet<int, CommonConfig> dsConfig)
      {
         this.dsConfig = dsConfig;
      }

      //Получить суммарные данные по подразделению
      //public object[] MakeDivisionSummary(Division division, DateTime begin, DateTime end, DataGridViewImageColumn progressColumn, TreeGridView treeView)
      public SummaryDivisionData MakeDivisionSummary(Division division, DateTime begin, DateTime end, DataGridViewImageColumn progressColumn, TreeGridView treeView)
      {
         IDataSet cdata;
         Agents dsAgents = Agents.GetDataSet();
         DataSet<int, OrgFolder> dsOrgFolder = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME);

         Clear();

         foreach (Division.DivisionAgent agent in division.agents)
            if (agent.agent != null)
               this.Add(agent.agent);

         cdata = DataModule.Get(ScriptDoc.OBJECT_NAME);
         if (cdata != null)
            foreach (ScriptDoc order in cdata.Data)
               this.Add(order);


         PostAddData();

         //DivisionSumaryItem dsi = CreateDivisionSummaryItem();
         SummaryDivisionData sdd = CreateSummaryDivisionData(division);

         //DateTime begin = dtpBeginDate.Value.Date;
         //DateTime end = GetRangeEndDate();

         List<GRSoft.UILib.TreeGridNode> list = new List<GRSoft.UILib.TreeGridNode>();
         foreach (KeyValuePair<string, SummaryData> el in this)
         {
            Agent agent = dsAgents[el.Key];
            if (agent == null)
               continue;

            SummaryData data = el.Value;
            data.CountProgress(begin, end, dsOrgFolder);
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
            data.ProgressImage = ProgressImage.CreateProgressImage(data.plan, progressColumn);

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

      virtual protected void PostAddData() { }

      virtual protected SummaryData CreateSummaryData(Agent agent, DataSet<int, CommonConfig> config)
      {
         return new SummaryData(agent, config);
      }

      virtual protected SummaryDivisionData CreateSummaryDivisionData(Division d)
      {
         return new SummaryDivisionData(d);
      }

#if Avalon
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
            Add(a.id, CreateSummaryData(a, dsConfig));
      }

      public virtual void Add(ScriptDoc o)
      {
         if (ContainsKey(o.userid))
         {
            SummaryData sd = this[o.userid];
            sd.Add(o);
         }
      }
   }

   public class SummaryDivisionData
   {
      protected int visits, orders;

      protected double sum;
      public double plan;
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
         this.plan += data.plan;

         visits += data.GetVisitCount();
         orders += data.orders;
         sum += data.sum;
         uniqOrders += data.GetUniqueOrderCount();
      }

      public List<TreeGridNode> Agents { get { return agents; } }
      public Division Division { get { return division; } }
      public string Name { get { return division.name; } }
      public Image ProgressImage { get { return progressImage; } set { progressImage = value; } }
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

         if (ChildsCount != 0)
            plan = (plan * (ChildsCount - 1) + chData.plan) / ChildsCount;
      }

      internal void SetAgents(List<TreeGridNode> agentList)
      {
         this.agents = agentList;
      
         if (agents.Count == 0)
            plan = 0;
         else
            plan /= agents.Count;
      }
   }
}

using GRSoft.Network;
using System;
using System.Collections.Generic;
using GRSoft.UILib;
using System.Globalization;
using System.Windows.Forms;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   class DivisionSummary : Dictionary<string, SummaryData>
   {
      private DataSet<int, CommonConfig> dsConfig;

      public DivisionSummary(DataSet<int, CommonConfig> dsConfig)
      {
         this.dsConfig = dsConfig;
      }

      //Получить суммарные данные по подразделению
      public object[] MakeDivisionSummary(Division division, DateTime begin, DateTime end, DataGridViewImageColumn progressColumn, TreeGridView treeView)
      {
         IDataSet cdata;
         Agents dsAgents = Agents.GetDataSet();
         DataSet<int, OrgFolder> dsOrgFolder = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME);

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

         int visits = 0;
         int orders = 0;
         double sum = 0;
         double totPlan = 0;
         //DateTime begin = dtpBeginDate.Value.Date;
         //DateTime end = GetRangeEndDate();

         List<GRSoft.UILib.TreeGridNode> list = new List<GRSoft.UILib.TreeGridNode>();
         foreach (KeyValuePair<string, SummaryData> el in this)
         {
            Agent agent = dsAgents[el.Key];
            if (agent == null)
               continue;

            SummaryData data = el.Value;
            double plan = data.Progress(agent.id, begin, end, dsOrgFolder);
            totPlan += plan;

            visits += data.GetVisitCount();
            orders += data.orders;
            sum += data.sum;

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
            node.CreateCells(treeView, agent.name, data.GetVisitCount().ToString(), data.orders.ToString(),
               data.sum.ToString("C", Config.GetCultureInfo()), lastAccess,
               ProgressImage.CreateProgressImage(plan, progressColumn, treeView), agent.id, null, plan);
            node.Tag = agent;
            list.Add(node);
         }

         if (this.Count == 0)
            totPlan = 0;
         else
            totPlan /= this.Count;
         return new object[] { list, division.name, visits.ToString(), orders.ToString(), sum.ToString(),
            ProgressImage.CreateProgressImage(totPlan, progressColumn, treeView), -1, null, totPlan };
      }

      public void Add(Agent a)
      {
         if (ContainsKey(a.id) == false)
            Add(a.id, new SummaryData(dsConfig));
      }

      public void Add(Order o)
      {
         if (ContainsKey(o.AgentID))
         {
            SummaryData sd = this[o.AgentID];
            sd.orders++;
            sd.sum += o.Sum();
            sd.AddOrg(o.id, o.AgentID, o.created);
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
            //sd.visit++;
            sd.AddOrg(v.id, v.AgentID, v.date);
         }
      }

      public void Add(OrgRemnants or)
      {
         if (or.agent != null && ContainsKey(or.AgentID))
         {
            SummaryData sd = this[or.AgentID];
            sd.remnants++;
            sd.AddOrg(or.id, or.AgentID, or.date);
         }
      }

      internal void Add(PKO pko)
      {
         if (pko.agent != null && ContainsKey(pko.AgentID))
         {
            SummaryData sd = this[pko.AgentID];
            sd.pko++;
            sd.sum += pko.Sum;
            sd.AddOrg(pko.id, pko.AgentID, pko.date);
         }
      }
   }
}
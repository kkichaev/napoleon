using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmRpt : Form
   {
      List<IDataSet> dsVisit = new List<IDataSet>();
      DataSet<int, Order> dsOrder;
      DataSet<string, Org> dsOrg;

      public FmRpt()
      {
         InitializeComponent();

         dsVisit.Add(DataModule.Get(Visit.V_OBJECT_NAME) ?? new DataSet<int, VisitInfo>(Visit.V_OBJECT_NAME));
         dsVisit.Add(DataModule.Get(Incass.OBJECT_NAME) ?? new DataSet<int, Incass>(Incass.OBJECT_NAME));
         dsVisit.Add(DataModule.Get(PKO.OBJECT_NAME) ?? new DataSet<int, PKO>(PKO.OBJECT_NAME));
         dsVisit.Add(DataModule.Get(OrgRemnants.OBJECT_NAME) ?? new DataSet<int, OrgRemnants>(OrgRemnants.OBJECT_NAME));
         dsVisit.Add(DataModule.Get(TaskDone.OBJECT_NAME) ?? new DataSet<int, TaskDone>(TaskDone.OBJECT_NAME));
         dsVisit.Add(DataModule.Get(Answer.OBJECT_NAME) ?? new DataSet<int, Answer>(Answer.OBJECT_NAME));

         dsOrder = (DataSet<int, Order>)DataModule.Get(Order.OBJECT_NAME) ?? new DataSet<int, Order>(Order.OBJECT_NAME);
         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.OBJECT_NAME) ?? new DataSet<string, Org>(Org.OBJECT_NAME);
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         initFilter();
      }

      private String AgentWhere()
      {
         StringBuilder result = new StringBuilder();

         if (rbAgent.Checked && cbAgent.SelectedItem != null)
         {
            Agent agent = cbAgent.SelectedItem as Agent;

            if (agent != null)
               result.Append("\"userid\" = '").Append(agent.id).Append("'");
         }
         else if (rbDivision.Checked && cbDivision.SelectedItem != null)
         {
            Division division = cbDivision.SelectedItem as Division;

            if (division != null)
            {
               List<GRSoft.NapoleonManager.Division.DivisionAgent> agents = division.GetAllAgents();

               if (agents.Count > 0)
               {
                  result.Append("\"userid\" in (");

                  List<GRSoft.NapoleonManager.Division.DivisionAgent>.Enumerator iter = agents.GetEnumerator();
                  List<string> ids = new List<string>();

                  while (iter.MoveNext())
                  {
                     GRSoft.NapoleonManager.Division.DivisionAgent agent = iter.Current;

                     if (agent != null)
                        ids.Add(String.Format("'{0}'", agent.id));
                  };

                  result.Append(String.Join(",", ids.ToArray()));
                  result.Append(")");
               }
            }
         }

         return result.ToString();
      }

      private void initFilter()
      {
         string FILTER_STR = "\"created\" >= ToDate('{0:dd/MM/yyyy}') and \"created\" < ToDate('{1:dd/MM/yyyy} 23:59:59')";

         List<IDataSet> upd = new List<IDataSet>();

         upd.Add(dsOrg);

         String agentWhere = AgentWhere();

         if (agentWhere.Length > 0)
            FILTER_STR += " and " + agentWhere;

         foreach (object o in dsVisit)
         {
            IDataSet ds = (IDataSet)o;
            ds.Filter = string.Format(FILTER_STR, period.Start.Date, period.Finish.Date);
            upd.Add(ds);
         }

         dsOrder.Filter = string.Format(FILTER_STR, period.Start.Date, period.Finish.Date);
         upd.Add(dsOrder);

         FmWait.StdDataRefresh(this, upd, () => { dataLoaded(); });
      }

      private void dataLoaded()
      {
         ReportData rd = makeData();
         new Rpt(rd);
      }

      private ReportData makeData()
      {
         ReportData result = new ReportData();
         result.initData(dsOrder, dsVisit);
         
         return result;
      }

      class DocComparer : IComparer<BaseDocument>
      {
         public int Compare(BaseDocument x, BaseDocument y)
         {
            int result = x.AgentName.CompareTo(y.AgentName);

            if (result == 0)
               result = x.OrgName.CompareTo(y.OrgName);

            if (result == 0)
               result = x.created.CompareTo(y.created);

            return result;
         }
      }

      class DataItem
      {
         public string agentName = string.Empty;
         public string userid = string.Empty;
         public string orgName = string.Empty;
         public string orgid = string.Empty;
         public int succ = 0;
         public int inRoute = 0;
         public int outRoute = 0;
         public DateTime created = DateTime.MinValue;
      }

      class ReportData
      { 
         private Dictionary<string, List<string>> agentRoute = new Dictionary<string, List<string>>();
         public List<DataItem> items = new List<DataItem>();

         private static List<string> GetRoutePeriod(DateTime begin, DateTime end, Agent agent)
         {
            List<string> result = new List<string>();
            List<string> dayProcessed = new List<string>();
            DataSet<int, OrgFolder> routes = (DataSet<int, OrgFolder>)DataModule.Get(OrgFolder.OBJECT_NAME);
            DataSet<int, CommonConfig> configs = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME);

            SummaryData sd = new SummaryData(agent, configs);

            while (begin.Date < end.Date)
            {
               WeekDay weekDay = new WeekDay(begin.DayOfWeek);

               List<OrgFolderItem> items = sd.GetAgentRoute(begin, routes.Data);
               if (items != null)
               {
                  foreach (OrgFolderItem item in items)
                     if (!result.Contains(item.name))
                        result.Add(item.name);
               }

               begin = begin.AddDays(1);
            }

            return result;
         }

         private void doCreateItems(List<BaseDocument> list, bool order, Dictionary<string, DataItem> outer)
         {
            foreach (BaseDocument d in list)
            {
               string key = createKey(d);
               if (!outer.ContainsKey(key) && d.agent != null)
               {
                  DataItem item = new DataItem();
                  item.agentName = d.AgentName;
                  item.userid = d.userid;
                  item.orgName = d.OrgName;
                  item.orgid = d.id;
                  item.succ = order ? 1 : 0;
                  item.created = d.created;

                  string rk = createRouteKey(d);

                  if (!agentRoute.ContainsKey(rk))
                     agentRoute.Add(rk, GetRoutePeriod(d.created.Date, d.created.Date.AddDays(1), d.agent));

                  item.inRoute = agentRoute[rk].Contains(d.id) ? 1 : 0;
                  item.outRoute = item.inRoute == 1 ? 0 : 1;
                  outer.Add(key, item);
               }
               else
               {
                  DataItem item = outer[key];

                  if (d.created < item.created)
                     item.created = d.created;
               }
            }
         }

         private bool addOrders(ICollection outer, ICollection data)
         {
            List<BaseDocument> list = (List<BaseDocument>)outer;
            DataSet<int, Order> dsOrder = (DataSet<int, Order>)data;

            foreach (Order o in dsOrder.Data)
               list.Add(o);

            return true;
         }

         private delegate bool Func(ICollection outer, ICollection data);

         private void doProcess(List<BaseDocument> list, ICollection data, IComparer<BaseDocument> cmp, Func func, Dictionary<string, DataItem> outer)
         {
            bool succ = func(list, data);
            list.Sort(cmp);
            doCreateItems(list, succ, outer);
         }

         public void initData(DataSet<int, Order> dsOrder, List<IDataSet> dsVisits) 
         {
            Dictionary<string, DataItem> data = new Dictionary<string, DataItem>();
            agentRoute.Clear();

            List<BaseDocument> list = new List<BaseDocument>();
            DocComparer cmp = new DocComparer();
            doProcess(list, dsOrder, cmp, addOrders, data);
            list.Clear();
            doProcess(list, dsVisits, cmp, addVisits, data);

            items.AddRange(data.Values);
            items.Sort(sortItems);
         }

         private int sortItems(DataItem lhs, DataItem rhs) 
         {
            int result = lhs.agentName.CompareTo(rhs.agentName);

            if (result == 0)
               result = lhs.orgName.CompareTo(rhs.orgName);

            if (result == 0)
               result = lhs.created.CompareTo(rhs.created);

            return result;
         }

         private bool addVisits(ICollection outer, ICollection data)
         {
            List<BaseDocument> list = (List<BaseDocument>)outer;
            List<IDataSet> dsVisits = (List<IDataSet>)data;

            foreach (IDataSet ds in dsVisits)
               foreach (BaseDocument d in ds.Data)
                  list.Add(d);

            return false;
         }

         private string createKey(BaseDocument d)
         {
            StringBuilder result = new StringBuilder();

            result.Append(d.userid);
            result.Append(d.id);
            result.Append(d.created.ToString("yyyyMMdd"));

            return result.ToString();
         }

         private string createRouteKey(BaseDocument d)
         {
            StringBuilder result = new StringBuilder();

            result.Append(d.userid);
            result.Append(d.created.ToString("yyyyMMdd"));

            return result.ToString();
         }
      }

      
      class Rpt : Excel
      {
         private void groupFooter(int row, int grow, DataItem i)
         {
            int dif = grow - row;
            SetValue(row, 1, i.agentName);
            SetValue(row, 2, i.userid);
            SetValue(row, 3, "ИТОГО");
            SetValue(row, 5, "=СУММ(R[" + dif.ToString() + "]C:R[-1]C)");
            SetValue(row, 6, "=СУММ(R[" + dif.ToString() + "]C:R[-1]C)");
            SetValue(row, 7, "=СУММ(R[" + dif.ToString() + "]C:R[-1]C)");
            SetValue(row, 8, "=СУММ(RC[-2]:RC[-1])");
            SetProperty(GetCell(row, 8), "NumberFormat", "0");
            SetValue(row, 10, "=RC[-5] / RC[-2] * 100");
            SetProperty(GetCell(row, 10), "NumberFormat", "0");
         }

         
         public Rpt(ReportData rd)
         {
            SetValue(1, 1, "Агент");
            SetValue(1, 2, "Код агена");
            SetValue(1, 3, "ТТ");
            SetValue(1, 4, "Код ТТ");
            SetValue(1, 5, "Успешный");
            SetValue(1, 6, "Плановый");
            SetValue(1, 7, "Внеплановый");
            SetValue(1, 8, "Дата");
            SetValue(1, 9, "Время перв. док.");
            SetValue(1, 10, "% успешных");

            int row = 2;

            string userid = null;
            int grow = row;
            DataItem lastitem = null;

            foreach (DataItem i in rd.items)
            {
               if (userid == null)
               {
                  userid = i.userid;
                  grow = row;
               }
               else if (!userid.Equals(i.userid) && lastitem != null)
               {
                  groupFooter(row, grow, lastitem);
                  row += 2;
                  grow = row;
                  userid = i.userid;
               }

               SetValue(row, 1, i.agentName);
               SetValue(row, 2, i.userid);
               SetValue(row, 3, i.orgName);
               SetValue(row, 4, i.orgid);
               SetValue(row, 5, i.succ);
               SetValue(row, 6, i.inRoute);
               SetValue(row, 7, i.outRoute);
               SetValue(row, 8, i.created.ToString("dd.MM.yyyy"));
               SetValue(row, 9, i.created.ToString("HH:mm"));

               lastitem = i;
               row++;
            }

            if(lastitem != null)
               groupFooter(row, grow, lastitem);

            Visible = true;
         }
      }

      private void FmRpt_Load(object sender, EventArgs e)
      {
         Manager m = CurrentUser.user as Manager;
         List<Agent> agents = new List<Agent>();
         List<Division> divisions = new List<Division>();

         if (m != null)
         {
            foreach (Division.DivisionAgent a in m.Division.GetAllAgents())
               if (a.agent != null && agents.Contains(a.agent) == false)
                  agents.Add(a.agent);

            divisions.Add(m.Division);
            foreach (Division d in m.Childs)
               divisions.Add(d);
         }

         if (agents.Count > 0)
         {
            agents.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });
            cbAgent.Items.AddRange(agents.ToArray());
            cbAgent.SelectedIndex = 0;
         }


         if (divisions.Count > 0)
         {
            divisions.Sort((lhs, rhs) => { return lhs.DivisionName.CompareTo(rhs.DivisionName); });
            cbDivision.Items.AddRange(divisions.ToArray());
            cbDivision.SelectedIndex = 0;
         }

         cbAgent.Enabled = false;
         cbDivision.Enabled = false;
         rbAll.Checked = true;

         period.Start = DateTime.Now;
         period.Finish = DateTime.Now;
      }

      private void rbAgent_CheckedChanged(object sender, EventArgs e)
      {
         if (sender is RadioButton)
            cbAgent.Enabled = (sender as RadioButton).Checked;
      }

      private void rbDivision_CheckedChanged(object sender, EventArgs e)
      {
         if (sender is RadioButton)
            cbDivision.Enabled = (sender as RadioButton).Checked;
      }
   }
}

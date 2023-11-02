using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using System.IO;

namespace GRSoft.NapoleonManager
{
   public partial class FmAgentPlanReport : Form
   {
      public FmAgentPlanReport()
      {
         InitializeComponent();
         cbType.SelectedIndex = 0;
         dtBegin.Value = DateTime.Now;
         dtEnd.Value = DateTime.Now;
      }

      DateTime begin, end;
      int reportType;
      SimpleDataSet<AgentPlan> plans = new SimpleDataSet<AgentPlan>(AgentPlan.OBJECT_NAME, false);
      SimpleDataSet<Order> orders = new SimpleDataSet<Order>(Order.OBJECT_NAME, false);
      DataSet<string, Price> price;

      private void ok_Click(object sender, EventArgs e)
      {
         reportType = cbType.SelectedIndex;

         Manager m = CurrentUser.user as Manager;
         if (m == null)
         {
            MessageBox.Show("Пользователь не является менеджером");
            return;
         }

         DBConnection conn = Config.GetConfig().GetConnection();
         List<IDataSet> upd = new List<IDataSet>();
         begin = new DateTime(dtBegin.Value.Year, dtBegin.Value.Month, 1);
         end = new DateTime(dtEnd.Value.Year, dtEnd.Value.Month, 1);
         end = end.AddMonths(1);

         if (price == null)
         {
            price = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ??
               new DataSet<string, Price>(Price.OBJECT_NAME, true);
         }
         if (price.Count == 0)
            upd.Add(price);

         const String FILTER = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" < ToDate('{2:dd/MM/yyyy}')";
         plans.Filter = String.Format(FILTER, "begin", begin, end);
         upd.Add(plans);

         orders.Filter = String.Format(FILTER, "created", begin, end);
         upd.Add(orders);
         DataModule.OnDataResponceError += new EventDataResponseError(DataError);
         DataModule.DataProcessed += new EventHandler(DataReceived);

         Thread t = DataModule.RefreshGiveSets(conn, upd, FmWait.ProgressIndicator);
         FmWait.ShowForm(this, t);
      }

      class OrderKey : IEquatable<OrderKey>
      {
         Agent agent;
         DateTime begin;

         public OrderKey(Order o)
         {
            agent = o.agent;
            begin = new DateTime(o.date.Year, o.date.Month, 1);
         }

         public OrderKey(Agent a, DateTime cur)
         {
            agent = a;
            begin = cur;
         }

         public override int GetHashCode()
         {
            return agent.id.GetHashCode() + begin.GetHashCode();
         }

         #region Члены IEquatable<OrderKey>

         public bool Equals(OrderKey a)
         {
            return (agent.id.CompareTo(a.agent.id) == 0 && begin.CompareTo(a.begin) == 0);
         }

         #endregion
      }

      void DataReceived(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Manager m = CurrentUser.user as Manager;
         Agents a = m.GetAgents();

         Dictionary<OrderKey, OrderData> orderData = new Dictionary<OrderKey, OrderData>();
         foreach(Order o in orders.Data)
         {
            OrderData od = null;
            OrderKey ok = new OrderKey(o);
            if (orderData.ContainsKey(ok))
               od = orderData[ok];
            else
            {
               od = new OrderData();
               orderData.Add(ok, od);
            }
            od.Add(o, price);
         }

         Dictionary<Agent, List<PlanItem>> data = new Dictionary<Agent, List<PlanItem>>();
         foreach (Agent agent in a.Data)
         {
            List<PlanItem> planItems = new List<PlanItem>();
            for (DateTime cur = begin; cur.CompareTo(end) < 0; cur = cur.AddMonths(1))
            {
               PlanItem pi = new PlanItem(agent, cur);

               OrderKey ok = new OrderKey(agent, cur);
               OrderData odata = (orderData.ContainsKey(ok) ? orderData[ok] : null);
               pi.FillAgentPlans(agent, plans.Data, odata);
               planItems.Add(pi);
            }

            data.Add(agent, planItems);
         }

         string fileName;
         if (reportType == 0)
         {
            fileName = PrintData(data);
            OpenLink.NewWindow(String.Format("\"{0}\"", fileName));
         }
         else
         {
            fileName = DoCSV(data);
            OpenLink.OpenFile(String.Format("\"{0}\"", fileName));
         }
         Invoke(new EmptyParamHandler(delegate() { Close(); }));
      }

      private string DoCSV(Dictionary<Agent, List<PlanItem>> data)
      {
         StringBuilder sb = new StringBuilder();
         foreach (KeyValuePair<Agent, List<PlanItem>> kv in data)
         {
            foreach (PlanItem pi in kv.Value)
            {
               sb.AppendFormat("{0};{1:MMMM yyyy};\"Заморозка\";{2};{3};", kv.Key.Name, pi.Begin, pi.Plan1, pi.Fact1)
                 .Append(Environment.NewLine)
                 .AppendFormat("{0};{1:MMMM yyyy};\"Колбасы\";{2};{3};", kv.Key.Name, pi.Begin, pi.Plan2, pi.Fact2)
                 .Append(Environment.NewLine);
            }
         }
         string fileName = String.Format("agent_plan_report{0}.csv", ++doc_number);
         string result = System.IO.Path.GetTempPath() + fileName;
         using (StreamWriter sw = new StreamWriter(result, false, Encoding.GetEncoding("Windows-1251")))
         {
            sw.Write(sb.ToString());
            sw.Flush();
         }

         return result;
      }

      private string PrintData(Dictionary<Agent, List<PlanItem>> data)
      {
         StringBuilder sb = new StringBuilder();
         sb.Append("<html><head><meta http-equiv='content-type' content='text/html; charset=utf-8'>" +
           "</head><body><FONT FACE='Arial'>")
         .Append("<H2>Отчет по планам </H2>")
         .AppendFormat("<FONT SIZE='2'>Период: <b>{0:MMMM yyyy} - {1:MMMM yyyy}</b></FONT>", begin, end.AddMonths(-1));
         foreach(KeyValuePair<Agent, List<PlanItem>> kv in data)
         {
            sb.AppendFormat("<h3>Торговый агент: {0}</h3>", kv.Key.Name)
              .Append("<table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR='#000000'><tr BGCOLOR='#CCCCCC' >")
              .Append("<td rowspan='2'><FONT SIZE='2'><b>Название</b></td>");

            StringBuilder head2 = new StringBuilder("</tr><tr BGCOLOR='#CCCCCC' >");
            StringBuilder row1 = new StringBuilder("</tr><tr>");
            StringBuilder row2 = new StringBuilder("</tr><tr>");
            row1.AppendFormat("<td>{0}</td>", "Заморозка");
            row2.AppendFormat("<td>{0}</td>", "Колбасы");
            foreach(PlanItem pi in kv.Value)
            {
               sb.AppendFormat("<td colspan='2'><FONT SIZE='2'><b>{0:MMMM yyyy}</b></td>", pi.Begin);
               head2.Append("<td><FONT SIZE='2'><b>План</b></td><td><FONT SIZE='2'><b>Факт</b></td>");
               row1.AppendFormat("<td>{0}</td><td>{1}</td>", pi.Plan1, pi.Fact1);
               row2.AppendFormat("<td>{0}</td><td>{1}</td>", pi.Plan2, pi.Fact2);
            }

            sb.Append(head2).Append(row1).Append(row2).Append("</table>");
         }

         sb.Append("<SUB>Построен в системе \"Наполеон\" <a href=http://grsoft.ru/>http://grsoft.ru/</a></SUB></body></html>");

         string fileName = String.Format("agent_plan_report{0}.html", ++doc_number);
         string result = System.IO.Path.GetTempPath() + fileName;
         using (StreamWriter sw = new StreamWriter(result))
         {
            sw.Write(sb.ToString());
            sw.Flush();
         }

         return result;
      }

      static int doc_number;

      void DataError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
      }
   }
}

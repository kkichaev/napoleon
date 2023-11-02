using GRSoft.Network;
using GRSoft.UILib;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Globalization;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class HtmlReportEx : HtmlReport
   {
      Dictionary<string, TimeSpan> visitTime = new Dictionary<string, TimeSpan>();
      Dictionary<string, int> avgOrder = new Dictionary<string, int>();
      Dictionary<string, int> itemOrder = new Dictionary<string, int>();

      public override string makeAgentSummaryFileInfo(TreeGridView grid, TimeInterval interval)
      {
         CollectVisitTime();
         CollectAvgOrder();
         return base.makeAgentSummaryFileInfo(grid, interval);
      }

      private void CollectAvgOrder()
      {
         avgOrder.Clear();

         Dictionary<String, List<Order>> map = new Dictionary<string, List<Order>>();
         DataSet<int, Order> dsOrder = DataModule.Get("Order") as DataSet<int, Order>;

         foreach (Order ord in dsOrder.Data)
         {
            if (!map.ContainsKey(ord.userid))
               map[ord.userid] = new List<Order>();

            map[ord.userid].Add(ord);
         }

         foreach (KeyValuePair<String, List<Order>> entry in map)
         {
            avgOrder[entry.Key] = CalcAvgOrder(entry.Value);
            itemOrder[entry.Key] = CalcOrderItems(entry.Value);
         }
      }

      private int CalcOrderItems(List<Order> list)
      {
         int res = 0;

         foreach (Order o in list)
            res += o.items.Count;

         return res;
      }

      public int CalcAvgOrder(List<Order> list)
      {
         int res = 0;

         for (int i = 0; i < list.Count; i++)
         {
            res += list[i].items.Count;
         }

         res = list.Count != 0 ? res / list.Count : 0;

         return res;
      }

      public override string TableHeader(string html)
      {
         html += String.Format("<tr BGCOLOR=\"#CCCCCC\" ><td><FONT SIZE=\"2\"><b>{0}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{1}</b></td><td>" +
            "<FONT SIZE=\"2\"><b>{2}</b></td><td>" +
            "<FONT SIZE=\"2\"><b>{3}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{4}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{5}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{6}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{7}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{8}</b></td></tr>",
            "Подразделение / агент", "визиты", "заявки", "сумма", "процент заявок", "прогресс", "рабочее время", "Кол-во строк в заказах", "Среднее кол-во в день");
         return html;
      }

      private void CollectVisitTime()
      {
         visitTime.Clear();

         Dictionary<String, List<VisitInfo>> map = new Dictionary<string, List<VisitInfo>>();
         DataSet<int, VisitInfo> dsVisit = DataModule.Get("VisitInfo") as DataSet<int, VisitInfo>;

         foreach (VisitInfo vi in dsVisit.Data)
         {
            if (!map.ContainsKey(vi.userid))
               map[vi.userid] = new List<VisitInfo>();

            map[vi.userid].Add(vi);
         }

         foreach (KeyValuePair<String, List<VisitInfo>> entry in map)
         {
            visitTime[entry.Key] = calcTime(entry.Value);
         }
      }

      private TimeSpan calcTime(List<VisitInfo> list)
      {
         TimeSpan res = new TimeSpan();

         int day = -1;
         DateTime first = DateTime.MinValue;
         DateTime last = DateTime.MinValue;

         for (int i = 0; i < list.Count; i++)
         {
            if (day == -1 || list[i].Created.Day != day)
            {
               if (day != -1)
                  res += last - first;

               day = list[i].Created.Day;
               first = list[i].Created;
            }

            last = list[i].Created;
         }

         if (first.Day == last.Day)
            res += last - first;

         return res;
      }

      protected override string makeHtmlRowFromTreeGridNode(TreeGridNode node, TreeGridView grid,
         AgentSummaryInfo asi)
      {
         string result = "<tr>";
         bool expanded = false;

         if (node.Nodes.Count > 0)
         {
            if (!node.IsExpanded)
            {
               try
               {
                  node.Expand();
                  expanded = true;
               }
               catch (Exception) { }
            }
         }

         Agent a = node.Tag as Agent;
         Division d = node.Tag as Division;

#if WEIGHT_IN_TOTAL_REPORT
         double weight = 0;

         if( a != null )
         {
            Dictionary<string, bool> ad = new Dictionary<string, bool>();
            ad[a.id] = true;
            weight = CountOrdersWeigth(ad);
            asi.weight += weight;
         }
         else if (d != null)
         {
            Dictionary<string, bool> ad = new Dictionary<string, bool>();
            foreach (Division.DivisionAgent da in d.agents)
               ad[da.id] = true;
            weight = CountOrdersWeigth(ad);
         }
#endif

         TimeSpan time = new TimeSpan();
         int average = 0;
         int itemCount = 0;

         if (a != null)
         {
            asi.VisitTime += visitTime.ContainsKey(a.id) ? visitTime[a.id] : new TimeSpan();
            asi.AvgOrd += avgOrder.ContainsKey(a.id) ? avgOrder[a.id] : 0;
            asi.OrdItemCount += itemOrder.ContainsKey(a.id) ? itemOrder[a.id] : 0; 

            time = visitTime.ContainsKey(a.id) ? visitTime[a.id] : new TimeSpan();
            average = avgOrder.ContainsKey(a.id) ? avgOrder[a.id] : 0;
            itemCount = itemOrder.ContainsKey(a.id) ? itemOrder[a.id] : 0; 
         }
         else if (d != null)
         {
            foreach (Division.DivisionAgent da in d.agents)
            {
               time += visitTime.ContainsKey(da.id) ? visitTime[da.id] : new TimeSpan();
               average += avgOrder.ContainsKey(da.id) ? avgOrder[da.id] : 0;
               itemCount += itemOrder.ContainsKey(da.id) ? itemOrder[da.id] : 0; 
            }

            average = d.agents.Count != 0 ? average / d.agents.Count : 0;
            time = d.agents.Count != 0 ? new TimeSpan(time.Ticks / d.agents.Count ) : new TimeSpan();
         }

         if (node.Nodes.Count == 0)
         {
            asi.visitCount += Convert.ToInt32(node.Cells[1].Value);
            asi.orderCount += Convert.ToInt32(node.Cells[2].Value);
            asi.unicOrderCount += Convert.ToInt32(node.Cells[10].Value);
            NumberStyles styles = NumberStyles.Number | NumberStyles.AllowCurrencySymbol;
            asi.summ += Double.Parse(node.Cells[3].Value.ToString(), styles, Config.GetCultureInfo());
         }

         for (int i = 0; i < node.Cells.Count; i++)
         {
            if (node.Cells[i].Value != null && grid.Columns[i].Visible && i != 5 && i != 4 && CanPrint(i))
            {
               result += String.Format("<td><FONT SIZE=\"2\">{0}</td>", node.Cells[i].Value.ToString());
#if WEIGHT_IN_TOTAL_REPORT
               if( i == 2 )
                  result += String.Format("<td><FONT SIZE=\"2\">{0:0.0}</td>", weight);
#endif
            }
         }

         result += String.Format("<td><FONT SIZE=\"2\">{0:0}</td>",
            getVisitPercent(Convert.ToInt32(node.Cells[1].Value), Convert.ToInt32(node.Cells[10].Value)));

         Bitmap bmp = node.Cells[5].Value as Bitmap;
         double progress = (bmp == null) ? 0 : (double)(bmp.Tag);
         result += String.Format("<td><FONT SIZE=\"2\">{0:0}</td>", progress);

         result += String.Format("<td><FONT SIZE=\"2\">{0}:{1}</td>", time.Hours, time.Minutes);
         result += String.Format("<td><FONT SIZE=\"2\">{0}</td>", itemCount);
         result += String.Format("<td><FONT SIZE=\"2\">{0}</td>", average);

         result += PostUpdateForNode(node);

         result += "</tr>";

         foreach (TreeGridNode childNode in node.Nodes)
         {
            result += makeHtmlRowFromTreeGridNode(childNode, grid, asi);
         }

         if (expanded)
         {
            node.Collapse();
         }

         return result;
      }

      protected override string TableFooter(string html, AgentSummaryInfo asi)
      {
         html += String.Format("<tr><td><FONT SIZE=\"2\"><b>{0}</b></td><td><FONT SIZE=\"2\"><b>{1}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{2}</b></td><td><FONT SIZE=\"2\"><b>{3}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{4:0.00}</b></td><td>&nbsp</td>"+
            "<td>&nbsp</td><td>&nbsp</td><td>&nbsp</td></tr>",
            "Итого", asi.visitCount, asi.orderCount,
            asi.summ.ToString("C", Config.GetCultureInfo()),
            getVisitPercent(asi.visitCount, asi.orderCount));
         return html;
      }
   }

   public partial class AgentSummaryInfo
   {
      public TimeSpan VisitTime { get; set; }
      public int AvgOrd { get; set; }
      public int OrdItemCount { get; set; }
   }
}


/*
 * Copyright (C), 2011, Гильдия разработчиков
 *
 * Базовый класс для отчетов экспортируемых в HTML
 * 
 * kki   19/03/2011   creating
 */

using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.UILib;
using System.IO;
using System.Globalization;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Reports;
using System.Reflection;
using System.Drawing;

namespace GRSoft.NapoleonManager
{
   internal class HtmlReport
   {
      protected static int doc_number;
      protected readonly string reportFileName;

      public HtmlReport()
      { 
      }

      public HtmlReport(string fileMask)
      {
         this.reportFileName = System.IO.Path.GetTempPath() + 
            String.Format(fileMask, ++doc_number);
      }

      public void makeHtmlFile(DataGridView dgv)
      {
         
      }

      

      double CountOrdersWeigth(Dictionary<string, bool>  ad)
      {
         double weight = 0;
         IDataSet cdata = DataModule.Get(Order.OBJECT_NAME);
         if (cdata != null)
            foreach (Order order in cdata.Data)
            {
               if (ad.ContainsKey(order.AgentID))
                  weight += order.Weight;
            }
         return weight;
      }

      protected virtual string makeHtmlRowFromTreeGridNode(TreeGridNode node, TreeGridView grid, 
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
               catch(Exception){ }
            }
         }

#if WEIGHT_IN_TOTAL_REPORT
         double weight = 0;
         Agent a = node.Tag as Agent;
         Division d = node.Tag as Division;
 
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

      protected virtual string PostUpdateForNode(TreeGridNode node)
      {
         return string.Empty;
      }

      protected virtual bool CanPrint(int i)
      {
         return true;
      }

      public virtual string makeAgentSummaryFileInfo(TreeGridView grid, TimeInterval interval)
      {        
         string fileName = String.Format("agent_summary_file_info_{0}.html", ++doc_number);
         string result = System.IO.Path.GetTempPath() + fileName;

         int clmns = grid.Columns.Count;

         string html = "<html><head> " + 
            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"+
            "</head><body>" + 
               "<FONT FACE=\"Arial\">" + 
               "<table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR=\"#000000\">";
         html += String.Format("<H3>Итоговый отчёт подразделения {0} </H3><p>", grid.Nodes[0].Cells[0].Value.ToString());
         html += String.Format("<FONT SIZE=\"2\">Период: <b>{0} - {1}</b><br><br>",
            interval.begin.ToString("dd.MM.yyyy"), interval.end.ToString("dd.MM.yyyy"));
#if WEIGHT_IN_TOTAL_REPORT
         html += String.Format("<tr BGCOLOR=\"#CCCCCC\" ><td><FONT SIZE=\"2\"><b>{0}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{1}</b></td><td>" +
            "<FONT SIZE=\"2\"><b>{2}</b></td><td>" +
            "<FONT SIZE=\"2\"><b>{3}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{4}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{5}</b></td>"+
            "<td><FONT SIZE=\"2\"><b>{6}</b></td></tr>",
            "Подразделение / агент", "визиты", "заявки", "вес", "сумма", "процент заявок", "прогресс");

#else
         html = TableHeader(html);

#endif

         AgentSummaryInfo asi = new AgentSummaryInfo();
         foreach (TreeGridNode node in grid.Nodes)
         {
            html += makeHtmlRowFromTreeGridNode(node, grid, asi);
         }


#if WEIGHT_IN_TOTAL_REPORT
         html += String.Format("<tr><td><FONT SIZE=\"2\"><b>{0}</b></td><td><FONT SIZE=\"2\"><b>{1}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{2}</b></td><td><FONT SIZE=\"2\"><b>{3:0.0}</b></td><td><FONT SIZE=\"2\"><b>{4}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{5:0.00}</b></td><td></td></tr>",
            "Итого", asi.visitCount, asi.orderCount, asi.weight,
            asi.summ.ToString("C", Config.GetCultureInfo()),
            getVisitPercent(asi.visitCount, asi.orderCount));
#else
         html = TableFooter(html, asi);
#endif
         html += "</table>";
         html += "<SUB>Построен в системе \"Наполеон\" <a href=http://grsoft.ru/>http://grsoft.ru/</a></SUB>";
         html += "</body></html>";

         StreamWriter sw = new StreamWriter(result);
         sw.Write(html);
         sw.Flush();
         return result;
      }

      protected virtual string TableFooter(string html, AgentSummaryInfo asi)
      {
         html += String.Format("<tr><td><FONT SIZE=\"2\"><b>{0}</b></td><td><FONT SIZE=\"2\"><b>{1}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{2}</b></td><td><FONT SIZE=\"2\"><b>{3}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{4:0.00}</b></td><td>&nbsp</td></tr>",
            "Итого", asi.visitCount, asi.orderCount,
            asi.summ.ToString("C", Config.GetCultureInfo()),
            getVisitPercent(asi.visitCount, asi.orderCount));
         return html;
      }

      public virtual string TableHeader(string html)
      {
         html += String.Format("<tr BGCOLOR=\"#CCCCCC\" ><td><FONT SIZE=\"2\"><b>{0}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{1}</b></td><td>" +
            "<FONT SIZE=\"2\"><b>{2}</b></td><td>" +
            "<FONT SIZE=\"2\"><b>{3}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{4}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{5}</b></td></tr>",
            "Подразделение / агент", "визиты", "заявки", "сумма", "процент заявок", "прогресс");
         return html;
      }

      protected double getVisitPercent(int visit, int order)
      {
         return visit == 0 ? 0 : (double)order * 100.0 / (double)visit;
      }

      struct OffTakeData : IComparable<OffTakeData>
      {
         public string org;
         public DateTime date;
         public string item;
         public double qty;
         public double diff;

         public OffTakeData(string org, DateTime date, string item, double qty, double diff)
         {
            this.org = org;
            this.date = date;
            this.item = item;
            this.qty = qty;
            this.diff = diff;
         }

         public int CompareTo(OffTakeData other)
         {
            int cmp = org.CompareTo(other.org);
            if (cmp != 0) return cmp;
            cmp = date.CompareTo(other.date);
            if (cmp != 0) return cmp;
            cmp = item.CompareTo(other.item);
            return cmp;
         }
      }

      public string MakeOffTakeReport(DataSet<int, Order> orders, TimeInterval interval, string AgentName)
      {
         StringBuilder html = new StringBuilder();

         html.Append("<html><head>");
         html.Append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head>");
         html.Append("<body><FONT FACE=\"Arial\">");
         html.AppendFormat("<H3>Отчёт агента  {0}</H3><p>", AgentName);
         html.AppendFormat("<FONT SIZE=\"2\"> Период: <b>{0} - {1}</b><br><br>",
            interval.begin.ToString("MM.dd.yyyy"), interval.end.ToString("MM.dd.yyyy"));
         html.AppendFormat("<table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR=\"#000000\">"+
            "<tr BGCOLOR=\"#CCCCCC\"><td><FONT SIZE=\"2\"><b>{0}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{1}</b></td><td><FONT SIZE=\"2\"><b>{2}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{3}</b></td><td><FONT SIZE=\"2\"><b>{4}</b></td></tr>",
            "контрагенты", "дата", "товар", "кол-во", "разница");

         List<OffTakeData> data = new List<OffTakeData>();
         foreach (Order o in orders.Data)
         {
            foreach (OrderItem item in o.items)
            {
               if( item.offTakeDiff != 0 )
                  data.Add(new OffTakeData(o.OrgName, o.Date, item.Item, item.qty, item.offTakeDiff));
            }
         }

         data.Sort();
         string sname = "", sdate = "";
         foreach(OffTakeData od in data)
         {
            string vname = od.org;
            string vdate = od.date.ToShortDateString();
            if( sname.CompareTo(vname) == 0 ) vname = "";
            else sname = vname;
            if( sdate.CompareTo(vdate) == 0 ) vdate = "";
            else sdate = vdate;
            html.AppendFormat("<tr><td><FONT SIZE=\"2\">{0}</td><td><FONT SIZE=\"2\">{1}</td><td><FONT SIZE=\"2\">{2}</td><td><FONT SIZE=\"2\">{3}</td><td><FONT SIZE=\"2\">{4}</td></tr>",
               vname, vdate, od.item, od.qty, od.diff);
         }

         html.Append("</table>");
         //html.AppendFormat("<FONT SIZE=\"3\"><br><b>Итого:</b> котрагентов: <b>{0}</b>," + 
         //   " документов: <b>{1}</b>, сумма: <b>{2}</b> <br><br>",
         //   orgs.Count, docCount, sum.ToString("C", Config.GetCultureInfo()));
         html.Append("<FONT SIZE=\"2\"> <SUB>Построен в системе \"Наполеон\" <a href=http://grsoft.ru/>http://grsoft.ru/</a></SUB>");
         html.Append("</body></html>");

         string fileName = String.Format("agent_offtake_report_{0}.html", ++doc_number);
         string result = System.IO.Path.GetTempPath() + fileName;
         StreamWriter sw = new StreamWriter(result);
         sw.Write(html.ToString());
         sw.Flush();
         return result;
      }

      public class DocTime
      {
         // минут до точки
         public double minTo = 0;
         // минут в точке
         public double minIn = 0;

         private static string Format(double value)
         {
            StringBuilder result = new StringBuilder();
            int roundedTime = Math.Abs((int)Math.Round(value));

            if (roundedTime == 0)
               return String.Empty;

            result.Append(StringUtil.IntToWithAddLeadingZero(roundedTime / 60)).
               Append(":").Append(StringUtil.IntToWithAddLeadingZero(roundedTime % 60));

            return result.ToString();
         }

         public string MinToStr { get { return Format(minTo); } }
         public string MinInStr { get { return Format(minIn); } }
      }

      class ODRCmp : IEqualityComparer<OrderDetailRepresentation>
      {
         public bool Equals(OrderDetailRepresentation x, OrderDetailRepresentation y)
         {
            return (x.DateCreatedDT.CompareTo(y.DateCreatedDT) == 0);
         }

         public int GetHashCode(OrderDetailRepresentation obj)
         {
            return obj.DateCreatedDT.GetHashCode();
         }
      }

      int OrderCreated(OrderDetailRepresentation x, OrderDetailRepresentation y) { return x.DateCreatedDT.CompareTo(y.DateCreatedDT); }
      int ToReportOrdering(OrderDetailRepresentation x, OrderDetailRepresentation y) 
      {
         if (x.DateCreatedDT == y.DateCreatedDT)
            return 0;

         else if (x.DateCreatedDT == DateTime.MinValue)
            return 1;

         else if (y.DateCreatedDT == DateTime.MinValue)
            return -1;

         return x.DateCreatedDT.CompareTo(y.DateCreatedDT); 
      }

      //private DocTime LoadDocTime(DataGridViewRowCollection rows, out Dictionary<OrderDetailRepresentation, DocTime> docs)
      //{
      //   List<OrderDetailRepresentation> odocs = new List<OrderDetailRepresentation>();
      //   foreach (DataGridViewRow row in rows)
      //   {
      //      OrderDetailRepresentation odr = row.DataBoundItem as OrderDetailRepresentation;
      //      if (odr == null) continue;

      //      ScriptDoc sd = odr.StoreObject as ScriptDoc;
      //      if(sd == null) continue;

      //      odocs.Add(odr);
      //   }

      //   return LoadDocTime(odocs, out docs);
      //}

      //private DocTime LoadDocTime(List<OrderDetailRepresentation> rows, out Dictionary<OrderDetailRepresentation, DocTime> docs)
      //{
      //   rows.Sort(OrderCreated);

      //   DocTime total = new DocTime();
      //   DateTime prevDoc = DateTime.MinValue;
      //   docs = new Dictionary<OrderDetailRepresentation, DocTime>(new ODRCmp());

      //   foreach (OrderDetailRepresentation odr in rows)
      //   {
      //      ScriptDoc sd = odr.StoreObject as ScriptDoc;

      //      if (sd == null)
      //         continue;

      //      if (prevDoc == DateTime.MinValue)
      //         prevDoc = sd.date;

      //      DocTime current = new DocTime();
      //      current.minTo = sd.date.Subtract(prevDoc).TotalMinutes;
      //      current.minIn = sd.dateEnd.Subtract(sd.date).TotalMinutes;

      //      total.minIn += current.minIn;
      //      total.minTo += current.minTo;

      //      docs[odr] = current;
      //      prevDoc = sd.dateEnd;
      //   }
      //   return total;
      //}

      public class RouteDetailItem : IComparable<RouteDetailItem>
      {
         public OrderDetailRepresentation data = null;
         public bool outRoute = false;

         #region IComparable<RouteDetailItem> Members

         public int CompareTo(RouteDetailItem other)
         {
            return data.DateCreatedDT.CompareTo(other.data.DateCreatedDT);
         }

         #endregion
      }

      public class Total
      {
         public int visit;
         public int routeVisit;
         public int outVisit;
         public int notVisit;
         public int docs;

         public double sum;
         public double qty;
         public double weigth;

         public Total() { }

         public virtual void Add(Total src)
         {
            visit += src.visit;
            routeVisit += src.routeVisit;
            outVisit += src.outVisit;
            notVisit += src.notVisit;

            sum += src.sum;
            qty += src.qty;
            weigth += src.weigth;
            docs += src.docs;
         }
      }

      public virtual Total CreateTotal()
      {
         return new Total();      
      }

      public class RouteDetailData : List<RouteDetailItem>
      {
         public List<Org> route;
         public int OrgCount { get { return 0; } }

         public void Add(OrderDetailRepresentation odr, List<Org> route)
         {
            RouteDetailItem item = CreateItem();
            InitItem(odr, route, item);
            Add(item);
         }

         public virtual void InitItem(OrderDetailRepresentation odr, List<Org> route, RouteDetailItem item)
         {
            item.data = odr;
            item.outRoute = (route == null) ? false : (route.Contains(odr.NOrg) == false);
         }

         public virtual RouteDetailItem CreateItem()
         {
            return new RouteDetailItem();
         }

         public void AddNotVisited(List<Org> route)
         {
            foreach (RouteDetailItem i in this)
               route.Remove(i.data.NOrg);

            foreach (Org o in route)
            {
               Add(new OrderDetailRepresentation(DateTime.MaxValue,
                  new ObjType(ObjType.TObjType.NotVisit), DateTime.MinValue, DateTime.MinValue, o, 0, 0, 0, o, true), null);
            }

            Sort();
         }

         public virtual Total PrintData(StringBuilder sb, Total ret)
         {
            List<Org> orgs = new List<Org>();
            foreach (RouteDetailItem item in this)
            {
               OrderDetailRepresentation data = item.data;

               ret.docs++;
               if (orgs.Contains(data.NOrg) == false)
               {
                  ret.visit++;

                  if (item.outRoute) 
                     ret.outVisit++;
                  else 
                     ret.routeVisit++;

                  orgs.Add(data.NOrg);
               }

               sb.Append("<tr>");
               BuildRow(sb, ret, item, data);
               DoPrintData(sb, item.data);

               /*Конец строки*/
               sb.Append("</tr>");
            }

            if (route != null)
               ret.notVisit = route.Count - ret.routeVisit;

            if (ret.notVisit < 0)
               ret.notVisit = 0;

            return ret;
         }

         //private String GetDocTypeCaption(ObjType objType, GRSoft.Network.DataObject data)
         //{
         //   string result = objType.ToString();

         //   if (objType.Val == ObjType.TObjType.Script)
         //   {
         //      DataSet<int, ScriptDef> df = DataModule.Get(ScriptDef.OBJECT_NAME) as DataSet<int, ScriptDef>;
         //      ScriptDoc sd = data as ScriptDoc;

         //      if (df != null && sd != null)
         //      {
         //         if (df.ContainsKey(sd.scriptId))
         //            result = df[sd.scriptId].Name;
         //      }
         //   }

         //   return result;
         //}

         public virtual void PrintAfterWeight(StringBuilder sb, Total ret, RouteDetailItem item, OrderDetailRepresentation data)
         {

         }

         public virtual void BuildRow(StringBuilder sb, Total ret, RouteDetailItem item, OrderDetailRepresentation data)
         {
            sb.AppendFormat("<td><FONT SIZE=\"2\">{0}</td>", data.Org);
            sb.AppendFormat("<td><FONT SIZE=\"2\">{0}</td>", data.GetDocTypeCaption());
            //sb.AppendFormat("<td><FONT SIZE=\"2\">{0}</td>", GetDocTypeCaption(data.Doctype, data.StoreObject));
            sb.AppendFormat("<td align=\"center\"><FONT SIZE=\"2\">{0}</td>", item.outRoute ? "нет" : "");

            string v;
            sb.AppendFormat("<td><FONT SIZE=\"2\">{0}</td>", data.DateExec.Length > 0 ? data.DateExec : "&nbsp;");

            v = (data.DateCreatedDT == DateTime.MinValue || data.DateCreatedDT == new DateTime(1601, 1, 1) || data.DateCreatedDT == DateTime.MaxValue) ?
               "&nbsp;" :
               data.DateCreatedDT.ToString("HH:mm");
            sb.AppendFormat("<td><FONT SIZE=\"2\">{0}</td>", v);

            v = (data.DateSendedDT == DateTime.MinValue || data.DateSendedDT == new DateTime(1601, 1, 1)) ?
               "&nbsp;" :
               data.DateSendedDT.ToString("dd.MM.yy HH:mm");
            sb.AppendFormat("<td><FONT SIZE=\"2\">{0}</td>", v);

            v = data.Sum;
            int cqty = data.Qty;

            ret.qty += cqty;
            ret.sum += data.DblSum;

            sb.AppendFormat("<td><FONT SIZE=\"2\">{0}</td>", (v.Length > 0) ? v : "&nbsp;");
            sb.AppendFormat("<td><FONT SIZE=\"2\">{0}</td>", (cqty > 0) ? cqty.ToString() : "&nbsp;");

            Order order = data.StoreObject as Order;
            /*Позиций*/
            sb.Append("<td><FONT SIZE=\"2\">" + ((order == null) ? "&nbsp;" : order.items.Count.ToString()) + "</td>");

            /*Вес*/
            double cw = 0;
            if (order != null)
               cw = order.Weight;
            ret.weigth += cw;

            sb.Append("<td><FONT SIZE=\"2\">" + ((cw == 0) ? "&nbsp;" : cw.ToString()) + "</td>");

            PrintAfterWeight(sb, ret, item, data);

            /*Комментарий*/
            string comment = string.Empty;
            GRSoft.Network.DataObject obj = data.StoreObject;

            if (obj != null)
            {
               FieldInfo fi = obj.GetType().GetField("remark");
               if (fi != null)
               {
                  object val = fi.GetValue(obj);

                  if (val != null && val is string)
                     comment = (string)val;
               }
            }

            sb.Append("<td><FONT SIZE=\"2\">" + ((comment.Length == 0) ? "&nbsp;" : comment) + "</td>");

            /*Адрес*/
            sb.Append("<td><FONT SIZE=\"2\">" + ToHtmlConv(data.OrgAddr) + "</td>");

#if Quad
               /*Просрочено*/
               v = "&nbsp;";

               if(order != null && order.debet > 0)
                  v = order.debet.ToString("C", Config.GetCultureInfo());


               sb.Append("<td><FONT SIZE=\"2\">" + v + "</td>");
#endif
         }

         protected virtual void DoPrintData(StringBuilder sb, OrderDetailRepresentation orderDetailRepresentation)
         {
         }
      }

      public class RouteDetailReport : Dictionary<DateTime, RouteDetailData>
      {
         protected DataGridView grid;

         public RouteDetailReport(DataGridView dgvDetail, Agent agent)
         {
            this.grid = dgvDetail;

            Dictionary<DateTime, List<Org>> routes = new Dictionary<DateTime, List<Org>>();
            Agent a = Agents.GetDataSet()[agent.id];

            CollectItems(dgvDetail, routes, a);

#if ADD_NOT_VISITED_IN_REPORT
            foreach(KeyValuePair<DateTime, List<Org>> kv in routes)
            {
               if (!ContainsKey(kv.Key))
                  this[kv.Key] = new RouteDetailData();
               this[kv.Key].AddNotVisited(kv.Value);
            }
#endif
         }

         public virtual void CollectItems(DataGridView dgvDetail, Dictionary<DateTime, List<Org>> routes, Agent a)
         {
            foreach (DataGridViewRow r in dgvDetail.Rows)
            {
               OrderDetailRepresentation odr = r.DataBoundItem as OrderDetailRepresentation;
               DateTime key = odr.DateCreatedDT.Date;
               if (odr.Doctype.Val == ObjType.TObjType.NotVisit)
                  key = odr.RouteOrder.date;

#if ADD_NOT_VISITED_IN_REPORT
               RouteDetailData rdd = null;
               List<Org> dayRoute = null;
               if (!ContainsKey(key))
               {
                  rdd = CreateRouteDetailData();
                  this[key] = rdd;

                  dayRoute = OrdersDetail.GetRoutePeriod(key, key.AddDays(1), a);
                  rdd.route = dayRoute;
                  routes[key] = dayRoute;
               }
               else
               {
                  rdd = this[key];
                  dayRoute = routes[key];
               }
               if (SkipItem(odr))
                  continue;

               // маршшрут берется на дату DateTime.MinDate здесь добавим контрагента в список чтобы был по маршруту
               if (odr.Doctype.Val == ObjType.TObjType.NotVisit)
                  dayRoute.Add(odr.NOrg);

               rdd.Add(odr, dayRoute);
#else
               if (SkipItem(odr))
                  continue;

               RouteDetailData rdd = null;
               List<Org> dayRoute = null;
               if (!ContainsKey(key))
               {
                  rdd = CreateRouteDetailData();
                  this[key] = rdd;

                  dayRoute = OrdersDetail.GetRoutePeriod(key, key.AddDays(1), a);
                  rdd.route = dayRoute;
                  routes[key] = dayRoute;
               }
               else
               {
                  rdd = this[key];
                  dayRoute = routes[key];
               }
               // маршшрут берется на дату DateTime.MinDate здесь добавим контрагента в список чтобы был по маршруту
               if (odr.Doctype.Val == ObjType.TObjType.NotVisit)
                  dayRoute.Add(odr.NOrg);

               rdd.Add(odr, dayRoute);
#endif

            }
         }

         protected virtual bool SkipItem(OrderDetailRepresentation odr)
         {
            return odr.Doctype.Val == ObjType.TObjType.NotVisit;
         }

         protected virtual  RouteDetailData CreateRouteDetailData()
         {
            return new RouteDetailData();
         }
      }

      public virtual string makeDetailsFileInfo(DataGridView dgvDetail, TimeInterval interval, Agent agent)
      {
         string fileName = String.Format("detail_file_info_{0}.html",++doc_number);
         string result = System.IO.Path.GetTempPath() + fileName;

         //int clmns = dgvDetail.Columns.Count;
         int rows = dgvDetail.Rows.Count;


         string html = "<html><head>"+
            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head>" +
            "<body><FONT FACE=\"Arial\">";
         html += String.Format("<H3>Отчёт агента  {0}</H3><p>", ToHtmlConv(agent.name));
         html += String.Format("<FONT SIZE=\"2\"> Период: <b>{0} - {1}</b><br></FONT>", 
            interval.begin.ToString("dd.MM.yyyy"), interval.end.ToString("dd.MM.yyyy"));


         //int docCount = 0;
         //double sum = 0.0;
         //List<string> orgs = new List<string>();

         DocTime totalTime = null;
         Dictionary<OrderDetailRepresentation, DocTime> docsTime = null;
         //if (routes != null)
         //   totalTime = LoadDocTime(dgvDetail.Rows, out docsTime);

         string[] head;
         head = headerTableDetail(docsTime);

         //html += "<tr BGCOLOR=\"#CCCCCC\">";
         //foreach (string ht in head)
         //   html += "<td><FONT SIZE=\"2\"><b>" + ht + "</b></td>";

         //html += "</tr>";

         RouteDetailReport report = CreateRouteDetailReport(dgvDetail, agent);

         CultureInfo culture = Config.GetCultureInfo();
         NumberFormatInfo nfi = culture.NumberFormat;
         nfi.NumberDecimalDigits = 2;

         StringBuilder sb = new StringBuilder();
         Total total = CreateTotal();

         foreach(KeyValuePair<DateTime, RouteDetailData> kv in report)
         {
            ItemHeader(sb, kv.Key);
            sb.Append("<br><table cellpadding='5' CELLSPACING='0' border='1' BORDERCOLOR='#000000' width='100%'><tr BGCOLOR='#CCCCCC'>");
            foreach (string ht in head)
               sb.Append("<td><FONT SIZE=\"2\"><b>" + ht + "</b></td>");
            sb.Append("</tr>");

            Total t = kv.Value.PrintData(sb, CreateTotal());
            total.Add(t);

            sb.AppendLine("</table>");
            DayResult(culture, sb, t);
         }

         AppendResultRow(report, culture, sb, total);

         html += sb.ToString();

         //int qty = 0;
         //double totalWeight = 0;
         //string content = string.Empty;
         //for (int r = 0; r < rows; r++)
         //{
         //   OrderDetailRepresentation data = (OrderDetailRepresentation)dgvDetail.Rows[r].DataBoundItem;
         //   content += "<tr>";
         //   if (!orgs.Contains(data.Org))
         //   {
         //      orgs.Add(data.Org);
         //   }

         //   sum += data.DblSum;

         //   docCount++;

         //   //ScriptDoc sd = data.StoreObject as ScriptDoc;

         //   content += String.Format("<td><FONT SIZE=\"2\">{0}</td>", data.Org);
         //   string docType = data.Doctype.ToString();

         //   content += String.Format("<td><FONT SIZE=\"2\">{0}</td>", docType);
         //   content += String.Format("<td align=\"center\"><FONT SIZE=\"2\">&nbsp;</td>");
         //   //content += String.Format("<td align=\"center\"><FONT SIZE=\"2\">{0}</td>",
         //   //   ToHtmlConv(GetScriptDocumentCode(sd)));

         //   string v;
         //   content += String.Format("<td><FONT SIZE=\"2\">{0}</td>", data.DateExec.Length > 0 ? data.DateExec : "&nbsp;");
            
         //   v = (data.DateCreatedDT == DateTime.MinValue || data.DateCreatedDT == new DateTime(1601, 1, 1)) ?
         //      "&nbsp;" :
         //      data.DateCreatedDT.ToString("HH:mm");
         //   content += String.Format("<td><FONT SIZE=\"2\">{0}</td>", v);
            
         //   v = (data.DateSendedDT  == DateTime.MinValue || data.DateSendedDT == new DateTime(1601, 1, 1)) ?
         //      "&nbsp;" : 
         //      data.DateSendedDT.ToString("dd.MM.yy HH:mm");
         //   content += String.Format("<td><FONT SIZE=\"2\">{0}</td>", v);

         //   v = data.Sum;
         //   int cqty = data.Qty;
         //   qty += cqty;
         //   content += String.Format("<td><FONT SIZE=\"2\">{0}</td>", (v.Length > 0) ? v : "&nbsp;");
         //   content += String.Format("<td><FONT SIZE=\"2\">{0}</td>", (cqty > 0) ? cqty.ToString() : "&nbsp;");

         //   Order order = data.StoreObject as Order;
         //   /*Позиций*/
         //   content += "<td><FONT SIZE=\"2\">" +
         //      ((order == null) ? "&nbsp;" : order.items.Count.ToString()) + "</td>";

         //   /*Вес*/
         //   double cw = 0;
         //   if( order != null )
         //      cw = order.Weight();
         //   totalWeight += cw;
         //   content += "<td><FONT SIZE=\"2\">" +
         //      ((cw == 0) ? "&nbsp;" : cw.ToString()) + "</td>";
            
         //   if (docsTime != null)
         //   {
         //      DocTime dt = null;
         //      if (docsTime.ContainsKey(data))
         //      {
         //         dt = docsTime[data];

         //         content += "<td><FONT SIZE=\"2\">" + ToHtmlConv(dt.MinToStr) + "</td>";
         //         content += "<td><FONT SIZE=\"2\">" + ToHtmlConv(dt.MinInStr) + "</td>";
         //      }
         //      else
         //         content += "<td>&nbsp;</td><td>&nbsp;</td>";
         //   }

         //   /*Комментарий*/
         //   string comment = string.Empty;
         //   GRSoft.Network.DataObject obj = data.StoreObject;

         //   if (obj != null)
         //   {
         //      FieldInfo fi = obj.GetType().GetField("remark");
         //      if (fi != null)
         //      {
         //         object val = fi.GetValue(obj);

         //         if (val != null && val is string)
         //            comment = (string)val;
         //      }
         //   }
            
         //   content += "<td><FONT SIZE=\"2\">" +
         //      ((comment.Length == 0) ? "&nbsp;" : comment) + "</td>";

         //   /*Адрес*/
         //   content += "<td><FONT SIZE=\"2\">" +
         //      ToHtmlConv(data.OrgAddr) + "</td>";

         //   /*Конец строки*/
         //   content += "</tr>";
         //}

         //html += content + "</table>";
         //html += String.Format("<FONT SIZE=\"3\"><br><b>Итого:</b> котрагентов: <b>{0}</b>," + 
         //   " документов: <b>{1}</b>, сумма: <b>{2}</b>, штук <b>{3}</b>, вес <b>{4}</b> кг",
         //   orgs.Count, docCount, ToHtmlConv(sum.ToString("C", culture)), qty, totalWeight);

         if (totalTime != null)
            html += ", время в пути <b>" + totalTime.MinToStr + "</b>, время в точке <b>" + 
               totalTime.MinInStr + "</b>.";

         html += "<br><br><FONT SIZE=\"2\"> <SUB>Построен в системе \"Наполеон\" <a href=http://grsoft.ru/>http://grsoft.ru/</a></SUB>";
         html += "</body></html>";

         StreamWriter sw = new StreamWriter(result);
         sw.Write(html);
         sw.Flush();
         return result;
      }

      protected virtual void AppendResultRow(RouteDetailReport report, CultureInfo culture, StringBuilder sb, Total total)
      {
         if (report.Count > 1)
            ReportResult(culture, sb, total);
      }

      public virtual void ItemHeader(StringBuilder sb, DateTime dt)
      {
         if(dt == DateTime.MinValue)
            sb.AppendFormat("<p>", dt);
         else
            sb.AppendFormat("<FONT SIZE='2'><p>Дата <b>{0:dd/MM (dddd)}</b></FONT>", dt);
      }

      public virtual void ReportResult(CultureInfo culture, StringBuilder sb, Total total)
      {
         sb.AppendFormat("<FONT SIZE=\"2\"><br><b>Всего:</b> посетил: <b>{0}</b>, по маршруту <b>{1}</b>, вне маршрута <b>{2}</b>, не посетил <b>{3}</b>" +
            " документов: <b>{4}</b>, сумма: <b>{5:0.00}</b>, штук <b>{6}</b>, вес <b>{7:0}</b> кг</FONT>",
            total.visit, total.routeVisit, total.outVisit, total.notVisit, total.docs, ToHtmlConv(total.sum.ToString("C", culture)), total.qty, total.weigth
            );
      }

      public virtual void DayResult(CultureInfo culture, StringBuilder sb, Total t)
      {
         sb.AppendFormat("<FONT SIZE=\"2\"><br><b>Итого по дню:</b> посетил: <b>{0}</b>, по маршруту <b>{1}</b>, вне маршрута <b>{2}</b>, не посетил <b>{3}</b>" +
            " документов: <b>{4}</b>, сумма: <b>{5:0.00}</b>, штук <b>{6}</b>, вес <b>{7:0.000}</b> кг</FONT>",
            t.visit, t.routeVisit, t.outVisit, t.notVisit, t.docs, ToHtmlConv(t.sum.ToString("C", culture)), t.qty, t.weigth
            );
      }

      protected virtual RouteDetailReport CreateRouteDetailReport(DataGridView dgvDetail, Agent agent)
      {
         return new RouteDetailReport(dgvDetail, agent);
      }

      protected virtual string[] headerTableDetail(Dictionary<OrderDetailRepresentation, DocTime> docsTime)
      {
         string[] head;
         if (docsTime != null)
            head = new string[] { "контрагенты", "тип посещения", "по маршруту", "дата", 
               "время создания", "дата передачи", "сумма", "штук", "позиций", "вес, кг", "время в пути", 
               "время в точке", "комментарий", "адрес" };
         else
            head = new string[] { "контрагенты", "тип посещения", "по маршруту", "дата", 
               "время создания", "дата передачи", "сумма", "штук", "позиций", "вес, кг", "комментарий", "адрес" 
#if Quad
               , "просрочено"
#endif
            };
         return head;
      }

      public string OrderVisitDivision(OrderVisitDivision ovdData)
      {
         string fileName = String.Format("order_visit_division_{0}.html", ++doc_number);
         string result = System.IO.Path.GetTempPath() + fileName;

         StringBuilder html = new StringBuilder("<html><head>" +
            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head>" +
            "<body><FONT FACE=\"Arial\">");
         html.Append("<H3>Отчёт подразделения:  \"").Append(ovdData.DivisionName).Append("\"</H3><p>");
         html.Append("<FONT SIZE=\"2\"> Период: <b>").Append(ovdData.DateBegin.ToString("dd.MM.yyyy")).
            Append(" - ").Append(ovdData.DateEnd.ToString("dd.MM.yyyy")).Append("</b><br><br>");

         int allOrgsCounter = 0;
         double allSum = 0.0;
         double allQty = 0;
         StringCounter allDocCounter = new StringCounter();
         CultureInfo culture = Config.GetCultureInfo();

         foreach (KeyValuePair<String, List<OrderDetailRepresentation>> kp in ovdData)
         {
            string agentName = Agents.GetDataSet()[kp.Key].Name;
            html.Append("<H4>").Append(agentName).Append("</H4>");
            html.Append("<table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR=\"#000000\" width=100%>");

            double sum = 0.0;
            List<string> orgs = new List<string>();

            DocTime totalTime = null;
            Dictionary<OrderDetailRepresentation, DocTime> docsTime = null;

            List<Org> routes = ovdData.GetRoute(kp.Key);

            //if (routes != null)
            //   totalTime = LoadDocTime(kp.Value, out docsTime);

            kp.Value.Sort(ToReportOrdering);
            string[] head;
            if (docsTime != null)
               head = new string[] { "контрагенты", "тип посещения", "документы", "дата", "дата создания", "дата передачи", "сумма", "штук", "время в пути", "время в точке" };
            else
               head = new string[] { "контрагенты", "тип посещения", "документы", "дата", "дата создания", "дата передачи", "сумма", "штук" };
            html.Append("<tr BGCOLOR=\"#CCCCCC\">");

            foreach (string ht in head)
               html.Append("<td><FONT SIZE=\"2\"><b>").Append(ht).Append("</b></td>");

            html.Append("</tr>");
            
            NumberFormatInfo nfi = culture.NumberFormat;
            nfi.NumberDecimalDigits = 2;
            int qty = 0;

            StringBuilder content = new StringBuilder();
            StringCounter docCounter = new StringCounter();
            
            foreach (OrderDetailRepresentation data in kp.Value)
            {
               content.Append("<tr>");
            
               if (!orgs.Contains(data.Org) && 
                  !data.Doctype.Equals(ObjType.TObjType.NotVisit))
               {
                  orgs.Add(data.Org);
               }

               sum += data.DblSum;

               //ScriptDoc sd = data.StoreObject as ScriptDoc;

               /* контрагенты */
               content.Append("<td><FONT SIZE=\"2\">").Append(ToHtmlConv(data.Org)).Append("</td>");

               string docType = data.Doctype.ToString();

               //if (routes != null && sd != null)
               //{
               //   if (sd.OrderOutOfPlan) docType = "Не посетил";
               //   else docType = (routes.Contains(data.NOrg)) ? "План" : "Вне маршрута";
               //}

               /*тип посещения*/
               content.Append("<td><FONT SIZE=\"2\">").Append(docType).Append("</td>");

               /*документы*/
               String docStringCode = "";
               //String docStringCode = GetScriptDocumentCode(sd);

               if (docStringCode.Length != 0 &&
                  !docStringCode.Equals("-"))
               {
                  docCounter.AppendString(docStringCode);
                  allDocCounter.AppendString(docStringCode);
               }

               content.Append("<td align=\"center\"><FONT SIZE=\"2\">").Append(
                  ToHtmlConv(docStringCode)).Append("</td>");

               /*"дата"*/
               content.Append("<td><FONT SIZE=\"2\">").Append(ToHtmlConv(data.DateExec)).Append("</td>");

               string v;
               
               v = (data.DateCreatedDT == DateTime.MinValue || data.DateCreatedDT == new DateTime(1601, 1, 1)) ?
                  "&nbsp;" :
                  data.DateCreatedDT.ToString("dd.MM.yy HH:mm");

               /* "дата создания" */
               content.Append("<td><FONT SIZE=\"2\">").Append(v).Append("</td>");

               v = (data.DateSendedDT == DateTime.MinValue || data.DateSendedDT == new DateTime(1601, 1, 1)) ?
                  "&nbsp;" :
                  data.DateSendedDT.ToString("dd.MM.yy HH:mm");

               /* "дата передачи" */
               content.Append("<td><FONT SIZE=\"2\">").Append(v).Append("</td>");

               v = data.Sum;
               int cqty = data.Qty;
               qty += cqty;

               /* "сумма" */
               content.Append("<td><FONT SIZE=\"2\">").Append(ToHtmlConv(v)).Append("</td>");

               /* "штук" */
               content.Append("<td><FONT SIZE=\"2\">").Append(
                  ToHtmlConv(cqty == 0 ? String.Empty : cqty.ToString())).Append("</td>");

               if (docsTime != null)
               {
                  DocTime dt = null;
                  if (docsTime.ContainsKey(data))
                  {
                     dt = docsTime[data];

                     /* "время в пути" */  
                     content.Append("<td><FONT SIZE=\"2\">").Append(
                        ToHtmlConv((dt.MinToStr))).Append("</td>");

                     /* "время в точке" */
                     content.Append("<td><FONT SIZE=\"2\">").Append(ToHtmlConv(dt.MinInStr)).Append("</td>");
                  }
                  else
                     content.Append("<td>&nbsp;</td><td>&nbsp;</td>");
               }
               content.Append("</tr>");
            }

            html.Append(content).Append("</table>");
            html.Append("<FONT SIZE=\"3\"><br><b>Итого ").Append(agentName).
               Append(":</b> котрагентов: <b>").Append(orgs.Count).Append("</b>,").
               Append(" документов: <b>").Append(docCounter.GetValues()).Append("</b>").
               Append(", сумма: <b>").Append(ToHtmlConv(sum.ToString("C", culture))).Append("</b>").
               Append(", штук <b>").Append(qty).Append("</b>");

            if (totalTime != null)
               html.Append(", время в пути <b>").
                  Append(ToHtmlConv(totalTime.MinToStr)).
                  Append("</b>, время в точке <b>").Append(ToHtmlConv(totalTime.MinInStr)).Append("</b>.");

            html.Append("<hr>");

            /*Считаем данные для итого по подразделению*/
            allOrgsCounter += orgs.Count;
            allSum += sum;
            allQty += qty;
         }

         html.Append("<FONT SIZE=\"3\"><br><b>Итого по подразделению: ").Append(ovdData.DivisionName).
               Append(":</b> котрагентов: <b>").Append(allOrgsCounter.ToString()).Append("</b>,").
               Append(" документов: <b>").Append(allDocCounter.GetValues()).Append("</b>").
               Append(", сумма: <b>").Append(ToHtmlConv(allSum.ToString("C", culture))).Append("</b>").
               Append(", штук <b>").Append(allQty).Append("</b>");

         html.Append("<br><br><FONT SIZE=\"2\"> <SUB>Построен в системе \"Наполеон\" <a href=http://grsoft.ru/>http://grsoft.ru/</a></SUB>");
         html.Append("</body></html>");

         StreamWriter sw = new StreamWriter(result);
         sw.Write(html.ToString());
         sw.Flush();
         return result;
      }

      //private string GetScriptDocumentCode(ScriptDoc doc)
      //{
      //   if (doc == null)
      //      return String.Empty;

      //   if (doc.IsScriptComplete)
      //      return "C";
      //   else if (doc.IsIncassOutOfPlan)
      //      return "И";
      //   else if (doc.IsOrderOutOfPlan)
      //      return "З";
      //   else return "-";
      //}

      public static string ToHtmlConv(string val)
      {
         if (val.Trim().Length == 0)
            return "&nbsp;";
         else
            return val;
      }

      public void OpenReportInAssociationBrowser()
      {
         OpenLink.NewWindow(String.Format("\"{0}\"", reportFileName));
      }

      protected void WriteToTmpFile(string report)
      {
         StreamWriter sw = new StreamWriter(reportFileName);
         sw.Write(report);
         sw.Flush();
      }

      public string GetFooter()
      {
         return "<SUB>Построен в системе \"Наполеон\" <a href=http://grsoft.ru/>http://grsoft.ru/</a></SUB>";
      }
   }

   internal class TimeInterval
   {
      public DateTime begin;
      public DateTime end;

      public TimeInterval(DateTime begin, DateTime end)
      {
         this.begin = begin;
         this.end = end;
      }
   }

   internal class HtmlCell
   {
      const string PREFIX = "\t<td>";
      const string POSTFIX = "</td>\n";

      private string val;

      public HtmlCell()
         : this(String.Empty)
      {
      }

      public HtmlCell(string val)
      {
         this.val = val;
      }

      public string Val
      {
         get
         {
            return String.Format("{0}{1}{2}", PREFIX, HtmlReport.ToHtmlConv(val), POSTFIX);
         }

         set
         {
            val = value;
         }
      }
   }

   internal class HTMLTableRow
   {
      List<string> cellData = new List<string>();
      public HTMLTableRow() { }

      public void AppendCell(string cellValue) { cellData.Add(cellValue); }
      public string Val
      {
         get
         {
            return "<tr>" + ToCells() + "</tr>\n";
         }
      }

      private string ToCells()
      {
         string res = "";
         cellData.ForEach(x => { res += "<td>" + x + " </td>"; });
         return res;
      }
   }

   internal class HtmlRow
   {
      private string PREFIX = "<tr>\n";
      private string POSTFIX = "</tr>\n";

      private HtmlCell[] cells;

      public HtmlRow(int cellCount)
      {
         cells = new HtmlCell[cellCount];

         InitCells(cellCount);
      }

      private void InitCells(int cellCount)
      {
         for (int i = 0; i < cellCount; i++)
            cells[i] = new HtmlCell();
      }

      public string this[int index]
      {
         get { return cells[index].Val; }
         set { cells[index].Val = value; }
      }

      public string Val
      {
         get
         {
            return String.Format("{0}{1}{2}", PREFIX, AllCellsValue(), POSTFIX);
         }
      }

      private string AllCellsValue()
      {
         StringBuilder result = new StringBuilder();

         foreach (HtmlCell cell in cells)
            result.Append(cell.Val);

         return result.ToString();
      }
   }

   public partial class AgentSummaryInfo
   {
      public int visitCount = 0;
      public int orderCount = 0;
      public int unicOrderCount = 0;
      public double summ = 0.0;
      public double weight = 0;
   }
}

class StringCounter : Dictionary<string, int>
{
   public string GetValues()
   {
      if (this.Count > 0)
      {
         StringBuilder sb = new StringBuilder();
         foreach (KeyValuePair<string, int> kp in this)
            sb.Append(kp.Key.ToString()).Append("-").
               Append(kp.Value.ToString()).Append(", ");

         sb.Remove(sb.Length - 2, 2);
         return sb.ToString();
      }
      else
         return "0";
   }

   public void AppendString(string val)
   {
      if (!ContainsKey(val))
         Add(val, 1);
      else
         this[val]++;
   }

   
}



using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Reflection;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class HtmlReportEx : HtmlReport
   {
      public static int prec = 300;
      public static FmVisitReportParams.SortMode sort;
      public static string RED_COLOR = "#ff7d7b";
      public static string YELLOW_COLOR = "#d7d787";
      public static string OUTROUTE_COLOR = "#8c00ff";
      public static string INROUTE_COLOR = "#000000";

      protected override HtmlReport.RouteDetailReport CreateRouteDetailReport(DataGridView dgvDetail, Agent agent)
      {
         return new RouteDetailReportEx(dgvDetail, agent);
      }

      public override void ItemHeader(StringBuilder sb, DateTime dt)
      {
         if (sort == FmVisitReportParams.SortMode.Created)
            base.ItemHeader(sb, dt);
      }

      public override void ReportResult(CultureInfo culture, StringBuilder sb, Total total)
      {
         sb.AppendFormat("<FONT SIZE=\"2\"><br><b>Всего:</b> " +
            "посетил клиентов: <b>{0}</b>, посетил клиентов по маршруту: <b>{4}</b>, не посетил клиентов: <b>{1}</b>, документов: <b>{2}</b>, сумма: <b>{3:0.00}</b>",
            total.visit, total.notVisit, total.docs, ToHtmlConv(total.sum.ToString("C", culture)), total.routeVisit);
         sb.Append("<br>");

         TotalEx te = (TotalEx)total;
         sb.Append("<br>");
         sb.AppendFormat("Всего заявок: {0}<br>", te.ordcnt);
         sb.AppendFormat("Всего посещений: {0}<br>", te.vztcnt);
         sb.Append("<br>");
         sb.AppendFormat("По маршруту <b>{0}</b>, из них:<br>", te.inroute);
         sb.AppendFormat("Заявок: {0}<br>", te.ordroutecnt);
         sb.AppendFormat("Посещений: {0}<br>", te.vztroutecnt);
         sb.AppendFormat("Посещений с НК <b>{0} ({1}%)</b><br>", te.inroutebadvisit, Math.Round(te.vztroutecnt != 0 ? (double)te.inroutebadvisit / te.vztroutecnt * 100 : 0));
         sb.AppendFormat("Заявок с НК <b>{0}</b> с зачетным посещением<br>", te.inrouteordergood);
         sb.AppendFormat("Заявок с НК <b>{0} ({1}%)</b> с незачетным посещением<br>", te.inrouteorderbad, Math.Round(te.ordroutecnt != 0 ? (double)te.inrouteorderbad / te.ordroutecnt * 100 : 0));
         sb.Append("<br>");
         sb.AppendFormat("Вне маршрута <b>{0}</b>, из них:<br>", te.outroute);
         sb.AppendFormat("Заявок: {0}<br>", te.ordoutroutecnt);
         sb.AppendFormat("Посещений: {0}<br>", te.vztoutroutecnt);
         sb.AppendFormat("Посещений с НК <b>{0}</b><br>", te.outroutebadvisit);
         sb.AppendFormat("Заявок с НК <b>{0}</b> с зачетным посещением<br>", te.outrouteordergood);
         sb.AppendFormat("Заявок с НК <b>{0} ({1}%)</b> с незачетным посещением<br>", te.outrouteorderbad, Math.Round(te.ordcnt != 0 ? (double)te.outrouteorderbad / te.ordcnt * 100 : 0));
         sb.Append("</FONT>");
      }

      public override void DayResult(CultureInfo culture, StringBuilder sb, Total t)
      {
         //sb.AppendFormat("<FONT SIZE=\"2\"><br><b>Итого по {4}:</b> " +
         //   "посетил: <b>{0}</b>, не посетил: <b>{1}</b>, документов: <b>{2}</b>, сумма: <b>{3:0.00}</b>",
         //   t.visit, t.visit - t.routeVisit, t.docs, ToHtmlConv(t.sum.ToString("C", culture)), 
         //   sort == FmVisitReportParams.SortMode.Created ? "дню" : "контрагенту", t.routeVisit);
         sb.AppendFormat("<FONT SIZE=\"2\"><br><b>Итого по {5}:</b> " +
            "посетил клиентов: <b>{0}</b>, посетил клиентов по маршруту: <b>{4}</b>, не посетил клиентов: <b>{1}</b>, документов: <b>{2}</b>, сумма: <b>{3:0.00}</b>",
            t.visit, t.notVisit, t.docs, ToHtmlConv(t.sum.ToString("C", culture)), t.routeVisit, sort == FmVisitReportParams.SortMode.Created ? "дню" : "контрагенту");

         sb.Append("<br>");
         TotalEx te = (TotalEx)t;
         sb.AppendFormat("Всего заявок: {0}<br>", te.ordcnt);
         sb.AppendFormat("Всего посещений: {0}<br>", te.vztcnt);
         sb.Append("<br>");
         sb.AppendFormat("По маршруту <b>{0}</b>, из них:<br>", te.inroute);
         sb.AppendFormat("Заявок: {0}<br>", te.ordroutecnt);
         sb.AppendFormat("Посещений: {0}<br>", te.vztroutecnt);
         sb.AppendFormat("Посещений с НК <b>{0}</b><br>", te.inroutebadvisit);
         sb.AppendFormat("Заявок с НК <b>{0}</b> с зачетным посещением<br>", te.inrouteordergood);
         sb.AppendFormat("Заявок с НК <b>{0}</b> с незачетным посещением<br>", te.inrouteorderbad);
         sb.Append("<br>");
         sb.AppendFormat("Вне маршрута <b>{0}</b>, из них:<br>", te.outroute);
         sb.AppendFormat("Заявок: {0}<br>", te.ordoutroutecnt);
         sb.AppendFormat("Посещений: {0}<br>", te.vztoutroutecnt);
         sb.AppendFormat("Посещений с НК <b>{0}</b><br>", te.outroutebadvisit);
         sb.AppendFormat("Заявок с НК <b>{0}</b> с зачетным посещением<br>", te.outrouteordergood);
         sb.AppendFormat("Заявок с НК <b>{0}</b> с незачетным посещением<br>", te.outrouteorderbad);
         sb.Append("</FONT>");
      }

      protected override void AppendResultRow(RouteDetailReport report, CultureInfo culture, StringBuilder sb, Total total)
      {
         ReportResult(culture, sb, total);
      }

      class RouteDetailReportEx : RouteDetailReport
      {
         List<string> goodvisit = new List<string>();

         public RouteDetailReportEx(DataGridView dgvDetail, Agent agent)
            : base(dgvDetail, agent)
         {

            DataSet<DateTime, GPSPos> dsGPSPos = (DataSet<DateTime, GPSPos>)DataModule.Get(GPSPos.OBJECT_NAME); 

            if(dsGPSPos != null)
            {
            /// 1 раз собираем посещения
            foreach (RouteDetailData item in this.Values)
               foreach(RouteDetailItem data in item)
               {
                  OrderDetailRepresentation odr = data.data;

                  if(odr != null)
                     if (odr.NOrg != null && odr.StoreObject is VisitInfo)
                     {
                        BaseDocument doc = (BaseDocument)odr.StoreObject;
                        if (doc != null && (doc.latitude != 0 || doc.longitude != 0))
                        {
                           Location l = Route.GetLocation(odr.NOrg);
                           NapoleonManager.Location check = new Location(doc.latitude, doc.longitude);
                           if (l != null && !Route.IsNearestToOrg(odr.NOrg, ref check, doc.created, Convert.ToDouble(HtmlReportEx.prec), dsGPSPos))
                              ((RouteDetailItemEx)data).badvisit = true;
                           else
                              goodvisit.Add(doc.created.ToString("dd/MM/yyyy") + odr.NOrg.id);

                           data.outRoute = !FmDetailBase.IsCreatedBySelectedAgentRoute(odr.NOrg, doc.userid, doc.created);
                        }
                     }
               }

            //2 раз заявки
            foreach (RouteDetailData item in this.Values)
               foreach (RouteDetailItem data in item)
               {
                  OrderDetailRepresentation odr = data.data;

                  if (odr != null)
                     if (odr.NOrg != null && odr.StoreObject is Order)
                     {
                        BaseDocument doc = (BaseDocument)odr.StoreObject;
                        if (doc != null && (doc.latitude != 0 || doc.longitude != 0))
                        {
                           Location l = Route.GetLocation(odr.NOrg);
                           NapoleonManager.Location check = new Location(doc.latitude, doc.longitude);
                           if (l != null && !Route.IsNearestToOrg(odr.NOrg, ref check, doc.created, Convert.ToDouble(HtmlReportEx.prec), dsGPSPos))
                           {
                              if (goodvisit.Contains(doc.created.ToString("dd/MM/yyyy") + odr.NOrg.id))
                                 ((RouteDetailItemEx)data).orderyellow = true;
                              else
                                 ((RouteDetailItemEx)data).orderred = true;

                              data.outRoute = !FmDetailBase.IsCreatedBySelectedAgentRoute(odr.NOrg, doc.userid, doc.created);
                           }
                        }
                     }
               }
            }
         }

         protected override bool SkipItem(OrderDetailRepresentation odr)
         {
            bool result = base.SkipItem(odr);

            if (!result)
               result = !(odr.StoreObject is Order || odr.StoreObject is VisitInfo);

            return result;
         }

         protected override RouteDetailData CreateRouteDetailData()
         {
            return new RouteDetailDataEx();
         }

         public override void CollectItems(DataGridView dgvDetail, Dictionary<DateTime, List<Org>> routes, Agent a)
         {
            if (HtmlReportEx.sort == FmVisitReportParams.SortMode.Org)
            {
               List<OrderDetailRepresentation> list = new List<OrderDetailRepresentation>();
               DateTime now = DateTime.Now;

               foreach (DataGridViewRow r in dgvDetail.Rows)
               {
                  OrderDetailRepresentation odr = r.DataBoundItem as OrderDetailRepresentation;
                  if (!SkipItem(odr))
                     list.Add(odr);
               }

               list.Sort((lhs, rhs) => { return compareODR(lhs, rhs); });

               string orgName = string.Empty;
               foreach (OrderDetailRepresentation rep in list) 
               {
                  if (orgName != rep.Org)
                  {
                     now = now.AddSeconds(1);
                     orgName = rep.Org;
                  }

                  RouteDetailData rdd = null;
                  if (!ContainsKey(now))
                  {
                     rdd = CreateRouteDetailData();
                     this[now] = rdd;
                  }
                  else
                  {
                     rdd = this[now];
                  }

                   rdd.Add(rep, null);
               }
            }
            else
               base.CollectItems(dgvDetail, routes, a);

         }

         private int compareODR(OrderDetailRepresentation lhs, OrderDetailRepresentation rhs)
         {
            int result = 0;

            result = lhs.Org.CompareTo(rhs.Org);

            if (result == 0)
               result = lhs.DateCreatedDT.CompareTo(rhs.DateCreatedDT);

            return result;
         }
      }

      class RouteDetailItemEx : RouteDetailItem
      {
         public bool badvisit = false;
         public bool orderyellow = false;
         public bool orderred = false;
      }

      public class TotalEx : Total
      {
         public int inroute = 0;
         public int inroutebadvisit = 0;
         public int inrouteordergood = 0;
         public int inrouteorderbad = 0;

         public int outroute = 0;
         public int outroutebadvisit = 0;
         public int outrouteordergood = 0;
         public int outrouteorderbad = 0;

         public int vztcnt = 0;
         public int ordcnt = 0;
         public int vztroutecnt = 0;
         public int ordroutecnt = 0;
         public int vztoutroutecnt;
         public int ordoutroutecnt;

         public override void Add(Total src)
         {
            base.Add(src);
            TotalEx se = (TotalEx)src;
            inroute += se.inroute;
            inroutebadvisit += se.inroutebadvisit;
            inrouteorderbad += se.inrouteorderbad;
            inrouteordergood += se.inrouteordergood;

            outroute += se.outroute;
            outroutebadvisit += se.outroutebadvisit;
            outrouteorderbad += se.outrouteorderbad;
            outrouteordergood += se.outrouteordergood;

            vztcnt += se.vztcnt;
            ordcnt += se.ordcnt;
            docs += se.docs;
            vztroutecnt += se.vztroutecnt;
            ordroutecnt += se.ordroutecnt;
            vztoutroutecnt += se.vztoutroutecnt;
            ordoutroutecnt += se.ordoutroutecnt;
         }
      }

      public override HtmlReport.Total CreateTotal()
      {
         return new TotalEx();
      }

      class RouteDetailDataEx : RouteDetailData
      {
         public override RouteDetailItem CreateItem()
         {
            return new RouteDetailItemEx();
         }

         public override void InitItem(OrderDetailRepresentation odr, List<Org> route, RouteDetailItem item)
         {
            base.InitItem(odr, route, item);
         }

         public override void BuildRow(StringBuilder sb, GRSoft.NapoleonManager.HtmlReport.Total ret, RouteDetailItem item, OrderDetailRepresentation data)
         {
            RouteDetailItemEx i = (RouteDetailItemEx)item;
            string bkg = i.orderred || i.badvisit ? RED_COLOR : i.orderyellow ? YELLOW_COLOR : "white";
            string fcl = i.outRoute? OUTROUTE_COLOR : INROUTE_COLOR;

            TotalEx te = (TotalEx)ret;
            if (!i.outRoute)
            {
               te.inroute++;
               if (i.badvisit)
                  te.inroutebadvisit++;
               if (i.orderyellow)
                  te.inrouteordergood++;
               if (i.orderred)
                  te.inrouteorderbad++;

               if (data.StoreObject is Visit)
                  te.vztroutecnt++;
               else if (data.StoreObject is Order)
                  te.ordroutecnt++;
            }
            else
            {
               te.outroute++;
               if (i.badvisit)
                  te.outroutebadvisit++;
               if (i.orderyellow)
                  te.outrouteordergood++;
               if (i.orderred)
                  te.outrouteorderbad++;

               if (data.StoreObject is Visit)
                  te.vztoutroutecnt++;
               else if (data.StoreObject is Order)
                  te.ordoutroutecnt++;
            }

            if (data.StoreObject is Visit)
               te.vztcnt++;
            else if (data.StoreObject is Order)
               te.ordcnt++;

            sb.AppendFormat("<td bgcolor='{1}'><FONT SIZE=\"2\" color='{2}'>{0}</td>", data.Org, bkg, fcl);
            sb.AppendFormat("<td bgcolor='{1}'><FONT SIZE=\"2\" color='{2}'>{0}</td>", data.Doctype.ToString(), bkg, fcl);
            sb.AppendFormat("<td bgcolor='{1}' align=\"center\"><FONT SIZE=\"2\" color='{2}'>{0}</td>", item.outRoute ? "нет" : "", bkg, fcl);

            string v;
            v = (data.DateCreatedDT == DateTime.MinValue || data.DateCreatedDT == new DateTime(1601, 1, 1) || data.DateCreatedDT == DateTime.MaxValue) ?
               "&nbsp;" :
               data.DateCreatedDT.ToString("dd.MM.yy");

            sb.AppendFormat("<td bgcolor='{1}'><FONT SIZE=\"2\" color='{2}'>{0}</td>", v, bkg, fcl);

            v = (data.DateCreatedDT == DateTime.MinValue || data.DateCreatedDT == new DateTime(1601, 1, 1) || data.DateCreatedDT == DateTime.MaxValue) ?
               "&nbsp;" :
               data.DateCreatedDT.ToString("HH:mm");
            sb.AppendFormat("<td bgcolor='{1}'><FONT SIZE=\"2\" color='{2}'>{0}</td>", v, bkg, fcl);

            v = (data.DateSendedDT == DateTime.MinValue || data.DateSendedDT == new DateTime(1601, 1, 1)) ?
               "&nbsp;" :
               data.DateSendedDT.ToString("dd.MM.yy HH:mm");
            sb.AppendFormat("<td bgcolor='{1}'><FONT SIZE=\"2\" color='{2}'>{0}</td>", v, bkg, fcl);

            v = data.Sum;
            int cqty = data.Qty;

            ret.qty += cqty;
            ret.sum += data.DblSum;

            sb.AppendFormat("<td bgcolor='{1}'><FONT SIZE=\"2\" color='{2}'>{0}</td>", (v.Length > 0) ? v : "&nbsp;", bkg, fcl);
            sb.AppendFormat("<td bgcolor='{1}'><FONT SIZE=\"2\" color='{2}'>{0}</td>", (cqty > 0) ? cqty.ToString() : "&nbsp;", bkg, fcl);

            Order order = data.StoreObject as Order;
            /*Позиций*/
            sb.AppendFormat("<td bgcolor='{1}'><FONT SIZE=\"2\" color='{2}'>{0}</td>", ((order == null) ? "&nbsp;" : order.items.Count.ToString()), bkg, fcl);

            /*Вес*/
            double cw = 0;
            if (order != null)
               cw = order.Weight;
            ret.weigth += cw;

            sb.AppendFormat("<td bgcolor='{1}'><FONT SIZE=\"2\" color='{2}'>{0}</td>", (cw == 0) ? "&nbsp;" : cw.ToString(), bkg, fcl);

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

            sb.AppendFormat("<td bgcolor='{1}'><FONT SIZE=\"2\" color='{2}'>{0}</td>", ((comment.Length == 0) ? "&nbsp;" : comment), bkg, fcl);

            /*Адрес*/
            sb.AppendFormat("<td bgcolor='{1}'><FONT SIZE=\"2\" color='{2}'>{0}</td>", ToHtmlConv(data.OrgAddr), bkg, fcl);

#if Quad
               /*Просрочено*/
               v = "&nbsp;";

               if(order != null && order.debet > 0)
                  v = order.debet.ToString("C", Config.GetCultureInfo());


               sb.Append("<td><FONT SIZE=\"2\">" + v + "</td>");
#endif
         }
      }
   }

   
}

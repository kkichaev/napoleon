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
      protected override string[] headerTableDetail(Dictionary<OrderDetailRepresentation, DocTime> docsTime)
      {
         string[] head;
         if (docsTime != null)
            head = new string[] { "контрагенты", "тип посещения", "по маршруту", "дата", 
               "время создания", "дата передачи", "сумма", "время в пути", 
               "время в точке", "адрес", "документ основание"};
         else
            head = new string[] { "контрагенты", "тип посещения", "по маршруту", "дата", 
               "время создания", "дата передачи", "сумма", "адрес", "документ основание"
            };
         return head;
      }

      protected override RouteDetailReport CreateRouteDetailReport(DataGridView dgvDetail, Agent agent)
      {
         return new RouteDetailReportEx(dgvDetail, agent);
      }

      public override void ReportResult(CultureInfo culture, StringBuilder sb, Total total)
      {
         sb.AppendFormat("<FONT SIZE=\"2\"><br><b>Всего:</b> посетил: <b>{0}</b>, по маршруту <b>{1}</b>, вне маршрута <b>{2}</b>, не посетил <b>{3}</b>" +
            " документов: <b>{4}</b>, сумма: <b>{5:0.00}</b></FONT>",
            total.visit, total.routeVisit, total.outVisit, total.notVisit, total.docs, ToHtmlConv(total.sum.ToString("C", culture))
            );
      }

      public override void DayResult(CultureInfo culture, StringBuilder sb, Total t)
      {
         sb.AppendFormat("<FONT SIZE=\"2\"><br><b>Итого по дню:</b> посетил: <b>{0}</b>, по маршруту <b>{1}</b>, вне маршрута <b>{2}</b>, не посетил <b>{3}</b>" +
            " документов: <b>{4}</b>, сумма: <b>{5:0.00}</b></FONT>",
            t.visit, t.routeVisit, t.outVisit, t.notVisit, t.docs, ToHtmlConv(t.sum.ToString("C", culture))
            );
      }

      class RouteDetailReportEx : RouteDetailReport
      { 
         public RouteDetailReportEx(DataGridView dgvDetail, Agent agent) : base (dgvDetail, agent)
         {
         }

         protected override RouteDetailData CreateRouteDetailData()
         {
            return new RouteDetailDataEx();
         }
      }

      class RouteDetailDataEx : RouteDetailData
      {
         public override void BuildRow(StringBuilder sb, Total ret, RouteDetailItem item, OrderDetailRepresentation data)
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
            //sb.AppendFormat("<td><FONT SIZE=\"2\">{0}</td>", (cqty > 0) ? cqty.ToString() : "&nbsp;");

            Order order = data.StoreObject as Order;
            /*Позиций*/
            //sb.Append("<td><FONT SIZE=\"2\">" + ((order == null) ? "&nbsp;" : order.items.Count.ToString()) + "</td>");

            /*Вес*/
            double cw = 0;
            if (order != null)
               cw = order.Weight;
            ret.weigth += cw;

            //sb.Append("<td><FONT SIZE=\"2\">" + ((cw == 0) ? "&nbsp;" : cw.ToString()) + "</td>");

            PrintAfterWeight(sb, ret, item, data);

            /*Адрес*/
            sb.Append("<td><FONT SIZE=\"2\">" + ToHtmlConv(data.OrgAddr) + "</td>");

            string value = "&nbsp;";

            Incass incass = data.StoreObject as Incass;
            if (incass != null && incass.items.Count > 0)
            {
               StringBuilder s = new StringBuilder();

               foreach(IncassItem ii in incass.items)
               {
                  if (s.Length > 0)
                     s.Append(", ");

                  s.Append(ii.number);
               }

               value = s.ToString();
            }

            sb.AppendFormat("<td><FONT SIZE=\"2\">{0}</td>",value);

            /*Комментарий*/
            //string comment = string.Empty;
            //GRSoft.Network.DataObject obj = data.StoreObject;

            //if (obj != null)
            //{
            //   FieldInfo fi = obj.GetType().GetField("remark");
            //   if (fi != null)
            //   {
            //      object val = fi.GetValue(obj);

            //      if (val != null && val is string)
            //         comment = (string)val;
            //   }
            //}

            //sb.Append("<td><FONT SIZE=\"2\">" + ((comment.Length == 0) ? "&nbsp;" : comment) + "</td>");
         }
      }
   }
}

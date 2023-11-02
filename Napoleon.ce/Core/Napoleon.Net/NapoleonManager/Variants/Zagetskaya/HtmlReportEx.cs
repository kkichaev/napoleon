using System;
using System.Collections.Generic;
using System.Reflection;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class HtmlReportEx : HtmlReport
   {
      protected override string[] headerTableDetail(Dictionary<OrderDetailRepresentation, HtmlReport.DocTime> docsTime)
      {
         string[] head;
         if (docsTime != null)
            head = new string[] { "контрагенты", "тип посещения", "по маршруту", "дата", 
               "время создания", "дата передачи", "сумма", "номер накладной", "штук", "позиций", "вес, кг", "время в пути", 
               "время в точке", "комментарий", "адрес" };
         else
            head = new string[] { "контрагенты", "тип посещения", "по маршруту", "дата", 
               "время создания", "дата передачи", "сумма", "номер накладной", "штук", "позиций", "вес, кг", "комментарий", "адрес" 
            };
         return head;
      }

      protected override HtmlReport.RouteDetailReport CreateRouteDetailReport(DataGridView dgvDetail, Agent agent)
      {
         return new RouteDetailReportEx(dgvDetail, agent);
      }

      class RouteDetailReportEx : RouteDetailReport
      {
         public RouteDetailReportEx(DataGridView dgvDetail, Agent agent)
            :base(dgvDetail, agent)
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

            StringBuilder dlvs = new StringBuilder();
            if (data.StoreObject is Incass)
            {
               Incass i = (Incass)data.StoreObject;

               foreach(Incass.IncassPayItem ii in i.items){
                  if (dlvs.ToString().Trim().Length > 0)
                     dlvs.Append(",&nbsp;");

                  if (ii.number.Trim().Length > 0)
                     dlvs.Append(ii.number.Trim());
               }
            }

            string sdlvs = dlvs.ToString();
            sb.AppendFormat("<td><FONT SIZE=\"2\">{0}</td>", (sdlvs.Length > 0) ? sdlvs : "&nbsp;");

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
         }
      }
   }

   
}

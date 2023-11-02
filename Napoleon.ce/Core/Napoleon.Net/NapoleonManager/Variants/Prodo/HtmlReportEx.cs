using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class HtmlReportEx : HtmlReport
   {
      public Dictionary<DateTime, Dictionary<string, double>> planPerVisit;

      public override string TableHeader(string html)
      {
         html += String.Format("<tr BGCOLOR=\"#CCCCCC\" >" +
            "<td><FONT SIZE=\"2\"><b>{0}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{1}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{2}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{3}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{4}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{5}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{6}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{7}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{8}</b></td>" +
            "</tr>",
            "Подразделение / агент", "визиты", "заявки", "сумма", "процент заявок", "прогресс", "план", "факт", "% план");
         return html;
      }

      protected override bool CanPrint(int i)
      {
         return base.CanPrint(i) && i != 13 && i != 11 && i != 12;
      }

      protected override string PostUpdateForNode(UILib.TreeGridNode node)
      {
         StringBuilder result = new StringBuilder();
         result.Append(String.Format("<td><FONT SIZE=\"2\">{0:0}</td>", node.Cells[11].Value));
         result.Append(String.Format("<td><FONT SIZE=\"2\">{0:0}</td>", node.Cells[12].Value));
         Bitmap bmp = node.Cells[13].Value as Bitmap;
         double progress = (bmp == null) ? 0 : (double)(bmp.Tag);
         result.Append(String.Format("<td><FONT SIZE=\"2\">{0:0}</td>", progress));

         return result.ToString();
      }

      protected override string TableFooter(string html, AgentSummaryInfo asi)
      {
         html += String.Format("<tr><td><FONT SIZE=\"2\"><b>{0}</b></td><td><FONT SIZE=\"2\"><b>{1}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{2}</b></td><td><FONT SIZE=\"2\"><b>{3}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{4:0}</b></td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td></tr>",
            "Итого", asi.visitCount, asi.orderCount,
            asi.summ.ToString("C", Config.GetCultureInfo()),
            getVisitPercent(asi.visitCount, asi.unicOrderCount));
         return html;
      }

      protected override string[] headerTableDetail(Dictionary<OrderDetailRepresentation, DocTime> docsTime)
      {
         string[] head = new string[] { "контрагенты", "тип посещения", "по маршруту", "дата", 
               "время создания", "дата передачи", "сумма", "штук", "позиций", "вес, кг", "комментарий", "адрес", "план, кг", 
               "прогресс, %"
            };
         return head;
      }

      protected override HtmlReport.RouteDetailReport CreateRouteDetailReport(System.Windows.Forms.DataGridView dgvDetail, Agent agent)
      {
         dgvDetail.Tag = planPerVisit;
         RouteDetailReportEx result = new RouteDetailReportEx(dgvDetail, agent);
         return result;
      }
   }

   class RouteDetailReportEx : HtmlReport.RouteDetailReport
   {
      public RouteDetailReportEx(DataGridView dgvDetail, Agent agent)
         :base(dgvDetail, agent)
      {
      }

      protected override HtmlReport.RouteDetailData CreateRouteDetailData()
      {
         RouteDetailDataEx result = new RouteDetailDataEx();
         result.planPerVisit = grid.Tag as Dictionary<DateTime, Dictionary<string, double>>;
         return result;
      }
   }

   class RouteDetailDataEx : HtmlReport.RouteDetailData
   {
      public Dictionary<DateTime, Dictionary<string, double>> planPerVisit;

      protected override void DoPrintData(StringBuilder sb, OrderDetailRepresentation orderDetailRepresentation)
      {
         Network.DataObject data = orderDetailRepresentation.StoreObject;
         bool setEmptyCells = true;

         if (data is Order)
         {
            Order order = (Order)data;
            DateTime created = new DateTime(order.created.Year, order.created.Month, 1);

            if (planPerVisit != null && planPerVisit.ContainsKey(created) && planPerVisit[created].ContainsKey(order.id)) 
            {
               double plan = planPerVisit[created][order.id];

               if (plan > 0)
               {
                  double fact = order.Weight;
                  double percent = fact / plan * 100;

                  sb.Append("<td><FONT SIZE=\"2\">" + plan+ "</td>");
                  sb.Append("<td><FONT SIZE=\"2\">" + Math.Round(percent) + "</td>");
                  setEmptyCells = false;
               }
            }
         }
         
         if(setEmptyCells)
         {
            sb.Append("<td><FONT SIZE=\"2\">&nbsp;</td>");
            sb.Append("<td><FONT SIZE=\"2\">&nbsp;</td>");
         }
      }
   }
}

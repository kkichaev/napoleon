using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class HTMLOrderReportBuilder : HtmlReport
   {
#if Servolux
      static readonly string ITEMS_HEAD = "Базовая номенклатура";
      static readonly string ITEM_QTY = "кг";
      static readonly string PACK_QTY = "ящ.";
#else
      static readonly string ITEMS_HEAD = "Номенклатура";
      static readonly string ITEM_QTY = "шт.";
      static readonly string PACK_QTY = "уп.";
#endif
      
      public HTMLOrderReportBuilder() : 
         base("order_{0}.html")
      {
      }

      public string Build(OrdersReport.ReportData data)
      {
         StringBuilder html = new StringBuilder("<html><head>" +
            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head>" +
            "<body><FONT FACE=\"Arial\">");

         if (data.agent != null)
            html.Append("Агент: ").Append(data.agent.Name);
         else if (data.division != null)
            html.Append("Подразделение: ").Append(data.division.ToString());
         html.Append("<br>");
         html.Append(data.Filter).Append("<br>");
         html.Append("<table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR=\"#000000\">\n");

         string cellspan = "";
         int cPan = 0;
         if (data.drawPack)
            cPan++;
         if (data.drawQty)
            cPan++;
         if( cPan > 0 )
            cellspan = String.Format("colspan={0}", cPan+1);

         html.Append("<tr>\n\t<td rowspan=2>Артикул</td>");
         html.Append("<td rowspan=2>").Append(ITEMS_HEAD).Append("</td>");
#if Servolux
         html.Append("<td rowspan=2>").Append("Вид упаковки rowspan=2").Append("</td>").Append("<td rowspan=2>").Append("Терм.состояние").Append("</td>");
#endif
         List<Org> orgs = new List<Org>(data.data.Keys);
         orgs.Sort();

         if (data.totalOnly == false)
         {
            foreach (Org org in orgs)
            {
               html.Append("\t<td ").Append(cellspan).Append(">").Append(ToHtmlConv(org.Name)).Append("</td>\n");
            }
         }
         html.Append("\t<td ").Append(cellspan).Append(">итого</td>\n");

#if COVER_IN_ORDER_REPORT
         html.Append("\t<td rowspan=2>").Append("Покрытие").Append("</td>\n");
         html.Append("\t<td rowspan=2>").Append("На складе").Append("</td>\n");
         html.Append("\t<td rowspan=2>").Append("Факт РТТ").Append("</td>\n");
         html.Append("\t<td rowspan=2>").Append("Целевое кол-во РТТ").Append("</td>\n");
#endif

         html.Append("</tr>\n");

         if (data.totalOnly == false)
         {
            foreach (Org org in orgs)
            {
               if (data.drawPack)
                  html.Append("\t<td>").Append(ITEM_QTY).Append("</td>\n");
               if (data.drawQty)
                  html.Append("\t<td>").Append(PACK_QTY).Append("</td>\n");
               html.Append("\t<td>сумма</td>\n");
            }
         }
         if (data.drawPack)
            html.Append("\t<td>").Append(ITEM_QTY).Append("</td>\n");
         if (data.drawQty)
            html.Append("\t<td>").Append(PACK_QTY).Append("</td>\n");
         html.Append("\t<td>сумма</td>\n");

         html.Append("</tr>\n");

         int value_cells_count = orgs.Count + 1;
         if (data.drawPack)
            value_cells_count += orgs.Count + 1;
         if (data.drawQty)
            value_cells_count += orgs.Count + 1;
         value_cells_count++; //Артикул товара, колонка 0
         value_cells_count++; //На имя товара, колонка 1

#if COVER_IN_ORDER_REPORT
         value_cells_count += 4;
#endif

#if Servolux
         value_cells_count+=2; //терм сост+вид упак
#endif

         foreach (ManagerFolder mf in data.folders)
         {
            /* Жирным шрифтом пишется заголовок группы товаров*/
            html.Append("<tr>\n");
            html.Append("\t<td colspan=").Append((value_cells_count)).
               Append("><b>").Append(mf.name).Append("</b></td>\n");
            html.Append("</tr>\n");

            List<Price> price = data.GetPriceList(mf);
            foreach (Price p in price)
            {
               double totQty = 0, totPack = 0, totSum = 0;

               HTMLTableRow htmlRow = new HTMLTableRow();

               htmlRow.AppendCell(p.id);
#if Servolux
               Price item = rrow.Key;
               htmlRow.AppendCell(item.name);
               htmlRow.AppendCell(item.packName == null ? "" : item.packName);
               htmlRow.AppendCell(item.thermalState == null ? "" : item.thermalState);
#else
               htmlRow.AppendCell(p.name);
#endif
               List<Org> cOrgs = new List<Org>();
               foreach (Org o in orgs)
               {
                  OrdersReport.ReportData.Item value = data.GetValue(o, p);
                  double pack = value.qty;
                  totQty += value.qty;
                  if (p.inPack != 0)
                     pack /= p.inPack;
                  totPack += pack;
                  totSum += value.sum;
                  if (value.qty != 0 && cOrgs.Contains(o) == false)
                     cOrgs.Add(o);

                  if (data.totalOnly)
                     continue;

                  if (data.drawQty)
                     htmlRow.AppendCell(ToHtmlConv(value.qty.ToString()));

                  if (data.drawPack)
                     htmlRow.AppendCell(ToHtmlConv(pack.ToString("N2")));

                  htmlRow.AppendCell(ToHtmlConv(String.Format("{0:C}", value.sum)));
               }

               if (data.drawQty)
                  htmlRow.AppendCell(ToHtmlConv(totQty.ToString()));

               if (data.drawPack)
                  htmlRow.AppendCell(ToHtmlConv(totPack.ToString("N2")));

                  htmlRow.AppendCell(ToHtmlConv(String.Format("{0:C}", totSum)));

#if COVER_IN_ORDER_REPORT
               htmlRow.AppendCell(ToHtmlConv((((double)cOrgs.Count / orgs.Count) * 100).ToString("N2")));
               htmlRow.AppendCell(ToHtmlConv(p.qty.ToString()));
               htmlRow.AppendCell(ToHtmlConv(cOrgs.Count.ToString()));
               htmlRow.AppendCell(ToHtmlConv(((int)((double)orgs.Count * 0.8 + 0.5)).ToString()));
#endif
               html.Append(htmlRow.Val);
            }
         }

         html.Append("</table>\n");
         html.Append(GetFooter());
         html.Append("</body></html>");

         WriteToTmpFile(html.ToString());
         return reportFileName;
      }
   }
}

/*
 * Copyright (C), 2010, Гильдия разработчиков
 *
 * Отчет по заявкам в формате HTML
 * 
 * kki   19/03/2011   creating
 */

using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;

using OrgPriceT = System.Collections.Generic.KeyValuePair<GRSoft.NapoleonManager.Org, GRSoft.NapoleonManager.Reports.PricesStruct>;
using PriceT = System.Collections.Generic.KeyValuePair<GRSoft.NapoleonManager.Price, GRSoft.NapoleonManager.Reports.DataOrgPrice>;
using GroupedPriceT = System.Collections.Generic.KeyValuePair<string, GRSoft.NapoleonManager.Reports.DataPrice>;

namespace GRSoft.NapoleonManager.Reports.Html
{
   class HTMLOrderReport : HtmlReport, IReportImplementation
   {
      private const string ORDER_FILE_MASK = "order_{0}.html";
#if Servolux
      static readonly string ITEMS_HEAD = "Базовая номенклатура";
      static readonly string ITEM_QTY = "кг";
      static readonly string PACK_QTY = "ящ.";
#else
      static readonly string ITEMS_HEAD = "Номенклатура";
      static readonly string ITEM_QTY = "шт.";
      static readonly string PACK_QTY = "уп.";
#endif

      public HTMLOrderReport()
         :base(ORDER_FILE_MASK)
      {
         
      }

      #region IReport Members

      public void Show()
      {
         OpenReportInAssociationBrowser();
      }

      #endregion

      #region IReport Members


      public void Build(ReportData reportData)
      {
         StringBuilder html = new StringBuilder("<html><head>" +
            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"></head>" +
            "<body><FONT FACE=\"Arial\">");

         OrderReportData ordData = (OrderReportData)reportData;
         OrderReportOptions options = ordData.options;
         int collumns_count = (options.onlyTotal) ? 1 : ordData.orgs.Count + 1;

         if (options.agent != null)
            html.Append("Агент: ").Append(options.agent.Name);
         else if (options.division != null)
            html.Append("Подразделение: ").Append(options.division.ToString());
         
         html.Append("<br>");
         html.Append(ordData.options.filter).Append("<br>");

         html.Append("<table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR=\"#000000\">\n");
         
         string cellspan = String.Empty;

         if (options.itemType == ItemType.itBoth)
            cellspan = "colspan=2";

         html.Append("<tr>\n\t<td colspan=2>").Append(ITEMS_HEAD).Append("</td>");
#if Servolux
         html.Append("<td>").Append("Вид упаковки").Append("</td>").Append("<td>").Append("Терм.состояние").Append("</td>");
#endif
         if (options.onlyTotal == false)
         {
            foreach (Org org in ordData.orgs)
            {
               html.Append("\t<td ").Append(cellspan).Append(">").Append(ToHtmlConv(org.Name)).Append("</td>\n");
            }
         }
         html.Append("\t<td ").Append(cellspan).Append(">итого</td>\n");
         html.Append("</tr>\n");

#if Servolux
         html.Append("<tr>\n\t<td colspan=4>&nbsp;</td>\n");
#else
         html.Append("<tr>\n\t<td colspan=2>&nbsp;</td>\n");
#endif

         if (options.onlyTotal == false)
         {
            foreach (Org org in ordData.orgs)
            {
               html.Append("\t<td>").Append(options.itemType == ItemType.itPiece ||
                  options.itemType == ItemType.itBoth ? ITEM_QTY : PACK_QTY).Append("</td>\n");

               if (options.itemType == ItemType.itBoth)
                  html.Append("\t<td>уп.</td>\n");
            }
         }

         html.Append("\t<td>").Append(options.itemType == ItemType.itPiece ||
            options.itemType == ItemType.itBoth ? ITEM_QTY : PACK_QTY).Append("</td>\n");

         if (options.itemType == ItemType.itBoth)
            html.Append("\t<td>уп.</td>\n");

         html.Append("</tr>\n");

         int value_cells_count = options.itemType == ItemType.itBoth ? collumns_count * 2 : collumns_count;
         value_cells_count++; //Артикул товара, колонка 0
         value_cells_count++; //На имя товара, колонка 1
#if Servolux
         value_cells_count+=2; //терм сост+вид упак
#endif

         foreach (GroupedPriceT row in ordData.CollectPriceRows())
         {
            /* Жирным шрифтом пишется заголовок группы товаров*/
            html.Append("<tr>\n");
            string group_caption = options.folders.ContainsKey(row.Key)
               ? options.folders[row.Key].name
               : row.Key;

            html.Append("\t<td colspan=").Append((value_cells_count)).
               Append("><b>").Append(group_caption).Append("</b></td>\n");
           // CreateEmpryCellRegion(collumns_count, cellspan, html);
            html.Append("</tr>\n");

            /*Построчно товары - значение*/
            foreach (PriceT rrow in row.Value)
            {
               HtmlRow htmlRow = new HtmlRow(value_cells_count);
               htmlRow[0] = rrow.Key.id;
#if Servolux
               Price item = rrow.Key;
               htmlRow[1] = item.name;
               htmlRow[2] = item.packName == null ? "" : item.packName;
               htmlRow[3] = item.thermalState == null ? "" : item.thermalState;
#else
               htmlRow[1] = rrow.Key.name;
#endif
               double packs = 0;

               double val = 0;

               foreach (OrgPriceT rrrow in rrow.Value)
               {
                  if (options.onlyTotal == false)
                  {
                     int org_col = options.itemType == ItemType.itBoth ?
                        ordData.GetOrgCol(rrrow.Key) * 2 :
                        ordData.GetOrgCol(rrrow.Key) + 1;

                     org_col += 2; // добавим место под товар
#if Servolux
                     org_col += 2;
#endif
                     htmlRow[org_col] = ToHtmlConv(
                        (options.itemType == ItemType.itPiece ||
                         options.itemType == ItemType.itBoth) ?
                           rrrow.Value.GetValue(0).ToString() :
                           rrrow.Value.GetValue(1).ToString());

                     if (options.itemType == ItemType.itBoth)
                        htmlRow[org_col +1 ] = ToHtmlConv(rrrow.Value.GetValue(1).ToString());
                  }

                  val += rrrow.Value.GetValue(0);
                  packs += rrrow.Value.GetValue(1);
               }

               htmlRow[options.itemType == ItemType.itBoth ? value_cells_count - 2 : value_cells_count - 1] = ToHtmlConv(
                     (options.itemType == ItemType.itPiece ||
                      options.itemType == ItemType.itBoth) ?
                        val.ToString() :
                        packs.ToString());

               if (options.itemType == ItemType.itBoth)
                  htmlRow[value_cells_count - 1] = packs.ToString();

               html.Append(htmlRow.Val);
            }
         }

         html.Append("</table>\n");
         html.Append(GetFooter());
         html.Append("</body></html>");

         WriteToTmpFile(html.ToString());
      }

      #endregion
   }
}

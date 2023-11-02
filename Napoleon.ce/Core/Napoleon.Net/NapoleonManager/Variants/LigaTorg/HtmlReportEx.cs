/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Отчет по заявкам
 * 
 * kki   21/12/2010   creating
 */
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.NapoleonManager.Reports;
using GRSoft.NapoleonManager.Reports.Html;
using GRSoft.UILib;
using System.IO;

namespace GRSoft.NapoleonManager
{
   internal class HtmlReportEx : HtmlReport
   {
      public HtmlReportEx()
      {
      }

      public override string makeAgentSummaryFileInfo(UILib.TreeGridView grid, TimeInterval interval)
      {
         string fileName = String.Format("agent_summary_file_info_{0}.html", ++doc_number);
         string result = System.IO.Path.GetTempPath() + fileName;

         int clmns = grid.Columns.Count;

         string html = "<html><head> " +
            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">" +
            "</head><body>" +
               "<FONT FACE=\"Arial\">" +
               "<table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR=\"#000000\">";
         html += String.Format("<H3>Итоговый отчёт подразделения {0} </H3><p>", grid.Nodes[0].Cells[0].Value.ToString());
         html += String.Format("<FONT SIZE=\"2\">Период: <b>{0} - {1}</b><br><br>",
            interval.begin.ToString("dd.MM.yyyy"), interval.end.ToString("dd.MM.yyyy"));

         html += String.Format("<tr BGCOLOR=\"#CCCCCC\" ><td><FONT SIZE=\"2\"><b>{0}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{1}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{2}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{3}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{4}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{5:0.00}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{6:0.00}</b></td>" + "</tr>",
            "Подразделение / агент", "Визиты", "Заявки всего, шт", "в т.ч. Заказы", "в т.ч. Сдача денег в банк", "Сумма", "Средний заказ");

         SummaryDivisionDataEx sd = grid.Nodes[0].DataItem as SummaryDivisionDataEx;
         if( sd != null )
         {
            html += PrintData(sd, "#92d050");
         }

         html += "</table>";
         html += "<SUB>Построен в системе \"Наполеон\" <a href=http://grsoft.ru/>http://grsoft.ru/</a></SUB>";
         html += "</body></html>";

         StreamWriter sw = new StreamWriter(result);
         sw.Write(html);
         sw.Flush();
         return result;
      }

      private string PrintData(SummaryDivisionDataEx sd, string backcolor)
      {
         string fmtStr = "<tr BGCOLOR=\"{0}\" ><td><FONT SIZE=\"2\"><b>{1}</b></td>" +
            "<td style=\"text-align:right\"><FONT SIZE=\"2\"><b>{2}</b></td>" +
            "<td style=\"text-align:right\"><FONT SIZE=\"2\"><b>{3}</b></td>" +
            "<td style=\"text-align:right\"><FONT SIZE=\"2\"><b>{4}</b></td>" +
            "<td style=\"text-align:right\"><FONT SIZE=\"2\"><b>{5}</b></td>" +
            "<td style=\"text-align:right\"><FONT SIZE=\"2\"><b>{6:0.00}</b></td>" +
            "<td style=\"text-align:right\"><FONT SIZE=\"2\"><b>{7:0.00}</b></td>" + "</tr>";

         StringBuilder sb = new StringBuilder();
         sb.AppendFormat(fmtStr, backcolor, sd.Division.name, sd.Visits, sd.Orders, sd.ord, sd.bank, sd.DocSum, sd.AvgOrder);

         foreach(SummaryDivisionDataEx ch in sd.Childs)
         {
            sb.Append(PrintData(ch, "#FFFF33"));
         }

         foreach(TreeGridNode tn in sd.Agents)
         {
            SummaryDataEx sde = (SummaryDataEx)tn.DataItem;
            if( sde != null )
            {
               sb.AppendFormat(fmtStr, "#FFFFFF", sde.Name, sde.Visits, sde.Orders, sde.ord, sde.bank, sde.DocSum, sde.AvgOrder);
            }
         }

         return sb.ToString();
      }
   }
}
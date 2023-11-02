/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Отчет по "общая статистика"
 * 
 * kki   12/01/2011   creating
 */
using System;
using System.Collections.Generic;
using System.Text;
using DW.RtfWriter;
using System.Diagnostics;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager.Reports
{
   class CommonStatisticReport
   {
      private String fileName = "crpt.rtf";

      private RtfDocument doc = new RtfDocument(
         PaperSize.A4, PaperOrientation.Portrait, Lcid.Russian_Russia);

      public void Build(DateTime begin, DateTime end,
         DataGridView data)
      {
         const String DATE_MASK = "dd.MM.yyyy";
         RtfParagraph par = doc.Header.addParagraph();
         par.Text = String.Format("Отчет за период {0} - {1}", begin.Date.ToString(DATE_MASK),
            end.Date.ToString(DATE_MASK));
         par = doc.Footer.addParagraph();
         par.Text = "Гильдия разработчиков www.grsoft.ru";

         RtfTable table = doc.addTable(data.RowCount + 1, data.ColumnCount);

         for (int c = 0; c < data.ColumnCount; c++)
            table.cell(0, c).addParagraph().Text = data.Columns[c].HeaderText;

         for (int r = 0; r < data.RowCount; r++)
            for (int c = 0; c < data.ColumnCount; c++)
               table.cell(r + 1, c).addParagraph().Text =
                  data.Rows[r].Cells[c].Value.ToString();

         table.setInnerBorder(DW.RtfWriter.BorderStyle.Single, 1f);
         table.setOuterBorder(DW.RtfWriter.BorderStyle.Single, 1f);

         doc.save(fileName);
      }

      public void Show()
      {
         Process p = new Process();
         p.StartInfo.FileName = fileName;
         p.Start();
      }
   }
}

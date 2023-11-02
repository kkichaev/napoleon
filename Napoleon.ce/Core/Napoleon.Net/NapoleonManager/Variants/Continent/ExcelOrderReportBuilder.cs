using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class ExcelOrderReportBuilder : Excel
   {
      static readonly string ITEMS_HEAD = "Номенклатура";
      static readonly string ITEM_QTY = "шт.";
      static readonly string PACK_QTY = "уп.";
      const int COL_ORGS_INDEX = 3;

      int DrawHeader(OrdersReport.ReportData data, List<Org> orgs)
      {
         const int ROW_INDEX_AGENT_CAPTION = 1;
         const int COLUMN_INDEX_AGENT_CAPTION = 2;

         int rowIndex = ROW_INDEX_AGENT_CAPTION;

         string caption = string.Empty;
         if (data.agent != null)
            caption = String.Format("Агент: {0}", data.agent.Name);
         else if (data.division != null)
            caption = String.Format("Подразделение: {0}", data.division.ToString());
         SetValue(rowIndex++, COLUMN_INDEX_AGENT_CAPTION, caption);

#if COVER_IN_ORDER_REPORT
         string agentsAmount = "Всего контрагентов: " + data.data.Count.ToString();
         SetValue(rowIndex++, COLUMN_INDEX_AGENT_CAPTION, agentsAmount);
#endif
         SetValue(rowIndex++, COLUMN_INDEX_AGENT_CAPTION, data.Filter);

#if COVER_IN_ORDER_REPORT
         FreezePanes("C4");
#elif Servolux
         const int FIRST_ROW_POS = 3;
         FreezePanes("E3");
#else
         const int FIRST_ROW_POS = 3;
         FreezePanes("C3");
#endif
         const int FIRST_COL_POS = 1;
         SetValue(rowIndex, FIRST_COL_POS, "Артикул");
         SetCellBoldFont(rowIndex, FIRST_COL_POS, true);
         SetCellHorizontalAlign(rowIndex, FIRST_COL_POS, xlCenter);
         SetCellVerticalAlign(rowIndex, FIRST_COL_POS, xlCenter);

         SetValue(rowIndex, FIRST_COL_POS + 1, ITEMS_HEAD);
         SetCellBoldFont(rowIndex, FIRST_COL_POS + 1, true);
         SetCellHorizontalAlign(rowIndex, FIRST_COL_POS + 1, xlCenter);
         SetCellVerticalAlign(rowIndex, FIRST_COL_POS + 1, xlCenter);

#if Servolux
         int col = FIRST_COL_POS + 2;
         SetValue(rowIndexCounter, col, "Вид упаковки");
         SetCellBoldFont(rowIndexCounter, col, true);
         SetCellHorizontalAlign(rowIndexCounter, col, xlCenter);
         SetCellVerticalAlign(rowIndexCounter, col, xlCenter);

         col++;
         SetValue(rowIndexCounter, col, "Терм.состояние");
         SetCellBoldFont(rowIndexCounter, col, true);
         SetCellHorizontalAlign(rowIndexCounter, col, xlCenter);
         SetCellVerticalAlign(rowIndexCounter, col, xlCenter);
         SetOrientation(rowIndexCounter, col-1, ROW_ORGS_INDEX, col, 90);
#endif

         const double FIRST_COL_WIDTH = 50.0;
         const double ARTICUL_COL_WIDTH = 7.5;
         SetColumnWidth(FIRST_COL_POS, ARTICUL_COL_WIDTH);
         SetColumnWidth(FIRST_COL_POS + 1, FIRST_COL_WIDTH);

         int colIndex = COL_ORGS_INDEX;
         int MERGE_ORG_COLUMNS_AMOUNT = 0;
         if (data.drawPack)
            MERGE_ORG_COLUMNS_AMOUNT++;
         if (data.drawQty)
            MERGE_ORG_COLUMNS_AMOUNT++;

         object cell;

         if (!data.totalOnly)
         {
            foreach (Org o in orgs)
            {
               cell = GetCell(rowIndex, colIndex);
               SetShrinkToFit(cell, true);

               MergeCells(rowIndex, colIndex, rowIndex, colIndex + MERGE_ORG_COLUMNS_AMOUNT);
               SetCellHorizontalAlign(cell, xlCenter);

               SetValue(cell, o.Name);
               SetWrapeText(cell, true);

               DrawSubTitile(data, rowIndex + 1, colIndex);

               colIndex += MERGE_ORG_COLUMNS_AMOUNT + 1;
            }
         }

         cell = GetCell(rowIndex, colIndex);
         SetValue(cell, "итого");
         SetCellHorizontalAlign(cell, xlCenter);
         SetCellBoldFont(cell, true);
         MergeCells(rowIndex, colIndex, rowIndex, colIndex + MERGE_ORG_COLUMNS_AMOUNT);

         DrawSubTitile(data, rowIndex + 1, colIndex);

         colIndex += MERGE_ORG_COLUMNS_AMOUNT + 1;

#if COVER_IN_ORDER_REPORT
         SetValue(rowIndex, colIndex++, "Покрытие");
         SetValue(rowIndex, colIndex++, "На складе");
         SetValue(rowIndex, colIndex++, "Факт РТТ");
         SetValue(rowIndex, colIndex++, "Целевое кол-во РТТ");
#endif

         SetOrientation(rowIndex, COL_ORGS_INDEX, rowIndex, colIndex - 1, 90);
         return rowIndex + 2;
      }

      void DrawSubTitile(OrdersReport.ReportData data, int rowIndex, int colIndex)
      {
         object cell;
         int ci = colIndex;
         if (data.drawQty)
         {
            cell = GetCell(rowIndex, ci++);
            SetValue(cell, ITEM_QTY);
         }
         if (data.drawPack)
         {
            cell = GetCell(rowIndex, ci++);
            SetValue(cell, PACK_QTY);
         }
         cell = GetCell(rowIndex, ci++);
         SetValue(cell, "сумма");
      }

      public void Build(OrdersReport.ReportData data, IProgress progress)
      {
         progress.SetMax(data.folders.Count);

         List<Org> orgs = new List<Org>(data.data.Keys);
         orgs.Sort();

         int rowIndex = DrawHeader(data, orgs);


         const int COL_PRICE_INDEX = 2;
         const int COL_ARTCL_INDEX = 1;

         foreach (ManagerFolder mf in data.folders)
         {
            SetValue(rowIndex, COL_PRICE_INDEX, mf.name);
            SetCellBoldFont(rowIndex, COL_PRICE_INDEX, true);
            SetCellHorizontalAlign(rowIndex, COL_PRICE_INDEX, xlCenter);

            rowIndex++;
            List<Price> price = data.GetPriceList(mf);

            object cell;
            foreach (Price p in price)
            {
               SetValue(rowIndex, COL_ARTCL_INDEX, p.id);

               cell = GetCell(rowIndex, COL_PRICE_INDEX);
               SetValue(cell, p.name);
               SetShrinkToFit(cell, true);

               double totQty = 0, totPack = 0, totSum = 0;
               List<Org> cOrgs = new List<Org>();
               int ci = COL_ORGS_INDEX;
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
                  {
                     cell = GetCell(rowIndex, ci++);
                     SetValue(cell, value.qty);
                  }
                  if (data.drawPack)
                  {
                     cell = GetCell(rowIndex, ci++);
                     SetValue(cell, pack);
                  }
                  cell = GetCell(rowIndex, ci++);
                  SetValue(cell, String.Format("{0:C}", value.sum));
               }

               if (data.drawQty)
               {
                  cell = GetCell(rowIndex, ci++);
                  SetValue(cell, totQty);
               }
               if (data.drawPack)
               {
                  cell = GetCell(rowIndex, ci++);
                  SetValue(cell, totPack);
               }
               cell = GetCell(rowIndex, ci++);
               SetValue(cell, String.Format("{0:C}", totSum));

#if COVER_IN_ORDER_REPORT
               cell = GetCell(rowIndex, ci++);
               SetValue(cell, ((double)cOrgs.Count / orgs.Count) * 100);
               cell = GetCell(rowIndex, ci++);
               SetValue(cell, p.qty);
               cell = GetCell(rowIndex, ci++);
               SetValue(cell, cOrgs.Count);
               cell = GetCell(rowIndex, ci++);
               SetValue(cell, (int)((double)orgs.Count * 0.8 + 0.5));
#endif

               rowIndex++;
            }

            progress.AdvancePos(1);
         }
      }
   }
}

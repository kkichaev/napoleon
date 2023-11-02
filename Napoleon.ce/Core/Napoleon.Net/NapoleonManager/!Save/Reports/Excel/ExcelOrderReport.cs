/*
 * Copyright (C), 2010, Гильдия разработчиков
 *
 * Отчет по заявкам
 * 
 * kki   21/12/2010   creating
 */
using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Reports;

namespace GRSoft.NapoleonManager.Reports.Excel
{
   class ExcelOrderReport : Excel, IReportImplementation
   {
      #region IReport Members

      public void Show()
      {
         Visible = true;
      }

      #endregion

      public void Build(ReportData reportData)
      {
         OrderReportData ordData = (OrderReportData)reportData;
         OrderReportData.DataGroupedPice group = ordData.CollectPriceRows();

         const int COL_PRICE_INDEX = 2;
         const int ROW_ORGS_INDEX = 1;
         const int COL_ORGS_INDEX = 3;
         const int COL_ARTCL_INDEX = 1;
         int org_pos = COL_ORGS_INDEX;

         int table_width = ordData.orgs.Count;
         int table_height = group.Count + group.Values.Count;

         int col_index = COL_ORGS_INDEX;
         int row_index = ROW_ORGS_INDEX;

         FreezePanes("C2");

         while (table_width >= 0)
         {
            SetShrinkToFit(row_index, col_index, true);
            if (ordData.itemType == ItemType.itBoth) 
               MergeCells(row_index, col_index, row_index, col_index + 1);

            table_width--;
            col_index += ordData.itemType == ItemType.itBoth ? 2 : 1;
         }

         table_width = ordData.orgs.Count;

         SetCellHorizontalAlign(ROW_ORGS_INDEX + 1, COL_ORGS_INDEX, 
            ROW_ORGS_INDEX + 1, COL_ORGS_INDEX + table_width * 2 + 1, xlCenter);

         const double DEF_COLUMN_WITDH = 5.0;

         foreach (Org org in ordData.orgs)
         {
            SetValue(ROW_ORGS_INDEX, org_pos, org.Name);
            SetValue(ROW_ORGS_INDEX + 1, org_pos, 
               ordData.itemType == ItemType.itPiece ||
               ordData.itemType == ItemType.itBoth ? "шт." : "уп.");

            if (ordData.itemType == ItemType.itBoth)
               SetValue(ROW_ORGS_INDEX + 1, org_pos + 1, "уп.");

            SetColumnWidth(org_pos, DEF_COLUMN_WITDH);
            SetColumnWidth(org_pos + 1, DEF_COLUMN_WITDH);

            org_pos += ordData.itemType == ItemType.itBoth ? 2 : 1; ;
         }

         SetValue(ROW_ORGS_INDEX + 1, org_pos, ordData.itemType == ItemType.itPiece ||
               ordData.itemType == ItemType.itBoth ? "шт." : "уп.");

         if (ordData.itemType == ItemType.itBoth)
            SetValue(ROW_ORGS_INDEX + 1, org_pos + 1, "уп.");

         SetColumnWidth(org_pos, DEF_COLUMN_WITDH);
         SetColumnWidth(org_pos + 1, DEF_COLUMN_WITDH);

         const double DEF_ORGS_ROW_HEIGHT = 85.0;
         SetRowHeight(ROW_ORGS_INDEX, DEF_ORGS_ROW_HEIGHT);
         SetOrientation(ROW_ORGS_INDEX, COL_ORGS_INDEX,
            ROW_ORGS_INDEX, org_pos - 1, 90);

         row_index += 2;

         List<KeyValuePair<int, Pair<double, double>>> rowVal = new List<KeyValuePair<int, Pair<double, double>>>();

         foreach (KeyValuePair<string, Dictionary<Price,
            Dictionary<Org, Pair<double, double>>>> row in group)
         {
            string group_caption = ordData.dsManagerFolder.ContainsKey(row.Key)
               ? ordData.dsManagerFolder[row.Key].name
               : row.Key;

            SetValue(row_index, COL_PRICE_INDEX, group_caption);
            SetCellBoldFont(row_index, COL_PRICE_INDEX, true);
            SetCellHorizontalAlign(row_index, COL_PRICE_INDEX, xlCenter);
            SetShrinkToFit(row_index, COL_PRICE_INDEX, true);
            row_index++;

            foreach (KeyValuePair<Price, Dictionary<Org, Pair<double, double>>> rrow in row.Value)
            {
               SetValue(row_index, COL_ARTCL_INDEX, rrow.Key.id);
               SetValue(row_index, COL_PRICE_INDEX, rrow.Key.name);
               SetShrinkToFit(row_index, COL_PRICE_INDEX, true);

               double val = 0;
               double packs = 0;

               foreach (KeyValuePair<Org, Pair<double, double>> rrrow in rrow.Value)
               {
                  int org_col = ordData.itemType == ItemType.itBoth ?
                     ordData.GetOrgCol(rrrow.Key) * 2 - 1 :
                     ordData.GetOrgCol(rrrow.Key);

                  int curCol = COL_ORGS_INDEX + org_col - 1;

                  SetValue(row_index, curCol,
                     (ordData.itemType == ItemType.itPiece || ordData.itemType == ItemType.itBoth) ?
                     rrrow.Value.obj_1 : rrrow.Value.obj_2);

                  if (ordData.itemType == ItemType.itBoth)
                     SetValue(row_index, curCol + 1, rrrow.Value.obj_2);

                  val += rrrow.Value.obj_1;
                  packs += rrrow.Value.obj_2;
               }

               rowVal.Add(new KeyValuePair<int, Pair<double,double>>(row_index, 
                  new Pair<double, double>(val, packs)));
               row_index++;
            }

         }

         const int FIRST_COL_POS = 1;
         const int FIRST_ROW_POS = 1;
         SetValue(FIRST_ROW_POS, 1, "Артикул");
         SetCellBoldFont(FIRST_ROW_POS, FIRST_COL_POS, true);
         SetCellHorizontalAlign(FIRST_ROW_POS, FIRST_COL_POS, xlCenter);
         SetCellVerticalAlign(FIRST_ROW_POS, FIRST_COL_POS, xlCenter);

         SetValue(FIRST_ROW_POS, FIRST_COL_POS + 1, "Номенклатура");
         SetCellBoldFont(FIRST_ROW_POS, FIRST_COL_POS + 1, true);
         SetCellHorizontalAlign(FIRST_ROW_POS, FIRST_COL_POS + 1, xlCenter);
         SetCellVerticalAlign(FIRST_ROW_POS, FIRST_COL_POS + 1, xlCenter);

         const double FIRST_COL_WIDTH = 50.0;
         const double ARTICUL_COL_WIDTH = 7.5;
         SetColumnWidth(FIRST_COL_POS, ARTICUL_COL_WIDTH);
         SetColumnWidth(FIRST_COL_POS + 1, FIRST_COL_WIDTH);

         SetValue(ROW_ORGS_INDEX, org_pos, "итого");
         SetCellHorizontalAlign(ROW_ORGS_INDEX, org_pos, xlCenter);
         SetCellBoldFont(ROW_ORGS_INDEX, org_pos, true);

         foreach (KeyValuePair<int, Pair<double, double>> v in rowVal)
         {
            SetValue(v.Key, org_pos, ordData.itemType == ItemType.itPiece 
               || ordData.itemType == ItemType.itBoth ? 
               v.Value.obj_1 :
               v.Value.obj_2);

            if (ordData.itemType == ItemType.itBoth)
               SetValue(v.Key, org_pos + 1, v.Value.obj_2);
         }

         SetSelectedCell("A1");
      }
   }
}

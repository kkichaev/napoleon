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
using System.Globalization;

using OrgPriceT = System.Collections.Generic.KeyValuePair<GRSoft.NapoleonManager.Org, GRSoft.NapoleonManager.Reports.PricesStruct>;
using PriceT = System.Collections.Generic.KeyValuePair<GRSoft.NapoleonManager.Price, GRSoft.NapoleonManager.Reports.DataOrgPrice>;
using GroupedPriceT = System.Collections.Generic.KeyValuePair<string, GRSoft.NapoleonManager.Reports.DataPrice>;

namespace GRSoft.NapoleonManager.Reports.Excel
{
   class ExcelOrderReport : Excel, IReportImplementation
   {
#if REPORT_INCLUDE_DELIVERIES
      const int ROW_ORD_DEL_INDEX = 4;
#endif

#if Servolux
      const int COL_ORGS_INDEX = 5;
      static readonly string ITEMS_HEAD = "Базовая номенклатура";
      static readonly string ITEM_QTY = "кг";
      static readonly string PACK_QTY = "ящ.";
#elif MyasoDel || Sapfire || PoultryNSib || Antonov || MeatAlliance
      const int COL_ORGS_INDEX = 3;
      static readonly string ITEMS_HEAD = "Номенклатура";
      static readonly string ITEM_QTY = "шт.";
      static readonly string PACK_QTY = "кг";
#else
      const int COL_ORGS_INDEX = 3;
      static readonly string ITEMS_HEAD = "Номенклатура";
      static readonly string ITEM_QTY = "шт.";
      static readonly string PACK_QTY = "уп.";
#endif

#if PoultryNSib || TDLider || Antonov || MeatAlliance
      Dictionary<int, double> weights = new Dictionary<int, double>();
#endif

#if TDLider
      Dictionary<int, double> dlvWeights = new Dictionary<int, double>();
#endif

#if LukasN
      Dictionary<int, double> sums = new Dictionary<int, double>();
#endif

      bool INCLUDE_BOTH_COLUMNS = false;
      bool PIECES_CAPTION_FIRST = false;
      int COLUMN_INDEX_MULT = 1;
      int ROW_ORGS_INDEX = 3;
      int ROW_TYPE_INDEX = 1;

      #region IReport Members

      public void Show()
      {
         Visible = true;
      }

      #endregion

      public void Build(ReportData reportData)
      {
         OrderReportData ordData = (OrderReportData)reportData;
         OrderReportOptions options = ordData.options;
         DataGroupedPrice group = ordData.CollectPriceRows();

         INCLUDE_BOTH_COLUMNS = options.itemType == ItemType.itBoth;
         PIECES_CAPTION_FIRST = options.itemType == ItemType.itPiece || options.itemType == ItemType.itBoth;
         COLUMN_INDEX_MULT = INCLUDE_BOTH_COLUMNS ? 2 : 1;

#if COVER_IN_ORDER_REPORT
         ROW_ORGS_INDEX += 1;
#endif

         ROW_TYPE_INDEX += ROW_ORGS_INDEX;

#if REPORT_INCLUDE_DELIVERIES
         if (options.includeDeliveries)
         {
            COLUMN_INDEX_MULT *= 2;
            ROW_TYPE_INDEX += ROW_ORD_DEL_INDEX - ROW_ORGS_INDEX;
         }
#endif
         BuildAgents(ordData);
         PrepareOrgsHeader(ordData, group);
         FillConstHeaders();
         FillOrgs(ordData);
         List<KeyValuePair<int, PricesStruct>> values = FillValues(ordData, group);
         FillFinals(ordData, values);
         SetSelectedCell("A1");
      }

      public void BuildAgents(OrderReportData ordData)
      {
         const int ROW_INDEX_AGENT_CAPTION = 1;
         const int COLUMN_INDEX_AGENT_CAPTION = 2;

         int rowIndexCounter = ROW_INDEX_AGENT_CAPTION;

         string caption = string.Empty;
         if (ordData.options.agent != null)
            caption = String.Format("Агент: {0}", ordData.options.agent.Name);
         else if (ordData.options.division != null)
            caption = String.Format("Подразделение: {0}", ordData.options.division.ToString());

         SetValue(rowIndexCounter++, COLUMN_INDEX_AGENT_CAPTION, caption);
#if COVER_IN_ORDER_REPORT
         string agentsAmount = "Всего контрагентов: " + ordData.orgs.Count.ToString();
         SetValue(rowIndexCounter++, COLUMN_INDEX_AGENT_CAPTION, agentsAmount);
#endif
         SetValue(rowIndexCounter++, COLUMN_INDEX_AGENT_CAPTION, ordData.options.filter);
      }

      int GetOrgColumnsAmount(OrderReportData ordData)
      {
         int orgsAmount = ordData.options.onlyTotal ? 0 : ordData.orgs.Count * COLUMN_INDEX_MULT;
         return COL_ORGS_INDEX + (orgsAmount);
      }

      void PrepareOrgsHeader(OrderReportData ordData, DataGroupedPrice group)
      {
         int colIndex = COL_ORGS_INDEX;
         int rowIndex = ROW_ORGS_INDEX;

         int MERGE_ORG_COLUMNS_AMOUNT = INCLUDE_BOTH_COLUMNS ? 1 : 0;
#if REPORT_INCLUDE_DELIVERIES
         int MERGE_DELIVERY_COLUMNS_AMOUNT = 0;
         if (ordData.options.includeDeliveries)
         {
            MERGE_ORG_COLUMNS_AMOUNT += INCLUDE_BOTH_COLUMNS ? 2 : 1;
            MERGE_DELIVERY_COLUMNS_AMOUNT = INCLUDE_BOTH_COLUMNS ? 1 : 0;
         }
#endif
         int columnsCounter = ordData.options.onlyTotal ? 0 : ordData.orgs.Count;
         int tableHeight = group.Count + group.Values.Count;

         while (columnsCounter-- >= 0)
         {
            SetShrinkToFit(rowIndex, colIndex, true);
            MergeCells(rowIndex, colIndex, rowIndex, colIndex + MERGE_ORG_COLUMNS_AMOUNT);
#if REPORT_INCLUDE_DELIVERIES
            if (ordData.options.includeDeliveries)
            {
               MergeCells(ROW_ORD_DEL_INDEX, colIndex, ROW_ORD_DEL_INDEX, colIndex + MERGE_DELIVERY_COLUMNS_AMOUNT);
               int nextColIndex = colIndex + MERGE_DELIVERY_COLUMNS_AMOUNT + 1;
               MergeCells(ROW_ORD_DEL_INDEX, nextColIndex, ROW_ORD_DEL_INDEX, nextColIndex + MERGE_DELIVERY_COLUMNS_AMOUNT);
            }
#endif
            colIndex += MERGE_ORG_COLUMNS_AMOUNT + 1;
         }

         int amountOfColumns = GetOrgColumnsAmount(ordData);
#if REPORT_INCLUDE_DELIVERIES
         if (ordData.options.includeDeliveries)
         {
            SetCellHorizontalAlign(ROW_ORD_DEL_INDEX, COL_ORGS_INDEX, ROW_TYPE_INDEX, amountOfColumns, xlCenter);
         }
#endif

         SetValue(ROW_ORGS_INDEX, amountOfColumns, "итого");
         SetCellHorizontalAlign(ROW_ORGS_INDEX, amountOfColumns, xlCenter);
         SetCellBoldFont(ROW_ORGS_INDEX, amountOfColumns, true);

#if TDLider

         SetShrinkToFit(rowIndex, colIndex, true);

#if REPORT_INCLUDE_DELIVERIES
         if(ordData.options.includeDeliveries)
            MergeCells(rowIndex, colIndex, rowIndex, colIndex + 1);
#endif
         SetValue(ROW_ORGS_INDEX, amountOfColumns + COLUMN_INDEX_MULT, "итого");
         SetCellHorizontalAlign(ROW_ORGS_INDEX, amountOfColumns + COLUMN_INDEX_MULT, xlCenter);
         SetCellBoldFont(ROW_ORGS_INDEX, amountOfColumns + COLUMN_INDEX_MULT, true);
#endif



}

      void FillConstHeaders()
      {
#if COVER_IN_ORDER_REPORT
         const int FIRST_ROW_POS = 4;
         FreezePanes("C4");
#elif Servolux
         const int FIRST_ROW_POS = 3;
         FreezePanes("E3");
#else
         const int FIRST_ROW_POS = 3;
         FreezePanes("C3");
#endif
         const int FIRST_COL_POS = 1;
         SetValue(FIRST_ROW_POS, FIRST_COL_POS, "Артикул");
         SetCellBoldFont(FIRST_ROW_POS, FIRST_COL_POS, true);
         SetCellHorizontalAlign(FIRST_ROW_POS, FIRST_COL_POS, xlCenter);
         SetCellVerticalAlign(FIRST_ROW_POS, FIRST_COL_POS, xlCenter);

         SetValue(FIRST_ROW_POS, FIRST_COL_POS + 1, ITEMS_HEAD);
         SetCellBoldFont(FIRST_ROW_POS, FIRST_COL_POS + 1, true);
         SetCellHorizontalAlign(FIRST_ROW_POS, FIRST_COL_POS + 1, xlCenter);
         SetCellVerticalAlign(FIRST_ROW_POS, FIRST_COL_POS + 1, xlCenter);

#if Servolux
         int col = FIRST_COL_POS + 2;
         SetValue(FIRST_ROW_POS, col, "Вид упаковки");
         SetCellBoldFont(FIRST_ROW_POS, col, true);
         SetCellHorizontalAlign(FIRST_ROW_POS, col, xlCenter);
         SetCellVerticalAlign(FIRST_ROW_POS, col, xlCenter);

         col++;
         SetValue(FIRST_ROW_POS, col, "Терм.состояние");
         SetCellBoldFont(FIRST_ROW_POS, col, true);
         SetCellHorizontalAlign(FIRST_ROW_POS, col, xlCenter);
         SetCellVerticalAlign(FIRST_ROW_POS, col, xlCenter);
         SetOrientation(FIRST_ROW_POS, col-1, ROW_ORGS_INDEX, col, 90);
#endif

         const double FIRST_COL_WIDTH = 50.0;
         const double ARTICUL_COL_WIDTH = 7.5;
         SetColumnWidth(FIRST_COL_POS, ARTICUL_COL_WIDTH);
         SetColumnWidth(FIRST_COL_POS + 1, FIRST_COL_WIDTH);
      }

      void FillOrgs(OrderReportData ordData)
      {
         double DEF_COLUMN_WIDTH = 5.0;
#if REPORT_INCLUDE_DELIVERIES
         if (ordData.options.includeDeliveries && !INCLUDE_BOTH_COLUMNS)
            DEF_COLUMN_WIDTH = 10.0;
#endif
         const double DEF_ORGS_ROW_HEIGHT = 85.0;
         SetRowHeight(ROW_ORGS_INDEX, DEF_ORGS_ROW_HEIGHT);

         int startColumn = COL_ORGS_INDEX;
         int columnCounter = startColumn;
         int cnt = ordData.orgs.Count;
            

         for (int i = 0; i <= cnt; ++i)
         {
            if (i < ordData.orgs.Count)
            {
               if (ordData.options.onlyTotal)
                  continue;

               Org org = ordData.orgs[i];
               SetValue(ROW_ORGS_INDEX, columnCounter, org.Name);
               SetWrapeText(ROW_ORGS_INDEX, columnCounter, true);
            }
#if REPORT_INCLUDE_DELIVERIES
            if (ordData.options.includeDeliveries)
            {
               SetValue(ROW_ORD_DEL_INDEX, columnCounter, "заявлено");
            }
#endif
            SetColumnWidth(columnCounter, DEF_COLUMN_WIDTH);
            SetValue(ROW_TYPE_INDEX, columnCounter++, PIECES_CAPTION_FIRST ? ITEM_QTY : PACK_QTY);
            
            if (INCLUDE_BOTH_COLUMNS)
            {
               SetColumnWidth(columnCounter, DEF_COLUMN_WIDTH);
               SetValue(ROW_TYPE_INDEX, columnCounter++, PACK_QTY);
            }

#if REPORT_INCLUDE_DELIVERIES
            if (ordData.options.includeDeliveries)
            {
               SetValue(ROW_ORD_DEL_INDEX, columnCounter, "отгружено");

               SetColumnWidth(columnCounter, DEF_COLUMN_WIDTH);
               SetValue(ROW_TYPE_INDEX, columnCounter++, PIECES_CAPTION_FIRST ? ITEM_QTY : PACK_QTY);
               if (INCLUDE_BOTH_COLUMNS)
               {
                  SetColumnWidth(columnCounter, DEF_COLUMN_WIDTH);
                  SetValue(ROW_TYPE_INDEX, columnCounter++, PACK_QTY);
               }
            }
#endif
         }

#if TDLider
#if REPORT_INCLUDE_DELIVERIES
         if (ordData.options.includeDeliveries)
         {
            SetValue(ROW_ORD_DEL_INDEX, columnCounter, "заявлено");
         }
#endif
         SetColumnWidth(columnCounter, DEF_COLUMN_WIDTH);
         SetValue(ROW_TYPE_INDEX, columnCounter++, "кг");

#if REPORT_INCLUDE_DELIVERIES
         if (ordData.options.includeDeliveries)
         {
            SetValue(ROW_ORD_DEL_INDEX, columnCounter, "отгружено");

            SetColumnWidth(columnCounter, DEF_COLUMN_WIDTH);
            SetValue(ROW_TYPE_INDEX, columnCounter++, "кг");
         }
#endif
#endif

#if COVER_IN_ORDER_REPORT
         SetValue(ROW_ORGS_INDEX, columnCounter++, "Покрытие");
         SetValue(ROW_ORGS_INDEX, columnCounter++, "На складе");
         SetValue(ROW_ORGS_INDEX, columnCounter++, "Факт РТТ");
         SetValue(ROW_ORGS_INDEX, columnCounter++, "Целевое кол-во РТТ");
#endif

#if LukasN
         SetValue(ROW_ORGS_INDEX, columnCounter++, "цены");
         SetCellHorizontalAlign(ROW_ORGS_INDEX, columnCounter, xlCenter);
         SetCellBoldFont(ROW_ORGS_INDEX, columnCounter, true);
#endif

         SetOrientation(ROW_ORGS_INDEX, startColumn, ROW_ORGS_INDEX, columnCounter - 1, 90);
      }

      List<KeyValuePair<int, PricesStruct>> FillValues(OrderReportData ordData, DataGroupedPrice group)
      {
         const int COL_PRICE_INDEX = 2;
         const int COL_ARTCL_INDEX = 1;
         int row_index = ROW_ORGS_INDEX + 2;
#if REPORT_INCLUDE_DELIVERIES
         if (ordData.options.includeDeliveries)
         {
            row_index += 1;
         }
#endif

         List<KeyValuePair<int, PricesStruct>> rowVal = new List<KeyValuePair<int, PricesStruct>>();

         foreach (GroupedPriceT row in group)
         {
            string group_caption = ordData.options.folders.ContainsKey(row.Key)
               ? ordData.options.folders[row.Key].name
               : row.Key;

            SetValue(row_index, COL_PRICE_INDEX, group_caption);
            SetCellBoldFont(row_index, COL_PRICE_INDEX, true);
            SetCellHorizontalAlign(row_index, COL_PRICE_INDEX, xlCenter);
#if !REPORT_INCLUDE_DELIVERIES
            SetShrinkToFit(row_index, COL_PRICE_INDEX, true);
#endif
            row_index++;

            foreach (PriceT rrow in row.Value)
            {
               SetValue(row_index, COL_ARTCL_INDEX, rrow.Key.id);
#if Servolux
               Price item = rrow.Key;
               SetValue(row_index, COL_PRICE_INDEX, item.name);
               SetValue(row_index, COL_PRICE_INDEX+1, item.packName);
               SetValue(row_index, COL_PRICE_INDEX+2, item.thermalState);
#else
               SetValue(row_index, COL_PRICE_INDEX, rrow.Key.name);
#endif
               SetShrinkToFit(row_index, COL_PRICE_INDEX, true);

               PricesStruct finalValues = new PricesStruct();
               int valueIndex = 0;
               foreach (OrgPriceT rrrow in rrow.Value)
               {
                  int curCol = COL_ORGS_INDEX + (ordData.GetOrgCol(rrrow.Key) * COLUMN_INDEX_MULT);
                  int curValue = 0;

                  double value = PIECES_CAPTION_FIRST ? rrrow.Value.GetValue(curValue++) : rrrow.Value.GetValue(++curValue);
                  if (!ordData.options.onlyTotal)
                     SetValue(row_index, curCol++, value);

#if MyasoDel || Sapfire
                  curValue++;
                  if (INCLUDE_BOTH_COLUMNS && !ordData.options.onlyTotal)
                     SetValue(row_index, curCol++, "");
#else
                  if (INCLUDE_BOTH_COLUMNS && !ordData.options.onlyTotal)
                     SetValue(row_index, curCol++, rrrow.Value.GetValue(curValue++));
#endif

#if REPORT_INCLUDE_DELIVERIES
                  if (ordData.options.includeDeliveries)
                  {
                     if (!INCLUDE_BOTH_COLUMNS) curValue++;
                     value = PIECES_CAPTION_FIRST ? rrrow.Value.GetValue(curValue++) : rrrow.Value.GetValue(++curValue);
                     if (!ordData.options.onlyTotal)
                        SetValue(row_index, curCol++, value);

                     if (INCLUDE_BOTH_COLUMNS && !ordData.options.onlyTotal)
                        SetValue(row_index, curCol++, rrrow.Value.GetValue(curValue++));
                  }
#endif
                  for (valueIndex = 0; valueIndex < curValue; ++valueIndex)
                     finalValues.SetValue(valueIndex, finalValues.GetValue(valueIndex) + rrrow.Value.GetValue(valueIndex));
               }
#if COVER_IN_ORDER_REPORT
               finalValues.SetValue(valueIndex++, ((double)rrow.Value.Count / ordData.orgs.Count) * 100);
               finalValues.SetValue(valueIndex++, rrow.Key.qty);
               finalValues.SetValue(valueIndex++, rrow.Value.Count);
               finalValues.SetValue(valueIndex++, (int)((double)ordData.orgs.Count * 0.8 + 0.5));
#endif
               rowVal.Add(new KeyValuePair<int, PricesStruct>(row_index, finalValues));

#if LukasN
               if (rrow.Key != null && rrow.Key.cost != null && rrow.Key.cost.Length > 0)
                  sums.Add(row_index, rrow.Key.cost[0] * finalValues.GetValue(0));
#endif

#if PoultryNSib || TDLider || Antonov || MeatAlliance
               weights.Add(row_index, rrow.Key.weight * finalValues.GetValue(0));
#endif

#if TDLider
               dlvWeights.Add(row_index, rrow.Key.weight * finalValues.GetValue(2));
#endif

               row_index++;
            }
         }
         return rowVal;
      }

      void FillFinals(OrderReportData ordData, List<KeyValuePair<int, PricesStruct>> rowVal)
      {
         int amountOfColumns = GetOrgColumnsAmount(ordData);

         foreach (KeyValuePair<int, PricesStruct> v in rowVal)
         {
            int curColumn = amountOfColumns;
            int curValue = 0;
            SetValue(v.Key, curColumn++, PIECES_CAPTION_FIRST ? v.Value.GetValue(curValue++) : v.Value.GetValue(++curValue));

            if (INCLUDE_BOTH_COLUMNS)
               SetValue(v.Key, curColumn++, v.Value.GetValue(curValue++));

#if REPORT_INCLUDE_DELIVERIES
            if (ordData.options.includeDeliveries)
            {
               if (!INCLUDE_BOTH_COLUMNS) curValue++;
               SetValue(v.Key, curColumn++, PIECES_CAPTION_FIRST ? v.Value.GetValue(curValue++) : v.Value.GetValue(++curValue));

               if (INCLUDE_BOTH_COLUMNS)
                  SetValue(v.Key, curColumn++, v.Value.GetValue(++curValue));
            }
#endif


#if TDLider
            SetValue(v.Key, curColumn++, weights[v.Key]);
#if REPORT_INCLUDE_DELIVERIES
            if (ordData.options.includeDeliveries)
               SetValue(v.Key, curColumn++, dlvWeights[v.Key]);
#endif
#endif

#if COVER_IN_ORDER_REPORT
            SetValue(v.Key, curColumn++, v.Value.GetValue(curValue++));
            SetValue(v.Key, curColumn++, v.Value.GetValue(curValue++));
            SetValue(v.Key, curColumn++, v.Value.GetValue(curValue++));
            SetValue(v.Key, curColumn++, v.Value.GetValue(curValue++));
#endif

#if LukasN
            if (sums.ContainsKey(v.Key))
               SetValue(v.Key, curColumn++, sums[v.Key]);
#endif
         }
      }
   }
}

using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Reports;
using System.Globalization;

namespace GRSoft.NapoleonManager.Reports.Excel
{
   class ExcelMerchReport : Excel, IReportImplementation
   {
      #region IReport Members

      public void Show()
      {
         Visible = true;
      }

      #endregion

      const int NAME_MERGE_START_INDEX = 1;
      const int NAME_MERGE_FINISH_INDEX = 3;
      const int MARKS_MERGE_START_INDEX = NAME_MERGE_FINISH_INDEX + 1;
      const int MARKS_MERGE_FINISH_INDEX = MARKS_MERGE_START_INDEX + 1;

      const int VALUES_COLUMN_START_INDEX = MARKS_MERGE_FINISH_INDEX + 1;

      List<ActionCategory> categories = new List<ActionCategory>();

      static readonly Color BackHeadColor = Color.LightGreen;

      private void PrepareHeader(int maxLevel, int agentsAmount)
      {
         const int startingRow = 1;
         int finishRow = startingRow + maxLevel;
         MergeCells(startingRow, NAME_MERGE_START_INDEX, finishRow, NAME_MERGE_FINISH_INDEX);
         if (1 == agentsAmount)
            SetValue(startingRow, NAME_MERGE_START_INDEX, "Торговые точки");
         else
            SetValue(startingRow, NAME_MERGE_START_INDEX, "Агенты");
         SetCellVerticalAlign(startingRow, NAME_MERGE_START_INDEX, xlCenter);

         MergeCells(startingRow, MARKS_MERGE_START_INDEX, finishRow, MARKS_MERGE_FINISH_INDEX);
         SetValue(startingRow, MARKS_MERGE_START_INDEX, "Показатели");
         SetCellVerticalAlign(startingRow, MARKS_MERGE_START_INDEX, xlCenter);

         SetBackColor(GetRange(startingRow, NAME_MERGE_START_INDEX, startingRow, MARKS_MERGE_FINISH_INDEX), BackHeadColor);
      }

      private void PrepareAgentsOrgsList(MerchReportData reportData)
      {
         int dataRowsAmount = (null != reportData.agent) ? reportData.orgs.Count : reportData.agents.Count;
         int startingRow = reportData.maxLevel + 2;
         for (int i = 0; i < dataRowsAmount; ++i)
         {
            string curName = (null != reportData.agent) ? reportData.orgs[i].Name : reportData.agents[i].Name;
            //names
            int row = startingRow + i * 2;
            MergeCells(row, NAME_MERGE_START_INDEX, row + 1, NAME_MERGE_FINISH_INDEX);
            SetValue(row, NAME_MERGE_START_INDEX, curName);
            SetCellVerticalAlign(row, NAME_MERGE_START_INDEX, xlCenter);

            //values
            MergeCells(row, MARKS_MERGE_START_INDEX, row, MARKS_MERGE_FINISH_INDEX);
            SetValue(row, MARKS_MERGE_START_INDEX, "Метров");
            MergeCells(row + 1, MARKS_MERGE_START_INDEX, row + 1, MARKS_MERGE_FINISH_INDEX);
            SetValue(row + 1, MARKS_MERGE_START_INDEX, "SCU");
         }
      }

      private void PrepareCategoriesTree(MerchReportData reportData)
      {
         Dictionary<int, Dictionary<ActionCategory, int>> childrenAmount = new Dictionary<int, Dictionary<ActionCategory, int>>();
         for (int i = 0; i < reportData.maxLevel - 1; ++i)
            childrenAmount.Add(i, new Dictionary<ActionCategory, int>());

         foreach (MerchReportData.OrgShelf curNode in reportData.data)
         {
            if (categories.Contains(curNode.category))
               continue;

            categories.Add(curNode.category);
            ActionCategory curParent = curNode.category.parent;
            ActionCategory tempParent = curParent;
            while (tempParent != null)
            {
               if (!childrenAmount[tempParent.level].ContainsKey(tempParent))
                  childrenAmount[tempParent.level].Add(tempParent, 1);
               else
                  ++childrenAmount[tempParent.level][tempParent];

               tempParent = tempParent.parent;
            }
         }

         const int originCol = 6;
         foreach (KeyValuePair<int, Dictionary<ActionCategory, int>> row in childrenAmount)
         {
            int startRow = row.Key + 1;
            int startCol = originCol;
            int finishCol = 0;
            foreach (KeyValuePair<ActionCategory, int> subRow in row.Value)
            {
               finishCol = startCol + subRow.Value * 2 - 1;
               MergeCells(startRow, startCol, startRow, finishCol);
               SetValue(startRow, startCol, subRow.Key.Name);
               SetCellHorizontalAlign(startRow, startCol, xlCenter);
               SetBackColor(GetRange(startRow, startCol, startRow, finishCol), BackHeadColor);
               startCol = finishCol + 1;
            }
            startCol = originCol;
         }
      }

      public void Build(ReportData reportData)
      {
         MerchReportData merchData = reportData as MerchReportData;

         PrepareHeader(merchData.maxLevel, merchData.data.Count);
         PrepareAgentsOrgsList(merchData);
         PrepareCategoriesTree(merchData);

         const int originCol = 6;
         int catRow = merchData.maxLevel;
         int catCol = originCol;
         foreach (ActionCategory curCategory in categories)
         {
            MergeCells(catRow, catCol, catRow, catCol + 1);
            SetValue(catRow, catCol, curCategory.Name);
            SetBackColor(GetRange(catRow, catCol, catRow, catCol + 1), BackHeadColor);
            catCol += 2;
         }

         int valuesRow = merchData.maxLevel;
         int valuesCol = originCol;
         Dictionary<int, Dictionary<int, double>> values = new Dictionary<int,Dictionary<int,double>>();
         foreach (MerchReportData.OrgShelf shelf in merchData.data)
         {
            ActionCategory category = shelf.category;
            int columnOffset = categories.IndexOf(category) * 2;
            int rowOffset = ((null != merchData.agent) ? merchData.orgs.IndexOf(shelf.org) : merchData.agents.IndexOf(shelf.agent)) * 2;
            int curRow = 2 + valuesRow + rowOffset;
            int curCol = valuesCol + columnOffset;
            SetValue(valuesRow + 1, curCol, "Всего");
            SetCellHorizontalAlign(valuesRow + 1, curCol, xlCenter);
            SetBackColor(GetRange(valuesRow + 1, curCol, valuesRow + 1, curCol), BackHeadColor);
            SetValue(valuesRow + 1, curCol + 1, "Свои");
            SetCellHorizontalAlign(valuesRow + 1, curCol + 1, xlCenter);
            SetBackColor(GetRange(valuesRow + 1, curCol + 1, valuesRow + 1, curCol + 1), BackHeadColor);

            if (!values.ContainsKey(curRow))
               values.Add(curRow, new Dictionary<int, double>());
            if (!values[curRow].ContainsKey(curCol))
               values[curRow].Add(curCol, 0.0);
            if (!values[curRow].ContainsKey(curCol + 1))
               values[curRow].Add(curCol + 1, 0.0);
            if (!values.ContainsKey(curRow + 1))
               values.Add(curRow + 1, new Dictionary<int, double>());
            if (!values[curRow + 1].ContainsKey(curCol))
               values[curRow + 1].Add(curCol, 0.0);
            if (!values[curRow + 1].ContainsKey(curCol + 1))
               values[curRow + 1].Add(curCol + 1, 0.0);

            if (null != merchData.agent)
            {
               values[curRow][curCol] = shelf.metersAll;
               values[curRow][curCol + 1] = shelf.metersOur;
               values[curRow + 1][curCol] = shelf.skuAll;
               values[curRow + 1][curCol + 1] = shelf.skuOur;
            }
            else
            {
               values[curRow][curCol] += shelf.metersAll;
               values[curRow][curCol + 1] += shelf.metersOur;
               values[curRow + 1][curCol] += shelf.skuAll;
               values[curRow + 1][curCol + 1] += shelf.skuOur;
            }
         }
         foreach (KeyValuePair<int, Dictionary<int, double>> row in values)
         {
            foreach (KeyValuePair<int, double> value in row.Value)
            {
               SetValue(row.Key, value.Key, value.Value);
            }
         }
      }
   };
}

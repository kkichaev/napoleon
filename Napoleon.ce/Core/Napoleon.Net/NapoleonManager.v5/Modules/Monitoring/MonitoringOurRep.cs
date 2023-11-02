using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class MonitoringOurRep : Excel
   {
      delegate String GetItemData(object item);

      public void Do(MonReportParams.Data data, DataSet<int, Monitoring> dsMonitoring, DataSet<string, MonitoringItem> dsItems)
      {
         List<MonitoringItem> items = new List<MonitoringItem>();
         foreach (MonitoringItem i in dsItems.Data)
            if (i.IsOwn)
               items.Add(i);
         items.Sort();

         int sr1 = 1, sr2 = 1;

#if STD_MONITOR_REPORT
         int sr3 = 1;
         int maxCell3 = 0;
#endif

         int maxCell1 = 0, maxCell2 = 0;

         object sheet, sheet2;

         sheet = GetSheetByIndex(1);
#if STD_MONITOR_REPORT
         SetSheetName(sheet, "Фейсинг");
#else
         SetSheetName(sheet, "Суммы");
#endif
         sheet2 = AddSheet();
#if STD_MONITOR_REPORT
         SetSheetName(sheet2, "Цены");
#else
         SetSheetName(sheet2, "Количества");
#endif

#if STD_MONITOR_REPORT
         object sheet3;
         sheet3 = AddSheet();
         SetSheetName(sheet3, "На складе");


         GetItemData firstItemData = new GetItemData(delegate(object item) { return ((MonitoringDocItem)item).sku.ToString(); });
         GetItemData secondItemData = new GetItemData(delegate(object item) { return ((MonitoringDocItem)item).cost.ToString("C", Config.GetCultureInfo()); });
         GetItemData thirdItemData = new GetItemData(delegate(object item) { return ((MonitoringDocItem)item).face.ToString(); });
#else
         GetItemData firstItemData = new GetItemData(delegate(object item) { return ((MonitoringVolumeItem)item).qty.ToString(); });
         GetItemData secondItemData = new GetItemData(delegate(object item) { return ((MonitoringVolumeItem)item).cost.ToString("C", Config.GetCultureInfo()); });
#endif
         foreach (Agent agent in data.Agents)
         {
            SetSelectedSheet(1);
            SetValue(sr1, 1, "Ф.И.О.");
            SetValue(sr1++, 2, agent.Name);
            SetValue(sr1++, 1, "Ежедневный отчет");
            sr1++;

            int lastCell = DrawHead(items, sr1, 1);

            if (maxCell1 < lastCell)
               maxCell1 = lastCell;

            int lastRow = DrawData(data, agent.id, dsMonitoring, items, sr1, 1,
               firstItemData);

            SetBordersOnRange(sr1, 1, lastRow, lastCell, xlContinuous);
            sr1 = lastRow + 2;

            SetSelectedSheet(2);

            SetValue(sr2, 1, "Ф.И.О.");
            SetValue(sr2++, 2, agent.Name);
            SetValue(sr2++, 1, "Ежедневный отчет");
            sr2++;

            lastCell = DrawHead(items, sr2, 1);

            if (maxCell2 < lastCell)
               maxCell2 = lastCell;

            lastRow = DrawData(data, agent.id, dsMonitoring, items, sr2, 1, secondItemData);

            SetBordersOnRange(sr2, 1, lastRow, lastCell, xlContinuous);
            sr2 = lastRow + 2;

#if STD_MONITOR_REPORT
            SetSelectedSheet(3);

            SetValue(sr3, 1, "Ф.И.О.");
            SetValue(sr3++, 2, agent.Name);
            SetValue(sr3++, 1, "Ежедневный отчет");
            sr3++;

            lastCell = DrawHead(items, sr3, 1);

            if (maxCell3 < lastCell)
               maxCell3 = lastCell;


            lastRow = DrawData(data, agent.id, dsMonitoring, items, sr3, 1, thirdItemData);

            SetBordersOnRange(sr3, 1, lastRow, lastCell, xlContinuous);
            sr3 = lastRow + 2;
#endif
         }

         SetSelectedSheet(1);
         AutoFit(1);
         AutoFit(2);
         AutoFit(3);
         AutoFit(4);
         AutoFit(maxCell1);

         SetSelectedSheet(2);
         AutoFit(1);
         AutoFit(2);
         AutoFit(3);
         AutoFit(4);
         AutoFit(maxCell2);

#if STD_MONITOR_REPORT
         SetSelectedSheet(3);
         AutoFit(1);
         AutoFit(2);
         AutoFit(3);
         AutoFit(4);
         AutoFit(maxCell3);
#endif
      }

      private int DrawHead(List<MonitoringItem> items, int sr, int sc)
      {
         int cr = sr;
         int cc = sc;
         object cell;

         cell = GetCell(cr, cc);
         SetCellHorizontalAlign(cell, xlCenter);
         SetValue(cell, "№");
         MergeCells(cr, cc, cr + 1, cc);

         cc++;
         cell = GetCell(cr, cc);
         SetCellHorizontalAlign(cell, xlCenter);
         SetValue(cell, "Время");
         MergeCells(cr, cc, cr + 1, cc);

         cc++;
         cell = GetCell(cr, cc);
         SetCellHorizontalAlign(cell, xlCenter);
         SetValue(cell, "Наименование ТТ");
         MergeCells(cr, cc, cr + 1, cc);

         cc++;
         cell = GetCell(cr, cc);
         SetCellHorizontalAlign(cell, xlCenter);
         SetValue(cell, "Адрес");
         MergeCells(cr, cc, cr + 1, cc);
         cc++;

         foreach (MonitoringItem i in items)
         {

            cell = GetCell(cr, cc);
            SetCellHorizontalAlign(cell, xlCenter);
            SetValue(cell, i.Name);
#if STD_MONITOR_REPORT
            MergeCells(cr, cc, cr + 1, cc);
            cc++;
#endif
#if Ardis
            MergeCells(cr, cc, cr, cc + 3);
            cell = GetCell(cr + 1, cc++);
            SetCellHorizontalAlign(cell, xlCenter);
            SetValue(cell, "0.25");

            cell = GetCell(cr + 1, cc++);
            SetCellHorizontalAlign(cell, xlCenter);
            SetValue(cell, "0.5");

            cell = GetCell(cr + 1, cc++);
            SetCellHorizontalAlign(cell, xlCenter);
            SetValue(cell, "0.7");

            cell = GetCell(cr + 1, cc++);
            SetCellHorizontalAlign(cell, xlCenter);
            SetValue(cell, "1.0");
#endif
         }

         cell = GetCell(cr, cc);
         SetCellHorizontalAlign(cell, xlCenter);
         SetValue(cell, "Комментарии");
         MergeCells(cr, cc, cr + 1, cc);

         return cc;
      }

      private int DrawData(MonReportParams.Data data, string agentID,
         DataSet<int, Monitoring> dsMonitoring, List<MonitoringItem> items, int sr, int sc, GetItemData itemData)
      {
         int cr = sr + 2;
         int cc = sc;
         int idx = 1;
         DateTime date = data.date;
         DateTime till = data.dateEnd.AddDays(1);
         foreach (Monitoring m in dsMonitoring.Data)
         {
            if (m.AgentID != agentID)
               continue;

            if (m.created.CompareTo(date) >= 0 && m.created.CompareTo(till) < 0)
            {
               cc = sc;
               SetValue(cr, cc++, idx);
               SetValue(cr, cc++, m.created.ToString("dd.MM.yyyy HH:mm"));
               SetValue(cr, cc++, m.OrgName);
               SetValue(cr, cc++, m.OrgAddr);


               foreach (MonitoringItem mi in items)
               {
                  MonitoringDocItem di = m.FindItem(mi);

                  if (di != null
#if !STD_MONITOR_REPORT
                     && di.items.Count >= 4
#endif
)
                  {

                     di.items.Sort();
#if STD_MONITOR_REPORT
                     SetValue(cr, cc++, itemData(di));
#else
                     SetValue(cr, cc++, itemData(di.items[0]));
                     SetValue(cr, cc++, itemData(di.items[1]));
                     SetValue(cr, cc++, itemData(di.items[2]));
                     SetValue(cr, cc++, itemData(di.items[3]));
#endif
                  }
                  else
                     cc += 4;
               }

               SetValue(cr, cc, m.remark);
               cr++;
               idx++;
            }
         }
         return cr - 1;
      }
   }
}

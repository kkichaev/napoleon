using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class MonitoringConurentRep : Excel
   {
      public void Do(MonReportParams.Data data, DataSet<int, Monitoring> dsMonitoring, DataSet<string, MonitoringItem> dsItems)
      {
         List<MonitoringItem> items = new List<MonitoringItem>();
         foreach (MonitoringItem i in dsItems.Data)
            if (!i.IsOwn)
               items.Add(i);
         items.Sort();

         int sr = 1;
         foreach (Agent agent in data.Agents)
         {
            SetValue(sr, 1, "Ф.И.О.");
            SetValue(sr++, 2, agent.Name);

            int lastCell = DrawHead(items, sr, 1);
            int lastRow = DrawData(data, agent.id, dsMonitoring, items, sr, 1);

            SetBordersOnRange(sr, 1, lastRow, lastCell, xlContinuous);
            sr = lastRow + 2;
         }
         AutoFit(1);
         AutoFit(2);
         AutoFit(3);
         AutoFit(4);
      }

      private int DrawData(MonReportParams.Data data, string agentID, DataSet<int, Monitoring> dsMonitoring, List<MonitoringItem> items, int sr, int sc)
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
                  if (di != null)
                  {
                     di.items.Sort();
                     SetValue(cr, cc++, di.face.ToString());
                     SetValue(cr, cc++, di.sku.ToString());
#if Ardis
                     SetValue(cr, cc++, di.items[0].cost.ToString("C", Config.GetCultureInfo()));
                     SetValue(cr, cc++, di.items[1].cost.ToString("C", Config.GetCultureInfo()));
                     SetValue(cr, cc++, di.items[2].cost.ToString("C", Config.GetCultureInfo()));
                     SetValue(cr, cc++, di.items[3].cost.ToString("C", Config.GetCultureInfo()));
#endif
#if STD_MONITOR_REPORT
                     SetValue(cr, cc++, di.cost.ToString());
#endif
                  }
                  else
                     cc += 6;
               }

               SetValue(cr, cc, m.remark);
               cr++;
               idx++;
            }
         }

         return cr - 1;
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
#if Ardis
            MergeCells(cr, cc, cr, cc + 5);
#endif
#if STD_MONITOR_REPORT
            MergeCells(cr, cc, cr, cc + 2);
#endif
            cell = GetCell(cr + 1, cc++);
            SetCellHorizontalAlign(cell, xlCenter);
            SetValue(cell, "Фейс (кол-во)");

            cell = GetCell(cr + 1, cc++);
            SetCellHorizontalAlign(cell, xlCenter);
#if Ardis
            SetValue(cell, "SKU (0.25-1.0)");
#endif
#if STD_MONITOR_REPORT
            SetValue(cell, "SKU ");
            cell = GetCell(cr + 1, cc++);
            SetCellHorizontalAlign(cell, xlCenter);
            SetValue(cell, "Цена ");
#endif
#if Ardis
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

         return cc - 1;
      }
   }
}

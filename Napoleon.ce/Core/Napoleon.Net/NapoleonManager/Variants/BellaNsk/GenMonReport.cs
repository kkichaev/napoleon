using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class GenMonReport : Excel
   {
      public void Do(MonReportParams.Data data, DataSet<int, Monitoring> dsMonitoring, DataSet<string, MonitoringItem> dsItems, bool own)
      {
         List<MonitoringItem> items = new List<MonitoringItem>();
         foreach (MonitoringItem i in dsItems.Data)
            if (i.IsOwn == own)
               items.Add(i);
         
         items.Sort((lhs, rhs) => { return lhs.pos - rhs.pos; });

         List<string> itemIds = new List<string>();
         foreach(MonitoringItem mi in items)
            itemIds.Add(mi.id);

         int sr = 1;
         SetValue(sr++, 1, own ? "Наша продукция" : "Конкуренты");

         /*Наличие документов у агента*/
         Dictionary<String, bool> sz = new Dictionary<string,bool>();
         foreach (Monitoring m in dsMonitoring.Data)
         {
            if (!sz.ContainsKey(m.userid))
               sz[m.userid] = false;

            if (!sz[m.userid])
            {
               foreach (MonitoringDocItem mi in m.items)
               {
                  if (itemIds.Contains(mi.id))
                  {
                     sz[m.userid] = true;
                     break;
                  }
               }
            }
         }

         foreach (Agent agent in data.Agents)
         {
            if (agent == null || !sz.ContainsKey(agent.id) || !sz[agent.id])
               continue;

            SetValue(sr, 1, "Ф.И.О.");
            SetValue(sr, 2, agent.Name);
            sr++;
            SetValue(sr, 1, String.Format("Период: {0:dd/MM/yyyy} - {1:dd/MM/yyyy}", data.date, data.dateEnd));

            int lastCell = DrawHead(items, sr, 1);
            int lastRow = DrawData(data, agent.id, dsMonitoring, items, sr, 1);

            SetBordersOnRange(sr, 1, lastRow, lastCell, xlContinuous);
            sr = lastRow + 2;
         }

         for (int i = 1; i <= 8; i++)
            AutoFit(i);
      }

      private int DrawData(MonReportParams.Data data, string agentID, DataSet<int, Monitoring> dsMonitoring, List<MonitoringItem> items, int sr, int sc)
      {
         int cr = sr + 1;
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
               foreach (MonitoringItem mi in items)
               {
                  cc = sc;
                  SetValue(cr, cc++, idx);
                  SetValue(cr, cc++, m.created.ToString("dd.MM.yyyy"));
                  SetValue(cr, cc++, m.OrgName);
                  SetValue(cr, cc++, m.OrgAddr);

                  MonitoringDocItem di = m.FindItem(mi);
                  if (di != null)
                  {
                     SetValue(cr, cc++, mi.Name);
                     SetValue(cr, cc++, di.cost.ToString());
                     //SetValue(cr, cc++, di.face.ToString());
                     //SetValue(cr, cc++, di.sku.ToString());
                  }

                  cr++;
                  idx++;
               }
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

         cc++;
         cell = GetCell(cr, cc);
         SetCellHorizontalAlign(cell, xlCenter);
         SetValue(cell, "Дата");

         cc++;
         cell = GetCell(cr, cc);
         SetCellHorizontalAlign(cell, xlCenter);
         SetValue(cell, "Наименование торговой точки");

         cc++;
         cell = GetCell(cr, cc);
         SetCellHorizontalAlign(cell, xlCenter);
         SetValue(cell, "Адрес");

         cc++;
         cell = GetCell(cr, cc);
         SetCellHorizontalAlign(cell, xlCenter);
         SetValue(cell, "Товар");

         cc++;
         cell = GetCell(cr, cc);
         SetCellHorizontalAlign(cell, xlCenter);
         SetValue(cell, "Цена");

         //cc++;
         //cell = GetCell(cr, cc);
         //SetCellHorizontalAlign(cell, xlCenter);
         //SetValue(cell, "Фейс");

         //cc++;
         //cell = GetCell(cr, cc);
         //SetCellHorizontalAlign(cell, xlCenter);
         //SetValue(cell, "SKU");

         return cc;
      }
   }
}

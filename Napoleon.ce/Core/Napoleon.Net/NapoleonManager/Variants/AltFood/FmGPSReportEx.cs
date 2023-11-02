using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager.Reports.Excel;

namespace GRSoft.NapoleonManager
{
   class FmGPSReportEx : FmGPSReport
   {
      public FmGPSReportEx()
         :base()
      {
         btnReport.Text = "Excel";
      }

      protected override void MakeReport(List<GPSPos> list)
      {
         Dictionary<Agent, List<KeyValuePair<DateTime, KeyValuePair<double, double>>>> path = new Dictionary<Agent, List<KeyValuePair<DateTime, KeyValuePair<double, double>>>>();

         foreach (GPSPos pos in list)
         {
            if (pos.agent != null)
            {
               List<KeyValuePair<DateTime, KeyValuePair<double, double>>> pl = null;

               if (path.ContainsKey(pos.agent))
                  pl = path[pos.agent];
               else
               {
                  pl = new List<KeyValuePair<DateTime, KeyValuePair<double, double>>>();
                  path.Add(pos.agent, pl);
               }

               pl.Add(new KeyValuePair<DateTime,KeyValuePair<double,double>>(pos.date, new KeyValuePair<double,double>(pos.longitude, pos.latitude)));
            }
         }

         Dictionary<Agent, List<DayDist>> dist = new Dictionary<Agent, List<DayDist>>();
         foreach(Agent a in path.Keys)
         {
            List<KeyValuePair<DateTime, KeyValuePair<double, double>>> cl = path[a];
            cl.Sort((lhs, rhs) => {return lhs.Key.CompareTo(rhs.Key);});
            
            DateTime date = DateTime.MinValue;
            
            double lat = 0;
            double lon = 0;

            List<DayDist> dl = new List<DayDist>();
            dist.Add(a, dl);

            foreach(KeyValuePair<DateTime, KeyValuePair<double, double>> cla in cl)
            {
               if(!IsTheSameDay(date, cla.Key))
               {
                  date = cla.Key;
                  lon = cla.Value.Key;
                  lat = cla.Value.Value;
                  dl.Add(new DayDist(cla.Key, 0));
               }
               else
               {
                  dist[a][dl.Count - 1].dist += (int)Utils.Coordutils.Distance(lat, lon, cla.Value.Value, cla.Value.Key);
                  lon = cla.Value.Key;
                  lat = cla.Value.Value;
               }
            }
         }

         List<KeyValuePair<Agent, Dictionary<DateTime, int>>> outlist = new List<KeyValuePair<Agent, Dictionary<DateTime, int>>>();
         foreach (Agent a in dist.Keys)
         {
            Dictionary<DateTime, int> dic = new Dictionary<DateTime, int>();
            outlist.Add(new KeyValuePair<Agent, Dictionary<DateTime, int>>(a, dic));
            foreach (DayDist dd in dist[a])
               dic.Add(new DateTime(dd.date.Year, dd.date.Month, dd.date.Day), dd.dist);
         }

         outlist.Sort((lhs, rhs) => (lhs.Key.Name.CompareTo(rhs.Key.Name)));

         ReportData rd = new ReportData();
         rd.start = dtpBegin.Value.Date;
         rd.finish = dtpEnd.Value.Date;
         rd.items = outlist;

         RouteExcel report = new RouteExcel();
         report.DoReport(rd);
         report.Visible = true;
      }

      private static bool IsTheSameDay(DateTime date1, DateTime date2)
      {
         return (date1.Year == date2.Year && date1.DayOfYear == date2.DayOfYear);
      }
   }

   class ReportData
   {
      public DateTime start;
      public DateTime finish;
      public List<KeyValuePair<Agent, Dictionary<DateTime, int>>> items;
   }

   class RouteExcel : Excel
   {
      public void DoReport(ReportData data)
      {
         SetColumnWidth(1, 29);
         SetValue(1, 1, "Отчет по километражу");
         SetValue(2, 1, string.Format("период: {0:dd/MM/yyyy} - {1:dd/MM/yyyy}", data.start, data.finish));
         SetValue(3, 1, "Агент");
         FreezePanes("B4");

         DateTime s = data.start;
         const int DATA_START_COL = 2;
         int col = DATA_START_COL;

         do
         {
            SetValue(3, col, s.ToString("dd/MM/yyyy"));
            s = s.AddDays(1);
            SetColumnWidth(col, 15);
            col++;
         } while (s <= data.finish);

         SetValue(3, col, "Пробег итого");
         SetColumnWidth(col, 15);

         const int DATA_START_ROW = 3;
         int row = DATA_START_ROW + 1;

         foreach (KeyValuePair<Agent, Dictionary<DateTime, int>> r in data.items)
         {
            SetValue(row, 1, r.Key.Name);
            col = DATA_START_COL;
            s = data.start;

            do
            {
               if(r.Value.ContainsKey(s))
                  SetValue(row, col, r.Value[s]);

               col++;
               s = s.AddDays(1);
            } while (s <= data.finish);

            SetValue(row, col, string.Format("=СУММ(RC[-{0}]:RC[-1])", col - DATA_START_COL));

            row++;
         }

         SetBordersOnRange(DATA_START_ROW, 1, row - 1, col, xlContinuous); 
      }
   }

   class DayDist
   {
      public DateTime date;
      public int dist;

      public DayDist(DateTime date, int dist)
      {
         this.date = date;
         this.dist = dist;
      }
   }
}

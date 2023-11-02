using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Windows;
using System.Windows.Controls.Ribbon;
using System.Windows.Input;

namespace Ads2017
{
   public partial class DistanceWindow : RibbonWindow, Update.IDataLoadProcess
   {
      private ObservableCollection<Data> gridData = new ObservableCollection<Data>();
      private int doc_number = 0;
      private double sumDistance = 0.0;

      public static DependencyProperty SumDistanceProperty = DependencyProperty.Register("SumDistance",
          typeof(string), typeof(DistanceWindow), new PropertyMetadata(""));

      public DistanceWindow()
      {
         InitializeComponent();

         startDate.SelectedDate = DateTime.Now;
         finishDate.SelectedDate = DateTime.Now;
         startTime.Time = new TimeSpan();
         finishTime.Time = new TimeSpan(1, 0, 0, 0);

         grid.ItemsSource = gridData;
      }

      private void HTML_Click(object sender, RoutedEventArgs e)
      {
         MakeReport();
      }

      public void DoLoadData(Update.UpdateResult data)
      {
         sumDistance = 0.0;
         gridData.Clear();
         List<GPSPos> list = data.GetList<GPSPos>(GPSPos.OBJECT_NAME);

         list.Sort(new Comparison<GPSPos>(delegate (GPSPos p1, GPSPos p2)
         {
            int result = 0;
            if (p1.agent == null)
               return p2.agent == null ? 0 : -1;
            if (p2.agent == null)
               return 1;

            if (p1.agent != null && p2.agent != null)
               result = p1.agent.id.CompareTo(p2.agent.id);

            if (result == 0)
               result = p1.date.CompareTo(p2.date);

            return result;
         }));

         Dictionary<Agent, double> path = new Dictionary<Agent, double>();

         double lat = 0;
         double lon = 0;

         foreach (GPSPos pos in list)
         {
            if (pos.agent != null && CheckTime(pos.date) && pos.isGSM == 0)
            {
               if (path.ContainsKey(pos.agent))
                  path[pos.agent] += Ads2017.Coordutils.Distance(lat, lon, pos.latitude, pos.longitude);
               else
                  path.Add(pos.agent, 0);

               lat = pos.latitude;
               lon = pos.longitude;
            }
         }

         foreach (KeyValuePair<Agent, double> p in path)
         {
            Data d = new Data()
            {
               Distance = DistanceHuman((int)p.Value),
               Agent = p.Key.Name,
            };

            sumDistance += p.Value;
            gridData.Add(d);
         }

         SumDistance = DistanceHuman((int)sumDistance);
      }

      private bool CheckTime(DateTime date)
      {
         return date.TimeOfDay >= startTime.Time && date.TimeOfDay <= finishTime.Time;
      }

      public UIElement[] GetRefreshControls()
      {
         return new UIElement[] { btnHTML };
      }

      private class Data
      {
         public string Agent { get; set; }
         public string Distance { get; set; }
      }

      protected virtual void MakeReport()
      {
         DateTime s = (startDate.SelectedDate ?? DateTime.Now).Date.Add(startTime.Time);
         DateTime f = (finishDate.SelectedDate ?? DateTime.Now).Date.Add(finishTime.Time);

         string fileName = String.Format("gsmreport{0}.html", ++doc_number);
         string result = System.IO.Path.GetTempPath() + fileName;

         string html = "<html><head> " +
           "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">" +
           "</head><body>" +
              "<FONT FACE=\"Arial\">" +
              "<table cellpadding=5 CELLSPACING=0 border=1 BORDERCOLOR=\"#000000\">";
         html += String.Format("<H3>{0}</H3><p>", ((App)System.Windows.Application.Current).resource.GetString("distance_report"));
         html += String.Format("<FONT SIZE=\"2\">{2}: <b>{0} - {1}</b><br><br>",
            s.ToString("dd.MM.yyyy"), f.ToString("dd.MM.yyyy"),
            ((App)System.Windows.Application.Current).resource.GetString("range"));

         html += String.Format("<tr BGCOLOR=\"#CCCCCC\" ><td><FONT SIZE=\"2\"><b>{0}</b></td>" +
            "<td><FONT SIZE=\"2\"><b>{1}</b></td></tr>",
             ((App)System.Windows.Application.Current).resource.GetString("agent"),
             ((App)System.Windows.Application.Current).resource.GetString("mileage"));

         foreach (Data d in gridData)
         {
            html += string.Format("<tr><td>{0}</td><td>{1}</td></tr>",
               d.Agent, d.Distance);
         }

         html += "</table>";
         html += "Итого:" + DistanceHuman((int)sumDistance) + "<br>";
         html += String.Format("<SUB>{0} <a href=http://grsoft.ru/>http://grsoft.ru/</a></SUB>",
            ((App)System.Windows.Application.Current).resource.GetString("created_by_napoleon"));
         html += "</body></html>";

         using (StreamWriter sw = new StreamWriter(result))
         {
            sw.Write(html);
            sw.Flush();
         }

         OpenLink.NewWindow(String.Format("\"{0}\"", result));
      }

      private String DistanceHuman(int distance)
      {
         return FormatHelper.Instance.DistanceHuman(distance);
      }

      private void RefreshExecuted(object sender, ExecutedRoutedEventArgs e)
      {
         Refresh();
      }

      private void Refresh()
      {
         btnRefresh.Focus();
         DateTime s = (startDate.SelectedDate ?? DateTime.Now).Date.Add(startTime.Time);
         DateTime f = (finishDate.SelectedDate ?? DateTime.Now).Date.Add(finishTime.Time);

         Update.QueryList upd = new Update.QueryList();
         upd.Add(GPSPos.OBJECT_NAME,
            String.Format("\"date\" >= ToDate('{0:dd/MM/yyyy HH:mm:ss}') and \"date\" < ToDate('{1:dd/MM/yyyy HH:mm:ss}')" +
            " and \"isGSM\" = '0' and \"userid\" in ({2})", s, f, ManagerHelper.Instance.AgentsWhere(false)));

         Update.StdDataRefresh(upd, this);
      }

      public string SumDistance
      {
         get { return (string)GetValue(SumDistanceProperty); }
         set { SetValue(SumDistanceProperty, value); }
      }

      private void RibbonWindow_Loaded(object sender, RoutedEventArgs e)
      {
         Refresh();
      }

      private void Report_Click(object sender, RoutedEventArgs e)
      {
         ReportHelper rh = new ReportHelper();
         DistanceReportData arg = new DistanceReportData
         {
            start = (startDate.SelectedDate ?? DateTime.Now).Date.Add(startTime.Time),
            finish = (finishDate.SelectedDate ?? DateTime.Now).Date.Add(finishTime.Time),
            locale = System.Threading.Thread.CurrentThread.CurrentUICulture.ToString()
         };

         rh.DoReport("distance", arg);
      }
   }
}

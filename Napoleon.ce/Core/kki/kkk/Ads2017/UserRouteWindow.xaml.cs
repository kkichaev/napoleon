using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Ribbon;
using System.Windows.Input;
using System.Windows.Media;

namespace Ads2017
{
   public partial class UserRouteWindow : RibbonWindow, Update.IDataLoadProcess
   {
      private MapControlHelper mhc = new MapControlHelper();
      private const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{2:dd/MM/yyyy HH:mm:ss}') and \"{0}\" < ToDate('{3:dd/MM/yyyy HH:mm:ss}') and \"userid\" = '{1}'";
      private string htmlText = string.Empty;

      public UserRouteWindow()
      {
         InitializeComponent();

         start.Time = Properties.Settings.Default.WorkTimeStart;
         finish.Time = Properties.Settings.Default.WorkTimeFinish;

         routePointUseInterval.IsChecked = Properties.Settings.Default.RoutePointUseInterval;
         routePointInterval.Text = Properties.Settings.Default.RoutePoinInterval.ToString();
         routePointAccuracy.Text = Properties.Settings.Default.RoutePointAccuracy.ToString();

         datePicker.SelectedDate = DateTime.Now;

         distance.Content = DistanceText(0);
         taskPoints.OnItemClick = TaskItemClick;
      }

      private void TaskItemClick(object sender, object item)
      {
         try
         {
            browser.InvokeScript("showInfo", new object[] { ((ITaskTicket)item).Number });
         }
         catch (Exception)
         {
            
         }
      }

      private string DistanceText(double distance)
      {
         return FormatHelper.Instance.DistanceHuman((int)distance);
      }

      private void RefreshExecuted(object sender, ExecutedRoutedEventArgs e)
      {
         Refresh();
      }

      private string GpsWhere(string userid, DateTime start, DateTime finish)
      {
         string result = string.Format(COMMON_FILTER_STR, "date", userid, start, finish);
         string f = gpsFilter.SelectionBoxItem.ToString();

         if (!f.Equals("Все"))
            result = string.Format("{0} and \"isGSM\" = {1}", result, f.Equals("GSM") ? 1 : 0);

         return result;
      }

      private void Refresh()
      {
         if (rgAgents.SelectedItem is Agent a)
         {
            DateTime date = datePicker.SelectedDate ?? DateTime.Now;
            DateTime s = date.Add(start.Time);
            DateTime f = date.Add(finish.Time);

            Update.QueryList upd = new Update.QueryList();
            upd.Add(GPSPos.OBJECT_NAME, GpsWhere(a.id, s, f));
            upd.Add(TaskQuery.OBJECT_NAME_MANAGER, TaskQueryWhere(a.id, s, f));
            upd.Add(UserLog.OBJECT_NAME, UserLogWhere(a.id, s, f));

            Update.StdDataRefresh(upd, this);
         }
      }

      private static string UserLogWhere(string userid, DateTime start, DateTime finish)
      {
         return string.Format(COMMON_FILTER_STR, "date", userid, start, finish) + " and \"category\" = 1";
      }

      private static string TaskQueryWhere(string userid, DateTime start, DateTime finish)
      {
         return string.Format("{0:dd/MM/yyyy HH:mm:ss};{1:dd/MM/yyyy HH:mm:ss};{2}", start, finish, userid);
      }

      public void DoLoadData(Update.UpdateResult res)
      {
         MapData data = CreateData(res);
         distance.Content = DistanceText(data.distance);

         ObservableCollection<MapObj> list = IdxData(data);
         taskPoints.Adapter = new ListAdapter(list);

         ObservableCollection<UserLog> log = new ObservableCollection<UserLog>();
         foreach (UserLog u in res.GetList<UserLog>(UserLog.OBJECT_NAME))
            log.Add(u);

         gridLog.ItemsSource = log;

         string map = rgMaps.SelectedItem.ToString();
         htmlText = mhc.CreateMap(map, data);
         browser.NavigateToString(htmlText);
      }

      private class ListAdapter : ListViewAdapter
      {
         List<MapObj> data = new List<MapObj>();
         private TaskTicketFactory factory = new TaskTicketFactory();

         public ListAdapter(ICollection<MapObj> data)
         {
            this.data.AddRange(data);
         }

         public override int Count
         {
            get { return data.Count; }
         }

         public override object GetItem(int position)
         {
            return data[position];
         }

         public override UserControl GetView(int position)
         {
            return factory.CreateTicket((MapObj)GetItem(position));
         }
      }

      private void RibbonWindow_Loaded(object sender, RoutedEventArgs e)
      {
         InitAgentsList();
         mhc.InitControl(rgMaps, rgcMaps);
         Refresh();
      }

      private void InitAgentsList()
      {
         List<Agent> list = new List<Agent>();
         Agent sel = null;

         foreach (object o in Update.GetList(Agent.OBJECT_NAME))
         {
            Agent a = (Agent)o;

            if (ManagerHelper.Instance.HaveAgent(a.id) && a.hidden == 0)
            {
               list.Add((Agent)o);

               if (sel == null && UserId != null && a.id.Equals(UserId))
                  sel = a;
            }
         }

         list.Sort((x, y) => { return x.Name.CompareTo(y.Name); });

         rgcAgents.ItemsSource = list;

         if (sel != null)
            rgAgents.SelectedItem = sel;
         else if (rgcAgents.Items.Count > 0)
            rgAgents.SelectedItem = rgcAgents.Items[0];
      }

      public bool IsNearest(GPSPos l1, GPSPos l2)
      {
         const int MAX_DIST = 100;
         const int MAX_SPEED = 50;
         double dst = Coordutils.Distance(l1.latitude, l1.longitude, l2.latitude, l2.longitude);
         return dst < MAX_DIST && l1.speed < MAX_SPEED && l2.speed < MAX_SPEED;
      }

      public bool IsStopEnough(GPSPos l1, GPSPos l2)
      {
         const int MIN = 10;
         return ((l2.date.Ticks - l1.date.Ticks) / (10000000L * 60 * MIN) > 0);
      }

      private List<object> CollectStops(List<GPSPos> points)
      {
         List<object> result = new List<object>();

         if (points.Count > 0)
         {
            int ctr = 0;
            while (ctr < points.Count - 1)
            {
               GPSPos l1 = points[ctr++], l2 = null;
               while (ctr < points.Count)
               {
                  GPSPos cp = points[ctr++];
                  if (!IsNearest(l1, cp))
                     break;
                  l2 = cp;
               }

               if (l2 != null)
               {
                  if (IsStopEnough(l1, l2))
                  {
                     StopPoint sp = new StopPoint();
                     sp.date = l1.date.ToUniversalTime();
                     sp.endTime = l2.date;
                     sp.latitude = l1.latitude;
                     sp.longitude = l1.longitude;

                     Location loc = new Location(l1.latitude, l1.longitude);
                     sp.address = loc.GetAddress();

                     result.Add(sp);
                  }
                  else
                     ctr--;
               }
               else
                  ctr--;
            }
         }

         return result;
      }

      private MapData CreateData(Update.UpdateResult res)
      {
         MapData data = new MapData();
         List<GPSPos> points = new List<GPSPos>();
         GPSPos pp = null;
         
         foreach (GPSPos p in res.GetList<GPSPos>(GPSPos.OBJECT_NAME))
         {
            points.Add(p);

            if (pp != null)
               data.distance += Coordutils.Distance(pp.latitude, pp.longitude, p.latitude, p.longitude);

            pp = p;
         }

         data.points.AddRange(points);
         data.stops.AddRange(CollectStops(points));

         foreach (TaskQuery t in res.GetList<TaskQuery>(TaskQuery.OBJECT_NAME_MANAGER))
         {
            TaskPoint p = new TaskPoint(t);
            p.date = t.start;

            if (t.solution == Task.NEW)
            {
               Location loc = Route.GetLocation(p.task.address);

               if (loc != null && loc.Latitude != 0)
               {
                  p.task.latitude = loc.Latitude;
                  p.task.longitude = loc.Longitude;
                  p.date = t.start;
                  data.pendings.Add(p);
               }
            }
            else
            {
               Location check = new Location(p.task.latitude, p.task.longitude);
               p.factAddress = GetFactAddress(check);
               p.isNearest = Route.IsNearestToOrg(p.Address, ref check, p.date, Convert.ToDouble(routePointAccuracy.Text), points);
               data.executed.Add(p);
            }
         }

         if (routePointUseInterval.IsChecked ?? false)
            data.stepoints.AddRange(CollectStepPoint(points, int.Parse(routePointInterval.Text)));

         return data;
      }

      private string GetFactAddress(Location check)
      {
         return check.GetAddress();
      }

      private List<object> CollectStepPoint(List<GPSPos> route, int min)
      {
         List<object> result = new List<object>();

         int i = 1;
         GPSPos pp = null;

         foreach (GPSPos loc in route)
         {
            if (pp == null || ((loc.date - pp.date).TotalMinutes >= min) && (loc.latitude != pp.latitude || loc.longitude != pp.longitude))
            {
               MapObj rp = new MapObj();
               rp.idx = i;
               rp.latitude = loc.latitude;
               rp.longitude = loc.longitude;
               rp.speed = loc.speed;
               rp.date = loc.date.ToUniversalTime();

               result.Add(rp);
               i++;
               pp = loc;
            }
         }

         return result;
      }

      private ObservableCollection<MapObj> IdxData(MapData data)
      {
         List<MapObj> list = new List<MapObj>();
         data.stops.ForEach((x) => { list.Add((MapObj)x); });
         data.executed.ForEach((x) => { list.Add((MapObj)x); });
         data.pendings.ForEach((x) => { list.Add((MapObj)x); });
         list.Sort((x, y) => { return x.date.CompareTo(y.date); });

         ObservableCollection<MapObj> result = new ObservableCollection<MapObj>(list);

         int idx = 1;

         foreach (MapObj m in result)
         {
            m.idx = idx++;
            m.accuracy = Convert.ToDouble(routePointAccuracy.Text);
         }

         return result;
      }


      public UIElement[] GetRefreshControls()
      {
         return new UIElement[] { btnRefresh };
      }

      private void OpenExecuted(object sender, ExecutedRoutedEventArgs e)
      {
         try { 
            String tmpPath = Path.GetTempPath();
            StringBuilder sb = new StringBuilder();
            File.WriteAllText(tmpPath + "route.html", htmlText);

            string agentName = (rgAgents.SelectedItem as Agent ?? new Agent()).Name;
            DateTime date = datePicker.SelectedDate ?? DateTime.Now;
            string dst = distance.Content.ToString();

            sb.Length = 0;
            sb.AppendLine("<html>");
            sb.AppendLine("<head>");
            sb.AppendLine("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'>");
            sb.AppendLine("</head>");
            sb.Append("Агент: ").Append(agentName).Append("<br>");
            sb.Append("Дата: ").Append(date.ToString("dd.MM.yyyy")).Append("<br>");
            sb.Append(dst);
            sb.Append("<table width=\"100%\" border=\"1\">");
            sb.Append("<tr>");
            sb.Append("<td>Номер</td>");
            sb.Append("<td>Клиент</td>");
            sb.Append("<td>Время план</td>");
            sb.Append("<td>Время факт</td>");
            sb.Append("<td>Адрес</td>");
            sb.Append("<td>Факт адрес</td>");
            sb.Append("</tr>");

            ListViewAdapter adapter = taskPoints.Adapter;

            if (adapter != null)
                for (int i = 0; i < adapter.Count; i++)
                {
                   TaskPoint p = adapter.GetItem(i) as TaskPoint;

                   if (p != null)
                   {
                      sb.Append("<tr>");
                      sb.Append("<td>").Append(p.Num).Append("</td>");
                      sb.Append("<td>").Append(p.Client).Append("</td>");
                      sb.Append("<td>").Append(p.TimePlan).Append("</td>");
                      sb.Append("<td>").Append(p.TimeFact).Append("</td>");
                      sb.Append("<td>").Append(p.Address).Append("</td>");
                      sb.Append("<td>").Append(p.FactAddress).Append("</td>");
                      sb.Append("</tr>");
                   }
                }

            sb.AppendLine("</html>");
            File.WriteAllText(tmpPath + "data.html", sb.ToString());

            sb.Length = 0;
            sb.AppendLine("<html>");
            sb.AppendLine("<head>");
            sb.AppendLine("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
            sb.AppendLine("<title>Маршрут</title>");
            sb.AppendLine("</head>");
            sb.AppendLine("<frameset rows=\"*\" cols=\"50%,*\">");
            sb.AppendLine("<frame src=\"route.html\" name=\"routeFrame\" scrolling=\"yes\">");
            sb.AppendLine("<frame src=\"data.html\" name=\"dataFrame\" scrolling=\"yes\">");
            sb.AppendLine("</frameset>");
            sb.AppendLine("</html>");
            File.WriteAllText(tmpPath + "report.html", sb.ToString());

            OpenLink.NewWindow(String.Format("\"{0}\"", tmpPath + "report.html"));
         }
         catch (Exception ex)
         {
            MessageBox.Show(ex.Message + '\n' + ex.StackTrace.ToString() );
         }
      }

      private void MessageExecuted(object sender, ExecutedRoutedEventArgs e)
      {
         if (rgAgents.SelectedItem is Agent a)
         {
            MessageWindow w = new MessageWindow();
            w.Target = a;
            w.Show();
         }
      }

      public string UserId { get; set; }
   }
}

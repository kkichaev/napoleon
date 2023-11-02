using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Globalization;
using System.IO;
using System.Threading;
using System.Windows.Forms.VisualStyles;

namespace GRSoft.NapoleonManager
{
   public partial class FmRoute : Form
   {
      private DataSet<DateTime, GPSPos> dsGPSPos;
      private DataSet<int, UserLog> dsUserLog;
      private DataSet<string, TaskQuery> dsTask;
      private MapHelper mapHelper;

      private FmRouteSetting setting = null;
      internal const int DEF_NUM_INTERVAL = 15;

      public FmRoute(string idAgent, DateTime date)
      {
         InitializeComponent();
         InitDatasets();
         Init(idAgent, date);

         toolTip1.SetToolTip(cbRoadPoints, "Показывать промежуточные точки на маршруте");
         toolTip1.SetToolTip(numInterval, "Показать следующую точку не раньше заданного интервала в минутах");
         
         foreach(UserLog.ActionInfo ai in UserLog.LogActions)
            cbActionFilter.Items.Add(new CheckComboBoxItem(ai, true));

         cbActionFilter.SelectedIndex = 0;
         this.cbActionFilter.SelectedIndexChanged += new System.EventHandler(this.cbActionFilter_SelectedIndexChanged);

         mapHelper = new MapHelper();

         grid.AutoGenerateColumns = false;
         dgvLog.AutoGenerateColumns = false;
      }

      private void InitDatasets()
      {
         dsGPSPos = DataModule.Get("GPSPos") == null ? new DataSet<DateTime, GPSPos>("GPSPos") : (DataSet<DateTime, GPSPos>)DataModule.Get("GPSPos");
         dsUserLog = DataModule.Get("UserLog") == null ? new DataSet<int, UserLog>("UserLog") : (DataSet<int, UserLog>)DataModule.Get("UserLog");
         dsTask = new DataSet<string, TaskQuery>(TaskQuery.OBJECT_NAME_MANAGER);
      }

      private void AdjustFilterForDS(string idAgent, DateTime date)
      {
         const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy} {3:HH:mm:ss}') and \"{0}\" <= ToDate('{1:dd/MM/yyyy} {4:HH:mm:ss}') and \"userid\" = '{2}'";
         const string GPS_FILTER = COMMON_FILTER_STR + " and \"isGSM\" = '{5}'";

         if (((RouteFilterItem) cbFilter.SelectedItem) is AllItem)
            dsGPSPos.Filter = string.Format(COMMON_FILTER_STR, "date", date, idAgent, timeBegin.Value, timeEnd.Value);
         else
            dsGPSPos.Filter = string.Format(GPS_FILTER, "date", date,
              idAgent, timeBegin.Value, timeEnd.Value, ((RouteFilterItem)cbFilter.SelectedItem).Code);

         const string TASK_FILTER = "{0:dd/MM/yyyy} {1:HH:mm:ss};{0:dd/MM/yyyy} {2:HH:mm:ss};{3}";
         dsTask.Filter = string.Format(TASK_FILTER, date, timeBegin.Value, timeEnd.Value, idAgent);
         dsUserLog.Filter = String.Format(COMMON_FILTER_STR, "date", date, idAgent, timeBegin.Value, timeEnd.Value) + " and \"category\" = 1";
      }

      private void Init(string idAgent, DateTime date)
      {
         lbDistance.Text = string.Empty;
         
         foreach (Agent agent in Agents.GetDataSet().Data)
            cbAgents.Items.Add(new AgentItem(agent));

         cbAgents.Sorted = true;

         if (idAgent.Trim().Length == 0)
         {
            if (cbAgents.Items.Count > 0)
               cbAgents.SelectedIndex = 0;
         }
         else
            SelectAgentFromId(idAgent);

         dtpDate.Value = date;
      }

      private void UpdateDataSets(string idAgent, DateTime date)
      {
         btnRefresh.Enabled = false;
         AdjustFilterForDS(idAgent, date);
         List<IDataSet> list = new List<IDataSet>();
         
         list.Add(dsGPSPos);
         list.Add(dsTask);
         list.Add(dsUserLog);

         OrgLocations ol = OrgLocations.GetDataSet();
            list.Add(ol);

         FmWait.StdDataRefresh(this, list, DoLoadData, btnRefresh);
      }

      void DoLoadData()
      {
         List<Location> route = GetRoute();
         lbDistance.Text = String.Format("Путь: {0} м", GetFullDistance(route));
         Data data = CreateData();
         List<MapObj> list = IdxData(data);
         grid.DataSource = list;

         List<UserLog> log = new List<UserLog>();
         log.AddRange(dsUserLog.Values);

         dgvLog.DataSource = log;

         string map = cbMap.SelectedItem.ToString();
         wb.DocumentText = mapHelper.CreateMap(map, "traceroute", data);
      }

      private List<MapObj> IdxData(Data data)
      {
         List<MapObj> list = new List<MapObj>();
         data.stops.ForEach((x) => { list.Add((MapObj)x); });
         data.executed.ForEach((x) => { list.Add((MapObj)x); });
         data.pendings.ForEach((x) => { list.Add((MapObj)x); });
         list.Sort((x, y) => { return x.date.CompareTo(y.date); });

         int idx = 1;

         foreach (MapObj m in list)
         {
            m.idx = idx++;
            m.accuracy = Convert.ToDouble(distAccurate.Value);
         }

         return list;
      }

      private Data CreateData()
      {
         List<GPSPos> points = new List<GPSPos>();
         points.AddRange(dsGPSPos.Values);
         Data data = new Data();
         data.points.AddRange(points);
         data.stops.AddRange(CollectStops(points));

         foreach (TaskQuery t in dsTask.Values)
         {
            TaskPoint p = new TaskPoint(t);
            p.date = t.finishexec;

            if (t.solution == Task.NEW)
            {
               Location loc = Route.GetLocation(p.task.address);
               p.task.latitude = loc.Latitude;
               p.task.longitude = loc.Longitude;
               p.date = t.created;
               data.pendings.Add(p);
            }
            else
            {
               Location check = new Location(p.task.latitude, p.task.longitude);
               p.factAddress = GetFactAddress(check);
               p.isNearest = Route.IsNearestToOrg(p.Address, ref check, p.date, Convert.ToDouble(distAccurate.Value), dsGPSPos);
               data.executed.Add(p);
            }
         }

         if (cbRoadPoints.Checked)
            data.stepoints.AddRange(CollectStepPoint(points, (int)numInterval.Value));

         return data;
      }

      private string GetFactAddress(Location check)
      {
         return check.GetAddress();
      }
      public bool IsNearest(GPSPos l1, GPSPos l2)
      {
         const int MAX_DIST = 100;
         const int MAX_SPEED = 50;
         double dst =  Coordutils.Distance(l1.latitude, l1.longitude, l2.latitude, l2.longitude);
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
                     sp.date = l1.date;
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


      private int GetFullDistance(List<Location> route)
      {
         double result = 0;
         int i = 0;
         
         while (i  < route.Count - 1)
         {
            result += Coordutils.Distance(route[i].Latitude,
                  route[i].Longitude, route[i + 1].Latitude, route[i + 1].Longitude);
            i++;
         }

         return (int)result;
      }

      private void SelectAgentFromId(string AgentId)
      {
         foreach (AgentItem a in cbAgents.Items)
         {
            if (a.id == AgentId)
            {
               cbAgents.SelectedItem = a;
            }
         }
      }

      private List<Location> GetRoute()
      {
         List<Location> result = new List<Location>();
         foreach (KeyValuePair<DateTime, GPSPos> kv in dsGPSPos)
         {
            GPSPos pos = kv.Value;
            if( pos.latitude != 0 || pos.longitude != 0 )
               result.Add(new Location(pos.latitude, pos.longitude, pos.isGSM == 1, pos.speed, pos.date));
         }
         return result;
      }

      class LogItem : IComparable<LogItem>
      {
         UserLog item;
         public LogItem(UserLog item)
         {
            this.item = item;
         }

         public string Event { get { return item.userAction; } }
         public DateTime Date { get { return item.date; } }

         #region IComparable<LogItem> Members

         public int CompareTo(LogItem other) { return item.date.CompareTo(other.item.date); }

         #endregion
      }

      private void FillLogGrid()
      {
         dgvLog.SuspendLayout();
         try
         {
            //dgvLog.Rows.Clear();

            Dictionary<int, bool> check = new Dictionary<int, bool>();
            foreach (CheckComboBoxItem ci in cbActionFilter.Items)
               check[(ci.Item as UserLog.ActionInfo).action] = true;

            List<LogItem> src = new List<LogItem>();
            foreach (UserLog userLog in dsUserLog.Data)
            {
               if (check.ContainsKey(userLog.action))
                  src.Add(new LogItem(userLog));
                  //dgvLog.Rows.Add(new object[] { userLog.date.ToString("HH:mm"), userLog.userAction });
            }
            src.Sort();
            dgvLog.DataSource = src;
         }
         finally
         {
            dgvLog.ResumeLayout();
         }
      }

      private void grid_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         e.CellStyle.BackColor = Color.White;
         e.CellStyle.ForeColor = Color.Black;

         MapObj mo  = grid.Rows[e.RowIndex].DataBoundItem as MapObj;
         if (mo != null && mo.isNearest == false)
            e.CellStyle.ForeColor = Color.Red;

         if (mo != null && mo is TaskPoint)
         {
            TaskPoint dp = (TaskPoint)mo;

            if (dp.task is TaskQuery)
            { 
               TaskQuery t = (TaskQuery)dp.task;
               e.CellStyle.BackColor = TaskHelper.BkgItemColor(t.solution, 255);

               Console.WriteLine(e.CellStyle.BackColor.ToString());
            }
         }
      }

      public class Data
      {
         public List<GPSPos> points = new List<GPSPos>();
         public List<object> stops = new List<object>();
         public List<object> executed = new List<object>();
         public List<object> pendings = new List<object>();
         public List<object> stepoints = new List<object>();
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
               rp.date = loc.date;

               result.Add(rp);
               i++;
               pp = loc;
            }
         }

         return result;
      }

      private List<KeyValuePair<double, double>> RemoveExcessPoints(List<KeyValuePair<double, double>> coords)
      {
         const int MAXLISTSIZE = 98;
         
         int i = 0;
         int rm_index = 0;

         while (coords.Count > MAXLISTSIZE)
         {
            int min_length = int.MaxValue;

            for ( i = 0; i < coords.Count - 3; i++)
            {
               int length = (int)Coordutils.Distance(coords[i].Key, coords[i].Value, coords[i + 3].Key, coords[i + 3].Value);

               if (length < min_length)
               {
                  rm_index = i + 1;
               }

            }

            coords.RemoveAt(rm_index);
         }

         return coords;
      }

      private void FmRoute_FormClosing(object sender, FormClosingEventArgs e)
      {
         wb.Dispose();
      }

      private void CheckPoint(VisitQueueItem vi, Org org)
      {
         if (org == null || org.Address == null || org.Address.Length == 0)
            return;

         Location l = Route.GetLocation(org);
         if (l != null)
         {
            vi.address = org.Address;

            if (vi.longitude != 0 || vi.latitude != 0)
            {
               Location check = new Location(vi.latitude, vi.longitude);
               if( Route.IsNearestToOrg(org, ref check, vi.startTime, Convert.ToDouble(distAccurate.Value), dsGPSPos))
               {
                  vi.latitude = check.Latitude;
                  vi.longitude = check.Longitude;
               } else
               {
                  vi.address = org.Address;
                  vi.factAddress = check.GetAddress();
                  vi.outOfRange = true;
               }
            }
         }
      }

      private Location FindNearesRoutePoint(DateTime dateTime)
      {
         Location check = null;
         List<DateTime> keys = new List<DateTime>(dsGPSPos.Keys);
         keys.Sort();
         foreach(DateTime key in keys)
         {
            if( key.CompareTo(dateTime) > 0)
            {
               GPSPos pos = dsGPSPos[key];
               check = new Location(pos.latitude, pos.longitude);
               break;
            }
         }

         return check;
      }

      //Создать список посещений, отсортированный по дате
      private List<VisitQueueItem> MakeVisitQueue(List<Location> route)
      {
         List<VisitQueueItem> result = new List<VisitQueueItem>();

         foreach (TaskQuery t in dsTask.Data)
         {
            if (t.solution != Task.NEW && t.solution != Task.APPLY && t.solution != Task.REJECT)
            {
               VisitQueueItem vi = new VisitQueueItem(t, new VisitType(ObjType.TObjType.Task));
               CheckPoint(vi, t.org);
               result.Add(vi);
            }
         }

         AddStopping(result, route);
//#endif
         result.Sort(new CmpVisitQueueItem());

         foreach (VisitQueueItem item in result)
         {
            if (item.latitude == 0 || item.longitude == 0)
            {
               GPSPos nearestPos = GetBestLocation(item);

               if (nearestPos != null)
               {
                  item.latitude = nearestPos.latitude;
                  item.longitude = nearestPos.longitude;
               }
            }
         }

         return result;
      }

      private GPSPos GetBestLocation(VisitQueueItem item)
      {
         GPSPos result = null;
         double milliseconds = 5 * 60 * 1000; // 5 minutes
 
         foreach (KeyValuePair<DateTime, GPSPos> kv in dsGPSPos)
         {
            double ms = Math.Abs((kv.Key - item.startTime).TotalMilliseconds);
            if (ms < milliseconds)
            {
               result = kv.Value;
               milliseconds = ms;
            }
         }

         return result;
      }

      public bool IsNearest(Location l1, Location l2)
      {
         return (NapoleonManager.Location.Distance(l1, l2) < 100 && l1.speed < 5 && l2.speed < 5);
      }

      public bool IsStopEnough(Location l1, Location l2)
      {
         return ((l2.date.Ticks - l1.date.Ticks) / (10000000L * 60 * 10) > 0);
      }

      private void AddStopping(List<VisitQueueItem> result, List<Location> route)
      {
         if (route.Count == 0)
            return;

         int ctr = 0;
         while (ctr < route.Count - 1)
         {
            Location l1 = route[ctr++], l2 = null;
            while (ctr < route.Count)
            {
               Location cp = route[ctr++];
               if (!IsNearest(l1, cp))
                  break;
               l2 = cp;
            }

            if (l2 != null)
            {
               if (IsStopEnough(l1, l2))
               {
                  VisitQueueItem vi = new VisitQueueItem(l1.date, null, l1.Latitude, l1.Longitude, new VisitType("Остановка"));
                  vi.address = l1.GetAddress();
                  vi.endTime = l2.date;
                  result.Add(vi);
               }
               else
                  ctr--;
            }
            else
               ctr--;
         }
      }

      private void cbAgents_MeasureItem(object sender, MeasureItemEventArgs e)
      {
         e.ItemHeight = 14;
      }

      private void cbAgents_DrawItem(object sender, DrawItemEventArgs e)
      {
         e.DrawBackground();
         e.Graphics.DrawString((sender as ComboBox).Items[e.Index].ToString(), e.Font,
            System.Drawing.Brushes.Black, new RectangleF(e.Bounds.X, e.Bounds.Y, e.Bounds.Width, e.Bounds.Height));
      }

      private void tsbMessage_Click(object sender, EventArgs e)
      {
         Agent agent = new Agent();
         agent.id = (cbAgents.SelectedItem as AgentItem).id;
         agent.name = (cbAgents.SelectedItem as AgentItem).name;
         FmMessage.MessageShow(agent);
      }

      private void FmRoute_Load(object sender, EventArgs e)
      {
         setting = BaseFormSetting<FmRouteSetting>.Load();
         
         cbFilter.Items.Clear();
         cbFilter.Items.Add(new AllItem());
         cbFilter.Items.Add(new GSMItem());
         cbFilter.Items.Add(new GPSItem());

         int selected = 0;

         if(setting.filter != null && setting.filter.Trim().Length > 0)
            for (int i = 0; i < cbFilter.Items.Count; i++)
            {
               RouteFilterItem item = cbFilter.Items[i] as RouteFilterItem;

               if (item != null 
                  && item.ToString().Equals(setting.filter))
               {
                  selected = i;
                  break;
               }
            }

         cbFilter.SelectedIndex = selected;
         cbRoadPoints.Checked = setting.roadPoints;
         numInterval.Value = setting.numInterval;

         timeBegin.Value = setting.timeBegin;
         timeEnd.Value = setting.timeEnd;

         mapHelper.InitControl(cbMap);
         btnRefresh.PerformClick();
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         new Thread(new ParameterizedThreadStart(delegate(object o)
         {
            BeginInvoke(new EmptyParamHandler(delegate() { FmWait.ShowForm(this, true); }));
            RouteExcelReport rpt = new RouteExcelReport();
            rpt.Build(grid);
            rpt.Show();
            BeginInvoke(new EmptyParamHandler(delegate() { FmWait.CloseForm(); }));
         })).Start();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         AgentItem a = cbAgents.SelectedItem as AgentItem;
         if(a != null)
            UpdateDataSets(a.id, dtpDate.Value.Date);
      }

      private void FmRoute_FormClosed(object sender, FormClosedEventArgs e)
      {
         setting.filter = cbFilter.SelectedItem.ToString();
         setting.roadPoints = cbRoadPoints.Checked;
         setting.numInterval = (int)numInterval.Value;
         setting.timeBegin = timeBegin.Value;
         setting.timeEnd = timeEnd.Value;
         setting.Save();
      }

      private string CellValue(DataGridViewCell cell)
      {
         if (cell.Value == null || 
               cell.Value.ToString().Trim().Length == 0)
            return "&nbsp;";
         else
            return cell.Value.ToString().Trim();
      }

      private void btnHtml_Click(object sender, EventArgs e)
      {
         String tmpPath = Path.GetTempPath();
         StringBuilder sb = new StringBuilder();
         Invoke(new EmptyParamHandler(delegate() { 
            sb.Append(wb.DocumentText); 
         }));
         
         File.WriteAllText(tmpPath + "route.html", sb.ToString());

         sb.Length = 0;
         sb.AppendLine("<html>");
         sb.AppendLine("<head>");
         sb.AppendLine("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'>");
         sb.AppendLine("</head>");
         sb.Append("Агент: ").Append((cbAgents.SelectedItem as AgentItem).name).Append("<br>");
         sb.Append("Дата: ").Append(dtpDate.Value.ToString("dd.MM.yyyy")).Append("<br>");
         sb.Append(lbDistance.Text);
         sb.Append("<table width=\"100%\" border=\"1\">");
         sb.Append("<tr>");
         sb.Append("<td>Номер</td>");
         sb.Append("<td>Организация</td>");
         sb.Append("<td>Действие</td>");
         sb.Append("<td>Время</td>");
         sb.Append("<td>Сумма</td>");
         sb.Append("<td>Продолжительность</td>");
         sb.Append("<td>Адрес клиента</td>");
         sb.Append("<td>Адрес документа</td>");
         sb.Append("</tr>");

         foreach (DataGridViewRow row in grid.Rows)
         {
            sb.Append("<tr>");
            sb.Append("<td>").Append(CellValue(row.Cells[0])).Append("</td>");
            sb.Append("<td>").Append(CellValue(row.Cells[1])).Append("</td>");
            sb.Append("<td>").Append(CellValue(row.Cells[2])).Append("</td>");
            sb.Append("<td>").Append(CellValue(row.Cells[3])).Append("</td>");
            sb.Append("<td>").Append(CellValue(row.Cells[4])).Append("</td>");
            sb.Append("<td>").Append(CellValue(row.Cells[5])).Append("</td>");
            sb.Append("<td>").Append(CellValue(row.Cells[6])).Append("</td>");
            sb.Append("<td>").Append(CellValue(row.Cells[7])).Append("</td>");
            sb.Append("</tr>");
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

      private void tabControl1_SelectedIndexChanged(object sender, EventArgs e)
      {
         bool visible = tabControl1.SelectedTab == tpLog;
         tsLogPanel.Visible = visible;
         cbActionFilter.Visible = visible;
      }

      private void cbActionFilter_DrawItem(object sender, DrawItemEventArgs e)
      {
         // make sure the index is valid (sanity check)
         if (e.Index == -1)
         {
            return;
         }

         // test the item to see if its a CheckComboBoxItem
         if (!(cbActionFilter.Items[e.Index] is CheckComboBoxItem))
         {
            // it's not, so just render it as a default string
            e.Graphics.DrawString(
               cbActionFilter.Items[e.Index].ToString(),
               cbActionFilter.Font,
               Brushes.Black,
               new Point(e.Bounds.X, e.Bounds.Y));
            return;
         }

         // get the CheckComboBoxItem from the collection
         CheckComboBoxItem box = (CheckComboBoxItem)cbActionFilter.Items[e.Index];

         // render it
         //CheckBoxRenderer.RenderMatchingApplicationState = true;

         int GlyphSize = CheckBoxRenderer.GetGlyphSize(e.Graphics, CheckBoxState.CheckedNormal).Width;
         Rectangle r = new Rectangle(e.Bounds.Left + GlyphSize, e.Bounds.Top, e.Bounds.Width - GlyphSize, e.Bounds.Height);
         CheckBoxRenderer.DrawCheckBox(e.Graphics, new Point(e.Bounds.X, e.Bounds.Y), r, box.Text,
            cbActionFilter.Font, TextFormatFlags.Left, (e.State & DrawItemState.Focus) != 0, box.CheckState ? CheckBoxState.CheckedNormal : CheckBoxState.UncheckedNormal);
      }

      private void cbActionFilter_SelectedIndexChanged(object sender, EventArgs e)
      {
         CheckComboBoxItem item = (CheckComboBoxItem)cbActionFilter.SelectedItem;
         item.CheckState = !item.CheckState;
         FillLogGrid();
      }

      private void grid_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         if (e.RowIndex != -1)
         {
            MapObj u = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as MapObj;
            if (u != null && wb.Document != null)
            {
               wb.Document.InvokeScript("showInfo", new object[] { u.Num });
            }
         }
      }
   }

   public class CheckComboBoxItem
   {
      public CheckComboBoxItem(object item, bool initialCheckState)
      {
         _checkState = initialCheckState;
         this.item = item;
      }

      private bool _checkState = false;
      /// <summary>
      /// Gets the check value (true=checked)
      /// </summary>
      public bool CheckState
      {
         get { return _checkState; }
         set { _checkState = value; }
      }

      private object item;
      /// <summary>
      /// Gets the label of the check box
      /// </summary>
      public string Text
      {
         get { return item == null ? "" : item.ToString(); }
      }

      public object Item { get { return item; } }

      private object _tag = null;
      /// <summary>
      /// User defined data
      /// </summary>
      public object Tag
      {
         get { return _tag; }
         set { _tag = value; }
      }
      /// <summary>
      /// This is used to keep the edit control portion of the combo box consistent
      /// </summary>
      /// <returns></returns>
      public override string ToString()
      {
         return "Фильтр событий";
      }
   }
   
   public abstract class RouteFilterItem
   {
      string caption;
      int code;

      public RouteFilterItem(string caption, int code)
      {
         this.caption = caption;
         this.code = code;
      }

      public int Code { get { return code; } }
      public override string ToString()
      {
         return caption;
      }
   }

   class AllItem : RouteFilterItem
   {
      public AllItem():base("Все",-1){ }
   }

   class GPSItem : RouteFilterItem
   {
      public GPSItem() : base("GPS", 0) { }
   }

   class GSMItem : RouteFilterItem
   {
      public GSMItem() : base("GSM", 1) { }
   }

   class RouteExcelReport : Excel
   {
      public void Build(DataGridView grid)
      {
         SetValue(1, 1, "№");
         SetValue(1, 2, "Организация");
         SetValue(1, 3, "Действие"); 
         SetValue(1, 4, "Время");
         SetValue(1, 5, "Сумма");
         SetValue(1, 6, "Продолжительность");
         SetValue(1, 7, "Адрес клиента");
         SetValue(1, 8, "Адрес документа");
         SetValue(1, 9, "НП");
         SetValue(1, 10, "Район");
         SetValue(1, 11, "Область");

         int i = 2;

         foreach (DataGridViewRow row in grid.Rows)
         {
            for(int c = 0; c < 10; c++)
               SetValue(i, c + 1, row.Cells[c].Value == null ? string.Empty : 
                  row.Cells[c].Value.ToString());
            
            i++;
         }
      }

      public void Show()
      {
         Visible = true;
      }
   }

   [Serializable]
   class FmRouteSetting : BaseFormSetting<FmRouteSetting>
   {
      public string filter = string.Empty;
      public int numInterval = FmRoute.DEF_NUM_INTERVAL;
      public bool roadPoints = false;
      public DateTime timeBegin = new DateTime(1980, 1, 1, 0, 0, 0);
      public DateTime timeEnd = new DateTime(1980, 1, 1, 23, 59, 0);
   }
}
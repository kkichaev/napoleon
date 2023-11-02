using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Globalization;
using GRSoft.NapoleonManager.Maps;
using System.IO;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Reports.Excel;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   //Если для файлов png вызывается другая программа, а в Наполеоне выдает "запрос отменен"
   //Надо посмотреть в панель управления->свойство папки->типы файлов 
   //и удалить регистрацию для файлов PNG

   public partial class FmRoute : Form
   {
      //Необходимые наборы данных
      private DataSet<DateTime, GPSPos> dsGPSPos;
      private DataSet<int, VisitInfo> dsVisit;
      private DataSet<int, OrgRemnants> dsOrgRemnants;
      private DataSet<int, Order> dsOrder;
      private DataSet<string, Region> dsRegion;
      private DataSet<string, Region1> dsRegion1;
      private DataSet<string, Region2> dsRegion2;


#if HappyLand
      private DataSet<int, ScriptDoc> dsScriptDoc;
#endif
      private DataSet<int, PKO> dsPKO;
      private Agents dsAgents;
      private DataSet<int, UserLog> dsUserLog;

      private bool showMap = false;

      //private const string YANDEX_KEY = "http://static-maps.yandex.ru/1.x/?l=sat,skl&key=ANzXmEoBAAAAkjQFPwIAQphddQGLBfkqE3qCI3Vo8OCM8yYAAAAAAAAAAACYpxbA0OaEdJRuAHC50onaXfYLBQ==&";
      //private const string YANDEX_KEY = "http://static-maps.yandex.ru/1.x/?key=ANzXmEoBAAAAkjQFPwIAQphddQGLBfkqE3qCI3Vo8OCM8yYAAAAAAAAAAACYpxbA0OaEdJRuAHC50onaXfYLBQ==&";

      internal FmRoute(string idAgent, DateTime date)
      {
         InitializeComponent();
         InitDatasets();
         Init(idAgent, date);
         //ShowRoute(GetRouteFromAgent(idAgent, date), 12);

         toolTip1.SetToolTip(cbRoadPoints, "Показывать промежуточные точки на маршруте");
         toolTip1.SetToolTip(numInterval, "Показать следующую точку не раньше заданного интервала в минутах");
      }

      private void InitDatasets()
      {
         dsGPSPos = DataModule.Get("GPSPos") == null ? new DataSet<DateTime, GPSPos>("GPSPos") : (DataSet<DateTime, GPSPos>)DataModule.Get("GPSPos");
         dsVisit = DataModule.Get("VisitInfo") == null ? new DataSet<int, VisitInfo>("Visit") : (DataSet<int, VisitInfo>)DataModule.Get(VisitInfo.V_OBJECT_NAME);
         dsOrgRemnants = DataModule.Get("OrgRemnants") == null ? new DataSet<int, OrgRemnants>("OrgRemnants") : (DataSet<int, OrgRemnants>)DataModule.Get("OrgRemnants");
         dsOrder = DataModule.Get("Order") == null ? new DataSet<int, Order>("Order") : (DataSet<int, Order>)DataModule.Get("Order");

#if HappyLand
         dsScriptDoc = DataModule.Get(ScriptDoc.OBJECT_NAME) == null ? new DataSet<int, ScriptDoc>(ScriptDoc.OBJECT_NAME) :
            (DataSet<int, ScriptDoc>)DataModule.Get(ScriptDoc.OBJECT_NAME);
#endif
         dsPKO = DataModule.Get(PKO.OBJECT_NAME) == null ? new DataSet<int, PKO>(PKO.OBJECT_NAME) :
            (DataSet<int, PKO>)DataModule.Get(PKO.OBJECT_NAME);
         dsAgents = Agents.GetDataSet();
         dsUserLog = DataModule.Get("UserLog") == null ? new DataSet<int, UserLog>("UserLog") :
           (DataSet<int, UserLog>)DataModule.Get("UserLog");

         dsRegion = (DataSet<string, Region>)DataModule.Get(GRSoft.NapoleonManager.Region.OBJECT_NAME) ??
            new DataSet<string, Region>(GRSoft.NapoleonManager.Region.OBJECT_NAME);
         dsRegion1 = (DataSet<string, Region1>)DataModule.Get(Region1.OBJECT_NAME) ??
            new DataSet<string, Region1>(Region1.OBJECT_NAME);
         dsRegion2 = (DataSet<string, Region2>)DataModule.Get(Region2.OBJECT_NAME) ??
            new DataSet<string, Region2>(Region2.OBJECT_NAME);
      }

      private void AdjustFilterForDS(string idAgent, DateTime date)
      {
         const string COMMON_FILTER_STR = "{0} >= ToDate('{1:dd/MM/yyyy}') and {0} < ToDate('{2:dd/MM/yyyy}') and userid = '{3}'";
         const string GPS_FILTER = COMMON_FILTER_STR + " and isGSM = '{4}'";

         if (((RouteFilterItem) cbFilter.SelectedItem) is AllItem)
            dsGPSPos.Filter = string.Format(COMMON_FILTER_STR, "date", date, date.AddDays(1), idAgent);
         else
            dsGPSPos.Filter = string.Format(GPS_FILTER, "date", date,
               date.AddDays(1), idAgent, ((RouteFilterItem)cbFilter.SelectedItem).Code);

         dsVisit.Filter = string.Format(COMMON_FILTER_STR, "date", date, date.AddDays(1), idAgent);
         dsOrgRemnants.Filter = string.Format(COMMON_FILTER_STR, "date", date, date.AddDays(1), idAgent);

#if HappyLand
         dsScriptDoc.Filter = string.Format(COMMON_FILTER_STR, "date", date, date.AddDays(1), idAgent);
#endif
         dsOrder.Filter = string.Format(COMMON_FILTER_STR, "created", date, date.AddDays(1), idAgent);
         dsPKO.Filter = String.Format(COMMON_FILTER_STR, "created", date, date.AddDays(1), idAgent);
         dsUserLog.Filter = String.Format(COMMON_FILTER_STR, "date", date, date.AddDays(1), idAgent) + " and category = 1";
      }

      //Начальные установки формы
      private void Init(string idAgent, DateTime date)
      {
         lbDistance.Text = string.Empty;

         foreach (Agent agent in dsAgents.Data)
         {
            cbAgents.Items.Add(new AgentItem(agent));
         }

         cbAgents.Sorted = true;
         SelectAgentFromId(idAgent);
         dtpDate.Value = date;
      }

      private void UpdateDataSets(string idAgent, DateTime date)
      {
         btnRefresh.Enabled = false;
         AdjustFilterForDS(idAgent, date);
         DataModule.CurrentUser = idAgent;
         DataModule.SetDataRepsonceHandlers(DataLoaded, DataConnectionError);
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsRegion1);
         list.Add(dsRegion2);
         list.Add(dsRegion);
         list.Add(dsGPSPos); 
         list.Add(dsVisit);
         list.Add(dsOrgRemnants);
         list.Add(dsOrder);
         list.Add(dsPKO);
         list.Add(dsUserLog);
         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), 
            list,
            FmWait.ProgressIndicator);
      }

      private void DataLoaded(object sender, EventArgs e)
      {
         DataModule.DataProcessed -= new EventHandler(DataLoaded);
         List<Location> route = GetRoute();

         BeginInvoke(new EmptyParamHandler(delegate
         {
            btnRefresh.Enabled = true;
            ShowRoute(route, 12);
            lbDistance.Text = String.Format("Путь: {0} м", GetFullDistance(route)); 
         }));
      }

      private int GetFullDistance(List<Location> route)
      {
         int result = 0;
         int i = 0;
         while (i + 1 < route.Count - 1)
         {
            result += Coordutils.Distance(route[i].Latitude,
                  route[i].Longitude, route[i + 1].Latitude, route[i + 1].Longitude);
            i++;
         }

         return result;
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate
         {
            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      //Выделить агента в списка
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

      //Получить маршрут для агента на дату
      private List<Location> GetRoute()
      {
         List<Location> result = new List<Location>();
         foreach (KeyValuePair<DateTime, GPSPos> kv in dsGPSPos)
         {
            GPSPos pos = kv.Value;
            if( pos.latitude != 0 || pos.longitude != 0 )
               result.Add(new Location(pos.latitude, pos.longitude, pos.isGSM == 1, pos.speed, pos.date));
         }
         //foreach (GPSPos pos in DataModule.Get("GPSPos").Data)
         //{
         //      result.Add(new Location(pos.latitude, pos.longitude, pos.isGSM == 1));
         //}

         //List<KeyValuePair<double, double>> result = new List<KeyValuePair<double, double>>();
         //foreach (GPSPos pos in DataModule.Get("GPSPos").Data)
         //{
         //   if (pos.agent.id == idAgent && pos.date.Date == data.Date)
         //   {
         //      result.Add(new KeyValuePair<double, double>( pos.latitude, pos.longitude));
         //   }
         //}

         return result;
      }

      //Заполнить грид, с посещенными организациями
      private void FillVisitGrid(List<VisitQueueItem> visitQueue)
      {
         dgvOrgs.SuspendLayout();
         try
         {
            dgvOrgs.Rows.Clear();
            int count = 0;
            int stopCount = 0;
            foreach (VisitQueueItem item in visitQueue)
            {
               string sumtext = (item.sum != 0) ? item.sum.ToString("C") : "";

               item.number = item.objType.IsStopType ? "P" + (++stopCount).ToString() : (!item.HavePosition) ? "" : (++count).ToString();
               //string num = (item.objType.IsStopType || !item.HavePosition) ? "" : (++count).ToString();

               PotenzialOrg po = item.org as PotenzialOrg;
               string r = string.Empty;
               string r1 = string.Empty;
               string r2 = string.Empty;

               if (po != null && po.region != null && 
                  po.region.r1 != null && po.region.r2 != null)
               {
                  r = po.region.Name;
                  r1 = po.region.r1.Name;
                  r2 = po.region.r2.Name;
               }

               dgvOrgs.Rows.Add(new object[] { item.number, item.OrgName, item.objType, 
                  item.startTime.ToString("HH:mm"), sumtext,
                  item.StopTime, item.address, item.factAddress, r, r1, r2});
            }
         }
         finally
         {
            dgvOrgs.ResumeLayout();
         }
      }

      private void FillLogGrid()
      {
         dgvLog.SuspendLayout();
         try
         {
            dgvLog.Rows.Clear();

            foreach (UserLog userLog in dsUserLog.Data)
               dgvLog.Rows.Add(new object[] { userLog.date.ToString("HH:mm"), userLog.userAction});
         }
         finally
         {
            dgvLog.ResumeLayout();
         }
      }

      private void dgvOrgs_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         DataGridViewRow r = dgvOrgs.Rows[e.RowIndex];
         if (r.Cells[7].Value != null)
            e.CellStyle.ForeColor = Color.Red;
      }

      delegate void ShowRouteHandler(List<Location> route, int zoom);

      //Вывести маршрут
      private void ShowRoute(List<Location> route, int zoom)
      {
         //wb.SuspendLayout();
         BeginInvoke(new ShowRouteHandler(ShowRouteFromThread), new object[] { route, zoom});
      }

      private void ShowRouteFromThread(List<Location> route, int zoom)
      {
         FmWait.ShowForm(this, true);
         
         List<VisitQueueItem> visitQueue = MakeVisitQueue(route);
         List<RoadPoint> roadPoint = cbRoadPoints.Checked ? MakeRoutePoitns(route) : null;
         FillVisitGrid(visitQueue);
         FillLogGrid();

         if (route.Count == 0 && visitQueue.Count > 0)
         {
            foreach (VisitQueueItem vi in visitQueue)
            {
               if (vi.longitude != 0 && vi.latitude != 0)
               {
                  Location l = new Location(vi.latitude, vi.longitude, true, 0, vi.startTime);
                  route.Add(l);
               }
            }
         }

         if (route.Count > 0)
         {
            string txt = MapEngine.TraceRoute(Config.GetConfig().mapSource, route, visitQueue, roadPoint);
            //File.WriteAllText("c:\\ttt1.html", txt);
            wb.DocumentText = txt;
         }
         else
         {
            wb.DocumentText = String.Format("<html><body>Нет данных для пользователя " +
               "<font color=blue><b>{0}</b></font> на <font color=blue><b>{1}</b></font> <body></html>",
               (cbAgents.SelectedItem as AgentItem).name, dtpDate.Value.Date.ToString("d"));
         }
      }

      private List<RoadPoint> MakeRoutePoitns(List<Location> route)
      {
         List<RoadPoint> result = new List<RoadPoint>();

         int i = 1;
         DateTime prevPointTime = DateTime.MinValue;

         foreach (Location loc in route)
         {
            RoadPoint rp = new RoadPoint();
            rp.Caption = i.ToString();

            rp.loc = loc;
            if (prevPointTime == DateTime.MinValue ||
               (loc.date - prevPointTime).TotalMinutes >= (double)numInterval.Value)
            {
               result.Add(rp);
               i++;
               prevPointTime = loc.date;
            }
         }

         return result;
      }

      
      //Удалить "лишние" точки с самым маленким расстоянием
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
               int length = Coordutils.Distance(coords[i].Key,
                  coords[i].Value, coords[i + 3].Key, coords[i + 3].Value);

               if (length < min_length)
               {
                  rm_index = i + 1;
               }

            }

            coords.RemoveAt(rm_index);
         }

         return coords;
      }

      //Обработка события смены текущего агента
      private void cbAgents_SelectedIndexChanged(object sender, EventArgs e)
      {
         //if (Visible)
         //{
         //   UpdateDataSets((cbAgents.SelectedItem as AgentItem).id, dtpDate.Value.Date);
         //}
      }

      //Обработка событие смены даты
      private void dtpDate_ValueChanged(object sender, EventArgs e)
      {
         //if (Visible)
         //{
         //   UpdateDataSets((cbAgents.SelectedItem as AgentItem).id, dtpDate.Value.Date);
         //}
      }

      private void FmRoute_FormClosing(object sender, FormClosingEventArgs e)
      {
         wb.Dispose();
      }

      private void mapView_Clicked(object sender, EventArgs e)
      {
         showMap = !showMap;
         //mapView.Text = (showMap) ? "Показать спутник" : "Показать карту";
         //ShowRoute(GetRoute((cbAgents.SelectedItem as AgentItem).id, dtpDate.Value.Date), 12);
      }

      private void CheckPoint(VisitQueueItem vi, Org org)
      {
         if (org == null || org.address == null || org.address.Length == 0)
            return;

         Location l = Route.GetLocation(org.address);
         if (l != null)
         {
            vi.address = org.address;

            if (vi.longitude != 0 || vi.latitude != 0)
            {
               // проверим точку на удаленность от точки документа
               NapoleonManager.Location check = new Location(vi.latitude, vi.longitude);
               if (NapoleonManager.Location.Distance(l, check) > 100)
               {
                  vi.address = org.address;
                  vi.factAddress = check.GetAddress();
                  vi.outOfRange = true;
               }
            }
         }
      }

      //Создать список посещений, отсортированный по дате
      private List<VisitQueueItem> MakeVisitQueue(List<Location> route)
      {
         List<VisitQueueItem> result = new List<VisitQueueItem>();

#if HappyLand
         foreach (ScriptDoc sd in dsScriptDoc.Data)
         {
            double lng = sd.longitude;
            double lat = sd.latitude;
            if (lng == 0)
            {
               Visit[] v = sd.Visits;
               if (v != null && v.Length > 0 && v[0] != null)
               {
                  lng = v[0].longitude;
                  lat = v[0].latitude;
               }
            }

            if (lng == 0)
            {
               Order o = sd.Order;
               if (o != null)
               {
                  lng = o.longitude;
                  lat = o.latitude;
               }
            }
            result.Add(new VisitQueueItem(sd.date, sd.org, lat, lng, new VisitType(ObjType.TObjType.Script)));
         }
#else
         foreach (VisitInfo visit in dsVisit.Data)
         {
            VisitQueueItem vi = new VisitQueueItem(visit.date, visit.org,
               visit.latitude, visit.longitude, new VisitType(ObjType.TObjType.OtVisit));
            CheckPoint(vi, visit.org);
            result.Add(vi);
         }

         foreach (OrgRemnants orgRemnants in dsOrgRemnants.Data)
         {
            VisitQueueItem vi = new VisitQueueItem(orgRemnants.date, orgRemnants.org,
               orgRemnants.latitude, orgRemnants.longitude, new VisitType(ObjType.TObjType.OtOrgRemnants));
            CheckPoint(vi, orgRemnants.org);
            result.Add(vi);
         }

         foreach (Order order in dsOrder.Data)
         {
            VisitQueueItem vi = new VisitQueueItem(order.Created, order.org, order.latitude,
               order.longitude, new VisitType(ObjType.TObjType.OtOrder));
            CheckPoint(vi, order.org);
            vi.sum = order.Sum();
            result.Add(vi);
         }

         foreach (PKO pko in dsPKO.Data)
         {
            VisitQueueItem vi = new VisitQueueItem(pko.created, pko.org, pko.latitude,
               pko.longitude, new VisitType(ObjType.TObjType.PKO));
            CheckPoint(vi, pko.org);
            vi.sum = pko.Sum;
            result.Add(vi);
         }

         AddStopping(result, route);
#endif
         result.Sort(new CmpVisitQueueItem());
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
               NapoleonManager.Location cp = route[ctr++];
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

      private void wb_DocumentCompleted(object sender, WebBrowserDocumentCompletedEventArgs e)
      {
         //wb.ResumeLayout();
         FmWait.CloseForm();
      }

      private void FmRoute_Shown(object sender, EventArgs e)
      {
         //UpdateDataSets((cbAgents.SelectedItem as AgentItem).id, dtpDate.Value.Date);
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
         cbFilter.Items.Clear();
         cbFilter.Items.Add(new AllItem());
         cbFilter.Items.Add(new GSMItem());
         cbFilter.Items.Add(new GPSItem());
         cbFilter.SelectedIndex = 0;
      }

      private void cbFilter_SelectionChangeCommitted(object sender, EventArgs e)
      {
         //UpdateDataSets((cbAgents.SelectedItem as AgentItem).id, dtpDate.Value.Date);
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         new Thread(new ParameterizedThreadStart(delegate(object o)
         {
            BeginInvoke(new EmptyParamHandler(delegate() { FmWait.ShowForm(this, true); }));
            RouteExcelReport rpt = new RouteExcelReport();
            rpt.Build(dgvOrgs);
            rpt.Show();
            BeginInvoke(new EmptyParamHandler(delegate() { FmWait.CloseForm(); }));
         })).Start();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         UpdateDataSets((cbAgents.SelectedItem as AgentItem).id, dtpDate.Value.Date);
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

   class RoadPoint
   {
      public string Caption;
      public Location loc;
   }

}
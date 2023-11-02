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
using System.Windows.Forms.VisualStyles;

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
      //private DataSet<string, Region> dsRegion;
      //private DataSet<string, Region1> dsRegion1;
      //private DataSet<string, Region2> dsRegion2;
      private DataSet<int, Sales> dsSales;
      private DataSet<int, Incass> dsIncass;

//#if HappyLand
//      private DataSet<int, ScriptDoc> dsScriptDoc;
//#endif
      private DataSet<int, PKO> dsPKO;
      private Agents dsAgents;
      private DataSet<int, UserLog> dsUserLog;
      private DataSet<int, CommonConfig> dsConfig;

      List<DocumentInfo> documents;

      private bool showMap = false;

      //private const string YANDEX_KEY = "http://static-maps.yandex.ru/1.x/?l=sat,skl&key=ANzXmEoBAAAAkjQFPwIAQphddQGLBfkqE3qCI3Vo8OCM8yYAAAAAAAAAAACYpxbA0OaEdJRuAHC50onaXfYLBQ==&";
      //private const string YANDEX_KEY = "http://static-maps.yandex.ru/1.x/?key=ANzXmEoBAAAAkjQFPwIAQphddQGLBfkqE3qCI3Vo8OCM8yYAAAAAAAAAAACYpxbA0OaEdJRuAHC50onaXfYLBQ==&";

      private FmRouteSetting setting = null;
      internal const int DEF_NUM_INTERVAL = 15;
#if MOVEMENT_DOC
      private DataSet<int, MoveDoc> dsMove;
#endif
      internal FmRoute(string idAgent, DateTime date)
      {
         InitializeComponent();
         InitDatasets();
         Init(idAgent, date);
         //ShowRoute(GetRouteFromAgent(idAgent, date), 12);
         
         toolTip1.SetToolTip(cbRoadPoints, "Показывать промежуточные точки на маршруте");
         toolTip1.SetToolTip(numInterval, "Показать следующую точку не раньше заданного интервала в минутах");
         
         foreach(UserLog.ActionInfo ai in UserLog.LogActions)
            cbActionFilter.Items.Add(new CheckComboBoxItem(ai, true));

         cbActionFilter.SelectedIndex = 0;
         this.cbActionFilter.SelectedIndexChanged += new System.EventHandler(this.cbActionFilter_SelectedIndexChanged);

#if Vyatich
         distAccurate.Value = 300;
#endif
      }

      internal void SetDocuments(List<DocumentInfo> documents) { this.documents = documents; }

      private void InitDatasets()
      {
         dsGPSPos = DataModule.Get("GPSPos") == null ? new DataSet<DateTime, GPSPos>("GPSPos") : (DataSet<DateTime, GPSPos>)DataModule.Get("GPSPos");
         dsVisit = DataModule.Get("VisitInfo") == null ? new DataSet<int, VisitInfo>("Visit") : (DataSet<int, VisitInfo>)DataModule.Get(VisitInfo.V_OBJECT_NAME);
         dsOrgRemnants = DataModule.Get("OrgRemnants") == null ? new DataSet<int, OrgRemnants>("OrgRemnants") : (DataSet<int, OrgRemnants>)DataModule.Get("OrgRemnants");
         dsOrder = DataModule.Get("Order") == null ? new DataSet<int, Order>("Order") : (DataSet<int, Order>)DataModule.Get("Order");

//#if HappyLand
//         dsScriptDoc = DataModule.Get(ScriptDoc.OBJECT_NAME) == null ? new DataSet<int, ScriptDoc>(ScriptDoc.OBJECT_NAME) :
//            (DataSet<int, ScriptDoc>)DataModule.Get(ScriptDoc.OBJECT_NAME);
//#endif
         dsPKO = DataModule.Get(PKO.OBJECT_NAME) == null ? new DataSet<int, PKO>(PKO.OBJECT_NAME) :
            (DataSet<int, PKO>)DataModule.Get(PKO.OBJECT_NAME);
         dsAgents = CurrentUser.user.GetAgents();// Agents.GetDataSet();
         dsUserLog = DataModule.Get("UserLog") == null ? new DataSet<int, UserLog>("UserLog") :
           (DataSet<int, UserLog>)DataModule.Get("UserLog");

         //dsRegion = (DataSet<string, Region>)DataModule.Get(GRSoft.NapoleonManager.Region.OBJECT_NAME) ??
         //   new DataSet<string, Region>(GRSoft.NapoleonManager.Region.OBJECT_NAME);
         //dsRegion1 = (DataSet<string, Region1>)DataModule.Get(Region1.OBJECT_NAME) ??
         //   new DataSet<string, Region1>(Region1.OBJECT_NAME);
         //dsRegion2 = (DataSet<string, Region2>)DataModule.Get(Region2.OBJECT_NAME) ??
         //   new DataSet<string, Region2>(Region2.OBJECT_NAME);
         dsSales = (DataSet<int, Sales>)DataModule.Get(Sales.OBJECT_NAME) ??
            new DataSet<int, Sales>(Sales.OBJECT_NAME);
         dsIncass = (DataSet<int, Incass>)DataModule.Get(Incass.OBJECT_NAME) ??
            new DataSet<int, Incass>(Incass.OBJECT_NAME);
#if MOVEMENT_DOC
         dsMove = (DataSet<int, MoveDoc>)DataModule.Get(MoveDoc.OBJECT_NAME) ??
            new DataSet<int, MoveDoc>(MoveDoc.OBJECT_NAME);
#endif
         dsConfig = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME) ??
            new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME);
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

         dsVisit.Filter = string.Format(COMMON_FILTER_STR, "date", date, idAgent, timeBegin.Value, timeEnd.Value);
         dsOrgRemnants.Filter = string.Format(COMMON_FILTER_STR, "date", date, idAgent, timeBegin.Value, timeEnd.Value);

//#if HappyLand
         //dsScriptDoc.Filter = string.Format(COMMON_FILTER_STR, "date", date,idAgent, timeBegin.Value, timeEnd.Value);
//#endif
         String createdFilter = string.Format(COMMON_FILTER_STR, "created", date, idAgent, timeBegin.Value, timeEnd.Value);
         dsOrder.Filter = createdFilter;
         dsPKO.Filter = createdFilter;
         dsUserLog.Filter = String.Format(COMMON_FILTER_STR, "date", date, idAgent, timeBegin.Value, timeEnd.Value) + " and \"category\" = 1";
         dsSales.Filter = String.Format(COMMON_FILTER_STR, "created", date, idAgent, timeBegin.Value, timeEnd.Value);
         string f = "(" + String.Format(COMMON_FILTER_STR, "date", date, idAgent, timeBegin.Value, timeEnd.Value) + ") or (" +
           String.Format(COMMON_FILTER_STR, "created", date, idAgent, timeBegin.Value, timeEnd.Value) + ")";
         dsIncass.Filter = f;
#if MOVEMENT_DOC
         dsMove.Filter = string.Format(COMMON_FILTER_STR, "created", date, idAgent, timeBegin.Value, timeEnd.Value);
#endif
         foreach(DocumentInfo di in documents)
            di.DataSet.Filter = createdFilter;

         dsConfig.Filter = "(not (\"userid\" is null)) or \"userid\" is null";
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
         //DataModule.CurrentUser = idAgent;

         List<IDataSet> list = new List<IDataSet>();
         //list.Add(dsRegion1);
         //list.Add(dsRegion2);
         //list.Add(dsRegion);
         list.Add(dsGPSPos); 
         list.Add(dsVisit);
         list.Add(dsOrgRemnants);
         list.Add(dsOrder);
         list.Add(dsPKO);
         list.Add(dsUserLog);
         list.Add(dsSales);
         list.Add(dsIncass);
         list.Add(dsConfig);

         OrgLocations ol = OrgLocations.GetDataSet();
         //if (ol.Count == 0)
            list.Add(ol);

#if MOVEMENT_DOC
         list.Add(dsMove);
#endif
         FmWait.StdDataRefresh(this, list, DoLoadData, btnRefresh);
         //DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), 
         //   list,
         //   FmWait.ProgressIndicator);
      }

      void DoLoadData()
      {
         List<Location> route = GetRoute();
         btnRefresh.Enabled = true;
         ShowRoute(route, 12);
         lbDistance.Text = String.Format("Путь: {0} м", GetFullDistance(route));
      }

      private int GetFullDistance(List<Location> route)
      {
         double result = 0;
         int i = 0;
         //while (i + 1 < route.Count - 1)
         while (i  < route.Count - 1)
         {
            result += Coordutils.Distance(route[i].Latitude,
                  route[i].Longitude, route[i + 1].Latitude, route[i + 1].Longitude);
            i++;
         }

         return (int)result;
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
                  l.isVisitPoint = true;
                  route.Add(l);
               }
            }
         }

         if (route.Count > 0)
         {
            string txt = MapEngine.TraceRoute(Config.GetConfig().mapSource, route, visitQueue, roadPoint, GetPointType);
#if MAKE_HTML_FILE
            File.WriteAllText("c:\\ttt1.html", txt);
#endif
            wb.DocumentText = txt;
         }
         else
         {
            AgentItem selAgent = cbAgents.SelectedItem as AgentItem;
            StringBuilder wp = new StringBuilder();
            wp.Append("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'></head><body>");
            if (selAgent != null)
            {
               CommonConfig cc = ConfigUtils.GetConfig(dsConfig, ConfigKeyItems.GPS_TRACKING, selAgent.id);
               if (cc == null || cc.value.Equals("none"))
                  wp.Append("<font color=red>Не активирована функция GPSконтроля</font>");
               else
               {
                  wp.Append("Нет данных для пользователя <font color=blue><b>");
                  wp.Append(selAgent.name);
                  wp.Append("</b></font> на <font color=blue><b>");
                  wp.Append(dtpDate.Value.Date.ToString("d"));
                  wp.Append("</b></font> ");
               }
            }
            else
               wp.Append("Не выбран пользователь");

            wp.Append("<body></html>");
            wb.DocumentText = wp.ToString();
         }
      }

      MapEngine.PoitType GetPointType
      {
         get 
         { 
            return cbFilter.SelectedIndex == 0 ? MapEngine.PoitType.All :
               cbFilter.SelectedIndex == 1 ? MapEngine.PoitType.GSM : MapEngine.PoitType.GPS;
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
         if (org == null || org.Address == null || org.Address.Length == 0)
            return;

         Location l = Route.GetLocation(org);
         if (l != null)
         {
            vi.address = org.Address;

            if (vi.longitude != 0 || vi.latitude != 0)
            {
               NapoleonManager.Location check = new Location(vi.latitude, vi.longitude);
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
               //bool checking = false;
               //// проверим точку на удаленность от точки документа
               //if (NapoleonManager.Location.Distance(l, check) > Convert.ToDouble(distAccurate.Value))
               //{
               //   NapoleonManager.Location check1 = FindNearesRoutePoint(vi.startTime);
               //   if( check1 != null )
               //   {
               //      if( NapoleonManager.Location.Distance(l, check1) < Convert.ToDouble(distAccurate.Value) )
               //      {
               //         vi.latitude = check1.Latitude;
               //         vi.longitude = check1.Longitude;
               //         checking = true;
               //      }
               //   }
               //   if( !checking )
               //   {
               //      vi.address = org.Address;
               //      vi.factAddress = check.GetAddress();
               //      vi.outOfRange = true;
               //   }
               //}
            }
         }
      }

      private NapoleonManager.Location FindNearesRoutePoint(DateTime dateTime)
      {
         NapoleonManager.Location check = null;
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

/*
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
*/
         foreach (Sales sales in dsSales.Data)
         {
            VisitQueueItem vi = new VisitQueueItem(sales.created, sales.org,
               sales.latitude, sales.longitude, new VisitType(ObjType.TObjType.Sales));
            CheckPoint(vi, sales.org);
            vi.sum = sales.Sum();
            result.Add(vi);
         }

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
            vi.sum = pko.Sum();
            result.Add(vi);
         }

         foreach (Incass incass in dsIncass.Data)
         {
            VisitQueueItem vi = new VisitQueueItem(incass.created, incass.org, incass.latitude,
               incass.longitude, new VisitType(ObjType.TObjType.Incass));
            CheckPoint(vi, incass.org);
            vi.sum = incass.Sum();
            result.Add(vi);
         }
         
         foreach (DocumentInfo di in documents)
         {
            foreach (BaseDocument doc in di.DataSet.Data)
            {
               VisitQueueItem vi = new VisitQueueItem(doc, new VisitType(di.Type));
               CheckPoint(vi, doc.org);
               vi.sum = doc.Sum();
               result.Add(vi);
            }
         }

#if MOVEMENT_DOC
         foreach (MoveDoc mve in dsMove.Data)
         {
            VisitQueueItem vi = new VisitQueueItem(mve.date, mve.org,
               mve.latitude, mve.longitude, new VisitType(ObjType.TObjType.OtVisit));
            CheckPoint(vi, mve.org);
            result.Add(vi);
         }
#endif
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

         foreach (DataGridViewRow row in dgvOrgs.Rows)
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

   class RoadPoint
   {
      public string Caption;
      public Location loc;
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
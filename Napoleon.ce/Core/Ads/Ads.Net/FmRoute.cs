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
using System.Xml;
using System.Net;
using System.Runtime.Serialization.Formatters.Binary;
using GRSoft.Ads.Utils;

namespace GRSoft.Ads
{
   //Если для файлов png вызывается другая программа, а в Наполеоне выдает "запрос отменен"
   //Надо посмотреть в панель управления->свойство папки->типы файлов 
   //и удалить регистрацию для файлов PNG

   public partial class FmRoute : Form
   {
      //Необходимые наборы данных
      private DsGPSPos dsGPSPos;
      private DsBrigade dsBrigade;
      private DsWorkDay dsWorkDay;
      private DsOrderRcv dsOrder;
      private DsUserLog dsUserLog;

      private bool showMap = false;
      private static FmRoute instance;

      //private const string YANDEX_KEY = "http://static-maps.yandex.ru/1.x/?l=sat,skl&key=ANzXmEoBAAAAkjQFPwIAQphddQGLBfkqE3qCI3Vo8OCM8yYAAAAAAAAAAACYpxbA0OaEdJRuAHC50onaXfYLBQ==&";
      //private const string YANDEX_KEY = "http://static-maps.yandex.ru/1.x/?key=ANzXmEoBAAAAkjQFPwIAQphddQGLBfkqE3qCI3Vo8OCM8yYAAAAAAAAAAACYpxbA0OaEdJRuAHC50onaXfYLBQ==&";

      internal FmRoute(string idBrigade, DateTime date)
      {
         InitializeComponent();
         InitDatasets();
         Init(idBrigade, date);
         //ShowRoute(GetRouteFromAgent(idAgent, date), 12);
         lbWorkBegin.Text = "";
         lbWorkEnd.Text = "";

         toolTip1.SetToolTip(cbRoadPoints, "Показывать промежуточные точки на маршруте");
         toolTip1.SetToolTip(numInterval, "Показать следующую точку не раньше заданного интервала в минутах");

         dgvLog.AutoGenerateColumns = false;
      }

      internal static void ShowInstance(string idBrigade, DateTime date)
      {
         if (instance == null)
         {
            instance = FormEnties.CreateRouteForm(idBrigade, date);//new FmRoute(idBrigade, date);
            instance.Show();
         }
         else
            instance.Activate();
      }

      private void InitDatasets()
      {
         dsGPSPos = (DsGPSPos)DataModule.Get(GPSPos.OBJECT_NAME) ?? new DsGPSPos(true);
         dsBrigade = (DsBrigade)DataModule.Get(Brigade.OBJECT_NAME) ?? new DsBrigade(true);
         dsWorkDay = (DsWorkDay)DataModule.Get(WorkDay.OBJECT_NAME) ?? new DsWorkDay(true);
         dsOrder = (DsOrderRcv)DataModule.Get(Order.OBJECT_NAME) ?? new DsOrderRcv(true);
         dsUserLog = (DsUserLog)DataModule.Get(UserLog.OBJECT_NAME) ?? new DsUserLog(true);
      }

      protected virtual void AdjustFilterForDS(string idAgent, DateTime date)
      {
         const string COMMON_FILTER_STR = "{0} >= ToDate('{1}') and {0} < ToDate('{2}') and userid = '{3}'";
         const string GPS_FILTER = COMMON_FILTER_STR + " and isGSM = '{4}'";

         if (((RouteFilterItem) cbFilter.SelectedItem) is AllItem)
            dsGPSPos.Filter = string.Format(COMMON_FILTER_STR, "date", date, date.AddDays(1), idAgent);
         else
            dsGPSPos.Filter = string.Format(GPS_FILTER, "date", date, 
               date.AddDays(1), idAgent,((RouteFilterItem) cbFilter.SelectedItem).Code );

         dsWorkDay.Filter = String.Format("\"date\" = ToDate('{0}') and \"userid\" = '{1}'",
            date, idAgent);

         dsOrder.Filter = String.Format("not (\"factbegin\" is NULL) and \"userid\" = '{0}' " +
            "and \"factbegin\" >= ToDate('{1}') and \"factbegin\" < ToDate('{2}')", idAgent,
            date, date.AddDays(1));

         dsUserLog.Filter = String.Format(COMMON_FILTER_STR, "date", date, 
            date.AddDays(1), idAgent) + " and \"category\" = 1";
      }

      //Начальные установки формы
      private void Init(string idAgent, DateTime date)
      {
         lbDistance.Text = string.Empty;

         foreach (Brigade brigade in dsBrigade.Data)
         {
            cbBrigade.Items.Add(brigade);
         }

         cbBrigade.Sorted = true;
         SelectBrigadeFromId(idAgent);
         dtpDate.Value = date;
      }

      protected virtual List<IDataSet> DataSetList()
      {
         List<IDataSet> result = new List<IDataSet>();

         result.Add(dsGPSPos);
         result.Add(dsWorkDay);
         result.Add(dsOrder);
         result.Add(dsUserLog);

         return result;
      }

      private void UpdateDataSets(string idAgent, DateTime date)
      {
         AdjustFilterForDS(idAgent, date);
         DataModule.CurrentUser = idAgent;
         DataModule.SetDataRepsonceHandlers(DataLoaded, DataModule_OnDataResponceError);
         btnRefresh.Enabled = false;

         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), 
            DataSetList(),
            FmWait.ProgressIndicator);
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         Invoke(new InvokeDelegate(delegate() 
            { 
               MessageBox.Show(e.Msg);
               btnRefresh.Enabled = true;
            }));
      }

      private void DataLoaded(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         List<Location> route = GetRoute();

         BeginInvoke(new EmptyInvoker(delegate
         { 
            //ShowRoute(route, 12);
            //wb.SuspendLayout();
            ShowRouteFromThread(route, 12);
            lbDistance.Text = String.Format("Путь: {0} м", 0);

            WorkDay workDay = null;
            if (dsWorkDay.Count >= 1)
            {
               workDay = dsWorkDay[0];
               lbDistance.Text = String.Format("Путь: {0} м", workDay.distance);
               lbWorkBegin.Text = workDay.Begin;
               lbWorkEnd.Text = workDay.End;
            }
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

      //Выделить агента в списка
      private void SelectBrigadeFromId(string idBrigade)
      {
         if (idBrigade != null)
            foreach (Brigade b in cbBrigade.Items)
            {
               if (b.id == idBrigade)
               {
                  cbBrigade.SelectedItem = b;
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
            foreach (VisitQueueItem item in visitQueue)
            {
               string sumtext = (item.sum != 0) ? item.sum.ToString("C") : "";
               dgvOrgs.Rows.Add(new object[] { ++count, item.OrgName, item.objType, 
                  item.startTime.ToString("HH:mm"), sumtext,
                  item.StopTime, item.address, item.factAddress});
            }
         }
         finally
         {
            dgvOrgs.ResumeLayout();
         }
      }

      private void FillLogGrid()
      {
         List<UserLog> list = new List<UserLog>();
         list.AddRange(dsUserLog.Values);
         dgvLog.DataSource = list;
      }

      private void dgvOrgs_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         DataGridViewRow r = dgvOrgs.Rows[e.RowIndex];
         if (r.Cells[7].Value != null)
            e.CellStyle.ForeColor = Color.Red;
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
            string txt = CreateHTML(route, visitQueue, roadPoint);
            //File.WriteAllText("ttt1.html", txt);
            wb.DocumentText = txt;
         }
         else
         {
            wb.DocumentText = String.Format("<html><body>Нет данных для пользователя " +
               "<font color=blue><b>{0}</b></font> на <font color=blue><b>{1}</b></font> <body></html>",
               (cbBrigade.SelectedItem as Brigade).name, dtpDate.Value.Date.ToString("d"));
         }
      }

      public virtual string CreateHTML(List<Location> route, List<VisitQueueItem> visitQueue, List<RoadPoint> roadPoint)
      {
         return MapEngine.TraceRoute(Config.GetConfig().mapSource, route, visitQueue, roadPoint);
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

      private static Dictionary<string, Location> cachedLocations = new Dictionary<string, Location>();

      public static XmlDocument GetYandexRequest(string reqStr)
      {
         HttpWebRequest request = (HttpWebRequest)WebRequest.Create(
            "http://geocode-maps.yandex.ru/1.x/?geocode=" + reqStr +
            "&key=ANzXmEoBAAAAkjQFPwIAQphddQGLBfkqE3qCI3Vo8OCM8yYAAAAAAAAAAACYpxbA0OaEdJRuAHC50onaXfYLBQ==");

#pragma warning disable 618
         request.Proxy = WebProxy.GetDefaultProxy();

         Config c = Config.GetConfig();
         if (c.proxyLogin.Length > 0)
            request.Credentials = new NetworkCredential(c.proxyLogin, c.proxyPassword);

         HttpWebResponse response = (HttpWebResponse)request.GetResponse();

         Stream resStream = response.GetResponseStream();
         int count = 0;
         StringBuilder sb = new StringBuilder();
         byte[] buf = new byte[8192];
         do
         {
            count = resStream.Read(buf, 0, buf.Length);
            if (count != 0)
               sb.Append(Encoding.UTF8.GetString(buf, 0, count));
         } while (count > 0);

         XmlDocument doc = new XmlDocument();
         doc.LoadXml(sb.ToString());

         return doc;
      }

      static bool GoodPrecision(XmlNode node)
      {
         XmlElement element = node as XmlElement;
         if (element == null) return false;

         bool res = false;
         XmlNodeList resCount = element.GetElementsByTagName("precision");
         if (resCount.Count > 0)
         {
            //XmlNode n = resCount.Item(0);
            //if (n.InnerText == "exact" || n.InnerText == "near" || n.InnerText == "street")
            res = true;
         }

         return res;
      }

      static public Location GetLocation(string address)
      {
         Location location = null;
         if (cachedLocations.ContainsKey(address))
         {
            return cachedLocations[address];
         }

         if (address != null && address.Length > 0)
         {
            try
            {
               XmlDocument doc = GetYandexRequest(address);

               XmlNodeList result = doc.GetElementsByTagName("featureMember");
               foreach (XmlNode node in result)
               {
                  if (GoodPrecision(node))
                  {
                     XmlNodeList posList = (node as XmlElement).GetElementsByTagName("pos");
                     if (posList.Count > 0)
                     {
                        string posText = posList.Item(0).InnerText;
                        string[] posA = posText.Split(new char[] { ' ' });
                        location = new Location();
                        CultureInfo en = CultureInfo.GetCultureInfo("en-US");
                        location.Longitude = double.Parse(posA[0], en);
                        location.Latitude = double.Parse(posA[1], en);

                        break;
                     }
                  }
               }
            }
            catch (Exception)
            {
               //using (StreamWriter w = new StreamWriter("log.txt", true))
               //{
               //   w.Write(e.Message);
               //   w.Flush();
               //}
            }
         }

         if (location != null)
            cachedLocations[address] = location;
         return location;
      }

      private void CheckPoint(VisitQueueItem vi, Client org)
      {
         if (org == null || org.address == null || org.address.Length == 0)
            return;

         Location l = GetLocation(org.address);
         if (l != null)
         {
            vi.address = org.address;

            if (vi.longitude != 0 || vi.latitude != 0)
            {
               // проверим точку на удаленность от точки документа
               Location check = new Location(vi.latitude, vi.longitude);
               if (GRSoft.Ads.Location.Distance(l, check) > 100)
               {
                  vi.address = org.address;
                  vi.factAddress = check.GetAddress();
                  vi.outOfRange = true;
               }
            }
         }
      }

      //Создать список посещений, отсортированный по дате
      protected virtual List<VisitQueueItem> MakeVisitQueue(List<Location> route)
      {
         List<VisitQueueItem> result = new List<VisitQueueItem>();

         foreach (OrderRcv order in dsOrder.Data)
         {
            VisitQueueItem vi = new VisitQueueItem(order.factbegin, order.client,
               order.latitude, order.longitude, new VisitType(ObjType.TObjType.OtOrder));
            vi.endTime = order.factend;
            CheckPoint(vi, order.client);
            result.Add(vi);
         }

         AddStopping(result, route);

         result.Sort(new CmpVisitQueueItem());

         int index = 1;

         foreach (VisitQueueItem vqi in result)
            vqi.VisitNumber = index++;

         return result;
      }

      public bool IsNearest(Location l1, Location l2)
      {
         return (GRSoft.Ads.Location.Distance(l1, l2) < 100 && l1.speed < 5 && l2.speed < 5);
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

      private void wb_DocumentCompleted(object sender, WebBrowserDocumentCompletedEventArgs e)
      {
         wb.ResumeLayout();
         FmWait.CloseForm();
         btnRefresh.Enabled = true;
      }

      private void tsbMessage_Click(object sender, EventArgs e)
      {
         //Agent agent = new Agent();
         //agent.id = (cbBrigade.SelectedItem as AgentItem).id;
         //agent.name = (cbBrigade.SelectedItem as AgentItem).name;
         //FmMessage.MessageShow(agent);
      }

      private void FmRoute_Load(object sender, EventArgs e)
      {
         cbFilter.Items.Clear();
         cbFilter.Items.Add(new AllItem());
         cbFilter.Items.Add(new GSMItem());
         cbFilter.Items.Add(new GPSItem());
         cbFilter.SelectedIndex = 0;

         SettingFmRoute settingFmRoute = BaseFormSetting<SettingFmRoute>.Load();
         cbRoadPoints.Checked = settingFmRoute.routePointsEnabled;
         numInterval.Value = (decimal)settingFmRoute.routePointsInterval;
      }

      private void cbFilter_SelectionChangeCommitted(object sender, EventArgs e)
      {
         if (cbBrigade.SelectedItem == null)
            return;
         UpdateDataSets((cbBrigade.SelectedItem as Brigade).id, dtpDate.Value.Date);
      }

      private void FmRoute_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;

         SettingFmRoute settingFmRoute = new SettingFmRoute();
         settingFmRoute.routePointsEnabled = cbRoadPoints.Checked;
         settingFmRoute.routePointsInterval = (double)numInterval.Value;
         settingFmRoute.Save();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         Brigade brigade = cbBrigade.SelectedItem as Brigade;
         lbWorkBegin.Text = "";
         lbWorkEnd.Text = "";

         if (Visible && brigade != null)
            UpdateDataSets(brigade.id, dtpDate.Value.Date);
      }

      private void cbRoadPoints_CheckedChanged(object sender, EventArgs e)
      {
         numInterval.Enabled = ((CheckBox)sender).Checked;
      }

      private void dgvLog_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         if (e.RowIndex != -1 && e.RowIndex < ((DataGridView)sender).Rows.Count)
         {
            DataGridViewRow row = ((DataGridView)sender).Rows[e.RowIndex];
            UserLog ul = row.DataBoundItem as UserLog;

            if (ul != null)
            {
               if (ul.action == 9)
                  e.CellStyle.BackColor = Color.LightGray;
            }
         }

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

   public class Location
   {
      private double latitude = 0.0;
      private double longitude = 0.0;
      private bool isGsm = false;

      public double speed = 0.0;
      public DateTime date = DateTime.MinValue;

      public Location() { }

      public Location(double latitude, double longitude, bool isGsm, double speed, DateTime date)
      {
         this.latitude = latitude;
         this.longitude = longitude;
         this.isGsm = isGsm;
         this.speed = speed;
         this.date = date;
      }

      public Location(double lat, double lng)
      {
         this.latitude = lat;
         this.longitude = lng;
      }

      public double Latitude { get { return latitude; } set { latitude = value; } }
      public double Longitude { get { return longitude; } set { longitude = value; } }
      public bool IsGsm { get { return isGsm; } set { isGsm = value; } }

      public static double Distance(Location l1, Location l2)
      {
         return Coordutils.Distance(l1.latitude, l1.longitude, l2.latitude, l2.longitude);
      }
      public static XmlDocument GetYandexRequest(string reqStr)
      {
         HttpWebRequest request = (HttpWebRequest)WebRequest.Create(
            "http://geocode-maps.yandex.ru/1.x/?geocode=" + reqStr +
            "&key=ANzXmEoBAAAAkjQFPwIAQphddQGLBfkqE3qCI3Vo8OCM8yYAAAAAAAAAAACYpxbA0OaEdJRuAHC50onaXfYLBQ==");

#pragma warning disable 618
         request.Proxy = WebProxy.GetDefaultProxy();

         Config c = Config.GetConfig();
         if (c.proxyLogin.Length > 0)
            request.Credentials = new NetworkCredential(c.proxyLogin, c.proxyPassword);

         HttpWebResponse response = (HttpWebResponse)request.GetResponse();

         Stream resStream = response.GetResponseStream();
         int count = 0;
         StringBuilder sb = new StringBuilder();
         byte[] buf = new byte[8192];
         do
         {
            count = resStream.Read(buf, 0, buf.Length);
            if (count != 0)
               sb.Append(Encoding.UTF8.GetString(buf, 0, count));
         } while (count > 0);

         XmlDocument doc = new XmlDocument();
         doc.LoadXml(sb.ToString());

         return doc;
      }

      public string GetAddress()
      {
         string address = "";
         try
         {
            if (addresses.ContainsKey(this))
               address = addresses[this];
            else
            {
               CultureInfo enus = CultureInfo.GetCultureInfo("en-US");
               XmlDocument doc = GetYandexRequest(longitude.ToString(enus) + "," + latitude.ToString(enus));
               XmlNamespaceManager nsmgr = new XmlNamespaceManager(doc.NameTable);
               nsmgr.AddNamespace("ab", "http://maps.yandex.ru/geocoder/1.x");
               XmlNode res = doc.SelectSingleNode("//ab:GeocoderMetaData/ab:text", nsmgr);
               if (res != null)
               {
                  char[] sep = new char[] { ',' };
                  string[] v = res.InnerText.Split(sep);
                  if (v.Length > 2)
                  {
                     for (int i = 2; i < v.Length; i++)
                        address += v[i] + ",";

                     address = address.TrimEnd(sep);
                  }
                  else
                     address = res.InnerText;

                  addresses[this] = address;
               }
            }
         }
         catch (Exception)
         {
         }
         return address;
      }

      private static Dictionary<Location, string> addresses = new Dictionary<Location, string>();
   }

   public class VisitType : ObjType
   {
      public string typeName = null;

      public VisitType(TObjType val)
         : base(val)
      {
      }

      public VisitType(string objName)
      {
         if (!FromString(objName))
         {
            val = TObjType.NotVisit;
            typeName = objName;
         }
      }

      public override string ToString()
      {
         if (typeName != null)
            return typeName;
         return base.ToString();
      }

      public bool IsStopType { get { return typeName != null; } }
   }

   public class VisitQueueItem
   {
      public DateTime startTime;
      public double latitude;
      public double longitude;
      public VisitType objType;
      public Client client;

      public DateTime endTime = DateTime.MinValue;
      public string address;

      public string factAddress;
      public bool outOfRange;

      public double sum;
      private int visitNumber;

      public VisitQueueItem(DateTime dtVisit, Client client,
         double latitude, double longitude, VisitType objType)
      {
         this.startTime = dtVisit;
         this.latitude = latitude;
         this.longitude = longitude;
         this.objType = objType;
         this.client = client;
      }

      public string OrgName
      {
         get
         {
            return client != null 
               ? client.Name : string.Empty;
         }
      }

      public string StopTime
      {
         get
         {
            if (endTime.Equals(DateTime.MinValue) ||
               endTime.Equals(DateTime.Parse("01.01.1601")))
               return "";
            TimeSpan ts = endTime.Subtract(startTime);
            int min = (int)ts.TotalMinutes;
            return (min < 60) ? min + " мин." : (min / 60) + " ч" + (min % 60) + " мин.";
         }
      }

      public int VisitNumber { get { return visitNumber; } set { visitNumber = value; } }
   }

   public class CmpVisitQueueItem : IComparer<VisitQueueItem>
   {

      #region IComparer<VisitQueueItem> Members

      public int Compare(VisitQueueItem x, VisitQueueItem y)
      {
         return x.startTime.CompareTo(y.startTime);
      }

      #endregion
   }

   public class ObjType
   {
      public enum TObjType { OtOrder, OtVisit, OtOrgRemnants, DayDoc, PKO, Incass, Script, NotVisit, OutRoute }
      protected TObjType val;

      public ObjType(TObjType val)
      {
         this.val = val;
      }

      public ObjType(string objName)
      {
         FromString(objName);
      }

      protected ObjType() { }

      protected bool FromString(string objName)
      {
         switch (objName)
         {
            case "Order":
               val = TObjType.OtOrder;
               break;
            case "OrgRemnants":
               val = TObjType.OtOrgRemnants;
               break;
            case "Visit":
               val = TObjType.OtVisit;
               break;
            case "PKO":
               val = TObjType.PKO;
               break;
            case "Incass":
               val = TObjType.Incass;
               break;
            case "Script":
               val = TObjType.Script;
               break;
            case "NotVisit":
               val = TObjType.NotVisit;
               break;
            case "OutRoute":
               val = TObjType.OutRoute;
               break;
            default:
               return false;
         }
         return true;
      }

      public override string ToString()
      {
         switch (val)
         {
            case TObjType.OtOrder:
               return "Заявка";
            case TObjType.OtOrgRemnants:
               return "Съем остатков";
            case ObjType.TObjType.OtVisit:
               return "Посещение";
            case ObjType.TObjType.DayDoc:
               return "Рабочий день";
            case ObjType.TObjType.PKO:
               return "ПКО";
            case ObjType.TObjType.Incass:
               return "Инкассация";
            case ObjType.TObjType.Script:
               return "Визит";
            case ObjType.TObjType.NotVisit:
               return "Не посетил";
            case ObjType.TObjType.OutRoute:
               return "Не по маршруту";
            default: return string.Empty;
         }
      }

      public int CompareTo(ObjType ot)
      {
         return (int)val - (int)ot.val;
      }

      public bool Equals(ObjType.TObjType type)
      {
         return val == type;
      }

      public ObjType.TObjType Val { get { return val; } }
   }

   public class RoadPoint
   {
      public string Caption;
      public Location loc;
   }

   [Serializable]
   class SettingFmRoute : BaseFormSetting<SettingFmRoute>
   {
      public bool routePointsEnabled = false;
      public double routePointsInterval = 15.0;
   }
}
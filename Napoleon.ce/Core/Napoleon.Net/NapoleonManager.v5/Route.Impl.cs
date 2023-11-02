using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Globalization;
using System.Net;
using System.IO;
using System.Xml;
using GRSoft.Network;
using GRSoft.NapoleonManager.Maps;
using GRSoft.NapoleonManager.Utils;
using System.Reflection;
using System.Collections;

namespace GRSoft.NapoleonManager
{

   public partial class Route : Form, IRoute, FmSelectContrAgent.Selected
   {
      public const int SHEDULE_WEEK_COUNT = 4;
      public static readonly string DATE_FORMAT = "yyyy-MM-dd";
      protected DataSet<int, RouteTemplate> dsRouteTemplate;
      private DataSet<string, Org> dsOrg;
      private DataSet<string,PotenzialOrg> dsPtnzOrg;
      //protected OrgRouteQueue orgQueue = new OrgRouteQueue();
      private DataSet<int, CommonConfig> dsConfig;

      private Agent currentAgent;
      private int selectDayIdx = 0;

      //Экземпляр формы fmSelectContrAgentInstance, он необходим для того, если будут переключения 
      //текущего агента, то его нам надо закрывать, потому что у агентов может быть разный список организаций
      private FmSelectContrAgent fmSelectContrAgentInstance;
      private int weekStart = 0; // 0 -  понедельник, 1 - воскресенье

      protected class ControlOnChanges
      {
         private bool changed = false;
         private bool dateChanged = false;
         private ToolStripItem control;
         public bool enable = true;

         public ControlOnChanges(ToolStripItem control)
         {
            this.control = control;
            this.control.Enabled = false;
         }

         public void SetEnabled(bool enbl)
         {
            enable = enbl;
         }

         public void SetChanges()
         {
            if (enable)
            {
               changed = true;
               control.Enabled = true;
            }
         }

         public void SetDateChanges()
         {
            if (enable)
            {
               dateChanged = true;
               control.Enabled = true;
            }
         }

         public void ResetChanges()
         {
            changed = false;
            dateChanged = false;
            control.Enabled = false;
         }

         public bool IsChanged()
         {
            return changed;
         }

         public bool IsDateChanged()
         {
            return dateChanged;
         }
      }

      //Статус были ли сделаны "незаписанные" измениения, 
      //что бы задать вопрос на записть при закрытии формы
      protected ControlOnChanges routeWasChanged;

      // Route .ctor
      public void __Initing()
      {
         this.cbSelectDay.SelectionChangeCommitted += new System.EventHandler(this.cbSelectDay_SelectionChangeCommitted);
         this.cbAgents.SelectionChangeCommitted += new System.EventHandler(this.cbAgents_SelectionChangeCommitted);
         this.dtpStartRouteFrom.ValueChanged += new System.EventHandler(this.dtpStartRouteFrom_ValueChanged);
         this.dgvOrgs.CellContentClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvOrgs_CellContentClick);
         this.dgvOrgs.CellFormatting += new System.Windows.Forms.DataGridViewCellFormattingEventHandler(this.dgvOrgs_CellFormatting);
         this.dgvOrgs.CurrentCellDirtyStateChanged += new System.EventHandler(this.dgvOrgs_CurrentCellDirtyStateChanged);
         this.dgvOrgs.DragDrop += new System.Windows.Forms.DragEventHandler(this.dgvOrgs_DragDrop);
         this.dgvOrgs.DragEnter += new System.Windows.Forms.DragEventHandler(this.dgvOrgs_DragEnter);
         this.dgvOrgs.MouseDown += new System.Windows.Forms.MouseEventHandler(this.dgvOrgs_MouseDown);
         this.miSelectOrgLocation.Click += new System.EventHandler(this.miSelelctOrgLocation_Click);
         this.miAddTask.Click += new System.EventHandler(this.miAddTask_Click);
         this.tsbRouteSave.Click += new System.EventHandler(this.tsbRouteSave_Click);
         this.tsbRouteHistory.Click += new System.EventHandler(this.tsbRouteHistory_Click);

         //this.wb.DocumentCompleted += new System.Windows.Forms.WebBrowserDocumentCompletedEventHandler(this.wb_DocumentCompleted);
         this.tsbShowMap.Click += new System.EventHandler(this.tsbShowMap_Click);
         this.tsbSave.Click += new System.EventHandler(this.tsbSave_Click);
         this.tsbAddOrg.Click += new System.EventHandler(this.tsbAddOrg_Click);
         this.tsbDelete.Click += new System.EventHandler(this.tsbDelete_Click);
         this.tsbUp.Click += new System.EventHandler(this.tsbUp_Click);
         this.tsbDown.Click += new System.EventHandler(this.tsbDown_Click);
         this.btnReport.Click += new System.EventHandler(this.btnReport_Click);
         this.tsbExport.Click += new System.EventHandler(this.tsbExport_Click);
         this.dtpRouteStart.CloseUp += new System.EventHandler(this.dtpRouteStart_CloseUp);
         this.dtpRouteStart.ValueChanged += new System.EventHandler(this.MakeWeekColumnHeader);
         this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.Route_FormClosing);
         this.Load += new System.EventHandler(this.Route_Load);
         this.Shown += new System.EventHandler(this.Route_Shown);

         InitDataSets();
         AdjustForm();
         FetchStartDatas();


#if AGENT_ORG_TASK
         miAddTask.Visible = true;
#endif

#if COPY_AGENT_ROUTE
         tsbExport.Visible = true;
#endif

#if !SELECT_ORG_LOCATION
         miSelectOrgLocation.Visible = false;
#endif

#if ROUTE_HISTORY
         tslRouteStart.Visible = false;
         dtpRouteStart.Visible = false;
         tsbSave.Visible = false;
         btnReport.Margin = new Padding(1, btnReport.Margin.Top, btnReport.Margin.Right, btnReport.Margin.Bottom);
         dtpStartRouteFrom.Visible = true;
#else
         toolStripRoute.Visible = false;
#endif
      }

      //Настройка наборов данных
      private void InitDataSets()
      {
         dsRouteTemplate = new DataSet<int, RouteTemplate>(RouteTemplate.OBJECT_NAME, false);
         dsPtnzOrg = DataModule.Get(PotenzialOrg.OBJECT_NAME) == null ? new DataSet<string, PotenzialOrg>(PotenzialOrg.OBJECT_NAME) :
            (DataSet<string, PotenzialOrg>)DataModule.Get(PotenzialOrg.OBJECT_NAME);
         dsConfig = (DataSet<int, CommonConfig>)DataModule.Get(CommonConfig.OBJECT_NAME) ??
            new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME);
      }

      //Получить "необходимые" наборы данных
      private void FetchStartDatas()
      {
         DataModule.DataProcessed += new EventHandler(DataLoaded);
         Agents dsAgents = Agents.GetDataSet();
         dsAgents.Refresh(Config.GetConfig().GetConnection());
      }

      //Настройка формы
      private void AdjustForm()
      {
         this.Visible = false;
         dgvOrgs.AutoGenerateColumns = false;
         routeWasChanged = new ControlOnChanges(tsbSave);
         //wb.DocumentText 
         wb.Navigate(DefaultRoutePageMessage.GetContent(DefaultRoutePageMessage.STR_1));
      }

      //Запрос на сохранения измененных данных
      public virtual bool AskToSaveChanges()
      {
#if ORG_ROUTE_VIEW
         return false;
#else
         if (!routeWasChanged.IsChanged())
         {
            return false;
         }

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         return dr == DialogResult.Yes;
#endif
      }

      void CheckOrgFolder()
      {
         List<int> invalidFolder = new List<int>();
         foreach (KeyValuePair<int, RouteTemplate> kv in dsRouteTemplate)
         {
            List<RouteTemplate.Item> removed = new List<RouteTemplate.Item>();
            foreach (RouteTemplate.Item ofi in kv.Value.items)
            {
               //if (ofi.org == null || dsOrg.ContainsKey(ofi.name) == false)
               if (ofi.org == null)
               {
                  removed.Add(ofi);
                  continue;
               }
            }
            removed.ForEach(x => kv.Value.items.Remove(x));
            if (kv.Value.items.Count == 0)
               invalidFolder.Add(kv.Key);
            else
               kv.Value.items.Sort();
         }

         Agent rplAgent = null;
         foreach (int f in invalidFolder)
         {
            if (rplAgent == null)
               rplAgent = dsRouteTemplate[f].agent;
            dsRouteTemplate.Remove(f);
         }

         // если были косяки то попробуем сохранить изменения
         if (rplAgent != null)
         {
            List<ReplacedSet> replaced = new List<ReplacedSet>();
            replaced.Add(new ReplacedSet(rplAgent.id, dsRouteTemplate));
            DataModule.UpdateDataSet(null, null, replaced, Config.GetConfig().GetConnection());
         }
      }



      /// <summary>
      /// Взамен FillOrgQueueMaster
      /// </summary>
      /// <param name="day">null - для всех дней</param>
      /// <returns></returns>
      public static OrgRouteQueue GetRouteQueue(DataSet<int, RouteTemplate> ds, WeekDay day)
      {
         return new OrgRouteQueue(); 
      }

      public static OrgRouteQueue GetRouteQueue(DataSet<int, RouteTemplate> ds, int day)
      {
         OrgRouteQueue ret = new OrgRouteQueue();
         List<RouteTemplate> needAdd = new List<RouteTemplate>();

         foreach (KeyValuePair<int, RouteTemplate> kv in ds)
         {
            RouteTemplate of = kv.Value;
            if (day >= 0 && of.dayOfWeek != day)
               continue;

            needAdd.Add(of);


            foreach(RouteTemplate.Item ofi in of.items)
            {
               OrgRouteQueueItem fnd = ret.Find(ofi);

               if (fnd == null)
               {
                  fnd = OrgRouteQueueItem.Build(ret, ret.Count, ofi, of.dayOfWeek, of.weekIndex);
                  //fnd.SetItemActiveForWeek(of.WeekIndex);

                  ret.Add(fnd);
               }
               else
                  fnd.AddDay(of.dayOfWeek);
            }
         }

         if (needAdd.Count > 0)
            ret.AddOtherWeeks(needAdd);

         return ret;
      }

      private bool SelectAllDays()
      {
         return cbSelectDay.SelectedItem.Equals("<все>");
      }

      DateTime StartOfWeek
      {
         get
         {
            int dw = (int)DateTime.Today.DayOfWeek;
            int daySpan = (dw == 0) ? 6 : dw - 1;
            return DateTime.Today.Subtract(new TimeSpan(daySpan, 0, 0, 0));
         }
      }

      public DateTime StartOfWeekEx(DateTime dt, DayOfWeek startOfWeek)
      {
         int diff = (7 + (dt.DayOfWeek - startOfWeek)) % 7;
         return dt.AddDays(-1 * diff).Date;
      }

      //Применение фильтра и заполнения набора для отображения данных с учетом
      //фильтра, связь с гридом
      private void FillOrgGrid()
      {
         FmWait.CloseForm();
         Agent curAgent = cbAgents.SelectedItem as Agent;
         if( curAgent == null)
         {
            return;
         }

         dgvOrgs.SuspendLayout();

         // будем хранить начало расписания для каждого агента отдельно - мало ли что :)
         //CommonConfig cc = ConfigUtils.GetOrCreateConfig(dsConfig, ConfigKeyItems.SHEDULE_START, curAgent);
         //try
         //{
         //   dtpRouteStart.Value = cc.value.Length == 0 ? 
         //      StartOfWeek : 
         //      DateTime.ParseExact(cc.value,DATE_FORMAT,null);
         //}
         //catch (Exception)
         //{
         //   dtpRouteStart.Value = DateTime.Now;
         //}

         // будем хранить начало расписания для каждого агента отдельно - мало ли что :)
         
         CommonConfig cc = ConfigUtils.GetOrCreateConfig(dsConfig, ConfigKeyItems.WEEK_START, curAgent);
         Int32.TryParse(cc.value, out weekStart);


         int filter = GetSelectedDayID();

         if (dsRouteTemplate.Count > 0)
            dtpRouteStart.Value = dsRouteTemplate[0].firstDay;
         else
            dtpRouteStart.Value = DateTime.Now;

         CheckOrgFolder();

         OrgRouteQueue representation = GetRouteQueue(dsRouteTemplate, filter);
         if(filter == -1 )
            representation.DoSort("org", SortOrder.Ascending);
         dgvOrgs.DataSource = representation;

#if ROUTE_HISTORY
         UpdateHeaderFromHistory(dtpStartRouteFrom.Value);
#else
         UpdateColumnsHeader(dtpRouteStart.Value);
#endif
         dgvOrgs.ResumeLayout();
      }

      public static void AdjustIndex(DataSet<int, RouteTemplate> dsOrgFolder, OrgRouteQueue representation, WeekDay selectedDay)
      {
         //if (dsOrgFolder != null)

         //   foreach (OrgRouteQueueItem item in representation)
         //   {
         //      foreach (OrgFolder of in dsOrgFolder.Data)
         //      {
         //         if (WeekDay.CheckDay(of.name) == false)
         //            continue;

         //         WeekDay wd = new WeekDay(of.name);
         //         if (wd.Equals(selectedDay))
         //         {
         //            foreach (OrgFolderItem i in of.items)
         //            {
         //               if (i.name.Equals(item.OrgID))
         //                  item.Index = i.pos;
         //            }
         //         }
         //      }
         //   }
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

      private static Dictionary<string, Location> cachedLocations = new Dictionary<string,Location>();

      static bool initedProxy = false;
      static IWebProxy proxy = null;
      static ICredentials credentails = null;
      public static void FreeProxyInfo() { initedProxy = false; }

      public static XmlDocument OSMGeocoding(string address)
      {
         Location location = null;

         String req = "https://nominatim.openstreetmap.org/search?q=" + address + "&format=xml";
         HttpWebRequest request = (HttpWebRequest)WebRequest.Create(req);
         request.UserAgent = @"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36";
         //#if USE_MANAGER_LOG
         //         Log.Write(req);
         //#endif

#pragma warning disable 618
         if (!initedProxy)
         {
            credentails = null;
            proxy = null;

            Config c = Config.GetConfig();
            if (c.proxyIP.Length > 0)
            {
               proxy = new WebProxy(c.proxyIP + ":" + c.proxyPort.ToString(), false);
            }
            else
            {
               proxy = WebProxy.GetDefaultProxy();
               //proxy = null;
            }

            if (c.proxyLogin.Length > 0)
            {
               //CredentialCache credentialCache = new CredentialCache();
               //credentialCache.Add(new Uri("http://geocode-maps.yandex.ru"), "Kerberos", new NetworkCredential(c.proxyLogin, c.proxyPassword));
               //credentails = credentialCache;

               credentails = new NetworkCredential(c.proxyLogin, c.proxyPassword, c.proxyDomen);//, "prodo_ru");
               proxy.Credentials = credentails;
            }
            initedProxy = true;
         }

         //request.Proxy = WebProxy.GetDefaultProxy();
         //request.UseDefaultCredentials = true;
         request.Proxy = proxy;
         //request.Proxy.Credentials = credentails;
         //request.Credentials = credentails;

         //request.Method = "GET";
         //request.KeepAlive = true;
         //request.Accept = @"*/*";

         //request.Credentials = CredentialCache.DefaultCredentials;

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

         //#if USE_MANAGER_LOG
         //         Log.Write(sb.ToString());
         //#endif


         XmlDocument doc = new XmlDocument();
         doc.LoadXml(sb.ToString());

         return doc;
      }
      
      public static XmlDocument GetYandexRequest(string reqStr)
      {
         String req = "http://geocode-maps.yandex.ru/1.x/?geocode=" + reqStr +
            "&key=ANzXmEoBAAAAkjQFPwIAQphddQGLBfkqE3qCI3Vo8OCM8yYAAAAAAAAAAACYpxbA0OaEdJRuAHC50onaXfYLBQ==";
         HttpWebRequest request = (HttpWebRequest)WebRequest.Create(req);
//#if USE_MANAGER_LOG
//         Log.Write(req);
//#endif

#pragma warning disable 618
         if (!initedProxy)
         {
            credentails = null;
            proxy = null;

            Config c = Config.GetConfig();
            if (c.proxyIP.Length > 0)
            {
               proxy = new WebProxy(c.proxyIP + ":" + c.proxyPort.ToString(), false);
            }
            else
            {
               proxy = WebProxy.GetDefaultProxy();
               //proxy = null;
            }

            if (c.proxyLogin.Length > 0)
            {
               //CredentialCache credentialCache = new CredentialCache();
               //credentialCache.Add(new Uri("http://geocode-maps.yandex.ru"), "Kerberos", new NetworkCredential(c.proxyLogin, c.proxyPassword));
               //credentails = credentialCache;

               credentails = new NetworkCredential(c.proxyLogin, c.proxyPassword, c.proxyDomen);//, "prodo_ru");
               proxy.Credentials = credentails;
            }
            initedProxy = true;
         }

         //request.Proxy = WebProxy.GetDefaultProxy();
         //request.UseDefaultCredentials = true;
         request.Proxy = proxy;
         //request.Proxy.Credentials = credentails;
         //request.Credentials = credentails;

         //request.Method = "GET";
         //request.KeepAlive = true;
         //request.Accept = @"*/*";

         //request.Credentials = CredentialCache.DefaultCredentials;

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

//#if USE_MANAGER_LOG
//         Log.Write(sb.ToString());
//#endif

         XmlDocument doc = new XmlDocument();
         doc.LoadXml(sb.ToString());

         return doc;
      }

      static public bool IsNearestToOrg(Org org, ref Location check, DateTime pointTime, double accurace, DataSet<DateTime, GPSPos> route)
      {
         Location l = Route.GetLocation(org);
         if (l == null)
            return true;

         if (NapoleonManager.Location.Distance(l, check) < accurace)
            return true;

         NapoleonManager.Location check1 = FindNearesRoutePoint(pointTime, route);
         if (check1 != null && NapoleonManager.Location.Distance(l, check1) < accurace)
         {
            check.Latitude = check1.Latitude;
            check.Longitude = check1.Longitude;
            return true;
         }

         return false;
      }

      static NapoleonManager.Location FindNearesRoutePoint(DateTime dateTime, DataSet<DateTime, GPSPos> dsGPSPos)
      {
         NapoleonManager.Location check = null;
         List<DateTime> keys = new List<DateTime>(dsGPSPos.Keys);
         keys.Sort();
         foreach (DateTime key in keys)
         {
            if (key.CompareTo(dateTime) > 0)
            {
               GPSPos pos = dsGPSPos[key];
               check = new Location(pos.latitude, pos.longitude);
               break;
            }
         }

         return check;
      }

      static public Location GetLocation(Org org)
      {
         if (org.latitude != 0)
            return new Location(org.latitude, org.longitude);

         OrgLocations ol = OrgLocations.GetDataSet();
         OrgLocation loc = ol.GetLocation(org.id);
         if (loc != null)
         {
            Location l = new Location(loc.latitude, loc.longitude);
            if (org.Address.Length > 0)
               cachedLocations[org.Address] = l;
            return l;
         }

         if (org.Address.Length > 0 && cachedLocations.ContainsKey(org.Address))
            return cachedLocations[org.Address];

         return GetLocation(org.Address);
      }

      static public Location GetLocation(string address)
      {
         if (cachedLocations.ContainsKey(address))
         {
            return cachedLocations[address];
         }

#if NO_YANDEX_LOCATION
         return null;
#else
         Location location = null;
         if (address != null && address.Length > 0)
         {

            try
            {
               
//               XmlDocument doc = OSMGeocoding(address);

//               XmlNodeList result = doc.GetElementsByTagName("place");
//               foreach (XmlNode node in result)
//               {

//                  if (node.Attributes["class"].Value == "building")
//                  {
//                     location = new Location();
//                     CultureInfo en = CultureInfo.GetCultureInfo("en-US");
//                     location.Longitude = double.Parse(node.Attributes["lon"].Value, en);
//                     location.Latitude = double.Parse(node.Attributes["lat"].Value, en);

//                     break;
//                  }
//               }
//            }
//            catch (Exception e)
//            {
//#if USE_MANAGER_LOG
//               Log.Write("Exception " + e.Message);
//#endif
//               MessageBox.Show(e.Message, "Ошибка при получении адреса", MessageBoxButtons.OK, MessageBoxIcon.Stop);
//               using (StreamWriter w = new StreamWriter("log.txt", true))
//               {
//                  w.Write(e.Message);
//                  w.Flush();
//               }
//            }

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
            catch (Exception )
            {
#if USE_MANAGER_LOG
               Log.Write("Exception " + e.Message);
#endif
               //MessageBox.Show(e.Message, "Ошибка при получении адреса", MessageBoxButtons.OK, MessageBoxIcon.Stop);
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
#endif
      }

      //Событие Shown
      private void Route_Shown(object sender, EventArgs e)
      {
         cbSelectDay.SelectedIndex = 0;
         AdjustControlsBtns();
      }

      //Окончание выборки "стартовых" данных
      private void DataLoaded(object sender, EventArgs e)
      {
         DataModule.DataProcessed -= new EventHandler(DataLoaded);
         BeginInvoke(new EmptyParamHandler(FillListAgentsFromDataSet));
      }

      //Заполнение выпадающего списка агентов - "Агент"
      private void FillListAgentsFromDataSet()
      {
         Manager m = CurrentUser.user as Manager;

         List<Agent> al = new List<Agent>((IEnumerable<Agent>)m.GetAgents().Data);
         al.Sort();
         al.ForEach(x => cbAgents.Items.Add(x));
         //foreach (Agent a in m.GetAgents().Data)
         //   cbAgents.Items.Add(a);
         //cbAgents.Sorted = true;

         if (currentAgent != null)
         {
            SelectAgentInCombobox(currentAgent);
            RefreshDataSetsForAgent(currentAgent);
         }
         this.Visible = true;
      }

      //Установить текущего агента в cbAgents
      private void SelectAgentInCombobox(Agent agent)
      {
         foreach (object a in cbAgents.Items)
         {
            if ((a is Agent) && (a as Agent).Equals(agent))
            {
               cbAgents.SelectedItem = a; 
            }
         }
      }

      //Показать на карте
      private void tsbShowMap_Click(object sender, EventArgs e)
      {
         dgvOrgs.EndEdit();
         OrgRouteQueue routeQueue = GetConnectedDataSource();
         if (routeQueue.Count > 0)
         {
            DataGridViewSelectedRowCollection sr = dgvOrgs.SelectedRows;

            //FmWait.ShowForm(this, null);
            routeQueue = getRouteForWeek(routeQueue, cbWeek.SelectedIndex + 1);
            wb.Navigate(MapEngine.Route(Config.GetConfig().mapSource, routeQueue));
            //#if USE_MANAGER_LOG
            //            Log.Write("wb.DocumentText " + txt);
            //#endif
            //wb.DocumentText = txt;

#if MAKE_HTML_FILE
            File.WriteAllText("route.html", txt);
#endif
         }
         else
         {
            Agent agent = (cbAgents.SelectedItem as Agent);
            if (agent != null)
            {
               //wb.DocumentText = String.Format("<html><body>Нет данных для пользователя " +
               //"<font color=blue><b>{0}</b></font><body></html>",
               //agent.Name);
               wb.Navigate(String.Format("<html><body>Нет данных для пользователя " +
               "<font color=blue><b>{0}</b></font><body></html>",
               agent.Name));
            }
         }
      }

      OrgRouteQueue getRouteForWeek(OrgRouteQueue queue, int week)
      {
         OrgRouteQueue result = new OrgRouteQueue();
         foreach (OrgRouteQueueItem item in queue)
         {
            if (item.IsItemActiveForWeek(week))
               result.Add(item);
         }

         return result;
      }

      //Cобытие для инициации начала Drag&Drop, для изменения порядка в очереди
      private void dgvOrgs_MouseDown(object sender, MouseEventArgs e)
      {
         Location loc = new Location();

         if (e.Button == MouseButtons.Left)
         {
            if (dgvOrgs.Rows.Count > 0 && 
               dgvOrgs.SelectedRows.Count == 1 &&
               dgvOrgs.SelectedRows[0] != null)
            {
               System.Windows.Forms.DataGridView.HitTestInfo hti = dgvOrgs.HitTest(e.X, e.Y);
               int rowIndex = hti.RowIndex;
               int colIndex = hti.ColumnIndex;

               if (!cbSelectDay.SelectedItem.Equals("<все>") && 
                  rowIndex >= 0 &&
                  colIndex < 3)
               {
                  dgvOrgs.EndEdit();
                  DragDropObject ddo = new DragDropObject(this, dgvOrgs.Rows[rowIndex]);
                  dgvOrgs.DoDragDrop(ddo, DragDropEffects.Move); 
               }
            }
         }else if (e.Button == MouseButtons.Right)
         {
            DataGridView.HitTestInfo info = dgvOrgs.HitTest(e.X, e.Y);

            if (info.ColumnIndex != -1 && info.RowIndex != -1)
            {
               dgvOrgs.CurrentCell = dgvOrgs[info.ColumnIndex, info.RowIndex];
            }
         }
      }

      //Событие переменщения для Drag&Drop
      private void dgvOrgs_DragEnter(object sender, DragEventArgs e)
      {
         e.Effect = DragDropEffects.Move | DragDropEffects.Copy;
      }

      void AddOrg(object sender, Org org)
      {
         OrgRouteQueue representation = GetConnectedDataSource();
         if (representation == null)
         {
            return;
         }

         RouteTemplate.Item ofi = new RouteTemplate.Item();
         ofi.org = org;
         ofi.id = ofi.org.id;
         OrgRouteQueueItem orqi = OrgRouteQueueItem.Build(representation, representation.Count, ofi, GetSelectedDayID(), 0);
         //orqi.SetItemForAllWeek();

         //По просьбе Володи мы не вставляем новые строки в указанную позицию, а добавляем их вниз списка
         representation.Add(orqi);
         //representation.ApplyNewOrder();
         UpdateDataSourceConnection();
         routeWasChanged.SetChanges();

         if (fmSelectContrAgentInstance != null)
            fmSelectContrAgentInstance.Focus();
      }

      //Событе окночания Drag&Drop
      private void dgvOrgs_DragDrop(object sender, DragEventArgs e)
      {
         //Мы не разрешаем Drag&Drop если не включен фильтр по дням
         if (IsSelectedAllDays())
         {
            MessageBox.Show("Невозможно добавить/переместить контрагента, если включен фильтр \"<все>\"",
               "Ошибка", MessageBoxButtons.OKCancel, MessageBoxIcon.Error);
            return;
         }

         Point clientPoint = dgvOrgs.PointToClient(new Point(e.X, e.Y));
         int newRowIndex = dgvOrgs.HitTest(clientPoint.X, clientPoint.Y).RowIndex;

         DragDropObject ddo = e.Data.GetData(typeof(DragDropObject)) as DragDropObject;
         OrgRouteQueue representation = GetConnectedDataSource();

         if (ddo == null || representation == null)
         {
            return;
         }

         if (ddo.Source is Route)
         {
            DataGridViewRow rToMove = ddo.Data as DataGridViewRow;

            if (newRowIndex == rToMove.Index)
            {
              return;
            }

            OrgRouteQueueItem orqi = representation[newRowIndex];

            representation[newRowIndex] = representation[rToMove.Index];
            representation[rToMove.Index] = orqi;
            
         }
         else if (ddo.Source is FmSelectContrAgent)
         {
            // т.к. строки идут в обратном порядке придется нам их так же брать наоборот
            List<Org> rc = ddo.Data as List<Org>;
            int i = rc.Count-1;
            for( ; i >= 0; i--)
            {
               //DataGridViewRow row = rc[i];
               RouteTemplate.Item ofi = new RouteTemplate.Item();

               //ofi.org = row.DataBoundItem as Org;
               ofi.org = rc[i];
               ofi.id = ofi.org.id;
               OrgRouteQueueItem orqi = OrgRouteQueueItem.Build(representation, newRowIndex, ofi, GetSelectedDayID(), 0);
               //orqi.SetItemForAllWeek();
               //orgQueue.Insert(newRowIndex == -1 ? orgQueue.Count : newRowIndex, orqi);

               //По просьбе Володи мы не вставляем новые строки в указанную позицию, а добавляем их вниз списка
               representation.Add(orqi);
            }
         }

         //representation.ApplyNewOrder();
         UpdateDataSourceConnection();
         routeWasChanged.SetChanges();
      }
      
      //Пересоеденить источник данных
      private void UpdateDataSourceConnection()
      {
         OrgRouteQueue representation = GetConnectedDataSource();
         representation.ResetBindings();
      }

      //Сохранить изменения
      public void tsbSave_Click(object sender, EventArgs e)
      {
         SaveChanges();
      }

      private void SaveChanges()
      {
         SaveChangesLow(currentAgent, GetSelectedDayID());
      }


      void PrepareDataForWriting(List<int> removeDays, SimpleDataSet<RouteTemplate> wrds, Agent agent, int day, OrgRouteQueue representation)
      {
         RouteTemplate modifyFolder = null;
         
         removeDays.Add(day);

         for (int week = 1; week <= SHEDULE_WEEK_COUNT; week++)
         {
            modifyFolder = GetModifyOrgFolder(agent, day, week);

            int pos = 0;
            foreach (OrgRouteQueueItem orqi in representation)
            {
               if (orqi.IsItemActiveForWeek(week))
               {
                  RouteTemplate.Item src = orqi.Item;
                  RouteTemplate.Item ofi = new RouteTemplate.Item();
                  ofi.index = pos++;
                  ofi.id = src.id;
                  ofi.org = src.org;
                  modifyFolder.items.Add(ofi);
                  //modifyFolder.items.Add(orqi.Item);
               }
            }

            if (modifyFolder.items.Count == 0)
               removeDays.Add(modifyFolder.dayOfWeek);
            else
               wrds.Add(modifyFolder);
         }
      }

      void WriteRouteSQL(Agent agent, int day, OrgRouteQueue representation)
      {
         List<int> removeDays = new List<int>();
         SimpleDataSet<RouteTemplate> wrds = new SimpleDataSet<RouteTemplate>(RouteTemplate.OBJECT_NAME, false);

         PrepareDataForWriting(removeDays, wrds, agent, day, representation);

         List<IDataSet> wrSet = new List<IDataSet>();

         if (wrds.Count == 0)
            return;

         wrSet.Add(wrds);
         DateTime firstDay = wrds[0].firstDay;

         foreach (RouteTemplate t in dsRouteTemplate.Values)
            if (t.dayOfWeek != day)
            {
               t.firstDay = firstDay;
               wrds.Add(t);
            }
        

         // заменим маршрут 
         if (removeDays.Count > 0)
         {
            SimpleDataSet<RouteTemplate> rmv = new SimpleDataSet<RouteTemplate>(RouteTemplate.OBJECT_NAME, false);

            String rmvStr = "";
            foreach (int rday in removeDays)
            {
               int removeKey = GetOrgFolderKeyByDay(rday);
               if (removeKey != -1)
               {
                  //dsRouteTemplate.Remove(removeKey);
                  rmvStr += "'" + rday + "',";
               }
            }
            rmv.Filter = String.Format("\"userid\" = '{0}' and \"dayOfWeek\" in ({1})", agent.id, rmvStr.TrimEnd(new char[] {','}));
            DataModule.RemoveDataSet(rmv, Config.GetConfig().GetConnection());
         }

         if (DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()) == false)
         {
            MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
      }


      private void SaveChangesLow(Agent agent, int day)
      {
#if ORG_ROUTE_VIEW
         routeWasChanged.ResetChanges();
         return;
#else
         if (agent == null)
         {
            MessageBox.Show("Для данной операции не определен агент, обратитесь к разработчикам программы(Route.SaveChangesLow())",
               "Ошибка",MessageBoxButtons.OK, MessageBoxIcon.Error);
            return;
         }

         if (routeWasChanged.IsChanged())
         {
            dgvOrgs.EndEdit();
            OrgRouteQueue representation = GetConnectedDataSource();
            if (representation == null)
            {
               MessageBox.Show("Невозможно получить данные для сохранения", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
               return;
            }

            WriteRouteSQL(agent, day, representation);
         }
         else if (routeWasChanged.IsDateChanged())
         {
            List<IDataSet> wrSet = new List<IDataSet>();
            SimpleDataSet<RouteTemplate> wrds = new SimpleDataSet<RouteTemplate>(RouteTemplate.OBJECT_NAME, false);

            wrSet.Add(wrds);

            if (dsRouteTemplate.Values.Count == 0)
               return;
            
            DateTime firstDay = StartOfWeekEx(dtpRouteStart.Value.Date, weekStart == 1 ? DayOfWeek.Sunday : DayOfWeek.Monday);

            foreach (RouteTemplate t in dsRouteTemplate.Values)
               if (t.dayOfWeek != day)
               {
                  t.firstDay = firstDay;
                  wrds.Add(t);
               }

            if (DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()) == false)
            {
               MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
         }

         routeWasChanged.ResetChanges();
#endif
      }

      private RouteTemplate GetModifyOrgFolder(Agent agent, int day, int week)
      {
         RouteTemplate result = GetOrgFolderByDay(day, week);

         if (result == null)
         {
#if ROUTE_HISTORY
            result = new OrgRouteShedule();
#else
            result = new RouteTemplate();
#endif
            result.agent = agent;
            result.items = new List<RouteTemplate.Item>();
            result.dayOfWeek = day;
            result.weekIndex = week;
            
            dsRouteTemplate.Add(GetOrgFolderKey(), result);
         }

         result.firstDay = StartOfWeekEx(dtpRouteStart.Value.Date, weekStart == 1 ? DayOfWeek.Sunday : DayOfWeek.Monday);
         result.items.Clear();

         return result;
      }

      protected OrgRouteQueue GetConnectedDataSource()
      {
         return (OrgRouteQueue)dgvOrgs.DataSource;
      }

      private int GetOrgFolderKey()
      {
         int result = -1;

         foreach(KeyValuePair<int, RouteTemplate> kvp in dsRouteTemplate)
         {
            if (kvp.Key > result)
               result = kvp.Key;
         }

         return result + 1;
      }

      private RouteTemplate GetOrgFolderByDay(int day, int week)
      {
         RouteTemplate result = null;

         foreach (RouteTemplate of in dsRouteTemplate.Data)
         {
            if (of.dayOfWeek == day && of.weekIndex == week)
            {
               result = of;
               break;
            }
         }

         return result;
      }

      private int GetOrgFolderKeyByDay(int day)
      {
         int result = -1;

         foreach (KeyValuePair<int, RouteTemplate> kvp in dsRouteTemplate)
         {
            if (kvp.Value.dayOfWeek.Equals(day))
               return kvp.Key;
         }

         return result;
      }

      private void tsbAddOrg_Click(object sender, EventArgs e)
      {
         fmSelectContrAgentInstance = FmSelectContrAgent.ShowForm(dsOrg, dsPtnzOrg, this, AddOrg, this);
      }

      protected string GetSelectedDay()
      {
         return cbSelectDay.SelectedItem.ToString();
      }

      protected int GetSelectedDayID()
      {
         if (selectDayIdx == 0)
            return -1;

         int res = selectDayIdx;

         if (res == 7)
            return 0;

         return res;
      }

      protected bool IsSelectedAllDays()
      {
         return GetSelectedDay().Equals("<все>");
      }

      private void tsbDelete_Click(object sender, EventArgs e)
      {
         OrgRouteQueue representation = GetConnectedDataSource();

         if (IsSelectedAllDays() || representation == null || representation.Count == 0 )
         {
            return;
         }

         string POMPT_MSG = dgvOrgs.SelectedRows.Count > 1 ? "Удалить выбранных контрагентов?" :
            "Удалить выбранного контрагента?";

         if (MessageBox.Show(POMPT_MSG, "Вопрос", MessageBoxButtons.OKCancel,
            MessageBoxIcon.Question) != DialogResult.OK)
         {
            return;
         }

         foreach (DataGridViewRow dgvr in dgvOrgs.SelectedRows)
         {
            representation.RemoveAt(dgvr.Index);
         }

         //representation.ApplyNewOrder();
         routeWasChanged.SetChanges();
         UpdateDataSourceConnection();
      }

      //Изменения порядка следования организаций в маршруте
      private void SwitchOrder(Direction direction)
      {
         OrgRouteQueue representation = GetConnectedDataSource();

         if (IsSelectedAllDays() || 
            dgvOrgs.SelectedRows.Count != 1 || 
            representation == null || 
            representation.Count == 0)
         {
            return;
         }

         int pos = dgvOrgs.SelectedRows[0].Index + (int)direction;

         if (pos < 0 || pos >= dgvOrgs.Rows.Count)
         {
            return;
         }

         OrgRouteQueueItem orgi = representation[pos];
         representation[pos] = representation[dgvOrgs.SelectedRows[0].Index];
         representation[dgvOrgs.SelectedRows[0].Index] = orgi;

         //representation.ApplyNewOrder();
         UpdateDataSourceConnection();

         dgvOrgs.MultiSelect = false;
         dgvOrgs.MultiSelect = true;
         dgvOrgs.Rows[pos].Selected = true;

         routeWasChanged.SetChanges();
      }

      private void tsbUp_Click(object sender, EventArgs e)
      {
         SwitchOrder(Direction.UP);
      }

      private void tsbDown_Click(object sender, EventArgs e)
      {
         SwitchOrder(Direction.DOWN);
      }

      //Показать форму и установить "текущего" агента
      public static void Show(Agent agent)
      {
#if NO_ROUTE_EDITOR
#else
         Type prcType = FormEntries.GetFormType(typeof(Route));
         ConstructorInfo ci = prcType.GetConstructor(Type.EmptyTypes);
         IRoute fmRoute = (IRoute)ci.Invoke(new object[] { });
         fmRoute.SetCurrentAgent(agent);
         ((Form)fmRoute).Show();
#endif
      }

      class ETextNotFound : Exception
      {
         private string text;

         public ETextNotFound(string text)
         {
            this.text = text;
         }

         public override string Message
         {
            get
            {
               return String.Format("Строка текста \"{0}\" не найдена.", text);
            }
         }
      }

      private object GetComboboxItemByText(string text, ComboBox comboBox)
      {
         foreach (object item in comboBox.Items)
         {
            if (item.ToString().Equals(text))
            {
               return item;
            }
         }

         throw new ETextNotFound(text);
      }

      //Событие изменение дня, если были какие то изменения маршрута, то даем запрос на сохранение
      private void cbSelectDay_SelectionChangeCommitted(object sender, EventArgs e)
      {
         try
         {
            if (AskToSaveChanges())
            {
               Agent agent = (Agent)cbAgents.SelectedItem;
               string day = cbSelectDay.Tag as string;

               if (day != null && !day.Equals("<все>"))
                  SaveChangesLow(agent, selectDayIdx);
            }

            selectDayIdx = cbSelectDay.SelectedIndex;

            cbSelectDay.Tag = cbSelectDay.SelectedItem;
            string txt = DefaultRoutePageMessage.GetContent(DefaultRoutePageMessage.STR_2);
            wb.Navigate(txt);
            //wb.DocumentText = DefaultRoutePageMessage.GetContent(DefaultRoutePageMessage.STR_2);
            tsbSave.Enabled = false;
            AdjustControlsBtns();

            FillOrgGrid();

            dgvOrgs.ReadOnly = cbSelectDay.SelectedItem.Equals("<все>");
         }
         catch
         { 
         }
      }

      DateTime GetRouteStart()
      {
         return dtpStartRouteFrom.Value.Date;
      }

      protected virtual void AdjustControls(bool selectAllDays)
      {
         tsbAddOrg.Enabled = !selectAllDays;
         tsbDelete.Enabled = !selectAllDays;
         tsbUp.Enabled = !selectAllDays;
         tsbDown.Enabled = !selectAllDays;
         UpdateRouteSave(selectAllDays);

         dtpStartRouteFrom.Enabled = !selectAllDays;

         if (selectAllDays)
            CloseSelectContrAgentForm();
      }

      private void AdjustControlsBtns()
      {
         AdjustControls(IsSelectedAllDays());
      }

      void SelectCurrentAgent(Agent a)
      {
         currentAgent = a;
         //wb.DocumentText
         wb.Navigate(DefaultRoutePageMessage.GetContent(DefaultRoutePageMessage.STR_2));

         cbSelectDay.SelectedIndex = 0;
         dgvOrgs.ReadOnly = true;
         RefreshDataSetsForAgent(currentAgent);
         AdjustControlsBtns();
      }

      //Событие изменение текущего агента, если были какие то изменения маршрута, то даем запрос на сохранение
      private void cbAgents_SelectionChangeCommitted(object sender, EventArgs e)
      {
         try
         {
            if (currentAgent != null && AskToSaveChanges())
            {
               SaveChangesLow(currentAgent, GetSelectedDayID());
            }

            Agent a = cbAgents.SelectedItem as Agent;
            if (a == null)
               return;
            SelectCurrentAgent(a);
         }
         finally
         {
            CloseSelectContrAgentForm();
         }
      }

      private void CloseSelectContrAgentForm()
      {
         try
         {
            if( fmSelectContrAgentInstance != null )
               fmSelectContrAgentInstance.Close();
         }
         catch
         {
         }
      }

      //Обновить наборы данных для агента
      private void RefreshDataSetsForAgent(Agent agent)
      {
         const string USERID_IN_STR = "\"userid\" in ('{0}')";
         //DataModule.DataProcessed += new EventHandler(OrgFolderLoaded);

         List<IDataSet> refreshList = new List<IDataSet>();

         dsOrg = DataModule.GetUserDataSet(agent.id, "Org", typeof(DataSet<string, Org>)) as DataSet<string, Org>;
         dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agent.id), dsOrg.Name);

         dsPtnzOrg.Filter = String.Format(USERID_IN_STR, agent.id);
         dsConfig.Filter = "(not (\"userid\" is null)) or \"userid\" is null";

         //if (dsConfig.Count == 0)
         refreshList.Add(dsConfig);

         refreshList.Add(dsOrg);
         refreshList.Add(dsPtnzOrg);

#if ROUTE_HISTORY
         dsOrgRouteShedule = DataModule.GetUserDataSet(agent.id, OrgRouteShedule.CURRENT_ROUTE_NAME, typeof(SimpleDataSet<OrgRouteShedule>), true) as
            SimpleDataSet<OrgRouteShedule>;
         refreshList.Add(dsOrgRouteShedule);
#else
         dsRouteTemplate = DataModule.GetUserDataSet(agent.id, RouteTemplate.OBJECT_NAME, typeof(DataSet<int, RouteTemplate>), true) as DataSet<int, RouteTemplate>;
         refreshList.Add(dsRouteTemplate);
#endif

         OrgLocations ol = OrgLocations.GetDataSet();
         refreshList.Add(ol);

         UpdateRefreshList(refreshList);
         FmWait.StdDataRefresh(this, refreshList, FillOrgGrid);
         //FmWait.StdDataRefresh(this, refreshList, DoLoadData);
      }

      public virtual void UpdateRefreshList(List<IDataSet> list)
      { 
      }

      //Событие закрытие формы, задаем вопрос "сохранить изменения", если они были
      private void Route_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (AskToSaveChanges())
         {
            SaveChanges();
         }
      }

      private void dgvOrgs_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         DrawCell(e);
      }

      protected virtual void DrawCell(DataGridViewCellFormattingEventArgs e)
      {
         OrgRouteQueue data = dgvOrgs.DataSource as OrgRouteQueue;
         if (data != null)
         {
            OrgRouteQueueItem item = data[e.RowIndex];
            if (item.Item.org is PotenzialOrg)
               e.CellStyle.ForeColor = Color.Red;
         }
      }

      private void dgvOrgs_CellContentClick(object sender, DataGridViewCellEventArgs e)
      {
         if (!((DataGridView)sender).ReadOnly)
            routeWasChanged.SetChanges();
      }

      private void Route_Load(object sender, EventArgs e)
      {
         dgvOrgs.ReadOnly = true;
         cbWeek.SelectedIndex = 0;
      }

      private void dtpRouteStart_CloseUp(object sender, EventArgs e)
      {
         routeWasChanged.SetDateChanges();
      }

      DateTime WeekDate(WeekDay wd, int weekIndex)
      {
         int needDate = wd.Number;
         DateTime dt = DateTime.Today;
         int curDate = (int)dt.DayOfWeek;
         if (curDate == 0) curDate = 7;
      
         dt = dt.AddDays(needDate - curDate);
         dt = dt.AddDays(7 * weekIndex);

         return dt;
      }

      string GetDate(WeekDay wd, int weekIndex)
      {
         return WeekDate(wd, weekIndex).ToString("dd/MM/yy");
      }

      void UpdateColumnsHeader(DateTime value)
      {
         WeekDay wd = new WeekDay((!SelectAllDays()) ? cbSelectDay.SelectedItem as string : "Понедельник");

         // найдем индекс текущей недели
         TimeSpan ts = new TimeSpan(DateTime.Today.Ticks);
         ts = ts.Subtract(new TimeSpan(value.Ticks));
         if (wd != null)// && ts.TotalDays >= 0)
         {
            int curWeekIndex = (int)(ts.TotalDays / 7) % 4;
            List<DataGridViewColumn> columns = new List<DataGridViewColumn>();

            curWeekIndex = -curWeekIndex;
            dgvOrgW1.HeaderText = GetDate(wd, curWeekIndex++);
            dgvOrgW2.HeaderText = GetDate(wd, curWeekIndex++);
            dgvOrgW3.HeaderText = GetDate(wd, curWeekIndex++);
            dgvOrgW4.HeaderText = GetDate(wd, curWeekIndex++);
         }
      }

      void UpdateHeaderFromHistory(DateTime startFrom)
      {
         WeekDay wd = new WeekDay((!SelectAllDays()) ? cbSelectDay.SelectedItem as string : "Понедельник");
         int curWeekIndex;

         if (startFrom.CompareTo(DateTime.Today) < 0)
         {
            TimeSpan ts = new TimeSpan(DateTime.Today.Ticks);
            ts = ts.Subtract(new TimeSpan(startFrom.Ticks));
            curWeekIndex = (int)(ts.TotalDays / 7) % 4;
            curWeekIndex = -curWeekIndex;
         }
         else
         {
            // найдем индекс текущей недели
            TimeSpan ts = new TimeSpan(startFrom.Ticks);
            ts = ts.Subtract(new TimeSpan(DateTime.Today.Ticks));

            curWeekIndex = (int)((ts.TotalDays + 6) / 7);
         }

         List<DataGridViewColumn> columns = new List<DataGridViewColumn>();

       
         //curWeekIndex = -curWeekIndex;
         dgvOrgW1.HeaderText = GetDate(wd, curWeekIndex++);
         dgvOrgW2.HeaderText = GetDate(wd, curWeekIndex++);
         dgvOrgW3.HeaderText = GetDate(wd, curWeekIndex++);
         dgvOrgW4.HeaderText = GetDate(wd, curWeekIndex++);
      }

      private void MakeWeekColumnHeader(object sender, EventArgs e)
      {
         UpdateColumnsHeader(dtpRouteStart.Value);
      }

      public static Location GetFirstKnownPoint(IList<OrgRouteQueueItem> queue)
      {
         Location loc = null;

         foreach (OrgRouteQueueItem item in queue)
         {
            if(item.Item.org != null)
            {
               loc = Route.GetLocation(item.Item.org);
            }

            if (loc != null)
               break;
         }

         return loc;
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         MakeReport();
      }

      protected virtual void MakeReport()
      {
         Invoke(new EmptyParamHandler(
            delegate
            {
               OrgRouteQueue orgQueue = new OrgRouteQueue();
               //FillOrgQueueMaster();
               RouteReport report = new RouteReport("route_report_{0}.html");
               RouteReportData data = new RouteReportData();
               data.agent = cbAgents.SelectedItem as Agent;
               data.queue.AddRange(orgQueue);
               report.Build(data, dsRouteTemplate);
               report.Show();
            }));
      }

      private void dgvOrgs_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         int clmnIndex = dgvOrgs.CurrentCell.ColumnIndex;
         if (clmnIndex == dgvOrgW1.DisplayIndex || clmnIndex == dgvOrgW2.DisplayIndex ||
            clmnIndex == dgvOrgW3.DisplayIndex || clmnIndex == dgvOrgW4.DisplayIndex)
         {
            dgvOrgs.CommitEdit(DataGridViewDataErrorContexts.Commit);
         }
      }

      public void SetCurrentAgent(Agent a)
      {
         currentAgent = a;
      }

      public bool IsSelected(Org o)
      {
         OrgRouteQueue representation = GetConnectedDataSource();
         foreach (OrgRouteQueueItem i in representation)
            if (i.Item.id == o.id)
               return true;

         foreach(RouteTemplate of in dsRouteTemplate.Data)
            foreach (RouteTemplate.Item ofi in of.items)
               if (ofi.id == o.id)
                  return true;

         return false;
      }

      private void tsbExport_Click(object sender, EventArgs e)
      {
         if( routeWasChanged.IsChanged() )
         {
            if (!AskToSaveChanges())
               return;
            SaveChanges();
         }

         if(dsRouteTemplate.Count == 0)
         {
            MessageBox.Show("Перед копированием необходимо сначала завести маршрут.");
            return;
         }

         FmCopyRoute form = new FmCopyRoute();
         form.SetCopiedAgent(currentAgent);
         if (form.ShowDialog() == System.Windows.Forms.DialogResult.OK && form.SelectedAgent != null)
         {
            DBConnection conn = Config.GetConfig().GetConnection();
            Agent newAgent = form.SelectedAgent;
            SimpleDataSet<RouteTemplate> of = new SimpleDataSet<RouteTemplate>(RouteTemplate.OBJECT_NAME, false);
            of.Filter = String.Format("\"userid\" = '{0}'", currentAgent.id);
            DataModule.RefreshGiveSets(conn, of, null).Join();

            foreach (RouteTemplate oi in of.Data)
            {
               oi.userid = newAgent.id;
               oi.agent = newAgent;
            }

            List<IDataSet> wrSet = new List<IDataSet>();
            List<ReplacedSet> rpl = new List<ReplacedSet>(new ReplacedSet[] { new ReplacedSet(newAgent.id, of) });

            if (DataModule.UpdateDataSet(wrSet, null, rpl, conn))
            {
               cbAgents.SelectedItem = newAgent;
               SelectCurrentAgent(newAgent);
            }
            else
               MessageBox.Show("Ошибка при записи");
         }
      }

      private void miSelelctOrgLocation_Click(object sender, EventArgs e)
      {
#if SELECT_ORG_LOCATION
         Agent agent = (cbAgents.SelectedItem as Agent);
         
         if (agent != null)
         {
            if(dgvOrgs.CurrentRow != null)
            {
               OrgRouteQueueItem q = dgvOrgs.CurrentRow.DataBoundItem as OrgRouteQueueItem;
            
               if(q != null && q.OrgID != null)
               {
                  Type t = FormEntries.GetFormType(typeof(SelectOrgLocation));
                  ConstructorInfo ci = t.GetConstructor(Type.EmptyTypes);
                  SelectOrgLocation form = (SelectOrgLocation)ci.Invoke(null);
                  form.Agent = agent;
                  form.orgid = q.OrgID;
                  form.Show();
               }
            }
         }
#endif
      }

      void UpdateRouteSave(bool isAllDays)
      {
         bool enabled = false;
         if (!isAllDays)
         {
            DateTime routeStart = GetRouteStart();
            DateTime checkDate = DateTime.Now.Date;
            int cmp = routeStart.CompareTo(checkDate);
            if (cmp >= 0)
            {
               WeekDay wd = new WeekDay(cbSelectDay.SelectedItem as string);
               WeekDay cd = new WeekDay(routeStart.DayOfWeek);
               enabled = cd.Number == wd.Number;
            }
         }
         tsbRouteSave.Enabled = enabled;
      }

      private void dtpStartRouteFrom_ValueChanged(object sender, EventArgs e)
      {
         UpdateRouteSave(IsSelectedAllDays());
         UpdateHeaderFromHistory(dtpStartRouteFrom.Value);
      }


      //bool WriteShedule(DateTime routeStart, Agent agent, int day, OrgRouteQueue representation)
      //{
      //   List<int> removeDays = new List<int>();
      //   SimpleDataSet<RouteTemplate> updated = new SimpleDataSet<RouteTemplate>(RouteTemplate.OBJECT_NAME, false);

      //   PrepareDataForWriting(removeDays, updated, agent, day, representation);

      //   string rmv = String.Format("\"userid\" = '{0}' and \"name\" like '%{1}' and \"dateFrom\" = ToDate('{2:dd/MM/yyyy}')", agent.id, day, routeStart);
      //   SimpleDataSet<OrgRouteShedule> wrds = new SimpleDataSet<OrgRouteShedule>(OrgRouteShedule.ROUTE_OBJECT_NAME, false);
      //   foreach (OrgFolder of in updated.Data)
      //   {
      //      OrgRouteShedule ors = (OrgRouteShedule)of;
      //      ors.dateFrom = routeStart;
      //      wrds.Add(ors);
      //   }

      //   bool res = DataModule.ReplaceDataSet(wrds, Config.GetConfig().GetConnection(), rmv);
      //   if (!res)
      //   {
      //      MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
      //   }
      //   return res;
      //}

      public void tsbRouteSave_Click(object sender, EventArgs e)
      {
         //DateTime routeStart = GetRouteStart();
         //if(routeStart.CompareTo(DateTime.Now.Date) < 0)
         //{
         //   return;
         //}

         //dgvOrgs.EndEdit();
         //OrgRouteQueue representation = GetConnectedDataSource();
         //if( WriteShedule(routeStart, currentAgent, GetSelectedDayID(), representation) )
         //{
         //   routeWasChanged.ResetChanges();
         //}
      }

      private void tsbRouteHistory_Click(object sender, EventArgs e)
      {
         FmRouteHistory.Open(currentAgent, this);
      }

      public void ShowHistoryDay(WeekDay wd, List<RouteTemplate> dayShedule)
      {
         //dsOrgRouteShedule.Clear();
         //List<int> rmv = new List<int>();
         //foreach(KeyValuePair<int, OrgRouteShedule> kv in dsOrgRouteShedule)
         //{
         //   if (wd.Equals(kv.Value.name))
         //      rmv.Add(kv.Key);
         //}
         //rmv.ForEach(x => dsOrgRouteShedule.Remove(x));
         //dayShedule.ForEach(x => dsOrgRouteShedule.Add(x));

         //cbSelectDay.SelectedIndex = wd.Number;
         //cbSelectDay_SelectionChangeCommitted(this, EventArgs.Empty);
      }

      private void miAddTask_Click(object sender, EventArgs e)
      {
#if AGENT_ORG_TASK
         Agent agent = (cbAgents.SelectedItem as Agent);

         if (agent != null)
         {
            if (dgvOrgs.CurrentRow != null)
            {
               OrgRouteQueueItem q = dgvOrgs.CurrentRow.DataBoundItem as OrgRouteQueueItem;

               if (q != null && q.Item.org != null)
               {
                  DateTime taskDate = DateTime.Now;
                  DateTime startDate = dtpRouteStart.Value;
                  WeekDay wd = new WeekDay((!SelectAllDays()) ? cbSelectDay.SelectedItem as string : "Понедельник");

                  // найдем индекс текущей недели
                  TimeSpan ts = new TimeSpan(DateTime.Today.Ticks);
                  ts = ts.Subtract(new TimeSpan(startDate.Ticks));
                  if (wd != null)// && ts.TotalDays >= 0)
                  {
                     int curWeekIndex = (int)(ts.TotalDays / 7) % 4;
                     while (true)
                     {
                        DateTime cd = WeekDate(wd, curWeekIndex++);
                        if (cd > taskDate)
                        {
                           taskDate = cd;
                           break;
                        }
                     }

                     OrgTask task = new OrgTask();
                     task.id = GRSoft.Network.DataObject.GenId(); ;
                     task.orgid = q.OrgID;
                     task.start = taskDate;
                     task.finish = taskDate;
                     task.userid = agent.id;
                     task.created = DateTime.Now;
                     task.manager = CurrentUser.user.User.id;

                     OrgTask ot = FmAgentTaskEdit.EditTask(task);
                     if(ot != null)
                     {
                        SimpleDataSet<OrgTask> wrTask = new SimpleDataSet<OrgTask>(OrgTask.OBJECT_NAME, false);
                        wrTask.Add(ot);
                        DataModule.WriteDataSet(new List<IDataSet>(new IDataSet[] { wrTask }), Config.GetConfig().GetConnection());
                     }
                  }
               }
            }
         }
#endif
      }
   }

   public class Location
   {
      private double latitude  = 0.0;
      private double longitude = 0.0;
      private bool isGsm = false;
      public bool isVisitPoint = false; 

      public double speed = 0.0;
      public DateTime date = DateTime.MinValue;

      public Location(){}

      public Location(double latitude, double longitude, bool isGsm, double speed, DateTime date)
      {
         this.latitude  = latitude;
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

      public bool IsEmpty { get { return latitude == 0 && longitude == 0; } }

      public string GetAddress()
      {
         string address = "";
#if NO_YANDEX_LOCATION
         return address;
#else
         try
         {
            if (addresses.ContainsKey(this))
               address = addresses[this];
            else
            {
               CultureInfo enus = CultureInfo.GetCultureInfo("en-US");
               XmlDocument doc = Route.GetYandexRequest(longitude.ToString(enus) + "," + latitude.ToString(enus));
               XmlNamespaceManager nsmgr = new XmlNamespaceManager(doc.NameTable);
               nsmgr.AddNamespace("ab", "http://maps.yandex.ru/geocoder/1.x");
               XmlNode res = doc.SelectSingleNode("//ab:GeocoderMetaData/ab:text", nsmgr);
               if(res != null)
               {
                  char[] sep = new char[] {','};
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
         catch (Exception )
         {
         }
         return address;
#endif
      }

      private static Dictionary<Location, string> addresses = new Dictionary<Location,string>();
   }

   //public class AgentItem
   //{
   //   public string id;
   //   public string name;

   //   public AgentItem(Agent a)
   //   {
   //      id = a.id;
   //      name = a.name;
   //   }

   //   public override string ToString()
   //   {
   //      return name;
   //   }
   //}

   public partial class OrgRouteQueueItem : CmpByField<OrgRouteQueueItem>
   {
      //private int pos;
      protected RouteTemplate.Item org;
      protected List<int> days = new List<int>();
      private bool w1 = false;
      private bool w2 = false;
      private bool w3 = false;
      private bool w4 = false;
      //private int index;
      
      IList owner;

      //public OrgRouteQueueItem(IList<OrgRouteQueueItem> owner, int pos, OrgFolderItem org, WeekDay day)
      //{
      //   //this.pos = pos;
      //   this.org = org;
      //   //index = pos+1;
      //   days.Add(day);
      //   this.owner = owner;
      //}

      public OrgRouteQueueItem(IList owner, int pos, RouteTemplate.Item org, int day, int week) 
         //:this(owner, pos, org, day)
      {
         this.org = org;
         days.Add(day);
         this.owner = owner;
         SetItemActiveForWeek(week);
      }


      public static OrgRouteQueueItem Build(IList owner, int pos, RouteTemplate.Item org, int day, int week)
      {
         Type t = FormEntries.GetFormType(typeof(OrgRouteQueueItem));
         ConstructorInfo cs = t.GetConstructor(new Type[] {
            typeof(IList), typeof(int), typeof(RouteTemplate.Item), typeof(int), typeof(int) });

         if(cs != null)
         {
            object[] prm = new object[]
            {
               owner, pos, org, day, week
            };
            return (OrgRouteQueueItem)cs.Invoke(prm);
         }
         return new OrgRouteQueueItem(owner, pos, org, day, week);
      }

      //public int Pos { get { return pos + 1; } set { pos = value; } }
      //public int Index { get { return index; } set { index = value; } }

      public int Pos { get { return Index + 1; } }
      public int Index { get { return owner.IndexOf(this); } }

      public RouteTemplate.Item Item { get { return org; } }
      public string OrgName { get { return org == null || org.org == null ? string.Empty : org.org.Name; } }
      public string OrgID { get { return org == null || org.org == null ? string.Empty : org.org.id; } }
      public string Day
      {
         get
         {
            StringBuilder sb = new StringBuilder();
            foreach (int d in days)
            {
               WeekDay wd = new WeekDay(d);

               if (sb.Length > 0)
               {
                  sb.Append(", ");
               }

               sb.Append(wd.ShortName);
            }

            return sb.ToString();
         }
      }

      public bool ContainsDay(int wd)
      {
         return days.Contains(wd);
      }


      public string Address { get { return org == null || org.org == null ? string.Empty : org.org.Address; } }
      //public Location Location
      //{
      //   get
      //   {
      //      Location result = null;
      //      if (org != null && org.org != null)
      //      {
      //         OrgLocations ol = OrgLocations.GetDataSet();
      //         OrgLocation loc = ol.GetLocation(org.org.id);
      //         if( loc != null )
      //            result = new Location(loc.latitude, loc.longitude);
      //         else if (org.org.latitude != 0 && org.org.longitude != 0)
      //            result = new Location(org.org.latitude, org.org.latitude);
      //      }

      //      return result;
      //   }
      //}

      public void AddDay(int wd)
      {
         if (!days.Contains(wd))
            days.Add(wd);
      }

      public void AddDays(List<int> day)
      {
         foreach (int d in day)
            if (!days.Contains(d))
               days.Add(d);
      }

      public List<int> GetDays()
      {
         return days;
      }

      [Compare]
      public static CompareCondition CC = new CompareCondition();

      public bool IsItemActiveForWeek(int weekNumber)
      {
         switch (weekNumber)
         {
            case 1: return w1;
            case 2: return w2;
            case 3: return w3;
            case 4: return w4;
            case 0: return true;
            default: return false;
         }
      }

      public void SetItemActiveForWeek(int weekNumber)
      {
         switch (weekNumber)
         {
            case 1: w1 = true; break;
            case 2: w2 = true; break;
            case 3: w3 = true; break;
            case 4: w4 = true; break;
            case 0:
               SetItemForAllWeek();
               break;
         }
      }

      public void SetItemForAllWeek()
      {
         w1 = w2 = w3 = w4 = true;
      }

      public bool W1 { get { return w1; } set { w1 = value; } }
      public bool W2 { get { return w2; } set { w2 = value; } }
      public bool W3 { get { return w3; } set { w3 = value; } }
      public bool W4 { get { return w4; } set { w4 = value; } }
   }

   public class OrgRouteQueue : BindingList<OrgRouteQueueItem>
   {
      public void DoSort(string cmpField, SortOrder sortOrder)
      {
         if (cmpField == "pos" || cmpField == "index")
            return;

         OrgRouteQueueItem.CC.SetCompareCondition(cmpField, sortOrder == SortOrder.Ascending);

         (Items as List<OrgRouteQueueItem>).Sort();
         //ApplyNewOrder();
      }

      public void ApplyNewOrder()
      {
         //int counter = 1;
         //foreach (OrgRouteQueueItem orqi in this)
         //{
         //   orqi.Pos = counter++;
         //}
      }

      public void AddItem(OrgRouteQueueItem item, bool applayWeek)
      {
         //Console.WriteLine(item.OrgName.ToString());
         OrgRouteQueueItem orqi = Find(item);

         if (orqi == null)
         {
            Add(item);
         }
         else
         {
            orqi.AddDays(item.GetDays());

            if (applayWeek)
            {
               if (orqi.W1 == false)
                  orqi.W1 = item.W1;
               if (orqi.W2 == false)
                  orqi.W2 = item.W2;
               if (orqi.W3 == false)
                  orqi.W3 = item.W3;
               if (orqi.W4 == false)
                  orqi.W4 = item.W4;
            }
         }
      }

      public OrgRouteQueueItem Find(OrgRouteQueueItem item)
      {
         foreach (OrgRouteQueueItem theItem in this)
         {
            //Console.WriteLine(theItem.OrgName.ToString());
            if (theItem.OrgName.Equals(item.OrgName))
            {
               return theItem;
            }
         }

         return null;
      }

      public OrgRouteQueueItem Find(RouteTemplate.Item item)
      {
         foreach (OrgRouteQueueItem theItem in this)
         { 
            if (item != null && item.id != null &&
                theItem.OrgID.Equals(item.id))
            {
               return theItem;
            }
         }

         return null;
      }

      public OrgRouteQueue Filter(int day)
      {
         OrgRouteQueue result = new OrgRouteQueue();
         foreach (OrgRouteQueueItem i in this)
         {
            if (i.ContainsDay(day))
               result.Add(i);
         }
         return result;
      }

      internal bool IsRouteComplex()
      {
         return true;
         //foreach (OrgRouteQueueItem item in this)
         //   if (!item.W1 || !item.W2 || !item.W3 || !item.W4)
         //      return true;

         //return false;
      }

      internal void AddRange(OrgRouteQueue orgQueue)
      {
         foreach (OrgRouteQueueItem item in orgQueue)
            Add(item);
      }

      internal IList<OrgRouteQueueItem> List { get { return Items; } }

      internal void AddOtherWeeks(List<RouteTemplate> needAdd)
      {
         Dictionary<int, List<RouteTemplate.Item>> needInsert = new Dictionary<int, List<RouteTemplate.Item>>();

         foreach(RouteTemplate of in needAdd)
         {
            int wi = of.weekIndex;
            if (wi == 0)
               continue;

            OrgRouteQueueItem lastIns = null;
            for (int i = of.items.Count - 1; i >= 0;  i--)
            {
               RouteTemplate.Item ofi = of.items[i];
               OrgRouteQueueItem fnd = Find(ofi);
               if(fnd == null)
               {
                  if (lastIns == null)
                  {
                     // если еще ничего не добавили - добавим во второй проход
                     List<RouteTemplate.Item> lst = null;
                     if (needInsert.ContainsKey(of.dayOfWeek))
                        lst = needInsert[of.dayOfWeek];
                     else
                     {
                        lst = new List<RouteTemplate.Item>();
                        needInsert.Add(of.dayOfWeek, lst);
                     }
                     lst.Add(ofi);
                  } else
                  {
                     // Если не нашли вставляем перед последней вставленной
                     OrgRouteQueueItem orqi = OrgRouteQueueItem.Build(this, Count, ofi, of.dayOfWeek, wi);
                     Insert(IndexOf(lastIns), orqi);
                     lastIns = orqi;
                  }
               }
               else
               {
                  fnd.SetItemActiveForWeek(wi);
                  lastIns = fnd;
               }
            }
         }

         foreach(KeyValuePair<int, List<RouteTemplate.Item>> kv in needInsert)
         {
            int wi = kv.Key;

            foreach(RouteTemplate.Item ofi in kv.Value)
            {
               OrgRouteQueueItem fnd = Find(ofi);
               if (fnd == null)
               {
                  OrgRouteQueueItem orqi =OrgRouteQueueItem.Build(this, Count, ofi, wi, wi);
                  Add(orqi);
               }
               else
               {
                  fnd.SetItemActiveForWeek(wi);
               }
            }
         }
      }
   }

   internal class DragDropObject
   {
      private object source;
      private object data;

      public DragDropObject(object source, object data)
      {
         this.source = source;
         this.data = data;
      }

      public object Source { get { return source; } }
      public object Data { get { return data; } }
   }

   public enum Direction { UP = -1, DOWN = 1 };

   //Класс создает страницу сообщений когда карту показывать нет смысла
   internal static class DefaultRoutePageMessage
   {
      public static readonly string STR_1 = "Для просмотра расположения всех контрагентов нажмите кнопку \"показать на карте\".";
      public static readonly string STR_2 = "Для просмотра маршрута - выберите день и нажмите кнопку \"показать на карте\".";

      public static string GetContent(string msg)
      {
         StringBuilder content = new StringBuilder();
         content.AppendLine("<html><body>");
         content.AppendLine("<div align=\"center\">");
         content.AppendLine("<font color=Gray size=3>");
         content.AppendLine(msg);
         content.AppendLine("</font>");
         content.AppendLine("</div>");
         content.AppendLine("<body></html>");
         return content.ToString();
      }
   }

   public interface IRoute
   {
      void SetCurrentAgent(Agent a);
   }

}

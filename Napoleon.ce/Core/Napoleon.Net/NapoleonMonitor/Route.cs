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
using System.Security.Cryptography.X509Certificates;
using System.Net.Security;

namespace GRSoft.NapoleonManager
{

   public partial class Route : Form, IRoute
   {
      public const int SHEDULE_WEEK_COUNT = 4;
      public const string DATE_FORMAT = "yyyy-MM-dd";
      private DataSet<int, OrgFolder> dsOrgFolder;
      private DataSet<string, Org> dsOrg;
      private DataSet<string,PotenzialOrg> dsPtnzOrg;
      private OrgRouteQueue orgQueue = new OrgRouteQueue();
      private DataSet<int, CommonConfig> dsConfig;

      private Agent currentAgent;

      //Экземпляр формы fmSelectContrAgentInstance, он необходим для того, если будут переключения 
      //текущего агента, то его нам надо закрывать, потому что у агентов может быть разный список организаций
      private FmSelectContrAgent fmSelectContrAgentInstance;

      private class ControlOnChanges
      {
         private bool changed = false;
         private bool dateChanged = false;
         private ToolStripItem control;

         public ControlOnChanges(ToolStripItem control)
         {
            this.control = control;
            this.control.Enabled = false;
         }

         public void SetChanges()
         {
            changed = true;
            control.Enabled = true;
         }

         public void SetDateChanges()
         {
            dateChanged = true;
            control.Enabled = true;
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
      private ControlOnChanges routeWasChanged;

      // Route .ctor
      public Route()
      {
         InitializeComponent();
         InitDataSets();
         AdjustForm();
         FetchStartDatas();
      }

      //Настройка наборов данных
      private void InitDataSets()
      {
         dsOrgFolder = new DataSet<int, OrgFolder>("OrgFolder", false);
         //dsOrg = DataModule.Get("Org") == null ? new DataSet<string, Org>("Org") :
         //   (DataSet<string, Org>)DataModule.Get("Org");
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
         wb.DocumentText = DefaultRoutePageMessage.GetContent(DefaultRoutePageMessage.STR_1);
      }

      //Запрос на сохранения измененных данных
      private bool AskToSaveChanges()
      {
         if (!routeWasChanged.IsChanged())
         {
            return false;
         }

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         return dr == DialogResult.Yes;
      }

      //Событие загрузки набора данных маршрута "dsOrgFolder"
      private void OrgFolderLoaded(object sender, EventArgs e)
      {
         try
         {
            DataModule.DataProcessed -= new EventHandler(OrgFolderLoaded);
            BeginInvoke(new EmptyParamHandler(FillOrgGrid));
         }
         catch(Exception exception)
         {
            Console.WriteLine(exception.ToString());
         }
      }

      //Загрузка набора, что содержит все записи (orgQueue), есть еще один 
      //набор, которы и отображает фильтрованные записи(representatin)
      private int FillOrgQueueMaster()
      {
         orgQueue.Clear();
         int result = 1;

         // список на все дни недели
         List<OrgOrderer> orgOrderer = new List<OrgOrderer>(7);
         for (int i = 0; i < 7; i++)
            orgOrderer.Add(null);

         List<int> invalidFolder = new List<int>();

         foreach (KeyValuePair<int, OrgFolder> kv in dsOrgFolder)
         {
            OrgFolder of = kv.Value;
            if (of.name.Length == 0)
               continue;
            if (WeekDay.CheckDay(of.name) == false)
            {
               invalidFolder.Add(kv.Key);
               continue;
            }

            WeekDay wd;
            try
            {
               wd = new WeekDay(of.name);
            }
            catch (ENonWeekDay)
            {
               //MessageBox.Show(e.Message);
               invalidFolder.Add(kv.Key);
               continue;
            }

            // проверим день недели на спец формат
            int wi;
            if (!SelectAllDays() && int.TryParse(new String(new char[] { of.name[0] }), out wi))
            {
               int dayIndex = wd.Number-1;

               if (orgOrderer[dayIndex] == null)
                  orgOrderer[dayIndex] = new OrgOrderer(wd);

               orgOrderer[dayIndex].Add(of.items, wi);
               continue;
            }

            List<OrgFolderItem> removed = new List<OrgFolderItem>();
            foreach (OrgFolderItem ofi in of.items)
            {
               //if (ofi.org == null || dsOrg.ContainsKey(ofi.name) == false)
               if (ofi.org == null )
               {
                  removed.Add(ofi);
                  continue;
               }

               OrgRouteQueueItem queueItem = null;

               //if (Char.IsNumber(of.name[0]) && !SelectAllDays())
               //   queueItem = new OrgRouteQueueItem(result++, ofi, wd,
               //      int.Parse(new String(new char[] { of.name[0] })));
               //else
               //{
//#if SQL_ORG_ROUTE
                  queueItem = new OrgRouteQueueItem(ofi.pos, ofi, wd);
//#else
//                  queueItem = new OrgRouteQueueItem(result++, ofi, wd);
//#endif
                  queueItem.SetItemForAllWeek();
               //}

               bool applayWeekSchedule = SelectAllDays() ? false :
                  new WeekDay(GetSelectedDay()).Equals(wd);

               if (SelectAllDays())
                  queueItem.SetItemForAllWeek();

               orgQueue.AddItem(queueItem, SelectAllDays() ? false : applayWeekSchedule);
            }

            foreach (OrgFolderItem ofi in removed)
               of.items.Remove(ofi);
            if (of.items.Count == 0)
               invalidFolder.Add(kv.Key);
         }

         Agent rplAgent = null;
         foreach (int f in invalidFolder)
         {
            if (rplAgent == null)
               rplAgent = dsOrgFolder[f].agent;

            dsOrgFolder.Remove(f);
         }
         // если были косяки то попробуем сохранить изменения
         if (rplAgent != null)
         {
            List<ReplacedSet> replaced = new List<ReplacedSet>();
            replaced.Add(new ReplacedSet(rplAgent.id, dsOrgFolder));
            DataModule.UpdateDataSet(null, null, replaced, Config.GetConfig().GetConnection());
         }

         // выгрузим дни в orgQueue
         foreach (OrgOrderer oo in orgOrderer)
         {
            if (oo != null)
               oo.AddItems(orgQueue, ref result);
         }

         return result;
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

      //Применение фильтра и заполнения набора для отображения данных с учетом
      //фильтра, связь с гридом
      private void FillOrgGrid()
      {
         Agent curAgent = cbAgents.SelectedItem as Agent;
         if( curAgent == null)
         {
            return;
         }

         dgvOrgs.SuspendLayout();

         // будем хранить начало расписания для каждого агента отдельно - мало ли что :)
         CommonConfig cc = ConfigUtils.GetOrCreateConfig(dsConfig, ConfigKeyItems.SHEDULE_START, curAgent);
         try
         {
            dtpRouteStart.Value = cc.value.Length == 0 ? 
               StartOfWeek : 
               DateTime.ParseExact(cc.value,DATE_FORMAT,null);
         }
         catch (Exception)
         {
            dtpRouteStart.Value = DateTime.Now;
         }
         

         string filter = cbSelectDay.SelectedItem.ToString();

         FillOrgQueueMaster();

         //Набор данных содержит "фильтрованные записи"
         OrgRouteQueue representation = new OrgRouteQueue();

         if (filter.Equals("<все>"))
         {
            representation.AddRange(orgQueue);
            representation.DoSort("org", SortOrder.Ascending);
         }
         else
         {
            WeekDay selectedDay = new WeekDay(filter);
            representation = orgQueue.Filter(selectedDay);//.MakeQueueOrderAsDataset(dsOrgFolder, selectedDay);
//#if SQL_ORG_ROUTE
            AdjustIndex(dsOrgFolder, representation, selectedDay);

            representation.DoSort("index", SortOrder.Ascending);
//#endif
         }

         representation.ApplyNewOrder();
         dgvOrgs.DataSource = representation;

         MakeWeekColumnHeader(null, new EventArgs());
         dgvOrgs.ResumeLayout();
      }

      public static void AdjustIndex(DataSet<int, OrgFolder> dsOrgFolder, OrgRouteQueue representation, WeekDay selectedDay)
      {
         //DataSet<int, OrgFolder> dsOrgFolder = DataModule.Get(OrgFolder.OBJECT_NAME) as DataSet<int, OrgFolder>;

         if (dsOrgFolder != null)

            foreach (OrgRouteQueueItem item in representation)
            {
               foreach (OrgFolder of in dsOrgFolder.Data)
               {
                  if (WeekDay.CheckDay(of.name) == false)
                     continue;

                  WeekDay wd = new WeekDay(of.name);
                  if (wd.Equals(selectedDay))
                  {
                     foreach (OrgFolderItem i in of.items)
                     {
                        if (i.name.Equals(item.OrgID))
                           item.Index = i.pos;
                     }
                  }
               }
            }
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

      public static XmlDocument GetYandexRequest(string reqStr)
      {
         String req = "http://geocode-maps.yandex.ru/1.x/?geocode=" + reqStr +
            "&key=ANzXmEoBAAAAkjQFPwIAQphddQGLBfkqE3qCI3Vo8OCM8yYAAAAAAAAAAACYpxbA0OaEdJRuAHC50onaXfYLBQ==";
         HttpWebRequest request = (HttpWebRequest)WebRequest.Create(req);

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
            catch(Exception)
            {
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
         foreach (Agent a in DataModule.Get("Agents").Data)
         {
            cbAgents.Items.Add(a);
         }

         cbAgents.Sorted = true;
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

            FmWait.ShowForm(this, null);
            routeQueue = getRouteForWeek(routeQueue, cbWeek.SelectedIndex + 1);
            string txt = MapEngine.Route(Config.GetConfig().mapSource, routeQueue);
//            File.WriteAllText("ttt.html", txt);
            wb.DocumentText = txt;
         }
         else
         {
            Agent agent = (cbAgents.SelectedItem as Agent);
            if (agent != null)
            {
               wb.DocumentText = String.Format("<html><body>Нет данных для пользователя " +
               "<font color=blue><b>{0}</b></font><body></html>",
               agent.Name);
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

         OrgFolderItem ofi = new OrgFolderItem();
         ofi.org = org;
         ofi.name = ofi.org.id;
         OrgRouteQueueItem orqi = new OrgRouteQueueItem(representation.Count, ofi, new WeekDay(GetSelectedDay()));
         orqi.SetItemForAllWeek();

         //По просьбе Володи мы не вставляем новые строки в указанную позицию, а добавляем их вниз списка
         representation.Add(orqi);
         representation.ApplyNewOrder();
         UpdateDataSourceConnection();
         routeWasChanged.SetChanges();
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
               OrgFolderItem ofi = new OrgFolderItem();

               //ofi.org = row.DataBoundItem as Org;
               ofi.org = rc[i];
               ofi.name = ofi.org.id;
               OrgRouteQueueItem orqi = new OrgRouteQueueItem(newRowIndex, ofi, new WeekDay(GetSelectedDay()));
               orqi.SetItemForAllWeek();
               //orgQueue.Insert(newRowIndex == -1 ? orgQueue.Count : newRowIndex, orqi);

               //По просьбе Володи мы не вставляем новые строки в указанную позицию, а добавляем их вниз списка
               representation.Add(orqi);
            }
         }

         representation.ApplyNewOrder();
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
      private void tsbSave_Click(object sender, EventArgs e)
      {
         SaveChanges();
      }

      private void SaveChanges()
      {
         string day = GetSelectedDay();
         SaveChangesLow(currentAgent, day);
      }

#if SQL_ORG_ROUTE
      void WriteRouteSQL(Agent agent, string day, OrgRouteQueue representation)
      {
         OrgFolder modifyFolder = null;
         List<String> removeDays = new List<string>();
         SimpleDataSet<OrgFolder> wrds = new SimpleDataSet<OrgFolder>(OrgFolder.OBJECT_NAME, false);

         if (!representation.IsRouteComplex())
         {
            for (int i = 1; i <= SHEDULE_WEEK_COUNT; i++)
            {
               String cday = i.ToString() + day;
               int removeKey = GetOrgFolderKeyByDay(cday);

               if (removeKey != -1)
               {
                  dsOrgFolder.Remove(removeKey);
                  removeDays.Add(cday);
               }
            }

            modifyFolder = GetModifyOrgFolder(agent, day);

            int pos = 0;
            foreach (OrgRouteQueueItem orqi in representation)
            {
               orqi.Item.pos = pos++;
               modifyFolder.items.Add(orqi.Item);
            }

            if (modifyFolder.items.Count == 0)
               removeDays.Add(modifyFolder.name);
            else
               wrds.Add(modifyFolder);
         }
         else
         {

            int removeKey = GetOrgFolderKeyByDay(day);

            if (removeKey != -1)
            {
               removeDays.Add(day);
               dsOrgFolder.Remove(removeKey);
            }

            for (int week = 1; week <= SHEDULE_WEEK_COUNT; week++)
            {
               string folderDay = week.ToString() + day;
               modifyFolder = GetModifyOrgFolder(agent, folderDay);

               int pos = 0;
               foreach (OrgRouteQueueItem orqi in representation)
               {
                  if (orqi.IsItemActiveForWeek(week))
                  {
                     orqi.Item.pos = pos++;
                     modifyFolder.items.Add(orqi.Item);
                  }
               }

               if (modifyFolder.items.Count == 0)
                  removeDays.Add(modifyFolder.name);
               else
                  wrds.Add(modifyFolder);
            }
         }

         List<IDataSet> wrSet = ConfigUpdate(agent);
         if (wrds.Count > 0)
            wrSet.Add(wrds);

         // заменим маршрут 
         if (removeDays.Count > 0)
         {
            SimpleDataSet<OrgFolder> rmv = new SimpleDataSet<OrgFolder>(OrgFolder.OBJECT_NAME, false);

            String rmvStr = "";
            foreach (String rday in removeDays)
               rmvStr += "'" + rday + "',";
            rmv.Filter = String.Format("\"userid\" = '{0}' and \"name\" in ({1})", agent.id, rmvStr.TrimEnd(new char[] {','}));
            DataModule.RemoveDataSet(rmv, Config.GetConfig().GetConnection());
         }

         if (wrSet.Count > 0 && DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()) == false)
         {
            MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
         }
      }
#endif

      private void SaveChangesLow(Agent agent, string day)
      {
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

#if Vyatich
            if (dsOrgFolder.Count == 0)
            {
               DialogResult res = MessageBox.Show("Нет маршрута. Удалить маршрут агента?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
               if (res != DialogResult.Yes)
                  return;
            }
            if (FmAdminLogin.CheckAdmin(this) == false)
               return;
#endif

#if SQL_ORG_ROUTE
            WriteRouteSQL(agent, day, representation);
#else
            OrgFolder modifyFolder = null;

            if (!representation.IsRouteComplex())
            {
               for (int i = 1; i <= SHEDULE_WEEK_COUNT; i++)
               {
                  int removeKey = GetOrgFolderKeyByDay(i.ToString() + day);

                  if (removeKey != -1)
                     dsOrgFolder.Remove(removeKey);
               }

               modifyFolder = GetModifyOrgFolder(agent, day);

               int pos = 0;
               foreach (OrgRouteQueueItem orqi in representation)
               {
                  orqi.Item.pos = pos++;
                  modifyFolder.items.Add(orqi.Item);
               }
            }
            else
            {

               int removeKey = GetOrgFolderKeyByDay(day);

               if (removeKey != -1)
                  dsOrgFolder.Remove(removeKey);

               for (int week = 1; week <= SHEDULE_WEEK_COUNT; week++)
               {
                  string folderDay = week.ToString() + day;
                  modifyFolder = GetModifyOrgFolder(agent, folderDay);

                  int pos = 0;
                  foreach (OrgRouteQueueItem orqi in representation)
                  {
                     if (orqi.IsItemActiveForWeek(week))
                     {
                        orqi.Item.pos = pos++;
                        modifyFolder.items.Add(orqi.Item);
                     }
                  }
               }
            }

            List<IDataSet> wrSet = ConfigUpdate(agent);
            List<ReplacedSet> replaced = new List<ReplacedSet>();
            replaced.Add(new ReplacedSet(agent.id, dsOrgFolder));

            // заменим маршрут 
            if (DataModule.UpdateDataSet(wrSet, null, replaced, Config.GetConfig().GetConnection()) == false)
            {
               MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
#endif
         }
         //Если изменилась одна только дата
         else if (routeWasChanged.IsDateChanged())
         {
            List<IDataSet> wrSet = ConfigUpdate(agent);

            if (DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()) == false)
            {
               MessageBox.Show("Ошибка при записи в базу данных.", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
         }

         routeWasChanged.ResetChanges();
      }

      private List<IDataSet> ConfigUpdate(Agent agent)
      {
         // проверим и обновим начало расписания (если надо)
         List<IDataSet> wrSet = new List<IDataSet>();
         CommonConfig cc = ConfigUtils.GetOrCreateConfig(dsConfig, ConfigKeyItems.SHEDULE_START, agent);
         string v = dtpRouteStart.Value.ToString(DATE_FORMAT);
         if (v != cc.value)
         {
            DataSet<int, CommonConfig> dcc = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);

            cc.value = v;
            dcc[0] = cc;
            wrSet.Add(dcc);
         }
         return wrSet;
      }

      private OrgFolder GetModifyOrgFolder(Agent agent, string day)
      {
         OrgFolder result = GetOrgFolderByDay(day);

         if (result == null)
         {
            result = new OrgFolder();
            result.agent = agent;
            result.items = new List<OrgFolderItem>();
            result.name = day;
            dsOrgFolder.Add(GetOrgFolderKey(), result);
         }

         result.items.Clear();

         return result;
      }

      private OrgRouteQueue GetConnectedDataSource()
      {
         return (OrgRouteQueue)dgvOrgs.DataSource;
      }

      private int GetOrgFolderKey()
      {
         int result = -1;

         foreach(KeyValuePair<int, OrgFolder> kvp in dsOrgFolder)
         {
            if (kvp.Key > result)
               result = kvp.Key;
         }

         return result + 1;
      }

      private OrgFolder GetOrgFolderByDay(string day)
      {
         OrgFolder result = null;

         foreach (OrgFolder of in dsOrgFolder.Data)
         {
            if (of.name == day)
            {
               result = of;
               break;
            }
         }

         return result;
      }

      private int GetOrgFolderKeyByDay(string day)
      {
         int result = -1;

         foreach (KeyValuePair<int, OrgFolder> kvp in dsOrgFolder)
         {
            if (kvp.Value.name.Equals(day))
               return kvp.Key;
         }

         return result;
      }

      private void tsbAddOrg_Click(object sender, EventArgs e)
      {
         fmSelectContrAgentInstance = FmSelectContrAgent.ShowForm(dsOrg, dsPtnzOrg, dsOrgFolder, AddOrg, this);
      }

      private string GetSelectedDay()
      {
         return cbSelectDay.SelectedItem.ToString();
      }

      private bool IsSelectedAllDays()
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

         representation.ApplyNewOrder();
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

         representation.ApplyNewOrder();
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
         Type prcType = FormEntries.GetFormType(typeof(Route));
         ConstructorInfo ci = prcType.GetConstructor(Type.EmptyTypes);
         IRoute fmRoute = (IRoute)ci.Invoke(new object[] { });
         fmRoute.SetCurrentAgent(agent);
         ((Form)fmRoute).Show();
      }
      
      //Событие окончания загрузки браузера
      private void wb_DocumentCompleted(object sender, WebBrowserDocumentCompletedEventArgs e)
      {
         FmWait.CloseForm();
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
                  SaveChangesLow(agent, day);
            }

            cbSelectDay.Tag = cbSelectDay.SelectedItem;
            wb.DocumentText = DefaultRoutePageMessage.GetContent(DefaultRoutePageMessage.STR_2);
            tsbSave.Enabled = false;
            AdjustControlsBtns();

            FillOrgGrid();

            dgvOrgs.ReadOnly = cbSelectDay.SelectedItem.Equals("<все>");
         }
         catch
         { 
         }
      }

      private void AdjustControlsBtns()
      {
         tsbAddOrg.Enabled = !IsSelectedAllDays();
         tsbDelete.Enabled = !IsSelectedAllDays();
         tsbUp.Enabled = !IsSelectedAllDays();
         tsbDown.Enabled = !IsSelectedAllDays();

         if (IsSelectedAllDays())
            CloseSelectContrAgentForm();
      }

      //Событие изменение текущего агента, если были какие то изменения маршрута, то даем запрос на сохранение
      private void cbAgents_SelectionChangeCommitted(object sender, EventArgs e)
      {
         try
         {
            if (currentAgent != null && AskToSaveChanges())
            {
               string day = GetSelectedDay();
               SaveChangesLow(currentAgent, day);
            }

            wb.DocumentText = DefaultRoutePageMessage.GetContent(DefaultRoutePageMessage.STR_2);

            cbSelectDay.SelectedIndex = 0;

            currentAgent = cbAgents.SelectedItem as Agent;
            if (currentAgent == null)
               return;

            dgvOrgs.ReadOnly = true;
            RefreshDataSetsForAgent(currentAgent);
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
         DataModule.DataProcessed += new EventHandler(OrgFolderLoaded);
         
         dsOrgFolder.Clear();
         dsOrgFolder.Filter = String.Format(USERID_IN_STR, agent.id);

         dsOrg = DataModule.GetUserDataSet(agent.id, "Org", typeof(DataSet<string, Org>)) as DataSet<string, Org>;
         dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agent.id), dsOrg.Name);
         //dsOrg.Filter = String.Format(USERID_IN_STR, agent.id);

         dsPtnzOrg.Filter = String.Format(USERID_IN_STR, agent.id);
         dsConfig.Filter = "(not (\"userid\" is null)) or \"userid\" is null";

         List<IDataSet> refreshList = new List<IDataSet>();
         refreshList.Add(dsOrg);
         refreshList.Add(dsOrgFolder);
         refreshList.Add(dsPtnzOrg);

         //if (dsConfig.Count == 0)
         refreshList.Add(dsConfig);

         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), 
            refreshList, FmWait.ProgressIndicator);
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

      string GetDate(WeekDay wd, int weekIndex)
      {
         int needDate = wd.Number;
         DateTime dt = DateTime.Today;
         int curDate = (int)dt.DayOfWeek;
         if (curDate == 0) curDate = 7;
      
         dt = dt.AddDays(needDate - curDate);
         dt = dt.AddDays(7 * weekIndex);

         return dt.ToString("dd/MM/yy");
      }

      private void MakeWeekColumnHeader(object sender, EventArgs e)
      {
         WeekDay wd = new WeekDay((!SelectAllDays()) ? cbSelectDay.SelectedItem as string : "Понедельник");

         // найдем индекс текущей недели
         TimeSpan ts = new TimeSpan(DateTime.Today.Ticks);
         ts = ts.Subtract(new TimeSpan(dtpRouteStart.Value.Ticks));
         if( wd != null && ts.TotalDays >= 0 )
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

      public static Location GetFirstKnownPoint(IList<OrgRouteQueueItem> queue)
      {
         Location loc = null;

         foreach (OrgRouteQueueItem item in queue)
         {
            loc = item.Location;

            if (loc == null)
            {
               loc = Route.GetLocation(item.Address);
            }

            if (loc != null)
               break;
         }

         return loc;
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         Invoke(new EmptyParamHandler(
            delegate
            {
               FillOrgQueueMaster();
               RouteReport report = new RouteReport("route_report_{0}.html");
               RouteReportData data = new RouteReportData();
               data.agent = cbAgents.SelectedItem as Agent;
               data.queue.AddRange(orgQueue);
               report.Build(data, dsOrgFolder);
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
      }

      private static Dictionary<Location, string> addresses = new Dictionary<Location,string>();
   }

   public class AgentItem
   {
      public string id;
      public string name;

      public AgentItem(Agent a)
      {
         id = a.id;
         name = a.name;
      }

      public override string ToString()
      {
         return name;
      }
   }

   public class OrgRouteQueueItem : CmpByField<OrgRouteQueueItem>
   {
      private int pos;
      private OrgFolderItem org;
      private List<WeekDay> days = new List<WeekDay>();
      private bool w1 = false;
      private bool w2 = false;
      private bool w3 = false;
      private bool w4 = false;
      private int index;

      public OrgRouteQueueItem(int pos, OrgFolderItem org, WeekDay day)
      {
         this.pos = pos;
         this.org = org;
         index = pos;
         days.Add(day);
      }

      public OrgRouteQueueItem(int pos, OrgFolderItem org, WeekDay day, int week):
         this(pos, org, day)
      {
         SetItemActiveForWeek(week);
      }

      public int Pos { get { return pos; } set { pos = value; } }
      public OrgFolderItem Item { get { return org; } }
      public string OrgName { get { return org == null || org.org == null ? string.Empty : org.org.Name; } }
      public string OrgID { get { return org == null || org.org == null ? string.Empty : org.org.id; } }
      public string Day 
      { 
         get 
         {
            StringBuilder sb = new StringBuilder();
            foreach (WeekDay wd in days)
            {
               if (sb.Length > 0)
               { 
                  sb.Append(", ");
               }

               sb.Append(wd.ShortName);
            }

            return sb.ToString(); 
         }
      }

      public bool ContainsDay(WeekDay wd)
      {
         foreach (WeekDay d in days)
            if (d.Number == wd.Number)
               return true;
         return false;
      }


      public string Address { get { return org == null || org.org == null ? string.Empty : org.org.Address; } }
      public Location Location
      {
         get
         {
            Location result = null;
            if (org != null && org.org != null)
            {
               OrgLocations ol = OrgLocations.GetDataSet();
               OrgLocation loc = ol.GetLocation(org.org.id);
               if( loc != null )
                  result = new Location(loc.latitude, loc.longitude);
               else if (org.org.latitude != 0 && org.org.longitude != 0)
                  result = new Location(org.org.latitude, org.org.latitude);
            }

            return result;
         }
      }

      public void AddDays(List<WeekDay> day)
      {
         foreach (WeekDay d in day)
            if (!days.Contains(d))
               days.Add(d);
      }

      public List<WeekDay> GetDays()
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
      public int Index { get { return index; } set { index = value; } }
   }

   public class OrgRouteQueue : BindingList<OrgRouteQueueItem>
   {
      public void DoSort(string cmpField, SortOrder sortOrder)
      {
         OrgRouteQueueItem.CC.SetCompareCondition(cmpField, sortOrder == SortOrder.Ascending);

         (Items as List<OrgRouteQueueItem>).Sort();
         ApplyNewOrder();
      }

      public void ApplyNewOrder()
      {
         int counter = 1;
         foreach (OrgRouteQueueItem orqi in this)
         {
            orqi.Pos = counter++;
         }
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

      public OrgRouteQueueItem Find(OrgFolderItem item)
      {
         foreach (OrgRouteQueueItem theItem in this)
         { 
            if (item != null && item.name != null &&
                theItem.OrgID.Equals(item.name))
            {
               return theItem;
            }
         }

         return null;
      }

      public OrgRouteQueue Filter(WeekDay day)
      {
         OrgRouteQueue result = new OrgRouteQueue();
         foreach (OrgRouteQueueItem i in this)
         {
            if (i.ContainsDay(day))
               result.Add(i);
         }
         return result;
      }

      //public OrgRouteQueue MakeQueueOrderAsDataset(DataSet<int, OrgFolder> baseDataSet, WeekDay day)
      //{
      //   OrgRouteQueue result = new OrgRouteQueue();

      //   foreach (OrgFolder of in baseDataSet.Data)
      //   {
      //      if (of == null || of.name == null)
      //         continue;

      //      bool canAdd = false;
      //      try
      //      {
      //         WeekDay wd = new WeekDay(of.name);
      //         canAdd = wd.Equals(day);
      //      }
      //      catch 
      //      {
      //      }

      //      if (!canAdd) continue;

      //      foreach (OrgFolderItem ofi in of.items)
      //      {
      //         int weekIndex = -1;

      //         if (Char.IsNumber(of.name[0]))
      //            weekIndex = int.Parse(new string(new char[] { of.name[0] }));

      //         OrgRouteQueueItem candidat = Find(ofi);
      //         if (candidat != null)
      //         {
      //            if (weekIndex == -1 || weekIndex == 0)
      //               result.Add(candidat);
      //         }
      //      }
      //   }

      //   return result;
      //}

      internal bool IsRouteComplex()
      {
         foreach (OrgRouteQueueItem item in this)
            if (!item.W1 || !item.W2 || !item.W3 || !item.W4)
               return true;

         return false;
      }

      internal void AddRange(OrgRouteQueue orgQueue)
      {
         foreach (OrgRouteQueueItem item in orgQueue)
            Add(item);
      }

      internal IList<OrgRouteQueueItem> List { get { return Items; } }
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

   public class OrgOrderer : Dictionary<int, List<OrgFolderItem>>
   {
      public WeekDay day;

      public OrgOrderer(WeekDay d) { this.day = d; }

      public void Add(List<OrgFolderItem> items, int weekIndex)
      {
         this.Add(weekIndex, items);
      }

      /// <summary>
      /// отсортировать и добавить значения в очередь
      /// </summary>
      /// <param name="orgQueue"></param>
      public void AddItems(OrgRouteQueue orgQueue, ref int curIndex)
      {
         List<OrgRouteQueueItem> ritems = Ordering(ref curIndex);

         if (ritems != null)
            foreach (OrgRouteQueueItem item in ritems)
               orgQueue.Add(item);
      }

      private List<OrgRouteQueueItem> Ordering(ref int curIndex)
      {
         List<OrgRouteQueueItem> res = null;
         // найдем references - т.е. первый не пустой элемент
         int idx = 1;
         List<OrgFolderItem> ritems = null;
         for (; idx <= Route.SHEDULE_WEEK_COUNT; idx++)
         {
            if (ContainsKey(idx))
            {
               ritems = this[idx];
               if (ritems != null)
                  break;
            }
         }

         if (ritems != null)
         {
            res = new List<OrgRouteQueueItem>();
            foreach (OrgFolderItem item in ritems)
            {
               OrgRouteQueueItem ri = new OrgRouteQueueItem(curIndex++, item, day, idx);
               res.Add(ri);
            }

            // есть еще элементы - будем сортировать
            while (++idx <= Route.SHEDULE_WEEK_COUNT)
            {
               if (ContainsKey(idx))
                  AddElements(res, this[idx], idx, ref curIndex);
            }
         }

         return res;
      }

      OrgRouteQueueItem FindItem(List<OrgRouteQueueItem> dest, OrgFolderItem item, ref int startIndex)
      {
         OrgRouteQueueItem res = null;
         while (startIndex < dest.Count)
         {
            OrgRouteQueueItem check = dest[startIndex];
            if (check.Item.name == item.name)
            {
               res = check;
               break;
            }
            startIndex++;
         }

         return res;
      }

      private void AddElements(List<OrgRouteQueueItem> dest, List<OrgFolderItem> src, int weekIndex, ref int curIndex)
      {
         int startIndex = 0;
         List<OrgRouteQueueItem> unOrdered = new List<OrgRouteQueueItem>();
         foreach (OrgFolderItem srcItem in src)
         {
            int ci = startIndex;
            OrgRouteQueueItem finded = FindItem(dest, srcItem, ref ci);
            if (finded != null)
            {
               //startIndex = ci + 1;
               finded.SetItemActiveForWeek(weekIndex);

               if (unOrdered.Count > 0)
               {
                  dest.InsertRange(ci, unOrdered);
                  unOrdered.Clear();
               }
            }
            else
            {
               unOrdered.Add(new OrgRouteQueueItem(curIndex++, srcItem, day, weekIndex));
            }
         }

         if (unOrdered.Count > 0)
            dest.AddRange(unOrdered);
      }
   }

   public interface IRoute
   {
      void SetCurrentAgent(Agent a);
   }

}

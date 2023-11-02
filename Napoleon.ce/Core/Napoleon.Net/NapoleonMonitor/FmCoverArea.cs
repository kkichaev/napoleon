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
   public partial class FmCoverArea : Form
   {
      private DataSet<DateTime, GPSPos> dsGPSPos;
      private DataSet<int, VisitInfo> dsVisit;
      private DataSet<int, OrgRemnants> dsOrgRemnants;
      private DataSet<int, Order> dsOrder;
      //private DataSet<string, Region> dsRegion;
      //private DataSet<string, Region1> dsRegion1;
      //private DataSet<string, Region2> dsRegion2;
      private DataSet<int, Sales> dsSales;
      private DataSet<int, Incass> dsIncass;
      protected DataSet<string, Org> dsOrg;

//#if HappyLand
//      private DataSet<int, ScriptDoc> dsScriptDoc;
//#endif
      private DataSet<int, PKO> dsPKO;
      private Agents dsAgents;
      private DataSet<int, UserLog> dsUserLog;

      internal const int DEF_NUM_INTERVAL = 15;
#if MOVEMENT_DOC
      private DataSet<int, MoveDoc> dsMove;
#endif
      public FmCoverArea(string idAgent, DateTime date)
      {
         InitializeComponent();
         InitDatasets();
         Init(idAgent, date);
#if MOVEMENT_DOC
         btnMove.Visible = true;
#else
         btnMove.Visible = false;
#endif
         DecoratorFactory.GetDecorator(this);
      }

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
      }

      private void AdjustFilterForDS(string idAgent, DateTime start, DateTime finish)
      {
         const string COMMON_FILTER_STR = "\"{0}\" >= ToDate('{1:dd/MM/yyyy}') and \"{0}\" <= ToDate('{2:dd/MM/yyyy}') and \"userid\" = '{3}'";
         //const string GPS_FILTER = COMMON_FILTER_STR + " and \"isGSM\" = '{5}'";

         string DATE_FILTER = string.Format(COMMON_FILTER_STR, "date", start, finish, idAgent);
         dsGPSPos.Filter = DATE_FILTER;
         dsVisit.Filter = DATE_FILTER;
         dsOrgRemnants.Filter = DATE_FILTER;

//#if HappyLand
         //dsScriptDoc.Filter = string.Format(COMMON_FILTER_STR, "date", date,idAgent, timeBegin.Value, timeEnd.Value);
//#endif

         string CREATED_FILTER = string.Format(COMMON_FILTER_STR, "created", start, finish, idAgent);
         dsOrder.Filter = CREATED_FILTER;
         dsPKO.Filter = CREATED_FILTER; 
         dsUserLog.Filter = DATE_FILTER + " and category = 1";
         dsSales.Filter = CREATED_FILTER;
         string f = "(" + DATE_FILTER + ") or (" + CREATED_FILTER + ")";
         dsIncass.Filter = f;
#if MOVEMENT_DOC
         dsMove.Filter = CREATED_FILTER;
#endif
         dsOrg = DataModule.GetUserDataSet(idAgent, "Org", typeof(DataSet<string, Org>)) as DataSet<string, Org>;
         dsOrg.Command = new ServerCommand(Commands.Impersonate(Commands.GET, idAgent), dsOrg.Name);
      }

      //Начальные установки формы
      private void Init(string idAgent, DateTime date)
      {
         foreach (Agent agent in dsAgents.Data)
         {
            cbAgents.Items.Add(new AgentItem(agent));
         }

         cbAgents.Sorted = true;
         SelectAgentFromId(idAgent);
         dtpStart.Value = date.AddDays(-7);
         dtpFinish.Value = date;
      }

      private void UpdateDataSets(string idAgent, DateTime start, DateTime finish)
      {
         btnRefresh.Enabled = false;
         AdjustFilterForDS(idAgent, start, finish);
         //DataModule.CurrentUser = idAgent;
         //DataModule.SetDataRepsonceHandlers(DataLoaded, DataConnectionError);
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
         list.Add(dsOrg);
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
         ShowRouteFromThread(12);
      }

      //private void DataLoaded(object sender, EventArgs e)
      //{
      //   DataModule.DataProcessed -= new EventHandler(DataLoaded);

      //   BeginInvoke(new EmptyParamHandler(delegate
      //   {
      //      btnRefresh.Enabled = true;
      //      ShowRouteFromThread(12);
      //   }));
      //}

      ////Произошла ошибка в соединении
      //private void DataConnectionError(EDataResponse e)
      //{
      //   DataModule.ClearEvents();
      //   FmWait.CloseForm();

      //   Invoke(new EmptyParamHandler(delegate
      //   {
      //      const string TITLE = "Ошибка";

      //      MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
      //         MessageBoxIcon.Error);
      //   }));
      //}

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

               item.number = (!item.HavePosition) ? "" : (++count).ToString();

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
                  item.startTime == DateTime.MinValue ? "" : item.startTime.ToString("dd/MM/yyyy HH:mm"), sumtext,
                  item.StopTime, item.address, item.factAddress, r, r1, r2});
            }
         }
         finally
         {
            dgvOrgs.ResumeLayout();
         }
      }

      private void dgvOrgs_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         DataGridViewRow r = dgvOrgs.Rows[e.RowIndex];
         if (r.Cells[7].Value != null)
            e.CellStyle.ForeColor = Color.Red;
      }

      private void ShowRouteFromThread(int zoom)
      {
         FmWait.ShowForm(this, true);

         List<VisitQueueItem> visitQueue = new List<VisitQueueItem>();

         if(btnDoc.Checked)
            MakeVisitQueue(visitQueue);

         if (btnUndoc.Checked)
            MakeUndocQueue(visitQueue);

         visitQueue.Sort((lhs, rhs) => lhs.OrgName.CompareTo(rhs.OrgName));
         FillVisitGrid(visitQueue);

         string txt = MapEngine.CoverArea(Config.GetConfig().mapSource, visitQueue);
#if MAKE_HTML_FILE
         File.WriteAllText("c:\\ttt1.html", txt);
#endif
         wb.DocumentText = txt;
      }

      private void MakeUndocQueue(List<VisitQueueItem> result)
      {
         List<string> ids = new List<string>();

         foreach (VisitQueueItem item in result)
            if (!ids.Contains(item.org.id))
               ids.Add(item.org.id);
         AgentItem agentItem = (cbAgents.SelectedItem as AgentItem);
         List<Org> orgs = null;

         if (btnOnlyFromRoute.Checked && agentItem != null)
            orgs = OrdersDetail.GetRoutePeriod(dtpStart.Value.Date, dtpFinish.Value.Date, Agents.GetDataSet()[agentItem.id]);
         else
         {
            orgs = new List<Org>();
            orgs.AddRange(dsOrg.Values);
         }


         foreach(Org o in orgs)
            if(!ids.Contains(o.id))
            {
               VisitQueueItem item = new VisitQueueItem(DateTime.MinValue, o, 0, 0, new OrgVisitType());
               if (o.Address == null || o.Address.Length == 0)
                  continue;

               Location l = Route.GetLocation(o.Address);

               if (l != null)
               {
                  item.latitude = l.Latitude;
                  item.longitude = l.Longitude;
                  item.address = o.Address;
                  item.color = "red";

                  result.Add(item);
               }
            }
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
               int length = (int)Coordutils.Distance(coords[i].Key,
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

      private void CheckPoint(VisitQueueItem vi, Org org)
      {
         if (org == null || org.Address == null || org.Address.Length == 0)
            return;

         Location l = Route.GetLocation(org.Address);
         if (l != null)
         {
            vi.address = org.Address;

            if (vi.longitude != 0 || vi.latitude != 0)
            {
               // проверим точку на удаленность от точки документа
               NapoleonManager.Location check = new Location(vi.latitude, vi.longitude);
               if (NapoleonManager.Location.Distance(l, check) > 100)
               {
                  vi.address = org.Address;
                  vi.factAddress = check.GetAddress();
                  vi.outOfRange = true;
               }
            }
         }
      }

      //Создать список посещений, отсортированный по дате
      private List<VisitQueueItem> MakeVisitQueue(List<VisitQueueItem> result)
      {
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
         if(btnSales.Checked)
            foreach (Sales sales in dsSales.Data)
            {
               VisitQueueItem vi = new VisitQueueItem(sales.created, sales.org,
                  sales.latitude, sales.longitude, new VisitType(ObjType.TObjType.Sales));
               CheckPoint(vi, sales.org);
               result.Add(vi);
            }

         if(btnVisit.Checked)
            foreach (VisitInfo visit in dsVisit.Data)
            {
               VisitQueueItem vi = new VisitQueueItem(visit.date, visit.org,
                  visit.latitude, visit.longitude, new VisitType(ObjType.TObjType.OtVisit));
               CheckPoint(vi, visit.org);
               result.Add(vi);
            }

         if(btnRemnants.Checked)
            foreach (OrgRemnants orgRemnants in dsOrgRemnants.Data)
            {
               VisitQueueItem vi = new VisitQueueItem(orgRemnants.date, orgRemnants.org,
                  orgRemnants.latitude, orgRemnants.longitude, new VisitType(ObjType.TObjType.OtOrgRemnants));
               CheckPoint(vi, orgRemnants.org);
               result.Add(vi);
            }

         if(btnOrder.Checked)
            foreach (Order order in dsOrder.Data)
            {
               VisitQueueItem vi = new VisitQueueItem(order.Created, order.org, order.latitude,
                  order.longitude, new VisitType(ObjType.TObjType.OtOrder));
               CheckPoint(vi, order.org);
               vi.sum = order.Sum();
               result.Add(vi);
            }

         if (btnIncass.Checked)
         {
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
         }
#if MOVEMENT_DOC
         if(btnMove.Checked)
            foreach (MoveDoc mve in dsMove.Data)
            {
               VisitQueueItem vi = new VisitQueueItem(mve.date, mve.org,
                  mve.latitude, mve.longitude, new VisitType(ObjType.TObjType.OtVisit));
               CheckPoint(vi, mve.org);
               result.Add(vi);
            }
#endif
         //AddStopping(result, route);
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

      private void tsbMessage_Click(object sender, EventArgs e)
      {
         Agent agent = new Agent();
         agent.id = (cbAgents.SelectedItem as AgentItem).id;
         agent.name = (cbAgents.SelectedItem as AgentItem).name;
         FmMessage.MessageShow(agent);
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
         UpdateDataSets((cbAgents.SelectedItem as AgentItem).id, 
            dtpStart.Value.Date, dtpFinish.Value.AddDays(1).Date);
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
         sb.AppendLine("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
         sb.AppendLine("</head>");
         sb.Append("Агент: ").Append((cbAgents.SelectedItem as AgentItem).name).Append("<br>");
         sb.Append("Дата: ").Append(dtpStart.Value.ToString("dd.MM.yyyy")).Append("<br>");
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

      private void btnDoc_CheckedChanged(object sender, EventArgs e)
      {
         bool ch = ((ToolStripButton)sender).Checked;

         btnOrder.Enabled = ch;
         btnVisit.Enabled = ch;
         btnRemnants.Enabled = ch;
         btnQuestion.Enabled = ch;
         btnIncass.Enabled = ch;
      }

      private void btnUndoc_CheckedChanged(object sender, EventArgs e)
      {
         bool ch = ((ToolStripButton)sender).Checked;

         btnOnlyFromRoute.Enabled = ch;
      }
   }

   

   [Serializable]
   class FmCoverAreaSetting : BaseFormSetting<FmCoverAreaSetting>
   {
      public string filter = string.Empty;
      public int numInterval = FmRoute.DEF_NUM_INTERVAL;
      public bool roadPoints = false;
      public DateTime timeBegin = new DateTime(1980, 1, 1, 0, 0, 0);
      public DateTime timeEnd = new DateTime(1980, 1, 1, 23, 59, 0);
   }

   class OrgVisitType : VisitType
   {
      public OrgVisitType()
         :base("")
      {
      }

      public override bool IsStopType
      {
         get { return false; }
      }
   }
}
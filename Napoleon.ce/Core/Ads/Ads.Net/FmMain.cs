using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Threading;
using GRSoft.Network;
using System.Data.Odbc;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;
using System.Data.Common;
using Microsoft.Win32;

namespace GRSoft.Ads
{
   public partial class FmMain : Form
   {
      public static readonly int INTERVAL_IN_MIN = 20;
      private DsBrigade dsBrigade;
      private DsOrderRcv dsOrder;
      public Order selectedOrder;
      public Brigade selectedBrigade;
      private DsClient dsClient;
      private DsJobType dsJobType;
      private DsDistrict dsDistrict;
      private DsSchedule dsSchedule;
      private DsUserOrder dsUserOrder;
      private bool showServerErrorMessage = true;
      public static FmMain instance;
      private DsWorkType dsWorkType;

      ToolTip listViewToolTip = new ToolTip();
      private System.Timers.Timer tooltipTimer;

      private System.Windows.Forms.Timer refreshTimer = new System.Windows.Forms.Timer();
      private System.Windows.Forms.Timer higlighTimer = new System.Windows.Forms.Timer();
      private System.Windows.Forms.Timer userOrderAnimation = new System.Windows.Forms.Timer();
      public bool updateInProcess = false;
      private List<Rectangle> refreshBounds = new List<Rectangle>();
      private Higliht highlightRejected;
      private Higliht highlightMissed;
      private readonly static string MISSED_ORDER_FOLDER = "//missed_order";
      List<string> missedOrdersList = new List<string>();
      ListSchedule listScedule;

      private const int INVALID_INDEX = -1;

      public event EmptyInvoker OnRefreshData;

      public FmMain()
      {
         InitializeComponent();

         dsBrigade = new DsBrigade(true);
         dsOrder = new DsOrderRcv(true);
         dsClient = new DsClient(true);
         dsClient.Filter = "id is null or id is not null";
         dsJobType = new DsJobType(true);
         dsDistrict = new DsDistrict(true);
         dsSchedule = new DsSchedule(true);
         dsUserOrder = new DsUserOrder(true);
         dsWorkType = new DsWorkType(true);

         tooltipTimer = new System.Timers.Timer();
         tooltipTimer.Interval = listViewToolTip.AutoPopDelay;
         tooltipTimer.Elapsed += new System.Timers.ElapsedEventHandler(tooltipTimer_Elapsed);
         listViewToolTip.IsBalloon = true;

         List<Color> rejectColorList = new List<Color>();
         rejectColorList.Add(Color.Green);
         rejectColorList.Add(Color.LightGray);
         highlightRejected = new Higliht(rejectColorList);

         List<Color> missedColorList = new List<Color>();
         missedColorList.Add(Color.Red);
         missedColorList.Add(Color.LightGray);
         highlightMissed = new Higliht(missedColorList);

         string path = Config.GetAppHomeDir() + MISSED_ORDER_FOLDER;
         Directory.CreateDirectory(path);
         path += "//" + DateTime.Now.ToString("yyyy.MM.dd");

         if (File.Exists(path))
         {
            Stream stream = File.Open(path, FileMode.Open);
            missedOrdersList = (List<string>)new BinaryFormatter().Deserialize(stream);
            stream.Close();
         }

         listScedule = new ListSchedule(delegate(Brigade brigade)
         {
            ListViewItem item = new ListViewItem(brigade.Name);
            item.Tag = brigade;
            item.SubItems.AddRange(MakeScheduleLine(brigade, datePickerCtrl1.Date));
            lvSchedule.Items.Add(item);
         });


         instance = this;

         Decorator.Adjust(this);
      }

      void tooltipTimer_Elapsed(object sender, System.Timers.ElapsedEventArgs e)
      {
         tooltipTimer.Stop();
      }

      private TimeSubItem[] MakeScheduleLine(Brigade brigade, DateTime today)
      {
         int TIME_TICK = lvSchedule.Columns.Count - 1;
         
         TimeSubItem[] result = new TimeSubItem[TIME_TICK];
         Thread.Sleep(1);
         Random rnd1 = new Random();
         DateTime begin = new DateTime(today.Year, today.Month, today.Day, 8, 0, 0);

         for (int i = 0; i < TIME_TICK; i++)
         {
            result[i] = new TimeSubItem(brigade, begin,
               GetOrder(dsOrder, brigade, begin));
            begin = begin.AddMinutes(INTERVAL_IN_MIN);
         }

         return result;
      }

      private void listView1_DrawSubItem(object sender, DrawListViewSubItemEventArgs e)
      {
         Rectangle bounds = e.Bounds;

         if (e.SubItem is TimeSubItem)
         {
            bounds.Offset(new Point(3, 2));
            bounds.Size = new Size(bounds.Size.Width - 5, bounds.Size.Height - 5);

            e.Graphics.FillRectangle(new SolidBrush(Color.White), e.Bounds);

            TimeSubItem tsi = (TimeSubItem)e.SubItem;
            bool timeExpired = DateTimeIsExpired(tsi.time);

            if (timeExpired)
            {
               e.Graphics.FillRectangle(new SolidBrush(Color.LightGray), e.Bounds);
            }

            Order order = tsi.order;
            
            if (order != null)
            {
               Color brushColor = Color.White;

               if (timeExpired && !(order.Done | order.Doing))
               {
                  if (!refreshBounds.Contains(e.Bounds))
                     refreshBounds.Add(e.Bounds);

                  if (order.Rejected)
                     brushColor = highlightRejected.GetColor(e.Bounds);
                  else if ( !missedOrdersList.Contains(order.number) && order.Missed)
                     brushColor = highlightMissed.GetColor(e.Bounds);
                  else
                  {
                     brushColor = Color.Red;

                     if (refreshBounds.Contains(e.Bounds))
                        refreshBounds.Remove(e.Bounds);
                  }
               } 
               else
               {
                  if (refreshBounds.Contains(e.Bounds))
                     refreshBounds.Remove(e.Bounds);

                  if (order.client == null 
                        || order.client.id.Trim().Length == 0 
                        || (!timeExpired && order.Rejected))
                     brushColor = Color.Green;
                  else if (order.Doing)
                     brushColor = Color.Yellow;
                  else if (order.Done)
                     brushColor = Color.Gray;
                  else
                     brushColor = Color.Red;
               }

               SolidBrush brush = new SolidBrush(brushColor);
               
               e.Graphics.FillRectangle(brush, bounds);
            }
         }
         else
         {
            Brigade b = e.Item.Tag as Brigade;

            if (b != null)
            {
               Schedule schedule = null;
               foreach (Schedule s in dsSchedule.Data)
                  if (s.brigade.Equals(b))
                  {
                     schedule = s;
                     break;
                  }

               if (schedule != null && schedule.status == (int) Schedule.Status.Reserved)
                  e.Graphics.FillRectangle(new SolidBrush(Color.LightGray), e.Bounds);
               else
                  e.Graphics.FillRectangle(new SolidBrush(Color.White), e.Bounds);

               SolidBrush brush = new SolidBrush(b.JobTypeColor);
               if (schedule != null && schedule.status == (int)Schedule.Status.Disabled)
                  brush = new SolidBrush(Color.Gray);

               e.Graphics.DrawString(b.Name, ((ListView)sender).Font, brush, bounds);
            }
         }
      }

      private class Higliht 
      {
         public List<Color> colors;
         private Dictionary<Rectangle, int> cells = new Dictionary<Rectangle, int>();
         private int index;

         public Higliht(List<Color> colors)
         {
            this.colors = colors;
         }

         public Color GetColor(Rectangle rectangle) 
         {
            if (cells.ContainsKey(rectangle))
               return colors[cells[rectangle]];
            else
               return colors[0];
         }

         public void SetNext()
         {
            if (++index == colors.Count)
               index = 0;
         }

         public void SetRectangle(Rectangle rectangle)
         {
            if (cells.ContainsKey(rectangle))
               cells[rectangle] = index;
            else
               cells.Add(rectangle, index);
         }
      }

      private void listView1_DrawColumnHeader(object sender, DrawListViewColumnHeaderEventArgs e)
      {
         ColumnHeader columnHeader = e.Header;
         e.DrawBackground();
         if (columnHeader.Index == 0)
         {
            e.DrawText();
         }
         else
         {
            SolidBrush brush = new SolidBrush(Color.Black);
            e.Graphics.DrawString(columnHeader.Text, e.Font, brush, 
               e.Bounds.Left-1, e.Bounds.Top + 4);
         }
      }

      private void miProfession_Click(object sender, EventArgs e)
      {
         FmProfession.ShowInstance();
      }

      private void miStuff_Click(object sender, EventArgs e)
      {
         FmStuff.ShowInstance();
      }

      private void miDistrict_Click(object sender, EventArgs e)
      {
         FmDistrict.ShowInstance();
      }

      private void miClient_Click(object sender, EventArgs e)
      {
         FmClient.ShowInstance();
      }

      private void miBrigade_Click(object sender, EventArgs e)
      {
         FmBrigade.ShowInstance();
      }

      private void miOrder_Click(object sender, EventArgs e)
      {
         FmOrder.ShowInstance();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         if (!showServerErrorMessage)
            showServerErrorMessage = true;

         btnRefresh_Click_Low(sender, e);
      }

      private void btnRefresh_Click_Low(object sender, EventArgs e)
      {
         if (updateInProcess)
            return;

         datePickerCtrl1.Enabled = false;

         updateInProcess = true;

         List<IDataSet> list = new List<IDataSet>();

         DBConnection dbc = Config.GetConfig().GetConnection();
         if (dsClient.Count != 0)
         {
            dsClient.Filter = string.Format("valid > ToDate('{0:dd/MM/yyyy}')", DateTime.Now.AddDays(-1));
            //dsClient.Filter = "valid > strftime('%s','now', '-1 day', 'localtime')";
         }
            //dsClient.Command = new GetCommand(dbc.login, dbc.password, new string[] { dsClient.Name });
         list.Add(dsClient);

         list.Add(dsJobType);
         list.Add(dsBrigade);
         list.Add(dsOrder);
         list.Add(dsDistrict);
         list.Add(dsSchedule);
         list.Add(dsUserOrder);
         list.Add(dsWorkType);

         DateTime begin = GetSelectedDate();
         DateTime end = begin.AddDays(1);
         dsOrder.Filter = string.Format("planbegin >= ToDate('{0:dd/MM/yyyy}') and planbegin < ToDate('{1:dd/MM/yyyy}')",
            begin, end);
         dsSchedule.Filter = string.Format("date = ToDate('{0:dd/MM/yyyy}')", begin);
         dsUserOrder.Filter = string.Format("unread=0 or unread is NULL and created >= ToDate('{0:dd/MM/yyyy}')", begin.AddDays(-1)); ;

         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed, DataModule_OnDataResponceError);

         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(dbc, list, FmWait.ProgressIndicator));
      }

      private DateTime GetSelectedDate()
      {
         return new DateTime(datePickerCtrl1.Date.Year,
            datePickerCtrl1.Date.Month, datePickerCtrl1.Date.Day);
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         Invoke(new InvokeDelegate(delegate() 
            { 
               RefreshData(); 
               updateInProcess = false;
               lvSchedule.Refresh();
               datePickerCtrl1.Enabled = true;
            }));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         updateInProcess = false;

         if (showServerErrorMessage)
         {
            showServerErrorMessage = false;
            MessageBox.Show(e.Msg);
         }

         Invoke(new InvokeDelegate(delegate(){datePickerCtrl1.Enabled = true;}));
      }

      private bool CompareList(List<string> list1, List<string> list2)
      {
         bool result = false;

         if (list1.Count == list2.Count)
         {
            list1.Sort();
            list2.Sort();

            for (int i = 0; i < list1.Count; i++)
            { 
               if (!list1[i].Equals(list2[i]))
                  break;
            }

            result = true;
         }

         return result;
      }

      void RefreshData()
      {
         tcDistrict.SuspendLayout();

         fillListSchedule();
         refreshBounds.Clear();
         TabPage tbMain = tcDistrict.TabPages[0];

         List<string> tabPagesOld = new List<string>();
         
         foreach(TabPage tp in tcDistrict.TabPages)
            tabPagesOld.Add(tbMain.Text);

         List<string> tabPagesNew = new List<string>();
         tabPagesNew.Add(tabPagesOld[0]);

         foreach(District d in dsDistrict.Data)
         {
            tabPagesNew.Add(d.Name);
         }

         int currentTabPageIndex = tcDistrict.SelectedIndex;

         if (!CompareList(tabPagesOld, tabPagesNew))
         {
            tcDistrict.TabPages.Clear();
            tcDistrict.TabPages.Add(tbMain);

            List<District> list = new List<District>();
            list.AddRange(dsDistrict.Values);

            list.Sort(new Comparison<District>(delegate(District d1, District d2) { return d1.Name.CompareTo(d2.Name); }));

            foreach (District d in list)
            {
               ToolStripButton btn = new ToolStripButton(d.Name);
               TabPage tp = new TabPage(d.Name);
               tp.Padding = new System.Windows.Forms.Padding(3);
               tp.Tag = d;
               tp.UseVisualStyleBackColor = true;
               tcDistrict.TabPages.Add(tp);
            }

         }
         btnRoute.Enabled = dsBrigade.Count > 0;

         bool hasUptimeRejected = false;

         foreach (Order o in dsOrder.Data)
         {
            if (o.Missed || (o.Rejected && DateTimeIsExpired(o.planbegin)))
            {
               hasUptimeRejected = true;
               break;
            }
         }

         if (Config.GetConfig().alert &&  hasUptimeRejected)
         {
            if (!higlighTimer.Enabled)
               higlighTimer.Start();
         }
         else if (higlighTimer.Enabled)
         {
            higlighTimer.Stop();
            refreshBounds.Clear();
         }

         if (tcDistrict.SelectedIndex != currentTabPageIndex &&
               currentTabPageIndex < tcDistrict.TabCount)
            tcDistrict.SelectedIndex = currentTabPageIndex;

         btnUserOrder.Visible = HasUnreadOrders();
         tcDistrict.ResumeLayout();

         if (OnRefreshData != null)
            OnRefreshData();
      }

      public bool HasUnreadOrders()
      {
         foreach (UserOrder uo in dsUserOrder.Data)
            if (!uo.IsMarkAsRead())
               return true;

         return false;
      }

      

      

      private void fillListSchedule()
      {
         lvSchedule.SuspendLayout();
         lvSchedule.Items.Clear();
         listScedule.DoList(dsSchedule, dsBrigade, (District)tcDistrict.SelectedTab.Tag);
         lvSchedule.ResumeLayout();
      }

      private void RefreshDb()
      {
         btnRefresh_Click(null, null);
      }

      internal static Order GetOrder(DsOrderRcv dsOrder, Brigade brigade, DateTime worktime)
      {
         foreach (Order order in dsOrder.Data)
         {
            if (order.brigade != null &&
               order.brigade.id.Equals(brigade.id) &&
               (worktime >= order.planbegin && worktime < order.planend))
               return order;
         }

         return null;
      }

      private void contextMenuStrip1_Opening(object sender, CancelEventArgs e)
      {
         miDel.Visible = true;
         miMove.Visible = true;
         miMessage.Visible = true;

         if (selectedOrder == null || (selectedOrder._params > 1 && !selectedOrder.Rejected))
         {
            miDel.Visible = false;
            miMove.Visible = false;
         }

         if (selectedBrigade == null)
            miMessage.Visible = false;
      }

      private void miDelToolStripMenuItem_Click(object sender, EventArgs e)
      {
         if (MessageBox.Show("Заявка будет удалена. Удалить?",
               "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            DsOrder dsToDel = new DsOrder(false);
            dsToDel.Add(selectedOrder.created, selectedOrder);
            List<IDataSet> delSet = new List<IDataSet>();
            delSet.Add(dsToDel);

            DsOrderDel dsOrderDel = new DsOrderDel(false);
            OrderDel orderDel = new OrderDel();
            orderDel.created = selectedOrder.created;
            orderDel.userid = selectedOrder.brigade.id;
            dsOrderDel.Add(orderDel.created, orderDel);
            List<IDataSet> updSet = new List<IDataSet>();
            updSet.Add(dsOrderDel);

            if (DataModule.UpdateDataSet(updSet, delSet, null, Config.GetConfig().GetConnection()))
            {
               btnRefresh_Click(null, null);
            }
            else MessageBox.Show("Ошибка при удалении записи");
         }
      }

      private void miMoveToolStripMenuItem_Click(object sender, EventArgs e)
      {
         if (selectedOrder != null)
            FmCalendar.ShowInstance(selectedOrder, RefreshDb);
      }

      private void lvSchedule_MouseUp(object sender, MouseEventArgs e)
      {
         selectedOrder = null;
         selectedBrigade = null;

         ListViewHitTestInfo info = lvSchedule.HitTest(e.X, e.Y);

         if (info != null)
         {
            ListViewItem.ListViewSubItem si = info.SubItem;
            if (si is TimeSubItem)
            {
               TimeSubItem tsi = (TimeSubItem)si;
               selectedOrder = tsi.order;
               selectedBrigade = tsi.brigade;

               if (e.Button == MouseButtons.Left)
               {
                  if (tsi.order != null && DateTimeIsExpired(tsi.time) && 
                        !missedOrdersList.Contains(tsi.order.number))
                  {
                     missedOrdersList.Add(tsi.order.number);

                     Stream stream = File.Open(Config.GetAppHomeDir() + MISSED_ORDER_FOLDER +
                        "//" + DateTime.Now.ToString("yyyy.MM.dd"), FileMode.OpenOrCreate);
                     new BinaryFormatter().Serialize(stream, missedOrdersList);
                     stream.Close();
                     lvSchedule.Invalidate();
                  }

                  if (tsi.order != null || !DateTimeIsExpired(tsi.time))
                  {
                     Schedule schedule = null;

                     foreach (Schedule s in dsSchedule.Data)
                        if (s.brigade.Equals(tsi.brigade))
                        {
                           schedule = s;
                           break;
                        }

                     if (schedule != null && schedule.status == (int)Schedule.Status.Disabled)
                        MessageBox.Show("Нельзя создать заявку на нерабочий день", "Ошибка",
                           MessageBoxButtons.OK, MessageBoxIcon.Error);
                     else
                        FmOrderEdit.ShowInstance(tsi.order, tsi.brigade, tsi.time,
                           new EmptyInvoker(RefreshDb));
                  }
               }
            }

            if (info.Item != null &&
               info.Item.Tag is Brigade)
               selectedBrigade = (Brigade)info.Item.Tag;
         }
      }

      public static bool DateTimeIsExpired(DateTime time)
      {
         return time < DateTime.Now;
      }

      private void FmMain_Load(object sender, EventArgs e)
      {
         //---------------------- Небольшой фикс для интернет экплорера -------------------
         RegistryKey RegKeyWrite = Registry.CurrentUser;
         RegKeyWrite = RegKeyWrite.CreateSubKey(@"Software\Microsoft\Internet Explorer\Styles");
         RegKeyWrite.SetValue("MaxScriptStatements", 1000000000);
         RegKeyWrite.Close();
         //--------------------------------------------------------------------------------

         if (!Config.Exist())
         {
            DialogResult dr = new FmWelcome().ShowDialog();
            if (dr != DialogResult.OK)
            {
               Close();
            }
         }

         btnRoute.Enabled = false;
         listViewToolTip.Popup += new PopupEventHandler(listViewToolTip_Popup);

         StartRefresh();

         higlighTimer.Interval = 600;
         higlighTimer.Tick += new EventHandler(delegate(object s, EventArgs ea) 
            {
               foreach (Rectangle r in refreshBounds)
               {
                  lvSchedule.Invalidate(r);
                  highlightMissed.SetRectangle(r);
                  highlightRejected.SetRectangle(r);
               }

               highlightMissed.SetNext();
               highlightRejected.SetNext();
            });

         btnUserOrder.Visible = false;

         userOrderAnimation.Interval = 1000;
         userOrderAnimation.Tick += new EventHandler(delegate(object s, EventArgs ea)
         {
            if (imageList1.Images.Count > 0)
            {
               btnUserOrder.Image = btnUserOrder.Image == null ? imageList1.Images[0] : null;
               btnUserOrder.Invalidate();
            }
         });
         userOrderAnimation.Start();

         datePickerCtrl1.Date = DateTime.Now;
      }

      private void StartRefresh()
      {
         DisableRefreshTimer();
         refreshTimer.Interval = Config.GetConfig().refreshTime * 60 * 1000;
         refreshTimer.Tick += new EventHandler(delegate(object s, EventArgs ea) { btnRefresh_Click_Low(null, null); });
         refreshTimer.Start();
      }

      void listViewToolTip_Popup(object sender, PopupEventArgs e)
      {
         Point pnt = MousePosition;
         ListViewHitTestInfo hitInfo = lvSchedule.HitTest(lvSchedule.PointToClient(MousePosition));

         if (hitInfo != null)
         {
            if (hitInfo.SubItem is TimeSubItem)
            {
               TimeSubItem tsi = (TimeSubItem)hitInfo.SubItem;

               if (tsi != null && tsi.order != null)
               {
                  return;
               }
            }
         }

         e.Cancel = true;
      }

      private void miClose_Click(object sender, EventArgs e)
      {
         Close();
      }

      private void miSetting_Click(object sender, EventArgs e)
      {
         FmSetting.ShowInstance(new EmptyInvoker(
            delegate() 
            {
               StartRefresh();
               RefreshData();
            }));
      }

      private void btnRoute_Click(object sender, EventArgs e)
      {
         FmRoute.ShowInstance(null, GetSelectedDate());
      }

      private void miImportKladr_Click(object sender, EventArgs e)
      {
         ImportKladr.Execute(this);
      }

      private void кладрToolStripMenuItem_Click(object sender, EventArgs e)
      {
         FmKladr.ShowInstance();
      }

      private void miPrice_Click(object sender, EventArgs e)
      {
         FmPrice.ShowInstance();
      }

      private void miMessage_Click(object sender, EventArgs e)
      {
         if (selectedBrigade != null)
            FmMessage.MessageShow(selectedBrigade);
      }

      private void miUserOrder_Click(object sender, EventArgs e)
      {
         FmUserOrder.ShowInstance();
      }

      private void miGSMReport_Click(object sender, EventArgs e)
      {
         FmGsmReport.ShowInstance();
      }

      private void lvSchedule_MouseMove(object sender, MouseEventArgs e)
      {
         ListViewHitTestInfo hitInfo = ((ListView)sender).HitTest(e.X, e.Y);

         if (hitInfo != null)
         {
            if (hitInfo.SubItem is TimeSubItem)
            {
               TimeSubItem tsi = (TimeSubItem)hitInfo.SubItem;

               if (tsi != null && tsi.order != null && !tooltipTimer.Enabled)
               {
                  tooltipTimer.Start();
                  Order order = tsi.order;
                  listViewToolTip.SetToolTip((Control)sender, createToolTipText(order));
                  listViewToolTip.Tag = order;
                  return;
               }
            }
         }

         tooltipTimer.Stop();
      }

      private string createToolTipText(Order order)
      {
         string result = string.Empty;

         if (order != null)
         {
            if (order.Address.Length > 0)
               result = String.Format("{0}\n{1}, {2}",order.Address, order.ClientName, order.text);
            else
               result = String.Format("{0}, {1}", order.ClientName, order.text);
         }

         return result;
      }

      private void miJobsType_Click(object sender, EventArgs e)
      {
         FmJobsType.ShowInstance(null, null);
      }

      private void tcDistrict_Selected(object sender, TabControlEventArgs e)
      {
         if (e.TabPage != null &&
            e.TabPage.Controls.Find(lvSchedule.Name, true).Length == 0)
         {
            e.TabPage.SuspendLayout();
            e.TabPage.Controls.Add(lvSchedule);
            e.TabPage.ResumeLayout();
         }

         if (e.TabPage != null)
            fillListSchedule();
      }

      private void miPdaPasw_Click(object sender, EventArgs e)
      {
         new FmPdaPasw().ShowDialog();
      }

      public bool DisableRefreshTimer()
      {
         bool result = refreshTimer.Enabled;

         if (result)
            refreshTimer.Stop();

         return result;
      }

      private void Schedule_Click(object sender, EventArgs e)
      {
         new FmSchedule(null).Show();
      }

      private void btnUserOrder_Click(object sender, EventArgs e)
      {
         miUserOrder_Click(null, null);
      }

      private void btnNewOrder_Click(object sender, EventArgs e)
      {
         FmOrderEdit.ShowInstance(datePickerCtrl1.Date.Date, new EmptyInvoker(RefreshDb));
      }

      public List<Schedule> GetTodaySchedule()
      {
         List<Schedule> result = new List<Schedule>();
         result.AddRange(dsSchedule.Values);
         return result;
      }

      private void miWorkType_Click(object sender, EventArgs e)
      {
         new FmWorkType().Show();
      }

      private void miBrigadeAddress_Click(object sender, EventArgs e)
      {
         new FmBrigadeAddress().Show();
      }

      private void miUpdateKladr_Click(object sender, EventArgs e)
      {
         KladrLoader.Execute(this);
      }

      private void datePickerCtrl1_OnDayChanged(DateTime date)
      {
         btnRefresh_Click(null, null);
      }
   }

   class TimeSubItem : ListViewItem.ListViewSubItem
   {
      public DateTime time;
      public Brigade brigade;
      public Order order;

      public TimeSubItem(Brigade brigade, DateTime time, Order order)
      {
         this.time = time;
         this.order = order;
         this.brigade = brigade;
      }
   }

   class KladrLoader
   {
      public static void Execute(FmMain form)
      {
         
            if (form.updateInProcess)
            {
               MessageBox.Show("Обновление с базой данных в процессе, попробуйте выполнить импрот позже.");
            }
            else
            {
               form.updateInProcess = true;

               new Thread(new ThreadStart(delegate()
               {
                  try
                  {
                     form.Invoke(new InvokeDelegate(delegate() { FmWait.ShowForm(form, true); }));

                     Console.Out.WriteLine("start update: " + DateTime.Now.ToString());
                     DsKladr dsKladr = (DsKladr)DataModule.Get(Kladr.OBJECT_NAME) ?? new DsKladr(true);
                     DsStreet dsStreet = (DsStreet)DataModule.Get(Street.OBJECT_NAME) ?? new DsStreet(true);

                     List<IDataSet> list = new List<IDataSet>();
                     list.Add(dsKladr);
                     list.Add(dsStreet);

                     string[] filters = new string[] {
                        "code like '0%'",
                        "code like '1%'",
                        "code like '2%'",
                        "code like '3%'",
                        "code like '4%'",
                        "code like '5%'",
                        "code like '6%'",
                        "code like '7%'",
                        "code like '8%'",
                        "code like '9%'"};

                     Console.Out.WriteLine("start refresh: " + DateTime.Now.ToString());
                     
                     DBConnection dbc = Config.GetConfig().GetConnection();
                     int timeout = dbc.ReceiveTimeout;
                     dbc.ReceiveTimeout = 10 * 60 * 1000;

                     Console.Out.WriteLine("end  refresh: " + DateTime.Now.ToString());

                     DbConnection con = LocalDataBase.Instance().Connection;

                     using (con)
                     {
                        con.Open();
                        DbCommand delKladr = con.CreateCommand();
                        delKladr.CommandText = "delete from kladr";
                        delKladr.ExecuteNonQuery();

                        DbCommand delStreet = con.CreateCommand();
                        delStreet.CommandText = "delete from street";
                        delStreet.ExecuteNonQuery();

                        foreach (string filter in filters)
                        {
                           dsStreet.Filter = filter;
                           DataModule.RefreshGiveSets(dbc, list, null).Join();

                           DbCommand insertKladr = con.CreateCommand();
                           insertKladr.CommandText = "INSERT INTO kladr VALUES(@code, @name, @socr)";

                           DbTransaction trans = con.BeginTransaction();

                           const int COMMIT_INTERVAL = 10000;
                           int i = 0;

                           Console.Out.WriteLine("start write kladr: " + DateTime.Now.ToString());

                           DbParameter code = null, name = null, socr = null;
                           LocalDataBase db = LocalDataBase.Instance();
                           foreach (Kladr kladr in dsKladr.Data)
                           {
                              i++;
                              if (insertKladr.Parameters.Count == 0)
                              {
                                 code = db.CreateParameter("code", kladr.code);
                                 name = db.CreateParameter("name", kladr.name);
                                 socr = db.CreateParameter("socr", kladr.socr);
                                 insertKladr.Parameters.AddRange(new DbParameter[] { code, name, socr });
                              }
                              else
                              {
                                 code.Value = kladr.code;
                                 name.Value = kladr.name;
                                 socr.Value = kladr.socr;
                              }
                              //insertKladr.Parameters.Clear();
                              //insertKladr.Parameters.Add(db.CreateParameter("code", kladr.code));
                              //insertKladr.Parameters.Add(db.CreateParameter("name", kladr.name));
                              //insertKladr.Parameters.Add(db.CreateParameter("socr", kladr.socr));
                              insertKladr.ExecuteNonQuery();

                              if ((i % COMMIT_INTERVAL) == 0)
                              {
                                 trans.Commit();
                                 trans = con.BeginTransaction();
                              }
                           }
                           trans.Commit();

                           Console.Out.WriteLine("end write kladr: " + DateTime.Now.ToString());

                           i = 0;

                           trans = con.BeginTransaction();

                           DbCommand insertStreet = con.CreateCommand();
                           insertStreet.CommandText = "INSERT INTO street VALUES(@code, @name, @socr)";

                           Console.Out.WriteLine("start write street: " + DateTime.Now.ToString());

                           foreach (Street street in dsStreet.Data)
                           {
                              i++;
                              if (insertStreet.Parameters.Count == 0)
                              {
                                 code = db.CreateParameter("code", street.code);
                                 name = db.CreateParameter("name", street.name);
                                 socr = db.CreateParameter("socr", street.socr);
                                 insertStreet.Parameters.AddRange(new DbParameter[] { code, name, socr });
                              }
                              else
                              {
                                 code.Value = street.code;
                                 name.Value = street.name;
                                 socr.Value = street.socr;
                              }
                              //insertStreet.Parameters.Clear();
                              //insertStreet.Parameters.Add(db.CreateParameter("code", street.code));
                              //insertStreet.Parameters.Add(db.CreateParameter("name", street.name));
                              //insertStreet.Parameters.Add(db.CreateParameter("socr", street.socr));
                              insertStreet.ExecuteNonQuery();

                              if ((i % COMMIT_INTERVAL) == 0)
                              {
                                 trans.Commit();
                                 trans = con.BeginTransaction();
                              }
                           }

                           trans.Commit();

                           dsKladr.Clear();
                           dsStreet.Clear();

                           list.Remove(dsKladr);
                        }

                        dbc.ReceiveTimeout = timeout;

                        dsKladr = null;
                        dsStreet = null;

                        GC.Collect();
                        GC.WaitForPendingFinalizers();

                        Console.Out.WriteLine("end write street: " + DateTime.Now.ToString());

                        Console.Out.WriteLine("end refresh: " + DateTime.Now.ToString());

                        form.Invoke(new InvokeDelegate(delegate() { FmWait.CloseForm(); }));
                        form.Invoke(new InvokeDelegate(delegate() { MessageBox.Show("Локальная загрузка КЛАДР завершена."); }));

                        form.updateInProcess = false;
                     }
                  }
                  catch (Exception e)
                  {
                     //using (StreamWriter writer = new StreamWriter("log.txt"))
                     //{
                     //   writer.Write(e.Message);
                     //}
                     form.Invoke(new InvokeDelegate(delegate() { FmWait.CloseForm(); }));
                     form.Invoke(new InvokeDelegate(delegate() { MessageBox.Show("Ошибка: " + e.Message); }));
                  }
               })).Start();
            }
         }
      }

   class ImportKladr
   {
      public static void Execute(FmMain form)
      {
         if (form.updateInProcess)
         {
            MessageBox.Show("Обновление с базой данных в процессе, попробуйте выполнить импрот позже.");
         }
         else
         {
            form.updateInProcess = true;

            if (form.folderBrowser.ShowDialog() == DialogResult.OK)
               new Thread(new ThreadStart(delegate()
                  {
                     form.Invoke(new InvokeDelegate(delegate() { FmWait.ShowForm(form, true); }));

                     OdbcConnection conn = new OdbcConnection();
                     conn.ConnectionString = "Driver={Microsoft dBase Driver (*.dbf)};" +
                        "SourceType=DBF;Exclusive=No;" +
                        "Collate=Machine;NULL=NO;DELETED=NO;" +
                        "BACKGROUNDFETCH=NO;" +
                        "DBQ=" + form.folderBrowser.SelectedPath + ";";
                     conn.Open();

                     form.Invoke(new InvokeDelegate(delegate() { FmWait.AddMessage = "Чтение данных адресов."; }));
                     OdbcCommand cmd = conn.CreateCommand();
                     cmd.CommandText = "SELECT * FROM kladr";
                     DataTable dataTable = new DataTable();
                     dataTable.Load(cmd.ExecuteReader());

                     DsKladr dsKladr = new DsKladr(false);

                     List<IDataSet> list = new List<IDataSet>();
                     list.Add(dsKladr);

                     foreach (DataRow row in dataTable.Rows)
                     {
                        Kladr kladr = new Kladr();
                        kladr.code = row["code"].ToString();
                        kladr.name = row["name"].ToString();
                        kladr.socr = row["socr"].ToString();

                        dsKladr.Add(kladr.code, kladr);
                     }

                     DBConnection dbConn = Config.GetConfig().GetConnection();
                     int oldTimeout = dbConn.ReceiveTimeout;
                     dbConn.ReceiveTimeout = 20 * 60 * 1000;

                     form.Invoke(new InvokeDelegate(delegate() { FmWait.AddMessage = "Запись адресов в БД."; }));

                     if (!DataModule.UpdateDataSet(list, null, null, dbConn))
                        MessageBox.Show("Ошибка записи в базу данных");


                     form.Invoke(new InvokeDelegate(delegate() { FmWait.AddMessage = "Чтение данных улиц."; }));
                     dataTable.Clear();
                     cmd.CommandText = "SELECT * FROM street";
                     dataTable.Load(cmd.ExecuteReader());

                     DsStreet dsStreet = new DsStreet(false);

                     foreach (DataRow row in dataTable.Rows)
                     {
                        Street street = new Street();
                        street.code = row["code"].ToString();
                        street.name = row["name"].ToString();
                        street.socr = row["socr"].ToString();

                        dsStreet.Add(street.code, street);
                     }

                     list.Clear();
                     list.Add(dsStreet);

                     form.Invoke(new InvokeDelegate(delegate() { FmWait.AddMessage = "Запись улиц в БД."; }));

                     if (!DataModule.UpdateDataSet(list, null, null, dbConn))
                        MessageBox.Show("Ошибка записи в базу данных");

                     dbConn.ReceiveTimeout = oldTimeout;
                     form.Invoke(new InvokeDelegate(delegate() { FmWait.CloseForm(); }));
                     form.Invoke(new InvokeDelegate(delegate() { MessageBox.Show("Импорт КЛАДР завершен."); }));

                     form.updateInProcess = false;
                  })).Start();
               else 
                  form.updateInProcess = false;
         }
      }

      private static string addLeadingZero(int val, int width)
      {
         StringBuilder result = new StringBuilder();
         
         string valStr = val.ToString();
         int i = width - valStr.Length;

         if (i > 0)
         {
            String s = new String('0', i);
            result.Append(s);
         }

         result.Append(val);
         return result.ToString();
      }
   }

   delegate void BrigadeVisitor(Brigade brigade);

   class ListSchedule
   {
      BrigadeVisitor visitor;

      public ListSchedule(BrigadeVisitor visitor)
      {
         this.visitor = visitor;
      }

      public void DoList(DsSchedule dsSchedule, DsBrigade dsBrigade, District district)
      {
         // удалим задания из удаленных бригад
         List<int> remove = new List<int>();
         foreach (KeyValuePair<int, Schedule> kv in dsSchedule)
            if (kv.Value.brigade == null)
               remove.Add(kv.Key);
         foreach (int key in remove)
            dsSchedule.Remove(key);

         List<Brigade> list = new List<Brigade>();
         list.AddRange(dsBrigade.Values);
         list.Sort(new Comparison<Brigade>(
            delegate(Brigade b1, Brigade b2)
            {
               int result = 0;
               Schedule s1 = null;
               Schedule s2 = null;

               foreach (Schedule s in dsSchedule.Data)
                  if (s1 == null && s.brigade.Equals(b1))
                     s1 = s;
                  else if (s2 == null && s.brigade.Equals(b2))
                     s2 = s;

               if (s1 == null)
               {
                  s1 = new Schedule();
                  s1.status = (int)Schedule.Status.Active;
               }

               if (s2 == null)
               {
                  s2 = new Schedule();
                  s2.status = (int)Schedule.Status.Active;
               }

               result = s1.status.CompareTo(s2.status);

               if (result == 0)
                  result = b1.Name.CompareTo(b2.Name);

               return result;
            }));

         foreach (Brigade b in list)
         {
            Schedule schedule = null;

            foreach (Schedule s in dsSchedule.Data)
               if (s.brigade.Equals(b))
               {
                  schedule = s;
                  break;
               }

            if (district == null || (b.hasService(district) && schedule == null) ||
               (schedule != null &&
               ((schedule.hasService(district) &&
               (schedule.status == (int)Schedule.Status.Active ||
               schedule.status == (int)Schedule.Status.Reserved)))))
            {
               visitor(b);
            }
         }
      }
   }
}

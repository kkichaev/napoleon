using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Drawing.Drawing2D;

namespace GRSoft.Ads
{
   public partial class FmCalendar : Form
   {
      private static FmCalendar instance;
      private DateTime startOfWeek;
      private DateTime endOfWeek;
      private DsOrderRcv dsOrderRcv;
      private Order order;
      private EmptyInvoker postProcess;

      public FmCalendar(Order order)
      {
         InitializeComponent();
         this.order = order;
         DateTime now = new DateTime(DateTime.Now.Year, DateTime.Now.Month, DateTime.Now.Day);
         int dayOfWeek = (int)now.DayOfWeek;
         dayOfWeek = dayOfWeek == 0 ? 7 : dayOfWeek;
         startOfWeek = now.AddDays(1 - (int)now.DayOfWeek);
         endOfWeek = startOfWeek.AddDays(6);
         dsOrderRcv = (DsOrderRcv)DataModule.Get(OrderRcv.OBJECT_NAME);

         Text += " бригада - " + order.BrigadeName;
      }

      public static void ShowInstance(Order order, EmptyInvoker postProcess)
      {
         if (instance == null)
         {
            instance = new FmCalendar(order);
            instance.postProcess = postProcess;
            instance.ShowDialog();
         }
         else
            instance.Activate();
      }

      private void FmCalendar_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;

         if (postProcess != null)
            postProcess();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         Brigade brigade = cbBrigade.SelectedItem as Brigade;

         if (brigade != null)
         {
            dsOrderRcv.Filter = string.Format("planbegin >= ToDate('{0:dd/MM/yyyy}') and planbegin < ToDate('{1:dd/MM/yyyy}') and userid='{2}'",
               startOfWeek, endOfWeek.AddDays(1), brigade.id);

            List<IDataSet> list = new List<IDataSet>();
            list.Add(dsOrderRcv);

            DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed, DataModule_OnDataResponceError);
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), list, FmWait.ProgressIndicator);
         }
         else
            MessageBox.Show("Выберите бригаду");
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();

         Invoke(new InvokeDelegate(RefreshData));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         MessageBox.Show(e.Msg);
      }

      void RefreshData()
      {
         Brigade brigade = cbBrigade.SelectedItem as Brigade;

         if (brigade != null)
         {
            lvCalendar.BeginUpdate();
            lvCalendar.Items.Clear();

            DateTime startTime = new DateTime(DateTime.Now.Year, DateTime.Now.Month, DateTime.Now.Day, 8, 0, 0);
            DateTime endTime = new DateTime(DateTime.Now.Year, DateTime.Now.Month, DateTime.Now.Day, 20, 0, 0);

            while (startTime <= endTime)
            {
               DateTime step = startOfWeek;
               DateTime time = new DateTime(step.Year, step.Month, step.Day,
                 startTime.Hour, 0, 0);
               step = step.AddHours(startTime.Hour).AddMinutes(startTime.Minute);
               ListViewItem lvi = new ListViewItem(step.ToShortTimeString());

               int clmnInd = 1;
               string[] dayShortNames = new string[] { "пн", "вт", "ср", "чт", "пт", "сб", "вс" };

               while (step <= endOfWeek.AddDays(1))
               {
                  lvCalendar.Columns[clmnInd].Text =
                     String.Format("{0}({1})", step.ToString("dd.MM.yy"), dayShortNames[clmnInd - 1]);
                  clmnInd++;
                  TimeSubItem tsi = new TimeSubItem(brigade, step,
                     FmMain.GetOrder(dsOrderRcv, brigade, step));
                  lvi.SubItems.Add(tsi);
                  step = step.AddDays(1);
               }

               startTime = startTime.AddMinutes(FmMain.INTERVAL_IN_MIN);

               lvCalendar.Items.Add(lvi);
            }

            lvCalendar.EndUpdate();
         }
      }

      private void lvCalendar_DrawSubItem(object sender, DrawListViewSubItemEventArgs e)
      {
         Rectangle bounds = e.Bounds;
         bounds.Offset(new Point(3, 2));
         bounds.Size = new Size(bounds.Size.Width - 5, bounds.Size.Height - 5);

         if (e.SubItem is TimeSubItem)
         {
            e.Graphics.FillRectangle(new SolidBrush(Color.White), e.Bounds);
            SolidBrush brush = new SolidBrush(((TimeSubItem)e.SubItem).order != null ?
               ((TimeSubItem)e.SubItem).order.created.Equals(order.created) ? Color.Blue : Color.Red : Color.White);
            
            e.Graphics.FillRectangle(brush, bounds);
         }
         else
         {
            e.Graphics.FillRectangle(new SolidBrush(Color.White), bounds);
            e.DrawText();
         }
      }

      private void lvCalendar_DrawColumnHeader(object sender, DrawListViewColumnHeaderEventArgs e)
      {
         e.DrawBackground();
         const int LEFT_PADDING = 10;
         Rectangle bounds = e.Bounds;
         bounds.X += LEFT_PADDING;
         if (e.ColumnIndex >= 6)
            e.Graphics.DrawString(
               lvCalendar.Columns[e.ColumnIndex].Text, 
               e.Font, new SolidBrush(Color.Red), bounds);
         else
            e.DrawText();
      }

      private void FmCalendar_Load(object sender, EventArgs e)
      {
         DsBrigade dsBrigade = (DsBrigade)DataModule.Get(Brigade.OBJECT_NAME);

         if (dsBrigade != null)
         {
            List<Brigade> list = new List<Brigade>();
            list.AddRange(dsBrigade.Values);

            list.Sort(new Comparison<Brigade>(delegate(Brigade b1, Brigade b2) { return b1.Name.CompareTo(b2.Name); }));
            cbBrigade.Items.AddRange(list.ToArray());

            if (order.brigade != null && cbBrigade.Items.Count > 0)
               foreach(Brigade b in cbBrigade.Items)
                  if (b.id.Equals(order.brigade.id))
                  {
                     cbBrigade.SelectedItem = b;
                     break;
                  }
         }

         btnRefresh_Click(null, null);
         cbBrigade.SelectedIndexChanged += new System.EventHandler(this.cbBrigade_SelectedIndexChanged);
      }

      private void SaveOrder(TimeSubItem tsi, TimeSpan orderHours, Brigade brigade)
      {
         if (brigade != null)
         {
            List<IDataSet> list = new List<IDataSet>();
            DsOrderDel dsOrderDel = new DsOrderDel(false);

            if (!brigade.id.Equals(order.brigade.id))
            {
               OrderDel orderDel = new OrderDel();
               orderDel.created = order.created;
               orderDel.userid = order.brigade.id;
               dsOrderDel.Add(orderDel.created, orderDel);
               list.Add(dsOrderDel);
            }

            DsOrder dsOrder = new DsOrder(false);
            order.planbegin = tsi.time;
            order.planend = tsi.time + orderHours;
            order._params = 0;
            order.brigade = brigade;
            dsOrder.Add(order.created, order);

            list.Add(dsOrder);

            if (DataModule.UpdateDataSet(list, null, null, Config.GetConfig().GetConnection()))
               btnRefresh_Click(null, null);
            else
               MessageBox.Show("Ошибка записи в базу данных");
         }
      }

      private void btnWeekDec_Click(object sender, EventArgs e)
      {
         startOfWeek = startOfWeek.AddDays(-7);
         endOfWeek = endOfWeek.AddDays(-7);
         btnRefresh_Click(null, null);
      }

      private void btnWeekInc_Click(object sender, EventArgs e)
      {
         startOfWeek = startOfWeek.AddDays(7);
         endOfWeek = endOfWeek.AddDays(7);
         btnRefresh_Click(null, null);
      }

      private void lvCalendar_MouseUp(object sender, MouseEventArgs e)
      {
         ListViewHitTestInfo info = lvCalendar.HitTest(e.X, e.Y);
         Brigade brigade = cbBrigade.SelectedItem as Brigade;
         if (brigade != null && info != null)
         {
            ListViewItem.ListViewSubItem si = info.SubItem;
            if (si is TimeSubItem)
            {
               TimeSubItem tsi = (TimeSubItem)si;

               if (e.Button == MouseButtons.Left)
               {
                  if (tsi.order != null)
                  {
                     order = tsi.order;
                     lvCalendar.Refresh();
                  }
                  else if (tsi.order == null && !FmMain.DateTimeIsExpired(tsi.time) &&
                     MessageBox.Show(String.Format("Перенести заявку на {0}?", tsi.time),
                     "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
                  {
                     int orderHours = order.planend.Hour - order.planbegin.Hour;
                     TimeSpan worktime = order.planend - order.planbegin;
                     if (FmOrderEdit.IsPeriodGoodForOrder(brigade, order, tsi.time, tsi.time + worktime))
                     {
                        SaveOrder(tsi, worktime, brigade);
                     }
                     else if (MessageBox.Show("Нельзя уместить заявку в данный период времени, вписать заявку в данный период," +
                        " время выполнение будет скорректировано?",
                     "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
                     {
                        TimeSpan dec = new TimeSpan(0, FmMain.INTERVAL_IN_MIN, 0);

                        do
                        {
                           worktime -= dec;
                           if (FmOrderEdit.IsPeriodGoodForOrder(brigade,
                              order, tsi.time, tsi.time + worktime))
                           {
                              SaveOrder(tsi, worktime, brigade);
                              break;
                           }
                        } while (worktime.TotalMinutes > 0);
                     }
                  }
               }
            }
         }
      }

      private void cbBrigade_SelectedIndexChanged(object sender, EventArgs e)
      {
         btnRefresh_Click(null, null);
      }
   }
}

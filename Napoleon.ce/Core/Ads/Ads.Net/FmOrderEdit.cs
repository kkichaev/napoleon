using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.Ads
{
   public partial class FmOrderEdit : Form
   {
      private DateTime workdate = DateTime.MinValue;
      private Order order;
      private UserOrder userOrder;
      private bool autosubstitution;
      private bool initing = false;
      
      public FmOrderEdit()
      {
         InitializeComponent();

         tbBrigade.Text = Order.N_A;
         tbClient.Text = Order.N_A;
         ToolTip tooltip = new ToolTip();
         tooltip.SetToolTip(btnBrigade, "Выбрать бригаду");
         tooltip.SetToolTip(btnClient, "Выбрать клиента");
         const string CLEAR_STR = "Очистить";
         tooltip.SetToolTip(btnBrigadeClear, CLEAR_STR);
         tooltip.SetToolTip(btnClientClear, CLEAR_STR);
         dgvWorkType.AutoGenerateColumns = false;
      }

      private List<TimeDateTime> CreateTimeLine()
      {
         DateTime startTime = new DateTime(dtpDate.Value.Year, dtpDate.Value.Month, dtpDate.Value.Day, 8, 0, 0);
         DateTime endTime = startTime.AddHours(12);

         if (dtpDate.Value.Date == DateTime.Now.Date)
         {
            if (startTime < DateTime.Now)
               startTime = new DateTime(DateTime.Now.Year, DateTime.Now.Month, DateTime.Now.Day,
                  DateTime.Now.Minute < 40 ? DateTime.Now.Hour : DateTime.Now.Hour + 1,
                  DateTime.Now.Minute < 20 ? 20 : DateTime.Now.Minute < 40 ? 40 : 0, 0);
         }

         if (order != null && order.planbegin < startTime)
            startTime = order.planbegin;

         List<TimeDateTime> result = new List<TimeDateTime>();

         while (startTime < endTime)
         {
            result.Add(new TimeDateTime(startTime));
            startTime = startTime.AddMinutes(FmMain.INTERVAL_IN_MIN);
         }

         return result;
      }

      public static bool ShowInstance(DateTime workTimeBegin, EmptyInvoker postEditProcess)
      {
         return ShowInstance(null, null, workTimeBegin, postEditProcess, null, true);
      }

      public static bool ShowInstance(Order order, Brigade brigade, DateTime workTimeBegin)
      {
         return ShowInstance(order, brigade, workTimeBegin, null, null, false);
      }

      public static bool ShowInstance(Order order, Brigade brigade, DateTime workTimeBegin,
         EmptyInvoker postEditProcess)
      {
         return ShowInstance(order, brigade, workTimeBegin, postEditProcess, null, false);
      }

      public static bool ShowInstance(Order order, Brigade brigade, DateTime workTimeBegin,
         EmptyInvoker postEditProcess, UserOrder userOrder)
      {
         return ShowInstance(order, brigade, workTimeBegin, postEditProcess, userOrder, false);
      }

      public static bool ShowInstance(Order order, Brigade brigade, DateTime workTimeBegin, 
         EmptyInvoker postEditProcess, UserOrder userOrder, bool autosubstitution)
      {
         FmOrderEdit instance = new FmOrderEdit();
         bool addMode = order == null;

         instance.initing = true;

         if (Config.GetConfig().prefix.Length == 0)
         {
            MessageBox.Show("Задайте префикс для номера документа через настройки программы.",
               "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return false;
         }
         
         instance.gbReject.Visible = false;
         instance.autosubstitution = autosubstitution;

         if (userOrder != null)
         {
            StringBuilder sb = new StringBuilder();

            sb.Append(userOrder.Address);

            if (userOrder.Address.Trim().Length > 0 &&
               userOrder.remark.Trim().Length > 0)
               sb.Append("\r\n");

            sb.Append(userOrder.remark);
            instance.tbText.Text = sb.ToString();
            instance.userOrder = userOrder;
         }

         if (order != null)
         {
            instance.tbBrigade.Text = order.BrigadeName;
            instance.tbBrigade.Tag = order.brigade;
            instance.tbClient.Text = order.ClientName;
            instance.tbClient.Tag = order.client;
            instance.workdate = order.planbegin;
            instance.tbText.Text = order.Text;
            instance.tbAddress.Text = order.Address;

            if (order._params > 1)
               instance.btnOK.Enabled = false;

            instance.tbNumber.Text = order.number;

            if (order is OrderRcv && order.Rejected)
            {
               instance.gbReject.Visible = true;
               instance.tbReject.Text = ((OrderRcv)order).remark;
            }
         }

         Config config = Config.GetConfig();

         if (order == null && brigade != null)
         {
            instance.tbBrigade.Text = brigade.Name;
            instance.tbBrigade.Tag = brigade;
         }

         if (order == null && workTimeBegin != DateTime.MinValue)
            instance.workdate = workTimeBegin;

         if (instance.workdate == DateTime.MinValue)
         {
            instance.workdate = new DateTime(DateTime.Now.Year, DateTime.Now.Month, DateTime.Now.Day,
               DateTime.Now.Hour, DateTime.Now.Minute < 20 ? 20 : DateTime.Now.Minute < 40 ? 40 : 0, 0);

            if (DateTime.Now.Minute >= 40)
            {
               DateTime dt = instance.workdate;
               instance.workdate = dt.AddHours(1);
            }
         }
         
         instance.order = order;

         if (addMode)
            instance.Text = "Создать";
         else
            instance.Text = "Изменить";

         if (addMode)
         {
            instance.tbNumber.Text = String.Format("{0}{1,6:000000}", 
               config.prefix, config.orderNumber);
         }

         instance.dtpDate.Value = instance.workdate;

         if (instance.ShowDialog() == DialogResult.OK)
         {
            Order newOrder = order ?? new Order();
            newOrder.brigade = (Brigade)instance.tbBrigade.Tag;
            newOrder.client = (Client)instance.tbClient.Tag;
            newOrder.planbegin = ((TimeDateTime)instance.cbBegin.SelectedItem).DateTime;
            newOrder.planend = ((TimeDateTime)instance.cbEnd.SelectedItem).DateTime;
            newOrder.text = instance.tbText.Text;
            newOrder.address = instance.tbAddress.Text;
            newOrder._params = 0;
            newOrder.number = instance.tbNumber.Text;

            newOrder.wtypes = new List<OrderWorkType>();

            foreach (DataGridViewRow row in instance.dgvWorkType.Rows)
            {
               object vo = row.Cells[instance.dgvWorkTypeChecked.DisplayIndex].Value;
               WorkType wt = row.DataBoundItem as WorkType;
               if (wt != null && vo != null && vo == instance.dgvWorkTypeChecked.TrueValue)
               {
                  OrderWorkType owt = new OrderWorkType();
                  owt.item = wt;
                  newOrder.wtypes.Add(owt);
               }
            }

            DsOrder dsOrder = new DsOrder(false);
            dsOrder.Add(newOrder.created, newOrder);

            List<IDataSet> list = new List<IDataSet>();
            list.Add(dsOrder);

            bool result = false;

            result = DataModule.UpdateDataSet(list, null, null, Config.GetConfig().GetConnection());

            if (!result)
               MessageBox.Show("Ошибка записи в базу данных");

            if (result && postEditProcess != null)
               postEditProcess();

            if (result && addMode)
            {
               config.orderNumber++;
               config.Save();
            }

            return result;
         }

         return false;
      }

      private void btnBrigade_Click(object sender, EventArgs e)
      {
         FmBrigade.ShowInstance(new Invoker(delegate(object param) 
            {
               tbBrigade.Text = ((Brigade)param).Name;
               tbBrigade.Tag = param;
            }));
      }

      private void btnBrigadeClear_Click(object sender, EventArgs e)
      {
         tbBrigade.Text = Order.N_A;
         tbBrigade.Tag = null;
      }

      private void btnClient_Click(object sender, EventArgs e)
      {
         FmClientCreate.ShowInstance(new Invoker(delegate(object param)
            {
               tbClient.Tag = param;
               tbClient.Text = ((Client)param).Name;

               tbAddress.Text = ((Client)param).address;

               if (autosubstitution)
               {
                  tbAddress.Text = tbAddress.Text.Replace("Респ.", "");
                  Location clientLocation = FmBrigadeAddress.GetLocation(tbAddress.Text);

                  if (clientLocation != null)
                  {
                     List<Schedule> sc = FmMain.instance.GetTodaySchedule();
                     Brigade brigade = null;
                     double distance = 0.0;

                     foreach (Schedule c in sc)
                     {
                        if (c.status == (int)Schedule.Status.Active &&
                           c.latitude != 0 && c.longitude != 0)
                        {
                           double d = Coordutils.Distance(clientLocation.Latitude,
                              clientLocation.Longitude, c.latitude, c.longitude);

                           if (distance == 0 || d < distance)
                           {
                              distance = d;
                              brigade = c.brigade;
                           }
                        }
                     }

                     if (brigade != null)
                     {
                        tbBrigade.Text = brigade.Name;
                        tbBrigade.Tag = brigade;
                     }
                  }
               }
            }));
      }

      private void btnClientClear_Click(object sender, EventArgs e)
      {
         tbClient.Text = Order.N_A;
         tbClient.Tag = null;
      }

      private void setComboBoxValue(ComboBox comboBox, DateTime time)
      {
         foreach (object item in comboBox.Items)
         {
            if (((TimeDateTime)item).DateTime == time)
            {
               comboBox.SelectedItem = item;
               return;
            }
         }
      }

      private bool IsTimePeriodUnique()
      {
         bool result = false;

         if (cbBegin.SelectedItem != null || 
            cbEnd.SelectedItem != null)
         {
            DateTime dateBegin = ((TimeDateTime)cbBegin.SelectedItem).DateTime;
            DateTime dateEnd = ((TimeDateTime)cbEnd.SelectedItem).DateTime;

            if (dateEnd > dateBegin)
            {
               result =  tbBrigade.Tag != null ? IsPeriodGoodForOrder((Brigade)tbBrigade.Tag, 
                  this.order, dateBegin, dateEnd) : true;
            }
         }

         return result;
      }

      public static bool IsPeriodGoodForOrder(Brigade brigade, Order checkOrder, 
         DateTime dateBegin, DateTime dateEnd)
      {
         DsOrderRcv dsOrder = (DsOrderRcv)DataModule.Get(Order.OBJECT_NAME);

         if (!FmMain.DateTimeIsExpired(dateBegin) && dsOrder != null)
         {
            foreach (Order order in dsOrder.Data)
            {
               if (order.brigade == null ||
                  (brigade != null &&
                     order.brigade != null
                     && !brigade.id.Equals(order.brigade.id)))
                  continue;

               if ((checkOrder == null || checkOrder.created != order.created) &&
                     (((dateBegin >= order.planbegin && dateBegin < order.planend)
                     || (dateEnd > order.planbegin && dateEnd <= order.planend))
                     || ((order.planbegin >= dateBegin && order.planbegin < dateEnd)
                     || (order.planend > dateBegin && order.planend <= dateEnd))))
                  return false;
            }

            return true;
         }

         return false;
      }

      private void FmOrderEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            if (!IsTimePeriodUnique())
            {
               e.Cancel = true;
               MessageBox.Show("Ошибка при выборе времени.");
               return;
            }

            if (tbText.Text.Trim().Length == 0)
            {
               e.Cancel = true;
               tbText.Focus();
               MessageBox.Show("Введите текст заявки.");
               return;
            }
         }
         
         if (userOrder != null)
         {
            DsUserOrder dsUserOrder = new DsUserOrder(false);
            userOrder.SetAsRead();
            dsUserOrder.Add(1, userOrder);

            List<IDataSet> list = new List<IDataSet>();
            list.Add(dsUserOrder);
            DataModule.UpdateDataSet(list, null, null, Config.GetConfig().GetConnection(), userOrder.brigade.id);
         }
      }

      private void btnKladr_Click(object sender, EventArgs e)
      {
         FmKladr fmKladr = new FmKladr();

         if (fmKladr.ShowDialog() == DialogResult.OK)
            tbAddress.Text = fmKladr.Address;
      }

      private void FmOrderEdit_Load(object sender, EventArgs e)
      {
         FillTimeControls();

         DsWorkType dsWorkType = (DsWorkType)DataModule.Get(WorkType.OBJECT_NAME);

         if (dsWorkType != null)
         {
            List<WorkType> list = new List<WorkType>();
            list.AddRange(dsWorkType.Values);
            dgvWorkType.DataSource = list;

            if (order != null && order.wtypes != null)
            {
               foreach (OrderWorkType owt in order.wtypes)
               {
                  foreach (DataGridViewRow r in dgvWorkType.Rows)
                  {
                     WorkType wtt = r.DataBoundItem as WorkType;

                     if (wtt != null && wtt.Id.Equals(owt.item.Id))
                        r.Cells[dgvWorkTypeChecked.DisplayIndex].Value = dgvWorkTypeChecked.TrueValue;
                  }
               }
            }
         }

         btnMap.Enabled = autosubstitution;
      }

      private void FillTimeControls()
      {
         const int SKIP_TO_PERIOD = 1;
         int skipToPeriod = 1;
         cbBegin.Items.Clear();
         cbEnd.Items.Clear();

         foreach (TimeDateTime dt in CreateTimeLine())
         {
            cbBegin.Items.Add(dt);

            if (skipToPeriod > 0)
               skipToPeriod--;
            else
               cbEnd.Items.Add(dt);
         }

         DateTime last = ((TimeDateTime)cbEnd.Items[cbEnd.Items.Count - 1]).DateTime;

         for (int i = 0; i < 12; i++)
         {
            last = last.AddMinutes(FmMain.INTERVAL_IN_MIN);
            cbEnd.Items.Add(new TimeDateTime(last));
         }

         if (order == null)
         {
            if (workdate != DateTime.MinValue)
            {
               selectCurrentTime(cbBegin, 0);
               selectCurrentTime(cbEnd, SKIP_TO_PERIOD);
            }
            else
            {
               if (cbBegin.Items.Count > 0)
                  cbBegin.SelectedIndex = 0;

               if (cbEnd.Items.Count > 0)
                  cbEnd.SelectedIndex = 0;
            }
         }
         else
         {
            setComboBoxValue(cbBegin, order.planbegin);
            setComboBoxValue(cbEnd, order.planend);
         }
      }

      private void selectCurrentTime(ComboBox comboBox, int hourAdd)
      {
         bool setted = false;
         object last = null;

         DateTime dt = dtpDate.Value.Date.AddHours(hourAdd).AddHours(workdate.Hour).AddMinutes(workdate.Minute);
         foreach (object timeObject in comboBox.Items)
         {
            last = timeObject;
            if (((TimeDateTime)timeObject).DateTime == dt)
            {
               setted = true;
               comboBox.SelectedItem = timeObject;
               break;
            }
         }

         // если начинаем в 19-30, то закончить надо в 20-00
         if (!setted)
            comboBox.SelectedItem = last;
      }

      private void btnMap_Click(object sender, EventArgs e)
      {
         Client client = tbClient.Tag as Client;

         if (client != null)
            new FmOrderMap(client, workdate).Show();
      }

      private void dgvWorkType_CellContentClick(object sender, DataGridViewCellEventArgs e)
      {
         DataGridViewCheckBoxCell cell = dgvWorkType.CurrentCell as DataGridViewCheckBoxCell;

         if (cell != null)
         {
            dgvWorkType.CommitEdit(DataGridViewDataErrorContexts.Commit);
            DataGridViewRow row = dgvWorkType.CurrentRow;

            if (row != null)
            {
               WorkType wt = row.DataBoundItem as WorkType;

               if (wt != null)
               {
                  if (cell.Value == cell.TrueValue)
                  {
                     tbText.Text += wt.Name + "\r\n";
                  }
                  else
                  {
                     string str = tbText.Text;

                     int i = str.IndexOf(wt.Name);

                     if (i != -1)
                        tbText.Text = tbText.Text.Remove(i, wt.Name.Length + 2);
                  }
               }
            }
         }
      }

      private void dtpDate_ValueChanged(object sender, EventArgs e)
      {
         if (initing)
         {
            FillTimeControls();
         }
      }
   }

   internal class TimeDateTime
   {
      private DateTime dateTime;

      public TimeDateTime(DateTime dateTime)
      {
         this.dateTime = dateTime;
      }

      public DateTime DateTime { get { return dateTime; } }

      public override string ToString()
      {
         return dateTime.ToString("HH:mm");
      }
   }

}

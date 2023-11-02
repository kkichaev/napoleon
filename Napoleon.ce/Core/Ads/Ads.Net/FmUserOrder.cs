using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.Ads.Utils;
using System.Collections;

namespace GRSoft.Ads
{
   public partial class FmUserOrder : Form
   {
      private static FmUserOrder instance;
      private DsBrigade dsBrigade;
      private DataWarningTooltip dateWarningTooltip;
      private SearchEngine searchEngine;
      private DsWarehouse dsWarehouse;
      private IDataSet dsUserOrder;

      public FmUserOrder()
      {
         InitializeComponent();
         dsBrigade = (DsBrigade)DataModule.Get(Brigade.OBJECT_NAME) ?? new DsBrigade(true);
         dsUserOrder = createDsUserOrder();
         dsWarehouse = (DsWarehouse)DataModule.Get(Warehouse.OBJECT_NAME) ?? new DsWarehouse(true);

         dgvOrder.AutoGenerateColumns = false;
         dateWarningTooltip = new DataWarningTooltip(this);

         searchEngine = new SearchEngine(new FindDataGridObject(dgvOrder, 0));
      }

      protected virtual IDataSet createDsUserOrder()
      {
         return (DsUserOrder)DataModule.Get(UserOrder.OBJECT_NAME) ?? new DsUserOrder(true);
      }

      private void FmOrder_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
         if (dateWarningTooltip.Visible)
            dateWarningTooltip.Hide();
      }

      public static void ShowInstance()
      {
         if (instance == null)
         {
            instance = FormEnties.CreateUserOrder();
            instance.Show();
         }
         else
            instance.Activate();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         if (FmOrderEdit.ShowInstance(null, null, DateTime.MinValue))
            btnRefresh_Click(null, null);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();

         DateTime begin = new DateTime(dtpBegin.Value.Year, dtpBegin.Value.Month, dtpBegin.Value.Day);
         DateTime end = dtpEnd.Enabled ? dtpEnd.Value : begin.AddDays(1);
         dsUserOrder.Filter = string.Format("date >= ToDate('{0}') and date < ToDate('{1}') {2}",
            begin, end, cbUnread.Checked ? " and (unread=0 or unread is null)" : string.Empty);

         fillUpdateList(list);

         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed,
            DataModule_OnDataResponceError);
         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               list, FmWait.ProgressIndicator));
      }

      protected virtual void fillUpdateList(List<IDataSet> list)
      {
         list.Add(dsWarehouse);
         list.Add(dsBrigade);
         list.Add(dsUserOrder);
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         FmWait.CloseForm();
         DataModule.ClearEvents();

         Invoke(new InvokeDelegate(delegate()
         {
            RefreshData();
            FillFilterComboBox();
            DataUtils.GridSort<UserOrder>(dgvOrder, 0, gridComparer);
            dgvOrder_SelectionChanged(null, null);
         }));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
      }

      protected virtual IList createDataSource()
      {
         return new List<UserOrder>();
      }

      void RefreshData()
      {
         IList list = createDataSource();

         Brigade filter = cbBrigade.SelectedItem is Brigade ? (Brigade)cbBrigade.SelectedItem
            : null;

         foreach (UserOrder o in dsUserOrder.Data)
         {
            if ((filter == null || (filter != null && 
                  o.brigade != null &&
                  filter.id.Equals(o.brigade.id))))
               list.Add(o);
         }

         dgvOrder.DataSource = list;
      }

      void FillFilterComboBox()
      {
         cbBrigade.Items.Clear();
         cbBrigade.Items.Add("Все");

         List<Brigade> brigadeList = new List<Brigade>();
         foreach (Brigade brigade in dsBrigade.Data)
            brigadeList.Add(brigade);

         brigadeList.Sort(new Comparison<Brigade>(delegate(Brigade b1, Brigade b2)
         {
            return b1.Name.CompareTo(b2.Name);
         }));

         cbBrigade.Items.AddRange(brigadeList.ToArray());
         cbBrigade.SelectedIndex = 0;
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvOrder.CurrentRow;

         if (row != null)
         {
            Order order = (Order)row.DataBoundItem;

            if (FmOrderEdit.ShowInstance(order, null, DateTime.MinValue))
               dgvOrder.Refresh();
         }

      }

      private void FmOrder_Load(object sender, EventArgs e)
      {
         if (dsUserOrder.Data.Count == 0)
         {
            dtpBegin.Value = new DateTime(DateTime.Now.Year, DateTime.Now.Month, DateTime.Now.Day);
            dtpEnd.Value = dtpBegin.Value.AddDays(1);
            dtpEnd.Enabled = false;
            btnRefresh_Click(null, null);
         }
         else
         {
            dtpBegin.Value = GetMinTime();
            dtpEnd.Value = DateTime.Now;
            miRange_Click(null, null); 
            RefreshData();
            FillFilterComboBox();
         }
      }

      private DateTime GetMinTime()
      {
         List<UserOrder> list = new List<UserOrder>();
         foreach (UserOrder o in dsUserOrder.Data)
            list.Add(o);

         list.Sort(new Comparison<UserOrder>(delegate(UserOrder u1, UserOrder u2) { return u1.created.CompareTo(u2.Created); }));

         if (list.Count > 0)
            return list[0].created;
         else
            return DateTime.MinValue;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         
         DataGridViewRow row = dgvOrder.CurrentRow;

         if (row != null)
         {
            Order del = (Order)row.DataBoundItem;
            
            if (MessageBox.Show("Запись будет удалена. Удалить?",
               "Вопрос", MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
            {
               DsOrder dsToDel = new DsOrder(false);
               dsToDel.Add(del.created, del);
               List<IDataSet> delSet = new List<IDataSet>();
               delSet.Add(dsToDel);

               DsOrderDel dsOrderDel = new DsOrderDel(false);
               OrderDel orderDel = new OrderDel();
               orderDel.created = del.created;
               dsOrderDel.Add(orderDel.created, orderDel);
               List<IDataSet> updSet = new List<IDataSet>();
               updSet.Add(dsOrderDel);

               if (DataModule.UpdateDataSet(null, delSet, null, Config.GetConfig().GetConnection()))
               {
                  btnRefresh_Click(null, null);
               }
               else MessageBox.Show("Ошибка при удалении записи");
            }
         }
      }

      //Настройка кнопок для выбора периода 
      private void AdjustRangeButton(bool isToday, string toolTipText)
      {
         btnRange.Image = isToday ? miToday.Image : miRange.Image;
         miToday.Checked = isToday;
         miRange.Checked = !isToday;
         btnRange.ToolTipText = toolTipText;
         dtpEnd.Enabled = !isToday;
         CheckDateValid();
      }

      private void btnRange_ButtonClick(object sender, EventArgs e)
      {
         if (miToday.Checked)
         {
            miRange_Click(null, null); 
         }
         else
         {
            miToday_Click(null, null);
         }
      }

      private void miToday_Click(object sender, EventArgs e)
      {
         AdjustRangeButton(true, "За сегодня");
      }

      private void miRange_Click(object sender, EventArgs e)
      {
         AdjustRangeButton(false, "За период");
      }

      //форма всплывающего окна "предупреждение" о неправильном выборе даты
      class DataWarningTooltip : Form
      {
         private Label label = new Label();
         private LinkLabel lbDateChange = new LinkLabel();
         private FmUserOrder fmOrder;

         public DataWarningTooltip(FmUserOrder fmOrder)
         {
            this.fmOrder = fmOrder;

            StartPosition = FormStartPosition.Manual;
            TopMost = true;
            BackColor = Color.Lime;
            FormBorderStyle = FormBorderStyle.None;
            ShowInTaskbar = false;
            Size = new Size(250, 30);

            label.SetBounds(2, 2, 250, 15);
            label.Text = "Дата окончания выборки меньше даты начала";
            this.Controls.Add(label);

            lbDateChange.SetBounds(100, 17, 250, 15);
            lbDateChange.Text = "поменять";
            lbDateChange.Click += OnChangeLabel_Click;
            this.Controls.Add(lbDateChange);
         }

         public void Show(Point point)
         {
            Location = point;
            Show();
         }

         private void OnChangeLabel_Click(object sender, EventArgs e)
         {
            DateTime dtTemp = fmOrder.dtpBegin.Value;
            fmOrder.dtpBegin.Value = fmOrder.dtpEnd.Value;
            fmOrder.dtpEnd.Value = dtTemp;
         }
      }

      //Проверка на правильность установки диапазона дат выборки
      private void CheckDateValid()
      {
         if (!Visible)
         {
            return;
         }

         if (miRange.Checked && dtpBegin.Value.Date > dtpEnd.Value.Date)
         {
            dateWarningTooltip.Show(new Point(Location.X + dtpEnd.Location.X,
                 Location.Y + dtpEnd.Location.Y - 5));
         }
         else
         {
            dateWarningTooltip.Hide();
         }
      }

      private void FmOrder_Move(object sender, EventArgs e)
      {
         if (dateWarningTooltip.Visible)
         {
            dateWarningTooltip.Location = new Point(Location.X + dtpEnd.Location.X,
                 Location.Y + dtpEnd.Location.Y - 5);
         }
      }

      private void dtpBegin_LocationChanged(object sender, EventArgs e)
      {
         CheckDateValid();
      }

      private void dtpEnd_ValueChanged(object sender, EventArgs e)
      {
         CheckDateValid();
      }

      private void cbBrigade_SelectionChangeCommitted(object sender, EventArgs e)
      {
         RefreshData();
      }

      private void dgvOrder_ColumnHeaderMouseClick(object sender, DataGridViewCellMouseEventArgs e)
      {
         searchEngine = new SearchEngine(new FindDataGridObject(dgvOrder, e.ColumnIndex));
         DataUtils.GridSort<UserOrder>(dgvOrder, e.ColumnIndex, gridComparer);
      }

      private OrderGridComparer gridComparer = new OrderGridComparer();

      class OrderGridComparer : GridBoundedObjectComparer
      {
      }

      private void RefreshWithSortOrder()
      {
         int columnIndex = 0;

         foreach (DataGridViewColumn column in dgvOrder.Columns)
         {
            if (column.HeaderCell.SortGlyphDirection != SortOrder.None)
            {
               columnIndex = column.Index;
            }
         }

         RefreshData();

         DataUtils.GridSort<UserOrder>(dgvOrder, columnIndex, gridComparer);
      }

      private void dgvOrder_SelectionChanged(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvOrder.CurrentRow;
         List<OrderItem> list = new List<OrderItem>();
         tbRemark.Text = string.Empty;

         if (row != null)
         {
            UserOrder order = (UserOrder)row.DataBoundItem;

            if (order != null && order.items != null)
            {
               foreach (OrderItem item in order.items)
                  list.Add(item);
            }

            tbRemark.Text = order.remark;
         }

         dgvOrderItems.DataSource = list;
      }

      private void dgvOrder_MouseDoubleClick(object sender, MouseEventArgs e)
      {
         DataGridViewRow row = dgvOrder.CurrentRow;

         if (row != null)
         {
            UserOrder userOrder = row.DataBoundItem as UserOrder;

            if (userOrder != null)
            {
               FmOrderEdit.ShowInstance(null, userOrder.brigade, DateTime.MinValue, null, userOrder);
            }
         }
      }

      private void dgvOrder_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         DataGridViewRow row = (sender as DataGridView).Rows[e.RowIndex];

         if (row != null)
         {
            UserOrder userOrder = row.DataBoundItem as UserOrder;

            if (userOrder != null)
            {
               e.CellStyle.BackColor = userOrder.IsMarkAsRead() ? Color.White : Color.LightBlue;
            }
         }
      }

      private UserOrder GetSelectedUserOrder()
      {
         UserOrder result = null;

         DataGridViewRow row = dgvOrder.CurrentRow;

         if (row != null)
            result = row.DataBoundItem as UserOrder;

         return result;
      }

      private void miMarkAsread_Click(object sender, EventArgs e)
      {
         UserOrder userOrder = GetSelectedUserOrder();

         if (userOrder != null)
         {
            userOrder.SetAsRead();
            DsUserOrder ds = new DsUserOrder(false);
            List<IDataSet> list = new List<IDataSet>();
            ds.Add(1, userOrder);
            list.Add(ds);
            bool r = DataModule.UpdateDataSet(list, null, null, 
               Config.GetConfig().GetConnection(), userOrder.brigade.id);
            System.Console.Out.WriteLine(tbRemark.ToString());
            dgvOrder.Refresh();
         }
      }

      private void dgvOrder_MouseDown(object sender, MouseEventArgs e)
      {
         DataGridView.HitTestInfo info = ((DataGridView)sender).HitTest(e.X, e.Y);

         if (info != null && e.Button == MouseButtons.Right)
         {
            ((DataGridView)sender).CurrentCell =
               ((DataGridView)sender).Rows[info.RowIndex].Cells[info.ColumnIndex];
         }
      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         new FmUserOrderPrepareReport((IList)dgvOrder.DataSource).Show();
      }
   }
   
}

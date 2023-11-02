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
   public partial class FmOrder : Form
   {
      private static FmOrder instance;
      private DsBrigade dsBrigade;
      private DsClient dsClient;
      protected IDataSet dsOrder;
      private DataWarningTooltip dateWarningTooltip;
      private SearchEngine searchEngine;
      private DsWarehouse dsWarehouse;
      private GridBoundedObjectComparer gridComparer;

      public FmOrder()
      {
         InitializeComponent();
         gridComparer = CreateComparer();
         dsBrigade = (DsBrigade)DataModule.Get(Brigade.OBJECT_NAME) ?? new DsBrigade(true);
         dsClient = (DsClient)DataModule.Get(Client.OBJECT_NAME) ?? new DsClient(true);
         dsOrder = CreateDsOrder();
         dsWarehouse = (DsWarehouse)DataModule.Get(Warehouse.OBJECT_NAME) ?? new DsWarehouse(true);

         dgvOrder.AutoGenerateColumns = false;
         dateWarningTooltip = new DataWarningTooltip(this);

         searchEngine = new SearchEngine(new FindDataGridObject(dgvOrder, 0));
      }

      virtual protected IDataSet CreateDsOrder()
      {
         return (DsOrderRcv)DataModule.Get(Order.OBJECT_NAME) ?? new DsOrderRcv(true);
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
            instance = FormEnties.CreateOrderForm();
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
         dsOrder.Filter = string.Format("planbegin >= ToDate('{0}') and planbegin < ToDate('{1}')",
            begin, end);

         list.Add(dsWarehouse);
         list.Add(dsBrigade);
         list.Add(dsClient);
         list.Add(dsOrder);

         DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed,
            DataModule_OnDataResponceError);
         FmWait.ShowForm(this, 
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               list, FmWait.ProgressIndicator));
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new InvokeDelegate(delegate()
         {
            RefreshData();
            FillFilterComboBox();
            GridSort(0, gridComparer);
            dgvOrder_SelectionChanged(null, null);
         }));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
      }

      virtual protected IList createDataSource()
      {
         return new List<OrderRcv>();
      }

      private void RefreshData()
      {
         IList list = createDataSource(); 

         Brigade filter = cbBrigade.SelectedItem is Brigade ? (Brigade)cbBrigade.SelectedItem
            : null;

         foreach (OrderRcv o in dsOrder.Data)
         {
            if ((filter == null || (filter != null && 
               o.brigade != null &&
               filter.id.Equals(o.brigade.id))) && (miAll.Checked ||
               ((o.client == null) || o.Rejected)))
            {
               list.Add(o);
            }
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
         dtpBegin.Value = new DateTime(DateTime.Now.Year, DateTime.Now.Month, DateTime.Now.Day);
         dtpEnd.Value = dtpBegin.Value.AddDays(1);
         dtpEnd.Enabled = false;
         btnRefresh_Click(null, null);
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
         private FmOrder fmOrder;

         public DataWarningTooltip(FmOrder fmOrder)
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
         GridSort(e.ColumnIndex, gridComparer);
      }

      protected virtual GridBoundedObjectComparer CreateComparer()
      {
         return new OrderGridComparer();
      }

      class OrderGridComparer : GridBoundedObjectComparer
      {
      }

      private void btnSearchBack_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.UP);
      }

      private void btnSearchForward_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.DOWN);
      }

      private void tbFind_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            searchEngine.find(tbFind.Text, Direction.DOWN);
      }

      private void btnComplete_ButtonClick(object sender, EventArgs e)
      {
         if (miAll.Checked)
         {
            miNA.Checked = true;
            miAll.Checked = false;
            btnComplete.Image = miNA.Image;
         }
         else
         {
            miAll.Checked = true;
            miNA.Checked = false;
            btnComplete.Image = miAll.Image;
         }

         RefreshWithSortOrder();
      }

      private void miAll_Click(object sender, EventArgs e)
      {
         miAll.Checked = true;
         miNA.Checked = false;
         btnComplete.Image = miAll.Image;
         RefreshWithSortOrder();
      }

      private void miNA_Click(object sender, EventArgs e)
      {
         miNA.Checked = true;
         miAll.Checked = false;
         btnComplete.Image = miNA.Image;
         RefreshWithSortOrder();
      }

      private void RefreshWithSortOrder()
      {
         int columnIndex = 0;

         foreach (DataGridViewColumn column in dgvOrder.Columns)
         {
            if (column.HeaderCell.SortGlyphDirection != SortOrder.None)
               columnIndex = column.Index;
         }

         RefreshData();
         GridSort(columnIndex, gridComparer);
      }

      protected virtual void GridSort(int index, GridBoundedObjectComparer cmp)
      {
         DataUtils.GridSort<OrderRcv>(dgvOrder, index, cmp);
      }

      private void dgvOrder_SelectionChanged(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvOrder.CurrentRow;
         List<OrderItem> list = new List<OrderItem>();
         tbReport.Text = "";

         if (row != null)
         {
            OrderRcv order = (OrderRcv)row.DataBoundItem;

            if (order != null && order.items != null)
            {
               foreach (OrderItem item in order.items)
                  list.Add(item);

               tbReport.Text = order.remark;
            }
         }

         dgvOrderItems.DataSource = list;
      }

      private void dgvOrder_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         OrderRcv order = (OrderRcv)((DataGridView)sender).Rows[e.RowIndex].DataBoundItem;

         if (order.Rejected || order.client == null)
            e.CellStyle.ForeColor = Color.Green;

         DataGridView grid = (DataGridView)sender;
         if (grid.Columns[e.ColumnIndex].DataPropertyName.Equals("Text"))
         {
            string str = grid.Rows[e.RowIndex].Cells[e.ColumnIndex].Value.ToString();
            str = str.Replace("\r\n", " ");
            e.Value = str;
         }

      }

      private void btnReport_Click(object sender, EventArgs e)
      {
         List<OrderRcv> list = new List<OrderRcv>();

         foreach (OrderRcv o in (ICollection)dgvOrder.DataSource)
            list.Add(o);

         new  FmOrderPrepareReport(list).Show();
      }
   }
   
}

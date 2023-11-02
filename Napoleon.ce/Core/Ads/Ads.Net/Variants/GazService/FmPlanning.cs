using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using GRSoft.Ads.Utils;
using System.Collections;

namespace GRSoft.Ads
{
   public partial class FmPlanning : Form
   {
      private DsDistrict dsDistrict;
      private DsSchedule dsSchedule;
      private DsBrigade dsBrigade;
      private DsOrderRcv dsOrder;
      private DataGridView dataGrid;
      private SearchEngine searchEngine;

      public FmPlanning(DateTime date)
      {
         InitializeComponent();
         dsDistrict = (DsDistrict)DataModule.Get(District.OBJECT_NAME) ?? new DsDistrict(true);
         dsSchedule = (DsSchedule)DataModule.Get(Schedule.OBJECT_NAME) ?? new DsSchedule(true);
         dsBrigade = (DsBrigade)DataModule.Get(Brigade.OBJECT_NAME) ?? new DsBrigade(true);
         dsOrder = (DsOrderRcv)DataModule.Get(OrderRcv.OBJECT_NAME) ?? new DsOrderRcv(true);

         datePickerCtrl1.Date = date;
         datePickerCtrl1.OnDayChanged += new DatePickerCtrl.DayChangeHandler(datePickerCtrl1_OnDayChanged);

         dataGrid = new DataGridView();
         dataGrid.Name = "dataGrid";
         dataGrid.Dock = DockStyle.Fill;
         dataGrid.CellPainting += new DataGridViewCellPaintingEventHandler(dataGrid_CellPainting);
         dataGrid.AllowUserToAddRows = false;
         dataGrid.AllowUserToDeleteRows = false;
         dataGrid.DefaultCellStyle.WrapMode = DataGridViewTriState.True;
         dataGrid.MultiSelect = false;
         dataGrid.MouseUp += new MouseEventHandler(dataGrid_MouseUp);
         dataGrid.ReadOnly = true;

         searchEngine = new SearchEngine(new FindDataGridObjectEx(dataGrid, 0));
      }

      void dataGrid_MouseUp(object sender, MouseEventArgs e)
      {
         new Thread(new ThreadStart(delegate()
            {
               Invoke(new EmptyInvoker(delegate()
                  {
                     DataGridView.HitTestInfo info = dataGrid.HitTest(e.X, e.Y);

                     if (info.RowIndex >= 0 && info.ColumnIndex >= 0)
                     {
                        DateTime date = (DateTime)dataGrid.Rows[info.RowIndex].Tag;

                        if (date != null)
                        {
                           Brigade b = (Brigade)dataGrid.Columns[info.ColumnIndex].Tag;

                           if (date > DateTime.Now)
                              FmOrderEdit.ShowInstance(
                                 dataGrid[info.ColumnIndex, info.RowIndex].Tag as Order, b, date,
                                 new EmptyInvoker(delegate() { btnRefresh_Click(btnRefresh, EventArgs.Empty); }));
                        }
                     }
                  }));
            })).Start();
      }

      void dataGrid_CellPainting(object sender, DataGridViewCellPaintingEventArgs e)
      {
         if (e.ColumnIndex < 0 && e.RowIndex >= 0)
         {
            e.PaintBackground(e.ClipBounds, false);
            object tag = dataGrid.Rows[e.RowIndex].Tag;

            if (tag != null && tag is DateTime)
            {

               DateTime date = (DateTime)tag;
               String str = date.ToString("HH:mm");
               Size size = TextRenderer.MeasureText(str, dataGrid.Font);
               Rectangle r = new Rectangle(e.CellBounds.Width / 2 - size.Width / 2,
                  e.CellBounds.Y + (e.CellBounds.Height / 2 - size.Height / 2), size.Width, size.Height);
               e.Graphics.DrawString(str, dataGrid.Font, Brushes.Red, r);
            }
            e.Handled = true;
         }
      }

      void datePickerCtrl1_OnDayChanged(DateTime date)
      {
         btnRefresh_Click(btnRefresh, EventArgs.Empty);
      }

      private void FmPlanning_Load(object sender, EventArgs e)
      {
         foreach (District d in dsDistrict.Data)
         {
            TabPage p = new TabPage(d.Name);
            p.Tag = d;
            tabControl.TabPages.Add(p);
         }

         if (tabControl.TabPages.Count > 0)
         {
            tabControl_Selected(tabControl, new TabControlEventArgs(
               tabControl.TabPages[0], 0, TabControlAction.Selected));
         }
      }

      private void RefreshData()
      {
         tabControl.SelectedTab.SuspendLayout();
         District district = (District)tabControl.SelectedTab.Tag;
         
         List<object> brigadeList = new List<object>();
         ListSchedule listSchedule = new ListSchedule(delegate(Brigade brigade)
         {
            brigadeList.Add(brigade);
         });

         listSchedule.DoList(dsSchedule, dsBrigade, district);

         Invoke(new InvokeDelegate(delegate()
         {

            DateTime dateTimeStart = new DateTime(datePickerCtrl1.Date.Year,
               datePickerCtrl1.Date.Month, datePickerCtrl1.Date.Day, 8, 0, 0);
            DateTime dateTimeEnd = new DateTime(datePickerCtrl1.Date.Year,
               datePickerCtrl1.Date.Month, datePickerCtrl1.Date.Day, 20, 0, 0);

            dataGrid.Columns.Clear();
            dataGrid.Rows.Clear();

            foreach (Brigade b in brigadeList)
            {
               DataGridViewTextBoxColumn clmn = new DataGridViewTextBoxColumn();
               clmn.HeaderText = b.Name;
               clmn.Tag = b;
               clmn.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
               dataGrid.Columns.Add(clmn);
            }

            if (dataGrid.Columns.Count > 0)
               for (; dateTimeStart <= dateTimeEnd; )
               {
                  DataGridViewRow row = new DataGridViewRow();
                  dataGrid.Rows.Add(row);
                  row.Tag = new DateTime(dateTimeStart.Ticks);
                  dateTimeStart = dateTimeStart.AddMinutes(20);
               }

            foreach (OrderRcv order in dsOrder.Data)
            {
               int pos = FindBrigadeInList(order.brigade, brigadeList);

               if (pos != -1)
               {
                  int hour = order.planbegin.Hour;
                  int minutes = order.planbegin.Minute;

                  int r = ((hour - 8) * 60 + minutes) / FmMain.INTERVAL_IN_MIN;
                  string info = order.ShortInfo;
                  DataGridViewRow row = dataGrid.Rows[r];
                  row.Height = TextRenderer.MeasureText(info, dataGrid.Font).Height;
                  DataGridViewCell cell = row.Cells[pos];
                  cell.Value = info;
                  cell.Tag = order;

                  int range = (int)(order.planend - order.planbegin).TotalMinutes / FmMain.INTERVAL_IN_MIN;

                  if (range > 1)
                  {
                     for (; range > 1; range--)
                     {
                        int ri = r + range - 1;

                        if (ri >= dataGrid.Rows.Count)
                           break;

                        DataGridViewRow row2 = dataGrid.Rows[ri];
                        row2.Height = row.Height;
                        DataGridViewCell cell2 = row2.Cells[pos];
                        cell2.Value = info;
                        cell2.Tag = order;
                     }

                  }
               }
            }
            
            tabControl.SelectedTab.ResumeLayout();
         }));
         
      }

      private void tabControl_Selected(object sender, TabControlEventArgs e)
      {
         if (e.TabPage != null)
         {
            dataGrid.DataSource = null;
            RefreshData();
            e.TabPage.Controls.Add(dataGrid);
         }
      }

      private int FindBrigadeInList(Brigade brigade, List<object> brigadeList)
      {
         return brigadeList.IndexOf(brigade);
      }

      private void ControlRefreshStatus(bool enable)
      {
         btnRefresh.Enabled = enable;
         datePickerCtrl1.Enabled = enable;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         if (tabControl.SelectedTab != null)
         {
            ControlRefreshStatus(false);
            List<IDataSet> list = new List<IDataSet>();
            list.Add(dsOrder);

            DateTime begin = datePickerCtrl1.Date;
            DateTime end = begin.AddDays(1);
            DBConnection dbc = Config.GetConfig().GetConnection();

            dsOrder.Filter = string.Format("planbegin >= ToDate('{0:dd/MM/yyyy}') and planbegin < ToDate('{1:dd/MM/yyyy}')",
               begin, end);
            dsSchedule.Filter = string.Format("date = ToDate('{0:dd/MM/yyyy}')", begin);

            DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed, DataModule_OnDataResponceError);

            FmWait.ShowForm(this,
               DataModule.RefreshGiveSets(dbc, list, FmWait.ProgressIndicator));
         }
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         Invoke(new InvokeDelegate(delegate()
         {
            ControlRefreshStatus(true);
            RefreshData();
         }));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
         Invoke(new InvokeDelegate(delegate()
         {
            ControlRefreshStatus(true);
         }));
      }

      private void btnFindNext_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.DOWN);
      }

      private void btnFindPrev_Click(object sender, EventArgs e)
      {
         searchEngine.find(tbFind.Text, Direction.UP);
      }

      private void tbFind_KeyDown(object sender, KeyEventArgs e)
      {
         if (e.KeyCode == Keys.Enter)
            searchEngine.find(tbFind.Text, Direction.DOWN);
      }
   }

   class FindDataGridObjectEx : FindDataGridObject
   {
      public FindDataGridObjectEx(DataGridView dgv, int columnIndex)
         :base(dgv, columnIndex)
      {
      }

      public override IEnumerator GetEnumerator()
      {
         int start = pos;

         while (true)
         {
            DataGridViewRow row = Next();

            for (int c = 0; c < row.Cells.Count; c++)
            {
               object celVal = row.Cells[c].Value;

               if (celVal != null)
               {
                  string val = celVal.ToString().ToUpper();

                  if (val.Contains(text.ToUpper()))
                     yield return row.Cells[c];
               }

               if (pos == start)
                  yield break;
            }
         }
      }

      public override void Select(IEnumerator iter)
      {
         if (iter != null)
         {
            dgv.CurrentCell = (DataGridViewCell)iter.Current;
            dgv.CurrentCell.Selected = true;
         }
      }
   }
}
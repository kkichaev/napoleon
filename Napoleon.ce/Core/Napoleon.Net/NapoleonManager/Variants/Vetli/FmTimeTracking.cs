using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmTimeTracking : Form
   {
      List<Agent> agents = new List<Agent>();
      BindingList<TimeTracking.Item> data = new BindingList<TimeTracking.Item>();
      SimpleDataSet<TimeTracking> dsTT = new SimpleDataSet<TimeTracking>(TimeTracking.OBJECT_NAME, false);
      MaskedTextBox maskText;

      private const string TIME_START = "00:00";
      private const string TIME_FINISH = "23:59";

      public FmTimeTracking()
      {
         InitializeComponent();
         grid.DataSource = data;
         btnAdd.Enabled = false;
         maskText = new MaskedTextBox();
         maskText.Mask = "00:00";
         maskText.Visible = false;
         grid.Controls.Add(maskText);
      }

      private void FmGSMEdit_Load(object sender, EventArgs e)
      {
         Manager mc = CurrentUser.user as Manager;
         agents.Clear();

         if (mc != null)
         {
            foreach (Division.DivisionAgent da in mc.Division.GetAllAgents())
            {
               if (da.agent == null)
                  continue;
               agents.Add(da.agent);
            }

            agents.Sort((x, y) => { return x.Name.CompareTo(y.Name); });
         }

         dateTimePicker.ValueChanged -= dateTimePicker_ValueChanged;
         dateTimePicker.Value = DateTime.Now;
         dateTimePicker.ValueChanged += dateTimePicker_ValueChanged;
         btnRefresh.PerformClick();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         var dlg = new FmCalendar();
         dlg.Date = dateTimePicker.Value.Date;

         if (dlg.ShowDialog() == DialogResult.OK) 
         {
            bool has = false;

            foreach(TimeTracking.Item i in data)
               if (i.Date == dlg.Date)
               {
                  has = true;
                  break;
               }

            if (!has)
            {
               foreach (Agent a in agents)
               {
                  TimeTracking.Item tti = new TimeTracking.Item();
                  tti.Date = dlg.Date;
                  tti.Start = TIME_START;
                  tti.Finish = TIME_FINISH;
                  tti.Agent = a;
                  data.Add(tti);
               }

               SortGridItems();

               btnSave.Enabled = true;
            }
         }
      }

      private void SortGridItems()
      {
         List<TimeTracking.Item> list = new List<TimeTracking.Item>();
         
         foreach (TimeTracking.Item i in data)
            list.Add(i);

         list.Sort(SortFunc);
         data.Clear();

         foreach (TimeTracking.Item i in list)
            data.Add(i);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         DateTime dt = dateTimePicker.Value.Date;
         dsTT.Filter = string.Format("month={0} and year={1}", dt.Month, dt.Year);
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsTT);
         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         btnSave.Enabled = false;
         btnAdd.Enabled = true;
         data.Clear();

         if (dsTT.Count > 0)
         {
            List<TimeTracking.Item> list = new List<TimeTracking.Item>();
            TimeTracking tt = dsTT[0];
            DateTime dt = new DateTime(tt.year, tt.month, 1);
            dateTimePicker.Value = dt;
            tbCost.Text = tt.cost.ToString();

            foreach (TimeTracking.Item i in tt.items)
               list.Add(i);

            list.Sort(SortFunc);

            foreach (TimeTracking.Item i in list)
               data.Add(i);
         }

         btnSave.Enabled = false;
      }

      private int SortFunc(TimeTracking.Item x, TimeTracking.Item y)
      {
         int r = x.Date.CompareTo(y.Date) * -1;
         if (r == 0 && x.Agent != null && y.Agent != null)
            r = x.Agent.Name.CompareTo(y.Agent.Name);
         return r;
      }

      private int CheckValidData()
      {
         int result = -1;

         for (int i = 0; i < data.Count; i++ )
         {
            if (CheckTrackingItem(data[i]) != null)
            {
               result = i;
               break;
            }
         }

         return result;
      }

      private TimeTracking.Item CheckTrackingItem(TimeTracking.Item item)
      {
         TimeTracking.Item result = null;
         const int TIME_LENGTH = 5;
         

         if(item.Start.Trim().Length != TIME_LENGTH || item.Finish.Trim().Length != TIME_LENGTH)
            result = item;

         if (result == null)
            result = CheckItemTime(item);

         return result;
      }

      private static TimeTracking.Item CheckItemTime(TimeTracking.Item item)
      {
         TimeTracking.Item result = null;
         const char TIME_SEPARATOR = ':';

         if (!CheckItemTimeLow(item.Start.Split(TIME_SEPARATOR)))
            result = item;

         if (!CheckItemTimeLow(item.Finish.Split(TIME_SEPARATOR)))
            result = item;

         return result;
      }

      private static bool CheckItemTimeLow(string[] time)
      {
         bool result = true;

         if (time.Length != 2)
            result = false;

         if (result)
         {
            string hh = time[0];
            int hv = 0;

            if (!Int32.TryParse(hh, out hv) || !(hv >= 0 && hv < 24))
               result = false;

            if (result)
            {
               string mm = time[1];
               int mv = 0;

               if (!Int32.TryParse(mm, out mv) || !(mv >= 0 && mv < 60))
                  result = false;
            }
         }

         return result;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         int invalid = CheckValidData();

         if (invalid != -1)
         {
            grid.CurrentCell = grid[0, invalid];
            MessageBox.Show("Неверные данные");
         }else
            DoSave();
      }

      private void DoSave()
      {
         
         SimpleDataSet<TimeTracking> dsUpd = new SimpleDataSet<TimeTracking>(TimeTracking.OBJECT_NAME);
         DateTime dt = dateTimePicker.Value.Date;

         TimeTracking tt = new TimeTracking();
         tt.month = dt.Month;
         tt.year = dt.Year;

         double c = 0.0;
         if (!Double.TryParse(tbCost.Text, out c))
            c = 0.0;

         tt.cost = c;

         foreach (TimeTracking.Item i in data)
            tt.items.Add(i);

         dsUpd.Add(tt);
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsUpd);

         if (!DataModule.UpdateDataSet(upd, null, null, Config.GetConfig().GetConnection()))
            DialogUtil.UpdateErrMsg(this);
         else
            btnSave.Enabled = false;
      }

      private void grid_CellBeginEdit(object sender, DataGridViewCellCancelEventArgs e)
      {
         if (e.ColumnIndex == 2 || e.ColumnIndex == 3)
         {
            maskText.Text = ((DataGridView)sender)[e.ColumnIndex, e.RowIndex].Value.ToString();
            Rectangle rect = ((DataGridView)sender).GetCellDisplayRectangle(e.ColumnIndex, e.RowIndex, true);
            maskText.Location = rect.Location;
            maskText.Size = rect.Size;
            maskText.Visible = true;

            grid.Invalidate();
         }
      }

      private void grid_CellEndEdit(object sender, DataGridViewCellEventArgs e)
      {
         if (maskText.Visible)
         {
            ((DataGridView)sender).CurrentCell.Value = maskText.Text;
            maskText.Visible = false;
         }

         btnSave.Enabled = true;
      }

      private void tbCost_TextChanged(object sender, EventArgs e)
      {
         btnSave.Enabled = true;
      }

      private DateTime gridDate = DateTime.Now;
      private Color gridBackColor = Color.White;

      private void grid_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         //TimeTracking.Item item = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as TimeTracking.Item;

         //if (item != null)
         //{
         //   if (item.Date != gridDate)
         //   {
         //      InvertGridBackColor();
         //      gridDate = item.Date;
         //   }

         //   e.CellStyle.BackColor = gridBackColor;
         //}
      }

      private void InvertGridBackColor()
      {
         if (gridBackColor == Color.White)
            gridBackColor = Color.LightBlue;
         else
            gridBackColor = Color.White;
      }

      private void dateTimePicker_ValueChanged(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void btnRem_Click(object sender, EventArgs e)
      {
         var dlg = new FmCalendar();
         dlg.Date = dateTimePicker.Value.Date;

         if (dlg.ShowDialog() == DialogResult.OK)
         {
            bool has = false;
            List<TimeTracking.Item> list = new List<TimeTracking.Item>();
            foreach (TimeTracking.Item i in data)
               list.Add(i);

            foreach (TimeTracking.Item i in list)
               if (i.Date == dlg.Date)
               {
                  if (!has)
                     has = true;

                  data.Remove(i);
               }

            if (has)
            {
               SortGridItems();
               btnSave.Enabled = true;
            }
         }
      }

      private void bntReport_Click(object sender, EventArgs e)
      {
         TimeTrackingReport.Do(dateTimePicker.Value.Month, dateTimePicker.Value.Year, this);
      }
   }
}

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Globalization;

namespace GRSoft.Ads
{
   public partial class FmSchedule : Form
   {
      DsBrigade dsBrigade;
      DsSchedule dsSchedule;
      private const int ADDRESS_CLMN_INDEX = 3;

      public FmSchedule(Brigade brigade)
      {
         InitializeComponent();
         dsBrigade = (DsBrigade) DataModule.Get(Brigade.OBJECT_NAME) ?? new DsBrigade(true);
         dsSchedule = new DsSchedule(false);
         cbBrigade.Tag = brigade;
         dgvScheduleAddress.DisplayMember = "Address";
      }

      private void bntRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> updSet = new List<IDataSet>();
         Brigade brigade = (Brigade)cbBrigade.SelectedItem;

         if (brigade != null)
         {
            DateTime fd = new DateTime((int)udYear.Value, cbMonth.SelectedIndex + 1, 1);

            dsSchedule.Filter = String.Format("brigade='{0}' and date >= ToDate('{1}') and date <= ToDate('{2}')", 
               brigade.id, fd, fd.AddMonths(1).AddDays(-1));

            updSet.Add(dsSchedule);

            DataModule.SetDataRepsonceHandlers(DataModule_DataProcessed,
               DataModule_OnDataResponceError);
            FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               updSet, FmWait.ProgressIndicator));
         }
      }

      void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         Invoke(new InvokeDelegate(RefreshData));
      }

      void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();
         MessageBox.Show(e.Msg);
      }

      private void RefreshData()
      {
         List<Schedule> slist = new List<Schedule>();

         foreach (Schedule schedule in dsSchedule.Data)
            slist.Add(schedule);

         slist.Sort(new Comparison<Schedule>(delegate(Schedule s1, Schedule s2) { return s1.date.CompareTo(s2.date); }));
         dgvSchedule.DataSource = slist;

         Brigade b = cbBrigade.SelectedItem as Brigade;

         if (b != null)
         {
            List<BrigadeAddress> blist = new List<BrigadeAddress>();
            BrigadeAddress emptyAddress = new BrigadeAddress();
            blist.Add(emptyAddress);
            blist.AddRange(b.address);

            dgvScheduleAddress.DataSource = blist;
            
            
         }
      }

      private void FmSchedule_Load(object sender, EventArgs e)
      {
         List<Brigade> brigades = new List<Brigade>();

         foreach (Brigade b in dsBrigade.Data)
            brigades.Add(b);

         brigades.Sort(new Comparison<Brigade>(
            delegate(Brigade b1, Brigade b2)
            {
               return b1.Name.CompareTo(b2.Name);
            }));

         cbBrigade.Items.AddRange(brigades.ToArray());
         Brigade selBrigade = (Brigade)cbBrigade.Tag;

         if(selBrigade != null)
            foreach(Brigade b in cbBrigade.Items)
               if (b.id.Equals(selBrigade.id))
               {
                  cbBrigade.SelectedItem = b;
                  break;
               }

         for (int i = 1; i <= 12; i++)
         {
            string monthName = CultureInfo.CurrentCulture.DateTimeFormat.GetMonthName(i);
            cbMonth.Items.Add(monthName);
         }

         cbMonth.SelectedIndex = DateTime.Now.Month == 12 ? 1 : DateTime.Now.Month;
         udYear.Value = DateTime.Now.Month == 12 ? DateTime.Now.AddYears(1).Year : DateTime.Now.Year;

         var values = Enum.GetValues(typeof(Schedule.Status));        
         foreach(var value in values)
         {
            Schedule.Status status = (Schedule.Status) Enum.ToObject(typeof(Schedule.Status), value);
            ToolStripMenuItem item = new ToolStripMenuItem(Schedule.StatusToStr(status));
            item.Tag = status;
            menu.Items.Add(item);
         }

         btnSave.Enabled = false;
      }

      private void btnCreate_Click(object sender, EventArgs e)
      {

         if (dgvSchedule.RowCount > 0 &&
            MessageBox.Show(this, "График работ будет перезаписан!", "Внимание", 
            MessageBoxButtons.OKCancel, MessageBoxIcon.Warning) != DialogResult.OK)
               return;
         
         DateTime start = new DateTime((int)udYear.Value, cbMonth.SelectedIndex + 1, 1);
         DateTime end = start.AddMonths(1).AddDays(-1);
         Brigade brigade = cbBrigade.SelectedItem as Brigade;

         if (brigade != null)
         {
            btnSave.Enabled = true;
            dsSchedule.Clear();
            int i = 0;
            while (start <= end)
            {
               Schedule schedule = new Schedule();
               schedule.brigade = brigade;
               schedule.date = start;
               schedule.status = (int)Schedule.Status.Active;

               foreach (BrigadeDistrict bd in brigade.region)
               {
                  Schedule.ScheduleDistrict district = new Schedule.ScheduleDistrict();
                  district.district = bd.district;
                  schedule.districts.Add(district);
               }

               dsSchedule.Add(i++, schedule);
               start = start.AddDays(1);
            }

            RefreshData();
         }
      }

      private void btnDistrict_Click(object sender, EventArgs e)
      {
         FmDistrict.ShowInstance(new Invoker(delegate(object param) 
            {
               Schedule schedule = GetSelectedSchedule();

               if (schedule != null)
               {
                  Schedule.ScheduleDistrict scheduleDistrict = new Schedule.ScheduleDistrict();
                  scheduleDistrict.district = (District)param;
                  bool contains = false;
                  foreach(Schedule.ScheduleDistrict d in schedule.districts)
                     if (d.district.id.Equals(scheduleDistrict.district.id))
                        contains = true;

                  if (!contains)
                  {
                     schedule.districts.Add(scheduleDistrict);
                     UpdateDetailTable();
                     btnSave.Enabled = true;
                  }
               }
            }));
      }

      private Schedule GetSelectedSchedule()
      {
         DataGridViewRow row = dgvSchedule.CurrentRow;

         if (row == null)
            return null;

         return (Schedule)row.DataBoundItem;
      }

      private void UpdateDetailTable()
      {
         List<Schedule.ScheduleDistrict> list = new List<Schedule.ScheduleDistrict>();
         Schedule schedule = GetSelectedSchedule();

         if(schedule != null)
         {
            list.AddRange(schedule.districts);
            list.Sort(new Comparison<Schedule.ScheduleDistrict>(
               delegate(Schedule.ScheduleDistrict d1, Schedule.ScheduleDistrict d2)
               {
                  if (d1.district == null)
                     return (d2.district == null)  ? 0 : - 1;
                  if (d2.district == null)
                     return 1;
                  return d1.district.Name.CompareTo(d2.district.Name);
               }));
         }

         dgvDistrict.DataSource = list;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         List<IDataSet> wrSet = new List<IDataSet>();
         wrSet.Add(dsSchedule);

         if (!DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()))
            MessageBox.Show("Ошибка");
         else
            btnSave.Enabled = false;
      }

      private void dgvSchedule_SelectionChanged(object sender, EventArgs e)
      {
         UpdateDetailTable();
      }

      private void menu_Opening(object sender, CancelEventArgs e)
      {
         Schedule schedule = GetSelectedSchedule();

         foreach (ToolStripMenuItem item in ((ContextMenuStrip)sender).Items)
            if (item.Tag.Equals((Schedule.Status)schedule.status))
               item.Checked = true;
            else
               item.Checked = false;
      }

      private void dgvSchedule_MouseDown(object sender, MouseEventArgs e)
      {
         DataGridView.HitTestInfo info = ((DataGridView)sender).HitTest(e.X, e.Y);

         if (info != null && e.Button == MouseButtons.Right)
         {
            ((DataGridView)sender).CurrentCell =
               ((DataGridView)sender).Rows[info.RowIndex].Cells[info.ColumnIndex];
         }

         UpdateDetailTable();
      }

      private void menu_ItemClicked(object sender, ToolStripItemClickedEventArgs e)
      {
         Schedule schedule = GetSelectedSchedule();

         if (schedule != null)
            schedule.status = (int)e.ClickedItem.Tag;

         dgvSchedule.Refresh();
         btnSave.Enabled = true;
      }

      private void dgvSchedule_CellFormatting(object sender, DataGridViewCellFormattingEventArgs e)
      {
         Schedule schedule = (Schedule)((DataGridView)sender).Rows[e.RowIndex].DataBoundItem;

         if (schedule != null && e.ColumnIndex != ADDRESS_CLMN_INDEX)
         {
            switch ((Schedule.Status)schedule.status)
            {
               case Schedule.Status.Reserved:
                  e.CellStyle.BackColor = Color.Yellow;
                  break;
               case Schedule.Status.Disabled:
                  e.CellStyle.BackColor = Color.Gray;
                  break;
               default:
                  e.CellStyle.BackColor = Color.White;
                  break;
            }
         }

         DataGridViewColumn clmn = dgvSchedule.Columns[e.ColumnIndex];

         if (clmn.Name.Equals("dgvScheduleAddress"))
         { 
            List<BrigadeAddress> list = dgvScheduleAddress.DataSource as List<BrigadeAddress>;

            if (list != null)
            {
               foreach (BrigadeAddress ba in list)
               {
                  if (ba.latitude == schedule.latitude &&
                     ba.longitude == schedule.longitude &&
                     ba.address.Equals(schedule.address))
                  {
                     e.Value = ba;
                     break;
                  }
               }
            }
         }
      }

      private void btnDelDistrict_Click(object sender, EventArgs e)
      {
         Schedule.ScheduleDistrict district = GetSelectedDistrict();
         Schedule schedule = GetSelectedSchedule();

         if (schedule != null && district != null)
            schedule.districts.Remove(district);

         UpdateDetailTable();
         btnSave.Enabled = true;
      }

      private Schedule.ScheduleDistrict GetSelectedDistrict()
      {
         Schedule.ScheduleDistrict result = null;
         DataGridViewRow row = dgvDistrict.CurrentRow;

         if (row != null)
            result = (Schedule.ScheduleDistrict)row.DataBoundItem;

         return result;
      }

      private void FmSchedule_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled == true &&
            MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.OKCancel) == DialogResult.OK)
            btnSave_Click(null, null);
      }

      private void dgvSchedule_EditingControlShowing(object sender, DataGridViewEditingControlShowingEventArgs e)
      {
         if (e.Control is ComboBox)
         {
            ComboBox cb = (ComboBox)e.Control;
            cb.SelectedIndexChanged += new EventHandler(cb_SelectedIndexChanged);
         }
      }

      void cb_SelectedIndexChanged(object sender, EventArgs e)
      {
         int index = ((DataGridViewComboBoxEditingControl)sender).EditingControlRowIndex;

         if (index >= 0 && index < dgvSchedule.RowCount)
         {
            DataGridViewRow row = dgvSchedule.Rows[index];

            if (row != null)
            {
               Schedule schedule = row.DataBoundItem as Schedule;
               BrigadeAddress ba = ((DataGridViewComboBoxEditingControl)sender).SelectedValue as BrigadeAddress;
               if (schedule != null && ba != null)
               {
                  schedule.address = ba.Address;
                  schedule.longitude = ba.longitude;
                  schedule.latitude = ba.latitude;
               }

               btnSave.Enabled = true;
            }
         }
      }
   }
}

/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма Рассписание уроков
 * 
 * kki   11/12/2010   creating
 */
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager.DataObjects;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class FmSchedule : Form
   {
      private static FmSchedule instance;

      private DsLocality dsLocality = DsLocality.GetDataSet();
      private DsSchoolEntity dsSchoolEntity = DsSchoolEntity.GetDataSet();
      private DsSchoolSubject dsSchoolSubject = DsSchoolSubject.GetDataSet();
      private DsSchedule dsSchedule = DsSchedule.GetDataSet();

      private ScheduleMediator controlObserver;

      public FmSchedule()
      {
         InitializeComponent();
         controlObserver = new ScheduleMediator(this);
      }

      public static void ShowInstance()
      {
         if (instance == null)
         {
            instance = new FmSchedule();
            instance.Show();
         }
         else
         {
            instance.Activate();
         }
      }

      private void FmSchedule_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void btnLocality_Click(object sender, EventArgs e)
      {
         FmLocality.ShowInstance();
      }

      private void btnSchool_Click(object sender, EventArgs e)
      {
         FmSchool.ShowInstance();
      }

      private void btnClass_Click(object sender, EventArgs e)
      {
         FmClass.ShowInstance();
      }

      private void btnSchoolSubject_Click(object sender, EventArgs e)
      {
         FmSchoolSubject.ShowInstance();
      }

      private void FmSchedule_Load(object sender, EventArgs e)
      {
         RefreshDataSets();
      }

      private void RefreshDataSets()
      {
         DataModule.DataProcessed += RefreshRetrieveComlete;
         DataModule.OnDataResponceError += DataConnectionError;

         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsLocality);
         list.Add(dsSchoolEntity);
         list.Add(dsSchedule);
         list.Add(dsSchoolSubject);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.Connection, 
            list, FmWait.ProgressIndicator));
      }

      private void UpdateForm()
      {
         Dialogs.UpdateLocalityComboBox(cbLocality, dsLocality);

         if (cbLocality.SelectedItem != null)
            Dialogs.UpdateSchoolComboBox(cbLocality, cbSchool, dsSchoolEntity);

         controlObserver.Update();
      }

      private void RefreshRetrieveComlete(object o, EventArgs e)
      {
         ClearRegisterDataModuleEvents();
         Invoke(new InvokeDelegate(delegate { UpdateForm(); }));
      }

      private void DataConnectionError(EDataResponse e)
      {
         ClearRegisterDataModuleEvents();
         MessageBox.Show(e.Msg);
      }

      private void ClearRegisterDataModuleEvents()
      {
         FmWait.CloseForm();
         DataModule.OnDataResponceError -= DataConnectionError;
         DataModule.DataProcessed -= RefreshRetrieveComlete;
      }

      class ScheduleMediator : ControlDbMediator
      {
         FmSchedule fmSchedule;

         public ScheduleMediator(FmSchedule fmSchedule)
         {
            this.fmSchedule = fmSchedule;
         }

         public override void Update()
         {
            //throw new Exception("The method or operation is not implemented.");
         }
      }

      private void cbSchool_SelectedIndexChanged(object sender, EventArgs e)
      {
         Dialogs.UpdateClassComboBox(cbSchool, cbClass,
            dsSchoolEntity);
         controlObserver.Update();

         if (cbSchool.SelectedItem != null)
            PermanentData.Data.SchoolID =
               ((SchoolItem)cbSchool.SelectedItem).entity.id;

         if (cbClass.SelectedItem == null)
         {
            UpdateSchedules();
         }
      }

      private void cbLocality_SelectedIndexChanged(object sender, EventArgs e)
      {
         Dialogs.UpdateSchoolComboBox(cbLocality, cbSchool, dsSchoolEntity);
         controlObserver.Update();

         if (cbLocality.SelectedItem != null)
            PermanentData.Data.LocalityID =
               ((LocalityItem)cbLocality.SelectedItem).locality.id;

         if (cbSchool.SelectedItem == null)
         {
            cbClass.Items.Clear();
            cbClass.Text = string.Empty;
            UpdateSchedules();
         }
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshDataSets();
      }

      private void DataGrids_DragEnter(object sender, DragEventArgs e)
      {
         if (e.Data.GetData(typeof(SchoolSubject)) == null)
            e.Effect = DragDropEffects.None;
         else
            e.Effect = DragDropEffects.Copy;
      }

      private void DataGrids_DragDrop(object sender, DragEventArgs e)
      {
         DataGridView dataGrid = (DataGridView)sender;

         SchoolSubject ss = (SchoolSubject) e.Data.GetData(typeof(SchoolSubject));
         if (ss == null)
            return;

         int day = Convert.ToInt32((string)dataGrid.Tag);
         int groupID = ((SchoolItem)cbClass.SelectedItem).entity.id;
         Schedule schedule = GetSchedule(groupID, day);

         if (schedule == null)
         {
            schedule = new Schedule();
            schedule.day = day;
            schedule.group = groupID;

            DsSchedule ds = DsSchedule.GetDataSet(false);
            ds.Add(ds.Count, schedule);
            List<IDataSet> listDS = new List<IDataSet>();
            listDS.Add(ds);
            DataModule.InsertDataSets(listDS, Config.Connection);

            dsSchedule.Add(schedule.id, schedule);
         }

         if (schedule.subjects == null)
            schedule.subjects = new List<ScheduleItem>();

         ScheduleItem si = new ScheduleItem();
         si.id = ss.id;

         schedule.subjects.Add(si);

         DataGridViewRow row = new DataGridViewRow();
         row.CreateCells(dataGrid, dataGrid.RowCount + 1, ss.name, si.PeriodToStr());
         row.Tag = si;
         dataGrid.Rows.Add(row);

         List<IDataSet> wrObj = new List<IDataSet>();
         wrObj.Add(dsSchedule);

         DataModule.UpdateDataSet(wrObj, null, null, Config.Connection);
      }

      private Schedule GetSchedule(int group, int day)
      {
         foreach (Schedule schedule in dsSchedule.Data)
         {
            if (schedule.group == group && schedule.day == day)
               return schedule;
         }

         return null;
      }

      private void cmSchedule_Opening(object sender, CancelEventArgs e)
      {
         DataGridView dgv = (DataGridView)((ContextMenuStrip)sender).SourceControl;

         if (dgv.RowCount == 0)
         {
            e.Cancel = true;
            return;
         }

         ScheduleItem scheduleItem = (ScheduleItem)dgv.CurrentRow.Tag;

         if (scheduleItem == null)
         {
            e.Cancel = true;
            return;
         }

         cmEven.Checked = false;
         cmOdd.Checked = false;

         if (scheduleItem.period == 1)
            cmOdd.Checked = true;
         else if (scheduleItem.period == 2)
            cmEven.Checked = true;
      }

      private void cmEven_Click(object sender, EventArgs e)
      {
         SetPeriod(sender ,2);
      }

      private void cmOdd_Click(object sender, EventArgs e)
      {
         SetPeriod(sender, 1);
      }

      private void SetPeriod(object sender, int period)
      {
         DataGridView dgv;
         ScheduleItem scheduleItem;
         GetSelectedScheduleItem(sender, out dgv, out scheduleItem);

         if (dgv.RowCount == 0 || scheduleItem == null)
            return;

         if (scheduleItem.period == period)
            scheduleItem.period = 0;
         else
            scheduleItem.period = period;

         DataGridViewRow row = dgv.CurrentRow;
         row.Cells[2].Value = scheduleItem.PeriodToStr();
      }

      private static void GetSelectedScheduleItem(object sender, out DataGridView dgv, out ScheduleItem scheduleItem)
      {
         dgv = (DataGridView)((ContextMenuStrip)((System.Windows.Forms.ToolStripMenuItem)(sender)).Owner).SourceControl;
         scheduleItem = (ScheduleItem)dgv.CurrentRow.Tag;
      }

      private void cbClass_SelectedIndexChanged(object sender, EventArgs e)
      {
         UpdateSchedules();

         if (cbClass.SelectedItem != null)
            PermanentData.Data.ClassID = ((SchoolItem)cbClass.SelectedItem).entity.id;
      }

      private void UpdateSchedules()
      {
         DataGridView[] grids = new DataGridView[] {dgvMonday, dgvTuesday, dgvWednesday, dgvThursday, dgvFriday, dgvSaturday};

         foreach (DataGridView dgv in grids)
            dgv.SuspendLayout();


         try
         {
            foreach (DataGridView dgv in grids)
               dgv.Rows.Clear();

            if (cbClass.SelectedItem == null)
               return;

            int groupID = ((SchoolItem)cbClass.SelectedItem).entity.id;

            for (int i = 0; i <= 6; i++)
            {
               Schedule schedule = GetSchedule(groupID, i);

               if (schedule != null && 
                     schedule.subjects != null &&
                     schedule.subjects.Count > 0)
               {
                  DataGridView grid = grids[i];

                  foreach (ScheduleItem si in schedule.subjects)
                  {
                     DataGridViewRow row = new DataGridViewRow();
                     SchoolSubject ss = dsSchoolSubject[si.id];
                     row.CreateCells(grid, grid.RowCount + 1, ss.name, si.PeriodToStr());
                     row.Tag = si;
                     grid.Rows.Add(row);
                  }
               }
            }
         }
         finally
         {
            foreach (DataGridView dgv in grids)
               dgv.ResumeLayout();
         }
      }

      private void cmDel_Click(object sender, EventArgs e)
      {
         if (!Dialogs.AllowedDelCurRow())
            return;

         DataGridView dgv;
         ScheduleItem scheduleItem;
         GetSelectedScheduleItem(sender, out  dgv, out scheduleItem);

         int groupID = ((SchoolItem)cbClass.SelectedItem).entity.id;
         Schedule schedule = GetSchedule(groupID, Convert.ToInt32((string)dgv.Tag));

         if (schedule == null)
            return;

         schedule.subjects.Remove(scheduleItem);

         DsSchedule toRem = DsSchedule.GetDataSet(false);
         toRem.Add(schedule.id, schedule);
         List<IDataSet> rm = new List<IDataSet>();
         rm.Add(toRem);

         if (DataModule.UpdateDataSet(null, rm, null, Config.Connection))
         {
            dgv.Rows.RemoveAt(dgv.CurrentRow.Index);
            UpdateSubjectsNumber(dgv);
         }
      }

      private void UpdateSubjectsNumber(DataGridView dgv)
      {
         dgv.SuspendLayout();

         try
         {
            int number = 1;
            foreach (DataGridViewRow row in dgv.Rows)
               row.Cells[0].Value = number++;
         }
         finally
         {
            dgv.ResumeLayout();
         }
      }
   }
}
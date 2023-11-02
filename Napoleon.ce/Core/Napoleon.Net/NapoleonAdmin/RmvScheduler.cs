using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public partial class RmvScheduler : UserControl
   {
      static readonly string RMV_SCHEDULER = "rmvPhotoTask";
      static readonly string MONTHS_TO_REMOVE = "schMnthRmv";
      static readonly int DEF_MONTHS = 6;

      SimpleDataSet<ServerTaskScheduler> scheduler = new SimpleDataSet<ServerTaskScheduler>(ServerTaskScheduler.OBJECT_NAME, false);
      SimpleDataSet<ServerTaskParams> paramSet = new SimpleDataSet<ServerTaskParams>(ServerTaskParams.OBJECT_NAME);
      SimpleDataSet<ServerTaskLog> log = new SimpleDataSet<ServerTaskLog>(ServerTaskLog.OBJECT_NAME, false);

      CheckBox[] days;

      Config config;

      public RmvScheduler()
      {
         InitializeComponent();

         dtpLogEnd.Value = DateTime.Now.Date;
         dtpLogStart.Value = DateTime.Now.Date.AddMonths(-1);

         for(int i=1; i<= 12; i++)
         {
            cbMonths.Items.Add(i);
         }
         cbMonths.SelectedItem = DEF_MONTHS;

         DateTime src = DateTime.Now;
         dtpSchedule.Value = new DateTime(src.Year, src.Month, src.Day, 23, 0, 0);

         days = new CheckBox[]
         {
            cbSun, cbMon, cbTue, cbWed, cbThr, cbFri, cbSat
         };
      }

      public void Init(Config config)
      {
         this.config = config;
      }

      public string LogFilter()
      {
         return string.Format("\"id\"='{0}' and \"date\">=ToDate('{1:dd/MM/yyyy}') and \"date\" <=ToDate('{2:dd/MM/yyyy 23:59:59}')"
            ,RMV_SCHEDULER
            ,dtpLogStart.Value
            ,dtpLogEnd.Value);
      }

      public void UpdateData(List<IDataSet> upd)
      {
         string filter = string.Format("\"id\"='{0}'", RMV_SCHEDULER);
         scheduler.Filter = filter;
         log.Filter = LogFilter();
         paramSet.Filter = filter;

         upd.Add(scheduler);
         upd.Add(paramSet);
         upd.Add(log);
      }

      void UpdateLog()
      {
         List<ServerTaskLog> src = new List<ServerTaskLog>();
         foreach (ServerTaskLog stl in log.Data)
         {
            src.Add(stl);
         }

         src.Sort((x, y) => y.date.CompareTo(x.date));
         dgvSchLog.DataSource = src;
      }

      void UpdateControl()
      {
         foreach(CheckBox cb in days)
         {
            cb.Checked = false;
         }

         foreach(ServerTaskScheduler sts in scheduler.Data)
         {
            bool setTime = false;
            foreach(ServerTaskScheduler.Item sti in sts.items)
            {
               DateTime dateTime = new DateTime(1970, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc);
               dateTime = dateTime.AddSeconds(sti.starting).ToLocalTime();
               
               if (!setTime)
               {
                  dtpSchedule.Value = dateTime;
                  setTime = true;
               }

               int wd = (int)dateTime.DayOfWeek;
               if(wd < days.Length)
               {
                  days[wd].Checked = true;
               }
            }
            break;
         }

         foreach(ServerTaskParams stp in paramSet.Data)
         {
            if(stp.key == MONTHS_TO_REMOVE)
            {
               int val = 0;
               if(int.TryParse(stp.value, out val))
               {
                  cbMonths.SelectedItem = val;
               }
               else
               {
                  cbMonths.SelectedItem = DEF_MONTHS;
               }

               break;
            }
         }
      }

      public void OnDataLoaded()
      {
         UpdateLog();
         UpdateControl();
      }

      private void btnSchSave_Click(object sender, EventArgs e)
      {
         SimpleDataSet<ServerTaskParams> wrP = new SimpleDataSet<ServerTaskParams>(ServerTaskParams.OBJECT_NAME, false);
         SimpleDataSet<ServerTaskScheduler> wrS = new SimpleDataSet<ServerTaskScheduler>(ServerTaskScheduler.OBJECT_NAME, false);

         ServerTaskParams stp = new ServerTaskParams();
         stp.id = RMV_SCHEDULER;
         stp.key = MONTHS_TO_REMOVE;
         stp.value = cbMonths.SelectedItem == null? DEF_MONTHS.ToString() :
            ((int)cbMonths.SelectedItem).ToString();
         wrP.Add(stp);

         List<IDataSet> wr = new List<IDataSet>();
         List<IDataSet> rmv = new List<IDataSet>();

         ServerTaskScheduler sts = new ServerTaskScheduler();
         sts.id = RMV_SCHEDULER;
         sts.name = "Удаление фото данных за период";
         sts.module = "remove_old_photos";
         wrS.Add(sts);

         DateTime startH = dtpSchedule.Value;
         while (startH.DayOfWeek != DayOfWeek.Sunday)
            startH = startH.AddDays(-1);

         foreach(CheckBox cb in days)
         {
            if(cb.Checked)
            {
               ServerTaskScheduler.Item item = new ServerTaskScheduler.Item();
               int val = (int)TimeZoneInfo.ConvertTimeToUtc(startH).Subtract(new DateTime(1970, 1, 1)).TotalSeconds;
               item.starting = val;
               item.day = 7;
               item.cycle = 1;

               sts.items.Add(item);
            }
            startH = startH.AddDays(1);
         }

         if(sts.items.Count != 0)
         {
            wr.Add(wrP);
            wr.Add(wrS);
         }
         else
         {
            rmv.Add(wrP);
            rmv.Add(wrS);
         }

         if(DataModule.UpdateDataSet(wr, rmv, null, config.GetConnection()))
         {
            MessageBox.Show("Данные записаны");
         }
         else
         {
            MessageBox.Show("Ошибка при записи");
         }
      }

      private void toolStripButton2_Click(object sender, EventArgs e)
      {
         string filter = LogFilter();
         List<IDataSet> upd = new List<IDataSet>();
         log.Filter = filter;
         upd.Add(log);

         toolStripButton2.Enabled = false;

         DBConnection conn = config.GetConnection();

         DataModule.OnDataResponceError += DataModule_OnDataResponceError; ;
         DataModule.DataProcessed += DataModule_DataProcessed;
         DataModule.RefreshGiveSets(conn, upd, null);
      }

      private void DataModule_DataProcessed(object sender, EventArgs e)
      {
         DataModule.ClearEvents();
         BeginInvoke((Action)(() => {
            toolStripButton2.Enabled = true;
            UpdateLog();
         }));
      }

      private void DataModule_OnDataResponceError(EDataResponse e)
      {
         DataModule.ClearEvents();
         BeginInvoke((Action)(() => {
            toolStripButton2.Enabled = true;
         }));
      }
   }
}

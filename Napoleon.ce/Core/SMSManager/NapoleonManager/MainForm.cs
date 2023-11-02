/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Главная форма
 * 
 * ert   21/04/2010   creating
 */

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Net;
using System.Net.Sockets;
using GRSoft.Network;
using System.Collections;
using System.Reflection;
using System.Threading;
using System.Globalization;
using GRSoft.UILib;
using System.Runtime.InteropServices;
using System.IO;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.DataObjects;
using GRSoft.NapoleonManager.Reports;

namespace GRSoft.NapoleonManager
{
   public partial class MainForm : Form
   {
      private DataWarningTooltip dateWarningTooltip;
      private DsAgent dsAgents = DsAgent.GetDataSet();
      private DsLesson dsLesson = DsLesson.GetDataSet();
      private DsSchoolEntity dsScheduleEntity = DsSchoolEntity.GetDataSet();

      public MainForm()
      {
         InitializeComponent();
         Init();
      }

      void Init()
      {
         dateWarningTooltip = new DataWarningTooltip(this);
      }

      //Справочник город
      private void miLocality_Click(object sender, EventArgs e)
      {
         FmLocality.ShowInstance();
      }

      //Справочник школы
      private void miSchool_Click(object sender, EventArgs e)
      {
         FmSchool.ShowInstance();
      }

      //Справочник классы
      private void miClass_Click(object sender, EventArgs e)
      {
         FmClass.ShowInstance();
      }

      //Справочник ученики
      private void miStudent_Click(object sender, EventArgs e)
      {
         FmStudent.ShowInstance();
      }

      //Справочник родители
      private void miParents_Click(object sender, EventArgs e)
      {
         FmParents.ShowInstatnce();
      }

      //Справочник договоры
      private void miDogovors_Click(object sender, EventArgs e)
      {
         FmDogovor.ShowInstance();
      }

      //Настройки
      private void miConfig_Click(object sender, EventArgs e)
      {
         FmConfig.ShowConfig();
      }

      //Справочник школьные предметы
      private void miSchoolSubject_Click(object sender, EventArgs e)
      {
         FmSchoolSubject.ShowInstance();
      }

      //Справочник расписание
      private void miSchedule_Click(object sender, EventArgs e)
      {
         FmSchedule.ShowInstance();
      }

      //Справочник агенты
      private void miAgents_Click(object sender, EventArgs e)
      {
         FmAgent.ShowInstance();
      }

      //Справочник маршрут
      private void miRoute_Click(object sender, EventArgs e)
      {
         FmRoute.ShowInstance();
      }

      //Условие выборки "за сегодня"
      private void miToday_Click(object sender, EventArgs e)
      {
         AdjustRangeButton(true, "За сегодня");
      }

      //Условие выборки "за период"
      private void miRange_Click(object sender, EventArgs e)
      {
         AdjustRangeButton(false, "За период");
      }

      //Настройка кнопок для выбора периода 
      private void AdjustRangeButton(bool isToday, string toolTipText)
      {
         tsbSelectRange.Image = isToday ? miToday.Image : miRange.Image;
         miToday.Checked = isToday;
         miRange.Checked = !isToday;
         tsbSelectRange.ToolTipText = toolTipText;
         dtpEnd.Enabled = !isToday;
         CheckDateValid();
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

      //форма всплывающего окна "предупреждение" о неправильном выборе даты
      class DataWarningTooltip : Form
      {
         private Label label = new Label();
         private LinkLabel lbDateChange = new LinkLabel();
         private MainForm mainForm;

         public DataWarningTooltip(MainForm mainForm)
         {
            this.mainForm = mainForm;

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
            DateTime dtTemp = mainForm.dtpBegin.Value;
            mainForm.dtpBegin.Value = mainForm.dtpEnd.Value;
            mainForm.dtpEnd.Value = dtTemp;
         }
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshDataSet();
      }

      private void RefreshDataSet()
      {
         DataModule.SetDataRepsonceHandlers(DataProcessed, Dialogs.ResponceError);

         List<IDataSet> updList = new List<IDataSet>();

         const string FILTER = "date >= ToDate('{0}') and date < ToDate('{1}')";

         dsLesson.Filter = String.Format(FILTER, dtpBegin.Value.Date, dtpEnd.Value.Date);

         updList.Add(dsAgents);
         updList.Add(dsScheduleEntity);
         updList.Add(dsLesson);

         FmWait.ShowForm(this,
            DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               updList, FmWait.ProgressIndicator));
      }

      // Событие окончания выборки
      private void DataProcessed(object o, EventArgs e)
      {
         Dialogs.EndOfResponce();
         Invoke(new InvokeDelegate(delegate { UpdateGrid(); }));
      }

      private void UpdateGrid()
      {
         dgvSummary.SuspendLayout();

         try
         {
            Dictionary<string,
               KeyValuePair<List<int>, Counter>> statistic =
               new Dictionary<string,
                  KeyValuePair<List<int>, Counter>>();


            foreach (Lesson lesson in dsLesson.Data)
            {
               SchoolEntity se = dsScheduleEntity[lesson.classID];

               if (statistic.ContainsKey(lesson.userid))
               {
                  KeyValuePair<List<int>, Counter> data = statistic[lesson.userid];

                  
                  if (!data.Key.Contains(se.parent))
                     data.Key.Add(se.parent);

                  data.Value.Inc();
               }
               else
               {
                  List<int> schools = new List<int>();
                  schools.Add(se.parent);
                  Counter counter = new Counter();
                  counter.Inc();

                  KeyValuePair<List<int>, Counter> pair =
                     new KeyValuePair<List<int>, Counter>(schools, counter);

                  statistic.Add(lesson.userid, pair);
               }
            }

            foreach (KeyValuePair<string,
               KeyValuePair<List<int>, Counter>> kvp in statistic)
            {
               Agent agent;

               if (!dsAgents.GetAgentByID(kvp.Key, out agent))
                  continue;

               DataGridViewRow row = new DataGridViewRow();
               row.CreateCells(dgvSummary, agent.name, kvp.Value.Key.Count,
                  kvp.Value.Value.Val);
               row.Tag = agent;
               dgvSummary.Rows.Add(row);
            }
         }
         finally
         {
            dgvSummary.ResumeLayout();
         }
      }

      //Получить конечный интервал выборки дат
      private DateTime GetRangeEndDate()
      {
         DateTime endDate = miRange.Checked ? dtpEnd.Value.AddDays(1) 
            : dtpBegin.Value.Date.AddDays(1);
         return new DateTime(endDate.Year, endDate.Month, endDate.Day);
      }

      private void MainForm_Load(object sender, EventArgs e)
      {
         SetVersionText();
      }

      //Версия программы
      private void SetVersionText()
      {
         string result = string.Empty;
         Assembly a = Assembly.GetEntryAssembly();

         object[] attrs = a.GetCustomAttributes(typeof(AssemblyFileVersionAttribute), false);
         if (attrs.Length > 0)
         {
            lbVersion.Text = "версия: " + (attrs[0] as AssemblyFileVersionAttribute).Version;

            string f = a.GetModules()[0].FullyQualifiedName;
            lbVersion.Text += " / " + File.GetLastWriteTime(f).ToShortDateString();
         }
         else
            lbVersion.Text = string.Empty;
      }

      //Открыть www.grsoft.ru в браузере
      private void linkLabel1_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         OpenLink.NewWindow("www.grsoft.ru");
      }

      //Переключение условий выборки по щелчку на кнопку
      private void tsbSelectRange_Click(object sender, EventArgs e)
      {
         if (miToday.Checked)
         {
            miRange_Click(sender, e);
         }
         else
         {
            miToday_Click(sender, e);
         }
      }

      //Отчет
      private void btnReport_Click(object sender, EventArgs e)
      {
         CommonStatisticReport report = new CommonStatisticReport();
         report.Build(dtpBegin.Value, GetRangeEndDate(), dgvSummary);
         report.Show();
      }

      //Событие изменение условия даты начала выборки
      private void dtpBeginDate_ValueChanged(object sender, EventArgs e)
      {
         CheckDateValid();
      }

      //Событие изменение условия даты конца выборки
      private void dtpEndDate_ValueChanged(object sender, EventArgs e)
      {
         CheckDateValid();
      }

      //Подробно
      private void dgvSummary_DoubleClick(object sender, EventArgs e)
      {
         if (dgvSummary.CurrentRow == null)
            return;

         Agent agent = (Agent)dgvSummary.CurrentRow.Tag;
         
         if (agent != null)
            FmDetail.ShowInstance(agent, dtpBegin.Value.Date, GetRangeEndDate());
      }
   }

   /// <summary>
   /// Класс счетчик
   /// </summary>
   class Counter
   {
      private int val;

      public int Val { get { return val; } }
      public void Inc() { val++; }
   }
}

/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма Подробно
 * 
 * kki   28/12/2010   creating
 */
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using System.Globalization;
using System.Collections;
using System.IO;
using System.Reflection;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.DataObjects;
using GRSoft.NapoleonManager.Reports;

namespace GRSoft.NapoleonManager
{
   public partial class FmDetail : Form
   {
      private static FmDetail instance;
      private DsAgent dsAgents = DsAgent.GetDataSet();
      private DsLesson dsLesson = DsLesson.GetDataSet();
      private DsAnnonce dsAnnonce = DsAnnonce.GetDataSet();
      private DsSchoolEntity dsSchoolEntity = DsSchoolEntity.GetDataSet();
      private DsSchoolSubject dsSchoolSubject = DsSchoolSubject.GetDataSet();
      private DsStudent dsStudent = DsStudent.GetDataSet();

      private FmDetail()
      {
         InitializeComponent();
      }

      private void Init(Agent curAgent)
      {
         AgentItem selectedItem = null;
         foreach (Agent agent in dsAgents.Data)
         {
            if (agent.id == Agent.MANAGER_ID)
               continue;

            AgentItem item = new AgentItem(agent);
            cbAgents.Items.Add(new AgentItem(agent));
            if (agent.id.Equals(curAgent.id))
               selectedItem = item;
         }

         cbAgents.Sorted = true;
         Dialogs.SelectItem(cbAgents, selectedItem);
      }
      public static void ShowInstance(Agent agent, DateTime begin, DateTime end)
      {
         if (instance == null)
         {
            instance = new FmDetail();
            instance.Init(agent);
            instance.Show();
            instance.dtpBegin.Value = begin;
            instance.dtpEnd.Value = end;
            instance.RefreshDataSets(instance.dtpBegin.Value.Date,
               instance.dtpEnd.Value.Date, agent.id);
         }
         else
            instance.Activate();
      }

      //Обновить наборы данных
      private void RefreshDataSets(DateTime begin, DateTime end, string agentID)
      {
         const string FILTER = "date >= ToDate('{0}') and date < ToDate('{1}') and userid={2}";
         dsLesson.Filter = string.Format(FILTER, begin, end, agentID);

         List<IDataSet> updList = new List<IDataSet>();
         updList.Add(dsLesson);
         updList.Add(dsAnnonce);
         updList.Add(dsSchoolEntity);
         updList.Add(dsSchoolSubject);
         updList.Add(dsStudent);

         DataModule.SetDataRepsonceHandlers(DataProcessed, Dialogs.ResponceError);
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
         dgvDetail.SuspendLayout();

         try
         {
            dgvDetail.Rows.Clear();

            foreach (Lesson lesson in dsLesson.Data)
            {
               DataGridViewRow row = new DataGridViewRow();
               row.CreateCells(dgvDetail, dsSchoolEntity[lesson.classID].number,
                  dsSchoolSubject[lesson.subjectID].name, lesson.date, lesson.task);
               row.Tag = lesson;
               dgvDetail.Rows.Add(row);
            }

            if (dgvDetail.Rows.Count == 0)
               dgvSchedule.Rows.Clear();
         }
         finally
         {
            dgvDetail.ResumeLayout();
         }
      }

      private void FmDetail_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void tbnMessage_Click(object sender, EventArgs e)
      {
         Dialogs.NotImplementedYetMsg();
      }

      private void tsbMakeHtml_Click(object sender, EventArgs e)
      {
         if (dgvDetail.RowCount == 0)
            return;

         SchoolActivityReport report = new SchoolActivityReport();
         report.Build(dgvDetail, dsStudent);

         SaveFileDialog dialog = new SaveFileDialog();
         dialog.InitialDirectory = Application.StartupPath;
         dialog.AddExtension = true;
         dialog.Filter = "rtf files (*.rtf)|*.rtf";
         dialog.CheckFileExists = false;
         const string DATE_MSK = "ddMMyyyy";
         dialog.FileName =
            String.Format("SchoolActivityReport_{0}_{1}.rtf",
            dtpBegin.Value.Date.ToString(DATE_MSK),
            dtpEnd.Value.Date.ToString(DATE_MSK));

         if (dialog.ShowDialog() == DialogResult.OK)
            report.Save(dialog.FileName);
      }

      private void UpdateDetailTable(Lesson lesson)
      {
         dgvSchedule.SuspendLayout();
         try
         {
            lblAdress.Text = dsSchoolEntity[dsSchoolEntity[lesson.classID].parent].address;
            dgvSchedule.Rows.Clear();

            foreach (LessonItem item in lesson.items)
            {
               DataGridViewRow row = new DataGridViewRow();
               row.CreateCells(dgvSchedule, dsStudent[item.studentID].name, item.mark,
                  item.behavior, item.remark);
               row.Tag = item;
               dgvSchedule.Rows.Add(row);
            }
         }
         finally
         {
            dgvSchedule.ResumeLayout();
         }
      }

      private void dgvDetail_SelectionChanged(object sender, EventArgs e)
      {
         UpdateDetailTable((Lesson)dgvDetail.CurrentRow.Tag);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshDataSets(dtpBegin.Value.Date, dtpEnd.Value.Date.AddDays(1),
            ((AgentItem)cbAgents.SelectedItem).Object.id);
      }
   }
}
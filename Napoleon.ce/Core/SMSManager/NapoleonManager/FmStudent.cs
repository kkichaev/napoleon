/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма данных учеников
 * 
 * kki   01/12/2010   creating
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
   public partial class FmStudent : Form
   {
      private static FmStudent instance;
      private DsStudent dsStudent = DsStudent.GetDataSet();
      private DsLocality dsLocality = DsLocality.GetDataSet();
      private DsSchoolEntity dsSchoolEntity = DsSchoolEntity.GetDataSet();
      private StudentMediator controlObserver;

      private FmStudent()
      {
         InitializeComponent();
         controlObserver = new StudentMediator(this);
      }

      public static void ShowInstance()
      {
         if (instance == null)
         {
            instance = new FmStudent();
            instance.Show();
         }
         else
         {
            instance.Activate();
         }
      }

      private void RefreshDataSets()
      {
         DataModule.DataProcessed += RefreshRetrieveComlete;
         DataModule.OnDataResponceError += DataConnectionError;

         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsLocality);
         list.Add(dsSchoolEntity);
         list.Add(dsStudent);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(
            Config.Connection, list, FmWait.ProgressIndicator));
      }

      private void RefreshRetrieveComlete(object o, EventArgs e)
      {
         ClearRegisterDataModuleEvents();
         Invoke(new InvokeDelegate(delegate { UpdateForm(); }));
      }

      private void ClearRegisterDataModuleEvents()
      {
         FmWait.CloseForm();
         DataModule.OnDataResponceError -= DataConnectionError;
         DataModule.DataProcessed -= RefreshRetrieveComlete;
      }

      private void DataConnectionError(EDataResponse e)
      {
         ClearRegisterDataModuleEvents();
         MessageBox.Show(e.Msg);
      }

      private void UpdateForm()
      {
         Dialogs.UpdateLocalityComboBox(cbLocality, dsLocality);
      }

      private void FmStudent_Load(object sender, EventArgs e)
      {
         RefreshDataSets();
      }

      private void FmStudent_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void cbLocality_SelectedIndexChanged(object sender, EventArgs e)
      {
         cbSchool.Items.Clear();
         cbSchool.Text = String.Empty;
         cbClass.Items.Clear();
         cbClass.Text = String.Empty;
         dgvStudent.Rows.Clear();

         Dialogs.UpdateSchoolComboBox(cbLocality, cbSchool, dsSchoolEntity);
         controlObserver.Update();

         if (cbLocality.SelectedItem != null)
            PermanentData.Data.LocalityID =
               ((LocalityItem)cbLocality.SelectedItem).locality.id;
      }

      private void cbSchool_SelectedIndexChanged(object sender, EventArgs e)
      {
         cbClass.Items.Clear();
         cbClass.Text = String.Empty;
         dgvStudent.Rows.Clear();

         Dialogs.UpdateClassComboBox(cbSchool, cbClass, 
            dsSchoolEntity);
         controlObserver.Update();

         if (cbSchool.SelectedItem != null)
            PermanentData.Data.SchoolID = 
               ((SchoolItem)cbSchool.SelectedItem).entity.id;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         Student student = new Student();
         student.group = ((SchoolItem)cbClass.SelectedItem).entity.id;

         if (new FmStudentEdit(student).ShowDialog() == DialogResult.OK)
            insertRow(student);

         controlObserver.Update();
      }

      private void insertRow(Student student)
      {
         DsStudent ds = DsStudent.GetDataSet(false);

         ds.Add(ds.Count, student);
         List<IDataSet> listDS = new List<IDataSet>();
         listDS.Add(ds);
         if (DataModule.InsertDataSets(listDS, Config.Connection))
         {
            DataGridViewRow row = new DataGridViewRow();
            row.CreateCells(dgvStudent, student.name);
            row.Tag = student;
            dgvStudent.Rows.Add(row);

            dsStudent.Add(student.id, student);
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         Edit();
      }

      private void Edit()
      {
         Student student = (Student)dgvStudent.CurrentRow.Tag;

         if (new FmStudentEdit(student).ShowDialog() == DialogResult.OK)
            EditRow(student);

         controlObserver.Update();
      }

      private void EditRow(Student student)
      {
         List<IDataSet> wrObj = new List<IDataSet>();
         wrObj.Add(dsStudent);

         if (DataModule.UpdateDataSet(wrObj, null, null, Config.Connection))
         {
            DataGridViewRow row = dgvStudent.CurrentRow;
            row.Cells[0].Value = student.name;
         }
      }

      private void UpdateStudentGrid()
      {
         if (cbClass.SelectedItem == null)
            return;

         dgvStudent.SuspendLayout();
         try
         {
            dgvStudent.Rows.Clear();

            int classID = ((SchoolItem)cbClass.SelectedItem).entity.id;

            foreach (Student student in dsStudent.Data)
            {
               if (student.group == classID)
               {
                  DataGridViewRow row = new DataGridViewRow();
                  row.CreateCells(dgvStudent, student.name);
                  row.Tag = student;
                  dgvStudent.Rows.Add(row);
               }
            }
         }
         finally
         {
            dgvStudent.ResumeLayout();
         }
      }

      private void cbClass_SelectedIndexChanged(object sender, EventArgs e)
      {
         dgvStudent.Rows.Clear();
         UpdateStudentGrid();
         controlObserver.Update();

         if (cbClass.SelectedItem != null)
            PermanentData.Data.ClassID = ((SchoolItem)cbClass.SelectedItem).entity.id;
      }

      private void dgvStudent_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Clicks == 2)
            Edit();

         Dialogs.SetRowCurrent((DataGridView)sender, e);
         dgvStudent.DoDragDrop(dgvStudent.CurrentRow.Tag, DragDropEffects.Copy);
      }

      class StudentMediator : ControlDbMediator
      {
         FmStudent fmStudent;

         public StudentMediator(FmStudent fmStudent)
         {
            this.fmStudent = fmStudent;

            fmStudent.btnAdd.Enabled = false;
            fmStudent.btnEdit.Enabled = false;
            fmStudent.btnDel.Enabled = false;
         }

         public override void Update()
         {
            fmStudent.btnAdd.Enabled = fmStudent.cbClass.SelectedItem != null;

            bool editAndDelEnable = fmStudent.dgvStudent.Rows.Count > 0;

            fmStudent.btnEdit.Enabled = editAndDelEnable;
            fmStudent.btnDel.Enabled = editAndDelEnable;
         }
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshDataSets();
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (dgvStudent.CurrentRow.Tag == null)
            return;

         if (!Dialogs.AllowedDelCurRow())
            return;

         List<IDataSet> rm = new List<IDataSet>();
         DsStudent toRem = DsStudent.GetDataSet(false);
         Student st = (Student)dgvStudent.CurrentRow.Tag;
         toRem.Add(1, st);
         rm.Add(toRem);

         if (DataModule.UpdateDataSet(null, rm, null, Config.Connection))
         {
            dsStudent.Remove(st.id);
            dgvStudent.Rows.RemoveAt(dgvStudent.CurrentRow.Index);
         }

         controlObserver.Update();
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
   }
}
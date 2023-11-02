/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма Договоры
 * 
 * kki   07/12/2010   creating
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
   public partial class FmDogovor : Form
   {
      private DsDogovor dsDogovor = DsDogovor.GetDataSet();
      private DsStudent dsStudent = DsStudent.GetDataSet();
      private DsParent dsParent = DsParent.GetDataSet();
      private static FmDogovor instance;
      private DogovorMediator controlObserver;

      private FmDogovor()
      {
         InitializeComponent();
         controlObserver = new DogovorMediator(this);
      }

      public static void ShowInstance()
      {
         if (instance == null)
         {
            instance = new FmDogovor();
            instance.Show();
         }
         else
            instance.Activate();
      }

      private void FmDogovor_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void RefreshDataSets()
      {
         DataModule.OnDataResponceError += DataConnectionError;
         DataModule.DataProcessed += RefreshRetrieveComlete;

         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsDogovor);
         list.Add(dsStudent);
         list.Add(dsParent);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.Connection, 
            list, FmWait.ProgressIndicator));
      }

      private void RefreshRetrieveComlete(object o, EventArgs e)
      {
         FmWait.CloseForm();
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
         DataModule.OnDataResponceError -= DataConnectionError;
         DataModule.DataProcessed -= RefreshRetrieveComlete;
      }

      private void UpdateForm()
      {
         dgvDogovor.SuspendLayout();

         try
         {
            dgvDogovor.Rows.Clear();

            foreach (Dogovor dogovor in dsDogovor.Data)
            {
               DataGridViewRow row = new DataGridViewRow();
               row.CreateCells(dgvDogovor, dogovor.number, 
                  dogovor.start.ToString("dd.MM.yyyy"),
                  dogovor.end.ToString("dd.MM.yyyy"), 
                  Dogovor.TypeToString(dogovor.type));
               row.Tag = dogovor;
               dgvDogovor.Rows.Add(row);
            }

            UpdateStudentGrid();
            UpdateParentGrid();

            controlObserver.Open();
         }
         finally
         {
            dgvDogovor.ResumeLayout();
         }
      }

      private void btnAddDogovor_Click(object sender, EventArgs e)
      {
         Dogovor dogovor = new Dogovor();

         if (new FmDogovorEdit(dogovor).ShowDialog() == DialogResult.OK)
            insertRow(dogovor);

         controlObserver.Update();
      }

      private void insertRow(Dogovor dogovor)
      {
         DsDogovor ds = DsDogovor.GetDataSet(false);

         ds.Add(ds.Count, dogovor);
         List<IDataSet> listDS = new List<IDataSet>();
         listDS.Add(ds);

         if (DataModule.InsertDataSets(listDS, Config.Connection))
         {
            DataGridViewRow row = new DataGridViewRow();
            row.CreateCells(dgvDogovor, dogovor.number,
               dogovor.start.ToString("dd.MM.yyyy"), 
               dogovor.end.ToString("dd.MM.yyyy"),
               Dogovor.TypeToString(dogovor.type));
            row.Tag = dogovor;
            dgvDogovor.Rows.Add(row);

            dsDogovor.Add(dogovor.id, dogovor);
         }
      }

      private void btnEditDogovor_Click(object sender, EventArgs e)
      {
         Edit();
      }

      private void Edit()
      {
         Dogovor dogovor = (Dogovor)dgvDogovor.CurrentRow.Tag;

         if (new FmDogovorEdit(dogovor).ShowDialog() == DialogResult.OK)
            editRow(dogovor);

         controlObserver.Update();
      }

      private void editRow(Dogovor dogovor)
      {
         List<IDataSet> wrObj = new List<IDataSet>();
         wrObj.Add(dsDogovor);

         if (DataModule.UpdateDataSet(wrObj, null, null, Config.Connection))
         {
            DataGridViewRow row = dgvDogovor.CurrentRow;
            row.Cells[0].Value = dogovor.number;
            row.Cells[1].Value = dogovor.start.ToString("dd.MM.yyyy");
            row.Cells[2].Value = dogovor.end.ToString("dd.MM.yyyy");
            row.Cells[3].Value = Dogovor.TypeToString(dogovor.type);
         }
      }

      private void btnDeleteDogovor_Click(object sender, EventArgs e)
      {
         if (!Dialogs.AllowedDelCurRow())
            return;

         List<IDataSet> rm = new List<IDataSet>();
         DsDogovor toRem = DsDogovor.GetDataSet(false);
         Dogovor p = (Dogovor)dgvDogovor.CurrentRow.Tag;
         toRem.Add(1, p);
         rm.Add(toRem);

         if (DataModule.UpdateDataSet(null, rm, null, Config.Connection))
         {
            dsDogovor.Remove(p.id);
            dgvDogovor.Rows.RemoveAt(dgvDogovor.CurrentRow.Index);
         }

         controlObserver.Update();
      }

      private void FmDogovor_Load(object sender, EventArgs e)
      {
         RefreshDataSets();
      }

      private void btnStudent_Click(object sender, EventArgs e)
      {
         FmStudent.ShowInstance();
      }

      private void btnParent_Click(object sender, EventArgs e)
      {
         FmParents.ShowInstatnce();
      }

      private void dgvStudents_DragEnter(object sender, DragEventArgs e)
      {
         if (dgvDogovor.Rows.Count == 0)
            e.Effect = DragDropEffects.None;
         else if (e.Data.GetData(typeof(Student)) == null)
            e.Effect = DragDropEffects.None;
         else
            e.Effect = DragDropEffects.Copy;
      }

      private void dgvStudents_DragDrop(object sender, DragEventArgs e)
      {
         Student student = (Student)e.Data.GetData(typeof(Student));
         
         if (student == null)
            return;

         Dogovor dogovor = (Dogovor)dgvDogovor.CurrentRow.Tag;

         if (dogovor.students == null)
            dogovor.students = new List<DogStudent>();

         DogStudent dogStudent = new DogStudent();
         dogStudent.id = student.id;
         dogovor.students.Add(dogStudent);

         editRow(dogovor);
         UpdateStudentGrid();

         controlObserver.Update();
      }

      private void dgvParents_DragDrop(object sender, DragEventArgs e)
      {
         Parent parent = (Parent)e.Data.GetData(typeof(Parent));

         if (parent == null)
            return;

         Dogovor dogovor = (Dogovor)dgvDogovor.CurrentRow.Tag;

         if (dogovor.parents == null)
            dogovor.parents = new List<DogParent>();

         DogParent dogParent = new DogParent();
         dogParent.id = parent.id;

         dogovor.parents.Add(dogParent);

         editRow(dogovor);
         UpdateParentGrid();
         controlObserver.Update();
      }

      private void dgvParents_DragEnter(object sender, DragEventArgs e)
      {
         if (dgvDogovor.Rows.Count == 0)
            e.Effect = DragDropEffects.None;
         else if (e.Data.GetData(typeof(Parent)) == null)
            e.Effect = DragDropEffects.None;
         else
            e.Effect = DragDropEffects.Copy;
      }

      private void UpdateStudentGrid()
      {
         dgvStudent.SuspendLayout();

         try
         {
            dgvStudent.Rows.Clear();

            if (dgvDogovor.Rows.Count == 0)
               return;

            Dogovor dogovor = (Dogovor)dgvDogovor.CurrentRow.Tag;

            if (dogovor == null || dogovor.students == null)
               return;

            foreach (DogStudent dogStudent in dogovor.students)
            {
               if (!dsStudent.ContainsKey(dogStudent.id))
                  continue;

               DataGridViewRow row = new DataGridViewRow();
               row.CreateCells(dgvStudent, dsStudent[dogStudent.id].name);
               row.Tag = dogStudent;
               dgvStudent.Rows.Add(row);
            }
         }
         finally
         {
            dgvStudent.ResumeLayout();
         }
      }

      private void UpdateParentGrid()
      {
         dgvParent.SuspendLayout();

         try
         {
            dgvParent.Rows.Clear();

            if (dgvDogovor.Rows.Count == 0)
               return;

            Dogovor dogovor = (Dogovor)dgvDogovor.CurrentRow.Tag;

            if (dogovor == null || dogovor.parents == null)
               return;

            foreach (DogParent dogParent in dogovor.parents)
            {
               if (!dsParent.ContainsKey(dogParent.id))
                  continue;

               DataGridViewRow row = new DataGridViewRow();
               row.CreateCells(dgvParent, dsParent[dogParent.id].name);
               row.Tag = dogParent;
               dgvParent.Rows.Add(row);
            }
         }
         finally
         {
            dgvParent.ResumeLayout();
         }
      }

      private void btnDelStudent_Click(object sender, EventArgs e)
      {
         if (!Dialogs.AllowedDelCurRow())
            return;

         Dogovor dogovor = (Dogovor)dgvDogovor.CurrentRow.Tag;

         if (dogovor == null || dogovor.students == null)
            return;

         DogStudent dogStudent = (DogStudent)dgvStudent.CurrentRow.Tag;
         dogovor.students.Remove(dogStudent);
         editRow(dogovor);
         dgvStudent.Rows.Remove(dgvStudent.CurrentRow);
         controlObserver.Update();
      }

      private void btnDelParent_Click(object sender, EventArgs e)
      {
         if (!Dialogs.AllowedDelCurRow())
            return;

         Dogovor dogovor = (Dogovor)dgvDogovor.CurrentRow.Tag;

         if (dogovor == null || dogovor.parents == null)
            return;

         DogParent dogParent = (DogParent)dgvParent.CurrentRow.Tag;
         dogovor.parents.Remove(dogParent);
         editRow(dogovor);
         dgvParent.Rows.Remove(dgvParent.CurrentRow);
         controlObserver.Update();
      }

      class DogovorMediator : ControlDbMediator
      {
         FmDogovor fmDogovor;

         public DogovorMediator(FmDogovor fmDogovor)
         {
            this.fmDogovor = fmDogovor;

            fmDogovor.btnAddDogovor.Enabled = false;
            fmDogovor.btnEditDogovor.Enabled = false;
            fmDogovor.btnDeleteDogovor.Enabled = false;
            fmDogovor.btnAddStudent.Enabled = false;
            fmDogovor.btnDelStudent.Enabled = false;
            fmDogovor.btnAddParent.Enabled = false;
            fmDogovor.btnDelParent.Enabled = false;
         }

         public override void Update()
         {
            bool canEdit = fmDogovor.dgvDogovor.Rows.Count > 0;

            fmDogovor.btnEditDogovor.Enabled = canEdit;
            fmDogovor.btnDeleteDogovor.Enabled = canEdit;
            fmDogovor.btnAddStudent.Enabled = canEdit;
            fmDogovor.btnAddParent.Enabled = canEdit;

            fmDogovor.btnDelStudent.Enabled = fmDogovor.dgvStudent.Rows.Count > 0;
            fmDogovor.btnDelParent.Enabled = fmDogovor.dgvParent.Rows.Count > 0;
         }

         public override void Open()
         {
            fmDogovor.btnAddDogovor.Enabled = true;
            base.Open();
         }
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshDataSets();
      }

      private void dgvDogovor_DoubleClick(object sender, EventArgs e)
      {
         Edit();
      }

      private void dgvDogovor_SelectionChanged(object sender, EventArgs e)
      {
         UpdateParentGrid();
         UpdateStudentGrid();

         controlObserver.Update();
      }
   }
}
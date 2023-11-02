/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма Школьные Предметы
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
   public partial class FmSchoolSubject : Form
   {
      private static FmSchoolSubject instance;
      private DsSchoolSubject dsSchoolSubject = DsSchoolSubject.GetDataSet();
      private SchoolSubjectMediator controlObserver;

      private FmSchoolSubject()
      {
         InitializeComponent();
         controlObserver = new SchoolSubjectMediator(this);
      }

      public static void ShowInstance()
      {
         if (instance == null)
         {
            instance = new FmSchoolSubject();
            instance.Show();
         }
         else
         {
            instance.Activate();
         }
      }

      private void FmSchoolSubject_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void FmSchoolSubject_Load(object sender, EventArgs e)
      {
         RefreshDataSets();
      }

      private void UpdateForm()
      {
         dgvSchoolSubject.SuspendLayout();
         try
         {
            dgvSchoolSubject.Rows.Clear();

            foreach (SchoolSubject ss in dsSchoolSubject.Data)
            {
               DataGridViewRow row = new DataGridViewRow();
               row.CreateCells(dgvSchoolSubject, ss.name, ss.secondName);
               row.Tag = ss;
               dgvSchoolSubject.Rows.Add(row);
            }

            controlObserver.Open();
         }
         finally
         {
            dgvSchoolSubject.ResumeLayout();
         }
      }

      private void RefreshDataSets()
      {
         DataModule.DataProcessed += RefreshRetrieveComlete;
         DataModule.OnDataResponceError += DataConnectionError;

         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsSchoolSubject);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(
            Config.Connection, list, FmWait.ProgressIndicator));
      }

      private void RefreshRetrieveComlete(object o, EventArgs e)
      {
         FmWait.CloseForm();
         ClearRegisterDataModuleEvents();
         Invoke(new InvokeDelegate(delegate { UpdateForm(); }));
      }

      private void ClearRegisterDataModuleEvents()
      {
         DataModule.OnDataResponceError -= DataConnectionError;
         DataModule.DataProcessed -= RefreshRetrieveComlete;
      }

      private void DataConnectionError(EDataResponse e)
      {
         ClearRegisterDataModuleEvents();
         MessageBox.Show(e.Msg);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshDataSets();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         SchoolSubject schoolSubject = new SchoolSubject();

         if (new FmSchoolSubjectEdit(schoolSubject).ShowDialog() == DialogResult.OK)
            insertRow(schoolSubject);

         controlObserver.Update();
      }

      private void insertRow(SchoolSubject schoolSubject)
      {
         DsSchoolSubject ds = DsSchoolSubject.GetDataSet(false);

         ds.Add(ds.Count, schoolSubject);
         List<IDataSet> listDS = new List<IDataSet>();
         listDS.Add(ds);

         if (DataModule.InsertDataSets(listDS, Config.Connection))
         {
            DataGridViewRow row = new DataGridViewRow();
            row.CreateCells(dgvSchoolSubject, schoolSubject.name, schoolSubject.secondName);
            row.Tag = schoolSubject;
            dgvSchoolSubject.Rows.Add(row);

            dsSchoolSubject.Add(schoolSubject.id, schoolSubject);
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         Edit();
      }

      private void Edit()
      {
         if (dgvSchoolSubject.CurrentRow == null)
            return;

         SchoolSubject ss = (SchoolSubject)dgvSchoolSubject.CurrentRow.Tag;

         if (new FmSchoolSubjectEdit(ss).ShowDialog() == DialogResult.OK)
            editRow(ss);

         controlObserver.Update();
      }

      private void editRow(SchoolSubject ss)
      {
         List<IDataSet> wrObj = new List<IDataSet>();
         wrObj.Add(dsSchoolSubject);

         if (DataModule.UpdateDataSet(wrObj, null, null, Config.Connection))
         {
            DataGridViewRow row = dgvSchoolSubject.CurrentRow;
            row.Cells[0].Value = ss.name;
            row.Cells[1].Value = ss.secondName;
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (dgvSchoolSubject.CurrentRow.Tag == null)
            return;

         if (!Dialogs.AllowedDelCurRow())
            return;

         List<IDataSet> rm = new List<IDataSet>();
         DsSchoolSubject toRem = DsSchoolSubject.GetDataSet(false);
         SchoolSubject ss = (SchoolSubject)dgvSchoolSubject.CurrentRow.Tag;
         toRem.Add(1, ss);
         rm.Add(toRem);

         if (DataModule.UpdateDataSet(null, rm, null, Config.Connection))
         {
            dsSchoolSubject.Remove(ss.id);
            dgvSchoolSubject.Rows.RemoveAt(dgvSchoolSubject.CurrentRow.Index);
         }

         controlObserver.Update();
      }

      class SchoolSubjectMediator : ControlDbMediator
      {
         private FmSchoolSubject fmSchoolSubject;

         public SchoolSubjectMediator(FmSchoolSubject fmSchoolSubject)
         {
            this.fmSchoolSubject = fmSchoolSubject;

            fmSchoolSubject.btnAdd.Enabled = false;
            fmSchoolSubject.btnEdit.Enabled = false;
            fmSchoolSubject.btnDel.Enabled = false;
         }

         public override void Update()
         {
            if (!isOpen())
               return;

            bool editAndDelEnable = fmSchoolSubject.dgvSchoolSubject.Rows.Count > 0;
            fmSchoolSubject.btnEdit.Enabled = editAndDelEnable;
            fmSchoolSubject.btnDel.Enabled = editAndDelEnable;
         }

         public override void Open()
         {
            fmSchoolSubject.btnAdd.Enabled = true;
            base.Open();
         }
      }

      private void dgvSchoolSubject_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Clicks == 2)
            Edit();

         Dialogs.SetRowCurrent((DataGridView)sender, e);
         DoDragDrop(dgvSchoolSubject.CurrentRow.Tag, DragDropEffects.Copy);
      }
   }  
}
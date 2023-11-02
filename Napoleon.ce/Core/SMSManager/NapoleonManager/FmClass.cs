/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма управления классами
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
   public partial class FmClass : Form
   {
      private static FmClass instance;
      private DsLocality dsLocality = DsLocality.GetDataSet();
      private DsSchoolEntity dsSchoolEntity = DsSchoolEntity.GetDataSet();
      private ClassMediator controlObserver;

      private FmClass()
      {
         InitializeComponent();
         controlObserver = new ClassMediator(this);
      }

      private void UpdateClassGrid()
      {
         dgvClass.Rows.Clear();

         SchoolEntity parent = ((SchoolItem)cbSchool.SelectedItem).entity;

         foreach (SchoolEntity entity in dsSchoolEntity.Data)
         {
            if (entity.parent == parent.id)
            {
               DataGridViewRow row = new DataGridViewRow();
               row.CreateCells(dgvClass, entity.number,
                  MakeContactsStr(entity.contacts));
               row.Tag = entity;
               dgvClass.Rows.Add(row);
            }
         }
      }

      public string MakeContactsStr(List<Contact> contacts)
      {
         StringBuilder sb = new StringBuilder();

         if (contacts == null)
            return String.Empty;

         foreach (Contact contact in contacts)
         {
            sb.Append(contact.name).Append(" ").
               Append(contact.phone).Append(" ").
               Append(contact.remark).Append("; ");
         }

         return sb.ToString();
      }

      public static void ShowInstance()
      {
         if (instance == null)
         {
            instance = new FmClass();
            instance.Show();
         }
         else
         {
            instance.Activate();
         }
      }

      private void FmClass_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void btnLocality_Click(object sender, EventArgs e)
      {
         FmLocality.ShowInstance();
      }

      private void btnShool_Click(object sender, EventArgs e)
      {
         FmSchool.ShowInstance();
      }

      private void RefreshDataSets()
      {
         DataModule.DataProcessed += RefreshRetrieveComlete;
         DataModule.OnDataResponceError += DataConnectionError;

         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsLocality);
         list.Add(dsSchoolEntity);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(
            Config.Connection, list, FmWait.ProgressIndicator));
      }

      private void FmClass_Load(object sender, EventArgs e)
      {
         RefreshDataSets();
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

      private void UpdateForm()
      {
         Dialogs.UpdateLocalityComboBox(cbLocality, dsLocality);

         if (cbLocality.SelectedItem != null)
            Dialogs.UpdateSchoolComboBox(cbLocality, cbSchool, dsSchoolEntity);

         controlObserver.Update();
      }

      private void cbLocality_SelectedIndexChanged(object sender, EventArgs e)
      {
         UpdateFormToNewLocality();

         if (cbLocality.SelectedItem != null)
            PermanentData.Data.LocalityID =
               ((LocalityItem)cbLocality.SelectedItem).locality.id;
      }

      private void UpdateFormToNewLocality()
      {
         cbSchool.Items.Clear();
         cbSchool.SelectedIndex = -1;
         cbSchool.Text = String.Empty;
         dgvClass.Rows.Clear();
         Dialogs.UpdateSchoolComboBox(cbLocality, cbSchool, dsSchoolEntity);
         controlObserver.Update();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         SchoolEntity schoolEntity = new SchoolEntity();
         SchoolEntity school = ((SchoolItem)cbSchool.SelectedItem).entity;
         Locality locality = ((LocalityItem)cbLocality.SelectedItem).locality;

         schoolEntity.locality = locality.id;
         schoolEntity.parent = school.id;

         if (new FmClassEdit(schoolEntity, school, locality).
               ShowDialog() == DialogResult.OK)
            insertRow(schoolEntity);

         controlObserver.Update();
      }

      private void insertRow(SchoolEntity schoolEntity)
      {
         DsSchoolEntity ds = DsSchoolEntity.GetDataSet(false);

         ds.Add(ds.Count, schoolEntity);
         List<IDataSet> listDS = new List<IDataSet>();
         listDS.Add(ds);
         if (DataModule.InsertDataSets(listDS, Config.Connection))
         {
            DataGridViewRow row = new DataGridViewRow();
            row.CreateCells(dgvClass, schoolEntity.number, MakeContactsStr(schoolEntity.contacts));
            row.Tag = schoolEntity;
            dgvClass.Rows.Add(row);

            dsSchoolEntity.Add(schoolEntity.id, schoolEntity); 
         }
      }

      private void cbShool_SelectedIndexChanged(object sender, EventArgs e)
      {
         UpdateClassGrid();
         controlObserver.Update();

         if (cbSchool.SelectedItem != null)
            PermanentData.Data.SchoolID = ((SchoolItem)cbSchool.SelectedItem).entity.id;

      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         Edit();
      }

      private void Edit()
      {
         if (dgvClass.CurrentRow == null)
            return;

         SchoolEntity school = ((SchoolItem)cbSchool.SelectedItem).entity;
         Locality locality = ((LocalityItem)cbLocality.SelectedItem).locality;

         SchoolEntity schoolEntity = (SchoolEntity)dgvClass.CurrentRow.Tag;

         if (new FmClassEdit(schoolEntity, school, locality).ShowDialog() == DialogResult.OK)
            editRow(schoolEntity);

         controlObserver.Update();
      }

      private void editRow(SchoolEntity schoolEntity)
      {
         List<IDataSet> wrObj = new List<IDataSet>();
         wrObj.Add(dsSchoolEntity);

         if (DataModule.UpdateDataSet(wrObj, null, null, Config.Connection))
         {
            DataGridViewRow row = dgvClass.CurrentRow;
            row.Cells[0].Value = schoolEntity.number;
            row.Cells[1].Value = MakeContactsStr(schoolEntity.contacts);
         }
      }

      class ClassMediator : ControlDbMediator
      {
         FmClass fmClass;

         public ClassMediator(FmClass fmClass)
         {
            this.fmClass = fmClass;

            fmClass.btnAdd.Enabled = false;
            fmClass.btnEdit.Enabled = false;
            fmClass.btnDel.Enabled = false;
         }

         public override void Update()
         {
            fmClass.btnAdd.Enabled = fmClass.cbSchool.SelectedItem != null;

            bool editAndDelEnable = fmClass.dgvClass.Rows.Count > 0;

            fmClass.btnEdit.Enabled = editAndDelEnable;
            fmClass.btnDel.Enabled = editAndDelEnable;
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (dgvClass.CurrentRow.Tag == null)
            return;

         if (!Dialogs.AllowedDelCurRow())
            return;

         List<IDataSet> rm = new List<IDataSet>();
         DsSchoolEntity toRem = DsSchoolEntity.GetDataSet(false);
         SchoolEntity se = (SchoolEntity)dgvClass.CurrentRow.Tag;
         toRem.Add(1, se);
         rm.Add(toRem);

         if (DataModule.UpdateDataSet(null, rm, null, Config.Connection))
         {
            dsSchoolEntity.Remove(se.id);
            dgvClass.Rows.RemoveAt(dgvClass.CurrentRow.Index);
         }

         controlObserver.Update();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         UpdateFormToNewLocality();
         RefreshDataSets();
      }

      private void dgvClass_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Clicks == 2)
            Edit();

         List<SchoolEntity> list = new List<SchoolEntity>();

         foreach (DataGridViewRow row in dgvClass.SelectedRows)
            list.Add((SchoolEntity)row.Tag);

         DoDragDrop(list, DragDropEffects.Copy);
      }
   }

   class SchoolItem
   {
      public SchoolEntity entity;

      public SchoolItem(SchoolEntity entity)
      {
         this.entity = entity;
      }

      public override string ToString()
      {
         return entity.number;
      }

      public override bool Equals(object item)
      {
         return entity.id == ((SchoolItem)item).entity.id;
      }

      public override int GetHashCode()
      {
         return base.GetHashCode();
      }
   }
}
/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма управления школами
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
   public partial class FmSchool : Form
   {

      private DsLocality dsLocality = DsLocality.GetDataSet();
      private DsSchoolEntity dsSchoolEntity = DsSchoolEntity.GetDataSet();
      private static FmSchool instance;
      private SchoolMediator controlObserver;

      private FmSchool()
      {
         InitializeComponent();

         controlObserver = new SchoolMediator(this);
      }

      public static void ShowInstance()
      {
         if (instance == null)
         {
            instance = new FmSchool();
            instance.Show();
         }
         else
         {
            instance.Activate();
         }
      }

      private bool SelectLocality(object item)
      {
         if (item == null)
         {
            cbLocality.SelectedIndex = -1;
            return false;
         }

         foreach(object i in cbLocality.Items)
            if (i.Equals(item))
            {
               cbLocality.SelectedItem = i;
               return true;
            }

         cbLocality.SelectedIndex = -1;
         return false;
      }

      private void btnLocality_Click(object sender, EventArgs e)
      {
         FmLocality.ShowInstance();
         RefreshLocality();
      }

      private void RefreshLocality()
      {
         DataModule.OnDataResponceError += DataConnectionError;
         DataModule.DataProcessed += LocalityRetrieveComlete;

         dsLocality.Clear();
         DataModule.RefreshDataSet(dsLocality, Config.Connection, false, null);
      }

      private void LocalityRetrieveComlete(object o, EventArgs e)
      {
         ClearRegisterDataModuleEvents();
         Invoke(new InvokeDelegate(delegate { 
            Dialogs.UpdateLocalityComboBox(cbLocality, dsLocality); }));
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         SchoolEntity schoolEntity = new SchoolEntity();
         if (FmSchoolEdit.ShowDialog(schoolEntity) == DialogResult.OK)
         {
            schoolEntity.locality = ((LocalityItem)cbLocality.
               Items[cbLocality.SelectedIndex]).locality.id;

            schoolEntity.parent = SchoolEntity.SHOOL_PARENT;
            insertRow(schoolEntity);
         }

         controlObserver.Update();
      }

      private void insertRow(SchoolEntity schoolEntity)
      {
         DataGridViewRow row = new DataGridViewRow();
         row.CreateCells(dgvSchool, schoolEntity.number,  schoolEntity.address);
         row.Tag = schoolEntity;
         dgvSchool.Rows.Add(row);

         DsSchoolEntity ds = DsSchoolEntity.GetDataSet(false);

         ds.Add(ds.Count, schoolEntity);
         List<IDataSet> listDS = new List<IDataSet>();
         listDS.Add(ds);
         DataModule.InsertDataSets(listDS, Config.Connection);

         dsSchoolEntity.Add(schoolEntity.id, schoolEntity);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
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

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(
            Config.Connection, list, FmWait.ProgressIndicator));
      }

      private void DataConnectionError(EDataResponse e)
      {
         ClearRegisterDataModuleEvents();
         MessageBox.Show(e.Msg);
      }

      private void RefreshRetrieveComlete(object o, EventArgs e)
      {
         ClearRegisterDataModuleEvents();
         Invoke(new InvokeDelegate(delegate { UpdateForm(); }));
      }

      private void UpdateForm()
      {
         Dialogs.UpdateLocalityComboBox(cbLocality, dsLocality);
         UpdateGrid();
         controlObserver.Open();
      }

      private void UpdateGrid()
      {
         dgvSchool.Rows.Clear();

         if ((LocalityItem)cbLocality.SelectedItem != null)
         {
            int selectedLocality = ((LocalityItem)cbLocality.SelectedItem).locality.id;
            foreach (SchoolEntity entity in dsSchoolEntity.Data)
            {
               if (entity.locality == selectedLocality &&
                  entity.parent == SchoolEntity.SHOOL_PARENT)
               {
                  DataGridViewRow row = new DataGridViewRow();
                  row.CreateCells(dgvSchool, entity.number, entity.address);
                  row.Tag = entity;
                  dgvSchool.Rows.Add(row);
               }
            }
         }
      }

      private void ClearRegisterDataModuleEvents()
      {
         FmWait.CloseForm();
         DataModule.OnDataResponceError -= DataConnectionError;
         DataModule.DataProcessed -= RefreshRetrieveComlete;
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         Edit();
      }

      private void Edit()
      {
         if (dgvSchool.CurrentRow == null)
            return;

         SchoolEntity schoolEntity = (SchoolEntity)dgvSchool.CurrentRow.Tag;
         schoolEntity.locality = ((LocalityItem)cbLocality.SelectedItem).locality.id;
         schoolEntity.parent = SchoolEntity.SHOOL_PARENT;

         if (FmSchoolEdit.ShowDialog(schoolEntity) == DialogResult.OK)
            editRow(schoolEntity);

         controlObserver.Update();
      }

      private void editRow(SchoolEntity schoolEntity)
      {
         List<IDataSet> wrObj = new List<IDataSet>();
         wrObj.Add(dsSchoolEntity);

         if (DataModule.UpdateDataSet(wrObj, null, null, Config.Connection))
         {
            DataGridViewRow row = dgvSchool.CurrentRow;
            row.Cells[0].Value = schoolEntity.number;
            row.Cells[1].Value = schoolEntity.address;
         }
      }

      private void FmSchool_Load(object sender, EventArgs e)
      {
         RefreshDataSets();
      }

      private void cbLocality_SelectedIndexChanged(object sender, EventArgs e)
      {
         UpdateGrid();
         controlObserver.Update();

         if (cbLocality.SelectedItem != null)
            PermanentData.Data.LocalityID = 
               ((LocalityItem)cbLocality.SelectedItem).locality.id;
      }

      private void FmSchool_FormClosing(object sender, FormClosingEventArgs e)
      {
         instance = null;
      }

      class SchoolMediator : ControlDbMediator
      {
         FmSchool fmSchool;

         public SchoolMediator(FmSchool fmSchool)
         {
            this.fmSchool = fmSchool;

            fmSchool.btnAdd.Enabled = false;
            fmSchool.btnEdit.Enabled = false;
            fmSchool.btnDel.Enabled = false;
         }

         public override void Update()
         {
            if (!isOpen())
               return;

            fmSchool.btnAdd.Enabled = fmSchool.cbLocality.SelectedItem != null;
            bool editAndDelEnable = fmSchool.dgvSchool.Rows.Count > 0;
            
            fmSchool.btnEdit.Enabled = editAndDelEnable;
            fmSchool.btnDel.Enabled = editAndDelEnable;
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (dgvSchool.CurrentRow.Tag == null)
            return;

         if (!Dialogs.AllowedDelCurRow())
            return;

         List<IDataSet> rm = new List<IDataSet>();
         DsSchoolEntity toRem = DsSchoolEntity.GetDataSet(false);
         SchoolEntity se = (SchoolEntity)dgvSchool.CurrentRow.Tag;
         toRem.Add(1, se);
         rm.Add(toRem);

         if (DataModule.UpdateDataSet(null, rm, null, Config.Connection))
         {
            dsSchoolEntity.Remove(se.id);
            dgvSchool.Rows.RemoveAt(dgvSchool.CurrentRow.Index);
         }

         controlObserver.Update();
      }

      private void dgvSchool_MouseDoubleClick(object sender, MouseEventArgs e)
      {
         Edit();
      }
   }

   class LocalityItem
   {
      public Locality locality;

      public LocalityItem(Locality locality)
      {
         this.locality = locality;
      }

      public override string ToString()
      {
         return locality.name;
      }

      public override bool Equals(object item)
      {
         return locality.id == ((LocalityItem)item).locality.id;
      }

      public override int GetHashCode()
      {
         return base.GetHashCode();
      }
   }
}
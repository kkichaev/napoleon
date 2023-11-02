/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма управления городами
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
   public partial class FmLocality : Form
   {
      private DsLocality dsLocality = DsLocality.GetDataSet();
      private LocalityMediator controlObserver;
      private static FmLocality instance;

      private FmLocality()
      {
         InitializeComponent();
         Init();
      }

      private void Init()
      {
         controlObserver = new LocalityMediator(this);
         controlObserver.Update();
      }

      public static void ShowInstance()
      {
         if (instance == null)
         {
            instance = new FmLocality();
            instance.Show();
         }
         else
         {
            instance.Activate();
         }
         
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         Locality locality = new Locality();

         if (FmLocalityEdit.ShowDialog(locality) == DialogResult.OK)
            insertRow(locality);

         controlObserver.Update();
      }

      private void insertRow(Locality locality)
      {
         DataGridViewRow row = new DataGridViewRow();
         row.CreateCells(dgvLocality, locality.name);
         row.Tag = locality;
         dgvLocality.Rows.Add(row);
        
         DsLocality ds = DsLocality.GetDataSet(false);
         ds.Add(ds.Count, locality);
         List<IDataSet> listDS = new List<IDataSet>();
         listDS.Add(ds);
         DataModule.InsertDataSets(listDS, Config.Connection);

         dsLocality.Add(locality.id, locality);
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         Edit();
      }

      private void Edit()
      {
         Locality locality = (Locality)dgvLocality.CurrentRow.Tag;

         if (FmLocalityEdit.ShowDialog(locality) == DialogResult.OK)
            editRow(locality);

         controlObserver.Update();
      }

      private void editRow(Locality locality)
      {
         List<IDataSet> wrObj = new List<IDataSet>();
         wrObj.Add(dsLocality);
         if (DataModule.UpdateDataSet(wrObj, null, null, Config.Connection))
         {
            DataGridViewRow row = dgvLocality.CurrentRow;
            row.Cells[0].Value = locality.name;
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (dgvLocality.CurrentRow.Tag == null)
            return;

         if (!Dialogs.AllowedDelCurRow())
            return;

         List<IDataSet> rm = new List<IDataSet>();
         DsLocality toRem = DsLocality.GetDataSet(false);
         Locality loc = (Locality)dgvLocality.CurrentRow.Tag;
         toRem.Add(1, loc);
         rm.Add(toRem);

         if (DataModule.UpdateDataSet(null, rm, null, Config.Connection))
         {
            dsLocality.Remove(loc.id);
            dgvLocality.Rows.RemoveAt(dgvLocality.CurrentRow.Index);
         }

         controlObserver.Update();
      }

      private void btnUpdate_Click(object sender, EventArgs e)
      {
         RefreshDataSets();
      }

      private void RefreshDataSets()
      {
         DataModule.DataProcessed += RefreshRetrieveComlete;
         DataModule.OnDataResponceError += DataConnectionError;

         FmWait.ShowForm(this, DataModule.RefreshDataSet(
            dsLocality, Config.Connection, false, FmWait.ProgressIndicator));
      }

      private void ClearRegisterDataModuleEvents()
      {
         FmWait.CloseForm();
         DataModule.OnDataResponceError -= DataConnectionError;
         DataModule.DataProcessed -= RefreshRetrieveComlete;
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

      private void UpdateForm()
      {
         dgvLocality.SuspendLayout();

         try
         {
            dgvLocality.Rows.Clear();

            foreach (Locality locality in dsLocality.Data)
            {
               DataGridViewRow row = new DataGridViewRow();
               row.CreateCells(dgvLocality, locality.name);
               row.Tag = locality;
               dgvLocality.Rows.Add(row);
            }

            controlObserver.Open();
         }
         finally
         {
            dgvLocality.ResumeLayout();
         }
      }

      private void FmLocality_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void FmLocality_Load(object sender, EventArgs e)
      {
         RefreshDataSets();
      }

      /// <summary>
      /// Класс управляет состоянием визальных компонентов
      /// в зависимости от состояния набора базы данных
      /// </summary>
      class LocalityMediator : ControlDbMediator
      {
         FmLocality fmLocality;

         public LocalityMediator(FmLocality fmLocality)
         {
            this.fmLocality = fmLocality;

            fmLocality.btnAdd.Enabled = false;
            fmLocality.btnDel.Enabled = false;
            fmLocality.btnEdit.Enabled = false;
         }

         public override void Update()
         {
            if (!isOpen())
               return;

            bool rows = fmLocality.dgvLocality.Rows.Count > 0;
            fmLocality.btnEdit.Enabled = rows;
            fmLocality.btnDel.Enabled = rows;
         }

         public override void Open()
         {
            fmLocality.btnAdd.Enabled = true;
            base.Open();
         }
      }

      private void dgvLocality_MouseDoubleClick(object sender, MouseEventArgs e)
      {
         Edit();
      }
   }
}
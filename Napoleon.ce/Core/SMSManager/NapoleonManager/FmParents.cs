/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма Родители учеников
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
   public partial class FmParents : Form
   {
      private static FmParents instance;
      private DsParent dsParent = DsParent.GetDataSet();
      private ParentMediator controlObserver;

      private FmParents()
      {
         InitializeComponent();
         Init();
      }

      private void Init()
      {
         controlObserver = new ParentMediator(this);
      }

      public static void ShowInstatnce()
      {
         if (instance == null)
         {
            instance = new FmParents();
            instance.Show();
         }
         else
            instance.Activate();
      }

      private void FmParents_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private void RefreshDataSets()
      {
         DataModule.OnDataResponceError += DataConnectionError;
         DataModule.DataProcessed += RefreshRetrieveComlete;

         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsParent);

         FmWait.ShowForm(this, DataModule.RefreshGiveSets(
            Config.Connection, list, FmWait.ProgressIndicator));
      }

      private void RefreshRetrieveComlete(object o, EventArgs e)
      {
         ClearRegisterDataModuleEvents();
         Invoke(new InvokeDelegate(delegate { UpdateForm(); }));
      }

      private void UpdateForm()
      {
         dgvParents.SuspendLayout();

         try
         {
            dgvParents.Rows.Clear();

            foreach (Parent parent in dsParent.Data)
            {
               DataGridViewRow row = new DataGridViewRow();
               row.CreateCells(dgvParents, parent.name, MakePhonesStr(parent));
               row.Tag = parent;
               dgvParents.Rows.Add(row);
            }

            controlObserver.Open();
         }
         finally
         {
            dgvParents.ResumeLayout();
         }
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

      private void FmParents_Load(object sender, EventArgs e)
      {
         RefreshDataSets();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         Parent parent = new Parent();

         if (new FmParentsEdit(parent).ShowDialog()
            == DialogResult.OK)
            insertRow(parent);

         controlObserver.Update();
      }

      private void insertRow(Parent parent)
      {
         DsParent ds = DsParent.GetDataSet(false);

         ds.Add(ds.Count, parent);
          List<IDataSet> listDS = new List<IDataSet>();
         listDS.Add(ds);

         if (DataModule.InsertDataSets(listDS, Config.Connection))
         {
            DataGridViewRow row = new DataGridViewRow();
            row.CreateCells(dgvParents, parent.name, MakePhonesStr(parent));
            row.Tag = parent;
            dgvParents.Rows.Add(row);

            dsParent.Add(parent.id, parent);
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         Edit();
      }

      private void Edit()
      {
         Parent parent = (Parent)dgvParents.CurrentRow.Tag;

         if (new FmParentsEdit(parent).ShowDialog()
            == DialogResult.OK)
            editRow(parent);

         controlObserver.Update();
      }

      private void editRow(Parent parent)
      {
         List<IDataSet> wrObj = new List<IDataSet>();
         wrObj.Add(dsParent);

         if (DataModule.UpdateDataSet(wrObj, null, null, Config.Connection))
         {
            DataGridViewRow row = dgvParents.CurrentRow;
            row.Cells[0].Value = parent.name;
            row.Cells[1].Value = MakePhonesStr(parent);
         }
      }

      private string MakePhonesStr(Parent parent)
      {
         if (parent.phones == null)
            return String.Empty;

         StringBuilder sb = new StringBuilder();

         foreach (Phone phone in parent.phones)
            sb.Append(phone.phone).Append("; ");

         return sb.ToString();
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (!Dialogs.AllowedDelCurRow())
            return;

         List<IDataSet> rm = new List<IDataSet>();
         DsParent toRem = DsParent.GetDataSet(false);
         Parent p = (Parent)dgvParents.CurrentRow.Tag;
         toRem.Add(1, p);
         rm.Add(toRem);

         if (DataModule.UpdateDataSet(null, rm, null, Config.Connection))
         {
            dsParent.Remove(p.id);
            dgvParents.Rows.RemoveAt(dgvParents.CurrentRow.Index);
         }

         controlObserver.Update();
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         RefreshDataSets();
      }

      private void dgvParents_MouseDown(object sender, MouseEventArgs e)
      {
         if (e.Clicks == 2)
            Edit();

         Dialogs.SetRowCurrent((DataGridView)sender, e);
         DoDragDrop(dgvParents.CurrentRow.Tag, DragDropEffects.Copy);
      }

      class ParentMediator : ControlDbMediator
      {
         FmParents fmParents;

         public ParentMediator(FmParents fmParents)
         {
            this.fmParents = fmParents;
         }

         public override void Update()
         {
            bool editAndDelEnable = fmParents.dgvParents.Rows.Count > 0;

            fmParents.btnEdit.Enabled = editAndDelEnable;
            fmParents.btnDel.Enabled = editAndDelEnable;
         }

         public override void Open()
         {
            fmParents.btnAdd.Enabled = true;
            base.Open();
         }
      }
   }
}
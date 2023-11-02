using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmReturnCauseEditor : Form
   {
      bool canSave = false;
      SimpleDataSet<ReturnCause> dsCause = new SimpleDataSet<ReturnCause>(ReturnCause.OBJECT_NAME, false);
      SimpleDataSet<ReturnCause> removed = new SimpleDataSet<ReturnCause>(ReturnCause.OBJECT_NAME, false);
      DataSet<string, PriceType> dsTypes;

      public FmReturnCauseEditor()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;

         dsTypes = (DataSet<string, PriceType>)DataModule.Get(PriceType.OBJECT_NAME) ??
            new DataSet<string, PriceType>(PriceType.OBJECT_NAME, true);

         Manager m = CurrentUser.user as Manager;
         if (m != null)
         {
            canSave = true; // m.HaveRight(RightTokens.Get("ReturnEditRigth"), RightActions.Write);
            tsbAdd.Enabled = canSave;
            tsbRemove.Enabled = canSave;

            clmnName.ReadOnly = !canSave;
            clmnNeedPhoto.ReadOnly = !canSave;
         }
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         LoadData();
      }

      private void LoadData()
      {
         List<IDataSet> sets = new List<IDataSet>();

         if (dsTypes.Count == 0)
            sets.Add(dsTypes);
         sets.Add(dsCause);

         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), sets, null).Join();

         List<ReturnCause> data = new List<ReturnCause>();
         foreach (ReturnCause rc in dsCause.Data)
         {
            data.Add(rc);
            rc.Owner = this;
         }

         List<PriceType> src = new List<PriceType>();
         foreach (PriceType pt in dsTypes.Data)
            src.Add(pt);
         clmnType.DataSource = src;
         clmnType.ValueMember = "ID";
         clmnType.DisplayMember = "Name";

         //clmnType.Items.Clear();
         //foreach (PriceType pt in dsTypes.Data)
         //   clmnType.Items.Add(pt);

         dgvItems.DataSource = new BindingList<ReturnCause>(data);

         tsbSave.Enabled = false;
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      bool CheckChanges()
      {
         if (!tsbSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      private bool SaveChanges(bool showDialog)
      {
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);

         SimpleDataSet<ReturnCause> wrset = new SimpleDataSet<ReturnCause>(ReturnCause.OBJECT_NAME, false);

         foreach (ReturnCause ri in (BindingList<ReturnCause>)dgvItems.DataSource)
         {
            wrset.Add(ri);
         }

         List<IDataSet> wr = new List<IDataSet>();
         wr.Add(wrset);
         
         List<IDataSet> rmv = new List<IDataSet>();
         if (removed.Count > 0)
            rmv.Add(removed);

         bool ret = DataModule.UpdateDataSet(wr, rmv, null, Config.GetConfig().GetConnection());
         if (ret)
         {
            removed.Clear();
            dsCause = wrset;
         }


         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }
         return ret;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }

      public void SetDirty()
      {
         tsbSave.Enabled = canSave && true;
      }

      private void tsbAdd_Click(object sender, EventArgs e)
      {
         ReturnCause newItem = ((BindingList<ReturnCause>)dgvItems.DataSource).AddNew();
         newItem.id = Guid.NewGuid().ToString().Replace("-", "");
         SetDirty();
      }

      private void tsbRemove_Click(object sender, EventArgs e)
      {
         if (dgvItems.CurrentRow == null)
            return;

         ReturnCause rc = (ReturnCause)dgvItems.CurrentRow.DataBoundItem;
         removed.Add(rc);
         ((BindingList<ReturnCause>)dgvItems.DataSource).Remove(rc);
         SetDirty();
      }

      private void dgvItems_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if (dgvItems.CurrentCell.ColumnIndex == clmnNeedPhoto.DisplayIndex)
         {
            dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
            SetDirty();
         }
      }

      private void dgvItems_CellEndEdit(object sender, DataGridViewCellEventArgs e)
      {
         SetDirty();
      }
   }
}

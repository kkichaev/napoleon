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
   public partial class FmFocusRejectReason : Form
   {
      DataSet<string, FocusRejectReason> dsReasons = new DataSet<string, FocusRejectReason>(FocusRejectReason.OBJECT_NAME, false);
      public FmFocusRejectReason()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsReasons);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);

         if (!CheckChanges())
            e.Cancel = true;
      }

      bool CheckChanges()
      {
         if (!btnSave.Enabled)
            return true;

         DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
         if (dr == DialogResult.No)
            return true;
         if (dr == DialogResult.Cancel)
            return false;

         return SaveChanges(false);
      }

      void DoLoadData()
      {
         List<FocusRejectReason> dsrc = new List<FocusRejectReason>(dsReasons.Values);
         SortableBindingList<FocusRejectReason> src = new SortableBindingList<FocusRejectReason>(dsrc);
         dgvItems.DataSource = src;
         btnSave.Enabled = false;
      }

      bool SaveChanges(bool showDialog)
      {
         List<ReplacedSet> rpl = new List<ReplacedSet>();
         SimpleDataSet<FocusRejectReason> upd = new SimpleDataSet<FocusRejectReason>(FocusRejectReason.OBJECT_NAME, false);
         SortableBindingList<FocusRejectReason> src = (SortableBindingList<FocusRejectReason>)dgvItems.DataSource;
         foreach (FocusRejectReason i in src)
            upd.Add(i);

         rpl.Add(new ReplacedSet(upd));
         bool ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());
         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи");

         return ret;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
         btnSave.Enabled = !SaveChanges(true);
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if(dgvItems.CurrentRow != null)
         {
            FocusRejectReason o = dgvItems.CurrentRow.DataBoundItem as FocusRejectReason;
            SortableBindingList<FocusRejectReason> src = (SortableBindingList<FocusRejectReason>)dgvItems.DataSource;
            src.Remove(o);
         }

         btnSave.Enabled = true;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         SortableBindingList<FocusRejectReason> src = (SortableBindingList<FocusRejectReason>)dgvItems.DataSource;
         FocusRejectReason n = src.AddNew();
         n.id = Guid.NewGuid().ToString().Replace("-", "");

         btnSave.Enabled = true;
      }

      private void dgvItems_CellValueChanged(object sender, DataGridViewCellEventArgs e)
      {
         btnSave.Enabled = true;
      }
   }
}

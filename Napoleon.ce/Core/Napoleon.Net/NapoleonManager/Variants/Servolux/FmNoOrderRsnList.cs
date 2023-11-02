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
   public partial class FmNoOrderRsnList : Form
   {
      SimpleDataSet<NoOrderReason> data = new SimpleDataSet<NoOrderReason>(NoOrderReason.OBJECT_NAME, false);
      public FmNoOrderRsnList()
      {
         InitializeComponent();

         dgvItems.AutoGenerateColumns = false;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         LoadData();
      }

      private void LoadData()
      {
         data.Clear();
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(data);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }
      
      void DoLoadData()
      {
         List<NoOrderReason> src = new List<NoOrderReason>();
         foreach (NoOrderReason i in data.Data)
            src.Add(i);

         data.Clear();
         dgvItems.DataSource = new SortableBindingList<NoOrderReason>(src);
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

         List<ReplacedSet> rpl = new List<ReplacedSet>();
         SimpleDataSet<NoOrderReason> wr = new SimpleDataSet<NoOrderReason>(NoOrderReason.OBJECT_NAME, false);
         foreach (NoOrderReason i in ((SortableBindingList<NoOrderReason>)dgvItems.DataSource))
            wr.Add(i);

         ReplacedSet rs = new ReplacedSet(wr);
         rpl.Add(rs);
         bool ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());
         if (ret)
            data = wr;


         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }
         return ret;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         LoadData();
      }

      private void tsbAdd_Click(object sender, EventArgs e)
      {
         SortableBindingList<NoOrderReason> src = (SortableBindingList<NoOrderReason>)dgvItems.DataSource;
         NoOrderReason nor = src.AddNew();
         nor.id = Guid.NewGuid().ToString().Replace("-", "");

         int idx = src.IndexOf(nor);
         dgvItems.CurrentCell = dgvItems.Rows[idx].Cells[0];
         dgvItems.BeginEdit(true);

         tsbSave.Enabled = true;
      }

      private void tsbRemove_Click(object sender, EventArgs e)
      {
         if (dgvItems.CurrentRow == null)
            return;

         NoOrderReason item = dgvItems.CurrentRow.DataBoundItem as NoOrderReason;
         SortableBindingList<NoOrderReason> src = (SortableBindingList<NoOrderReason>)dgvItems.DataSource;
         src.Remove(item);

         tsbSave.Enabled = true;
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }
   }
}

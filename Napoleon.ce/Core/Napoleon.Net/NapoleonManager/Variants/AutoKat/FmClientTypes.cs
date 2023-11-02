using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmClientTypes : Form
   {
      SimpleDataSet<ClientType> dsItems = new SimpleDataSet<ClientType>(ClientType.OBJECT_NAME, false);
      public FmClientTypes()
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
         List<IDataSet> sets = new List<IDataSet>();

         sets.Add(dsItems);

         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), sets, null).Join();

         List<ClientType> src = new List<ClientType>();
         foreach (ClientType i in dsItems.Data)
            src.Add(i);

         src.Sort();

         dgvItems.DataSource = new BindingList<ClientType>(src);
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

         SimpleDataSet<ClientType> wrset = new SimpleDataSet<ClientType>(ClientType.OBJECT_NAME, false);

         int pos = 0;
         foreach (ClientType ri in (BindingList<ClientType>)dgvItems.DataSource)
         {
            ri.pos = pos++;
            wrset.Add(ri);
         }

         ReplacedSet rs = new ReplacedSet(wrset);
         List<ReplacedSet> rplSet = new List<ReplacedSet>();
         rplSet.Add(rs);


         bool ret = DataModule.UpdateDataSet(null, null, rplSet, Config.GetConfig().GetConnection());
         if (showDialog)
         {
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
         }
         return ret;
      }

      public void SetDirty()
      {
         tsbSave.Enabled = true;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         BindingList<ClientType> src = (BindingList<ClientType>)dgvItems.DataSource;
         ClientType pt = src.AddNew();
         pt.id = Guid.NewGuid().ToString().Replace("-", "");

         SetDirty();
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         BindingList<ClientType> src = (BindingList<ClientType>)dgvItems.DataSource;
         DataGridViewRow r = dgvItems.CurrentRow;
         if(r != null)
         {
            src.Remove((ClientType)r.DataBoundItem);
            SetDirty();
         }
      }

      void ShiftCurItem(bool up)
      {
         BindingList<ClientType> src = (BindingList<ClientType>)dgvItems.DataSource;
         DataGridViewRow cur = dgvItems.CurrentRow;
         if (cur == null)
            return;

         int index = cur.Index;
         ClientType el = src[index];
         if (up)
         {
            if (index == 0)
               return;
            index--;
         }
         else
         {
            if (index >= src.Count - 1)
               return;
            index++;
         }
         src.Remove(el);
         src.Insert(index, el);
         dgvItems.CurrentCell = dgvItems.Rows[index].Cells[0];
         SetDirty();
      }

      private void tsbUp_Click(object sender, EventArgs e)
      {
         ShiftCurItem(true);
      }

      private void tsbDn_Click(object sender, EventArgs e)
      {
         ShiftCurItem(false);
      }

      private void tsbSave_Click_1(object sender, EventArgs e)
      {
         if (SaveChanges(true))
            tsbSave.Enabled = false;
      }
   }
}

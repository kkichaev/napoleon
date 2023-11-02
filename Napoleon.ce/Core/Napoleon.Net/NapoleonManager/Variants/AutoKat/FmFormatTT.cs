using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmFormatTT : Form
   {
      SimpleDataSet<FormatTT> dsItems = new SimpleDataSet<FormatTT>(FormatTT.OBJECT_NAME, false);

      public FmFormatTT()
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

         List<FormatTT> src = new List<FormatTT>();
         foreach (FormatTT i in dsItems.Data)
            src.Add(i);

         src.Sort();

         dgvItems.DataSource = new BindingList<FormatTT>(src);
         tsbSave.Enabled = false;
      }

      private void LoadItems(FormatTT ftt)
      {
         if (ftt == null)
            return;

         List<FormatTT.Item> src = new List<FormatTT.Item>();

         foreach (FormatTT.Item i in ftt.items)
            src.Add(i);

         src.Sort();

         grid.DataSource = new BindingList<FormatTT.Item>(src);
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

      private void CommitGridChages()
      {
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);
         grid.CommitEdit(DataGridViewDataErrorContexts.Commit);
      }

      private bool SaveChanges(bool showDialog)
      {
         CommitGridChages();
         SimpleDataSet<FormatTT> wrset = new SimpleDataSet<FormatTT>(FormatTT.OBJECT_NAME, false);

         int pos = 0;
         foreach (FormatTT ri in (BindingList<FormatTT>)dgvItems.DataSource)
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
         BindingList<FormatTT> src = (BindingList<FormatTT>)dgvItems.DataSource;
         FormatTT pt = src.AddNew();
         pt.id = Guid.NewGuid().ToString().Replace("-", "");

         SetDirty();
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         BindingList<FormatTT> src = (BindingList<FormatTT>)dgvItems.DataSource;
         DataGridViewRow r = dgvItems.CurrentRow;
         if(r != null)
         {
            src.Remove((FormatTT)r.DataBoundItem);
            SetDirty();
         }
      }

      void ShiftCurItem(DataGridView gr, bool up)
      {
         IList src = (IList)gr.DataSource;
         DataGridViewRow cur = gr.CurrentRow;
         if (cur == null)
            return;

         int index = cur.Index;
         System.Object el = cur.DataBoundItem;

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
         gr.CurrentCell = gr.Rows[index].Cells[0];

         SetDirty();

         for (int i = 0; i < src.Count; i++)((PayType)src[i]).pos = i;
      }

      private void tsbUp_Click(object sender, EventArgs e)
      {
         ShiftCurItem(dgvItems, true);
      }

      private void tsbDn_Click(object sender, EventArgs e)
      {
         ShiftCurItem(dgvItems, false);
      }

      private void tsbSave_Click_1(object sender, EventArgs e)
      {
         if (SaveChanges(true))
            tsbSave.Enabled = false;
      }

      private void btnAddItem_Click(object sender, EventArgs e)
      {
         CommitGridChages();

         if (!(dgvItems.CurrentRow?.DataBoundItem is FormatTT ftt))
            return;

         BindingList<FormatTT.Item> src = (BindingList<FormatTT.Item>)grid.DataSource;

         if (src == null)
         {
            src  = new BindingList<FormatTT.Item>();
            grid.DataSource = src;
         }

         FormatTT.Item pt = src.AddNew();
         pt.id = Guid.NewGuid().ToString().Replace("-", "");
         pt.pos = src.Count;

         ftt.items.Add(pt);
      }

      private void dgvItems_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         LoadItems((FormatTT)((DataGridView)sender).Rows[e.RowIndex].DataBoundItem);
      }

      private void btUpItem_Click(object sender, EventArgs e)
      {
         ShiftCurItem(grid, true);
      }

      private void btnDownItem_Click(object sender, EventArgs e)
      {
         ShiftCurItem(grid, false);
      }

      private void btnDelItem_Click(object sender, EventArgs e)
      {
         BindingList<FormatTT.Item> src = (BindingList<FormatTT.Item>)grid.DataSource;
         DataGridViewRow r = dgvItems.CurrentRow;
         if (r != null)
         {
            src.Remove((FormatTT.Item)r.DataBoundItem);
            SetDirty();

            for (int i = 0; i < src.Count; i++) ((PayType)src[i]).pos = i;
         }
      }
   }
}

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
   public partial class FmDistribGroupEditor : Form
   {
      static FmDistribGroupEditor instance = null;
      DataSet<string, DistribGroup> dsGroups = new DataSet<string, DistribGroup>(DistribGroup.OBJECT_NAME, false);
      DataSet<string, Price> dsPrice;
      SimpleDataSet<DistribGroup> dsRemoved = new SimpleDataSet<DistribGroup>(DistribGroup.OBJECT_NAME, false);

      public FmDistribGroupEditor()
      {
         InitializeComponent();
         
         dgvGroups.AutoGenerateColumns = false;
         dgvItems.AutoGenerateColumns = false;

         dsPrice = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ?? new DataSet<string, Price>(Price.OBJECT_NAME);
         dsGroups.Filter = "not id is null";
      }

      public static void Open()
      {
         if (instance == null)
         {
            instance = new FmDistribGroupEditor();
            instance.Show();
         }
         else
         {
            instance.RefreshData(false);
            instance.BringToFront();
         }
      }
      protected override void OnClosed(EventArgs e)
      {
         instance = null;
         base.OnClosed(e);
      }

      public void Changed(DistribGroup item)
      {
         btnSave.Enabled = true;
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData(false);
      }

      private void RefreshData(bool forceReload)
      {
         List<IDataSet> upd = new List<IDataSet>();
         if(dsPrice.Count == 0 || forceReload)
         {
            dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
            upd.Add(dsPrice);
         }
         upd.Add(dsGroups);
         FmWait.StdDataRefresh(this, upd, LoadData);
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         base.OnClosing(e);
         if (!CheckChanges())
            e.Cancel = true;
      }

      private void tsbRefresh_Click(object sender, EventArgs e)
      {
         if (CheckChanges())
            RefreshData(true);
      }

      void LoadData()
      {
         List<DistribGroup> list = new List<DistribGroup>(dsGroups.Values);
         list.ForEach(x => x.owner = this);
         list.Sort();

         SortableBindingList<DistribGroup> src = new SortableBindingList<DistribGroup>(list);
         dgvGroups.DataSource = src;
         btnSave.Enabled = false;
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

      private bool SaveChanges(bool showDialog)
      {
         dgvGroups.CommitEdit(DataGridViewDataErrorContexts.Commit);
         int pos = 1;
         foreach (DistribGroup g in (SortableBindingList<DistribGroup>)dgvGroups.DataSource)
            g.pos = pos++;

         List<IDataSet> wr = new List<IDataSet>();
         wr.Add(dsGroups);

         List<IDataSet> rm = new List<IDataSet>(new IDataSet[] { dsRemoved });

         bool ret = DataModule.UpdateDataSet(wr, rm, null, Config.GetConfig().GetConnection());
         if (ret)
            dsRemoved.Clear();

         if (showDialog)
            MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");

         return ret;
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         btnSave.Enabled = !SaveChanges(true);
      }

      private void dgvGroups_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         DistribGroup grp = dgvGroups.Rows[e.RowIndex].DataBoundItem as DistribGroup;
         if( grp != null )
         {
            SortableBindingList<DistribGroup.Item> src = new SortableBindingList<DistribGroup.Item>(grp.items);
            dgvItems.DataSource = src;
         }
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         SortableBindingList<DistribGroup> src = (SortableBindingList<DistribGroup>)dgvGroups.DataSource;
         DistribGroup item = src.AddNew();
         item.id = GRSoft.Network.DataObject.GenId();
         item.owner = this;
         dsGroups[item.id] = item;
         btnSave.Enabled = true;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if( dgvGroups.SelectedRows.Count == 0 )
            return;

         if (MessageBox.Show("Удалить данные?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == System.Windows.Forms.DialogResult.Yes)
         {
            List<DistribGroup> rmvd = new List<DistribGroup>();
            foreach (DataGridViewRow r in dgvGroups.SelectedRows)
            {
               DistribGroup item = r.DataBoundItem as DistribGroup;

               rmvd.Add(item);

               dsRemoved.Add(item);
               dsGroups.Remove(item.id);
            }

            SortableBindingList<DistribGroup> src = (SortableBindingList<DistribGroup>)dgvGroups.DataSource;
            rmvd.ForEach(x => 
            {
               int idx = src.IndexOf(x);
               if (idx >= 0)
                  src.RemoveAt(idx);
            });

            btnSave.Enabled = true;
         }
      }

      private void btnDelItem_Click(object sender, EventArgs e)
      {
         if (dgvItems.SelectedRows.Count == 0 || dgvGroups.CurrentRow == null)
            return;
         
         if (MessageBox.Show("Удалить данные?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == System.Windows.Forms.DialogResult.Yes)
         {
            DistribGroup src = dgvGroups.CurrentRow.DataBoundItem as DistribGroup;
            foreach (DataGridViewRow r in dgvItems.SelectedRows)
            {
               DistribGroup.Item item = r.DataBoundItem as DistribGroup.Item;
               src.items.Remove(item);
            }

            SortableBindingList<DistribGroup.Item>  dsrc = new SortableBindingList<DistribGroup.Item>(src.items);
            dgvItems.DataSource = dsrc;
              

            btnSave.Enabled = true;
         }
      }

      private void btnAddItem_Click(object sender, EventArgs e)
      {
         if (dgvGroups.CurrentRow == null)
            return;
         
         List<Price> curList = new List<Price>();
         SortableBindingList<DistribGroup.Item> dsrc = (SortableBindingList<DistribGroup.Item>)dgvItems.DataSource;
         foreach (DistribGroup.Item i in dsrc)
            if( i.item != null )
               curList.Add(i.item);

         DistribGroup src = dgvGroups.CurrentRow.DataBoundItem as DistribGroup;
         List<Price> newSel = FmSelectSKU.SelectItems(this, curList, null, true);
         if (newSel != null)
         {
            src.items.Clear();
            foreach (Price p in newSel)
            {
               DistribGroup.Item item = new DistribGroup.Item();
               item.id = p.id;
               item.item = p;
               src.items.Add(item);
            }

            dsrc = new SortableBindingList<DistribGroup.Item>(src.items);
            dgvItems.DataSource = dsrc;

            btnSave.Enabled = true;
         }
      }

      void MoveData(int index)
      {
         DistribGroup item = dgvGroups.CurrentRow.DataBoundItem as DistribGroup;

         SortableBindingList<DistribGroup> src = (SortableBindingList<DistribGroup>)dgvGroups.DataSource;
         src.Remove(item);
         src.Insert(index, item);
         dgvGroups.CurrentCell = dgvGroups.Rows[index].Cells[0];
         btnSave.Enabled = true;
      }

      private void tsbUp_Click(object sender, EventArgs e)
      {
         if( dgvGroups.CurrentRow == null || dgvGroups.CurrentRow.Index == 0 )
            return;

         MoveData(dgvGroups.CurrentRow.Index - 1);
         //int index = dgvGroups.CurrentRow.Index - 1;
         //DistribGroup item = dgvGroups.CurrentRow.DataBoundItem as DistribGroup;

         //SortableBindingList<DistribGroup> src = (SortableBindingList<DistribGroup>)dgvGroups.DataSource;
         //src.Remove(item);
         //src.Insert(index, item);

         //btnSave.Enabled = true;
      }

      private void tbDn_Click(object sender, EventArgs e)
      {
         if (dgvGroups.CurrentRow == null || dgvGroups.CurrentRow.Index >= dsGroups.Count - 1)
            return;

         MoveData(dgvGroups.CurrentRow.Index + 1);
         //int index = dgvGroups.CurrentRow.Index + 1;
         //DistribGroup item = dgvGroups.CurrentRow.DataBoundItem as DistribGroup;

         //SortableBindingList<DistribGroup> src = (SortableBindingList<DistribGroup>)dgvGroups.DataSource;
         //src.Remove(item);
         //src.Insert(index, item);

         //btnSave.Enabled = true;
      }

      private void tsbAgentsSet_Click(object sender, EventArgs e)
      {
         if (!CheckChanges())
            return;
         FmSetItemGroups fm = new FmSetItemGroups();
         fm.Groups = dsGroups;
         fm.ShowDialog();

      }
   }
}

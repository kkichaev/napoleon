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
   public partial class FmCauseEditor : Form
   {
      SimpleDataSet<LayoutFailureCause> causes, dsCause;

      public FmCauseEditor()
      {
         InitializeComponent();

         dgvItems.AutoGenerateColumns = false;
         dsCause = (SimpleDataSet<LayoutFailureCause>)DataModule.Get(LayoutFailureCause.OBJECT_NAME) ??
            new SimpleDataSet<LayoutFailureCause>(LayoutFailureCause.OBJECT_NAME);
         causes = new SimpleDataSet<LayoutFailureCause>(LayoutFailureCause.OBJECT_NAME, false);

      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      private void RefreshData()
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(causes);

         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData()
      {
         if(causes.Count == 0)
         {
            foreach (LayoutFailureCause lc in dsCause.Data)
            {
               causes.Add(lc);
            }
         }

         List<Item> src = new List<Item>();
         
         foreach (LayoutFailureCause c in causes.Data)
         {
               src.Add(new Item(c, this));
         }

         SortableBindingList<Item> ds = new SortableBindingList<Item>(src);
         dgvItems.DataSource = ds;
      }

      public void SetDirty(bool dirty)
      {
         tsbSave.Enabled = dirty;
      }

      class Item
      {
         LayoutFailureCause data;
         FmCauseEditor owner;

         public Item(LayoutFailureCause data, FmCauseEditor owner)
         {
            this.data = data;
            this.owner = owner;
         }

         public LayoutFailureCause Data { get { return data; } }

         public string Name
         {
            get { return data.name; }
            set
            {
               if( data.name != value)
               {
                  data.name = value;
                  owner.SetDirty(true);
               }
            }
         }
      }

      private void tsbAdd_Click(object sender, EventArgs e)
      {
         SortableBindingList<Item> ds = (SortableBindingList<Item>)dgvItems.DataSource;

         LayoutFailureCause lac = new LayoutFailureCause();
         Item i = new Item(lac, this);
         ds.Add(i);
         causes.Add(lac);

         SetDirty(true);
      }

      private void tsbType_SelectedIndexChanged(object sender, EventArgs e)
      {
         DoLoadData();
      }

      private void tsbDel_Click(object sender, EventArgs e)
      {
         List<Item> removed = new List<Item>();
         foreach (DataGridViewCell c in dgvItems.SelectedCells)
         {
            Item i = dgvItems.Rows[c.RowIndex].DataBoundItem as Item;
            if (!removed.Contains(i))
               removed.Add(i);
         }

         SortableBindingList<Item> ds = (SortableBindingList<Item>)dgvItems.DataSource;
         removed.ForEach(x => { 
            ds.Remove(x);
            RemoveItem(x.Data);
         });

         SetDirty(true);
      }

      private void RemoveItem(LayoutFailureCause item)
      {
         foreach (KeyValuePair<int, LayoutFailureCause> kv in causes)
         {
            if( kv.Value == item)
            {
               causes.Remove(kv.Key);
               break;
            }
         }
      }

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
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

      bool SaveChanges(bool showDialog)
      {
         dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);

         List<ReplacedSet> rpcSet = new List<ReplacedSet>();

         rpcSet.Add(new ReplacedSet(causes));

         bool result = DataModule.UpdateDataSet(null, null, rpcSet, Config.GetConfig().GetConnection());
         if (!result && showDialog)
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);

         if(result)
         {
            dsCause.Clear();
            foreach (LayoutFailureCause lc in causes.Data)
               dsCause.Add(lc);
         }

         return result;
      }
   }
}

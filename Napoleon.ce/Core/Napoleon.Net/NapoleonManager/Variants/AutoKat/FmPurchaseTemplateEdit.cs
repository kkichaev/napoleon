using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmPurchaseTemplateEdit : Form
   {
      DataSet<string, Price> price;
      DataSet<string, ManagerFolder> folder;
      SimpleDataSet<PurchaseTemplate> dsItems = new SimpleDataSet<PurchaseTemplate>(PurchaseTemplate.OBJECT_NAME, false);
      public FmPurchaseTemplateEdit()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;

         price = (DataSet<string, Price>)DataModule.Get(Price.OBJECT_NAME) ??
            new DataSet<string, Price>(Price.OBJECT_NAME);
         folder = (DataSet<string, ManagerFolder>)DataModule.Get(ManagerFolder.OBJECT_NAME) ??
            new DataSet<string, ManagerFolder>(ManagerFolder.OBJECT_NAME);
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         LoadData();
      }

      private void LoadData()
      {
         List<IDataSet> sets = new List<IDataSet>();

         if (price.Count == 0)
            sets.Add(price);

         if (folder.Count == 0)
            sets.Add(folder);

         sets.Add(dsItems);

         DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), sets, null).Join();

         List<PurchaseTemplate> src = new List<PurchaseTemplate>();
         foreach (PurchaseTemplate i in dsItems.Data)
            src.Add(i);

         src.Sort();

         dgvItems.DataSource = new BindingList<PurchaseTemplate>(src);
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

         SimpleDataSet<PurchaseTemplate> wrset = new SimpleDataSet<PurchaseTemplate>(PurchaseTemplate.OBJECT_NAME, false);

         int pos = 0;
         foreach (PurchaseTemplate ri in (BindingList<PurchaseTemplate>)dgvItems.DataSource)
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

      private void tsbSave_Click(object sender, EventArgs e)
      {
         tsbSave.Enabled = !SaveChanges(true);
      }

      public void SetDirty()
      {
         tsbSave.Enabled = true;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         Dictionary<Price, PurchaseTemplate> selected = new Dictionary<Price, PurchaseTemplate>();
         BindingList<PurchaseTemplate> src = (BindingList<PurchaseTemplate>)dgvItems.DataSource;
         foreach (PurchaseTemplate i in src)
         {
            if(i.item != null)
               selected[i.item] = i;
         }

         List<Price> ret = FmSelectSKU.SelectItems(this, new List<Price>(selected.Keys), null);
         if(ret != null)
         {
            SetDirty();

            foreach (Price p in ret)
            {
               if (selected.ContainsKey(p))
               {
                  selected.Remove(p);
                  continue;
               }

               PurchaseTemplate item = src.AddNew();
               item.item = p;
               item.id = p.id;
               item.pos = selected.Count;
            }

            foreach(KeyValuePair<Price, PurchaseTemplate> kv in selected)
            {
               src.Remove(kv.Value);
            }
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         BindingList<PurchaseTemplate> src = (BindingList<PurchaseTemplate>)dgvItems.DataSource;
         DataGridViewRow r = dgvItems.CurrentRow;
         if (r != null)
         {
            src.Remove((PurchaseTemplate)r.DataBoundItem);
            SetDirty();
         }
      }

      void ShiftCurItem(bool up)
      {
         BindingList<PurchaseTemplate> src = (BindingList<PurchaseTemplate>)dgvItems.DataSource;
         DataGridViewRow cur = dgvItems.CurrentRow;
         if (cur == null)
            return;

         int index = cur.Index;
         PurchaseTemplate el = src[index];
         if(up)
         {
            if (index == 0)
               return;
            index--;
         } else
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

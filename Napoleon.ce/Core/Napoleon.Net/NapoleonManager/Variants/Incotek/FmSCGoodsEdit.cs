using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmSCGoodsEdit : Form
   {
      StorcheckGoods doc;

      public FmSCGoodsEdit()
      {
         InitializeComponent();

         dgvNewItems.AutoGenerateColumns = false;
         dgvTop30.AutoGenerateColumns = false;
      }

      public void SetDoc(StorcheckGoods doc)
      {
         this.doc = doc;
         dtpStart.Value = doc.date.Date;

         LoadGrid(dgvNewItems, doc.items, StorcheckGoods.NEW_GOODS_FOLDER);
         LoadGrid(dgvTop30, doc.items, StorcheckGoods.TOP_30_FOLDER);
      }

      private void LoadGrid(DataGridView grid, List<StorcheckGoods.Item> list, int folder)
      {
         List<Price> src = new List<Price>();
         foreach(StorcheckGoods.Item i in list)
         {
            if(i.folder == folder && i.price != null)
            {
               src.Add(i.price);
            }
         }

         src.Sort();
         grid.DataSource = src;
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         AddItem(dgvNewItems);
      }

      private void toolStripButton1_Click(object sender, EventArgs e)
      {
         AddItem(dgvTop30);
      }

      private void AddItem(DataGridView grid)
      {
         List<Price> prc = (List<Price>)grid.DataSource;
         List<Price> sel = FmSelectSKU.SelectItems(this, prc, null, true);
         if (sel != null)
         {
            sel.Sort();
            grid.DataSource = null;
            grid.DataSource = sel;
            grid.Invalidate();
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         RemoveItem(dgvNewItems);
      }

      private void toolStripButton2_Click(object sender, EventArgs e)
      {
         RemoveItem(dgvTop30);
      }

      private void RemoveItem(DataGridView grid)
      {
         if (grid.CurrentRow == null)
            return;

         //if (MessageBox.Show("Удалить товар?", "Вопрос", MessageBoxButtons.YesNo, MessageBoxIcon.Question) != System.Windows.Forms.DialogResult.Yes)
         //   return;

         List<Price> lst = (List<Price>)grid.DataSource;
         lst.RemoveAt(grid.CurrentRow.Index);
         grid.DataSource = null;
         grid.DataSource = lst;
      }

      private void btnCancel_Click(object sender, EventArgs e)
      {
         DialogResult = System.Windows.Forms.DialogResult.Cancel;
         Close();
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         DialogResult = System.Windows.Forms.DialogResult.OK;

         doc.date = dtpStart.Value.Date;

         doc.items.Clear();

         LoadList(doc.items, dgvNewItems, StorcheckGoods.NEW_GOODS_FOLDER);
         LoadList(doc.items, dgvTop30, StorcheckGoods.TOP_30_FOLDER);

         Close();
      }

      private void LoadList(List<StorcheckGoods.Item> list, DataGridView grid, int folder)
      {
         List<Price> src = (List<Price>)grid.DataSource;
         foreach(Price p in src)
         {
            StorcheckGoods.Item i = new StorcheckGoods.Item();
            i.id = p.id;
            i.price = p;
            i.folder = folder;

            list.Add(i);
         }
      }
   }
}

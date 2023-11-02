using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmContractEdit : Form
   {
      BindingList<Price> datasource = new BindingList<Price>();

      public FmContractEdit()
      {
         InitializeComponent();
         grid.AutoGenerateColumns = false;
         grid.DataSource = datasource;
      }

      public DateTime Start { get { return dpv.Start; } set { dpv.Start = value; } }

      public DateTime Finish { get { return dpv.Finish; } set { dpv.Finish = value; } }

      public string Contract { get { return tbName.Text.Trim(); } set { tbName.Text = value; } }

      public IList<Price> Items { get { return datasource; } 
         set 
         {
            datasource.Clear();

            foreach (Price p in value)
               datasource.Add(p);
         } 
      }

      private void FmContractEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK) 
         { 
            if (Contract.Length == 0)
            {
               e.Cancel = true;
               tbName.Focus();
               DialogUtil.HaveToValueMsg(this);
            }
            else if (datasource.Count == 0)
            {
               const string BLOCK_CREATED_EMPTY_DOC = "Невозможно создать пустой контракт";
               MessageBox.Show(this, BLOCK_CREATED_EMPTY_DOC, DialogUtil.TITLE_ERR, MessageBoxButtons.OK);
               e.Cancel = true;
            }
         }
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         FmPriceEdit dialog = new FmPriceEdit();

         if (dialog.ShowDialog() == DialogResult.OK)
         {
            Price p = new Price();
            p.id = Price.GenId();
            p.name = dialog.Price;
            datasource.Add(p);
         }
      }

      private void btnEdit_Click(object sender, EventArgs e)
      {
         if(grid.CurrentRow != null)
         {
            Price p = grid.CurrentRow.DataBoundItem as Price;

            if(p != null)
            {
               FmPriceEdit dialog = new FmPriceEdit();
               dialog.Price = p.name;

               if (dialog.ShowDialog() == DialogResult.OK)
               {
                  p.name = dialog.Price;
                  grid.Refresh();
               }
            }
         }
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         if (grid.CurrentRow != null && DialogUtil.AskToDel(this))
            datasource.Remove((Price)grid.CurrentRow.DataBoundItem);
      }
   }
}

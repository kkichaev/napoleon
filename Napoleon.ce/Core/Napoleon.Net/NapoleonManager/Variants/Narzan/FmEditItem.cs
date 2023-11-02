using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmEditItem : Form
   {
      OrgMatrixItem item;

      public FmEditItem(OrgMatrixItem item)
      {
         InitializeComponent();
         this.item = item;
         lblName.Text = item.price.Name;
         tbFace.Text = item.face.ToString();
         tbQty.Text = item.qty.ToString();
      }

      private void FmEditItem_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            if (!CheckControl(tbQty, out item.qty) || !CheckControl(tbFace, out item.face))
            {
               MessageBox.Show("Ошибка преобразования текста в число!");
               e.Cancel = true;
            }
         }
      }

      private bool CheckControl(TextBox textBox, out double value)
      {
         bool res = Double.TryParse(textBox.Text.Trim(), out value);
         textBox.Focus();
         return res;
      }
   }
}

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmSKUEdit : Form
   {
      public FmSKUEdit()
      {
         InitializeComponent();
      }

      public static Price EditSKU(Price price)
      {
         Price result = null;
         FmSKUEdit form = new FmSKUEdit();

         if (price != null)
         {
            form.tbName.Text = price.name;
            form.tbNTZ.Text = price.ntz.ToString();
         }

         if (form.ShowDialog() == DialogResult.OK)
         {
            result = price == null ? new Price() : price;
            result.name = form.tbName.Text.Trim();

            int ntz = 0;

            if (Int32.TryParse(form.tbNTZ.Text.Trim(), out ntz))
               result.ntz = ntz;

            if(price == null)
               result.id = GRSoft.Network.DataObject.GenId();
         }

         return result;
      }

      private void FmSKUEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK && tbName.Text.Trim().Length == 0)
         {
            tbName.Focus();
            MessageBox.Show("Поле не может быть пустым",
                     "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
            e.Cancel = true;
         }
      }
   }
}

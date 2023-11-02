using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmPriceEdit : Form
   {
      public FmPriceEdit()
      {
         InitializeComponent();
      }

      public string ItemName { get { return tbName.Text.Trim(); } set { tbName.Text = value; } }
      public string Category { get { return tbCateg.Text.Trim(); } set { tbCateg.Text = value; } }
      public string Production { get { return tbProduct.Text.Trim(); } set { tbProduct.Text = value; } }
      public string Code { get { return tbCode.Text.Trim(); } set { tbCode.Text = value; } }

      public string Barcode { get { return tbBarcode.Text.Trim(); } set { tbBarcode.Text = value; } }

      public double Weight
      {
         get
         {
            double w = 0;
            Double.TryParse(tbWeight.Text, out w);
            return w;
         }

         set { tbWeight.Text = value.ToString(); }
      }

      private void FmPriceEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK && Name.Length == 0)
         {
            e.Cancel = true;
            tbName.Focus();
            DialogUtil.HaveToValueMsg(this);
         }
      }
   }
}

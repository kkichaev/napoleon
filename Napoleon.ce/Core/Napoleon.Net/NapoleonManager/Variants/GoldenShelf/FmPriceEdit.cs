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

      public string Price { get { return tbName.Text.Trim(); } set { tbName.Text = value; } }

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

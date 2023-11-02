using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmCityEdit : Form
   {
      public FmCityEdit()
      {
         InitializeComponent();
      }

      public string City { get { return tbText.Text.Trim(); } set { tbText.Text = value; } }

      private void FmCityEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (City.Length == 0)
         {
            e.Cancel = true;
            tbText.Focus();
            DialogUtil.HaveToValueMsg(this);
         }
      }
   }
}

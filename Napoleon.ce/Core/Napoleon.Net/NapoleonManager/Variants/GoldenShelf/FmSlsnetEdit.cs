using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmSlsnetEdit : Form
   {
      public FmSlsnetEdit()
      {
         InitializeComponent();
      }

      public string Slsnet { get { return tbText.Text.Trim(); } set { tbText.Text = value; } }

      private void FmSlsnetEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (Slsnet.Length == 0)
         {
            e.Cancel = true;
            tbText.Focus();
            DialogUtil.HaveToValueMsg(this);
         }
      }
   }
}

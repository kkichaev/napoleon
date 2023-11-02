using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmTaskEdit : Form
   {
      public FmTaskEdit()
      {
         InitializeComponent();
      }

      public string Task { get { return tbTask.Text.Trim(); } set { tbTask.Text = value; } }

      private void btnClear_Click(object sender, EventArgs e)
      {
         tbTask.Clear();
      }
   }
}

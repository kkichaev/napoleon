using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmTaskTextEdit : Form
   {
      public FmTaskTextEdit()
      {
         InitializeComponent();
      }

      protected override void OnActivated(EventArgs e)
      {
         base.OnActivated(e);
         tbTask.Focus();
      }

      public String Task { get { return tbTask.Text; } set { tbTask.Text = value; } }
   }
}

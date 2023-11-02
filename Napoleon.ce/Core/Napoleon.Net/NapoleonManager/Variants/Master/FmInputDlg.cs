using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmInputDlg : Form
   {
      public FmInputDlg()
      {
         InitializeComponent();
      }

      public String Value { get { return tbName.Text.Trim(); } set { tbName.Text = value; } }
   }
}

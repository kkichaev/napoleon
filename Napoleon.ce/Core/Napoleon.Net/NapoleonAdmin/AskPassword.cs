using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public partial class AskPassword : Form
   {
      public AskPassword()
      {
         InitializeComponent();
      }

      public string Password
      {
         get { return this.password.Text; }
         set { this.password.Text = value; }
      }
   }
}

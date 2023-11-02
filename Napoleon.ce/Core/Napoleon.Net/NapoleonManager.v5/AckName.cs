using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class AckName : Form
   {
      public AckName()
      {
         InitializeComponent();
      }

      public string EnteredName
      {
         set { name.Text = value; }
         get { return name.Text; }
      }
   }
}

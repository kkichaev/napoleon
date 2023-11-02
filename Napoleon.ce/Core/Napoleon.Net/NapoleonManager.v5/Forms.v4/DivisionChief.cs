using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class DivisionChief : Form
   {
      public DivisionChief(Division d)
      {
         InitializeComponent();
         __Initing(d);
      }
   }
}

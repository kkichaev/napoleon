using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class TimeGridControl : UserControl
   {
      public TimeGridControl()
      {
         InitializeComponent();
      }

      [Browsable(true)]
      public TaskContextMenuStrip ItemContextMenuStrip
      {
         get { return grid.ItemContextMenuStrip; }
         set { grid.ItemContextMenuStrip = value; }
      }
   }
}

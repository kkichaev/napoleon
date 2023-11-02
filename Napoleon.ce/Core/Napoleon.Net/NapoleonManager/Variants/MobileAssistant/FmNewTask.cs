using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmNewTask : Form
   {
      public FmNewTask()
      {
         InitializeComponent();
      }

      protected override void OnActivated(EventArgs e)
      {
         base.OnActivated(e);
         tbTask.Focus();
      }

      public DateTime Date
      {
         get { return dtpDate.Value; }
         set { dtpDate.Value = value; }
      }

      public string Task { get { return tbTask.Text; } set { tbTask.Text = value; } }
   }
}

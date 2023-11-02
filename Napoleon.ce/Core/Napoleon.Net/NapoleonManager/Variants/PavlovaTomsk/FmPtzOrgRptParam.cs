using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmPtzOrgRptParam : Form
   {
      public FmPtzOrgRptParam()
      {
         InitializeComponent();
      }

      public DateTime Start { get { return dtpStart.Value.Date; } set { dtpStart.Value = value; } }

      public DateTime Finish { get { return dtpFinish.Value.Date; } set { dtpFinish.Value = value; } }
   }
}

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmStatOperRpt : Form
   {
      public FmStatOperRpt()
      {
         InitializeComponent();
         dpv.Start = DateTime.Now;
         dpv.Finish = DateTime.Now;
      }

      private class Param : GRSoft.Network.DataObject
      {
         public DateTime start = DateTime.MinValue;
         public DateTime finish = DateTime.MinValue;
      }

      private void btnExcel_Click(object sender, EventArgs e)
      {
         Param arg = new Param();
         arg.start = dpv.Start.Date;
         arg.finish = dpv.Finish.Date;
         ReportResult.DoReport("statoper", arg, this);
      }
   }
}

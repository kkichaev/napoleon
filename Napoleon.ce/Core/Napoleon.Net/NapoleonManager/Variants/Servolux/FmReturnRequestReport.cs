using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmReturnRequestReport : Form
   {
      public FmReturnRequestReport()
      {
         InitializeComponent();
         dpv.Start = DateTime.Now.Date;
         dpv.Finish = DateTime.Now.Date;
      }

      private void button1_Click(object sender, EventArgs e)
      {
         ReportData data = new ReportData();
         data.start = dpv.Start;
         data.end = dpv.Finish;

         ReportResult.DoReport("return_request_report", data, this);
      }

      class ReportData : GRSoft.Network.DataObject
      {
         public DateTime start;
         public DateTime end;
      }
   }
}
